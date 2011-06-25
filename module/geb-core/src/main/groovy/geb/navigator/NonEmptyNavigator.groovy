package geb.navigator

import geb.Browser
import geb.Page
import java.util.regex.Pattern
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.FindsByCssSelector
import static java.util.Collections.EMPTY_LIST
import static java.util.Collections.EMPTY_SET

import geb.textmatching.TextMatcher

class NonEmptyNavigator extends Navigator {

	static {
		def mc = new AttributeAccessingMetaClass(new ExpandoMetaClass(NonEmptyNavigator))
		mc.initialize()
		NonEmptyNavigator.metaClass = mc
	}

	private final List<WebElement> contextElements

	NonEmptyNavigator(Browser browser, WebElement... contextElements) {
		this(browser, contextElements as List)
	}

	NonEmptyNavigator(Browser browser, Collection<? extends WebElement> contextElements) {
		super(browser)
		this.contextElements = contextElements.toList().unique().asImmutable()
	}

	protected Navigator navigatorFor(Collection<? extends WebElement> contextElements) {
		on browser, contextElements
	}

	protected Navigator navigatorFor(WebElement... contextElements) {
		on browser, contextElements
	}

	Navigator find(String selectorString) {
		if (contextElements.head() instanceof FindsByCssSelector) {
			List<WebElement> list = []
			contextElements.each {
				list.addAll it.findElements(By.cssSelector(selectorString))
			}
			navigatorFor list
		} else {
			navigatorFor CssSelector.findByCssSelector(allElements(), selectorString)
		}
	}

	Navigator find(Map<String, Object> predicates) {
		find predicates, "*"
	}

	Navigator find(Map<String, Object> predicates, String selector) {
		selector = optimizeSelector(selector, predicates)
		find(selector).filter(predicates)
	}

	Navigator filter(String selectorString) {
		navigatorFor contextElements.findAll { element ->
			CssSelector.matches(element, selectorString)
		}
	}

	Navigator filter(Map<String, Object> predicates) {
		navigatorFor contextElements.findAll { matches(it, predicates) }
	}

	Navigator filter(Map<String, Object> predicates, String selector) {
		filter(selector).filter(predicates)
	}

	Navigator not(String selectorString) {
		navigatorFor contextElements.findAll { element ->
			!CssSelector.matches(element, selectorString)
		}
	}

	Navigator getAt(int index) {
		navigatorFor getElement(index)
	}

	Navigator getAt(Range range) {
		navigatorFor getElements(range)
	}

	Navigator getAt(EmptyRange range) {
		new EmptyNavigator(browser)
	}

	Navigator getAt(Collection indexes) {
		navigatorFor getElements(indexes)
	}

	Collection<WebElement> allElements() {
		contextElements as WebElement[]
	}

	WebElement getElement(int index) {
		contextElements[index]
	}

	List<WebElement> getElements(Range range) {
		contextElements[range]
	}

	List<WebElement> getElements(EmptyRange range) {
		EMPTY_LIST
	}

	List<WebElement> getElements(Collection indexes) {
		contextElements[indexes]
	}

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

	Navigator next() {
		navigatorFor collectElements {
			it.findElement By.xpath("following-sibling::*")
		}
	}

	Navigator next(String selectorString) {
		navigatorFor collectElements {
			def siblings = it.findElements(By.xpath("following-sibling::*"))
			siblings.find { CssSelector.matches(it, selectorString) }
		}
	}

	Navigator nextAll() {
		navigatorFor collectElements {
			it.findElements By.xpath("following-sibling::*")
		}
	}

	Navigator nextAll(String selectorString) {
		navigatorFor collectElements {
			def siblings = it.findElements(By.xpath("following-sibling::*"))
			siblings.findAll { CssSelector.matches(it, selectorString) }
		}
	}

	Navigator nextUntil(String selectorString) {
		navigatorFor collectElements { element ->
			def siblings = element.findElements(By.xpath("following-sibling::*"))
			collectUntil(siblings, selectorString)
		}
	}

	Navigator previous() {
		navigatorFor collectElements {
			def siblings = it.findElements(By.xpath("preceding-sibling::*"))
			siblings ? siblings.last() : EMPTY_LIST
		}
	}

	Navigator previous(String selectorString) {
		navigatorFor collectElements {
			def siblings = it.findElements(By.xpath("preceding-sibling::*")).reverse()
			siblings.find { CssSelector.matches(it, selectorString) }
		}
	}

	Navigator prevAll() {
		navigatorFor collectElements {
			it.findElements(By.xpath("preceding-sibling::*"))
		}
	}

	Navigator prevAll(String selectorString) {
		navigatorFor collectElements {
			def siblings = it.findElements(By.xpath("preceding-sibling::*")).reverse()
			siblings.findAll { CssSelector.matches(it, selectorString) }
		}
	}

	Navigator prevUntil(String selectorString) {
		navigatorFor collectElements { element ->
			def siblings = element.findElements(By.xpath("preceding-sibling::*")).reverse()
			collectUntil(siblings, selectorString)
		}
	}

	Navigator parent() {
		navigatorFor collectElements {
			it.findElement By.xpath("parent::*")
		}
	}

	Navigator parent(String selectorString) {
		parent().filter(selectorString)
	}

	Navigator parents() {
		navigatorFor collectElements {
			it.findElements(By.xpath("ancestor::*")).reverse()
		}
	}

	Navigator parents(String selectorString) {
		navigatorFor collectElements {
			def ancestors = it.findElements(By.xpath("ancestor::*")).reverse()
			ancestors.findAll { CssSelector.matches(it, selectorString) }
		}
	}

	Navigator parentsUntil(String selectorString) {
		navigatorFor collectElements { element ->
			def ancestors = element.findElements(By.xpath("ancestor::*")).reverse()
			collectUntil(ancestors, selectorString)
		}
	}

	Navigator closest(String selectorString) {
		navigatorFor collectElements {
			def parents = it.findElements(By.xpath("ancestor::*")).reverse()
			parents.find { CssSelector.matches(it, selectorString) }
		}
	}

	Navigator children() {
		navigatorFor collectElements {
			it.findElements By.xpath("child::*")
		}
	}

	Navigator children(String selectorString) {
		children().filter(selectorString)
	}

	Navigator siblings() {
		navigatorFor collectElements {
			it.findElements(By.xpath("preceding-sibling::*")) + it.findElements(By.xpath("following-sibling::*"))
		}
	}

	Navigator siblings(String selectorString) {
		siblings().filter(selectorString)
	}

	boolean hasClass(String valueToContain) {
		any { valueToContain in it.classes() }
	}

	boolean is(String tag) {
		contextElements.any { tag.equalsIgnoreCase(it.tagName) }
	}

	boolean isDisplayed() {
		firstElement()?.displayed ?: false
	}

	String tag() {
		firstElement().tagName
	}

	String text() {
		firstElement().text
	}

	String getAttribute(String name) {
		firstElement().getAttribute(name)
	}
	
	Collection<String> classes() {
		def classNames = contextElements.head().getAttribute("class")?.tokenize()
		classNames as Set ?: EMPTY_SET
	}

	def value() {
		getInputValue(contextElements.head())
	}

	Navigator value(value) {
		setInputValues(contextElements, value)
		this
	}

	Navigator leftShift(value) {
		contextElements.each {
			it.sendKeys value
		}
		this
	}

	void click() {
		contextElements*.click()
	}

	void click(Class<? extends Page> pageClass) {
		click()
		browser.page(pageClass)
	}

	void click(List<Class<? extends Page>> potentialPageClasses) {
		click()
		browser.page(*potentialPageClasses)
	}

	int size() {
		contextElements.size()
	}

	boolean isEmpty() {
		size() == 0
	}

	Navigator head() {
		first()
	}

	Navigator first() {
		navigatorFor firstElement()
	}

	Navigator last() {
		navigatorFor lastElement()
	}

	Navigator tail() {
		navigatorFor contextElements.tail()
	}

	Navigator verifyNotEmpty() {
		this
	}

	String toString() {
		contextElements*.toString()
	}

	def methodMissing(String name, arguments) {
		if (!arguments) {
			navigatorFor collectElements {
				it.findElements By.name(name)
			}
		} else {
			throw new MissingMethodException(name, getClass(), arguments)
		}
	}

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
	private String optimizeSelector(String selector, Map<String, Object> predicates) {
		def buffer = new StringBuilder(selector)
		if (predicates.containsKey("id") && predicates["id"] in String) {
			buffer << "#" << predicates.remove("id")
		}
		if (predicates.containsKey("class") && predicates["class"] in String) {
			predicates.remove("class").split(/\s+/).each { className ->
				buffer << "." << className
			}
		}
		if (buffer[0] == "*" && buffer.length() > 1) buffer.deleteCharAt(0)
		return buffer.toString()
	}

	private boolean matches(WebElement element, Map<String, Object> predicates) {
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

	private boolean matches(String actualValue, String requiredValue) { actualValue == requiredValue }

	private boolean matches(String actualValue, Pattern requiredValue) { actualValue ==~ requiredValue }
	
	private boolean matches(String actualValue, TextMatcher matcher) { matcher.matches(actualValue) }

	private boolean matches(Collection<String> actualValue, String requiredValue) { requiredValue in actualValue }

	private boolean matches(Collection<String> actualValue, Pattern requiredValue) {
		actualValue.any { it ==~ requiredValue }
	}

	private boolean matches(Collection<String> actualValue, TextMatcher matcher) { 
		actualValue.any { matcher.matches(it) }
	}
	
	private getInputValues(Collection<WebElement> inputs) {
		def values = []
		inputs.each { WebElement input ->
			def value = getInputValue(input)
			if (value != null) values << value
		}
		return values.size() < 2 ? values[0] : values
	}

	private getInputValue(WebElement input) {
		def value = null
		if (input.tagName == "select") {
			if (getBooleanAttribute(input, "multiple")) {
				value = input.findElements(By.tagName("option")).findAll { it.isSelected() }.collect { getValue(it) }
			} else {
				value = getValue(input.findElements(By.tagName("option")).find { it.isSelected() })
			}
		} else if (input.getAttribute("type") in ["checkbox", "radio"]) {
			if (input.isSelected()) {
				value = getValue(input)
			}
		} else {
			value = getValue(input)
		}
		value
	}

	private void setInputValues(Collection<WebElement> inputs, value) {
		inputs.each { WebElement input ->
			setInputValue(input, value)
		}
	}

	private void setInputValue(WebElement input, value) {
		if (input.tagName == "select") {
			if (getBooleanAttribute(input, "multiple")) {
				def valueStrings = value*.toString()
				input.findElements(By.tagName("option")).each { WebElement option ->
					if (getValue(option) in valueStrings || option.text in valueStrings) {
						option.setSelected()
					} else if (option.isSelected()) {
						// Can't use click() to deselect - http://code.google.com/p/selenium/issues/detail?id=1899
						// Note that toggle() is deprecated though and will go at some point
						option.toggle()
					}
				}
			} else {
				def valueString = value.toString()
				input.findElements(By.tagName("option")).find {
					getValue(it) == valueString || it.text == valueString
				}?.setSelected()
			}
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
		} else {
			input.clear()
			input.sendKeys value as String
		}
	}
	
	private getValue(WebElement input) {
		def tag = input.tagName
		if (tag == "textarea") {
			input.text
		} else {
			input.getAttribute('value')
		}
	}
	
	private String labelFor(WebElement input) {
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
	private boolean getBooleanAttribute(WebElement input, String attribute) {
		!(input.getAttribute(attribute) in [null, false, "false"])
	}

	private WebElement firstElementInContext(Closure closure) {
		def result = null
		for (int i = 0; !result && i < contextElements.size(); i++) {
			try {
				result = closure(contextElements[i])
			} catch (org.openqa.selenium.NoSuchElementException e) { }
		}
		result
	}

	private List<WebElement> collectElements(Closure closure) {
		List<WebElement> list = []
		contextElements.each {
			try {
				def value = closure(it)
				switch (value) {
					case Collection:
						list.addAll value
						break
					default:
						if (value) list << value
				}
			} catch (org.openqa.selenium.NoSuchElementException e) { }
		}
		list
	}

	private Collection<WebElement> collectUntil(Collection<WebElement> elements, String selectorString) {
		int index = elements.findIndexOf { CssSelector.matches(it, selectorString) }
		index == -1 ? elements : elements[0..<index]
	}

}
