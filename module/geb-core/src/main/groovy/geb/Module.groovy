/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb

import geb.content.*
import geb.download.DefaultDownloadSupport
import geb.download.DownloadSupport
import geb.frame.DefaultFrameSupport
import geb.frame.FrameSupport
import geb.js.AlertAndConfirmSupport
import geb.js.DefaultAlertAndConfirmSupport
import geb.js.JQueryAdapter
import geb.js.JavascriptInterface
import geb.navigator.Navigator
import geb.navigator.factory.NavigatorFactory
import geb.textmatching.TextMatchingSupport
import geb.waiting.DefaultWaitingSupport
import geb.waiting.Wait
import geb.waiting.WaitingSupport
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

@SuppressWarnings("FieldName")
class Module implements Navigator, PageContentContainer {

	static base = null

	@Delegate
	private PageContentSupport pageContentSupport
	@Delegate
	private DownloadSupport downloadSupport
	@Delegate
	private WaitingSupport waitingSupport
	@Delegate
	private FrameSupport frameSupport

	@Delegate
	@SuppressWarnings("UnusedPrivateField")
	private TextMatchingSupport textMatchingSupport = new TextMatchingSupport()
	@Delegate
	private AlertAndConfirmSupport alertAndConfirmSupport

	private Browser browser

	//manually delegating here because @Delegate doesn't work with cross compilation http://jira.codehaus.org/browse/GROOVY-6865
	private Navigator _navigator

	@SuppressWarnings('SpaceBeforeOpeningBrace')
	void init(Browser browser, NavigatorFactory navigatorFactory) {
		this.browser = browser
		_navigator = navigatorFactory.base
		Map<String, PageContentTemplate> contentTemplates = PageContentTemplateBuilder.build(browser, this, navigatorFactory, 'content', this.class, Module)
		pageContentSupport = new DefaultPageContentSupport(this, contentTemplates, navigatorFactory, _navigator)
		downloadSupport = new DefaultDownloadSupport(browser)
		waitingSupport = new DefaultWaitingSupport(browser.config)
		frameSupport = new DefaultFrameSupport(browser)
		alertAndConfirmSupport = new DefaultAlertAndConfirmSupport({ this.getJs() }, browser.config)
	}

	JavascriptInterface getJs() {
		browser.js
	}

	def methodMissing(String name, args) {
		pageContentSupport.methodMissing(name, args)
	}

	def propertyMissing(String name) {
		pageContentSupport.propertyMissing(name)
	}

	def propertyMissing(String name, val) {
		pageContentSupport.propertyMissing(name, val)
	}

	boolean asBoolean() {
		_navigator.asBoolean()
	}

	@Override
	Navigator $(Map<String, Object> predicates) {
		_navigator.$(predicates)
	}

	@Override
	Navigator $(Map<String, Object> predicates, int index) {
		_navigator.$(predicates, index)
	}

	@Override
	Navigator $(Map<String, Object> predicates, Range<Integer> range) {
		_navigator.$(predicates, range)
	}

	@Override
	Navigator $(Map<String, Object> predicates, String selector) {
		_navigator.$(predicates, selector)
	}

	@Override
	Navigator $(Map<String, Object> predicates, String selector, int index) {
		_navigator.$(predicates, selector, index)
	}

	@Override
	Navigator $(Map<String, Object> predicates, String selector, Range<Integer> range) {
		_navigator.$(predicates, selector, range)
	}

	@Override
	Navigator $(String selector) {
		_navigator.$(selector)
	}

	@Override
	Navigator $(String selector, int index) {
		_navigator.$(selector, index)
	}

	@Override
	Navigator $(String selector, Range<Integer> range) {
		_navigator.$(selector, range)
	}

	@Override
	Navigator $(Map<String, Object> predicates, By bySelector) {
		_navigator.$(predicates, bySelector)
	}

	@Override
	Navigator $(Map<String, Object> predicates, By bySelector, int index) {
		_navigator.$(predicates, bySelector, index)
	}

	@Override
	Navigator $(Map<String, Object> predicates, By bySelector, Range<Integer> range) {
		_navigator.$(predicates, bySelector, range)
	}

	@Override
	Navigator $(By bySelector) {
		_navigator.$(bySelector)
	}

	@Override
	Navigator $(By bySelector, int index) {
		_navigator.$(bySelector, index)
	}

	@Override
	Navigator $(By bySelector, Range<Integer> range) {
		_navigator.$(bySelector, range)
	}

	@Override
	<T extends Module> T module(Class<T> moduleClass) {
		_navigator.module(moduleClass)
	}

	@Override
	String css(String propertyName) {
		_navigator.css(propertyName)
	}

	@Override
	Navigator unique() {
		_navigator.unique()
	}

	@Override
	int getY() {
		_navigator.getY()
	}

	@Override
	int getX() {
		_navigator.getX()
	}

	@Override
	int getWidth() {
		_navigator.getWidth()
	}

	@Override
	int getHeight() {
		_navigator.getHeight()
	}

	@Override
	JQueryAdapter getJquery() {
		_navigator.getJquery()
	}

	@Override
	Navigator verifyNotEmpty() {
		_navigator.verifyNotEmpty()
	}

	@Override
	Navigator findAll(Closure predicate) {
		_navigator.findAll(predicate)
	}

	@Override
	Iterator<Navigator> iterator() {
		_navigator.iterator()
	}

	@Override
	Collection<WebElement> allElements() {
		_navigator.allElements()
	}

	@Override
	WebElement lastElement() {
		_navigator.lastElement()
	}

	@Override
	WebElement firstElement() {
		_navigator.firstElement()
	}

	@Override
	Navigator tail() {
		_navigator.tail()
	}

	@Override
	Navigator last() {
		_navigator.last()
	}

	@Override
	Navigator first() {
		_navigator.first()
	}

	@Override
	Navigator head() {
		_navigator.head()
	}

	@Override
	boolean isEmpty() {
		_navigator.isEmpty()
	}

	@Override
	int size() {
		_navigator.size()
	}

	@Override
	Navigator click(List potentialPages, Wait wait = null) {
		_navigator.click(potentialPages, wait)
	}

	@Override
	Navigator click(Page pageInstance, Wait wait = null) {
		_navigator.click(pageInstance, wait)
	}

	@Override
	Navigator click(Class<? extends Page> pageClass, Wait wait = null) {
		_navigator.click(pageClass, wait)
	}

	@Override
	Navigator click() {
		_navigator.click()
	}

	@Override
	Navigator leftShift(value) {
		_navigator << value
	}

	@Override
	Navigator value(value) {
		_navigator.value(value)
	}

	@Override
	def value() {
		_navigator.value()
	}

	@Override
	List<String> classes() {
		_navigator.classes()
	}

	@Override
	String attr(String name) {
		_navigator.attr(name)
	}

	@Override
	String getAttribute(String name) {
		_navigator.getAttribute(name)
	}

	@Override
	String text() {
		_navigator.text()
	}

	@Override
	String tag() {
		_navigator.tag()
	}

	@Override
	boolean isEditable() {
		_navigator.isEditable()
	}

	@Override
	boolean isReadOnly() {
		_navigator.isReadOnly()
	}

	@Override
	boolean isEnabled() {
		_navigator.isEnabled()
	}

	@Override
	boolean isDisabled() {
		_navigator.isDisabled()
	}

	@Override
	boolean isDisplayed() {
		_navigator.isDisplayed()
	}

	@Override
	boolean is(String tag) {
		_navigator.is(tag)
	}

	@Override
	boolean hasClass(String className) {
		_navigator.hasClass(className)
	}

	@Override
	Navigator siblings(Map<String, Object> attributes = [:], String selector) {
		_navigator.siblings(attributes, selector)
	}

	@Override
	Navigator siblings(Map<String, Object> attributes) {
		_navigator.siblings(attributes)
	}

	@Override
	Navigator siblings() {
		_navigator.siblings()
	}

	@Override
	Navigator children(Map<String, Object> attributes = [:], String selector) {
		_navigator.children(attributes, selector)
	}

	@Override
	Navigator children(Map<String, Object> attributes) {
		_navigator.children(attributes)
	}

	@Override
	Navigator children() {
		_navigator.children()
	}

	@Override
	Navigator closest(Map<String, Object> attributes = [:], String selector) {
		_navigator.closest(attributes, selector)
	}

	@Override
	Navigator closest(Map<String, Object> attributes) {
		_navigator.closest(attributes)
	}

	@Override
	Navigator parentsUntil(Map<String, Object> attributes = [:], String selector) {
		_navigator.parentsUntil(attributes, selector)
	}

	@Override
	Navigator parentsUntil(Map<String, Object> attributes) {
		_navigator.parentsUntil(attributes)
	}

	@Override
	Navigator parents(Map<String, Object> attributes = [:], String selector) {
		_navigator.parents(attributes, selector)
	}

	@Override
	Navigator parents(Map<String, Object> attributes) {
		_navigator.parents(attributes)
	}

	@Override
	Navigator parents() {
		_navigator.parents()
	}

	@Override
	Navigator parent(Map<String, Object> attributes = [:], String selector) {
		_navigator.parent(attributes, selector)
	}

	@Override
	Navigator parent(Map<String, Object> attributes) {
		_navigator.parent(attributes)
	}

	@Override
	Navigator parent() {
		_navigator.parent()
	}

	@Override
	Navigator prevUntil(Map<String, Object> attributes = [:], String selector) {
		_navigator.prevUntil(attributes, selector)
	}

	@Override
	Navigator prevUntil(Map<String, Object> attributes) {
		_navigator.prevUntil(attributes)
	}

	@Override
	Navigator prevAll(Map<String, Object> attributes = [:], String selector) {
		_navigator.prevAll(attributes, selector)
	}

	@Override
	Navigator prevAll(Map<String, Object> attributes) {
		_navigator.prevAll(attributes)
	}

	@Override
	Navigator prevAll() {
		_navigator.prevAll()
	}

	@Override
	Navigator previous(Map<String, Object> attributes = [:], String selector) {
		_navigator.previous(attributes, selector)
	}

	@Override
	Navigator previous(Map<String, Object> attributes) {
		_navigator.previous(attributes)
	}

	@Override
	Navigator previous() {
		_navigator.previous()
	}

	@Override
	Navigator nextUntil(Map<String, Object> attributes = [:], String selector) {
		_navigator.nextUntil(attributes, selector)
	}

	@Override
	Navigator nextUntil(Map<String, Object> attributes) {
		_navigator.nextUntil(attributes)
	}

	@Override
	Navigator nextAll(Map<String, Object> attributes = [:], String selector) {
		_navigator.nextAll(attributes, selector)
	}

	@Override
	Navigator nextAll(Map<String, Object> attributes) {
		_navigator.nextAll(attributes)
	}

	@Override
	Navigator nextAll() {
		_navigator.nextAll()
	}

	@Override
	Navigator next(Map<String, Object> attributes = [:], String selector) {
		_navigator.next(attributes, selector)
	}

	@Override
	Navigator next(Map<String, Object> attributes) {
		_navigator.next(attributes)
	}

	@Override
	Navigator next() {
		_navigator.next()
	}

	@Override
	Navigator plus(Navigator navigator) {
		_navigator + navigator
	}

	@Override
	Navigator remove(int index) {
		_navigator.remove(index)
	}

	@Override
	Navigator add(Collection<WebElement> elements) {
		_navigator.add(elements)
	}

	@Override
	Navigator add(WebElement[] elements) {
		_navigator.add(elements)
	}

	@Override
	Navigator add(By bySelector) {
		_navigator.add(bySelector)
	}

	@Override
	Navigator add(String selector) {
		_navigator.add(selector)
	}

	@Override
	@SuppressWarnings("ExplicitCallToGetAtMethod")
	Navigator getAt(Collection indexes) {
		_navigator.getAt(indexes)
	}

	@Override
	Navigator getAt(Range range) {
		_navigator[range]
	}

	@Override
	Navigator getAt(int index) {
		_navigator[index]
	}

	@Override
	Navigator eq(int index) {
		_navigator.eq(index)
	}

	@Override
	Navigator not(Map<String, Object> predicates = [:], String selector) {
		_navigator.not(predicates, selector)
	}

	@Override
	Navigator not(Map<String, Object> predicates) {
		_navigator.not(predicates)
	}

	@Override
	Navigator filter(Map<String, Object> predicates = [:], String selector) {
		_navigator.filter(predicates, selector)
	}

	@Override
	Navigator filter(Map<String, Object> predicates) {
		_navigator.filter(predicates)
	}

	@Override
	Navigator has(Map<String, Object> predicates = [:], By bySelector) {
		_navigator.has(predicates, bySelector)
	}

	@Override
	Navigator has(Map<String, Object> predicates) {
		_navigator.has(predicates)
	}

	@Override
	Navigator has(Map<String, Object> predicates = [:], String selector) {
		_navigator.has(predicates, selector)
	}

	@Override
	Navigator find(Map<String, Object> predicates) {
		_navigator.find(predicates)
	}

	@Override
	Navigator find(Map<String, Object> predicates, int index) {
		_navigator.find(predicates, index)
	}

	@Override
	Navigator find(Map<String, Object> predicates, Range<Integer> range) {
		_navigator.find(predicates, range)
	}

	@Override
	Navigator find(Map<String, Object> predicates, String selector) {
		_navigator.find(predicates, selector)
	}

	@Override
	Navigator find(Map<String, Object> predicates, String selector, int index) {
		_navigator.find(predicates, selector, index)
	}

	@Override
	Navigator find(Map<String, Object> predicates, String selector, Range<Integer> range) {
		_navigator.find(predicates, selector, range)
	}

	@Override
	Navigator find(String selector) {
		_navigator.find(selector)
	}

	@Override
	Navigator find(String selector, int index) {
		_navigator.find(selector, index)
	}

	@Override
	Navigator find(String selector, Range<Integer> range) {
		_navigator.find(selector, range)
	}

	@Override
	Navigator find(Map<String, Object> predicates, By bySelector) {
		_navigator.find(predicates, bySelector)
	}

	@Override
	Navigator find(Map<String, Object> predicates, By bySelector, int index) {
		_navigator.find(predicates, bySelector, index)
	}

	@Override
	Navigator find(Map<String, Object> predicates, By bySelector, Range<Integer> range) {
		_navigator.find(predicates, bySelector, range)
	}

	@Override
	Navigator find(By bySelector) {
		_navigator.find(bySelector)
	}

	@Override
	Navigator find(By bySelector, int index) {
		_navigator.find(bySelector, index)
	}

	@Override
	Navigator find(By bySelector, Range<Integer> range) {
		_navigator.find(bySelector, range)
	}
}