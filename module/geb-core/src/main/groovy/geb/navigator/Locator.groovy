/*
 * Copyright 2014 the original author or authors.
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

import org.openqa.selenium.By

/**
 * Allows to create {@link geb.navigator.Navigator}s by selecting {@link org.openqa.selenium.WebElement}s
 * using different criteria (CSS selectors, attribute maps, indexes, ranges and {@link org.openqa.selenium.By} selectors).
 */
public interface Locator {

	/**
	 * Shorthand for <code>find(null, selector, null)</code>
	 *
	 * @param selector
	 * @return new Navigator
	 */
	Navigator find(String selector)

	/**
	 * Shorthand for <code>find(selector)</code>
	 *
	 * @param selector
	 * @return new Navigator
	 * @see #find(java.lang.String)
	 */
	Navigator $(String selector)

	/**
	 * Shorthand for <code>find(selector)[indexOfElement]</code>.
	 * @param selector a CSS selector
	 * @param index index of the required element in the selection
	 * @return new Navigator instance containing a single element
	 */
	Navigator find(String selector, int index)

	/**
	 * Shorthand for <code>find(null, selector, range)</code>
	 *
	 * @param selector The css selector
	 * @return new Navigator
	 */
	Navigator find(String selector, Range<Integer> range)

	/**
	 * Shorthand for <code>find(selector, index)</code>.
	 *
	 * @param selector a CSS selector
	 * @param index index of the required element in the selection
	 * @return new Navigator instance containing a single element
	 * @see #find(java.lang.String, int)
	 */
	Navigator $(String selector, int index)

	/**
	 * Shorthand for <code>find(selector, range)</code>
	 *
	 * @param selector The css selector
	 * @return new Navigator
	 * @see #find(java.lang.String, groovy.lang.Range)
	 */
	Navigator $(String selector, Range<Integer> range)

	/**
	 * Shorthand for <code>find(predicates, bySelector)</code>
	 *
	 * @param bySelector a WebDriver By selector
	 * @param predicates a Map with keys representing attributes and values representing required values or patterns
	 * @return a new Navigator instance containing the matched elements
	 * @see #find(java.util.Map, org.openqa.selenium.By)
	 */
	Navigator $(Map<String, Object> attributes, By bySelector)

	/**
	 * Selects elements by both <code>By</code> selector and attributes. For example <code>find(By.tagName("input"), name: "firstName")</code> will select
	 * all input elements with the name "firstName".
	 * @param bySelector a WebDriver By selector
	 * @param predicates a Map with keys representing attributes and values representing required values or patterns
	 * @return a new Navigator instance containing the matched elements
	 */
	Navigator find(Map<String, Object> attributes, By bySelector)

	/**
	 * Shorthand for <code>find(predicates, bySelector, index)</code>
	 *
	 * @param bySelector a WebDriver By selector
	 * @return new Navigator
	 * @see #find(java.util.Map, org.openqa.selenium.By, int)
	 */
	Navigator $(Map<String, Object> attributes, By bySelector, int index)

	/**
	 * Shorthand for <code>find(predicates, bySelector, index..index)</code>
	 *
	 * @param bySelector a WebDriver By selector
	 * @return new Navigator
	 */
	Navigator find(Map<String, Object> attributes, By bySelector, int index)

	/**
	 * Shorthand for <code>find(predicates, bySelector, range)</code>
	 *
	 * @param bySelector a WebDriver By selector
	 * @return new Navigator instance containing the matched elements
	 * @see #find(java.util.Map, org.openqa.selenium.By, groovy.lang.Range)
	 */
	Navigator $(Map<String, Object> attributes, By bySelector, Range<Integer> range)

	/**
	 * Creates a new Navigator instance containing the elements matching the given
	 * <code>By</code> type selector. Any <code>By</code> type capabilities supported by the underlying WebDriver instance are supported.
	 * @param bySelector a WebDriver By selector
	 * @return new Navigator instance containing the matched elements
	 */
	Navigator find(Map<String, Object> attributes, By bySelector, Range<Integer> range)

	/**
	 * Shorthand for <code>find(bySelector)</code>
	 *
	 * @param bySelector a WebDriver By selector
	 * @return new Navigator
	 * @see #find(org.openqa.selenium.By)
	 */
	Navigator $(By bySelector)

	/**
	 * Shorthand for <code>find(null, bySelector, null)</code>
	 *
	 * @param bySelector a WebDriver By selector
	 * @return new Navigator
	 */
	Navigator find(By bySelector)

	/**
	 * Shorthand for <code>find(bySelector, index)</code>.
	 *
	 * @param bySelector a WebDriver By selector
	 * @param index index of the required element in the selection
	 * @return new Navigator instance containing a single element
	 * @see #find(org.openqa.selenium.By, int)
	 */
	Navigator $(By bySelector, int index)

	/**
	 * Shorthand for <code>find(bySelector)[indexOfElement]</code>.
	 * @param bySelector a WebDriver By selector
	 * @param index index of the required element in the selection
	 * @return new Navigator instance containing a single element
	 */
	Navigator find(By bySelector, int index)

	/**
	 * Shorthand for <code>find(bySelector, range)</code>
	 *
	 * @param bySelector a WebDriver By selector
	 * @return new Navigator
	 * @see #find(org.openqa.selenium.By, groovy.lang.Range)
	 */
	Navigator $(By bySelector, Range<Integer> range)

	/**
	 * Shorthand for <code>find(null, bySelector, range)</code>
	 *
	 * @param bySelector a WebDriver By selector
	 * @return new Navigator
	 */
	Navigator find(By bySelector, Range<Integer> range)

	/**
	 * Creates a new Navigator instance containing the elements whose attributes match the specified values or patterns.
	 * The key 'text' can be used to match the text contained in elements. Regular expression Pattern objects may be
	 * used as values.
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
	Navigator find(Map<String, Object> attributes)

	/**
	 * Shorthand for <code>find(predicates)</code>
	 *
	 * @param predicates a Map with keys representing attributes and values representing required values or patterns
	 * @return a new Navigator instance containing the matched elements
	 * @see #find(java.util.Map)
	 */
	Navigator $(Map<String, Object> attributes)

	/**
	 * Shorthand for <code>find(predicates, null, index..index)</code>
	 *
	 * @param selector
	 * @return new Navigator
	 */
	Navigator find(Map<String, Object> attributes, int index)

	/**
	 * Shorthand for <code>find(predicates, null, range)</code>
	 *
	 * @param predicates attribute predicates
	 * @param predicates range the range of matches to select
	 * @return new Navigator
	 */
	Navigator find(Map<String, Object> attributes, Range<Integer> range)

	/**
	 * Shorthand for <code>find(predicates, index)</code>
	 *
	 * @param selector
	 * @return new Navigator
	 * @see #find(java.util.Map, int)
	 */
	Navigator $(Map<String, Object> attributes, int index)

	/**
	 * Shorthand for <code>find(predicates, range)</code>
	 *
	 * @param predicates attribute predicates
	 * @param predicates range the range of matches to select
	 * @return new Navigator
	 * @see #find(java.util.Map, groovy.lang.Range)
	 */
	Navigator $(Map<String, Object> attributes, Range<Integer> range)

	/**
	 * Selects elements by both CSS selector and attributes. For example find("input", name: "firstName") will select
	 * all input elements with the name "firstName".
	 * @param selector a CSS selector
	 * @param predicates a Map with keys representing attributes and values representing required values or patterns
	 * @return a new Navigator instance containing the matched elements
	 */
	Navigator find(Map<String, Object> attributes, String selector)

	/**
	 * Shorthand for <code>find(predicates, selector)</code>
	 *
	 * @param selector a CSS selector
	 * @param predicates a Map with keys representing attributes and values representing required values or patterns
	 * @return a new Navigator instance containing the matched elements
	 * @see #find(java.util.Map, java.lang.String)
	 */
	Navigator $(Map<String, Object> attributes, String selector)

	/**
	 * Shorthand for <code>find(predicates, selector, index..index)</code>
	 *
	 * @param selector
	 * @return new Navigator
	 */
	Navigator find(Map<String, Object> attributes, String selector, int index)

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
	Navigator find(Map<String, Object> attributes, String selector, Range<Integer> range)

	/**
	 * Shorthand for <code>find(predicates, selector, index)</code>
	 *
	 * @param selector
	 * @return new Navigator
	 * @see #find(java.util.Map, java.lang.String, int)
	 */
	Navigator $(Map<String, Object> attributes, String selector, int index)

	/**
	 * Shorthand for <code>find(predicates, selector, range)</code>
	 *
	 * @param selector a CSS selector
	 * @return new Navigator instance containing the matched elements
	 * @see #find(java.util.Map, groovy.lang.Range)
	 */
	Navigator $(Map<String, Object> attributes, String selector, Range<Integer> range)
}