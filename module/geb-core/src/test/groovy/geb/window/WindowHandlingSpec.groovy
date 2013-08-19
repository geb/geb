package geb.window

import geb.Page
import geb.error.NoNewWindowException
import spock.lang.Unroll

class WindowHandlingSpec extends BaseWindowHandlingSpec {

	@Unroll
	def "ensure withWindow block closure parameter called for all windows for which specification closure returns true"() {
		given:
		openAllWindows()

		when:
		def called = 0
		withWindow(specification) { called++ }

		then:
		called == expecetedCalls

		where:
		expecetedCalls | specification
		3              | { true }
		1              | { title == windowTitle() }
		2              | { title in [windowTitle(1), windowTitle(2)] }
	}

	@Unroll
	def "withWindow block closure is called in the context of the page passed as the 'page' option"() {
		given:
		go MAIN_PAGE_URL
		page WindowHandlingSpecMainPage
		openAllWindows()

		when:
		withWindow(page: WindowHandlingSpecNewWindowPage, specification) {
			assert page.getClass() == WindowHandlingSpecNewWindowPage
		}

		then:
		page.getClass() == WindowHandlingSpecMainPage

		where:
		specification << [
			{ true },
			{ title in [windowTitle(1), windowTitle(2)] },
			windowName(1)
		]
	}

	@Unroll
	def "withWindow by default does not close the matching windows"() {
		go MAIN_PAGE_URL
		openAllWindows()

		when:
		withWindow(specification) {}

		then:
		availableWindows.size() == 3

		where:
		specification << [
			{ true },
			windowName(1)
		]
	}

	@Unroll
	def "withWindow closes matching windows if 'close' option is passed and block closure throws an exception"() {
		given:
		go MAIN_PAGE_URL
		openAllWindows()

		when:
		withWindow(specification, close: true) { throw Exception() }

		then:
		thrown(Exception)
		availableWindows.size() == 2

		where:
		specification << [
			{ title == windowTitle(1) },
			windowName(1),
			{ title in [windowTitle(1), windowTitle(2)] }
		]
	}

	@Unroll("ensure withNewWindow throws an exception when: '#message'")
	def "ensure withNewWindow throws exception if there was none or more than one windows opened"() {
		when:
		withNewWindow(newWindowBlock) {}

		then:
		NoNewWindowException e = thrown()
		e.message.startsWith(message)

		where:
		message                                      | newWindowBlock
		'No new window has been opened'              | {}
		'There has been more than one window opened' | { openAllWindows() }
	}

	def "withNewWindow closes the new window even if closure throws an exception"() {
		given:
		go MAIN_PAGE_URL

		when:
		withNewWindow({ openWindow(1) }) { throw new Exception() }

		then:
		thrown(Exception)
		availableWindows.size() == 1
	}

	def "withNewWindow does not close the new window if close option is set to false"() {
		given:
		go MAIN_PAGE_URL

		when:
		withNewWindow({ openWindow(1) }, close: false) {}

		then:
		availableWindows.size() == 2
		inContextOfMainWindow
	}

	def "withNewWindow block closure is called in the context of the page passed as the 'page' option"() {
		given:
		go MAIN_PAGE_URL
		page WindowHandlingSpecMainPage

		when:
		withNewWindow({ openWindow(1) }, page: WindowHandlingSpecNewWindowPage) {
			assert page.getClass() == WindowHandlingSpecNewWindowPage
		}

		then:
		page.getClass() == WindowHandlingSpecMainPage
	}

	def "page context is reverted after a withNewWindow call where block closure throws an exception and 'page' option is present"() {
		given:
		go MAIN_PAGE_URL
		page WindowHandlingSpecMainPage

		when:
		withNewWindow({ openWindow(1) }, page: WindowHandlingSpecNewWindowPage) {
			throw new Exception()
		}

		then:
		thrown(Exception)
		page.getClass() == WindowHandlingSpecMainPage
	}

	def "'wait' option can be used in withNewWindow call if the new window opens asynchronously"() {
		given:
		go MAIN_PAGE_URL
		page WindowHandlingSpecMainPage

		when:
		withNewWindow({
			js.exec """
				setTimeout(function() {
					document.getElementById('main-1').click();
				}, 200);
			"""
		}, wait: true) {}

		then:
		notThrown(NoNewWindowException)
	}
	
	def "withWindow methods can be nested"() {
		given:
		openAllWindows()
		
		when: // can't put this in an expect block, some spock bug
		withWindow(windowName(1)) {
			assert title == windowTitle(1)
			openWindow(2)
			withWindow(windowName(1, 2)) {
				assert title == windowTitle(1, 2)
				openWindow(1)
				withWindow(windowName(1, 2, 1)) {
					assert title == windowTitle(1, 2, 1)
				}
				assert title == windowTitle(1, 2)
			}
			assert title == windowTitle(1)
		}
		
		then:
		true
	}

	def "withNewWindow methods can be nested"() {
		given:
		openAllWindows()
		
		when: // can't put this in an expect block, some spock bug
		withWindow(windowName(1)) {
			assert title == windowTitle(1)
			withNewWindow({openWindow(2)}) {
				assert title == windowTitle(1, 2)
				withNewWindow({openWindow(1)}) {
					assert title == windowTitle(1, 2, 1)
				}
				assert title == windowTitle(1, 2)
			}
			assert title == windowTitle(1)
		}
		
		then:
		true
	}
	
}

class WindowHandlingSpecMainPage extends Page {}
class WindowHandlingSpecNewWindowPage extends Page {}
