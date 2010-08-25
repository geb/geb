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
import org.openqa.selenium.WebDriver
import org.easyb.plugin.BasePlugin
import org.easyb.exception.VerificationException

class GebPlugin extends BasePlugin {

	private initialised = false
	
	String getName() {
		"geb"
	}

	def beforeWhen(Binding binding) {
		if (initialised) return
		
		def driver = fromBindingOr(binding, 'driver')
		def baseUrl = fromBindingOr(binding, 'baseUrl')
		def browser = fromBindingOr(binding, 'browser')
		
		if (!browser) {
			binding.browser = driver ? new Browser(driver, baseUrl) : new Browser(baseUrl)
		}
		
		binding.browser.registerPageChangeListener(new BindingUpdatingPageChangeListener(binding))
		binding.go = { Object[] args -> binding.browser.go(*args) }
		binding.to = { Object[] args -> binding.browser.to(*args) }
		binding.$ = { Object[] args -> binding.browser.page.$(*args) }
		binding.setPageType = { Class pageType -> binding.browser.page(pageType) }
		binding.js = binding.browser.js

		binding.at = { Class pageClass ->
			try {
				def page = binding.browser.page

				if (page.class != pageClass) {
					throw new VerificationException('unexpected page type', pageClass, page.class)
				}

				if (!page.verifyAt()) {
					throw new VerificationException("'at check' of page '$page' failed")
				}
			} catch (Throwable e) {
				e.printStackTrace()
				throw e
			}
		}
		
		initialised = true
	}
	
	private fromBindingOr(Binding binding, String name, defaultValue = null) {
		try {
			binding.getVariable(name)
		} catch (MissingPropertyException e) {
			defaultValue
		}
	}
}