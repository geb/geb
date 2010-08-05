package pages

import pages.modules.*

class EditPage extends ScaffoldPage {

	static at = {
		heading.text() ==~ /Edit.+/
	}
	
	static content = {
		form { module PersonFormModule }
		updateButton(to: ShowPage) { $("input", value: "Update") }
		deleteButton(to: ListPage) { $("input", value: "Delete") }
	}

}