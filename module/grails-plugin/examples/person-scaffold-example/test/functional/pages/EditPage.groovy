package pages

import pages.modules.*

class EditPage extends ScaffoldPage {

	static at = {
		heading.text() ==~ /Edit.+/
	}
	
	static content = {
		form { module PersonFormModule }
		updateButton(to: ShowPage) { $("input").withValue("Update") }
		deleteButton(to: ListPage) { $("input").withValue("Delete") }
	}

}