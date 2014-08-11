import geb.buildadapter.BuildAdapterFactory
import geb.driver.BrowserStackDriverFactory
import geb.driver.SauceLabsDriverFactory

testValue = true // used in a test in geb-core

String getForkIndex(int total) {
	String workerName = System.getProperty('org.gradle.test.worker')
	int workerNumber = workerName ? ((workerName =~ /[^\d]*([\d]+)/)[0][1]).toInteger() : 0
	(workerNumber % total).toString()
}

void setPortIndexProperty(String index) {
	System.setProperty('geb.port.index', index)
}

if (!BuildAdapterFactory.getBuildAdapter(this.class.classLoader).reportsDir) {
	reportsDir = "build/geb"
}

def sauceLabsBrowser = System.getProperty("geb.saucelabs.browser")
if (sauceLabsBrowser) {
	setPortIndexProperty(getForkIndex(3))
	driver = {
		def username = System.getenv("GEB_SAUCE_LABS_USER")
		assert username
		def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
		assert accessKey
		new SauceLabsDriverFactory().create(sauceLabsBrowser, username, accessKey)
	}
}

def browserStackBrowser = System.getProperty("geb.browserstack.browser")
if (browserStackBrowser) {
	def forkIndex = getForkIndex(5)
	setPortIndexProperty(forkIndex)
	driver = {
		def username = System.getenv("GEB_BROWSERSTACK_USERNAME")
		assert username
		def accessKey = System.getenv("GEB_BROWSERSTACK_AUTHKEY")
		assert accessKey
		new BrowserStackDriverFactory().create(browserStackBrowser, username, accessKey)
	}
}

def devDriver = System.getProperty("geb.dev.driver")
if (devDriver != "htmlunit") {
	driver = devDriver
}