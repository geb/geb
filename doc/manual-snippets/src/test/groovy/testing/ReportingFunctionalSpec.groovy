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

import geb.Browser
//tag::example[]
import geb.spock.GebReportingSpec

//end::example[]
import geb.test.CallbackHttpServer
import spock.lang.AutoCleanup
import spock.lang.Shared

import javax.servlet.http.HttpServletRequest

//tag::example[]
class ReportingFunctionalSpec extends GebReportingSpec {
    //end::example[]
    @Shared
    @AutoCleanup("stop")
    CallbackHttpServer callbackServer = new CallbackHttpServer()

    def setupSpec() {
        callbackServer.start(0)
        browser.baseUrl = callbackServer.baseUrl
        callbackServer.html { HttpServletRequest request ->
            if (request.requestURI.endsWith("/login")) {
                head {
                    title "Logged in!"
                }
                body {
                    input(type: "text", name: "username")
                    input(type: "button", name: "login")
                }
            }
        }
    }

    def cleanupSpec() {
        reportExists("001-002-login-end")
    }

    Browser createBrowser() {
        def browser = super.createBrowser()
        if (callbackServer.started) {
            browser.baseUrl = callbackServer.baseUrl
        }
        browser
    }

    void reportExists(String filename) {
        assert new File(browser.config.reportsDir, "testing/ReportingFunctionalSpec/${filename}.html").exists()
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
        //end::example[]

        and:
        reportExists("001-001-login-login screen")
        //tag::example[]
    }
}
//end::example[]
