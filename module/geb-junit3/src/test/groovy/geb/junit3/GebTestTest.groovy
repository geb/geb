/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *			http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.junit3

import geb.test.CallbackHttpServer
import geb.Page

class GebTestTest extends GebTest {

	def server = new CallbackHttpServer()

	void setUp() {
		server.start()
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<div class="d1" id="d1">d1</div>
			</body>
			</html>"""
		}
		super.setUp()
		browser.baseUrl = server.baseUrl
	}

	void testMissingMethodsAreInvokedOnTheDriverInstance() {
		// This also verifies that the driver instance is instantiated correctly
		go("/")
	}

	void testMissingPropertyAccessesAreRequestedOnTheDriverInstance() {
		page SomePage
		assert prop == 1
	}

	void testMissingPropertyAssignmentsAreForwardedToTheDriverInstance() {
		page SomePage
		prop = 2
		assert prop == 2
	}

	void tearDown() {
		server.stop()
		super.tearDown()
	}
}

class SomePage extends Page {
	def prop = 1
}