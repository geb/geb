package geb.navigator

import java.util.regex.Pattern
import org.apache.commons.lang.NotImplementedException
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.internal.FindsByCssSelector
import static java.util.Collections.EMPTY_LIST

class NonEmptyNavigator extends Navigator {

	private final List<WebElement> contextElements

	NonEmptyNavigator(WebElement... contextElements) {
		this.contextElements = contextElements as List
	}

	NonEmptyNavigator(Collection<? extends WebElement> contextElements) {
		this.contextElements = contextElements as List
	}

	Navigator find(String selectorString) {
		if (contextElements.head() instanceof FindsByCssSelector) {
			List<WebElement> list = []
			contextElements.each {
				println "using native css '$selectorString'"
				list.addAll it.findElements(By.cssSelector(selectorString))
			}
			on(list)
		} else {
			on CssSelector.findByCssSelector(allElements(), selectorString)
		}
	}

	Navigator find(Map<String, Object> predicates) {
		List<WebElement> list = []
		contextElements*.findElements(By.xpath("descendant::*")).flatten().each { WebElement element ->
			if (matches(element, predicates)) {
				list << element
			}
		}
		on list
	}

	Navigator find(Map<String, Object> predicates, String selector) {
		def navigator = find(selector)
		on navigator.allElements().findAll { WebElement element ->
			matches(element, predicates)
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

	Navigator filter(String selectorString) {
		def selectors = CssSelector.compile(selectorString)
		on contextElements.findAll { element ->
			selectors.any { selectorGroup ->
				selectorGroup.every { it.matches(element) }
			}
		}
	}

	Navigator filter(Map<String, Object> predicates) {
		on contextElements.findAll { matches(it, predicates) }
	}

	Navigator getAt(int index) {
		on getElement(index)
	}

	Navigator getAt(Range range) {
		on getElements(range)
	}

	Navigator getAt(EmptyRange range) {
		EmptyNavigator.instance
	}

	Navigator getAt(Collection indexes) {
		on getElements(indexes)
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
			on(contextElements - contextElements[index])
		}
	}

	Navigator next() {
		List<WebElement> siblings = []
		contextElements.each { WebElement element ->
			try {
				siblings << element.findElement(By.xpath("following-sibling::*"))
			} catch (org.openqa.selenium.NoSuchElementException e) {}
		}
		on siblings
	}

	Navigator next(String tag) {
		List<WebElement> siblings = []
		contextElements.each { WebElement element ->
			try {
				siblings << element.findElement(By.xpath("following-sibling::$tag"))
			} catch (org.openqa.selenium.NoSuchElementException e) {}
		}
		on siblings
	}

	Navigator previous() {
		List<WebElement> siblings = []
		contextElements.each { WebElement element ->
			try {
				siblings << element.findElement(By.xpath("preceding-sibling::*"))
			} catch (org.openqa.selenium.NoSuchElementException e) {}
		}
		on siblings
	}

	Navigator previous(String tag) {
		List<WebElement> siblings = []
		contextElements.each { WebElement element ->
			try {
				siblings << element.findElement(By.xpath("preceding-sibling::$tag"))
			} catch (org.openqa.selenium.NoSuchElementException e) {}
		}
		on siblings
	}

	Navigator parent() {
		List<WebElement> siblings = []
		contextElements.each { WebElement element ->
			try {
				siblings << element.findElement(By.xpath("parent::*"))
			} catch (org.openqa.selenium.NoSuchElementException e) {}
		}
		on siblings
	}

	Navigator parent(String tag) {
		List<WebElement> ancestors = []
		contextElements.each { WebElement element ->
			try {
				ancestors << element.findElement(By.xpath("ancestor::$tag[1]"))
			} catch (org.openqa.selenium.NoSuchElementException e) {}
		}
		on ancestors
	}

	Navigator unique() {
		new NonEmptyNavigator(contextElements.unique())
	}

	Navigator findByAttribute(String attribute, MatchType matchType, String value) {
		List<WebElement> list = []
		contextElements*.findElements(By.xpath("descendant::*")).flatten().each { WebElement child ->
			if (matchType.isMatch(child.getAttribute(attribute), value)) {
				list << child
			}
		}
		on list
	}

	Navigator findByAttributeMatching(String attribute, String pattern) {
		findByAttributeMatching attribute, ~pattern
	}

	Navigator findByAttributeMatching(String attribute, Pattern pattern) {
		List<WebElement> list = []
		contextElements*.findElements(By.xpath("descendant::*")).flatten().each { WebElement child ->
			if (child.getAttribute(attribute) ==~ pattern) {
				list << child
			}
		}
		on list
	}

	boolean hasClass(String valueToContain) {
		hasAttribute("class", MatchType.CONTAINED_WITH_WHITESPACE, valueToContain)
	}

	boolean is(String tag) {
		contextElements.any { tag.equalsIgnoreCase(it.tagName) }
	}

	Navigator withTag(String tag) {
		on contextElements.findAll { it.tagName == tag }
	}

	Navigator withTextContaining(String textToContain) {
		on contextElements.findAll { it.text == textToContain }
	}

	Navigator withTextMatching(String pattern) {
		withTextMatching ~pattern
	}

	Navigator withTextMatching(Pattern pattern) {
		on contextElements.findAll { it.text ==~ pattern }
	}

	Navigator withAttributeMatching(String key, String pattern) {
		withAttributeMatching key, ~pattern
	}

	Navigator withAttributeMatching(String key, Pattern pattern) {
		on contextElements.findAll { it.getAttribute(key) ==~ pattern }
	}

	Navigator withAttribute(String key, MatchType matchType, String value) {
		on contextElements.findAll {
			matchType.isMatch(it.getAttribute(key), value)
		}
	}

	boolean isChecked() {
		contextElements.every { it.isSelected() }
	}

	boolean isSelected() {
		contextElements.every { it.isSelected() }
	}

	boolean hasAttribute(String key, MatchType matchType, String value) {
		contextElements.any {
			matchType.isMatch(it.getAttribute(key), value)
		}
	}

	String text() {
		firstElement().text
	}

	String[] texts() {
		contextElements.text as String[]
	}

	String trimmedText() {
		text()?.replaceAll(/\s+/, " ")?.trim()
	}

	String[] trimmedTexts() {
		texts().collect {
			it.replaceAll(/\s+/, " ").trim()
		} as String[]
	}

	String attribute(String key) {
			firstElement().getAttribute(key) ?: ""
	}

	String[] attributes(String key) {
		contextElements.collect { it.getAttribute(key) ?: "" }
	}

	def value() {
		def element = firstElement()
		if (element.tagName ==~ /(?i)select/) {
			if (element.getAttribute("multiple")) {
				element.findElements(By.tagName("option")).findAll { it.isSelected() }.value
			} else {
				element.findElements(By.tagName("option")).find { it.isSelected() }.value
			}
		} else if (element.getAttribute("type") == "checkbox") {
			element.isSelected() ? element.value : null
		} else if (element.getAttribute("type") == "radio") {
			// TODO: this feels a little hacky
			withType("radio").withName(element.getAttribute("name")).allElements().find { it.isSelected() }?.value
		} else {
			element.value
		}
	}

	Navigator value(value) {
		def element = firstElement()
		if (element.tagName ==~ /(?i)select/) {
			if (element.getAttribute("multiple")) {
				element.findElements(By.tagName("option")).each {
					if (it.value in value) {
						it.setSelected()
					} else if (it.isSelected()) {
						it.toggle()
					}
				}
			} else {
				element.findElements(By.tagName("option")).find { it.value == value }.setSelected()
			}
		} else if (element.getAttribute("type") == "checkbox") {
			if (element.value == value) {
				element.setSelected()
			} else if (element.isSelected()) {
				element.toggle()
			}
		} else if (element.getAttribute("type") == "radio") {
			// TODO: this feels a little hacky
			withType("radio").withName(element.getAttribute("name")).allElements().find { it.value == value }?.setSelected()
		} else {
			element.clear()
			element.sendKeys(value)
		}
		this
	}

	String[] values() {
		def list = []
		contextElements.collect { element ->
			if (element.tagName ==~ /(?i)select/) {
				def options = element.findElements(By.tagName("option")).findAll { it.isSelected() }
				list.addAll options.value
			} else {
				list << element.value
			}
		}
		list as String[]
	}

	void click() {
		throw new NotImplementedException()
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
		on firstElement()
	}

	Navigator last() {
		on lastElement()
	}

	Navigator tail() {
		on contextElements.tail()
	}

	Navigator verifyNotEmpty() {
		this
	}

	String toString() {
		contextElements*.toString()
	}

}
