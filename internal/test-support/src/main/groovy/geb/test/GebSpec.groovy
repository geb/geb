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
package geb.test

import geb.Browser
import geb.Configuration
import geb.ConfigurationLoader
import geb.report.ReporterSupport
import org.junit.Rule
import org.junit.rules.TestName
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

class GebSpec extends Specification {

	String gebConfEnv = null
	String gebConfScript = null

	@Shared Browser _browser
	@Shared boolean takeReports = true

	// Ridiculous name to avoid name clashes
	@Rule TestName _gebReportingSpecTestName
	def _gebReportingPerTestCounter = 0
	@Shared _gebReportingSpecTestCounter = 0

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
		if (_browser?.config?.autoClearCookies) {
			_browser.clearCookiesQuietly()
		}
		_browser = null
	}

	def setupSpec() {
		reportGroup getClass()
		cleanReportGroupDir()
	}

	def setup() {
		reportGroup getClass()
	}

	void report(String label = "") {
		if (takeReports) {
			browser.report(ReporterSupport.toTestReportLabel(_gebReportingSpecTestCounter++, _gebReportingPerTestCounter++, _gebReportingSpecTestName.methodName, label))
		}
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

	private isSpecStepwise() {
		this.class.getAnnotation(Stepwise) != null
	}

	def cleanup() {
		report("end")
		if (!isSpecStepwise()) {
			resetBrowser()
		}
	}

	def cleanupSpec() {
		if (isSpecStepwise()) {
			resetBrowser()
		}
	}
}