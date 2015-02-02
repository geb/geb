/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package browser

import geb.driver.CachingDriverFactory
import geb.test.GebSpecWithServer

import javax.servlet.http.HttpServletRequest

class DriveSpec extends GebSpecWithServer {

	def setupSpec() {
		Browser.serverBaseUrl = server.baseUrl
		takeReports = false
	}

	def "signup"() {
		given:
		html { HttpServletRequest request ->
			if (request.requestURI.endsWith("/signup")) {
				h1("Signup Page")
			}
		}

		expect:
		// tag::using_drive[]
		Browser.drive {
			go "signup"
			assert $("h1").text() == "Signup Page"
		}
		// end::using_drive[]

		and:
		// tag::explicit[]
		def browser = new Browser()
		browser.go "signup"
		assert browser.$("h1").text() == "Signup Page"
		// end::explicit[]
	}

	def "quit"() {
		expect:
		// tag::quit[]
		Browser.drive {
			//…
		}.quit()
		// end::quit[]
		cleanup:
		CachingDriverFactory.clearCacheCache()
	}
}

class Browser extends geb.Browser {
	static String serverBaseUrl

	Browser() {
		browser.baseUrl = serverBaseUrl
	}

	static Browser drive(Closure script) {
		drive(new Browser(), script)
	}
}
