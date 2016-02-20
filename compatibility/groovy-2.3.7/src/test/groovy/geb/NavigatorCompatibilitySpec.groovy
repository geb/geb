/*
 * Copyright 2016 the original author or authors.
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
package geb

import geb.test.GebSpecWithCallbackServer
import spock.lang.Issue

class NavigatorCompatibilitySpec extends GebSpecWithCallbackServer {
    def setupSpec() {
        responseHtml {
            body {
                input(type: "text", id: "my-input", value: "val")
            }
        }
    }

    def setup() {
        go()
    }

    @Issue("https://github.com/geb/issues/issues/422")
    def 'can set form input field value with Groovy 2.3.7'() {
        when:
        $('#my-input').value('newValue')

        then:
        $('#my-input').value() == 'newValue'
    }
}