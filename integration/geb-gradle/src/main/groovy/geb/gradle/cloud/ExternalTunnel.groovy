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

import groovy.util.logging.Slf4j
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.process.ExecOperations

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import static org.gradle.api.tasks.PathSensitivity.RELATIVE

@Slf4j
abstract class ExternalTunnel {

    final protected ExecOperations execOperations

    protected Process tunnelProcess

    ExternalTunnel(ExecOperations execOperations) {
        this.execOperations = execOperations
        timeout.convention(3)
        timeoutUnit.convention(TimeUnit.MINUTES)
    }

    @InputFiles
    @PathSensitive(RELATIVE)
    abstract ConfigurableFileCollection getExecutable()

    @Internal
    abstract Property<Integer> getTimeout()

    @Internal
    abstract Property<TimeUnit> getTimeoutUnit()

    @Internal
    abstract String getOutputPrefix()

    abstract List<Object> assembleCommandLine()

    @Internal
    abstract Property<String> getTunnelReadyMessage()

    @Internal
    String getExecutablePath() {
        executable.asFileTree.singleFile.absolutePath
    }

    void startTunnel(File workingDir, boolean background) {
        def command = assembleCommandLine()*.toString()
        log.debug("Executing command: {}", command)
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
                            log.info "$outputPrefix: $line"
                            if (line.contains(tunnelReadyMessage.get())) {
                                latch.countDown()
                            }
                        } else {
                            log.debug "$outputPrefix: $line"
                        }
                    }
                } catch (IOException ignore) {
                }
            }

            if (!latch.await(timeout.get(), timeoutUnit.get())) {
                throw new RuntimeException("Timeout waiting for tunnel to open")
            }
        } else {
            execOperations.exec {
                commandLine command
            }
        }
    }

    void stopTunnel() {
        if (tunnelProcess) {
            log.info "disconnecting tunnel"
            tunnelProcess.destroy()
        }
    }
}
