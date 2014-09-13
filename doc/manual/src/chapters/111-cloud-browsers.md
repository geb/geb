# Cloud Browser Testing

When you want to perform web testing on multiple browsers and operating systems, it can be quite complicated to maintain machines for each of the target environments. There are a few companies that provide "remote web browsers as a service", making it easy to do this sort of matrix testing without having to maintain the multiple browser installations yourself. Geb provides easy integration with two such services, [SauceLabs](https://saucelabs.com/) and [BrowserStack](http://www.browserstack.com/). This integration includes two parts: assistance with creating a driver in `GebConfig.groovy` and a Gradle plugin.

## Creating a Driver

For both SauceLabs and BrowserStack, a special driver factory is provided that, given a browser specification as well as an username and access key, creates an instance of `RemoteWebDriver` configured to use a browser in the cloud. Examples of typical usage in `GebConfig.groovy` are included below. They will configure Geb to run in SauceLabs/BrowserStack if the appropriate system property is set, and if not it will use whatever driver that is configured. This is useful if you want to run the code in a local browser for development. In theory you could use any system property to pass the browser specification but `geb.saucelabs.browser`/`geb.browserstack.browser` are also used by the Geb Gradle plugins, so it's a good idea to stick with those property names.

The first parameter passed to the `create()` method is a ”browser specification“ and it should have the following format:

	«browser»:«operating system»:«version»

Assuming you're using the following snippet in your `GebConfig.groovy` to execute your code via SauceLabs with Firefox 19 on Linux, you would set the `geb.saucelabs.browser` system property to `firefox:linux:19`, and to execute it with IE 9 on Vista to `internetExplorer:vista:9`. Some browsers like Chrome automatically update to the latest version; for these browsers you don't need to specify the version as there's only one, and you would use something like `chrome:mac` as the ”browser specification“. For a full list of available browsers, versions and operating systems refer to your cloud provider's documentation:

* [SauceLabs platform list](https://saucelabs.com/docs/platforms/webdriver)
* [BrowserStack Browsers and Platforms list](http://www.browserstack.com/list-of-browsers-and-platforms?product=automate)

Please note that Geb Gradle plugins can set the `geb.saucelabs.browser`/`geb.browserstack.browser` system properties for you using the aforementioned format.

Following the browser specification are the username and access key used to identify your account with the cloud provider. The example uses two environment variables to access this information. This is usually the easiest way of passing something secret to your build in open CI services like [drone.io](https://drone.io/) or [Travis CI](https://travis-ci.org/) if your code is public, but you can use other mechanisms if desired.

You can optionally pass additional configuration settings by providing a Map to the `create()` method as the last parameter. The configuration options available are described in your cloud provider's documentation:

* [SauceLabs additional config](https://saucelabs.com/docs/additional-config)
* [BrowserStack Capabilities](http://www.browserstack.com/automate/capabilities)

### SauceLabsDriverFactory

	def sauceLabsBrowser = System.getProperty("geb.saucelabs.browser")
	if (sauceLabsBrowser) {
		driver = {
			def username = System.getenv("GEB_SAUCE_LABS_USER")
			assert username
			def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
			assert accessKey
			new SauceLabsDriverFactory().create(sauceLabsBrowser, username, accessKey)
		}
	}

### BrowserStackDriverFactory

	def browserStackBrowser = System.getProperty("geb.browserstack.browser")
	if (browserStackBrowser) {
		driver = {
			def username = System.getenv("GEB_BROWSERSTACK_USERNAME")
			assert username
			def accessKey = System.getenv("GEB_BROWSERSTACK_AUTHKEY")
			assert accessKey
			new BrowserStackDriverFactory().create(browserStackBrowser, username, accessKey)
		}
	}

If using `localIdentifier` support:

	def browserStackBrowser = System.getProperty("geb.browserstack.browser")
	if (browserStackBrowser) {
		driver = {
			def username = System.getenv("GEB_BROWSERSTACK_USERNAME")
			assert username
			def accessKey = System.getenv("GEB_BROWSERSTACK_AUTHKEY")
			assert accessKey
			def localId = System.getenv("GEB_BROWSERSTACK_LOCALID")
			assert localId
			new BrowserStackDriverFactory().create(browserStackBrowser, username, accessKey, localId)
		}
	}

## Geb Gradle plugins

For both SauceLabs and BrowserStack, Geb provides a Gradle plugin which simplifies declaring the account and browsers that are desired, as well as configuring a tunnel to allow the cloud provider to access local applications. These plugins allow easily creating multiple `Test` tasks that will have the appropriate `geb.PROVIDER.browser` property set (where *PROVIDER* is either `saucelabs` or `browserstack`). The value of that property can be then passed in configuration file to [SauceLabsDriverFactory](#saucelabsdriverfactory)/[BrowserStackDriverFactory](#browserstackdriverfactory) as the ”browser specification“. Examples of typical usage are included below.

### Gradle geb-saucelabs Plugin

	apply plugin: "geb-saucelabs" //1

	buildscript { //2
		repositories {
			mavenCentral()
		}
		dependencies {
			classpath 'org.gebish:geb-gradle:@geb-version@'
		}
	}

	repositories { //3
		maven { url "http://repository-saucelabs.forge.cloudbees.com/release" }
	}

	dependencies { //4
		sauceConnect "com.saucelabs:sauce-connect:3.0.28"
	}

	sauceLabs {
		browsers { //5
			firefox_linux_19
			chrome_mac
			internetExplorer_vista_9
		}
		task { //6
			testClassesDir = test.testClassesDir
			testSrcDirs = test.testSrcDirs
			classpath = test.classpath
		}
		account { //7
			username = System.getenv(SauceAccount.USER_ENV_VAR)
			accessKey = System.getenv(SauceAccount.ACCESS_KEY_ENV_VAR)
		}
	}

In (1) we apply the plugin to the build and in (2) we're specifying how to resolve the plugin.
In (3) and (4) we're defining dependencies for the `sauceConnect` configuration; this will be used by tasks that open a [SauceConnect](https://saucelabs.com/docs/connect) tunnel before running the generated test tasks which means that the browsers in the cloud will have localhost pointing at the machine running the build.
In (5) we're saying that we want our tests to run in 3 different browsers; this will generate the following `Test` tasks: `firefoxLinux19Test`, `chromeMacTest` and `internetExplorerVista9Test`.
You can use `allSauceLabsTests` task that will depend on all of the generated test tasks to run all of them during a build.
The configuration closure specified at (6) is used to configure all of the generated test tasks; for each of them the closure is run with delegate set to a test task being configured.
Finally in (7) we pass credentials for [SauceConnect](https://saucelabs.com/docs/connect).

### Gradle geb-browserstack Plugin

	apply plugin: "geb-browserstack" //1

	buildscript { //2
		repositories {
			mavenCentral()
		}
		dependencies {
			classpath 'org.gebish:geb-gradle:@geb-version@'
		}
	}

	browserStack {
		application 'http://localhost:8080' //3
		browsers { //4
			firefox_mac_19
			chrome_mac
			internetExplorer_windows_9
		}
		task { //5
			testClassesDir = test.testClassesDir
			testSrcDirs = test.testSrcDirs
			classpath = test.classpath
		}
		account { //6
			username = System.getenv(BrowserStackAccount.USER_ENV_VAR)
			accessKey = System.getenv(BrowserStackAccount.ACCESS_KEY_ENV_VAR)
		}
	}

In (1) we apply the plugin to the build and in (2) we're specifying how to resolve the plugin.
In (3) we're specifying which applications the BrowserStack Tunnel should be able to access.
Multiple applications can be specified.
If no applications are specified, the tunnel will not be restricted to particular URLs. 
In (4) we're saying that we want our tests to run in 3 different browsers; this will generate the following `Test` tasks: `firefoxMac19Test`, `chromeMacTest` and `internetExplorerWindows9Test`.
You can use `allBrowserStackTests` task that will depend on all of the generated test tasks to run all of them during a build.
The configuration closure specified at (5) is used to configure all of the generated test tasks; for each of them the closure is run with delegate set to a test task being configured.
Finally in (6) we pass credentials for BrowserStack.
