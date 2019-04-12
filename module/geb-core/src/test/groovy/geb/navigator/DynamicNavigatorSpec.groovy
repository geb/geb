/*
 * Copyright 2019 the original author or authors.
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
package geb.navigator

import geb.test.GebSpecWithCallbackServer

class DynamicNavigatorSpec extends GebSpecWithCallbackServer {

    private final static String JQUERY_CODE = getClass().getResource("/jquery-1.4.2.min.js").text

    void bodyWithJquery(Closure closure) {
        html {
            head {
                script(type: "text/javascript") {
                    mkp.yieldUnescaped JQUERY_CODE
                }
            }
            body {
                closure.delegate = delegate
                closure.resolveStrategy = DELEGATE_FIRST
                closure.call()
            }
        }
    }

    def "selector based dynamic navigator"() {
        given:
        bodyWithJquery {
            div("div")
        }

        and:
        def nonDynamic = $("div")
        def dynamic = $("div", dynamic: true)

        when:
        $("body").jquery.append("<div>div</div>")

        then:
        nonDynamic.size() == 1
        dynamic.size() == 2
    }

    def "dynamic attribute is interpreted using Groovy truth regardless of type"() {
        given:
        bodyWithJquery {
            div("div")
        }

        and:
        def dynamic = $("div", dynamic: "true")

        when:
        $("body").jquery.append("<div>div</div>")

        then:
        dynamic.size() == 2
    }

    def "attribute based dynamic navigator"() {
        given:
        bodyWithJquery {
            input(type: "text")
        }

        and:
        def nonDynamic = $(type: "text")
        def dynamic = $(type: "text", dynamic: true)

        when:
        $("body").jquery.append('<input type="text">')

        then:
        nonDynamic.size() == 1
        dynamic.size() == 2
    }

    def "selector and index based dynamic navigator"() {
        given:
        bodyWithJquery {
            div("first")
            div("second")
        }

        and:
        def nonDynamic = $("div", 1)
        def dynamic = $("div", 1, dynamic: true)

        when:
        $("div", 0).jquery.after('<div>inserted</div>')

        then:
        nonDynamic.text() == "second"
        dynamic.text() == "inserted"
    }

}
