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
package geb.spock

import spock.lang.*
import geb.test.CallbackHttpServer

@Stepwise
class GebReportingSpecSpec extends GebReportingSpec {

	@Shared server = new CallbackHttpServer()

	static responseText = """
		<html>
		<body>
			<div class="d1" id="d1">d1</div>
		</body>
		</html>
	"""

	def setupSpec() {
		server.start()
		server.get = { req, res ->
			res.outputStream << responseText
		}
	}

	def setup() {
		baseUrl = server.baseUrl
		go()
	}

	def getFirstOutputFile() {
		new File(reportGroupDir, "001-001-a request is made-end.html")
	}

	def "a request is made"() {
		given:
		go("/") // make a request
	}

	def "a report should have been created with the response text"() {
		given:
		def report = getFirstOutputFile()
		expect:
		report.exists()
		report.text.startsWith("<?xml")
	}

	def "there should be a second report"() {
		expect:
		reportGroupDir.listFiles().any { it.name.startsWith("002") }
	}

	def cleanupSpec() {
		server.stop()
	}
}