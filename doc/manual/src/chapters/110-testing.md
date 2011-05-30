# Testing

Geb provides first class support for functional web testing via integration with popular testing frameworks such as [Spock][spock], [JUnit][junit], [EasyB][junit] and [Cuke4Duke][cuke4duke].

## Spock & JUnit

The Spock and JUnit integrations both work fundamentally the same way. They provide subclasses that setup a [browser][browser-api] instance that all method calls and property accesses/references resolve against via Groovy's `methodMissing` and `propertyMissing` mechanism. 

> Recall that the browser instance also forwards any method calls or property accesses/references that it can't handle to it's current page object, which helps to remove a lot of noise from the test.

Consider the following Spock spec…

	import geb.spock.GebSpec
	
	class FunctionalSpec extends GebSpec {
		def "go to login"() {
			when:
			go "/login"
			
			then:
			title = "Login Screen"
		}
	}

Which is a nicer way of saying…

	import geb.spock.GebSpec
	
	class FunctionalSpec extends GebSpec {
		def "go to login"() {
			when:
			browser.go "/login"
			
			then:
			browser.title = "Login Screen"
		}
	}

### Configuration

The browser to use for the tests is supplied by the `createBrowser()` method which by default uses `new Browser()`, meaning that the browser is configured entirely by the [configuration mechanism](configuration.html) which is the preferred mechanism.

### Reporting

> See the section [testing](reporting.html#testing) section of the [reporting](reporting.html) chapter.

### Cookie management

The Spock and JUnit integrations both will automatically clear the browser's cookies at the end of each test method. For JUnit 3 this happens in the `tearDown()` method in `geb.junit3.GebTest` and for JUnit 4 it happens in an `@After` method in `geb.junit4.GebTest`.

The `geb.spock.GebSpec` class will clear the cookies in the `cleanup()` method unless the spec is `@Stepwise`, in which case they are cleared in `cleanupSpec()` (meaning that all feature methods in a stepwise spec share the same browser state).

This auto clearing of cookies can be [disabled via configuration](configuration.html#auto_clearing_cookies).

### Jar and class names

The following table illustrates the specific jars and class names for Spock and JUnit.

<table class="graybox" border="0" cellspacing="0" cellpadding="5">
	<tr>
		<th>Framework</th>
		<th>JAR</th>
		<th>Base Class</th>
		<th>Reporting Base Class</th>
	</tr>
	<tr>
		<td>Spock</td>
		<td><a href="http://mvnrepository.com/artifact/org.codehaus.geb/geb-spock">geb-spock</a></td>
		<td><a href="api/geb-spock/geb/spock/GebSpec.html">geb.spock.GebSpec</a></td>
		<td><a href="api/geb-spock/geb/spock/GebReportingSpec.html">geb.spock.GebReportingSpec</a></td>
	</tr>
	<tr>
		<td>JUnit 4</td>
		<td><a href="http://mvnrepository.com/artifact/org.codehaus.geb/geb-junit4">geb-junit4</a></td>
		<td><a href="api/geb-junit4/geb/junit4/GebTest.html">geb.junit4.GebTest</a></td>
		<td><a href="api/geb-junit4/geb/junit4/GebReportingTest.html">geb.junit4.GebReportingTest</a></td>
	</tr>
	<tr>
		<td>JUnit 3</td>
		<td><a href="http://mvnrepository.com/artifact/org.codehaus.geb/geb-junit3">geb-junit3</a></td>
		<td><a href="api/geb-junit3/geb/junit3/GebTest.html">geb.junit3.GebTest</a></td>
		<td><a href="api/geb-junit3/geb/junit3/GebReportingTest.html">geb.junit3.GebReportingTest</a></td>
	</tr>
</table>

### Example Projects

The following projects can be used as starting references:

* [https://github.com/geb/geb-example-gradle](https://github.com/geb/geb-example-gradle)

## Easyb

TBD

## Cucumber (Cuke4Duke)

Geb doesn't offer any explicit integration with [Cucumber](http://cukes.info/ "Cucumber - Making BDD fun") (through [Cuke4Duke](http://wiki.github.com/aslakhellesoy/cuke4duke/ "Home - cuke4duke - GitHub")). It does however work with it and there is an [example project available](http://github.com/geb/geb-example-cuke4duke "geb's geb-example-cuke4duke at master - GitHub") to show you how to put it together.
