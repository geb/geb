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

import geb.Module
import geb.error.InvalidPageContent
import geb.navigator.Navigator
import geb.navigator.factory.NavigatorFactory
import org.openqa.selenium.WebElement

class PageContentTemplateFactoryDelegate {

	static DISALLOWED_MODULE_PARAMS = ['_template', '_navigator', '_args']

	private PageContentTemplate template
	private Object[] args

	@Delegate
	private final NavigableSupport navigableSupport

	PageContentTemplateFactoryDelegate(PageContentTemplate template, Object[] args) {
		this.template = template
		this.navigableSupport = new NavigableSupport(template.navigatorFactory)
		this.args = args
	}

	def methodMissing(String name, args) {
		template.owner."$name"(* args)
	}

	def propertyMissing(String name) {
		template.owner."$name"
	}

	def module(Class<? extends Module> moduleClass) {
		module(null, moduleClass, null)
	}

	def module(Map params, Class<? extends Module> moduleClass) {
		module(params, moduleClass, null)
	}

	def module(Class<? extends Module> moduleClass, container) {
		module(null, moduleClass, container)
	}

	def module(Map params, Class<? extends Module> moduleClass, Navigator base) {
		if (params == null) {
			params = [:]
		}

		if (!(moduleClass in Module)) {
			throw new InvalidPageContent("class '${moduleClass}' should extend from ${Module} to be allowed to be a part of a module definition with name '${template.name}'")
		}

		// Make sure they haven't used params that map to our internal ivars
		if (params.any { it.key in DISALLOWED_MODULE_PARAMS }) {
			def disallowed = DISALLOWED_MODULE_PARAMS.join(', ')
			throw new InvalidPageContent("params for module $moduleClass with name ${template.name} contains one or more disallowed params (${disallowed})")
		}

		def baseNavigatorFactory = base != null ? template.navigatorFactory.relativeTo(base) : template.navigatorFactory

		NavigatorFactory moduleBaseNavigatorFactory = ModuleBaseCalculator.calculate(moduleClass, baseNavigatorFactory, params)

		def module = moduleClass.newInstance()
		module.init(template, moduleBaseNavigatorFactory, * args)
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

	Navigator $() {
		navigableSupport.$()
	}

	Navigator $(int index) {
		navigableSupport.$(index)
	}

	Navigator $(Range<Integer> range) {
		navigableSupport.$(range)
	}

	Navigator $(String selector) {
		navigableSupport.$(selector)
	}

	Navigator $(String selector, int index) {
		navigableSupport.$(selector, index)
	}

	Navigator $(String selector, Range<Integer> range) {
		navigableSupport.$(selector, range)
	}

	Navigator $(Map<String, Object> attributes) {
		navigableSupport.$(attributes)
	}

	Navigator $(Map<String, Object> attributes, int index) {
		navigableSupport.$(attributes, index)
	}

	Navigator $(Map<String, Object> attributes, Range<Integer> range) {
		navigableSupport.$(attributes, range)
	}

	Navigator $(Map<String, Object> attributes, String selector) {
		navigableSupport.$(attributes, selector)
	}

	Navigator $(Map<String, Object> attributes, String selector, int index) {
		navigableSupport.$(attributes, selector, index)
	}

	Navigator $(Map<String, Object> attributes, String selector, Range<Integer> range) {
		navigableSupport.$(attributes, selector, range)
	}

	Navigator $(Navigator[] navigators) {
		navigableSupport.$(navigators)
	}

	Navigator $(WebElement[] elements) {
		navigableSupport.$(elements)
	}
}
