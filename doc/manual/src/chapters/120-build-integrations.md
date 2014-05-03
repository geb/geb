# Build System & Framework Integrations

This kind of integration for Geb is typically focused on managing the base URL and reports dir, as build systems tend to be able to provide this configuration (via the [build adapter](configuration.html#build_adapter) mechanism).

## Grails

[Grails][grails] is a popular web app framework. There is a [grails-geb plugin](http://grails.org/plugin/geb) available that allows you to use Geb for your Grails functional tests. This plugin simply manages the `baseUrl` and `reportsDir` configuration items.

You still need to include the appropriate Geb module for testing (i.e. `geb-junit4` or `geb-spock`) yourself. You may also need to depend on other plugins like the [`grails-spock`](http://grails.org/plugin/spock) plugin to enable those kinds of tests.

For example, if you plan to use Spock with Geb you would need to add the following to the `BuildConfig.groovy`…

    dependencies {
        test "@geb-group@:geb-spock:@geb-version@"
    }
    plugins {
        test ":spock:@spock-version@"
        test ":geb:@geb-version@"
    }

Where `@geb-version@` and `@spock-version@` are the versions of Geb and Spock you wish to use.

As Grails provides JUnit support out of the box, you only need to pull in the `geb-junit4` jar to use Geb with JUnit.

> Grails 1.3 and later use JUnit 4. Earlier versions of Grails than this use Groovy 1.6 which Geb no longer supports.

    dependencies {
        test "@geb-group@:geb-junit4:@geb-version@"
    }
    plugins {
        test ":geb:@geb-version@"
    }

You only need the appropriate Geb test integration JAR, as it will depend on `geb-core` and Grails' dependency management will take care of getting that for you.

You will also of course need a driver and the `selenium-support` dependency, which you can also specify in `BuildConfig.groovy`.

    dependencies {
        test "org.seleniumhq.selenium:selenium-support:«webdriver version»"
        test "org.seleniumhq.selenium:selenium-firefox-driver:«webdriver version»"
    }

HTMLUnit depends on some XML processing libraries that cause issues with Grails. You can avoid this by excluding certain dependencies of the HTMLUnit driver…

    test("org.seleniumhq.selenium:selenium-htmlunit-driver:«webdriver version»") {
        exclude 'xml-apis'
    }

Recall that Geb looks for its configuration file as `GebConfig.groovy` on the classpath. A good location for this file is in a Grails project is the `test/functional` directory is on the classpath at test time. You **do not** need to set the `baseUrl` and `reportsDir` config entries in a Grails project as the plugin takes care of those for you. The `baseUrl` is set to the root of the Grails application, and the `reportsDir` is set to `geb` inside Grails' test reports dir (which by default is `target/test-reports`).

There is nothing special about writing Geb tests with Grails. You subclass the same classes as usual (e.g. `geb.spock.GebReportingSpec` for Spock tests).

There is an example project available that uses `geb-junit4` and `geb-spock` to test some Grails scaffold pages.

* [http://github.com/geb/geb-example-grails](https://github.com/geb/geb-example-grails)

## Gradle

Using Geb with Gradle simply involves pulling in the appropriate dependencies, and configuring the base URL and reports dir in the build script if they are necessary.

Below is a valid Gradle `build.gradle` file for working with Geb for testing.

    apply plugin: "groovy"

    repositories {
        mavenCentral()
    }

    configurations {
        testCompile.transitive = true
    }

    dependencies {
        groovy "org.codehaus.groovy:groovy-all:@groovy-version@"

        def gebVersion = "@geb-version@"
        def seleniumVersion = "@selenium-version@"

        // If using Spock, need to depend on geb-spock
        testCompile "@geb-group@:geb-spock:@geb-version@"
        testCompile "org.spockframework:spock-core:@spock-core-version@"

        // If using JUnit, need to depend on geb-junit (3 or 4)
        testCompile "@geb-group@:geb-junit4:@geb-version@"
        testCompile "junit:junit-dep:4.8.2"

        // Need a driver implementation
        testCompile "org.seleniumhq.selenium:selenium-firefox-driver:@selenium-version@"
        testRuntime "org.seleniumhq.selenium:selenium-support:@selenium-version@"
    }

    test {
        systemProperties "geb.build.reportsDir": "$reportsDir/geb"
    }

There is a Gradle example project available.

* [http://github.com/geb/geb-example-gradle](https://github.com/geb/geb-example-gradle)

## Maven

Using Geb with Maven simply involves pulling in the appropriate dependencies, and configuring the base URL and reports dir in the build script if they are necessary.

Below is a valid `pom.xml` file for working with Geb for testing (with Spock).

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>@geb-group@.example</groupId>
      <artifactId>geb-maven-example</artifactId>
      <packaging>jar</packaging>
      <version>1</version>
      <name>Geb Maven Example</name>
      <url>http://www.gebish.org</url>
      <dependencies>
        <dependency>
          <groupId>org.codehaus.groovy</groupId>
          <artifactId>groovy-all</artifactId>
          <version>1.7.10</version>
        </dependency>
        <dependency>
          <groupId>junit</groupId>
          <artifactId>junit</artifactId>
          <version>4.8.1</version>
          <scope>test</scope>
        </dependency>
        <dependency>
          <groupId>org.spockframework</groupId>
          <artifactId>spock-core</artifactId>
          <version>@spock-core-version@</version>
          <scope>test</scope>
        </dependency>
        <dependency>
          <groupId>@geb-group@</groupId>
          <artifactId>geb-spock</artifactId>
          <version>@geb-version@</version>
          <scope>test</scope>
        </dependency>
        <dependency>
          <groupId>org.seleniumhq.selenium</groupId>
          <artifactId>selenium-firefox-driver</artifactId>
          <version>@selenium-version@</version>
          <scope>test</scope>
        </dependency>
        <dependency>
          <groupId>org.seleniumhq.selenium</groupId>
          <artifactId>selenium-support</artifactId>
          <version>@selenium-version@</version>
          <scope>test</scope>
        </dependency>
      </dependencies>
      <build>
        <plugins>
          <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.9</version>
            <configuration>
              <includes>
                <include>*Spec.*</include>
              </includes>
              <systemPropertyVariables>
                <geb.build.baseUrl>http://google.com/ncr</geb.build.baseUrl>
                <geb.build.reportsDir>target/test-reports/geb</geb.build.reportsDir>
              </systemPropertyVariables>
            </configuration>
          </plugin>
          <plugin>
            <groupId>org.codehaus.gmaven</groupId>
            <artifactId>gmaven-plugin</artifactId>
            <version>1.3</version>
            <configuration>
              <providerSelection>1.7</providerSelection>
            </configuration>
            <executions>
              <execution>
                <goals>
                  <goal>testCompile</goal>
                </goals>
              </execution>
            </executions>
          </plugin>
        </plugins>
      </build>
    </project>
    
There is a Maven example project available.

* [http://github.com/geb/geb-example-maven](https://github.com/geb/geb-example-maven)
