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
import geb.waiting.WaitingSupport
import geb.download.DownloadSupport
import geb.internal.RemoteDriverOperations
import geb.error.PageChangeListenerAlreadyRegisteredException
import geb.error.RequiredPageContentNotPresent
import geb.error.UnexpectedPageException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException

class Browser {

	/**
	 * The 
	 */
	Page page
	
	/**
	 * 
	 */
	final Configuration config
	
	@Delegate final WaitingSupport _waitingSupport
	@Delegate final AlertAndConfirmSupport  _alertAndConfirmSupport = new AlertAndConfirmSupport({ this.getJs() }) 
	@Delegate final DownloadSupport _downloadSupport = new DownloadSupport(this)
	
	private final pageChangeListeners = new LinkedHashSet()
	private _js

	/**
	 * If the driver is remote, this object allows access to it's capabilities. This indirection is needed
	 * to avoid a hard dependency on the remote driver classes which aren't part of core.
	 * 
	 * @see RemoteDriverOperations
	 */
	@Lazy WebDriver augmentedDriver = new RemoteDriverOperations(this.class.classLoader).getAugmentedDriver(driver)
	
	/**
	 * Create a new browser with a default configuration loader, loading the default configuration file.
	 * 
	 * @see geb.ConfigurationLoader
	 */
	Browser() {
		this(new ConfigurationLoader().conf)
	}
	
	/**
	 * Create a new browser backed by the given configuration.
	 * 
	 * @see geb.Configuration
	 */
	Browser(Configuration config) {
		this.config = config
		this._waitingSupport = new WaitingSupport(config)
		
		page(Page)
	}

	Browser(Map props, Configuration config) {
		this(config)
		this.metaClass.setProperties(this, props)
	}

	/**
	 * The driver implementation used to automate the actual browser.
	 * <p>
	 * The driver implementation to use is determined by the configuration.
	 * 
	 * @see geb.Configuration#getDriverInstance()
	 */
	WebDriver getDriver() {
		config.driver
	}
	
	/**
	 * Set (or change) the webdriver underneath this browser.
	 * <p>
	 * If you change the driver, you probably want to also change the page.
	 */
	void setDriver(WebDriver driver) {
		config.driver = driver
	}
	
	/**
	 * The url to resolve all relative urls to navigate to against.
	 * <p>
	 * The base url is determined by the configuration.
	 * 
	 * @see geb.Configuration#getBaseUrl()
	 */
	String getBaseUrl() {
		config.baseUrl
	}
	
	/**
	 * Change the base url used for resolving relative urls.
	 * 
	 * @see geb.Configuration#setBaseUrl(String)
	 */
	void setBaseUrl(String baseUrl) {
		config.baseUrl = baseUrl
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
		informPageChangeListeners(this.page, page)
		this.page?.onUnload(page)
		def previousPage = this.page
		this.page = page
		this.page.onLoad(previousPage)
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
		pageClass.newInstance().init(this)
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
	
	static Browser drive(Configuration conf, Closure script) {
		doDrive(new Browser(conf), script)
	}
	
	static Browser drive(Map params, Closure script) {
		doDrive(new Browser(params), script)
	}
	
	static Browser drive(Closure script) {
		doDrive(new Browser(), script)
	}
	
	private static Browser doDrive(Browser browser, Closure script) {
		script.delegate = browser
		script()
		browser
	}
}