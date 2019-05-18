/*
 * Copyright 2015 the original author or authors.
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
package testing

import geb.test.CallbackHttpServer
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification
import spock.util.EmbeddedSpecRunner

class ReportingFunctionalSpec extends Specification {

    EmbeddedSpecRunner specRunner = new EmbeddedSpecRunner(throwFailure: false)

    @Shared
    @AutoCleanup("stop")
    def server = new CallbackHttpServer()

    @Rule
    TemporaryFolder tempDir

    def setupSpec() {
        server.start()
        server.html {
            head {
                title "Not logged in!"
            }
            body {
                input(type: "text", name: "username")
                input(type: "button", name: "login")
            }
        }
    }

    File getReportsDir() {
        new File(tempDir.root, "reports")
    }

    void reportExists(String filename) {
        assert new File(reportsDir, "my/tests/LoginSpec/${filename}.html").exists()
    }

    def "reporting on a failing test which also writes a custom report"() {
        when:
        specRunner.run """
            //tag::example[]
            package my.tests

            import geb.spock.GebReportingSpec

            class LoginSpec extends GebReportingSpec {
            //end::example[]

                def setup() {
                    baseUrl = "${server.baseUrl}"
                    config.rawConfig.reportsDir = "${reportsDir.absolutePath.replaceAll("\\\\", "\\\\\\\\")}"
                }

                //tag::example[]
                def "login"() {
                    when:
                    go "/login"
                    username = "me"
                    report "login screen" //<1>
                    login().click()

                    then:
                    title == "Logged in!"
                }
            }
            //end::example[]
        """

        then:
        reportExists("001-001-login-login screen")
        reportExists("001-002-login-failure")
    }
}
