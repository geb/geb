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

import com.gargoylesoftware.htmlunit.html.HtmlElement
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * Navigator is a jQuery-style DOM traversal tool that wraps a set of WebDriver WebElements.
 * The code is based on the Doj library written by Kevin Wetzels: http://code.google.com/p/hue/
 */
abstract class Navigator implements Iterable<Navigator> {

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
		List<WebElement> result = []
		result.addAll allElements()
		result.addAll navigator.allElements()
		on(result)
	}

	/**
	 * Creates a new Navigator instance containing the next sibling elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator next()

	/**
	 * Creates a new Navigator instance containing the next sibling elements of the
	 * current context elements, matching the given tag.
	 * <p>
	 * Unlike         {@link #next()}        , this method will keep looking for the first
	 * matching sibling until it finds a match or is out of siblings.
	 * </p>
	 * @param selector tag to match
	 * @return new Navigator instance
	 */
	abstract Navigator next(String selector)

	/**
	 * Creates a new Navigator instance containing the previous sibling elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator previous()

	/**
	 * Creates a new Navigator instance containing the previous sibling elements of the
	 * current context elements, matching the given tag.
	 * <p>
	 * Unlike         {@link #previous()}        , this method will keep looking for the first
	 * matching sibling until it finds a match or is out of siblings.
	 * </p>
	 * @param selector tag to match
	 * @return new Navigator instance
	 */
	abstract Navigator previous(String selector)

	/**
	 * Creates a new Navigator instance containing the direct parent elements of the
	 * current context elements.
	 * @return new Navigator instance
	 */
	abstract Navigator parent()

	/**
	 * Creates a new Navigator instance containing the parent elements of the current
	 * context elements that match the given tag.
	 * <p>
	 * Unlike         {@link #parent()}        , this method will keep traversing up the DOM
	 * until a match is found or the top of the DOM has been found
	 * </p>
	 * @param selector tag to match
	 * @return new Navigator instance
	 */
	abstract Navigator parent(String selector)

	abstract Navigator children()

	abstract Navigator children(String selector)

	abstract Navigator siblings()

	abstract Navigator siblings(String selector)

	abstract Navigator unique()

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
	 * Shorthand for <code>hasAttribute("disabled")</code>.
	 * @return true when the first element is disabled
	 */
	boolean isDisabled() {
		getAttribute("disabled") == "disabled"
	}

	/**
	 * Shorthand for <code>hasAttribute("readonly")</code>.
	 * @return true when the first element is readonly
	 */
	boolean isReadOnly() {
		getAttribute("readonly") == "readonly"
	}

	/**
	 * Returns the tag name of the first context element.
	 * @return the tag name of the first context element
	 */
	abstract String getTag()

	/**
	 * Returns the text content of the first context element.
	 * @return the text content of the first context element
	 */
	abstract String getText()

	/**
	 * Returns the value of the given attribute of the first context element.
	 * @param name name of the attribute
	 * @return the value of the given attribute of the first context element
	 */
	abstract String getAttribute(String name)

	/**
	 * Returns the class names present on all elements. The result is a unique set in no guaranteed order.
	 * @return the class names present on all elements.
	 */
	abstract Collection<String> getClassNames()

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
	 * Sets the value of the form input elements to the given value. In the
	 * case of a multiple select, this will select an extra option.
	 * @param value value to use
	 * @return current Navigator instance
	 */
	abstract Navigator value(value)

	abstract leftShift(value)

	/**
	 * Clicks on the first context element.
	 * @throws java.io.IOException
	 * @throws java.lang.ClassCastException
	 */
	abstract void click() throws IOException, ClassCastException

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
	protected WebElement firstElement() {
		return getElement(0)
	}

	/**
	 * Returns the last context element (not wrapped).
	 * @return the last context element (not wrapped)
	 */
	protected WebElement lastElement() {
		return getElement(-1)
	}

	/**
	 * Returns all context elements.
	 * @return all context elements
	 */
	protected abstract Collection<WebElement> allElements()

	Iterator<Navigator> iterator() {
		return new NavigatorIterator(this)
	}

	/**
	 * Overrides the standard Groovy findAll so that the object returned is a Navigator rather than a Collection<Navigator>.
	 */
	Navigator findAll(Closure predicate) {
		Collection<Navigator> results = super.findAll(predicate)
		on results*.allElements().flatten()
	}

	/**
	 * Throws an exception when the Navigator instance is empty.
	 * @return the current Navigator instance
	 */
	abstract Navigator verifyNotEmpty()

	/**
	 * Factory method to create an initial Navigator instance.
	 * <p>
	 * Hides the fact that there are two implementations of Navigator at work behind
	 * the scenes: one for working with an empty context that keeps the code
	 * for the other one, with most of the logic, simple.
	 * </p>
	 * @param contextElements the context elements to use
	 * @return new Navigator instance
	 */
	static Navigator on(WebElement... contextElements) {
		contextElements ? new NonEmptyNavigator(contextElements).unique() : EmptyNavigator.instance
	}

	/**
	 * Factory method to create an initial Navigator instance.
	 * <p>
	 * Hides the fact that there are two implementations of Navigator at work behind
	 * the scenes: one for working with an empty context that keeps the code
	 * for the other one, with most of the logic, simple.
	 * </p>
	 * @param contextElements the context elements to use
	 * @return new Navigator instance
	 */
	static Navigator on(Collection<? extends WebElement> contextElements) {
		return contextElements ? new NonEmptyNavigator(contextElements).unique() : EmptyNavigator.instance
	}

	/**
	 * Factory method to create an initial Navigator instance.
	 * <p>
	 * Hides the fact that there are two implementations of Navigator at work behind
	 * the scenes: one for working with an empty context that keeps the code
	 * for the other one, with most of the logic, simple.
	 * </p>
	 * @param driver the page supplying the document element
	 * @return new Navigator instance
	 */
	static Navigator on(WebDriver driver) {
		def rootElement = driver?.findElement(By.tagName("html"))
		rootElement ? new NonEmptyNavigator(rootElement) : EmptyNavigator.instance
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


