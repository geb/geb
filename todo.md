# To-Do

## Navigator

* incomplete
	* click is not implemented yet
* API
    * make API WebDriver agnostic?
    * support concepts from RenderedWebElement
* syntax
    * replace with* methods with filter(String) and filter(Map)
    * remove getBy* (make non-public as used internally)
    * values() doesn't do the right thing
    * additional methods from jQuery
        * add(selector)
        * children(selector)
        * has(selector)
        * not(selector)
        * siblings()
* enhancements
	* next, previous, parent could possibly be optimised depending on the WebDriver implementation being used
	* is() should accept selector not just tag name
	* get could use native CSS support in non-HTMLUnit drivers
	* FirefoxWebElement has no toString, so Navigator should fully implement it
* cruft
    * do we really need MatchType enum?
* groovy-ness
	* next & previous imply Navigator could implement Range
	* no-arg methods such as id() could be Groovy properties
	* withAttribute(String, String) and the like could be replaced by with(Map<String, String>) 
