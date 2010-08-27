package geb.navigator

import geb.Browser
import java.util.regex.Pattern
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.FindsByCssSelector
import static java.util.Collections.EMPTY_LIST
import static java.util.Collections.EMPTY_SET

class NonEmptyNavigator extends Navigator {

	static {
		def mc = new AttributeAccessingMetaClass(new ExpandoMetaClass(NonEmptyNavigator))
		mc.initialize()
		NonEmptyNavigator.metaClass = mc
	}

	private final List<WebElement> contextElements
	final Browser browser

	NonEmptyNavigator(Browser browser, WebElement... contextElements) {
		this.browser = browser
		this.contextElements = contextElements as List
	}

	NonEmptyNavigator(Browser browser, Collection<? extends WebElement> contextElements) {
		this.browser = browser
		this.contextElements = contextElements as List
	}

	protected Navigator navigatorFor(Collection<? extends WebElement> contextElements) {
		on(browser, contextElements)
	}

	protected Navigator navigatorFor(WebElement... contextElements) {
		on(browser, contextElements)
	}
	
	Navigator find(String selectorString) {
		if (contextElements.head() instanceof FindsByCssSelector) {
			List<WebElement> list = []
			contextElements.each {
				list.addAll it.findElements(By.cssSelector(selectorString))
			}
			navigatorFor(list)
		} else {
			navigatorFor CssSelector.findByCssSelector(allElements(), selectorString)
		}
	}

	Navigator find(Map<String, Object> predicates) {
		List<WebElement> list = []
		contextElements*.findElements(By.xpath("descendant::*")).flatten().each { WebElement element ->
			if (matches(element, predicates)) {
				list << element
			}
		}
		navigatorFor list
	}

	Navigator find(Map<String, Object> predicates, String selector) {
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

	Navigator getAt(int index) {
		navigatorFor getElement(index)
	}

	Navigator getAt(Range range) {
		navigatorFor getElements(range)
	}

	Navigator getAt(EmptyRange range) {
		EmptyNavigator.instance
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
			EmptyNavigator.instance
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

	Navigator parent() {
		navigatorFor collectElements {
			it.findElement By.xpath("parent::*")
		}
	}

	Navigator parent(String selectorString) {
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

	Navigator unique() {
		new NonEmptyNavigator(this.browser, contextElements.unique())
	}

	boolean hasClass(String valueToContain) {
		any { valueToContain in it.classes() }
	}

	boolean is(String tag) {
		contextElements.any { tag.equalsIgnoreCase(it.tagName) }
	}

	String tag() {
		firstElement().tagName
	}

	String text() {
		firstElement().text
	}

	String getAttribute(String name) {
		firstElement().getAttribute(name) ?: ""
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

	int size() {
		contextElements.size()
	}

	boolean isEmpty() {
		size() == 0
	}

	boolean asBoolean() { 
		!isEmpty() 
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

	private boolean matches(WebElement element, Map<String, Object> predicates) {
		return predicates.every { name, requiredValue ->
			def actualValue = name == "text" ? element.text : element.getAttribute(name)
			if (requiredValue instanceof Pattern) {
				actualValue ==~ requiredValue
			} else {
				actualValue == requiredValue
			}
		}
	}

	private getInputValues(Collection<WebElement> inputs) {
		def values = []
		inputs.each { WebElement input ->
			def value = getInputValue(input)
			if (value) values << value
		}
		return values.size() < 2 ? values[0] : values
	}

	private getInputValue(WebElement input) {
		def value = null
		if (input.tagName == "select") {
			if (input.getAttribute("multiple")) {
				value = input.findElements(By.tagName("option")).findAll { it.isSelected() }*.value
			} else {
				value = input.findElements(By.tagName("option")).find { it.isSelected() }.value
			}
		} else if (input.getAttribute("type") in ["checkbox", "radio"]) {
			if (input.isSelected()) {
				value = input.value
			}
		} else {
			value = input.value
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
			if (!isAttributeEffectivelyFalse(input, "multiple")) {
				input.findElements(By.tagName("option")).each { WebElement option ->
					if (option.value in value) {
						option.setSelected()
					} else if (option.isSelected()) {
						option.toggle()
					}
				}
			} else {
				input.findElements(By.tagName("option")).find { it.value == value }.setSelected()
			}
		} else if (input.getAttribute("type") == "checkbox") {
			if (input.value == value || value == true) {
				input.setSelected()
			} else if (input.isSelected()) {
				input.toggle()
			}
		} else if (input.getAttribute("type") == "radio") {
			if (input.value == value) {
				input.setSelected()
			}
		} else {
			input.clear()
			input.sendKeys value
		}
	}
	
	// The Firefox driver at least will return a literal false when checking for certain
	// attributes. This goes against the spec of WebElement#getAttribute() but it happens
	// none the less.
	private boolean isAttributeEffectivelyFalse(WebElement input, String attribute) {
		def value = input.getAttribute(attribute)
		value == null || value == "" || value == "false" || value == false
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

}
