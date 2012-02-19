package geb.navigator

import geb.test.GebSpec
import org.openqa.selenium.*
import spock.lang.*
import geb.Page

import geb.textmatching.*

@Stepwise
class NavigatorSpec extends GebSpec {

	@Shared WebDriver driver
	@Shared Navigator page
	@Shared testPageUrlString = getClass().getResource("/test.html") as String
	@Shared textmatching = new TextMatchingSupport()

	def setupSpec() {
		driver = browser.driver
		browser.go(testPageUrlString)
		page = Navigator.on(browser)
	}
	
	def "navigator with content coerces to true"() {
		given: def navigator = $("div")
		expect: navigator
	}
	
	def "empty navigator coerces to false"() {
		given: def navigator = $("bdo")
		expect: !navigator
	}
	
	def "getAtttribute returns false for boolean attributes that are not present"() {
		expect:
		def element = $("#the_plain_select")
		element.getAttribute("multiple") == "false"
		element.@multiple == "false"
	}

	def "getElement by index"() {
		expect:
		$("div").getElement(1).getAttribute("id") == "header"
		$("bdo").getElement(0) == null
	}
	
	@Unroll("\$('#selector1').add('#selector2') should result in the elements #expectedContent")
	def "add"() {
		when:
		def navigator = $(selector1).add(selector2)
		
		then:
		navigator*.@id == expectedIds
		
		where:
		selector1    | selector2  | expectedIds
		"#header"    | ".article" | ["header", "article-1", "article-2", "article-3"]
		"#header"    | "bdo"      | ["header"]
		"bdo"        | "#header"  | ["header"]
		"#article-1" | ".article" | ["article-1", "article-2", "article-3"]
	}

	@Unroll("calling remove(#index) on the navigator should leave #expectedSize elements")
	def "remove"() {
		when:
		iterations.times {
			navigator = navigator.remove(index)
		}

		then:
		navigator.size() == expectedSize

		where:
		navigator           | index | iterations | expectedSize
		$("li")             | 5     | 1          | 22
		$("li")             | 0     | 1          | 22
		$("li")             | -1    | 1          | 22
		$("li")             | 1     | 1          | 22
		$("li")             | 23    | 1          | 23
		$("li")             | 0     | 2          | 21
		$("li").find("bdo") | 0     | 1          | 0
	}
	
	@Unroll("\$('#selector1').has('#selector2') should return #expected")
	def "has with selector"() {
		given: def navigator = $(selector1)
		expect:
		navigator.has(selector2).collect {
			it.@id ? "${it.tag()}#${it.@id}" : it.tag()
		} == expected
		
		where:
		selector1 | selector2  | expected
		"div"     | "h1"       | ["div#container", "div#header"]
//		"div"     | ".article" | ["div#container", "div#content", "div#main"] // TODO: this fails due to http://code.google.com/p/selenium/issues/detail?id=1498
		"div"     | "h3"       | []
	}

	@Unroll("find('#selector') should return elements with #property of '#expected'")
	def "find by CSS selector"() {
		given: def navigator = $(selector)
		expect: navigator*.@id == expected

		where:
		selector                  | expected
		"#sidebar form input"     | ["keywords", "site-1", "site-2", "site-3", "checker1", "checker2"]
		"div#sidebar form input"  | ["keywords", "site-1", "site-2", "site-3", "checker1", "checker2"]
		".col-1 form input"       | ["keywords", "site-1", "site-2", "site-3", "checker1", "checker2"]
		"div.col-1 form input"    | ["keywords", "site-1", "site-2", "site-3", "checker1", "checker2"]
		"div#sidebar.col-1 input" | ["keywords", "site-1", "site-2", "site-3", "checker1", "checker2"]
		"#header"                 | ["header"]
		".col-3.module"           | ["navigation"]
		".module.col-3"           | ["navigation"]
		"#THIS_ID_DOES_NOT_EXIST" | []
	}

	@Unroll("find('#selector1').find('#selector2') should find #expectedSize elements")
	def "find by id in element context"() {
		expect:
		$(selector1).find(selector2).size() == expectedSize

		where:
		selector1    | selector2 | expectedSize
		"#container" | "#header" | 1
		"#footer"    | "#header" | 0
	}

	@Unroll("find('#selector') should find #expectedSize elements")
	def "find with grouped selectors"() {
		expect:
		$(selector).size() == expectedSize

		where:
		selector                                | expectedSize
		"#header  , #sidebar, #footer"          | 3
		"div ol  , #sidebar   , blockquote,bdo" | 5 + 1 + 1 + 0
	}

	@Unroll("find(#attributes) should find elements with the ids #expectedIds")
	def "find by attributes"() {
		expect: $(attributes)*.@id == expectedIds

		where:
		attributes                          | expectedIds
		[name: "keywords"]                  | ["keywords"]
		[name: ~/checker\d/]                | ["checker1", "checker2"]
		[name: "site", value: "google"]     | ["site-1"]
		[name: "DOES-NOT-EXIST"]            | []
		[id: "container"]                   | ["container"]
		[class: "article"]                  | ["article-1", "article-2", "article-3"]
		[id: "article-1", class: "article"] | ["article-1"]
		[id: "main", class: "article"]      | []
		[class: "col-3 module"]             | ["navigation"]
		[class: "module col-3"]             | ["navigation"]
		[class: ~/col-\d/]                  | ["navigation", "content", "main", "sidebar"]
	}

	@Issue("http://jira.codehaus.org/browse/GEB-14")
	def "find by attributes passing a class pattern should match any of the classes on an element"() {
		expect: $(class: ~/col-\d/)*.@id == ["navigation", "content", "main", "sidebar"]
	}

	@Unroll("find(text: '#text') should find #expectedSize elements")
	def "find by text"() {
		expect: $(text: text).size() == expectedSize

		where:
		text                            | expectedSize
		"First paragraph of article 2." | 1
		~/.*article 1\./                | 2
		"DOES NOT EXIST"                | 0
	}

	@Unroll("find('#selector', #index) should find the element with the id '#expectedId'")
	def "find by selector and index"() {
		when: def navigator = $(selector, index)
		then:
		navigator.size() == 1
		navigator.@id == expectedId

		where:
		selector   | index | expectedId
		".article" | 0     | "article-1"
		".article" | 1     | "article-2"
		".article" | 2     | "article-3"
		".article" | -1    | "article-3"
	}

	@Unroll("find('#selector', #attributes) should find elements with the ids #expectedIds")
	def "find by selector and attributes"() {
		expect: $(attributes, selector)*.@id == expectedIds

		where:
		selector   | attributes                              | expectedIds
		"input"    | [type: "checkbox"]                      | ["checker1", "checker2"]
		"input"    | [name: "site"]                          | ["site-1", "site-2", "site-3"]
		"input"    | [name: "site", value: "google"]         | ["site-1"]
		"input"    | [name: ~/checker\d/]                    | ["checker1", "checker2"]
		"bdo"      | [name: "whatever"]                      | []
		".article" | [:]                                     | ["article-1", "article-2", "article-3"]
		"div"      | [id: "container"]                       | ["container"]
		"div"      | [class: "article"]                      | ["article-1", "article-2", "article-3"]
		"div"      | [id: "article-1", class: "article"]     | ["article-1"]
		"div"      | [id: "main", class: "article"]          | []
		"div"      | [class: "col-3 module"]                 | ["navigation"]
		"div"      | [class: "module col-3"]                 | ["navigation"]
		"div"      | [class: ~/col-\d/]                      | ["navigation", "content", "main", "sidebar"]
	}

	@Unroll("find('#selector', text: '#text') should find #expectedSize elements")
	def "find by selector and text predicate"() {
		expect: $(selector, text: text).size() == expectedSize

		where:
		selector   | text                                   | expectedSize
		"p"        | "First paragraph of article 2."        | 1
		"p"        | ~/.*article 1\./                       | 2
		"p"        | "DOES NOT EXIST"                       | 0
		"p"        | textmatching.iContains("copyright")    | 1
		"p"        | textmatching.iNotContains("copyright") | 9
	}

	@Unroll("filter('#filter') should select elements with the ids #expectedIds")
	def "filter by selector"() {
		expect: navigator.filter(filter)*.@id == expectedIds

		where:
		navigator     | filter        | expectedIds
		$(".article") | "#article-2"  | ["article-2"]
		$(".article") | "#no-such-id" | []
		$("div")      | ".article"    | ["article-1", "article-2", "article-3"]
		// TODO: case for filter by tag
	}

	@Unroll("filter(#filter) should select elements with the ids #expectedIds")
	def "filter by attributes"() {
		expect: navigator.filter(filter)*.@id == expectedIds

		where:
		navigator       | filter                          | expectedIds
		$("input")      | [type: "checkbox"]              | ["checker1", "checker2"]
		$("input")      | [name: "site"]                  | ["site-1", "site-2", "site-3"]
		$("input")      | [name: "site", value: "google"] | ["site-1"]
		$(".article")   | [id: ~/article-[1-2]/]          | ["article-1", "article-2"]
		$("#article-1") | [id: "article-2"]               | []
	}

	@Unroll("filter(text: '#text') should select #expectedSize elements")
	def "filter by text"() {
		expect: navigator.filter(text: text).size() == expectedSize

		where:
		navigator | text                            | expectedSize
		$("p")    | "First paragraph of article 2." | 1
		$("p")    | ~/.*article 1\./                | 2
		$("p")    | "DOES NOT EXIST"                | 0
	}

	@Unroll("filter('#selector', #attributes should select elements with the ids #expectedIds")
	def "filter by selector and attributes"() {
		expect: navigator.filter(attributes, selector)*.@id == expectedIds

		where:
		navigator             | selector | attributes         | expectedIds
		$("a, input, select") | "input"  | [type: "checkbox"] | ["checker1", "checker2"]
		$("a, input, select") | "select" | [type: "checkbox"] | []
	}

	@Unroll("not('#selector') should select elements with the ids #expectedIds")
	def "not by selector"() {
		expect: navigator.not(selector)*.@id == expectedIds

		where:
		navigator     | selector      | expectedIds
		$(".article") | "#article-2"  | ["article-1", "article-3"]
		$(".article") | "#no-such-id" | ["article-1", "article-2", "article-3"]
	}

	@Unroll("calling next() on #selector should return #expectedIds")
	def "next selects immediately following elements"() {
		given: def navigator = $(selector)
		expect: navigator.next()*.@id == expectedIds

		where:
		selector        | expectedIds
		"#main"         | ["sidebar"]
		"#header"       | ["navigation"]
		".col-3"        | ["content", "footer"]
		"#container"    | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling nextAll() on #selector should return #expectedIds")
	def "nextAll selects all following elements"() {
		given: def navigator = $(selector)
		expect: navigator.nextAll()*.@id == expectedIds

		where:
		selector          | expectedIds
		"#main"           | ["sidebar"]
		"#header"         | ["navigation", "content", "footer"]
		".col-3"          | ["content", "footer"]
		"#content, #main" | ["footer", "sidebar"]
		"#DOESNOTEXIST"   | []
	}

	@Unroll("calling next(#nextSelector) on #selector should return #expectedIds")
	def "next with selector argument"() {
		given: def navigator = $(selector)
		expect: navigator.next(nextSelector)*.@id == expectedIds

		where:
		selector     | nextSelector | expectedIds
		"#keywords"  | "input"      | ["site-1"]
		"#keywords"  | "select"     | ["the_plain_select"]
		"input"      | "input"      | ["site-1", "site-2", "checker1", "checker2"]
		"input"      | "select"     | ["the_plain_select"]
		"#keywords"  | "bdo"        | []
		"#article-1" | ".article"   | ["article-2"]
	}

	@Unroll("calling nextAll(#nextSelector) on #selector should return #expectedIds")
	def "nextAll with selector"() {
		given: def navigator = $(selector)
		expect: navigator.nextAll(nextSelector)*.@id == expectedIds

		where:
		selector     | nextSelector | expectedIds
		"#keywords"  | "input"      | ["site-1", "site-2", "checker1", "checker2"]
		"#keywords"  | "select"     | ["the_plain_select", "the_multiple_select"]
		"input"      | "input"      | ["site-1", "site-2", "checker1", "checker2"]
		"#keywords"  | "bdo"        | []
		"#article-1" | ".article"   | ["article-2", "article-3"]
	}

	@Unroll("calling nextUntil(#nextSelector) on #selector should return #expectedIds")
	def "nextUntil with selector"() {
		given: def navigator = $(selector)

		expect:
		def nextElements = navigator.nextUntil(nextSelector).collect {
			it.@id ? "${it.tag()}#${it.@id}" : it.tag()
		}
		nextElements == expectedIds

		where:
		selector      | nextSelector  | expectedIds
		"#header"     | "#navigation" | []
		"#keywords"   | "input"       | ["br"]
		"#site-2"     | "select"      | ["label", "br", "label", "br", "input#checker1", "label", "br", "input#checker2", "label", "br"]
		"#navigation" | "bdo"         | ["div#content", "div#footer"]
	}

	@Unroll("calling previous() on #selector should return #expectedIds")
	def "previous selects immediately preceding elements"() {
		given: def navigator = $(selector)
		expect: navigator.previous()*.@id == expectedIds

		where:
		selector        | expectedIds
		"#footer"       | ["content"]
		"#navigation"   | ["header"]
		".col-3"        | ["header", "navigation"]
		"#container"    | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling prevAll() on #selector should return #expectedIds")
	def "prevAll selects immediately preceding elements"() {
		given: def navigator = $(selector)
		expect: navigator.prevAll()*.@id == expectedIds

		where:
		selector        | expectedIds
		"#footer"       | ["header", "navigation", "content"]
		"#navigation"   | ["header"]
		".col-3"        | ["header", "navigation"]
		"#container"    | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling previous(#previousSelector) on #selector should return #expectedIds")
	def "previous with selector"() {
		given: def navigator = $(selector)
		expect: navigator.previous(previousSelector)*.@id == expectedIds

		where:
		selector               | previousSelector | expectedIds
		"#the_multiple_select" | "input"          | ["checker2"]
		"#the_multiple_select" | "select"         | ["the_plain_select"]
		"input"                | "input"          | ["keywords", "site-1", "site-2", "checker1"]
		"select"               | "select"         | ["the_plain_select"]
		"#the_multiple_select" | "bdo"            | []
		"#article-3"           | ".article"       | ["article-2"]
	}

	@Unroll("calling prevAll(#previousSelector) on #selector should return #expectedIds")
	def "prevAll with selector"() {
		given: def navigator = $(selector)
		expect: navigator.prevAll(previousSelector)*.@id == expectedIds

		where:
		selector               | previousSelector | expectedIds
		"#the_multiple_select" | "input"          | ["checker2", "checker1", "site-2", "site-1", "keywords"]
		"#the_multiple_select" | "select"         | ["the_plain_select"]
		"input"                | "input"          | ["keywords", "site-1", "site-2", "checker1"]
		"select"               | "select"         | ["the_plain_select"]
		"#the_multiple_select" | "bdo"            | []
		"#article-3"           | ".article"       | ["article-2", "article-1"]
	}

	@Unroll("calling prevUntil(#previousSelector) on #selector should return #expectedIds")
	def "prevUntil with selector"() {
		given: def navigator = $(selector)

		expect:
		def previous = navigator.prevUntil(previousSelector).collect {
			it.@id ? "${it.tag()}#${it.@id}" : it.tag()
		}
		previous == expectedIds

		where:
		selector               | previousSelector | expectedIds
		"#the_multiple_select" | "input"          | ["select#the_plain_select", "br", "label"]
		"#the_multiple_select" | "select"         | []
		"#content"             | "#header"        | ["div#navigation"]
		"#content"             | "bdo"            | ["div#navigation", "div#header"]
	}

	@Unroll("calling parent() on #selector should return #expectedIds")
	def "parent selects immediate parent of each element"() {
		given: def navigator = $(selector)
		expect: navigator.parent()*.@id == expectedIds

		where:
		selector        | expectedIds
		"h1"            | ["header"]
		".article"      | ["main"]
		"option"        | ["the_plain_select", "the_multiple_select"]
		"html"          | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling parents() on #selector should return #expectedTags")
	def "parents selects all parents of each element"() {
		given: def navigator = $(selector)
		expect: navigator.parents()*.tag() == expectedTags

		where:
		selector        | expectedTags
		"h1"            | ["div", "div", "body", "html"]
		".article"      | ["div", "div", "div", "body", "html"]
		"html"          | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling parents(#parentSelector) on #selector should return #expectedIds")
	def "parents selects all parents of each element filtered by a selector"() {
		given: def navigator = $(selector)
		expect: navigator.parents(parentSelector)*.@id == expectedIds

		where:
		selector        | parentSelector | expectedIds
		"h1"            | "div"          | ["header", "container"]
		"h1"            | "#container"   | ["container"]
		".article"      | ".col-3"       | ["content"]
		"p"             | ".article"     | ["article-1", "article-2", "article-3"]
		"#DOESNOTEXIST" | "div"          | []
	}

	@Unroll("calling parentsUntil(#parentSelector) on #selector should return #expectedTags")
	def "parentsUntil selects all parents of each element filtered by a selector"() {
		given: def navigator = $(selector)

		expect:
		def parents = navigator.parentsUntil(parentSelector).collect {
			it.@id ? "${it.tag()}#${it.@id}" : it.tag()
		}
		parents == expectedTags

		where:
		selector        | parentSelector | expectedTags
		"h1"            | "body"         | ["div#header", "div#container"]
		"h1"            | "#container"   | ["div#header"]
		"h1"            | "bdo"          | ["div#header", "div#container", "body", "html"]
		"h1"            | "#header"      | []
		".article"      | "#container"   | ["div#main", "div#content"]
		"h2"            | "#content"     | ["div#article-1", "div#main", "div#article-2", "div#article-3"]
		"#DOESNOTEXIST" | "html"         | []
	}

	@Unroll("calling parent(#parentSelector) on #selector should return #expectedIds")
	def "parent with selector"() {
		given: def navigator = $(selector)
		expect: navigator.parent(parentSelector)*.@id == expectedIds

		where:
		selector         | parentSelector | expectedIds
		"#keywords"      | "div"          | []
		"#article-1"     | "div"          | ["main"]
		".article"       | "div"          | ["main"]
		"form, .article" | "div"          | ["main", "sidebar"]
		"form"           | ".col-1"       | ["sidebar"]
		"form"           | ".col-3"       | []
		"bdo"            | "div"          | []
	}

	@Unroll("calling closest(#closestSelector) on #selector should return #expectedIds")
	def "closest with selector"() {
		given: def navigator = $(selector)
		expect: navigator.closest(closestSelector)*.@id == expectedIds

		where:
		selector     | closestSelector | expectedIds
		"#keywords"  | "div"           | ["sidebar"]
		"input"      | "div"           | ["sidebar"]
		"ul, ol"     | "div"           | ["navigation", "sidebar"]
		"#keywords"  | "bdo"           | []
		"#article-1" | ".col-3"        | ["content"]
	}

	@Unroll("calling children() on #selector should return #expected")
	def "children selects immediate child elements of each element"() {
		given: def navigator = $(selector)
		expect: navigator.children()*.tag() == expected

		where:
		selector        | expected
		"#header"       | ["h1"]
		"#navigation"   | ["ul"]
		"#content"      | ["div", "div"]
		".article"      | ["h2", "div"] * 3
		"p"             | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling children(#childSelector) on #selector should return #expected")
	def "children with selector"() {
		given: def navigator = $(selector)
		expect: navigator.children(childSelector)*.tag() == expected

		where:
		selector     | childSelector | expected
		"#header"    | "h1"          | ["h1"]
		"#header"    | "div"         | []
		".article"   | ".content"    | ["div"] * 3
	}

	@Unroll("calling siblings() on #selector should return #expectedIds")
	def "siblings selects immediately following elements"() {
		given: def navigator = $(selector)
		expect: navigator.siblings()*.@id == expectedIds

		where:
		selector          | expectedIds
		"#header"         | ["navigation", "content", "footer"]
		"#footer"         | ["header", "navigation", "content"]
		"#content"        | ["header", "navigation", "footer"]
		"#header h1"      | []
		"#content, #main" | ["header", "navigation", "footer", "sidebar"]
		"#DOESNOTEXIST"   | []
	}

	@Unroll("calling siblings(#siblingSelector) on #selector should return #expectedIds")
	def "siblings with selector argument"() {
		given: def navigator = $(selector)
		expect: navigator.siblings(siblingSelector)*.@id == expectedIds

		where:
		selector            | siblingSelector | expectedIds
		"#site-1"           | "input"         | ["keywords", "site-2", "checker1", "checker2"]
		"#site-1"           | "select"        | ["the_plain_select", "the_multiple_select"]
		"#the_plain_select" | "select"        | ["the_multiple_select"]
		"#header"           | ".col-3"        | ["navigation", "content"]
		"#header"           | ".col-2"        | []
	}

	def "adding two navigators results in a new navigator with the unique set of elements"() {
		when: def navigator = (navigator1 + navigator2)

		then: navigator*.@id == expectedIds

		where:
		navigator1      | navigator2    | expectedIds
		$("#article-1") | $(".article") | ["article-1", "article-2", "article-3"]
	}

	def "unique is applied by default"() {
		when:
		def navigator = $("div").find("ol")

		then:
		navigator.size() == expectedSize

		where:
		expectedSize = 5
	}

	@Unroll("the value of text() on #selector should be '#expectedText'")
	def "text of the first element can be accessed as a property"() {
		given: def navigator = $(selector)
		expect: navigator.text() == expectedText

		where:
		selector | expectedText
		"p"      | "First paragraph of article 1."
		"hr"     | ""
		"bdo"    | null
	}

	@Unroll("the value of tag() on #selector should be '#expectedTag'")
	def "tagName of the first element can be accessed as a property"() {
		given: def navigator = $(selector)
		expect: navigator.tag() == expectedTag

		where:
		selector        | expectedTag
		"p"             | "p"
		".article"      | "div"
		"input, select" | "input"
		"bdo"           | null
	}

	@Unroll("navigator.attr('#attribute') should return '#expectedValue'")
	def "attribute access via jQuery-like method"() {
		expect: navigator.attr("$attribute") == expectedValue

		where:
		navigator           | attribute | expectedValue
		$("div", 0)         | "id"      | "container"
		$("div div", 1)     | "id"      | "navigation"
		$("#article-1 div") | "id"      | null
		$("#navigation a")  | "href"    | testPageUrlString + "#home"
		$("bdo")            | "id"      | null
	}

	@Unroll("navigator.@#attribute should return '#expectedValue'")
	def "attribute access via field operator"() {
		expect: navigator.@"$attribute" == expectedValue

		where:
		navigator           | attribute | expectedValue
		$("div", 0)         | "id"      | "container"
		$("div div", 1)     | "id"      | "navigation"
		$("#article-1 div") | "id"      | null
		$("#navigation a")  | "href"    | testPageUrlString + "#home"
		$("bdo")            | "id"      | null
	}

	@Unroll("navigator*.@#attribute should return #expectedValue")
	def "attributes of all elements accessed via field operator"() {
		expect: navigator*.@"$attribute" == expectedIds

		where:
		navigator          | attribute | expectedIds
		$("div")[0..<5]    | "id"      | ["container", "header", "navigation", "content", "main"]
		$("#navigation a") | "href"    | ["#home", "#about", "#contact"].collect { testPageUrlString + it }
		$("bdo")           | "id"      | []
	}

	@Unroll("the class names on #selector are #expected")
	def "getClassNames returns the classes of the first matched element"() {
		given: def navigator = $(selector)
		expect: navigator.classes() == expected

		where:
		selector      | expected
		"#article-1"  | ["article"]
		"#navigation" | ["col-3", "module"]
		"ol"          | []
		"bdo"         | []
	}

	@Unroll("the result of findClass('#className') on #selector should be #expectedResult")
	def hasClass() {
		given: def navigator = $(selector)
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
		navigator.is(expectedTag) == expectedResult

		where:
		navigator                     | expectedTag  | expectedResult
		$("div")                      | "div"        | true
		$("#article-1 p").parent()    | "div"        | true
		$("#article-1 p").parent()    | "blockquote" | true
		$("#article-1 p").parent()[0] | "blockquote" | false
	}

	def text() {
		expect:
		navigator.text().contains(expectedText) == expectedResult

		where:
		navigator       | expectedText      | expectedResult
		page            | "Article title 2" | true
		$("#article-2") | "Article title 2" | true
		$("#article-3") | "Article title 2" | false
	}

	@Ignore
	def attributes() {
		expect:
		navigator*.@"$key" == expectedValues

		where:
		navigator                   | key       | expectedValues
		$("input").withName("site") | "value"   | ["google", "thisone"]
		$("input").withName("site") | "checked" | ["checked", ""]
	}

	@Unroll("the dynamic method #fieldName() should return elements with the ids #expected")
	def "can find named inputs using a dynamic method call"() {
		when: def navigator = context."$fieldName"()
		then: navigator*.@id == expected

		where:
		context    | fieldName     | expected
		page       | "keywords"    | ["keywords"]
		$("form")  | "keywords"    | ["keywords"]
		page       | "site"        | ["site-1", "site-2", "site-3"]
		$("#main") | "keywords"    | []
		page       | "nosuchfield" | []
		$("bdo")   | "keywords"    | []
	}

	def "dynamic methods for finding fields do not accept arguments"() {
		when: context."$fieldName"(*arguments)
		then: thrown(MissingMethodException)

		where:
		context  | fieldName  | arguments
		page     | "keywords" | ["foo", "bar"]
		$("bdo") | "keywords" | ["foo"]
	}

	@Unroll("the value of '#fieldName' retrieved via property access should be '#expectedValue'")
	def "form field values can be retrieved using property access"() {
		expect: $("form")."$fieldName" == expectedValue

		where:
		fieldName         | expectedValue
		"keywords"        | "Enter keywords here"
		"checker1"        | false
		"checker2"        | "123"
		"textext"         | "The textarea content." // note whitespace has been removed
		"plain_select"    | "4"
		"multiple_select" | ["2", "4"]
	}

	@Issue("http://jira.codehaus.org/browse/GEB-57")
	@Unroll("the value of '#fieldName' when empty should be '#expectedValue'")
	def "empty text inputs are handled correctly"() {
		given: "the input has an empty value"
		def form = $("form")
		def initialValue = form."$fieldName"
		form."$fieldName"().getElement(0).clear()
		
		expect: "the input value retrieved by property access to be correct"
		form."$fieldName" == expectedValue
		
		cleanup:
		form."$fieldName" = initialValue

		where:
		fieldName         | expectedValue
		"keywords"        | ""
		"textext"         | ""
	}

	@Unroll("when a radio button with the value '#expectedValue' is selected getting then value of the group returns '#expectedValue'")
	def "get property access works on radio button groups"() {
		given:
		def radios = driver.findElements(By.name("site"))

		when:
		radios.find { it.getAttribute('value') == expectedValue }.click()

		then:
		$("form").site == expectedValue

		where:
		index | expectedValue
		0     | "google"
		1     | "thisone"
	}

	def "form field property access works on any node in the Navigator"() {
		when:
		def navigator = $(".article, form")

		then:
		navigator.keywords == "Enter keywords here"
	}

	def "invalid form field names raise the correct exception type"() {
		when:
		$("form").someFieldThatDoesNotExist

		then:
		thrown(MissingPropertyException)
	}

	@Unroll("setting the value of '#fieldName' to '#newValue' using property access sets the value of the input element")
	def "form field values can be set using property access"() {
		given:
		def form = $("form")
		def initialValue = form."$fieldName"

		when: form."$fieldName" = newValue
		then: form."$fieldName" == newValue
		cleanup: form."$fieldName" = initialValue

		where:
		fieldName         | newValue
		"keywords"        | "Lorem ipsum dolor sit amet"
		"site"            | "thisone"
		"checker1"        | "123"
		"checker2"        | false
		"textext"         | "Lorem ipsum dolor sit amet"
		"plain_select"    | "3"
		"multiple_select" | ["1", "3", "5"]
	}

	@Issue("http://jira.codehaus.org/browse/GEB-29")
	@Unroll("setting the value of '#fieldName' to '#newValue' using property access sets the value of the input element")
	def "form field values can be set using non-string values"() {
		given:
		def form = $("form")
		def initialValue = form."$fieldName"

		when: form."$fieldName" = newValue
		then: form."$fieldName" == expectedValue
		cleanup: form."$fieldName" = initialValue

		where:
		fieldName         | newValue   | expectedValue
		"keywords"        | true       | "true"
		"keywords"        | 123        | "123"
		"checker1"        | 123        | "123"
		"plain_select"    | 3          | "3"
		"multiple_select" | [1, 3, 5]  | ["1", "3", "5"]
	}

	def "when the value of a checkbox is set using a boolean then the checked-ness is set accordingly"() {
		given:
		def form = $("form")
		def initialChecker1 = form.checker1
		def initialChecker2 = form.checker2
		
		expect:
		form.checker1 == false
		form.checker2 == "123"
		
		when:
		form.checker1 = true
		form.checker2 = false
		
		then:
		form.checker1 == "123"
		form.checker2 == false

		when:
		form.checker1 = false
		form.checker2 = true
		
		then:
		form.checker1 == false
		form.checker2 == "123"
		
		cleanup:
		form.checker1 = initialChecker1
		form.checker2 = initialChecker2
	}
	
	@Unroll("when the radio button group's value is set to '#value' then the corresponding radio button is selected")
	def "set property access works on radio button groups"() {
		when:
		$("form").site = value

		then:
		driver.findElements(By.name("site")).find { it.getAttribute('value') == value }.click()

		cleanup:
		driver.findElement(By.id("site-1")).click()

		where:
		value << ["google", "thisone"]
	}
	
	@Issue("http://jira.codehaus.org/browse/GEB-118")
	def "setting a select to a value that isn't one of its options blows up"() {
		when:
		$("form").plain_select = "KTHXBYE"
		
		then:
		IllegalArgumentException e = thrown()
		e.message.contains "couldn't select option with text or value: KTHXBYE"
	}
	
	@Issue("http://jira.codehaus.org/browse/GEB-118")
	def "setting a multiple select to a value that isn't one of its options blows up"() {
		setup:
		def originalValues = $("form").multiple_select
		
		when:
		$("form").multiple_select = ["KTHXBYE"]
		
		then:
		IllegalArgumentException e = thrown()
		e.message.contains "couldn't select options with text or values: [KTHXBYE]"
		
		cleanup:
		$("form").multiple_select = originalValues
	}

	@Unroll("input value should be '#expected'")
	def "value() returns value of first element"() {
		expect: navigator.value() == expected

		where:
		navigator                     | expected
		$("#the_plain_select")        | "4"
		$("#the_multiple_select")     | ["2", "4"]
		$("#the_plain_select option") | "1"
		$("textarea")                 | "The textarea content." // note no leading/trailing whitespace
		$("#keywords")                | "Enter keywords here"
		$("#checker1")                | false
		$("#checker2")                | "123"
		$("#keywords, textarea")      | "Enter keywords here"
	}

	@Unroll("input values should be '#expected'")
	def "get value on all elements"() {
		expect: navigator*.value() == expected

		where:
		navigator                     | expected
		$("select")                   | ["4", ["2", "4"]]
		$("#the_plain_select option") | ["1", "2", "3", "4", "5"]
		$("#keywords, textarea")      | ["Enter keywords here", "The textarea content."] // note no leading/trailing whitespace for textarea
	}

	@Unroll("input value can be changed to '#newValue'")
	def "set value"() {
		given: def initialValue = navigator.value()
		when: navigator.value(newValue)
		then: navigator.value() == newValue
		cleanup: navigator.value(initialValue)

		where:
		navigator                 | newValue
		$("#the_plain_select")    | "2"
		$("#the_multiple_select") | ["1", "3", "5"]
		$("#keywords")            | "bar"
		$("textarea")             | "This is the new content of the textarea. Yeah!"
		$("#checker1")            | "123"
		$("#checker2")            | false
	}

	@Unroll("select element can be changed to #newValue using option label #optionLabel")
	def "select elements can be set using their label text as well as option value"() {
		given: def initialValue = navigator.value()
		when: navigator.value(optionLabel)
		then: navigator.value() == newValue
		cleanup: navigator.value(initialValue)

		where:
		navigator                 | optionLabel                             | newValue
		$("#the_plain_select")    | "Option #3"                             | "3"
		$("#the_multiple_select") | ["Option #1", "Option #3", "Option #5"] | ["1", "3", "5"]
	}

	@Issue("http://jira.codehaus.org/browse/GEB-37")
	@Unroll("radio button can be changed to #newValue using label text '#labelText'")
	def "radio buttons can be set using their label text"() {
		given: def initialValue = $("form").site
		when: $("form").site = labelText
		then: $("form").site == newValue
		cleanup: $("form").site = initialValue

		where:
		labelText | newValue
		"Site #1" | "google"
		"Site #2" | "thisone"
		"Site #3" | "bing"
	}

	@Ignore
	@Unroll("value() on '#selector' should return '#expected'")
	def "get value handles radio buttons as groups"() {
		given:
		def navigator = $(selector)

		expect:
		navigator.value() == expected
		navigator*.value() == [expected]

		where:
		selector           | expected
		"#site-1"          | "google"
		"#site-2"          | null
		"#site-1, #site-2" | "google"
	}

	@Ignore
	@Unroll("value('#newValue') on '#selector' should select the matching radio button")
	def "set value handles radio buttons as groups"() {
		given: def navigator = $(selector)
		when: navigator.value(newValue)
		then: navigator.value() == newValue
		cleanup: driver.findElement(By.id("site-1")).click()

		where:
		selector           | newValue
		"#site-1"          | "google"
		"#site-2"          | "thisone"
		"#site-1, #site-2" | "thisone"
	}

	@Unroll("find('#selector') << '#keystrokes' should append '#keystrokes' to the input's value")
	def "can use leftShift to enter text in inputs"() {
		given:
		def navigator = $(selector)
		def initialValue = navigator.value()

		when: navigator << keystrokes
		then: navigator.value() == old(navigator.value()) + keystrokes

		cleanup: navigator.value(initialValue)

		where:
		selector    | keystrokes
		"#keywords" | "abc"
		"textarea"  | "abc"
	}

	@Unroll("using leftShift on '#selector' will append text to all fields")
	def "can use leftShift to append text to multiple inputs"() {
		given:
		def navigator = $(selector)
		def initialValue = navigator.value()

		when: navigator << keystrokes
		then: navigator.every { it.value().endsWith(keystrokes) }

		cleanup: navigator.value(initialValue)

		where:
		selector              | keystrokes
		"#keywords, textarea" | "abc"
	}

	def "leftShift returns the navigator so appends can be chained"() {
		given:
		def navigator = page.keywords()
		def initialValue = navigator.value()

		when: navigator << "a" << "b" << "c"
		then: navigator.value().endsWith("abc")

		cleanup: navigator.value(initialValue)
	}

	def first() {
		expect:
		navigator.first().size() == 1
		navigator.first().@id == expectedId

		where:
		navigator     | expectedId
		$(".article") | "article-1"
	}

	def firstElement() {
		expect:
		navigator.firstElement().getAttribute("id") == expectedId

		where:
		navigator     | expectedId
		$(".article") | "article-1"
	}

	def last() {
		expect:
		navigator.last().size() == 1
		navigator.last().@id == expectedId

		where:
		navigator     | expectedId
		$(".article") | "article-3"
	}

	def lastElement() {
		expect:
		navigator.lastElement().getAttribute("id") == expectedId

		where:
		navigator     | expectedId
		$(".article") | "article-3"
	}

	def verifyNotEmpty() {
		expect:
		navigator.verifyNotEmpty()

		where:
		navigator << [$("#container"), $("#container").find("div")]
	}

	def "verifyNotEmtpy on empty Navigator"() {
		when: navigator.verifyNotEmpty()
		then: thrown(EmptyNavigatorException)

		where:
		navigator = $("#does_not_exist")
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
		def navigator = new NonEmptyNavigator(browser, element1, element2)

		when: navigator.click()

		then:
		1 * element1.click()
		0 * element2.click()
		0 * _
	}

	@Issue('GEB-160')
	@Unroll('click call returns reciever for parameters: #clickParams')
	def "click returns the receiver"() {
		when:
		def navigator = $('p')

		then:
		navigator.click(*clickParams).tag() == 'p'

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
	
	@Unroll
	def "can use eq(int) on Navigator"() {
		expect: navigator.eq(index).@id == expectedId
		where:
		navigator     | index | expectedId
		$("div")      | 0     | "container"
		$("div")      | 1     | "header"
		$("div")      | -1    | "footer"
		$(".article") | 0     | "article-1"
		$(".article") | 1     | "article-2"
		$(".article") | -1    | "article-3"
		$("bdo")      | 0     | null
	}

}
