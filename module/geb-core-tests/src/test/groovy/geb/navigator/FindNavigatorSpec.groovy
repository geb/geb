package geb.navigator

import geb.textmatching.TextMatchingSupport
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class FindNavigatorSpec extends AbstractNavigatorSpec {

	@Shared textmatching = new TextMatchingSupport()

	def "find('#selector') should return elements with #property of '#expected'"() {
		given:
		def navigator = $(selector)
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

	def "find('#selector1').find('#selector2') should find #expectedSize elements"() {
		expect:
		$(selector1).find(selector2).size() == expectedSize

		where:
		selector1    | selector2 | expectedSize
		"#container" | "#header" | 1
		"#footer"    | "#header" | 0
	}

	def "find('#selector') should find #expectedSize elements"() {
		expect:
		$(selector).size() == expectedSize

		where:
		selector                                | expectedSize
		"#header  , #sidebar, #footer"          | 3
		"div ol  , #sidebar   , blockquote,bdo" | 5 + 1 + 1 + 0
	}

	def "find(#attributes) should find elements with the ids #expectedIds"() {
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

	def "find(text: '#text') should find #expectedSize elements"() {
		expect: $(text: text).size() == expectedSize

		where:
		text                            | expectedSize
		"First paragraph of article 2." | 1
		~/.*article 1\./                | 2
		"DOES NOT EXIST"                | 0
	}

	def "find('#selector', #index) should find the element with the id '#expectedId'"() {
		when:
		def navigator = $(selector, index)
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

	def "find('#selector', #attributes) should find elements with the ids #expectedIds"() {
		expect: $(attributes, selector)*.@id == expectedIds

		where:
		selector   | attributes                          | expectedIds
		"input"    | [type: "checkbox"]                  | ["checker1", "checker2"]
		"input"    | [name: "site"]                      | ["site-1", "site-2", "site-3"]
		"input"    | [name: "site", value: "google"]     | ["site-1"]
		"input"    | [name: ~/checker\d/]                | ["checker1", "checker2"]
		"bdo"      | [name: "whatever"]                  | []
		".article" | [:]                                 | ["article-1", "article-2", "article-3"]
		"div"      | [id: "container"]                   | ["container"]
		"div"      | [class: "article"]                  | ["article-1", "article-2", "article-3"]
		"div"      | [id: "article-1", class: "article"] | ["article-1"]
		"div"      | [id: "main", class: "article"]      | []
		"div"      | [class: "col-3 module"]             | ["navigation"]
		"div"      | [class: "module col-3"]             | ["navigation"]
		"div"      | [class: ~/col-\d/]                  | ["navigation", "content", "main", "sidebar"]
	}

	def "find('#selector', text: '#text') should find #expectedSize elements"() {
		expect: $(selector, text: text).size() == expectedSize

		where:
		selector | text                                   | expectedSize
		"p"      | "First paragraph of article 2."        | 1
		"p"      | ~/.*article 1\./                       | 2
		"p"      | "DOES NOT EXIST"                       | 0
		"p"      | textmatching.iContains("copyright")    | 1
		"p"      | textmatching.iNotContains("copyright") | 9
	}

}
