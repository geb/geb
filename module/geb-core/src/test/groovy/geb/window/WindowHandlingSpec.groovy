/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.window

import geb.Page
import geb.error.NoNewWindowException
import geb.error.UndefinedAtCheckerException
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
		withWindow(specification) {
		}

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
		withNewWindow(newWindowBlock) {
		}

		then:
		NoNewWindowException e = thrown()
		e.message.startsWith(message)

		where:
		message                                      | newWindowBlock
		'No new window has been opened'              | { }
		'There has been more than one window opened' | { openAllWindows() }
	}

	@SuppressWarnings('SpaceBeforeOpeningBrace')
	def "withNewWindow closes the new window even if closure throws an exception"() {
		given:
		go MAIN_PAGE_URL

		when:
		withNewWindow({ openWindow(1) }) { throw new Exception() }

		then:
		thrown(Exception)
		availableWindows.size() == 1
	}

	@SuppressWarnings('SpaceBeforeOpeningBrace')
	def "withNewWindow does not close the new window if close option is set to false"() {
		given:
		go MAIN_PAGE_URL

		when:
		withNewWindow({ openWindow(1) }, close: false) {
		}

		then:
		availableWindows.size() == 2
		inContextOfMainWindow
	}

	@SuppressWarnings('SpaceBeforeOpeningBrace')
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

	@SuppressWarnings('SpaceBeforeOpeningBrace')
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

	@SuppressWarnings('SpaceBeforeOpeningBrace')
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
		}, wait: true) {
		}

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

	@SuppressWarnings('SpaceBeforeOpeningBrace')
	def "withNewWindow methods can be nested"() {
		given:
		openAllWindows()

		when: // can't put this in an expect block, some spock bug
		withWindow(windowName(1)) {
			assert title == windowTitle(1)
			withNewWindow({ openWindow(2) }) {
				assert title == windowTitle(1, 2)
				withNewWindow({ openWindow(1) }) {
					assert title == windowTitle(1, 2, 1)
				}
				assert title == windowTitle(1, 2)
			}
			assert title == windowTitle(1)
		}

		then:
		true
	}

	@Unroll
	def "withWindow successfully verifies at checker"() {
		given:
		go MAIN_PAGE_URL
		openAllWindows()

		when:
		withWindow(page: WindowHandlingSpecNewWindowWithAtCheckPage, specification) {
		}

		then:
		notThrown(Exception)

		where:
		specification << [
			{ title == windowTitle(1) },
			windowName(1)
		]
	}

	@Unroll
	def "withWindow does not fail if there is no at checker"() {
		given:
		go MAIN_PAGE_URL
		openAllWindows()

		when:
		withWindow(page: WindowHandlingSpecNewWindowPage, specification) {
			assert page.getClass() == WindowHandlingSpecNewWindowPage
		}

		then:
		notThrown(UndefinedAtCheckerException)

		where:
		specification << [
			{ title == windowTitle(1) },
			windowName(1)
		]
	}

	@Unroll
	def "withWindow verifies at checker"() {
		given:
		go MAIN_PAGE_URL
		openAllWindows()

		when:
		withWindow(page: WindowHandlingSpecNewWindowWithAtCheckPage, specification) {
		}

		then:
		thrown(AssertionError)

		where:
		specification << [
			{ title == windowTitle(2) },
			windowName(2)
		]
	}

	@SuppressWarnings('SpaceBeforeOpeningBrace')
	def "withNewWindow successfully verifies at checker"() {
		given:
		go MAIN_PAGE_URL
		page WindowHandlingSpecMainPage

		when:
		withNewWindow({ openWindow(1) }, page: WindowHandlingSpecNewWindowWithAtCheckPage) {
		}

		then:
		notThrown(Exception)
	}

	@SuppressWarnings('SpaceBeforeOpeningBrace')
	def "withNewWindow does not fail if there is no at checker"() {
		go MAIN_PAGE_URL
		page WindowHandlingSpecMainPage

		when:
		def newWindowPage = withNewWindow({ openWindow(1) }, page: WindowHandlingSpecNewWindowPage) {
			page
		}

		then:
		newWindowPage instanceof WindowHandlingSpecNewWindowPage
		notThrown(UndefinedAtCheckerException)
	}

	@SuppressWarnings('SpaceBeforeOpeningBrace')
	def "withNewWindow verifies at checker"() {
		given:
		go MAIN_PAGE_URL
		page WindowHandlingSpecMainPage

		when:
		withNewWindow({ openWindow(2) }, page: WindowHandlingSpecNewWindowWithAtCheckPage) {
		}

		then:
		thrown(AssertionError)
	}
}

class WindowHandlingSpecMainPage extends Page {
}

class WindowHandlingSpecNewWindowPage extends Page {
}

class WindowHandlingSpecNewWindowWithAtCheckPage extends Page {
	static at = { title == "Window main-1" }
}
