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
import geb.test.CrossBrowser
import spock.lang.Specification
import spock.lang.Unroll

@CrossBrowser
class EmptyNavigatorSpec extends Specification {

	EmptyNavigator navigator
	Browser browser = Mock(Browser)

	def setup() {
		navigator = new EmptyNavigator(browser)
	}

	def find() {
		expect:
		navigator.find("foo").is(navigator)
		navigator.find("foo", 0).is(navigator)
		// navigator.find("foo", 0..1).is(navigator)
		navigator.find("foo", 0, a: "b").is(navigator)
		navigator.find("foo", 0..1, a: "b").is(navigator)
		navigator.find("foo", 0..<0, a: "b").is(navigator)
		navigator.find("foo", a: "b").is(navigator)
		navigator.find(0, a: "b").is(navigator)
		// navigator.find(0..1, a: "b").is(navigator)
		navigator.find(a: "b").is(navigator)
	}

	def getAt() {
		navigator[1].is(navigator)
		navigator[1..10].is(navigator)
		navigator[0..<0].is(navigator)
	}

	@Unroll
	def 'does not support checking of the #property property'() {
		when:
		navigator.getProperty(property)

		then:
		UnsupportedOperationException e = thrown()
		e.message == "Cannot check value of '$attribute' attribute for an EmptyNavigator"

		where:
		property   | attribute
		'readOnly' | 'readonly'
		'editable' | 'readonly'
		'enabled'  | 'disabled'
		'disabled' | 'disabled'
	}
}
