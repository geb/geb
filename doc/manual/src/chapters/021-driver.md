# The WebDriver Implementation

A `geb.Browser` instance interacts with an actual browser via an instance of [WebDriver][webdriver-api]. The browser's driver can always be retrieved via the `WebDriver getDriver()` method.

> One of the key design principles that WebDriver embraces is that tests/scripts should be written to the [WebDriver API][webdriver-api] making them agnostic to the actual browser being driven, and therefore portable. Geb always supports this goal. However, the reality is that there are still quirks and behavioural differences between driver implementations. Each release of WebDriver has historically worked to minimise these issues so expect the situation to improve over time as WebDriver matures.

## Explicit Driver Management

One option for specifying the driver implementation is to construct the driver instance and pass it to the `geb.Browser` to be used [when it is constructed](browser.html).

However, where possible prefer implicit driver management which is discussed later in this chapter.

> Note that some of the integrations (e.g. Spock) provide hook methods like `WebDriver createDriver()` that you can override in specs to return a constructed driver implementation. Consult the section on [integrations](integrations.html) for specific info.

### Lifecycle

When the driver is constructed by the user, the user is responsible for quitting the driver at the appropriate time. This can be done via the methods on the webdriver instance (obtainable via `geb.Browser#getDriver()`) or by calling the [delegating methods on the browser object](browser.html#quitting_the_browser).

## Implicit Driver Management

If a driver is not given when a `Browser` object is constructed, one will be created and managed implicitly by Geb by one of the following mechanisms.

### Specifying the driver

#### Config

The preferred way to control the driver is via the [configuration](configuration.html#driver_implementation) mechanism. This mechanism is preferred as it gives you full control over how the driver instance is created while still leveraging Geb's automatic driver lifecycle management (which is just a fancy way of saying “sharing one driver instance across tests/scripts”).

#### System Property

In versions of Geb prior to 0.6, the preferred way to specify the driver to use while leveraging implicit driver management was to specify the driver class to use via the `geb.driver` system property. This mechanism is still available and **will override** the driver specified by the config mechanism.

The value of the system property can be either the full class name of a [WebDriver][webdriver-api] implementation, or one of the following short names:

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

#### Default

If no explicit driver was specified at `Browser` construction and neither the geb config nor `geb.driver` system property are set, then Geb will look for the following drivers on the classpath in the given order:

* `org.openqa.selenium.htmlunit.HtmlUnitDriver`
* `org.openqa.selenium.firefox.FirefoxDriver`
* `org.openqa.selenium.ie.InternetExplorerDriver`
* `org.openqa.selenium.chrome.ChromeDriver`

If none of these classes can be found, a `geb.error.UnableToLoadAnyDriversException` will be thrown.

### Implicit Lifecycle

Geb internally caches and reuses the first implicit driver created per thread, meaning that all subsequent browser instances created without an explicit driver will reuse the cached driver. This avoids the overhead of creating a new driver each time which can be significant when working with a real browser.

This means that you may need to call the `clearCookies()` method on the browser in order to not get strange results due to cookies from previous executions.

The shared driver will be closed and quitted when the JVM shuts down.

A new driver can be forced at anytime by calling either of the following `static` methods on the `CachingDriverFactory` class…

    import geb.driver.CachingDriverFactory
    
    def cachedDriver = CachingDriverFactory.clearCache()
    def cachedDriver = CachingDriverFactory.clearCacheAndQuitDriver()

After calling either of this methods, the next request for a default driver will result in a new driver instance being created.
