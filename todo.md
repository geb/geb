# To-Do

## Navigator

* API
    * make API WebDriver agnostic?
    * support concepts from RenderedWebElement
    * all methods that return WebElement should be package protected
* syntax
    * additional methods from jQuery
		* html()? (not sure if this is possible)
		* css(propName) - delegate to RenderedRemoteElement.getValueOfCssProperty and handle separately in HtmlUnit
		* val() and val(value)
		* width() and height() using RenderedRemoteElement.getSize()
		* offset() using RenderedRemoteElement.getLocation
		* get() and toArray()
		* .length
* enhancements
	* scrollTo() - no op on HtmlUnit I guess otherwise RenderedRemoteElement.getLocationOnScreenOnceScrolledIntoView
	* next, previous, parent could possibly be optimised depending on the WebDriver implementation being used
	* FirefoxWebElement has no toString, so Navigator should fully implement it
* Browser driver methods should accept param maps
* DriveExceptions aren't very informative
* Work out what extra methods NavigableSupport needs (e.g. $(0..2))

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
