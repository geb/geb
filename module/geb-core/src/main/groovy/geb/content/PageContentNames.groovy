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
package geb.content

import geb.Module
import geb.Page

import java.beans.Introspector

class PageContentNames {

    static final Set<String> NOT_ALLOWED = findNotAllowedPageContentNames()

    private static Set<String> findNotAllowedPageContentNames() {
        def notAllowed = [Module, Page].collectMany {
            getPropertyNames(it)
        } as Set<String>

        notAllowed << "content"
    }

    private static List<String> getPropertyNames(Class<?> aClass) {
        ["get", "is"].collect { propertyNamesFromMethodNames(aClass, it) }.sum()
    }

    private static List<String> propertyNamesFromMethodNames(Class aClass, String propertyAccessorPrefix) {
        def prefixLength = propertyAccessorPrefix.size()
        aClass.methods*.name
            .findAll { it.size() > prefixLength && it.startsWith(propertyAccessorPrefix) }*.substring(prefixLength)
            .collect { Introspector.decapitalize(it) }
    }

    private PageContentNames() {
    }

}
