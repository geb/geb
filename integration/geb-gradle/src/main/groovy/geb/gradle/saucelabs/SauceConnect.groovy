/*
 * Copyright 2012 the original author or authors.
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

package geb.gradle.saucelabs

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.slf4j.Logger

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SauceConnect {
	final protected Project project
	final protected SauceAccount account
	final protected Logger logger

	protected Process tunnelProcess

	SauceConnect(Project project, SauceAccount account, Logger logger) {
		this.project = project
		this.account = account
		this.logger = logger
	}

	void startTunnel(File sauceConnectJar, File workingDir, boolean background) {
		if (!account.username || !account.accessKey) {
			throw new InvalidUserDataException("No sauce labs username or passwords set")
		}

		def jvm = org.gradle.internal.jvm.Jvm.current()
		def javaBinary = jvm.javaExecutable.absolutePath

		if (background) {

			workingDir.mkdirs()
			tunnelProcess = new ProcessBuilder(javaBinary, "-jar", sauceConnectJar.absolutePath, account.username, account.accessKey).
				redirectErrorStream(true).
				directory(workingDir).
				start()

			def latch = new CountDownLatch(1)
			Thread.start {
				try {
					tunnelProcess.inputStream.eachLine { String line ->
						if (latch.count) {
							logger.info "sauce-connect: $line"
							if (line.endsWith("Connected! You may start your tests.")) {
								latch.countDown()
							}
						} else {
							logger.debug "sauce-connect: $line"
						}
					}
				} catch (IOException ignore) {}
			}

			if (!latch.await(3, TimeUnit.MINUTES)) {
				throw new RuntimeException("Timeout waiting for sauce tunnel to open")
			}
		} else {
			project.exec {
				executable javaBinary
				args "-jar", sauceConnectJar.absolutePath, account.username, account.accessKey
			}
		}
	}

	void stopTunnel() {
		if (tunnelProcess) {
			logger.info "disconnecting sauce labs tunnel"
			tunnelProcess.destroy()
		}
	}
}
