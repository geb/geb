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

import com.google.common.net.UrlEscapers
import geb.driver.RemoteDriverOperations
import geb.error.NoNewWindowException
import geb.error.PageChangeListenerAlreadyRegisteredException
import geb.error.UnexpectedPageException
import geb.js.JavascriptInterface
import geb.navigator.factory.NavigatorFactory
import geb.url.UrlFragment
import geb.report.ReportState
import org.openqa.selenium.NoSuchWindowException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebDriverException

/**
 * The browser is the centre of Geb. It encapsulates a {@link org.openqa.selenium.WebDriver} implementation and references
 * a {@link geb.Page} object that provides access to the content.
 * <p>
 * Browser objects dynamically delegate all method calls and property read/writes that it doesn't implement to the current
 * page instance via {@code propertyMissing ( )} and {@code methodMissing ( )}.
 */
@SuppressWarnings("UnnecessaryPublicModifier")
class Browser {

    public static final String UTF8 = "UTF-8"
    public static final String QUERY_STRING_SEPARATOR = "&"

    private Page page
    private final Configuration config
    private final pageChangeListeners = new LinkedHashSet()
    private String reportGroup = null
    private NavigatorFactory navigatorFactory = null

    /**
     * If the driver is remote, this object allows access to its capabilities (users of Geb should not access this object, it is used internally).
     */
    @Lazy
    WebDriver augmentedDriver = new RemoteDriverOperations(this.class.classLoader).getAugmentedDriver(driver)

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
        if (page == null) {
            page = createPage(Page)
        }

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
     * Returns the factory that creates navigator instances for this browser.
     *
     * @return The navigator factory
     */
    NavigatorFactory getNavigatorFactory() {
        if (navigatorFactory == null) {
            navigatorFactory = createNavigatorFactory()
        }

        navigatorFactory
    }

    /**
     * Called to create the navigator factory, the first time it is requested.
     *
     * @return The navigator factory
     */
    protected NavigatorFactory createNavigatorFactory() {
        config.createNavigatorFactory(this)
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
     * Retrieves the current url
     *
     * @see org.openqa.selenium.WebDriver#getCurrentUrl()
     */
    String getCurrentUrl() {
        driver.currentUrl
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
            listener.pageWillChange(this, null, getPage())
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
        getPage()."$name"(*args)
    }

    /**
     * Delegates the property access directly to the current page object.
     */
    def propertyMissing(String name) {
        getPage()."$name"
    }

    /**
     * Delegates the property assignment directly to the current page object.
     */
    def propertyMissing(String name, value) {
        getPage()."$name" = value
    }

    /**
     * Changes the browser's page to be an instance of the given class.
     * <p>
     * This method performs the following:
     * <ul>
     * <li>Create a new instance of the given class (which must be {@link geb.Page} or a subclass thereof) and connect it to this browser object
     * <li>Inform any registered page change listeners
     * <li>Set the browser's page property to the created instance (if it is not already of this type)
     * </ul>
     * <p>
     * <b>Note that it does not verify that the page matches the current content by running its at checker</b>
     *
     * @return an initialized page instance set as the current page
     */
    public <T extends Page> T page(Class<T> pageClass) {
        makeCurrentPage(createPage(pageClass))
    }

    /**
     * Changes the browser's page to be an instance of the first given type whose at checker returns a true value.
     * <p>
     * This method performs the following:
     * <ul>
     *   <li>Check if not at an unexpected page
     *   <li>For each given page type:
     *     <ul>
     *     <li>Create a new instance of the class (which must be {@link geb.Page} or a subclass thereof) and connect it to the browser object
     *     <li>Test if the page represents the new instance by running its at checker
     *     <li>If the page's at checker is successful:
     *       <ul>
     *       <li>Inform any registered page change listeners
     *       <li>Set the browser's page property to the instance
     *       <li>Discard the rest of the potentials
     *       </ul>
     *     <li>If the page's at checker is not successful:
     *       <ul>
     *       <li>Try the next potential
     *       </ul>
     *     </ul>
     * </ul>
     *
     * @return an initialized page instance set as the current page
     */
    Page page(Class<? extends Page>[] potentialPageClasses) {
        checkIfAtAnUnexpectedPage(potentialPageClasses)
        verifyPages(potentialPageClasses.collect { createPage(it) })
    }

    /**
     * Sets this browser's page to be the given page after initializing it.
     *
     * <p>
     * This method performs the following:
     * <ul>
     * <li>Connect the instance passed in to this browser object
     * <li>Inform any registered page change listeners
     * <li>Set the browser's page property to the instance passed in
     * <p>
     * <b>Note that it does not verify that the page matches the current content by running its at checker</b>
     *
     * @return a page instance passed as the first argument after initializing
     */
    public <T extends Page> T page(T page) {
        makeCurrentPage(initialisePage(page))
        page
    }

    /**
     * Changes the browser's page to be an instance of the first given instance whose at checker returns a true value.
     * <p>
     * This method performs the following:
     * <ul>
     *   <li>Check if not at an unexpected page
     *   <li>For each given page instance:
     *     <ul>
     *     <li>Connects the page instance to the browser object
     *     <li>Test if the page represents the instance by running its at checker
     *     <li>If the page's at checker is successful:
     *       <ul>
     *       <li>Inform any registered page change listeners
     *       <li>Set the browser's page property to the instance (if it is not already of this type)
     *       <li>Discard the rest of the potentials
     *       </ul>
     *     </ul>
     *   <li>If the page's at checker is not successful:
     *   <ul>
     *     <li>Try the next potential
     *   </ul>
     * </ul>
     *
     * @return an initialized page instance set as the current page
     */
    public Page page(Page[] potentialPageInstances) {
        checkIfAtAnUnexpectedPage(potentialPageInstances)
        verifyPages(potentialPageInstances.collect { initialisePage(it) })
    }

    /**
     * Checks if the browser is at the current page by running the at checker for this page type
     *
     * A new instance of the page is created for the at check. If the at checker is successful,
     * this browser object's page instance is updated to the new instance of the given page type and the new instance is returned.
     *
     * If the given pageType does not define an at checker, UndefinedAtCheckerException is thrown.
     *
     * <p>
     * If <a href="../../#implicit-assertions">implicit assertions</a>
     * are enabled (which they are by default). This method will only ever return a page instance or throw an {@link AssertionError}
     *
     * @return a page instance of the given page type when the at checker succeeded or null otherwise (never null if implicit assertions are enabled)
     */
    public <T extends Page> T at(Class<T> pageType) {
        validatePage(pageType)
        doAt(createPage(pageType))
    }

    /**
     * Checks if the browser is at the current page by running the at checker for this page instance
     *
     * If the at checker is successful, this browser object's page instance is updated to the said instance of the page.
     *
     * If the given page object does not define an at checker, UndefinedAtCheckerException is thrown.
     *
     * <p>
     * If <a href="../../#implicit-assertions">implicit assertions</a>
     * are enabled (which they are by default). This method will only ever return a page instance or throw an {@link AssertionError}
     *
     * @return a page instance of the passed page after initializing when the at checker succeeded or null otherwise (never null if implicit assertions are enabled)
     */
    public <T extends Page> T at(T page) {
        doAt(page)
    }

    /**
     * Checks if the browser is at the given page by running the at checker for this page type, suppressing assertion errors.
     *
     * If the at checker is successful, this browser object's page instance is updated the one the method is called with.
     *
     * If the given pageType does not define an at checker, UndefinedAtCheckerException is thrown.
     *
     * If the at check throws an {@link AssertionError}
     * (as it will when <a href="../../#implicit-assertions">implicit assertions</a>
     * are enabled) this method will suppress the exception and return false.
     *
     * @return true if browser is at the given page otherwise false
     */
    boolean isAt(Class<? extends Page> pageType, boolean honourGlobalAtCheckWaiting = true) {
        isAt(createPage(pageType), honourGlobalAtCheckWaiting)
    }

    /**
     * Checks if the browser is at the given page by running the at checker for this page instance, suppressing assertion errors.
     *
     * If the at checker is successful, this browser object's page instance is updated the one the method is called with.
     *
     * If the given page instance does not define an at checker, UndefinedAtCheckerException is thrown.
     *
     * If the at check throws an {@link AssertionError}
     * (as it will when <a href="../../#implicit-assertions">implicit assertions</a>
     * are enabled) this method will suppress the exception and return false.
     *
     * @return true if browser is at the given page otherwise false
     */
    boolean isAt(Page page, boolean honourGlobalAtCheckWaiting = true) {
        initialisePage(page)
        def isAt = page.verifyAtSafely(honourGlobalAtCheckWaiting)
        if (isAt) {
            makeCurrentPage(page)
        }
        isAt
    }

    /**
     * Check if at one of the pages configured to be unexpected.
     *
     * @param expectedPages allows to specify which of the unexpected pages to ignore for the check
     * @throws UnexpectedPageException when at an unexpected page
     */
    void checkIfAtAnUnexpectedPage(Class<? extends Page>[] expectedPages) {
        doCheckIfAtAnUnexpectedPage(expectedPages)
    }

    /**
     * Check if at one of the pages configured to be unexpected.
     *
     * @param expectedPages allows to specify which of the unexpected pages to ignore for the check
     * @throws UnexpectedPageException when at an unexpected page
     */
    void checkIfAtAnUnexpectedPage(Page[] expectedPages) {
        doCheckIfAtAnUnexpectedPage(expectedPages)
    }

    private doCheckIfAtAnUnexpectedPage(def expectedPages) {
        def unexpectedPages = config.unexpectedPages - expectedPages.toList()
        unexpectedPages.each {
            if (isAt(it, false)) {
                throw new UnexpectedPageException(it, *expectedPages)
            }
        }
    }

    /**
     * Sets this browser's page to be the given page, which has already been initialised with this browser instance.
     * <p>
     * Any page change listeners are informed and {@link geb.Page#onUnload(geb.Page)} is called
     * on the previous page and {@link geb.Page#onLoad(geb.Page)} is called on the incoming page.
     * <p>
     */
    private Page makeCurrentPage(Page page) {
        if (page != getPage()) {
            informPageChangeListeners(getPage(), page)
            getPage().onUnload(page)
            def previousPage = getPage()
            this.page = page
            getPage().onLoad(previousPage)
        }
        getPage()
    }

    private <T extends Page> T initialisePage(T page) {
        if (!this.is(page.browser)) {
            page.init(this)
        }
        page
    }

    /**
     * Runs a page's at checker, expecting the page to be initialised with this browser instance.
     */
    private <T extends Page> T doAt(T page) {
        initialisePage(page)
        def atResult = page.verifyAt()
        if (atResult) {
            makeCurrentPage(page)
            page
        } else {
            null
        }
    }

    private void validatePage(Class<?> pageType) {
        if (!Page.isAssignableFrom(pageType)) {
            throw new IllegalArgumentException("$pageType is not a subclass of ${Page}")
        }
    }

    /**
     * Sends the browser to the given url. If it is relative it is resolved against the {@link #getBaseUrl() base url}.
     */
    void go(String url) {
        go([:], url, null)
    }

    /**
     * Sends the browser to the given url and fragment. If the url is relative it is resolved against the {@link #getBaseUrl() base url}.
     */
    void go(String url, UrlFragment fragment) {
        go([:], url, fragment)
    }

    /**
     * Sends the browser to the {@link #getBaseUrl() base url} with the given query params and fragment.
     */
    void go(Map params = [:], UrlFragment fragment) {
        go(params, null, fragment)
    }

    /**
     * Sends the browser to the given url with the given query params and fragment. If the url is relative it is resolved against the {@link #getBaseUrl() base url}.
     */
    void go(Map params = [:], String url = null, UrlFragment fragment = null) {
        def newUri = calculateUri(url, params, fragment)
        def currentUri = retrieveCurrentUri()
        driver.get(newUri.toString())
        if (sameUrlWithDifferentFragment(currentUri, newUri)) {
            driver.navigate().refresh()
        }
        if (!page) {
            page(Page)
        }
    }

    private URI retrieveCurrentUri() {
        def currentUri = null
        try {
            def currentUrl = driver.currentUrl
            currentUri = currentUrl ? new URI(currentUrl) : null
        } catch (NullPointerException npe) {
        } catch (URISyntaxException use) {
        } catch (WebDriverException webDriverException) {
            if (!webDriverException.message.contains("Remote browser did not respond to getCurrentUrl")) {
                throw webDriverException
            }
        }
        currentUri
    }

    private boolean sameUrlWithDifferentFragment(URI current, URI next) {
        current && next.fragment && ignoreFragment(current) == ignoreFragment(next)
    }

    private URI ignoreFragment(URI uri) {
        new URI(uri.scheme, uri.schemeSpecificPart, null)
    }

    /**
     * Sends the browser to the given page type's url, sets the page to a new instance of the given type and verifies the at checker of that page.
     *
     * @return a page instance of the passed page type when the at checker succeeded
     * @see #page(geb.Page)
     * @see geb.Page#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    public <T extends Page> T to(Map params = [:], Class<T> pageType, Object[] args) {
        to(params, pageType, null, args)
    }

    /**
     * Sends the browser to the given page type's url, sets the page to a new instance of the given type and verifies the at checker of that page.
     *
     * @return a page instance of the passed page type when the at checker succeeded
     * @see #page(geb.Page)
     * @see geb.Page#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    public <T extends Page> T to(Map params = [:], Class<T> pageType, UrlFragment fragment, Object[] args) {
        to(params, createPage(pageType), fragment, args)
    }

    /**
     * Sends the browser to the given page instance url, sets the page to the given page instance and verifies the at checker of that page.
     *
     * @return a page instance of the passed page after initializing when the at checker succeeded
     * @see #page(geb.Page)
     * @see geb.Page#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    public <T extends Page> T to(Map params = [:], T page, Object[] args) {
        to(params, page, null, args)
    }

    /**
     * Sends the browser to the given page instance url, sets the page to a new instance of the given type and verifies the at checker of that page.
     *
     * @return a page instance of the passed page type when the at checker succeeded
     * @see #page(geb.Page)
     * @see geb.Page#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    public <T extends Page> T to(Map params = [:], T page, UrlFragment fragment, Object[] args) {
        via(params, page, fragment, args)
        page.at ? at(page) : page
    }

    /**
     * Sends the browser to the given page type's url and sets the page to a new instance of the given type.
     *
     * @return a page instance of the passed page type
     * @see #page(geb.Page)
     * @see geb.Page#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    public <T extends Page> T via(Map params = [:], Class<T> pageType, Object[] args) {
        via(params, pageType, null, args)
    }

    /**
     * Sends the browser to the given page type's url and sets the page to a new instance of the given type.
     *
     * @return a page instance of the passed page type
     * @see #page(geb.Page)
     * @see geb.Page#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    public <T extends Page> T via(Map params = [:], Class<T> pageType, UrlFragment fragment, Object[] args) {
        via(params, createPage(pageType), fragment, args)
    }

    /**
     * Sends the browser to the given page instance url and sets the page the given instance.
     *
     * @return a page instance that was passed after initializing it.
     * @see #page(geb.Page)
     * @see geb.Page#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    public <T extends Page> T via(Map params = [:], T page, Object[] args) {
        via(params, page, null, args)
    }

    /**
     * Sends the browser to the given page instance url and sets the page the given instance.
     *
     * @return a page instance that was passed after initializing it.
     * @see #page(geb.Page)
     * @see geb.Page#to(java.util.Map, geb.url.UrlFragment, java.lang.Object)
     */
    public <T extends Page> T via(Map params = [:], T page, UrlFragment fragment, Object[] args) {
        initialisePage(page)
        page.to(params, fragment, args)
        page
    }

    /**
     * Clears cookies for the current domain and a number of additional domains by navigating the browser to each of to the urls passed as the argument to this method and clearing cookies for them.
     *
     * @param additionalUrls urls for which the cookies should be cleared in addition to the current domain
     */
    void clearCookies(String... additionalUrls) {
        clearCookies()
        additionalUrls.each {
            go(it)
            clearCookies()
        }
    }

    /**
     * Clears all cookies for the <b>current domain</b> that the browser has.
     * <p>
     * If the driven browser has accumulated cookies for additional domains that are to be cleared then {@link #clearCookies(java.lang.String[])} method should be used.
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
     * Retrieves current window
     *
     * @see org.openqa.selenium.WebDriver#getWindowHandle()
     */
    String getCurrentWindow() {
        driver.windowHandle
    }

    /**
     * Retrieves all available windows
     *
     * @see org.openqa.selenium.WebDriver#getWindowHandles()
     */
    Set<String> getAvailableWindows() {
        driver.windowHandles
    }

    protected switchToWindow(String window) {
        driver.switchTo().window(window)
    }

    /**
     * Executes a closure within the context of a window specified by a name
     *
     * @param window name of the window to use as context
     * @param block closure to be executed in the window context
     * @return The return value of {@code block}
     */
    def withWindow(String window, Closure block) {
        withWindow([:], window, block)
    }

    /**
     * Executes a closure within the context of all windows for which the specification
     * closure returns groovy truth.
     *
     * @param specification closure executed once in context of each window, if it returns groovy truth for a given
     * window then also the block closure is executed in the context of that window
     * @param block closure to be executed in the window context
     * @return List containing values returned from {@code block} for each window for which {@code specification} returns groovy truth
     */
    List<?> withWindow(Closure specification, Closure block) {
        withWindow([:], specification, block)
    }

    /**
     * Executes a closure within the context of all windows for which the specification
     * closure returns groovy truth.
     *
     * @param options a map that can be used to pass additional options
     * @param specification closure executed once in context of each window, if it returns groovy truth for a given
     * window then also the block closure is executed in the context of that window
     * @param block closure to be executed in the window context
     * @return List containing values returned from {@code block} for each window for which {@code specification} returns groovy truth
     */
    List<?> withWindow(Map options, Closure specification, Closure block) {
        def anyMatching = false
        def original = currentWindow
        def originalPage = getPage()
        def blockResults = []

        try {
            availableWindows.each {
                switchToWindow(it)
                if (specification.call()) {
                    verifyAtIfPresent(options.page)

                    try {
                        blockResults << block.call()
                    } finally {
                        if (options.close) {
                            driver.close()
                        }
                        anyMatching = true
                    }
                }
            }
        } finally {
            switchToWindow(original)
            page originalPage
        }
        if (!anyMatching) {
            throw new NoSuchWindowException('Could not find a window that would match the specification')
        }
        blockResults
    }

    /**
     * Executes a closure within the context of a window specified by a name
     *
     * @param options a map that can be used to pass additional options
     * @param window name of the window to use as context
     * @param block closure to be executed in the window context
     * @return The return value of {@code block}
     */
    def withWindow(Map options, String window, Closure block) {
        def original = currentWindow
        def originalPage = getPage()

        switchToWindow(window)
        try {
            verifyAtIfPresent(options.page)

            block.call()
        } finally {
            if (options.close) {
                driver.close()
            }
            switchToWindow(original)
            page originalPage
        }
    }

    /**
     * Expects the first closure argument to open a new window and calls the second closure argument in the context
     * of the newly opened window. A map of options can also be specified that allows to close the new window, switch to a
     * different page for closure executed in the context of the new window and also to wait for the window opening if the
     * window opening is asynchronous.
     *
     * @param options a map that can be used to pass additional options
     * @param windowOpeningBlock a closure that should open a new window
     * @param block closure to be executed in the new window context
     * @return The return value of {@code block}
     * @throws geb.error.NoNewWindowException if the window opening closure doesn't open one or opens more
     * than one new window
     */
    def withNewWindow(Map options, Closure windowOpeningBlock, Closure block) {
        def originalWindow = currentWindow
        def originalPage = getPage()

        def newWindow = executeNewWindowOpening(windowOpeningBlock, options.wait)
        try {
            switchToWindow(newWindow)
            verifyAtIfPresent(options.page)

            block.call()
        } finally {
            if (!options.containsKey('close') || options.close) {
                driver.close()
            }
            switchToWindow(originalWindow)
            page originalPage
        }
    }

    private String executeNewWindowOpening(Closure windowOpeningBlock, wait) {
        def originalWindows = availableWindows
        windowOpeningBlock.call()

        if (wait) {
            config.getWaitForParam(wait).waitFor { (availableWindows - originalWindows).size() == 1 }
        }

        def newWindows = (availableWindows - originalWindows) as List

        if (newWindows.size() != 1) {
            def message = newWindows ? 'There has been more than one window opened' : 'No new window has been opened'
            throw new NoNewWindowException(message)
        }
        newWindows.first()
    }

    /**
     * Expects the first closure argument to open a new window and calls the second closure argument in the context
     * of the newly opened window.
     *
     * @param windowOpeningBlock a closure that should open a new window
     * @param block closure to be executed in the new window context
     * @return The return value of {@code block}
     * @throws geb.error.NoNewWindowException if the window opening closure doesn't open one or opens more
     * than one new window
     */
    def withNewWindow(Closure windowOpeningBlock, Closure block) {
        withNewWindow([:], windowOpeningBlock, block)
    }

    /**
     * Creates a new instance of the given page type and initialises it.
     *
     * @return The newly created page instance
     */
    public <T extends Page> T createPage(Class<T> pageType) {
        validatePage(pageType)
        pageType.newInstance().init(this)
    }

    /**
     * Returns a newly created javascript interface connected to this browser.
     */
    JavascriptInterface getJs() {
        new JavascriptInterface(this)
    }

    /**
     * The directory that will be used for the {@link #report(java.lang.String) method}.
     * <p>
     * Uses the {@link geb.Configuration#getReportsDir()} for the base location for reports (throwing an exception if this is not set), and
     * appending the current report group. The returned file object is guaranteed to exist on the filesystem.
     * <p>
     * If the current report group is {@code null}, this method returns the same as {@code config.reportsDir}.
     *
     * @see #reportGroup(java.lang.String)
     * @see #report(java.lang.String)
     */
    File getReportGroupDir() {
        def reportsDir = config.reportsDir
        if (reportsDir == null) {
            throw new IllegalStateException("No reports dir has been configured, you need to set in the config file or via the build adapter.")
        }

        def reportGroupDir = reportGroup ? new File(reportsDir, reportGroup) : reportsDir
        if (!(reportGroupDir.mkdirs() || reportGroupDir.exists())) {
            throw new IllegalStateException("Could not create report group dir '${reportGroupDir}'")
        }

        reportGroupDir
    }

    /**
     * Sets the "group" for all subsequent reports, which is the relative path inside the reports dir that reports will be written to.
     *
     * @param path a <strong>relative</strong> path, or {@code null} to have reports written to the base reports dir
     */
    void reportGroup(String path) {
        reportGroup = path
    }

    /**
     * Sets the report group to be the full name of the class, replacing "." with "/".
     *
     * @see #reportGroup(String)
     */
    void reportGroup(Class clazz) {
        reportGroup(clazz.name.replace('.', '/'))
    }

    /**
     * Removes the directory returned by {@link #getReportGroupDir()} from the filesystem if it exists.
     */
    void cleanReportGroupDir() {
        def dir = getReportGroupDir()
        if (dir != null) {
            if (dir.exists() && !dir.deleteDir()) {
                throw new IllegalStateException("Could not clean report dir '${dir}'")
            }
        }
    }

    /**
     * Writes a snapshot of the browser's state to the current {@link #getReportGroupDir()} using
     * the {@link geb.Configuration#getReporter() config's reporter}.
     *
     * @param label The name for the report file (should not include a file extension)
     */
    void report(String label) {
        config.reporter.writeReport(new ReportState(this, label, getReportGroupDir()))
    }

    private informPageChangeListeners(Page oldPage, Page newPage) {
        pageChangeListeners*.pageWillChange(this, oldPage, newPage)
    }

    private String toQueryString(Map params) {
        def escaper = UrlEscapers.urlFormParameterEscaper()
        if (params) {
            params.collectMany { name, value ->
                def values = value instanceof Collection ? value : [value]
                values.collect { v ->
                    "${escaper.escape(name.toString())}=${escaper.escape(v.toString())}"
                }
            }.join(QUERY_STRING_SEPARATOR)
        } else {
            null
        }
    }

    private String getBaseUrlRequired() {
        def baseUrl = getBaseUrl()
        if (baseUrl == null) {
            throw new geb.error.NoBaseUrlDefinedException()
        }
        baseUrl
    }

    private URI calculateUri(String path, Map params, UrlFragment fragment) {
        def absolute = calculateAbsoluteUri(path)

        def uriStringBuilder = new StringBuilder()
        uriStringBuilder << new URI(absolute.scheme, absolute.userInfo, absolute.host, absolute.port, absolute.path, null, null)

        def queryString = [absolute.rawQuery, toQueryString(params)].findAll().join(QUERY_STRING_SEPARATOR) ?: null
        if (queryString) {
            uriStringBuilder << "?" << queryString
        }

        def effectiveFragment = UrlEscapers.urlFragmentEscaper().escape(fragment?.toString() ?: "") ?: absolute.rawFragment
        if (effectiveFragment) {
            uriStringBuilder << "#" << effectiveFragment
        }

        new URI(uriStringBuilder.toString())
    }

    private URI calculateAbsoluteUri(String path) {
        def absolute
        if (path) {
            absolute = new URI(path)
            if (!absolute.absolute) {
                absolute = new URI(getBaseUrlRequired()).resolve(absolute)
            }
        } else {
            absolute = new URI(getBaseUrlRequired())
        }
        absolute
    }

    protected void verifyAtIfPresent(def targetPage) {
        if (targetPage) {
            if (targetPage.at) {
                if (!at(targetPage)) {
                    throw new UnexpectedPageException(targetPage)
                }
            } else {
                page(targetPage)
            }
        }
    }

    private Page verifyPages(List<Page> pages) {
        Map pageVerificationResults = [:]
        def match = pages.find {
            AtVerificationResult atVerificationResult = it.atVerificationResult
            if (!atVerificationResult) {
                pageVerificationResults.put(it, atVerificationResult)
            }
            atVerificationResult
        }
        if (match) {
            makeCurrentPage(match)
        } else {
            throw new UnexpectedPageException(pageVerificationResults)
        }
        match
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
