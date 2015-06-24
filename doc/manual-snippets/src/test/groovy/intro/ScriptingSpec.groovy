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
package intro

// tag::imports[]
import geb.Browser
// end::imports[]
import geb.driver.CachingDriverFactory
import intro.page.GebHomePage

import spock.lang.Specification

class ScriptingSpec extends Specification {

    void setupSpec() {
        CachingDriverFactory.clearCache()
    }

    void cleanupSpec() {
        CachingDriverFactory.clearCache()
    }

    def "inline"() {
        expect:
        // tag::inline[]
        Browser.drive {
            go "http://gebish.org"

            assert title == "Geb - Very Groovy Browser Automation" // <1>

            $("#sidebar .sidemenu a", text: "jQuery-like API").click()//<2>

            assert $("#main h1")*.text() == ["Navigating Content", "Form Control Shortcuts"] //<3>
            assert $("#sidebar .sidemenu a", text: "jQuery-like API").parent().hasClass("selected")// <4>
        }
        // end::inline[]
    }

    def "using page objects"() {
        expect:
        // tag::using_page_objects[]
        Browser.drive {
            to GebHomePage //<1>

            highlights.jQueryLikeApi.click()

            assert sectionTitles == ["Navigating Content", "Form Control Shortcuts"]
            assert highlights.jQueryLikeApi.selected
        }
        // end::using_page_objects[]
    }
}