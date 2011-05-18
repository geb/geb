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

This is valid Geb code, and it works well for a one off script but there are two big issues with this approach. Imagine that you have _many_ tests that involve searching and checking results. The implementation of how to search and how to find the results is going to have to be duplicated in _every_ test, maybe _many_ times per test. The _Page Object Pattern_ allows us to apply the same principles of modularity, reuse and encapsulation that we use in other aspects of programming.

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

You have now encapsulated, in a reusable fashion, information about each page and how to interact with it. As anyone who has tried to knows, maintaining a large suite of functional web tests for a changing application can become an expensive and frustrating process. A core philosophy of Geb is to address this issue through its content definition DSL.

## The Content DSL

Geb features a DSL for defining page content in a *templated* fashion, which allows very concise yet flexible page definitions. Pages define a `static` closure property called `content` that describes the page content. 

Consider the following HTML…

    <div id="a">a</div>

We could define this content as so…

    class ExamplePage extends Page {
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

    class ExamplePage extends Page {
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

    class ExamplePage extends Page {
        static content = {
            theDivText { $('div', id: a).text() }
        }
    }
    
    Browser.drive {
        to ExamplePage
        assert theDivText == "a"
    }

It's important to realise that `«definition»` code is evaluated against the page instance. This allows code like the following…

    class ExamplePage extends Page {
        static content = {
            theDiv { $('div', id: a) }
            theDivText { theDiv.text() }
        }
    }

And this is not restricted to other content…

    class ExamplePage extends Page {
        def divId = a
        static content = {
            theDiv { $('div', id: divId) }
            theDivText { theDiv.text() }
        }
    }

Or…

    class ExamplePage extends Page {
        static content = {
            theDiv { $('div', id: getDivId()) }
            theDivText { theDiv.text() }
        }
        def getDivId() {
            "a"
        }
    }

### Template Options

Template definitions can take different options. The syntax is…

    «name»(«options map») { «definition» }

For example…

    theDiv(cache: false, required: false) { $("div", id: "a") }

The following are the available options.

#### required

Default value: `true`

The `required` option controls whether or not the content returned by the definition has to exist or not. This is only relevant when the definition returns a `Navigator` object (via the $ function), it is ignored if the definition returns anything else.

If the `required` option is set to `true` and the returned content does not exist, a `geb.error.RequiredPageContentNotPresent` exception will be thrown.

    class ExamplePage extends Page {
        static content = {
            theDiv { $('div', id: "b") }
        }
    }
    
    Browser.drive {
        to ExamplePage
        def thrown = false
        try {
            theDiv
        } catch (RequiredPageContentNotPresent e) {
            thrown = true
        }
        assert thrown
    }

#### cache

Default value: `false`

The `cache` option controls whether or not the definition is evaluated each time the content is requested (the content is cached for each unique set of parameters). 

    class ExamplePage extends Page {
        def value = 1
        static content = {
            theValue(cache: true) { value }
        }
    }
    
    Browser.drive {
        to ExamplePage
        assert theValue == 1
        value = 2
        assert theValue == 1
    }

With caching disabled…

    class ExamplePage extends Page {
        def value = 1
        static content = {
            theValue(cache: false) { value }
        }
    }
    
    Browser.drive {
        to ExamplePage
        assert theValue == 1
        value = 2
        assert theValue == 2
    }

Caching is a performance optimisation and is disabled by default. You may want to enable if you notice that the a particular content definition is taking a long time to resolve.

#### to

Default value: `null`

The `to` option allows the definition of which page the browser will be sent to if the content is clicked.

    class ExamplePage extends Page {
        static content = {
            helpLink(to: HelpPage) { $("a", text: "Help") }
        }
    }

    class HelpPage extends Page {
        
    }
    
    Browser.drive {
        to ExamplePage
        helpLink.click()
        assert at(HelpPage)
    }

The `to` value will be implictly used as an argument to the content's `click()` method, effectively setting the new page type. See the section on [clicking content][clicking] for how this changes the browser's page object.

The list variant can also be used…

    static content = {
        loginButton(to: [LoginSuccessfulPage, LoginFailedPage]) { $("input.loginButton") }
    }

Which on click sets the brower's page to be the first page in the list whose at checker returns true. This is equivalent to the `page(List<Class>)` method which is explained in the section on [changing pages][changing-pages].

## “At” Verification

Each page can define a way to check whether the underling browser is at the page that the page class actually represents. This is done via a `static` `at` closure…

    class ExamplePage extends Page {
        static at = { $("h1").text() == "Example" }
    }

This closure can either return a `false` value or throw an `AssertionError` (e.g. via the `assert` method).

    Browser.drive {
        to ExamplePage
        assert verifyAt()
    }

The `verifyAt()` method is used by the browser `at()` method…

    Browser.drive {
        to ExamplePage
        assert at(ExamplePage)
    }

> If using Groovy 1.7, the use of `assert` in “at” checkers is recommended because you get the benefit of Groovy's expressive assert output.

As mentioned previously, when a content template defines a “to” option of more than one page the page's `verifyAt()` method is used to determine which one of the pages to use. In this situation, any `AssertionError`s thrown by at checkers are suppressed.

The “at” checker is evaluated against the page instance, and can access defined content or any other variables or methods…

    class ExamplePage extends Page {
        static at = { heading == "Example" }
        static content = {
            heading { $("h1").text() }
        }
    }

If a page does not verify an “at” checker, the `verifyAt()` method will always return `true`.

## Page URLs

Pages can define URLs via the `static` `url` property.

    class ExamplePage extends Page {
        static url = "examples"
    }

The url is used when using the browser `to()` method.

    Browser.drive("http://myapp.com") {
        to ExamplePage
    }

When URLs are resolved against base URLs, it is done in the same manner as web browsers resolve URLs. Given a base of “`http://myapp.com/path`” and a another URL to resolve against it of “`/examples`”, the resultant URL will be “`http://myapp.com/example`”. The leading “`/`” on the non base URL causes it to be treated as an absolute path from the server base. This means that in almost all cases your page URLs **should not** start with a leading “`/`”.

See the section on [Advanced Page Navigation][page-navigation] for more information on the `to()` method.

## Inheritance

Pages can be arranged in an inheritance hierarchy. The content definitions are merged…

    class ExamplePage extends Page {
        static content = {
            heading { $("h1") }
        }
    }
    
    class SpecialExamplePage extends ExamplePage {
        static content = {
            footer { $("div.footer") }
        }
    }
    
    Browser.drive {
        to SpecialExamplePage
        assert heading.text() == "Special Example"
        assert footer.text() == "This is the footer"
    }

If a subclass defines a content template with the same name as a content template defined in a superclass, the subclass version replaces the version from the superclass.

## Lifecycle Hooks

Page classes can optionally implement methods that are called when the page is set as the browser's current page and when it is swapped out for another page. This can be used to transfer state between pages.

### onLoad(Page previousPage)

The `onLoad()` method is called with previous page object instance when the page becomes the new page object for a browser.

    import geb.*
    
    class SomePage extends Page {
        def onLoad(Page previousPage) {
            // do some stuff with the previous page
        }
    }

### onUnload(Page newPage)

The `onUnload()` method is called with next page object instance when the page is being replaced as the page object for the browser.

    import geb.*
    
    class SomePage extends Page {
        def onUnload(Page newPage) {
            // do some stuff with the new page
        }
    }

## This and That

* The browser that the page is connected to is available via the `browser` property.
* The title of the page is available via the `title` property.