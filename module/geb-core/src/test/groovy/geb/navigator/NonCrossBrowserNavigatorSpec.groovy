package geb.navigator

import geb.test.GebSpecWithServer
import org.openqa.selenium.WebElement

class NonCrossBrowserNavigatorSpec extends GebSpecWithServer {

	def "click is called only on the first element of the navigator"() {
		given:
		def element1 = Mock(WebElement)
		def element2 = Mock(WebElement)
		def navigator = new NonEmptyNavigator(browser, [element1, element2])

		when: navigator.click()

		then:
		1 * element1.click()
		0 * element2.click()
		0 * _
	}
}
