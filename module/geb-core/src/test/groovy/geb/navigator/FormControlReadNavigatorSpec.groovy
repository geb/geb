package geb.navigator

import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Unroll

@Unroll
class FormControlReadNavigatorSpec extends AbstractNavigatorSpec {

	@Ignore
	def attributes() {
		expect:
		navigator*.@"$key" == expectedValues

		where:
		navigator                   | key       | expectedValues
		$("input").withName("site") | "value"   | ["google", "thisone"]
		$("input").withName("site") | "checked" | ["checked", ""]
	}

	def "the dynamic method #fieldName() should return elements with the ids #expected"() {
		when:
		def navigator = (selector == null ? $() : $(selector))."$fieldName"()
		then: navigator*.@id == expected

		where:
		selector | fieldName     | expected
		null     | "keywords"    | ["keywords"]
		"form"   | "keywords"    | ["keywords"]
		null     | "site"        | ["site-1", "site-2", "site-3"]
		"#main"  | "keywords"    | []
		null     | "nosuchfield" | []
		"bdo"    | "keywords"    | []
	}

	def "dynamic methods for finding fields do not accept arguments"() {
		when: context."$fieldName"(* arguments)
		then: thrown(MissingMethodException)

		where:
		context   | fieldName  | arguments
		getPage() | "keywords" | ["foo", "bar"]
		$("bdo")  | "keywords" | ["foo"]
	}

	def "the value of '#fieldName' retrieved via property access should be '#expectedValue'"() {
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
	def "the value of '#fieldName' when empty should be '#expectedValue'"() {
		given: "the input has an empty value"
		def form = $("form")
		def initialValue = form."$fieldName"
		form."$fieldName"().getElement(0).clear()

		expect: "the input value retrieved by property access to be correct"
		form."$fieldName" == expectedValue

		cleanup:
		form."$fieldName" = initialValue

		where:
		fieldName  | expectedValue
		"keywords" | ""
		"textext"  | ""
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

	def "input value should be '#expected'"() {
		expect: $(selector).value() == expected

		where:
		selector                   | expected
		"#the_plain_select"        | "4"
		"#the_multiple_select"     | ["2", "4"]
		"#the_plain_select option" | "1"
		"textarea"                 | "The textarea content." // note no leading/trailing whitespace
		"#keywords"                | "Enter keywords here"
		"#checker1"                | false
		"#checker2"                | "123"
		"#keywords, textarea"      | "Enter keywords here"
	}

	def "input values should be '#expected'"() {
		expect: $(selector)*.value() == expected

		where:
		selector                   | expected
		"select"                   | ["4", ["2", "4"]]
		"#the_plain_select option" | ["1", "2", "3", "4", "5"]
		"#keywords, textarea"      | ["Enter keywords here", "The textarea content."] // note no leading/trailing whitespace for textarea
	}


	@Ignore
	def "value() on '#selector' should return '#expected'"() {
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




}
