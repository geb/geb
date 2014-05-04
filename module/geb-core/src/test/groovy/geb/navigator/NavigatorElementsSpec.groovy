package geb.navigator

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer

@CrossBrowser
class NavigatorElementsSpec extends GebSpecWithServer {

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
}
