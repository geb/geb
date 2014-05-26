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
package geb.js

import geb.Browser
import geb.error.GebException
import org.openqa.selenium.JavascriptExecutor

class JavascriptInterface {

	final Browser browser

	JavascriptInterface(Browser browser) {
		this.browser = browser

	}

	private _execjs(String script, Object[] args) {
		def driver = browser.driver
		if (!(driver instanceof JavascriptExecutor)) {
			throw new GebException("driver '$driver' can not execute javascript")
		}

		// Temporarily disabled due to issues with 2.0rc3
		//if (!driver.javascriptEnabled) {
		//	throw new GebException("javascript is disabled for driver '$driver'")
		//}

		browser.driver.executeScript(script, * args)
	}

	def propertyMissing(String name) {
		_execjs("return $name;")
	}

	def methodMissing(String name, args) {
		_execjs("return ${name}.apply(window, arguments)", * args)
	}

	def exec(Object[] args) {
		if (args.size() == 0) {
			throw new IllegalArgumentException("there must be a least one argument")
		}

		def script
		def jsArgs
		if (args.size() == 1) {
			script = args[0]
			jsArgs = []
		} else {
			script = args[args.size() - 1]
			jsArgs = args[0..(args.size() - 2)]
		}

		if (!(script instanceof CharSequence)) {
			throw new IllegalArgumentException("The last argument to the js function must be string-like")
		}

		_execjs(script.toString(), * jsArgs)
	}

}