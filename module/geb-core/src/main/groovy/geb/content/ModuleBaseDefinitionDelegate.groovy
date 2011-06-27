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

class ModuleBaseDefinitionDelegate {

	private startingBase
	private params
	
	@Delegate private NavigableSupport navigableSupport
	
	ModuleBaseDefinitionDelegate(Navigator startingBase, Map params) {
		this.startingBase = startingBase
		this.params = params
		navigableSupport = new ConstantBaseNavigableSupport(this, null, startingBase)
	}
	
	def methodMissing(String name, args) {
		startingBase."$name"(*args)
	}
	
	def propertyMissing(String name) {
		if (params.containsKey(name)) {
			params[name]
		} else {
			navigableSupport."$name"
		}
	}

}