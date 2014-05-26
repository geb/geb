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
import spock.lang.Unroll
import javax.servlet.http.HttpServletRequest

class ViaSpec extends GebSpecWithServer {
	def setupSpec() {
		responseHtml { HttpServletRequest request ->
			body {
				if (!request.parameterMap.hideA) {
					div id: 'a'
				}
				if (request.parameterMap.showB) {
					div id: 'b'
				}
			}
		}
	}

	def "verify our server is configured correctly"() {
		when:
		go '/'

		then:
		$('#a')
		!$('#b')

		when:
		go '/?hideA=true'

		then:
		!$('#a')
		!$('#b')
	}

	def "verify at checking works"() {
		when:
		via ViaSpecPageA
		and:
		at ViaSpecPageB

		then:
		PowerAssertionError error = thrown()
		error.message.contains('div')
	}

	def "successful at verification modifies browser's page instance"() {
		given:
		go ''

		when:
		at ViaSpecPageA

		then:
		page.getClass() == ViaSpecPageA
	}

	def "verify isAt() works"() {
		when:
		via ViaSpecPageA

		then:
		isAt ViaSpecPageA
		!isAt(ViaSpecPageB)
		!isAt(ViaSpecPageB)
	}

	def "when isAt() returns true it also modifies browser's page instance"() {
		given:
		go ''

		expect:
		isAt ViaSpecPageA
		page.getClass() == ViaSpecPageA

		and:
		!isAt(ViaSpecPageB)
		page.getClass() == ViaSpecPageA
	}

	@Unroll
	def "verify to() asserts that we are at the expected page - #scenario"() {
		when:
		to(* args)

		then:
		PowerAssertionError error = thrown()
		error.message.contains('div')

		where:
		scenario                      | args
		'simple call'                 | [ViaSpecPageB]
		'call with map'               | [[hideA: true], ViaSpecPageA]
		'call with parameter'         | [ViaSpecPageA, true]
		'call with parameter and map' | [[hideA: true], ViaSpecPageA, true]
	}

	@Unroll
	def "verify to() succeeds when we are at the expected page - #scenario"() {
		when:
		def newPage = to(* args)

		then:
		notThrown(PowerAssertionError)
		newPage in args.find { it instanceof Class }

		where:
		scenario                      | args
		'simple call'                 | [ViaSpecPageA]
		'call with map'               | [[showB: true], ViaSpecPageB]
		'call with parameter'         | [ViaSpecPageB, true]
		'call with parameter and map' | [[showB: true], ViaSpecPageB, true]
	}

	@Unroll
	def "via() returns a page instance - #scenario"() {
		expect:
		via(* args) in args.find { it instanceof Class }

		where:
		scenario                      | args
		'simple call'                 | [ViaSpecPageA]
		'call with map'               | [[showB: true], ViaSpecPageB]
		'call with parameter'         | [ViaSpecPageB, true]
		'call with parameter and map' | [[showB: true], ViaSpecPageB, true]
	}

	def 'at() returns an instance of a page if it succeeds'() {
		when:
		via ViaSpecPageA

		then:
		at(ViaSpecPageA) in ViaSpecPageA
	}
}

class ViaSpecPageA extends Page {
	static at = { div }
	static content = {
		div(required: false) { $("#a") }
	}

	String convertToPath(param) {
		return param ? "?hideA=$param" : ''
	}
}

class ViaSpecPageB extends Page {
	static at = { div }
	static content = {
		div(required: false) { $("#b") }
	}

	String convertToPath(param) {
		return param ? "?showB=$param" : ''
	}
}

class ViaSpecPageC extends Page {
	static at = { false }
}
