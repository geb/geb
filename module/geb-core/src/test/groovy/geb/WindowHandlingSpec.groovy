package geb

import geb.driver.CachingDriverFactory
import geb.error.NoNewWindowException
import geb.test.GebSpecWithServer
import org.openqa.selenium.NoSuchWindowException
import spock.lang.Unroll

class WindowHandlingSpec extends GebSpecWithServer {

	private final static String MAIN_PAGE_URL = '/main'

	def cleanup() {
		/*
		 * set the browser instance to null as we're going to quit the driver and otherwise parent's cleanup will
		 * want to do some work on that closed driver which will fail
		 */
		resetBrowser()
		// make sure that driver is recreated for the next test so that there is only one browser window opened
		CachingDriverFactory.clearCacheAndQuitDriver()
	}

	def setupSpec() {
		responseHtml { request ->
			def page = (~'/(.*)').matcher(request.requestURI)[0][1]
			head {
				title("Window $page")
			}
			body {
				[1, 2].each {
					def label = "$page-$it"
					a(id: label, target: "window-$label", href: "/$label")
				}
			}
		}
	}

	private void allWindowsOpened() {
		go MAIN_PAGE_URL
		$('a')*.click()
		assert availableWindows.size() == 3
	}

	private boolean isInContextOfMainWindow() {
		title == windowTitle()
	}

	private String windowTitle(int[] indexes = []) {
		def name = "Window main"
		if (indexes) {
			name += "-" + indexes*.toString().join("-")
		}
		name
	}

	private String windowName(int[] indexes = []) {
		def name = "window-main"
		if (indexes) {
			name += "-" + indexes*.toString().join("-")
		}
		name
	}

	private void openWindow(int index) {
		$("a", index - 1).click()
	}
	
	@Unroll
	def "withWindow changes focus to window with given name and returns closure return value"() {
		when:
		allWindowsOpened()

		then:
		withWindow(windowName(index)) { title } == windowTitle(index)

		where:
		index << [1,2]
	}

	@Unroll
	def "ensure original context is preserved after a call to withWindow"() {
		given:
		allWindowsOpened()

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
	def "ensure withWindow block closure parameter called for all windows for which specification closure returns true"() {
		given:
		allWindowsOpened()

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
		allWindowsOpened()

		when:
		withWindow(page: WindowHandlingSpecNewWindowPage, specification) {
			assert page.getClass() == WindowHandlingSpecNewWindowPage
		}

		then:
		page.getClass() == WindowHandlingSpecMainPage

		where:
		specification << [
			{ true },
			{ title == windowTitle() },
			{ title in [windowTitle(1), windowTitle(2)] },
			windowName(1),
			windowName(2)
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
		'There has been more than one window opened' | { allWindowsOpened() }
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

	def "withNewWindow closes the new window if 'close' option is passed"() {
		given:
		go MAIN_PAGE_URL

		when:
		withNewWindow({ openWindow(1) }, close: true) {}

		then:
		availableWindows.size() == 1
		inContextOfMainWindow

	}

	def "withNewWindow closes the new window if 'close' option is passed and block closure throws an exception"() {
		given:
		go MAIN_PAGE_URL

		when:
		withNewWindow({ openWindow(1) }, close: true) { throw new Exception() }

		then:
		thrown(Exception)
		availableWindows.size() == 1
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
		allWindowsOpened()
		
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
		allWindowsOpened()
		
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
