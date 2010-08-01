# To-Do

## Navigator

* API
    * make API WebDriver agnostic?
    * support concepts from RenderedWebElement
    * all methods that return WebElement should be package protected
* syntax
    * additional methods from jQuery
        * add(selector)
        * has(selector)
        * not(selector)
* enhancements
	* next, previous, parent could possibly be optimised depending on the WebDriver implementation being used
	* is() should accept selector not just tag name
	* FirefoxWebElement has no toString, so Navigator should fully implement it
* Browser driver methods should accept param maps
* DriveExceptions aren't very informative

## Input Handling/Shortcuts:

Find

    $("div").firstName() // methodMissing
    $("div").find("input", name: "firstName")
    
Read

    $("div").firstName // propertyMissing(name)
    $("div").find("input", name: "firstName").value()
    
Write

    $("div").firstName = "abc" // propertyMissing(name, value)
    $("div").find("input", name: "firstName").value("abc")
    
Append

    $("div").firstName() << "abc" // is just methodMissing() from above, but Navigator implements leftShift()
    $("div").find("input", name: "firstName") << "abc"
