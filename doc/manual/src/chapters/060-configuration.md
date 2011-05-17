# Configuration

Geb provides a configuration mechanism that allows you to control various aspects of Geb in a flexible way.

## The Config Script

Geb's default behaviour is to load a ConfigSlurper script named `geb-conf.groovy` from the *default package* of the **executing thread's context class loader** as the source of the configuration values. In a Grails project, the `test/functional` directory is a good place for this. If you are using a build tool such as Gradle or Maven that has the concept of test “resources”, then that directory is a suitable place.

### Environment Sensitivity

The Groovy [ConfigSlurper][configslurper] mechanism has built in support for environment sensitive configuration, and Geb leverages this by using the `geb.env` system property to determine the environment to load the configuration file with. You can potentially use this mechanism to configure different drivers (i.e. actual browsers) based on the designated “Geb environment”.

How you set the environment system property is going to be dependent on the build system you are using. 

For example, when using Grails you could control the Geb environment by specifying it on the command line…

    grails -Dgeb.env=windows test-app functional:

### Config Options

#### Driver Implementation

The driver to use is specified by the key `driver`. 

It can be a closure that when invoked, with no arguments, returns an instance of [WebDriver][webdriver-api] …

    import org.openqa.selenium.firefox.FirefoxDriver
    
    driver = { new FireFoxDriver() }

Or can be the fully qualified name of a class that implements [WebDriver][webdriver-api] (it will be constructed with no arguments) …

    driver = "org.openqa.selenium.firefox.FirefoxDriver"

Or it can be one of the following short names; `ie`, `htmlunit`, `firefox` or `chrome`. These will be implicitly expanded to their fully qualified class names …

    driver = "firefox"

You can use the environment sensitivity to configure different drivers per environment …

    import org.openqa.selenium.firefox.FirefoxDriver
    
    import org.openqa.selenium.remote.DesiredCapabilities
    import org.openqa.selenium.remote.RemoteWebDriver
    
    // default is to use firefox
    driver = { new FirefoxDriver() }
    
    environments {
        'win-ie' {
            driver = {
                new RemoteWebDriver(new URL("http://windows.ci-server.local"), DesiredCapabilities.internetExplorer())
            }
        }
    }
    
> WebDriver has the ability to drive browsers on a remote host, which is what we are using above. For more information consult the WebDriver documentation on [remote clients][remotewebdriver] and [remote servers][remotewebdriver-server].

#### Driver Caching

Geb's ability to cache a driver and re-use it for the lifetime of the JVM (i.e. [the implicit driver lifecycle](driver.html#implicit_lifecycle)) can be disabled by setting the `cacheDriver` config option to `false`. However, if you do this you become [responsible for quitting](driver.html#explicit_lifecycle) every driver that is created at the appropriate time.

#### Base URL

The [base URL](browser.html#the_base_url) to be used can be specified by setting the `baseUrl` config property (with a `String`) value.
