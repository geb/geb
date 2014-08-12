# Introduction

Geb is a developer focused tool for automating the interaction between web browsers and web content. It uses the dynamic language features of [Groovy][groovy] to provide a powerful content definition DSL (for modelling content for reuse) and key concepts from [jQuery][jQuery] to provide a powerful content inspection and traveral API (for finding and interacting with content).

Geb was born out of a desire to make browser automation (originally for web testing) easier and more productive. It aims to be a **developer tool** in that it allows and encourages the using of programming and language constructs instead of creating a restricted environment. It uses Groovy's dynamism to remove the noise and boiler plate code in order to focus on what's important — the content and interaction.

## The Browser Automation Technology

Geb builds on the [WebDriver][webdriver] browser automation library, which means that Geb can work with [any browser that WebDriver can](http://code.google.com/p/selenium/wiki/FrequentlyAskedQuestions#Q:_Which_browsers_does_support?). While Geb provides an extra layer of convenience and productivity, it is always possible to “drop down” to the WebDriver level to do something directly should you need to.

For more information see the manual section on [using a driver implementation](driver.html).

## The Page Object Pattern

The Page Object Pattern gives us a common sense way to model content in a reusable and maintainable way. From the [WebDriver wiki page on the Page Object Pattern](http://code.google.com/p/selenium/wiki/PageObjects):

> Within your web app's UI there are areas that your tests interact with. A Page Object simply models these as objects within the test code. This reduces the amount of duplicated code and means that if the UI changes, the fix need only be applied in one place.

Furthermore (from the same document):

> PageObjects can be thought of as facing in two directions simultaneously. Facing towards the developer of a test, they represent the services offered by a particular page. Facing away from the developer, they should be the only thing that has a deep knowledge of the structure of the HTML of a page (or part of a page) It's simplest to think of the methods on a Page Object as offering the "services" that a page offers rather than exposing the details and mechanics of the page. As an example, think of the inbox of any web-based email system. Amongst the services that it offers are typically the ability to compose a new email, to choose to read a single email, and to list the subject lines of the emails in the inbox. How these are implemented shouldn't matter to the test.

The Page Object Pattern is an important technique, and Geb provides first class support via its [page](pages.html) and [module](modules.html) constructs.

## The jQuery-ish Navigator API

The [jQuery][jquery] JavaScript library provides an excellent API for (among other things) selecting or targeting content on a page and traversing through and around content. Geb takes a lot of inspiration from this. 

In Geb, content is selected through the `$` function, which returns a [`Navigator`][navigator-api] object. A [`Navigator`][navigator-api] object is in someways analogous to the `jQuery` data type in jQuery in that it represents one or more targeted elements on the page.

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

This is just the beginning of what is possible with the Navigator API. See the [chapter on the navigator][navigator] for more details.

## Full Examples

Let's have a look at a simple case of wanting to search for “wikipedia” on Google and follow the first result returned.

### Inline Scripting

Here's an example of using Geb in an inline (i.e. no page objects or predefined content) scripting style…

    import geb.Browser

    Browser.drive {
        go "http://google.com/ncr"
        
        // make sure we actually got to the page
        assert title == "Google"

        // enter wikipedia into the search field
        $("input", name: "q").value("wikipedia")

        // wait for the change to results page to happen
        // (google updates the page dynamically without a new request)
        waitFor { title.endsWith("Google Search") }

        // is the first link to wikipedia?
        def firstLink = $("li.g", 0).find("a.l")
        assert firstLink.text() == "Wikipedia"

        // click the link 
        firstLink.click()

        // wait for Google's javascript to redirect to Wikipedia
        waitFor { title == "Wikipedia" }
    }

### Scripting with Page Objects

This time let us define our content up front using the Page Object pattern…

    import geb.Browser
    import geb.Page
    import geb.Module
    
    // modules are reusable fragments that can be used across pages that can be parameterised
    // here we are using a module to model the search function on the home and results pages
    class GoogleSearchModule extends Module {
        
        // a parameterised value set when the module is included
        def buttonValue
        
        // the content DSL
        static content = {

            // name the search input control “field”, defining it with the jQuery like navigator
            field { $("input", name: "q") }
            
            // the search button declares that it takes us to the results page, and uses the 
            // parameterised buttonValue to define itself
            button(to: GoogleResultsPage) { 
                $("input", value: buttonValue)
            }
        }
    }

    class GoogleHomePage extends Page {

        // pages can define their location, either absolutely or relative to a base
        static url = "http://google.com/ncr"
        
        // “at checkers” allow verifying that the browser is at the expected page
        static at = { title == "Google" }
        
        static content = {
            // include the previously defined module
            search { module GoogleSearchModule, buttonValue: "Google Search" }
        }
    }

    class GoogleResultsPage extends Page {
        static at = { title.endsWith "Google Search" }
        static content = {
            // reuse our previously defined module
            search { module GoogleSearchModule, buttonValue: "Search" }
            
            // content definitions can compose and build from other definitions
            results { $("li.g") }
            result { i -> results[i] }
            resultLink { i -> result(i).find("a.l") }
            firstResultLink { resultLink(0) }
        }
    }

    class WikipediaPage extends Page {
        static at = { title == "Wikipedia" }
    }
    
Now our script again, using the above defined content…

    Browser.drive {
        to GoogleHomePage
        assert at(GoogleHomePage)
        search.field.value("wikipedia")
        waitFor { at GoogleResultsPage }
        assert firstResultLink.text() == "Wikipedia"
        firstResultLink.click()
        waitFor { at WikipediaPage }
    }

### Testing

Geb itself does not include any kind of testing or execution framework. Rather, it works with existing popular tools like [Spock][spock], [JUnit][junit], [TestNG][testng] and [Cucumber][cucumber-jvm]. While Geb works well with all of these test tools, we encourage the use of [Spock][spock] as it's a great match for Geb with its focus and style.

Here is our Google case again, this time use Geb's [Spock][spock] integration…

    import geb.spock.GebSpec
    
    class GoogleWikipediaSpec extends GebSpec {
        
        def "first result for wikipedia search should be wikipedia"() {
            given:
            to GoogleHomePage
            
            expect:
            at GoogleHomePage
            
            when:
            search.field.value("wikipedia")
            
            then:
            waitFor { at GoogleResultsPage }
            
            and:
            firstResultLink.text() == "Wikipedia"
            
            when:
            firstResultLink.click()
            
            then:
            waitFor { at WikipediaPage }
        }
    }

For more information on using Geb for web and functional testing, see the [testing chapter](testing.html).

## Installation & Usage

Geb itself is a available as a single [`geb-core` jar from the central Maven repository](http://mvnrepository.com/artifact/@geb-group@/geb-core). To get up and running you simply need this jar, a WebDriver driver implementation and the `selenium-support` jar.

Via `@Grab`…
    
    @Grapes([
        @Grab("@geb-group@:geb-core:@geb-version@"),
        @Grab("org.seleniumhq.selenium:selenium-firefox-driver:@selenium-version@"),
        @Grab("org.seleniumhq.selenium:selenium-support:@selenium-version@")
    ])
    import geb.Browser

Via Maven…

    <dependency>
      <groupId>@geb-group@</groupId>
      <artifactId>geb-core</artifactId>
      <version>@geb-version@</version>
    </dependency>
    <dependency>
      <groupId>org.seleniumhq.selenium</groupId>
      <artifactId>selenium-firefox-driver</artifactId>
      <version>@selenium-version@</version>
    </dependency>
    <dependency>
      <groupId>org.seleniumhq.selenium</groupId>
      <artifactId>selenium-support</artifactId>
      <version>@selenium-version@</version>
    </dependency>
    
Via Gradle…

    compile "@geb-group@:geb-core:@geb-version@", "org.seleniumhq.selenium:selenium-firefox-driver:@selenium-version@", "org.seleniumhq.selenium:selenium-support:@selenium-version@"

Alternatively, if using an integration such as `geb-spock` or `geb-junit` you can depend on that instead of `geb-core`.

> Be sure to check the chapter on [build integrations](build-integrations.html) for information on using Geb with particular environments, like [Grails][grails].
