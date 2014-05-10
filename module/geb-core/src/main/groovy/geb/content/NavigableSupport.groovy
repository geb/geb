package geb.content

import geb.navigator.Navigator
import geb.navigator.factory.NavigatorFactory
import org.openqa.selenium.WebElement

class NavigableSupport implements Navigable {

	private final NavigatorFactory navigatorFactory

	NavigableSupport(NavigatorFactory navigatorFactory) {
		this.navigatorFactory = navigatorFactory
	}

	private Navigator getBase() {
		navigatorFactory.base
	}

	Navigator find() {
		base
	}

	Navigator $() {
		base
	}

	Navigator find(int index) {
		base[index]
	}

	Navigator find(Range<Integer> range) {
		base[range]
	}

	Navigator $(int index) {
		base[index]
	}

	Navigator $(Range<Integer> range) {
		base[range]
	}

	Navigator find(String selector) {
		base.find(selector)
	}

	Navigator $(String selector) {
		base.find(selector)
	}

	Navigator find(String selector, int index) {
		base.find(selector, index)
	}

	Navigator find(String selector, Range<Integer> range) {
		base.find(selector, range)
	}

	Navigator $(String selector, int index) {
		base.find(selector, index)
	}

	Navigator $(String selector, Range<Integer> range) {
		base.find(selector, range)
	}

	Navigator find(Map<String, Object> attributes) {
		base.find(attributes)
	}

	Navigator $(Map<String, Object> attributes) {
		base.find(attributes)
	}

	Navigator find(Map<String, Object> attributes, int index) {
		base.find(attributes, index)
	}

	Navigator find(Map<String, Object> attributes, Range<Integer> range) {
		base.find(attributes, range)
	}

	Navigator $(Map<String, Object> attributes, int index) {
		base.find(attributes, index)
	}

	Navigator $(Map<String, Object> attributes, Range<Integer> range) {
		base.find(attributes, null, range)
	}

	Navigator find(Map<String, Object> attributes, String selector) {
		base.find(attributes, selector)
	}

	Navigator $(Map<String, Object> attributes, String selector) {
		base.find(attributes, selector)
	}

	Navigator find(Map<String, Object> attributes, String selector, int index) {
		base.find(attributes, selector, index)
	}

	Navigator find(Map<String, Object> attributes, String selector, Range<Integer> range) {
		base.find(attributes, selector, range)
	}

	Navigator $(Map<String, Object> attributes, String selector, int index) {
		base.find(attributes, selector, index)
	}

	Navigator $(Map<String, Object> attributes, String selector, Range<Integer> range) {
		base.find(attributes, selector, range)
	}

	Navigator $(Navigator[] navigators) {
		navigatorFactory.createFromNavigators(Arrays.asList(navigators))
	}

	Navigator $(WebElement[] elements) {
		navigatorFactory.createFromWebElements(Arrays.asList(elements))
	}
}
