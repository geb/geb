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

class NumberInput extends TextLikeInput {

    final String inputType = "number"

    void setNumber(int number) {
        value(number)
    }

    Integer getNumber() {
        String value = value()
        value ? Integer.valueOf(value) : null
    }

    Integer getMin() {
        String minString = attr("min")
        minString ? Integer.valueOf(minString) : null
    }

    Integer getMax() {
        String maxString = attr("max")
        maxString ? Integer.valueOf(maxString) : null
    }

}
