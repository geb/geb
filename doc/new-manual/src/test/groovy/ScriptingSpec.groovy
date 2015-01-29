/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// tag::imports[]
import geb.Browser

// end::imports[]
// tag::content_imports[]
import geb.Module
import geb.Page

// end::content_imports[]
import spock.lang.Specification

class ScriptingSpec extends Specification {

	def "inline"() {
		expect:
		// tag::inline[]
		Browser.drive {
			go "http://gebish.org"

			assert title == "Geb - Very Groovy Browser Automation" // <1>

			def manualsMenu = $("#header-content ul li", 0)
			interact {
				moveToElement(manualsMenu.find("span")) // <2>
			}

			def firstManualLink = manualsMenu.find(".link-list li a", 0)
			firstManualLink.text().endsWith("- CURRENT") //<3>

			firstManualLink.click() //<4>

			assert title.startsWith("The Book Of Geb") //<5>
		}
		// end::inline[]
	}

	def "using page objects"() {
		expect:
		// tag::using_page_objects[]
		Browser.drive {
			to GebHomePage //<1>

			manualsMenu.open()

			manualsMenu.links[0].text().endsWith("- CURRENT")

			manualsMenu.links[0].click()

			at TheBookOfGebPage //<2>
		}
		// end::using_page_objects[]
	}
}

// tag::page_objects[]
class MenuModule extends Module { //<1>
	static content = { //<2>
		toggle { children("span") } //<3>
		links(to: TheBookOfGebPage) { $('.link-list li a') } //<4>
	}

	void open() { //<5>
		interact {
			moveToElement(toggle)
		}
	}
}

class GebHomePage extends Page {
	static url = "http://gebish.org" //<6>

	static at = { title == "Geb - Very Groovy Browser Automation" } //<7>

	static content = {
		manualsMenu { $("#header-content ul li", 0).module(MenuModule) } //<8>
	}
}

class TheBookOfGebPage extends Page {
	static at = { title.startsWith("The Book Of Geb") }
}
// end::page_objects[]