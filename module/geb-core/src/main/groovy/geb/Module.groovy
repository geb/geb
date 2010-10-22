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

import geb.js.*
import geb.internal.*
import geb.navigator.Navigator
import geb.navigator.AttributeAccessingMetaClass

class Module extends TemplateDerivedPageContent {

	static base = null
	
	static {
		def mc = new AttributeAccessingMetaClass(new ExpandoMetaClass(Module))
		mc.initialize()
		Module.metaClass = mc
	}
	
	@Delegate private NavigableSupport navigableSupport
	@Delegate private TextMatchingSupport textMatchingSupport = new TextMatchingSupport()
	@Delegate private final WaitingSupport _waitingSupport = new WaitingSupport()
	@Delegate private final AlertAndConfirmSupport  _alertAndConfirmSupport = new AlertAndConfirmSupport({ this.getJs() })
	
	void init(PageContentTemplate template, Navigator navigator, Object[] args) {
		def contentTemplates = PageContentTemplateBuilder.build(this, 'content', this.class, Module)
		navigableSupport = new NavigableSupport(this, contentTemplates, { return navigator }) 
		super.init(template, navigator, *args)
	}

	JavascriptInterface getJs() {
		page.js
	}

}