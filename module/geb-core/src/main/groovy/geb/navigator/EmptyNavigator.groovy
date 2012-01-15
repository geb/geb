package geb.navigator

import org.openqa.selenium.WebElement
import static java.util.Collections.EMPTY_LIST

import geb.Browser
import geb.Page

/**
 * Implementation of an empty Navigator object - helps keep the other code simple.
 */
class EmptyNavigator extends Navigator {

	static {
		def mc = new AttributeAccessingMetaClass(new ExpandoMetaClass(EmptyNavigator))
		mc.initialize()
		EmptyNavigator.metaClass = mc
	}
	
	EmptyNavigator(Browser browser) {
		super(browser)
	}

	private static final String[] EMPTY_STRING_ARRAY = new String[0]

	Navigator find(String selector) { this }

	Navigator find(Map<String, Object> predicates) { this }

	Navigator find(Map<String, Object> predicates, String selector) { this }

	Navigator filter(String selector) { this }

	Navigator filter(Map<String, Object> predicates) { this }

	Navigator filter(Map<String, Object> predicates, String selector) { this }

	Navigator not(String selector) { this }

	Navigator click() { }

	Navigator click(Class<? extends Page> pageClass) {
		throw new UnsupportedOperationException("not supported on empty navigator objects")
	}
	
	Navigator click(List<Class<? extends Page>> pageClasses) {
		throw new UnsupportedOperationException("not supported on empty navigator objects")
	}

	Navigator head() { this }

	Navigator first() { this }

	Collection<WebElement> allElements() { EMPTY_LIST }

	WebElement getElement(int index) { null }

	List<WebElement> getElements(Range range) { EMPTY_LIST }

	List<WebElement> getElements(Collection indexes) { EMPTY_LIST }

	boolean hasClass(String valueToContain) { false }

	boolean is(String tag) { false }

	boolean isEmpty() { true }

	Navigator last() { this }

	Navigator tail() { this }

	Navigator next() { this }

	Navigator next(String selector) { this }

	Navigator nextAll() { this }

	Navigator nextAll(String selector) { this }

	Navigator nextUntil(String selector) { this }

	Navigator previous() { this }

	Navigator previous(String selector) { this }

	Navigator prevAll() { this }

	Navigator prevAll(String selector) { this }

	Navigator prevUntil(String selector) { this }

	Navigator parent() { this }

	Navigator parent(String selector) { this }

	Navigator parents() { this }

	Navigator parents(String selector) { this }

	Navigator parentsUntil(String selector) { this }

	Navigator closest(String selector) { this }

	Navigator children() { this }

	Navigator children(String selector) { this }

	Navigator siblings() { this }

	Navigator siblings(String selector) { this }
	
	Navigator remove(int index) { this }

	int size() { 0 }

	boolean isDisplayed() { false }
	
	String tag() { null }

	String text() { null }

	String getAttribute(String name) { null }

	List<String> classes() { EMPTY_LIST }

	def value() { null }

	Navigator leftShift(value) { this }

	Navigator getAt(int index) { this }

	Navigator getAt(Range range) { this }

	Navigator getAt(Collection indexes) { this }

	Navigator verifyNotEmpty() { throw new EmptyNavigatorException() }

	Navigator value(value) { this }

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
