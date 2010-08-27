# Javascript, AJAX and Dynamic Pages

This section discusses how to deal with some of the challenges in testing and/or automating modern web applications.

## The “js” object

The browser instance exposes a “`js`” object that provides support for working with Javascript over and above what WebDriver provides. It's important to understand how WebDriver does handle Javascript, which is through a driver's implementation of [`JavascriptExecutor`](javascriptexecutor)'s [`executeScript()`](execscript) method. 

> Before reading further, it's **strongly** recommended to read the description of [`executeScript()`](execscript) in order to understand how type conversion works between the two worlds.

You can execute Javascript like you would with straight WebDriver using the driver instance via the browser…

    assert browser.driver.executeScript("return arguments[0];", 1) == 1

This is a bit long winded, and as you would expect Geb uses the dynamism of Groovy to make life easier.

> The [JavascriptExecutor](javascriptexecutor) interface does not define any contract in regards to the driver's responsibility when there is some issue executing Javascript. All drivers however throw _some kind_ of exception when this happens.

### Accessing Variables

Any _global_ javascript variables inside the browser can be read as _properties_ of the `js` object.

Given the following page…

    <html>
        <script type="text/javascript">
            var aVariable = 1;
        </script>
    <body>
    </body>
    </html>

We could access the javascript variable “`aVariable`” with…

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

Any _global_ javascript functions can be called as methods on the `js` object.

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

The `js` object also has an `exec()` method that can be used to run snippets of Javascript. It is identical to the [JavascriptExecutor.executeScript()](execscript) method, except that it takes its arguments in the other order…

    assert js.exec(1, 2, "return arguments[0] + arguments[1];") == 3

You might be wondering why the order has been changed (i.e. the arguments go _before_ the script). It makes writing multiline javascript more convenient…

    js.exec 1, 2, """
        someJsMethod();
        // lots of javascript
        return true;
    """

## Waiting

Geb provides some convenient methods for _waiting_ for a certain condition to be true. This is useful for testing pages using AJAX or Timers.

There are three methods:

    def waitFor(Closure condition)
    def waitFor(Double timeoutSeconds, Closure condition)
    def waitFor(Double timeoutSeconds, Double intervalSeconds, Closure condition)

These methods all do the same thing, except that they used default values for parameters that are not part of their signature. **They are all available on browsers, pages and modules**.

The `condition` parameter is a closure that is periodically (executed until it either **returns a true value** (according to the Groovy Truth) or a timeout is reached.

The `timeoutSeconds` (default is `5`) parameter defines the number of seconds to wait for the condition to become true. Note that this value is an approximation, it's used in conjuction with the `intervalSeconds` value to determine how many times the condition should be tested rather than doing any actual timing. Non whole numbers can be used for this value (e.g. `2.5`)

The `intervalSeconds` (default is `0.5`) parameter defines the number of seconds to wait after testing the condition to test it again if it did not pass. Non whole numbers can be used for this value (e.g. `2.5`). If this value is higher than the given `timeoutSeconds`, the condition will be tested once initially, then once again just before the timeout would occur.

### Examples

Here is an example showing one way of using `waitFor()` to deal with the situation where clicking a button invokes an AJAX request that creates a new `div` on it's completion.

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

> Notice that the '`theResultDiv`' is declared `required: false`. This is almost always necessary when dealing with dynamic content as it's likely to not be present on the page when it is first accessed (see: [section on required](required))

Because the browser instance also implements the `waitFor()` method, the above could have been written as…

    Browser.drive {
        $("input", value: "Make Request")
        waitFor { $("div#result").present }
        assert $("div#result").text() == "The Result"
    }

It's generally preferable to put the waiting behind a method on the page or module so that it's reusable across tests.

## Alert and Confirm Dialogs

WebDriver currently [does not handle](http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Does_support_Javascript_alerts_and_prompts?) the [`alert()` and `confirm()` dialog windows](http://www.w3schools.com/JS/js_popup.asp). However, we can fake it through some Javascript magic as [discussed on the WebDriver issue for this](http://code.google.com/p/selenium/issues/detail?id=27#c17). Geb implements a workaround based on this solution for you. Note that this feature relies on making changes to the browser's `window` DOM object so may not work on all browsers on all platforms. At the time when WebDriver adds support for this functionality the underlying implementation of the following methods will change to use that which will presumably be more robust.

The Geb methods **prevent** the browser from actually displaying the dialog, which is a good thing. This prevents the browser blocking while the dialog is displayed and causing your test to hang indefinitely.

> Unexpected `alert()` and `confirm()` calls can have strange results. This is due to the nature of how Geb handles this internally. If you are seeing strange results, you may want to run your tests/scripts against a real browser and watch what happens to make sure there aren't `alert()`'s or `confirm()`'s being called that you aren't expecting. To do this, you need to disable Geb's handling by changing your code to not use the methods below.

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

The second method, `withNoAlert()`, is used to verify actions that will not produce an `alert()` dialog. If an alert dialog is raised by the given “actions” closure, an `AssertionError` will be thrown.

Given the following HTML…

    <input type="button" name="dontShowAlert" />

The `withNoAlert()` method is used like so…

    withNoAlert { $("input", name: "dontShowAlert").click() }

> It's a good idea to use `withNoAlert()` when doing something that _might_ raise an alert. If you don't, the browser is going to raise a real alert dialog and sit there waiting for someone to click it which means your test is going to hang. Using `withNoAlert()` prevents this.

### confirm()

There are three methods that deal with `confirm()` dialogs:

    String withConfirm(boolean ok, Closure actions)
    String withConfirm(Closure actions) // defaults 'ok' to true
    void withNoConfirm(Closure actions)

The first method, `withConfirm()` (and it's ‘`ok`’ defaulted relative), is used to verify actions that will produce an `confirm()` dialog. This method returns the confirmation message. The `ok` parameter controls whether the `confirm()` call should return `true` or `false` (i.e. the user has clicked the “OK” or “Cancel” buttons).

Given the following HTML…

    <input type="button" name="showConfirm" onclick="confirm('Do you like Geb?');" />

The `withConfirm()` method is used like so…

    assert withConfirm(true) { $("input", name: "showConfirm").click() } == "Do you like Geb?"

If a confirmation dialog is not raised by the given “actions” closure, an `AssertionError` will be thrown.

The second method, `withNoConfirm()`, is used to verify actions that will not produce an `confirm()` dialog. If a confirmation dialog is raised by the given “actions” closure, an `AssertionError` will be thrown.

Given the following HTML…

    <input type="button" name="dontShowConfirm" />

The `withNoConfirm()` method is used like so…

    withNoConfirm { $("input", name: "dontShowConfirm").click() }

> It's a good idea to use `withNoConfirm()` when doing something that _might_ raise a a confirmation. If you don't, the browser is going to raise a real confirmation dialog and sit there waiting for someone to click it which means your test is going to hang. Using `withNoConfirm()` prevents this.

### About prompt()

Geb does not provide any support for prompt() due to it's infrequent and generally discouraged use.