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
import geb.test.util.GebSpecWithServer

@Stepwise
class GebReportingSpecSpec extends GebSpecWithServer {
	
	static responseText = """
		<html>
		<body>
			<div class="d1" id="d1">d1</div>
		</body>
		</html>
	"""
	
	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << responseText
		}
		
		def reportsDir = getClassReportDir()
		def firstOutputFile = getFirstOutputFile()
		
		if (!reportsDir.exists()) {
			assert reportsDir.mkdirs()
		}
		
		if (!firstOutputFile.exists()) {
			assert firstOutputFile.createNewFile()
		}
		
		// Put some jibberish in this file, so we can test
		// that it was actually removed by the reporting spec
		// setup() method.
		firstOutputFile << "asdfajsdifoamsdfoiawdncwonc"
	}
	
	def getClassReportDir() {
		new File(getReportDir(), this.class.name.replace('.', '/'))
	}
	
	def getFirstOutputFile() {
		new File(getClassReportDir(), "1-a request is made.html")
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
		// If the text equals the response text, the jibberish we put in in setupSpec()
		// was removed, which is what we expect to happen.
		report.text = responseText
	}
	
}