# The Browser

The entry point to Geb is the `Browser` class. A browser object drives an underlying `WebDriver` instance which drives the real or simulated browser. 

Here are some examples of `browser` construction…

    import geb.Browser
    import org.openqa.selenium.firefox.FirefoxDriver
    
    new Browser(new FirefoxDriver(), "http://google.com")
    new Browser(new FirefoxDriver())
    new Browser("http://google.com")
    new Browser()

The `Browser` class also maintains a `Page` instance that represents the current page that the browser is at. The browser instance delegates any method calls or property assignments or accesses that it can't handle to the page instance, which significantly cuts down on boilerplate code.

If a `WebClient` instance is not provided at construction, the browser will attempt to create an instance of `org.openqa.selenium.htmlunit.HtmlUnitDriver`.

If a _base url_ is not provided, all page urls must be absolute.