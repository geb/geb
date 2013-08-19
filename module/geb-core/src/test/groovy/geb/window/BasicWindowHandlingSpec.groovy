package geb.window

import geb.test.CrossBrowser
import org.openqa.selenium.NoSuchWindowException
import spock.lang.Unroll

@CrossBrowser
class BasicWindowHandlingSpec extends BaseWindowHandlingSpec {

	@Unroll
	def "withWindow changes focus to window with given name and returns closure return value"() {
		when:
		openAllWindows()

		then:
		withWindow(windowName(index)) { title } == windowTitle(index)

		where:
		index << [1,2]
	}

	@Unroll
	def "ensure original context is preserved after a call to withWindow"() {
		given:
		openAllWindows()

		when:
		withWindow(specification) {}

		then:
		inContextOfMainWindow

		when:
		withWindow(specification) { throw new Exception() }

		then:
		thrown(Exception)
		inContextOfMainWindow

		where:
		specification << [windowName(1), { title == windowTitle(1) }]
	}

	@Unroll
	def "ensure exception is thrown for a non existing window passed to withWindow"() {
		when:
		withWindow(specification) {}

		then:
		thrown(NoSuchWindowException)

		where:
		specification << ['nonexisting', { false }]
	}

	@Unroll
	def "withWindow closes matching windows if 'close' option is passed"() {
		given:
		go MAIN_PAGE_URL
		openAllWindows()

		when:
		withWindow(specification, close: true) {}

		then:
		availableWindows.size() == windowsLeft

		where:
		where:
		windowsLeft | specification
		2           | { title == windowTitle(1) }
		2           | windowName(1)
		1           | { title in [windowTitle(1), windowTitle(2)] }
	}

	def "ensure original context is preserved after a call to withNewWindow"() {
		given:
		go MAIN_PAGE_URL

		when:
		withNewWindow({ openWindow(1) }) {}

		then:
		inContextOfMainWindow

		when:
		withNewWindow({ openWindow(2) }) { throw new Exception() }

		then:
		thrown(Exception)
		inContextOfMainWindow
	}

	@Unroll
	def "ensure withNewWindow block closure called in the context of the newly opened window"() {
		when:
		go MAIN_PAGE_URL

		then:
		withNewWindow({ openWindow(windowNum) }) { title } == expectedTitle

		where:
		expectedTitle  | windowNum
		windowTitle(1) | 1
		windowTitle(2) | 2
	}

	def "withNewWindow closes the new window by default"() {
		given:
		go MAIN_PAGE_URL

		when:
		withNewWindow({ openWindow(1) }) {}

		then:
		availableWindows.size() == 1
		inContextOfMainWindow

	}
}
