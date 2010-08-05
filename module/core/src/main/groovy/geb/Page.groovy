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

import geb.internal.*
import geb.navigator.Navigator

class Page {

	static at = null
	static url = ""
	
	Browser browser
	
	@Delegate private NavigableSupport navigableSupport
	@Delegate private TextMatchingSupport textMatchingSupport = new TextMatchingSupport()
	
	Page() {
		def contentTemplates = PageContentTemplateBuilder.build(this, 'content', this.class, Page)
		navigableSupport = new NavigableSupport(this, contentTemplates) { Navigator.on(browser.driver) }
	}
	
	void setBrowser(Browser browser) {
		this.browser = browser
	}

	String toString() {
		this.class.simpleName
	}
	
	/**
	 * To be implemented by page subclasses to check that the current
	 * actual page is the page for this page object.
	 */
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
}