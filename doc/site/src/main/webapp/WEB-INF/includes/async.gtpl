<h1>Asynchronicity</h1>

<p>Modern web pages are full of asynchronous operations like AJAX requests and animations. Geb provides built in support for this fact of life.</p>

<p>Any content lookup, or operation can be wrapped in a <code>waitFor</code> clause.</p>

<pre class="brush: groovy">
    waitFor { 
        \$("p.status").text() == "Asynchronous operation complete!" 
    }
</pre>

<p>This will keep testing the condition for a certain amount of time (which is configurable) until it passes. The same technique can be used to just wait for the content, not necessarily for the content to have some characteristic.</p>

<pre class="brush: groovy">
    def dynamicParagraph = waitFor { \$("p.dynamically-added") }
    dynamicParagraph.text() == "Added dynamically!" 
</pre>

<p>You can also define that content should be implicitly waited for in the Content DSL for page objects</p>

<pre class="brush: groovy">import geb.Page
    
class DynamicPage extends Page {
    static content = {
        dynamicParagraph(wait: true) { \$("p.dynamically-added") }
    }
}
</pre>

<p>With this definition, when <code>dynamicParagraph</code> is requested Geb will implictly wait for a certain amount of time for it to appear.</p>

<p>See the <a href="manual/current/javascript.html#waiting">manual section on waiting</a> for more information.</p>