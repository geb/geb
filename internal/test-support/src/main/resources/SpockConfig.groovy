import geb.test.CrossBrowser

def crossBrowser = System.getProperty("geb.saucelabs.browser") || System.getProperty("geb.browserstack.browser")
if (crossBrowser) {
	runner {
		include CrossBrowser
	}
}