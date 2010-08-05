package pages.modules

import geb.Module

class PersonFormModule extends Module {

	static content = {
		firstName { $("input").withName("firstName") }
		lastName { $("input").withName("lastName") }
		enabled { $("input").withName("enabled") }
	}

}