/*
 * Copyright 2021 the original author or authors.
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
package geb.spock

import geb.test.CallbackHttpServer
import spock.lang.*
import spock.util.EmbeddedSpecRunner

import javax.servlet.http.HttpServletRequest

class ParallelExecutionSpec extends Specification {

    EmbeddedSpecRunner specRunner = new EmbeddedSpecRunner(
        throwFailure: false,
        configurationScript: {
            runner {
                parallel {
                    enabled true
                }
            }
        }
    )

    @TempDir
    File temporaryDir

    @Shared
    @AutoCleanup("stop")
    CallbackHttpServer server = new CallbackHttpServer()

    def setup() {
        server.start()
        server.html { HttpServletRequest request ->
            body {
                div "${request.requestURI.toURI().path}"
            }
        }

        specRunner.addClassImport(GebSpec)
        specRunner.addClassImport(GebReportingSpec)
        specRunner.addClassImport(Unroll)
    }

    def 'GebSpec supports parallel execution at feature level'() {
        when:
        def result = specRunner.run """
            class SpecRunningFixturesInParallel extends GebSpec {

                def setup() {
                    baseUrl = "${server.baseUrl}"
                    config.cacheDriverPerThread = true
                }

                @Unroll
                def 'fixture running iterations in parallel'() {
                    when:
                    go path

                    then:
                    \$('div').text() == "/\${path}"

                    where:
                    path << ('a'..'d')
                }
            }
        """

        then:
        !result.failures*.exception
    }

    def 'GebReportingSpec supports parallel execution at feature level'() {
        when:
        def result = specRunner.run """
            abstract class AbstractSpecRunningFixturesInParallel extends GebReportingSpec {
                def setup() {
                    baseUrl = "${server.baseUrl}"
                    config.cacheDriverPerThread = true
                    config.reportOnTestFailureOnly = false
                    config.rawConfig.reportsDir = "${reportDir.absolutePath.replaceAll("\\\\", "\\\\\\\\")}"
                }

                @Unroll
                def 'fixture running iterations in parallel'() {
                    when:
                    go path

                    then:
                    \$('div').text() == "/\${path}"

                    where:
                    path << ('a'..'d')
                }
            }

            class SpecRunningFixturesInParallel1 extends AbstractSpecRunningFixturesInParallel {
            }

            class SpecRunningFixturesInParallel2 extends AbstractSpecRunningFixturesInParallel {
            }
        """

        then:
        !result.failures*.exception
        reportFileTestCounterPrefixes("SpecRunningFixturesInParallel1") == (1..4)*.toString()*.padLeft(3, "0").toSet()
        reportFileTestCounterPrefixes("SpecRunningFixturesInParallel2") == (1..4)*.toString()*.padLeft(3, "0").toSet()
    }

    private Set<String> reportFileTestCounterPrefixes(String className) {
        def reportGroupDir = new File(reportDir, className)
        reportGroupDir.listFiles().collect {
            it.name.tokenize("-").first()
        }
    }

    private File getReportDir() {
        new File(temporaryDir, "reports")
    }

}
