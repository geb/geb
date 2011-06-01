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

import geb.*
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
		
		def browser = fromBindingOr(binding, 'browser')
		if (!browser) {
			def conf = fromBindingOr(binding, 'conf')
			if (!conf) {
				def confEnv = fromBindingOr(binding, 'gebConfEnv')
				def confScript = fromBindingOr(binding, 'gebConfScript')
				conf = new ConfigurationLoader(confEnv).getConf(confScript)
			} 
			
			def args = [:]
			def baseUrl = fromBindingOr(binding, 'baseUrl')
			if (baseUrl) {
				args.baseUrl = baseUrl
			}
			
			browser = new Browser(args, conf)
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