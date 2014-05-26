/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb

import geb.error.PageChangeListenerAlreadyRegisteredException
import geb.test.GebSpecWithServer
import spock.lang.Shared
import spock.lang.Stepwise

@Stepwise
class PageChangeListeningSpec extends GebSpecWithServer {

	@Shared pgl1
	@Shared pgl2
	@Shared pgl1Callback
	@Shared pgl2Callback

	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<div>a</div>
			</body>
			</html>"""
		}
	}

	def "register listeners"() {
		given:
		def pgl1Called = false
		pgl1Callback = { b, o, n ->
			assert b == browser
			assert o == null
			assert n instanceof Page
			pgl1Called = true
		}
		def pgl2Called = false
		pgl2Callback = { b, o, n ->
			assert b == browser
			assert o == null
			assert n instanceof Page
			pgl2Called = true
		}
		pgl1 = [
			pageWillChange: { Browser browser, Page oldPage, Page newPage -> pgl1Callback?.call(browser, oldPage, newPage) },
			toString: { "pgl1" }
		] as PageChangeListener
		pgl2 = [
			pageWillChange: { Browser browser, Page oldPage, Page newPage -> pgl2Callback?.call(browser, oldPage, newPage) },
			toString: { "pgl2" }
		] as PageChangeListener
		when:
		registerPageChangeListener(pgl1)
		registerPageChangeListener(pgl2)
		then:
		// registering causes the listener to be invoked so it gets the current page
		pgl1Called
		pgl2Called
	}

	def "set the initial page"() {
		given:
		def pgl1Called = false
		pgl1Callback = { b, o, n ->
			assert b == browser
			assert o instanceof Page
			assert n instanceof PageChangeListeningSpecPage1
			pgl1Called = true
		}
		def pgl2Called = false
		pgl2Callback = { b, o, n ->
			assert b == browser
			assert o instanceof Page
			assert n instanceof PageChangeListeningSpecPage1
			pgl2Called = true
		}
		when:
		to PageChangeListeningSpecPage1
		then:
		// TODO - This sometimes fails for me, I have no idea why. The waitFor() pause worksaround.
		waitFor { pgl1Called }
		pgl2Called
	}

	def "change the page"() {
		given:
		def pgl1Called = false
		pgl1Callback = { b, o, n ->
			assert b == browser
			assert o instanceof PageChangeListeningSpecPage1
			assert n instanceof PageChangeListeningSpecPage2
			pgl1Called = true
		}
		def pgl2Called = false
		pgl2Callback = { b, o, n ->
			assert b == browser
			assert o instanceof PageChangeListeningSpecPage1
			assert n instanceof PageChangeListeningSpecPage2
			pgl2Called = true
		}
		when:
		to PageChangeListeningSpecPage2
		then:
		pgl1Called
		pgl2Called
	}

	def "try to register an already registered"() {
		when:
		registerPageChangeListener(pgl1)
		then:
		thrown(PageChangeListenerAlreadyRegisteredException)
	}

	def "remove a listener"() {
		given:
		def pgl1Called = false
		pgl1Callback = { b, o, n -> pgl1Called = true }
		def pgl2Called = false
		pgl2Callback = { b, o, n -> pgl2Called = true }
		when:
		def removed = removePageChangeListener(pgl1)
		to PageChangeListeningSpecPage1
		then:
		removed
		!pgl1Called
		pgl2Called
	}
}

class PageChangeListeningSpecPage1 extends Page {
}

class PageChangeListeningSpecPage2 extends Page {
}