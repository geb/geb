<h1>Page Objects</h1>
<p>Geb has first class support for the <a href="http://code.google.com/p/selenium/wiki/PageObjects">Page Object</a> pattern, leveraging Groovy's DSL capabilities to allow <strong>you the developer</strong> to easily define the interesting parts of your pages in a concise, maintanable and extensible manner.</p>

<pre class="brush: groovy">import geb.Page
    
class LoginPage extends Page {
    static url = "http://myapp.com/login"
    static at = { heading.text() == "Please Login" }
    static content = {
        heading { \$("h1") }
        loginForm { \$("form.login") }
        loginButton(to: AdminPage) { loginForm.login() }
    }
}

class AdminPage extends Page {
    static at = { heading.text() == "Admin Section" }
    static content = {
        heading { \$("h1") }
    }
}
</pre>

<p>Pages define their location, an “at checker” and content (among other things). Defining this information as part of the page allows you to separate the implementation details from the intention.</p>

<pre class="brush: groovy">import geb.Browser
    
Browser.drive {
    to LoginPage
    assert at(LoginPage)
    loginForm.with {
        username = "admin"
        password = "password"
    }
    loginButton.click()
    assert at(AdminPage)
}
</pre>

<p>See the <a href="manual/current/pages.html">manual section on pages</a> for more information on.</p>