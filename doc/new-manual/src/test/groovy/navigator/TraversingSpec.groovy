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

class TraversingSpec extends GebSpecWithCallbackServer {

    def "siblings"() {
        given:
        responseHtml """
            <html>
                // tag::siblings_html[]
                <div class="a">
                    <div class="b">
                        <p class="c"></p>
                        <p class="d"></p>
                        <p class="e"></p>
                    </div>
                    <div class="f"></div>
                </div>
                // end::siblings_html[]
            </html>
        """

        when:
        go()

        then:
        // tag::siblings[]
        $("p.d").previous() == $("p.c")
        $("p.e").prevAll() == $("p.c").add("p.d")
        $("p.d").next() == $("p.e")
        $("p.c").nextAll() == $("p.d").add("p.e")
        $("p.d").parent() == $("div.b")
        $("p.c").siblings() == $("p.d").add("p.e")
        $("div.a").children() == $("div.b").add("div.f")
        // end::siblings[]
    }

    def "next"() {
        given:
        responseHtml """
            <html>
                // tag::next_html[]
                <div class="a">
                    <p class="a"></p>
                    <p class="b"></p>
                    <p class="c"></p>
                </div>
                // end::next_html[]
            </html>
        """

        when:
        go()

        then:
        // tag::next[]
        $("p").next() == $("p.b").add("p.c")
        // end::next[]
        // tag::next_with_args[]
        $("p").next(".c") == $("p.c").add("p.c")
        $("p").next(class: "c") == $("p.c").add("p.c")
        $("p").next("p", class: "c") == $("p.c").add("p.c")
        // end::next_with_args[]
    }

    def "parent and closest"() {
        given:
        responseHtml """
            <html>
                // tag::parent_closest_html[]
                <div class="a">
                    <div class="b">
                        <p></p>
                    </div>
                </div>
                // end::parent_closest_html[]
            </html>
        """

        when:
        go()

        then:
        // tag::parent[]
        $("p").parent(".b") == $("div.b")
        $("p").parent(class: "b") == $("div.b")
        $("p").parent("div", class: "b") == $("div.b")
        // end::parent[]
        // tag::closest[]
        $("p").closest(".a") == $("div.a")
        $("p").closest(class: "a") == $("div.a")
        $("p").closest("div", class: "a") == $("div.a")
        // end::closest[]
    }

    def "next until"() {
        given:
        responseHtml """
            <html>
                // tag::next_until_html[]
                <div class="a"></div>
                <div class="b"></div>
                <div class="c"></div>
                <div class="d"></div>
                // end::next_until_html[]
            </html>
        """

        when:
        go()

        then:
        // tag::next_until[]
        $(".a").nextUntil(".d") == $("div.b").add("div.c")
        $(".a").nextUntil(class: "d") == $("div.b").add("div.c")
        $(".a").nextUntil("div", class: "d") == $("div.b").add("div.c")
        // end::next_until[]
    }
}