package geb.navigator

import java.util.regex.Pattern
import org.openqa.selenium.WebElement
import static java.util.Collections.EMPTY_LIST

/**
 * Implementation of an empty Navigator object - helps keep the other code simple.
 */
@Singleton class EmptyNavigator extends Navigator {

	private static final WebElement[] EMPTY_ELEMENT_ARRAY = new WebElement[0]
	private static final String[] EMPTY_STRING_ARRAY = new String[0]

	Navigator find(String selector) { this }

	Navigator find(Map<String, Object> predicates) { this }

	Navigator find(Map<String, Object> predicates, String selector) { this }

	Navigator filter(String selector) { this }

	Navigator unique() { this }

	Navigator withTag(String tag) { this }

	String attribute(String key) { null }

	String[] attributes(String key) { EMPTY_STRING_ARRAY }

	void click() { }

	Navigator head() { this }

	Navigator first() { this }

	Navigator findByAttribute(String attribute, MatchType matchType, String value) { this }

	Collection<WebElement> allElements() { EMPTY_ELEMENT_ARRAY }

	WebElement getElement(int index) { null }

	List<WebElement> getElements(Range range) { EMPTY_LIST }

	List<WebElement> getElements(Collection indexes) { EMPTY_LIST }

	boolean hasAttribute(String key, MatchType matchType, String value) { false }

	boolean hasClass(String valueToContain) { false }

	boolean is(String tag) { false }

	boolean isEmpty() { true }

	Navigator last() { this }

	Navigator tail() { this }

	Navigator next() { this }

	Navigator next(String tag) { this }

	Navigator parent() { this }

	Navigator parent(String tag) { this }

	Navigator previous() { this }

	Navigator previous(String tag) { this }

	Navigator remove(int index) { this }

	int size() { 0 }

	String text() { null }

	String[] texts() { EMPTY_STRING_ARRAY }

	String trimmedText() { null }

	String[] trimmedTexts() { EMPTY_STRING_ARRAY }

	def value() { null }

	String[] values() { EMPTY_STRING_ARRAY }

	Navigator withAttribute(String key, MatchType matchType, String value) { this }

	Navigator getAt(int index) { this }

	Navigator getAt(Range range) { this }

	Navigator getAt(Collection indexes) { this }

	Navigator verifyNotEmpty() { throw new EmptyNavigatorException() }

	Navigator value(value) { this }

	Navigator withTextContaining(String textToContain) { this }

	Navigator withTextMatching(String pattern) { this }

	Navigator withTextMatching(Pattern pattern) { this }

	Navigator withAttributeMatching(String key, String pattern) { this }

	Navigator withAttributeMatching(String key, Pattern pattern) { this }

	Navigator findByAttributeMatching(String attribute, String pattern) { this }

	Navigator findByAttributeMatching(String attribute, Pattern pattern) { this }

	String toString() { "[]" }
}
