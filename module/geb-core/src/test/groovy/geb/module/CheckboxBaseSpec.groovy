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

import geb.error.InvalidModuleBaseException
import geb.test.GebSpecWithServer

class CheckboxBaseSpec extends GebSpecWithServer {
	def "can base the module on input of type checkbox"() {
		given:
		html {
			input(type: checkbox)
		}

		when:
		$("input").module(Checkbox)

		then:
		noExceptionThrown()
	}

	def "can base the module on an empty navigator"() {
		given:
		html {
		}

		when:
		$("input").module(Checkbox)

		then:
		noExceptionThrown()
	}

	def "creating the module for anything other than input results in an exception"() {
		given:
		html {
			div("div")
		}

		when:
		$("div").module(Checkbox)

		then:
		InvalidModuleBaseException e = thrown()
		e.message == "Specified base element for ${Checkbox.name} module was 'div' but only input is allowed as the base element."
	}

	def "creating the module for an input of type that is not checkbox results in an exception"() {
		given:
		html {
			input(type: "text")
		}

		when:
		$("input").module(Checkbox)

		then:
		InvalidModuleBaseException e = thrown()
		e.message == "Specified base element for ${Checkbox.name} module was an input of type 'text' but only input of type checkbox is allowed as the base element."
	}
}
