package geb

import geb.test.util.GebSpecWithServer
import geb.driver.CachingDriverFactory
import groovy.xml.MarkupBuilder
import spock.lang.Unroll
import org.openqa.selenium.NoSuchWindowException


class WindowHandlingSpec extends GebSpecWithServer {

    private final static String MAIN_PAGE_URL = '/main'

    def cleanupSpec() {
        CachingDriverFactory.clearCacheAndQuitDriver()
    }

    def setupSpec() {
        server.get = { req, res ->
            def writer = new OutputStreamWriter(res.outputStream)
            def page = (~'/(.*)').matcher(req.requestURI)[0][1]
            new MarkupBuilder(writer).html {
                head {
                    title("Window $page")
                }
                if (req.requestURI == MAIN_PAGE_URL) {
                    body {
                        [1, 2].each {
                            a(target: "window$it", href: "/$it")
                        }
                    }
                }
            }
        }
    }

    private void allWindowsOpened() {
        go MAIN_PAGE_URL
        $('a').each { it.click() }
        assert availableWindows.size() == 3
    }

    @Unroll('check that tiltle is configured properly for page at #url')
    def "check that the page titles are configured properly"() {
        when:
        go url

        then:
        $('title').text() == "Window $title"

        where:
        url           | title
        MAIN_PAGE_URL | 'main'
        '/1'          | '1'
        '/2'          | '2'
    }

    def "check that there are links rendered for main page"() {
        when:
        go MAIN_PAGE_URL

        then:
        $('a').size() == 2
    }


    def "ensure withWindow closure parameter called"() {
        given:
        allWindowsOpened()

        when:
        def called = 0
        availableWindows.each {
            withWindow(currentWindow) { called++ }
        }

        then:
        called == 3
    }

    private void checkWindowTitle(String window, String title) {
        withWindow(window) {
            assert $('title').text() == title
        }
    }

    def "ensure withWindow closure called in context of a named window"() {
        given:
        allWindowsOpened()

        expect:
        [1, 2].each { windowNumber ->
            checkWindowTitle("window$windowNumber", "Window $windowNumber")
        }

        and:
        checkWindowTitle(currentWindow, "Window main")
    }

    def "ensure original context is preserved after a call to withWindow"() {
        given:
        allWindowsOpened()

        when:
        withWindow('window1') {}

        then:
        $('title').text() == 'Window main'
    }


    def "ensure exception is thrown for a non existing window passed to withWindow"() {
        when:
        withWindow('nonexisting') {}

        then:
        thrown(NoSuchWindowException)
    }
}
