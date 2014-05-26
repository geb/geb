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
