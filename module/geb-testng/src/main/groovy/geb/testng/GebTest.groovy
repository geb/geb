/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.testng

import geb.Browser
import geb.Configuration
import geb.ConfigurationLoader
import org.testng.annotations.AfterMethod

class GebTest {

	String gebConfEnv = null
	String gebConfScript = null

	private Browser _browser

	Configuration createConf() {
		new ConfigurationLoader(gebConfEnv).getConf(gebConfScript)
	}

	Browser createBrowser() {
		new Browser(createConf())
	}

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
		getBrowser()."$name"(* args)
	}

	def propertyMissing(String name) {
		getBrowser()."$name"
	}

	def propertyMissing(String name, value) {
		getBrowser()."$name" = value
	}

	@AfterMethod
	void clearBrowserCookies() {
		if (_browser?.config?.autoClearCookies) {
			_browser?.clearCookiesQuietly()
		}
	}

}
