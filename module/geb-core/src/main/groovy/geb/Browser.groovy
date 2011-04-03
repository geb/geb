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
package geb

import geb.driver.*
import geb.js.*
import geb.conf.*
import geb.internal.WaitingSupport
import geb.internal.RemoteDriverOperations
import geb.error.PageChangeListenerAlreadyRegisteredException
import geb.error.RequiredPageContentNotPresent
import geb.error.UnexpectedPageException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException

class Browser {

	Page page
	final WebDriver driver
	final Configuration config
	String baseUrl
	
	@Delegate final WaitingSupport _waitingSupport = new WaitingSupport()
	@Delegate final AlertAndConfirmSupport  _alertAndConfirmSupport = new AlertAndConfirmSupport({ this.getJs() }) 
	
	private final pageChangeListeners = new LinkedHashSet()
	private _js

	/**
	 * If the driver is remote, this object allows access to it's capabilities. This indirection is needed
	 * to avoid a hard dependency on the remote driver classes which aren't part of core.
	 * 
	 * @see RemoteDriverOperations
	 */
	@Lazy WebDriver augmentedDriver = new RemoteDriverOperations(this.class.classLoader).getAugmentedDriver(driver)
	
	Browser(Configuration config) {
		this(null, null, null, null, config)
	}

	Browser(String baseUrl, Configuration config) {
		this(null, baseUrl, null, null, config)
	}
	
	Browser(Class pageClass, Map params = null, Configuration config = null) {
		this(null, null, pageClass, params, config)
	}
	
	Browser(String baseUrl, Class pageClass = null, Map params = null, Configuration config = null) {
		this(null, baseUrl, pageClass, params, config)
	}
	
	Browser(WebDriver driver = null, String baseUrl = null, Class pageClass = null, Map params = null, Configuration config = null) {
		this.config = config ?: createConfiguration()
		this.driver = driver ?: defaultDriver
		this.baseUrl = baseUrl != null ? baseUrl : this.config.baseUrl
		
		params = params == null ? [:] : params
		
		if (pageClass) {
			to(pageClass, *:params)
		} else {
			page Page
		}
	}
	
	protected Configuration createConfiguration() {
		def configurationLocation = getConfigurationLocation()
		if (configurationLocation) {
			createConfigurationLoader().load(configurationLocation)
		} else {
			new Configuration()
		}
	}
	
	protected URL getConfigurationLocation() {
		Thread.currentThread().contextClassLoader.getResource("geb-conf.groovy")
	}
	
	protected ConfigurationLoader createConfigurationLoader() {
		new ConfigurationLoader(getConfigurationEnvironment(), System.properties)
	}
	
	protected String getConfigurationEnvironment() {
		System.getProperty("geb.env")
	}
	
	protected WebDriver getDefaultDriver() {
		config.driverInstance
	}
		
	void registerPageChangeListener(PageChangeListener listener) {
		if (pageChangeListeners.add(listener)) {
			listener.pageWillChange(this, null, page)
		} else {
			throw new PageChangeListenerAlreadyRegisteredException(this, listener)
		}
	}
	
	boolean removePageChangeListener(PageChangeListener listener) {
		pageChangeListeners.remove(listener)
	}
	
	def methodMissing(String name, args) {
		page."$name"(*args)
	}

	def propertyMissing(String name) {
		page."$name"
	}
	
	def propertyMissing(String name, value) {
		page."$name" = value
	}	
	
	void page(Class pageClass) {
		page(createPage(pageClass))
	}
	
	void page(List<Class<? extends Page>> potentialPageClasses) {
		def potentialPageClassesClone = potentialPageClasses.clone()
		def match = null
		while (match == null && !potentialPageClassesClone.empty) {
			def potential = createPage(potentialPageClassesClone.remove(0))
			if (potential.verifyAtSafely()) {
				match = potential
			}
		}
		
		if (match) {
			page(match)
		} else {
			throw new UnexpectedPageException(potentialPageClasses)
		}
	}
	
	void page(Page page) {
		if (this.page == null || this.page.class != page.class) {
			informPageChangeListeners(this.page, page)
			this.page?.onUnload(page)
			def previousPage = this.page
			this.page = page
			page.browser = this
			this.page.onLoad(previousPage)
		}
	}
	
	boolean at(Class pageClass) {
		def targetPage = createPage(pageClass)
		try {
			if (targetPage.verifyAt()) {
				page(targetPage)
				true
			} else {
				false
			}
		} catch (RequiredPageContentNotPresent e) {
			false
		}
	}

	def go() {
		go([:], null)
	}
	
	def go(Map params) {
		go(params, null)
	}
	
	def go(String url) {
		go([:], url)
	}
	
	def go(Map params, String url) {
		def newUrl = _calculateUri(url, params)
		def newPage = driver.get(newUrl)
		if (!page) {
			page(Page)
		}
	}
	
	def to(Class pageClass, Object[] args) {
		to([:], pageClass, *args)
	}

	def to(Map params, Class pageClass) {
		to(params, pageClass, null)
	}

	def to(Map params, Class pageClass, Object[] args) {
		createPage(pageClass).to(params, *args)
	}
	
	void clearCookies() {
		driver?.manage()?.deleteAllCookies()
	}

	void clearCookiesQuietly() {
		try {
			clearCookies()
		} catch (WebDriverException e) {
			// ignore
		}
	}
	
	void quit() {
		driver.quit()
	}
	
	void close() {
		driver.close()
	}
	
	protected String _calculateUri(String path, Map params) {
		def uri
		if (path) {
			uri = new URI(path)
			if (!uri.absolute) {
				uri = new URI(baseUrl).resolve(uri)
			}
		} else {
			uri = new URI(baseUrl)
		}
		
		def queryString = _toQueryString(params)
		if (queryString) {
			def joiner = uri.query ? '&' : '?'
			new URL(uri.toString() + joiner + queryString).toString()
		} else {
			uri.toString()
		}
	}
	
	Page createPage(Class pageClass) {
		if (!Page.isAssignableFrom(pageClass)) {
			throw new IllegalArgumentException("$pageClass is not a subclass of ${Page}")
		}
		pageClass.newInstance(browser: this)
	}
	
	JavascriptInterface getJs() {
		new JavascriptInterface(this)
	}
	
	protected informPageChangeListeners(Page oldPage, Page newPage) {
		pageChangeListeners*.pageWillChange(this, oldPage, newPage)
	}
	
	protected _toQueryString(Map params) {
		if (params) {
			params.collect { name, value ->
				def values = value instanceof Collection ? value : [value]
				values.collect { v ->
					"${URLEncoder.encode(name.toString(), "UTF-8")}=${URLEncoder.encode(v.toString(), "UTF-8")}" 
				}
			}.flatten().join("&")
		} else {
			""
		}
	}
	
	static drive(Closure script) {
		doDrive(new Browser(), script)
	}
	
	static drive(Class pageClass, Closure script) {
		doDrive(new Browser(pageClass), script)
	}
	
	static drive(String baseUrl, Closure script) {
		def browser = new Browser(baseUrl)
		browser.go("")
		doDrive(browser, script)
	}
	
	static drive(String baseUrl, Class pageClass, Closure script) {
		doDrive(new Browser(baseUrl, pageClass), script)
	}
	
	static drive(WebDriver driver, Closure script) {
		doDrive(new Browser(driver), script)
	}

	static drive(WebDriver driver, String baseUrl, Closure script) {
		doDrive(new Browser(driver, baseUrl), script)
	}

	static drive(WebDriver driver, Class pageClass, Closure script) {
		doDrive(new Browser(driver, null, pageClass), script)
	}
	
	private static doDrive(Browser browser, Closure script) {
		script.delegate = browser
		script()
	}
}