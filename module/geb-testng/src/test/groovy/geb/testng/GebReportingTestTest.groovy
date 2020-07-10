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

import org.testng.annotations.Test

import java.lang.reflect.Method

class GebReportingTestTest extends AbstractGebReportingTestTest {

    @Test(groups = ["GebReportingTestTest"])
    void reportingTestShouldReportOnDemand(Method testMethod) {
        report("ondemand")
        doTestReport(testMethod.name, "ondemand")
    }

    @Test(groups = ["GebReportingTestTest"])
    void reportingTestShouldReportAfterMethodInit() {
        // initialization method that created in order to assert report creation in next method
        config.reportOnTestFailureOnly = false
        methodNumberOfInitTest = methodNumber
    }

    @Test(dependsOnMethods = ["reportingTestShouldReportAfterMethodInit"], groups = ["GebReportingTestTest"])
    void reportingTestShouldReportAfterMethod() {
        // check previous method reporting (reportingTestShouldReportAfterMethodInit)
        config.reportOnTestFailureOnly = false
        report("ondemand")
        doTestReport("reportingTestShouldReportAfterMethodInit", END_OF_METHOD_REPORT_LABEL, methodNumberOfInitTest, 1)
        methodNumberOfInitTest = methodNumber
    }

    @Test(dependsOnMethods = ["reportingTestShouldReportAfterMethod"], groups = ["GebReportingTestTest"])
    void reportingTestShouldReportAfterMethodAndOnDemand() {
        // check previous method reporting (reportingTestShouldReportAfterMethod)
        doTestReport("reportingTestShouldReportAfterMethod", "ondemand", methodNumberOfInitTest, 1)
        doTestReport("reportingTestShouldReportAfterMethod", END_OF_METHOD_REPORT_LABEL, methodNumberOfInitTest, 2)
    }

}