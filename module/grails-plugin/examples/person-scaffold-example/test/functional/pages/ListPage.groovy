package pages

import geb.Module

class ListPage extends ScaffoldPage {
	static url = "person/list"
	
	static at = {
		heading.text() ==~ /Person List/
	}
	
	static content = {
		newPersonButton(to: CreatePage) { $("a", text: "New Person") }
		peopleTable { $("div.list table", 0) }
		personRow { module PersonRow, personRows[it] }
		personRows(required: false) { peopleTable.find("tbody").find("tr") }
	}
}

class PersonRow extends Module {
	static content = {
		cell { $("td", it) }
		id { cell(0) }
		enabled { cell(1) }
		firstName { cell(2) }
		lastName { cell(3) }
		showLink(to: ShowPage) { id.find("a") }
	}
}