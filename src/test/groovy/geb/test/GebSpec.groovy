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
import com.gargoylesoftware.htmlunit.WebClient
import org.openqa.selenium.htmlunit.HtmlUnitDriver

abstract class GebSpec extends Specification {

	@Rule _testName = new TestName()
	@Shared _testCounter = 0
	
	@Shared browser
	
	def setup() {
		if (browser == null) {
			browser = createBrowser()
		}
		def reportDir = getClassReportDir()
		if (reportDir?.exists() && _testCounter == 0) {
			if (!reportDir.deleteDir()) {
				throw new IllegalStateException("Could not clean class report dir '${reportDir}'")
			}
		}
	}
	
	def methodMissing(String name, args) {
		browser."$name"(*args)
	}

	def propertyMissing(String name) {
		browser."$name"
	}

	def propertyMissing(String name, value) {
		browser."$name" = value
	}
	
	def createBrowser() {
		new Browser(createDriver(), getBaseUrl())
	}
	
	def createDriver() {
		new HtmlUnitDriver()
	}
	
	def getBaseUrl() {
		null
	}
		
	def getReportDir() {
		new File("target/responses")
	}
	
	def cleanup() {
		def testCount = ++_testCounter
		def reportDir = getClassReportDir()
	
		if (!reportDir.exists() && !reportDir.mkdirs()) {
			throw new IllegalStateException("Could not create class report dir '${reportDir}'")
		}
		
		def extension = getFileExtension()
		def fileName = "${testCount}-${_testName.methodName}.$extension"
		def output = new File(reportDir, fileName)
		output << browser.driver.pageSource
	}

	private getFileExtension(response) {
		'html'
	}
		
	private getClassReportDir() {
		def reportDir = getReportDir()
		reportDir ? new File(reportDir, this.class.name.replace('.', '/')) : null
	}
	
}