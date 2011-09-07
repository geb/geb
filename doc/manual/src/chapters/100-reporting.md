# Reporting

Geb includes a simple reporting mechanism which can be used to snapshot the state of the browser at any point in time. Reporters are implementations of the [`Reporter`](api/geb-core/geb/report/Reporter.html) interface. Geb ships with two implementations; [`PageSourceReporter`](api/geb-core/geb/report/PageSourceReporter.html) and [`ScreenshotAndPageSourceReporter`](api/geb-core/geb/report/ScreenshotAndPageSourceReporter.html) (the default). There are three bits of configuration that pertain to reporting; the [reporter](configuration.html#reporter) implementation, the [reports directory](configuration.html#reports_dir) and whether to [only report test failures](configuration.html#report_test_failures_only) or not.

You take a report by calling the [`report(String label)`](api/geb-core/geb/Browser.html#report(java.lang.String\)) method on the browser object.

    Browser.drive {
        go "http://google.com"
        report "google home page"
    }

> The `report()` method will thrown an exception if it is called and there is no configured `reportsDir`. If you are going to use reporting you **must** specify a `reportsDir` via config.

Assuming that we configured a `reportsDir` of “`reports/geb`”, after running this script we will find two files in this directory:

* `google home page.html` - A html dump of the page source
* `google home page.png` - A screenshot of the browser as a PNG file (if the driver implementation supports this)

> To avoid issues with reserved characters in filenames, Geb replaces any character in the report name that is not an alphanumeric, a space or a hyphen with an underscore.

## The report group

The configuration mechanism allows you to specify the base `reportsDir` which is where reports are written to by default. It is also possible to change the [report group](api/geb-core/geb/Browser.html#reportGroup(java.lang.String\)) to a relative path inside this directory.

    Browser.drive {
        reportGroup "google"
        go "http://google.com"
        report "home page"
        
        reportGroup "wikipedia"
        go "http://wikipedia.org"
        report "home page"
    }

We have now created the following files inside the `reportsDir`…

* `google/home page.html`
* `google/home page.png`
* `wikipedia/home page.html`
* `wikipedia/home page.png`

The browser will create the directory for the report group as needed. By default, the report group is not set which means that reports are written to the base of the `reportsDir`. To go back to this after setting a report group, simply call `reportGroup(null)`.

> It is common for test integrations to manage the report group for you, setting it to the name of the test class.

## Cleaning

Geb does not automatically clean the reports dir for you. It does however provide a method that you can call to do this.

    Browser.drive {
        cleanReportGroupDir()
        go "http://google.com"
        report "home page"
    }

The [`cleanReportGroupDir()`](api/geb-core/geb/Browser.html#cleanReportGroupDir(\)) method will remove whatever the reports group dir is set to at the time. If it cannot do this it will throw an exception.

> The Spock, JUnit and TestNG test integrations **do** automatically clean the reports dir for you, see the [section in the testing chapter](testing.html#reporting) on these integrations.
