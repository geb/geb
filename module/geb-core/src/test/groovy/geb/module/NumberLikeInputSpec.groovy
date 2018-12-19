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
package geb.module

import geb.test.GebSpecWithCallbackServer

abstract class NumberLikeInputSpec extends GebSpecWithCallbackServer {

    abstract NumberLikeInput getInput()

    def "getting and setting number"() {
        when:
        input.number = 2

        then:
        input.number == 2
    }

    def "can use left shift on the module"() {
        when:
        input << "2.1"

        then:
        input.number == 2.1
    }

    def "getting and setting text on an empty navigator based number input"() {
        given:
        def input = $("i-dont-exist").module(NumberInput)

        when:
        input.number = 3.2

        then:
        noExceptionThrown()
        input.number == null
    }

}
