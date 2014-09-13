# Scripts and Binding

Geb supports being used in scripting environments via both the `Browser.drive()` method, and by using the [`geb.binding.BindingUpdater`][bindingupdater-api] class that populates and updates a [`groovy.lang.Binding`][groovy-binding] that can be used with scripts. This is also the same mechanism that can be used with [Cucumber-JVM](testing.html#cucumber_cucumber_jvm).

## Setup

To use the binding support, you simply create a [`BindingUpdater`][bindingupdater-api] object with a [`Binding`][groovy-binding] and [`Browser`][browser-api]…

    import geb.Browser
    import geb.binding.BindingUpdater
    import groovy.lang.Binding
    
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

### Browser methods & properties

The [`BindingUpdater`][bindingupdater-api] installs shortcuts into the binding for most of the [browser][browser-api] object's public methods. 

For example…

	go "some/page"
	assert at(SomePage)
	waitFor { $("p.status").text() == "ready" }
	js.someJavaScriptFunction()
	downloadText($("a.csvFile"))

In a managed binding, all of the methods/properties that you can usually call in the [`Browser.drive()`](browser.html#the_drive_method) method are available. This includes the `$()` function.

The following methods are available:

* $
* go
* to
* at
* waitFor
* withAlert
* withNoAlert
* withConfirm
* withNoConfirm
* download
* downloadStream
* downloadText
* downloadBytes
* downloadContent
* report
* reportGroup
* cleanReportGroupDir

The JavaScript interface property [`js`](javascript.html#the_js_object) is also available. The browser object itself is available as the `browser` property.

### The current page

The binding updater also updates the `page` property of the binding to be the browser's current page…

	import geb.Page
	
	class SomePage extends Page {
		static content = {
			button(to: OtherPage) { $("input.do-stuff") }
		}
	}
	
	to SomePage
	assert page instanceof SomePage
	page.button.click()
	assert page instanceof OtherPage
