/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.example

import geb.*
import geb.test.*
import spock.lang.*

//@Ignore // ignored because it requires a network connection
@Stepwise
class GoogleSpockSpec extends GebSpec {
	
	def "perform search"() {
		when:
		to GoogleHomePage
		// Call the search method on the search module
		search.search "spock framework" 
		then:
		// Use the page at handler to check we are where we think we are
		at GoogleResultsPage
		// page content can be parameterised
		resultLink(0).text() == "spock - Project Hosting on Google Code"
	}
	
	def "go to spock project page and check owner"() {
		when:
		// click() methods can take a page class, which is the next page
		resultLink(0).click SpockGoogleCodePage
		then:
		at SpockGoogleCodePage
		// Accessing content defined in super class
		signInLink.present
		!signOutLink.present
		
		// access text and values with JQuery like API
		projectOwner.text() == 'pniederw'
	}
	
	def "go straight to search results"() {
		when:
		// Go to pages with GET parameters
		to GoogleResultsPage, q: "spock framework"
		then:
		at GoogleResultsPage
		resultLink(0).text() == "spock - Project Hosting on Google Code"
	}
	
	def "use the search module from the results page"() {
		expect:
		// Still at GoogleResultsPage because this spec is Stepwise
		at GoogleResultsPage
		when:
		// Call the search module defined in the results page object
		search.search "groovy programming language"
		then:
		resultLink(0).text() ==~ /.+Wikipedia, the free encyclopedia/
	}

}

// Modules are content that is independent of a particular page
class GoogleSearchModule extends Module {
	
	// Modules can be parameterised
	def buttonValue
	
	// content is defined using a DSL with a Jquery like finding API
	static content = {
		searchField { find("input").withName("q") }
		
		// content can define which page is next when it is clicked
		searchButton(toPage: GoogleResultsPage) { 
			// can use instance variables in content locators
			find("input").withValue(buttonValue) 
		}
	}
	
	// instance methods refer to content by name
	def search(term) {
		// Jquery like API for setting input values and clicking
		searchField.value(term)
		searchButton.click()
	}
}

// Pages contain content and modules
class GoogleHomePage extends Page {

	// can define a URL for going straight to the page
	static url = "http://google.com"

	// can define a custom check to verify the page content matches expectations
	static at = { page.titleText == "Google" }
	
	// can include parameterised modules
	static content = {
		search { module GoogleSearchModule, buttonValue: "Google Search" }
	}
}

class GoogleResultsPage extends Page {
	static url = "http://www.google.com/search"
	static at = { page.titleText.endsWith("Google Search") }
	
	// Pages can define individual content and/or modules
	static content = {
		// Reuse the module, with different params
		search { module GoogleSearchModule, buttonValue: "Search" }
		
		
		results { find("li.g") }
		// Content can be paramterised
		result { results.get(it) }
		// Content can be defined relative to other content
		resultLink { result(it).get("a.l") }
	}
}

class GoogleCodePage extends Page {
	static at = { projectSummary.present }
		
	static content = {
		projectSummary { find("a#project_summary_link") }
		
		// If content might not exist, it can be marked non required
		// If content is required and it's not present when requested, an assertion error is thrown
		signInLink(required: false) { topToolbarLink("Sign in") }
		signOutLink(required: false) { topToolbarLink("Sign out") }
		topToolbar { find("#gaia") }
		topToolbarLink(required: false) { topToolbar.getByTag("u").withTextMatching(it).parent("a") }
		
		peopleTable { find("table.pmeta").get(2) }
		projectOwner { peopleTable.getByTag("a").first() }
	}
}

class SpockGoogleCodePage extends GoogleCodePage {
	// Pages can subclass, inheriting all defined content and overriding where necessary 
	static at = { projectSummary.text() == "the enterprise ready specification framework" }
}