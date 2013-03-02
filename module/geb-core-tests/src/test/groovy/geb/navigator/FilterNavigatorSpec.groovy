package geb.navigator

import spock.lang.Unroll

@Unroll
class FilterNavigatorSpec extends AbstractNavigatorSpec {

	def "filter(#filter) should select elements with the ids #expectedIds"() {
		expect: $(selector).filter(filter)*.@id == expectedIds

		where:
		selector     | filter                          | expectedIds
		"input"      | [type: "checkbox"]              | ["checker1", "checker2"]
		"input"      | [name: "site"]                  | ["site-1", "site-2", "site-3"]
		"input"      | [name: "site", value: "google"] | ["site-1"]
		".article"   | [id: ~/article-[1-2]/]          | ["article-1", "article-2"]
		"#article-1" | [id: "article-2"]               | []
	}

	def "filter(text: '#text') should select #expectedSize elements"() {
		expect: $("p").filter(text: text).size() == expectedSize

		where:
		text                            | expectedSize
		"First paragraph of article 2." | 1
		~/.*article 1\./                | 2
		"DOES NOT EXIST"                | 0
	}

	def "filter('#selector', #attributes should select elements with the ids #expectedIds"() {
		expect: $("a, input, select").filter(attributes, selector)*.@id == expectedIds

		where:
		selector | attributes         | expectedIds
		"input"  | [type: "checkbox"] | ["checker1", "checker2"]
		"select" | [type: "checkbox"] | []
	}

	def "filter('#filter') should select elements with the ids #expectedIds"() {
		expect: $(selector).filter(filter)*.@id == expectedIds

		where:
		selector   | filter        | expectedIds
		".article" | "#article-2"  | ["article-2"]
		".article" | "#no-such-id" | []
		"div"      | ".article"    | ["article-1", "article-2", "article-3"]
		// TODO: case for filter by tag
	}

}
