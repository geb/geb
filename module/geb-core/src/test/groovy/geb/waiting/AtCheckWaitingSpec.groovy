package geb.waiting

import geb.Configuration
import geb.Page
import spock.lang.Unroll

class AtCheckWaitingSpec extends WaitingSpec {

	Configuration config
	ConfigObject rawConfig

	def setup() {
		config = browser.config
		rawConfig = config.rawConfig

		config.setWaitPreset('forAtCheck', 1, 0.1)
	}

	void 'by default at checking does not wait'() {
		given:
		via AtCheckWaitingSpecPage

		when:
		js.showIn(1)

		then:
		!isAt(AtCheckWaitingSpecPage)
	}

	@Unroll
	void 'at checking can be configured via config file to wait with waitFor parameter: #waitFor'() {
		given:
		rawConfig.atCheckWaiting = waitFor
		via AtCheckWaitingSpecPage

		when:
		js.showIn(0.3)

		then:
		at AtCheckWaitingSpecPage

		where:
		waitFor << [true, 1, 'forAtCheck']
	}

	void 'at checking can be configured programmatically to wait'() {
		given:
		config.atCheckWaiting = true
		via AtCheckWaitingSpecPage

		when:
		js.showIn(0.3)

		then:
		at AtCheckWaitingSpecPage
	}


	void 'if at checker fails with waiting enabled it should provide assertion verification output'() {
		given:
		rawConfig.atCheckWaiting = 0.1
		via AtCheckWaitingSpecPage

		when:
		at AtCheckWaitingSpecPage

		then:
		WaitTimeoutException e = thrown()
		e.cause.message.contains('$("div", text: "a")')
	}
}

class AtCheckWaitingSpecPage extends Page {
	static at = { $("div", text: "a") }
}
