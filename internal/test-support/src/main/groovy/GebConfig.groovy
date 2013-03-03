import geb.buildadapter.BuildAdapterFactory
import org.openqa.selenium.Platform
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

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

		def url = new URL("http://$username:$accessKey@ondemand.saucelabs.com:80/wd/hub")

		def parts = sauceBrowser.split(":", 3)
		def name = parts[0]
		def platform = parts.size() > 1 ? parts[1] : null
		def version = parts.size() > 2 ? parts[2] : null

		def browser = DesiredCapabilities."$name"();
		if (platform) {
			browser.setCapability("platform", Platform."${platform.toUpperCase()}")
		}
		if (version != null) {
			browser.setCapability("version", version.toString())
		}

		new RemoteWebDriver(url, browser)
	}
}
