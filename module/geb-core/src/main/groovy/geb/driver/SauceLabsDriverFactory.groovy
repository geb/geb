package geb.driver

import org.openqa.selenium.Capabilities
import org.openqa.selenium.Platform
import org.openqa.selenium.WebDriver

class SauceLabsDriverFactory {

	WebDriver create(String specification, String user, String accessKey, Map<String, Object> capabilities = [:]) {
		def remoteDriverOperations = new RemoteDriverOperations(getClass().classLoader)
		Class<? extends WebDriver> remoteWebDriverClass = remoteDriverOperations.remoteWebDriverClass
		if (!remoteWebDriverClass) {
			throw new ClassNotFoundException('org.openqa.selenium.remote.RemoteWebDriver needs to be on the classpath to create SauceLabs RemoteWebDriverInstances')
		}

		def url = new URL("http://$user:$accessKey@ondemand.saucelabs.com:80/wd/hub")

		def parts = specification.split(":", 3)
		def name = parts[0]
		def platform = parts.size() > 1 ? parts[1] : null
		def version = parts.size() > 2 ? parts[2] : null

		def browser = remoteDriverOperations.softLoadRemoteDriverClass('DesiredCapabilities')."$name"();
        capabilities.each{ String key, Object value ->
        	browser.setCapability(key, value)
        }
		if (platform) {
			try {
				platform = Platform."${platform.toUpperCase()}"
			} catch (MissingPropertyException ignore) {
			}
			browser.setCapability("platform", platform)
		}
		if (version != null) {
			browser.setCapability("version", version.toString())
		}

		remoteWebDriverClass.getConstructor(URL, Capabilities).newInstance(url, browser)
	}
}
