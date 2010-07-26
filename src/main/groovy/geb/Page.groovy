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

import geb.error.UndefinedPageContentException
import geb.internal.content.PageContentTemplateBuilder
import geb.internal.content.Content
import be.roam.hue.doj.Doj

class Page implements Content {

	static at = null
	static url = ""
	
	Driver driver
	
	protected _contentTemplates
	
	Page() {
		_contentTemplates = PageContentTemplateBuilder.build(this, 'content', this.class, Page)
	}
	
	void setDriver(Driver driver) {
		this.driver = driver
	}
	
	Doj getNavigator() {
		Doj.on(driver.client.currentWindow?.enclosedPage)
	}
	
	String toString() {
		this.class.simpleName
	}
	
	/**
	 * To be implemented by page subclasses to check that the current
	 * actual page is the page for this page object.
	 */
	def verifyAt() {
		def verifier = this.class.at?.clone()
		if (verifier) {
			verifier.delegate = this
			verifier.resolveStrategy = Closure.DELEGATE_FIRST
			verifier()
		} else {
			true
		}
	}
	
	def methodMissing(String name, args) {
		_getContent(name, *args)
	}

	def propertyMissing(String name) {
		_getContent(name)
	}

	Doj $() {
		navigator
	}
	
	Doj $(int index) {
		navigator.get(index)
	}

	Doj $(String name) {
		navigator.get(name)
	}
	
	Doj $(String selector, int index) {
		navigator.get(selector, index)
	}
	
	private _getContent(String name, Object[] args) {
		def contentTemplate = _contentTemplates[name]
		if (contentTemplate) {
			contentTemplate.get(*args)
		} else {
			throw new UndefinedPageContentException(this, name)
		}
	}
	
	def to(Map params, Object[] args) {
		def path = convertToPath(*args)
		if (path == null) {
			path = ""
		}
		driver.go(params, getPageUrl(path))
		driver.page(this)
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
		driver.client.currentWindow?.enclosedPage?.titleText
	}
}