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
import geb.test.util.CallbackHttpServer
import org.testng.ITest
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class GebReportingTestTest extends GebReportingTest {

	def server = new CallbackHttpServer()

	private methodNumber = 0;
	private reportNumberInTest = 0;

	private methodNumberOfInitTest = 0;

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
		reportNumberInTest = 0;

		browser.baseUrl = server.baseUrl
		go()
	}

	@Test
	void reportingTestShouldReportOnDemand(ITest test) {
		report("ondemand")
		doTestReport(test.testName, "ondemand");
	}

	@Test
	void reportingTestShouldReportAfterMethodInit() {
		// initialization method that created in order to assert report creation in next method
		methodNumberOfInitTest = methodNumber
	}

	@Test(dependsOnMethods = ["reportingTestShouldReportAfterMethodInit"])
	void reportingTestShouldReportAfterMethod() {
		// check previous method (reportingTestShouldReportAfterMethodInit)
		report("ondemand")
		doTestReport("reportingTestShouldReportAfterMethodInit", GebReportingTest.END_OF_METHOD_REPORT_LABEL, methodNumberOfInitTest, 1);
		methodNumberOfInitTest = methodNumber
	}

	@Test(dependsOnMethods = ["reportingTestShouldReportAfterMethod"])
	void reportingTestShouldReportAfterMethodAndOnDemand() {
		// check previous method (reportingTestShouldReportAfterMethod)
		doTestReport("reportingTestShouldReportAfterMethod", "ondemand", methodNumberOfInitTest, 1);
		doTestReport("reportingTestShouldReportAfterMethod", GebReportingTest.END_OF_METHOD_REPORT_LABEL, methodNumberOfInitTest, 2);
	}

	def doTestReport(methodName = "", label = "", methodNumber = this.methodNumber, reportCounter = ++reportNumberInTest) {
		def reportName = ReporterSupport.toTestReportLabel(methodNumber, reportCounter, methodName, label) + ".html"
		def report = reportGroupDir.listFiles().find { it.name == reportName }
		assert report != null, "${reportName} not found. Following files are found: ${reportGroupDir.listFiles()}"
		assert report.exists()
		assert report.text.contains('<div class="d1" id="d1">')
	}

	@AfterClass
	void tearDown() {
		server.stop()
	}
}