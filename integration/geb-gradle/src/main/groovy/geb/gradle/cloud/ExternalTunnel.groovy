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
package geb.gradle.cloud

import org.gradle.api.Project
import org.slf4j.Logger

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

abstract class ExternalTunnel {
	final protected Project project
	final protected Logger logger

	protected Process tunnelProcess

	long timeout = 3
	TimeUnit timeoutUnit = TimeUnit.MINUTES

	ExternalTunnel(Project project, Logger logger) {
		this.project = project
		this.logger = logger
	}

	void validateState() {
	}

	abstract String getOutputPrefix()

	abstract List<String> assembleCommandLine()

	abstract String getTunnelReadyMessage()

	void startTunnel(File workingDir, boolean background) {
		validateState()

		def command = assembleCommandLine()
		logger.debug("Executing command: {}", command)
		if (background) {
			workingDir.mkdirs()
			tunnelProcess = new ProcessBuilder(command).
				redirectErrorStream(true).
				directory(workingDir).
				start()

			def latch = new CountDownLatch(1)
			Thread.start {
				try {
					tunnelProcess.inputStream.eachLine { String line ->
						if (latch.count) {
							logger.info "$outputPrefix: $line"
							if (line.contains(tunnelReadyMessage)) {
								latch.countDown()
							}
						} else {
							logger.debug "$outputPrefix: $line"
						}
					}
				} catch (IOException ignore) {
				}
			}

			if (!latch.await(timeout, timeoutUnit)) {
				throw new RuntimeException("Timeout waiting for tunnel to open")
			}
		} else {
			project.exec {
				commandLine command
			}
		}
	}

	void stopTunnel() {
		if (tunnelProcess) {
			logger.info "disconnecting tunnel"
			tunnelProcess.destroy()
		}
	}
}
