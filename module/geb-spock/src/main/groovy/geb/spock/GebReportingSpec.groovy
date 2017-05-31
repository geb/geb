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

import geb.junit4.rule.FailureTracker
import geb.report.ReporterSupport
import spock.lang.*
import org.junit.Rule
import org.junit.rules.TestName

class GebReportingSpec extends GebSpec {

    @Rule
    FailureTracker failureTracker

    // Ridiculous name to avoid name clashes
    @Rule
    TestName gebReportingSpecTestName
    protected int gebReportingPerTestCounter = 1
    @Shared
    protected int gebReportingSpecTestCounter = 1

    def setupSpec() {
        reportGroup getClass()
        cleanReportGroupDir()
    }

    def setup() {
        reportGroup getClass()
    }

    def cleanup() {
        if (_browser && !browser.config.reportOnTestFailureOnly) {
            report "end"
        }

        ++gebReportingSpecTestCounter
    }

    void reportFailure() {
        if (_browser) {
            report "failure"
        }
    }

    void report(String label = "") {
        browser.report(createReportLabel(label))
    }

    String createReportLabel(String label = "") {
        ReporterSupport.toTestReportLabel(gebReportingSpecTestCounter, gebReportingPerTestCounter++, gebReportingSpecTestName.methodName, label)
    }

}
