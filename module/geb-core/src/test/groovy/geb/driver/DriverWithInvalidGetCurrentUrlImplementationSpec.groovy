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
