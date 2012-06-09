/* Copyright 2009 the original author or authors.
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
package geb.domdecorating

import geb.Page;
import geb.test.GebSpecWithServer

class DomDecoratingSupportSpec extends GebSpecWithServer {

	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << DefaultDecoratedPage.getHTML()
		}
	}

	def setup() {
		go()
	}

	def getSelectors() {
		[
			"table#book-details td.__book-title_value",
			"table#book-details td.__publisher_value",
			"table#book-details td.__author_value"
		]
	}

	def getDecoratedResults() {
		[
			"Grails in Action",
			"Manning Publications Co",
			"Glen Smith and Peter Ledbrook"
		]
	}
	
	def "non-decorated page is not decorated"() {
		setup:
		at NonDecoratedPage
		
		expect:
		result == $( selector )?.text()
		
		where:
		selector << getSelectors()
		result << [null, null, null]
	}

	def "default-decorated page is decorated"() {
		setup:
		at DefaultDecoratedPage
		
		expect:
		result == $( selector )?.text()
		
		where:
		selector << getSelectors()
		result << getDecoratedResults()
	}
	
	def "single-file-decorated page is decorated"() {
		setup:
		at SingleFileDecoratedPage
		
		expect:
		result == $( selector )?.text()
		
		where:
		selector << getSelectors()
		result << getDecoratedResults()
	}
	
	def "multi-file-decorated page is decorated"() {
		setup:
		at MultiFileDecoratedPage
		
		expect:
		result == $( selector )?.text()
		
		where:
		selector << getSelectors()
		result << getDecoratedResults()
	}
}

