# Javascript

Geb provides some support over and above what WebDriver provides. It's important to understand how WebDriver does handle Javascript, which is through a driver's implementation of [`JavascriptExecutor`](javascriptexecutor)'s [`executeScript()`](execscript) method. 

> Before reading further, it's **strongly** recommended to read the description of [`executeScript()`](execscript) in order to understand how type conversion works between the two worlds.

Remember that you can get access to the driver instance via the browser…

    assert browser.driver.executeScript("return arguments[0];", 1) == 1

This is a bit long winded, and as you would expect Geb uses the dynamism of Groovy to make life easier.

## The “js” object

The browser instance exposes a “`js`” object that provides the syntax sweetness. It is available implicitly in pages and modules.

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

## Script Errors

The [JavascriptExecutor](javascriptexecutor) interface does not define any contract in regards to the driver's responsibility when there is some issue executing Javascript. All drivers however throw _some kind_ of exception when this happens.    