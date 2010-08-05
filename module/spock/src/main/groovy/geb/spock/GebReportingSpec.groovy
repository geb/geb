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

import geb.report.*
import spock.lang.*
import org.junit.Rule
import org.junit.rules.TestName

class GebReportingSpec extends GebSpec {

	// Ridiculous name to avoid name clashes
	@Rule _gebReportingSpecTestName = new TestName()
	@Shared _gebReportingSpecTestCounter = 0
	@Shared _getReportingSpecReporter = null
	
	def cleanup() {
		// We have to do this lazily here so the subclass gets a chance to run _some_ code to setup the reporter if need be.
		// If we used setupSpec() that would run before the subclasses setupSpec() and limit the users options.
		if (_gebReportingSpecTestCounter++ == 0) {
			_getReportingSpecReporter = createReporter()
		}
		
		_getReportingSpecReporter?.writeReport("${_gebReportingSpecTestCounter}-${_gebReportingSpecTestName.methodName}", getBrowser())
	}

	/**
	 * Subclasses can override this to use a different reporter
	 */
	Reporter createReporter() {
		def reportDir = getReportDir()
		reportDir ? new PageSourceReporter(reportDir, this.class, true) : null
	}
	
	/**
	 * Subclasses override this to determine where the reports are written
	 */
	File getReportDir() {
		null
	}

}