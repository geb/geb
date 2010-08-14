# Integrations

Geb provides support for using it via different frameworks and testing mechanisms. All of the integrations are versioned identically to the version of Geb they use.

## JUnit 3

The JUnit 3 support is in the `junit3` jar.

### geb.junit3.GebTest

The `GebTest` class extends `GroovyTestCase` and provides a `Browser` instance to work with. In a `GebTest`, method calls and property accesses/assignments are automatically forwarded to the browser instance, just like in the [`drive()` method][drive].

    import geb.junit3.GebTest
    
    class MyTests extends GebTest {
        
        void testSomething() {
            to "/help"
            assert $("h1").text() == "Help"
        }
    
    }

#### Configuring the Base URL

Subclasses can override the `getBaseUrl()` method to have the browser configured with a certain base.

    import geb.junit3.GebTest
    
    class MyTests extends GebTest {
        
        String getBaseUrl() {
            "http://myapp.com"
        }
        
    }

#### Configuring the Driver

The [default driver][defaultdriver] is used by default. This can be overridden by overriding the `createDriver()` method like so…

    import geb.junit3.GebTest
    import org.openqa.selenium.WebDriver
    import org.openqa.selenium.firefox.FirefoxDriver
    
    class FirefoxTests extends GebTest {
        
        WebDriver createDriver() {
            new FirefoxDriver()
        }
        
    }

#### Configuring the Browser

Subclasses can modify the browser creation behaviour by overriding the `createBrowser()` method…

    import geb.junit3.GebTest
    import geb.Browser

    class MyTests extends GebTest {

        Browser createBrowser() {
            new Browser()
        }

    }

The default implementation creates a browser instance using the return value of `createDriver()` (which is `null` by default meaning the default driver is used) and `getBaseUrl()`.

### geb.junit3.GebReportingTest

The `GebReportingTest` class extends from `GebTest` (so has the same customisation options and behaviour) but writes out the page source at the end of each test method to a file on disk. To enable this, you **must** override the `getReportDir()` method…

    import geb.junit3.GebReportingTest
    
    class MyTests extends GebReportingTest {
        File getReportDir() {
            new File("reports")
        }
    }

At the end of each test method a file will be written called «n».html, where «n» is the number of the test execution (e.g first test to run gets “1”, second gets “2” etc), in a directory inside the given report directory that matches the full class name. Given the class above, the reports would be written to `reports/MyTests`. If the class was in the package `my.app`, the reports would be written in the `reports/my/app/MyTests` directory.

> JUnit 3 does not provide a mechanism for accessing the name of the executing test method, hence the number only report file naming. This is not the case for JUnit 4.

## JUnit 4

The JUnit 4 support is in the `junit4` jar.

### geb.junit.GebTest

The `GebTest` class extends `GroovyTestCase` and provides a `Browser` instance to work with. In a `GebTest`, method calls and property accesses/assignments are automatically forwarded to the browser instance, just like in the [`drive()` method][drive].

    import geb.junit.GebTest
    
    class MyTests extends GebTest {
        
        void testSomething() {
            to "/help"
            assert $("h1").text() == "Help"
        }
    
    }

#### Configuring the Base URL

Subclasses can override the `getBaseUrl()` method to have the browser configured with a certain base.

    import geb.junit.GebTest
    
    class MyTests extends GebTest {
        
        String getBaseUrl() {
            "http://myapp.com"
        }
        
    }

#### Configuring the Driver

The [default driver][defaultdriver] is used by default. This can be overridden by overriding the `createDriver()` method like so…

    import geb.junit.GebTest
    import org.openqa.selenium.WebDriver
    import org.openqa.selenium.firefox.FirefoxDriver
    
    class FirefoxTests extends GebTest {
        
        WebDriver createDriver() {
            new FirefoxDriver()
        }
        
    }

#### Configuring the Browser

Subclasses can modify the browser creation behaviour by overriding the `createBrowser()` method…

    import geb.junit.GebTest
    import geb.Browser

    class MyTests extends GebTest {

        Browser createBrowser() {
            new Browser()
        }

    }

The default implementation creates a browser instance using the return value of `createDriver()` (which is `null` by default meaning the default driver is used) and `getBaseUrl()`.

### geb.junit.GebReportingTest

The `GebReportingTest` class extends from `GebTest` (so has the same customisation options and behaviour) but writes out the page source at the end of each test method to a file on disk. To enable this, you **must** override the `getReportDir()` method…

    import geb.junit.GebReportingTest
    
    class MyTests extends GebReportingTest {
        File getReportDir() {
            new File("reports")
        }
    }

At the end of each test method a file will be written called «n»-«test method name».html, where «n» is the number of the test execution (e.g first test to run gets “1”, second gets “2” etc) and «test method name» is the name of the test method, in a directory inside the given report directory that matches the full class name. Given the class above, the reports would be written to `reports/MyTests`. If the class was in the package `my.app`, the reports would be written in the `reports/my/app/MyTests` directory.

## Spock

The [Spock][spock] support is in the `spock` jar.

### geb.spock.GebSpec

The `GebSpec` class extends `spock.lang.Specification` and provides a `Browser` instance to work with. In a `GebSpec`, method calls and property accesses/assignments are automatically forwarded to the browser instance, just like in the [`drive()` method][drive].

    import geb.spock.GebSpec
    
    class MySpec extends GebSpec {
        
        def "test something"() {
            when:
            to "/help"
            then:
            $("h1").text() == "Help"
        }
    
    }

#### Configuring the Base URL

Subclasses can override the `getBaseUrl()` method to have the browser configured with a certain base.

    import geb.spock.GebSpec
    
    class MySpec extends GebSpec {
        
        String getBaseUrl() {
            "http://myapp.com"
        }
        
    }

#### Configuring the Driver

The [default driver][defaultdriver] is used by default. This can be overridden by overriding the `createDriver()` method like so…

    import geb.spock.GebSpec
    import org.openqa.selenium.WebDriver
    import org.openqa.selenium.firefox.FirefoxDriver
    
    class FirefoxSpec extends GebSpec {
        
        WebDriver createDriver() {
            new FirefoxDriver()
        }
        
    }

#### Configuring the Browser

Subclasses can modify the browser creation behaviour by overriding the `createBrowser()` method…

    import geb.spock.GebSpec
    import geb.Browser

    class MySpec extends GebSpec {

        Browser createBrowser() {
            new Browser()
        }

    }

The default implementation creates a browser instance using the return value of `createDriver()` (which is `null` by default meaning the default driver is used) and `getBaseUrl()`.

### geb.spock.GebReportingSpec

The `GebReportingSpec` class extends from `GebSpec` (so has the same customisation options and behaviour) but writes out the page source at the end of each test method to a file on disk. To enable this, you **must** override the `getReportDir()` method…

    import geb.spock.GebReportingSpec
    
    class MySpec extends GebReportingSpec {
        File getReportDir() {
            new File("reports")
        }
    }

At the end of each test method a file will be written called «n»-«test method name».html, where «n» is the number of the test execution (e.g first test to run gets “1”, second gets “2” etc) and «test method name» is the name of the test method, in a directory inside the given report directory that matches the full class name. Given the class above, the reports would be written to `reports/MyTests`. If the class was in the package `my.app`, the reports would be written in the `reports/my/app/MyTests` directory.

## Grails

Grails support is provided by the `grails-geb` plugin, and provides support for both Spock and JUnit (3 and 4) tests. Working with Spock requires installing the spock plugin separately.

### Writing Tests

Geb tests go into the `test/functional` directory. The tests are configured to have a base url of the root of the Grails application under test. This can be overridden by either overriding the `getBaseUrl()` method in your test/spec on a per test basis, or globally by specifying the `baseUrl` command line option…

    grails test-app -baseUrl=http://myapp.com

#### JUnit

If you are using Grails 1.2 and want to write JUnit tests, you subclass the `grails.plugin.geb.JUnit3GebTests` class. If you are using Grails 1.3 or later, you subclass `grails.plugin.geb.GebTests`.

#### Spock

To write Spock specs, you subclass `grails.plugin.geb.GebSpec`.

### Reports

Reports are written to `${testReportsDir}/geb` (which is `target/test-reports/geb` by default).

### Drivers

The plugin does not bundle any WebDriver drivers. You can include one in your application using Grails' [dependency management features](http://grails.org/doc/latest/guide/3.%20Configuration.html#3.7%20Dependency%20Resolution). For example, you can use the firefox by placing the following in the `BuildConfig.groovy` file…

    test("org.seleniumhq.selenium:selenium-firefox-driver:latest.release")

Unless otherwise specified, the [default driver][defaultdriver] will be used when testing. This will be the first valid driver that can be found. If you want to switch between multiple drivers, you can use the system property to do so…

    grails -Dgeb.driver=ie test-app

#### A note on the HtmlUnitDriver

HTMLUnit depends on some XML processing libraries that cause issues with Grails. You can avoid this by excluding certain dependencies of the HTMLUnit driver…

    test("org.seleniumhq.selenium:selenium-htmlunit-driver:latest.release") {
        exclude 'xml-apis'
    }
