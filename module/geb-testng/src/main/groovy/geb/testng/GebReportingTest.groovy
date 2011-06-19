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

import geb.report.Reporter
import geb.report.ScreenshotAndPageSourceReporter
import org.testng.ITestResult
import org.testng.TestListenerAdapter
import org.testng.annotations.Listeners
import org.testng.annotations.BeforeMethod

@Listeners([GebTestListener.class])
class GebReportingTest extends GebTest {

	static private testCounters = [:]
	static private testCleanFlags = [:]
	
	private instanceTestCounter = 1
	def testMethodName = ""
	
	void report(String label = "") {
		browser.report("${getTestCounterValue()}-${instanceTestCounter++}-${testMethodName}-${label}")
	}

	/**
	 * Called by GebTestListener, should not be called by users.
	 */
	void resetGebTestCounter() {
		instanceTestCounter = 1
	}
	
	/**
	 * Called by GebTestListener, should not be called by users.
	 */
	void setGebTestMethodName(String testMethodName) {
		this.testMethodName = testMethodName
	}
	
	@BeforeMethod
	void setupReporting() {
		reportGroup getClass()
		incrementTestCounterValue()

		// We need to clean the inner reports dir just once for this class so we have to
		// use this static tracking data to see if we are about to run the first test.
		def key = getKeyNameForTracking()
		if (!testCleanFlags.containsKey(key)) {
			testCleanFlags[key] = true
			cleanReportGroupDir()
		}
	}

	private incrementTestCounterValue() {
		def key = getKeyNameForTracking()
		if (testCounters.containsKey(key)) {
			testCounters[key] = ++testCounters[key]
		} else {
			testCounters[key] = 1
		}
	}

	private getTestCounterValue() {
		testCounters[getKeyNameForTracking()] ?: 1
	}
	
	private getKeyNameForTracking() {
		getClass().name
	}
}

class GebTestListener extends TestListenerAdapter {

	@Override
	void onTestStart(ITestResult tr) {
		def testInstance = tr.instance
		if (testInstance instanceof GebReportingTest) {
			testInstance.resetGebTestCounter()
			testInstance.setGebTestMethodName(tr.method.methodName)
		}
	}
	
	@Override
	void onTestSuccess(ITestResult tr) {
		createReport(tr)
	}

	@Override
	void onTestFailure(ITestResult tr) {
		createReport(tr)
	}

	private void createReport(ITestResult tr) {
		def testInstance = tr.instance
		if (testInstance instanceof GebReportingTest) {
			testInstance.report("end")
		}
	}

}