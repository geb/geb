package geb.navigator

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer
import spock.lang.Issue

@CrossBrowser
class FindNavigatorSpec extends GebSpecWithServer {

	def "find by selector"() {
		given:
		html {
			div(id: "idA", 'class': 'classA', "a")
			div(id: "idB", 'class': 'classB', "b")
		}

		expect:
		$("#idA").text() == "a"
		$(".classA").text() == "a"
		$("#idB").text() == "b"
		$(".classB").text() == "b"
		$(".dontexist").empty
	}

	def "nested find"() {
		given:
		html {
			div(id: "a") {
				div(id: "b", "b")
				div(id: "c", "c")
			}
		}

		expect:
		$("#a").find("#b").text() == "b"
		$("#a").find("#c").text() == "c"
		$("#a").find("#d").empty
	}

	def "find by attributes"() {
		given:
		html {
			div(a: "1", id: "id1")
			div(a: "2", id: "id2")
		}

		expect:
		$(a: "1")*.@id == ["id1"]
		$(a: "2")*.@id == ["id2"]
		$(a: ~/\d/)*.@id == ["id1", "id2"]
		$(a: ~/\d/, id: "id1")*.@id == ["id1"]
	}

	@Issue("http://jira.codehaus.org/browse/GEB-14")
	def "find by attributes passing a class pattern should match any of the classes on an element"() {
		given:
		html {
			(1..3).each {
				div('class': "col-$it", it)
			}
		}

		expect:
		$(class: ~/col-\d/)*.text() == ["1", "2", "3"]
	}

	def "find by node text"() {
		given:
		html {
			p "a"
			p "b"
			p "c"
		}

		expect:
		$(text: "a")*.text() == ["a"]
		$(text: "b")*.text() == ["b"]
		$(text: ~/\w/)*.text() == ["a", "b", "c"]
	}

	def "selecting with index"() {
		given:
		html {
			div('class': 'a', id: 'a')
			div('class': 'b', id: 'b')
			div('class': 'b', id: 'c')
		}

		expect:
		$("div", 0)*.@id == ["a"]
		$("div", 1)*.@id == ["b"]
		$("div", -1)*.@id == ["c"]
	}

	def "find by selector and attribute"() {
		given:
		html {
			div('class': 'a', name: 'a1', id: 'a1')
			div('class': 'a', name: 'a2', id: 'a2')
			div('class': 'b', name: 'b1', id: 'b1')
			div('class': 'b', name: 'b2', id: 'b2')
			div('class': 'c', name: 'c1', id: 'c1')
			div('class': 'c', name: 'c2', id: 'c2')
		}

		expect:
		$(".a", name: "a1")*.@id == ["a1"]
		$(".b", name: "b2")*.@id == ["b2"]
		$(".c", name: ~/c\d/)*.@id == ["c1", "c2"]
		$(".c", name: "d")*.@id == []
	}

	def "find by selector and text"() {
		given:
		html {
			div('class': 'a', id: 'a1', 'a1')
			div('class': 'a', id: 'a2', 'a2')
			div('class': 'b', id: 'b1', 'b1')
			div('class': 'b', id: 'b2', 'b2')
			div('class': 'c', id: 'c1', 'c1')
			div('class': 'c', id: 'c2', 'c2')
		}

		expect:
		$(".a", text: "a1")*.@id == ["a1"]
		$(".b", text: "b2")*.@id == ["b2"]
		$(".c", text: ~/c\d/)*.@id == ["c1", "c2"]
		$(".c", text: "d")*.@id == []
	}

}
