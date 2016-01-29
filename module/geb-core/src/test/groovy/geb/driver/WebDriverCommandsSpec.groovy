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
package geb.driver

import geb.Module
import geb.Page
import geb.test.CallbackAndWebDriverServer
import geb.test.GebSpecWithServer
import geb.test.RemoteWebDriverWithExpectations
import geb.test.TestHttpServer
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Shared
import spock.lang.Unroll

class WebDriverCommandsSpec extends GebSpecWithServer {

    @Shared
    CallbackAndWebDriverServer callbackAndWebDriverServer = new CallbackAndWebDriverServer()
    RemoteWebDriverWithExpectations driver

    def setup() {
        driver = new RemoteWebDriverWithExpectations(callbackAndWebDriverServer.webdriverUrl, DesiredCapabilities.htmlUnit())
        browser.driver = driver
        browser.baseUrl = callbackAndWebDriverServer.applicationUrl
        browser.config.cacheDriver = false
    }

    def cleanup() {
        driver.resetExpectations()
    }

    @Override
    TestHttpServer getServerInstance() {
        callbackAndWebDriverServer
    }

    void html(Closure htmlMarkup) {
        callbackAndWebDriverServer.responseHtml(htmlMarkup)
        go()
        driver.clearRecordedCommands()
    }

    void "going to a page and getting its title"() {
        given:
        html {
            head {
                title "a title"
            }
        }

        when:
        title

        then:
        driver.getTitleExecuted()
    }

    void "using a selector that returns multiple elements"() {
        given:
        html {
            body {
                p "first"
                p "second"
            }
        }

        when:
        $("p")

        then:
        driver.findElementsByCssExecuted("p")
    }

    void "using form control shortcuts in a baseless module should not generate multiple root element searches"() {
        given:
        html {
            body {
                input type: "text", name: "someName"
            }
        }
        page WebDriverCommandSpecModulePage

        when:
        def module = plainModule

        then:
        driver.findRootElementExecuted()
        driver.resetExpectations()

        when:
        module.someName()

        then:
        driver.findChildElementsByNameExecuted("someName")
    }

    void "attribute map passed to find method is translated into a css selector"() {
        given:
        html {
            body {
                input type: "text", name: "someName"
            }
        }

        when:
        def input = $(type: "text", name: "someName")

        then:
        input

        and:
        driver.findElementsByCssExecuted("""[type="text"][name="someName"]""")
    }

    @Unroll("passing #attributes to find results in a findElements command using #using")
    void "passing a single attribute map to find should be translated to a specific By selector usage where possible"() {
        given:
        html {
            body {
                input id: "foo", class: "bar", name: "fizz"
            }
        }

        when:
        def input = $(attributes)

        then:
        input

        and:
        driver.ensureExecuted("findElements", using: using, value: selector)

        where:
        attributes     | using        | selector
        [id: "foo"]    | "id"         | "foo"
        [class: "bar"] | "class name" | "bar"
        [name: "fizz"] | "name"       | "fizz"
    }

    void "setting text input value"() {
        given:
        html {
            body {
                input type: "text"
            }
        }
        def input = $("input")
        driver.clearRecordedCommands()

        when:
        input.value("foo")

        then:
        driver.getElementTagNameExecuted()
        driver.getElementAttributeExecuted("type")
        driver.clearElementExecuted()
        driver.sendKeysExecuted()
    }
}

class WebDriverCommandSpecModulePage extends Page {

    static content = {
        plainModule { module Module }
    }
}
