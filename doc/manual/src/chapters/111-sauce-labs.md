# SauceLabs Integration

Geb comes with some tools that make configuring your Geb code to run in SauceLabs easier.

## SauceLabsDriverFactory

First, there is the `SauceLabsDriverFactory` that given a browser specification as well as an username and access key creates an instance of `RemoteWebDriver` configured to use a browser in SauceLabs cloud. One would typically use it in `GebConfig.groovy` like this:

	def sauceBrowser = System.getProperty("geb.sauce.browser")
	if (sauceBrowser) {
		driver = {
			def username = System.getenv("GEB_SAUCE_LABS_USER")
			assert username
			def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
			assert accessKey
			new SauceLabsDriverFactory().create(sauceBrowser, username, accessKey)
		}
	}

This will configure Geb to run in SauceLabs if `geb.sauce.browser` system property is set and if not it will use whatever driver that is configured - this is useful if you want to run the code in local browser for development. In theory you could use any system property to pass the browser specification but `geb.sauce.browser` is also used by [Gradle geb-saucelabs plugin](#gradle_geb_saucelabs_plugin) so it's a good idea to stick with that property name. The example also uses two environment variables to pass the username and access key to the factory as it's usually the easiest way of passing something secret to your build in open CI services like [drone.io](https://drone.io/) or [Travis CI](https://travis-ci.org/) if your code is public, but you could use any other mechanism if desired.
You can optionally pass additional configuration settings by providing a Map to the`create()` method. The configuration options available are described on the [SauceLabs additional config page](https://saucelabs.com/docs/additional-config).

The first parameter passed to the `create()` method is a ”browser specification“ and it should have the following format:

	«browser»:«operating system»:«version»

Assuming you're using the snippet from the example above in your `GebConfig.groovy` to execute your code with Firefox 19 on Linux you would set the `geb.sauce.browser` system property to `firefox:linux:19` and to execute it with IE 9 on Vista to `internetExplorer:vista:9`. Some browsers like Chrome automatically update to the latest version - for these browsers you don't need to specify the version as there's only one and you would use something like `chrome:mac` as the ”browser specification“. For a full list of available browsers, versions and operating systems refer to the [SauceLabs platform list](https://saucelabs.com/docs/platforms/webdriver). Please note that [Gradle geb-saucelabs plugin](#gradle_geb_saucelabs_plugin) can set the `geb.sauce.browser` system property for you using the aforementioned format.

## Gradle geb-saucelabs Plugin

The second part of SauceLabs integration is the `geb-saucelabs` Gradle plugin. You can use it in your Gradle build to easily create multiple `Test` tasks that will have `geb.sauce.browser` property set.  The value of that property can be then passed in configuration file to [SauceLabsDriverFactory](#saucelabsdriverfactory) as the ”browser specification“. What follows is a typical usage example:

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

In (1) we apply the plugin to the build and in (2) we're specifying how to resolve the plugin. In (3) and (4) we're defining dependencies for the `sauceConnect` configuration - this will be used by tasks that open a [SauceConnect](https://saucelabs.com/docs/connect) tunnel before running the generated test tasks which means that the browsers in the cloud will have localhost pointing at the machine running the build. In (5) we're saying that we want our tests to run in 3 different browsers - this will generate the following `Test` tasks: `firefoxLinux19Test`, `chromeMacTest` and `internetExplorerVista9Test`. You can use `allSauceTests` task that will depend on all of the generated test tasks to run all of them during a build. The configuration closure specified at (6) is used to configure all of the generated test tasks - for each of them the closure is run with delegate set to a test task being configured. Finally in (7) we pass credentials for [SauceConnect](https://saucelabs.com/docs/connect).