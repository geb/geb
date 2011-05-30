# History

This page lists the high level changes between versions of Geb.

## 0.6

* selenium-common is now a 'provided' scoped dependency of Geb
* Radio buttons can be selected with their label text as well as their value attribute.
* Select options can be selected with their text as well as their value attribute.
* `Navigator.getAttribute` returns `null` rather than the empty string when an attribute is not found.
* The `jquery` property on `Navigator` now returns whatever the jQuery method called on it returns.

### New Features

* All waitFor clauses now treat exceptions raised in the condition as an evaluation failure, instead of propagation the exception
* Content can be defined with `wait: true` to make Geb implicitly wait for it when it is requested
* Screenshots are now taken when reporting for all drivers that implement the `TakesScreenshot` interface (which is nearly all)
* Added `BindingUpdater` class that can manage a groovy script binding for use with Geb
* Added `quit()` and `close()` methods to browser that delegate to the underlying driver instance
* `geb.Browser.drive()` methods now return the used `Browser` instance
* The underlying WebElements of a Navigator are now retrievable
* Added $() methods that take one or more Navigator or WebElement objects and returns a new Navigator composed of these objects
* Added Direct Download API which can be used for directly downloading content (PDFs, CSVs etc.) into your Geb program (not via the browser)
* Introduced new configuration mechanism for more flexible and environment sensitive configuration of Geb (e.g. driver implementation, base url)
* Default wait timeout and retry interval is now configurable, and can now also use user configuration presets (e.g. quick, slow)
* Added a “build adapter” mechanism, making it easier for build systems to take control of relevant configuration

### Breaking Changes

* Raised minimum Groovy version to 1.7
* All failed waitFor clauses now throw a `geb.waiting.WaitTimeoutException` instead of `AssertionError`
* Upgraded minimum version requirement of WebDriver to 2.0b2

## 0.5.1

* Fixed problem with incorrectly compiled specs and the geb grails module

## 0.5

### New Features

* Navigator objects now implement the Groovy truth (empty == false, non empty == true)
* Introduced “js” short notation
* Added “[easyb][easyb]” support (`geb-easyb` and Grails support)
* Page change listening support through `geb.PageChangeListener`
* `waitFor()` methods added, making dealing with dynamic pages easier
* Support for `alert()` and `confirm()` dialogs
* Added jQuery integration
* Reporting integration classes (e.g. GebReportingSpec) now save a screenshot if using the FirefoxDriver
* Added `displayed` property to navigator objects for determining visibility
* Added `find` as an alias for `$` (e.g. `find("div.section")`)
* Browser objects now implement the `page(List<Class>)` method that sets the page to the first type whose at-checker matches the page
* The click() methods that take one or more page classes are now available on `Navigator` objects
* Added page lifecycle methods `onLoad()`/`onUnload()`

### Breaking Changes

* Exceptions raised in `drive()` blocks are no longer wrapped with `DriveException`
* the `at(Class pageClass)` method no longer requires the existing page instance to be of that class (page will be updated if the given type matches)

## 0.4

**Initial Public Release**