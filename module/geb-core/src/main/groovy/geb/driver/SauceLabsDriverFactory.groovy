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
package geb.driver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

class SauceLabsDriverFactory extends CloudDriverFactory {

    private final String host

    SauceLabsDriverFactory(String host = "ondemand.saucelabs.com") {
        this.host = host
    }

    WebDriver create(String specification, Map<String, Object> additionalCapabilities = [:]) {
        create(specification, System.getenv("GEB_SAUCE_LABS_USER"), System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD"), additionalCapabilities)
    }

    @Override
    String assembleProviderUrl(String username, String password) {
        "https://$username:$password@$host/wd/hub"
    }

    @Override
    protected void configureCapabilities(DesiredCapabilities desiredCapabilities) {
        def tunnelId = System.getenv("GEB_SAUCE_LABS_TUNNEL_ID")
        if (tunnelId) {
            desiredCapabilities.setCapability("tunnel-identifier", tunnelId)
        }
    }
}
