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
import geb.Configuration

class PageContentTemplateBuilder {

	static PARAM_DEFAULTS = [
		required: true,
		cache: false,
		to: null
	]

	Configuration config
	Navigable container
	
	final templates = [:]
	
	def methodMissing(String name, args) {
		def definition
		def params
		
		if (args.size() == 0) {
			throw new InvalidPageContent("Definition of page content template '$name' of '$container' contains no definition")
		} else if (args.size() == 1) {
			if ((args[0] instanceof Map)) {
				params = args[0]
			} else {
				definition = args[0]
			}
		} else if (args.size() == 2) {
			params = args[0]
			definition = args[1]
		}
		
		if (params != null && !(params instanceof Map)) {
				throwBadInvocationError(name, args)
		}
		if (definition != null) {
			if (!(definition instanceof Closure)) {
				throwBadInvocationError(name, args)
			}
		} else {
			if (params?.aliases == null) {
				throwBadInvocationError(name, args)
			}
		}
		
		def template = create(name, params, definition)
		templates[name] = template
		template
	}
	
	private throwBadInvocationError(name, args) {
		throw new InvalidPageContent("Definition of page component template '$name' of '$container' is invalid, params must be either a Closure, or Map and Closure (args were: ${args*.class})")
	}
	
	private create(name, params, definition) {
		def aliasedName = params?.aliases
		if (aliasedName) {
			if (!templates[aliasedName]) {
				throw new InvalidPageContent("Definition of page component template '$name' of '$container' aliases an unknown element '${params.aliases}'")
			}
			templates[aliasedName]
		} else {
			new PageContentTemplate(config, container, name, mergeWithDefaultParams(params), definition)
		}
	}

	protected mergeWithDefaultParams(Map params) {
		params ? PARAM_DEFAULTS + params : PARAM_DEFAULTS.clone()
	}

	static build(Configuration config, Navigable container, List<Closure> templatesDefinitions) {
		def builder = newInstance(config: config, container: container)
		for (templatesDefinition in templatesDefinitions) {
			templatesDefinition.delegate = builder
			templatesDefinition()
		}
		builder.templates
	}
	
	static build(Configuration config, Navigable container, String property, Class startAt, Class stopAt = Object) {
		if (!stopAt.isAssignableFrom(startAt)) {
			throw new IllegalArgumentException("$startAt is not a subclass of $stopAt")
		}
		
		def templatesDefinitions = []
		def clazz = startAt
		
		while (clazz != stopAt) {
			def templatesDefinition
			try {
				templatesDefinition = clazz[property]
			} catch (MissingPropertyException e) {
				// swallow
			}
			
			if (templatesDefinition) {
				if (!(templatesDefinition instanceof Closure)) {
					throw new IllegalArgumentException("'$property' static property of class $clazz should be a Closure")
				}
				templatesDefinitions << templatesDefinition.clone()
			}
			
			clazz = clazz.superclass
		}
		
		PageContentTemplateBuilder.build(config, container, templatesDefinitions.reverse())
	}
}