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
