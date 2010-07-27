package geb.navigator

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore
import geb.navigator.Navigator
import geb.navigator.MatchType
import geb.navigator.EmptyNavigatorException

class NavigatorSpec extends Specification {

	@Shared WebDriver driver
	@Shared geb.navigator.Navigator onPage

	def setupSpec() {
		driver = new HtmlUnitDriver()
		driver.get(getClass().getResource("/test.html") as String)
		onPage = Navigator.on(driver)
	}

	def cleanupSpec() {
		driver.close()
	}

	def "getElement by index"() {
		expect:
		onPage.find("div").getElement(1).getAttribute("id") == "header"
		onPage.find("bdo").getElement(0) == null
	}

	def "remove"() {
		when:
		iterations.times {
			navigator = navigator.remove(index)
		}

		then:
		navigator.size() == expectedSize

		where:
		navigator                     | index | iterations | expectedSize
		onPage.find("li")             | 5     | 1          | 22
		onPage.find("li")             | 0     | 1          | 22
		onPage.find("li")             | -1    | 1          | 22
		onPage.find("li")             | 1     | 1          | 22
		onPage.find("li")             | 23    | 1          | 23
		onPage.find("li")             | 0     | 2          | 21
		onPage.find("li").find("bdo") | 0     | 1          | 0
	}

	def "find by CSS selector"() {
		when:
		def navigator = onPage.find(selector)

		then:
		navigator."$property"() == expectedValue

		where:
		selector                  | property       | expectedValue
		"#content div li"         | "trimmedText"  | "Item #1"
		"div.article h2 a"        | "trimmedTexts" | ["Article title 1", "Article title 2", "Article title 3"]
		"#header"                 | "id"           | "header"
		".col-3.module"           | "id"           | "navigation"
		".module.col-3"           | "id"           | "navigation"
		"#THIS_ID_DOES_NOT_EXIST" | "size"         | 0
	}

	def "find by id in element context"() {
		expect:
		onPage.find(selector1).find(selector2).size() == expectedSize

		where:
		selector1    | selector2 | expectedSize
		"#container" | "#header" | 1
		"#footer"    | "#header" | 0
	}

	def "find with grouped selectors"() {
		expect:
		onPage.find(selector).size() == expectedSize

		where:
		selector                                | expectedSize
		"#header  , #sidebar, #footer"          | 3
		"div ol  , #sidebar   , blockquote,bdo" | 5 + 1 + 1 + 0
	}

	def "find by attributes"() {
		expect: onPage.find(attributes).ids() == expectedIds

		where:
		attributes                      | expectedIds
		[name: "keywords"]              | ["keywords"]
		[name: ~/checker\d/]            | ["checker1", "checker2"]
		[name: "site", value: "google"] | ["site-1"]
		[name: "DOES-NOT-EXIST"]        | []
	}

	def "find by text"() {
		expect: onPage.find(text: text).size() == expectedSize

		where:
		text                            | expectedSize
		"First paragraph of article 2." | 1
		~/.*article 1\./                | 2
		"DOES NOT EXIST"                | 0
	}

	def "find by selector and index"() {
		expect:
		navigator.size() == 1
		navigator.id() == expectedId

		where:
		navigator                   | expectedId
		onPage.find(".article", 0)  | "article-1"
		onPage.find(".article", 1)  | "article-2"
		onPage.find(".article", 2)  | "article-3"
		onPage.find(".article", -1) | "article-3"
	}

	def "find by selector and attributes"() {
		expect: onPage.find(attributes, selector).ids() == expectedIds

		where:
		selector   | attributes                              | expectedIds
		"input"    | [type: "checkbox"]                      | ["checker1", "checker2"]
		"input"    | [name: "site"]                          | ["site-1", "site-2"]
		"input"    | [name: "site", value: "google"]         | ["site-1"]
		"input"    | [name: ~/checker\d/]                    | ["checker1", "checker2"]
		"bdo"      | [name: "whatever"]                      | []
		".article" | [:]                                     | ["article-1", "article-2", "article-3"]
	}

	def "find by selector and text predicate"() {
		expect: onPage.find(selector, text: text).size() == expectedSize

		where:
		selector   | text                            | expectedSize
		"p"        | "First paragraph of article 2." | 1
		"p"        | ~/.*article 1\./                | 2
		"p"        | "DOES NOT EXIST"                | 0
	}

	def "filter by selector"() {
		expect: navigator.filter(filter).ids() == expectedIds

		where:
		navigator               | filter        | expectedIds
		onPage.find(".article") | "#article-2"  | ["article-2"]
		onPage.find(".article") | "#no-such-id" | []
		onPage.find("div")      | ".article"    | ["article-1", "article-2", "article-3"]
		// TODO: case for filter by tag
	}

	def "filter by attributes"() {
		expect: navigator.filter(filter).ids() == expectedIds

		where:
		navigator                 | filter                          | expectedIds
		onPage.find("input")      | [type: "checkbox"]              | ["checker1", "checker2"]
		onPage.find("input")      | [name: "site"]                  | ["site-1", "site-2"]
		onPage.find("input")      | [name: "site", value: "google"] | ["site-1"]
		onPage.find(".article")   | [id: ~/article-[1-2]/]          | ["article-1", "article-2"]
		onPage.find("#article-1") | [id: "article-2"]               | []
	}

	def "filter by text"() {
		expect: navigator.filter(text: text).size() == expectedSize

		where:
		navigator        | text                            | expectedSize
		onPage.find("p") | "First paragraph of article 2." | 1
		onPage.find("p") | ~/.*article 1\./                | 2
		onPage.find("p") | "DOES NOT EXIST"                | 0
	}

	def "next selects following elements"() {
		expect:
		navigator.next().is(expectedTag)

		where:
		navigator                | expectedTag
		onPage.find("div") | "div"
		onPage.find("div") | "hr"
	}

	def "next selects single element"() {
		when:
		def navigator = onPage.find(selector)
		iterations.times {
			navigator = navigator.next()
		}

		then:
		navigator.id() == expectedId

		where:
		selector  | iterations | expectedId
		"#header" | 1          | "navigation"
		"#header" | 2          | "content"
	}

	def "next returns empty if element has no sibling"() {
		when:
		def navigator = onPage.find("body").next()

		then:
		navigator.isEmpty()
	}

	def "next with tag argument"() {
		expect: expectedId ? navigator.next(tag).id() == expectedId : navigator.next(tag).isEmpty()

		where:
		navigator                | tag      | expectedId
		onPage.find("#keywords") | "select" | "the_plain_select"
		onPage.find("#keywords") | "bdo"    | null
	}

	@Ignore
	def "previous selects preceding elements"() {
		expect:
		navigator.previous().is(expectedTag)

		where:
		navigator          | expectedTag
		onPage.find("div") | "div"
		onPage.find("div") | "hr"
	}

	@Ignore
	def "previous selects single element"() {
		when:
		def navigator = onPage.find(selector)
		iterations.times {
			navigator = navigator.previous()
		}

		then:
		navigator.id() == expectedId

		where:
		selector   | iterations | expectedId
		"#content" | 1          | "navigation"
		"#content" | 2          | "header"
	}

	def "previous returns empty if element has no sibling"() {
		when:
		def navigator = onPage.find("head").previous()

		then:
		navigator.isEmpty()
	}

	@Ignore
	def "previous with tag argument"() {
		when:
		navigator = navigator.previous(tag)

		then:
		expectedId ? navigator.id() == expectedId : navigator.isEmpty()

		where:
		navigator                           | tag     | expectedId
		onPage.find("#the_multiple_select") | "input" | "checker2"
		onPage.find("#keywords")            | "bdo"   | null
	}

	def "parent selects parent of single element"() {
		when:
		def navigator = onPage.find("#content").parent()

		then:
		navigator.id() == "container"
	}

	def "parent selects parents of multiple elements"() {
		when:
		def navigator = onPage.find(selector).parent()
		if (parentTag) {
			navigator = navigator.withTag(parentTag)
		}

		then:
		navigator.size() == expectedSize

		where:
		selector   | parentTag | expectedSize
		"#content" | null      | 1
		"li"       | null      | 6
		"li"       | "ol"      | 5
	}

	def "parent with tag argument"() {
		when:
		navigator = navigator.parent(tag)

		then:
		navigator.size() == 1
		!expectedId || navigator.id() == expectedId

		where:
		navigator                      | tag    | expectedId
		onPage.find("ol.ol-simple li") | "div"  | "sidebar"
		onPage.find("option")          | "form" | null
	}

	def unique() {
		when: def navigator = (navigator1 + navigator2).unique()

		then: navigator.size() == expectedSize

		where:
		navigator1                | navigator2                    | expectedSize
		onPage.find("#article-1") | onPage.find(".article") | 3
	}

	def "unique is applied by default"() {
		when:
		def navigator = onPage.find("div").find("ol")

		then:
		navigator.size() == expectedSize

		where:
		expectedSize = 5
	}

	def id() {
		expect:
		navigator.id() == expectedId

		where:
		navigator                     | expectedId
		onPage.find("div", 0)         | "container"
		onPage.find("div div", 1)     | "navigation"
		onPage.find("#article-1 div") | ""
		onPage.find("bdo")            | null
	}

	def ids() {
		expect:
		navigator.ids() == expectedIds

		where:
		navigator                 | expectedIds
		onPage.find("div")[0..<5] | ["container", "header", "navigation", "content", "main"]
		onPage.find("bdo")        | []
	}

	@Unroll("findByAttribute '#key' with value '#value' and matcher #matcher should find #expectedSize elements")
	def findByAttribute() {
		expect:
		onPage.findByAttribute(key, matcher, value).size() == expectedSize

		where:
		key     | matcher                             | value       | expectedSize
		"lang"  | MatchType.EXISTING                  | null        | 1
		"align" | MatchType.EXISTING                  | "ol"        | 0
		"class" | MatchType.EQUALS                    | "ol-simple" | 1
		"class" | MatchType.EQUALS                    | "ol"        | 0
		"class" | MatchType.CONTAINED_WITH_WHITESPACE | "ol-simple" | 3
		"class" | MatchType.CONTAINED_WITH_WHITESPACE | "dummy"     | 1
		"class" | MatchType.STARTING_WITH             | "ol-simple" | 2
		"class" | MatchType.STARTING_WITH             | "ol"        | 3
		"class" | MatchType.ENDING_WITH               | "imple"     | 2
		"class" | MatchType.ENDING_WITH               | "my"        | 1
		"class" | MatchType.CONTAINING                | "simple"    | 4
		"class" | MatchType.CONTAINING                | "module"    | 1
		"lang"  | MatchType.CONTAINED_WITH_HYPHENS    | "en"        | 1
		"lang"  | MatchType.CONTAINED_WITH_HYPHENS    | "US"        | 1
		"class" | MatchType.CONTAINED_WITH_HYPHENS    | "simple"    | 1
	}

	def hasClass() {
		expect:
		navigator.hasClass(className) == expectedResult

		where:
		navigator               | className | expectedResult
		onPage.find(".article") | "article" | true
		onPage.find("div")      | "module"  | true
		onPage.find("#content") | "col-3"   | true
		onPage.find("#content") | "col-2"   | false
		onPage.find("#content") | "col"     | false
	}

	def is() {
		expect:
		navigator.is(expectedTag) == expectedResult

		where:
		navigator                               | expectedTag  | expectedResult
		onPage.find("div")                      | "div"        | true
		onPage.find("#article-1 p").parent()    | "div"        | true
		onPage.find("#article-1 p").parent()    | "blockquote" | true
		onPage.find("#article-1 p").parent()[0] | "blockquote" | false
	}

	@Unroll("withAttribute '#key' with value '#value' and matcher #matcher should find #expectedSize elements")
	def withAttribute() {
		when:
		def navigator = selector ? onPage.find(selector) : onPage

		then:
		navigator.withAttribute(key, matcher, value).size() == expectedSize

		where:
		selector          | key     | matcher                             | value       | expectedSize
		null              | "lang"  | MatchType.EXISTING                  | null        | 1
		"div.article div" | "class" | MatchType.EXISTING                  | null        | 3
		null              | "lang"  | MatchType.EQUALS                    | "en-US"     | 1
		"div.article div" | "class" | MatchType.EQUALS                    | "content"   | 3
		"div.article div" | "class" | MatchType.EQUALS                    | " content"  | 0
		null              | "lang"  | MatchType.CONTAINED_WITH_WHITESPACE | "en-US"     | 1
		"ol"              | "class" | MatchType.CONTAINED_WITH_WHITESPACE | "ol-simple" | 3
		"ol"              | "class" | MatchType.CONTAINED_WITH_WHITESPACE | "ol"        | 1
		"ol"              | "class" | MatchType.STARTING_WITH             | "ol-simple" | 2
		"ol"              | "class" | MatchType.STARTING_WITH             | "ol"        | 3
		"ol"              | "class" | MatchType.ENDING_WITH               | "imple"     | 2
		"ol"              | "class" | MatchType.ENDING_WITH               | "my"        | 1
		"ol"              | "class" | MatchType.CONTAINING                | "simple"    | 4
		"div"             | "class" | MatchType.CONTAINING                | "module"    | 1
		null              | "lang"  | MatchType.CONTAINED_WITH_HYPHENS    | "en"        | 1
		null              | "lang"  | MatchType.CONTAINED_WITH_HYPHENS    | "US"        | 1
		"ol"              | "class" | MatchType.CONTAINED_WITH_HYPHENS    | "simple"    | 1
	}

	def hasAttribute() {
		when:
		def navigator = selector ? onPage.find(selector) : onPage

		then:
		navigator.hasAttribute(key, matcher, value) == expectedResult

		where:
		selector          | key     | matcher                             | value       | expectedResult
		".article"        | "class" | MatchType.EXISTING                  | null        | true
		".article"        | "bdo"   | MatchType.EXISTING                  | null        | false
		".article"        | "class" | MatchType.EQUALS                    | "article"   | true
		".article"        | "class" | MatchType.EQUALS                    | " article"  | false
		"div"             | "class" | MatchType.EQUALS                    | "col-2"     | true
		"div"             | "class" | MatchType.CONTAINED_WITH_WHITESPACE | "module"    | true
		"ol"              | "class" | MatchType.CONTAINED_WITH_WHITESPACE | "ol"        | true
		"ol"              | "class" | MatchType.CONTAINED_WITH_WHITESPACE | "ol-simple" | true
		"ol"              | "class" | MatchType.CONTAINED_WITH_WHITESPACE | "dummy"     | true
		"ol"              | "class" | MatchType.CONTAINED_WITH_WHITESPACE | "nope"      | false
		"ol"              | "class" | MatchType.STARTING_WITH             | "ol-simple" | true
		"ol"              | "class" | MatchType.STARTING_WITH             | "ol"        | true
		"div"             | "class" | MatchType.STARTING_WITH             | "ola"       | false
		"ol"              | "class" | MatchType.ENDING_WITH               | "imple"     | true
		"ol"              | "class" | MatchType.ENDING_WITH               | "my"        | true
		"ol"              | "class" | MatchType.ENDING_WITH               | "nono"      | false
		"ol"              | "class" | MatchType.CONTAINING                | "simple"    | true
		"div"             | "class" | MatchType.CONTAINING                | "module"    | true
		"div"             | "class" | MatchType.CONTAINING                | "ol-simple" | false
		null              | "lang"  | MatchType.CONTAINED_WITH_HYPHENS    | "en"        | true
		null              | "lang"  | MatchType.CONTAINED_WITH_HYPHENS    | "US"        | true
		"ol"              | "class" | MatchType.CONTAINED_WITH_HYPHENS    | "simple"    | true
		"div"             | "class" | MatchType.CONTAINED_WITH_HYPHENS    | "simple"    | false
	}

	def text() {
		expect:
		navigator.text().contains(expectedText) == expectedResult

		where:
		navigator                 | expectedText      | expectedResult
		onPage                    | "Article title 2" | true
		onPage.find("#article-2") | "Article title 2" | true
		onPage.find("#article-3") | "Article title 2" | false
	}

	def texts() {
		expect:
		navigator.texts() == expectedValues

		where:
		navigator                  | expectedValues
		onPage.find(".article h2") | ["Article title 1", "Article title 2", "Article title 3"]
	}

	def trimmedText() {
		expect:
		navigator.trimmedText() == expectedText

		where:
		navigator                      | expectedText
		onPage.find("#article-2 h2 a") | "Article title 2"
		onPage.find("ol li")           | "Item #1"
	}
	
	def trimmedTexts() {
		expect:
		navigator.trimmedTexts() == ["Article title 1", "Article title 2", "Article title 3"]

		where:
		navigator                  | expectedValues
		onPage.find(".article h2") | ["Article title 1", "Article title 2", "Article title 3"]
	}

	def attribute() {
		expect:
		navigator.attribute(key) == expectedValue

		where:
		navigator                                             | key     | expectedValue
		onPage.find("#header")                                | "id"    | "header"
		onPage.find("#article-3")                             | "class" | "article"
		onPage.find("input").withName("site").with("checked") | "value" | "google"
		onPage.find("#article-3")                             | "style" | ""
	}

	@Ignore
	def attributes() {
		expect:
		navigator.attributes(key) == expectedValues

		where:
		navigator                             | key       | expectedValues
		onPage.find("input").withName("site") | "value"   | ["google", "thisone"]
		onPage.find("input").withName("site") | "checked" | ["checked", ""]
	}

	def "get value"() {
		expect:
		navigator.value() == expectedValue

		where:
		navigator                                                     | expectedValue
		onPage.find("select", name: "plain_select")                   | "4"
		onPage.find("select", name: "multiple_select")                | ["2", "4"]
		onPage.find("select", name: "plain_select").find("option")    | "1"
		onPage.find("textarea")                                       | " The textarea content. "
		onPage.find("#keywords")                                      | "Enter keywords here"
		onPage.find("#checker1")                                      | null
		onPage.find("#checker2")                                      | "123"
		onPage.find("input", name: "site")                            | "google"
	}

	def "set value"() {
		expect:
		navigator.value() == expectedValue
		navigator.value(newValue).value() == newValue
		navigator.value(expectedValue).value() == expectedValue

		where:
		navigator                           | expectedValue             | newValue
		onPage.find("#the_plain_select")    | "4"                       | "2"
		onPage.find("#the_multiple_select") | ["2", "4"]                | ["1", "3", "5"]
		onPage.find("#keywords")            | "Enter keywords here"     | "bar"
		onPage.find("textarea")             | " The textarea content. " | "This is the new content of the textarea. Yeah!"
		onPage.find("#checker1")            | null                      | "123"
		onPage.find("#checker2")            | "123"                     | null
		onPage.find("input", name: "site")  | "google"                  | "thisone"
		// TODO: tear down?
	}

	def values() {
		expect:
		navigator.values() == expectedValues

		where:
		navigator                                             | expectedValues
		onPage.find("select").withName("multiple_select")     | ["2", "4"]
		onPage.find("input").withName("site")                 | ["google", "thisone"]
		onPage.find("select").withName("that_does_not_exist") | []
	}

	def click() {
		// TODO: need to test this
	}

	def first() {
		expect:
		navigator.first().size() == 1
		navigator.first().id() == expectedId

		where:
		navigator               | expectedId
		onPage.find(".article") | "article-1"
	}

	def firstElement() {
		expect:
		navigator.firstElement().getAttribute("id") == expectedId

		where:
		navigator               | expectedId
		onPage.find(".article") | "article-1"
	}

	def last() {
		expect:
		navigator.last().size() == 1
		navigator.last().id() == expectedId

		where:
		navigator               | expectedId
		onPage.find(".article") | "article-3"
	}

	def lastElement() {
		expect:
		navigator.lastElement().getAttribute("id") == expectedId

		where:
		navigator               | expectedId
		onPage.find(".article") | "article-3"
	}

	def verifyNotEmpty() {
		expect:
		navigator.verifyNotEmpty()

		where:
		navigator << [onPage.find("#container"), onPage.find("#container").find("div")]
	}

	def "verifyNotEmtpy on empty Navigator"() {
		when: navigator.verifyNotEmpty()
		then: thrown(EmptyNavigatorException)

		where:
		navigator = onPage.find("#does_not_exist")
	}

	def withTextContaining() {
		expect:
		navigator.withTextContaining(text).size() == 1
		navigator.withTextContaining(text).attribute(attribute) == expectedValue

		where:
		navigator        | text    | attribute | expectedValue
		onPage.find("a") | "Home"  | "href"    | "#home"
		onPage.find("a") | "About" | "href"    | "#about"
	}

	def withTextMatching() {
		expect:
		navigator.withTextMatching(pattern).size() == expectedSize

		where:
		navigator        | pattern      | expectedSize
		onPage.find("p") | /.*block.*/  | 1
		onPage.find("p") | ~/.*block.*/ | 1
		onPage.find("p") | /.*Nono.*/   | 0
		onPage.find("p") | ~/.*Nono.*/  | 0
	}

	def withAttributeMatching() {
		expect:
		navigator.withAttributeMatching(attribute, pattern).size() == expectedSize

		where:
		navigator            | attribute | pattern        | expectedSize
		onPage.find("div")   | "class"   | /.*col\-\d.*/  | 4
		onPage.find("div")   | "class"   | ~/.*col\-\d.*/ | 4
		onPage.find("input") | "value"   | /.*nono.*/     | 0
		onPage.find("input") | "value"   | ~/.*nono.*/    | 0
	}

	def getByAttributeMatching() {
		expect:
		onPage.findByAttributeMatching(attribute, pattern).size() == expectedSize

		where:
		attribute | pattern        | expectedSize
		"class"   | /.*col\-\d.*/  | 4
		"class"   | ~/.*col\-\d.*/ | 4
		"class"   | /.*nono.*/     | 0
		"class"   | ~/.*nono.*/    | 0
	}

}
