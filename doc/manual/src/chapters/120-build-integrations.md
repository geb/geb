# Build System & Framework Integrations

## Grails

Grails support is provided by the `grails-geb` plugin, and provides support for both Spock, JUnit (3 and 4) tests and Easyb. Working with Spock requires installing the spock plugin separately, as does working with Easyb.

> See the JUnit, Spock and Easyb sections above for more info on how to write Geb tests with these tools

### Writing Tests

Geb tests go into the `test/functional` directory. The tests are configured to have a base url of the root of the Grails application under test. This can be overridden by either overriding the `getBaseUrl()` method in your test/spec on a per test basis, or globally by specifying the `baseUrl` command line option…

    grails test-app -baseUrl=http://myapp.com

#### JUnit

If you are using Grails 1.2 and want to write JUnit tests, you subclass the `grails.plugin.geb.JUnit3GebTests` class. If you are using Grails 1.3 or later, you subclass `grails.plugin.geb.GebTests`.

#### Spock

To write Spock specs, you subclass `grails.plugin.geb.GebSpec`.

#### Easyb

To use Easyb, you use the `geb-grails` plugin in your Easyb stories or scenarios (instead of the normal `geb` plugin).

> There is currently a classloader issue with the Grails Easyb plugin that makes it impossible to use page object classes that are defined outside of the current story or scenario. This limits the usefulness of the grails+geb+easyb for the time being. This will be addressed in future Easyb releases.

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
