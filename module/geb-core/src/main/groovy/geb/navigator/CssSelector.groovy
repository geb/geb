package geb.navigator

import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import static java.util.Collections.EMPTY_LIST
import static geb.navigator.SelectorType.*

class CssSelector {

	private static final CssSelector DESCENDANT_SELECTOR = new CssSelector(SelectorType.DESCENDANT, " ")
	private static final String CSS_SELECTOR_SPECIAL_CHARS_PATTERN = '[!"#$%&\'\\(\\)*+,./:;<=>?@\\[\\]^`\\{|\\}~\\\\]'

	final SelectorType type
	final String value

	CssSelector() {
		super()
	}

	CssSelector(SelectorType type, String value) {
		this.type = type
		this.value = value
	}

	// TODO: better name

	static List<WebElement> findByCssSelector(Collection<WebElement> elements, String selectorString) {
		def result = []
		CssSelector.compile(selectorString).each { List<CssSelector> selectorGroup ->
			def context = elements
			boolean descend = true
			selectorGroup.each { CssSelector selector ->
				if (selector.type == SelectorType.DESCENDANT) {
					descend = true
				} else {
					context = context.inject([]) { list, element ->
						list.addAll selector.apply(element, descend)
						list
					}
					descend = false
				}
			}
			result += context
		}
		return result
	}

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

	List<WebElement> apply(WebElement element, boolean descend) {
		if (descend) {
			return select(element)
		} else {
			return matches(element) ? [element] : EMPTY_LIST
		}
	}

	boolean matches(WebElement element) {
		// TODO: switch = failure of object orientation
		switch (type) {
			case ELEMENT:
				return element.tagName == value
			case HTML_CLASS:
				return element.getAttribute("class") =~ /(^|\s)$value($|\s)/
			case ID:
				return element.getAttribute("id") == value
			default:
				return false
		}
	}

	List<WebElement> select(WebElement element) {
		switch (type) {
			case ELEMENT:
				return element.findElements(By.tagName(value))
			case HTML_CLASS:
				return element.findElements(By.className(value))
			case ID:
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
					list << new CssSelector(SelectorType.HTML_CLASS, part.substring(1))
				} else if (part.startsWith("#")) {
					list << new CssSelector(SelectorType.ID, part.substring(1))
				} else {
					list << new CssSelector(SelectorType.ELEMENT, part)
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

enum SelectorType {
	ELEMENT(""),
	HTML_CLASS("."),
	ID("#"),
	DESCENDANT("")

	final prefix

	SelectorType(String prefix) {
		this.prefix = prefix
	}
}
