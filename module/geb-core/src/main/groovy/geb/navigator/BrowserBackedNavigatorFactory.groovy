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
import org.openqa.selenium.By

class BrowserBackedNavigatorFactory implements NavigatorFactory {

	private final Browser browser

	BrowserBackedNavigatorFactory(Browser browser) {
		this.browser = browser
	}

	@Override
	Navigator getBase() {
		def rootElement = browser.driver.findElement(By.tagName("html"))
		rootElement ? new NonEmptyNavigator(browser, rootElement) : new EmptyNavigator(browser)
	}

	@Override
	Navigator create(Map<String, Object> attributePredicates, String cssSelector, Range<Integer> range) {
		base.find(attributePredicates, cssSelector, range)
	}

}
