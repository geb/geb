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
