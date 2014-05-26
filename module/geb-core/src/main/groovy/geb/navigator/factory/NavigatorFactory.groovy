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

import geb.navigator.Navigator
import org.openqa.selenium.WebElement

/**
 * The object that produces Navigator objects.
 *
 * A browser has-a NavigatorFactory, and passes it down to pages/modules etc.
 */
interface NavigatorFactory {

	/**
	 * The base for all content lookups from this factory.
	 *
	 * @return The base for all content lookups from this factory.
	 */
	Navigator getBase()

	/**
	 * Create a navigator, backed by the given web elements.
	 *
	 * @param elements The web elements to back the navigator.
	 * @return The created navigator
	 */
	Navigator createFromWebElements(Iterable<WebElement> elements)

	/**
	 * Create a navigator, backed by the given navigators.
	 * @param navigators The navigators to back the navigator
	 * @return The created navigator
	 */
	Navigator createFromNavigators(Iterable<Navigator> navigators)

	/**
	 * Create a new factory, relative to the given navigator.
	 *
	 * @param newBase The base to use for the new navigator factory.
	 * @return The new navigator factory.
	 */
	NavigatorFactory relativeTo(Navigator newBase)
}
