/*
 * Copyright 2013 the original author or authors.
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

package geb.waiting

import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class WebDriverWaitWrapper {
    long timeout
    WebDriverWait webDriverWait

    WebDriverWaitWrapper(WebDriver driver, long timeout) {
        this.webDriverWait = new WebDriverWait(driver, timeout)
        this.timeout = timeout
    }

    WebElement waitUntilElementPresent(By locator) {
        WebElement element = null

        try {
            element = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(locator))
        } catch (TimeoutException timeoutException) {
            throw new WebDriverWaitWrapperException(
                    "WebDriver wait timeout of ${timeout} seconds exceeded for locator [${locator}]",
                    timeoutException
            )
        }

        return element
    }
}
