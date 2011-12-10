package geb.interaction

import geb.test.util.GebSpec
import org.openqa.selenium.Keys

class InteractionsSupportSpec extends GebSpec {

    def setupSpec() {
        go (getClass().getResource("/test.html") as String)
    }

    def "Move between elements using interactions"() {

        given:
            $('#checker1').click()

        when:
            interact {
               moveToElement($('#keywords'))
               click()
               sendKeys(' geb')
               keyDown(Keys.SHIFT)
               sendKeys(' geb')
               keyUp(Keys.SHIFT)
           }

        then:
            $('#keywords').value() == 'Enter keywords here geb GEB'

    }

    def "Focus various elements by moving to and clicking them"() {

        expect:
            $('form').plain_select == '4'

        when:
            interact {
                moveToElement($('#the_plain_select'))
                click()
                moveToElement($('#the_plain_select option', value: '2'))
                click()
            }

        then:
            $('form').plain_select == '2'

    }

}