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

import geb.page.*
import geb.page.error.*
import be.roam.hue.doj.Doj

class Module extends PageContent {

	static absolute = false
	static locator = null
	
	private actualContainer
	private _contentTemplates
	
/*	Module() {
		contentTemplates = buildContentTemplates()
	}
*/	
	def getContentTemplates() {
		if (_contentTemplates == null) {
			_contentTemplates = PageContentTemplateBuilder.build(this, 'content', this.class, Module)
		}
		_contentTemplates
	}
	
	def getBase() {
		if (!actualContainer) {
			actualContainer = calculateActualContainer()
		}
		actualContainer
	}
	
	boolean isHasAbsoluteLocation() {
		this.class.absolute != null
	}
	
	private getLocatorDefinition() {
		def locator  = this.class.locator
		if (locator == null) {
			null
		} else {
			if (!(locator instanceof Closure)) {
				throw new InvalidPageContent("The 'locator' static property of ${toString()} should be a Closure")
			}
			locator
		}
	}
	
	private calculateActualContainer() {
		def containerBase
		if (hasAbsoluteLocation) {
			containerBase = getPage()
		} else {
			containerBase = super.container
		}
		
		def locator = getLocatorDefinition()
		if (locator) {
			def locatorClone = locator.clone()
			locatorClone.find = { Object[] args -> 
				containerBase.find(*args) 
			}
			
			def result = locatorClone()
			if (result instanceof Doj || result instanceof PageContent) {
				result
			} else {
				throw new InvalidPageContent("The 'locator' static property of ${toString()} should return page content")
			}
		} else {
			containerBase
		}
	}
	
	def methodMissing(String name, args) {
		if (hasContent(name)) {
			getContent(name, *args)
		} else {
			base."$name"(*args)
		}
	}

	def propertyMissing(String name) {
		if (hasContent(name)) {
			getContent(name)
		} else {
			base."$name"
		}
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
	
}
