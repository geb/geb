# IDE Support

Geb does not require any special plugins or configuration for use inside an IDE. However, there are some considerations that will be addressed in this chapter.

## Execution

Geb _scripts_ can be executed in an IDE if that IDE supports executing Groovy scripts. All IDEs that support Groovy typically support this. There are typically only two concerns in the configuration of this: getting the Geb classes on the classpath, and the `GebConfig` file.

Geb _tests_ can be executed in an IDE if that IDE supports Groovy scripts and the testing framework that you are using with Geb. If you are using JUnit or Spock (which is based on JUnit) this is trivial, as all modern Java IDEs support JUnit. As far as the IDE is concerned, the Geb test is simply a JUnit test and no special support is required. As with executing scripts, the IDE must put the Geb classes on the classpath for test execution and the `GebConfig` file must be accessible (typically putting this file at the root of the test source tree is sufficient).

In both cases, the simplest way to create such an IDE configuration is to use a build tool (such as Gradle or Maven) that supports IDE integration. This will take care of the classpath setup and other concerns.

## Authoring Assistance (autocomplete and navigation)

This section discusses what kind of authoring assistance can be provided by IDEs and usage patterns that enable better authoring support.

### Dynamism and conciseness vs tooling support

Geb heavily embraces the dynamic typing offered by Groovy, to achieve conciseness for the purpose of readability. This immediately reduces the amount of authoring assistance that an IDE can provide when authoring Geb code. This is an intentional compromise. The primary cost in functional/acceptance testing is in the _maintenance_ of the test suite over time. Geb optimizes for this in several ways, one of which being the focus on intention revealing code (which is achieved through conciseness).

That said, if authoring support is a concern for you, read on to learn for details on ways to forsake conciseness in order to improve authoring support.

### Strong typing

In order to gain improved authoring support, you must include types in your tests and page objects. Additionally, you must explicitly access the browser and page objects instead of relying on dynamic dispatch.

Here's an example of idiomatic (untyped) Geb code.

    to HomePage
    loginButton.click()

    at LoginPage
    username = "user1"
    password = "password1"
    loginButton.click()

    at SecurePage

The same code written with types would look like:

	HomePage homePage = browser.to HomePage
	homePage.loginButton.click()

    LoginPage loginPage = browser.at LoginPage
    SecurePage securePage = loginPage.login("user1", "password1")

Where the page objects are:

	class HomePage extends Page {
		Navigator getLoginButton() {
			$("#loginButton")
		}
	}

	class LoginPage extends Page {

		static at = { title == "Login Page" }

		SecurePage login(String username, String password) {
		    $("#username").value username
		    $("#password").value username
		    $("#loginButton").click()
		    browser.at SecurePage
		}
	}

In summary:

1. Use the `browser` object explicitly (made available by the testing adapters)
2. Use the page instance returned by the `to()` and `at()` methods instead of calling through the browser
3. Use methods on the `Page` classes instead of the `content {}` block and dynamic properties
4. If you need to use content definition options like `required:` and `wait:` then you can still reference content elements defined using the DSL in methods on `Page` and `Module` classes as usual, e.g.:

    static content = {
        async(wait: true) { $(".async") }
    }

    String asyncText() {
        async.text() // Wait here for the async definition to return a non-empty Navigator...
    }

Using this “typed” style is not an all or nothing proposition. The typing options exist on a spectrum and can be used selectively where/when the cost of the extra “noise” is worth it to achieve better IDE support. For example, a mix of using the `content {}` DSL and methods can easily be used. The key enabler is to capture the result of the `to()` and `at()` methods in order to access the page object instance.

#### IntelliJ IDEA support

IntelliJ IDEA (since version 12) has special support for authoring Geb code. This is built in to the Groovy support; no additional installations are required.

The support provides:

* Understanding of implicit browser methods (e.g. `to()`, `at()`) in test classes (e.g. `extends GebSpec`)
* Understanding of content defined via the Content DSL (within `Page` and `Module` classes only)
* Completion in `at {}` and `content {}` blocks

This effectively enables more authoring support with less explicit type information. The Geb development team would like to thank the good folks at JetBrains for adding this explicit support for Geb to IDEA.
