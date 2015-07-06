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

    public static final String MATCH_ALL_SELECTOR = "*"

    /**
     * Shorthand for <code>find(null, bySelector, null)</code>
     *
     * @param bySelector a WebDriver By selector
     * @return new Navigator
     */
    Navigator find(By bySelector)

    /**
     * Selects elements by both CSS selector and attributes. For example find("input", name: "firstName") will select
     * all input elements with the name "firstName".
     * @param selector a CSS selector
     * @param predicates a Map with keys representing attributes and values representing required values or patterns
     * @return a new Navigator instance containing the matched elements
     */
    Navigator find(Map<String, Object> attributes, String selector)

    /**
     * Creates a new Navigator instance containing the elements whose attributes match the specified values or patterns.
     * The key 'text' can be used to match the text contained in elements. Regular expression Pattern objects may be
     * used as values.
     * <p>Examples:</p>
     * <dl>
     * <dt>find(name: "firstName")</dt>
     * <dd>selects all elements with the name "firstName"</dd>
     * <dt>find(name: "firstName", readonly: "readonly")</dt>
     * <dd>selects all elements with the name "firstName" that are read-only</dd>
     * <dt>find(text: "I can has cheezburger")</dt>
     * <dd>selects all elements containing the exact text</dd>
     * <dt>find(text: ~/I can has.+/)</dt>
     * <dd>selects all elements whose text matches a regular expression</dd>
     * </dl>
     * @param predicates a Map with keys representing attributes and values representing required values or patterns
     * @return a new Navigator instance containing the matched elements
     */
    Navigator find(Map<String, Object> attributes)
}
