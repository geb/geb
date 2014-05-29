/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
class ExoticAttributeValuesSpec extends GebSpecWithServer {

	def setupSpec() {
		responseHtml {
			div(id: "a:b", "foo")
			div(id: "a b", "bar")
		}

		go()
	}

	def "jsf style ids"() {
		expect:
		$(id: "a:b").text() == "foo"
	}

	def "ids with spaces"() {
		expect:
		$(id: "a b").text() == "bar"
	}

}