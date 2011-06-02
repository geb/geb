# Reporting

Geb includes a simple reporting mechanism which can be used to snapshot the state of the browser at any point in time. Reporters are implementations of the [`geb.report.Reporter`](api/geb-core/geb/report/Reporter.html) interface. Geb ships with two implementations; [`PageSourceReporter`](api/geb-core/geb/report/PageSourceReporter.html) and [`ScreenshotAndPageSourceReporter`](api/geb-core/geb/report/ScreenshotAndPageSourceReporter.html).

Reporters need to be supplied with a directory to write reports to, which can be [controlled by configuration](configuration.html#reports_dir).

## Scripting

If you are using Geb for scripting (or without one of the testing integrations discussed below), you can create and manage a reporter manually.

	import geb.Browser
	import geb.report.ScreenshotAndPageSourceReporter
	
	// create a reporter that writes to the “reports” dir and that cleans this dir on construction 
	def reporter = new ScreenshotAndPageSourceReporter(new File("reports"), true)
	
	Browser.drive {
		go "http://google.com"
		reporter.writeReport("google home page", delegate) // delegate is the browser instance
	}

If you plan on taking multiple reports, it can be handy to use Groovy's support for getting methods as closures and currying…

	Browser.drive {
		def report = reporter.&writeReport.rcurry(delegate)
		
		go "http://google.com"
		report "google home page"
	}

## Testing

The testing integrations such as [Spock](testing.html#spock_junit__testng), [JUnit](testing.html#spock_junit__testng), [TestNG](testing.html#testng) and (by extension) [Grails](build-integrations.html#grails) include support for reporting and use the [`ScreenshotAndPageSourceReporter`](api/geb-core/geb/report/ScreenshotAndPageSourceReporter.html) by default. Reports are taken implicitly by the testing framework that is driving Geb at the beginning and end of each test method.

All of these testing subclasses have a method `File getReportDir()` which by default calls through to the browser configuration's [`getReportsDir()`](configuration.html#reports_dir) method. If this method returns `null`, no reports will be taken. The dir that will actually be used will be the dir returned by this method plus the fully qualified class name of the test class. For example, if the reports dir is `/reports` and the test class is `my.app.FunctionalSpec` then the directory that will be used is `/reports/my/app/FunctionalSpec`.

All of these integrations also provide the method `void report(String label)` which can be used for taking report snapshots in the middle of test methods. The `label` argument will in some cases be appended to the name of the current test method where that can be determined (for JUnit 4 and Spock integrations).

	import geb
	import geb.spock.GebReportingSpec
	
	class FunctionalSpec extends GebReportingSpec {
		
		def "login"() {
			when:
			go "/login"
			username = "me"
			report "login screen" // take a report of the login screen
			login().click()
			
			then:
			title == "Logged in!"
		}
	}

### TestNG
Class `geb.testng.GebReportingTest` uses [TestNG Listeners](http://testng.org/doc/documentation-main.html#testng-listeners),
so you can implement your own listener, which for example will be save reports only on fail tests:

	class MyCustomListener extends TestListenerAdapter {

		@Override
		void onTestFailure(ITestResult tr) {
			createReport(tr)
		}

		private void createReport(ITestResult tr) {
			def testInstance = tr.testClass.getInstances(true).first()
			if (testInstance instanceof GebReportingTest) {
				testInstance.report(tr.method.methodName)
			}
		}
	}

And add your listener to the tests using the annotation, maven or gradle:

With annotation:

	@Listeners([MyCustomListener.class])
	class MyTest {

	}

With Maven2:

	<plugin>
		<groupId>org.apache.maven.plugins</groupId>
		<artifactId>maven-surefire-plugin</artifactId>
		<version>2.8.1</version>
		<configuration>
			<properties>
				<property>
					<name>listener</name>
					<value>MyCustomListener</value>
				</property>
			</properties>
		</configuration>
	</plugin>

With Gradle:

	test {
		useTestNG()

		options {
			listeners << 'MyCustomListener'
		}
	}
