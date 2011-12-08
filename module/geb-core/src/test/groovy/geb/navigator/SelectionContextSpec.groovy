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
    
    def "Can access the passed-in selector"() {
        when:
            to SelectionPage
        then:
            firstDiv.selectionContext.selector == "div#test-1"
            
    }
    

    
}
class SelectionPage extends Page {
    static content = { firstDiv { $("div#test-1") } }

}
