/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.testng

import geb.report.ReporterSupport
import java.lang.reflect.Method
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod

class GebReportingTest extends GebTest {

	public static final END_OF_METHOD_REPORT_LABEL = "end"
	private testMethodNumber = 0
	private reportNumberInTestMethod = 1
	def testMethodName = ""

	void report(String label = "") {
		browser.report(ReporterSupport.toTestReportLabel(testMethodNumber, reportNumberInTestMethod++, testMethodName, label))
	}

	@BeforeClass
	void initReportGroupDir() {
		reportGroup getClass()
		cleanReportGroupDir()
	}

	@BeforeMethod
	void setupReporting(Method method) {
		reportNumberInTestMethod = 1
		++testMethodNumber
		testMethodName = method.name
	}

	@AfterMethod
	void reportingAfter(ITestResult testResult) {
		if(!config.reportOnTestFailureOnly || ITestResult.FAILURE == testResult.status)
			report(END_OF_METHOD_REPORT_LABEL)
	}

}