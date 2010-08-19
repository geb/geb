using "geb"

ignore all

scenario "scripting style", {

	when "we go to google", {
		go "http://google.com"
	}

	then "we are at google", {
		page.title.shouldBe "Google"
	}
	
	when "we search for chuck", {
		$("input", name: "q").value("chuck norris")
		$("input", value: "Google Search").click()
	}

	then "we are now at the results page", {
		page.title.shouldEndWith "Google Search"
	}
	
	and "we get straight up norris", {
		$("li.g", 0).find("a.l").text().shouldStartWith "Chuck Norris"
	}
	
}

scenario "page objects style", {

	when "we go to google", {
		to GoogleHomePage
	}

	then "we are at google", {
		at GoogleHomePage
	}
	
	when "we search for chuck", {
		page.searchField.value("chuck norris")
		page.searchButton.click()
	}

	then "we are now at the results page", {
		at GoogleResultsPage
	}
	
	and "we get straight up norris", {
		page.resultLink(0).text().shouldStartWith "Chuck Norris"
	}

}

class GoogleHomePage extends geb.Page {
	static url = "http://google.com"
	static at = { title == "Google" }
	static content = {
		searchField { $("input", name: "q") }
		searchButton(to: GoogleResultsPage) { $("input", value: "Google Search") }
	}
}

class GoogleResultsPage extends geb.Page {
	static at = { resultStats }
	static content = {
		resultStats { $("div#resultStats") }
		results { $("li.g") }
		result { results[it] }
		resultLink { result(it).find("a.l") }
	}
}