# To-Do

## Navigator

* incomplete
	* click is not implemented yet
* API
    * make API WebDriver agnostic?
    * support concepts from RenderedWebElement
    * all methods that return WebElement should be package protected
* syntax
    * attribute access via .@
    * text access via .text
    * .tagName
    * replace values() methods with property access to .value
    * class names as Collection<String>
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
