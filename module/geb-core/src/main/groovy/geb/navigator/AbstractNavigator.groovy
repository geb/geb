/*
 * Copyright 2012 the original author or authors.
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
package geb.navigator

import geb.Browser
import geb.Module
import geb.content.ModuleBaseCalculator
import geb.js.JQueryAdapter
import geb.navigator.factory.NavigatorFactory
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

abstract class AbstractNavigator implements Navigator {

    final Browser browser

    final Locator locator

    AbstractNavigator(Browser browser, BasicLocator basicLocator) {
        this.browser = browser
        this.locator = new DefaultLocator(basicLocator)
    }

    /**
     * Gets the element at the given index.
     * @param index index of the element to retrieve - pass a negative value to start from the back
     * @return the element at the given index, or null if no such element exists
     */
    protected abstract WebElement getElement(int index)

    /**
     * Gets the elements in the given range.
     * @param range range of the elements to retrieve
     * @return the elements in the given range, or an empty list if no such elements exist
     */
    protected abstract List<WebElement> getElements(Range range)

    /**
     * Gets the elements at the given indexes.
     * @param indexes indexes of the elements to retrieve
     * @return the elements at the given indexes, or an empty list if no such elements exist
     */
    protected abstract List<WebElement> getElements(Collection indexes)

    boolean asBoolean() {
        !empty
    }

    Navigator $(Map<String, Object> predicates) {
        locator.$(predicates)
    }

    @Override
    Navigator $(Map<String, Object> predicates, int index) {
        locator.$(predicates, index)
    }

    @Override
    Navigator $(Map<String, Object> predicates, Range<Integer> range) {
        locator.$(predicates, range)
    }

    @Override
    Navigator $(Map<String, Object> predicates, String selector) {
        locator.$(predicates, selector)
    }

    @Override
    Navigator $(Map<String, Object> predicates, String selector, int index) {
        locator.$(predicates, selector, index)
    }

    @Override
    Navigator $(Map<String, Object> predicates, String selector, Range<Integer> range) {
        locator.$(predicates, selector, range)
    }

    @Override
    Navigator $(String selector) {
        locator.$(selector)
    }

    @Override
    Navigator $(String selector, int index) {
        locator.$(selector, index)
    }

    @Override
    Navigator $(String selector, Range<Integer> range) {
        locator.$(selector, range)
    }

    @Override
    Navigator $(Map<String, Object> predicates, By bySelector) {
        locator.$(predicates, bySelector)
    }

    @Override
    Navigator $(Map<String, Object> predicates, By bySelector, int index) {
        locator.$(predicates, bySelector, index)
    }

    @Override
    Navigator $(Map<String, Object> predicates, By bySelector, Range<Integer> range) {
        locator.$(predicates, bySelector, range)
    }

    @Override
    Navigator $(By bySelector) {
        locator.$(bySelector)
    }

    @Override
    Navigator $(By bySelector, int index) {
        locator.$(bySelector, index)
    }

    @Override
    Navigator $(By bySelector, Range<Integer> range) {
        locator.$(bySelector, range)
    }

    Navigator find(Map<String, Object> predicates) {
        locator.find(predicates)
    }

    @Override
    Navigator find(Map<String, Object> predicates, int index) {
        locator.find(predicates, index)
    }

    @Override
    Navigator find(Map<String, Object> predicates, Range<Integer> range) {
        locator.find(predicates, range)
    }

    @Override
    Navigator find(Map<String, Object> predicates, String selector) {
        locator.find(predicates, selector)
    }

    @Override
    Navigator find(Map<String, Object> predicates, String selector, int index) {
        locator.find(predicates, selector, index)
    }

    @Override
    Navigator find(Map<String, Object> predicates, String selector, Range<Integer> range) {
        locator.find(predicates, selector, range)
    }

    @Override
    Navigator find(String selector) {
        locator.find(selector)
    }

    @Override
    Navigator find(String selector, int index) {
        locator.find(selector, index)
    }

    @Override
    Navigator find(String selector, Range<Integer> range) {
        locator.find(selector, range)
    }

    @Override
    Navigator find(Map<String, Object> predicates, By bySelector) {
        locator.find(predicates, bySelector)
    }

    @Override
    Navigator find(Map<String, Object> predicates, By bySelector, int index) {
        locator.find(predicates, bySelector, index)
    }

    @Override
    Navigator find(Map<String, Object> predicates, By bySelector, Range<Integer> range) {
        locator.find(predicates, bySelector, range)
    }

    @Override
    Navigator find(By bySelector) {
        locator.find(bySelector)
    }

    @Override
    Navigator find(By bySelector, int index) {
        locator.find(bySelector, index)
    }

    @Override
    Navigator find(By bySelector, Range<Integer> range) {
        locator.find(bySelector, range)
    }

    @Override
    Navigator filter(Map<String, Object> predicates, String selector) {
        filter(selector).filter(predicates)
    }

    Navigator has(String selector) {
        findAll { Navigator it ->
            !it.find(selector).empty
        }
    }

    Navigator has(Map<String, Object> predicates) {
        findAll { Navigator it ->
            !it.find(predicates).empty
        }
    }

    Navigator has(Map<String, Object> predicates, String selector) {
        findAll { Navigator it ->
            !it.find(predicates, selector).empty
        }
    }

    Navigator has(By bySelector) {
        findAll { Navigator it ->
            !it.find(bySelector).empty
        }
    }

    Navigator has(Map<String, Object> predicates, By bySelector) {
        findAll { Navigator it ->
            !it.find(predicates, bySelector).empty
        }
    }

    Navigator hasNot(String selector) {
        findAll { Navigator it ->
            it.find(selector).empty
        }
    }

    Navigator hasNot(Map<String, Object> predicates) {
        findAll { Navigator it ->
            it.find(predicates).empty
        }
    }

    Navigator hasNot(Map<String, Object> predicates, String selector) {
        findAll { Navigator it ->
            it.find(predicates, selector).empty
        }
    }

    Navigator hasNot(By bySelector) {
        findAll { Navigator it ->
            it.find(bySelector).empty
        }
    }

    Navigator hasNot(Map<String, Object> predicates, By bySelector) {
        findAll { Navigator it ->
            it.find(predicates, bySelector).empty
        }
    }

    Navigator eq(int index) {
        this[index]
    }

    Navigator add(String selector) {
        add(By.cssSelector(selector))
    }

    Navigator add(By bySelector) {
        add browser.driver.findElements(bySelector)
    }

    Navigator add(WebElement[] elements) {
        add Arrays.asList(elements)
    }

    Navigator add(Collection<WebElement> elements) {
        List<WebElement> result = []
        result.addAll allElements()
        result.addAll elements
        browser.navigatorFactory.createFromWebElements(result)
    }

    Navigator plus(Navigator navigator) {
        add navigator.allElements()
    }

    String attr(String name) {
        getAttribute(name)
    }

    WebElement singleElement() {
        getElement(0)
    }

    WebElement firstElement() {
        getElement(0)
    }

    WebElement lastElement() {
        getElement(-1)
    }

    Iterator<Navigator> iterator() {
        new NavigatorIterator()
    }

    Navigator findAll(Closure predicate) {
        browser.navigatorFactory.createFromNavigators(super.findAll(predicate))
    }

    JQueryAdapter getJquery() {
        new JQueryAdapter(this)
    }

    int getHeight() {
        firstElement()?.size?.height ?: 0
    }

    int getWidth() {
        firstElement()?.size?.width ?: 0
    }

    int getX() {
        firstElement()?.location?.x ?: 0
    }

    int getY() {
        firstElement()?.location?.y ?: 0
    }

    String css(String propertyName) {
        firstElement()?.getCssValue(propertyName)
    }

    @SuppressWarnings("UnnecessaryPublicModifier")
    public <T extends Module> T module(Class<T> moduleClass) {
        if (!Module.isAssignableFrom(moduleClass)) {
            throw new IllegalArgumentException("$moduleClass is not a subclass of ${Module}")
        }

        module(moduleClass.newInstance())
    }

    @SuppressWarnings("UnnecessaryPublicModifier")
    public <T extends Module> T module(T module) {
        def baseNavigatorFactory = browser.navigatorFactory.relativeTo(this)

        NavigatorFactory moduleBaseNavigatorFactory = ModuleBaseCalculator.calculate(module, baseNavigatorFactory)

        module.init(browser, moduleBaseNavigatorFactory)

        module
    }

    @SuppressWarnings("UnnecessaryPublicModifier")
    public <T extends Module> List<T> moduleList(Class<T> moduleClass) {
        iterator()*.module(moduleClass)
    }

    @SuppressWarnings(["UnnecessaryPublicModifier", "UnnecessaryCollectCall"])
    public <T extends Module> List<T> moduleList(Closure<T> moduleFactory) {
        iterator().collect { it.module(moduleFactory.call()) }
    }

    @Override
    String getStringRepresentation() {
        getClass().name
    }

    /**
     * Iterator for looping over the context elements of a Navigator instance.
     */
    private class NavigatorIterator implements Iterator<Navigator> {

        private int index

        boolean hasNext() {
            index < AbstractNavigator.this.size()
        }

        Navigator next() {
            AbstractNavigator.this[index++]
        }

        void remove() {
            throw new UnsupportedOperationException()
        }
    }

}
