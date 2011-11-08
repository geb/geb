package geb

import geb.test.util.GebSpecWithServer
import geb.driver.CachingDriverFactory
import groovy.xml.MarkupBuilder
import spock.lang.Unroll
import org.openqa.selenium.NoSuchWindowException


class WindowHandlingSpec extends GebSpecWithServer {

    private final static String MAIN_PAGE_URL = '/main'

    def cleanup() {
        resetBrowser()
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
        $('a')*.click()
        assert availableWindows.size() == 3
    }

    private boolean isInContextOfMainWindow() {
        $('title').text() == 'Window main'
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


    def "ensure withWindow block closure parameter called for named windows"() {
        given:
        allWindowsOpened()

        when:
        def called = 0
        availableWindows.each {
            withWindow(it) { called++ }
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

    @Unroll
    def "ensure original context is preserved after a call to withWindow"() {
        given:
        allWindowsOpened()

        when:
        withWindow(specification) {}

        then:
        inContextOfMainWindow

        when:
        withWindow(specification) { throw new Exception() }

        then:
        thrown(Exception)
        inContextOfMainWindow

        where:
        specification << ['window1', { $('title').text() == 'Window 1' }]
    }

    @Unroll
    def "ensure exception is thrown for a non existing window passed to withWindow"() {
        when:
        withWindow(specification) {}

        then:
        thrown(NoSuchWindowException)

        where:
        specification << ['nonexisting', { false }]
    }

    @Unroll
    def "ensure withWindow block closure parameter called for all windows for which specification closure returns true"() {
        given:
        allWindowsOpened()

        when:
        def called = 0
        withWindow(specification) { called++ }

        then:
        called == expecetedCalls

        where:
        expecetedCalls | specification
        3              | { true }
        1              | { $('title').text() == 'Window main' }
        2              | { $('title').text() ==~ /Window [0-9]/ }
    }

    @Unroll("ensure block closure parameter called in the context of the window specified by the title: #title")
    def "ensure block closure parameter called in the context of the window specified by the specification closure"() {
        given:
        allWindowsOpened()

        when:
        def titleAsExpected = false
        withWindow({ $('title').text() == title }) {
            titleAsExpected = ($('title').text() == title)
        }

        then:
        titleAsExpected

        where:
        title << ['Window main', 'Window 1', 'Window 2']
    }

    @Unroll("ensure withNewWindow throws an exception when: '#message'")
    def "ensure withNewWindow throws exception if there was none or more than one windows opened"() {
        when:
        withNewWindow(newWindowBlock) {}

        then:
        NoSuchWindowException e = thrown()
        e.message.startsWith(message)

        where:
        message                                      | newWindowBlock
        'No new window has been opened'              | {}
        'There has been more than one window opened' | { allWindowsOpened() }
    }

    def "ensure original context is preserved after a call to withNewWindow"() {
        given:
        go MAIN_PAGE_URL

        when:
        withNewWindow({ $('a', 0).click() }) {}

        then:
        inContextOfMainWindow

        when:
        withNewWindow({ $('a', 1).click() }) { throw new Exception() }

        then:
        thrown(Exception)
        inContextOfMainWindow
    }

    @Unroll
    def "ensure withNewWindow block closure called in the context of the newly opened window"() {
        given:
        go MAIN_PAGE_URL

        when:
        def title
        withNewWindow({ $('a', anchorIndex).click() }) {
            title = $('title').text()
        }

        then:
        title == expectedTitle

        where:
        expectedTitle | anchorIndex
        'Window 1'    | 0
        'Window 2'    | 1
    }
}
