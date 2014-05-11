import geb.buildadapter.BuildAdapterFactory
import geb.driver.SauceLabsDriverFactory

testValue = true // used in a test in geb-core

if (!BuildAdapterFactory.getBuildAdapter(this.class.classLoader).reportsDir) {
	reportsDir = "build/geb"
}

def sauceLabsBrowser = System.getProperty("geb.saucelabs.browser")
if (sauceLabsBrowser) {
	driver = {
		def username = System.getenv("GEB_SAUCE_LABS_USER")
		assert username
		def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
		assert accessKey
		new SauceLabsDriverFactory().create(sauceLabsBrowser, username, accessKey, ['selenium-version': '2.41.0'])
	}
}
