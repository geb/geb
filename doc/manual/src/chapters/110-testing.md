# Testing

Geb provides first class support for functional web testing via integration with popular testing frameworks such as [Spock][spock], [JUnit][junit], [TestNG][testng], [EasyB][easyb] and [Cuke4Duke][cuke4duke].

## Spock, JUnit & TestNG

The Spock, JUnit and TestNG integrations work fundamentally the same way. They provide subclasses that setup a [browser][browser-api] instance that all method calls and property accesses/references resolve against via Groovy's `methodMissing` and `propertyMissing` mechanism.

> Recall that the browser instance also forwards any method calls or property accesses/references that it can't handle to it's current page object, which helps to remove a lot of noise from the test.

Consider the following Spock spec…

    import geb.spock.GebSpec
    
    class FunctionalSpec extends GebSpec {
        def "go to login"() {
            when:
            go "/login"
            
            then:
            title == "Login Screen"
        }
    }

Which is equivalent to…

    import geb.spock.GebSpec
    
    class FunctionalSpec extends GebSpec {
        def "go to login"() {
            when:
            browser.go "/login"
            
            then:
            browser.page.title == "Login Screen"
        }
    }

### Configuration

The browser instance is created by the testing integration. The [configuration mechanism](configuration.html) allows you to control aspects such as the driver implementation and base url.

### Reporting

The Spock, JUnit and TestNG integrations also ship a superclass (the name of the class for each integration module is provided below) that automatically takes reports at the end of test methods with the label “end”. They also set the [report group](reporting.html#the_report_group) to the name of the test class (substituting “.” for “/”).

The [`report(String label)`](api/geb-core/geb/Browser.html#report(java.lang.String\)) browser method is replaced with a specialised version. This method works the same as the browser method, but adds counters and the current test method name as prefixes to the given label.

    package my.tests
    
    import geb.spock.GebReportingSpec
    
    class FunctionalSpec extends GebReporting {
        
        def "login"() {
            when:
            go "login"
            username = "me"
            report "login screen" // take a report of the login screen
            login().click()
            
            then:
            title == "Logged in!"
        }
    }

Assuming a configured `reportsDir` of `reports/geb` and the default reporter (i.e. [`ScreenshotAndPageSourceReporter`](api/geb-core/geb/report/ScreenshotAndPageSourceReporter.html)), we would find the following files:

* `reports/geb/my/tests/FunctionalSpec/1-1-login-login screen.html`
* `reports/geb/my/tests/FunctionalSpec/1-1-login-login screen.png`
* `reports/geb/my/tests/FunctionalSpec/1-2-login-end.html`
* `reports/geb/my/tests/FunctionalSpec/1-2-login-end.png`

The report file name format is:

    «test method number»-«report number in test method»-«test method name»-«label».«extension»

Reporting is an extremely useful feature and can help you diagnose test failures much easier. Wherever possible, favour the use of the auto reporting base classes.

### Cookie management

The Spock, JUnit and TestNG integrations will automatically clear the browser's cookies at the end of each test method. For JUnit 3 this happens in the `tearDown()` method in `geb.junit3.GebTest`, for JUnit 4 it happens in an `@After` method in `geb.junit4.GebTest` and for TestNG it happens in an `@AfterMethod` method in `geb.testng.GebTest`.

The `geb.spock.GebSpec` class will clear the cookies in the `cleanup()` method unless the spec is `@Stepwise`, in which case they are cleared in `cleanupSpec()` (meaning that all feature methods in a stepwise spec share the same browser state).

This auto clearing of cookies can be [disabled via configuration](configuration.html#auto_clearing_cookies).

### Jar and class names

The following table illustrates the specific jars and class names for Spock and JUnit.

<table class="graybox" border="0" cellspacing="0" cellpadding="5">
    <tr>
        <th>Framework</th>
        <th>JAR</th>
        <th>Base Class</th>
        <th>Reporting Base Class</th>
    </tr>
    <tr>
        <td>Spock</td>
        <td><a href="http://mvnrepository.com/artifact/org.codehaus.geb/geb-spock">geb-spock</a></td>
        <td><a href="api/geb-spock/geb/spock/GebSpec.html">geb.spock.GebSpec</a></td>
        <td><a href="api/geb-spock/geb/spock/GebReportingSpec.html">geb.spock.GebReportingSpec</a></td>
    </tr>
    <tr>
        <td>JUnit 4</td>
        <td><a href="http://mvnrepository.com/artifact/org.codehaus.geb/geb-junit4">geb-junit4</a></td>
        <td><a href="api/geb-junit4/geb/junit4/GebTest.html">geb.junit4.GebTest</a></td>
        <td><a href="api/geb-junit4/geb/junit4/GebReportingTest.html">geb.junit4.GebReportingTest</a></td>
    </tr>
    <tr>
        <td>JUnit 3</td>
        <td><a href="http://mvnrepository.com/artifact/org.codehaus.geb/geb-junit3">geb-junit3</a></td>
        <td><a href="api/geb-junit3/geb/junit3/GebTest.html">geb.junit3.GebTest</a></td>
        <td><a href="api/geb-junit3/geb/junit3/GebReportingTest.html">geb.junit3.GebReportingTest</a></td>
    </tr>
    <tr>
        <td>TestNG</td>
        <td><a href="http://mvnrepository.com/artifact/org.codehaus.geb/geb-testng">geb-testng</a></td>
        <td><a href="api/geb-testng/geb/testng/GebTest.html">geb.testng.GebTest</a></td>
        <td><a href="api/geb-testng/geb/testng/GebReportingTest.html">geb.testng.GebReportingTest</a></td>
    </tr>
</table>

### Example Projects

The following projects can be used as starting references:

* [https://github.com/geb/geb-example-gradle](https://github.com/geb/geb-example-gradle)

## EasyB

Geb's [EasyB][easyb] support is based around the [binding management features](binding.html) discussed earlier. 
EasyB stories are implemented as Groovy scripts with a binding, 
and the Geb plugin simply integrates Geb's [BindingUpdater][bindingupdater-api] into the EasyB lifecycle.

> The Geb EasyB plugin is currently under-developed and is in need of some attention. If you'd like to see better EasyB integration consider getting involved in its development.

Here's a quick example…

    using "geb" // EasyB syntax for using plugins
    
    scenario "using geb", {
        given "our base url", {
            baseUrl = "http://my.app"
        }

        when "we go to the page", {
            to SomePage
        }

        then "we arrive at the page", {
            at SomePage
        }

        and "can use the javascript interface", {
            js.someJsVariable.shouldBe 1
        }

        and "can do some waiting", {
            waitFor { $("p").text() == "done" }
        }
        
        and "can work with the page", {
            page.div.text().shouldBe "d1"
        }
    }
    
    class SomePage extends geb.Page {
        static content = {
            div { $("#d1") }
        }
    }

### Configuration

Configuration is done in the `given` block of a scenario or story. Here you can optionally set 3 properties; `driver`, `baseUrl` and `browser`.

You can set the `driver` property to the driver instance that you want to implicitly created browser instance to use. However, using the [configuration mechanism for driver implementation](configuration.html#driver_implementation) is preferred.

You can set the `baseUrl` property to the base url that you want to implicitly created browser instance to use. However, using the [configuration mechanism for base url](configuration.html#base_url) is preferred.

For fine grained control, you can create your own [browser][browser-api] instance and assign it to the `browser` property. Otherwise, an implicit browser object is created using `driver` and/or `baseUrl` if they were explicitly set (otherwise the configuration mechanism is used.)

## Cucumber (Cuke4Duke)

Geb doesn't offer any explicit integration with [Cuke4Duke][cuke4duke] but due to Cuke4Duke's use of Groovy scripts, Geb's [binding management features](binding.html) can be used to great effect.

The following is an example of what is possible…

    import static org.junit.Assert.*
    import static org.junit.matchers.JUnitMatchers.*

    import pages.*

    this.metaClass.mixin(cuke4duke.GroovyDsl)

    Given(~"I am on the Google search page") { ->
        to GoogleHomePage
        waitFor { at GoogleHomePage }
    }

    When(~"I search for \"(.*)\"") { String query ->
        page.searchField.value(query)
        page.searchButton.click()
    }

    Then(~"I am at the results page") {
        assert at(GoogleResultsPage)
    }

    Then(~"The first link should be \"(.*)\"") { String text ->
        waitFor { page.results }
        assertThat page.resultLink(0).text(), containsString(text)
    }
    
### Example Projects

The following projects can be used as starting references:

* [https://github.com/geb/geb-example-cuke4duke](https://github.com/geb/geb-example-cuke4duke)
