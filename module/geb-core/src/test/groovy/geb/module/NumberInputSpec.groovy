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
package geb.module

import spock.lang.Unroll

class NumberInputSpec extends NumberLikeInputSpec {

    def setup() {
        html {
            input(type: "number", min: "-2.5", max: "2.5", step: "0.5")
        }
    }

    NumberInput getInput() {
        $("input").module(NumberInput)
    }

    def 'unset'() {
        expect:
        verifyAll {
            input.value() == ""
            input.number == null
        }
    }

    @Unroll
    def 'set #number'() {
        when:
        input.number = number

        then:
        input.number == number

        and:
        input.value() == "${number}"

        where:
        number << [-2.5, 0, 1, 2.5]
    }

    def 'get min, max and step'() {
        expect:
        input.min == -2.5

        and:
        input.max == 2.5

        and:
        input.step == 0.5
    }

    def "can use left shift on the module"() {
        when:
        input << "2.1"

        then:
        input.number == 2.1
    }

}
