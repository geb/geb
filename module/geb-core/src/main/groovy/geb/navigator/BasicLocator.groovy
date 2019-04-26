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

import org.openqa.selenium.By

/**
 * Defines element search operations that are sufficient to be able to implement a {@link geb.navigator.Locator} on top of.
 */
interface BasicLocator {

    public static final String DYNAMIC_ATTRIBUTE_NAME = "dynamic"

    /**
     * Shorthand for <code>find(null, bySelector, null)</code>
     *
     * @param bySelector a WebDriver By selector
     * @return new Navigator
     */
    Navigator find(By bySelector)

    /**
     * Shorthand for <code>find(bySelector)[indexOfElement]</code>.
     * @param bySelector a WebDriver By selector
     * @param index index of the required element in the selection
     * @return new Navigator instance containing a single element
     */
    Navigator find(By bySelector, int index)

    /**
     * Selects elements by both <code>By</code> selector and attributes. For example <code>find(By.tagName("input"), name: "firstName")</code> will select
     * all input elements with the name "firstName".
     * @param bySelector a WebDriver By selector
     * @param predicates a Map with keys representing attributes and values representing required values or patterns
     * @return a new Navigator instance containing the matched elements
     */
    Navigator find(Map<String, Object> attributes, By bySelector)

    /**
     * Selects elements by both CSS selector and attributes. For example find("input", name: "firstName") will select
     * all input elements with the name "firstName".
     * @param selector a CSS selector
     * @param predicates a Map with keys representing attributes and values representing required values or patterns
     * @return a new Navigator instance containing the matched elements
     */
    Navigator find(Map<String, Object> attributes, String selector)

    /**
     * Shorthand for <code>find(predicates, selector, index..index)</code>
     *
     * @param selector
     * @return new Navigator
     */
    Navigator find(Map<String, Object> attributes, String selector, int index)
}
