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

import geb.page.error.UndefinedPageContentException
import geb.page.PageContentTemplateBuilder

class Page {

	static at = null
	static url = ""
	
	Driver driver
	
	protected contentTemplates
	
	Page() {
		contentTemplates = buildContentTemplates()
	}
	
	protected buildContentTemplates() {
		PageContentTemplateBuilder.build(this, 'content', this.class, Page)
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
		if (hasContent(name)) {
			getContent(name, *args)
		} else {
			geb."$name"(*args)
		}
	}

	def propertyMissing(String name) {
		if (hasContent(name)) {
			getContent(name)
		} else {
			geb."$name"
		}
	}
	
	def propertyMissing(String name, value) {
		geb."$name" = value
	}
	
	protected getGeb() {
		driver.geb
	}

	def getContent(String name, Object[] args) {
		def contentTemplate = contentTemplates[name]
		if (contentTemplate) {
			contentTemplate.get(*args)
		} else {
			throw new UndefinedPageContentException(this, name)
		}
	}
	
	boolean hasContent(String name) {
		contentTemplates.containsKey(name)
	}
	
	def to(Map params, Object[] args) {
		def path = convertToPath(args)
		if (path == null) {
			path = ""
		}
		driver.go(params, getPageUrl(path))
	}
	
	def getPageUrl() {
		this.class.url
	}
	
	def getPageUrl(String path) {
		def pageUrl = getPageUrl()
		path ? "$pageUrl/$path" : pageUrl
	}
	
	def convertToPath(Object[] args) {
		args ? args*.toString().join('/') : ""
	}	
	
	def getPageTitle() {
		geb.page.titleText
	}
}