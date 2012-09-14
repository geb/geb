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

package geb.conf

import geb.Browser
import geb.Configuration
import geb.navigator.NonEmptyNavigator
import geb.navigator.factory.BrowserBackedNavigatorFactory
import geb.navigator.factory.DefaultInnerNavigatorFactory
import geb.test.GebSpecWithServer
import org.openqa.selenium.WebElement
import geb.navigator.factory.InnerNavigatorFactory
import geb.navigator.Navigator
import geb.error.InvalidGebConfiguration

class ConfigurationNavigatorFactorySpec extends GebSpecWithServer {

	Configuration config
	ConfigObject raw

	def setup() {
		_browser = null
		config = browser.config
		raw = config.rawConfig
	}

	def "creates navigator with default inner by default"() {
		expect:
		browser.navigatorFactory instanceof BrowserBackedNavigatorFactory
		browser.navigatorFactory.is(browser.navigatorFactory)
		config.innerNavigatorFactory instanceof DefaultInnerNavigatorFactory
	}

	def "can use closure based inner"() {
		when:
		raw.innerNavigatorFactory = { Browser browser, List<WebElement> elements ->
			new CustomNavigator(browser, elements)
		}

		then:
		$("p").class == CustomNavigator
	}

	def "can use inner impl"() {
		when:
		def impl = new InnerNavigatorFactory() {
			@Override
			Navigator createNavigator(Browser browser, List<WebElement> elements) {
				new CustomNavigator(browser, elements)
			}
		}

		raw.innerNavigatorFactory = impl

		then:
		$("p").class == CustomNavigator
	}

	def "error when invalid factory type"() {
		given:
		raw.navigatorFactory = 1

		when:
		browser.navigatorFactory

		then:
		thrown InvalidGebConfiguration
	}

	def "error when invalid factory closure return type"() {
		given:
		raw.navigatorFactory = { 1 }

		when:
		browser.navigatorFactory

		then:
		thrown InvalidGebConfiguration
	}

	def "error when inner factory is invalid"() {
		given:
		raw.innerNavigatorFactory = 1

		when:
		browser.navigatorFactory

		then:
		thrown InvalidGebConfiguration
	}

	def "can explicitly set"() {
		when:
		raw.innerNavigatorFactory = new InnerNavigatorFactory() {
			Navigator createNavigator(Browser browser, List<WebElement> elements) {
				new CustomNavigator(browser, elements)
			}
		}

		then:
		$("p").class == CustomNavigator
	}

}

class CustomNavigator extends NonEmptyNavigator {
	CustomNavigator(Browser browser, Collection<? extends WebElement> contextElements) {
		super(browser, contextElements)
	}
}
