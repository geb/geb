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

import geb.test.*
import geb.example.pages.*
import spock.lang.*

@Ignore // ignored because it requires a network connection
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
	
}