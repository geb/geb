# The Browser

The entry point to Geb is the `Browser` class. A browser object drives an underlying `WebDriver` instance which drives the real or simulated browser and maintains an instance of the current page (if using the Page Object Pattern).

The browser constructor signature is…

    Browser(WebDriver driver = null, String baseUrl = null, Class<? extends Page> pageClass = Page)

Here are some examples of how to create a browser instance…

    import geb.Browser
    import myapp.HomePage
    import org.openqa.selenium.firefox.FirefoxDriver
    
    // Use default driver, no base url and generic first page
    new Browser()
    
    // Use a specific driver, no base url and generic first page
    new Browser(new FirefoxDriver())
    
    // Use a specific driver, a specific base url and generic first page
    new Browser(new FirefoxDriver(), "http://myapp.com")

    // Use a specific driver, a specific base url and a specific first page
    new Browser(new FirefoxDriver(), "http://myapp.com", HomePage)
    
There are also variants that allow using defaults for any parameter…

    new Browser(HomePage)
    new Browser("http://myapp.com")
    new Browser(new FirefoxDriver(), HomePage)

## The Driver

The browser instance drives the actual browser via an instance of [WebDriver][webdriver-api], which can be specified at browser construction. The browser's driver can always be retrieved via the `getDriver()` method.

### The Default Driver

If a driver is not given at construction time, one will attempted to be loaded via the “`geb.driver`” system property. The value can be either the full class name of a [WebDriver][webdriver-api] implementation, or one of the following short names:

<table class="graybox" border="0" cellspacing="0" cellpadding="5">
    <tr><th>Short Name</th><th>Driver</th></tr>
    <tr>
        <td><code>htmlunit</code></td>
        <td><a href="http://webdriver.googlecode.com/svn/javadoc/org/openqa/selenium/htmlunit/HtmlUnitDriver.html">org.openqa.selenium.htmlunit.HtmlUnitDriver</a></td>
    </tr>
    <tr>
        <td><code>firefox</code></td>
        <td><a href="http://webdriver.googlecode.com/svn/javadoc/org/openqa/selenium/firefox/FirefoxDriver.html">org.openqa.selenium.firefox.FirefoxDriver</a></td>
    </tr>
    <tr>
        <td><code>ie</code></td>
        <td><a href="http://webdriver.googlecode.com/svn/javadoc/org/openqa/selenium/ie/InternetExplorerDriver.html">org.openqa.selenium.ie.InternetExplorerDriver</a></td>
    </tr>
    <tr>
        <td><code>chrome</code></td>
        <td><a href="http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/chrome/ChromeDriver.html">org.openqa.selenium.chrome.ChromeDriver</a></td>
    </tr>
</table>

If the “`geb.driver`” system property is not set, each of the above drivers will be tried in the order listed.

#### Lifecycle

Geb internally reuses the default driver per thread. This avoids the overhead of creating a new driver each time which can be significant when working with a real browser.

This means that you may need to call the `clearCookies()` method on the browser in order to not get strange results due to cookies from previous executions.

The shared driver will be closed and quitted when the JVM shuts down.

A new driver can be forced at anytime by calling either of the following `static` methods on the `CachingDriverFactory` class…

    import geb.driver.CachingDriverFactory
    
    def cachedDriver = CachingDriverFactory.clearCache()
    def cachedDriver = CachingDriverFactory.clearCacheAndQuitDriver()

After calling either of this methods, the next request for a default driver will result in a new driver instance being created.

## The Base URL

Browser instances maintain a `String baseUrl` property that is used to complete all non absolute page URLs. This can be set either at construction time or anytime after.

Consider the following page class…

    class HomePage extends Page {
        static url = "/home"
    }

And the following browser…

    def browser = new Browser("http://myapp.com")

The following code will result in a request being sent to: `http://myapp.com/home`…

    browser.to(HomePage)

See [making requests](#making_requests) for more information on the to() method.

## The Page

Browser instances maintain a _page_ (an object of type `geb.Page`) that represents the page the browser is at. The page instance is retrievable via the read only property `page`. 

The browser uses Groovy's dynamism to delegate any method calls or property accesses that it can't handle to the current page…

    def browser = new Browser("http://myapp.com")
    browser.go("/signup")
    
    // The following two lines are equivalent
    assert browser.$("h1").text() == "Signup Page"
    assert browser.page.$("h1").text() == "Signup Page"

> for more information on the $ function and other methods seen here, see the section on [navigation][navigator]

By default, the starting page is an instance of the `geb.Page` base class which provides the basic navigation functions. There are constructor variants for `geb.Browser` that allow the initial page to be specified.

### Changing The Page

Typically, when the actual page that the real browser is at changes you want to change the browser's page instance to be of a new type. Or, your `geb.Browser` instance for one reason or another has a page instance that does not represent the actual page that the real browser is at. You can change the page instance by using the `page()` method on the browser object.

> Note that clicking content can implicitly change the page type on your behalf by calling the methods below.

#### page(Class newPageType)

The `page()` method that takes a single `Class` object does the following:

* Create a new instance of the given class and connect it to the browser object
* Inform any registered [page change listeners][#page_change_listening] 
* Set the browser's `page` property to the new instances

Note that it **does not** instruct the real browser to make a request. It simply changes the browser object's page instance.

#### page(List<Class> potentialPageTypes)

The `page()` method that takes a list of `Class` objects does the following:

* For each given page type:
    * Create a new instance of the given class and connect it to the browser object
    * Test if the page represents the new instance by running its [at checker][page-at]
    * If the page's at checker is successful:
        * Inform any registered [page change listeners](#page_change_listening)
        * Set the browser's `page` property to the match
        * Discard the rest of the potentials
    * If the page's at checker is not successful
        * Try the next potential

If no match can be found from the given potentials, a `geb.error.UnexpectedPageException` will be thrown. Note that this method **does not** instruct the real browser to make a request. It simply changes the browser object's page instance.

This method exists to cater for situations where you are unsure what the page might be due to some action taken server side.

## Making Requests

### Using Pages

The `page(Class<? extends Page>)` method only sets the current page instance to be of a new type. To do this and make a request to the url that the page specifies, you use the `to(Class<? extends Page>)` method(s).

    class SignupPage extends Page {
        static url = "/signup"
    }
    
    def browser = new Browser("http://myapp.com")
    browser.to(SignupPage)
    assert browser.$("h1").text() == "Signup Page"
    assert browser.page instanceof SignupPage

> see the section on [Advanced Page Navigation][page-navigation] for more information on this topic.

### Direct

To make a request without changing the current page type you can use the `go()` method…

    def browser = new Browser("http://myapp.com")
    
    // Go to the Base URL
    browser.go()
    
    // Go to a URL relative to Base URL
    browser.go("/signup")
    
    // Go to a URL with request params, i.e http://myapp.com/signup?param1=value1&param2=value2
    browser.go("/signup", param1: "value1", param2: "value2")
    
    // Go to the Base URL with request params, i.e http://myapp.com?param1=value1&param2=value2
    browser.go("/signup", param1: "value1", param2: "value2")

## Checking the current page

Browser objects have an `at(Class<? extends Page>)` method that returns `true` or `false` whether or not it is actually at the given type. This works by running the given page's [at verficiation][page-at].

This is typically used in conjuction with the `assert` keyword.

    def browser = new Browser("http://myapp.com")
    browser.to(SignupPage)
    assert browser.at(SignupPage)

## The drive() method

The Browser class features a static method that makes Geb scripting a little more convenient.

Here is an example:

    Browser.drive("http://myapp.com") {
        go "/signup"
        $("h1").text() == "Signup Page"
    }

The static `drive()` method takes all of the arguments that the `Browser` constructor takes, and a `Closure`. The closure is evaluated against created browser instance (i.e. it is the *delegate* of the closure). This enables a very convenient scripting environment.

## Page Change Listening

It is possible to be notified when a browser's page _instance_ changes (note that this is not necessarily when the browser makes a request to a new URL) using the `geb.PageChangeListener` interface.

    import geb.PageChangeListener
    
    class EchoingPageChangeListener implements PageChangeListener {
        void pageWillChange(Browser browser, Page oldPage, Page newPage) {
            println "browser '$browser' changing page from '$oldPage' to '$newPage'"
        }
    }
    
    def browser = new Browser()
    def listener = new EchoingPageChangeListener()
    
    browser.registerPageChangeListener(listener)

As soon as a listener is registered, its `pageWillChange()` method will be called with `newPage` as the current page and `oldPage` as `null`. Subsequently, each time the page changes `oldPage` will be the page that the browser currently has, and `newPage` will be the page that will soon be the browser's page.

You can remove remove a listener at any time…

    browser.removePageChangeListener(listener)

The `removePageChangeListener()` returns `true` if `listener` was registered and has now been removed, otherwise it returns `false`.

Listeners cannot be registered twice. If an attempt is made to register a listener that is already registered (i.e. there is another listener that is _equal_ to the listener trying to register, based on their `equals()` implementation) then a `geb.error.PageChangeListenerAlreadyRegisteredException` will be raised.