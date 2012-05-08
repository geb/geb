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
			res.outputStream << DomDecoratingTestPage.getHTML()
		}
	}

	def setup() {
		go()
	}

	def "base page is not processed"() {
		setup:
		at DomDecoratingTestPage
		expect:
		result == $( selector )?.text()
		where:
		selector << [
			"table#book-details td.__book-title_value",
			"table#book-details td.__publisher_value",
			"table#book-details td.__author_value"
		]
		result << [null, null, null]
	}

	def "processed page is processed"() {
		setup:
		at DomDecoratingProcessedTestPage
		expect:
		result == $( selector )?.text()
		where:
		selector << [
			"table#book-details td.__book-title_value",
			"table#book-details td.__publisher_value",
			"table#book-details td.__author_value"
		]
		result << ["Grails in Action", "Manning Publications Co", "Glen Smith and Peter Ledbrook"]
	}
}

class DomDecoratingTestPage extends Page {

	static String getExpectedTitle() {
		"Dom Decorating Test Page"
	}

	static String getHTML() {

		def jquery = getClass().getResource("/jquery-1.4.2.min.js")

		"""
			<html>
				<head>
					<title>
						${DomDecoratingTestPage.getExpectedTitle()}
					</title>
					<script type="text/javascript">
						${jquery.openStream().text}
					</script>
				</head>
				<body>
					<table id="book-details">
						<tr>
							<th>Book Title</th>
							<td>Grails in Action</td>
						</tr>
						<tr>
							<th>Publisher</th>
							<td>Manning Publications Co</td>
						</tr>
						<tr>
							<th>Author</th>
							<td>Glen Smith and Peter Ledbrook</td>
						</tr>
					</table>
				</body>
			</html>
		"""
	}

	static at = { title =~ DomDecoratingTestPage.getExpectedTitle() }
}

class DomDecoratingProcessedTestPage extends DomDecoratingTestPage {

	static def domDecoratingJsFile = "/geb.domdecorating.DomDecoratingProcessedTestPage.js"
}
