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
package geb.js

import geb.Browser
import geb.Configuration
import org.openqa.selenium.Alert
import org.openqa.selenium.WebDriver

class DefaultAlertAndConfirmSupport implements AlertAndConfirmSupport {

    private final Browser browser

    DefaultAlertAndConfirmSupport(Browser browser) {
        this.browser = browser
    }

    private Configuration getConfig() {
        browser.config
    }

    private WebDriver getDriver() {
        browser.driver
    }

    private String captureDialog(Closure actions, waitParam, Closure alertHandler) {
        actions()
        def wait = config.getWaitForParam(waitParam)
        def alert = wait ? wait.waitFor { driver.switchTo().alert() } : driver.switchTo().alert()
        def message = alert.text
        alertHandler.call(alert)
        message
    }

    private captureAlert(Closure actions, waitParam) {
        captureDialog(actions, waitParam) { Alert alert -> alert.accept() }
    }

    private captureConfirm(boolean ok, Closure actions, waitParam) {
        captureDialog(actions, waitParam) { Alert alert -> ok ? alert.accept() : alert.dismiss() }
    }

    def withAlert(Map params = [:], Closure actions) {
        captureAlert(actions, params.wait)
    }

    def withConfirm(boolean ok, Closure actions) {
        withConfirm([:], ok, actions)
    }

    def withConfirm(Map params = [:], boolean ok = true, Closure actions) {
        captureConfirm(ok, actions, params.wait)
    }
}