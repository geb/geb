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

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer

@CrossBrowser
class FindViaTextSpec extends GebSpecWithServer {

	def setup() {
		responseHtml {
			table {
				tr {
					td "Single-Line Content"
					td {
						mkp.yieldUnescaped "First Line<br/>Second Line"
					}
				}
			}
		}
		go()
	}

	def 'Get single-line td with text'() {
		expect:
		$('td', text: 'Single-Line Content')
	}

	def 'Get single-line td using contains'() {
		expect:
		$('td', text: contains('Single-Line'))
	}

	def 'Get single-line td using startsWith'() {
		expect:
		$('td', text: startsWith('Single-Line'))
	}

	def 'Get multi-line td with text'() {
		expect:
		$('td', text: "First Line\nSecond Line")
	}

	def 'Get multi-line td using startsWith'() {
		expect:
		$('td', text: startsWith('First Line'))
	}

	def 'Get multi-line td using contains (match first line)'() {
		expect:
		$('td', text: contains('First Line'))
	}

	def 'Get multi-line td using contains (match second line)'() {
		expect:
		$('td', text: contains('Second Line'))
	}

}
