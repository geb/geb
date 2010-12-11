# Introduction

Geb is a library for headless web browsing on the JVM, suitable for automation and functional web testing. It utilises the dynamic language features of [Groovy](http://groovy.codehaus.org/ "Groovy - Home") to provide a concise story-like DSL for defining steps, and a concise and manageable DSL for defining page structure using the page object pattern.

## Examples

The following is a simple example using Geb without defining page objects.

    import geb.Browser

    Browser.drive("http://google.com/ncr") {
        assert title == "Google"

        // enter wikipedia into the search field
        $("input", name: "q").value("wikipedia")

        // wait for the change to results page to happen
        // (google updates the page without a new request)
        waitFor { title.endsWith("Google Search") }

        // is the first link to wikipedia?
        def firstLink = $("li.g", 0).find("a.l")
        assert firstLink.text() == "Wikipedia"

        // click the link 
        firstLink.click()

        // wait for Google's javascript to redirect 
        // us to Wikipedia
        waitFor { title == "Wikipedia" }
    }

This example navigates to Google, searches for "_wikipedia_", and verifies that the first result is indeed for Wikipedia.

The following example is the same, except that it utilises page objects.

    import geb.Browser
    import geb.Page
    import geb.Module
    
    Browser.drive(GoogleHomePage) {
        // enter wikipedia into the search field
        search.field.value("wikipedia")

        // wait for the change to results page to happen
        // (google updates the page without a new request)
        waitFor { at(GoogleResultsPage) }

        // is the first link to wikipedia?
        assert resultLink(0).text() == "Wikipedia"

        // click the link
        resultLink(0).click()

        // wait for Google's javascript to redirect us
        // to wikipedia
        waitFor { at(WikipediaPage) }
    }

    class GoogleSearchModule extends Module {
        def buttonValue
        static content = {
            field { $("input", name: "q") }
            button(to: GoogleResultsPage) { 
                $("input", value: buttonValue)
            }
        }
    }

    class GoogleHomePage extends Page {
        static url = "http://google.com/ncr"
        static at = { title == "Google" }
        static content = {
            search { module GoogleSearchModule, buttonValue: "Google Search" }
        }
    }

    class GoogleResultsPage extends Page {
        static at = { title.endsWith("Google Search") }
        static content = {
            search { module GoogleSearchModule, buttonValue: "Search" }
            results { $("li.g") }
            result { i -> results[i] }
            resultLink { i -> result(i).find("a.l") }
        }
    }

    class WikipediaPage extends Page {
        static at = { title == "Wikipedia" }
    }

This example hopefully illustrates the kind of code that you write with Geb. Each of the constructs will be discussed in detail in this manual.

## Philosophy & Motivation

Geb was born out of a desire to make writing web tests easier and less verbose. It aims to be a developer tool over a designated tester tool in that it allows and encourages the using of programming and language constructs over a restricted environment. It also uses the flexibility and dynamism of the Groovy programming language to minimise the noise and boiler plate code.

It combines the browser automation technology of [WebDriver][webdriver] with two well known concepts; the content selection/navigation API from [jQuery][jquery] and the Page Object Pattern. 

### The Page Object Pattern

The Page Object Pattern gives us a common sense way to structure web tests in a reusable and maintainable way. From the [Selenium/WebDriver wiki page on the Page Object Pattern](http://code.google.com/p/selenium/wiki/PageObjects):

> Within your web app's UI there are areas that your tests interact with. A Page Object simply models these as objects within the test code. This reduces the amount of duplicated code and means that if the UI changes, the fix need only be applied in one place.

Furthermore (from the same document):

> PageObjects can be thought of as facing in two directions simultaneously. Facing towards the developer of a test, they represent the services offered by a particular page. Facing away from the developer, they should be the only thing that has a deep knowledge of the structure of the HTML of a page (or part of a page) It's simplest to think of the methods on a Page Object as offering the "services" that a page offers rather than exposing the details and mechanics of the page. As an example, think of the inbox of any web-based email system. Amongst the services that it offers are typically the ability to compose a new email, to choose to read a single email, and to list the subject lines of the emails in the inbox. How these are implemented shouldn't matter to the test.

The Page Object Pattern is an important technique, and Geb provides first class support for modelling pages via its `Page` class and Content DSL. It also supports the concept of _modules_ which are user interface or behavioural components that can be used across many different pages. 

### The jQuery-ish Navigator API

The [jQuery][jquery] JavaScript library provides an excellent API for (among other things) selecting or targeting content on a page and traversing through and around content. Geb gets inspiration from this API. 

In Geb, content is selected through the `$` function, which returns a `Navigator` object. A `Navigator` object is in someways analogous to the `jQuery` data type in jQuery. It represents one or more targeted elements on the page.

Let's see some examples:

    // match all 'div' elements on the page
    $("div")
    
    // match the first 'div' element on the page
    $("div", 0)
    
    // match all 'div' elements with a title attribute value of 'section'
    $("div", title: "section")
    
    // match the first 'div' element with a title attribute value of 'section'
    $("div", 0, title: "section")
    
    // match all 'div' elements who have the class 'main'
    $("div.main") 

    // match the first 'div' element with the class 'main'
    $("div.main", 0) 

These methods return `Navigator` objects that can be used to further refine the content.

    // The parent of the first paragraph
    $("p", 0).parent()
    
    // All tables with a cellspacing attribute value of 0 that are nested in a paragraph
    $("p").find("table", cellspacing: '0')

This is just the beginning of what is possible with the Navigator API. See the [dedicated manual section][navigator] for more details.

### The Browser Automation Technology

Geb is built on top of [WebDriver][webdriver], which means Geb can be used to drive any [browser that is supported by WebDriver](http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Which_browsers_does_support?).

It is intended that access to the underlying WebDriver classes is not needed when using Geb. However, you always have full access to the underlying driver instance should you need to delve that deep.

For more information see the manual section on the [browser interface][browser].

## Installation & Usage

> The current version of Geb is **0.4**

To get up and running you simply need to get the `geb-core` jar (available from Maven central) and a [WebDriver][webdriver-api] implementation.

Via `@Grab`…
    
    @Grapes([
        @Grab("org.codehaus.geb:geb-core:latest.release"),
        @Grab("org.seleniumhq.selenium:selenium-firefox-driver:latest.release")
    ])
    import geb.Browser

Via Maven…

    <dependency>
      <groupId>org.codehaus.geb</groupId>
      <artifactId>geb-core</artifactId>
      <version>RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.seleniumhq.selenium</groupId>
      <artifactId>selenium-firefox-driver</artifactId>
      <version>RELEASE</version>
    </dependency>
    
Via Gradle…

    compile "org.codehaus.geb:geb-core:latest.release", "org.seleniumhq.selenium:selenium-firefox-driver:latest.release"

> Development snapshots are available via the [Codehaus snapshot repository](http://snapshots.repository.codehaus.org/)