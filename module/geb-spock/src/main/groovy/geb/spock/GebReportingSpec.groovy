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
 *
 * Tests implemented by FrancisDelaPena 11/22/15.
 *
 */
package geb.spock

import geb.report.ReporterSupport
import spock.lang.*
import org.junit.Rule
import org.junit.rules.TestName

class GebReportingSpec extends GebSpec {

    @Rule
    MethodExecutionRule failTracker = new MethodExecutionRule()
    // Ridiculous name to avoid name clashes
    @Rule
    TestName gebReportingSpecTestName
    private int gebReportingPerTestCounter = 1
    @Shared
    private int gebReportingSpecTestCounter = 1

    def setupSpec() {
        reportGroup getClass()
        cleanReportGroupDir()
    }

    def setup() {
        reportGroup getClass()
        config.reportOnTestFailureOnly = false //In TestNG's GebReportingTestTest, they configure it to false in the "setup", while switching it to true in a later test.
    }

    @Test //Newly-added test by FrancisDelaPena
    def passingTest() { //should test while false checker is off
        assert report != null //ensures report will send if test result checker is off
    }

    @Test //Newly-added test by FrancisDelaPena
    def failingTest() { //Test containing config.reportOnTestFailureOnly = true
        config.reportTestOnFailureOnly = true //sets result checker on
        //still trying to figure out exactly how to do passing test.  Most likely using TestNG as a base
        /*
         * TestNG's test for the true checker produces two different results.
         * First, with a passed test, asserting that any report sent is null.
         * Second, with a failed test that successfully sends a report.
         */
    }

    def cleanup() {
        report "end"
        ++gebReportingSpecTestCounter
    }

    void report(String label = "") {
        if (!browser.config.reportOnTestFailureOnly || failTracker.failedTests.contains(gebReportingSpecTestName.methodName)) {
            browser.report(ReporterSupport.toTestReportLabel(gebReportingSpecTestCounter, gebReportingPerTestCounter++, gebReportingSpecTestName.methodName, label))
        }
    }

}
