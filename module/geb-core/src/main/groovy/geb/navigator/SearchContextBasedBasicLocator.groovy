/*
 * Copyright 2015 the original author or authors.
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

import geb.navigator.factory.NavigatorFactory
import org.openqa.selenium.By
import org.openqa.selenium.SearchContext
import org.openqa.selenium.WebElement

import java.util.function.BiFunction
import java.util.function.Supplier

import static geb.navigator.Locator.MATCH_ALL_SELECTOR
import static geb.navigator.BasicLocator.DYNAMIC_ATTRIBUTE_NAME

class SearchContextBasedBasicLocator implements BasicLocator {

    private static final Map<String, Closure> BY_SELECTING_ATTRIBUTES = [
            id   : By.&id,
            class: By.&className,
            name : By.&name
    ]

    public static final List<String> NON_SELECTOR_TRANSLATABLE_ATTRIBUTES = ["text", DYNAMIC_ATTRIBUTE_NAME]

    private final Iterable<? extends SearchContext> searchContexts
    private final NavigatorFactory navigatorFactory

    SearchContextBasedBasicLocator(SearchContext searchContext, NavigatorFactory navigatorFactory) {
        this([searchContext], navigatorFactory)
    }

    SearchContextBasedBasicLocator(Iterable<? extends SearchContext> searchContexts, NavigatorFactory navigatorFactory) {
        this.searchContexts = searchContexts
        this.navigatorFactory = navigatorFactory
    }

    @Override
    Navigator find(By bySelector) {
        find(false, bySelector)
    }

    @Override
    Navigator find(By bySelector, int index) {
        find(false, bySelector, index)
    }

    @Override
    Navigator find(Map<String, Object> attributes, By bySelector, int index) {
        find(dynamic(attributes), bySelector, index).filter(attributes)
    }

    @Override
    Navigator find(Map<String, Object> attributes, By bySelector) {
        find(dynamic(attributes), bySelector).filter(attributes)
    }

    @Override
    Navigator find(Map<String, Object> attributes, String selector) {
        find(attributes, selector) { Boolean dynamic, By bySelector ->
            find(dynamic, bySelector)
        }
    }

    @Override
    Navigator find(Map<String, Object> attributes, String selector, int index) {
        find(attributes, selector) { Boolean dynamic, By bySelector ->
            find(dynamic, bySelector, index)
        }
    }

    protected Navigator find(Map<String, Object> attributes, String selector, BiFunction<Boolean, By, Navigator> navigatorFromBy) {
        def attributesCopy = attributes.clone()
        def selectedUsingBy = findUsingByIfPossible(attributesCopy, selector, navigatorFromBy)
        if (selectedUsingBy != null) {
            return selectedUsingBy
        }

        def optimizedSelector = optimizeSelector(selector, attributesCopy)
        if (optimizedSelector) {
            navigatorFromBy.apply(dynamic(attributes), By.cssSelector(optimizedSelector)).filter(attributesCopy)
        } else {
            find(attributes, MATCH_ALL_SELECTOR)
        }
    }

    protected Navigator navigatorFor(Iterable<WebElement> contextElements) {
        navigatorFactory.createFromWebElements(contextElements)
    }

    protected Navigator findUsingByIfPossible(Map<String, Object> attributes, String selector, BiFunction<Boolean, By, Navigator> navigatorFromBy) {
        if (attributes.size() == 1 && selector == MATCH_ALL_SELECTOR) {
            BY_SELECTING_ATTRIBUTES.findResult { String attributeName, Closure<By> byFactory ->
                if (hasStringValueForKey(attributes, attributeName)) {
                    navigatorFromBy.apply(false, byFactory.call(attributes[attributeName]))
                }
            }
        }
    }

    protected boolean hasStringValueForKey(Map<String, Object> attributes, String key) {
        attributes.containsKey(key) && attributes[key] instanceof String
    }

    /**
     * Optimizes the selector by translating attributes map into a css attribute selector if possible.
     * Note this method has a side-effect in that it _removes_ those keys from the predicates map.
     */
    protected String optimizeSelector(String selector, Map<String, Object> attributes) {
        if (!selector) {
            return selector
        }

        def buffer = new StringBuilder(selector)
        for (def it = attributes.entrySet().iterator(); it.hasNext();) {
            def attribute = it.next()
            if (!(attribute.key in NON_SELECTOR_TRANSLATABLE_ATTRIBUTES) && attribute.value instanceof CharSequence) {
                def attributeValue = attribute.value.toString()
                if (attribute.key == "class") {
                    attributeValue.split(/\s+/).each { className ->
                        buffer << "." << CssSelector.escape(className)
                    }
                } else {
                    buffer << """[${attribute.key}="${CssSelector.escape(attributeValue)}"]"""
                }
                it.remove()
            }
        }

        if (buffer[0] == MATCH_ALL_SELECTOR && buffer.length() > 1) {
            buffer.deleteCharAt(0)
        }
        buffer.toString()
    }

    protected Navigator find(boolean dynamic, By bySelector) {
        toNavigator(dynamic, elementsSupplier(bySelector))
    }

    protected Navigator find(boolean dynamic, By bySelector, int index) {
        toNavigator(dynamic, elementsSupplier(bySelector, index))
    }

    protected Supplier<Collection<WebElement>> elementsSupplier(By bySelector) {
        { ->
            searchContexts.collectMany { it.findElements(bySelector) }
        }
    }

    protected Supplier<Collection<WebElement>> elementsSupplier(By bySelector, int index) {
        { ->
            [elementsSupplier(bySelector).get()[index]]
        }
    }

    protected Iterable<WebElement> toDynamicIterable(Supplier<Collection<WebElement>> contextElementsSupplier) {
        { -> contextElementsSupplier.get().iterator() } as Iterable<WebElement>
    }

    protected Navigator toNavigator(boolean dynamic, Supplier<Collection<WebElement>> contextElementsSupplier) {
        navigatorFor(dynamic ? toDynamicIterable(contextElementsSupplier) : contextElementsSupplier.get())
    }

    protected boolean dynamic(Map<String, Object> attributes) {
        attributes[DYNAMIC_ATTRIBUTE_NAME]
    }
}
