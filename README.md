Geb is a Groovy DSL for [Selenium 2 (i.e. WebDriver)](http://code.google.com/p/selenium/).

It is best explained by example…

    import geb.Browser
    import org.openqa.selenium.firefox.FirefoxDriver

    // Without page objects
    Browser.drive(new FirefoxDriver(), "http://google.com") {
        assert title == "Google"
        $("input", name: "q").value("wikipedia")
        $("input", value: "Google Search").click()
        assert title.endsWith("Google Search")
        assert $("li.g", 0).find("a.l").text() == "Wikipedia, the free encyclopedia"
    }

    // With page objects
    Browser.drive(new FirefoxDriver(), GoogleHomePage) {
        search.field.value("wikipedia")
        search.button.click()
        assert at(GoogleResultsPage)
        assert resultLink(0).text() == "Wikipedia, the free encyclopedia"
    }

Page objects look like…

    import geb.*

    // Pages contain content and modules
    class GoogleHomePage extends Page {

        // can define a URL for going straight to the page
        static url = "http://google.com"

        // can define a custom check to verify the page content matches expectations
        static at = { title == "Google" }

        // can include parameterised modules
        static content = {
            search { module GoogleSearchModule, buttonValue: "Google Search" }
        }
    }
    
And modules look like…

    import geb.*

    // Modules are content that is independent of a particular page
    class GoogleSearchModule extends Module {

        // Modules can be parameterised
        def buttonValue

        // content is defined using a DSL with a Jquery like finding API
        static content = {
            field { $("input", name: "q") }

            // content can define which page is next when it is clicked
            button(to: GoogleResultsPage) { 
                // can use instance variables in content locators
                $("input", value: buttonValue)
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
