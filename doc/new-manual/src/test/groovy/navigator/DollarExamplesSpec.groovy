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
package navigator

import geb.test.GebSpecWithCallbackServer
import org.openqa.selenium.By

class DollarExamplesSpec extends GebSpecWithCallbackServer {

    def "examples of all dollar method signatures"() {
        given:
        html {
            h1("first heading", class: "heading")
            h1("second heading", class: "heading")
            h1("third heading", class: "heading")
            div {
                p(title: "something")
            }
        }

        expect:
        // tag::concrete_example[]
        $("h1", 2, class: "heading")
        // end::concrete_example[]
        // tag::other_examples[]
        $("div p", 0)
        $("div p", title: "something")
        $(0)
        $(title: "something")
        // end::other_examples[]
    }

    def "support for CSS3 selectors"() {
        given:
        html {
            div(class: "some-class") {
                p(title: "something")
            }
        }

        expect:
        // tag::css3_selectors[]
        $('div.some-class p:first-child[title^="someth"]')
        // end::css3_selectors[]
    }

    def "support for By selectors"() {
        given:
        html {
            div(class: "some-class") {
                p(id: "some-id", class: "xpath")
            }
        }
        expect:
        // tag::by_selectors[]
        $(By.id("some-id"))
        $(By.className("some-class"))
        $(By.xpath('//p[@class="xpath"]'))
        // end::by_selectors[]
    }
}
