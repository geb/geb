package geb.waiting

class HtmlParseWaitingSpec extends WaitingSpec {
    def cleanup() {
        browser.config.htmlRootTagName = "html"
    }

    def 'when parse waiting disabled and content too late should throw exception'() {
        given:
        browser.config.htmlRootTagName = "div"

        when:
        go()

        js.showIn(2)

        $('span')

        then:
        thrown(org.openqa.selenium.NoSuchElementException)
    }

    def 'when parse waiting enabled and shows up in time should not throw exception'() {
        given:
        browser.config.htmlParseWaitEnabled = true
        browser.config.htmlParseWaitTimeout = 2

        browser.config.htmlRootTagName = "div"

        when:
        go()

        js.showIn(1)

        then:
        assert $('span')
    }

    def 'when parse waiting enabled and content shows up too late should throw exception'() {
        given:
        browser.config.htmlParseWaitEnabled = true
        browser.config.htmlParseWaitTimeout = 1

        browser.config.htmlRootTagName = "div"

        when:
        go()

        js.showIn(2)

        $('span')

        then:
        thrown(WebDriverWaitWrapperException)
    }
}
