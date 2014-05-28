/*
 * Copyright 2014 the original author or authors.
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
package geb.test

import com.google.common.base.Supplier
import org.mortbay.jetty.servlet.Context
import org.mortbay.jetty.servlet.ServletHolder
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.server.DefaultDriverFactory
import org.openqa.selenium.remote.server.DefaultDriverSessions
import org.openqa.selenium.remote.server.DriverServlet
import org.openqa.selenium.remote.server.DriverSessions

class CallbackAndWebDriverServer extends CallbackHttpServer {

	protected addServlets(Context context) {
		context.addServlet(new ServletHolder(new CallbackServlet(this)), "/application/*")
		context.addServlet(new ServletHolder(
			new DriverServlet(new Supplier<DriverSessions>() {

				DriverSessions get() {
					new DefaultDriverSessions(new DefaultDriverFactory(), [(DesiredCapabilities.htmlUnit()): HtmlUnitDriver])
				}
			}
		)), "/webdriver/*")
	}

	String getApplicationUrl() {
		"$protocol://localhost:$port/application/"
	}

	URL getWebdriverUrl() {
		new URL("$protocol://localhost:$port/webdriver")
	}
}
