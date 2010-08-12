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

The browser instance drives the actual browser via an instance of [WebDriver][webdriver-api], which is given at construction time. The browser's driver can always be retrieved via the `getDriver()` method.

### The Default Driver

If no driver is given at construction, an attempt will be made to create an instance of [HtmlUnitDriver][htmlunitdriver] by loading the class by name. If the HtmlUnitDriver class cannot be found, a `geb.driver.HtmlUnitUnavailableException` will be thrown.

To override the default driver, you can subclass and override one of either two methods…

    protected WebDriver getDefaultDriver()
    
    protected DriverFactory getDefaultDriverFactory()

The `getDefaultDriver()` method simply returns an instance of [`WebDriver`][webdriver-api] to use.

An example implementation:

    protected WebDriver getDefaultDriver() {
        new FirefoxDriver()
    }

An alternative is to override the `getDefaultDriverFactory()` method which returns an instance of `geb.driver.DriverFactory`…

    interface DriverFactory {
        WebDriver getDriver()
    }

The default implementation of the `getDefaultDriver()` method calls `getDefaultDriverFactory()` and calls `getDriver()` on it's return value.

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

See [making requests][#making_requests] for more information on the to() method.

## The Page

Browser instances always maintain a _page_ (an object of type `geb.Page`) which is retrievable via the read only property `page`. The browser uses Groovy's dynamism to delegate any method calls or property accesses that it can't handle to the current page…

    def browser = new Browser("http://myapp.com")
    browser.go("/signup")
    
    // The following two lines are equivalent
    assert browser.$("h1").text() == "Signup Page"
    assert browser.page.$("h1").text() == "Signup Page"

> for more information on the $ function and other methods seen here, see the section on [navigation][navigation]

Unless otherwise specified, the page is an instance of the `geb.Page` base class which provides the basic navigation functions. The initial page class can be specified at construction time or changed later using the `page(Class<? extends Page>)` method…

    browser.page(SignupPage)

This method creates a new instance of the given class (page classes can only have no-arg constructors) and assigns it to the page property.

### Navigating to pages

The `page(Class<? extends Page>)` method only sets the current page instance to be of a new type. To do this and make a request to the url that the page specifies, you use the `to(Class<? extends Page>)` method(s).

    class SignupPage extends Page {
        static url = "/signup"
    }
    
    def browser = new Browser("http://myapp.com")
    browser.to(SignupPage)
    assert browser.$("h1").text() == "Signup Page"
    assert browser.page instanceof SignupPage

> See the section on [Advanced Page Navigation][page-navigation] for more information on this topic.