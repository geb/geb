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

class GoogleCodePage extends Page {
	static at = { projectSummary.present }
		
	static content = {
		projectSummary { $("a#project_summary_link") }
		
		// If content might not exist, it can be marked non required
		// If content is required and it's not present when requested, an assertion error is thrown
		signInLink(required: false) { topToolbarLink("Sign in") }
		signOutLink(required: false) { topToolbarLink("Sign out") }
		topToolbar { $("#gaia") }
		topToolbarLink(required: false) { topToolbar.getByTag("u").withTextMatching(it).parent("a") }
		
		peopleTable { $("table.pmeta", 2) }
		projectOwner { peopleTable.getByTag("a").first() }
	}
}
