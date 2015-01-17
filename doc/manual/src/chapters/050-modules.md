# Modules

Modules are re-usable definitions of content that can be used across multiple pages. They are useful for modelling things like UI widgets that are used across multiple pages, or even for defining more complex UI elements in the one page.

They are defined in a manner similar to pages, but extend [`Module`](api/geb/Module.html)…

    class ExampleModule extends Module {
        static content = {
            button { $("input", type: "submit") }
        }
    }

Pages can “include” modules using the following syntax…

    class ExamplePage extends Page {
        static content = {
            theModule { module ExampleModule }
        }
    }

The `module` method sets the content to an instance of the module…

    Browser.drive {
        to ExamplePage
        theModule.button.click()
    }

Modules can also be parameterised…

    class ExampleModule extends Module {
        def buttonName
        static content = {
            button { $("input", type: "submit", name: buttonName) }
        }
    }

Where the parameters are passed to constructor of the module…
    class ExamplePage extends Page {
        static content = {
            theModule { name -> module(new ExampleModule(buttonName: name) }
        }
    }
    
    Browser.drive {
        to ExamplePage
        theModule("something").button.click()
    }

Modules can also include other modules…

    class ExampleModule extends Module {
        static content = {
            innerModule { module InnerModule }
        }
    }
    
    class InnerModule extends Module {
        static content = {
            button { $("input", type: "submit") }
        }
    }
    
    class ExamplePage extends Page {
        static content = {
            theModule { module ExampleModule }
        }
    }
    
    Browser.drive {
        theModule.innerModule.button.click()
    }

## Base And Context

Modules can be localised to a specific section of the page that they are used in, or they can specify an absolute context as part of their definition. There are two ways that a modules base/context can be defined.

Module can be based on a `Navigator` instance…

    static content = {
        form { $("form").module(FormModule) }
    }

It can also be done outside of a content definition…

    Browser.drive {
        to FormPage
        $("form").module(ExampleModule)
    }

We can define a `Navigator` context when including the module using the above syntax. This now means that calls to _all_ `Navigator` (e.g. `$()`) method calls that occur within the module are against the given context (in this case, the `form` element).

However, module classes can also define their own base…

    import geb.Module
    
    class FormModule extends Module {
        static base = { $("form") }
    }

This has the same effect as the code above.

They can also be combined. Consider the following HTML…

    <div class="a">
        <form>
            <input name="thing" value="a"/>
        </form>
    </div>
    <div class="b">
        <form>
            <input name="thing" value="b"/>
        </form>
    </div>

And the following content definitions…

    import geb.*
    
    class ExamplePage extends Page {
        static content = {
            formA { $("div.a").module(FormModule) }
            formB { $("div.b").module(FormModule)  }
        }
    }
    
    class FormModule extends Module {
        static base = { $("form") }
        static content = {
            thingValue { thing().value() }
        }
    }

When working with a browser located at `ExamplePage` page…

    assert formA.thingValue == "a"
    assert formB.thingValue == "b"

If the module declares a base, it is always calculated _relative_ to the `Navigator` used in the initialization statement. If the initialization statement does not use a `Navigator`, the module's base is calculated relative to the including page's base.

## Reusing modules across pages

As previously stated, modules can be used to model page fragments that are reused across multiple pages. For example, many different types of pages in your application may show information about the user's shopping cart. You could handle this with modules…

    class CartInfoModule extends Module {
        static content = {
            section { $("div.cart-info") }
            itemCount { section.find("span.item-count").toInteger() }
            totalCost { section.find("span.total-cost").toDouble() }
        }
    }
    
    class HomePage extends Page {
        static content = {
            cartInfo { module CartInfoModule }
        }
    }
    
    class OtherPage extends Page {
        static content = {
            cartInfo { module CartInfoModule }
        }
    }

Modules work well for this.

## Using modules for repeating content on a page

Other than content that is repeated on different pages (like the shopping cart mentioned above), pages also have content that is repeated on the page itself. On a checkout page, the contents of the shopping cart could be summarized with the product name, the quantity and price for each product contained. Thanks to `Navigator` implementing `Iterable<Navigator>` a list of modules can be collected using Groovy's `collect()`the `Navigator.module()` methods.

Consider the following HTML for our cart contents:

    <table>
        <tr>
            <th>Product</th><th>Quantity</th><th>Price</th>
        </tr>
        <tr>
            <td>The Book Of Geb</td><td>1</td><td>5.99</td>
        </tr>
        <tr>
            <td>Geb Single-User License</td><td>1</td><td>99.99</td>
        </tr>
        <tr>
            <td>Geb Multi-User License</td><td>1</td><td>199.99</td>
        </tr>
    </table>

We can model one line of the table like this:

    class CartRow extends Module {
        static content = {
            cell { $("td", it) }
            productName { cell(0).text() }
            quantity { cell(1).text().toInteger() }
            price { cell(2).text().toDouble() }
        }
    }

And define a list of CartRows in our Page:

    class CheckoutPage extends Page {
        static content = {
            cartItems { $("table tr").tail().collect { it.module(CartRow) }  } // tailing to skip the header row
        }
    }

> It's possible to make the creation of a list of modules from a `Navigator` even more concise by using the spread-dot operator (i.e. `$("table tr").tail()*.module(CartRow)`). Be careful though because it won't do what you'd expect it to do if you use this technique together with the `module()` method that takes module instance - instead of creating multiple module instances like when spreading a `module()` call that takes a class it will take the single instance passed to it and initialize it multiple times returning a list which will contain the same module instance multiple times!

Because the return value of `cartItems` is a list of CartRow instances, we can use any of the usual collection methods:

    assert cartItems.every { it.price > 0.0 }

We can also access the cart items using subscript operator together with an index or a range of indexes:

    assert cartItems[0].productName == "The Book Of Geb"
    assert cartItems[1..2]*.productName == ["Geb Single-User License", "Geb Multi-User License"]

Keep in mind that you can also use parametrized module instances to create lists of modules for repeating content:

	static content = {
		myContent { 
			$(".myModuleClass").collect { it.module(new MyModule(myParam: 'param value')) } 
		}
	}

## The Content DSL

The Content DSL used for modules is _exactly_ the same as the [one used for pages][content-dsl], so all of the same options and techniques can be used.

## Inheritance

Modules can use inheritance in the same way that pages can. That is, their content definitions are merged with any content redefined in the subclass taking precedence of the superclass.

## Size and Location

You can obtain the size and location of the module. All units are in pixels. The size is available via the `height` and `width` properties, while the location is available as the `x` and `y` properties which represent the distance from the top left of the page (or parent frame) to the top left point of the base of the module.

    $("div").height == 20
    $("div").width == 40
    $("div").x == 60
    $("div").y == 80
