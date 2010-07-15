/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *			http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geb.test

import geb.*
import spock.lang.*
import org.junit.Rule
import org.junit.rules.TestName

abstract class GebSpec extends Specification {

	@Rule _testName = new TestName()
	@Shared _testCounter = 0
	
	@Shared driver
	
	def setup() {
		if (driver == null) {
			driver = new Driver(createGeb())
		}
		def reportDir = getClassReportDir()
		if (reportDir?.exists() && _testCounter == 0) {
			if (!reportDir.deleteDir()) {
				throw new IllegalStateException("Could not clean class report dir '${reportDir}'")
			}
		}
	}
	
	def methodMissing(String name, args) {
		driver."$name"(*args)
	}

	def propertyMissing(String name) {
		driver."$name"
	}

	def propertyMissing(String name, value) {
		driver."$name" = value
	}
	
	def createGeb() {
		new Geb(getBaseUrl())
	}
	
	def getBaseUrl() {
		""
	}
		
	def getReportDir() {
		new File("target/responses")
	}
	
	def cleanup() {
		def testCount = ++_testCounter
		def reportDir = getClassReportDir()
	
		if (reportDir && driver?.response) {
			if (!reportDir.exists() && !reportDir.mkdirs()) {
				throw new IllegalStateException("Could not create class report dir '${reportDir}'")
			}
			
			def extension = getFileExtension(driver.response)
			def fileName = "${testCount}-${_testName.methodName}.$extension"
			def output = new File(reportDir, fileName)
			output << response.contentAsStream
		}
	}

	private getFileExtension(response) {
		response.contentType.split('/').toList().last() ?: 'html'
	}
	
	private getClassReportDir() {
		def reportDir = getReportDir()
		reportDir ? new File(reportDir, this.class.name.replace('.', '/')) : null
	}
	
}