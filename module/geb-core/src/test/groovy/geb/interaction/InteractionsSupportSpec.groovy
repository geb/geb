package geb.interaction

import geb.test.GebSpec
import org.openqa.selenium.Keys
import geb.Page
import geb.test.GebSpecWithServer

class InteractionsSupportSpec extends GebSpecWithServer {

	def setupSpec() {
		responseHtml { request ->
			body {
				input(id: 'first-input', value: '')
				input(id: 'second-input', value: '')
				form(id: 'form', name: 'form') {
					select(id: 'select-element', name: 'select_element') {
						(1..3).each {
							option(value: "${it}", "Element ${it}")
						}
						option(value: '4', selected: 'selected', "Element 4")
					}
				}
			}
		}
	}

	def "Move between elements using interactions"() {

		given:
		to TestHtmlPage
		$('#first-input').click()

		when:
		interact {
			moveToElement($('#second-input'))
			click()
			sendKeys('geb')
			moveToElement($('#first-input'))
			click()
			keyDown(Keys.SHIFT)
			sendKeys('GEB')
			keyUp(Keys.SHIFT)
		}

		then:
		$('#second-input').value() == 'geb'
		$('#first-input').value() == 'GEB'

	}

	def "Move between content defined elements using interactions"() {

		given:
		to TestHtmlPage
		firstInput.click()

		when:

		interact {
			moveToElement(secondInput)
			click()
			sendKeys('geb')
			moveToElement(firstInput)
			click()
			keyDown(Keys.SHIFT)
			sendKeys('GEB')
			keyUp(Keys.SHIFT)
		}

		then:
		secondInput.value() == 'geb'
		firstInput.value() == 'GEB'

	}

	def "Focus various elements by moving to and clicking them"() {

		given:
		to TestHtmlPage

		expect:
		$('form').select_element == '4'

		when:
		interact {
			moveToElement($('#select-element option', value: '4'))
			click()
			moveToElement($('#select-element option', value: '2'))
			click()
		}

		then:
		$('form').select_element == '2'

	}

}

class TestHtmlPage extends Page {

	static url = '/interactor'

	static content = {

		firstInput { $('#first-input') }
		secondInput { $('#second-input') }

	}

}