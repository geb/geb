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
package geb.junit3

import geb.report.ReporterSupport

class GebReportingTest extends GebTest {

	static private testCounters = [:]
	static private testCleanFlags = [:]
	private instanceTestCounter = 1

	void report(String label) {
		browser.report(ReporterSupport.toTestReportLabel(getTestCounterValue(), instanceTestCounter++, getName(), label))
	}

	void setUp() {
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

	void tearDown() {
		report "end"
		super.tearDown()
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