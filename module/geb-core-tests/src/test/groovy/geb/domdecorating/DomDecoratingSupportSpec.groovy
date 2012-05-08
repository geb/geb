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
	
	def "at expected page"() {
		expect:
			at DomDecoratingTestPage
	}
}

class DomDecoratingTestPage extends Page {
	
	static String getExpectedTitle() {
		"Dom Decorating Test Page"
	}
	
	static String getHTML() {
		"""
			<html>
				<head>
					<title>
						${DomDecoratingTestPage.getExpectedTitle()}
					</title>
				</head>
				<body>
					<table id="book-details">
						<tr>
							<th>
								Book Title
							</th>
							<td>
								Grails in Action
							</td>
						</tr>
						<tr>
							<th>
								Publisher
							</th>
							<td>
								Manning Publications Co
							</td>
						</tr>
						<tr>
							<th>
								Author
							</th>
							<td>
								Glen Smith and Peter Ledbrook
							</td>
						</tr>
					</table>
				</body>
			</html>
		"""
	}
	
	static at = { title =~ DomDecoratingTestPage.getExpectedTitle() }
}