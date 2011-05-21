<h1>Testing via Spock</h1>
<p>Geb was originally born into creation out of the <a href="http://spockframework.org">Spock Framework</a> project, which is an innovative, developer friendly, framework for helping you test your software.</p>
<p>Spock compliments Geb's expressive and concise nature, giving you clear and easy to understand tests that you don't have to jump through hoops to write.</p>
<pre class="brush: groovy">
import spock.lang.*
import geb.spock.GebSpec

@Stepwise
class PersonCRUDSpec extends GebSpec {

    def "there are no people listed"() {
        when:
          to ListPage
        
        then:
          personRows.size() == 0
    }
    
    def "add a person"() {
        when:
          newPersonButton.click()
        
        then:
          at CreatePage
    }
    
    def "enter their details"() {
        when:
          enabled = true
          firstName = "Johnny"
          lastName = "Cash"
          createButton.click()
        
        then:
          at ShowPage
    }
    
    def "that person is now listed"() {
        expect:
          firstName == "Johnny"
          lastName == "Cash"
          enabled == true
    }
}
</pre>
<p>Please see the <a href="manual/integrations.html#spock">manual section on integrating with Spock</a> for more information.</p>