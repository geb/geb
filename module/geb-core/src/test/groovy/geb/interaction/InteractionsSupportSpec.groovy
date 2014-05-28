/*
 * Copyright 2013 the original author or authors.
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
package geb.interaction

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer
import spock.lang.IgnoreIf

@CrossBrowser
@IgnoreIf({ System.getProperty("geb.saucelabs.browser")?.startsWith('safari') })
class InteractionsSupportSpec extends GebSpecWithServer {

	def setup() {
		html {
			body {
				input(id: 'first-input', value: '')
				input(id: 'second-input', value: '')
			}
		}
	}

	def "navigators are unpacked in interact block"() {
		when:
		interact {
			moveToElement $('#first-input')
			click()
			sendKeys 'GEB'
			moveToElement $('#second-input')
			click()
			sendKeys 'geb'
		}

		then:
		$('#first-input').value() == 'GEB'
		$('#second-input').value() == 'geb'
	}

	def "page content items are unpacked in interact block"() {
		given:
		at InteractionPage

		when:
		interact {
			moveToElement first
			click()
			sendKeys 'GEB'
			moveToElement second
			click()
			sendKeys 'geb'
		}

		then:
		first == 'GEB'
		second == 'geb'
	}

}

class InteractionPage extends geb.Page {
	static at = { true }
	static content = {
		first { $('#first-input') }
		second { $('#second-input') }
	}
}
