/*
 * Copyright 2012 the original author or authors.
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
package geb

import geb.pages.ApiPage
import geb.pages.ContentPage
import geb.pages.ManualPage
import geb.pages.NotFoundPage
import geb.spock.GebSpec
import org.jsoup.Jsoup
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Unroll

import static ratpack.test.http.TestHttpClients.testHttpClient

@Stepwise
class SiteSmokeSpec extends GebSpec {

	@Shared
	def app = new LocalScriptApplicationUnderTest()

	private def getMenuItemElements() {
		def html = Jsoup.parse(testHttpClient(app).get().body.text)
		html.select('#header-content > ul > li')
	}

	private def manualLinksData() {
		def links = menuItemElements.first().select('a')
		links.collect { [it.text() - ' - current', it.attr('href')] }
	}

	private def apiLinksData() {
		[menuItemElements.get(1).select('a').first().attr('href'), 'manual/0.7.0/api/']
	}

	def setup() {
		browser.baseUrl = app.address.toString()
	}

	def cleanupSpec() {
		app.stop()
	}

	void 'index'() {
		when:
		go()

		then:
		at ContentPage
		firstHeaderText == 'What is it?'
	}

	void 'requesting a non-existing page'() {
		when:
		go('idontexist')

		then:
		at NotFoundPage
	}

	@Unroll
	void 'highlight pages - #pagePath'() {
		when:
		go(pagePath)

		then:
		at ContentPage
		firstHeaderText == pageHeader

		where:
		pagePath       | pageHeader
		'crossbrowser' | 'Cross Browser Automation'
		'content'      | 'Navigating Content'
		'pages'        | 'Page Objects'
		'async'        | 'Asynchronicity'
		'testing'      | 'Testing'
		'integration'  | 'Build System Integration'
	}

	void 'manual and api links are available'() {
		when:
		go()

		then:
		at ContentPage
		menuItems(0).name == 'Manual'
		menuItems(0).links
		menuItems(1).name == 'API'
		menuItems(1).links
	}

	@Unroll
	void 'manual - #manualVersion'() {
		when:
		go(link)

		then:
		at ManualPage
		version == manualVersion

		where:
		[manualVersion, link] << manualLinksData()
	}

	@Unroll
	void 'api - #link'() {
		when:
		go(link)

		then:
		at ApiPage

		where:
		link << apiLinksData()
	}
}
