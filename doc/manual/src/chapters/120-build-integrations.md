# Build System & Framework Integrations

This kind of integration for Geb is typically focussed on managing the base url and reports dir as build systems tend to be able to provide this configuration (via the [build adapter](configuration.html#build_adapter) mechanism).

## Grails

[Grails][grails] is a popular web app framework. There is a [grails-geb plugin](http://grails.org/plugin/geb) available that allows you to use Geb for your Grails functional tests. This plugin simply manages the `baseUrl` and `reportsDir` configuration items.

You still need to include the appropriate Geb module for testing (i.e. `geb-junit4`, `geb-spock` or `geb-easyb`) yourself. You may also need to depend on other plugins like the [`grails-spock`](http://grails.org/plugin/spock) plugin to enable those kinds of tests.

For example, if you plan to use Spock with Geb you would need to add the following to the `BuildConfig.groovy`…

    dependencies {
        test "org.codehaus.geb:geb-spock:«geb version»"
    }
    plugins {
        test ":spock:«spock version»"
        test ":geb:«geb version»"
    }

Where `«geb version»` and `«spock version»` are the versions of Geb and Spock you wish to use.

As Grails provides JUnit support out of the box, you only need to pull in the `geb-junit4` jar to use Geb with JUnit.

> Grails 1.3 and later use JUnit 4. Earlier versions of Grails than this use Groovy 1.6 which Geb no longer supports.

    dependencies {
        test "org.codehaus.geb:geb-junit4:«geb version»"
    }
    plugins {
        test ":geb:«geb version»"
    }

You only need the appropriate Geb test integration jar, as it will depend on `geb-core` and Grails' dependency management will take care of getting that for you.

You will also of course need a driver, which you can also specify in `BuildConfig.groovy`.

    dependencies {
        test "org.seleniumhq.selenium:selenium-firefox-driver:«webdriver version»"
    }

HTMLUnit depends on some XML processing libraries that cause issues with Grails. You can avoid this by excluding certain dependencies of the HTMLUnit driver…

    test("org.seleniumhq.selenium:selenium-htmlunit-driver:«webdriver version»") {
        exclude 'xml-apis'
    }

Recall that Geb looks for its configuration file as `geb-conf.groovy` on the classpath. A good location for this file is in a Grails project is the `test/functional` directory is on the classpath at test time. You **do not** need to set the `baseUrl` and `reportsDir` config entries in a Grails project as the plugin takes care of those for you. The `baseUrl` is set to the root of the Grails application, and the `reportsDir` is set to `geb` inside Grails' test reports dir (which by default is `target/test-reports`).

There is nothing special about writing Geb tests with Grails. You subclass the same classes as usual (e.g. `geb.spock.GebReportingSpec` for Spock tests).

There is an example project available that uses `geb-junit4` and `geb-spock` to test some Grails scaffold pages.

* [http://github.com/geb/geb-example-gradle](https://github.com/geb/geb-example-gradle)

## Maven

## Gradle
