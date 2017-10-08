/*
 * Copyright 2012 the original author or authors.
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

import geb.Browser
import geb.Page
import geb.waiting.Wait
import spock.lang.Specification
import spock.lang.Unroll

class EmptyNavigatorSpec extends Specification {

    EmptyNavigator navigator
    Browser browser = Mock(Browser)

    def setup() {
        navigator = new EmptyNavigator(browser)
    }

    def find() {
        expect:
        navigator.find("foo") instanceof EmptyNavigator
        navigator.find("foo", 0) instanceof EmptyNavigator
        navigator.find("foo", 0..1) instanceof EmptyNavigator
        navigator.find("foo", 0, a: "b") instanceof EmptyNavigator
        navigator.find("foo", 0..1, a: "b") instanceof EmptyNavigator
        navigator.find("foo", 0..<0, a: "b") instanceof EmptyNavigator
        navigator.find("foo", a: "b") instanceof EmptyNavigator
        navigator.find(0, a: "b") instanceof EmptyNavigator
        navigator.find(0..1, a: "b") instanceof EmptyNavigator
        navigator.find(a: "b") instanceof EmptyNavigator
    }

    def getAt() {
        navigator[1] instanceof EmptyNavigator
        navigator[1..10] instanceof EmptyNavigator
        navigator[0..<0] instanceof EmptyNavigator
    }

    @Unroll('click() for args: #args')
    def click() {
        when:
        navigator.click(*args)

        then:
        UnsupportedOperationException e = thrown()
        e.message == "not supported on empty navigator objects"

        where:
        args << [
                [],
                [Page],
                [Page, new Wait()],
                [new Page()],
                [new Page(), new Wait()],
                [[Page]],
                [[Page], new Wait()]
        ]
    }

}
