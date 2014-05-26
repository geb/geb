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
package geb.driver

import org.openqa.selenium.WebDriver

/**
 * Wraps the operations on remote drivers to avoid a hard dependency on selenium-remote-client.
 */
class RemoteDriverOperations {

	final ClassLoader classLoader

	RemoteDriverOperations(ClassLoader classLoader) {
		this.classLoader = classLoader
	}

	/**
	 * If the driver is a remote driver, a proxy will be returned that implements the feature
	 * interfaces of the actual driver on the remote side. If it is not, the passed in driver
	 * is returned.
	 */
	WebDriver getAugmentedDriver(WebDriver driver) {
		if (isRemoteDriverAvailable()) {
			softLoadRemoteDriverClass("Augmenter").newInstance().augment(driver)
		} else {
			driver
		}
	}

	boolean isRemoteDriverAvailable() {
		remoteWebDriverClass != null
	}

	Class<? extends WebDriver> getRemoteWebDriverClass() {
		softLoadRemoteDriverClass("RemoteWebDriver")
	}

	public Class softLoadRemoteDriverClass(String name) {
		try {
			classLoader.loadClass("org.openqa.selenium.remote.$name")
		} catch (ClassNotFoundException e) {
			null
		}
	}
}