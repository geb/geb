/*
 * Copyright 2014 the original author or authors.
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

import com.google.common.collect.ImmutableMap
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.DesiredCapabilities

class BrowserStackDriverFactory extends CloudDriverFactory {
	@Override
	String assembleProviderUrl(String username, String password) {
		"http://$username:$password@hub.browserstack.com/wd/hub"
	}

	@Override
	protected void configureCapabilities(DesiredCapabilities desiredCapabilities) {
		desiredCapabilities.setCapability("browserstack.local", "true")
	}

	WebDriver create(String specification, String username, String password, String localId, Map<String, Object> capabilities = [:]) {
		def mergedCapabilities = ImmutableMap.builder().putAll(capabilities).put("browserstack.localIdentifier", localId).build()
		create(specification, username, password, mergedCapabilities)
	}
}
