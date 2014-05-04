package geb.window

import geb.test.CrossBrowser
import org.openqa.selenium.NoSuchWindowException
import spock.lang.Unroll

@CrossBrowser
class BasicWindowHandlingSpec extends BaseWindowHandlingSpec {

	def setup() {
		go MAIN_PAGE_URL
	}

	@Unroll
	def "withWindow changes focus to window with given name and returns closure return value"() {
		when:
		openWindow(index)

		then:
		withWindow(windowName(index)) { title } == windowTitle(index)

		where:
		index << [1,2]
	}

	@Unroll
	def "ensure original context is preserved after a call to withWindow"() {
		given:
		openWindow(1)

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
		openWindow(1)

		when:
		withWindow(specification, close: true) {}

		then:
		availableWindows.size() == old(availableWindows.size() - 1)

		where:
		specification << [{ title == windowTitle(1) }, windowName(1)]
	}

	def "ensure original context is preserved after a call to withNewWindow"() {
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
		expect:
		withNewWindow({ openWindow(windowNum) }) { title } == expectedTitle

		where:
		expectedTitle  | windowNum
		windowTitle(1) | 1
		windowTitle(2) | 2
	}

	def "withNewWindow closes the new window by default"() {
		when:
		withNewWindow({ openWindow(1) }) {}

		then:
		availableWindows.size() == old(availableWindows.size())
		inContextOfMainWindow
	}
}
