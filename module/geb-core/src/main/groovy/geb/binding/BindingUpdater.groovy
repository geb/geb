/* 
 * Copyright 2011 the original author or authors.
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

package geb.binding

import geb.Browser
import geb.Page
import geb.PageChangeListener

class BindingUpdater {

	final Browser browser
	final Binding binding
	
	protected final PageChangeListener pageChangeListener
	
	protected BindingUpdater(Binding binding, Browser browser) {
		this.binding = binding
		this.browser = browser
		this.pageChangeListener = createPageChangeListener(binding, browser)
	}

	protected PageChangeListener createPageChangeListener(Binding binding, Browser browser) {
		new BindingUpdatingPageChangeListener(this)
	}
	
	/**
	 * Populates the binding and starts the updater updating the binding as necessary.
	 */
	BindingUpdater initialize() {
		binding.browser = browser
		binding.go = { Object[] args -> browser.go(*args) }
		binding.to = { Object[] args -> browser.to(*args) }
		binding.at = { Class pageClass -> browser.at(pageClass) }
		binding.$ = { Object[] args -> browser.page.$(*args) }
		binding.js = binding.browser.js
		
		browser.registerPageChangeListener(pageChangeListener)
		
		this
	}
	
	/**
	 * Removes everything from the binding and stops updating it.
	 */
	BindingUpdater remove() {
		browser.removePageChangeListener(pageChangeListener)
		binding.variables.remove('page')
		
		binding.variables.remove('browser')
		binding.variables.remove('go')
		binding.variables.remove('to')
		binding.variables.remove('at')
		binding.variables.remove('$')
		binding.variables.remove('js')
		
		this
	}
	
	protected Closure createDollarFunctionDispatcher() {
		return { Object[] args ->
			if (browser.page.metaClass.respondsTo(browser.page, "\$", args)) {
				browser.page.$(*args)
			} else {
				throw new IllegalArgumentException("No variant of dollar function: \$(${args*.getClass()*.name.join(', ')})")
			}
		}
	}

	/**
	 * This method is only public for BindingUpdatingPageChangeListener and will be made protected
	 * in future versions that require Groovy 1.7+
	 */
	void changePage(Page page) {
		binding.setVariable("page", page)
	}
	
}

// TODO - make inner class in Groovy 1.7
class BindingUpdatingPageChangeListener implements PageChangeListener {
	
	final BindingUpdater updater
	
	BindingUpdatingPageChangeListener(BindingUpdater updater) {
		this.updater = updater
	}
	
	void pageWillChange(Browser browser, Page oldPage, Page newPage) {
		updater.changePage(newPage)
	}
}
