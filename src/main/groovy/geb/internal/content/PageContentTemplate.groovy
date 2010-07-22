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
package geb.internal.content

import be.roam.hue.doj.Doj
import geb.Page
import geb.Module

import geb.error.InvalidPageContent

class PageContentTemplate {

	final Content owner
	final String name
	final Map params
	final Closure factory
	
	private cache = [:]
	
	PageContentTemplate(Content owner, String name, Map params, Closure factory) {
		this.owner = owner
		this.name = name
		this.params = params
		this.factory = factory
	}
	
	String toString() {
		"$name - $owner"
	}
	
	boolean isRequired() {
		params.required
	}
	
	boolean isDynamic() {
		params.dynamic
	}
	
	Class getto() {
		params.to
	}
	
	Page getPage() {
		owner instanceof Page ? owner : owner.getPage()
	}
	
	def get(Object[] args) {
		dynamic ? create(*args) : fromCache(*args)
	}

	private create(Object[] args) {
		def factoryReturn = invokeFactory(*args)
		def pageContent = wrapFactoryReturn(factoryReturn, *args)
		if (required) {
			pageContent.require()
		}
		pageContent
	}
	
	private fromCache(Object[] args) {
		def argsHash = Arrays.deepHashCode(args)
		if (!cache.containsKey(argsHash)) {
			cache[argsHash] = create(*args)
		}
		cache[argsHash]
	}
	
	private invokeFactory(Object[] args) {
		factory.delegate = createFactoryDelegate(args)
		factory.resolveStrategy = Closure.DELEGATE_FIRST
		factory(*args)
	}
	
	private createFactoryDelegate(Object[] args) {
		new PageContentTemplateFactoryDelegate(this, args)
	}

	private wrapFactoryReturn(factoryReturn, Object[] args) {
		def pageContent
		if (factoryReturn instanceof Doj) {
			pageContent = new SimplePageContent()
			pageContent.init(this, factoryReturn, *args)
			pageContent
		} else if (factoryReturn instanceof SimplePageContent) {
			pageContent = new SimplePageContent()
			pageContent.init(this, factoryReturn.navigator, *args)
			pageContent
		} else if (factoryReturn instanceof Module) {
			factoryReturn
		} else {
			throw new InvalidPageContent("definition of content '${toString()}' did not return page content")
		}
	}
	
}