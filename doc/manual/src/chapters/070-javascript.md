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