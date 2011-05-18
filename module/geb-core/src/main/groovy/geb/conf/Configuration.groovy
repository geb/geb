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

package geb.conf

import geb.driver.*
import geb.waiting.Wait

import org.openqa.selenium.WebDriver

/**
 * Represents a particular configuration of Geb.
 */
class Configuration {
	
	static private final DEFAULT_WAIT_RETRY_SECS = 0.1
	
	final ConfigObject rawConfig
	final Properties properties
	
	private final Map<String, Wait> waits = null
	
	Configuration() {
		this(new ConfigObject(), new Properties())
	}
	
	Configuration(ConfigObject rawConfig) {
		this(rawConfig, new Properties())
	}
	
	Configuration(Properties properties) {
		this(new ConfigObject(), properties)
	}
	
	Configuration(ConfigObject rawConfig, Properties properties) {
		this.rawConfig = rawConfig
		this.properties = properties
	}

	void setCacheDriver(boolean cache) {
		rawConfig.cacheDriver = cache
	}
	
	Wait getWaitPreset(String name) {
		def preset = rawConfig.waiting.presets[name]
		def timeout = readValue(preset, 'timeout', getDefaultWaitTimeout())
		def retryInterval = readValue(preset, 'retryInterval', getDefaultWaitRetryInterval())
		
		new Wait(timeout, retryInterval)
	}
	
	Wait getDefaultWait() {
		new Wait(getDefaultWaitTimeout(), getDefaultWaitRetryInterval())
	}
	
	Wait getWait(Double timeout) {
		new Wait(timeout, getDefaultWaitRetryInterval())
	}

	/**
	 * The default {@code timeout} value to use for waiting (i.e. if unspecified).
	 * <p>
	 * Either the value at config path {@code waiting.timeout} or {@link geb.waiting.Wait#DEFAULT_TIMEOUT 5}.
	 */
	Double getDefaultWaitTimeout() {
		readValue(rawConfig.waiting, 'timeout', Wait.DEFAULT_TIMEOUT)
	}
	
	/**
	 * The default {@code retryInterval} value to use for waiting (i.e. if unspecified).
	 * <p>
	 * Either the value at config path {@code waiting.retryInterval} or {@link geb.waiting.Wait#DEFAULT_RETRY_INTERVAL 0.1}.
	 */
	Double getDefaultWaitRetryInterval() {
		readValue(rawConfig.waiting, 'retryInterval', Wait.DEFAULT_RETRY_INTERVAL)
	}
	
	boolean isCacheDriver() {
		readValue('cacheDriver', true)
	}

	void setDriver(value) {
		rawConfig.driver = value
	}
	
	def getDriver() {
		readValue("driver", null) ?: properties.getProperty("geb.driver")
	}

	String getBaseUrl() {
		readValue("baseUrl", null)
	}
	
	void setBaseUrl(baseUrl) {
		rawConfig.baseUrl = baseUrl.toString()
	}
	
	
	WebDriver getDriverInstance() {
		def driverValue = getDriver()
		
		if (driverValue instanceof WebDriver) {
			driverValue
		} else {
			wrapDriverFactoryInCachingIfNeeded(getDriverFactory(driverValue)).driver
		}
	}
	
	ClassLoader getClassLoader() {
		Thread.currentThread().contextClassLoader
	}
	
	protected DriverFactory getDriverFactory(driverValue) {
		if (driverValue instanceof CharSequence) {
			new NameBasedDriverFactory(classLoader, driverValue.toString())
		} else if (driverValue instanceof Closure) {
			new CallbackDriverFactory(driverValue)
		} else if (driverValue == null) {
			new DefaultDriverFactory(classLoader)
		} else {
			throw new DriverCreationException("Unable to determine factory for 'driver' config value '$driverValue'")
		}
	}
	
	protected DriverFactory wrapDriverFactoryInCachingIfNeeded(DriverFactory factory) {
		if (isCacheDriver()) {
			new CachingDriverFactory(factory)
		} else {
			factory
		}
	}

	protected readValue(String name, defaultValue) {
		readValue(rawConfig, name, defaultValue)
	}

	protected readValue(ConfigObject config, String name, defaultValue) {
		if (config.containsKey(name)) {
			config[name]
		} else {
			defaultValue
		}
	}
	
}