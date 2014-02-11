# Implicit Assertions

As of Geb 0.7.0, certain parts of Geb utilise “*implicit assertions*”. This sole goal of this feature is to provide more informative error messages. Put simply, it means that for a given block of code, all *expressions* are automatically turned into assertions. So the following code:

    1 == 1

Becomes…

    assert 1 == 1

> If you've used the [Spock Framework][spock] you will be well familiar with the concept of implicit assertions from Spock's `then:` blocks.

In Geb, waiting expressions and at expressions automatically use implicit assertions. Take the following page object…

    class ImplicitAssertionsExamplePage extends Page {
        
        static at = { title == "Implicit Assertions!" }
        
        static content = {
            dynamicParagraph(wait: true) { $("p", 0).text() == "implicit assertions are cool!" }
        }
        
        def waitForHeading() {
            waitFor { $("h1") }
        }
    }

This automatically becomes…

    class ImplicitAssertionsExamplePage extends Page {
        
        static at = { assert title == "Implicit Assertions!" }
        
        static content = {
            dynamicParagraph(wait: true) { assert $("p", 0).text() == "implicit assertions are cool!" }
        }
        
        def waitForHeading() {
            waitFor { assert $("h1") }
        }
    }

Because of this, Geb is able to provide much better error messages when the expression fails due to Groovy's [power asserts](http://dontmindthelanguage.wordpress.com/2009/12/11/groovy-1-7-power-assert/).

**Note:** A special form of `assert` is used that returns the value of the expression, whereas a regular `assert` returns `null`. 

This means that given…

    static content = {
        headingText(wait: true) { $("h1").text() }
    }

Accessing `headingText` here will *wait* for there to be a `h1` and for it to have some text (because an [empty string is `false` in Groovy](http://docs.codehaus.org/display/GROOVY/Groovy+Truth)), which will then be returned. This means that even when implicit assertions are used, the value is still returned and is is usable.

## At Verification

Let's take the `at` case.

> If you're unfamiliar with Geb's “at checking”, please read [this section][page-at]. 

Consider the following small Geb script…

    Browser.drive {
        go ImplicitAssertionsExamplePage
        at ImplicitAssertionsExamplePage
    }

At checking works by verifying that the page's “at check” returns a *trueish* value. If it does, the `at()` method returns `true`. If not, the `at()` method will return `false`. However, due to implicit assertions, the “at check” will never return `false`. Instead, the at checker will throw an [AssertionError][assertion-error]. Because the page's “at check” is turned into an assertion, we'll get an error like:

    Assertion failed: 

    title == "Implicit Assertions!"
    |     |
    |     false
    Something else

        at ImplicitAssertionsExamplePage._clinit__closure1(ImplicitAssertionsExamplePage.groovy:3)
        at ImplicitAssertionsExamplePage._clinit__closure1(ImplicitAssertionsExamplePage.groovy)
        at geb.Page.verifyAt(Page.groovy:131)
        at geb.Browser.doAt(Browser.groovy:335)
        at geb.Browser.at(Browser.groovy:259)
        at groovyscript(groovyscript.groovy:3)

As you can see, this is much more informative than the `at()` method simply returning `false`.

## Waiting

Another place where implicit assertions are utilised is for *waiting*.

> If you're unfamiliar with Geb's “waiting” support, please read [this section][waiting].

Consider the following Geb script:

    Browser.drive {
        waitFor { title == "Page Title" }
    }

The `waitFor` method verifies that the given clause returns a *trueish* value within a certain timeframe. Because of implicit assertions, when this fails the error will look something like this:

    geb.waiting.WaitTimeoutException: condition did not pass in 5.0 seconds (failed with exception)
        at geb.waiting.Wait.waitFor(Wait.groovy:128)
        at geb.waiting.WaitingSupport.doWaitFor(WaitingSupport.groovy:108)
        at geb.waiting.WaitingSupport.waitFor(WaitingSupport.groovy:84)
        at geb.waiting.WaitingSupport.waitFor(WaitingSupport.groovy:80)
        at geb.Browser.methodMissing(Browser.groovy:168)
        at geb.test.GebSpec.methodMissing(GebSpec.groovy:80)
        at groovyscript(groovyscript.groovy:2)
    Caused by: Assertion failed: 

    title == "Page Title"
    |     |
    |     false
    Something else

        at geb.waiting.WaitingSupportSpec.failed waiting_closure4(WaitingSupportSpec.groovy:67)
        at geb.waiting.WaitingSupportSpec.failed waiting_closure4(WaitingSupportSpec.groovy)
        at geb.waiting.Wait.waitFor(Wait.groovy:117)
        ... 6 more

The failed assertion is carried as the cause of the `geb.waiting.WaitTimeoutException` and gives you an informative message as to why the waiting failed.

### Waiting Content

The same implicit assertion semantics apply to content definitions that are waiting.

> If you're unfamiliar with Geb's “waiting content” support, please read [this section][waiting-content].

Any content definitions that declare a `wait` parameter have implicit assertions added to each expression just like `waitFor()` method calls.

## How it works 

The “implicit assertions” feature is implemented as a [Groovy compile time transformation](http://groovy.codehaus.org/Compile-time+Metaprogramming+-+AST+Transformations), which literally turns all *expressions* in a candidate block of code into assertions.

This transform is packaged as a separate JAR named `geb-implicit-assertions`. This JAR needs to be on the compilation classpath of your Geb test/pages/modules (and any other code that you want to use implicit assertions) in order for this feature to work.

If you are obtaining Geb via a dependency management system, this is typically not something you need to be concerned about as it will happen automatically. Geb is distributed via the Maven Central repository in Apache Maven format (i.e. via POM files). The main Geb module, `geb-core` depends on the `geb-implicit-assertions` module as a `compile` dependency. 

If your dependency management system *inherits* transitive compile dependencies (i.e. also makes compile dependencies of first class compile dependencies first class compile dependencies) then you will automatically have the `geb-implicit-assertions` module as a compile dependency and everything will work fine (Maven, Gradle, Grails, and most configurations of Ivy do this). If your dependency management system does not do this, or if you are manually managing the `geb-core` dependency, be sure to include the `geb-implicit-assertions` dependency as a compile dependency.

