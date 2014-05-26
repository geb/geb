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

	static public final FORWARDED_BROWSER_METHODS = [
		"go", "to", "via", "at",
		"waitFor",
		"withAlert", "withNoAlert", "withConfirm", "withNoConfirm",
		"download", "downloadStream", "downloadText", "downloadBytes", "downloadContent",
		"report", "reportGroup", "cleanReportGroupDir"
	].asImmutable()

	protected BindingUpdater(Binding binding, Browser browser) {
		this.binding = binding
		this.browser = browser
		this.pageChangeListener = createPageChangeListener(binding, browser)
	}

	private class BindingUpdatingPageChangeListener implements PageChangeListener {
		void pageWillChange(Browser browser, Page oldPage, Page newPage) {
			binding.setVariable("page", newPage)
			binding.setVariable("\$", new InvocationForwarding("\$", newPage))
		}

		void clearBinding() {
			binding.variables.remove("page")
			binding.variables.remove("\$")
		}
	}

	protected PageChangeListener createPageChangeListener(Binding binding, Browser browser) {
		new BindingUpdatingPageChangeListener()
	}

	private static class InvocationForwarding extends Closure {
		private final String methodName
		private final Object target

		InvocationForwarding(String theMethodName, Object theTarget) {
			super(theTarget)

			methodName = theMethodName
			target = theTarget
		}

		protected doCall(Object[] args) {
			target."$methodName"(* args)
		}
	}

	/**
	 * Populates the binding and starts the updater updating the binding as necessary.
	 */
	BindingUpdater initialize() {
		binding.browser = browser
		binding.js = browser.js

		FORWARDED_BROWSER_METHODS.each {
			binding.setVariable(it, new InvocationForwarding(it, browser))
		}

		browser.registerPageChangeListener(pageChangeListener)

		this
	}

	/**
	 * Removes everything from the binding and stops updating it.
	 */
	BindingUpdater remove() {
		browser.removePageChangeListener(pageChangeListener)
		pageChangeListener.clearBinding()

		binding.variables.remove('browser')
		binding.variables.remove('js')

		FORWARDED_BROWSER_METHODS.each {
			binding.variables.remove(it)
		}

		this
	}

}