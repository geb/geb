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

import geb.error.UnexpectedPageException
import geb.test.GebSpecWithServer
import spock.lang.Unroll

class UnexpectedPagesSpec extends GebSpecWithServer {

	def setup() {
		responseHtml { request ->
			head {
				title(request.parameterMap.title.first())
			}
		}
	}

	void 'unxepected pages don not need to be defined'() {
		expect:
		to ExpectedPage
	}

	private void defineUnexpectedPages() {
		browser.config.unexpectedPages = [UnexpectedPage, AnotherUnexpectedPage]
	}

	@Unroll
	void 'verify that page response is configured as expected'() {
		given:
		defineUnexpectedPages()

		when:
		go "?title=$pageTitle"

		then:
		title == pageTitle

		where:
		pageTitle << ['expected', 'unexpected']
	}

	void 'an exception is not thrown when we are not at unexpected page'() {
		given:
		defineUnexpectedPages()

		when:
		via ExpectedPage

		then:
		at ExpectedPage
	}

	void 'an exception is thrown when we end up at an unexpected page'() {
		given:
		defineUnexpectedPages()

		when:
		via UnexpectedPage
		at ExpectedPage

		then:
		UnexpectedPageException e = thrown()
		e.getMessage() == 'An unexpected page geb.UnexpectedPage was encountered when expected to be at geb.ExpectedPage'
	}

	void 'it is possible to do at checking for an unexpected page'() {
		given:
		defineUnexpectedPages()

		when:
		via UnexpectedPage

		then:
		at UnexpectedPage
	}

	void 'an exception is thrown when we end up on an unexpected page when setting a page from a list of possible pages'() {
		given:
		defineUnexpectedPages()

		when:
		via UnexpectedPage
		page(ExpectedPage, AnotherExpectedPage)

		then:
		UnexpectedPageException e = thrown()
		e.getMessage() == 'An unexpected page geb.UnexpectedPage was encountered when trying to find page match (given potentials: [class geb.ExpectedPage, class geb.AnotherExpectedPage])'
	}

	void 'it is possible to pass an unexpected page when setting a page from a list of possible pages'() {
		given:
		defineUnexpectedPages()

		when:
		via AnotherUnexpectedPage
		page(ExpectedPage, AnotherExpectedPage, AnotherUnexpectedPage)

		then:
		page.getClass() == AnotherUnexpectedPage
	}

	void 'isAt returns false if we end up at an unexpected page'() {
		given:
		defineUnexpectedPages()

		when:
		via UnexpectedPage

		then:
		!isAt(ExpectedPage)
	}

	void 'when at-check-waiting enabled should not wait for unexpected pages'() {
		given:
		defineUnexpectedPages()
		browser.config.atCheckWaiting = true

		when:
		via ExpectedPage

		then:
		at(ExpectedPage)
	}
}

class UnexpectedPage extends Page {

	static url = "?title=unexpected"

	static at = { title == 'unexpected' }
}

class AnotherUnexpectedPage extends Page {

	static url = "?title=anotherUnexpected"

	static at = { title == 'anotherUnexpected' }
}

class ExpectedPage extends Page {

	static url = "?title=expected"

	static at = { title == 'expected' }
}

class AnotherExpectedPage extends Page {
	static url = "?title=anotherExpected"

	static at = { title == 'anotherExpected' }
}
