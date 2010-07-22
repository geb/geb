Geb is a Groovy DSL for HTMLUnit.

It is best explained by example…

    package geb.example

    import geb.Driver
    import geb.example.pages.*

    // Without page objects
    Driver.drive("http://google.com") {
        assert pageTitle == "Google"
        find("input").withName("q").value("wikipedia")
        find("input").withValue("Google Search").click()
        assert pageTitle.endsWith("Google Search")
        assert find("li.g").get(0).get("a.l").text() == "Wikipedia, the free encyclopedia"
    }

    // With page objects
    Driver.drive(GoogleHomePage) {
        search.field.value("wikipedia")
        search.button.click()
        assert at(GoogleResultsPage)
        assert resultLink(0).text() == "Wikipedia, the free encyclopedia"
    }

Page objects look like…

    package geb.example.pages

    import geb.*

    // Pages contain content and modules
    class GoogleHomePage extends Page {

        // can define a URL for going straight to the page
        static url = "http://google.com"

        // can define a custom check to verify the page content matches expectations
        static at = { page.titleText == "Google" }

        // can include parameterised modules
        static content = {
            search { module GoogleSearchModule, buttonValue: "Google Search" }
        }
    }
    
And modules look like…

    package geb.example.pages

    import geb.*

    // Modules are content that is independent of a particular page
    class GoogleSearchModule extends Module {

        // Modules can be parameterised
        def buttonValue

        // content is defined using a DSL with a Jquery like finding API
        static content = {
            field { find("input").withName("q") }

            // content can define which page is next when it is clicked
            button(to: GoogleResultsPage) { 
                // can use instance variables in content locators
                find("input").withValue(buttonValue) 
            }
        }

        // instance methods refer to content by name
        def search(term) {
            // Jquery like API for setting input values and clicking
            field.value(term)
            button.click()
        }
    }
    
## Integrations

* [Spock](http://github.com/alkemist/geb-spock "alkemist's geb-spock at master - GitHub")
* [Grails](http://github.com/alkemist/grails-geb-spock "alkemist's grails-geb-spock at master - GitHub") 
