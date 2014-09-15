# Pages

> Before reading this page, please make sure you have read the [section on the Browser.drive() method][drive]

## The Page Object pattern

    import geb.Browser
    
    Browser.drive {
        go "http://google.com/ncr"
        $("input[name=q]").value "Chuck Norris"
        $("input[value='Google Search']").click()
        waitFor { $("li.g", 0).find("a.l").text().contains("Chuck") }
    }

This is valid Geb code, and it works well for a one off script but there are two big issues with this approach. Imagine that you have _many_ tests that involve searching and checking results. The implementation of how to search and how to find the results is going to have to be duplicated in _every_ test, maybe _many times_ per test. As soon as something as trivial as the name of the search field changes you have to update a lot of code. The Page Object Pattern allows us to apply the same principles of modularity, reuse and encapsulation that we use in other aspects of programming to avoid such issues in browser automation code.

Here is the same script, utilising page objects…

    import geb.*

    class GoogleHomePage extends Page {
        static url = "http://google.com/?complete=0"
        static at = { title == "Google" }
        static content = {
            searchField { $("input[name=q]") }
            searchButton(to: GoogleResultsPage) { $("input[value='Google Search']") }
        }

        void search(String searchTerm) {
            searchField.value searchTerm
            searchButton.click()
        }
    }

    class GoogleResultsPage extends Page {
        static at = { waitFor { title.endsWith("Google Search") } }
        static content = {
            results(wait: true) { $("li.g") }
            result { index -> results[index] }
            resultLink { index -> result(index).find("a.l") }
        }
    }

    // Now the script
    Browser.drive {
        to GoogleHomePage
        search "Chuck Norris"
        at GoogleResultsPage
        resultLink(0).text().contains("Chuck")
    }

You have now encapsulated, in a reusable fashion, information about each page and how to interact with it. As anyone who has tried to knows, maintaining a large suite of functional web tests for a changing application can become an expensive and frustrating process. Geb's support for the Page Object pattern addresses this problem.

## The Page superclass

All page objects **must** inherit from [`Page`][page-api].

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

A content template can return _anything_. Typically they will return a [`Navigator`][navigator-api] object through the use of the `$()` function, but it can be anything.

    class ExamplePage extends Page {
        static content = {
            theDivText { $('div#a').text() }
        }
    }
    
    Browser.drive {
        to ExamplePage
        assert theDivText == "a"
    }

It's important to realise that `«definition»` code is evaluated against the page instance. This allows code like the following…

    class ExamplePage extends Page {
        static content = {
            theDiv { $('div#a') }
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

The `required` option controls whether or not the content returned by the definition has to exist or not. This is only relevant when the definition returns a `Navigator` object (via the `$()` function), it is ignored if the definition returns anything else.

If the `required` option is set to `true` and the returned content does not exist, a [`RequiredPageContentNotPresent`](api/geb/error/RequiredPageContentNotPresent.html) exception will be thrown.

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

    class HelpPage extends Page {}
    
    Browser.drive {
        to ExamplePage
        helpLink.click()
        assert at(HelpPage)
    }

The `to` value will be implicitly used as an argument to the content's `click()` method, effectively setting the new page type and verifying its at checker. See the section on [clicking content][clicking] for how this changes the browser's page object.

The list variant can also be used…

    static content = {
        loginButton(to: [LoginSuccessfulPage, LoginFailedPage]) { $("input.loginButton") }
    }

Which, on click, sets the browser's page to be the first page in the list whose at checker returns true. This is equivalent to the [`page(Class[] potentialPageTypes)` browser method](api/geb/Browser.html#page\(Class%3C%3F%20extends%20Page%3E\)) which is explained in the section on
[changing pages][changing-pages].

All of the page classes passed in when using the list variant have to have an “at” checker defined otherwise an `UndefinedAtCheckerException` will be thrown.

#### wait

Default value: `false`

Allowed values:

* **`true`** - wait for the content using the _default wait_ configuration
* **a string** - wait for the content using the _wait preset_ with this name from the configuration
* **a number** - wait for the content for this many seconds, using the _default retry interval_ from the configuration
* **a 2 element list of numbers** - wait for the content using element 0 as the timeout seconds value, and element 1 as the retry interval seconds value

Any other value will be interpreted as `false`.

The `wait` option allows Geb to wait an amount of time for content to appear on the page, instead of throwing a [`RequiredPageContentNotPresent`](api/geb/error/RequiredPageContentNotPresent.html) exception if the content is not present when requested.

    class DynamicPage extends Page {
        static content = {
            dynamicallyAdded(wait: true) { $("p.dynamic") }
        }
    }
    
    Browser.drive {
        to DynamicPage
        assert dynamicallyAdded.text() == "I'm here now"
    }

This is equivalent to:

    class DynamicPage extends Page {
        static content = {
            dynamicallyAdded(required: false) { $("p.dynamic") }
        }
    }
    
    Browser.drive {
        to DynamicPage
        assert waitFor { dynamicallyAdded }.text() == "I'm here now"
    }

See the [section on waiting](javascript.html#waiting) for the semantics of the `waitFor()` method, that is used here internally. Like `waitFor()` a [`WaitTimeoutException`](api/geb/waiting/WaitTimeoutException.html) will be thrown if the wait timeout expires.

It is also possible to use `wait` when defining non-element content, such as a string or number. Geb will wait until the content definition returns a value that conforms to the Groovy Truth.

    class DynamicPage extends Page {
        static content = {
            status { $("p.status") }
            successStatus(wait: true) { status.text().contains("Success") }
        }
    }
    
    Browser.drive {
        to DynamicPage
        assert successStatus
    }

In this case, we are inherently waiting for the `status` content to be on the page and for it to contain the string “Success”. If the `status` element is not present when we request `successStatus`, the [`RequiredPageContentNotPresent`](api/geb/error/RequiredPageContentNotPresent.html) exception that would be thrown is swallowed and Geb will try again after the retry interval has expired.

You can modify the behaviour of content with `wait` option set to true if you use it together with `required` option set to false. Given a content definition:

    static content = {
        dynamicallyAdded(wait: true, required: false) { $("p.dynamic") }
    }

Then if wait timeout expires when retrieving `dynamicallyAdded`, there will be no `WaitTimeoutException` thrown, and the last closure evaluation value will be returned. If there is an exception thrown during closure evaluation, it will be wrapped in an [`UnknownWaitForEvaluationResult`](api/geb/waiting/UnknownWaitForEvaluationResult.html) instance and returned.

Waiting content blocks are subject to “implicit assertions”. See the section on [implicit assertions][implicit-assertions] for more information.

#### page

Default value: `null`

The `page` option allows the definition of a page the browser will be set to if the content describes a frame and is used in a `withFrame()` call.

Given the following HTML...

    <html>
        <body>
            <frame id="frame-id" src="frame.html"></frame>
        <body>
    </html>

...and the code for frame.html...

    <html>
        <body>
            <span>frame text</span>
        </body>
    </html>

...the following will pass...

    class PageWithFrame extends Page {
        static content = {
            myFrame(page: FrameDescribingPage) { $('#frame-id') }
        }
    }

    class FrameDescribingPage extends Page {
        static content = {
            frameContentsText { $('span').text() }
        }
    }

    to PageWithFrame
    withFrame(myFrame) {
        assert frameContentsText == 'frame text'
    }

### Aliasing

If you wish to have the same content definitions available under different names you can create a content definition that specifies `aliases` parameter:

	class AliasingPage extends Page {
		static content = {
			someButton { $("button", text: "foo") }
			someButtonByAnotherName(aliases: someButton)
		}
	}

	Browser.drive {
        to AliasingPage
        assert someButton.text() == someButtonByAnotherName.text()
    }

Remember that the aliased content has to be defined before the aliasing content, otherwise you will get a [`InvalidPageContent`](api/geb/error/InvalidPageContent.html) exception.

## “At” Verification

Each page can define a way to check whether the underlying browser is at the page that the page class actually represents. This is done via a `static` `at` closure…

    class ExamplePage extends Page {
        static at = { $("h1").text() == "Example" }
    }

This closure can either return a `false` value or throw an `AssertionError` (via the `assert` method). The `verifyAt()` method call will either return true or throw an `AssertionError` even if there are no explicit assertions in the “at” checker.

    Browser.drive {
        to ExamplePage
        verifyAt()
    }

The `verifyAt()` method is used by the browser `at()` method which also returns true or throws an `AssertionError` even if there are no explicit assertions in the “at” checker…

    Browser.drive {
        to ExamplePage
        at(ExamplePage)
    }

At checkers are subject to “implicit assertions”. See the section on [implicit assertions][implicit-assertions] for more information.

If you don't wish to get an exception when “at” checking fails there are methods that return `false` in that case: [`Page#verifyAtSafely()`](api/geb/Page.html#verifyAtSafely\(boolean\)) and [`Browser#isAt(Class<? extends Page>)`](api/geb/Browser.html#isAt\(Class%3C%3F%20extends%20Page%3E,%20boolean\)).

As mentioned previously, when a content template defines a “to” option of more than one page the page's `verifyAt()` method is used to determine which one of the pages to use. In this situation, any `AssertionError`s thrown by at checkers are suppressed.

The “at” checker is evaluated against the page instance, and can access defined content or any other variables or methods…

    class ExamplePage extends Page {
        static at = { heading == "Example" }
        static content = {
            heading { $("h1").text() }
        }
    }

If a page does not have an “at” checker, the `verifyAt()` method will throw an `UndefinedAtCheckerException`. The same will happen if any of the pages in a list passed to content template “to” option doesn't define an “at” checker.

It can sometimes prove useful to wrap at verification in `waitFor` calls by default - some drivers are known to return control after URL change before the page is fully loaded in some circumstances or before one might consider it to be loaded. This can be configured via [`atCheckWaiting`](configuration.html#waiting_in_at_checkers) option.

### Unexpected pages

A list of unexpected pages can be provided via [`unexpectedPages` configuration option](configuration.html#unexpected_pages).

> Note that this feature does not operate on HTTP response codes as these are not exposed by WebDriver thus Geb does not have access to them. To use this feature your application has to render custom error pages that can be modeled as `Page` classes and detected by an `at` checker.

If configured, the classes from the `unexpectedPages` list will be checked for first when ”at“ checking is performed for any page, and an `UnexpectedPageException` with an appropriate message will be raised if any of them is encountered.

Given that your application renders a custom error page when a page is not found and a 404 HTTP response code is returned with a text like "Sorry but we could not find that page", you can model that page with a class:

    class PageNotFoundPage extends Page {

        static at = { $('#errorMessage').text() == 'Sorry but we could not find that page' }
    }

Then register that page in configuration:

    unexpectedPages = [PageNotFoundPage]

When checking if the browser is at a page...

    at ExpectedPage

..but the `at` checker for `PageNotFoundPage` matches, an `UnexpectedPageException` will be raised with the following message: "An unexpected page PageNotFoundPage was encountered when expected to be at ExpectedPage".

Unexpected pages will be checked for whenever ”at“ checking is performed, even implicitly like when using `to` content template option or passing one or many `Page` classes to `Navigator`'s `click()` method.

Finally, you can still explicitly check if the browser is at an unexpected page if you need to. Following will pass without throwing an `UnexpectedPageException` if ”at“ checking for `PageNotFoundPage` succeeds:

    at PageNotFoundPage

## Page URLs

Pages can define URLs via the `static` `url` property.

    class ExamplePage extends Page {
        static url = "examples"
    }

The url is used when using the browser `to()` method.

    Browser.drive {
        go "http://myapp.com/"
        to ExamplePage
    }

See the section on [the base url](browser.html#the_base_url) for notes about URLs and slashes.

## Advanced Page Navigation

Page classes can customise how they generate URLs when used in conjunction with the browser `to()` method. 

Consider the following example…

    import geb.*
    
    class ExamplePage extends Page {
        static url = "example"
    }

    Browser.drive("http://myapp.com/") {
        to ExamplePage
    }

This will result in a request being made to “`http://myapp.com/example`”. 

The `to()` method can also take arguments…

    Browser.drive("http://myapp.com") {
        to ExamplePage, 1, 2
    }

This will result in a request being made to “`http://myapp.com/example/1/2`”. This is because by default, any arguments passed to the `to()` method after the page class are converted to a URL path by calling `toString()` on each argument and joining them with “`/`”. 

However, this is extensible. You can specify how a set of arguments is converted to a URL path to be added to the page URL. This is done by overriding the [`convertToPath()`](api/geb/Page.html#convertToPath\(java.lang.Object\)) method.
The [`Page`][page-api] implementation of this method looks like this…

    String convertToPath(Object[] args) {
        args ? '/' + args*.toString().join('/') : ""
    }

You can either overwrite this catchall method to control path conversion for all invocations or provide an overloaded version for a specific type signature. Consider the following…

    class Person {
        Long id
        String name
    }
    
    class PersonPage {
        static url = "person"
        
        String convertToPath(Person person) {
            person.id.toString()
        }
    }
    
    def newPerson = new Person(id: 5, name: "Bruce")
    
    Browser.drive {
        go "http://myapp.com/"
        to PersonPage, newPerson
    }

This will result in a request to “`http://myapp.com/person/5`”.

### Named params

Any type of argument can be used with the `to()` method, **except** named parameters (i.e. a `Map`). Named parameters are **always** interpreted as query parameters. Using the classes from the above example…

    Browser.driver {
        go "http://myapp.com/"
        to PersonPage, newPerson, flag: true
    }

This will result in a request to “`http://myapp.com/person/5?flag=true`”. The query parameters are **not** sent to the [`convertToPath()`](api/geb/Page.html#convertToPath\(java.lang.Object\)) method.


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
        void onLoad(Page previousPage) {
            // do some stuff with the previous page
        }
    }

### onUnload(Page newPage)

The `onUnload()` method is called with next page object instance when the page is being replaced as the page object for the browser.

    import geb.*
    
    class SomePage extends Page {
        void onUnload(Page newPage) {
            // do some stuff with the new page
        }
    }

## Dealing with frames

Frames might seem a thing of the past, but if you're accessing or testing some legacy application with Geb, you might still need to deal with them. Thankfully, Geb makes working with them groovier thanks to the `withFrame()` method which is available on Browser, Page and Module.

### Executing code in the context of a frame

There are multiple flavours of the `withFrame()` method, but for all of them the closure parameter is executed in the context of a frame specified by the first parameter, and after the execution the browser page is restored to what it was before the call:

* `withFrame(String, Closure)` - String parameter contains the name or id of a frame element
* `withFrame(int, Closure)` - int parameter contains the index of the frame element, that is, if a page has three frames, the first frame would be at index “0”, the second at index “1” and the third at index “2”
* `withFrame(Navigator, Closure)` - Navigator parameter should point to a frame element
* `withFrame(SimplePageContent, Closure)` - SimplePageContent should contain a frame element

Given the following HTML...

    <html>
        <body>
            <frame name="header" src="frame.html"></frame>
            <frame id="footer" src="frame.html"></frame>
            <iframe id="inline" src="frame.html"></iframe>
            <span>main</span>
        <body>
    </html>

...and the code for frame.html...

    <html>
        <body>
            <span>frame text</span>
        </body>
    </html>

...then this code will pass...

    static content = {
        footerFrame { $('#footer') }
    }

    withFrame('header') { assert $('span') == 'frame text' }
    withFrame('footer') { assert $('span') == 'frame text' }
    withFrame(0) { assert $('span') == 'frame text' }
    withFrame($('#footer')) { assert $('span') == 'frame text' }
    withFrame(footerFrame) { assert $('span') == 'frame text' }

    assert $('span') == 'main'
    
If a frame cannot be found for a given first argument of the `withFrame()` call, then [`NoSuchFrameException`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/NoSuchFrameException.html) is thrown.

### Switching pages and frames at once

All of the aforementioned `withFrame()` variants also accept an optional second argument (a page class) which allows to switch page for the execution of the closure passed as the last parameter.

Following shows an example usage:

    to PageWithFrames
    //browser.page set to a PageWithFrames instance

    withFrame('frame-name', PageDescribingFrameContents) {
        //browser.page set to a PageDescribingFrameContents instance
    }

    //browser.page set back to the PageWithFrames instance

It is also possible to [specify a page to switch to for a page content that describes a frame][page-option].

