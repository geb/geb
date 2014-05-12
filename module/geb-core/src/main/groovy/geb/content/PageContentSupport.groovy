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

import geb.error.UndefinedPageContentException
import geb.error.UnresolvablePropertyException
import geb.navigator.Navigator
import geb.navigator.factory.NavigatorFactory

class PageContentSupport {

	private final PageContentContainer owner
	private final Map<String, PageContentTemplate> contentTemplates
	private final NavigatorFactory navigatorFactory
	private final Navigator navigator

	PageContentSupport(PageContentContainer owner, Map<String, PageContentTemplate> contentTemplates, NavigatorFactory navigatorFactory, Navigator navigator = null) {
		this.owner = owner
		this.contentTemplates = contentTemplates ?: [:]
		this.navigatorFactory = navigatorFactory
		this.navigator = navigator
	}

	private Navigator getNavigator() {
		navigator ?: navigatorFactory.base
	}

	def getContent(String name, Object[] args) {
		def contentTemplate = contentTemplates[name]
		if (contentTemplate) {
			contentTemplate.get(* args)
		} else {
			throw new UndefinedPageContentException(this, name)
		}
	}

	def methodMissing(String name, args) {
		try {
			getContent(name, * args)
		} catch (UndefinedPageContentException e1) {
			getNavigator().methodMissing(name, args)
		}
	}

	def propertyMissing(String name) {
		try {
			getContent(name)
		} catch (UndefinedPageContentException e1) {
			try {
				getNavigator().propertyMissing(name)
			} catch (MissingPropertyException e2) {
				throw new UnresolvablePropertyException(owner, name, "Unable to resolve $name as content for ${owner}, or as a property on its Navigator context. Is $name a class you forgot to import?")
			}
		}
	}

	def propertyMissing(String name, val) {
		try {
			getContent(name).value(val)
		} catch (UndefinedPageContentException e) {
			try {
				getNavigator().propertyMissing(name, val)
			} catch (MissingPropertyException e1) {
				throw new UnresolvablePropertyException(owner, name, "Unable to resolve $name as a property to set on ${owner}'s Navigator context")
			}
		}
	}
}