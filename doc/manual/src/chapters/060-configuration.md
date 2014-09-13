# Configuration

Geb provides a configuration mechanism that allows you to control various aspects of Geb in a flexible way. At the heart of this is the [Configuration][configuration-api] object, which the [Browser][browser-api] and other objects query at runtime.

There are three general mechanisms for influencing configuration; *system properties*, *config script* and the *build adapter*.

## Mechanisms 

### The Config Script

Geb attempts to load a [ConfigSlurper][configslurper] script named `GebConfig.groovy` from the *default package* (in other words, in the root of a directory that is on the classpath). If it is not found, Geb will try to load a [ConfigSlurper][configslurper] class named `GebConfig` from the *default package* - this is usefull if you run tests that use Geb from an IDE because you won't have to specify `GebConfig.groovy` as a resource, Geb will simply fall back to the compiled version of the script. If both script and class are not found Geb will continue using all defaults.

First, the script is looked for with the **executing thread's context class loader** and if it is not found, then it is looked for with the class loader that loaded Geb. This covers 99% of scenarios out of the box perfectly well without any intervention. If however you do need to configure the context class loader to load the config script, you **must** make sure that it is either the same as the class loader that loaded Geb or a child of it. If the script is not found by both of those class loaders the procedure will be repeated but this time the class will be searched for - first using **executing thread's context class loader** and then using the class loader that loaded Geb.

> In a Grails project, the `test/functional` directory is a good place to put your config script in. If you are using a build tool such as [Gradle](http://gradle.org/) or [Maven](http://maven.apache.org/) that has the concept of test “resources”, then that directory is a suitable place. You can also put your script together with your compilation source and then the compiled version of the script will be used.

#### Environment Sensitivity

The Groovy [ConfigSlurper][configslurper] mechanism has built in support for environment sensitive configuration, and Geb leverages this by using the **`geb.env`** system property to determine the environment to use. An effective use of this mechanism is to configure different drivers based on the designated Geb “environment” (concrete details on how to do this further down).

How you set the environment system property is going to be dependent on the build system you are using. For example, when using Grails you could control the Geb environment by specifying it on the command line…

    grails -Dgeb.env=windows test-app functional:

Other build environments will allow you to do this in different ways.

### System Properties

Some config options can be specified by system properties. In general, config options specified by system properties will _override_ values set in the config script. See the config options below for which options are controllable via system properties.

### Build Adapter

The build adapter mechanism exists to allow Geb to integrate with development/build environments that logically dictate config options. For example, Grails dictates what the base URL and directory for reports should be set to and the Geb plugin for Grails uses the build adapter mechanism to set this up.

This mechanism works by loading the name of the class (fully qualified) by the system property `geb.build.adapter` that must implement the [BuildAdapter](api/geb/BuildAdapter.html) interface. Currently, the build adapter can only influence the base URL to use, and the location of the reports directory.

If the `geb.build.adapter` system property is not explicitly set, it defaults to [`SystemPropertiesBuildAdapter`](api/geb/buildadapter/SystemPropertiesBuildAdapter.html). As you can probably deduce, this default implementation uses system properties to specify values, so is usable in most circumstances. See the linked API doc for the details of the specific system properties it looks for.

> Note that while the default build adapter uses system properties, it should not be considered to be the same as system property configuration due to values in the config script taking precedence over the build adapter which is not true for system properties.

## Config Options

### Driver Implementation

The driver to use is specified by the config key `driver`, or the system property `geb.driver`.

#### Factory Closure

In the config script it can be a closure that when invoked with no arguments returns an instance of [WebDriver][webdriver-api] …

    import org.openqa.selenium.firefox.FirefoxDriver
    
    driver = { new FirefoxDriver() }

This is the preferred mechanism, as it allows the most control over the drivers creation and configuration.

You can use the [ConfigSlurper][configslurper] mechanism's environment sensitivity to configure different drivers per environment …

    import org.openqa.selenium.firefox.FirefoxDriver
    
    import org.openqa.selenium.remote.DesiredCapabilities
    import org.openqa.selenium.remote.RemoteWebDriver
    
    // default is to use firefox
    driver = { new FirefoxDriver() }
    
    environments {
        // when system property 'geb.env' is set to 'win-ie' use a remote IE driver
        'win-ie' {
            driver = {
                new RemoteWebDriver(new URL("http://windows.ci-server.local"), DesiredCapabilities.internetExplorer())
            }
        }
    }
    
> WebDriver has the ability to drive browsers on a remote host, which is what we are using above. For more information consult the WebDriver documentation on [remote clients][remotewebdriver] and [remote servers][remotewebdriver-server].

#### Driver Class Name

The name of the driver class to use (it will be constructed with no arguments) can be specified as a string with the key `driver` in the config script or via the `geb.driver` system property (the class must implement the [WebDriver API][webdriver-api]).

    driver = "org.openqa.selenium.firefox.FirefoxDriver"

Or it can be one of the following short names; `ie`, `htmlunit`, `firefox` or `chrome`. These will be implicitly expanded to their fully qualified class names …

    driver = "firefox"

The following table gives the possible short names that can be used:

<table class="graybox" border="0" cellspacing="0" cellpadding="5">
    <tr><th>Short Name</th><th>Driver</th></tr>
    <tr>
        <td><code>htmlunit</code></td>
        <td><a href="http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/htmlunit/HtmlUnitDriver.html">org.openqa.selenium.htmlunit.HtmlUnitDriver</a></td>
    </tr>
    <tr>
        <td><code>firefox</code></td>
        <td><a href="http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/firefox/FirefoxDriver.html">org.openqa.selenium.firefox.FirefoxDriver</a></td>
    </tr>
    <tr>
        <td><code>ie</code></td>
        <td><a href="http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/ie/InternetExplorerDriver.html">org.openqa.selenium.ie.InternetExplorerDriver</a></td>
    </tr>
    <tr>
        <td><code>chrome</code></td>
        <td><a href="http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/chrome/ChromeDriver.html">org.openqa.selenium.chrome.ChromeDriver</a></td>
    </tr>
</table>

If no explicit driver is specified then Geb will look for the following drivers on the classpath in the order they are listed in the above table. If none of these classes can be found, a [`UnableToLoadAnyDriversException`](api/geb/error/UnableToLoadAnyDriversException.html) will be thrown.

### Navigator Factory

It is possible to specify your own implementation of [`NavigatorFactory`](api/geb/navigator/factory/NavigatorFactory.html) via configuration. This is useful if you want to extend the [`Navigator`](api/geb/navigator/Navigator.html) class to provide your own behaviour extensions.

Rather than inject your own `NavigatorFactory`, it is simpler to inject a custom [`InnerNavigatorFactory`](api/geb/navigator/factory/InnerNavigatorFactory.html) which is a much simpler interface. To do this, you can specify a closure for the config key `innerNavigatorFactory`…

    innerNavigatorFactory = { Browser browser, List<org.openqa.selenium.WebElement> elements
        elements ? new MyCustomNavigator(browser, elements) : new geb.navigator.EmptyNavigator()
    }

This is a rather advanced use case. If you need to do this, check out the source code or get in touch via the mailing list if you need help.

### Driver Caching

Geb's ability to cache a driver and re-use it for the lifetime of the JVM (i.e. [the implicit driver lifecycle](driver.html#implicit_lifecycle)) can be disabled by setting the `cacheDriver` config option to `false`. However, if you do this you become [responsible for quitting](driver.html#explicit_lifecycle) every driver that is created at the appropriate time.

The default caching behavior is to cache the driver globally across the JVM. If you are using Geb in multiple threads this may not be what you want, as neither Geb `Browser` objects nor WebDriver at the core is thread safe. To remedy this, you can instruct Geb to cache the driver instance per thread by setting the config option `cacheDriverPerThread` to true.

Also, by default Geb will register a shutdown hook to quit any cached browsers when the JVM exits. You can disable this by setting te config property `quitCachedDriverOnShutdown` to `false`.

### Base URL

The [base URL](browser.html#the_base_url) to be used can be specified by setting the `baseUrl` config property (with a `String`) value or via the build adapter (the default implementation of which looks at the `geb.build.baseUrl` system property). Any value set in the config script will take precedence over the value provided by the build adapter.

### Waiting

The [`waitFor()`](javascript.html#waiting) methods available on browser, page and module objects can be affected by configuration (this is also true for [implicitly waiting content](pages.html#wait)). It is possible to specify default values for the timeout and retry interval, and to define presets of these values to be referred to by name.

#### Defaults

Defaults can be specified via:

    waiting {
        timeout = 10
        retryInterval = 0.5
    }

Both values are optional and in seconds. If unspecified, the values of `5` for `timeout` and `0.1` for `retryInterval`.

#### Presets

Presets can be specified via:

    waiting {
        presets {
            slow {
                timeout = 20
                retryInterval = 1
            }
            quick {
                timeout = 1
            }
        }
    }

Here we have defined two presets, `slow` and `quick`. Notice that the `quick` preset does not specify a `retryInterval` value; defaults will be substituted in for any missing values (i.e. giving the `quick` preset the default `retryInterval` value of `0.1`).

### Waiting in “at” checkers

At checkers can be configured to be implictly wrapped with `waitFor` calls. This can be set with:

	atCheckWaiting = true

The possible values for the `atCheckWaiting` option are consistent with the [ones for `wait` option of content definitions](pages.html#wait).

### Waiting for base navigator

Sometimes Firefox driver times out when trying to find the root HTML element of the page. This manifests itself in an error similar to:

    org.openqa.selenium.NoSuchElementException: Unable to locate element: {"method":"tag name","selector":"html"}
    Command duration or timeout: 576 milliseconds
    For documentation on this error, please visit: http://seleniumhq.org/exceptions/no_such_element.html

You can prevent this error from happening by configuring a wait timeout to use when the driver is locating the root HTML element, using:

    baseNavigatorWaiting = true

The possible values for the `baseNavigatorWaiting` option are consistent with the [ones for `wait` option of content definitions](pages.html#wait).

### Unexpected pages

The `unexpectedPages` option allows to specify a list of unexpected `Page` classes that will be checked for when ”at“ checks are performed. Given that `PageNotFoundPage` and `InternalServerErrorPage` have been defined:

	unexpectedPages = [PageNotFoundPage, InternalServerErrorPage]

See [this section](pages.html#unexpected_pages) for more information.

### Reporter

The *reporter* is the object responsible for snapshotting the state of the browser (see the [reporting](reporting.html) chapter for details). All reporters are implemenations of the [`Reporter`](api/geb/report/Reporter.html) interface. If no reporter is explicitly defined, a [composite reporter](api/geb/report/CompositeReporter.html) will be created from a `ScreenshotReporter` (takes a PNG screenshot) and `PageSourceReporter` (dumps the current DOM state as HTML). This is a sensible default, but should you wish to use a custom reporter you can assign it to the `reporter` config key.

    reporter = new CustomReporter()

### Reports Dir

The reports dir configuration is used by to control where the browser should write reports (see the [reporting](reporting.html) chapter for details).

In the config script, you can set the path to the directory to use for reports via the `reportsDir` key…

    reportsDir = "target/geb-reports"

> The value is interpreted as a path, and if not absolute will be relative to the JVM's working directory.

The reports dir can also be specified by the build adapter (the default implementation of which looks at the `geb.build.reportsDir` system property). Any value set in the config script will take precedence over the value provided by the build adapter.

It is also possible to set the `reportsDir` config item to a file.

    reportsDir = new File("target/geb-reports")

By default this value is **not set**. The browser's [`report()`](api/geb/Browser.html#report\(java.lang.String\)) method requires a value for this config item so if you are using the reporting features you **must** set a reports dir.


### Report Test Failures Only

By default Geb will take a report at the end of each test method, regardless of whether it ended successfully or not. The `reportOnTestFailureOnly` setting allows you to specify that a report should be taken only if a failure occurs. This might be useful as a way to speed up large test suites.

    reportOnTestFailureOnly = true

> Currently this flag is only supported by the TestNG adapter. Support for JUnit, Spock and other frameworks is forthcoming.

### Reporting listener

It is possible to specify a listener that will be notified when reports are taken. See the section on [listening to reporting](reporting.html#listening_to_reporting) for details.

### Auto Clearing Cookies

Certain integrations will automatically clear the driver's cookies, which is usually necessary when using an [implicit driver](driver.html#implicit_lifecycle). This configuration flag, which is `true` by default, can be disabled by setting the `autoClearCookies` value in the config to `false`.

	autoClearCookies = false

## Runtime Overrides

The [Configuration][configuration-api] object also has setters for all of the config properties it exposes, allowing you to override config properties at runtime in particular circumstances if you need to.

For example, you may have one Spock spec that requires the `autoClearCookies` property to be disabled. You could disable it for just this spec by doing something like…

    import geb.spock.GebReportingSpec
    
    class FunctionalSpec extends GebReportingSpec {
        def setup() {
            browser.config.autoClearCookies = false
        }
    }
	
