/*
 * Copyright 2014 the original author or authors.
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

import geb.Page
import geb.error.PageInstanceNotInitializedException
import geb.navigator.Navigator
import org.openqa.selenium.By
import org.openqa.selenium.WebElement


class UninitializedNavigableSupport implements Navigable {

	private Page page

	public UninitializedNavigableSupport(Page page) {
		this.page = page
	}

	@Override
	Navigator find() {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $() {
		throw new PageInstanceNotInitializedException(page)
}

	@Override
	Navigator find(int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Navigator[] navigators) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(WebElement[] elements) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(String selector) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(String selector) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(String selector, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(String selector, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(String selector, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(String selector, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes, By bySelector) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes, By bySelector) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes, By bySelector, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes, By bySelector, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes, By bySelector, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes, By bySelector, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(By bySelector) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(By bySelector) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(By bySelector, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(By bySelector, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(By bySelector, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(By bySelector, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes, String selector) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes, String selector) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes, String selector, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator find(Map<String, Object> attributes, String selector, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes, String selector, int index) {
		throw new PageInstanceNotInitializedException(page)
	}

	@Override
	Navigator $(Map<String, Object> attributes, String selector, Range<Integer> range) {
		throw new PageInstanceNotInitializedException(page)
	}
}
