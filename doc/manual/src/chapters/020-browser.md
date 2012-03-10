# The Browser

The entry point to Geb is the [`Browser`][browser-api] object. 
A browser object marries a [`WebDriver`][webdriver-api] instance (which drives the actual web browser being automated) with the concept of a “current page”.

Browser objects are created with a [configuration][configuration-api] that specifies which driver implemenation to use, the base url to resolve relative links against and other bits of config. The configuration mechansism allows you to externalise how Geb should operate, which means you can use the same suite of Geb code or tests with different browsers or site instances. The [chapter on configuration](configuration.html) contains more details on how to manage the configuration parameters and what they are.

The default constructor of [`Browser`][browser-api] simply loads its settings from the config mechanism.

    import geb.Browser
    
    def browser = new Browser()

However, if you prefer to specify the driver implementation (or any other settable property on the [browser][browser-api]) you can by using Groovy's map constructor syntax.

    import geb.Browser
    import org.openqa.selenium.firefox.FirefoxDriver
    
    def browser = new Browser(driver: new FirefoxDriver())

Which is that same as…

    def browser = new Browser()
    browser.driver = new FirefoxDriver()

Any property set this way will **override** any settings coming from the config mechanism.

> Note: The behaviour is undefined if a browser's driver is changed after its first use so you should avoid setting the driver this way and prefer the configuration mechanism.

For drastically custom configuration requirements, you can create your own [configuration][configuration-api] object and construct the browser with it, likely using the [configuration loader](api/geb-core/geb/ConfigurationLoader.html).

    import geb.Browser
    import geb.Configuration
    import geb.ConfigurationLoader
    
    def loader = new ConfigurationLoader("a-custom-environment")
    def config = loader.conf
    def browser = new Browser(config)

Wherever possible, you should strive to use the no-arg constructor and manage Geb through the inbuilt [configuration mechanism](configuration.html) as it offers a great deal of flexibility and separates your configuration from your code.

> Geb integrations typically remove the need to construct a browser object and do this for you, leaving you to just manage the configuration.

## The drive() method

The Browser class features a static method, [`drive()`](api/geb-core/geb/Browser.html#drive(groovy.lang.Closure\)), that makes Geb scripting a little more convenient.

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

Browser instances maintain a [`baseUrl`](api/geb-core/geb/Browser.html#getBaseUrl(\)) property that is used to resolve all non absolute URLs. 
This value can come from [configuration](configuration.html#base_url) or can be 
[explicitly set](api/geb-core/geb/Browser.html#setBaseUrl(java.lang.String\)) on the browser.

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

Page objects (discussed further shortly) can define a url that will be used when explicitly navigating to that page. This is done with the [`to()`](api/geb-core/geb/Browser.html#to(java.lang.Class, Object[]\)) methods.

    class SignupPage extends Page {
        static url = "signup"
    }
    
    Browser.drive {
        to SignupPage
        assert $("h1").text() == "Signup Page"
        assert page instanceof SignupPage
    }

The `to()` method makes a request to the resolved URL and sets the browser's page instance to an instance of the given class. Most Geb scripts and tests start with a `to()` call.

> See the section on [Advanced Page Navigation][page-navigation] for more information on how to use more complicated URL resolution for pages.

### Direct

You can also make a new request to a URL without setting or changing the page using the [`go()`](api/geb-core/geb/Browser.html#go(\)) methods. The following examples use a baseUrl of “`http://myapp.com/`”.

    Browser.drive {
        // Go to the Base URL
        go()

        // Go to a URL relative to Base URL
        go "signup"

        // Go to a URL with request params, i.e http://myapp.com/signup?param1=value1&param2=value2
        go "signup", param1: "value1", param2: "value2"
    }

## The Page

Browser instances hold a reference to a _page_. This page instance is retrievable via the [`page`](api/geb-core/geb/Browser.html#getPage(\)) property. Initially, all browser instances have a page of type [`Page`](api/geb-core/geb/Page.html) which provides the basic navigation functions and is the super class for all page objects. 

However, the page property is rarely accessed directly. The browser object will *forward* any method calls or property read/writes that it can't handle to the current page instance. 

    Browser.drive {
        go "signup"
        
        // The following two lines are equivalent
        assert $("h1").text() == "Signup Page"
        assert page.$("h1").text() == "Signup Page"
    }

The *page* is providing the $() function, not the browser. This forwarding facilitates very concise code, void of unnecessary noise.

> for more information on the $() function which is used to interact with page content, see the section on the [Navigator API][navigator].

When using the Page Object pattern, you create subclasses of [`Page`](api/geb-core/geb/Page.html) that define content via a powerful DSL that allows you to refer to content by meaningful names instead of tag names or CSS expressions.

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

The [`page(Class pageType)`](api/geb-core/geb/Browser.html#page(java.lang.Class\)) method allows you to change the page to a new instance of the given class. The class must be [Page](api/geb-core/geb/Page.html) or a subclass thereof. This method **does not** verify that the given page actually matches the content (at checking is discussed shortly).

The [`page(Class[] potentialPageTypes)`](api/geb-core/geb/Browser.html#page(java.lang.Class[]\)) method allows you to specify a number of *potential* page types. Each of the potential pages is instantiated and checked to see if it matches the content the browser is actually currently at by running each pages at checker.

The [`page(Page pageInstance)`](api/geb-core/geb/Browser.html#page(geb.Page\)) method allows you to change the page to an already created \(most probably with parameters\) page instance. It is useful when you have a lot of just slightly different pages, you don't want to create a subclass for every one of them and you can express the difference in those pages by passing different parameters to the constructor of your [`Page`](api/geb-core/geb/Page.html) subclass.

These methods are not typically used explicitly but are used by the `to()` method and content definitions that specify the page that the content navigates to when clicked (see the section on the [`to` attribute of the Content DSL](pages.html#to) for more information about this). However, should you need to manually change the page type they are there.

## At checking

Browser objects have an [`at(Class pageType)`](api/geb-core/geb/Browser.html#at(java.lang.Class\)) method that tests whether or not the browser is currently at the type of page modeled by the given page object type. There is also an [`at(Page pageInstance)`](api/geb-core/geb/Browser.html#at(geb.Page\)) version of the `at()` method available if you want to use page instances.

Pages define an [“at checker”][page-at] that the browser uses for this test.

    class SignupPage extends Page {
        static at = {
            $("h1").text() == "Signup Page"
        }
    }
    
    Browser.drive {
        to SignupPage
        at SignupPage
    }

The `at SignupPage` method call will either return true or throw an `AssertionError` even if there are no explicit assertions in the “at” checker if the checker doesn't pass.

> Not using explicit `return` statements in “at” checkers is preffered. Geb transforms all “at” checkers so that each statement in them is asserted (just like for `then:` blocks in Spock specifications). Thanks to that you can immediately see evaluated values of your “at” checker if it fails. See the [“at checker”][page-at] section for more details.

It's a good idea to use an at check whenever the page changes in order to *fail fast*. Otherwise, subsequent steps may fail in harder to diagnose ways due to the content not matching what is expected and content lookups having strange results.

The `to()` method that takes a single page type **does not** verify that the the browser ends up at the given type. This is because the request may initiate a redirect and take the browser to a different page. For example…

    Browser.drive {
        to SecurePage
        at AccessDeniedPage
    }

It's very common to see an at check directly after a `to()` call.

Pages can also define content that declares what the browser's page type should change to when that content is clicked. It's advised to use an at check after clicking on such content (see the DSL reference for the [`to`](pages.html#to) parameter).

    class LoginPage extends Page {
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
        at LoginPage
        loginButton.click()
        at AdminPage
    }

The `at()` method will also update the browser's page instance to the given page type (or the given page instance, depending on which version of the method is used) if its at checker is successful.
 
## Page change listening

It is possible to be notified when a browser's page _instance_ changes (note that this is not necessarily when the browser makes a request to a new URL) using the [`PageChangeListener`](api/geb-core/geb/PageChangeListener.html) interface.

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

The [`removePageChangeListener(PageChangeListener listener)`](api/geb-core/geb/Browser.html#removePageChangeListener(geb.PageChangeListener\)) returns `true` if `listener` was registered and has now been removed, otherwise it returns `false`.

Listeners cannot be registered twice. If an attempt is made to register a listener that is already registered (i.e. there is another listener that is _equal_ to the listener trying to register, based on their `equals()` implementation) then a [`PageChangeListenerAlreadyRegisteredException`](api/geb-core/geb/error/PageChangeListenerAlreadyRegisteredException.html) will be raised.

## Working with multiple tabs and windows

When you're working with an application that opens new windows or tabs, for example when clicking on a link with a target attribute set, you can use `withWindow()` and `withNewWindow()` methods to execute code in the context of other windows.

If you know the name of the window in which context you want to execute the code you can use [`withWindow(String windowName, Closure block)`](api/geb-core/geb/Browser.html#withWindow(java.lang.String, groovy.lang.Closure\)). Given this html:

    <a href="http://www.gebish.org" target="myWindow">Geb</a>

This code passes:

    $('a').click()
    withWindow('myWindow') {
        assert $('title').text() == 'Geb - Very Groovy Browser Automation'
    }

If you don't know the name of the window but you know something about the content of the window you can use the [`withWindow(Closure specification, Closure block)`](api/geb-core/geb/Browser.html#withWindow(groovy.lang.Closure, groovy.lang.Closure\)) method. The first closure passed should return true for the window, or windows, you want to use as context. Note that if there is no window for which the window specification closure returns true then [`NoSuchWindowException`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/NoSuchWindowException.html) is thrown. So given:

    <a href="http://www.gebish.org" target="_blank">Geb</a>

This code passes:

    $('a').click()
    withWindow({ $('title').text() == 'Geb - Very Groovy Browser Automation' }) {
        assert $('#slogan').text() == 'very groovy browser automation… web testing, screen scraping and more'
    }

Finally if you want to execute code in a window that is newly opened by some of your actions use the [`withNewWindow(Closure windowOpeningBlock, Closure block)`](api/geb-core/geb/Browser.html#withNewWindow(groovy.lang.Closure, groovy.lang.Closure\)) method. Given html as above the following will pass:

    withNewWindow({ $('a').click() }) {
        assert $('title').text() == 'Geb - Very Groovy Browser Automation'
    }

Note that if the first parameter opens none or more than one window then [`NoSuchWindowException`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/NoSuchWindowException.html) is thrown.

If you really need to know the name of the current window or all the names of open windows use [`getCurrentWindow()`](api/geb-core/geb/Browser.html#getCurrentWindow(\)) and [`getAvailableWindows()`](api/geb-core/geb/Browser.html#getAvailableWindows(\)) methods but `withWindow()` and `withNewWindow()` are the preferred methods when it comes to dealing with multiple windows.

## Quitting the browser

The browser object has [`quit()`](api/geb-core/geb/Browser.html#quit(\)) 
and [`close()`](api/geb-core/geb/Browser.html#close(\)) methods (that simply delegate to the underlying driver). See the section on [driver management](driver.html) for more information on when and why you need to quit the browser.
