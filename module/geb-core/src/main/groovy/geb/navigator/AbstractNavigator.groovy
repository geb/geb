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
import geb.js.JQueryAdapter
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

abstract class AbstractNavigator implements Navigator {

	final Browser browser

	AbstractNavigator(Browser browser) {
		this.browser = browser
	}

	/**
	 * Gets the element at the given index.
	 * @param index index of the element to retrieve - pass a negative value to start from the back
	 * @return the element at the given index, or null if no such element exists
	 */
	protected abstract WebElement getElement(int index)

	/**
	 * Gets the elements in the given range.
	 * @param range range of the elements to retrieve
	 * @return the elements in the given range, or an empty list if no such elements exist
	 */
	protected abstract List<WebElement> getElements(Range range)

	/**
	 * Gets the elements at the given indexes.
	 * @param indexes indexes of the elements to retrieve
	 * @return the elements at the given indexes, or an empty list if no such elements exist
	 */
	protected abstract List<WebElement> getElements(Collection indexes)

	boolean asBoolean() {
		!empty
	}

	@Override
	Navigator find(Map<String, Object> predicates, int index) {
		find(predicates, null, index..index)
	}

	@Override
	Navigator find(Map<String, Object> predicates, Range<Integer> range) {
		find(predicates, null, range)
	}

	@Override
	Navigator find(Map<String, Object> predicates, String selector, int index) {
		find(predicates, selector, index..index)
	}

	@Override
	Navigator find(String selector, Range<Integer> range) {
		find(selector)[range]
	}

	@Override
	Navigator find(Map<String, Object> predicates, String selector, Range<Integer> range) {
		find(predicates, (String) selector)[range]
	}

	Navigator find(Map<String, Object> predicates, String selector, Integer index) {
		find(predicates, selector, index..index)
	}

	Navigator find(Map<String, Object> predicates, Integer index) {
		find(predicates, "*", index)
	}

	@Override
	Navigator find(String selector, int index) {
		find(selector)[index]
	}

	@Override
	Navigator find(Map<String, Object> predicates) {
		find predicates, "*"
	}

	@Override
	Navigator find(Map<String, Object> predicates, String selector) {
		find(selector).filter(predicates)
	}

	@Override
	Navigator filter(Map<String, Object> predicates, String selector) {
		filter(selector).filter(predicates)
	}

	Navigator has(String selector) {
		findAll { Navigator it ->
			!it.find(selector).empty
		}
	}

	Navigator has(Map<String, Object> predicates) {
		findAll { Navigator it ->
			!it.find(predicates).empty
		}
	}

	Navigator has(Map<String, Object> predicates, String selector) {
		findAll { Navigator it ->
			!it.find(predicates, selector).empty
		}
	}

	Navigator eq(int index) {
		this[index]
	}

	Navigator add(String selector) {
		add browser.driver.findElements(By.cssSelector(selector))
	}

	Navigator add(WebElement[] elements) {
		add Arrays.asList(elements)
	}

	Navigator add(Collection<WebElement> elements) {
		List<WebElement> result = []
		result.addAll allElements()
		result.addAll elements
		browser.navigatorFactory.createFromWebElements(result)
	}

	Navigator plus(Navigator navigator) {
		add navigator.allElements()
	}

	String attr(String name) {
		getAttribute(name)
	}

	WebElement firstElement() {
		return getElement(0)
	}

	WebElement lastElement() {
		return getElement(-1)
	}

	Iterator<Navigator> iterator() {
		return new NavigatorIterator()
	}

	Navigator findAll(Closure predicate) {
		browser.navigatorFactory.createFromNavigators(super.findAll(predicate))
	}

	JQueryAdapter getJquery() {
		new JQueryAdapter(this)
	}

	int getHeight() {
		firstElement()?.size?.height ?: 0
	}

	int getWidth() {
		firstElement()?.size?.width ?: 0
	}

	int getX() {
		firstElement()?.location?.x ?: 0
	}

	int getY() {
		firstElement()?.location?.y ?: 0
	}

	String css(String propertyName) {
		firstElement()?.getCssValue(propertyName)
	}

	/**
	 * Iterator for looping over the context elements of a Navigator instance.
	 */
	private class NavigatorIterator implements Iterator<Navigator> {

		private int index

		boolean hasNext() {
			return index < AbstractNavigator.this.size()
		}

		Navigator next() {
			return AbstractNavigator.this[index++]
		}

		void remove() {
			throw new UnsupportedOperationException()
		}
	}

}
