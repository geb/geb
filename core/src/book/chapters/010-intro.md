# Introduction

Geb is a library for headless web browsing on the JVM, suitable for automation and functional web testing. It utilises the dynamic language features of [Groovy](http://groovy.codehaus.org/ "Groovy - Home") to provide a concise story-like DSL for defining steps, and a concise and manageable DSL for defining page structure using the page object pattern.

## Examples

The following is a simple example using Geb without defining page objects.

    import geb.Browser

    Browser.drive("http://google.com") {
        assert pageTitle == "Google"
        find("input").withName("q").value("wikipedia")
        find("input").withValue("Google Search").click()
        assert pageTitle.endsWith("Google Search")
        assert find("li.g").get(0).get("a.l").text() == "Wikipedia, the free encyclopedia"
    }
    

This example navigates to Google, searches for "_wikipedia_", and verifies that the first result is indeed for Wikipedia.

The following example is the same, except that it utilises page objects.

    import geb.Browser
    import geb.Page
    import geb.Module
    
    Browser.drive(GoogleHomePage) {
        search.field.value("wikipedia")
        search.button.click()
        assert at(GoogleResultsPage)
        assert resultLink(0).text() == "Wikipedia, the free encyclopedia"
    }
    
    class GoogleSearchModule extends Module {
        def buttonValue
        static content = {
            field { find("input").withName("q") }
            button(to: GoogleResultsPage) { 
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
    
## Philosophy & Motivation

TODO: talk about the guiding principles of Geb

## The Browser Technology

TODO: talk about WebDriver and why WebDriver etc.