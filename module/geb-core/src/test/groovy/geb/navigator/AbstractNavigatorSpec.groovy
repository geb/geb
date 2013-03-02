package geb.navigator

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer
import org.openqa.selenium.WebDriver

@CrossBrowser
abstract class AbstractNavigatorSpec extends GebSpecWithServer {

	def setupSpec() {
		takeReports = false
		responseHtml(getClass().getResource("/test.html").text)
	}

	def setup() {
		go server.baseUrl
	}

}
