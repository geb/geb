package pages

import pages.modules.*

class CreatePage extends ScaffoldPage {

	static at = {
		heading.text() ==~ /Create.+/
	}
	
	static content = {
		form { module PersonFormModule }
		createButton(to: ShowPage) { create() }
	}

}