# Basics

This is a discussion of the fundamental aspects of Geb.

## Components 

### The geb

The `geb.Geb` class provides the interface to [HTMLUnit][htmlunit] and acts as the browser for the driver. It is responsible for making requests and providing access to things like cookies and request/response headers.

A geb instance can be constructed with two optional parameters; a [HTMLUnit WebClient][webclient], and a _base url_ (as a string) to resolve the initial request url against.

Here are some examples of `geb` construction…

    import geb.Geb
    
    def geb = new Geb(new WebClient(), "http://google.com")
    def geb = new Geb(new WebClient())
    def geb = new Geb("http://google.com")
    def geb = new Geb()

If a `WebClient` instance is not provided at construction, an instance will be created using the no-arg constructor of `WebClient`.

If a _base url_ is not provided, `null` will be used.
 
### The driver

At the core of the Geb API, is the `geb.Driver` class. A driver is responsible for managing a `geb.Geb` instance (the browser) and tracking the current page object (the programmatic model of the page the browser is at).

### The page



## Pageless

The geb driver can be used without the page object aspect. This may be more convenient for one off scripting or prototyping. However, the use of page objects is highly recommended in all but the most simple of cases.

