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

import geb.js.JavascriptInterface
import geb.internal.RemoteDriverOperations

import geb.error.PageChangeListenerAlreadyRegisteredException
import geb.error.RequiredPageContentNotPresent
import geb.error.UnexpectedPageException

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException

/**
 * The browser is the centre of Geb. It encapsulates a {@link org.openqa.selenium.WebDriver} implementation and references
 * a {@link geb.Page} object that provides access to the content. 
 * <p>
 * Browser objects dynamically delegate all method calls and property read/writes that it doesn't implement to the current
 * page instance via {@code propertyMissing()} and {@code methodMissing()}.
 */
class Browser {

	private Page page
	private final Configuration config
	private final pageChangeListeners = new LinkedHashSet()
	
	/**
	 * If the driver is remote, this object allows access to its capabilities (users of Geb should not access this object, it is used internally).
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
		page(Page)
	}

	/**
	 * Creates a new browser instance backed by the given configuration, then applies {@code props} as property overrides on the browser.
	 * 
	 * @see geb.Configuration
	 */
	Browser(Map props, Configuration config) {
		this(config)
		this.metaClass.setProperties(this, props)
	}

	/**
	 * Provides access to the current page object.
	 * <p>
	 * All browser objects are created with a page type of {@link geb.Page} initially.
	 */
	Page getPage() {
		this.page
	}
	
	/**
	 * Provides access to the configuration object assoicated with this browser.
	 */
	Configuration getConfig() {
		this.config
	}
	
	/**
	 * The driver implementation used to automate the actual browser.
	 * <p>
	 * The driver implementation to use is determined by the configuration.
	 * 
	 * @see geb.Configuration#getDriver()
	 */
	WebDriver getDriver() {
		config.driver
	}
	
	/**
	 * Set (or change) the webdriver underneath this browser.
	 * <p>
	 * This should only be called before making any requests as a means to override the driver instance
	 * that would be created from the configuration. Where possible, prefer using the configuration mechanism
	 * to specify the driver implementation to use.
	 * <p>
	 * This method delegates to {@link geb.Configuration#setDriver(org.openqa.selenium.WebDriver)}.
	 */
	void setDriver(WebDriver driver) {
		config.driver = driver
	}
	
	/**
	 * The url to resolve all relative urls against. Typically the root of the application or system
	 * Geb is interacting with.
	 * <p>
	 * The base url is determined by the configuration.
	 * 
	 * @see geb.Configuration#getBaseUrl()
	 */
	String getBaseUrl() {
		config.baseUrl
	}
	
	/**
	 * Changes the base url used for resolving relative urls.
	 * <p>
	 * This method delegates to {@link geb.Configuration#setBaseUrl(def)}.
	 */
	void setBaseUrl(String baseUrl) {
		config.baseUrl = baseUrl
	}
	
	/**
	 * Allows new page change listeners to be registered with this browser.
	 * <p>
	 * This method will immediately call the {@link geb.PageChangeListener#pageWillChange(geb.Browser, geb.Page, geb.Page)} method on
	 * {@code listener} with the current page as the {@code newPage} argument and {@code null} for the {@code oldPage} argument.
	 * 
	 * @throws geb.error.PageChangeListenerAlreadyRegisteredException if the listener is already registered.
	 * @see geb.PageChangeListener
	 */
	void registerPageChangeListener(PageChangeListener listener) {
		if (pageChangeListeners.add(listener)) {
			listener.pageWillChange(this, null, page)
		} else {
			throw new PageChangeListenerAlreadyRegisteredException(this, listener)
		}
	}
	
	/**
	 * Removes the given page change listener.
	 * 
	 * @return whether or not the listener was actually registered or not.
	 */
	boolean removePageChangeListener(PageChangeListener listener) {
		pageChangeListeners.remove(listener)
	}
	
	/**
	 * Delegates the method call directly to the current page object.
	 */
	def methodMissing(String name, args) {
		page."$name"(*args)
	}

	/**
	 * Delegates the property access directly to the current page object.
	 */
	def propertyMissing(String name) {
		page."$name"
	}
	
	/**
	 * Delegates the property assignment directly to the current page object.
	 */
	def propertyMissing(String name, value) {
		page."$name" = value
	}	
	
	/**
	 * Changes the browser's page to be an instance of the given class.
	 * <p>
	 * This method performs the following:
	 * <ul>
	 * <li>Create a new instance of the given class (which must be {@link geb.Page} or a subclass thereof) and connect it to this browser object
	 * <li>Inform any registered page change listeners
	 * <li>Set the browser's page property to the created instance (if it is not already of this type)
	 * <p>
	 * <b>Note that it does not verify that the page matches the current content by running its at checker</b>
	 */
	void page(Class<? extends Page> pageClass) {
		page(createPage(pageClass))
	}

	/**
	 * Changes the browser's page to be an instance of the first given type whose at checker returns a true value.
	 * <p>
	 * This method performs the following:
	 * <ul>
	 *   <li>For each given page type:
	 *   <ul>
	 *   <li>Create a new instance of the class (which must be {@link geb.Page} or a subclass thereof) and connect it to the browser object
	 *   <li>Test if the page represents the new instance by running its at checker
	 *   <li>If the page's at checker is successful:
	 *   <ul>
	 *     <li>Inform any registered page change listeners
	 *     <li>Set the browser's page property to the instance (if it is not already of this type)
	 *     <li>Discard the rest of the potentials
	 *   </ul>
	 *   <li>If the page's at checker is not successful:
	 *   <ul>
	 *     <li>Try the next potential
	 */	
	void page(Class<? extends Page>[] potentialPageClasses) {
		def potentialPageClassesClone = potentialPageClasses.toList()
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
	
	/**
	 * Sets this browser's page to be the given page. 
	 * <p>
	 * Any page change listeners are informed and {@link geb.Page#onUnload(geb.Page)} is called 
	 * on the previous page and {@link geb.Page#onLoad(geb.Page)} is called on the incoming page.
	 * <p>
	 * @see #page(Class)
	 */
	void page(Page page) {
		informPageChangeListeners(this.page, page)
		this.page?.onUnload(page)
		def previousPage = this.page
		this.page = page
		this.page.onLoad(previousPage)
	}
	
	/**
	 * Checks if the browser is at the current page by running the at checker for the given page type.
	 * <p>
	 * If the give page type's at checker is successful, this browser object's page instance is updated
	 * to the given type unless it is already of the given type in which case it is not changed.
	 */
	boolean at(Class<? extends Page> pageType) {
		def targetPage = page.class == pageType ? page : createPage(pageType)
		try {
			if (targetPage.verifyAt()) {
				if (pageType != page.class) {
					page(targetPage)
				}
				true
			} else {
				false
			}
		} catch (RequiredPageContentNotPresent e) {
			false
		}
	}

	/**
	 * Sends the browser to the configured {@link #getBaseUrl() base url}.
	 */
	void go() {
		go([:], null)
	}
	
	/**
	 * Sends the browser to the configured {@link #getBaseUrl() base url}, appending {@code params} as
	 * query parameters.
	 */
	void go(Map params) {
		go(params, null)
	}
	
	/**
	 * Sends the browser to the given url. If it is relative it is resolved against the {@link #getBaseUrl() base url}.
	 */
	void go(String url) {
		go([:], url)
	}
	
	/**
	 * Sends the browser to the given url. If it is relative it is resolved against the {@link #getBaseUrl() base url}.
	 */
	void go(Map params, String url) {
		def newUrl = calculateUri(url, params)
		def newPage = driver.get(newUrl)
		if (!page) {
			page(Page)
		}
	}
	
	/**
	 * Sends the browser to the given page type's url and sets the page to a new instance of the given type.
	 * 
	 * @see page(geb.Page)
	 * @see geb.Page#to(Map, Object[])
	 */
	void to(Class<? extends Page> pageType, Object[] args) {
		to([:], pageType, *args)
	}

	/**
	 * Sends the browser to the given page type's url and sets the page to a new instance of the given type.
	 * 
	 * @see page(geb.Page)
	 * @see geb.Page#to(Map, Object[])
	 */
	void to(Map params, Class<? extends Page> pageType) {
		to(params, pageType, null)
	}

	/**
	 * Sends the browser to the given page type's url and sets the page to a new instance of the given type.
	 * 
	 * @see page(geb.Page)
	 * @see geb.Page#to(Map, Object[])
	 */
	void to(Map params, Class<? extends Page> pageType, Object[] args) {
		createPage(pageType).to(params, *args)
	}
	
	/**
	 * Clears all cookies that the browser currently has.
	 * 
	 * @see org.openqa.selenium.WebDriver.Options#deleteAllCookies()
	 */
	void clearCookies() {
		driver?.manage()?.deleteAllCookies()
	}

	/**
	 * Clears all cookies that the browser currently has, suppressing any webdriver exceptions.
	 */
	void clearCookiesQuietly() {
		try {
			clearCookies()
		} catch (WebDriverException e) {
			// ignore
		}
	}
	
	/**
	 * Quits the driver.
	 * 
	 * @see org.openqa.selenium.WebDriver#quit()
	 */
	void quit() {
		driver.quit()
	}

	/**
	 * Closes the current driver window.
	 * 
	 * @see org.openqa.selenium.WebDriver#close()
	 */	
	void close() {
		driver.close()
	}
	
	/**
	 * Creates a new instance of the given page type and initialises it.
	 * 
	 * @return The newly created page instance
	 */
	Page createPage(Class<? extends Page> pageType) {
		if (!Page.isAssignableFrom(pageType)) {
			throw new IllegalArgumentException("$pageType is not a subclass of ${Page}")
		}
		pageType.newInstance().init(this)
	}
	
	/**
	 * Returns a newly created javascript interface connected to this browser.
	 */
	JavascriptInterface getJs() {
		new JavascriptInterface(this)
	}
	
	private informPageChangeListeners(Page oldPage, Page newPage) {
		pageChangeListeners*.pageWillChange(this, oldPage, newPage)
	}
	
	private String toQueryString(Map params) {
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
	
	private String calculateUri(String path, Map params) {
		def uri
		if (path) {
			uri = new URI(path)
			if (!uri.absolute) {
				uri = new URI(baseUrl).resolve(uri)
			}
		} else {
			uri = new URI(baseUrl)
		}
		
		def queryString = toQueryString(params)
		if (queryString) {
			def joiner = uri.query ? '&' : '?'
			new URL(uri.toString() + joiner + queryString).toString()
		} else {
			uri.toString()
		}
	}

	/**
	 * Creates a new browser object via the default constructor and executes the closure
	 * with the browser instance as the closure's delegate.
	 * 
	 * @return the created browser
	 */
	static Browser drive(Closure script) {
		drive(new Browser(), script)
	}

	/**
	 * Creates a new browser with the configuration and executes the closure
	 * with the browser instance as the closure's delegate.
	 * 
	 * @return the created browser
	 */
	static Browser drive(Configuration conf, Closure script) {
		drive(new Browser(conf), script)
	}
	
	/**
	 * Creates a new browser with the properties and executes the closure
	 * with the browser instance as the closure's delegate.
	 * 
	 * @return the created browser
	 */
	static Browser drive(Map browserProperties, Closure script) {
		drive(new Browser(browserProperties), script)
	}
	
	/**
	 * Executes the closure with browser as its delegate.
	 * 
	 * @return browser
	 */
	static Browser drive(Browser browser, Closure script) {
		script.delegate = browser
		script()
		browser
	}
	
}