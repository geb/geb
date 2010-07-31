package geb.example

import geb.Browser
import geb.example.pages.*

// Without page objects
Browser.drive("http://google.com") {
	title == "Google"
	$("input", name: "q").value("wikipedia")
	$("input", value: "Google Search").click()
	assert title.endsWith("Google Search")
	assert $("li.g", 0).find("a.l").text() == "Wikipedia, the free encyclopedia"
}

// With page objects
Browser.drive(GoogleHomePage) {
	search.field.value("wikipedia")
	search.button.click()
	assert at(GoogleResultsPage)
	assert resultLink(0).text() == "Wikipedia, the free encyclopedia"
}