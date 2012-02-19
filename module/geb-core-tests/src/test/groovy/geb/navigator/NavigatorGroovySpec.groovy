package geb.navigator

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Shared
import spock.lang.Specification
import geb.Browser

class NavigatorGroovySpec extends Specification {

	@Shared Browser browser
	@Shared geb.navigator.Navigator onPage

	def setupSpec() {
		browser = new Browser()
		browser.go(getClass().getResource("/test.html") as String)
		onPage = Navigator.on(browser)
	}

	def "can use any(Closure) on Navigator"() {
		expect: navigator.any { it.hasClass("article") } == expectedResult
		where:
		navigator               | expectedResult
		onPage.find("div")      | true  // some match
		onPage.find(".article") | true  // all match
		onPage.find("ol")       | false // none match
		onPage.find("bdo")      | false // empty
	}

	def "can use collect(Closure) on Navigator"() {
		when: def list = navigator.collect { it.@id }
		then: list == expectedList
		where:
		navigator                 | expectedList
		onPage.find(".article")   | ["article-1", "article-2", "article-3"]
		onPage.find("#article-1") | ["article-1"]
		onPage.find("bdo")        | []
	}

	def "can use each(Closure) on Navigator"() {
		given: def list = []
		when: navigator = navigator.each { list << it.@id }
		then: list == expectedList
		where:
		navigator                 | expectedList
		onPage.find(".article")   | ["article-1", "article-2", "article-3"]
		onPage.find("#article-1") | ["article-1"]
		onPage.find("bdo")        | []
	}

	def "can use eachWithIndex(Closure) on Navigator"() {
		given: def map = [:]
		when: navigator = navigator.eachWithIndex { e, i -> map[e.@id] = i }
		then: map == expectedMap
		where:
		navigator                 | expectedMap
		onPage.find(".article")   | ["article-1": 0, "article-2": 1, "article-3": 2]
		onPage.find("#article-1") | ["article-1": 0]
		onPage.find("bdo")        | [:]
	}

	def "can use every(Closure) on Navigator"() {
		expect: navigator.every { it.hasClass("article") } == expectedResult
		where:
		navigator               | expectedResult
		onPage.find("div")      | false // some match
		onPage.find(".article") | true  // all match
		onPage.find("ol")       | false // none match
		onPage.find("bdo")      | true  // empty ([].every { any condition } is always true)
	}

	def "can use find(Closure) on Navigator"() {
		when: def result = navigator.find { it.hasClass("article") }
		then: (result != null) == expectedResult
		where:
		navigator               | expectedResult
		onPage.find("div")      | true
		onPage.find(".article") | true
		onPage.find("ol")       | false
		onPage.find("bdo")      | false
	}

	def "can use findAll(Closure) on Navigator"() {
		when: def result = navigator.findAll { it.hasClass("article") }
		then: result.size() == expectedSize
		and: result instanceof Navigator // findAll should return a Navigator not a Collection<Navigator>
		where:
		navigator               | expectedSize
		onPage.find("div")      | 3
		onPage.find(".article") | 3
		onPage.find("ol")       | 0
		onPage.find("bdo")      | 0
	}

	def "can use findIndexOf(Closure) on Navigator"() {
		expect: navigator.findIndexOf { it.hasClass("article") } == expectedIndex
		where:
		navigator               | expectedIndex
		onPage.find("div")      | 5
		onPage.find(".article") | 0
		onPage.find("ol")       | -1
		onPage.find("bdo")      | -1
	}

	def "can use findIndexOf(int, Closure) on Navigator"() {
		expect: navigator.findIndexOf(startIndex) { it.hasClass("article") } == expectedIndex
		where:
		navigator               | startIndex | expectedIndex
		onPage.find("div")      | 5          | 5
		onPage.find("div")      | 1          | 5
		onPage.find(".article") | 1          | 1
		onPage.find(".article") | 2          | 2
		onPage.find(".article") | 3          | -1
		onPage.find("ol")       | 1          | -1
		onPage.find("bdo")      | 1          | -1
	}

	def "can use findLastIndexOf(Closure) on Navigator"() {
		expect: navigator.findLastIndexOf { it.hasClass("article") } == expectedIndex
		where:
		navigator                     | expectedIndex
		onPage.find("div")      | 9
		onPage.find(".article") | 2
		onPage.find("ol")       | -1
		onPage.find("bdo")      | -1
	}

	def "can use findLastIndexOf(int, Closure) on Navigator"() {
		expect: navigator.findLastIndexOf(startIndex) { it.hasClass("article") } == expectedIndex
		where:
		navigator                     | startIndex | expectedIndex
		onPage.find("div")      | 5          | 9
		onPage.find("div")      | 1          | 9
		onPage.find(".article") | 1          | 2
		onPage.find(".article") | 2          | 2
		onPage.find(".article") | 3          | -1
		onPage.find("ol")       | 1          | -1
		onPage.find("bdo")      | 1          | -1
	}

	def "can use inject(Object, Closure) on Navigator"() {
		expect: navigator.inject(0) { i, e -> ++i } == expectedResult
		where:
		navigator               | expectedResult
		onPage.find("div")      | 13
		onPage.find(".article") | 3
		onPage.find("bdo")      | 0
	}

	def "can use getAt(int) on Navigator"() {
		expect: navigator[index].@id == expectedId
		where:
		navigator               | index | expectedId
		onPage.find("div")      | 0     | "container"
		onPage.find("div")      | 1     | "header"
		onPage.find("div")      | -1    | "footer"
		onPage.find(".article") | 0     | "article-1"
		onPage.find(".article") | 1     | "article-2"
		onPage.find(".article") | -1    | "article-3"
		onPage.find("bdo")      | 0     | null
	}

	def "can use getAt(Range) on Navigator"() {
		expect: navigator[range]*.@id == expectedIds
		where:
		navigator               | range  | expectedIds
		onPage.find("div")      | 0..1   | ["container", "header"]
		onPage.find("div")      | 0..<2  | ["container", "header"]
		onPage.find("div")      | 0..<0  | []
		onPage.find("div")      | 12..-1 | ["footer"]
	}

	def "can use getAt(Collection) on Navigator"() {
		expect: navigator[indexes]*.@id == expectedIds
		where:
		navigator               | indexes    | expectedIds
		onPage.find("div")      | [0, 2, 4]  | ["container", "navigation", "main"]
		onPage.find("div")      | [0, -1, 4] | ["container", "footer", "main"]
	}

	def "can use head() on Navigator"() {
		expect:
		navigator.head().size() == expectedSize
		navigator.head().@id == expectedId
		where:
		navigator               | expectedSize | expectedId
		onPage.find("div")      | 1            | "container"
		onPage.find(".article") | 1            | "article-1"
		onPage.find("bdo")      | 0            | null
	}

	def "can use tail() on Navigator"() {
		expect:
		navigator.tail().size() == expectedSize
		navigator.tail()*.@id == expectedIds
		where:
		navigator               | expectedSize | expectedIds
		onPage.find(".article") | 2            | ["article-2", "article-3"]
		onPage.find("bdo")      | 0            | []
	}
	
	def "can use plus(Navigator) on Navigator"() {
		when: def navigator = navigator1 + navigator2
		then: navigator.size() == expectedSize
		and: navigator*.@id == expectedIds
		where:
		navigator1                | navigator2                | expectedSize | expectedIds
		onPage.find("#article-1") | onPage.find("#article-2") | 2            | ["article-1", "article-2"]
		onPage.find(".article")   | onPage.find("#container") | 4            | ["article-1", "article-2", "article-3", "container"]
		onPage.find(".article")   | onPage.find("#article-1") | 3            | ["article-1", "article-2", "article-3"]
		onPage.find("bdo")        | onPage.find("#article-1") | 1            | ["article-1"]
		onPage.find("#article-1") | onPage.find("bdo")        | 1            | ["article-1"]
		onPage.find("bdo")        | onPage.find("bdo")        | 0            | []
	}

	def "can use Groovy Truth on Navigator"() {
		expect: navigator.asBoolean() == expectedResult
		where:
		navigator                 | expectedResult
		onPage.find("div")        | true
		onPage.find(".article")   | true
		onPage.find("ol.article") | false
		onPage.find("bdo")        | false
	}
}
