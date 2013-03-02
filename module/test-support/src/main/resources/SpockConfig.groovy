import geb.test.CrossBrowser

def sauceBrowserName = System.getProperty("geb.sauce.browser.name")
if (sauceBrowserName) {
	runner { include CrossBrowser }
}