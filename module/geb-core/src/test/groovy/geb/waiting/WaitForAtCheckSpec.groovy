package geb.waiting

import geb.Configuration
import geb.Page
import spock.lang.Unroll

class WaitForAtCheckSpec extends WaitingSpec {

	Configuration config
	ConfigObject rawConfig

	def setup() {
		config = browser.config
		rawConfig = config.rawConfig

		config.setWaitPreset('forAtCheck', 1, 0.1)
	}

	void 'by default at checking does not wait'() {
		given:
		via WaitForAtCheckSpecPage

		when:
		js.showIn(1)

		then:
		!isAt(WaitForAtCheckSpecPage)
	}

	@Unroll
	void 'at checking can be configured via config file to wait with waitFor parameter: #waitFor'() {
		given:
		rawConfig.waitForAtCheck = waitFor
		via WaitForAtCheckSpecPage

		when:
		js.showIn(0.3)

		then:
		at WaitForAtCheckSpecPage

		where:
		waitFor << [true, 1, 'forAtCheck']
	}

	void 'at checking can be configured programmatically to wait'() {
		given:
		config.waitForAtCheck = true
		via WaitForAtCheckSpecPage

		when:
		js.showIn(0.3)

		then:
		at WaitForAtCheckSpecPage
	}


	void 'if at checker fails with waiting enabled it should provide assertion verification output'() {
		given:
		rawConfig.waitForAtCheck = 0.1
		via WaitForAtCheckSpecPage

		when:
		at WaitForAtCheckSpecPage

		then:
		WaitTimeoutException e = thrown()
		e.cause.message.contains('$("div", text: "a")')
	}
}

class WaitForAtCheckSpecPage extends Page {
	static at = { $("div", text: "a") }
}
