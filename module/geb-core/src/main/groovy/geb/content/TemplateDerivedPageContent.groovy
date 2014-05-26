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

import geb.*
import geb.error.RequiredPageContentNotPresent
import geb.navigator.Navigator
import org.openqa.selenium.WebDriver

abstract class TemplateDerivedPageContent implements Navigator {

	private PageContentTemplate _template
	private Object[] _args

	@Delegate
	protected Navigator _navigator

	/**
	 * Called by the template when created (i.e. is not public).
	 *
	 * We don't use a constructor to prevent users from having to implement them.
	 */
	void init(PageContentTemplate template, Navigator navigator, Object[] args) {
		this._template = template
		this._navigator = navigator
		this._args = args
	}

	String toString() {
		"${_template.name} - ${this.class.simpleName} (owner: ${_template.owner}, args: $_args, value: ${_navigator.value()})"
	}

	/**
	 * The page that this content is part of
	 */
	Page getPage() {
		_template.page
	}

	Browser getBrowser() {
		getPage().browser
	}

	WebDriver getDriver() {
		getBrowser().driver
	}

	boolean isPresent() {
		!_navigator.empty
	}

	void require() {
		if (!isPresent()) {
			throw new RequiredPageContentNotPresent(this)
		}
		this
	}

	/**
	 * Returns the height of the first element the navigator matches or 0 if it matches nothing.
	 * <p>
	 * To get the height of all matched elements you can use the spread operator {@code navigator*.height}
	 */
	int getHeight() {
		firstElement()?.size?.height ?: 0
	}

	/**
	 * Returns the width of the first element the navigator matches or 0 if it matches nothing.
	 * <p>
	 * To get the width of all matched elements you can use the spread operator {@code navigator*.width}
	 */
	int getWidth() {
		firstElement()?.size?.width ?: 0
	}

	/**
	 * Returns the x coordinate (from the top left corner) of the first element the navigator matches or 0 if it matches nothing.
	 * <p>
	 * To get the x coordinate of all matched elements you can use the spread operator {@code navigator*.x}
	 */
	int getX() {
		firstElement()?.location?.x ?: 0
	}

	/**
	 * Returns the y coordinate (from the top left corner) of the first element the navigator matches or 0 if it matches nothing.
	 * <p>
	 * To get the y coordinate of all matched elements you can use the spread operator {@code navigator*.y}
	 */
	int getY() {
		firstElement()?.location?.y ?: 0
	}

	Navigator click() {
		if (templateParams.toSingle) {
			_navigator.click(templateParams.toSingle)
		} else if (templateParams.toList) {
			_navigator.click(templateParams.toList)
		} else {
			_navigator.click()
		}
	}

	PageContentTemplateParams getTemplateParams() {
		_template.params
	}

	boolean asBoolean() {
		_navigator.asBoolean()
	}

	def methodMissing(String name, args) {
		_navigator.methodMissing(name, args)
	}

	def propertyMissing(String name) {
		_navigator.propertyMissing(name)
	}

	def propertyMissing(String name, val) {
		_navigator.propertyMissing(name, val)
	}
}