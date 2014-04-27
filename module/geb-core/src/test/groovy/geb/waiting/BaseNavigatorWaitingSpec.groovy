package geb.waiting

import geb.Configuration
import geb.navigator.factory.BrowserBackedNavigatorFactory
import geb.test.GebSpecWithServer
import groovy.transform.InheritConstructors
import spock.lang.Unroll

@Unroll
class BaseNavigatorWaitingSpec extends GebSpecWithServer {

	Configuration config
	ConfigObject rawConfig

	def setup() {
		responseHtml """
			<html>
				<head>
					<script type="text/javascript" charset="utf-8">
						setTimeout(function() {
							document.body.innerHTML = "<div></div>";
						}, 500);
					</script>
				</head>
				<body></body>
			</html>
		"""

		config = browser.config
		config.setWaitPreset('forBaseNavigator', 1, 0.1)
		rawConfig = config.rawConfig
		rawConfig.navigatorFactory = {
			new TestBaseNavigatorNavigatorFactory(browser, browser.config.innerNavigatorFactory)
		}
	}

	void 'base navigator waiting can be configured via config file to wait with waitFor parameter: #waitFor'() {
		given:
		rawConfig.baseNavigatorWaiting = waitFor

		when:
		go()
		$('div')

		then:
		notThrown(NoSuchElementException)

		where:
		waitFor << [true, 1, 'forBaseNavigator', [1, 0.1]]
	}

	void 'base navigator waiting can be configured programmatically'() {
		given:
		config.baseNavigatorWaiting = true

		when:
		go()
		$('div')

		then:
		notThrown(NoSuchElementException)
	}
}

@InheritConstructors
class TestBaseNavigatorNavigatorFactory extends BrowserBackedNavigatorFactory {

	protected String getBaseTagName() {
		"div"
	}
}
