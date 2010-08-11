# Basics

## The Browser

The entry point to Geb is the `Browser` class. A browser object drives an underlying `WebDriver` instance which drives the real or simulated browser. The `Browser` class also maintains a `Page` instance that represents the current page that the browser is at. The browser instance delegates any method calls or property assignments or accesses that it can't handle to the page instance, which significantly cuts down on boilerplate code.

v

Here are some examples of `browser` construction…

    import geb.Browser
    import org.openqa.selenium.firefox.FirefoxDriver
    
    new Browser(new FirefoxDriver(), "http://google.com")
    new Browser(new FirefoxDriver())
    new Browser("http://google.com")
    new Browser()

If a `WebClient` instance is not provided at construction, the browser will attempt to create an instance of `org.openqa.selenium.htmlunit.HtmlUnitDriver`.

If a _base url_ is not provided, all page urls must be absolute.
 
### The driver

At the core of the Geb API, is the `geb.Driver` class. A driver is responsible for managing a `geb.Geb` instance (the browser) and tracking the current page object (the programmatic model of the page the browser is at).

## The Page Object Pattern

At it's heart, Geb is a DSL for defining the content and structure of webpages and interacting with that content. This is an implementation of the _Page Object Pattern_, where each page (or kind of page) is modelled as a programmatic object. The historical alternative to the _Page Object Pattern_ has typically been to use no pattern at all, which you can do with Geb should you want to.

    import geb.Browser
    
    Browser.session("http://google.com") {
        $("input[name=q]").value = "Chuck Norris"
        $("input[value=Google Search]").click()
        // Now at the results page
        // Check that the first result contains chuck
        assert $("li.g", 0).get("a.l").text ==~ /Chuck/
    }

This is valid Geb code, and it works well for a once off script but there are two big issues with this approach. Imagine that you have _many_ tests that involve searching and checking results. The implementation of how to search and how to find the results is going to have to be duplicated in _every_ test, maybe _many_ times per test. The _Page Object Pattern_ allows us to apply the same principles of modularity, reuse and encapsulation that we use in other aspects of programming.

Here is the same script, utilising page objects.

    import geb.*
    
    class GoogleHomePage extends Page {
        static url = "http://google.com"
        static at = { title == "Google" }
        static content = {
            searchField { $("input[name=q]").value }
            searchButton(to: GoogleResultsPage) { $("input[value=Google Search]") }
        }
    }
    
    class GoogleResultsPage extends Page {
        static at = { title.endsWith("Google Search") }
        static content = {
            results { $("li.g") }
            result { index -> results[index] }
            resultLink { index -> result(index).find("a.l") }
        }
    }
    
    // Now the script
    Browser.session(GoogleHomePage) {
        searchField.value = "Chuck Norris"
        searchButton.click()
        assert at(GoogleResultsPage)
        assert resultLink(0).text ==~ /Chuck/
    }

You have now encapsulated, in a reusable fashion, information about each page and how to interact with it. As anyone who has tried to knows, maintaining a large suite of functional web tests for a changing application can become an expensive and frustrating process. A core philosophy of Geb is to address this issue through it's content definition DSL. 