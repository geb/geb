package geb.download

import geb.Browser
import geb.Configuration
import org.openqa.selenium.WebDriver
import spock.lang.Specification

class DownloadSupportSpec extends Specification {

	void 'default config is executed before user-provided config'() {
		given: 'a configuration object which has a default closure set'
		def defaultConfigClosure = Mock(Closure)
		def configuration = new Configuration([defaultDownloadConfig: defaultConfigClosure])

		and: 'a closure to be passed to the download method'
		def methodArgumentClosure = Mock(Closure)

		and: 'a few mocks'
		def browser = Mock(Browser)
		def driver = Mock(WebDriver)
		driver.getCurrentUrl() >> 'http://www.example.com/'
		def options = Mock(WebDriver.Options)
		options.getCookies() >> ([] as Set)
		driver.manage() >> options
		browser.getDriver() >> driver
		browser.getConfig() >> configuration

		when: 'calling a download method with the custom config closure'
		new DownloadSupport(browser).downloadBytes(methodArgumentClosure)

		then: 'the default config is called first'
		1 * defaultConfigClosure.call(_)

		then: 'the closure passed as method argument is called afterwards if applicable'
		1 * methodArgumentClosure.call(_)
	}

}
