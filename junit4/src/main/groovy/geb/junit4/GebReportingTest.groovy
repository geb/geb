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
package geb.junit4

import geb.report.*
import org.junit.After
import org.junit.Rule
import org.junit.rules.TestName

class GebReportingTest extends GebTest {

	// Ridiculous name to avoid name clashes
	private _gebReportingTestCounter = 0
	private _getReportingTestReporter = null
	@Rule
	public TestName _gebReportingTestTestName = new TestName()
	
	@After
 	void writeGebReport() {
		if (_gebReportingTestCounter++ == 0) {
			_getReportingTestReporter = createReporter()
		}
		_getReportingTestReporter?.writeReport("${_gebReportingTestCounter}-${_gebReportingTestTestName.methodName}", getBrowser())
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