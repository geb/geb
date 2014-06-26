/*
 * Copyright 2010 the original author or authors.
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
import geb.Page
import org.openqa.selenium.WebElement

import static java.util.Collections.EMPTY_LIST

/**
 * Implementation of an empty Navigator object - helps keep the other code simple.
 */
class EmptyNavigator extends AbstractNavigator {

	EmptyNavigator(Browser browser) {
		super(browser)
	}

	@Override
	Navigator find(String selector) { this }

	@Override
	Navigator filter(String selector) { this }

	@Override
	Navigator filter(Map<String, Object> predicates) { this }

	@Override
	Navigator not(String selector) { this }

	Navigator not(Map<String, Object> predicates, String selector) { this }

	Navigator not(Map<String, Object> predicates) { this }

	@Override
	Navigator click() { this }

	@Override
	Navigator click(Class<? extends Page> pageClass) {
		throw new UnsupportedOperationException("not supported on empty navigator objects")
	}

	@Override
	Navigator click(List<Class<? extends Page>> pageClasses) {
		throw new UnsupportedOperationException("not supported on empty navigator objects")
	}

	@Override
	Navigator head() { this }

	@Override
	Navigator first() { this }

	@Override
	Collection<WebElement> allElements() { EMPTY_LIST }

	@Override
	WebElement getElement(int index) { null }

	@Override
	List<WebElement> getElements(Range range) { EMPTY_LIST }

	@Override
	List<WebElement> getElements(Collection indexes) { EMPTY_LIST }

	@Override
	boolean hasClass(String valueToContain) { false }

	@Override
	boolean is(String tag) { false }

	@Override
	boolean isEmpty() { true }

	@Override
	Navigator last() { this }

	@Override
	Navigator tail() { this }

	@Override
	Navigator next() { this }

	@Override
	Navigator next(Map<String, Object> attributes) { this }

	@Override
	Navigator next(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator nextAll() { this }

	@Override
	Navigator nextAll(Map<String, Object> attributes) { this }

	@Override
	Navigator nextAll(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator nextUntil(Map<String, Object> attributes) { this }

	@Override
	Navigator nextUntil(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator previous() { this }

	@Override
	Navigator previous(Map<String, Object> attributes) { this }

	@Override
	Navigator previous(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator prevAll() { this }

	@Override
	Navigator prevAll(Map<String, Object> attributes) { this }

	@Override
	Navigator prevAll(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator prevUntil(Map<String, Object> attributes) { this }

	@Override
	Navigator prevUntil(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator parent() { this }

	@Override
	Navigator parent(Map<String, Object> attributes) { this }

	@Override
	Navigator parent(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator parents() { this }

	@Override
	Navigator parents(Map<String, Object> attributes) { this }

	@Override
	Navigator parents(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator parentsUntil(Map<String, Object> attributes) { this }

	@Override
	Navigator parentsUntil(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator closest(Map<String, Object> attributes) { this }

	@Override
	Navigator closest(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator children() { this }

	@Override
	Navigator children(Map<String, Object> attributes) { this }

	@Override
	Navigator children(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator siblings() { this }

	@Override
	Navigator siblings(Map<String, Object> attributes) { this }

	@Override
	Navigator siblings(Map<String, Object> attributes = [:], String selector) { this }

	@Override
	Navigator remove(int index) { this }

	@Override
	int size() { 0 }

	@Override
	boolean isDisplayed() { false }

	@Override
	boolean isDisabled() {
		throw new UnsupportedOperationException("Cannot check value of 'disabled' attribute for an EmptyNavigator")
	}

	@Override
	boolean isEnabled() {
		throw new UnsupportedOperationException("Cannot check value of 'disabled' attribute for an EmptyNavigator")
	}

	@Override
	boolean isReadOnly() {
		throw new UnsupportedOperationException("Cannot check value of 'readonly' attribute for an EmptyNavigator")
	}

	@Override
	boolean isEditable() {
		throw new UnsupportedOperationException("Cannot check value of 'readonly' attribute for an EmptyNavigator")
	}

	@Override
	String tag() { null }

	@Override
	String text() { null }

	@Override
	String getAttribute(String name) { null }

	@Override
	List<String> classes() { EMPTY_LIST }

	@Override
	def value() { null }

	@Override
	Navigator leftShift(value) { this }

	@Override
	Navigator getAt(int index) { this }

	@Override
	Navigator getAt(Range range) { this }

	@Override
	Navigator getAt(Collection indexes) { this }

	@Override
	Navigator verifyNotEmpty() { throw new EmptyNavigatorException() }

	@Override
	Navigator value(value) { this }

	@Override
	Navigator unique() { this }

	@Override
	String toString() { "[]" }

	def methodMissing(String name, arguments) {
		if (!arguments) this
		else throw new MissingMethodException(name, getClass(), arguments)
	}

	def propertyMissing(String name) {
		if (name.startsWith("@")) {
			null
		} else {
			throw new MissingPropertyException(name, getClass())
		}
	}
}
