# BrowserStack Integration

Geb comes with some tools that make configuring your Geb code to run in BrowserStack easier.

## BrowserStackDriverFactory

First, there is the `BrowserStackDriverFactory` that given a browser specification as well as an username and access key creates an instance of `RemoteWebDriver` configured to use a browser in the BrowserStack cloud. One would typically use it in `GebConfig.groovy` like this:

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

This will configure Geb to run in BrowserStack if the `geb.browserstack.browser` system property is set, and if not it will use whatever driver that is configured. This is useful if you want to run the code in local browser for development. In theory you could use any system property to pass the browser specification but `geb.browserstack.browser` is also used by [Gradle geb-browserstack plugin](#gradle_geb_browserstack_plugin), so it's a good idea to stick with that property name. The example also uses two environment variables to pass the username and access key to the factory, as it's usually the easiest way of passing something secret to your build in open CI services like [drone.io](https://drone.io/) or [Travis CI](https://travis-ci.org/) if your code is public, but you could use any other mechanism if desired.

You can optionally pass additional configuration settings by providing a Map to the`create()` method as the last parameter. The configuration options available are described on the [BrowserStack Capabilities page](http://www.browserstack.com/automate/capabilities).

The first parameter passed to the `create()` method is a ”browser specification“ and it should have the following format:

	«browser»:«operating system»:«version»

Assuming you're using the snippet from the example above in your `GebConfig.groovy` to execute your code with Firefox 19 on Mac OS X, you would set the `geb.browserstack.browser` system property to `firefox:mac:19`, and to execute it with IE 10 on Windows 8 to `internetExplorer:win8:10`. If you don't specify a version, it will default to the latest stable version of the browser selected. If that's what you desire, you would use something like `chrome:mac` as the ”browser specification“. For a full list of available browsers, versions and operating systems refer to the [BrowserStack Browsers and Platforms list](http://www.browserstack.com/list-of-browsers-and-platforms?product=automate). Please note that [Gradle geb-browserstack plugin](#gradle_geb_browserstack_plugin) can set the `geb.browserstack.browser` system property for you using the aforementioned format.

## Gradle geb-browserstack Plugin

The second part of BrowserStack integration is the `geb-browserstack` Gradle plugin. You can use it in your Gradle build to easily create multiple `Test` tasks that will have `geb.browserstack.browser` property set.  The value of that property can be then passed in configuration file to [BrowserStackDriverFactory](#browserstackdriverfactory) as the ”browser specification“. What follows is a typical usage example:

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
			firefox_linux_19
			chrome_mac
			internetExplorer_vista_9
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

In (1) we apply the plugin to the build and in (2) we're specifying how to resolve the plugin. In (3) we're what applications the BrowserStack Tunnel will be able to access.  Multiple applications can be specified. In (4) we're saying that we want our tests to run in 3 different browsers; this will generate the following `Test` tasks: `firefoxLinux19Test`, `chromeMacTest` and `internetExplorerVista9Test`. You can use `allBrowserStackTests` task that will depend on all of the generated test tasks to run all of them during a build. The configuration closure specified at (5) is used to configure all of the generated test tasks; for each of them the closure is run with delegate set to a test task being configured. Finally in (6) we pass credentials for BrowserStack.
