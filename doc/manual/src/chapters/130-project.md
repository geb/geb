# About the Project

The Geb home page can be found at [http://www.gebish.org](http://www.gebish.org).

## API Reference

* [geb-core](api/geb-core/index.html)
* [geb-spock](api/geb-spock/index.html)
* [geb-junit4](api/geb-junit4/index.html)
* [geb-testng](api/geb-testng/index.html)
* [geb-easyb](api/geb-easyb/index.html)
* [geb-junit3](api/geb-junit3/index.html)

## Support & Development

Support for Geb is offered on the user@geb.codehaus.org mailing list, which can be subscribed to [here](http://xircles.codehaus.org/lists/user@geb.codehaus.org).

Ideas and new features for Geb can be discussed on the dev@geb.codehaus.org mailing list, which can be subscribed to [here](http://xircles.codehaus.org/lists/dev@geb.codehaus.org).

## Credits

### Committers

* [Luke Daley](http://ldaley.com)
* [Robert Fletcher](http://adhockery.blogspot.com/)
* [Peter Niederwieser](http://pniederw.wordpress.com/)
* [Marcin Erdmann](https://github.com/erdi)

### Contributors

* [Alexander Zolotov](http://github.com/zolotov) - TestNG Integration
* [Christoph Neuroth](http://c089.wordpress.com/) - Various fixes and patches
* [Antony Jones](http://www.desirableobjects.co.uk/) - Various fixes and patches, doc improvements

## History

This page lists the high level changes between versions of Geb.

### @geb-version@

#### New Features

* Added support for indexes and ranges in `moduleList()` method 
* Form control shortcuts now also work on page and module content
* Custom timeout message for `waitFor()` calls
* Navigators can be composed also from content
* Closure expressions passed to `waitFor()` calls are now transformed so that every statement in them is asserted - this provides better reporting on `waitFor()` timeouts.
* `at` closure properties of Page classes are now transformed so that every statement in them is asserted - this provides better reporting on failed at checks 
* new `isAt()` method on Browser that behaves like `at()` used to behave before, i.e. does not throw AssertionError but returns `false` if at checking fails
* `withAlert()` and `withConfirm()` now accept a `wait` option and the possible values are the same as for waiting content

#### Breaking Changes

* `click()` now instructs the browser to click **only on the first** element the navigator has matched
* All `click()` method variants return the reciever
* Content definitions with `required: false, wait: true` return `null` and do not throw `WaitTimeoutException` if the timeout expires
* Assignment statements are not allowed anymore in closure expressions passed to `waitFor()` calls
* `at()` now throws AssertionException if at checking fails instead of returning false

### 0.6.3 

#### New Features

* Compatibility with Spock 0.6

### 0.6.2

#### New Features

* New `interact()` function for mouse and keyboard actions which delegates to the WebDriver Actions class
* New `moduleList()` function for repeating content
* New `withFrame()` method for working with frames
* New `withWindow()` and `withNewWindow()` methods for working with multiple windows
* Added `getCurrentWindow()` and `getAvailableWindows()` methods to browser that delegate to the underlying driver instance
* Content aliasing is now possible using `aliases` parameter in content DSL
* If config script is not found a config class will be used if there is any - this is usefull if you run test using Geb from IDE
* Drivers are now cached across the whole JVM, which avoids the browser startup cost in some situations
* Added config option to disable quitting of cached browsers on JVM shutdown

#### Breaking Changes

* The `Page.convertToPath()` function is now responsible for adding a prefix slash if required (i.e. it's not added implicitly in `Page.getPageUrl()`) [GEB-139].
* Unchecked checkboxes now report their value as `false` instead of null

### 0.6.1

#### New Features

* Compatibility with at least Selenium 2.9.0 (version 0.6.0 of Geb did not work with Selenium 2.5.0 and up)
* Attempting to set a select to a value that it does not contain now throws an exception
* The waiting algorithm is now time based instead of number of retries based, which is better for blocks that are not near instant
* Better support for working with already instantiated pages

#### Breaking Changes

* Using `<select>` elements with Geb now requires an explicit dependency on an extra WebDriver jar (see [the section on installation for more info](intro.html#installation__usage))
* The `Navigator` `classes()` method now returns a `List` (instead of `Set`) and guarantees that it will be sorted alphabetically

### 0.6

#### New Features

* selenium-common is now a 'provided' scoped dependency of Geb
* Radio buttons can be selected with their label text as well as their value attribute.
* Select options can be selected with their text as well as their value attribute.
* `Navigator.getAttribute` returns `null` rather than the empty string when an attribute is not found.
* The `jquery` property on `Navigator` now returns whatever the jQuery method called on it returns.
* All waitFor clauses now treat exceptions raised in the condition as an evaluation failure, instead of propagating the exception
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
* The JUnit 3 integration now includes the test method name in the automatically generated reports
* The reporting support has been rewritten, making it much friendlier to use outside of testing
* Added the TestNG support (contributed by Alexander Zolotov)
* Added the `height`, `width`, `x` and `y` properties to navigator objects and modules

#### Breaking Changes

* Raised minimum Groovy version to 1.7
* All failed waitFor clauses now throw a `geb.waiting.WaitTimeoutException` instead of `AssertionError`
* Upgraded minimum version requirement of WebDriver to 2.0rc1
* The `onLoad()` and `onUnload()` page methods both have changed their return types from `def` to `void`
* The Grails specific testing subclasses have been REMOVED. Use the direct equivalent instead (e.g `geb.spock.GebReportingSpec` instead of `grails.plugin.geb.GebSpec`)
* The Grails plugin no longer depends on the test integration modules, you need to depend on the one you want manually
* The `getBaseUrl()` method from testing subclasses has been removed, use the configuration mechanism
* Inputs with no value now report their value as an empty string instead of `null`
* Select elements that are not multiple select enabled no longer report their value as a 1 element list, but now as the value of the selected element (if no selection, `null` is returned)

### 0.5.1

* Fixed problem with incorrectly compiled specs and the geb grails module

### 0.5

#### New Features

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

#### Breaking Changes

* Exceptions raised in `drive()` blocks are no longer wrapped with `DriveException`
* the `at(Class pageClass)` method no longer requires the existing page instance to be of that class (page will be updated if the given type matches)

### 0.4

**Initial Public Release**
