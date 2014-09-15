# The Browser

The entry point to Geb is the [`Browser`][browser-api] object. 
A browser object marries a [`WebDriver`][webdriver-api] instance (which drives the actual web browser being automated) with the concept of a “current page”.

Browser objects are created with a [configuration][configuration-api] that specifies which driver implemenation to use, the base URL to resolve relative links against and other bits of config. The configuration mechansism allows you to externalise how Geb should operate, which means you can use the same suite of Geb code or tests with different browsers or site instances. The [chapter on configuration](configuration.html) contains more details on how to manage the configuration parameters and what they are.

The default constructor of [`Browser`][browser-api] simply loads its settings from the config mechanism.

    import geb.Browser
    
    def browser = new Browser()

However, if you prefer to specify the driver implementation (or any other settable property on the [browser][browser-api]) you can by using Groovy's map constructor syntax.

    import geb.Browser
    import org.openqa.selenium.firefox.FirefoxDriver
    
    def browser = new Browser(driver: new FirefoxDriver())

Which is the same as…

    def browser = new Browser()
    browser.driver = new FirefoxDriver()

Any property set this way will **override** any settings coming from the config mechanism.

> Note: The behaviour is undefined if a browser's driver is changed after its first use, so you should avoid setting the driver this way and prefer the configuration mechanism.

For drastically custom configuration requirements, you can create your own [configuration][configuration-api] object and construct the browser with it, likely using the [configuration loader](api/geb/ConfigurationLoader.html).

    import geb.Browser
    import geb.Configuration
    import geb.ConfigurationLoader
    
    def loader = new ConfigurationLoader("a-custom-environment")
    def config = loader.conf
    def browser = new Browser(config)

Wherever possible, you should strive to use the no-arg constructor and manage Geb through the inbuilt [configuration mechanism](configuration.html) as it offers a great deal of flexibility and separates your configuration from your code.

> Geb integrations typically remove the need to construct a browser object and do this for you, leaving you to just manage the configuration.

## The drive() method

The Browser class features a static method, [`drive()`](api/geb/Browser.html#drive\(groovy.lang.Closure\)), that makes Geb scripting a little more convenient.

    Browser.drive {
        go "signup"
        $("h1").text() == "Signup Page"
    }

Which is equivalent to:

    def browser = new Browser()
    browser.go "signup"
    browser.$("h1").text() == "Signup Page"

The `drive()` method takes all of the arguments that the 
[`Browser`][browser-api] constructor takes (i.e. none, a 
[configuration][configuration-api] and/or property overrides) or an existing browser instance, and a 
closure. The closure is evaluated against created browser instance (i.e. the browser is made the *delegate* of the closure). The net result is that all top level method calls and property accesses are implied to be against the browser.

The `drive()` method always returns the browser object that was used, so if you need to quit the browser after the drive session you can do something like…

    Browser.drive("http://myapp.com") {
        …
    }.quit()

> For more on when/why you need to manually quit the browser, see the section on the [driver](driver.html)

## Making requests

### The base URL

Browser instances maintain a [`baseUrl`](api/geb/Browser.html#getBaseUrl\(\)) property that is used to resolve all relative URLs.
This value can come from [configuration](configuration.html#base_url) or can be 
[explicitly set](api/geb/Browser.html#setBaseUrl\(java.lang.String\)) on the browser.

Care must be taken with slashes when specifying both the base URL and the relative URL as trailing and leading slashes have significant meaning. The following table illustrates the resolution of different types of URLs.

<table class="graybox" border="0" cellspacing="0" cellpadding="5">
    <tr><th>Base</th><th>Navigating To</th><th>Result</th></tr>
    <tr><td>http://myapp.com/</td><td>abc</td><td>http://myapp.com/abc</td></tr>
    <tr><td>http://myapp.com</td><td>abc</td><td>http://myapp.comabc</td></tr>
    <tr><td>http://myapp.com</td><td>/abc</td><td>http://myapp.com/abc</td></tr>
    <tr><td>http://myapp.com/abc/</td><td>def</td><td>http://myapp.com/abc/def</td></tr>
    <tr><td>http://myapp.com/abc</td><td>def</td><td>http://myapp.com/def</td></tr>
    <tr><td>http://myapp.com/abc/</td><td>/def</td><td>http://myapp.com/def</td></tr>
    <tr><td>http://myapp.com/abc/def/</td><td>jkl</td><td>http://myapp.com/abc/def/jkl</td></tr>
    <tr><td>http://myapp.com/abc/def</td><td>jkl</td><td>http://myapp.com/abc/jkl</td></tr>
    <tr><td>http://myapp.com/abc/def</td><td>/jkl</td><td>http://myapp.com/jkl</td></tr>
</table>

It is usually most desirable to define your base urls with trailing slashes and not to use leading slashes on relative URLs.

### Using pages

Page objects (discussed further shortly) can define a url that will be used when explicitly navigating to that page. This is done with the [`to()`](api/geb/Browser.html#to\(Class%3CT%3E,%20java.lang.Object\)) and [`via()`](api/geb/Browser.html#via\(Class%3CT%3E,%20java.lang.Object\)) methods.

    class SignupPage extends Page {
        static url = "signup"
    }
    
    Browser.drive {
        to SignupPage
        assert $("h1").text() == "Signup Page"
        assert page instanceof SignupPage
    }

The `to()` and `via()` method makes a request to the resolved URL and sets the browser's page instance to an instance of the given class. Most Geb scripts and tests start with a `to()` or `via()` call.

> See the section on [Advanced Page Navigation][page-navigation] for more information on how to use more complicated URL resolution for pages.

### Direct

You can also make a new request to a URL without setting or changing the page using the [`go()`](api/geb/Browser.html#go\(\)) methods.

    import geb.Page
    import geb.spock.GebSpec

    class GoogleSpec extends GebSpec {

        def "go method does NOT set the page"() {
            given:
            Page oldPage = page

            when:
            go "http://google.com"

            then:
            oldPage == page
            driver.currentUrl == "http://google.com"
        }

        def "to method does set the page and change the current url"() {
            given:
            Page oldPage = page

            when:
            to GoogleHomePage

            then:
            oldPage != page
            driver.currentUrl == "http://google.com"
        }
    }

The following examples use a baseUrl of “`http://myapp.com/`”.

    Browser.drive {
        // Go to the Base URL
        go()

        // Go to a URL relative to Base URL
        go "signup"

        // Go to a URL with request params, i.e http://myapp.com/signup?param1=value1&param2=value2
        go "signup", param1: "value1", param2: "value2"
    }

## The Page

Browser instances hold a reference to a _page_. This page instance is retrievable via the [`page`](api/geb/Browser.html#getPage\(\)) property. Initially, all browser instances have a page of type [`Page`](api/geb/Page.html) which provides the basic navigation functions and is the superclass for all page objects.

However, the page property is rarely accessed directly. The browser object will *forward* any method calls or property read/writes that it can't handle to the current page instance. 

    Browser.drive {
        go "signup"
        
        // The following two lines are equivalent
        assert $("h1").text() == "Signup Page"
        assert page.$("h1").text() == "Signup Page"
    }

The *page* is providing the $() function, not the browser. This forwarding facilitates very concise code, void of unnecessary noise.

> for more information on the $() function which is used to interact with page content, see the section on the [Navigator API][navigator].

When using the Page Object pattern, you create subclasses of [`Page`](api/geb/Page.html) that define content via a powerful DSL that allows you to refer to content by meaningful names instead of tag names or CSS expressions.

    class SignupPage extends Page {
        static url = "signup"
        static content = {
            heading { $("h1").text() }
        }
    }
    
    Browser.drive {
        to SignupPage
        assert heading == "Signup Page"
    }

Page objects are discussed in depth in the [pages](pages.html) chapter, which also explores the Content DSL.

### Changing the page

We have already seen that that `to()` methods change the browser's page instance. It is also possible to change the page instance without initiating a new request with the `page()` methods.

The [`page(Class pageType)`](api/geb/Browser.html#page\(Class%3C%3F%20extends%20Page%3E\)) method allows you to change the page to a new instance of the given class. The class must be [Page](api/geb/Page.html) or a subclass thereof. This method **does not** verify that the given page actually matches the content (at checking is discussed shortly).

The [`page(Class[] potentialPageTypes)`](api/geb/Browser.html#page\(Class%3C%3F%20extends%20Page%3E\)) method allows you to specify a number of *potential* page types. Each of the potential pages is instantiated and checked to see if it matches the content the browser is actually currently at by running each pages at checker. All of the page classes passed in must have an “at” checker defined otherwise an `UndefinedAtCheckerException` will be thrown.

These methods are not typically used explicitly but are used by the `to()` method and content definitions that specify the page that the content navigates to when clicked (see the section on the [`to` attribute of the Content DSL](pages.html#to) for more information about this). However, should you need to manually change the page type, they are there.

## At checking

Pages define an [“at checker”][page-at] that the browser uses for checking if it is pointing at a given page.

    class SignupPage extends Page {
        static at = {
            $("h1").text() == "Signup Page"
        }
    }
    
    Browser.drive {
        to SignupPage
    }

> Not using explicit `return` statements in “at” checkers is preferred. Geb transforms all “at” checkers so that each statement in them is asserted (just like for `then:` blocks in Spock specifications). Thanks to that you can immediately see evaluated values of your “at” checker if it fails. See the [“at checker”][page-at] section for more details.

The `to()` method that takes a single page type **verifies** that the the browser ends up at the given type. If the request may initiate a redirect and take the browser to a different page you should use `via()` method:

    Browser.drive {
        via SecurePage
        at AccessDeniedPage
    }

Browser objects have an [`at(Class pageType)`](api/geb/Browser.html#at\(Class%3CT%3E\)) method that tests whether or not the browser is currently at the type of page modeled by the given page object type.

The `at AccessDeniedPage` method call will either return a page instance or throw an `AssertionError` even if there are no explicit assertions in the “at” checker if the checker doesn't pass.

It's always a good idea to either use just the `to()` method or the `via()` method followed by an `at()` check whenever the page changes in order to *fail fast*. Otherwise, subsequent steps may fail in harder to diagnose ways due to the content not matching what is expected and content lookups having strange results.

If you pass a page class that doesn't define an “at” checker to `at()` you will get an `UndefinedAtCheckerException` - “at” checkers are mandatory when doing explicit at checks. This is not the case when implicit at checks are being performed, like when using `to()`. This is done to make you aware that you probably want to define an “at” checker when explicitly verifing if you're at a given page but not forcing you to do so when using implicit at checking.

Pages can also define content that declares what the browser's page type should change to when that content is clicked. After clicking on such content page is automatically at verified (see the DSL reference for the [`to`](pages.html#to) parameter).

    class LoginPage extends Page {
    	static url = "/login"
        static content = {
            loginButton(to: AdminPage) { $("input", type: "submit", name: "login") }
        }
    }
    
    class AdminPage extends Page {
        static at = {
            assert $("h1").text() == "Admin Page"
        }
    }
    
    Browser.drive {
        to LoginPage
        loginButton.click()
        at AdminPage
    }

The `at()` method will also update the browser's page instance to the given page type if its at checker is successful.
 
## Page change listening

It is possible to be notified when a browser's page _instance_ changes (note that this is not necessarily when the browser makes a request to a new URL) using the [`PageChangeListener`](api/geb/PageChangeListener.html) interface.

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

The [`removePageChangeListener(PageChangeListener listener)`](api/geb/Browser.html#removePageChangeListener\(geb.PageChangeListener\)) returns `true` if `listener` was registered and has now been removed, otherwise it returns `false`.

Listeners cannot be registered twice. If an attempt is made to register a listener that is already registered (i.e. there is another listener that is _equal_ to the listener trying to register, based on their `equals()` implementation) then a [`PageChangeListenerAlreadyRegisteredException`](api/geb/error/PageChangeListenerAlreadyRegisteredException.html) will be raised.

## Working with multiple tabs and windows

When you're working with an application that opens new windows or tabs, for example when clicking on a link with a target attribute set, you can use `withWindow()` and `withNewWindow()` methods to execute code in the context of other windows.

If you really need to know the name of the current window or all the names of open windows use [`getCurrentWindow()`](api/geb/Browser.html#getCurrentWindow\(\)) and [`getAvailableWindows()`](api/geb/Browser.html#getAvailableWindows\(\)) methods but `withWindow()` and `withNewWindow()` are the preferred methods when it comes to dealing with multiple windows.

### Switching context to already opened windows
If you know the name of the window in which context you want to execute the code you can use [`withWindow(String windowName, Closure block)`](api/geb/Browser.html#withWindow\(java.lang.String,%20groovy.lang.Closure\)). Given this HTML:

    <a href="http://www.gebish.org" target="myWindow">Geb</a>

This code passes:

    $('a').click()
    withWindow('myWindow') {
        assert title == 'Geb - Very Groovy Browser Automation'
    }

If you don't know the name of the window but you know something about the content of the window you can use the [`withWindow(Closure specification, Closure block)`](api/geb/Browser.html#withWindow\(groovy.lang.Closure,%20groovy.lang.Closure\)) method. The first closure passed should return true for the window, or windows, you want to use as context. Note that if there is no window for which the window specification closure returns true then [`NoSuchWindowException`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/NoSuchWindowException.html) is thrown. So given:

    <a href="http://www.gebish.org" target="_blank">Geb</a>

This code passes:

    $('a').click()
    withWindow({ title == 'Geb - Very Groovy Browser Automation' }) {
        assert $('#slogan').text() == 'very groovy browser automation… web testing, screen scraping and more'
    }

#### Passing options when working with already opened windows

Currently there is only one option that can be passed to a [`withWindow()`](api/geb/Browser.html#withWindow\(java.util.Map,%20groovy.lang.Closure,%20groovy.lang.Closure\)) call which make working with already opened windows even simpler. The general syntax is:

    withWindow({ «window specification» }, «option name»: «option value», ...) { «action executed within the context of the window» }

##### close

Default value: `false`

If you pass any *truish* value as `close` option then all matching windows will be closed after the execution of the closure passed as the last argument to the `withWindow()` call.

##### page

Default value: `null`

If you pass a class that extends `Page` as `page` option, then browser's page will be set to that value before executing the closure passed as the last argument and will be reverted to its original value afterwards. If the page class defines an at checker then this will be verified when the page is set on the browser.

### Switching context to newly opened windows

If you wish to execute code in a window that is newly opened by some of your actions, use the [`withNewWindow(Closure windowOpeningBlock, Closure block)`](api/geb/Browser.html#withNewWindow\(groovy.lang.Closure,%20groovy.lang.Closure\)) method. Given HTML as above the following will pass:

    withNewWindow({ $('a').click() }) {
        assert title == 'Geb - Very Groovy Browser Automation'
    }

Note that if the first parameter opens none or more than one window, then [`NoNewWindowException`](api/geb/error/NoNewWindowException.html) is thrown.

#### Passing options when working with newly opened windows

There are several options that can be passed to a [`withNewWindow()`](api/geb/Browser.html#withNewWindow\(java.util.Map,%20groovy.lang.Closure,%20groovy.lang.Closure\)) call which make working with newly opened windows even simpler. The general syntax is:

	withNewWindow({ «window opening action» }, «option name»: «option value», ...) { «action executed within the context of the window» }

##### close

Default value: `true`

If you pass any truly value as `close` option then the newly opened window will be closed after the execution of the closure passed as the last argument to the `withNewWindow()` call.

##### page

Default value: `null`

If you pass a class that extends `Page` as `page` option then browser's page will be set to that value before executing the closure passed as the last argument and will be reverted to its original value afterwards.

##### wait

Default value: `null`

You can specify `wait` option if the action defined in the window opening closure passed as the first argument is asynchronous and you need to wait for the new window to be opened. The possible values for the `wait` option are consistent with the [ones for `wait` option of content definitions](pages.html#wait).

Given the following HTML:

	<a href="http://www.gebish.org" target="_blank" id="new-window-link">Geb</a>

the following will pass:

	withNewWindow({
		js.exec """
				setTimeout(function() {
					document.getElementById('new-window-link').click();
				}, 200);
			"""
	}, wait: true) {
        assert title == 'Geb - Very Groovy Browser Automation'
    }

## Quitting the browser

The browser object has [`quit()`](api/geb/Browser.html#quit\(\)) and [`close()`](api/geb/Browser.html#close\(\)) methods (that simply delegate to the underlying driver). See the section on [driver management](driver.html) for more information on when and why you need to quit the browser.
