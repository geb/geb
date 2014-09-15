# Javascript, AJAX and Dynamic Pages

This section discusses how to deal with some of the challenges in testing and/or automating modern web applications.

## The "js" object

The browser instance exposes a “[`js`](api/geb/Browser.html#getJs\(\))” object that provides support for working with JavaScript over and above what WebDriver provides.
It's important to understand how WebDriver does handle JavaScript, which is through a driver's implementation of [JavascriptExecutor]'s [`executeScript()`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/JavascriptExecutor.html#executeScript\(java.lang.String,%20java.lang.Object...\)) method.

> Before reading further, it's **strongly** recommended to read the description of [`executeScript()`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/JavascriptExecutor.html#executeScript\(java.lang.String,%20java.lang.Object...\)) in order to understand how type conversion works between the two worlds.

You can execute JavaScript like you would with straight WebDriver using the driver instance via the browser…

    assert browser.driver.executeScript("return arguments[0];", 1) == 1

This is a bit long winded, and as you would expect Geb uses the dynamism of Groovy to make life easier.

> The [JavascriptExecutor][javascriptexecutor] interface does not define any contract in regards to the driver's responsibility when there is some issue executing JavaScript. All drivers however throw _some kind_ of exception when this happens.

### Accessing Variables

Any _global_ JavaScript variables inside the browser can be read as _properties_ of the `js` object.

Given the following page…

    <html>
        <script type="text/javascript">
            var aVariable = 1;
        </script>
    <body>
    </body>
    </html>

We could access the JavaScript variable “`aVariable`” with…

    Browser.drive {
        assert js.aVariable == 1
    }

Or if we wanted to map it to page content…

    class ExamplePage extends Page {
        static content = {
            aVar { js.aVariable }
        }
    }
    
    Browser.drive {
        to ExamplePage
        assert aVar == 1
    }

We can even access _nested_ variables…

    assert js."document.title" == "Book of Geb"

### Calling Methods

Any _global_ JavaScript functions can be called as methods on the `js` object.

Given the following page…

    <html>
        <script type="text/javascript">
            function addThem(a,b) {
                return a + b;
            }
        </script>
    <body>
    </body>
    </html>

We can call the `addThem()` function with…

    Browser.drive {
        assert js.addThem(1, 2) == 3
    }

This also works from pages and modules.

To call _nested_ methods, we use the same syntax as properties…

    Browser.drive {
        js."document.write"("dynamic!")
    }

### Executing Arbitrary Code

The `js` object also has an `exec()` method that can be used to run snippets of JavaScript. It is identical to the [JavascriptExecutor.executeScript()](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/JavascriptExecutor.html#executeScript\(java.lang.String,%20java.lang.Object...\)) method, except that it takes its arguments in the other order…

    assert js.exec(1, 2, "return arguments[0] + arguments[1];") == 3

You might be wondering why the order has been changed (i.e. the arguments go _before_ the script). It makes writing multiline JavaScript more convenient…

    js.exec 1, 2, """
        someJsMethod();
        // lots of javascript
        return true;
    """

## Waiting

Geb provides some convenient methods for _waiting_ for a certain condition to be true. This is useful for testing pages using AJAX, timers or effects.

The `waitFor` methods are provided by the [`WaitingSupport`](api/geb/waiting/WaitingSupport.html) mixin which delegates to the [`Wait` class](api/geb/waiting/Wait.html) (see the documentation of the [`waitFor` method](api/geb/waiting/Wait.html#waitFor\(Closure%3CT%3E\)) of this class for the precise semantics of _waiting_). These methods take various parameters that determine how long to wait for the given closure to return a true object according to the [Groovy Truth](http://groovy.codehaus.org/Groovy+Truth "Groovy - Groovy Truth"), and how long to wait in between invoking the closure again.

    waitFor {}          // use default configuration
    waitFor(10) {}      // wait for up to 10 seconds, using the default retry interval
    waitFor(10, 0.5) {} // wait for up to 10 seconds, waiting half a second in between retries
    waitFor("quick") {} // use the preset “quick” as the wait settings

See the section on [wait configuration](configuration.html#waiting) for how to change the default values and define presets.

> It is also possible to declare that content should be implicitly waited on, see [the `wait` option for content definition](pages.html#wait). 

### Examples

Here is an example showing one way of using `waitFor()` to deal with the situation where clicking a button invokes an AJAX request that creates a new `div` on its completion.

    import geb.*
    
    class DynamicPage extends Page {
        static content = {
            theButton { $("input", value: "Make Request") }
            theResultDiv(required: false) { $("div#result") }
        }
        
        def makeRequest() {
            theButton.click()
            waitFor { theResultDiv.present }
        }
    }

    Browser.drive {
        to DynamicPage
        makeRequest()
        assert theResultDiv.text() == "The Result"
    }

> Notice that the '`theResultDiv`' is declared `required: false`. This is almost always necessary when dealing with dynamic content as it's likely to not be present on the page when it is first accessed (see: [section on `required`](pages.html#required))

Because the browser delegates method calls to the page object, the above could have been written as…

    Browser.drive {
        $("input", value: "Make Request")
        waitFor { $("div#result").present }
        assert $("div#result").text() == "The Result"
    }

Recall that the `return` keyword is optional in Groovy, so in the example above the `$("div#result").present` statement acts as the return value for the closure and is used as the basis on whether the closure _passed_ or not. This means that you must ensure that the last statement inside the closure returns a value that is `true` according to the [Groovy Truth](http://groovy.codehaus.org/Groovy+Truth) (if you're unfamiliar with the Groovy Truth **do** read that page).

> Not using explicit `return` statements in closure expressions passed to `waitFor()` is actually preferred. See the section on [implicit assertions][implicit-assertions] for more information.

The closures given to the `waitFor` method(s) do not need to be single statement.

    waitFor {
        def a = 1
        def b = 2
        a == b
    }

That will work fine.

If you wish to *test* multiple conditions as separate statement inside a `waitFor` closure, you can just put them in separate lines.

    waitFor {
        1 == 1
        2 == 2
    }

### Custom message

If you wish to add a custom message to `WaitTimeoutException` that is being thrown when `waitFor` call times out you can do so by providing a message parameter to the `waitFor` call:

	waitFor (message: 'My custom message') { $("div#result").present }

## Alert and Confirm Dialogs

WebDriver currently [does not handle](http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Does_support_Javascript_alerts_and_prompts?) the [`alert()` and `confirm()` dialog windows](http://www.w3schools.com/JS/js_popup.asp). However, we can fake it through some JavaScript magic as [discussed on the WebDriver issue for this](http://code.google.com/p/selenium/issues/detail?id=27#c17). Geb implements a workaround based on this solution for you. Note that this feature relies on making changes to the browser's `window` DOM object so may not work on all browsers on all platforms. At the time when WebDriver adds support for this functionality the underlying implementation of the following methods will change to use that which will presumably be more robust. Geb adds this functionality through the [`AlertAndConfirmSupport`](api/geb/js/AlertAndConfirmSupport.html) class that is mixed into 
[`Page`][page-api] and 
[`Module`][module-api].

The Geb methods **prevent** the browser from actually displaying the dialog, which is a good thing. This prevents the browser blocking while the dialog is displayed and causing your test to hang indefinitely.

> Unexpected `alert()` and `confirm()` calls can have strange results. This is due to the nature of how Geb handles this internally. If you are seeing strange results, you may want to run your tests/scripts against a real browser and watch what happens to make sure there aren't `alert()`s or `confirm()`s being called that you aren't expecting. To do this, you need to disable Geb's handling by changing your code to not use the methods below.

### alert()

There are two methods that deal with `alert()` dialogs:

    String withAlert(Closure actions)
    void withNoAlert(Closure actions)

The first method, `withAlert()`, is used to verify actions that will produce an `alert()` dialog. This method returns the alert message.

Given the following HTML…

    <input type="button" name="showAlert" onclick="alert('Bang!');" />

The `withAlert()` method is used like so…

    assert withAlert { $("input", name: "showAlert").click() } == "Bang!"

If an alert dialog is not raised by the given “actions” closure, an `AssertionError` will be thrown.

The `withAlert()` method also accepts a wait option. It is useful if the code in your “actions” closure is raising a dialog in an asynchronous manner and can be used like that:

	assert withAlert(wait: true) { $("input", name: "showAlert").click() } == "Bang!"

The possible values for the `wait` option are consistent with the [ones for `wait` option of content definitions](pages.html#wait).

The second method, `withNoAlert()`, is used to verify actions that will not produce an `alert()` dialog. If an alert dialog is raised by the given “actions” closure, an `AssertionError` will be thrown.

Given the following HTML…

    <input type="button" name="dontShowAlert" />

The `withNoAlert()` method is used like so…

    withNoAlert { $("input", name: "dontShowAlert").click() }

> It's a good idea to use `withNoAlert()` when doing something that _might_ raise an alert. If you don't, the browser is going to raise a real alert dialog and sit there waiting for someone to click it which means your test is going to hang. Using `withNoAlert()` prevents this.

A side effect of the way that this is implemented is that we aren't able to definitively handle actions that cause the browser's actual page to change (e.g. clicking a link in the closure given to `withAlert()`/`withNoAlert()`). We can detect that the browser page did change, but we can't know if `alert()` did or did not get called before the page change. If a page change was detected the `withAlert()` method will return a literal `true` (whereas it would normally return the alert message), while the `withNoAlert()` will succeed.

### confirm()

There are three methods that deal with `confirm()` dialogs:

    String withConfirm(boolean ok, Closure actions)
    String withConfirm(Closure actions) // defaults 'ok' to true
    void withNoConfirm(Closure actions)

The first method, `withConfirm()` (and its ‘`ok`’ defaulted relative), is used to verify actions that will produce an `confirm()` dialog. This method returns the confirmation message. The `ok` parameter controls whether the `confirm()` call should return `true` or `false` (i.e. the user has clicked the “OK” or “Cancel” buttons).

Given the following HTML…

    <input type="button" name="showConfirm" onclick="confirm('Do you like Geb?');" />

The `withConfirm()` method is used like so…

    assert withConfirm(true) { $("input", name: "showConfirm").click() } == "Do you like Geb?"

If a confirmation dialog is not raised by the given “actions” closure, an `AssertionError` will be thrown.

The `withConfirm()` method also accepts a wait option just like the `withAlert()` method. See the [description of `withAlert()`](javascript.html#alert) to learn about the possible values and usage.

The second method, `withNoConfirm()`, is used to verify actions that will not produce an `confirm()` dialog. If a confirmation dialog is raised by the given “actions” closure, an `AssertionError` will be thrown.

Given the following HTML…

    <input type="button" name="dontShowConfirm" />

The `withNoConfirm()` method is used like so…

    withNoConfirm { $("input", name: "dontShowConfirm").click() }

> It's a good idea to use `withNoConfirm()` when doing something that _might_ raise a a confirmation. If you don't, the browser is going to raise a real confirmation dialog and sit there waiting for someone to click it, which means your test is going to hang. Using `withNoConfirm()` prevents this.

A side effect of the way that this is implemented is that we aren't able to definitively handle actions that cause the browser's actual page to change (e.g. clicking a link in the closure given to `withConfirm()`/`withNoConfirm()`). We can detect that the browser page did change, but we can't know if `confirm()` did or did not get called before the page change. If a page change was detected, the `withConfirm()` method will return a literal `true` (whereas it would normally return the alert message), while the `withNoConfirm()` will succeed.

### About prompt()

Geb does not provide any support for prompt() due to its infrequent and generally discouraged use.

## jQuery Integration

Geb has special support for the [jQuery javascript library][jquery]. Navigator objects have a special adapter that makes calling jQuery methods against the underlying DOM elements simple. This is best explained by example.

> The jQuery integration only works when the pages you are working with include jQuery, Geb does not install it in the page for you. The minimum supported version of jQuery is 1.4.

Consider the following page:

    <html>
    <head>
        <script type="text/javascript" src="/js/jquery-1.4.2.min.js"></script>
        <script type="text/javascript">
            $(function() {
                $("#a").mouseover(function() {
                   $("b").show(); 
                });
            });
        </script>
    </head>
    <body>
        <div id="a"></div>
        <div id="b" style="display:none;"><a href="http://www.gebish.org">Geb!</a></div>
    </body>
    </html>

We want to click the Geb link, but can't because it's hidden (WebDriver does not let you interact with hidden elements). The div containing the link (div “a”) is only displayed when the mouse moves over div “a”.

The jQuery library provides convenient methods for triggering browser events. We can use this to simulate the mouse being moved over the div “a”.

In straight jQuery JavaScript we would do…

    jQuery("div#a").mouseover()

Which we could invoke via Geb easy enough…

    js.exec 'jQuery("div#a").mouseover();'

That will work, but can be inconvenient as it duplicates content definitions in our Geb pages. Geb's jQuery integration allows you to use your defined content in Geb with jQuery. Here is how we could call the `mouseover` jQuery function on an element from Geb…

    $("div#a").jquery.mouseover()

To be clear, that is Groovy (not JavaScript code). It can be used with pages…

    import geb.*

    class ExamplePage extends Page {
        static content = {
            divA { $("#a") }
            divB { $("#b") }
            gebLink { divB.find("a") }
        }
    }

    Browser.drive {
        to ExamplePage
        // div b is not showing
        divA.jquery.mouseover()
        // div b is showing now
        gebLink.click()
    }

The `jquery` property of a navigator is conceptually equivalent to a jQuery object for _all_ of the navigator's matched page elements. 

The methods can also take arguments…

    $("#a").jquery.trigger('mouseover')

The same set of restricted types as allowed by WebDriver's [`executeScript()`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/JavascriptExecutor.html#executeScript\(java.lang.String,%20java.lang.Object...\)) method are permitted here.

The return value of methods called on the `jquery` property depends on what the corresponding jQuery method returns. A jQuery object will be converted to a Navigator representing the same set of elements, other values such as objects, strings and numbers are returned as per WebDriver's [`executeScript()`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/JavascriptExecutor.html#executeScript\(java.lang.String,%20java.lang.Object...\)) method.

### Why?

This functionality was developed to make triggering mouse related events easier. Some applications are very sensitive to mouse events, and triggering these events in an automated environment is a challenge. jQuery provides a good API for faking these events, which makes for a good solution.
