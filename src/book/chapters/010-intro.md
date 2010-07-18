# Introduction

Geb is a library for headless web browsing on the JVM, suitable for automation and functional web testing. It utilises the dynamic language features of [Groovy](http://groovy.codehaus.org/ "Groovy - Home") to provide a concise story-like DSL for defining steps, and a concise and manageable DSL for defining page structure using the page object pattern.

## Examples

The following is a simple example using Geb without defining page objects.

    import geb.Driver

    Driver.drive("http://google.com") {
        assert pageTitle == "Google"
        find("input").withName("q").value("wikipedia")
        find("input").withValue("Google Search").click()
        assert pageTitle.endsWith("Google Search")
        assert find("li.g").get(0).get("a.l").text() == "Wikipedia, the free encyclopedia"
    }
    

This example navigates to Google, searches for "_wikipedia_", and verifies that the first result is indeed for Wikipedia.

The following example is the same, except that it utilises page objects.

    import geb.Driver
    import geb.Page
    import geb.Module
    
    Driver.drive(GoogleHomePage) {
        search.field.value("wikipedia")
        search.button.click()
        assert at(GoogleResultsPage)
        assert resultLink(0).text() == "Wikipedia, the free encyclopedia"
    }
    
    class GoogleSearchModule extends Module {
        def buttonValue
        static content = {
            field { find("input").withName("q") }
            button(toPage: GoogleResultsPage) { 
                find("input").withValue(buttonValue)
            }
        }
    }
    
    class GoogleHomePage extends Page {
        static url = "http://google.com"
        static at = { page.titleText == "Google" }
        static content = {
            search { module GoogleSearchModule, buttonValue: "Google Search" }
        }
    }
    
    class GoogleResultsPage extends Page {
        static url = "http://www.google.com/search"
        static at = { page.titleText.endsWith("Google Search") }
        static content = {
            search { module GoogleSearchModule, buttonValue: "Search" }
            results { find("li.g") }
            result { results.get(it) }
            resultLink { result(it).get("a.l") }
        }
    }
    
## How it works

Geb is an integration of [Groovy][groovy], [HTMLUnit][htmlunit] (the underlying browser technology) and the [Hue/Doj][hue] library (a [JQuery][jquery] like API for traversing and manipulating pages in HTMLUnit). Typically, the only API you need to know and use is the API for [Doj][dojapi] objects which are the building blocks of pages.