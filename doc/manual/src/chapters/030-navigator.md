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

> There is an alias for the dollar function named “find” if a method named “$” is not to your test (a current limitation of Groovy prevents us supporting a `find()` method like `$()` though, this will be fixed in later versions).

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
    $("p", 2).text() == "c"
    $("p", 0..1)*.text() = ["a", "b"]
    $("p", 1..2)*.text() = ["b", "c"]

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
    <tr><td><code>notStartsWith</code></td><td><code>iNotStartsWith</code></td><td>Matches values that DO NOT start with the given value</td>
    <tr><td><code>notContains</code></td><td><code>iNotContains</code></td><td>Matches values that DO NOT contain the given value anywhere</td>
    <tr><td><code>notEndsWith</code></td><td><code>iNotEndsWith</code></td><td>Matches values that DO NOT end with the given value</td>
    <tr><td><code>notContainsWord</code></td><td><code>iNotContainsWord</code></td><td>Matches values that DO NOT contain the given value surrounded by either whitespace or the beginning or end of the value</td>
        
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

Navigator objects have a `find` method for finding descendants, and `filter` and `not` methods for reducing the matched content.

Consider the following html…

    <div class="a">
        <p class="b">geb</p>
    </div>
    <div class="b">goodness</div>

We can select `p.b` by…

    $("div").find(".b")
   
We can select `div.b` by…

    $("div").filter(".b")

or…

	$(".b").not("p")

We can select the `div` containing the `p` with…

	$("div").has("p")

The `find` and `filter` methods support the **exact same options as the $ function**.

These methods return a new navigator object that represents the new content.

## Traversing

Navigators also have methods for selecting content _around_ the matched content.

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
    $("p.e").prevAll() // 'p.c' & 'p.d'
    $("p.d").next() // 'p.e'
    $("p.c").nextAll() // 'p.d' & 'p.e'
    $("p.d").parent() // 'div.b'
    $("p.c").siblings() // 'p.d' & 'p.e'
    $("div.a").children() // 'div.b' & 'div.f'

Consider the following html…

    <p class="a"></p>
    <p class="b"></p>
    <p class="c"></p>
   
The following code will select `p.b` & `p.c`…

    $("p").next()

The `previous`, `prevAll`, `next`, `nextAll`, `parent`, `parents`, `closest`, `siblings` and `children` methods can also take css selectors and attribute matchers.

Using the same html, the following code will select `p.c`…

    $("p").next(".c")

Likewise, consider the following html…

    <div class="a">
        <div class="b">
            <p></p>
        </div>
    </div>

The following code will select `div.b`…

    $("p").parent(".b")

The `closest` method is a special case in that it will select the first ancestors of the current elements that match a selector. There is no no-argument version of the `closest` method. For example, this will select `div.a`…

    $("p").closest(".a")

These methods do not take indexes as they automatically select the first matching content. To select multiple elements you can use `prevAll`, `nextAll` and `parents` all of which have no-argument versions and versions that filter by a selector.

The `nextUntil`, `prevUntil` and `parentsUntil` methods return all nodes along the relevant axis _until_ the first one that matches a selector. Consider the following markup:

	<div class="a"></div>
	<div class="b"></div>
	<div class="c"></div>
	<div class="d"></div>

The following code will select `div.b` and `div.c`:

	$(".a").nextUntil(".d")

## Clicking

Navigator objects implement the `click()` method, which will send a click event to the first matched item.

There are also `click(Class)` and `click(List<Class>)` methods that are analogous to the browser object's [`page(Class)` and `page(List<Class>)` methods respectively][changing-pages]. This allow page changes to be specified at the same time as click actions.
    
For example…

    $("input.loginButton").click(LoginPage)

Would click the “`input.loginButton`” element, then effectively call `browser.page(LoginPage)`.

## Determining Visibility

Navigator objects have a `displayed` property that indicates whether the element is visible to the user or not. The `displayed` property of a navigator object that doesn't match anything is always `false`

## Accessing tag name, attributes, text and classes

The `tag()`, `text()`, `@attribute` and `classes()` methods return the requested content on the _first_ matched content.

Consider the following HTML…

    <p title="a" class="a para">a</p>
    <p title="b" class="b para">b</p>
    <p title="c" class="c para">v</p>

The following assertions are valid…

    $("p").text() == "a"
    $("p").tag() == "p"
    $("p").@title == "a"
    $("p").classes() == ["a", "para"]

To obtain information about all matched content, you use the Groovy _spread operator_…

    $("p")*.text() == ["a", "b", "c"]
    $("p")*.tag() == ["p", "p", "p"]
    $("p")*.@title == ["a", "b", "c"]
    $("p")*.classes() == [["a", "para"], ["b", "para"], ["c", "para"]]

## Accessing input values

The value of `input`, `select` and `textarea` elements can be retrieved and set with the `value` method. Calling `value()` with no arguments will return the String value of _the first_ element in the Navigator. Calling `value(value)` will set the current value of _all_ elements in the Navigator. The argument can be of any type and will be coerced to a String if necessary. The exceptions are that when setting a `checkbox` value the method expects a `boolean` and when setting a multiple `select` the method expects an array or Collection of values.

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

Select values are set by assigning the value or text of the required option. Assigned values are automatically coerced to String. For example…

	<select name="artist">
		<option value="1">Ima Robot</option>
		<option value="2">Edward Sharpe and the Magnetic Zeros</option>
		<option value="3">Alexander</option>
	</select>

We can select options with…

	$("form").artist = "1"         // first option selected by its value attribute
	$("form").artist = 2           // second option selected by its value attribute
	$("form").artist = "Ima Robot" // first option selected by its text

#### multiple select

If the select has the `multiple` attribute it is set with a array or `Collection` of values. Any options not in the values are un-selected. For example…

	<select name="genres" multiple>
		<option value="1">Alt folk</option>
		<option value="2">Chiptunes</option>
		<option value="3">Electroclash</option>
		<option value="4">G-Funk</option>
		<option value="5">Hair metal</option>
	</select>

We can select options with…

	$("form").genres = ["2", "3"]                 // second and third options selected by their value attributes
	$("form").genres = [1, 4, 5]                  // first, fourth and fifth options selected by their value attributes
	$("form").genres = ["Alt folk", "Hair metal"] // first and last options selected by their text
	$("form").genres = []                         // all options un-selected

#### checkbox

Checkboxes are checked/unchecked by setting their value to `true` or `false`.

#### radio

Radio values are set by assigning the value of the radio button that is to be selected or the label text associated with a radio button.

For example, with the following radio buttons…

	<label for="site-current">Search this site</label>
	<input type="radio" id="site-current" name="site" value="current">
	
	<label>Search Google
		<input type="radio" name="site" value="google">
	</label>

We can select the radios with…

	$("form").site = "current"          // selects the first radio by its value
	$("form").site = "Search this site" // selects the first radio by its label
	$("form").site = "Search Google"    // selects the second radio by its label

#### text inputs and textareas

The value assigned to a text input becomes the new value of its `value` attribute. Any text currently in the input is cleared.

#### appending text

Text can be appended to the current value of an text input or `textarea` using the left-shift operator. For example…

	<input name="query" value="I can has">

	$("form").query << " cheezburger?"
	assert $("form").query == "I can has cheezburger?"

## Accessing the underlying `WebElement`s

A Geb navigator object is built on top of a collection of WebDriver [WebElement][webelement-api] objects. It is possible to access the raw web elements via the following methods on navigator objects…

    WebElement firstElement()
    WebElement lastElement()
    Collection<WebElement> allElements()

## Drag and Drop

Geb does not currently offer any direct drag and drop support, but you can dig into WebDriver's drag and drop API by working with the underlying [WebElement][webelement-api] objects that underpin the Geb navigator objects. Future versions of Geb will offer a more convenient API wrapper.

The WebDriver API for this revolves around the [DefaultActionChainsGenerator](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/interactions/DefaultActionChainsGenerator.html) class. Unfortunately there is not a lot of documentation available on this class currently.
