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

package geb

import geb.test.GebSpecWithServer
import org.codehaus.groovy.runtime.powerassert.PowerAssertionError

class ToAndAtSpec extends GebSpecWithServer {
	def setupSpec() {
		responseHtml {
			body {
				div id: 'a'
			}
		}
	}

	def "verify our server is configured correctly"() {
		when:
		go '/'

		then:
		$('#a')
		!$('#b')
	}

	def "verify at checking works"() {
		when:
		to ToAndAtSpecPageA
		and:
		at ToAndAtSpecPageB

		then:
		PowerAssertionError error = thrown()
		error.message.contains('div')
	}

	def "verify isAt() works"() {
		when:
		to ToAndAtSpecPageA

		then:
		isAt ToAndAtSpecPageA
		!isAt(ToAndAtSpecPageB)
		!isAt(ToAndAtSpecPageB)
	}
}

class ToAndAtSpecPageA extends Page {
	static at = { div }
	static content = {
		div { $("#a") }
	}
}

class ToAndAtSpecPageB extends Page {
	static at = { div }
	static content = {
		div(required: false) { $("#b") }
	}
}

class ToAndAtSpecPageC extends Page {
	static at = { false }
}
