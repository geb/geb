package geb

import geb.test.GebSpecWithCallbackServer
import spock.lang.Issue

class NavigatorCompatibilitySpec extends GebSpecWithCallbackServer {
    def setupSpec() {
        responseHtml {
            body {
                input(type: "text", id: "my-input", value: "val")
            }
        }
    }

    def setup() {
        go()
    }

    @Issue("https://github.com/geb/issues/issues/422")
    def 'can set form input field value with Groovy 2.3.7'() {
        when:
        $('#my-input').value('newValue')

        then:
        $('#my-input').value() == 'newValue'
    }
}