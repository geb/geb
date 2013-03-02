import org.openqa.selenium.Platform
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver

testValue = true // used in a test in geb-core
reportsDir = "build/geb"

def sauceBrowserName = System.getProperty("geb.sauce.browser.name")
if (sauceBrowserName) {
	driver = {
		def username = System.getenv("GEB_SAUCE_LABS_USER")
		assert username
		def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
		assert accessKey

		def url = new URL("http://$username:$accessKey@ondemand.saucelabs.com:80/wd/hub")

		def browser = DesiredCapabilities."$sauceBrowserName"();

		def browserVersion = System.getProperty("geb.sauce.browser.version")
		if (browserVersion != null) {
			browser.setCapability("version", browserVersion.toString());
		}

		def browserPlatform = System.getProperty("geb.sauce.browser.platform")
		if (browserPlatform) {
			browser.setCapability("platform", Platform."${browserPlatform.toUpperCase()}");
		}

		new RemoteWebDriver(url, browser)
	}
}
