# To-Do

## Navigator

* incomplete
	* click is not implemented yet
* API
    * make API WebDriver agnostic?
    * support concepts from RenderedWebElement
    * all methods that return WebElement should be package protected
    * accessing information about selection
        * .text() - text of first match
        * .classes() - Collection<String> of classes of first match
        * .name() - tag name of first match
        * .@attribute - value of attribute 'attribute' of first match
        * *.text() - collection of texts of all matches
        * *.classes() - collection of collections of class names of all matches
        * *.name() - collection of tag names of all matches
        * *.@attribute - collection of values of attribute 'attribute' of all matches
* syntax
    * replace values() methods with property access to .value
    * values() doesn't do the right thing
    * additional methods from jQuery
        * add(selector)
        * children(selector)
        * has(selector)
        * not(selector)
        * siblings()
    * support leftShift to type in field without clearing it
* enhancements
	* next, previous, parent could possibly be optimised depending on the WebDriver implementation being used
	* is() should accept selector not just tag name
	* FirefoxWebElement has no toString, so Navigator should fully implement it
* cruft
    * findBy*
    * with*
    * do we really need MatchType enum?
* groovy-ness
	* next & previous imply Navigator could implement Range
	* no-arg methods such as id() could be Groovy properties
	* withAttribute(String, String) and the like could be replaced by with(Map<String, String>) 

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
