package geb.navigator

import geb.Page
import org.openqa.selenium.WebElement
import spock.lang.Issue
import spock.lang.Unroll

@Unroll
class NavigatorSpec extends AbstractNavigatorSpec {

	def "navigator with content coerces to true"() {
		given:
		def navigator = $("div")
		expect: navigator
	}

	def "empty navigator coerces to false"() {
		given:
		def navigator = $("bdo")
		expect: !navigator
	}

	def "getAtttribute returns null for boolean attributes that are not present"() {
		expect:
		def element = $("#the_plain_select")
		element.getAttribute("multiple") == null
		element.@multiple == null
	}

	def "getElement by index"() {
		expect:
		$("div").getElement(1).getAttribute("id") == "header"
		$("bdo").getElement(0) == null
	}

	def "\$('#selector1').add('#selector2') should result in the elements #expectedContent"() {
		when:
		def navigator = $(selector1).add(selector2).unique()

		then:
		navigator*.@id == expectedIds

		where:
		selector1    | selector2  | expectedIds
		"#header"    | ".article" | ["header", "article-1", "article-2", "article-3"]
		"#header"    | "bdo"      | ["header"]
		"bdo"        | "#header"  | ["header"]
		"#article-1" | ".article" | ["article-1", "article-2", "article-3"]
	}

	def "calling remove(#index) on the navigator should leave #expectedSize elements"() {
		when:
		def navigator = $(selector)
		iterations.times {
			navigator = navigator.remove(index)
		}

		then:
		navigator.size() == expectedSize

		where:
		selector | index | iterations | expectedSize
		"li"     | 5     | 1          | 22
		"li"     | 0     | 1          | 22
		"li"     | -1    | 1          | 22
		"li"     | 1     | 1          | 22
		"li"     | 23    | 1          | 23
		"li"     | 0     | 2          | 21
		"li bdo" | 0     | 1          | 0
	}

	def "\$('#selector1').has('#selector2') should return #expected"() {
		given:
		def navigator = $(selector1)
		expect:
		navigator.has(selector2).collect {
			it.@id ? "${it.tag()}#${it.@id}" : it.tag()
		} == expected

		where:
		selector1 | selector2 | expected
		"div"     | "h1"      | ["div#container", "div#header"]
//		"div"     | ".article" | ["div#container", "div#content", "div#main"] // TODO: this fails due to http://code.google.com/p/selenium/issues/detail?id=1498
		"div" | "h3" | []
	}

	def "not('#selector') should select elements with the ids #expectedIds"() {
		expect: $(".article").not(selector)*.@id == expectedIds

		where:
		selector      | expectedIds
		"#article-2"  | ["article-1", "article-3"]
		"#no-such-id" | ["article-1", "article-2", "article-3"]
	}

	def "adding two navigators results in a new navigator with all of the elements"() {
		when:
		def navigator = $(navigator1) + $(navigator2)

		then: navigator*.@id == expectedIds

		where:
		navigator1   | navigator2 | expectedIds
		"#article-1" | ".article" | ["article-1", "article-1", "article-2", "article-3"]
	}

	def "navigator can contain duplicate elements"() {
		when:
		def navigator = $("div").find("ol")

		then:
		navigator.size() == expectedSize

		where:
		expectedSize = 15
	}

	def "the value of text() on #selector should be '#expectedText'"() {
		given:
		def navigator = $(selector)
		expect: navigator.text() == expectedText

		where:
		selector | expectedText
		"p"      | "First paragraph of article 1."
		"hr"     | ""
		"bdo"    | null
	}

	def "the value of tag() on #selector should be '#expectedTag'"() {
		given:
		def navigator = $(selector)
		expect: navigator.tag() == expectedTag

		where:
		selector        | expectedTag
		"p"             | "p"
		".article"      | "div"
		"input, select" | "input"
		"bdo"           | null
	}

	def "navigator.attr('#attribute') should return '#expectedValue'"() {
		expect: $(* selector).attr("$attribute") == expectedValue

		where:
		selector           | attribute | expectedValue
		["div", 0]         | "id"      | "container"
		["div div", 1]     | "id"      | "navigation"
		["#article-1 div"] | "id"      | null
		["#navigation a"]  | "href"    | server.baseUrl + "#home"
		["bdo"]            | "id"      | null
	}

	def "navigator.@#attribute should return '#expectedValue'"() {
		expect: $(* selector).@"$attribute" == expectedValue

		where:
		selector           | attribute | expectedValue
		["div", 0]         | "id"      | "container"
		["div div", 1]     | "id"      | "navigation"
		["#article-1 div"] | "id"      | null
		["#navigation a"]  | "href"    | server.baseUrl + "#home"
		["bdo"]            | "id"      | null
	}

	def "navigator*.@#attribute"() {
		expect: mod.call($(selector))*.@"$attribute" == expectedValue

		where:
		selector        | mod          | attribute | expectedValue
		"div"           | { it[0..4] } | "id"      | ["container", "header", "navigation", "content", "main"]
		"#navigation a" | { it }       | "href"    | ["#home", "#about", "#contact"].collect { server.baseUrl + it }
		"bdo"           | { it }       | "id"      | []
	}

	def "the class names on #selector are #expected"() {
		given:
		def navigator = $(selector)
		expect: navigator.classes() == expected

		where:
		selector      | expected
		"#article-1"  | ["article"]
		"#navigation" | ["col-3", "module"]
		"ol"          | []
		"bdo"         | []
	}

	def "the result of findClass('#className') on #selector should be #expectedResult"() {
		given:
		def navigator = $(selector)
		expect: navigator.hasClass(className) == expectedResult

		where:
		selector   | className | expectedResult
		".article" | "article" | true
		"div"      | "module"  | true
		"#content" | "col-3"   | true
		"#content" | "col-2"   | false
		"#content" | "col"     | false
	}

	def is() {
		expect:
		mod.call($(selector)).is(expectedTag) == expectedResult

		where:
		selector       | mod                | expectedTag  | expectedResult
		"div"          | { it }             | "div"        | true
		"#article-1 p" | { it.parent() }    | "div"        | true
		"#article-1 p" | { it.parent() }    | "blockquote" | true
		"#article-1 p" | { it.parent()[0] } | "blockquote" | false
	}

	def text() {
		expect:
		(selector == null ? $() : $(selector)).text().contains(expectedText) == expectedResult

		where:
		selector     | expectedText      | expectedResult
		null         | "Article title 2" | true
		"#article-2" | "Article title 2" | true
		"#article-3" | "Article title 2" | false
	}

	def first() {
		expect:
		$(".article").first().size() == 1
		$(".article").first().@id == "article-1"
	}

	def firstElement() {
		expect:
		$(".article").firstElement().getAttribute("id") == "article-1"
	}

	def last() {
		expect:
		$(".article").last().size() == 1
		$(".article").last().@id == "article-3"
	}

	def lastElement() {
		expect:
		$(".article").lastElement().getAttribute("id") == "article-3"
	}

	def verifyNotEmpty() {
		expect:
		$("#container").verifyNotEmpty()
		$("#container").find("div").verifyNotEmpty()
	}

	def "verifyNotEmtpy on empty Navigator"() {
		when: $("#does_not_exist").verifyNotEmpty()
		then: thrown(EmptyNavigatorException)
	}

	def "displayed property"() {
		expect:
		$("p").displayed // first p in page is displayed
		$("#hidden-paragraph").displayed == false
		$(".non-existant").displayed == false
	}

	def "click is called only on the first element of the navigator"() {
		given:
		def element1 = Mock(WebElement)
		def element2 = Mock(WebElement)
		def navigator = new NonEmptyNavigator(browser, [element1, element2])

		when: navigator.click()

		then:
		1 * element1.click()
		0 * element2.click()
		0 * _
	}

	@Issue('GEB-160')
	def 'click call returns reciever for parameters: #clickParams'() {
		when:
		def navigator = $('p')

		then:
		navigator.click(* clickParams).tag() == 'p'

		where:
		clickParams << [[], [Page], [[Page]]]
	}

	def accessingWebElements() {
		when:
		def articles = $("div.article")

		then:
		articles.size() == 3
		articles.firstElement().getAttribute("id") == "article-1"
		articles.allElements()*.getAttribute("id") == ["article-1", "article-2", "article-3"]
		articles.lastElement().getAttribute("id") == "article-3"
	}

	def "can use eq(int) on \$(#selector)"() {
		expect: $(selector).eq(index).@id == expectedId
		where:
		selector   | index | expectedId
		"div"      | 0     | "container"
		"div"      | 1     | "header"
		"div"      | -1    | "footer"
		".article" | 0     | "article-1"
		".article" | 1     | "article-2"
		".article" | -1    | "article-3"
		"bdo"      | 0     | null
	}

	def "leftShift returns the navigator so appends can be chained"() {
		given:
		def navigator = page.keywords()
		def initialValue = navigator.value()

		when: navigator << "a" << "b" << "c"
		then: navigator.value().endsWith("abc")

		cleanup: navigator.value(initialValue)
	}

}
