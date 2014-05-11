import geb.test.CrossBrowser

def usingSauce = System.getProperty("geb.saucelabs.browser")
if (usingSauce) {
	runner {
		include CrossBrowser
	}
}