<h1>What is it?</h1>
<p>Geb <em>(pronounced with a soft “G” like “Jeb”)</em> is a browser automation solution for the <a href="http://groovy.codehaus.org/" title="Groovy - Home">Groovy</a> programming language. It combines the browser driving features of <a href="http://code.google.com/p/selenium/">WebDriver</a> with a <a href="http://jquery.com/" title="jQuery: The Write Less, Do More, JavaScript Library">jQuery</a> inspired content navigation/inspection API and the expressiveness and conciseness of Groovy.</p>

<p>It can be used for scripting, scraping and general automation — or equally as a functional/web testing solution via its support for testing frameworks like <a href="spock">Spock</a> &amp; <a href="junit">JUnit</a>.</p>
<h1>What does it look like?</h1>
<pre class="brush: groovy">import geb.*

Browser.drive("http://google.com/ncr") {
    assert title == "Google"

    // enter wikipedia into the search field
    \$("input", name: "q").value("wikipedia")

    // wait for the change to results page to happen
    // (google updates the page without a new request)
    waitFor { title.endsWith("Google Search") }

    // is the first link to wikipedia?
    def firstLink = \$("li.g", 0).find("a.l")
    assert firstLink.text() == "Wikipedia"

    // click the link 
    firstLink.click()

    // wait for Google's javascript to redirect 
    // us to Wikipedia
    waitFor { title == "Wikipedia" }
}</pre>

<p>This is a very basic scripting style sample and does not illustrate many of Geb's most powerful features.</p>
<p>The <a href="manual/current/">Book of Geb</a> contains all the information you need to get started with Geb.</p>
