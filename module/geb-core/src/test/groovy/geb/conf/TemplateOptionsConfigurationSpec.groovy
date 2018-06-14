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

import geb.Page
import geb.test.GebSpecWithCallbackServer

class TemplateOptionsConfigurationSpec extends GebSpecWithCallbackServer {

    def "can configure all content to be cached by default"() {
        given:
        html {}
        browser.config.templateCacheOption = true

        when:
        to ValueHoldingPage

        then:
        notExplicitlyCachedValue == 1

        when:
        value = 2

        then:
        notExplicitlyCachedValue == 1
    }

    def "can configure all content to be waited on by default"() {
        given:
        html {
            head {
                script(type: "text/javascript", """
                    setTimeout(function() {
                        var p = document.createElement("p");
                        p.innerHTML = "Dynamic paragraph";
                        p.className = "dynamic";
                        document.body.appendChild(p);
                    }, 75);
                """)
            }
        }

        and:
        browser.config.templateWaitOption = true

        when:
        to DynamicPageWithWaiting

        then:
        dynamicallyAdded
    }

}

class ValueHoldingPage extends Page {
    def value = 1
    static content = {
        notExplicitlyCachedValue { value }
    }
}

class DynamicPageWithWaiting extends Page {
    static content = {
        dynamicallyAdded { $("p.dynamic") }
    }
}
