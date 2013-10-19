# IDE Support

Geb does not require any special plugins or configuration for use inside an IDE. However, there are some considerations that will be addressed in this chapter.

## Execution

Geb _scripts_ can be executed in an IDE if that IDE supports executing Groovy scripts.
All IDEs that support Groovy typically support this.
There are typically only two concerns in the configuration of this; getting the Geb classes on the classpath, and the `GebConfig` file.

Geb _tests_ can be executed in an IDE if that IDE supports Groovy scripts and the testing framework that you are using with Geb.
If you are using JUnit or Spock (which is based on JUnit) this is trivial, as all modern Java IDEs support JUnit.
As far as the IDE is concerned, the Geb test is simply a JUnit test and no special support is required.
As with executing scripts, the IDE must put the Geb classes on the classpath for test execution and the `GebConfig` file must be accessible (typically putting this file at the root of the test source tree is sufficient).

In both cases, the simplest way to create such an IDE configuration is to use a build tool (such as Gradle or Maven) that supports IDE integration.
This will take care of the classpath setup and other concerns.


