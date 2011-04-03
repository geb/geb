# Scripts and Binding

Geb supports being used in scripting environments via both the `Browser.drive()` method, and by using the `geb.binding.BindingUpdater` class that populates and updates a [`groovy.lang.Binding`][groovy-binding] that can be used with scripts. This is also the same mechanism that is used by the EasyB Geb plugin and can be used with Cuke4Duke (Cucumber for the JVM).

## Configuration

To use the binding support, you simply create a `geb.binding.BindingUpdater` object with a binding and `geb.Browser`…

    import geb.Browser
    import geb.binding.BindingUpdater

    def binding = new Binding()
    def browser = new Browser()
    def updater = new BindingUpdater(binding, browser)
    
    // populate and start updating the browser
    updater.initialize()
    
    // Run a script from the filesystem
    new GroovyShell(binding).evaluate(new File("someScript.groovy"))
    
    // remove Geb bits from the binding and stop updating it
    updater.remove()
    
## The binding environment

A binding that is managed by a `BindingUpdater` has the following properties & methods …

### browser - property

The instance of `geb.Browser` that the binding updater was created with.

### go(…)

A shortcut for `browser.go(…)`

### to(…)

A shortcut for `browser.to(…)`

### at(…)

A shortcut for `browser.at(…)`

### js

A shortcut for `browser.js` (i.e. the JavaScript interface)

### page

A shortcut for `browser.page` (i.e. the current `geb.Page` instance)

### $(…)

A shortcut for `browser.page.$(…)` (i.e. content lookups)
