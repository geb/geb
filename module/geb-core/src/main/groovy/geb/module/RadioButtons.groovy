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
package geb.module

import geb.Module
import geb.error.InvalidModuleBaseException
import geb.navigator.CssSelector
import org.openqa.selenium.WebElement

class RadioButtons extends Module {

    @Override
    protected void initialized() {
        if (!navigator.empty) {
            def tags = navigator*.tag()*.toLowerCase().unique().sort()
            if (tags != ["input"]) {
                throw new InvalidModuleBaseException("All elements of the base navigator for ${getClass().name} module have to be inputs but found the following elements: $tags")
            }
            def types = getAttributes("type")
            if (types != ["radio"]) {
                throw new InvalidModuleBaseException("All elements of the base navigator for ${getClass().name} module have to be radio buttons but found the following input types: $types")
            }
            def names = getAttributes("name")
            if (names.size() != 1) {
                throw new InvalidModuleBaseException("All elements of the base navigator for ${getClass().name} module have to have the same names but found the following names: $names")
            }
        }
    }

    private List<String> getAttributes(String attributeName) {
        navigator*.getAttribute(attributeName)*.toLowerCase().unique().sort()
    }

    private WebElement getCheckedElement() {
        navigator.allElements().find { it.selected }
    }

    String getChecked() {
        checkedElement?.getAttribute("value")
    }

    String getCheckedLabel() {
        def id = checkedElement?.getAttribute("id")
        if (id) {
            navigator.first().parents("html").find("label[for=\"${CssSelector.escape(id)}\"]").text()
        }
    }

    void setChecked(String valueOrLabelText) {
        navigator.value(valueOrLabelText)
    }
}
