/*
 * Copyright 2011 the original author or authors.
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
package geb.binding

import geb.Page
import geb.test.*

class BindingUpdaterSpec extends GebSpecWithServer {

	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<p>content</p>
			</body>
			</html>"""
		}
		go "/"
	}

	def binding = new Binding()
	def updater = new BindingUpdater(binding, browser)

	def "updater lifecycle"() {
		when:
		binding.browser

		then:
		thrown MissingPropertyException

		when:
		updater.initialize()

		then:
		binding.browser.is(browser)
		binding.$.call("p").text() == "content"
		binding.page.is(browser.page)

		when:
		updater.remove()

		and:
		binding.browser == null

		then:
		thrown MissingPropertyException

		when:
		page BindingUpdaterSpecPage1

		and:
		binding.page

		then:
		thrown MissingPropertyException
	}

	def "dollar function dispatch works in binding"() {
		given:
		def shell = new GroovyShell(binding)

		when:
		updater.initialize()

		then:
		shell.evaluate("\$('p', text: 'content').size()") == 1
	}

	def "page changing"() {
		given:
		updater.initialize()

		when:
		page BindingUpdaterSpecPage1

		then:
		binding.page instanceof BindingUpdaterSpecPage1

		when:
		page BindingUpdaterSpecPage2

		then:
		binding.page instanceof BindingUpdaterSpecPage2
	}

	def "dispatching with wrong args produces MME"() {
		when:
		updater.initialize()

		and:
		binding.go(123)

		then:
		thrown MissingMethodException
	}

}

class BindingUpdaterSpecPage1 extends Page {
	static content = {
		p1 { $("p") }
	}
}

class BindingUpdaterSpecPage2 extends Page {
	static content = {
		p2 { $("p") }
	}
}