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
package geb.content

import geb.Module
import geb.navigator.Navigator
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

class PageContentTemplateFactoryDelegate {

    static final DISALLOWED_MODULE_PARAMS = ['navigator', '_args']
    private static final String MODULE_METHOD_NAME = "module"
    private static final String MODULE_LIST_METHOD_NAME = "moduleList"

    private PageContentTemplate template
    private Object[] args

    @Delegate
    private final NavigableSupport navigableSupport

    PageContentTemplateFactoryDelegate(PageContentTemplate template, Object[] args) {
        this.template = template
        this.navigableSupport = new NavigableSupport(template.navigatorFactory)
        this.args = args
    }

    def methodMissing(String name, args) {
        template.owner."$name"(*args)
    }

    def propertyMissing(String name) {
        template.owner."$name"
    }

    def module(Map params, Class<? extends Module> moduleClass) {
        throw new MissingMethodException(MODULE_METHOD_NAME, getClass(), [params, moduleClass] as Object[])
    }

    def module(Class<? extends Module> moduleClass, container) {
        throw new MissingMethodException(MODULE_METHOD_NAME, getClass(), [moduleClass, container] as Object[])
    }

    def module(Map params, Class<? extends Module> moduleClass, Navigator base) {
        throw new MissingMethodException(MODULE_METHOD_NAME, getClass(), [params, moduleClass, base] as Object[])
    }

    def moduleList(Map params, Class moduleClass, Navigator navigator) {
        throw new MissingMethodException(MODULE_LIST_METHOD_NAME, getClass(), [params, moduleClass, navigator] as Object[])
    }

    def moduleList(Class moduleClass, Navigator navigator) {
        throw new MissingMethodException(MODULE_LIST_METHOD_NAME, getClass(), [moduleClass, navigator] as Object[])
    }

    def moduleList(Map params, Class moduleClass, Navigator navigator, index) {
        throw new MissingMethodException(MODULE_LIST_METHOD_NAME, getClass(), [params, moduleClass, navigator, index] as Object[])
    }

    def moduleList(Class moduleClass, Navigator navigator, index) {
        throw new MissingMethodException(MODULE_LIST_METHOD_NAME, getClass(), [moduleClass, navigator, index] as Object[])
    }

    Navigator $() {
        navigableSupport.$()
    }

    Navigator $(int index) {
        navigableSupport.$(index)
    }

    Navigator $(Range<Integer> range) {
        navigableSupport.$(range)
    }

    Navigator $(String selector) {
        navigableSupport.$(selector)
    }

    Navigator $(String selector, int index) {
        navigableSupport.$(selector, index)
    }

    Navigator $(String selector, Range<Integer> range) {
        navigableSupport.$(selector, range)
    }

    Navigator $(Map<String, Object> attributes) {
        navigableSupport.$(attributes)
    }

    Navigator $(Map<String, Object> attributes, int index) {
        navigableSupport.$(attributes, index)
    }

    Navigator $(Map<String, Object> attributes, Range<Integer> range) {
        navigableSupport.$(attributes, range)
    }

    Navigator $(Map<String, Object> attributes, String selector) {
        navigableSupport.$(attributes, selector)
    }

    Navigator $(Map<String, Object> attributes, String selector, int index) {
        navigableSupport.$(attributes, selector, index)
    }

    Navigator $(Map<String, Object> attributes, String selector, Range<Integer> range) {
        navigableSupport.$(attributes, selector, range)
    }

    Navigator $(Map<String, Object> attributes, By bySelector) {
        navigableSupport.find(attributes, bySelector)
    }

    Navigator $(Map<String, Object> attributes, By bySelector, int index) {
        navigableSupport.find(attributes, bySelector, index)
    }

    Navigator $(Map<String, Object> attributes, By bySelector, Range<Integer> range) {
        navigableSupport.find(attributes, bySelector, range)
    }

    Navigator $(By bySelector) {
        navigableSupport.find(bySelector)
    }

    Navigator $(By bySelector, int index) {
        navigableSupport.find(bySelector, index)
    }

    Navigator $(By bySelector, Range<Integer> range) {
        navigableSupport.find(bySelector, range)
    }

    Navigator $(Navigator[] navigators) {
        navigableSupport.$(navigators)
    }

    Navigator $(WebElement[] elements) {
        navigableSupport.$(elements)
    }
}
