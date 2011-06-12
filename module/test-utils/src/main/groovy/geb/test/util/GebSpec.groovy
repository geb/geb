/* 
 * Copyright 2011 the original author or authors.
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
package geb.test.util

import geb.*
import geb.report.*
import spock.lang.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver

import org.junit.Rule
import org.junit.rules.TestName

class GebSpec extends Specification {

	String gebConfEnv = null
	String gebConfScript = null
	
	@Shared Browser _browser

	// Ridiculous name to avoid name clashes
	@Rule _gebReportingSpecTestName = new TestName()
	def _gebReportingPerTestCounter = 0
	@Shared _gebReportingSpecTestCounter = 0
	@Shared _getReportingSpecReporter = null
	
	Configuration createConf() {
		def conf = new ConfigurationLoader(gebConfEnv).getConf(gebConfScript)
		conf
	}
	
	Browser createBrowser() {
		def browser = new Browser(createConf())
		if (browser.driver instanceof HtmlUnitDriver) {
			browser.driver.javascriptEnabled = true
		}
		browser
	}

	Browser getBrowser() {
		if (_browser == null) {
			_browser = createBrowser()
		}
		_browser
	}

	void resetBrowser() {
		if (_browser?.config.autoClearCookies) {
			_browser.clearCookiesQuietly()
		}
		_browser = null
	}

	void report(String label) {
		def name = "${++_gebReportingSpecTestCounter}-${++_gebReportingPerTestCounter}-${_gebReportingSpecTestName.methodName}"
		if (label) {
			name += "-$label"
		}
		
		browser.report(name)
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

	private isSpecStepwise() {
		this.class.getAnnotation(Stepwise) != null
	}
	
	def cleanup() {
		if (!isSpecStepwise()) resetBrowser()
		report("end")
	}
	
	def cleanupSpec() {
		if (isSpecStepwise()) resetBrowser()
	}
}