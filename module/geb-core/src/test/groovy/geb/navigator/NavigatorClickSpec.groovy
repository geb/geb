/*
 * Copyright 2014 the original author or authors.
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
package geb.navigator

import geb.Page
import geb.error.UndefinedAtCheckerException
import geb.test.CrossBrowser
import geb.test.GebSpecWithServer
import spock.lang.Issue
import spock.lang.Unroll

@CrossBrowser
@Unroll
class NavigatorClickSpec extends GebSpecWithServer {

	@Issue('GEB-160')
	def 'click call returns receiver for parameters: #clickParams'() {
		given:
		html { button("") }

		when:
		def navigator = $('button')

		then:
		navigator.click(* clickParams).is(navigator)

		where:
		clickParams << [[], [Page], [[PageWithAtChecker, PageWithAtChecker]]]
	}

	def 'click can be used with pages without at checker'() {
		given:
		html { div('some text') }

		when:
		$('div').click(Page)

		then:
		notThrown(UndefinedAtCheckerException)
	}

	def 'click fails when used with a list of pages, one of which does not have an at checker'() {
		given:
		html { div('some text') }

		when:
		$('div').click([PageWithoutAtChecker, PageWithAtChecker])

		then:
		thrown(UndefinedAtCheckerException)
	}
}
