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
package geb

import geb.test.GebSpecWithCallbackServer

class ModuleAsAModuleBaseSpec extends GebSpecWithCallbackServer {
    def setup() {
        html {
            div(class: "top") {
                div(class: "nested")
            }
        }
    }

    def "can construct a module that uses a module as base which does not have a page as the owner"() {
        given:
        page NestedModulesPage

        when:
        top.leaf

        then:
        noExceptionThrown()
    }
}

class NestedModulesPage extends Page {
    static content = {
        top { module TopLevelModule }
    }
}

class TopLevelModule extends Module {
    static base = { $("div.top") }
    static content = {
        nested { module Module, $("div.nested") }
        leaf { module Module, nested }
    }
}
