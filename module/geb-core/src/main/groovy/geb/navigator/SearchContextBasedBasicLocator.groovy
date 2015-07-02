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

class SearchContextBasedBasicLocator implements BasicLocator {

    private static final String MATCH_ALL_SELECTOR = "*"
    public static final String CLASS_ATTRIBUTE_NAME = "class"
    public static final String ID_ATTRIBUTE_NAME = "id"

    private final Collection<? extends SearchContext> searchContexts
    private final NavigatorFactory navigatorFactory

    SearchContextBasedBasicLocator(SearchContext searchContext, NavigatorFactory navigatorFactory) {
        this([searchContext], navigatorFactory)
    }

    SearchContextBasedBasicLocator(Collection<? extends SearchContext> searchContexts, NavigatorFactory navigatorFactory) {
        this.searchContexts = searchContexts
        this.navigatorFactory = navigatorFactory
    }

    private Navigator navigatorFor(Collection<WebElement> contextElements) {
        navigatorFactory.createFromWebElements(contextElements)
    }

    @Override
    Navigator find(By bySelector) {
        List<WebElement> list = []
        for (searchContext in searchContexts) {
            list.addAll searchContext.findElements(bySelector)
        }
        navigatorFor list
    }

    @Override
    Navigator find(Map<String, Object> attributes, String selector) {
        def attributesCopy = attributes.clone()
        def optimizedSelector = optimizeSelector(selector, attributesCopy)
        if (optimizedSelector) {
            find(By.cssSelector(optimizedSelector)).filter(attributesCopy)
        } else {
            find(attributes)
        }
    }

    @Override
    Navigator find(Map<String, Object> attributes) {
        find attributes, MATCH_ALL_SELECTOR
    }

    /**
     * Optimizes the selector if the predicates contains `class` or `id` keys that map to strings. Note this method has
     * a side-effect in that it _removes_ those keys from the predicates map.
     */
    protected String optimizeSelector(String selector, Map<String, Object> attributes) {
        if (!selector) {
            return selector
        }

        def buffer = new StringBuilder(selector)
        if (attributes.containsKey(ID_ATTRIBUTE_NAME) && attributes[ID_ATTRIBUTE_NAME] in String) {
            buffer << "#" << CssSelector.escape(attributes.remove(ID_ATTRIBUTE_NAME))
        }
        if (attributes.containsKey(CLASS_ATTRIBUTE_NAME) && attributes[CLASS_ATTRIBUTE_NAME] in String) {
            attributes.remove(CLASS_ATTRIBUTE_NAME).split(/\s+/).each { className ->
                buffer << "." << CssSelector.escape(className)
            }
        }
        if (buffer[0] == MATCH_ALL_SELECTOR && buffer.length() > 1) {
            buffer.deleteCharAt(0)
        }
        buffer.toString()
    }
}
