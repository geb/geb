# Interacting with content

Geb provides a concise and Groovy interface to the content and controls in your browser. This is implemented through the `Navigator` API which is a jQuery inspired mechanism for finding, filtering and interacting with DOM elements.

## The $ Function

A navigator for the _content context_ (more on this later) can always be obtained by the $ function…

    $("div")

This returns a navigator that represents all of the `div` elements in the current context. 

You can use any CSS selector that the underlying `WebDriver` supports…

    $("div.some-class p:first[title='something']")

There is also a more programmatic API for selecting the _N_th match and matching attribute values…

    $("div p", 0, title: "something")

The following are also valid…

    $("div p", 0)
    $("div p", title: "something")
    $(0)
    $(title: "something")
    
## Navigator Objects

TODO: section on the nature of navigator objects

    * Represent arbitrary content
    * Iterable
    * multi vs. single content
    * other?

## Finding & Filtering

Navigator objects have a `find` method for finding descendants, and a `filter` method for reducing the matched content.

Consider the following html…

    <div class="a">
        <p class="b">geb</p>
    </div>
    <div class="b">goodness</div>

We can select `p.b` by…

    $("div").find(".b")
    
We can select `div.b` by…

    $("div").filter(".b")

The `find` and `filter` methods support the same kinds of options as the $ function. That is, an `index` and/or `attribute matchers`.

These methods return a new navigator object that represents the new content.

## Traversing

Navigator's also have methods for selecting content _around_ the matched content.

Consider the following html…

    <div class="a">
        <div class="b">
            <p class="c"></p>
            <p class="d"></p>
            <p class="e"></p>
        </div>
    </div>

You can select content _around_ `p.d` by…

    $("p.d").previous() // 'p.c'
    $("p.d").next() // 'p.e'
    $("p.d").parent() // 'div.b'

These methods are different to `find` & `filter` in that they operate on the _first_ matched content in the navigator.

Consider the following html…

    <p class="a"></p>
    <p class="b"></p>
    <p class="c"></p>
    
The following code will select `p.b`…

    $("p").next()

While the initial `$("p")` matched 3 elements, the `next()` method only operates on the _first_ match.
    
The `previous`, `next` and `parent` methods can also take css selectors and attribute matchers.

Using the same html, the following code will select `p.c`…

    $("p").next(".c")

Likewise, consider the following html…

    <div class="a">
        <div class="b">
            <p></p>
        </div>
    </div>

The following code will select `div.a`…

    $("p").parent(".a")

As would…

    $("p").parent(class: "a")

These methods do not take indexes as they automatically select the first matching content.

## Accessing attributes and text


