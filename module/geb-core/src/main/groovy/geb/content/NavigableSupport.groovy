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
package geb.content

import geb.Module
import geb.navigator.Navigator
import geb.navigator.factory.NavigatorFactory
import org.openqa.selenium.By
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

	@Override
	Navigator $(Map<String, Object> attributes, By bySelector) {
		base.find(attributes, bySelector)
	}

	@Override
	Navigator find(Map<String, Object> attributes, By bySelector) {
		base.find(attributes, bySelector)
	}

	@Override
	Navigator $(Map<String, Object> attributes, By bySelector, int index) {
		base.find(attributes, bySelector, index)
	}

	@Override
	Navigator find(Map<String, Object> attributes, By bySelector, int index) {
		base.find(attributes, bySelector, index)
	}

	@Override
	Navigator $(Map<String, Object> attributes, By bySelector, Range<Integer> range) {
		base.find(attributes, bySelector, range)
	}

	@Override
	Navigator find(Map<String, Object> attributes, By bySelector, Range<Integer> range) {
		base.find(attributes, bySelector, range)
	}

	@Override
	Navigator $(By bySelector) {
		base.find(bySelector)
	}

	@Override
	Navigator find(By bySelector) {
		base.find(bySelector)
	}

	@Override
	Navigator $(By bySelector, int index) {
		base.find(bySelector, index)
	}

	@Override
	Navigator find(By bySelector, int index) {
		base.find(bySelector, index)
	}

	@Override
	Navigator $(By bySelector, Range<Integer> range) {
		base.find(bySelector, range)
	}

	@Override
	Navigator find(By bySelector, Range<Integer> range) {
		base.find(bySelector, range)
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
		base.find(attributes, (String)null, range)
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

	@Override
	<T extends Module> T module(Class<T> moduleClass) {
		base.module(moduleClass)
	}
}
