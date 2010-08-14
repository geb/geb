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

Browser instances always maintain a _page_ (an object of type `geb.Page`) which is retrievable via the read only property `page`. The browser uses Groovy's dynamism to delegate any method calls or property accesses that it can't handle to the current page…

    def browser = new Browser("http://myapp.com")
    browser.go("/signup")
    
    // The following two lines are equivalent
    assert browser.$("h1").text() == "Signup Page"
    assert browser.page.$("h1").text() == "Signup Page"

> for more information on the $ function and other methods seen here, see the section on [navigation][navigator]

Unless otherwise specified, the page is an instance of the `geb.Page` base class which provides the basic navigation functions. The initial page class can be specified at construction time or changed later using the `page(Class<? extends Page>)` method…

    browser.page(SignupPage)

This method creates a new instance of the given class (page classes can only have no-arg constructors) and assigns it to the page property.

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

Browser objects have an `at(Class<? extends Page>)` method that returns `true` or `false` whether or not it is actually at the given type. This includes two checks; whether or not the current page is of *exactly* the given type and whether or not the current page content is what the current page type expects it to be.

This is typically used in conjuction with the `assert` keyword.

    def browser = new Browser("http://myapp.com")
    browser.to(SignupPage)
    assert browser.at(SignupPage)

> see the section on [page at-verification][page-at] for more information

## The drive() method

The Browser class features a static method that makes Geb scripting a little more convenient.

Here is an example:

    Browser.drive("http://myapp.com") {
        go "/signup"
        $("h1").text() == "Signup Page"
    }

The static `drive()` method takes all of the arguments that the `Browser` constructor takes, and a `Closure`. The closure is evaluated against created browser instance (i.e. it is the *delegate* of the closure). This enables a very convenient scripting environment.