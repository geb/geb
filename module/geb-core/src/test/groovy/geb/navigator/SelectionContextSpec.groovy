package geb.navigator

import geb.Page
import geb.test.util.GebSpecWithServer
import spock.lang.Unroll

class SelectionContextSpec extends GebSpecWithServer {

    def setupSpec() {
        responseHtml { request ->
            html {
                body {
                    [1, 2, 3].each { divId ->
                        div(id: "test-$divId", class:"test") {
                            if(divId == 1) {
                                p(id: "testParagraph")
                            }
                        }
                    }
                }
            }
        }
    }

    @Unroll("element #element has selector #selector")
    def "selector matching"() {
        given:
        to SelectionPage

        expect:
        page."$element".selectionContext.selector == selector

        where:
        element              | selector
        "firstDiv"           | "div#test-1"
        "secondDiv"          | "div#test-2"
        "nonMatchingElement" | "div#non-matching"
        "firstDivParagraph"  | "p#testParagraph"

    }

}
class SelectionPage extends Page {
    static content = {
        firstDiv { $("div#test-1") }
        secondDiv { $("div#test-2") }
        nonMatchingElement(required: false) { $("div#non-matching")}
        firstDivParagraph { firstDiv.find("p#testParagraph") }
    }

}
