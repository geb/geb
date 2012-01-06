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
package geb.content

import geb.error.InvalidPageContent
import geb.navigator.Navigator

class PageContentTemplateFactoryDelegate {

	static DISALLOWED_MODULE_PARAMS = ['_template', '_navigator', '_args']
	
	private template
	private args
	
	private PageContentTemplateFactoryDelegate(PageContentTemplate template, Object[] args) {
		this.template = template
		this.args = args
	}
	
	def methodMissing(String name, args) {
		template.owner."$name"(*args)
	}
	
	def propertyMissing(String name) {
		template.owner."$name"
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

	def module(Map params, Class moduleClass, base) {
		if (params == null) {
			params = [:]
		}
		
		// Make sure they haven't used params that map to our internal ivars
		if (params.any { it.key in DISALLOWED_MODULE_PARAMS }) {
			def disallowed = DISALLOWED_MODULE_PARAMS.join(', ')
			throw new InvalidPageContent("params for module $moduleClass with name ${template.name} contains one or more disallowed params (${disallowed})")
		}
		
		def startingBase 
		if (base == null) {
			startingBase = template.owner.$()
		} else if (base instanceof Navigator) {
			startingBase = base
		} else if (base instanceof Navigable) {
			startingBase = base.$()
		} else {
			throw new InvalidPageContent("The base '$base' given to module $moduleClass for template $template is not page content")
		}
		
		Navigator moduleBase = ModuleBaseCalculator.calculate(moduleClass, startingBase, params)
		
		def module = moduleClass.newInstance()
		module.init(template, moduleBase, *args)
		params.each { name, value ->
			// TODO - catch MPE and provide better error message
			module."$name" = value
		}
		
		module
	}

	/**
	 * Returns a list of module instances, where the nth instance will use the
	 * nth navigator as its base.
	 */
	def moduleList(Map params, Class moduleClass, Navigator navigator, index = null) {
		if (index != null) {
			def modules = index.collect { module params, moduleClass, navigator[it] }
			modules.size() > 1 ? modules : modules.first()
		} else {
			(0..<navigator.size()).collect { module params, moduleClass, navigator[it] }
		}
	}

	def moduleList(Class moduleClass, Navigator navigator, index = null) {
		moduleList(null, moduleClass, navigator, index)
	}
}
