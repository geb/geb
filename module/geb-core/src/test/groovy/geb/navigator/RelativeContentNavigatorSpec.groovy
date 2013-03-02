package geb.navigator

import spock.lang.Unroll

@Unroll
class RelativeContentNavigatorSpec extends AbstractNavigatorSpec {

	def "calling children(#childSelector) on #selector should return #expected"() {
		given:
		def navigator = $(selector)
		expect: navigator.children(childSelector)*.tag() == expected

		where:
		selector   | childSelector | expected
		"#header"  | "h1"          | ["h1"]
		"#header"  | "div"         | []
		".article" | ".content"    | ["div"] * 3
	}

	def "calling siblings() on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
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

	def "calling siblings(#siblingSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.siblings(siblingSelector)*.@id == expectedIds

		where:
		selector            | siblingSelector | expectedIds
		"#site-1"           | "input"         | ["keywords", "site-2", "checker1", "checker2"]
		"#site-1"           | "select"        | ["the_plain_select", "the_multiple_select"]
		"#the_plain_select" | "select"        | ["the_multiple_select"]
		"#header"           | ".col-3"        | ["navigation", "content"]
		"#header"           | ".col-2"        | []
	}

	def "calling children() on #selector should return #expected"() {
		given:
		def navigator = $(selector)
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

	def "calling parent(#parentSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.parent(parentSelector).unique()*.@id == expectedIds

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

	def "calling closest(#closestSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.closest(closestSelector).unique()*.@id == expectedIds

		where:
		selector     | closestSelector | expectedIds
		"#keywords"  | "div"           | ["sidebar"]
		"input"      | "div"           | ["sidebar"]
		"ul, ol"     | "div"           | ["navigation", "sidebar"]
		"#keywords"  | "bdo"           | []
		"#article-1" | ".col-3"        | ["content"]
	}

	def "calling next(#nextSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.next(nextSelector).unique()*.@id == expectedIds

		where:
		selector     | nextSelector | expectedIds
		"#keywords"  | "input"      | ["site-1"]
		"#keywords"  | "select"     | ["the_plain_select"]
		"input"      | "input"      | ["site-1", "site-2", "checker1", "checker2"]
		"input"      | "select"     | ["the_plain_select"]
		"#keywords"  | "bdo"        | []
		"#article-1" | ".article"   | ["article-2"]
	}

	def "calling nextAll(#nextSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.nextAll(nextSelector).unique()*.@id == expectedIds

		where:
		selector     | nextSelector | expectedIds
		"#keywords"  | "input"      | ["site-1", "site-2", "checker1", "checker2"]
		"#keywords"  | "select"     | ["the_plain_select", "the_multiple_select"]
		"input"      | "input"      | ["site-1", "site-2", "checker1", "checker2"]
		"#keywords"  | "bdo"        | []
		"#article-1" | ".article"   | ["article-2", "article-3"]
	}

	def "calling nextUntil(#nextSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)

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

	def "calling previous() on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.previous()*.@id == expectedIds

		where:
		selector        | expectedIds
		"#footer"       | ["content"]
		"#navigation"   | ["header"]
		".col-3"        | ["header", "navigation"]
		"#container"    | []
		"#DOESNOTEXIST" | []
	}

	def "calling prevAll() on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.prevAll().unique()*.@id == expectedIds

		where:
		selector        | expectedIds
		"#footer"       | ["header", "navigation", "content"]
		"#navigation"   | ["header"]
		".col-3"        | ["header", "navigation"]
		"#container"    | []
		"#DOESNOTEXIST" | []
	}

	def "calling previous(#previousSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
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

	def "calling prevAll(#previousSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.prevAll(previousSelector).unique()*.@id == expectedIds

		where:
		selector               | previousSelector | expectedIds
		"#the_multiple_select" | "input"          | ["checker2", "checker1", "site-2", "site-1", "keywords"]
		"#the_multiple_select" | "select"         | ["the_plain_select"]
		"input"                | "input"          | ["keywords", "site-1", "site-2", "checker1"]
		"select"               | "select"         | ["the_plain_select"]
		"#the_multiple_select" | "bdo"            | []
		"#article-3"           | ".article"       | ["article-2", "article-1"]
	}

	def "calling prevUntil(#previousSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)

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

	def "calling parent() on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.parent().unique()*.@id == expectedIds

		where:
		selector        | expectedIds
		"h1"            | ["header"]
		".article"      | ["main"]
		"option"        | ["the_plain_select", "the_multiple_select"]
		"html"          | []
		"#DOESNOTEXIST" | []
	}

	def "calling parents() on #selector should return #expectedTags"() {
		given:
		def navigator = $(selector)
		expect: navigator.parents().unique()*.tag() == expectedTags

		where:
		selector        | expectedTags
		"h1"            | ["div", "div", "body", "html"]
		".article"      | ["div", "div", "div", "body", "html"]
		"html"          | []
		"#DOESNOTEXIST" | []
	}

	def "calling parents(#parentSelector) on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.parents(parentSelector).unique()*.@id == expectedIds

		where:
		selector        | parentSelector | expectedIds
		"h1"            | "div"          | ["header", "container"]
		"h1"            | "#container"   | ["container"]
		".article"      | ".col-3"       | ["content"]
		"p"             | ".article"     | ["article-1", "article-2", "article-3"]
		"#DOESNOTEXIST" | "div"          | []
	}

	def "calling parentsUntil(#parentSelector) on #selector should return #expectedTags"() {
		given:
		def navigator = $(selector)

		expect:
		def parents = navigator.parentsUntil(parentSelector).unique().collect {
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

	def "calling next() on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.next()*.@id == expectedIds

		where:
		selector        | expectedIds
		"#main"         | ["sidebar"]
		"#header"       | ["navigation"]
		".col-3"        | ["content", "footer"]
		"#container"    | []
		"#DOESNOTEXIST" | []
	}

	def "calling nextAll() on #selector should return #expectedIds"() {
		given:
		def navigator = $(selector)
		expect: navigator.nextAll().unique()*.@id == expectedIds

		where:
		selector          | expectedIds
		"#main"           | ["sidebar"]
		"#header"         | ["navigation", "content", "footer"]
		".col-3"          | ["content", "footer"]
		"#content, #main" | ["footer", "sidebar"]
		"#DOESNOTEXIST"   | []
	}

}
