/*
 * Copyright 2012 the original author or authors.
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
package geb.driver

import geb.test.CallbackAndWebDriverServer
import geb.test.GebSpecWithServer
import geb.test.TestHttpServer
import groovy.transform.InheritConstructors
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.openqa.selenium.remote.Response
import spock.lang.Issue
import spock.lang.Shared

import static org.openqa.selenium.remote.DriverCommand.GET_CURRENT_URL

class DriverWithInvalidGetCurrentUrlImplementationSpec extends GebSpecWithServer {

	@Shared CallbackAndWebDriverServer callbackAndWebDriverServer = new CallbackAndWebDriverServer()

	@Override
	TestHttpServer getServerInstance() {
		callbackAndWebDriverServer
	}

	def setup() {
		browser.driver = new DriverWithInvalidGetCurrentUrlImplementation(callbackAndWebDriverServer.webdriverUrl, DesiredCapabilities.htmlUnit())
		browser.baseUrl = callbackAndWebDriverServer.applicationUrl
		browser.config.cacheDriver = false
	}

	@Issue('http://jira.codehaus.org/browse/GEB-291')
	void 'go() does not fail even if the driver returns null for get current url command'() {
		when:
		go()

		then:
		notThrown(NullPointerException)
	}
}

@InheritConstructors
class DriverWithInvalidGetCurrentUrlImplementation extends RemoteWebDriver {

	@Override
	protected Response execute(String command) {
		command == GET_CURRENT_URL ? new Response() : super.execute(command)
	}
}
