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
    }

    /*
     * Based on TestNG's GebReportingTestTest and Spock's GebReportingSpecSpec, Tests will appear here.  Will be
     * finished by tomorrow morning.
     * - Francis (11/21/15 22:43)
     *
     * Just woke up.  Getting started.  Interruption may occur so may not be done until early afternoon, but will be
     * for sure.  Also, will remove these comments upon final submission today.
     * - Francis (11/22/15 9:19)
     */

    def test1() { //Will change name, only a placeholder.  Test containing config.reportOnTestFailureOnly = true

    }

    def test2() { //Will change name, only a placeholder.  Test containing config.reportOnTestFailureOnly = false
        
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
