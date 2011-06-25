<h1>Standalone Scripting</h1>
<p>Geb can be used in a standalone scripting context for process automation, screen scraping or even lightweight testing.</p>
<p>Simply create an instance of Geb's Browser object, and start driving…</p>
<pre class="brush: groovy">
@Grapes([
    @Grab("org.codehaus.geb:geb-core:latest.release"),
    @Grab("org.seleniumhq.selenium:selenium-firefox-driver:latest.release")
])
import geb.*

Browser.drive("http://mysite.com") {
    // property shorthand for setting form values
    username = "user"
    password = "admin"
  
    // method shorthand for finding form values via name
    login().click()
}
</pre>

<p>See the <a href="manual/current/browser.html#the_drive_method">manual section on the drive() method</a> for more information.</p>