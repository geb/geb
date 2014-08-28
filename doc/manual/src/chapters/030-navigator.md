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

> There is an alias for the dollar function named “find” if a method named “$” is not to your taste (a current limitation of Groovy prevents us supporting a `find()` method like `$()` though, this will be fixed in later versions).

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

Consider the following HTML…

    <div class="a">
        <p class="b">geb</p>
    </div>
    <div class="b">
        <input type="text"/>
    </div>

We can select `p.b` by…

    $("div").find(".b")
   
We can select `div.b` by…

    $("div").filter(".b")

or…

    $(".b").not("p")

We can select the `div` containing the `p` with…

    $("div").has("p")

Or select the `div` containing the `input` with a type attribute of "text" like so…

    $("div").has("input", type: "text")

The `find` and method supports the **exact same argument types as the $ function**.

The `filter`, `not` and `has` methods have the same signatures - they accept: a selector string, a predicates map or both.

These methods return a new navigator object that represents the new content.

## Traversing

Navigators also have methods for selecting content _around_ the matched content.

Consider the following HTML…

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

Consider the following HTML…

    <p class="a"></p>
    <p class="b"></p>
    <p class="c"></p>
   
The following code will select `p.b` & `p.c`…

    $("p").next()

The `previous`, `prevAll`, `next`, `nextAll`, `parent`, `parents`, `closest`, `siblings` and `children` methods can also take CSS selectors and attribute matchers.

Using the same html, the following examples will select `p.c`…

    $("p").next(".c")
    $("p").next(class: "c")
    $("p").next("p", class: "c")

Likewise, consider the following HTML…

    <div class="a">
        <div class="b">
            <p></p>
        </div>
    </div>

The following examples will select `div.b`…

    $("p").parent(".b")
    $("p").parent(class: "b")
    $("p").parent("div", class: "b")

The `closest` method is a special case in that it will select the first ancestors of the current elements that match a selector. There is no no-argument version of the `closest` method. For example, these will select `div.a`…

    $("p").closest(".a")
    $("p").closest(class: "a")
    $("p").closest("div", class: "a")

These methods do not take indexes as they automatically select the first matching content. To select multiple elements you can use `prevAll`, `nextAll` and `parents` all of which have no-argument versions and versions that filter by a selector.

The `nextUntil`, `prevUntil` and `parentsUntil` methods return all nodes along the relevant axis _until_ the first one that matches a selector or attributes. Consider the following markup:

    <div class="a"></div>
    <div class="b"></div>
    <div class="c"></div>
    <div class="d"></div>

The following examples will select `div.b` and `div.c`:

    $(".a").nextUntil(".d")
    $(".a").nextUntil(class: "d")
    $(".a").nextUntil("div", class: "d")

## Composition

It is also possible to compose navigator objects from other navigator objects, for situations where you can't express a content set in one query. To do this, simply call the $ function with the navigators to use…

    $($("div.a"), $("div.d"))

This will return a new navigator object that represents only the `a` and `d` divs.

You can compose navigator objects from content. So given a page content definition:

	static content = {
		divElement { divClass -> $('p', 'class': divClass) }
	}

And a call:

	$(divElement('a'), divElement('d'))

You will get a navigator that contains the same elements as the one above.

## Clicking

Navigator objects implement the `click()` method, which will instruct the browser to click **only the first item** the navigator has matched.

There are also `click(Class)` and `click(List<Class>)` methods that are analogous to the browser object's [`page(Class)` and `page(List<Class>)` methods respectively][changing-pages]. This allow page changes to be specified at the same time as click actions.
    
For example…

    $("input.loginButton").click(LoginPage)

Would click the “`input.loginButton`” element, then effectively call `browser.page(LoginPage)` and verify that the browser is at the expected page.

All of the page classes passed in when using the list variant have to have an “at” checker defined, otherwise an `UndefinedAtCheckerException` will be thrown.

## Determining Visibility

Navigator objects have a `displayed` property that indicates whether the element is visible to the user or not. The `displayed` property of a navigator object that doesn't match anything is always `false`

## Size and Location

You can obtain the size and location of content on the page. All units are in pixels. The size is available via the `height` and `width` properties, while the location is available as the `x` and `y` properties which represent the distance from the top left of the page (or parent frame) to the top left point of the content.

All of these properties operate on the **first** matched element only.

    $("div").height == 20
    $("div").width == 40
    $("div").x == 60
    $("div").y == 80

To obtain any of the properties for all matched elements, you can use the Groovy spread operator.

    $("div")*.height == [20, 30]
    $("div")*.width == [40, 50]
    $("div")*.x == [60, 70]
    $("div")*.y == [80, 90]

## Accessing tag name, attributes, text and classes

The `tag()`, `text()`, `@attribute` and `classes()` methods return the requested content on the _first_ matched content. The `classes()` method returns a `java.util.List` of unique class names sorted alphabetically.

Consider the following HTML…

    <p title="a" class="a para">a</p>
    <p title="b" class="b para">b</p>
    <p title="c" class="c para">c</p>

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

## Css properties

Css properties of a navigator can be accessed using the `css()` method.

Consider the following HTML…

    <div style="float: left">text</div>

You can obtain value of the `float` css property in the following way…

    $("div").css("float") == "left"

> There are some limitations when it comes to retrieving css properties of `Navigator` objects. Color values should be returned as rgba strings, so, for example if the `background-color` property is set as `green` in the HTML source, the returned value will be `rgba(0, 255, 0, 1)`. Note that shorthand CSS properties (e.g. `background`, `font`, `border`, `border-top`, `margin`, `margin-top`, `padding`, `padding-top`, `list-style`, `outline`, `pause`, `cue`) are not returned, in accordance with the DOM CSS2 specification - you should directly access the longhand properties (e.g. `background-color`) to access the desired values.

## Sending keystrokes

Keystrokes can be sent to any content via the leftShift operator, which is a shortcut for the [`sendKeys()`](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/WebElement.html#sendKeys\(java.lang.CharSequence...\)) method of WebDriver.

    $("div") << "abc"

How content responds to the keystrokes depends on what the content is.

### Non characters (e.g. delete key)

It is possible to send non-textual characters to content by using the WebDriver [Keys](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/Keys.html "Keys") enumeration…

    import org.openqa.selenium.Keys
    
    $("input", name: "firstName") << Keys.chord(Keys.CONTROL, "c")

Here we are sending a “control-c” to an input.

See the documentation for [Keys](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/Keys.html "Keys") for more information on the possible keys.

## Accessing input values

The value of `input`, `select` and `textarea` elements can be retrieved and set with the `value` method. Calling `value()` with no arguments will return the String value of _the first_ element in the Navigator. Calling `value(value)` will set the current value of _all_ elements in the Navigator. The argument can be of any type and will be coerced to a `String` if necessary. The exceptions are that when setting a `checkbox` value the method expects a `boolean` (or, an existing checkbox value) and when setting a multiple `select` the method expects an array or Collection of values.

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

> In the above and below examples with form controls we are using code like `$("form").someInput` where we could be using just `someInput` as long as there is only one control with the *name* `someInput` on the page. In the examples we are using `$("form").someInput` to hopefully be clearer.

If your content definition (either a page or a module) describes content which is an `input`, `select` or `textarea`, you can access and set its value the same way as described above for forms. Given a page and module definitions for the above mentioned HTML:

    class ShortcutModule extends Module {
        static content = {
            geb { $('form').geb() }
        }
    }

    static content = {
        geb { $('form').geb() }
        shortcutModule { module ShortcutModule }
    }

The following will pass:

    assert geb == "testing"
    geb = "goodness"
    assert geb == "goodness"

As well as:

    assert shortcutModule.geb == "testing"
    shortcutModule.geb = "goodness"
    assert shortcutModule.geb == "goodness"

> The following examples describe usage of form controls only using code like `$("form").someInput`. Given a content definition `myContent { $("form").someInput }`, you can substitute `$("form").someInput` in the examples with `myContent`.

### Setting Values

> Trying to set a value on an element which is not one of `input`, `select` or `textarea` will cause an `UnableToSetElementException` to be thrown.

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

If you attempt to set a select to a value that does not match the value or text of any options, an `IllegalArgumentException` will be thrown.

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

If the collection being assigned contains a value that does not match the value or text of any options, an `IllegalArgumentException` will be thrown.

#### checkbox

Checkboxes are generally checked/unchecked by setting their value to `true` or `false`.

You can also select a checkbox by explicitly setting its `value`. This is useful when you have a number of checkboxes with the same name, i.e.

    <input type="checkbox" name="pet" value="dogs" />
    <input type="checkbox" name="pet" value="cats" />

You can select dogs as your pet type, as follows:

    $("checkbox", name: "pet").value("dogs")

Calling `value()` on a checked checkbox will return the value of its `value` attribute, i.e:

    <input type="checkbox" name="pet" value="dogs" checked="checked"/>

    assert $("checkbox", name: "pet").value() == "dogs"

Calling `value()` on an unchecked checkbox will return `false`, i.e:

    <input type="checkbox" name="pet" value="dogs"/>

    assert $("checkbox", name: "pet").value() == false

In general you should use [Groovy Truth][groovy-truth] when checking if a checkbox is checked:

    if ($("checkbox", name: "pet").value()) {
        //evaluated only if "pet" checkbox is checked
    }

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

In the case of a text `input`, the assigned value becomes the input's *value* attribute and for a `textarea` effectively becomes the text.

It is also possible to append text by using the send keys shorthand…

    <input name="language" value="gro" />
    
    $("form").language() << "ovy"
    assert $("form").language == "groovy"

Which an also be used for non-character keys…

    <input name="postcode" />

    import org.openqa.selenium.Keys
    
    $("form").postcode = "12345"
    $("form").postcode() << Keys.BACK_SPACE
    assert $("form").postcode == "1234"

> Note that WebDriver has some issues with textareas and surrounding whitespace. Namely, some drivers implicitly trim whitespace from the beginning and end of the value. You can track this issue here: http://code.google.com/p/selenium/issues/detail?id=2131

#### file upload

It's currently not possible with WebDriver to simulate the process of a user clicking on a file upload control and choosing a file to upload via the normal file chooser. However, you can directly set the value of the upload control to the *absolute path* of a file on the system where the driver is running and on form submission that file will be uploaded.

    <input type="file" name="csvFile">
    
    $("form").csvFile = "/path/to/my/file.csv"

## Complex Interactions

WebDriver supports interactions that are more complex than simply clicking or typing into items, such as dragging. You can use this API from Geb, or use the more Geb friendly `interact {}` DSL (explained below).

### Using the WebDriver API directly

A Geb navigator object is built on top of a collection of WebDriver [WebElement][webelement-api] objects. It is possible to access the contained `WebElement`s via the following methods on navigator objects:

    WebElement firstElement()
    WebElement lastElement()
    Collection<WebElement> allElements()

By using the methods of the WebDriver [Actions](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/interactions/Actions.html) class with WebElements, complex user gestures can be emulated.

### Using Actions

Create an Actions instance after obtaining the WebDriver driver:

    def actions = new Actions(driver)

Next, use methods of Actions to compose a series of UI actions, then call build() to create a concrete Action:

    import org.openqa.selenium.Keys
    
    WebElement someItem = $('li.clicky').firstElement()
    def shiftDoubleClickAction = actions.keyDown(Keys.SHIFT).doubleClick(someItem).keyUp(Keys.SHIFT).build()

Finally, call perform() to actually trigger the desired mouse or keyboard behavior:

    shiftDoubleClickAction.perform()

### Using Interact Closures

To cut down on the amount of typing required, use an interact closure instead of using class `Actions` explicitly.  When using an interact closure, an `Actions` instance is implicitly created, built into an Action, and performed. As an added bonus, Geb navigators can be passed directly to `Actions` methods within an interact closure.

This interact closure performs the same work as the calls in the 'Using Actions' section:

    import org.openqa.selenium.Keys
    
    interact {
        keyDown(Keys.SHIFT)
        doubleClick($('li.clicky'))
        keyUp(Keys.SHIFT)
    }

This method creates code that is more readable than using `Actions` directly.

For the full list of available interactions, see the documentation for the WebDriver [Actions](http://selenium.googlecode.com/svn/trunk/docs/api/java/org/openqa/selenium/interactions/Actions.html) class.

### Interact Examples

Interact closures (or Actions) can be used to perform behaviors that are more complicated than clicking buttons and anchors or typing in input fields.  Shift-double-clicking was demonstrated earlier.

#### Drag and Drop

clickAndHold, moveByOffset, and then release will drag and drop an element on the page.

    interact {
        clickAndHold($('#element'))
        moveByOffset(400, -150)
        release()
    }

Drag-and-dropping can also be accomplished using the `dragAndDropBy` convenience method from the Actions API:

    interact {
        dragAndDropBy($('#element'), 400, -150)
    }

In this particular example, the element will be clicked then dragged 400 pixels to the right and 150 pixels upward before being released.

> Note that moving to arbitrary locations with the mouse is currently not supported by the HTMLUnit driver, but moving directly to elements is.

#### Control-Clicking

Control-clicking several elements, such as items in a list, is performed the same way as shift-clicking.

    import org.openqa.selenium.Keys
    
    interact {
        keyDown(Keys.CONTROL)
        click($('ul.multiselect li', text: 'Order 1'))
        click($('ul.multiselect li', text: 'Order 2'))
        click($('ul.multiselect li', text: 'Order 3'))
        keyUp(Keys.CONTROL)
    }

