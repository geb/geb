package geb.navigator

import geb.test.util.GebSpec
import spock.lang.Shared
import org.openqa.selenium.WebDriver
import geb.textmatching.TextMatchingSupport
import geb.test.util.GebSpecWithServer
import geb.Page

class SelectionContextSpec extends GebSpecWithServer {

    def setupSpec() {
        responseHtml { request ->
            html {
                body {
                    [1, 2, 3].each { divId ->
                        div(id: "test-$divId", class:"test")
                    }
                }
            }
        }
    }
    
    def "Verify we can get the CSS selector for the firstDiv element"() {
        when:
            to SelectionPage
        then:
            firstDiv.selectionContext.selector == "div#test-1"
            
    }
    
    def "Verify we can get the CSS selector for the secondDiv element"() {
        when:
          to SelectionPage
        then:
          secondDiv.selectionContext.selector == "div#test-2"
    }

    def "Verify non-matching selector has selection context selector"() {
        when:
            to SelectionPage
        then:
            nonMatchingElement.selectionContext.selector == "div#non-matching"
    }
    
}
class SelectionPage extends Page {
    static content = {
        firstDiv { $("div#test-1") }
        secondDiv { $("div#test-2") }
        nonMatchingElement(required: false) { $("div#non-matching")}
    }

}
