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

import geb.Configuration
import geb.Module
import geb.Page
import geb.error.RequiredPageValueNotPresent
import geb.navigator.Navigator
import geb.navigator.factory.NavigatorFactory
import geb.waiting.WaitTimeoutException

class PageContentTemplate {

	final Configuration config
	final PageContentContainer owner
	final String name
	final PageContentTemplateParams params
	final Closure factory
	final NavigatorFactory navigatorFactory

	private cache = [:]

	PageContentTemplate(Configuration config, PageContentContainer owner, String name, Map<String, ?> params, Closure factory, NavigatorFactory navigatorFactory) {
		this.config = config
		this.owner = owner
		this.name = name
		this.params = new PageContentTemplateParams(this, params)
		this.factory = factory
		this.navigatorFactory = navigatorFactory
	}

	String toString() {
		"$name - $owner"
	}

	Page getPage() {
		owner instanceof Page ? owner : owner.getPage()
	}

	def get(Object[] args) {
		params.cache ? fromCache(* args) : create(* args)
	}

	private create(Object[] args) {
		def createAction = {
			def factoryReturn = invokeFactory(* args)
			def creation = wrapFactoryReturn(factoryReturn, * args)
			if (params.required) {
				if (creation != null && creation instanceof TemplateDerivedPageContent) {
					creation.require()
				} else if (creation == null) {
					throw new RequiredPageValueNotPresent(this, * args)
				}
			}
			creation
		}

		def wait = config.getWaitForParam(params.wait)
		if (wait) {
			try {
				wait.waitFor(createAction)
			} catch (WaitTimeoutException e) {
				if (params.required) {
					throw e
				}
				e.lastEvaluationValue
			}
		} else {
			createAction()
		}
	}

	private fromCache(Object[] args) {
		def argsHash = Arrays.deepHashCode(args)
		if (!cache.containsKey(argsHash)) {
			cache[argsHash] = create(* args)
		}
		cache[argsHash]
	}

	private invokeFactory(Object[] args) {
		factory.delegate = createFactoryDelegate(args)
		factory.resolveStrategy = Closure.DELEGATE_FIRST
		factory(* args)
	}

	private createFactoryDelegate(Object[] args) {
		new PageContentTemplateFactoryDelegate(this, args)
	}

	private wrapFactoryReturn(factoryReturn, Object[] args) {
		if (Navigator.isInstance(factoryReturn) && !Module.isInstance(factoryReturn)) {
			def pageContent = new SimplePageContent()
			pageContent.init(this, factoryReturn, * args)
			pageContent
		} else {
			factoryReturn
		}
	}

}