package pages

class ShowPage extends ScaffoldPage {

	static at = {
		heading.text() ==~ /Show Person/
	}
	
	static content = {
		editButton(to: EditPage) { $("input").withValue("Edit") }
		deleteButton(to: ListPage) { $("input").withValue("Delete") }
		row { $("td.name").withTextMatching(it).parent() }
		value { row(it).get("td.value") }
		id { value("Id") }
		enabled { value("Enabled") }
		firstName { value("First Name") }
		lastName { value("Last Name") }
	}
}
