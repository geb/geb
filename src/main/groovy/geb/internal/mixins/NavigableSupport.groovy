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
package geb.internal.mixins

import geb.navigator.Navigator

class NavigableSupport {

	Navigator $() {
		_getNavigator()
	}
	
	Navigator $(int index) {
		_getNavigator().find(index)
	}
	
	Navigator $(String selector) {
		_getNavigator().find(selector)
	}
	
	Navigator $(Map attributes) {
		_getNavigator().find(attributes)
	}
	
	Navigator $(String selector, int index) {
		_getNavigator().find(selector, index)
	}
	
	Navigator $(Map attributes, String selector) {
		_getNavigator().find(attributes, selector)
	}
	
	Navigator $(Map attributes, int index) {
		_getNavigator().find(attributes, index)
	}
	
	Navigator $(Map attributes, String selector, int index) {
		_getNavigator().find(attributes, selector, index)
	}
	

}