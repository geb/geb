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
package geb.junit4

import geb.Browser
import org.openqa.selenium.WebDriver
import org.junit.After

class GebTest extends GroovyTestCase {

	private _browser
	
	Browser getBrowser() {
		if (_browser == null) {
			_browser = createBrowser()
		}
		_browser
	}

	void resetBrowser() {
		_browser = null
	}

	def methodMissing(String name, args) {
		getBrowser()."$name"(*args)
	}

	def propertyMissing(String name) {
		getBrowser()."$name"
	}
	
	def propertyMissing(String name, value) {
		getBrowser()."$name" = value
	}

	Browser createBrowser() {
		def driver = createDriver()
		def baseUrl = getBaseUrl()
		
		driver ? new Browser(driver, baseUrl) : new Browser(baseUrl)
	}
	
	WebDriver createDriver() {
		null // use Browser default
	}

	String getBaseUrl() {
		null
	}
	
	@After
	void clearBrowserCookies() {
		browser.clearCookiesQuietly()
	}

}