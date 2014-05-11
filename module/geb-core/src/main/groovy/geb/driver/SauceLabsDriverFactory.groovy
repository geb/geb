package geb.driver

class SauceLabsDriverFactory extends CloudDriverFactory {
	@Override
	String assembleProviderUrl(String username, String password) {
		"http://$username:$password@ondemand.saucelabs.com:80/wd/hub"
	}
}
