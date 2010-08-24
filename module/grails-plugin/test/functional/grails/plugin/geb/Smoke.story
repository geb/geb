package grails.plugin.geb

import geb.*

using "geb-grails"

scenario "using geb", {

	given "a browser", {
/*		driver = new FirefoxDriver()*/
	}
	
	when "to1", {
		to IndexPage
	}

	then "at1", {
		at IndexPage
	}

	when "to2", {
		to OtherPage
	}

	then "at2", {
		at OtherPage
	}

}

// Easyb can't see other classes in test…
// It's using the main class loader which doesn't have visibility
// of other classes in test

class IndexPage extends Page {
	static url = "index"
	static at = { 
		def t = div.text()
		assert t == "index" 
		true
	}
	static content = {
		div { $('div') }
	}
}

class OtherPage extends Page {
	static url = "the/page"
	static at = { assert div.text() == "page"; true }
	static content = {
		div { $('div') }
	}
}