package geb.interaction

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer
import spock.lang.IgnoreIf

@CrossBrowser
@IgnoreIf({ System.getProperty("geb.sauce.browser")?.startsWith('safari') })
class InteractionsSupportSpec extends GebSpecWithServer {

	def setup() {
		html {
			body {
				input(id: 'first-input', value: '')
				input(id: 'second-input', value: '')
			}
		}
	}

	def "navigators are unpacked in interact block"() {
		when:
		interact {
			moveToElement $('#first-input')
			click()
			sendKeys 'GEB'
			moveToElement $('#second-input')
			click()
			sendKeys 'geb'
		}

		then:
		$('#first-input').value() == 'GEB'
		$('#second-input').value() == 'geb'
	}

	def "page content items are unpacked in interact block"() {
		given:
		at InteractionPage

		when:
		interact {
			moveToElement first
			click()
			sendKeys 'GEB'
			moveToElement second
			click()
			sendKeys 'geb'
		}

		then:
		first == 'GEB'
		second == 'geb'
	}

}

class InteractionPage extends geb.Page {
	static at = { true }
	static content = {
		first { $('#first-input') }
		second { $('#second-input') }
	}
}
