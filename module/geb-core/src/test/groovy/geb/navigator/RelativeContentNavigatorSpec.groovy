package geb.navigator

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer

@CrossBrowser
class RelativeContentNavigatorSpec extends GebSpecWithServer {

	def children() {
		given:
		html {
			div(id: "a") {
				div('class': 'a', id: "aa", "aa")
				div('class': 'b', id: "ab", "ab")
			}
			div(id: "b") {
				div('class': 'a', id: "ba", "ba")
				div('class': 'b', id: "bb", "bb")
			}
		}

		expect:
		$("#a").children("#ab")*.text() == ["ab"]
		$("#a").children()*.text() == ["aa", "ab"]
		$("div").children(".a")*.text() == ["aa", "ba"]
		$("#a,#b").children()*.@id == ["aa", "ab", "ba", "bb"]
	}

	def siblings() {
		given:
		html {
			div(id: "a") {
				div(id: "aa", class: 'a', "")
				div(id: "ba", class: 'a', "")
				div(id: "ca", class: 'b', "")
				div(id: "da", class: 'b', "")
			}
			div(id: "b") {
				div(id: "ab", class: 'a', "")
				div(id: "bb", class: 'a', "")
				div(id: "cb", class: 'b', "")
				div(id: "db", class: 'b', "")
			}
		}

		expect:
		$("#ca").siblings()*.@id == ["aa", "ba", "da"]
		$("#a div").siblings("#aa").unique()*.@id == ["aa"]
		$("#ca,#cb").siblings().unique()*.@id == ["aa", "ba", "da", "ab", "bb", "db"]
	}

	def parent() {
		given:
		html {
			div(id: "a") {
				div(id: "a-a") {
					div(id: "a-a-a", "")
				}
				div(id: "a-b") {
					div(id: "a-b-a", "")
				}
			}
			div(id: "b") {
				div(id: "b-a") {
					div(id: "b-a-a", "")
				}
				div(id: "b-b") {
					div(id: "b-b-a", "")
				}
			}
		}

		expect:
		$("#a-b-a").parent()*.@id == ["a-b"]
		$("#b-a").parent()*.@id == ["b"]
		$("#a-b-a").parent("div")*.@id == ["a-b"]
		$("#a-b-a").parent("p")*.@id == []
	}

	def parents() {
		given:
		html {
			div(id: "a") {
				div(id: "a-a") {
					div(id: "a-a-a", "")
				}
				div(id: "a-b") {
					div(id: "a-b-a", "")
				}
			}
			div(id: "b") {
				div(id: "b-a") {
					div(id: "b-a-a", "")
				}
				div(id: "b-b") {
					div(id: "b-b-a", "")
				}
			}
		}

		expect:
		$("#a-b-a").parents()*.@id == ["a-b", "a", "", ""]
		$("#b-a").parents()*.@id == ["b", "", ""]
		$("#a-b-a").parents("div")*.@id == ["a-b", "a"]
		$("#a-b-a").parents("p")*.@id == []
	}

	def parentsUntil() {
		given:
		html {
			div(id: "a") {
				div(id: "a-a") {
					div(id: "a-a-a", "")
				}
				div(id: "a-b") {
					div(id: "a-b-a", "")
				}
			}
			div(id: "b") {
				div(id: "b-a") {
					div(id: "b-a-a", "")
				}
				div(id: "b-b") {
					div(id: "b-b-a", "")
				}
			}
		}

		expect:
		println driver.pageSource
		$("#a-b-a").parentsUntil("#a")*.@id == ["a-b"]
		$("#b-a").parentsUntil("html")*.@id == ["b", ""]
		$("foo").parentsUntil("div")*.@id == []
	}

	def closest() {
		given:
		html {
			div(id: "a") {
				div(id: "a-a") {
					div(id: "a-a-a", "")
				}
				div(id: "a-b") {
					div(id: "a-b-a", "")
				}
			}
			div(id: "b") {
				div(id: "b-a") {
					div(id: "b-a-a", "")
				}
				div(id: "b-b") {
					div(id: "b-b-a", "")
				}
			}
		}

		expect:
		$("#a-a-a").closest("#a")*.@id == ["a"]
		$("#a-a-a").closest("#a-a")*.@id == ["a-a"]
		$("#b-a-a").closest("#b")*.@id == ["b"]
		$("#a-a-a,#b-a-a").closest("div")*.@id == ["a-a", "b-a"]
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
			div(id: "a") {
				div(id:  "aa", "")
				div(id:  "ba", "")
				div(id:  "ca", "")
				div(id:  "da", 'class': 'end', "")
			}
			div(id: "b") {
				div(id:  "ab", "")
				div(id:  "bb", "")
				div(id:  "cb", "")
				div(id:  "db", 'class': 'end', "")
			}
		}

		expect:
		$("#ba").nextUntil("#da")*.@id == ["ca"]
		$("#ab").nextUntil("#db")*.@id == ["bb", "cb"]
		$("#aa,#ab").nextUntil(".end")*.@id == ["ba", "ca", "bb", "cb"]
	}

	def previous() {
		given:
		html {
			div(id: "a") {
				div(id:  "aa", "")
				div(id:  "ba", "")
				div(id:  "ca", "")
				div(id:  "da", 'class': 'end', "")
			}
			div(id: "b") {
				div(id:  "ab", "")
				div(id:  "bb", "")
				div(id:  "cb", "")
				div(id:  "db", 'class': 'end', "")
			}
		}

		expect:
		$("#b").previous()*.@id == ["a"]
		$("#aa").previous()*.@id == []
		$("#ba").previous()*.@id == ["aa"]
		$("#db").previous()*.@id == ["cb"]
		$("#db").previous("#cb")*.@id == ["cb"]
		$("foo").previous("#cb")*.@id == []
	}

	def prevAll() {
		given:
		html {
			div(id: "a") {
				div(id:  "aa", "")
				div(id:  "ba", "")
				div(id:  "ca", "")
				div(id:  "da", 'class': 'end', "")
			}
			div(id: "b") {
				div(id:  "ab", "")
				div(id:  "bb", "")
				div(id:  "cb", "")
				div(id:  "db", 'class': 'end', "")
			}
		}

		expect:
		$("#b").prevAll()*.@id == ["a"]
		$("#aa").prevAll()*.@id == []
		$("#ba").prevAll()*.@id == ["aa"]
		$("#db").prevAll()*.@id == ["ab", "bb", "cb"]
		$("#db").prevAll("#bb")*.@id == ["bb"]
		$("foo").prevAll("#bb")*.@id == []
	}

	def prevUntil() {
		given:
		html {
			div(id: "a") {
				div(id:  "aa", "")
				div(id:  "ba", "")
				div(id:  "ca", "")
				div(id:  "da", 'class': 'end', "")
			}
			div(id: "b") {
				div(id:  "ab", "")
				div(id:  "bb", "")
				div(id:  "cb", "")
				div(id:  "db", 'class': 'end', "")
			}
		}

		expect:
		$("#db").prevUntil("#ab")*.@id == ["cb", "bb"]
		$("#db").prevUntil("foo")*.@id == ["cb", "bb", "ab"]
		$("foo").prevUntil("foo")*.@id == []
	}

}
