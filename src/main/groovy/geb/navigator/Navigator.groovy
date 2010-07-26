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
import java.util.regex.Pattern
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import static geb.navigator.MatchType.EQUALS
import org.openqa.selenium.By

/**
 * Navigator is a jQuery-style DOM traversal tool, to be used in conjunction with
 * HtmlUnit's   {@link HtmlElement}  .
 * <p>
 * The best examples of usage can be found in the unit tests for this class,
 * but here are some pointers.
 * </p>
 * <p>
 * Say you want to get value of the text input inside a form with class "search",
 * located inside a div with id sidebar. Here's how you can do it with Navigator:
 * </p>
 * <pre><code>
 * // Possibility #1 - plain and simple
 * String value1 = Navigator.on(page).getById("sidebar").get("form").withClass("search").get("input").value();
 * // Possibility #2 - concise with simple CSS selectors
 * String value2 = Navigator.on(page).get("#sidebar form.search input").value();
 * </code></pre>
 * <p>
 * A Navigator instance is <strong>immutable</strong>. In the above code, each
 * possibility creates 5 new Navigator instances:
 * </p>
 * <ol>
 * <li>Navigator.on(): the initial object</li>
 * <li>One for getting an element by id "sidebar"</li>
 * <li>One for getting descendants by tag name "form"</li>
 * <li>One for filtering the context elements by class "search"</li>
 * <li>One for getting descendants by tag name "input"</li>
 * </ol>
 * <p>
 * Because Navigator relies on two implementations to keep the DOM traversal code
 * clean (one for an empty Navigator, one for a non-empty Navigator), construction of a new
 * Navigator instance is handled by the <code>on(...)</code> factory methods.
 * </p>
 * <p>
 * Note that Navigator implements   {@link Iterable}  , allowing you to loop over the
 * context elements with a for-each loop. Following code will print the value of
 * the class attribute of each div on the page:
 * </p>
 * <pre><code>
 * Navigator allDivsOnThePage = Navigator.on(page).get("div");
 * for (Navigator div : allDivsOnThePage) {*      System.out.println(div.attribute("class"));
 *}* </code></pre>
 * <p>
 * When traversing the DOM, missing elements will not throw exceptions. Assume
 * you've got a page without any tables and without any elements with the
 * class "nono":
 * </p>
 * <pre><code>
 * String thisWillBeNull = Navigator.on(page).get("table").get("a").get("span").attribute("title");
 * String asWillThis = Navigator.on(page).get(".nono").get("a").get("span").attribute("title");
 * </code></pre>
 * <p>
 * Checking whether there is actually anything there, goes like this:
 * </p>
 * <pre><code>
 * boolean thisIsTrue = Navigator.on(page).get("table").get("a").get("span").isEmpty();
 * boolean thisToo = Navigator.on(page).get(".nono").get("a").get("span").size() == 0;
 * boolean andThis = Navigator.on(page).get("table").isEmpty();
 * boolean thisIsProbablyFalse = Navigator.on(page).get("body").isEmpty();
 * </code></pre>
 * @author Kevin Wetzels
 */
abstract class Navigator implements Iterable<Navigator> {

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
	abstract WebElement getElement(int index)

	/**
	 * Gets the elements in the given range.
	 * @param range range of the elements to retrieve
	 * @return the elements in the given range, or an empty list if no such elements exist
	 */
	abstract List<WebElement> getElements(Range range)

	/**
	 * Gets the elements at the given indexes.
	 * @param indexes indexes of the elements to retrieve
	 * @return the elements at the given indexes, or an empty list if no such elements exist
	 */
	abstract List<WebElement> getElements(Collection indexes)

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
	 * Unlike   {@link #next()}  , this method will keep looking for the first
	 * matching sibling until it finds a match or is out of siblings.
	 * </p>
	 * @param tag tag to match
	 * @return new Navigator instance
	 */
	abstract Navigator next(String tag)

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
	 * Unlike   {@link #previous()}  , this method will keep looking for the first
	 * matching sibling until it finds a match or is out of siblings.
	 * </p>
	 * @param tag tag to match
	 * @return new Navigator instance
	 */
	abstract Navigator previous(String tag)

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
	 * Unlike   {@link #parent()}  , this method will keep traversing up the DOM
	 * until a match is found or the top of the DOM has been found
	 * </p>
	 * @param tag tag to match
	 * @return new Navigator instance
	 */
	abstract Navigator parent(String tag)

	/**
	 * Creates a new Navigator instance without the duplicate elements from the original.
	 * <p>
	 * Calling this method should not be necessary since Navigator tries to use it
	 * where possible.
	 * </p>
	 * <p>
	 * Note that in order to function correctly, this method will need to set
	 * some data on the element to serve as a hash code - which is missing in
	 * HtmlUnit's   {@link HtmlElement}   - but only if there's no id to use.
	 * </p>
	 * @return new Navigator instance
	 */
	abstract Navigator unique()

	/**
	 * Creates a new Navigator instance containing the elements matching the given
	 * (simple) CSS selector.
	 * <p>
	 * Following selectors are allowed:
	 * </p>
	 * <dl>
	 * <dt>type selectors</dt>
	 * <dd>HTML element tag names, e.g. "h1" will only match h1 elements</dd>
	 * <dt>class selectors</dt>
	 * <dd>passing ".something" will only match the elements with the class "something"</dd>
	 * <dt>id selectors</dt>
	 * <dd>pass "#theid" to math the element with id "theid"</dd>
	 * <dt>a combination of the above selectors</dt>
	 * <dd>passing "div.article" wil only match the div elements with class "article"</dd>
	 * <dt>selectors with descendant combinators</dt>
	 * <dd>passing "div.article a.more" will only match the anchors with class "more"
	 * that are descendants of a div with class "article". But when using a
	 * selector such as "div.article p#someid", remember that HtmlUnit will
	 * look for the element with the given id anywhere on the page, not
	 * just within divs with class "article".</dd>
	 * <dt>grouped selectors</dt>
	 * <dd>pass "p, div, a.someClass" to matches all paragraphs, divs and anchors
	 * (with class "someClass")</dd>
	 * </dl>
	 * @param selector selector to use to match elements
	 * @return new Navigator instance
	 */
	abstract Navigator find(String selector)

	/**
	 * Shorthand for <code>get(selector).get(indexOfElement)</code>.
	 * @param selector selector to use
	 * @param index index of the element matching the selector to return
	 * @return new Navigator instance
	 */
	Navigator find(String selector, int index) {
		find(selector)[index]
	}

	abstract Navigator find(Map<String, Object> predicates, String selector)

	/**
	 * Filters the set of elements represented by this Navigator to include only those that match
	 * the selector.
	 * @param selector a CSS selector, note only tag, id and class based selectors are supported for this method
	 * @return a new Navigator instance
	 */
	abstract Navigator filter(String selector)

	/**
	 * Shorthand for <code>attribute("id")</code>.
	 * @return the id of the first context element
	 * @see #attribute(java.lang.String)
	 */
	String id() {
		return attribute("id")
	}

	/**
	 * Shorthand for <code>attribute("id")</code>.
	 * @return the id of all context elements
	 * @see #attributes(java.lang.String)
	 */
	String[] ids() {
		return attributes("id")
	}

	/**
	 * Shorthand for <code>getByAttribute(attribute, MatchType.EQUALS, value)</code>.
	 * @param attribute attribute to consider
	 * @param value value to match exactly
	 * @return new Navigator instance
	 */
	Navigator findByAttribute(String attribute, String value) {
		return findByAttribute(attribute, EQUALS, value)
	}

	/**
	 * Creates a new Navigator instance containing all child elements of the current
	 * context elements with the given attribute matching the given value as
	 * determined by the match type.
	 *
	 * @param attribute attribute to consider
	 * @param matchType type of match to make
	 * @param value the value to match
	 * @return new Navigator instance
	 */
	abstract Navigator findByAttribute(String attribute, MatchType matchType, String value)

	/**
	 * Creates a new Navigator instance containing all child elements of the current
	 * context elements for which the value given attribute matches the given
	 * pattern.
	 * @param attribute attribute to use
	 * @param pattern pattern the value of the attribute should match
	 * @return new Navigator instance
	 * @see #findByAttributeMatching(java.lang.String, java.util.regex.Pattern)
	 */
	abstract Navigator findByAttributeMatching(String attribute, String pattern)

	/**
	 * Creates a new Navigator instance containing all child elements of the current
	 * context elements for which the value given attribute matches the given
	 * pattern.
	 * @param attribute attribute to use
	 * @param pattern pattern the value of the attribute should match
	 * @return new Navigator instance
	 * @see #findByAttributeMatching(java.lang.String, java.lang.String)
	 */
	abstract Navigator findByAttributeMatching(String attribute, Pattern pattern)

	/**
	 * Returns true if at least one of the context elements has the given class.
	 * @param valueToContain class to check for
	 * @return true if at least one of the context elements has the given class
	 */
	abstract boolean hasClass(String valueToContain)

	/**
	 * Returns true if at least one of the context elements matches the tag.
	 * @param tag tag to match
	 * @return true if at least one of the context elements matches the tag
	 */
	abstract boolean is(String tag)

	/**
	 * Creates a new Navigator instance by only retaining the elements that match
	 * the given tag.
	 * @param tag tag to match
	 * @return new Navigator instance
	 */
	abstract Navigator withTag(String tag)

	/**
	 * Creates a new Navigator instance by only retaining the elements that contain
	 * the given text, i.e.: <code>node.text().contains(textToContain)</code>
	 * will return <code>true</code> for each node.
	 * @param textToContain text the retained nodes should contain
	 * @return new Navigator instance
	 */
	abstract Navigator withTextContaining(String textToContain)

	/**
	 * Creates a new Navigator instance by only retaining the elements that contain
	 * text matching the given pattern.
	 * @param pattern the pattern to match
	 * @return new Navigator instance
	 * @see #withTextMatching(java.util.regex.Pattern)
	 */
	abstract Navigator withTextMatching(String pattern)

	/**
	 * Creates a new Navigator instance by only retaining the elements that contain
	 * text matching the given pattern.
	 * @param pattern the pattern to match
	 * @return new Navigator instance
	 * @see #withTextMatching(java.lang.String)
	 */
	abstract Navigator withTextMatching(Pattern pattern)

	/**
	 * Shorthand for <code>withAttribute(key, MatchType.EXISTING, someValueOrEvenNull)</code>
	 * @param key attribute to look for
	 * @return a filtered context
	 */
	Navigator with(String key) {
		return withAttribute(key, MatchType.EXISTING, null)
	}

	/**
	 * Shorthand for   {@link #withAttribute(java.lang.String, java.lang.String)}  .
	 * @param key key to consider
	 * @param value value to match exactly
	 * @return new Navigator instance
	 */
	Navigator with(String key, String value) {
		return withAttribute(key, value)
	}

	/**
	 * Shorthand for <code>withAttribute(key, MatchType.EQUALS, someValueOrEvenNull)</code>
	 * @param key key to consider
	 * @param value value value to match exactly
	 * @return new Navigator instance
	 */
	Navigator withAttribute(String key, String value) {
		return withAttribute(key, EQUALS, value)
	}

	/**
	 * Creates a new Navigator instance retaining only the context elements with the
	 * given attribute matching the pattern.
	 * @param key key of the attribute
	 * @param pattern pattern the value of the attribute should match
	 * @return new Navigator instance
	 * @see #withAttributeMatching(java.lang.String, java.util.regex.Pattern)
	 */
	abstract Navigator withAttributeMatching(String key, String pattern)

	/**
	 * Creates a new Navigator instance retaining only the context elements with the
	 * given attribute matching the pattern.
	 * @param key key of the attribute
	 * @param pattern pattern the value of the attribute should match
	 * @return new Navigator instance
	 * @see #withAttributeMatching(java.lang.String, java.lang.String)
	 */
	abstract Navigator withAttributeMatching(String key, Pattern pattern)

	/**
	 * Creates a new Navigator instance containing all context elements with the
	 * attribute matching the given value, as determined by the match type.
	 * @param key key to consider
	 * @param matchType type of match
	 * @param value value to match
	 * @return new Navigator instance
	 */
	abstract Navigator withAttribute(String key, MatchType matchType, String value)

	/**
	 * Shorthand for <code>withAttribute(key, MatchType.CONTAINING, someValueOrEvenNull)</code>
	 * @param key key to consider
	 * @param value value that should be contained by the attribute
	 * @return new Navigator instance
	 */
	Navigator withAttributeContaining(String key, String value) {
		return withAttribute(key, MatchType.CONTAINING, value)
	}

	/**
	 * Shorthand for <code>withAttribute("class", MatchType.CONTAINED_WITH_WHITESPACE, valueToContain)</code>
	 * @param valueToContain value
	 * @return new Navigator instance
	 */
	Navigator withClass(String valueToContain) {
		return withAttribute("class", MatchType.CONTAINED_WITH_WHITESPACE, valueToContain)
	}

	/**
	 * Shorthand for <code>withAttribute("value", MatchType.EQUALS, valueToContain)</code>
	 * @param valueToContain value
	 * @return new Navigator instance
	 */
	Navigator withValue(String valueToContain) {
		return withAttribute("value", MatchType.EQUALS, valueToContain)
	}

	/**
	 * Shorthand for <code>withAttribute("id", MatchType.EQUALS, valueToContain)</code>
	 * @param valueToContain value
	 * @return new Navigator instance
	 */
	Navigator withId(String valueToContain) {
		return withAttribute("id", MatchType.EQUALS, valueToContain)
	}

	/**
	 * Shorthand for <code>withAttribute("type", MatchType.EQUALS, valueToContain)</code>
	 * @param type type to match
	 * @return new Navigator instance
	 */
	Navigator withType(String type) {
		return withAttribute("type", type)
	}

	/**
	 * Shorthand for <code>withAttribute("name", MatchType.EQUALS, valueToContain)</code>
	 * @param name name to match
	 * @return new Navigator instance
	 */
	Navigator withName(String name) {
		return withAttribute("name", name)
	}

	/**
	 * Shorthand for <code>hasAttribute(key, MatchType.EXISTING, anyValue)</code>
	 * @param key of the attribute
	 * @return new Navigator instance
	 */
	boolean hasAttribute(String key) {
		return hasAttribute(key, MatchType.EXISTING, null)
	}

	/**
	 * Shorthand for <code>hasAttribute(key, MatchType.EQUALS, value)</code>
	 * @param key of the attribute
	 * @param value value to match
	 * @return new Navigator instance
	 */
	boolean hasAttribute(String key, String value) {
		return hasAttribute(key, MatchType.EQUALS, value)
	}

	/**
	 * Shorthand for <code>hasAttribute("checked")</code>.
	 * @return true when the first element is checked
	 */
	abstract boolean isChecked()

	;

	/**
	 * Shorthand for <code>hasAttribute("selected")</code>.
	 * @return true when the first element is selected
	 */
	abstract boolean isSelected()

	;

	/**
	 * Shorthand for <code>hasAttribute("disabled")</code>.
	 * @return true when the first element is disabled
	 */
	boolean isDisabled() {
		return hasAttribute("disabled")
	}

	/**
	 * Shorthand for <code>hasAttribute("readonly")</code>.
	 * @return true when the first element is readonly
	 */
	boolean isReadOnly() {
		return hasAttribute("readonly")
	}

	/**
	 * Returns true if one of the context elements has a value for the attribute
	 * matching the given value as defined by the match type.
	 * @param key key of the attribute to match
	 * @param matchType type of match to make
	 * @param value the value to match
	 * @return true if one of the context elements matches
	 */
	abstract boolean hasAttribute(String key, MatchType matchType, String value)

	/**
	 * Returns the text content of the first context element.
	 * @return the text content of the first context element
	 */
	abstract String text()

	/**
	 * Returns the text contents of all context elements.
	 * @return the text contents of all context elements
	 */
	abstract String[] texts()

	/**
	 * Returns the trimmed text content of the first context element.
	 * <p>
	 * All whitespace characters, including newlines are condensed into a
	 * single space - leading and trailing whitespace is removed.
	 * </p>
	 * @return the trimmed text content of the first context element
	 */
	abstract String trimmedText()

	/**
	 * Returns the trimmed text contents of all context elements.
	 * <p>
	 * All whitespace characters, including newlines are condensed into a
	 * single space - leading and trailing whitespace is removed.
	 * </p>
	 * @return the trimmed text contents of all context elements
	 */
	abstract String[] trimmedTexts()

	/**
	 * Returns the value of the given attribute of the first context element.
	 * @param key key of the attribute
	 * @return the value of the given attribute of the first context element
	 */
	abstract String attribute(String key)

	/**
	 * Returns the values of the given attribute of all context elements.
	 * @param key key of the attribute
	 * @return the values of the given attribute of all context elements
	 */
	abstract String[] attributes(String key)

	/**
	 * Shorthand for <code>attribute("name")</code>.
	 * @return the value of the name attribute of the first element
	 * @see #attribute(java.lang.String)
	 */
	String name() {
		return attribute("name")
	}

	/**
	 * Shorthand for <code>attributes("name")</code>.
	 * @return the value of the name attribute of the elements
	 * @see #attributes(java.lang.String)
	 */
	String[] names() {
		return attributes("name")
	}

	/**
	 * Shorthand for <code>attribute("type")</code>.
	 * @return the value of the type attribute of the first element
	 * @see #attribute(java.lang.String)
	 */
	String type() {
		return attribute("type")
	}

	/**
	 * Shorthand for <code>attributes("type")</code>.
	 * @return the value of the type attribute of the elements
	 * @see #attributes(java.lang.String)
	 */
	String[] types() {
		return attributes("type")
	}

	/**
	 * Shorthand for <code>attribute("class")</code>.
	 * @return the value of the class attribute of the first element
	 * @see #attribute(java.lang.String)
	 */
	String classValue() {
		return attribute("class")
	}

	/**
	 * Shorthand for <code>attributes("class")</code>.
	 * @return the value of the class attribute of the elements
	 * @see #attributes(java.lang.String)
	 */
	String[] classValues() {
		return attributes("class")
	}

	/**
	 * Returns the value of the first context element for input elements
	 * (including textarea, select and button).
	 * <p>
	 * In the case of a select, the value of the first selected option is returned.
	 * </p>
	 * <p>
	 * <strong>Note:</strong> use   {@link #values()}   if you want all selected
	 * options of a multiple select or if you want the values of all context
	 * elements.
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

	/**
	 * Returns the values of all form input context elements.
	 * <p>
	 * In the case of a select, the values of all selected options are returned.
	 * </p>
	 * @return the values of all form input context elements
	 */
	abstract String[] values()

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
	 * Returns the first context element (not wrapped).
	 * @return the first context element (not wrapped)
	 */
	WebElement firstElement() {
		return getElement(0)
	}

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
		return new DojIterator(this)
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
class DojIterator implements Iterator<Navigator> {

	private int index
	private Navigator navigator

	DojIterator(Navigator navigator) {
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


