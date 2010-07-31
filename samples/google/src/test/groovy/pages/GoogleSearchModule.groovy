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
package pages

import geb.*

// Modules are content that is independent of a particular page
class GoogleSearchModule extends Module {
	
	// Modules can be parameterised
	def buttonValue
	
	// content is defined using a DSL with a Jquery like finding API
	static content = {
		field { $("input", name: "q") }
		
		// content can define which page is next when it is clicked
		button(to: GoogleResultsPage) { 
			// can use instance variables in content locators
			$("input", value: buttonValue) 
		}
	}
	
	// instance methods refer to content by name
	def search(term) {
		// Jquery like API for setting input values and clicking
		field.value(term)
		button.click()
	}
}
