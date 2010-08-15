/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.driver

import org.openqa.selenium.WebDriver
import geb.error.UnknownDriverShortNameException
import geb.error.UnableToLoadAnyDriversException

class PropertyBasedDriverFactory implements DriverFactory {

	public static final String DRIVER_PROPERTY = "geb.driver"
	public static final String DRIVER_SEPARATOR = ":"

	public static final Map<String, String> SHORT_NAME_TO_DRIVERS = [
		htmlunit: "org.openqa.selenium.htmlunit.HtmlUnitDriver",
		firefox: "org.openqa.selenium.firefox.FirefoxDriver",
		ie: "org.openqa.selenium.ie.InternetExplorerDriver",
		chrome: "org.openqa.selenium.chrome.ChromeDriver"
	]

	final Properties properties
	final ClassLoader classLoader
	
	PropertyBasedDriverFactory() {
		this(System.getProperties())
	}
	
	PropertyBasedDriverFactory(Properties properties) {
		this(PropertyBasedDriverFactory.classLoader, properties)
	}
	
	PropertyBasedDriverFactory(ClassLoader classLoader) {
		this(classLoader, System.getProperties())
	}
	
	PropertyBasedDriverFactory(ClassLoader classLoader, Properties properties) {
		this.classLoader = classLoader
		this.properties = properties
	}
	
	WebDriver getDriver() {
		def potentials = getPotentialDriverClassNames()

		def driverClass
		for (potential in potentials) {
			driverClass = attemptToLoadDriverClass(potential)
			if (driverClass) break
		}
		
		if (driverClass) {
			driverClass.newInstance()
		} else {
			throw new UnableToLoadAnyDriversException(potentials as String[])
		}
	}
	
	protected attemptToLoadDriverClass(String driverClassName) {
		try {
			classLoader.loadClass(driverClassName)
		} catch (ClassNotFoundException e) {
			null
		}
	}
	
	protected translateFromShortNameIfRequired(String driverPropertyValue) {
		if (!driverPropertyValue.contains(".")) {
			if (SHORT_NAME_TO_DRIVERS.containsKey(driverPropertyValue)) {
				SHORT_NAME_TO_DRIVERS[driverPropertyValue]
			} else {
				throw new UnknownDriverShortNameException(driverPropertyValue)
			}
		} else {
			driverPropertyValue
		}
	}
	
	protected getPotentialDriverClassNames() {
		def systemPropertyValue = properties[DRIVER_PROPERTY]
		
		if (systemPropertyValue) {
			systemPropertyValue.split(DRIVER_SEPARATOR).collect { 
				translateFromShortNameIfRequired(it)
			}
		} else {
			SHORT_NAME_TO_DRIVERS.values()
		}
	}

}