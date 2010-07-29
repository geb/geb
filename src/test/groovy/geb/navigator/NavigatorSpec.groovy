package geb.navigator

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll
import spock.lang.Ignore
import org.openqa.selenium.By

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

	def cleanup() {
		driver.findElement(By.name("keywords")).clear()
		driver.findElement(By.name("keywords")).sendKeys("Enter keywords here")

		driver.findElements(By.name("site")).find { it.value == "google"}.setSelected()

		if (driver.findElement(By.name("checker1")).isSelected()) driver.findElement(By.name("checker1")).toggle()
		driver.findElement(By.name("checker2")).setSelected()

		driver.findElement(By.name("plain_select")).findElements(By.tagName("option"))[3].setSelected()

		def multiSelectOptions = driver.findElement(By.name("multiple_select")).findElements(By.tagName("option"))
		if (multiSelectOptions[0].isSelected()) multiSelectOptions[0].toggle()
		multiSelectOptions[1].setSelected()
		if (multiSelectOptions[2].isSelected()) multiSelectOptions[2].toggle()
		multiSelectOptions[3].setSelected()
		if (multiSelectOptions[4].isSelected()) multiSelectOptions[4].toggle()
		
		driver.findElement(By.name("textext")).clear()
		driver.findElement(By.name("textext")).sendKeys(" The textarea content. ")
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

	@Unroll("find('#selector') should return elements with #property of '#expectedValue'")
	def "find by CSS selector"() {
		given: def navigator = page.find(selector)
		expect: navigator*."$property" == expectedValue

		where:
		selector                  | property | expectedValue
		"#content div li"         | "text"   | ["Item #1", "Item #2", "Item #3", "Item #4"] * 5
		"div.article h2 a"        | "text"   | ["Article title 1", "Article title 2", "Article title 3"]
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
		"#keywords" | "input"  | ["site-1"]
		"#keywords" | "select" | ["the_plain_select"]
		"input"     | "input"  | ["site-1", "site-2", "checker1", "checker2"]
		"input"     | "select" | ["the_plain_select"]
		"#keywords" | "bdo"    | []
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
		"#keywords" | "div"    | ["sidebar"]
		"input"     | "div"    | ["sidebar"]
		"ul, ol"    | "div"    | ["navigation", "sidebar"]
		"#keywords" | "bdo"    | []
		"#article-1" | ".col-3"       | ["content"]
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

	@Unroll("the .text property of #selector should be '#expectedText'")
	def "text of the first element can be accessed as a property"() {
		given: def navigator = page.find(selector)
		expect: navigator.text == expectedText
		
		where:
		selector | expectedText
		"p"      | "First paragraph of article 1."
		"hr"     | ""
		"bdo"    | null
	}

	@Unroll("the .tag property of #selector should be '#expectedTag'")
	def "tagName of the first element can be accessed as a property"() {
		given: def navigator = page.find(selector)
		expect: navigator.tag == expectedTag
		
		where:
		selector        | expectedTag
		"p"             | "p"
		".article"      | "div"
		"input, select" | "input"
		"bdo"           | null
	}

	@Ignore
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

	@Unroll("findByAttribute '#key' with value '#value' and matcher #matcher should find #expectedSize elements")
	def findByAttribute() {
		expect:
		page.findByAttribute(key, matcher, value).size() == expectedSize

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
		navigator             | className | expectedResult
		page.find(".article") | "article" | true
		page.find("div")      | "module"  | true
		page.find("#content") | "col-3"   | true
		page.find("#content") | "col-2"   | false
		page.find("#content") | "col"     | false
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

	@Unroll("withAttribute '#key' with value '#value' and matcher #matcher should find #expectedSize elements")
	def withAttribute() {
		when:
		def navigator = selector ? page.find(selector) : page

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
		def navigator = selector ? page.find(selector) : page

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
		navigator.text.contains(expectedText) == expectedResult

		where:
		navigator               | expectedText      | expectedResult
		page                    | "Article title 2" | true
		page.find("#article-2") | "Article title 2" | true
		page.find("#article-3") | "Article title 2" | false
	}

	def texts() {
		expect:
		navigator.texts() == expectedValues

		where:
		navigator                | expectedValues
		page.find(".article h2") | ["Article title 1", "Article title 2", "Article title 3"]
	}

	def trimmedText() {
		expect:
		navigator.trimmedText() == expectedText

		where:
		navigator                    | expectedText
		page.find("#article-2 h2 a") | "Article title 2"
		page.find("ol li")           | "Item #1"
	}

	def trimmedTexts() {
		expect:
		navigator.trimmedTexts() == ["Article title 1", "Article title 2", "Article title 3"]

		where:
		navigator                | expectedValues
		page.find(".article h2") | ["Article title 1", "Article title 2", "Article title 3"]
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
		navigator.attributes(key) == expectedValues

		where:
		navigator                           | key       | expectedValues
		page.find("input").withName("site") | "value"   | ["google", "thisone"]
		page.find("input").withName("site") | "checked" | ["checked", ""]
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
		given: def form = page.find("form")
		when: form."$fieldName" = newValue
		then: form."$fieldName" == newValue

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

		where:
		value << ["google", "thisone"]
	}

	@Unroll("input value should be '#expectedValue'")
	def "get value"() {
		expect:
		navigator.value() == expectedValue

		where:
		navigator                                                | expectedValue
		page.find("select", name: "plain_select")                | "4"
		page.find("select", name: "multiple_select")             | ["2", "4"]
		page.find("select", name: "plain_select").find("option") | ["1", "2", "3", "4", "5"]
		page.find("textarea")                                    | " The textarea content. "
		page.find("#keywords")                                   | "Enter keywords here"
		page.find("#checker1")                                   | null
		page.find("#checker2")                                   | "123"
		page.find("input", name: "site")                         | "google"
	}

	@Unroll("input value can be changed from '#expectedValue' to '#newValue'")
	def "set value"() {
		expect:
		navigator.value() == expectedValue
		navigator.value(newValue).value() == newValue
		navigator.value(expectedValue).value() == expectedValue

		where:
		navigator                         | expectedValue             | newValue
		page.find("#the_plain_select")    | "4"                       | "2"
		page.find("#the_multiple_select") | ["2", "4"]                | ["1", "3", "5"]
		page.find("#keywords")            | "Enter keywords here"     | "bar"
		page.find("textarea")             | " The textarea content. " | "This is the new content of the textarea. Yeah!"
		page.find("#checker1")            | null                      | "123"
		page.find("#checker2")            | "123"                     | null
		page.find("input", name: "site")  | "google"                  | "thisone"
	}

	@Unroll
	def values() {
		expect:
		navigator.values() == expectedValues

		where:
		navigator                                           | expectedValues
		page.find("select").withName("multiple_select")     | ["2", "4"]
		page.find("input").withName("site")                 | ["google", "thisone"]
		page.find("select").withName("that_does_not_exist") | []
	}

	def click() {
		// TODO: need to test this
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

	def withTextContaining() {
		expect:
		navigator.withTextContaining(text).size() == 1
		navigator.withTextContaining(text).@"$attribute" == expectedValue

		where:
		navigator      | text    | attribute | expectedValue
		page.find("a") | "Home"  | "href"    | "#home"
		page.find("a") | "About" | "href"    | "#about"
	}

	def withTextMatching() {
		expect:
		navigator.withTextMatching(pattern).size() == expectedSize

		where:
		navigator      | pattern      | expectedSize
		page.find("p") | /.*block.*/  | 1
		page.find("p") | ~/.*block.*/ | 1
		page.find("p") | /.*Nono.*/   | 0
		page.find("p") | ~/.*Nono.*/  | 0
	}

	def withAttributeMatching() {
		expect:
		navigator.withAttributeMatching(attribute, pattern).size() == expectedSize

		where:
		navigator          | attribute | pattern        | expectedSize
		page.find("div")   | "class"   | /.*col\-\d.*/  | 4
		page.find("div")   | "class"   | ~/.*col\-\d.*/ | 4
		page.find("input") | "value"   | /.*nono.*/     | 0
		page.find("input") | "value"   | ~/.*nono.*/    | 0
	}

	def getByAttributeMatching() {
		expect:
		page.findByAttributeMatching(attribute, pattern).size() == expectedSize

		where:
		attribute | pattern        | expectedSize
		"class"   | /.*col\-\d.*/  | 4
		"class"   | ~/.*col\-\d.*/ | 4
		"class"   | /.*nono.*/     | 0
		"class"   | ~/.*nono.*/    | 0
	}

}
