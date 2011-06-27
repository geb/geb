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

import geb.navigator.Navigator
import geb.Page
import geb.Module

import geb.Configuration
import geb.waiting.Wait

import geb.error.InvalidPageContent
import geb.error.RequiredPageValueNotPresent

class PageContentTemplate {

	final Configuration config
	final Navigable owner
	final String name
	final Map params
	final Closure factory
	
	private cache = [:]
	
	PageContentTemplate(Configuration config, Navigable owner, String name, Map params, Closure factory) {
		this.config = config
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
	
	boolean isCaching() {
		params.cache
	}
	
	/**
	 * Can return Class or List&lt;Class&gt;
	 */
	def getTo() {
		params.to
	}
	
	/**
	 * Returns the Wait to use to wait for this content to materialise, or null if there should be no waiting.
	 */
	Wait getWait() {
		def waitingParam = params.wait
		if (waitingParam == true) {
			config.getDefaultWait()
		} else if (waitingParam instanceof CharSequence) {
			config.getWaitPreset(waitingParam.toString())
		} else if (waitingParam instanceof Number && waitingParam > 0) {
			config.getWait(waitingParam.doubleValue())
		} else if (waitingParam instanceof Collection) {
			if (waitingParam.size() == 2) {
				def timeout = waitingParam[0]
				def retryInterval = waitingParam[1]
				
				if (timeout instanceof Number && retryInterval instanceof Number) {
					new Wait(timeout.doubleValue(), retryInterval.doubleValue())
				} else {
					throw new IllegalArgumentException("'wait' param for content template ${this} has illegal value '$waitingParam' (collection elements must be numbers)")
				}
			} else {
				throw new IllegalArgumentException("'wait' param for content template ${this} has illegal value '$waitingParam' (collection must have 2 elements)")
			}
		} else {
			null
		}
	}
	
	Page getPage() {
		owner instanceof Page ? owner : owner.getPage()
	}
	
	def get(Object[] args) {
		caching ? fromCache(*args) : create(*args)
	}

	private create(Object[] args) {
		def createAction = {
			def factoryReturn = invokeFactory(*args)
			def creation = wrapFactoryReturn(factoryReturn, *args)
			if (required) {
				if (creation != null && creation instanceof PageContent) {
					creation.require()
				} else if (creation == null) {
					throw new RequiredPageValueNotPresent(this, *args)
				}
			}
			creation
		}
		
		def wait = getWait()
		if (wait) {
			wait.waitFor(createAction)
		} else {
			createAction()
		}
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
		if (factoryReturn instanceof Navigator) {
			pageContent = new SimplePageContent()
			pageContent.init(this, factoryReturn, *args)
			pageContent
		} else if (factoryReturn instanceof SimplePageContent) {
			pageContent = new SimplePageContent()
			pageContent.init(this, factoryReturn.$(), *args)
			pageContent
		} else if (factoryReturn instanceof Module) {
			factoryReturn
		} else {
			// Is some kind of value, like the text content of a node
			factoryReturn
		}
	}
	
}