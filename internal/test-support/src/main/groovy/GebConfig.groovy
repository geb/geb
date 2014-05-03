import geb.buildadapter.BuildAdapterFactory
import geb.driver.SauceLabsDriverFactory

testValue = true // used in a test in geb-core

if (!BuildAdapterFactory.getBuildAdapter(this.class.classLoader).reportsDir) {
	reportsDir = "build/geb"
}

def sauceBrowser = System.getProperty("geb.sauce.browser")
if (sauceBrowser) {
	driver = {
		def username = System.getenv("GEB_SAUCE_LABS_USER")
		assert username
		def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
		assert accessKey
		new SauceLabsDriverFactory().create(sauceBrowser, username, accessKey, ['selenium-version': '2.41.0'])
	}
}
