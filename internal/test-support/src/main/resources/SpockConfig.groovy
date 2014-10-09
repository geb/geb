import geb.test.CrossBrowser
import geb.test.Android

def crossBrowser = System.getProperty("geb.saucelabs.browser") || System.getProperty("geb.browserstack.browser")
if (crossBrowser) {
	def android = System.getProperty("geb.browserstack.browser")?.contains("android")
	runner {
		include android ? Android : CrossBrowser
	}
}