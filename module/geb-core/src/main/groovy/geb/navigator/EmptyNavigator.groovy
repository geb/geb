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
	Navigator next(String selector) { this }

	@Override
	Navigator nextAll() { this }

	@Override
	Navigator nextAll(String selector) { this }

	@Override
	Navigator nextUntil(String selector) { this }

	@Override
	Navigator previous() { this }

	@Override
	Navigator previous(String selector) { this }

	@Override
	Navigator prevAll() { this }

	@Override
	Navigator prevAll(String selector) { this }

	@Override
	Navigator prevUntil(String selector) { this }

	@Override
	Navigator parent() { this }

	@Override
	Navigator parent(String selector) { this }

	@Override
	Navigator parents() { this }

	@Override
	Navigator parents(String selector) { this }

	@Override
	Navigator parentsUntil(String selector) { this }

	@Override
	Navigator closest(String selector) { this }

	@Override
	Navigator children() { this }

	@Override
	Navigator children(String selector) { this }

	@Override
	Navigator siblings() { this }

	@Override
	Navigator siblings(String selector) { this }

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
