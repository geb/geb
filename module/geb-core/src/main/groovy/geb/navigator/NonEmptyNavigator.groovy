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
import geb.error.UnableToSetElementException
import geb.error.UndefinedAtCheckerException
import geb.error.UnexpectedPageException
import geb.textmatching.TextMatcher
import org.openqa.selenium.By
import org.openqa.selenium.NoSuchElementException
import org.openqa.selenium.WebElement

import java.util.regex.Pattern

import static java.util.Collections.EMPTY_LIST

class NonEmptyNavigator extends AbstractNavigator {

	protected final static BOOLEAN_ATTRIBUTES = ['async', 'autofocus', 'autoplay', 'checked', 'compact', 'complete',
		'controls', 'declare', 'defaultchecked', 'defaultselected', 'defer', 'disabled', 'draggable', 'ended',
		'formnovalidate', 'hidden', 'indeterminate', 'iscontenteditable', 'ismap', 'itemscope', 'loop',
		'multiple', 'muted', 'nohref', 'noresize', 'noshade', 'novalidate', 'nowrap', 'open', 'paused',
		'pubdate', 'readonly', 'required', 'reversed', 'scoped', 'seamless', 'seeking', 'selected',
		'spellcheck', 'truespeed', 'willvalidate']

	protected final static ELEMENTS_WITH_MUTABLE_VALUE = ['input', 'select', 'textarea']

	protected final List<WebElement> contextElements

	NonEmptyNavigator(Browser browser, Collection<? extends WebElement> contextElements) {
		super(browser)
		this.contextElements = contextElements.toList().asImmutable()
	}

	protected Navigator navigatorFor(Collection<WebElement> contextElements) {
		browser.navigatorFactory.createFromWebElements(contextElements)
	}

	@Override
	Navigator find(String selectorString) {
		List<WebElement> list = []
		for (contextElement in contextElements) {
			list.addAll contextElement.findElements(By.cssSelector(selectorString))
		}
		navigatorFor list
	}

	@Override
	Navigator find(Map<String, Object> predicates, String selector) {
		selector = optimizeSelector(selector, predicates)
		if (selector) {
			find(selector).filter(predicates)
		} else {
			find(predicates)
		}
	}

	@Override
	Navigator filter(String selectorString) {
		navigatorFor contextElements.findAll { element ->
			CssSelector.matches(element, selectorString)
		}
	}

	@Override
	Navigator filter(Map<String, Object> predicates) {
		navigatorFor contextElements.findAll { matches(it, predicates) }
	}

	@Override
	Navigator not(String selectorString) {
		navigatorFor contextElements.findAll { element ->
			!CssSelector.matches(element, selectorString)
		}
	}

	@Override
	Navigator not(Map<String, Object> predicates, String selectorString) {
		navigatorFor contextElements.findAll { element ->
			!(CssSelector.matches(element, selectorString) && matches(element, predicates))
		}
	}

	@Override
	Navigator not(Map<String, Object> predicates) {
		navigatorFor contextElements.findAll { element ->
			!matches(element, predicates)
		}
	}

	@Override
	Navigator getAt(int index) {
		navigatorFor(Collections.singleton(getElement(index)))
	}

	@Override
	Navigator getAt(Range range) {
		navigatorFor getElements(range)
	}

	Navigator getAt(EmptyRange range) {
		new EmptyNavigator(browser)
	}

	@Override
	Navigator getAt(Collection indexes) {
		navigatorFor getElements(indexes)
	}

	@Override
	Collection<WebElement> allElements() {
		contextElements as WebElement[]
	}

	@Override
	WebElement getElement(int index) {
		contextElements[index]
	}

	@Override
	List<WebElement> getElements(Range range) {
		contextElements[range]
	}

	List<WebElement> getElements(EmptyRange range) {
		EMPTY_LIST
	}

	@Override
	List<WebElement> getElements(Collection indexes) {
		contextElements[indexes]
	}

	@Override
	Navigator remove(int index) {
		int size = size()
		if (!(index in -size..<size)) {
			this
		} else if (size == 1) {
			new EmptyNavigator(browser)
		} else {
			navigatorFor(contextElements - contextElements[index])
		}
	}

	@Override
	Navigator next() {
		navigatorFor collectElements {
			it.findElement By.xpath("following-sibling::*")
		}
	}

	@Override
	Navigator next(String selectorString) {
		collectFollowingSiblings {
			it.find { CssSelector.matches(it, selectorString) }
		}
	}

	@Override
	Navigator next(Map<String, Object> attributes) {
		collectFollowingSiblings {
			it.find { matches(it, attributes) }
		}
	}

	@Override
	Navigator nextAll() {
		collectFollowingSiblings()
	}

	@Override
	Navigator nextAll(String selectorString) {
		collectFollowingSiblings {
			it.findAll { CssSelector.matches(it, selectorString) }
		}
	}

	@Override
	Navigator nextAll(Map<String, Object> attributes) {
		collectFollowingSiblings {
			it.findAll { matches(it, attributes) }
		}
	}

	@Override
	Navigator nextUntil(String selectorString) {
		collectFollowingSiblings {
			collectUntil(it, selectorString)
		}
	}

	@Override
	Navigator nextUntil(Map<String, Object> attributes) {
		collectFollowingSiblings {
			collectUntil(it, attributes)
		}
	}

	@Override
	Navigator previous() {
		collectPreviousSiblings {
			it ? it.last() : EMPTY_LIST
		}
	}

	@Override
	Navigator previous(String selectorString) {
		collectPreviousSiblings {
			it.reverse().find { CssSelector.matches(it, selectorString) }
		}
	}

	@Override
	Navigator previous(Map<String, Object> attributes) {
		collectPreviousSiblings {
			it.reverse().find { matches(it, attributes) }
		}
	}

	@Override
	Navigator prevAll() {
		collectPreviousSiblings()
	}

	@Override
	Navigator prevAll(String selectorString) {
		collectPreviousSiblings {
			it.reverse().findAll { CssSelector.matches(it, selectorString) }
		}
	}

	@Override
	Navigator prevAll(Map<String, Object> attributes) {
		collectPreviousSiblings {
			it.reverse().findAll { matches(it, attributes) }
		}
	}

	@Override
	Navigator prevUntil(String selectorString) {
		collectPreviousSiblings {
			collectUntil(it.reverse(), selectorString)
		}
	}

	@Override
	Navigator prevUntil(Map<String, Object> attributes) {
		collectPreviousSiblings {
			collectUntil(it.reverse(), attributes)
		}
	}

	@Override
	Navigator parent() {
		navigatorFor collectElements {
			it.findElement By.xpath("parent::*")
		}
	}

	@Override
	Navigator parent(String selectorString) {
		parent().filter(selectorString)
	}

	@Override
	Navigator parent(Map<String, Object> attributes) {
		parent().filter(attributes)
	}

	@Override
	Navigator parents() {
		collectParents {
			it.reverse()
		}
	}

	@Override
	Navigator parents(String selectorString) {
		collectParents {
			it.reverse().findAll { CssSelector.matches(it, selectorString) }
		}
	}

	@Override
	Navigator parents(Map<String, Object> attributes) {
		collectParents {
			it.reverse().findAll { matches(it, attributes) }
		}
	}

	@Override
	Navigator parentsUntil(String selectorString) {
		collectParents {
			collectUntil(it.reverse(), selectorString)
		}
	}

	@Override
	Navigator parentsUntil(Map<String, Object> attributes) {
		collectParents {
			collectUntil(it.reverse(), attributes)
		}
	}

	@Override
	Navigator closest(String selectorString) {
		collectParents {
			it.reverse().find { CssSelector.matches(it, selectorString) }
		}
	}

	@Override
	Navigator closest(Map<String, Object> attributes) {
		collectParents {
			it.reverse().find { matches(it, attributes) }
		}
	}

	@Override
	Navigator children() {
		collectChildren()
	}

	@Override
	Navigator children(String selectorString) {
		children().filter(selectorString)
	}

	@Override
	Navigator children(Map<String, Object> attributes) {
		children().filter(attributes)
	}

	@Override
	Navigator siblings() {
		collectSiblings()
	}

	@Override
	Navigator siblings(String selectorString) {
		siblings().filter(selectorString)
	}

	@Override
	Navigator siblings(Map<String, Object> attributes) {
		siblings().filter(attributes)
	}

	@Override
	boolean hasClass(String valueToContain) {
		any { valueToContain in it.classes() }
	}

	@Override
	boolean is(String tag) {
		contextElements.any { tag.equalsIgnoreCase(it.tagName) }
	}

	@Override
	boolean isDisplayed() {
		firstElement()?.displayed ?: false
	}

	@Override
	boolean isDisabled() {
		ensureTagIn(['button', 'input', 'option', 'select', 'textarea'], 'disabled')

		def value = getAttribute("disabled")
		// Different drivers return different values here
		(value == "disabled" || value == "true")
	}

	@Override
	boolean isEnabled() {
		return !disabled
	}

	@Override
	boolean isReadOnly() {
		ensureTagIn(['input', 'textarea'], 'readonly')

		def value = getAttribute("readonly")
		(value == "readonly" || value == "true")
	}

	@Override
	boolean isEditable() {
		return !readOnly
	}

	@Override
	String tag() {
		firstElement().tagName
	}

	@Override
	String text() {
		firstElement().text
	}

	@Override
	String getAttribute(String name) {
		def attribute = firstElement().getAttribute(name)
		if (attribute == 'false' && name in BOOLEAN_ATTRIBUTES) {
			attribute = null
		}

		attribute == null ? "" : attribute
	}

	@Override
	List<String> classes() {
		contextElements.head().getAttribute("class")?.tokenize()?.unique()?.sort() ?: EMPTY_LIST
	}

	@Override
	def value() {
		getInputValue(contextElements.head())
	}

	@Override
	Navigator value(value) {
		setInputValues(contextElements, value)
		this
	}

	@Override
	Navigator leftShift(value) {
		contextElements.each {
			it.sendKeys value
		}
		this
	}

	@Override
	Navigator click() {
		contextElements.first().click()
		this
	}

	@Override
	Navigator click(Class<? extends Page> pageClass) {
		click()
		browser.page(pageClass)
		def at = false
		def assertionError = null
		def throwable = null
		try {
			at = browser.verifyAt()
		} catch (AssertionError e) {
			assertionError = e
		} catch (UndefinedAtCheckerException e) {
			at = true
		} catch (Throwable e) {
			throwable = e
			throw e
		} finally {
			if (!at && !throwable) {
				throw new UnexpectedPageException(pageClass, (Throwable) assertionError)
			}
		}
		this
	}

	@Override
	Navigator click(List<Class<? extends Page>> potentialPageClasses) {
		click()
		browser.page(* potentialPageClasses)
		this
	}

	@Override
	int size() {
		contextElements.size()
	}

	@Override
	boolean isEmpty() {
		size() == 0
	}

	@Override
	Navigator head() {
		first()
	}

	@Override
	Navigator first() {
		navigatorFor(Collections.singleton(firstElement()))
	}

	@Override
	Navigator last() {
		navigatorFor(Collections.singleton(lastElement()))
	}

	@Override
	Navigator tail() {
		navigatorFor contextElements.tail()
	}

	@Override
	Navigator verifyNotEmpty() {
		this
	}

	@Override
	Navigator unique() {
		new NonEmptyNavigator(browser, contextElements.unique(false))
	}

	@Override
	String toString() {
		contextElements*.toString()
	}

	@Override
	def methodMissing(String name, arguments) {
		if (!arguments) {
			def navigator = navigatorFor collectElements {
				it.findElements By.name(name)
			}
			if (!navigator.empty) {
				return navigator
			}
		}
		throw new MissingMethodException(name, getClass(), arguments)
	}

	@Override
	def propertyMissing(String name) {
		switch (name) {
			case ~/@.+/:
				return getAttribute(name.substring(1))
			default:
				def inputs = collectElements {
					it.findElements(By.name(name))
				}

				if (inputs) {
					return getInputValues(inputs)
				} else {
					throw new MissingPropertyException(name, getClass())
				}
		}
	}

	def propertyMissing(String name, value) {
		def inputs = collectElements {
			it.findElements(By.name(name))
		}

		if (inputs) {
			setInputValues(inputs, value)
		} else {
			throw new MissingPropertyException(name, getClass())
		}
	}

	/**
	 * Optimizes the selector if the predicates contains `class` or `id` keys that map to strings. Note this method has
	 * a side-effect in that it _removes_ those keys from the predicates map.
	 */
	protected String optimizeSelector(String selector, Map<String, Object> predicates) {
		if (!selector) {
			return selector
		}

		def buffer = new StringBuilder(selector)
		if (predicates.containsKey("id") && predicates["id"] in String) {
			buffer << "#" << CssSelector.escape(predicates.remove("id"))
		}
		if (predicates.containsKey("class") && predicates["class"] in String) {
			predicates.remove("class").split(/\s+/).each { className ->
				buffer << "." << CssSelector.escape(className)
			}
		}
		if (buffer[0] == "*" && buffer.length() > 1) {
			buffer.deleteCharAt(0)
		}
		return buffer.toString()
	}

	protected boolean matches(WebElement element, Map<String, Object> predicates) {
		def result = predicates.every { name, requiredValue ->
			def actualValue
			switch (name) {
				case "text": actualValue = element.text; break
				case "class": actualValue = element.getAttribute("class")?.tokenize(); break
				default: actualValue = element.getAttribute(name)
			}
			matches(actualValue, requiredValue)
		}
		result
	}

	protected boolean matches(String actualValue, String requiredValue) {
		actualValue == requiredValue
	}

	protected boolean matches(String actualValue, Pattern requiredValue) {
		actualValue ==~ requiredValue
	}

	protected boolean matches(String actualValue, TextMatcher matcher) {
		matcher.matches(actualValue)
	}

	protected boolean matches(Collection<String> actualValue, String requiredValue) {
		requiredValue in actualValue
	}

	protected boolean matches(Collection<String> actualValue, Pattern requiredValue) {
		actualValue.any { it ==~ requiredValue }
	}

	protected boolean matches(Collection<String> actualValue, TextMatcher matcher) {
		actualValue.any { matcher.matches(it) }
	}

	protected getInputValues(Collection<WebElement> inputs) {
		def values = []
		inputs.each { WebElement input ->
			def value = getInputValue(input)
			if (value != null) {
				values << value
			}
		}
		return values.size() < 2 ? values[0] : values
	}

	protected getInputValue(WebElement input) {
		def value = null
		def type = input.getAttribute("type")
		if (input.tagName == "select") {
			def select = new SelectFactory().createSelectFor(input)
			if (select.multiple) {
				value = select.allSelectedOptions.collect { getValue(it) }
			} else {
				value = getValue(select.firstSelectedOption)
			}
		} else if (type in ["checkbox", "radio"]) {
			if (input.isSelected()) {
				value = getValue(input)
			} else {
				if (type == "checkbox") {
					value = false
				}
			}
		} else {
			value = getValue(input)
		}
		value
	}

	protected void setInputValues(Collection<WebElement> inputs, value) {
		def unsupportedElements = inputs*.tagName - ELEMENTS_WITH_MUTABLE_VALUE

		if (unsupportedElements) {
			throw new UnableToSetElementException(*unsupportedElements)
		}

		inputs.each { WebElement input ->
			setInputValue(input, value)
		}
	}

	protected void setInputValue(WebElement input, value) {
		if (input.tagName == "select") {
			setSelectValue(input, value)
		} else if (input.getAttribute("type") == "checkbox") {
			if (getValue(input) == value.toString() || value == true) {
				if (!input.isSelected()) {
					input.click()
				}
			} else if (input.isSelected()) {
				input.click()
			}
		} else if (input.getAttribute("type") == "radio") {
			if (getValue(input) == value.toString() || labelFor(input) == value.toString()) {
				input.click()
			}
		} else if (input.getAttribute("type") == "file") {
			input.sendKeys value as String
		} else {
			input.clear()
			input.sendKeys value as String
		}
	}

	protected getValue(WebElement input) {
		input?.getAttribute("value")
	}

	protected setSelectValue(WebElement element, value) {
		def select = new SelectFactory().createSelectFor(element)

		if (value == null || (value instanceof Collection && value.empty)) {
			select.deselectAll()
			return
		}

		def multiple = select.multiple
		def valueStrings
		if (multiple) {
			valueStrings = (value instanceof Collection ? new LinkedList(value) : [value])*.toString()
		} else {
			valueStrings = [value.toString()]
		}

		for (valueString in valueStrings) {
			try {
				select.selectByValue(valueString)
			} catch (NoSuchElementException e1) {
				try {
					select.selectByVisibleText(valueString)
				} catch (NoSuchElementException e2) {
					throw new IllegalArgumentException("couldn't select option with text or value: $valueString")
				}
			}
		}

		if (multiple) {
			def selectedOptions = select.getAllSelectedOptions()
			for (selectedOption in selectedOptions) {
				if (!valueStrings.contains(selectedOption.getAttribute("value")) && !valueStrings.contains(selectedOption.text)) {
					selectedOption.click()
					assert !selectedOption.isSelected()
				}
			}
		}
	}

	protected String labelFor(WebElement input) {
		def id = input.getAttribute("id")
		def labels = browser.driver.findElements(By.xpath("//label[@for='$id']"))
		if (!labels) {
			labels = input.findElements(By.xpath("ancestor::label"))
		}
		labels ? labels[0].text : null
	}

	/**
	 * This works around an inconsistency in some of the WebDriver implementations.
	 * According to the spec WebElement.getAttribute should return the Strings "true" or "false"
	 * however ChromeDriver and HtmlUnitDriver will return "" or null.
	 */
	protected boolean getBooleanAttribute(WebElement input, String attribute) {
		!(input.getAttribute(attribute) in [null, false, "false"])
	}

	protected WebElement firstElementInContext(Closure closure) {
		def result = null
		for (int i = 0; !result && i < contextElements.size(); i++) {
			try {
				result = closure(contextElements[i])
			} catch (org.openqa.selenium.NoSuchElementException e) {
			}
		}
		result
	}

	protected List<WebElement> collectElements(Closure closure) {
		List<WebElement> list = []
		contextElements.each {
			try {
				def value = closure(it)
				switch (value) {
					case Collection:
						list.addAll value
						break
					default:
						if (value) {
							list << value
						}
				}
			} catch (org.openqa.selenium.NoSuchElementException e) {
			}
		}
		list
	}

	protected Collection<WebElement> collectUntil(Collection<WebElement> elements, Closure matcher) {
		int index = elements.findIndexOf matcher
		index == -1 ? elements : elements[0..<index]
	}

	protected Collection<WebElement> collectUntil(Collection<WebElement> elements, String selectorString) {
		collectUntil(elements) { CssSelector.matches(it, selectorString) }
	}

	protected Collection<WebElement> collectUntil(Collection<WebElement> elements, Map<String, Object> attributes) {
		collectUntil(elements) { matches(it, attributes) }
	}

	protected Navigator collectRelativeElements(String xpath, Closure filter) {
		navigatorFor collectElements {
			def elements = it.findElements(By.xpath(xpath))
			filter ? filter(elements) : elements
		}
	}

	protected Navigator collectFollowingSiblings(Closure filter) {
		collectRelativeElements("following-sibling::*", filter)
	}

	protected Navigator collectPreviousSiblings(Closure filter) {
		collectRelativeElements("preceding-sibling::*", filter)
	}

	protected Navigator collectParents(Closure filter) {
		collectRelativeElements("ancestor::*", filter)
	}

	protected Navigator collectChildren(Closure filter) {
		collectRelativeElements("child::*", filter)
	}

	protected Navigator collectSiblings(Closure filter) {
		navigatorFor collectElements {
			def elements = it.findElements(By.xpath("preceding-sibling::*")) + it.findElements(By.xpath("following-sibling::*"))
			filter ? filter(elements) : elements
		}
	}

	protected void ensureTagIn(List<String> allowedTags, String attribute) {
		if (!allowedTags.contains(firstElement().tagName)) {

			String joinedValidTags = allowedTags.join(', ');
			throw new UnsupportedOperationException("Value of '$attribute' attribute can only be checked for the following elements: $joinedValidTags.")
		}
	}

}
