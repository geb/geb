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
package geb.navigator

import geb.test.CrossBrowser
import geb.test.GebSpecWithCallbackServer

@CrossBrowser
class NavigatorEqualsSpec extends GebSpecWithCallbackServer {

    @SuppressWarnings("ComparisonWithSelf")
    def "empty navigator"() {
        expect:
        $(".foo") != null
        $(".foo") == $(".foo")
        $(".foo") == $(".bar")

        when:
        def emptyNavigatorInstance = $(".buzz")

        then:
        emptyNavigatorInstance == emptyNavigatorInstance
    }

    def "non empty navigator"() {
        given:
        html {
            p(class: "a")
            p(class: "b z")
            p(class: "c z")
        }

        expect:
        $("p") != null
        $("p") != $(".z")
        $(".z") != $(".c").add(".b")

        $("p") == $("p")
        $(".a") == $(".a")
        $(".z") == $(".b").add(".c")
        $(".z") == $("p").not(".a")
    }

    def "empty and non empty navigator"() {
        given:
        html {
            p("a")
        }

        expect:
        $("p") != $(".foo")
        $(".foo") != $("p")

    }
}