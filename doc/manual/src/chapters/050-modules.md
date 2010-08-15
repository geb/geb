# Modules

Modules are re-usable definitions of content that can be used across multiple pages. They are useful for modelling things like UI widgets that are used across multiple pages, or even for defining more complex UI elements in the one page.

They are defined in a manner similar to pages, but extend `geb.Module`…

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

The `module` method is a special method only available in content template definitions. It sets the content to an instance of the module…

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

Where the parameters are set using the `module` method…

    class ExamplePage extends Page {
        static content = {
            theModule { name -> module ExampleModule, buttonName: name }
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

Modules can be localised to a specific section of the page that they are used in, or they can specify an absolute context as part of their definition. There two ways that a modules base/context can be defined.

It can be defined at inclusion time…

    static content = {
        form { module FormModule, $("form") }
    }

We can define a `Navigator` context when including the module using the above syntax. This now means that _all_ $ function calls that occur within the module are against the given context (in this case, the `form` element).

However, module classes can also define their own base…

    import geb.Module
    
    class FormModule extends {
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
            formA { module FormModule, $("div.a") }
            formB { module FormModule, $("div.b") }
        }
    }
    
    class FormModule extends Module {
        static base = { $("form") }
        static content = {
            thingValue { thing().value() }
        }
    }

When working with a browser at a `ExamplePage` page…

    assert formA.thingValue == "a"
    assert formB.thingValue == "b"

If the module declares a base, it is always calculated _relative_ to the base given by the including statement. If the including statement does not specify a base, then the base is effectively the entire page.

## Use Cases

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

Another not so obvious use case is using modules to deal with complex and/or repeating sections that are only present in one page.

## The Content DSL

The Content DSL used for modules is _exactly_ the same as the [one used for pages][content-dsl], so all of the same options and techniques can be used.
    