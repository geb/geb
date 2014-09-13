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

import geb.content.*
import geb.download.DownloadSupport
import geb.error.RequiredPageContentNotPresent
import geb.error.UndefinedAtCheckerException
import geb.error.UnexpectedPageException
import geb.frame.FrameSupport
import geb.interaction.InteractionsSupport
import geb.js.AlertAndConfirmSupport
import geb.js.JavascriptInterface
import geb.navigator.Navigator
import geb.textmatching.TextMatchingSupport
import geb.waiting.WaitingSupport
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement

/**
 * The Page type is the basis of the Page Object pattern in Geb.
 * <p>
 * This implementation is a generic model of every page. Subclasses add methods and
 * content definitions that model specific pages.
 * <p>
 * This class (or subclasses) should not be instantiated directly.
 * <p>
 * The following classes are also mixed in to this class:
 * <ul>
 * <li>{@link geb.content.PageContentSupport}
 * <li>{@link geb.download.DownloadSupport}
 * <li>{@link geb.waiting.WaitingSupport}
 * <li>{@link geb.textmatching.TextMatchingSupport}
 * <li>{@link geb.js.AlertAndConfirmSupport}
 * </ul>
 * <p>
 * See the chapter in the Geb manual on pages for more information on writing subclasses.
 */
class Page implements Navigable, PageContentContainer {

	/**
	 * The "at checker" for this page.
	 * <p>
	 * Subclasses should define a closure here that verifies that the browser is at this page.
	 * <p>
	 * This implementation does not have an at checker (i.e. this property is {@code null})
	 */
	static at = null

	/**
	 * Defines the url for this page to be used when navigating directly to this page.
	 * <p>
	 * Subclasses can specify either an absolute url, or one relative to the browser's base url.
	 * <p>
	 * This implementation returns an empty string.
	 *
	 * @see #to(java.util.Map, java.lang.Object)
	 */
	static url = ""

	private Browser browser

	@Delegate
	private PageContentSupport pageContentSupport
	@Delegate
	private DownloadSupport _downloadSupport
	@Delegate
	private WaitingSupport _waitingSupport
	@Delegate
	private FrameSupport frameSupport
	@Delegate
	private InteractionsSupport interactionsSupport
	@Delegate
	private final TextMatchingSupport textMatchingSupport = new TextMatchingSupport()
	@Delegate
	private AlertAndConfirmSupport _alertAndConfirmSupport

	private NavigableSupport navigableSupport

	/**
	 * Initialises this page instance, connecting it to the browser.
	 * <p>
	 * <b>This method is called internally, and should not be called by users of Geb.</b>
	 */
	@SuppressWarnings('SpaceBeforeOpeningBrace')
	Page init(Browser browser) {
		this.browser = browser
		def contentTemplates = PageContentTemplateBuilder.build(browser.config, this, browser.navigatorFactory, 'content', this.class, Page)
		pageContentSupport = new PageContentSupport(this, contentTemplates, browser.navigatorFactory)
		navigableSupport = new NavigableSupport(browser.navigatorFactory)
		_downloadSupport = new DownloadSupport(browser)
		_waitingSupport = new WaitingSupport(browser.config)
		frameSupport = new FrameSupport(browser)
		interactionsSupport = new InteractionsSupport(browser)
		_alertAndConfirmSupport = new AlertAndConfirmSupport({ this.getJs() }, browser.config)
		this
	}

	/**
	 * The browser that the page is connected to.
	 */
	Browser getBrowser() {
		browser
	}

	/**
	 * The driver of the browser that the page is connected to.
	 */
	WebDriver getDriver() {
		browser.driver
	}

	/**
	 * Returns the simple name of this class.
	 *
	 * @see Class#getSimpleName()
	 */
	String toString() {
		this.class.simpleName
	}

	/**
	 * Checks if the browser is not at an unexpected page and then executes this page's "at checker".
	 *
	 * @return whether the at checker succeeded or not.
	 * @see #verifyAtSafely(boolean)
	 * @throws AssertionError if this page's "at checker" doesn't pass (with implicit assertions enabled)
	 * @throws UnexpectedPageException when at an unexpected page
	 */
	boolean verifyAt() {
		browser.checkIfAtAnUnexpectedPage(getClass())
		verifyThisPageAtOnly(true)
	}

	/**
	 * Executes this page's "at checker", suppressing any AssertionError that is thrown
	 * and returning false.
	 *
	 * @return whether the at checker succeeded or not.
	 * @see #verifyAt()
	 */
	boolean verifyAtSafely(boolean allowAtCheckWaiting = true) {
		try {
			verifyThisPageAtOnly(allowAtCheckWaiting)
		} catch (AssertionError e) {
			false
		} catch (RequiredPageContentNotPresent e) {
			false
		}
	}

	/**
	 * Executes this page's "at checker".
	 *
	 * @return whether the at checker succeeded or not.
	 * @throws AssertionError if this page's "at checker" doesn't pass (with implicit assertions enabled)
	 */
	private boolean verifyThisPageAtOnly(boolean allowAtCheckWaiting) {
		def verifier = this.class.at?.clone()
		if (verifier) {
			verifier.delegate = this
			verifier.resolveStrategy = Closure.DELEGATE_FIRST
			def atCheckWaiting = browser.config.atCheckWaiting
			(atCheckWaiting && allowAtCheckWaiting) ? atCheckWaiting.waitFor(verifier) : verifier()
		} else {
			throw new UndefinedAtCheckerException(this.class.name)
		}
	}

	/**
	 * Sends the browser to this page's url.
	 *
	 * @param params request parameters to be appended to the url
	 * @param args "things" that can be used to generate an extra path to append to this page's url
	 * @see #convertToPath(java.lang.Object)
	 * @see #getPageUrl(java.lang.String)
	 */
	void to(Map params, Object[] args) {
		def path = convertToPath(* args)
		if (path == null) {
			path = ""
		}
		browser.go(params, getPageUrl(path))
		browser.page(this)
	}

	/**
	 * Returns the constant part of the url to this page.
	 * <p>
	 * This implementation returns the static url property of the class.
	 */
	String getPageUrl() {
		this.class.url
	}

	/**
	 * Returns the url to this page, with path appended to it.
	 *
	 * @see #getPageUrl()
	 */
	String getPageUrl(String path) {
		def pageUrl = getPageUrl()
		path ? (pageUrl ? pageUrl + path : path) : pageUrl
	}

	/**
	 * Converts the arguments to a path to be appended to this page's url.
	 * <p>
	 * This is called by the {@link #to(java.util.Map, java.lang.Object)} method and can be used for accessing variants of the page.
	 * <p>
	 * This implementation returns the string value of each argument, separated by "/"
	 */
	String convertToPath(Object[] args) {
		args ? '/' + args*.toString().join('/') : ""
	}

	/**
	 * Returns the title of the current browser window.
	 *
	 * @see org.openqa.selenium.WebDriver#getTitle()
	 */
	String getTitle() {
		browser.driver.title
	}

	/**
	 * Provides access to the browser object's JavaScript interface.
	 *
	 * @see geb.Browser#getJs()
	 */
	JavascriptInterface getJs() {
		browser.js
	}

	/**
	 * Lifecycle method called when the page is connected to the browser.
	 * <p>
	 * This implementation does nothing.
	 *
	 * @param previousPage The page that was active before this one
	 */
	void onLoad(Page previousPage) {

	}

	/**
	 * Lifecycle method called when this page is being replaced as the browser's page instance.
	 * <p>
	 * This implementation does nothing.
	 *
	 * @param nextPage The page that will be active after this one
	 */
	void onUnload(Page nextPage) {

	}

	Navigator find() {
		navigableSupport.find()
	}

	Navigator $() {
		navigableSupport.$()
	}

	Navigator find(int index) {
		navigableSupport.find(index)
	}

	Navigator find(Range<Integer> range) {
		navigableSupport.find(range)
	}

	Navigator $(int index) {
		navigableSupport.$(index)
	}

	Navigator $(Range<Integer> range) {
		navigableSupport.$(range)
	}

	Navigator find(String selector) {
		navigableSupport.find(selector)
	}

	Navigator $(String selector) {
		navigableSupport.$(selector)
	}

	Navigator find(String selector, int index) {
		navigableSupport.find(selector, index)
	}

	Navigator find(String selector, Range<Integer> range) {
		navigableSupport.find(selector, range)
	}

	Navigator $(String selector, int index) {
		navigableSupport.$(selector, index)
	}

	Navigator $(String selector, Range<Integer> range) {
		navigableSupport.$(selector, range)
	}

	Navigator find(Map<String, Object> attributes) {
		navigableSupport.find(attributes)
	}

	Navigator $(Map<String, Object> attributes) {
		navigableSupport.$(attributes)
	}

	Navigator find(Map<String, Object> attributes, int index) {
		navigableSupport.find(attributes, index)
	}

	Navigator find(Map<String, Object> attributes, Range<Integer> range) {
		navigableSupport.find(attributes, range)
	}

	Navigator $(Map<String, Object> attributes, int index) {
		navigableSupport.$(attributes, index)
	}

	Navigator $(Map<String, Object> attributes, Range<Integer> range) {
		navigableSupport.$(attributes, range)
	}

	Navigator find(Map<String, Object> attributes, String selector) {
		navigableSupport.find(attributes, selector)
	}

	Navigator $(Map<String, Object> attributes, String selector) {
		navigableSupport.$(attributes, selector)
	}

	Navigator find(Map<String, Object> attributes, String selector, int index) {
		navigableSupport.$(attributes, selector, index)
	}

	Navigator find(Map<String, Object> attributes, String selector, Range<Integer> range) {
		navigableSupport.find(attributes, selector, range)
	}

	Navigator $(Map<String, Object> attributes, String selector, int index) {
		navigableSupport.$(attributes, selector, index)
	}

	Navigator $(Map<String, Object> attributes, String selector, Range<Integer> range) {
		navigableSupport.$(attributes, selector, range)
	}

	Navigator $(Navigator[] navigators) {
		navigableSupport.$(navigators)
	}

	Navigator $(WebElement[] elements) {
		navigableSupport.$(elements)
	}
}
