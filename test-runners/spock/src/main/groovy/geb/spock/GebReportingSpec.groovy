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
import org.junit.Rule
import org.junit.rules.TestName

class GebReportingSpec extends GebSpec {

	// Ridiculous name to avoid name clashes
	@Rule _gebReportingSpecTestName = new TestName()
	@Shared _gebReportingSpecTestCounter = 0
	
	def setup() {
		def reportDir = getClassReportDir()
		if (reportDir?.exists() && _gebReportingSpecTestCounter == 0) {
			if (!reportDir.deleteDir()) {
				throw new IllegalStateException("Could not clean class report dir '${reportDir}'")
			}
		}
	}
	
	def cleanup() {
		def testCount = ++_gebReportingSpecTestCounter
		def reportDir = getClassReportDir()
		
		if (reportDir) {
			if (!reportDir.exists() && !reportDir.mkdirs()) {
				throw new IllegalStateException("Could not create class report dir '${reportDir}'")
			}
			
			def extension = getPageSourceFileExtension()
			def fileName = "${testCount}-${_gebReportingSpecTestName.methodName}.$extension"
			writePageSource(new File(reportDir, fileName))
		}
	}

	def getReportDir() {
		null
	}
	
	protected writePageSource(File file) {
		file << (getBrowser().driver.pageSource ?: " -- no page source --")
	}
	
	protected getPageSourceFileExtension() {
		"html"
	}
	
	protected getClassReportDir() {
		def reportDir = getReportDir()
		reportDir ? new File(reportDir, this.class.name.replace('.', '/')) : null
	}

}