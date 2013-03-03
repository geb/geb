package geb.interaction

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer

@CrossBrowser
class InteractionsSupportSpec extends GebSpecWithServer {

	def "navigators are unpacked in interact block"() {
		given:
		html {
			body {
				input(id: 'first-input', value: '')
				input(id: 'second-input', value: '')
			}
		}

		$('#first-input').click()

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


}