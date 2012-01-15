/*
 * Copyright 2009 Roam - roam.be
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.navigator

import geb.Browser
import geb.Page
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import geb.js.JQueryAdapter

/**
 * Navigator is a jQuery-style DOM traversal tool that wraps a set of WebDriver WebElements.
 * The code is based on the Doj library written by Kevin Wetzels: http://code.google.com/p/hue/
 */
abstract class Navigator implements Iterable<Navigator> {

	final Browser browser
	
	Navigator(Browser browser) {
		this.browser = browser
	}
	
	boolean asBoolean() {
		!empty
	}

	/**
	 * Creates a new Navigator instance containing the elements matching the given
	 * CSS selector. Any CSS capabilities supported by the underlying WebDriver instance are supported.
	 * If the underlying WebDriver instance does not natively support finding elements by CSS selectors then tag, id
	 * and class name selectors may be applied (in any combination).
	 * <p>Examples:</p>
	 * <dl>
	 * <dt>h1</dt>
	 * <dd>selects all 'h1' elements</dd>
	 * <dt>.article</dt>
	 * <dd>selects all elements with the class 'article'</dd>
	 * <dt>#header</dt>
	 * <dd>selects the element with the id 'header'</dd>
	 * <dt>div.article p</dt>
	 * <dd>selects all p elements that are descendants of a div with class 'article'</dd>
	 * <dt>h1, h2</dt>
	 * <dd>selects all h1 and h2 elements</dd>
	 * <dt>li:odd</dt>
	 * <dd>selects odd-numbered li elements (CSS3 selectors like this are only supported when supported by the
	 * underlying WebDriver instance)</dd>
	 * </dl>
	 * @param selector a CSS selector
	 * @return new Navigator instance containing the matched elements
	 */
	abstract Navigator find(String selector)

	/**
	 * Shorthand for <code>find(selector)[indexOfElement]</code>.
	 * @param selector a CSS selector
	 * @param index index of the required element in the selection
	 * @return new Navigator instance containing a single element
	 */
	Navigator find(String selector, int index) {
		find(selector)[index]
	}

	/**
	 * Creates a new Navigator instance containing the elements whose attributes match the specified values or patterns.
	 * The key 'text' can be used to match the text contained in elements. Regular expression Pattern objects may be
	 * used as values. Note that for selecting by class it is better to use a CSS selector.
	 * <p>Examples:</p>
	 * <dl>
	 * <dt>find(name: "firstName")</dt>
	 * <dd>selects all elements with the name "firstName"</dd>
	 * <dt>find(name: "firstName", readonly: "readonly")</dt>
	 * <dd>selects all elements with the name "firstName" that are read-only</dd>
	 * <dt>find(text: "I can has cheezburger")</dt>
	 * <dd>selects all elements containing the exact text</dd>
	 * <dt>find(text: ~/I can has.+/)</dt>
	 * <dd>selects all elements whose text matches a regular expression</dd>
	 * </dl>
	 * @param predicates a Map with keys representing attributes and values representing required values or patterns
	 * @return a new Navigator instance containing the matched elements
	 */
	abstract Navigator find(Map<String, Object> predicates)

	/**
	 * Selects elements by both CSS selector and attributes. For example find("input", name: "firstName") will select
	 * all input elements with the name "firstName".
	 * @param selector a CSS selector
	 * @param predicates a Map with keys representing attributes and values representing required values or patterns
	 * @return a new Navigator instance containing the matched elements
	 */
	abstract Navigator find(Map<String, Object> predicates, String selector)

	/**
	 * Filters the set of elements represented by this Navigator to include only that have one or more descendants
	 * that match the selector.
	 * @param selector a CSS selector
	 * @return a new Navigator instance
	 */
	Navigator has(String selector) {
		findAll {
			!it.find(selector).empty
		}
	}

	/**
	 * Filters the set of elements represented by this Navigator to include only those that match
	 * the selector. Note that unlike find only tag, id and class based selectors are supported for this method
	 * regardless of the capabilities of the underlying WebDriver instance.
	 * @param selector a CSS selector
	 * @return a new Navigator instance
	 */
	abstract Navigator filter(String selector)

	abstract Navigator filter(Map<String, Object> predicates)

	abstract Navigator filter(Map<String, Object> predicates, String selector)

	/**
	 * Returns a new Navigator instance containing all elements of the current Navigator that do not match the selector.
	 * @param selector a CSS selector
	 * @return a new Navigator instance
	 */
	abstract Navigator not(String selector)

	/**
	 * Gets the wrapped element at the given index.
	 * <p>
	 * When no such element exists, an empty Navigator instance is returned.
	 * </p>
	 * @param index index of the element to retrieve - pass a negative value to start from the back
	 * @return new Navigator instance
	 */
	Navigator eq(int index) {
		this[index]
	}

	/**
	 * Gets the wrapped element at the given index.
	 * <p>
	 * When no such element exists, an empty Navigator instance is returned.
	 * </p>
	 * @param index index of the element to retrieve - pass a negative value to start from the back
	 * @return new Navigator instance
	 */
	abstract Navigator getAt(int index)

	/**
	 * Gets the wrapped elements in the given range.
	 * <p>
	 * When no such elements exist, an empty Navigator instance is returned.
	 * </p>
	 * @param range range of the elements to retrieve
	 * @return new Navigator instance
	 */
	abstract Navigator getAt(Range range)

	/**
	 * Gets the wrapped elements at the given indexes.
	 * <p>
	 * When no such elements exist, an empty Navigator instance is returned.
	 * </p>
	 * @param indexes indexes of the elements to retrieve
	 * @return new Navigator instance
	 */
	abstract Navigator getAt(Collection indexes)

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
	
	Navigator add(String selector) {
		add browser.driver.findElements(By.cssSelector(selector))
	}
	
	Navigator add(WebElement[] elements) {
		add Arrays.asList(elements)
	}
	
	Navigator add(Collection<WebElement> elements) {
		def result = []
		result.addAll allElements()
		result.addAll elements
		on(browser, result)
	}

	/**
	 * Creates a new Navigator instance by removing the element at the given index
	 * from the context.
	 * <p>
	 * If no such element exists, the current instance is returned.
	 * </p>
	 * @param index index of the element to remove - pass a negative value to start from the back
	 * @return new Navigator instance
	 */
	abstract Navigator remove(int index)

	/**
	 * Merges the Navigator instance with the current instance to create a new
	 * Navigator instance containing the context elements of both.
	 * @param navigator navigator to merge with this one
	 * @return new Navigator instance
	 */
	Navigator plus(Navigator navigator) {
		add navigator.allElements()
	}

	/**
	 * Creates a new Navigator instance containing the next sibling elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator next()

	/**
	 * Creates a new Navigator instance containing the next sibling elements of the
	 * current context elements, matching the selector.
	 * <p>
	 * Unlike {@link #next()}, this method will keep looking for the first
	 * matching sibling until it finds a match or is out of siblings.
	 * </p>
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator next(String selector)

	/**
	 * Creates a new Navigator instance containing all following sibling elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator nextAll()

	/**
	 * Creates a new Navigator instance containing all following sibling elements of the
	 * current context elements that match the selector.
	 * <p>
	 * Unlike {@link #next()}, this method will keep looking for the first
	 * matching sibling until it finds a match or is out of siblings.
	 * </p>
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator nextAll(String selector)

	/**
	 * Creates a new Navigator instance containing all following sibling elements of the
	 * current context elements up to, but not including, the first to match the selector.
	 * 
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator nextUntil(String selector)

	/**
	 * Creates a new Navigator instance containing the previous sibling elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator previous()

	/**
	 * Creates a new Navigator instance containing all preceding sibling elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator prevAll()

	/**
	 * Creates a new Navigator instance containing the previous sibling elements of the
	 * current context elements, matching the selector.
	 * <p>
	 * Unlike {@link #previous()}, this method will keep looking for the first
	 * matching sibling until it finds a match or is out of siblings.
	 * </p>
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator previous(String selector)

	/**
	 * Creates a new Navigator instance containing all preceding sibling elements of the
	 * current context elements, matching the selector.
	 * 
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator prevAll(String selector)

	/**
	 * Creates a new Navigator instance containing all preceding sibling elements of the
	 * current context elements up to, but not including the first matching the selector.
	 * 
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator prevUntil(String selector)

	/**
	 * Creates a new Navigator instance containing the direct parent elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator parent()

	/**
	 * Creates a new Navigator instance containing the direct parent elements of the current
	 * context elements that match the selector.
	 *
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator parent(String selector)

	/**
	 * Creates a new Navigator instance containing all the ancestor elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator parents()

	/**
	 * Creates a new Navigator instance containing all the ancestor elements of the
	 * current context elements that match the selector.
	 *
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator parents(String selector)

	/**
	 * Creates a new Navigator instance containing all the ancestor elements of the
	 * current context elements up to but not including the first that matches the selector.
	 *
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator parentsUntil(String selector)

	/**
	 * Creates a new Navigator instance containing the first ancestor element of each of the current
	 * context elements that match the selector.
	 * <p>
	 * Unlike {@link #parent()}, this method will keep traversing up the DOM
	 * until a match is found or the top of the DOM has been found
	 * </p>
	 * @param selector to match
	 * @return new Navigator instance
	 */
	abstract Navigator closest(String selector)

	abstract Navigator children()

	abstract Navigator children(String selector)

	abstract Navigator siblings()

	abstract Navigator siblings(String selector)

	/**
	 * Returns true if at least one of the context elements has the given class.
	 * @param className class to check for
	 * @return true if at least one of the context elements has the given class
	 */
	abstract boolean hasClass(String className)

	/**
	 * Returns true if at least one of the context elements matches the tag.
	 * @param tag tag to match
	 * @return true if at least one of the context elements matches the tag
	 */
	abstract boolean is(String tag)

	/**
	 * Uses the isDisplayed() of RenderedWebElement to determine if the first element in the context is displayed.
	 * If the context is empty, or the first element is not a RenderedWebElement, false will be returned.
	 *
	 * @return true if the first element is displayed
	 */
	abstract boolean isDisplayed()

	/**
	 * Shorthand for <code>hasAttribute("disabled")</code>.
	 * @return true when the first element is disabled
	 */
	boolean isDisabled() {
		def value = getAttribute("disabled")
		// Different drivers return different values here
		(value == "disabled" || value == "true")
	}

	/**
	 * Shorthand for <code>hasAttribute("readonly")</code>.
	 * @return true when the first element is readonly
	 */
	boolean isReadOnly() {
		def value = getAttribute("readonly")
		(value == "readonly" || value == "true")
	}

	/**
	 * Returns the tag name of the first context element.
	 * @return the tag name of the first context element
	 */
	abstract String tag()

	/**
	 * Returns the text content of the first context element.
	 * @return the text content of the first context element
	 */
	abstract String text()

	/**
	 * Returns the value of the given attribute of the first context element.
	 * @param name name of the attribute
	 * @return the value of the given attribute of the first context element
	 */
	abstract String getAttribute(String name)

	/**
	 * Returns the value of the given attribute of the first context element.
	 * @param name name of the attribute
	 * @return the value of the given attribute of the first context element
	 */
	String attr(String name) {
		getAttribute(name)
	}

	/**
	 * Returns the class names present on all elements. The result is a unique set and is in alphabetical order.
	 * @return the class names present on all elements.
	 */
	abstract List<String> classes()

	/**
	 * Returns the value of the first context element for input elements
	 * (including textarea, select and button).
	 * <p>
	 * In the case of a select, the value of the first selected option is returned.
	 * </p>
	 * @return value of the first context element
	 */
	abstract value()

	/**
	 * Sets the value of the form input elements to the given value.
	 * @param value value to use
	 * @return current Navigator instance
	 */
	abstract Navigator value(value)

	abstract Navigator leftShift(value)

	/**
	 * Clicks on the first context element.
	 * @throws java.io.IOException
	 * @throws java.lang.ClassCastException
	 */
	abstract Navigator click() throws IOException, ClassCastException

	abstract Navigator click(Class<? extends Page> pageClass)

	abstract Navigator click(List<Class<? extends Page>> potentialPageClasses)

	/**
	 * Returns the number of context elements.
	 * @return the number of context elements
	 */
	abstract int size()

	/**
	 * Returns true when there are no context elements.
	 * @return true when there are no context elements
	 */
	abstract boolean isEmpty()

	/**
	 * Creates a new Navigator instance containing only the first context element (wrapped).
	 * @return new Navigator instance
	 */
	abstract Navigator head()

	 /**
	 * Creates a new Navigator instance containing only the first context element (wrapped).
	 * @return new Navigator instance
	 */
	abstract Navigator first()

	/**
	 * Creates a new Navigator instance containing only the last context element (wrapped).
	 * @return new Navigator instance
	 */
	abstract Navigator last()

	/**
	 * Creates a new Navigator instance containing all but the first context element (wrapped).
	 * @return new Navigator instance
	 */
	abstract Navigator tail()

	/**
	 * Returns the first context element (not wrapped).
	 * @return the first context element (not wrapped)
	 */
	WebElement firstElement() {
		return getElement(0)
	}

	/**
	 * Returns the last context element (not wrapped).
	 * @return the last context element (not wrapped)
	 */
	WebElement lastElement() {
		return getElement(-1)
	}

	/**
	 * Returns all context elements.
	 * @return all context elements
	 */
	abstract Collection<WebElement> allElements()

	Iterator<Navigator> iterator() {
		return new NavigatorIterator(this)
	}

	/**
	 * Overrides the standard Groovy findAll so that the object returned is a Navigator rather than a Collection<Navigator>.
	 */
	Navigator findAll(Closure predicate) {
		Collection<Navigator> results = super.findAll(predicate)
		on getBrowser(), results*.allElements().flatten()
	}

	/**
	 * Throws an exception when the Navigator instance is empty.
	 * @return the current Navigator instance
	 */
	abstract Navigator verifyNotEmpty()

	/**
	 * Returns an adapter for calling jQuery methods on the elements in this navigator.
	 */
	JQueryAdapter getJquery() {
		new JQueryAdapter(this)
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
	
	/**
	 * Factory method to create an initial Navigator instance.
	 * <p>
	 * Hides the fact that there are two implementations of Navigator at work behind
	 * the scenes: one for working with an empty context that keeps the code
	 * for the other one, with most of the logic, simple.
	 * </p>
	 * @param browser the browser the content is attached to
	 * @param contextElements the context elements to use
	 * @return new Navigator instance
	 */
	static Navigator on(Browser browser, WebElement[] contextElements) {
		contextElements ? new NonEmptyNavigator(browser, contextElements) : new EmptyNavigator(browser)
	}

	/**
	 * Factory method to create a Navigator instance that is composed of other instances.
	 * 
	 * @param browser the browser the content is attached to
	 * @param navigators the navigators to compose of
	 * @return new Navigator instance
	 */
	static Navigator on(Browser browser, Navigator[] navigators) {
		if (navigators) {
			on(browser, navigators*.allElements().flatten())
		} else {
			new EmptyNavigator(browser)
		}
	}

	/**
	 * Factory method to create an initial Navigator instance.
	 * <p>
	 * Hides the fact that there are two implementations of Navigator at work behind
	 * the scenes: one for working with an empty context that keeps the code
	 * for the other one, with most of the logic, simple.
	 * </p>
	 * @param browser the browser the content is attached to
	 * @param contextElements the context elements to use
	 * @return new Navigator instance
	 */
	static Navigator on(Browser browser, Collection<? extends WebElement> contextElements) {
		contextElements ? new NonEmptyNavigator(browser, contextElements) : new EmptyNavigator(browser)
	}

	/**
	 * Factory method to create an initial Navigator instance.
	 * <p>
	 * Hides the fact that there are two implementations of Navigator at work behind
	 * the scenes: one for working with an empty context that keeps the code
	 * for the other one, with most of the logic, simple.
	 * </p>
	 * @param browser the browser for which to create a navigator of it's entire content
	 * @return new Navigator instance
	 */
	static Navigator on(Browser browser) {
		def rootElement = browser.driver.findElement(By.tagName("html"))
		rootElement ? new NonEmptyNavigator(browser, rootElement) : new EmptyNavigator(browser)
	}
}

// TODO: this should be a private static member but can't in Groovy 1.6

/**
 * Iterator for looping over the context elements of a Navigator instance.
 */
class NavigatorIterator implements Iterator<Navigator> {

	private int index
	private Navigator navigator

	NavigatorIterator(Navigator navigator) {
		this.navigator = navigator
	}

	boolean hasNext() {
		return index < navigator.size()
	}

	Navigator next() {
		return navigator[index++]
	}

	void remove() {
		throw new UnsupportedOperationException()
	}
}


