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
package geb.navigator.factory

import geb.Browser
import geb.navigator.Navigator
import org.openqa.selenium.WebElement

abstract class AbstractNavigatorFactory implements NavigatorFactory {

	private final Browser browser
	private final InnerNavigatorFactory innerNavigatorFactory

	AbstractNavigatorFactory(Browser browser, InnerNavigatorFactory innerNavigatorFactory) {
		this.browser = browser
		this.innerNavigatorFactory = innerNavigatorFactory
	}

	protected Browser getBrowser() {
		return browser
	}

	Navigator createFromWebElements(Iterable<WebElement> elements) {
		List<WebElement> filtered = []
		elements.each {
			if (it != null) {
				filtered << it
			}
		}
		innerNavigatorFactory.createNavigator(browser, filtered)
	}

	Navigator createFromNavigators(Iterable<Navigator> navigators) {
		List<WebElement> filtered = []
		navigators.each {
			if (it != null) {
				filtered.addAll(it.allElements())
			}
		}
		innerNavigatorFactory.createNavigator(browser, filtered)
	}

	NavigatorFactory relativeTo(Navigator newBase) {
		new NavigatorBackedNavigatorFactory(newBase, innerNavigatorFactory)
	}

}
