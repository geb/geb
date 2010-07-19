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
package geb.page

import be.roam.hue.doj.Doj
import geb.Page
import geb.Module
import geb.page.error.InvalidPageContent

class PageContentTemplate {
	
	static DISALLOWED_MODULE_PARAMS = ['template', 'container', 'args']
	
	def name
	def params
	def definition
	
	def container
	def cache = [:]

	private definitionDelegate
	
	String toString() {
		"$name - $container"
	}
	
	boolean isRequired() {
		params.required
	}
	
	boolean isDynamic() {
		params.dynamic
	}
	
	def getToPage() {
		params.toPage
	}
	
	def getPage() {
		container instanceof Page ? container : container.getPage()
	}
	
	def get(Object[] args) {
		if (container == null) {
			throw new IllegalStateException("${toString()} has a null container")
		}
		dynamic ? create(*args) : fromCache(*args)
	}

	private create(Object[] args) {
		def definitionResult = invokeDefinition(*args)
		def content = wrapDefinitionResult(definitionResult, *args)
		if (required) {
			content.require()
		}
		content
	}
	
	private fromCache(Object[] args) {
		def argsHash = Arrays.deepHashCode(args)
		if (!cache.containsKey(argsHash)) {
			cache[argsHash] = create(*args)
		}
		cache[argsHash]
	}
	
	private invokeDefinition(Object[] args) {
		definition.delegate = createDefinitionDelegate(args)
		definition.resolveStrategy = Closure.DELEGATE_FIRST
		definition(*args)
	}
	
	private createDefinitionDelegate(Object[] args) {
		new PageContentTemplateDefinitionDelegate(template: this, args: args)
	}

	private wrapDefinitionResult(definitionResult, Object[] args) {
		if (definitionResult instanceof Doj || definitionResult instanceof Component) {
			new Component(template: this, container: definitionResult, args: args)
		} else if (definitionResult instanceof Module) {
			definitionResult
		} else {
			throw new InvalidPageContent("definition of content '${toString()}' did not return page content")
		}
	}
	
}

class PageContentTemplateDefinitionDelegate {

	private template
	private args
	
	def find(Object[] args) {
		template.container.find(*args)
	}
	
	def methodMissing(String name, args) {
		template.container."$name"(*args)
	}
	
	def propertyMissing(String name) {
		template.container."$name"
	}

	def propertyMissing(String name, value) {
		template.container."$name" = value
	}

	def module(Class moduleClass) {
		module(null, moduleClass, null)
	}

	def module(Map params, Class moduleClass) {
		module(params, moduleClass, null)
	}

	def module(Class moduleClass, container) {
		module(null, moduleClass, container)
	}

	def module(Map params, Class moduleClass, container) {
		def moduleContainer = null
		if (container == null) {
			moduleContainer = template.container
		} else {
			if (container instanceof Doj) {
				moduleContainer = new Component(template: template, container: container, args: null)
			} else if (container instanceof PageContent) {
				moduleContainer = container.container
			} else {
				throw new InvalidPageContent("base '$container' for module for content template '${template}' is not page content")
			}
		} 
		

		def ivars = [template: template, container: moduleContainer, args: args]
		if (params) {
			def disallowed = PageContentTemplate.DISALLOWED_MODULE_PARAMS
			if (params.any { it.key in disallowed }) {
				throw new InvalidPageContent("params for module $moduleClass with name ${template.name} contains one or more disallowd params (${disallowd.join(', ')})")
			}
			ivars.putAll(params)
		}
		
		moduleClass.newInstance(*:ivars)
	}

}