# About the Project

The Geb home page can be found at [http://www.gebish.org](http://www.gebish.org).

## API Reference

The API reference can be found [here](api/index.html).

## Support & Development

Support for Geb is offered on the geb-user@googlegroups.com mailing list, which can be subscribed to [here](https://groups.google.com/forum/#!forum/geb-user).

Ideas and new features for Geb can be discussed on the geb-dev@googlegroups.com mailing list, which can be subscribed to [here](https://groups.google.com/d/forum/geb-dev).

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
* [Colin Harrington](https://github.com/ColinHarrington) - Doc improvements
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
* [Ai-Lin Liou](https://github.com/alincode) - Doc improvements
* [Varun Menon](https://github.com/menonvarun) - Selenium By selector support and related documentation, Support navigating to page instances in addition to classes
* [Anders D. Johnson](https://github.com/adjohnson916) - Doc improvements
* [Hiroyuki Ohnaka](https://github.com/azusa) - Doc improvements
* [Erik Pragt](https://github.com/bodiam) - Migration of the manual to AsciiDoc
* [Vijay Bolleypally](https://github.com/vijaybolleypally) - Added verification of at checkers for pages passed to `withFrame()` methods
* [Pierre Hilt](https://github.com/pierre-hilt) - `hasNot()` filtering
* [Yotaro Takahashi](https://github.com/PoohSunny) - Doc improvements
* [Jochen Berger](https://github.com/jochenberger) - Better error reporting when trying to set a nonexistent select option

## History

This page lists the high level changes between versions of Geb.

### @geb-version@

#### New Features

* Support for finding elements using Webdriver's [`By`][by-api] selectors. \[[#348](https://github.com/geb/issues/issues/348)\]
* Support for navigating to page instances in addition to classes. \[[#310](https://github.com/geb/issues/issues/310)\]
* Support for using page instances as `page` option value of window switching methods. \[[#352](https://github.com/geb/issues/issues/352)\]
* Support for using page instances together with frame switching methods. \[[#354](https://github.com/geb/issues/issues/354)\]
* Support for using page instances with `Navigator.click()` methods. \[[#355](https://github.com/geb/issues/issues/355)\]
* Support for using page instances and lists of page instances as `page` option value of content templates. \[[#356](https://github.com/geb/issues/issues/356)\]
* New `Navigator.module(Class<? extends Module>)` and `Navigable.module(Class<? extends Module>)`. \[[#312](https://github.com/geb/issues/issues/312)\]
* New `Navigable.module(Module)` and `Navigable.module(Module)`. \[[#311](https://github.com/geb/issues/issues/311)\]

#### Fixes

* Improved message thrown from Navigator.isDisabled() and Navigator.isReadOnly() when navigator does not contain a form element. \[[#345](https://github.com/geb/issues/issues/345)\]
* Browser.verifyAtIfPresent() should fail for at checkers returning false when implicit assertions are disabled. \[[#357](https://github.com/geb/issues/issues/357)\]
* Provide better error reporting when unexpected pages configuration is not a collection that contains classes which extend `Page` \[[#270](https://github.com/geb/issues/issues/270)\]
* Don't fail when creating a report and driver's screenshot taking method returns null \[[#292](https://github.com/geb/issues/issues/292)\]

#### Breaking changes
* `Page.toString()` now returns full page class name instead of its simple name.
* Geb is now built with Groovy 2.3.9 and Spock 1.0-groovy-2.3.

#### Deprecations
* `module(Class<? extends Module>, Navigator base)` available in content DLS has been deprecated in favour of `Navigator.module(Class<? extends Module>)` and will be removed in a future version of Geb.
* `module(Class<? extends Module>, Map args)` available in content DLS has been deprecated in favour of `Navigable.module(Module)` and will be removed in a future version of Geb.
* `module(Class<? extends Module>, Navigator base, Map args)` available in content DLS has been deprecated in favour of `Navigator.module(Module)` and will be removed in a future version of Geb.
* all variants of `moduleList()` method available in content DLS have been deprecated in favour of using `Navigator.module()` methods together with a `collect()` call and will be removed in a future version of Geb, see [chapter on using modules for repeating content](modules.html#using_modules_for_repeating_content_on_a_page) for examples \[[#362](https://github.com/geb/issues/issues/362)\]

### 0.10.0

#### New Features

* New `css()` method on `Navigator` that allows to access CSS properties of elements. \[[#141](https://github.com/geb/issues/issues/141)\]
* Added attribute based methods to relative content navigators such as next(), children() etc. \[[#299](https://github.com/geb/issues/issues/299)\]
* Added signature that accepts `localIdentifier` to `BrowserStackDriverFactory.create`. \[[#332](https://github.com/geb/issues/issues/332)\]
* Added [`toWait`](pages.html#towait) content definition option which allows specifying that page transition happens asynchronously. \[[#134](https://github.com/geb/issues/issues/134)\]
* Added support for explicitly specifying browser capabilities when using cloud browsers Gradle plugins. \[[#340](https://github.com/geb/issues/issues/340)\]
* Added an overloaded `create()` method on cloud driver factories that allow specifying browser capabilities in a map and don't require a string capabilities specification. \[[#281](https://github.com/geb/issues/issues/281)\]

#### Fixes

* Allow access to module properties from its content block. \[[#245](https://github.com/geb/issues/issues/245)\]
* Support setting of elements for WebDriver implementations that return uppercase tag name. \[[#318](https://github.com/geb/issues/issues/318)\]
* Use native binaries for running BrowserStack tunnel. \[[#326](https://github.com/geb/issues/issues/326)\]
* Update BrowserStack support to use command-line arguments introduced in tunnel version 3.1. \[[#332](https://github.com/geb/issues/issues/332)\]
* Fix PermGen memory leak when using groovy script backed configuration. \[[#335](https://github.com/geb/issues/issues/335)\]
* Don't fail in `Browser.isAt()` if at check waiting is enabled and it times out. \[[#337](https://github.com/geb/issues/issues/337)\]
* The value passed to `aliases` content option in documentation examples should be a String \[[#338](https://github.com/geb/issues/issues/338)\]
* Added `$()` method on Navigator with all signatures of `find()`. \[[#321](https://github.com/geb/issues/issues/321)\]
* `geb-saucelabs` plugin now uses a native version of SauceConnect. \[[#341](https://github.com/geb/issues/issues/341)\]
* Don't modify the predicate map passed to  [`Navigator.find(Map<String, Object>, String)`](api/geb/navigator/Locator.html#find\(Map%3CString,%20Object%3E,%20java.lang.String\)). \[[#339](https://github.com/geb/issues/issues/339)\]
* Functional tests implemented using JUnit and Geb run twice in Grails 2.3+. \[[#314](https://github.com/geb/issues/issues/314)\]
* Mention in the manual where snapshot artifacts can be downloaded from. \[[#305](https://github.com/geb/issues/issues/305)\]
* Document that `withNewWindow()` and `withWindow()` switch page back to the original one. \[[#279](https://github.com/geb/issues/issues/279)\]
* Fix selectors in documentation for manipulating checkboxes. \[[#268](https://github.com/geb/issues/issues/268)\]

#### Project Related Changes

* Updated cucumber integration example to use `cucumber-jvm` instead of the now defunct `cuke4duke`. \[[#324](https://github.com/geb/issues/issues/324)\]
* Setup CI for all of the example projects. \[[#188](https://github.com/geb/issues/issues/188)\]
* Incorporate the example projects into the main build. \[[#189](https://github.com/geb/issues/issues/189)\]
* Add a test crawling the site in search for broken links. \[[#327](https://github.com/geb/issues/issues/327)\]
* Document the [release process](https://github.com/geb/geb/blob/master/RELEASING.md). \[[#325](https://github.com/geb/issues/issues/325)\]

#### Breaking changes
* Use Groovy 2.3.6 to build Geb. \[[#330](https://github.com/geb/issues/issues/330)\]
* Format of browser specification passed to `BrowserStackBrowserFactory.create()` and `SauceLabsBrowserFactory.create()` has changed to be a string in Java properties file format defining the required browser capabilities.
* `sauceConnect` configuration used with `geb-saucelabs` plugin should now point at a version of 'ci-sauce' artifact from 'com.saucelabs' group.

### 0.9.3

#### New Features

* Added `baseNavigatorWaiting` setting to prevent intermittent Firefox driver errors when creating base navigator. \[[#269](https://github.com/geb/issues/issues/269)\]
* Page content classes including `Module` now implement `Navigator` interface \[[#181](https://github.com/geb/issues/issues/181)\]
* Added some tests that guard performance by verifying which WebDriver commands are executed \[[#302](https://github.com/geb/issues/issues/302)\]
* Added [BrowserStack](http://www.browserstack.com) integration \[[#307](https://github.com/geb/issues/issues/307)\]
* Added a shortcut to `Browser` for getting current url \[[#294](https://github.com/geb/issues/issues/294)\]
* Verify pages at checker when passed as an option to open a new window via `withWindow()` and `withNewWindow()` \[[#278](https://github.com/geb/issues/issues/278)\]

#### Fixes

* Ignore `atCheckWaiting` setting when checking for unexpected pages. \[[#267](https://github.com/geb/issues/issues/267)\]
* Added missing range variants of find/$ methods. \[[#283](https://github.com/geb/issues/issues/283)\]
* Migrated `UnableToLoadException` to java. \[[#263](https://github.com/geb/issues/issues/263)\]
* Exception thrown when trying to set value on an invalid element (non form control). \[[#286](https://github.com/geb/issues/issues/286)\]
* Support for jQuery methods like offset() and position() which return a native Javascript object. \[[#271](https://github.com/geb/issues/issues/271)\]
* Finding elements when passing ids with spaces in the predicates map to the $() method. \[[#308](https://github.com/geb/issues/issues/308)\]

#### Breaking Changes

* Removed easyb support. \[[#277](https://github.com/geb/issues/issues/277)\]
* `MissingMethodException` is now thrown when using shortcut for obtaining a navigator based on a control name and the returned navigator is empty. \[[#239](https://github.com/geb/issues/issues/239)\]
* When using SauceLabs integration, the `allSauceTests` task was renamed to`allSauceLabsTests`
* When using SauceLabs integration, the `geb.sauce.browser` system property was renamed to `geb.saucelabs.browser`
* `Module` now implements `Navigator` instead of `Navigable` so `Navigator`'s methods can be called on it without having to first call `$()` to get the module's base `Navigator`.

#### Project Related Changes

* Documentation site has been migrated to [Ratpack](http://ratpack.io). \[[#261](https://github.com/geb/issues/issues/261)\]
* Cross browser tests are now also executed using Safari driver \[[#276](https://github.com/geb/issues/issues/276)\]
* Artifact snapshots are uploaded and gebish.org is updated after every successful build in CI \[[#295](https://github.com/geb/issues/issues/295)\]
* Migrated continuous integration build to [Snap CI](https://snap-ci.com/geb/geb/branch/master)
* Added a [Travis CI build](https://travis-ci.org/geb/geb) that runs tests on submitted pull requests \[[#309](https://github.com/geb/issues/issues/309)\]

### 0.9.2

#### New Features

* `page` and `close` options can be passed to `withWindow()` calls, see [this manual section](browser.html#passing_options_when_working_with_already_opened_windows) for more information.
* Unexpected pages can be specified to fail fast when performing ”at“ checks. This feature was contributed at a Hackergarten thanks to Andy Duncan. See [this manual section](pages.html#unexpected_pages) for details. \[[#70](https://github.com/geb/issues/issues/70)\]
* Support for running Geb using SauceLabs provided browsers, see [this manual section](cloud-browsers.html) for details.
* New [`isEnabled()`](api/geb/navigator/Navigator.html#isEnabled\(\)) and [`isEditable()`](api/geb/navigator/Navigator.html#isEditable\(\)) methods on `Navigator`.
* Support for ephemeral port allocation with Grails integration
* Compatibility with Grails 2.3

#### Fixes

* Default value of `close` option for `withNewWindow()` is set to `true` as specified in the documentation. \[[#258](https://github.com/geb/issues/issues/258)\]

#### Breaking Changes

* `isDisabled()` now throws `UnsupportedOperationException` if called on an `EmptyNavigator` or on a `Navigator` that contains anything else than a button, input, option, select or textarea.
* `isReadOnly()` now throws `UnsupportedOperationException` if called on an `EmptyNavigator` or on a `Navigator` that contains anything else than an input or a textarea.

### 0.9.1

#### Breaking Changes

* Explicitly calling `at()` with a page object will throw `UndefinedAtCheckerException` instead of silently passing if the page object does not define an at checker.
* Passing a page with no at checker to `click(List<Class<? extends Page>>)` or as one of the pages in `to` template option will throw `UndefinedAtCheckerException`.

#### New Features

* Support for dealing with self-signed certificates in Download API using `SelfSignedCertificateHelper`. \[[#150](https://github.com/geb/issues/issues/150)\]
* Connections created when using Download API can be configured globally using `defaultDownloadConfig` configuration option.
* New `atCheckWaiting` configuration option allowing to implictly wrap ”at“ checkers in `waitFor` calls. \[[#253](https://github.com/geb/issues/issues/253)\]

#### Fixes
* `containsWord()` and `iContainsWord()` now return expected results when matching against text that contains spaces \[[#254](https://github.com/geb/issues/issues/254)\]
* `has(Map<String, Object> predicates, String selector)` and `has(Map<String, Object> predicates)` were added to Navigator for consistency with `find()` and `filter()` \[[#256](https://github.com/geb/issues/issues/256)\]
*  Also catch WaitTimeoutException when page verification has failed following a `click()` call \[[#255](https://github.com/geb/issues/issues/255)\]
* `not(Map<String, Object> predicates, String selector)` and `not(Map<String, Object> predicates)` were added to Navigator for consistency with `find()` and `filter()` \[[#257](https://github.com/geb/issues/issues/257)\]
* Make sure that `NullPointerException` is not thrown for incorrect driver implementations of getting current url without previously driving the browser to a url \[[#291](https://github.com/geb/issues/issues/291)\]

### 0.9.0

#### New Features

* New `via()` method that behaves the same way as `to()` behaved previously - it sets the page on the browser and does not verify the at checker of that page\[[#249](https://github.com/geb/issues/issues/249)\].
* It is now possible to provide your own [`Navigator`][navigator-api] implementations by specifying a custom [`NavigatorFactory`](api/geb/navigator/factory/NavigatorFactory.html), see [this manual section](configuration.html#navigator_factory) for more information \[[#96](https://github.com/geb/issues/issues/96)\].
* New variants of `withFrame()` method that allow to switch into frame context and change the page in one go and also automatically change it back to the original page after the call, see [switching pages and frames at once][switch-frame-and-page] in the manual \[[#213](https://github.com/geb/issues/issues/213)\].
* `wait`, `page` and `close` options can be passed to `withNewWindow()` calls, see [this manual section](browser.html#passing_options_when_working_with_newly_opened_windows) for more information \[[#167](https://github.com/geb/issues/issues/167)\].
* Improved message of UnresolvablePropertyException to include a hint about forgetting to import the class \[[#240](https://github.com/geb/issues/issues/240)\].
* Improved signature of `Browser.at()` and `Browser.to()` to return the exact type of the page that was asserted to be at / was navigated to.
* [`ReportingListener`](api/geb/report/ReportingListener.html) objects can be registered to observe reporting (see: [reporting.html#listening_to_reporting](reporting.html#listening_to_reporting)

#### Fixes

* Fixed an issue where waitFor would throw a WaitTimeoutException even if the last evaluation before timeout returned a truthy value \[[#215](https://github.com/geb/issues/issues/215)\].
* Fixed taking screenshots for reporting when the browser is not on a HTML page (e.g. XML file) \[[#126](https://github.com/geb/issues/issues/126)\].
* Return the last evaluation value for a `(wait: true, required: false)` content instead of always returning null \[[#216](https://github.com/geb/issues/issues/216)\].
* `isAt()` behaves the same as `at()` in regards to updating the browser's page instance to the given page type if its at checker is successful \[[#227](https://github.com/geb/issues/issues/227)\].
* Handling of `select` elements has been reworked to be far more efficient \[[#229](https://github.com/geb/issues/issues/229)\].
* Modules support accessing base attributes' values using @attributeName notation \[[#237](https://github.com/geb/issues/issues/237)\].
* Use of text matchers in module base definitions is supported \[[#241](https://github.com/geb/issues/issues/241)\].
* Reading of textareas have been updated so that the current value of the text field is returned, instead of the initial \[[#174](https://github.com/geb/issues/issues/174)\].

#### Breaking Changes

* `to(Class<? extends Page>)` method now changes the page on the browser and verifies the at checker of that page in one method call \[[#1](https://github.com/geb/issues/issues/1)\], \[[#249](https://github.com/geb/issues/issues/249)\]; use `via()` if you need the old behaviour
* `getAttribute(String)` on `Navigator` now returns `null` for boolean attributes that are not present.
* `at()` and `to()` methods on `Browser` now return a page instance if they succeed and `via()` method always returns a page instance \[[#217](https://github.com/geb/issues/issues/217)\].
* `withFrame()` calls that don't take a page argument now change the browser page to what it was before the call, after the call \[[#222](https://github.com/geb/issues/issues/222)\].
* due to performance improvements duplicate elements are not removed when creating new `Navigator`s anymore; the new `unique()` method on `Navigator` can be used to remove duplicates if needed \[[#223](https://github.com/geb/issues/issues/223)\].
* `at(Page)` and `isAt(Page)` methods on `Browser` have been removed as they were inconsistent with the rest of the API \[[#242](https://github.com/geb/issues/issues/242)\].
* Navigator's `click(Class<? extends Page>)` and `to:` content option now verify page after switching to the new one to stay consistent with the new behaviour of `to(Class<? extends Page>)` \[[#250](https://github.com/geb/issues/issues/250)\].
* Reading an attribute that is not set on a navigator now returns an empty string across all drivers \[[#251](https://github.com/geb/issues/issues/251)\].

### 0.7.2

#### Fixes

* Further fixes for Java 7 \[[#211](https://github.com/geb/issues/issues/211)\].

### 0.7.1

#### New Features

* Geb is now built with Groovy 1.8.6. This was forced to resolve \[[#194](https://github.com/geb/issues/issues/194)\].

#### Fixes

* `startsWith()`, `contains()` etc. now work for selecting via element text now works for multiline (i.e. `<br/>`) text \[[#202](https://github.com/geb/issues/issues/202)\]
* Geb now works with Java 7 \[[#194](https://github.com/geb/issues/issues/194)\].

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
