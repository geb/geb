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
package geb.example.pages

import geb.*

class GoogleResultsPage extends Page {
	static url = "http://www.google.com/search"
	static at = { resultStats }
	
	// Pages can define individual content and/or modules
	static content = {
		// Reuse the module, with different params
		search { module GoogleSearchModule, buttonValue: "Search" }
		
		resultStats { $("div#resultStats") }
		
		results { $("li.g") }
		// Content can be paramterised
		result { results[it] }
		// Content can be defined relative to other content
		resultLink { result(it).find("a.l") }
	}
}
