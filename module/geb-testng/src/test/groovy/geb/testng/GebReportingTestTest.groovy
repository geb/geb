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
import geb.test.CallbackHttpServer
import java.lang.reflect.Method
import org.testng.ITestResult
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import org.testng.internal.TestResult

class GebReportingTestTest implements GebReportingTestTrait {

    def server = new CallbackHttpServer()

    private methodNumber = 0
    private reportNumberInTest = 1

    private methodNumberOfInitTest = 0

    static responseText = """
        <html>
        <body>
            <div class="d1" id="d1">d1</div>
        </body>
        </html>
    """

    @BeforeClass
    void setUpClass() {
        server.start()
        server.get = { req, res ->
            res.outputStream << responseText
        }
    }

    @BeforeMethod
    void setUp() {
        ++methodNumber
        reportNumberInTest = 1

        config.reportOnTestFailureOnly = false

        browser.baseUrl = server.baseUrl
        go()
    }

    @Test(groups = ["GebReportingTestTest"])
    void reportingTestShouldReportOnDemand(Method testMethod) {
        report("ondemand")
        doTestReport(testMethod.name, "ondemand")
    }

    @Test(groups = ["GebReportingTestTest"])
    void reportingTestShouldReportAfterMethodInit() {
        // initialization method that created in order to assert report creation in next method
        methodNumberOfInitTest = methodNumber
    }

    @Test(dependsOnMethods = ["reportingTestShouldReportAfterMethodInit"], groups = ["GebReportingTestTest"])
    void reportingTestShouldReportAfterMethod() {
        // check previous method reporting (reportingTestShouldReportAfterMethodInit)
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

    @Test(groups = ["GebReportingTestTest"])
    void reportingTestShouldReportOnTestFailureOnlyIfThatStrategyIsEnabled(Method testMethod) {
        config.reportOnTestFailureOnly = true
        def testResult = new TestResult()

        testResult.status = ITestResult.SUCCESS
        reportingAfter testResult
        def report = tryToFindReport(testMethod.name, END_OF_METHOD_REPORT_LABEL)
        assert report == null

        testResult.status = ITestResult.FAILURE
        reportingAfter testResult
        doTestReport(testMethod.name, END_OF_METHOD_REPORT_LABEL)
    }

    def doTestReport(methodName = "", label = "", methodNumber = this.methodNumber, reportCounter = reportNumberInTest) {
        def report = tryToFindReport(methodName, label, methodNumber, reportCounter)

        assert report != null, "${ReporterSupport.toTestReportLabel(methodNumber, reportCounter, methodName, label)} not found in ${reportGroupDir.listFiles()}"
        assert report.exists()
        reportNumberInTest++

        assert report.text.contains('<div class="d1" id="d1">')
    }

    def tryToFindReport(methodName = "", label = "", methodNumber = this.methodNumber, reportCounter = reportNumberInTest) {
        def reportName = ReporterSupport.toTestReportLabel(methodNumber, reportCounter, methodName, label)
        reportGroupDir.listFiles().find { it.name.startsWith reportName }
    }

    @AfterClass
    void tearDown() {
        server.stop()
    }
}