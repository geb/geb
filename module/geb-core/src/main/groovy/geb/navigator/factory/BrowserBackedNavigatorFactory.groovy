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
import org.openqa.selenium.By

class BrowserBackedNavigatorFactory extends AbstractNavigatorFactory {

	protected String getBaseTagName() {
		"html"
	}

	BrowserBackedNavigatorFactory(Browser browser, InnerNavigatorFactory innerNavigatorFactory) {
		super(browser, innerNavigatorFactory)
	}

	protected Navigator createBase() {
		createFromWebElements(Collections.singletonList(browser.driver.findElement(By.tagName(baseTagName))))
	}

	@Override
	Navigator getBase() {
		def baseNavigatorWaiting = browser.config.baseNavigatorWaiting
		baseNavigatorWaiting ? baseNavigatorWaiting.waitFor { createBase() } : createBase()
	}
}
