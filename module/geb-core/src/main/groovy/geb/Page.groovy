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
package geb

import geb.js.*
import geb.internal.*
import geb.navigator.Navigator
import org.openqa.selenium.WebDriver
import geb.textmatching.TextMatchingSupport

class Page {

	static at = null
	static url = ""
	
	private Browser browser
	
	@Delegate private NavigableSupport navigableSupport
	@Delegate private final TextMatchingSupport textMatchingSupport = new TextMatchingSupport()
	@Delegate private final WaitingSupport _waitingSupport = new WaitingSupport()
	@Delegate private final AlertAndConfirmSupport  _alertAndConfirmSupport = new AlertAndConfirmSupport({ this.getJs() }) 
	
	Page init(Browser browser) {
		this.browser = browser
		def contentTemplates = PageContentTemplateBuilder.build(this, 'content', this.class, Page)
		navigableSupport = new FactoryNavigableSupport(this, contentTemplates, browser, { return Navigator.on(browser) })
		this
	}
	
	Browser getBrowser() {
		browser
	}

	WebDriver getDriver() {
		browser.driver
	}
	
	String toString() {
		this.class.simpleName
	}
	
	boolean verifyAt() {
		def verifier = this.class.at?.clone()
		if (verifier) {
			verifier.delegate = this
			verifier.resolveStrategy = Closure.DELEGATE_FIRST
			verifier()
		} else {
			true
		}
	}
	
	boolean verifyAtSafely() {
		try {
			verifyAt()
		} catch (AssertionError e) {
			false
		}
	}
	
	def to(Map params, Object[] args) {
		def path = convertToPath(*args)
		if (path == null) {
			path = ""
		}
		browser.go(params, getPageUrl(path))
		browser.page(this)
	}
	
	def getPageUrl() {
		this.class.url
	}
	
	def getPageUrl(String path) {
		def pageUrl = getPageUrl()
		path ? (pageUrl ? "$pageUrl/$path" : path) : pageUrl
	}
	
	def convertToPath(Object[] args) {
		args ? args*.toString().join('/') : ""
	}	
	
	def getTitle() {
		browser.driver.title
	}
	
	JavascriptInterface getJs() {
		browser.js
	}
	
	def onLoad(Page previousPage) {
		
	}
	
	def onUnload(Page nextPage) {
		
	}
}