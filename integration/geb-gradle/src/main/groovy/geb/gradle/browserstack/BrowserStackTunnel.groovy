/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geb.gradle.browserstack

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.slf4j.Logger

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class BrowserStackTunnel {
	final protected Project project
	final protected BrowserStackAccount account
	final protected Logger logger
	final protected File tunnelJar

	protected Process tunnelProcess

	long timeout = 3
	TimeUnit timeoutUnit = TimeUnit.MINUTES

	BrowserStackTunnel(Project project, BrowserStackAccount account, Logger logger, File tunnelJar) {
		this.project = project
		this.account = account
		this.logger = logger
		this.tunnelJar = tunnelJar
	}

	void startTunnel(File workingDir, boolean background, List<URL> applicationUrls) {
		if (!account.accessKey) {
			throw new InvalidUserDataException("No BrowserStack access key set")
		}

		def jvm = org.gradle.internal.jvm.Jvm.current()
		def javaBinary = jvm.javaExecutable.absolutePath

		if (background) {

			workingDir.mkdirs()
			def command = [javaBinary] + assembleTunnelArgs(applicationUrls) as List<String>
			logger.debug("running {}", command)
			tunnelProcess = new ProcessBuilder(command).
				redirectErrorStream(true).
				directory(workingDir).
				start()

			def latch = new CountDownLatch(1)
			Thread.start {
				try {
					tunnelProcess.inputStream.eachLine { String line ->
						if (latch.count) {
							logger.info "browserstack-tunnel: $line"
							if (line.contains("You can now access your local server(s) in our remote browser:")) {
								latch.countDown()
							}
						} else {
							logger.debug "browserstack-tunnel: $line"
						}
					}
				} catch (IOException ignore) {}
			}

			if (!latch.await(timeout, timeoutUnit)) {
				throw new RuntimeException("Timeout waiting for BrowserStack tunnel to open")
			}
		} else {
			def javaArgs = assembleTunnelArgs(applicationUrls)
			logger.debug("running {} {}", javaBinary, javaArgs)
			project.exec {
				executable javaBinary
				args javaArgs
			}
		}
	}

	void stopTunnel() {
		if (tunnelProcess) {
			logger.info "disconnecting BrowserStack tunnel"
			tunnelProcess.destroy()
		}
	}

	List<String> assembleTunnelArgs(List<URL> applicationUrls) {
		def args = ["-jar", tunnelJar.absolutePath]
		if (account.localId) {
			args << "-localIdentifier" << account.localId
		}
		args << account.accessKey << assembleAppSpecifier(applicationUrls)
		args
	}

	static String assembleAppSpecifier(List<URL> applicationUrls) {
		return applicationUrls.collect { "${it.host}:${it.port}:${it.protocol=='https'?'1':'0'}" }.join(",")
	}
}
