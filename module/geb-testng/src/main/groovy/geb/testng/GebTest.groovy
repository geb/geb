package geb.testng

import geb.Browser
import org.openqa.selenium.WebDriver
import org.testng.annotations.AfterMethod;

class GebTest {
	private _browser

	Browser getBrowser() {
		if (_browser == null) {
			_browser = createBrowser()
		}
		_browser
	}

	void resetBrowser() {
		_browser = null
	}

	def methodMissing(String name, args) {
		getBrowser()."$name"(*args)
	}

	def propertyMissing(String name) {
		getBrowser()."$name"
	}

	def propertyMissing(String name, value) {
		getBrowser()."$name" = value
	}

	Browser createBrowser() {
		def driver = createDriver()
		def baseUrl = getBaseUrl()

		driver ? new Browser(driver, baseUrl) : new Browser(baseUrl)
	}

	WebDriver createDriver() {
		null // use Browser default
	}

	String getBaseUrl() {
		null
	}

	@AfterMethod
	void clearBrowserCookies() {
		if (browser.config.autoClearCookies) {
			browser.clearCookiesQuietly()
		}
	}

}
