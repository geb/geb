package geb.example

import geb.Driver
import geb.example.pages.*

// Without page objects
Driver.drive("http://google.com") {
	assert pageTitle == "Google"
	find("input").withName("q").value("wikipedia")
	find("input").withValue("Google Search").click()
	assert pageTitle.endsWith("Google Search")
	assert find("li.g").get(0).get("a.l").text() == "Wikipedia, the free encyclopedia"
}

// With page objects
Driver.drive(GoogleHomePage) {
	search.field.value("wikipedia")
	search.button.click()
	assert at(GoogleResultsPage)
	assert resultLink(0).text() == "Wikipedia, the free encyclopedia"
}