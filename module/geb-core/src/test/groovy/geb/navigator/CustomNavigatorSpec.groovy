/*
 * Copyright 2012 the original author or authors.
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
package geb.navigator

import geb.Browser
import geb.test.GebSpecWithServer
import groovy.transform.InheritConstructors
import org.openqa.selenium.WebElement

class CustomNavigatorSpec extends GebSpecWithServer {

	def setup() {
		_browser = null
		browser.config.rawConfig.innerNavigatorFactory = { Browser browser, List<WebElement> elements ->
			new CustomNavigatorSpecCustomNavigator(browser, elements)
		}

		responseHtml {
			body {
				input type: 'text', value: 'some text'
			}
		}

		go()
	}

	def "can use not overridden methods from NonEmptyNavigator"() {
		given:
		def input = $('input')

		when:
		input.value('some other text')

		then:
		input.value() == 'some other text'
	}

	def "can use field access notation to access attributes"() {
		expect:
		$('input').@type == 'text'
	}
}

@InheritConstructors
class CustomNavigatorSpecCustomNavigator extends NonEmptyNavigator {
}
