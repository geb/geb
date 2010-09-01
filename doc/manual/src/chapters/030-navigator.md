# Interacting with content

Geb provides a concise and Groovy interface to the content and controls in your browser. This is implemented through the `Navigator` API which is a jQuery inspired mechanism for finding, filtering and interacting with DOM elements.

## The $ Function

The $ function is the access point to the browser's page content. This returns a `geb.navigator.Navigator` object that is roughly analogous to a jQuery object. It is analogous in that it represents one or more elements on the page and can be used to refine the matched content or query the matched content. When a $ function is called that does not match any content, an “empty” navigator object is returned that represents no content. Operations on “empty” navigators return `null` or another “empty” navigator or other values that make sense (e.g. the `size()` method returns `0`).

The signature of the $ function is as follows…

    $(«css selector», «index or range», «attribute / text matchers»)

The following is a concrete example…

    $("h1", 2, class: "heading")

This would find the 3rd (elements are 0 indexed) `h1` element whose `class` *attribute* is exactly “`heading`”.

All arguments are optional, meaning the following calls are all valid:

    $("div p", 0)
    $("div p", title: "something")
    $(0)
    $(title: "something")

### CSS Selectors

You can use any CSS selector that the underlying `WebDriver` supports…

    $("div.some-class p:first[title='something']")

In the case of the HTMLUnit driver, which does not support CSS selectors at all, only basic CSS 2 type selectors can be used. A future version of the HTMLUnit driver may gain better CSS selector support.

### Indexes and Ranges

When matching, a single positive integer or integer range can be given to restrict by index.

Consider the following html…

    <p>a</p>
    <p>b</p>
    <p>c</p>

We can use indexes to match content like so…

    $("p", 0).text() == "a"
    $("p", 2).text() == "b"
    $("p", 0..1)*.text() = ["a", "b"]
    $("p", 1..2)*.text() = ["c", "d"]

See below for an explanation of the `text()` method and the use of the spread operator.

### Attribute and Text Matching

Matches can be made on attributes and node text values via Groovy's named parameter syntax. The value `text` is treated specially as a match against the node's text. All other values are matched against their corresponding attribute values.

Consider the following html…

    <p attr1="a" attr2="b">p1</p>
    <p attr1="a" attr2="c">p2</p>

We can use attribute matchers like so…

    $("p", attr1: "a").size() == 2
    $("p", attr2: "c").size() == 1

Attribute values are `and`ed together…

    $("p", attr1: "a", attr2: "b").size() == 1

We can use text matchers like so…

    $("p", text: "p1").size() == 1

You can mix attribute and text matchers…

    $("p", text: "p1", attr1: "a").size() == 1

#### Using Patterns

To match the entire value of an attribute or the text you use a `String` value. It is also possible to use a `Pattern` to do regexp matching…

    $("p", text: ~/p./).size() == 2

Geb also ships with a bunch of shortcut pattern methods…

    $("p", text: startsWith("p")).size() == 2
    $("p", text: endsWith("2")).size() == 1
    
The following is the complete listing:

<table class="graybox" border="0" cellspacing="0" cellpadding="5">
    <tr><th>Case Sensitive</th><th>Case Insensitive</th><th>Description</th></tr>
    <tr><td><code>startsWith</code></td><td><code>iStartsWith</code></td><td>Matches values that start with the given value</td>
    <tr><td><code>contains</code></td><td><code>iContains</code></td><td>Matches values that contain the given value anywhere</td>
    <tr><td><code>endsWith</code></td><td><code>iEndsWith</code></td><td>Matches values that end with the given value</td>
    <tr><td><code>containsWord</code></td><td><code>iContainsWord</code></td><td>Matches values that contain the given value surrounded by either whitespace or the beginning or end of the value</td>
</table>

All of these methods themselves can take a `String` or a `Pattern`…

    $("p", text: contains(~/\d/)).size() == 2

> You might be wondering how this magic works, i.e. where these methods come from and where they can be used. They are methods that are available on `geb.Page` and other _places_ where you can use the $ function. They simply just return patterns.

### Navigators are Iterable

The navigator objects implement the Java `Iterable` interface, which allows you to do lots of Groovy stuff like use the `max()` function…

    <p>1</p>
    <p>2</p>

    $("p").max { it.text() }.text() == "2"

This also means that navigator objects work with the Groovy spread operator…

    $("p")*.text().max() == "2"

When treating a navigator as `Iterable`, the iterated over content is always the exact matched elements (as opposed to including children).

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

The `find` and `filter` methods support the **exact same options as the $ function**.

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
        <div class="f"></div>
    </div>

You can select content _around_ `p.d` by…

    $("p.d").previous() // 'p.c'
    $("p.d").next() // 'p.e'
    $("p.d").parent() // 'div.b'
    $("p.c").siblings() // 'p.d' & 'p.e'
    $("div.a").children() // 'div.b' & 'div.f'

These methods are different to `find` & `filter` in that they operate on the _first_ matched content in the navigator.

Consider the following html…

    <p class="a"></p>
    <p class="b"></p>
    <p class="c"></p>
    
The following code will select `p.b`…

    $("p").next()

While the initial `$("p")` matched 3 elements, the `next()` method only operates on the _first_ match.
    
The `previous`, `next`, `parent`, `siblings` and `children` methods can also take css selectors and attribute matchers.

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

## Clicking

Navigator objects implement the `click()` method, which will send a click event to the first matched item.

## Determining Visibility

Navigator objects have a `displayed` property that indicates whether the element is visible to the user or not. The `displayed` property of a navigator object that doesn't match anything is always `false`

## Accessing tag name, attributes, text and classes

The `name()`, `text()`, `@attribute` and `classes()` methods return the requested content on the _first_ matched content.

Consider the following HTML…

    <p title="a" class="a para">a</p>
    <p title="b" class="b para">b</p>
    <p title="c" class="c para">v</p>

The following assertions are valid…

    $("p").text() == "a"
    $("p").name() == "p"
    $("p").@title == "a"
    $("p").classes() == ["a", "para"]

To obtain information about all matched content, you use the Groovy _spread operator_…

    $("p")*.text() == ["a", "b", "c"]
    $("p")*.name() == ["p", "p", "p"]
    $("p")*.@title == ["a", "b", "c"]
    $("p")*.classes() == [["a", "para"], ["b", "para"], ["c", "para"]]

## Form Control Shortcuts

Interacting with form controls (`input`, `select` etc.) is such a common task in web functional testing that Geb provides convenient shortcuts for common functions.

Geb supports the following shortcuts for dealing with form controls.

Consider the following HTML…

    <form>
        <input type="text" name="geb" value="testing" />
    </form>

The value can be read and written via property notation…

    $("form").geb == "testing"
    $("form").geb = "goodness"
    $("form").geb == "goodness"

These are literally shortcuts for…

    $("form").find("input", name: "geb").value() == "testing"
    $("form").find("input", name: "geb").value("goodness")
    $("form").find("input", name: "geb").value() == "goodness"

There is also a shortcut for obtaining a navigator based on a control name

    $("form").geb()

Which is literally a shortcut for…

    $("form").find("input", name: "geb")

### Setting Values

#### select

If the select is multiple select enabled, it is set with an array of strings which is to be the values which are to be selected.

#### checkbox

Checkboxes are checked/unchecked by setting their value to `true` or `false`.

#### radio

Radio values are set by assigning the value of the radio button that is to be selected

#### input

The value assigned to a text input becomes the new value of it's `value` attribute.

#### textarea

The value assigned to a text input becomes the textarea's value.