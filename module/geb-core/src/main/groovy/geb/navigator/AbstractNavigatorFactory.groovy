/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geb.navigator

import geb.Browser
import org.openqa.selenium.WebElement

abstract class AbstractNavigatorFactory implements NavigatorFactory {

	private final Browser browser

	AbstractNavigatorFactory(Browser browser) {
		this.browser = browser
	}

	protected Browser getBrowser() {
		return browser
	}

	@Override
	Navigator create(WebElement... elements) {
		Collection<WebElement> filteredElements = elements.findAll { it != null }
		filteredElements ? createNonEmptyNavigator(* filteredElements) : createEmptyNavigator()
	}

	@Override
	Navigator create(Navigator... elements) {
		Collection<Navigator> filteredElements = elements.findAll { it != null }
		filteredElements ? createNonEmptyNavigator(* filteredElements*.allElements().flatten()) : createEmptyNavigator()
	}

	@Override
	NavigatorFactory relativeTo(Navigator newBase) {
		createRelativeNavigatorFactory(newBase)
	}

	protected Navigator createNonEmptyNavigator(WebElement... elements) {
		new NonEmptyNavigator(browser, * elements)
	}

	protected Navigator createEmptyNavigator() {
		new EmptyNavigator(browser)
	}

	protected NavigatorFactory createRelativeNavigatorFactory(Navigator navigator) {
		new NavigatorBackedNavigatorFactory(navigator)
	}

}
