# Modules

Modules are re-usable definitions of content that can be used across multiple pages. They are defined in a manner similar to pages, but extend `geb.Module`…

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

## The Content DSL

The Content DSL used for modules is _exactly_ the same as the [one used for pages][content-dsl], so all of the same options and techniques can be used.
    