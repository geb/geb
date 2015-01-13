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

import geb.content.SimplePageContent
import geb.error.*
import geb.test.GebSpecWithServer
import spock.lang.Issue
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
class PageOrientedSpec extends GebSpecWithServer {

	def setupSpec() {
		server.get = { req, res ->
			def path = req.requestURI == "/b" ? "b" : "a"
			def other = path == "b" ? "a" : "b"
			res.outputStream << """
			<html>
				<head>
					<script type="text/javascript" charset="utf-8">
					setTimeout(function() {
						document.body.innerHTML += '<div id="dynamic">dynamic</div>';
					}, 500);
					</script>
				</head>
				<body>
					<a href="/$other" id="$path">$other</a>
					<div id="uri">$req.requestURI</div>
				</body>
			</html>"""
		}
	}

	def "verify our server is configured correctly"() {
		when:
		go "/"
		then:
		$("#a").empty == false

		when:
		go "/a"
		then:
		$("#a").empty == false

		when:
		go "/b"
		then:
		$("#b").empty == false
	}

	@Unroll
	def "verify the Page API works for '#contentName' content"() {
		when:
		via PageOrientedSpecPageA

		then:
		at PageOrientedSpecPageA

		when:
		page[contentName].click()

		then:
		at PageOrientedSpecPageB

		when:
		page[contentName].click()

		then:
		at PageOrientedSpecPageA

		where:
		contentName << ["link", "linkUsingPageInstance"]
	}

	@Unroll
	def "verify the Page API #methodName method works with a Page instance"() {
		given:
		def parametrizedPage = new PageOrientedSpecParametrizedPage(id: "uri")

		when:
		def currentPage = "$methodName"(parametrizedPage)

		then:
		currentPage == parametrizedPage

		where:
		methodName << ["via", "at", "to", "page"]
	}

	def "check accessing non navigator content"() {
		when:
		to PageOrientedSpecPageA
		then:
		linkText == "b"
	}

	def "error when required value not present"() {
		when:
		to PageOrientedSpecPageA
		notPresentValueRequired.text()
		then:
		thrown(RequiredPageValueNotPresent)
	}

	def "error when required component not present"() {
		when:
		to PageOrientedSpecPageA
		notPresentRequired.text()
		then:
		thrown(RequiredPageContentNotPresent)
	}

	def "no error when non required component not present"() {
		when:
		to PageOrientedSpecPageA
		notPresentNotRequired.text()
		then:
		notThrown(RequiredPageContentNotPresent)
	}

	def "no error when non required component times out"() {
		when:
		to PageOrientedSpecPageA
		def content = notPresentNotRequiredWithWait
		then:
		notThrown(Exception)
		!content
		content in SimplePageContent
	}

	def "error when explicitly requiring a component that is not present"() {
		when:
		to PageOrientedSpecPageA
		notPresentNotRequired.require()
		then:
		thrown(RequiredPageContentNotPresent)
	}

	def "no error when explicitly requiring component that is present"() {
		when:
		to PageOrientedSpecPageA
		link.require()
		then:
		notThrown(RequiredPageContentNotPresent)
	}

	@Unroll
	def "clicking on content with to specified changes the page for '#contentName' content"() {
		when:
		to PageOrientedSpecPageA
		page[contentName].click()

		then:
		page in PageOrientedSpecPageB

		where:
		contentName << ["link", "linkUsingPageInstance"]
	}

	@Unroll
	def "variant to should cycle through and select match for '#contentName' content"() {
		when:
		to PageOrientedSpecPageA
		page[contentName].click()

		then:
		page in PageOrientedSpecPageB

		where:
		contentName << ["linkWithVariantTo", "linkWithVariantToUsingPageInstances"]
	}

	@Unroll
	def "exception should be thrown when page specified in to is not the page we end up at - clicking on #clicked"() {
		when:
		to PageOrientedSpecPageA
		page[clicked].click()

		then:
		UnexpectedPageException e = thrown()
		e.message ==~ "At checker page verification failed for page .*"
		e.cause in cause

		where:
		clicked                                            | cause
		'linkWithNotMatchingTo'                            | AssertionError
		'linkWithNotMatchingToUsingPageInstance'           | AssertionError
		'linkWithToClassWithPlainFalseAt'                  | null
		'linkWithToClassWithPlainFalseAtUsingPageInstance' | null
	}

	@Unroll
	@SuppressWarnings(["SpaceAfterClosingBrace", "SpaceAfterOpeningBrace", "SpaceBeforeClosingBrace", "SpaceBeforeOpeningBrace"])
	def "exception should be thrown when support class #className methods are used on an uninitialized page instance"() {
		def pageInstance = new PageOrientedSpecPageA()

		when:
		pageInstance."$methodName"(*args)

		then:
		Throwable e = thrown(PageInstanceNotInitializedException)
		e.message == "Instance of page class geb.PageOrientedSpecPageA has not been initialized. Please pass it to Browser.to(), Browser.via(), Browser.page() or Browser.at() before using it."

		where:
		className                | methodName    | args
		"PageContentSupport"     | "someContent" | []
		"Navigable"              | "find"        | [""]
		"DownloadSupport"        | "download"    | [""]
		"WaitingSupport"         | "waitFor"     | [{}]
		"FrameSupport"           | "withFrame"   | ["frame-id", {}]
		"InteractionsSupport"    | "interact"    | [{}]
		"AlertAndConfirmSupport" | "withAlert"   | [{}]
	}

	@Unroll
	def "unexpected exceptions thrown in at checkers should bubble up from click for '#contentName' content"() {
		when:
		to PageOrientedSpecPageA
		page[contentName].click()

		then:
		Throwable e = thrown()
		e.message == "from at checker"

		where:
		contentName << ["linkWithToClassThrowingExceptionInAt", "linkWithToClassThrowingExceptionInAtUsingPageInstance"]
	}

	@Unroll
	def "exception should be thrown when no to values match for '#contentName' content"() {
		when:
		to PageOrientedSpecPageA
		page[contentName].click()

		then:
		thrown(UnexpectedPageException)

		where:
		contentName << ["linkWithVariantToNoMatches", "linkWithVariantToNoMatchesUsingPageInstances"]
	}

	def "call in mixed in method from TextMatchingSupport"() {
		when:
		to PageOrientedSpecPageA
		then:
		contains("b").matches("abc")
	}

	def "can use attribute notation on page content"() {
		when:
		to PageOrientedSpecPageA
		then:
		link.@id == "a"
	}

	@Issue("http://jira.codehaus.org/browse/GEB-2")
	def "can call instance methods from content definition blocks"() {
		when:
		to InstanceMethodPage
		then:
		val == 3
	}

	@Issue("http://jira.codehaus.org/browse/GEB-139")
	def "convertToPath should not introduce slashes were it should not"() {
		when: 'we go to the page by specifying the parameter manually'
		via ConvertPage, theParam: "foo"
		def manual = $('#uri').text()

		and: 'using the convertToPath method'
		via ConvertPage, 'foo'
		def converted = $('#uri').text()

		then: 'the results are the same'
		converted == manual

		then: 'the raw page url does not contain the extra slash'
		getPageUrl(convertToPath('foo')) == '/theview?theParam=foo'

		and: 'the default convertToPath still works'
		getPageUrl(convertToPath(1, 2)) == '/theview/1/2'
	}

	def "verify content aliasing works"() {
		when:
		to PageOrientedSpecPageA
		then:
		linkTextAlias == 'b'
	}

	def 'at check should fail when no at checker is defined on the page object class'() {
		when:
		at PageWithoutAtChecker

		then:
		def e = thrown UndefinedAtCheckerException
		e.message == "No at checker has been defined for page class geb.PageWithoutAtChecker."
	}

	@Unroll
	def "exception should be thrown when no at checker is defined for one of the to pages for '#contentName' content"() {
		when:
		to PageWithLinkToPageWithoutAtChecker
		page[contentName].click()

		then:
		thrown UndefinedAtCheckerException

		where:
		contentName << ["link", "linkUsingPageInstances"]
	}

	@Unroll
	def "invalid page parameter ( #pageParameter ) for content throws an informative exception"() {
		when:
		to pageClass

		then:
		InvalidPageContent e = thrown()
		e.message == "'page' content parameter should be a class that extends Page but it isn't for $contentName - ${pageClass.newInstance()}: $pageParameter"

		where:
		pageClass                        | contentName  | pageParameter
		PageContentStringPageParam       | 'wrongClass' | String
		PageContentPageInstancePageParam | 'instance'   | new PageContentPageInstancePageParam()
	}

	@Unroll
	def "implicitly waits when at checking if toWait content option is specified for '#contentName' content"() {
		when:
		to PageOrientedSpecPageA

		and:
		page[contentName].click()

		then:
		page in PageOrientedSpecPageE

		where:
		contentName << ["linkWithToWait", "linkWithToWaitUsingPageInstance"]
	}

	@Unroll
	def "implicitly waits when at checking if toWait content option is specified and to option contains a list of candidates for '#contentName' content"() {
		when:
		to PageOrientedSpecPageA

		and:
		page[contentName].click()

		then:
		page in PageOrientedSpecPageE

		where:
		contentName << ["linkWithToWaitAndVariantTo", "linkWithToWaitAndVariantToUsingPageInstances"]
	}
}

class PageOrientedSpecPageA extends Page {
	static at = { link }
	static content = {
		link(to: PageOrientedSpecPageB) { $("#a") }
		linkUsingPageInstance(to: new PageOrientedSpecPageB()) { $("#a") }
		linkWithNotMatchingTo(to: PageOrientedSpecPageC) { $("#a") }
		linkWithNotMatchingToUsingPageInstance(to: new PageOrientedSpecPageC()) { $("#a") }
		linkWithToClassThrowingExceptionInAt(to: PageWithAtCheckerThrowingException) { $("#a") }
		linkWithToClassThrowingExceptionInAtUsingPageInstance(to: new PageWithAtCheckerThrowingException()) { $("#a") }
		linkWithToClassWithPlainFalseAt(to: PageWithAtCheckerReturningFalse) { $("#a") }
		linkWithToClassWithPlainFalseAtUsingPageInstance(to: new PageWithAtCheckerReturningFalse()) { $("#a") }
		linkWithVariantTo(to: [PageOrientedSpecPageD, PageOrientedSpecPageC, PageOrientedSpecPageB]) { link }
		linkWithVariantToUsingPageInstances(to: [new PageOrientedSpecPageD(), new PageOrientedSpecPageC(), new PageOrientedSpecPageB()]) { link }
		linkWithVariantToNoMatches(to: [PageOrientedSpecPageD, PageOrientedSpecPageC]) { link }
		linkWithVariantToNoMatchesUsingPageInstances(to: [new PageOrientedSpecPageD(), new PageOrientedSpecPageC()]) { link }
		linkWithToWait(to: PageOrientedSpecPageE, toWait: true) { link }
		linkWithToWaitUsingPageInstance(to: new PageOrientedSpecPageE(), toWait: true) { link }
		linkWithToWaitAndVariantTo(to: [PageOrientedSpecPageC, PageOrientedSpecPageE], toWait: true) { link }
		linkWithToWaitAndVariantToUsingPageInstances(to: [new PageOrientedSpecPageC(), new PageOrientedSpecPageE()], toWait: true) { link }
		linkText { link.text().trim() }
		linkTextAlias(aliases: 'linkText')
		notPresentValueRequired { $("div#asdfasdf").text() }
		notPresentRequired { $("div#nonexistant") }
		notPresentNotRequired(required: false) { $("div#nonexistant") }
		notPresentNotRequiredWithWait(required: false, wait: 1) { $("div#nonexistant") }
	}
}

class PageOrientedSpecPageB extends Page {
	static at = { link }
	static content = {
		link(to: PageOrientedSpecPageA) { $("#b") }
		linkUsingPageInstance(to: new PageOrientedSpecPageA()) { $("#b") }
		linkText { link.text() }
	}
}

class PageOrientedSpecPageC extends Page {
	static at = { false }
}

@SuppressWarnings("ComparisonOfTwoConstants")
class PageOrientedSpecPageD extends Page {
	static at = { assert 1 == 2 }
}

class PageOrientedSpecPageE extends Page {
	static at = { dynamic }
	static content = {
		dynamic { $("#dynamic") }
	}

}

class ConvertPage extends Page {
	static url = '/theview'

	String convertToPath(param) {
		"?theParam=$param"
	}
}

class InstanceMethodPage extends Page {
	static content = {
		val { getValue() }
	}

	def getValue() { 3 }
}

class PageContentPageInstancePageParam extends Page {
	static content = {
		instance(page: new PageContentPageInstancePageParam()) { $('a') }
	}
}

class PageContentStringPageParam extends Page {
	static content = {
		wrongClass(page: String) { $('a') }
	}
}

class PageWithAtChecker extends Page {
	static at = { false }
}

class PageWithoutAtChecker extends Page {
}

class PageWithLinkToPageWithoutAtChecker extends Page {
	static content = {
		link(to: [PageWithAtChecker, PageWithoutAtChecker]) { $("#a") }
		linkUsingPageInstances(to: [new PageWithAtChecker(), new PageWithoutAtChecker()]) { $("#a") }
	}
}

class PageWithAtCheckerThrowingException extends Page {
	static at = { throw new Throwable('from at checker') }
}

class PageWithAtCheckerReturningFalse extends Page {
	//this circumvents implicit assertion AST transformation
	static atChecker = { false }
	static at = atChecker
}

class PageOrientedSpecParametrizedPage extends Page {
	String id

	static at = { elementWithId }
	static content = {
		elementWithId { $(id: id) }
	}
}
