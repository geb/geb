package geb.navigator

import org.openqa.selenium.By
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Unroll

@Unroll
class FormControlWriteNavigatorSpec extends AbstractNavigatorSpec {

	def "setting the value of '#fieldName' to '#newValue' using property access sets the value of the input element"() {
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
	def "setting the value of '#fieldName' to '#newValue' using property access sets the value of the input element with #expectedValue"() {
		given:
		def form = $("form")
		def initialValue = form."$fieldName"

		when: form."$fieldName" = newValue
		then: $("form")."$fieldName" == expectedValue
		cleanup: form."$fieldName" = initialValue

		where:
		fieldName         | newValue  | expectedValue
		"keywords"        | true      | "true"
		"keywords"        | 123       | "123"
		"checker1"        | 123       | "123"
		"plain_select"    | 3         | "3"
		"multiple_select" | [1, 3, 5] | ["1", "3", "5"]
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

	def "when the radio button group's value is set to '#value' then the corresponding radio button is selected"() {
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
		e.message.contains "couldn't select option with text or value: KTHXBYE"

		cleanup:
		$("form").multiple_select = originalValues
	}

	def "input value can be changed to '#newValue'"() {
		given:
		def navigator = $(selector)
		def initialValue = navigator.value()
		when: navigator.value(newValue)
		then: navigator.value() == newValue
		cleanup: navigator.value(initialValue)

		where:
		selector               | newValue
		"#the_plain_select"    | "2"
		"#the_multiple_select" | ["1", "3", "5"]
		"#keywords"            | "bar"
		"textarea"             | "This is the new content of the textarea. Yeah!"
		"#checker1"            | "123"
		"#checker2"            | false
	}

	def "select element can be changed to #newValue using option label #optionLabel"() {
		given:
		def navigator = $(selector)
		def initialValue = navigator.value()
		when: navigator.value(optionLabel)
		then: navigator.value() == newValue
		cleanup: navigator.value(initialValue)

		where:
		selector               | optionLabel                             | newValue
		"#the_plain_select"    | "Option #3"                             | "3"
		"#the_multiple_select" | ["Option #1", "Option #3", "Option #5"] | ["1", "3", "5"]
	}

	@Issue("http://jira.codehaus.org/browse/GEB-37")
	def "radio button can be changed to #newValue using label text '#labelText'"() {
		given:
		def initialValue = $("form").site
		when: $("form").site = labelText
		then: $("form").site == newValue
		cleanup: $("form").site = initialValue

		where:
		labelText | newValue
		"Site #1" | "google"
		"Site #2" | "thisone"
		"Site #3" | "bing"
	}

	def "find('#selector') << '#keystrokes' should append '#keystrokes' to the input's value"() {
		given:
		def navigator = $(selector)
		def initialValue = navigator.value()

		when: navigator << keystrokes
		then: navigator.value() == initialValue + keystrokes

		where:
		selector    | keystrokes
		"#keywords" | "abc"
		"textarea"  | "abc"
	}

	def "using leftShift on '#selector' will append text to all fields"() {
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

	@Ignore
	def "value('#newValue') on '#selector' should select the matching radio button"() {
		given:
		def navigator = $(selector)
		when: navigator.value(newValue)
		then: navigator.value() == newValue
		cleanup: driver.findElement(By.id("site-1")).click()

		where:
		selector           | newValue
		"#site-1"          | "google"
		"#site-2"          | "thisone"
		"#site-1, #site-2" | "thisone"
	}

	def "when a radio button with the value '#expectedValue' is selected getting then value of the group returns '#expectedValue'"() {
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

}
