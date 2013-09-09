package geb.navigator

import geb.Page
import geb.error.GebAssertionError
import geb.error.UndefinedAtCheckerException
import geb.test.CrossBrowser
import geb.test.GebSpecWithServer
import org.openqa.selenium.WebElement
import spock.lang.Issue
import spock.lang.Unroll

@Unroll
@CrossBrowser
class NavigatorSpec extends GebSpecWithServer {

	def truthiness() {
		given:
		html { div() }

		expect:
		$("div")
		!$("foo")
	}

	def "reading attributes"() {
		given:
		html {
			select(id: "single", "")
			select(id: "multi", multiple: "multiple", "")
		}

		expect:
	    $("select").getAttribute("id") == "single"
	    $("select").attr("id") == "single"
		$("#single").getAttribute("multiple") == ""
		$("#single").attr("multiple") == ""
		$("#single").@multiple == ""
		$("#single").attr('multiple') == ""
		$("#multi").getAttribute("multiple") == "true"
		$("#multi").attr("multiple") == "true"
		$("#multi").@multiple == "true"
		$("#multi").attr('multiple') == "true"
	}

	def getElement() {
		given:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		expect:
		$("div").getElement(0).getAttribute("id") == "a"
		$("div").getElement(1).getAttribute("id") == "b"
		$("foo").getElement(0) == null
		$("foo").getElement(10) == null
	}

	def add() {
		when:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		then:
		$("#a").add("#c")*.@id == ["a", "c"]
		$("#c").add("#a")*.@id == ["c", "a"]
		$("foo").add("#a")*.@id == ["a"]
	}

	def remove() {
		when:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		then:
		$("div").remove(1)*.@id == ["a", "c"]
		$("div").remove(5)*.@id == ["a", "b", "c"]
		$("foo").remove(5)*.@id == []
	}

	def has() {
		when:
		html {
			div(id: "a") {
				div("class": "a-1 z-1", "a")
				input(type: "text")
			}
			div(id: "b") {
				div("class": "b-1 z-1", "b")
				input(name: "someName", type: "checkbox")
			}
			div(id: "c") {
				div("class": "c-1 z-1", "c")
				input(type: "text")
			}
		}

		then:
		$("div").has(".z-1")*.@id == ["a", "b", "c"]
		$("div").has(".b-1")*.@id == ["b"]
		$("div").has("input", name: "someName")*.@id == ["b"]
		$("div").has("div", text: "b")*.@id == ["b"]
		$("div").has("input", type: "text")*.@id == ["a", "c"]
		$("div").has(text: ~/[abc]/)*.@id == ["a", "b", "c"]
	}

	def not() {
		when:
		html {
			div(id: "a", 'class': 'z', 'hello')
			div(id: "b", 'class': 'x', 'hello')
			div(id: "c", 'class': 'z')
		}

		then:
		$("div").not("#b")*.@id == ["a", "c"]
		$("div").not("foo")*.@id == ["a", "b", "c"]
		$("foo").not("foo")*.@id == []
		$("div").not(text: ~/.+/)*.@id == ["c"]
		$("div").not(".z", text: 'hello')*.@id == ["b", "c"]
	}

	def plus() {
		when:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		then:
		($("#a") + $("#b"))*.@id == ["a", "b"]
		($("#a") + $("foo"))*.@id == ["a"]
		($("foo") + $("#a"))*.@id == ["a"]
	}

	def text() {
		given:
		html {
			a "a"
			b "b"
			div {
				c "c"
				d "d"
			}
		}

		expect:
		$("a").text() == "a"
		$("a").add("b").text() == "a"
		$("a").add("b")*.text() == ["a", "b"]
		$("div").text() in ["cd", "c d"] // this is not consistent across drivers
		$("div")*.text() in [["cd"], ["c d"]] // this is not consistent across drivers
		$("foo")*.text() == []
	}

	def tag() {
		given:
		html {
			a "a"
			b "b"
			div {
				c "c"
				d "d"
			}
		}

		expect:
		$("a").tag() == "a"
		$("a").add("b").tag() == "a"
		$("a").add("b")*.tag() == ["a", "b"]
		$("div").tag() == "div"
		$("div")*.tag() == ["div"]
		$("foo")*.tag() == []
	}

	def classes() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("#a").classes() == ["a1", "a2", "a3"]
		$("#b").classes() == ["b1"]
		$("div").classes() == ["a1", "a2", "a3"]
		$("#b").add("#a").classes() == ["b1"]
		$("foo").classes() == []
	}

	def hasClass() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("#a").hasClass("a2")
		!$("#a").hasClass("a4")
		$("#b").hasClass("b1")
		$("#a").add("#b").hasClass("b1")
		$("#b").add("#a").hasClass("a1")
	}

	def is() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("#a").is("div")
		!$("#a").is("foo")
		!$("foo").is("foo")
	}

	def first() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("div").first()*.@id == ["a"]
		$("foo").first()*.@id == []
	}

	def firstElement() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("div").firstElement().getAttribute("id") == "a"
		$("foo").firstElement() == null
	}

	def last() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("div").last()*.@id == ["b"]
		$("foo").last()*.@id == []
	}

	def lastElement() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("div").lastElement().getAttribute("id") == "b"
		$("foo").lastElement() == null
	}

	def verifyNotEmpty() {
		given:
		html {
			div(id: "a", 'class': "a1 a2 a3")
			div(id: "b", 'class': "b1")
		}

		expect:
		$("div").verifyNotEmpty()

		when:
		$("foo").verifyNotEmpty()

		then:
		thrown(EmptyNavigatorException)
	}

	def displayed() {
		given:
		html {
			div(id: "a", "a")
			div(id: "b", style: "display: none;", "b")
		}

		expect:
		$("#a").displayed
		!$("#b").displayed
		!$("foo").displayed
	}

	def disabled() {
		given:
		html {
			input(id: "en")
			input(id: "di", disabled: 'disabled')
			input(id: "arb", disabled: 'xyz')
		}

		expect:
		$("#en").enabled
		!$("#en").disabled
		$("#di").disabled
		!$("#di").enabled
		$("#arb").disabled
		!$("#arb").enabled

	}

	def 'disabled on unsuitable element'() {
		given:
		html {
			input(id: "ip")
			textarea(id: "ta")
			password(id: "pw")
			button(id: "bt")
			select(id: "sl")
			div(id: "dv")
		}

		expect:
		['ip', 'ta', 'pw', 'bt', 'sl'].each {
			assert $('#' + it).enabled
		}

		when:
		$("#dv").enabled

		then:
		GebAssertionError gae = thrown GebAssertionError
		gae.message == 'You can only use the disabled assertion on input, textarea, password, select, button elements'

	}

	def readOnly() {
		given:
			html {
				input(id: "wr")
				input(id: "ro", readonly: 'readonly')
				input(id: "arb", readonly: 'xyz')
			}	

		expect:
			$("#wr").editable
			!$("#wr").readOnly
			$("#ro").readOnly
			!$("#ro").editable
			$("#arb").readOnly
			!$("#arb").editable

	}

	def 'readOnly on unsuitable element'() {
		given:
		html {
			input(id: "ip")
			textarea(id: "ta")
			password(id: "pw")
			button(id: "dv")
		}

		expect:
		['ip', 'ta', 'pw'].each {
			assert $('#' + it).editable
			assert !$('#' + it).readOnly
		}

		when:
		$("#dv").editable

		then:
		GebAssertionError gae = thrown GebAssertionError
		gae.message == 'You can only use the editable/readOnly assertion on input, textarea, password elements'

	}

	def "click is called only on the first element of the navigator"() {
		given:
		def element1 = Mock(WebElement)
		def element2 = Mock(WebElement)
		def navigator = new NonEmptyNavigator(browser, [element1, element2])

		when: navigator.click()

		then:
		1 * element1.click()
		0 * element2.click()
		0 * _
	}

	@Issue('GEB-160')
	def 'click call returns receiver for parameters: #clickParams'() {
		given:
		html { button("") }

		when:
		def navigator = $('button')

		then:
		navigator.click(* clickParams).is(navigator)

		where:
		clickParams << [[], [Page], [[PageWithAtChecker, PageWithAtChecker]]]
	}

	def 'click can be used with pages without at checker'() {
		given:
		html { div('some text') }

		when:
		$('div').click(Page)

		then:
		notThrown(UndefinedAtCheckerException)
	}

	def 'click fails when used with a list of pages, one of which does not have an at checker'() {
		given:
		html { div('some text') }

		when:
		$('div').click([PageWithoutAtChecker, PageWithAtChecker])

		then:
		thrown(UndefinedAtCheckerException)
	}

	def allElements() {
		when:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		then:
		$("div").allElements()*.getAttribute("id") == ["a", "b", "c"]
	}

	def eq() {
		when:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		then:
		$("div").allElements()*.getAttribute("id") == ["a", "b", "c"]
	}

	def "leftShift returns the navigator so appends can be chained"() {
		given:
		html {
			input(type: "text")
		}

		when:
		$("input") << "a" << "b" << "c"

		then:
		$("input").value() == "abc"
	}

	def size() {
		given:
		html {
			div(id: "a")
			div(id: "b")
			div(id: "c")
		}

		expect:
		$("#a").size() == 1
		$("#a").add("#b").size() == 2
		$("#d").size() == 0
	}

	def "cannot read or set value of non existent control"() {
		given:
		html {}

		when:
		$().someFieldThatDoesNotExist

		then:
		thrown(MissingPropertyException)

		when:
		$().someFieldThatDoesNotExist = "foo"

		then:
		thrown(MissingPropertyException)
	}

}

class PageWithoutAtChecker extends Page { }
class PageWithAtChecker extends Page {
	static at = { true }
}
