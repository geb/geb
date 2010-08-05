package geb.navigator

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Ignore
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import org.openqa.selenium.WebElement

class NavigatorSpec extends Specification {

	@Shared WebDriver driver
	@Shared Navigator page

	def setupSpec() {
		driver = new HtmlUnitDriver()
		driver.get(getClass().getResource("/test.html") as String)
		page = Navigator.on(driver)
	}

	def cleanupSpec() {
		driver.close()
	}

	def "getElement by index"() {
		expect:
		page.find("div").getElement(1).getAttribute("id") == "header"
		page.find("bdo").getElement(0) == null
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
		navigator                   | index | iterations | expectedSize
		page.find("li")             | 5     | 1          | 22
		page.find("li")             | 0     | 1          | 22
		page.find("li")             | -1    | 1          | 22
		page.find("li")             | 1     | 1          | 22
		page.find("li")             | 23    | 1          | 23
		page.find("li")             | 0     | 2          | 21
		page.find("li").find("bdo") | 0     | 1          | 0
	}

	@Unroll("find('#selector') should return elements with #property of '#expected'")
	def "find by CSS selector"() {
		given: def navigator = page.find(selector)
		expect: navigator*."$property" == expected

		where:
		selector                  | property | expected
		"#sidebar form input"     | "@id"    | ["keywords", "site-1", "site-2", "checker1", "checker2"]
		"div#sidebar form input"  | "@id"    | ["keywords", "site-1", "site-2", "checker1", "checker2"]
		".col-1 form input"       | "@id"    | ["keywords", "site-1", "site-2", "checker1", "checker2"]
		"div.col-1 form input"    | "@id"    | ["keywords", "site-1", "site-2", "checker1", "checker2"]
		"div#sidebar.col-1 input" | "@id"    | ["keywords", "site-1", "site-2", "checker1", "checker2"]
		"#header"                 | "@id"    | ["header"]
		".col-3.module"           | "@id"    | ["navigation"]
		".module.col-3"           | "@id"    | ["navigation"]
		"#THIS_ID_DOES_NOT_EXIST" | "@id"    | []
	}

	@Unroll("find('#selector1').find('#selector2') should find #expectedSize elements")
	def "find by id in element context"() {
		expect:
		page.find(selector1).find(selector2).size() == expectedSize

		where:
		selector1    | selector2 | expectedSize
		"#container" | "#header" | 1
		"#footer"    | "#header" | 0
	}

	@Unroll("find('#selector') should find #expectedSize elements")
	def "find with grouped selectors"() {
		expect:
		page.find(selector).size() == expectedSize

		where:
		selector                                | expectedSize
		"#header  , #sidebar, #footer"          | 3
		"div ol  , #sidebar   , blockquote,bdo" | 5 + 1 + 1 + 0
	}

	@Unroll("find(#attributes) should find elements with the ids #expectedIds")
	def "find by attributes"() {
		expect: page.find(attributes)*.@id == expectedIds

		where:
		attributes                      | expectedIds
		[name: "keywords"]              | ["keywords"]
		[name: ~/checker\d/]            | ["checker1", "checker2"]
		[name: "site", value: "google"] | ["site-1"]
		[name: "DOES-NOT-EXIST"]        | []
	}

	@Unroll("find(text: '#text') should find #expectedSize elements")
	def "find by text"() {
		expect: page.find(text: text).size() == expectedSize

		where:
		text                            | expectedSize
		"First paragraph of article 2." | 1
		~/.*article 1\./                | 2
		"DOES NOT EXIST"                | 0
	}

	@Unroll("find('#selector', #index) should find the element with the id '#expectedId'")
	def "find by selector and index"() {
		when: def navigator = page.find(selector, index)
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
		expect: page.find(attributes, selector)*.@id == expectedIds

		where:
		selector   | attributes                              | expectedIds
		"input"    | [type: "checkbox"]                      | ["checker1", "checker2"]
		"input"    | [name: "site"]                          | ["site-1", "site-2"]
		"input"    | [name: "site", value: "google"]         | ["site-1"]
		"input"    | [name: ~/checker\d/]                    | ["checker1", "checker2"]
		"bdo"      | [name: "whatever"]                      | []
		".article" | [:]                                     | ["article-1", "article-2", "article-3"]
	}

	@Unroll("find('#selector', text: '#text') should find #expectedSize elements")
	def "find by selector and text predicate"() {
		expect: page.find(selector, text: text).size() == expectedSize

		where:
		selector   | text                            | expectedSize
		"p"        | "First paragraph of article 2." | 1
		"p"        | ~/.*article 1\./                | 2
		"p"        | "DOES NOT EXIST"                | 0
	}

	@Unroll("filter('#filter') should select elements with the ids #expectedIds")
	def "filter by selector"() {
		expect: navigator.filter(filter)*.@id == expectedIds

		where:
		navigator             | filter        | expectedIds
		page.find(".article") | "#article-2"  | ["article-2"]
		page.find(".article") | "#no-such-id" | []
		page.find("div")      | ".article"    | ["article-1", "article-2", "article-3"]
		// TODO: case for filter by tag
	}

	@Unroll("filter(#filter) should select elements with the ids #expectedIds")
	def "filter by attributes"() {
		expect: navigator.filter(filter)*.@id == expectedIds

		where:
		navigator               | filter                          | expectedIds
		page.find("input")      | [type: "checkbox"]              | ["checker1", "checker2"]
		page.find("input")      | [name: "site"]                  | ["site-1", "site-2"]
		page.find("input")      | [name: "site", value: "google"] | ["site-1"]
		page.find(".article")   | [id: ~/article-[1-2]/]          | ["article-1", "article-2"]
		page.find("#article-1") | [id: "article-2"]               | []
	}

	@Unroll("filter(text: '#text') should select #expectedSize elements")
	def "filter by text"() {
		expect: navigator.filter(text: text).size() == expectedSize

		where:
		navigator      | text                            | expectedSize
		page.find("p") | "First paragraph of article 2." | 1
		page.find("p") | ~/.*article 1\./                | 2
		page.find("p") | "DOES NOT EXIST"                | 0
	}

	@Unroll("filter('#selector', #attributes should select elements with the ids #expectedIds")
	def "filter by selector and attributes"() {
		expect: navigator.filter(attributes, selector)*.@id == expectedIds

		where:
		navigator                     | selector | attributes         | expectedIds
		page.find("a, input, select") | "input"  | [type: "checkbox"] | ["checker1", "checker2"]
		page.find("a, input, select") | "select" | [type: "checkbox"] | []
	}

	@Unroll("calling next() on #selector should return #expectedIds")
	def "next selects immediately following elements"() {
		given: def navigator = page.find(selector)
		expect: navigator.next()*.@id == expectedIds

		where:
		selector        | expectedIds
		"#main"         | ["sidebar"]
		"#header"       | ["navigation"]
		".col-3"        | ["content", "footer"]
		"#container"    | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling next(#nextSelector) on #selector should return #expectedIds")
	def "next with selector argument"() {
		given: def navigator = page.find(selector)
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

	@Unroll("calling previous() on #selector should return #expectedIds")
	def "previous selects immediately preceding elements"() {
		given: def navigator = page.find(selector)
		expect: navigator.previous()*.@id == expectedIds

		where:
		selector        | expectedIds
		"#footer"       | ["content"]
		"#navigation"   | ["header"]
		".col-3"        | ["header", "navigation"]
		"#container"    | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling previous(#previousSelector) on #selector should return #expectedIds")
	def "previous with tag argument"() {
		given: def navigator = page.find(selector)
		expect: navigator.previous(previousSelector)*.@id == expectedIds

		where:
		selector               | previousSelector | expectedIds
		"#the_multiple_select" | "input"  | ["checker2"]
		"#the_multiple_select" | "select" | ["the_plain_select"]
		"input"                | "input"  | ["keywords", "site-1", "site-2", "checker1"]
		"select"               | "select" | ["the_plain_select"]
		"#the_multiple_select" | "bdo"    | []
		"#article-3"           | ".article"       | ["article-2"]
	}

	@Unroll("calling parent() on #selector should return #expectedIds")
	def "parent selects immediate parent of each element"() {
		given: def navigator = page.find(selector)
		expect: navigator.parent()*.@id == expectedIds

		where:
		selector        | expectedIds
		"h1"            | ["header"]
		".article"      | ["main"]
		"option"        | ["the_plain_select", "the_multiple_select"]
		"html"          | []
		"#DOESNOTEXIST" | []
	}

	@Unroll("calling parent(#parentSelector) on #selector should return #expectedIds")
	def "parent with tag argument"() {
		given: def navigator = page.find(selector)
		expect: navigator.parent(parentSelector)*.@id == expectedIds

		where:
		selector     | parentSelector | expectedIds
		"#keywords"  | "div"          | ["sidebar"]
		"input"      | "div"          | ["sidebar"]
		"ul, ol"     | "div"          | ["navigation", "sidebar"]
		"#keywords"  | "bdo"          | []
		"#article-1" | ".col-3"       | ["content"]
	}

	@Unroll("calling children() on #selector should return #expected")
	def "children selects immediate child elements of each element"() {
		given: def navigator = page.find(selector)
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
	def "children with tag argument"() {
		given: def navigator = page.find(selector)
		expect: navigator.children(childSelector)*.tag() == expected

		where:
		selector     | childSelector | expected
		"#header"    | "h1"          | ["h1"]
		"#header"    | "div"         | []
		".article"   | ".content"    | ["div"] * 3
	}

	@Unroll("calling siblings() on #selector should return #expectedIds")
	def "siblings selects immediately following elements"() {
		given: def navigator = page.find(selector)
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
		given: def navigator = page.find(selector)
		expect: navigator.siblings(siblingSelector)*.@id == expectedIds

		where:
		selector            | siblingSelector | expectedIds
		"#site-1"           | "input"         | ["keywords", "site-2", "checker1", "checker2"]
		"#site-1"           | "select"        | ["the_plain_select", "the_multiple_select"]
		"#the_plain_select" | "select"        | ["the_multiple_select"]
		"#header"           | ".col-3"        | ["navigation", "content"]
		"#header"           | ".col-2"        | []
	}

	def unique() {
		when: def navigator = (navigator1 + navigator2).unique()

		then: navigator.size() == expectedSize

		where:
		navigator1              | navigator2            | expectedSize
		page.find("#article-1") | page.find(".article") | 3
	}

	def "unique is applied by default"() {
		when:
		def navigator = page.find("div").find("ol")

		then:
		navigator.size() == expectedSize

		where:
		expectedSize = 5
	}

	@Unroll("the value of text() on #selector should be '#expectedText'")
	def "text of the first element can be accessed as a property"() {
		given: def navigator = page.find(selector)
		expect: navigator.text() == expectedText

		where:
		selector | expectedText
		"p"      | "First paragraph of article 1."
		"hr"     | ""
		"bdo"    | null
	}

	@Unroll("the value of tag() on #selector should be '#expectedTag'")
	def "tagName of the first element can be accessed as a property"() {
		given: def navigator = page.find(selector)
		expect: navigator.tag() == expectedTag

		where:
		selector        | expectedTag
		"p"             | "p"
		".article"      | "div"
		"input, select" | "input"
		"bdo"           | null
	}

	@Unroll("navigator.@#attribute should return '#expectedValue'")
	def "attribute access via field operator"() {
		expect: navigator.@"$attribute" == expectedValue

		where:
		navigator                   | attribute | expectedValue
		page.find("div", 0)         | "id"      | "container"
		page.find("div div", 1)     | "id"      | "navigation"
		page.find("#article-1 div") | "id"      | ""
		page.find("#navigation a")  | "href"    | "#home"
		page.find("bdo")            | "id"      | null
	}

	@Unroll("navigator*.@#attribute should return #expectedValue")
	def "attributes of all elements accessed via field operator"() {
		expect: navigator*.@"$attribute" == expectedIds

		where:
		navigator                  | attribute | expectedIds
		page.find("div")[0..<5]    | "id"      | ["container", "header", "navigation", "content", "main"]
		page.find("#navigation a") | "href"    | ["#home", "#about", "#contact"]
		page.find("bdo")           | "id"      | []
	}

	@Unroll("the class names on #selector are #expected")
	def "getClassNames returns the classes of the first matched element"() {
		given: def navigator = page.find(selector)
		expect: navigator.classes() == expected

		where:
		selector      | expected
		"#article-1"  | ["article"] as Set
		"#navigation" | ["col-3", "module"] as Set
		"ol"          | [] as Set
		"bdo"         | [] as Set
	}

	@Unroll("the result of findClass('#className') on #selector should be #expectedResult")
	def hasClass() {
		given: def navigator = page.find(selector)
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
		navigator                             | expectedTag  | expectedResult
		page.find("div")                      | "div"        | true
		page.find("#article-1 p").parent()    | "div"        | true
		page.find("#article-1 p").parent()    | "blockquote" | true
		page.find("#article-1 p").parent()[0] | "blockquote" | false
	}

	def text() {
		expect:
		navigator.text().contains(expectedText) == expectedResult

		where:
		navigator               | expectedText      | expectedResult
		page                    | "Article title 2" | true
		page.find("#article-2") | "Article title 2" | true
		page.find("#article-3") | "Article title 2" | false
	}

	@Unroll("the value of .@#attribute on #selector should be '#expectedValue'")
	def "attribute access"() {
		given: def navigator = page.find(selector)
		expect: navigator."@$attribute" == expectedValue

		where:
		selector     | attribute | expectedValue
		"#header"    | "id"      | "header"
		"#article-3" | "class"   | "article"
		"#site-1"    | "value"   | "google"
		"#article-3" | "style"   | ""
	}

	@Ignore
	def attributes() {
		expect:
		navigator*.@"$key" == expectedValues

		where:
		navigator                           | key       | expectedValues
		page.find("input").withName("site") | "value"   | ["google", "thisone"]
		page.find("input").withName("site") | "checked" | ["checked", ""]
	}

	@Unroll("the dynamic method #fieldName() should return elements with the ids #expected")
	def "can find named inputs using a dynamic method call"() {
		when: def navigator = context."$fieldName"()
		then: navigator*.@id == expected

		where:
		context            | fieldName     | expected
		page               | "keywords"    | ["keywords"]
		page.find("form")  | "keywords"    | ["keywords"]
		page               | "site"        | ["site-1", "site-2"]
		page.find("#main") | "keywords"    | []
		page               | "nosuchfield" | []
		page.find("bdo")   | "keywords"    | []
	}

	def "dynamic methods for finding fields do not accept arguments"() {
		when: context."$fieldName"(*arguments)
		then: thrown(MissingMethodException)

		where:
		context          | fieldName  | arguments
		page             | "keywords" | ["foo", "bar"]
		page.find("bdo") | "keywords" | ["foo"]
	}

	@Unroll("the value of '#fieldName' retrieved via property access should be '#expectedValue'")
	def "form field values can be retrieved using property access"() {
		expect: page.find("form")."$fieldName" == expectedValue

		where:
		fieldName         | expectedValue
		"keywords"        | "Enter keywords here"
		"checker1"        | null
		"checker2"        | "123"
		"textext"         | " The textarea content. "
		"plain_select"    | "4"
		"multiple_select" | ["2", "4"]
	}

	@Unroll("when a radio button with the value '#expectedValue' is selected getting then value of the group returns '#expectedValue'")
	def "get property access works on radio button groups"() {
		given:
		def radios = driver.findElements(By.name("site"))

		when:
		radios.find { it.value == expectedValue }.setSelected()

		then:
		page.find("form").site == expectedValue

		where:
		index | expectedValue
		0     | "google"
		1     | "thisone"
	}

	def "form field property access works on any node in the Navigator"() {
		when:
		def navigator = page.find(".article, form")

		then:
		navigator.keywords == "Enter keywords here"
	}

	def "invalid form field names raise the correct exception type"() {
		when:
		page.find("form").someFieldThatDoesNotExist

		then:
		thrown(MissingPropertyException)
	}

	@Unroll("setting the value of '#fieldName' to '#newValue' using property access sets the value of the input element")
	def "form field values can be set using property access"() {
		given:
		def form = page.find("form")
		def initialValue = form."$fieldName"

		when: form."$fieldName" = newValue
		then: form."$fieldName" == newValue
		cleanup: form."$fieldName" = initialValue

		where:
		fieldName         | newValue
		"keywords"        | "Lorem ipsum dolor sit amet"
		"site"            | "thisone"
		"checker1"        | "123"
		"checker2"        | null
		"textext"         | "Lorem ipsum dolor sit amet"
		"plain_select"    | "3"
		"multiple_select" | ["1", "3", "5"]
	}

	@Unroll("when the radio button group's value is set to '#value' then the corresponding radio button is selected")
	def "set property access works on radio button groups"() {
		when:
		page.find("form").site = value

		then:
		driver.findElements(By.name("site")).find { it.value == value }.isSelected()

		cleanup:
		driver.findElement(By.id("site-1")).setSelected()

		where:
		value << ["google", "thisone"]
	}

	@Unroll("input value should be '#expected'")
	def "value() returns value of first element"() {
		expect: navigator.value() == expected

		where:
		navigator                             | expected
		page.find("#the_plain_select")        | "4"
		page.find("#the_multiple_select")     | ["2", "4"]
		page.find("#the_plain_select option") | "1"
		page.find("textarea")                 | " The textarea content. "
		page.find("#keywords")                | "Enter keywords here"
		page.find("#checker1")                | null
		page.find("#checker2")                | "123"
		page.find("#keywords, textarea")      | "Enter keywords here"
	}

	@Unroll("input values should be '#expected'")
	def "get value on all elements"() {
		expect: navigator*.value() == expected

		where:
		navigator                             | expected
		page.find("select")                   | ["4", ["2", "4"]]
		page.find("#the_plain_select option") | ["1", "2", "3", "4", "5"]
		page.find("#keywords, textarea")      | ["Enter keywords here", " The textarea content. "]
	}

	@Unroll("input value can be changed to '#newValue'")
	def "set value"() {
		given: def initialValue = navigator.value()
		when: navigator.value(newValue)
		then: navigator.value() == newValue
		cleanup: navigator.value(initialValue)

		where:
		navigator                         | newValue
		page.find("#the_plain_select")    | "2"
		page.find("#the_multiple_select") | ["1", "3", "5"]
		page.find("#keywords")            | "bar"
		page.find("textarea")             | "This is the new content of the textarea. Yeah!"
		page.find("#checker1")            | "123"
		page.find("#checker2")            | null
	}

	@Ignore
	@Unroll("value() on '#selector' should return '#expected'")
	def "get value handles radio buttons as groups"() {
		given:
		def navigator = page.find(selector)

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
		given: def navigator = page.find(selector)
		when: navigator.value(newValue)
		then: navigator.value() == newValue
		cleanup: driver.findElement(By.id("site-1")).setSelected()

		where:
		selector           | newValue
		"#site-1"          | "google"
		"#site-2"          | "thisone"
		"#site-1, #site-2" | "thisone"
	}

	@Unroll("find('#selector') << '#keystrokes' should append '#keystrokes' to the input's value")
	def "can use leftShift to enter text in inputs"() {
		given:
		def navigator = page.find(selector)
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
		def navigator = page.find(selector)
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
		navigator             | expectedId
		page.find(".article") | "article-1"
	}

	def firstElement() {
		expect:
		navigator.firstElement().getAttribute("id") == expectedId

		where:
		navigator             | expectedId
		page.find(".article") | "article-1"
	}

	def last() {
		expect:
		navigator.last().size() == 1
		navigator.last().@id == expectedId

		where:
		navigator             | expectedId
		page.find(".article") | "article-3"
	}

	def lastElement() {
		expect:
		navigator.lastElement().getAttribute("id") == expectedId

		where:
		navigator             | expectedId
		page.find(".article") | "article-3"
	}

	def verifyNotEmpty() {
		expect:
		navigator.verifyNotEmpty()

		where:
		navigator << [page.find("#container"), page.find("#container").find("div")]
	}

	def "verifyNotEmtpy on empty Navigator"() {
		when: navigator.verifyNotEmpty()
		then: thrown(EmptyNavigatorException)

		where:
		navigator = page.find("#does_not_exist")
	}

	def click() {
		given:
		def element1 = Mock(WebElement)
		def element2 = Mock(WebElement)
		def navigator = new NonEmptyNavigator(element1, element2)

		when: navigator.click()

		then:
		1 * element1.click()
		1 * element2.click()
		0 * _
	}

}
