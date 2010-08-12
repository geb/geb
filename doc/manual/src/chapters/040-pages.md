# Pages

All page classes **must** extend `geb.Page`.

> before reading this page, please make sure you have read the [section on the Browser.drive() method][drive]

## The Page Object Pattern - why?

    import geb.Browser
    
    Browser.drive("http://google.com") {
        $("input[name=q]").value() = "Chuck Norris"
        $("input[value=Google Search]").click()
        assert $("li.g", 0).get("a.l").text() ==~ /Chuck/
    }

This is valid Geb code, and it works well for a once off script but there are two big issues with this approach. Imagine that you have _many_ tests that involve searching and checking results. The implementation of how to search and how to find the results is going to have to be duplicated in _every_ test, maybe _many_ times per test. The _Page Object Pattern_ allows us to apply the same principles of modularity, reuse and encapsulation that we use in other aspects of programming.

Here is the same script, utilising page objects…

    import geb.*
    
    class GoogleHomePage extends Page {
        static url = "http://google.com"
        static at = { title == "Google" }
        static content = {
            searchField { $("input[name=q]").value() }
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
    Browser.drive(GoogleHomePage) {
        searchField.value = "Chuck Norris"
        searchButton.click()
        assert at(GoogleResultsPage)
        assert resultLink(0).text() ==~ /Chuck/
    }

You have now encapsulated, in a reusable fashion, information about each page and how to interact with it. As anyone who has tried to knows, maintaining a large suite of functional web tests for a changing application can become an expensive and frustrating process. A core philosophy of Geb is to address this issue through it's content definition DSL.

## The Content DSL

Geb features a DSL for defining page content in a *templated* fashion, which allows very concise yet flexible page definitions. Pages define a `static` closure property called `content` that describes the page content. 

Consider the following HTML…

    <div id="a">a</div>

We could define this content as so…

    class ExamplePage {
        static content = {
            theDiv { $('div', id: 'a') }
        }
    }

The structure to the content DSL is…

    «name» { «definition» }

Where `«definition»` is Groovy code that is evaluated against the instance of the page.

Here is how it could be used…

    Browser.drive {
        to ExamplePage
        assert theDiv.text() == "a"
    }

So how is this working? First, remember that the `Browser` instance delegates any method calls or property accesses that it doesn't know about to the current page instance. So the above code is the same as…

    Browser.drive {
        to ExamplePage
        assert page.theDiv.text() == "a"
    }

Secondly, defined content becomes available as properties and methods on instance of the page…

    Browser.drive {
        to ExamplePage
        
        // Following two lines are equivalent
        assert theDiv.text() == "a"
        assert theDiv().text() == "a"
    }

The Content DSL actually defines content _templates_. This is best illustrated by example…

    class ExamplePage {
        static content = {
            theDiv { id -> $('div', id: id) }
        }
    }
    
    Browser.drive {
        to ExamplePage
        assert theDiv("a").text() == "a"
    }

There are no restrictions on what arguments can be passed to content templates.

A content template can return _anything_. Typically they will return a `Navigator` object through the use of the $ function, but it can be anything.

    class ExamplePage {
        static content = {
            theDivText { $('div', id: a).text() }
        }
    }
    
    Browser.drive {
        to ExamplePage
        assert theDivText == "a"
    }

