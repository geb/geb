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
package geb.easyb

import geb.Browser
import geb.binding.BindingUpdater
import org.openqa.selenium.WebDriver
import org.easyb.plugin.BasePlugin
import org.easyb.exception.VerificationException

class GebPlugin extends BasePlugin {

	private initialised = false
	private bindingUpdater
	
	String getName() {
		"geb"
	}

	def beforeWhen(Binding binding) {
		if (initialised) return
		
		def driver = fromBindingOr(binding, 'driver')
		def baseUrl = fromBindingOr(binding, 'baseUrl')
		def browser = fromBindingOr(binding, 'browser')
		
		if (!browser) {
			browser = driver ? new Browser(driver, baseUrl) : new Browser(baseUrl)
		}
		
		bindingUpdater = new BindingUpdater(binding, browser)
		bindingUpdater.initialize()
		
		def originalAt = binding.at
		binding.at = { Class pageClass ->
			try {
				if (!originalAt(pageClass)) {
					throw new VerificationException("not at page $pageClass")
				}
			} catch (Throwable e) {
				throw new VerificationException("not at page $pageClass", e)
			}
		}
		
		initialised = true
	}
	
	def afterStory(Binding binding) {
		bindingUpdater?.remove()
	}
	
	private fromBindingOr(Binding binding, String name, defaultValue = null) {
		try {
			binding.getVariable(name)
		} catch (MissingPropertyException e) {
			defaultValue
		}
	}
}