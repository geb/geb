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
package geb.testng

import geb.Browser
import geb.Configuration
import geb.ConfigurationLoader
import org.testng.annotations.AfterMethod

trait GebTestTrait {
    String gebConfEnv = null
    String gebConfScript = null

    private Browser browser

    Configuration createConf() {
        new ConfigurationLoader(gebConfEnv).getConf(gebConfScript)
    }

    Browser createBrowser() {
        new Browser(createConf())
    }

    Browser getRawBrowser() {
        browser
    }

    Browser getBrowser() {
        if (browser == null) {
            browser = createBrowser()
        }
        browser
    }

    @AfterMethod
    void resetBrowser() {
        if (browser?.config?.autoClearCookies) {
            browser?.clearCookiesQuietly()
        }
        browser = null
    }

    def methodMissing(String name, args) {
        getBrowser()."$name"(*args)
    }

    def propertyMissing(String name) {
        getBrowser()."$name"
    }

    def propertyMissing(String name, value) {
        getBrowser()."$name" = value
    }
}