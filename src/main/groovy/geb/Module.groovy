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

import geb.internal.content.*
import geb.error.*

class Module extends TemplateDerivedPageContent {

	static base = null
	
	private _contentTemplates
	
	Module() {
		_contentTemplates = PageContentTemplateBuilder.build(this, 'content', this.class, Module)
	}

	def methodMissing(String name, args) {
		_getContent(name, *args)
	}
	
	/**
	 * Groovy will delegate property access to a get(String) method,
	 * so we need to override to get defined content. For selecting content,
	 * we have to use find() to start navigating.
	 * 
	 * The doj replacement should not have a get(String) method for this reason.
	 */
	def get(String name) {
		_getContent(name)
	}

	private _getContent(String name, Object[] args) {
		def contentTemplate = _contentTemplates[name]
		if (contentTemplate) {
			contentTemplate.get(*args)
		} else {
			throw new UndefinedPageContentException(this, name)
		}
	}
}

