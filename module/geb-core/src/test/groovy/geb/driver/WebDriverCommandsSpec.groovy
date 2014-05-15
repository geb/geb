package geb.driver

import geb.Module
import geb.Page
import geb.test.CallbackAndWebDriverServer
import geb.test.GebSpecWithServer
import geb.test.RemoteWebDriverWithExpectations
import geb.test.TestHttpServer
import org.openqa.selenium.remote.DesiredCapabilities
import spock.lang.Shared

class WebDriverCommandsSpec extends GebSpecWithServer {

	@Shared CallbackAndWebDriverServer callbackAndWebDriverServer = new CallbackAndWebDriverServer()
	RemoteWebDriverWithExpectations driver

	def setup() {
		driver = new RemoteWebDriverWithExpectations(callbackAndWebDriverServer.webdriverUrl, DesiredCapabilities.htmlUnit())
		browser.driver = driver
		browser.baseUrl = callbackAndWebDriverServer.applicationUrl
		browser.config.cacheDriver = false
	}

	def cleanup() {
		driver.resetExpectations()
	}

	@Override
	TestHttpServer getServerInstance() {
		callbackAndWebDriverServer
	}

	void 'going to a page and getting its title'() {
		given:
		responseHtml {
			head {
				title 'a title'
			}
		}

		when:
		go()
		title

		then:
		driver.getUrlExecuted(callbackAndWebDriverServer.applicationUrl)
		driver.getTitleExecuted()
	}

	void 'using a selector that returns multiple elements'() {
		given:
		responseHtml {
			body {
				p 'first'
				p 'second'
			}
		}

		when:
		go()
		$('p')

		then:
		driver.getUrlExecuted(callbackAndWebDriverServer.applicationUrl)
		driver.findRootElementExecuted()
		driver.findChildElementsByCssExecuted('p')
	}

	void 'using form control shortcuts in a baseless module should not generate multiple root element searches'() {
		given:
		responseHtml {
			body {
				input type: 'text', name: 'someName'
			}
		}

		when:
		to WebDriverCommandSpecModulePage
		def module = plainModule

		then:
		driver.getUrlExecuted(callbackAndWebDriverServer.applicationUrl)
		driver.findRootElementExecuted()
		driver.resetExpectations()

		when:
		module.someName()

		then:
		driver.findChildElementsByNameExecuted('someName')
	}
}

class WebDriverCommandSpecModulePage extends Page {

	static content = {
		plainModule { module Module }
	}
}
