<h1>Page Objects</h1>
<p>Geb has first class support for the <a href="http://code.google.com/p/selenium/wiki/PageObjects">Page Object</a> pattern, leveraging Groovy's DSL capabilities to allow <strong>you the developer</strong> to easily define the interesting parts of your pages in a concise, maintanable and extensible manner.</p>

<pre class="brush: groovy">import geb.Browser
import geb.Page
import geb.Module

class GoogleSearchModule extends Module {
    def buttonValue
    static content = {
        field { \$("input", name: "q") }
        button(to: GoogleResultsPage) { 
            \$("input", value: buttonValue)
        }
    }
}

class GoogleHomePage extends Page {
    static url = "http://google.com/ncr"
    static at = { title == "Google" }
    static content = {
        search { module GoogleSearchModule, buttonValue: "Google Search" }
    }
}

class GoogleResultsPage extends Page {
    static at = { title.endsWith("Google Search") }
    static content = {
        search { module GoogleSearchModule, buttonValue: "Search" }
        results { \$("li.g") }
        result { i -> results[i] }
        resultLink { i -> result(i).find("a.l") }
    }
}

class WikipediaPage extends Page {
    static at = { title == "Wikipedia" }
}

Browser.drive(GoogleHomePage) {
    // enter wikipedia into the search field
    search.field.value("wikipedia")

    // wait for the change to results page to happen
    // (google updates the page without a new request)
    waitFor { at(GoogleResultsPage) }

    // is the first link to wikipedia?
    assert resultLink(0).text() == "Wikipedia"

    // click the link
    resultLink(0).click()

    // wait for Google's javascript to redirect us
    // to wikipedia
    waitFor { at(WikipediaPage) }
}
</pre>
<p>Pages can be used to model a particular page (or type of page) in your application, while modules can be used to model view fragments that are used across different pages. Page and module types in Geb can extend parent types, inheriting all content defined by the parent.</p>
<p>See the <a href="manual/pages.html">manual section on pages</a> for more information.</p>