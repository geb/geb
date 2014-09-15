# About the Project

The Geb home page can be found at [http://www.gebish.org](http://www.gebish.org).

## API Reference

The API reference can be found [here](api/index.html).

## Support & Development

Support for Geb is offered on the user@geb.codehaus.org mailing list, which can be subscribed to [here](http://xircles.codehaus.org/lists/user@geb.codehaus.org).

Ideas and new features for Geb can be discussed on the dev@geb.codehaus.org mailing list, which can be subscribed to [here](http://xircles.codehaus.org/lists/dev@geb.codehaus.org).

## Credits

### Committers

* [Luke Daley](http://ldaley.com)
* [Marcin Erdmann](http://blog.proxerd.pl)
* [Chris Prior](https://github.com/chris-prior)

### Contributors

* [Robert Fletcher](http://adhockery.blogspot.com/)
* [Peter Niederwieser](http://pniederw.wordpress.com/)
* [Alexander Zolotov](http://github.com/zolotov) - TestNG Integration
* [Christoph Neuroth](http://c089.wordpress.com/) - Various fixes and patches
* [Antony Jones](http://www.desirableobjects.co.uk/) - Various fixes and patches, doc improvements
* [Jan-Hendrik Peters](http://hennr.name/imprint/) - Doc improvements
* [Tomás Lin](http://fbflex.wordpress.com/) - Doc improvements
* [Jason Cahoon](https://github.com/measlyweasel) - Bug fix around text matchers
* [Tomasz Kalkosiński](http://refaktor.blogspot.com/) - Doc improvements
* [Rich Douglas Evans](https://github.com/richdouglasevans) - Doc improvements
* [Ian Durkan](https://github.com/idurkan) - Doc improvements
* [Colin Harrington](http://colinharrington.net/) - Doc improvements
* [Bob Herrmann](https://github.com/bherrmann7) - Doc improvements
* [George T Walters II](https://github.com/walterg2) - Page option support for `withWindow()`
* [Craig Atkinson](https://github.com/craigatk) - Doc improvements
* [Andy Duncan](https://github.com/andyjduncan) - Fail fast when unexpected pages are encountered
* [John Engelman](https://github.com/johnrengelman) - Grails integration improvements
* [Michael Legart](https://github.com/legart) - Grails integration improvements
* [Graeme Rocher](https://github.com/graemerocher) - Grails integration improvements
* [Craig Atkinson](https://github.com/craigatk) - Bug fix around unexpected pages
* [Ken Geis](https://github.com/kgeis) - Doc improvements
* [Kelly Robinson](https://github.com/kellyrob99) - Additional configuration parameters for SauceLabs
* [Todd Gerspacher](https://github.com/tygerpatch) - Doc improvements, Cleaned up settings.gradle
* [David M. Carr](https://github.com/davidmc24) - BrowserStack integration
* [Tom Dunstan](https://github.com/tomdcc) - Cucumber integration and related documentation
* [Brian Kotek](https://github.com/brian428) - Doc improvements
* [David W Millar](https://github.com/david-w-millar) - Doc improvements

## History

This page lists the high level changes between versions of Geb.

### @geb-version@

#### New Features

* New `css()` method on `Navigator` that allows to access CSS properties of elements. \[[GEB-141](http://jira.codehaus.org/browse/GEB-141)\]
* Added attribute based methods to relative content navigators such as next(), children() etc. \[[GEB-299](http://jira.codehaus.org/browse/GEB-299)\]
* Added signature that accepts `localIdentifier` to `BrowserStackDriverFactory.create`. \[[GEB-332](http://jira.codehaus.org/browse/GEB-332)\]

#### Fixes

* Allow access to module properties from its content block. \[[GEB-245](http://jira.codehaus.org/browse/GEB-245)\]
* Support setting of elements for WebDriver implementations that return uppercase tag name. \[[GEB-318](http://jira.codehaus.org/browse/GEB-318)\]
* Use native binaries for running BrowserStack tunnel. \[[GEB-326](http://jira.codehaus.org/browse/GEB-326)\]
* Update BrowserStack support to use command-line arguments introduced in tunnel version 3.1. \[[GEB-332](http://jira.codehaus.org/browse/GEB-332)\]

#### Project Related Changes

* Updated cucumber integration example to use `cucumber-jvm` instead of the now defunct `cuke4duke`. \[[GEB-324](http://jira.codehaus.org/browse/GEB-324)\]
* Setup CI for all of the example projects \[[GEB-188](http://jira.codehaus.org/browse/GEB-188)\]
* Incorporate the example projects into the main build \[[GEB-189](http://jira.codehaus.org/browse/GEB-189)\]
* Add a test crawling the site in search for broken links \[[GEB-327](http://jira.codehaus.org/browse/GEB-327)\]
* Use Groovy 2.3.6 to build the project \[[GEB-330](http://jira.codehaus.org/browse/GEB-330)\]

### 0.9.3

#### New Features

* Added `baseNavigatorWaiting` setting to prevent intermittent Firefox driver errors when creating base navigator. \[[GEB-269](http://jira.codehaus.org/browse/GEB-269)\]
* Page content classes including `Module` now implement `Navigator` interface \[[GEB-181](http://jira.codehaus.org/browse/GEB-181)\]
* Added some tests that guard performance by verifying which WebDriver commands are executed \[[GEB-302](http://jira.codehaus.org/browse/GEB-302)\]
* Added [BrowserStack](http://www.browserstack.com) integration \[[GEB-307](http://jira.codehaus.org/browse/GEB-307)\]
* Added a shortcut to `Browser` for getting current url \[[GEB-294](http://jira.codehaus.org/browse/GEB-294)\]
* Verify pages at checker when passed as an option to open a new window via `withWindow()` and `withNewWindow()` \[[GEB-278](http://jira.codehaus.org/browse/GEB-278)\]

#### Fixes

* Ignore `atCheckWaiting` setting when checking for unexpected pages. \[[GEB-267](http://jira.codehaus.org/browse/GEB-267)\]
* Added missing range variants of find/$ methods. \[[GEB-283](http://jira.codehaus.org/browse/GEB-283)\]
* Migrated `UnableToLoadException` to java. \[[GEB-263](http://jira.codehaus.org/browse/GEB-263)\]
* Exception thrown when trying to set value on an invalid element (non form control). \[[GEB-286](http://jira.codehaus.org/browse/GEB-286)\]
* Support for jQuery methods like offset() and position() which return a native Javascript object. \[[GEB-271](http://jira.codehaus.org/browse/GEB-271)\]
* Finding elements when passing ids with spaces in the predicates map to the $() method. \[[GEB-308](http://jira.codehaus.org/browse/GEB-308)\]

#### Breaking Changes

* Removed easyb support. \[[GEB-277](http://jira.codehaus.org/browse/GEB-277)\]
* `MissingMethodException` is now thrown when using shortcut for obtaining a navigator based on a control name and the returned navigator is empty. \[[GEB-239](http://jira.codehaus.org/browse/GEB-239)\]
* When using SauceLabs integration, the `allSauceTests` task was renamed to`allSauceLabsTests`
* When using SauceLabs integration, the `geb.sauce.browser` system property was renamed to `geb.saucelabs.browser`

#### Project Related Changes

* Documentation site has been migrated to [Ratpack](http://ratpack.io). \[[GEB-261](http://jira.codehaus.org/browse/GEB-261)\]
* Cross browser tests are now also executed using Safari driver \[[GEB-276](http://jira.codehaus.org/browse/GEB-276)\]
* Artifact snapshots are uploaded and gebish.org is updated after every successful build in CI \[[GEB-295](http://jira.codehaus.org/browse/GEB-295)\]
* Migrated continuous integration build to [Snap CI](https://snap-ci.com/geb/geb/branch/master)
* Added a [Travis CI build](https://travis-ci.org/geb/geb) that runs tests on submitted pull requests \[[GEB-309](http://jira.codehaus.org/browse/GEB-309)\]

### 0.9.2

#### New Features

* `page` and `close` options can be passed to `withWindow()` calls, see [this manual section](browser.html#passing_options_when_working_with_already_opened_windows) for more information.
* Unexpected pages can be specified to fail fast when performing ”at“ checks. This feature was contributed at a Hackergarten thanks to Andy Duncan. See [this manual section](pages.html#unexpected_pages) for details. \[[GEB-70](http://jira.codehaus.org/browse/GEB-70)\]
* Support for running Geb using SauceLabs provided browsers, see [this manual section](cloud-browsers.html) for details.
* New [`isEnabled()`](api/geb/navigator/Navigator.html#isEnabled\(\)) and [`isEditable()`](api/geb/navigator/Navigator.html#isEditable\(\)) methods on `Navigator`.
* Support for ephemeral port allocation with Grails integration
* Compatibility with Grails 2.3

#### Fixes

* Default value of `close` option for `withNewWindow()` is set to `true` as specified in the documentation. \[[GEB-258](http://jira.codehaus.org/browse/GEB-258)\]

#### Breaking Changes

* `isDisabled()` now throws `UnsupportedOperationException` if called on an `EmptyNavigator` or on a `Navigator` that contains anything else than a button, input, option, select or textarea.
* `isReadOnly()` now throws `UnsupportedOperationException` if called on an `EmptyNavigator` or on a `Navigator` that contains anything else than an input or a textarea.

### 0.9.1

#### Breaking Changes

* Explicitly calling `at()` with a page object will throw `UndefinedAtCheckerException` instead of silently passing if the page object does not define an at checker.
* Passing a page with no at checker to `click(List<Class<? extends Page>>)` or as one of the pages in `to` template option will throw `UndefinedAtCheckerException`.

#### New Features

* Support for dealing with self-signed certificates in Download API using `SelfSignedCertificateHelper`. \[[GEB-150](http://jira.codehaus.org/browse/GEB-150)\]
* Connections created when using Download API can be configured globally using `defaultDownloadConfig` configuration option.
* New `atCheckWaiting` configuration option allowing to implictly wrap ”at“ checkers in `waitFor` calls. \[[GEB-253](http://jira.codehaus.org/browse/GEB-253)\]

#### Fixes
* `containsWord()` and `iContainsWord()` now return expected results when matching against text that contains spaces \[[GEB-254](http://jira.codehaus.org/browse/GEB-254)\]
* `has(Map<String, Object> predicates, String selector)` and `has(Map<String, Object> predicates)` were added to Navigator for consistency with `find()` and `filter()` \[[GEB-256](http://jira.codehaus.org/browse/GEB-256)\]
*  Also catch WaitTimeoutException when page verification has failed following a `click()` call \[[GEB-255](http://jira.codehaus.org/browse/GEB-255)\]
* `not(Map<String, Object> predicates, String selector)` and `not(Map<String, Object> predicates)` were added to Navigator for consistency with `find()` and `filter()` \[[GEB-257](http://jira.codehaus.org/browse/GEB-257)\]
* Make sure that `NullPointerException` is not thrown for incorrect driver implementations of getting current url without previously driving the browser to a url \[[GEB-291](http://jira.codehaus.org/browse/GEB-291)\]

### 0.9.0

#### New Features

* New `via()` method that behaves the same way as `to()` behaved previously - it sets the page on the browser and does not verify the at checker of that page\[[GEB-249](http://jira.codehaus.org/browse/GEB-249)\].
* It is now possible to provide your own [`Navigator`][navigator-api] implementations by specifying a custom [`NavigatorFactory`](api/geb/navigator/factory/NavigatorFactory.html), see [this manual section](configuration.html#navigator_factory) for more information \[[GEB-96](http://jira.codehaus.org/browse/GEB-96)\].
* New variants of `withFrame()` method that allow to switch into frame context and change the page in one go and also automatically change it back to the original page after the call, see [switching pages and frames at once][switch-frame-and-page] in the manual \[[GEB-213](http://jira.codehaus.org/browse/GEB-213)\].
* `wait`, `page` and `close` options can be passed to `withNewWindow()` calls, see [this manual section](browser.html#passing_options_when_working_with_newly_opened_windows) for more information \[[GEB-167](http://jira.codehaus.org/browse/GEB-167)\].
* Improved message of UnresolvablePropertyException to include a hint about forgetting to import the class \[[GEB-240](http://jira.codehaus.org/browse/GEB-240)\].
* Improved signature of `Browser.at()` and `Browser.to()` to return the exact type of the page that was asserted to be at / was navigated to.
* [`ReportingListener`](api/geb/report/ReportingListener.html) objects can be registered to observe reporting (see: [reporting.html#listening_to_reporting](reporting.html#listening_to_reporting)

#### Fixes

* Fixed an issue where waitFor would throw a WaitTimeoutException even if the last evaluation before timeout returned a truthy value \[[GEB-215](http://jira.codehaus.org/browse/GEB-215)\].
* Fixed taking screenshots for reporting when the browser is not on a HTML page (e.g. XML file) \[[GEB-126](http://jira.codehaus.org/browse/GEB-126)\].
* Return the last evaluation value for a `(wait: true, required: false)` content instead of always returning null \[[GEB-216](http://jira.codehaus.org/browse/GEB-216)\].
* `isAt()` behaves the same as `at()` in regards to updating the browser's page instance to the given page type if its at checker is successful \[[GEB-227](http://jira.codehaus.org/browse/GEB-227)\].
* Handling of `select` elements has been reworked to be far more efficient \[[GEB-229](http://jira.codehaus.org/browse/GEB-229)\].
* Modules support accessing base attributes' values using @attributeName notation \[[GEB-237](http://jira.codehaus.org/browse/GEB-237)\].
* Use of text matchers in module base definitions is supported \[[GEB-241](http://jira.codehaus.org/browse/GEB-241)\].
* Reading of textareas have been updated so that the current value of the text field is returned, instead of the initial \[[GEB-174](http://jira.codehaus.org/browse/GEB-174)\].

#### Breaking Changes

* `to(Class<? extends Page>)` method now changes the page on the browser and verifies the at checker of that page in one method call \[[GEB-1](http://jira.codehaus.org/browse/GEB-1)\], \[[GEB-249](http://jira.codehaus.org/browse/GEB-249)\]; use `via()` if you need the old behaviour
* `getAttribute(String)` on `Navigator` now returns `null` for boolean attributes that are not present.
* `at()` and `to()` methods on `Browser` now return a page instance if they succeed and `via()` method always returns a page instance \[[GEB-217](http://jira.codehaus.org/browse/GEB-217)\].
* `withFrame()` calls that don't take a page argument now change the browser page to what it was before the call, after the call \[[GEB-222](http://jira.codehaus.org/browse/GEB-222)\].
* due to performance improvements duplicate elements are not removed when creating new `Navigator`s anymore; the new `unique()` method on `Navigator` can be used to remove duplicates if needed \[[GEB-223](http://jira.codehaus.org/browse/GEB-223)\].
* `at(Page)` and `isAt(Page)` methods on `Browser` have been removed as they were inconsistent with the rest of the API \[[GEB-242](http://jira.codehaus.org/browse/GEB-242)\].
* Navigator's `click(Class<? extends Page>)` and `to:` content option now verify page after switching to the new one to stay consistent with the new behaviour of `to(Class<? extends Page>)` \[[GEB-250](http://jira.codehaus.org/browse/GEB-250)\].
* Reading an attribute that is not set on a navigator now returns an empty string across all drivers \[[GEB-251](http://jira.codehaus.org/browse/GEB-251)\].

### 0.7.2

#### Fixes

* Further fixes for Java 7 \[[GEB-211](http://jira.codehaus.org/browse/GEB-211)\].

### 0.7.1

#### New Features

* Geb is now built with Groovy 1.8.6. This was forced to resolve \[[GEB-194](http://jira.codehaus.org/browse/GEB-194)\].

#### Fixes

* `startsWith()`, `contains()` etc. now work for selecting via element text now works for multiline (i.e. `<br/>`) text \[[GEB-202](http://jira.codehaus.org/browse/GEB-202)\]
* Geb now works with Java 7 \[[GEB-194](http://jira.codehaus.org/browse/GEB-194)\].

### 0.7.0

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
