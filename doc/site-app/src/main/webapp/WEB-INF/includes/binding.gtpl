<h1>Scripting via Binding</h1>
<p>Geb also supports directly managing the script binding which can make for more convenient Geb scripts. This allows you to set up a Geb environment, then dynamically execute scripts in that environment.</p>

<pre class="brush: groovy">
import geb.Browser
import geb.binding.BindingUpdater

def binding = new Binding()
def browser = new Browser()
def updater = new BindingUpdater(binding, browser)

// populate and start updating the browser
updater.initialize()

// Run all scripts with this geb env
def shell = new GroovyShell(binding)
new File("geb-scripts").eachFile { file ->
    if (file.name.endsWith(".groovy")) {
      shell.evaluate(file)
    }
}

// remove Geb bits from the binding and stop updating it
updater.remove()  
</pre>

<p>Which will allow you to write scripts like…</p>
<pre class="brush: groovy">
to "http://somesite.come"
username = "user"
password = "admin"
login().click()

assert \$("p.login_status").text() == "You are logged in"
</pre>

<p>See the <a href="manual/binding.html">manual section on binding management</a> for more information.</p>