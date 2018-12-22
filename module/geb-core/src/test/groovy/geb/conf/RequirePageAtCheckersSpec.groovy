/*
 * Copyright 2018 the original author or authors.
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
package geb.conf

import geb.PageWithoutAtChecker
import geb.error.UndefinedAtCheckerException
import geb.test.GebSpecWithCallbackServer

class RequirePageAtCheckersSpec extends GebSpecWithCallbackServer {

    def setup() {
        browser.config.requirePageAtCheckers = true
    }

    void "exception is thrown when page at checkers are required but page navigated to does not define one"() {
        when:
        to PageWithoutAtChecker

        then:
        thrown(UndefinedAtCheckerException)
    }

    void "exception is thrown when at checkers are required but page passed to withWindow does not define one"() {
        when:
        withWindow({ true }, page: PageWithoutAtChecker) {}

        then:
        thrown(UndefinedAtCheckerException)
    }

    void "exception is thrown when at checkers are required but page passed to withNewWindow does not define one"() {
        given:
        html {
            a(href: "/", target: "_blank")
        }

        when:
        withNewWindow({ $("a").click() }, page: PageWithoutAtChecker) {}

        then:
        thrown(UndefinedAtCheckerException)
    }

    void "exception is thrown when at checkers are required but page passed to withFrame does not define one"() {
        given:
        html {
            iframe(name: frameName, src: "/")
        }

        when:
        withFrame(frameName, PageWithoutAtChecker) {}

        then:
        thrown(UndefinedAtCheckerException)

        where:
        frameName = "test-frame"
    }

    void "exception is thrown when at checkers are required but page passed to click does not define one"() {
        given:
        html {
            button()
        }

        when:
        $("button").click(PageWithoutAtChecker)

        then:
        thrown(UndefinedAtCheckerException)
    }

}
