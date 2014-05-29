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

import org.openqa.selenium.By
import org.openqa.selenium.WebElement

import static java.util.Collections.EMPTY_LIST

class CssSelector {

	enum Type {
		ELEMENT(""),
		HTML_CLASS("."),
		ID("#"),
		DESCENDANT("")

		final prefix

		Type(String prefix) {
			this.prefix = prefix
		}
	}

	private static final CssSelector DESCENDANT_SELECTOR = new CssSelector(Type.DESCENDANT, " ")
	private static final String CSS_SELECTOR_SPECIAL_CHARS_PATTERN = '[ !"#$%&\'\\(\\)*+,./:;<=>?@\\[\\]^`\\{|\\}~\\\\]'

	final Type type
	final String value

	CssSelector(Type type, String value) {
		this.type = type
		this.value = value
	}

	// TODO: better name

	static String escape(String value) {
		value.replaceAll("($CSS_SELECTOR_SPECIAL_CHARS_PATTERN)", '\\\\$1')
	}

	static boolean matches(WebElement element, String selectorString) {
		def selectors = compile(selectorString)
		selectors.any { List<CssSelector> selectorGroup ->
			selectorGroup.every { CssSelector selector ->
				selector.matches(element)
			}
		}
	}

	boolean matches(WebElement element) {
		// TODO: switch = failure of object orientation
		switch (type) {
			case Type.ELEMENT:
				return element.tagName == value
			case Type.HTML_CLASS:
				return element.getAttribute("class") =~ /(^|\s)$value($|\s)/
			case Type.ID:
				return element.getAttribute("id") == value
			default:
				return false
		}
	}

	List<WebElement> select(WebElement element) {
		switch (type) {
			case Type.ELEMENT:
				return element.findElements(By.tagName(value))
			case Type.HTML_CLASS:
				return element.findElements(By.className(value))
			case Type.ID:
				return element.findElements(By.id(value))
			default:
				return EMPTY_LIST
		}
	}

	String toString() {
		"${type.prefix}$value"
	}

	// TODO: should be private

	static List<List<CssSelector>> compile(String groupSelector) {
		List<List<CssSelector>> result = []
		groupSelector.split(",").each { String part ->
			part = part.trim()
			if (part) {
				List<CssSelector> compiled = compileSingle(part)
				if (compiled) {
					result << compiled
				}
			}
		}
		return result
	}

	private static List<CssSelector> compileSingle(String selector) {
		List<CssSelector> result = []
		boolean first = true
		selector.split(/\s/).each { String part ->
			part = part.trim()
			if (part) {
				if (first) {
					first = false
				} else {
					result << DESCENDANT_SELECTOR
				}
				compileSimpleSelector(part, result)
			}
		}
		return result
	}

	private static void compileSimpleSelector(String selector, List<CssSelector> list) {
		tokenize(selector).each { String part ->
			if (part) {
				if (part.startsWith(".")) {
					list << new CssSelector(Type.HTML_CLASS, part.substring(1))
				} else if (part.startsWith("#")) {
					list << new CssSelector(Type.ID, part.substring(1))
				} else {
					list << new CssSelector(Type.ELEMENT, part)
				}
			}
		}
	}

	private static List<String> tokenize(String selector) {
		List<String> tokens = []
		int previous = 0
		int max = selector.length()
		for (int index = 0; index < max; ++index) {
			char character = selector.charAt(index)
			if (index > 0 && (character == '.' || character == '#')) {
				tokens << selector.substring(previous, index)
				previous = index
			}
		}
		tokens << selector.substring(previous)
		return tokens
	}
}