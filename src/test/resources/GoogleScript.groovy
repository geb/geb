package geb.example

import geb.Browser
import geb.example.pages.*

// Without page objects
Browser.drive("http://google.com") {
	title == "Google"
	$("input").withName("q").value("wikipedia")
	$("input").withValue("Google Search").click()
	assert title.endsWith("Google Search")
	assert $("li.g").get(0).get("a.l").text() == "Wikipedia, the free encyclopedia"
}

// With page objects
Browser.drive(GoogleHomePage) {
	search.field.value("wikipedia")
	search.button.click()
	assert at(GoogleResultsPage)
	assert resultLink(0).text() == "Wikipedia, the free encyclopedia"
}