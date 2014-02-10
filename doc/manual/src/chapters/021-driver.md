# The WebDriver Implementation

A `geb.Browser` instance interacts with an actual browser via an instance of [WebDriver][webdriver-api]. The browser's driver can always be retrieved via the `WebDriver getDriver()` method.

> One of the key design principles that WebDriver embraces is that tests/scripts should be written to the [WebDriver API][webdriver-api] making them agnostic to the actual browser being driven, and therefore portable. Geb always supports this goal. However, the reality is that there are still quirks and behavioural differences between driver implementations. Each release of WebDriver has historically worked to minimise these issues so expect the situation to improve over time as WebDriver matures.

## Explicit Driver Management

One option for specifying the driver implementation is to construct the driver instance and pass it to the `geb.Browser` to be used [when it is constructed](browser.html).

However, where possible prefer implicit driver management which is discussed later in this chapter.

### Explicit Lifecycle

When the driver is constructed by the user, the user is responsible for quitting the driver at the appropriate time. This can be done via the methods on the webdriver instance (obtainable via `geb.Browser#getDriver()`) or by calling the [delegating methods on the browser object](browser.html#quitting_the_browser).

## Implicit Driver Management

If a driver is not given when a `Browser` object is constructed, one will be created and managed implicitly by Geb by the the [configuration mechanism](configuration.html#driver_implementation).

### Implicit Lifecycle

By default, Geb internally caches and reuses the first driver created, meaning that all subsequent browser instances created without an explicit driver will reuse the cached driver. This avoids the overhead of creating a new driver each time, which can be significant when working with a real browser.

This means that you may need to call the `clearCookies()` method on the browser in order not to get strange results due to cookies from previous executions.

> Note that some of the integrations (e.g. Spock, JUnit) automatically clear the browser cookies at appropriate times such as after each test. Consult the section on [testing](testing.html#cookie_management) for specifics.

The shared driver will be closed and quitted when the JVM shuts down.

A new driver can be forced at anytime by calling either of the following `static` methods on the `CachingDriverFactory` class…

    import geb.driver.CachingDriverFactory
    
    def cachedDriver = CachingDriverFactory.clearCache()
    def cachedDriver = CachingDriverFactory.clearCacheAndQuitDriver()

After calling either of this methods, the next request for a default driver will result in a new driver instance being created.

This caching behavior is [configurable](configuration.html#driver_caching). 

## Driver Quirks

This section details various quirks or issues that have been encountered with different driver implementations.

### HTMLUnitDriver

#### Dealing with pages that use HTML refreshes

The default behaviour of the HTMLUnit driver is to immediately refresh the page as soon as it encounters a `<meta http-equiv="refresh" content="5">` regardless of the specified time. The solution is to use a refresh handler that handles the refresh asynchronously.

    import com.gargoylesoftware.htmlunit.ThreadedRefreshHandler

    Browser.drive {
      driver.webClient.refreshHandler = new ThreadedRefreshHandler()
      …
    }

See [this mailing list thread](http://markmail.org/thread/bu3g56oxz2uqzq43 "[geb-user] Meta Refresh on a page causes infinite loop - Bob Brown - org.codehaus.geb.user - MarkMail") for details.

#### Configuring logging

HTMLUnit can be very noisy, and it's not clear how to make it not so noisy.

See [this issue](http://jira.codehaus.org/browse/GEB-98) for some tips on how to tune its logging.
