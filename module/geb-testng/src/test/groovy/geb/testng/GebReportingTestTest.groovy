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

import geb.test.util.CallbackHttpServer
import org.testng.annotations.BeforeMethod
import org.testng.annotations.AfterMethod
import org.testng.annotations.Test

class GebReportingTestTest extends GebReportingTest {

	def server = new CallbackHttpServer()
	
	static private counter = 0
	
	static responseText = """
		<html>
		<body>
			<div class="d1" id="d1">d1</div>
		</body>
		</html>
	"""
	
	@BeforeMethod
	void setUp() {
		server.start()
		server.get = { req, res ->
			res.outputStream << responseText
		}
		browser.baseUrl = server.baseUrl
		go()
	}
	
	@Test
	void a() {
		doTestReport()
	}
	
	@Test
	void b() {
		doTestReport()
	}

	@Test
	void c() {
		doTestReport()
	}
	
	def doTestReport() {
		if (++counter > 1) {
			def report = reportGroupDir.listFiles().find { it.name.startsWith((counter - 1).toString()) }
			assert report.exists()
			assert report.text.contains('<div class="d1" id="d1">')
		}
	}
	
	@AfterMethod
	void tearDown() {
		server.stop()
	}
}