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

import geb.test.browsers.Chrome
import geb.test.browsers.Firefox
import geb.test.browsers.RequiresRealBrowser
import spock.lang.Unroll

@Chrome
@Firefox
@RequiresRealBrowser // maybe due to https://sourceforge.net/p/htmlunit/bugs/1923/
class RangeInputSpec extends NumberLikeInputSpec {

    def setup() {
        html {
            input(type: "range", min: "0", max: "10", step: "0.1")
        }
    }

    RangeInput getInput() {
        $("input").module(RangeInput)
    }

    def 'unset the value is half'() {
        expect:
        verifyAll {
            input.value() == "5"
            input.number == 5
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
        number << [0, 5.1, 10]
    }

    def 'get min, max and step'() {
        expect:
        input.min == 0

        and:
        input.max == 10

        and:
        input.step == 0.1
    }

}
