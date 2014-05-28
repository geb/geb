/*
 * Copyright 2014 the original author or authors.
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

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer

@CrossBrowser
class NavigatorElementsSpec extends GebSpecWithServer {

	def getElement() {
		given:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		expect:
		$("div").getElement(0).getAttribute("id") == "a"
		$("div").getElement(1).getAttribute("id") == "b"
		$("foo").getElement(0) == null
		$("foo").getElement(10) == null
	}

	def firstElement() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("div").firstElement().getAttribute("id") == "a"
		$("foo").firstElement() == null
	}

	def lastElement() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("div").lastElement().getAttribute("id") == "b"
		$("foo").lastElement() == null
	}

	def allElements() {
		when:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		then:
		$("div").allElements()*.getAttribute("id") == ["a", "b", "c"]
	}
}
