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
import org.openqa.selenium.By
import spock.lang.Unroll

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

    @Unroll("#scenario selector based dynamic navigator")
    def "selector based dynamic navigator"() {
        given:
        bodyWithJquery {
            div("div")
        }

        and:
        def nonDynamic = $(selector)
        def dynamic = $(selector, dynamic: true)

        when:
        $("body").jquery.append("<div>div</div>")

        then:
        nonDynamic.size() == 1
        dynamic.size() == 2

        where:
        scenario | selector
        "string" | "div"
        "By"     | By.cssSelector("div")
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

    def "attribute and index based dynamic navigator"() {
        given:
        bodyWithJquery {
            input(type: "text", value: "first")
            input(type: "password", value: "password")
            input(type: "text", value: "second")
        }

        and:
        def nonDynamic = $(1, type: "text")
        def dynamic = $(1, type: "text", dynamic: true)

        when:
        $(type: "password").jquery.after('<input type="text" value="inserted">')

        then:
        nonDynamic.value() == "second"
        dynamic.value() == "inserted"
    }

    def "attribute and range based dynamic navigator"() {
        given:
        bodyWithJquery {
            input(type: "text", value: "first")
            input(type: "password", value: "password")
            input(type: "text", value: "second")
            input(type: "text", value: "third")
        }

        and:
        def nonDynamic = $(1..2, type: "text")
        def dynamic = $(1..2, type: "text", dynamic: true)

        when:
        $(type: "password").jquery.after('<input type="text" value="inserted">')

        then:
        nonDynamic*.value() == ["second", "third"]
        dynamic*.value() == ["inserted", "second"]
    }

    @Unroll("#scenario selector and index based dynamic navigator")
    def "selector and index based dynamic navigator"() {
        given:
        bodyWithJquery {
            div("first")
            div("second")
        }

        and:
        def nonDynamic = $(selector, 1)
        def dynamic = $(selector, 1, dynamic: true)

        when:
        $("div", 0).jquery.after('<div>inserted</div>')

        then:
        nonDynamic.text() == "second"
        dynamic.text() == "inserted"

        where:
        scenario | selector
        "string" | "div"
        "By"     | By.cssSelector("div")
    }

    @Unroll("#scenario selector and range based dynamic navigator")
    def "selector and range based dynamic navigator"() {
        given:
        bodyWithJquery {
            div("first")
            div("second")
            div("third")
            div("fourth")
        }

        and:
        def nonDynamic = $(selector, 1..2)
        def dynamic = $(selector, 1..2, dynamic: true)

        when:
        $("div", 1).jquery.after('<div>inserted</div>')

        then:
        nonDynamic*.text() == ["second", "third"]
        dynamic*.text() == ["second", "inserted"]

        where:
        scenario | selector
        "string" | "div"
        "By"     | By.cssSelector("div")
    }

    @Unroll("#scenario selector, attribute and index based dynamic navigator")
    def "selector, attribute and index based dynamic navigator"() {
        given:
        bodyWithJquery {
            input(type: "text", value: "first")
            input(type: "password", value: "password")
            input(type: "text", value: "second")
        }

        and:
        def nonDynamic = $(selector, 1, type: "text")
        def dynamic = $(selector, 1, type: "text", dynamic: true)

        when:
        $(type: "password").jquery.after('<input type="text" value="inserted">')

        then:
        nonDynamic.value() == "second"
        dynamic.value() == "inserted"

        where:
        scenario | selector
        "string" | "input"
        "By"     | By.cssSelector("input")
    }

    @Unroll("#scenario selector, attribute and range based dynamic navigator")
    def "selector, attribute and range based dynamic navigator"() {
        given:
        bodyWithJquery {
            input(type: "text", value: "first")
            input(type: "password", value: "password")
            input(type: "text", value: "second")
            input(type: "text", value: "third")
        }

        and:
        def nonDynamic = $(selector, 1..2, type: "text")
        def dynamic = $(selector, 1..2, type: "text", dynamic: true)

        when:
        $(type: "password").jquery.after('<input type="text" value="inserted">')

        then:
        nonDynamic*.value() == ["second", "third"]
        dynamic*.value() == ["inserted", "second"]

        where:
        scenario | selector
        "string" | "input"
        "By"     | By.cssSelector("input")
    }

    def "string selector, non-translatable attribute and index based dynamic navigator"() {
        given:
        bodyWithJquery {
            div(id: "first", "matching text")
            div(id: "second", "not matching text")
            div(id: "third", "matching text")
        }

        and:
        def nonDynamic = $("div", 1, text: "matching text")
        def dynamic = $("div", 1, text: "matching text", dynamic: true)

        when:
        $(id: "second").jquery.after('<div id="inserted">matching text</div>')

        then:
        nonDynamic.@id == "third"
        dynamic.@id == "inserted"
    }

    def "string selector, non-translatable attribute and range based dynamic navigator"() {
        given:
        bodyWithJquery {
            div(id: "first", "matching text")
            div(id: "second", "not matching text")
            div(id: "third", "matching text")
            div(id: "fourth", "matching text")
        }

        and:
        def nonDynamic = $("div", 1..2, text: "matching text")
        def dynamic = $("div", 1..2, text: "matching text", dynamic: true)

        when:
        $(id: "second").jquery.after('<div id="inserted">matching text</div>')

        then:
        nonDynamic*.attr("id") == ["third", "fourth"]
        dynamic*.attr("id") == ["inserted", "third"]
    }

    def "attributes filter based dynamic navigator"() {
        given:
        bodyWithJquery {
            input(type: "text")
            input(type: "password")
        }

        and:
        def base = $("input", dynamic: true)
        def nonDynamic = base.filter(type: "text")
        def dynamic = base.filter(type: "text", dynamic: true)

        when:
        $("body").jquery.append('<input type="text">')

        then:
        nonDynamic.size() == 1
        dynamic.size() == 2
    }

    def "selector filter based dynamic navigator"() {
        given:
        bodyWithJquery {
            input(type: "text")
        }

        and:
        def base = $("input", dynamic: true)
        def nonDynamic = base.filter("input")
        def dynamic = base.filter("input", dynamic: true)

        when:
        $("body").jquery.append('<input type="text">')

        then:
        nonDynamic.size() == 1
        dynamic.size() == 2
    }

    def "children selector based dynamic navigator"() {
        given:
        bodyWithJquery {
            div("div")
        }

        and:
        def nonDynamic = $("body").children("div")
        def dynamic = $("body").children("div", dynamic: true)

        when:
        $("body").jquery.append("<div>div</div>")

        then:
        nonDynamic.size() == 1
        dynamic.size() == 2
    }

    def "children attribute based dynamic navigator"() {
        given:
        bodyWithJquery {
            input(type: "text")
        }

        and:
        def nonDynamic = $("body").children(type: "text")
        def dynamic = $("body").children(type: "text", dynamic: true)

        when:
        $("body").jquery.append('<input type="text">')

        then:
        nonDynamic.size() == 1
        dynamic.size() == 2
    }

    def "closest attribute based attribute"() {
        given:
        bodyWithJquery {
            div(id: "grandparent", class: "to-be-selected") {
                div(id: "parent") {
                    div(id: "child", "div")
                }
            }
        }

        and:
        def nonDynamic = $("#child").closest(class: "to-be-selected")
        def dynamic = $("#child").closest(class: "to-be-selected", dynamic: true)

        when:
        $("#parent").jquery.addClass("to-be-selected")

        then:
        nonDynamic.@id == "grandparent"
        dynamic.@id == "parent"
    }

    def "closest selector based attribute"() {
        given:
        bodyWithJquery {
            div(id: "grandparent", class: "to-be-selected") {
                div(id: "parent") {
                    div(id: "child", "div")
                }
            }
        }

        and:
        def nonDynamic = $("#child").closest(".to-be-selected")
        def dynamic = $("#child").closest(".to-be-selected", dynamic: true)

        when:
        $("#parent").jquery.addClass("to-be-selected")

        then:
        nonDynamic.@id == "grandparent"
        dynamic.@id == "parent"
    }

}
