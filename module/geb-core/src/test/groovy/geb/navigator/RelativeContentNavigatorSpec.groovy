package geb.navigator

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer

@CrossBrowser
class RelativeContentNavigatorSpec extends GebSpecWithServer {

	def children() {
		given:
		html {
			div(id: "a") {
				div('class': '1', id: "a1", "a1")
				div('class': '2', id: "a2", "a2")
			}
			div(id: "b") {
				div('class': '1', id: "b1", "b1")
				div('class': '2', id: "b2", "b2")
			}
		}

		expect:
		$("#a").children("#a2")*.text() == ["a2"]
		$("#a").children()*.text() == ["a1", "a2"]
		$("div").children(".1")*.text() == ["a1", "b1"]
		$("#a,#b").children()*.@id == ["a1", "a2", "b1", "b2"]
	}

	def siblings() {
		given:
		html {
			div(id: "1") {
				div(id: "a1", class: '1', "")
				div(id: "b1", class: '1', "")
				div(id: "c1", class: '2', "")
				div(id: "d1", class: '2', "")
			}
			div(id: "2") {
				div(id: "a2", class: '1', "")
				div(id: "b2", class: '1', "")
				div(id: "c2", class: '2', "")
				div(id: "d2", class: '2', "")
			}
		}

		expect:
		$("#c1").siblings()*.@id == ["a1", "b1", "d1"]
		$("#1 div").siblings("#a1").unique()*.@id == ["a1"]
		$("#c1,#c2").siblings().unique()*.@id == ["a1", "b1", "d1", "a2", "b2", "d2"]
	}

	def parent() {
		given:
		html {
			div(id: "1") {
				div(id: "1-1") {
					div(id: "1-1-1", "")
				}
				div(id: "1-2") {
					div(id: "1-2-1", "")
				}
			}
			div(id: "2") {
				div(id: "2-1") {
					div(id: "2-1-1", "")
				}
				div(id: "2-2") {
					div(id: "2-2-1", "")
				}
			}
		}

		expect:
		$("#1-2-1").parent()*.@id == ["1-2"]
		$("#2-1").parent()*.@id == ["2"]
		$("#1-2-1").parent("div")*.@id == ["1-2"]
		$("#1-2-1").parent("p")*.@id == []
	}

	def parents() {
		given:
		html {
			div(id: "1") {
				div(id: "1-1") {
					div(id: "1-1-1", "")
				}
				div(id: "1-2") {
					div(id: "1-2-1", "")
				}
			}
			div(id: "2") {
				div(id: "2-1") {
					div(id: "2-1-1", "")
				}
				div(id: "2-2") {
					div(id: "2-2-1", "")
				}
			}
		}

		expect:
		$("#1-2-1").parents()*.@id == ["1-2", "1", null, null]
		$("#2-1").parents()*.@id == ["2", null, null]
		$("#1-2-1").parents("div")*.@id == ["1-2", "1"]
		$("#1-2-1").parents("p")*.@id == []
	}

	def parentsUntil() {
		given:
		html {
			div(id: "1") {
				div(id: "1-1") {
					div(id: "1-1-1", "")
				}
				div(id: "1-2") {
					div(id: "1-2-1", "")
				}
			}
			div(id: "2") {
				div(id: "2-1") {
					div(id: "2-1-1", "")
				}
				div(id: "2-2") {
					div(id: "2-2-1", "")
				}
			}
		}

		expect:
		$("#1-2-1").parentsUntil("#1")*.@id == ["1-2"]
		$("#2-1").parentsUntil("html")*.@id == ["2", null]
		$("foo").parentsUntil("div")*.@id == []
	}

	def closest() {
		given:
		html {
			div(id: "1") {
				div(id: "1-1") {
					div(id: "1-1-1", "")
				}
				div(id: "1-2") {
					div(id: "1-2-1", "")
				}
			}
			div(id: "2") {
				div(id: "2-1") {
					div(id: "2-1-1", "")
				}
				div(id: "2-2") {
					div(id: "2-2-1", "")
				}
			}
		}

		expect:
		$("#1-1-1").closest("#1")*.@id == ["1"]
		$("#1-1-1").closest("#1-1")*.@id == ["1-1"]
		$("#2-1-1").closest("#2")*.@id == ["2"]
		$("#1-1-1,#2-1-1").closest("div")*.@id == ["1-1", "2-1"]
	}

	def next() {
		given:
		html {
			div(id:  "a", "")
			div(id:  "b", "")
			div(id:  "c", "")
		}

		expect:
		$("#b").next()*.@id == ["c"]
		$("#b").next("#e")*.@id == []
		$("#a").next()*.@id == ["b"]
		$("#c").next()*.@id == []
	}

	def nextAll() {
		given:
		html {
			div(id:  "a", "")
			div(id:  "b", "")
			div(id:  "c", "")
			div(id:  "d", "")
		}

		expect:
		$("#b").nextAll()*.@id == ["c", "d"]
		$("#b").nextAll("#d")*.@id == ["d"]
		$("#a").nextAll()*.@id == ["b", "c", "d"]
		$("#d").nextAll()*.@id == []
	}

	def nextUntil() {
		given:
		html {
			div(id: "1") {
				div(id:  "a1", "")
				div(id:  "b1", "")
				div(id:  "c1", "")
				div(id:  "d1", 'class': 'end', "")
			}
			div(id: "2") {
				div(id:  "a2", "")
				div(id:  "b2", "")
				div(id:  "c2", "")
				div(id:  "d2", 'class': 'end', "")
			}
		}

		expect:
		$("#b1").nextUntil("#d1")*.@id == ["c1"]
		$("#a2").nextUntil("#d2")*.@id == ["b2", "c2"]
		$("#a1,#a2").nextUntil(".end")*.@id == ["b1", "c1", "b2", "c2"]
	}

	def previous() {
		given:
		html {
			div(id: "1") {
				div(id:  "a1", "")
				div(id:  "b1", "")
				div(id:  "c1", "")
				div(id:  "d1", 'class': 'end', "")
			}
			div(id: "2") {
				div(id:  "a2", "")
				div(id:  "b2", "")
				div(id:  "c2", "")
				div(id:  "d2", 'class': 'end', "")
			}
		}

		expect:
		$("#2").previous()*.@id == ["1"]
		$("#a1").previous()*.@id == []
		$("#b1").previous()*.@id == ["a1"]
		$("#d2").previous()*.@id == ["c2"]
		$("#d2").previous("#c2")*.@id == ["c2"]
		$("foo").previous("#c2")*.@id == []
	}

	def prevAll() {
		given:
		html {
			div(id: "1") {
				div(id:  "a1", "")
				div(id:  "b1", "")
				div(id:  "c1", "")
				div(id:  "d1", 'class': 'end', "")
			}
			div(id: "2") {
				div(id:  "a2", "")
				div(id:  "b2", "")
				div(id:  "c2", "")
				div(id:  "d2", 'class': 'end', "")
			}
		}

		expect:
		$("#2").prevAll()*.@id == ["1"]
		$("#a1").prevAll()*.@id == []
		$("#b1").prevAll()*.@id == ["a1"]
		$("#d2").prevAll()*.@id == ["a2", "b2", "c2"]
		$("#d2").prevAll("#b2")*.@id == ["b2"]
		$("foo").prevAll("#b2")*.@id == []
	}

	def prevUntil() {
		given:
		html {
			div(id: "1") {
				div(id:  "a1", "")
				div(id:  "b1", "")
				div(id:  "c1", "")
				div(id:  "d1", 'class': 'end', "")
			}
			div(id: "2") {
				div(id:  "a2", "")
				div(id:  "b2", "")
				div(id:  "c2", "")
				div(id:  "d2", 'class': 'end', "")
			}
		}

		expect:
		$("#d2").prevUntil("#a2")*.@id == ["c2", "b2"]
		$("#d2").prevUntil("foo")*.@id == ["c2", "b2", "a2"]
		$("foo").prevUntil("foo")*.@id == []
	}

}
