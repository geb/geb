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
import geb.navigator.factory.NavigatorFactory
import geb.textmatching.TextMatchingSupport
import org.openqa.selenium.WebElement

class ModuleBaseDefinitionDelegate {

	private params

	@Delegate
	private NavigableSupport navigableSupport
	@Delegate
	private TextMatchingSupport textMatchingSupport = new TextMatchingSupport()

	ModuleBaseDefinitionDelegate(NavigatorFactory navigatorFactory, Map params) {
		this.params = params
		navigableSupport = new NavigableSupport(navigatorFactory)
	}

	def propertyMissing(String name) {
		if (params.containsKey(name)) {
			params[name]
		} else {
			throw new MissingPropertyException(name, ModuleBaseDefinitionDelegate)
		}
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