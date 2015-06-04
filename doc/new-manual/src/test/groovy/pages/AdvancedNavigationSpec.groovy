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
package pages

import geb.Browser
import geb.Page
import geb.test.GebSpec

class AdvancedNavigationSpec extends GebSpec {

    def setup() {
        browser.driver.javascriptEnabled = false
    }

    def "using to with page specifying an url"() {
        expect:
        // tag::to[]
        Browser.drive(baseUrl: "http://www.gebish.org/") {
            to PageObjectsPage
            assert currentUrl == "http://www.gebish.org/pages"
        }
        // end::to[]
    }

    def "using to with additional arguments"() {
        expect:
        // tag::to_with_args[]
        Browser.drive(baseUrl: "http://www.gebish.org/") {
            to ManualsPage, "0.9.3", "index.html"
            assert currentUrl == "http://www.gebish.org/manual/0.9.3/index.html"
        }
        // end::to_with_args[]
    }

    def "using to with custom convert to path"() {
        when:
        // tag::convert_to_path[]
        def someManualVersion = new Manual(version: "0.9.3")

        // end::convert_to_path[]
        then:
        // tag::convert_to_path[]
        Browser.drive(baseUrl: "http://www.gebish.org/") {
            to ManualsPage, someManualVersion
            assert currentUrl == "http://www.gebish.org/manual/0.9.3/index.html"
        }
        // end::convert_to_path[]
    }

    def "using to with named params"() {
        when:
        // tag::to_with_named_params[]
        def someManualVersion = new Manual(version: "0.9.3")

        // end::to_with_named_params[]
        then:
        // tag::to_with_named_params[]
        Browser.drive(baseUrl: "http://www.gebish.org/") {
            to ManualsPage, someManualVersion, flag: true
            assert currentUrl == "http://www.gebish.org/manual/0.9.3/index.html?flag=true"
        }
        // end::to_with_named_params[]
    }
}

// tag::pages_page[]
class PageObjectsPage extends Page {
    static url = "pages"
}
// end::pages_page[]

// tag::manuals_page[]
// tag::manuals_page_with_convert_to_path[]
class ManualsPage extends Page {
    static url = "manual"
    // end::manuals_page[]
    String convertToPath(Manual manual) {
        "/${manual.version}/index.html"
    }
    // tag::manuals_page[]
}
// end::manuals_page_with_convert_to_path[]
// end::manuals_page[]

// tag::manual_class[]
class Manual {
    String version
}
// end::manual_class[]