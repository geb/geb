package geb.frame

import geb.test.util.GebSpecWithServer
import geb.Page
import geb.Module
import spock.lang.Unroll
import spock.lang.FailsWith
import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchFrameException

class FrameSupportSpec extends GebSpecWithServer {

    final static String MAIN_PAGE_URL = '/main'

    def setupSpec() {
        server.get = { req, res ->
            if (req.requestURI == MAIN_PAGE_URL) {
                res.outputStream <<'''
                <html>
                    <body>
                        <frame name="header" id="header-id" src="/header"></frame>
                        <frame id="footer" src="/footer"></frame>
                        <iframe id="inline" src="/inline"></iframe>
                        <span>main</span>
                    <body>
                </html>
                '''
            } else {
                def pageText = (~'/(.*)').matcher(req.requestURI)[0][1]
                res.outputStream << """
                <html>
                <body>
                    <span>$pageText</span>
                </body>
                </html>"""
            }

        }
    }

    def "verify the server is configured correctly for main page"() {
        when:
        go MAIN_PAGE_URL

        then:
        $('frame').size() == 2
    }

    @Unroll("verify that server is configured correctly for frame page: #text")
    def "verify that server is configured correctly for frame pages"() {
        when:
        go "/$text"

        then:
        $('span').text() == text

        where:
        text << ['header', 'footer', 'inline']
    }

    @FailsWith(NoSuchFrameException)
    @Unroll("expect waitFor to fail if called for a non existing frame '#frame'")
    def "expect waitFor to fail if called for a non existing frame"() {
        expect:
        withFrame(frame) {}

        where:
        frame << ['frame', 0]
    }

    @FailsWith(NoSuchFrameException)
    def "expect waitFor to fail if called for a navigator that's not a frame"() {
        when:
        go MAIN_PAGE_URL

        then:
        withFrame($('span')) {}
    }

    @FailsWith(NoSuchFrameException)
    def "expect waitFor to fail if called for an empty navigator"() {
        when:
        go MAIN_PAGE_URL

        then:
        withFrame($('nonexistingelem')) {}
    }

    @Unroll("expect the closure argument passed to withFrame to be executed for '#frame' as frame identifier")
    def "expect the closure argument passed to withFrame to be executed"() {
        when:
        go MAIN_PAGE_URL
        boolean called = false
        withFrame(frame) {
            called = true
        }

        then:
        called

        where:
        frame << ['header', 'inline', 0]
    }

    def "expect the closure argument passed to withFrame to be executed for navigator as frame identifier"() {
        when:
        go MAIN_PAGE_URL
        boolean called = false
        withFrame($('#footer')) {
            called = true
        }

        then:
        called
    }

    private void checkFrameTextMatches(frame, text) {
        withFrame(frame) {
            assert $("span").text() == text
        }
    }

    @Unroll("expect withFrame to work correctly for frame identifier '#frame'")
    def "expect withFrame to work for different frame identifiers"() {
        when:
        go MAIN_PAGE_URL

        then:
        checkFrameTextMatches(frame, text)

        where:
        frame       | text
        'header'    | 'header'
        'footer'    | 'footer'
        'inline'    | 'inline'
        0           | 'header'
        1           | 'footer'
        2           | 'inline'
    }

    @Unroll("expect withFrame to work for navigator frame identifier with selector '#selector'")
    def "expect withFrame to work for navigator frame identifiers"() {
        when:
        go MAIN_PAGE_URL

        then:
        checkFrameTextMatches($(selector), text)

        where:
        selector        | text
        '#header-id'    | 'header'
        '#footer'       | 'footer'
        '#inline'       | 'inline'
    }

    def "ensure original context is kept after a withFrame call"() {
        when:
        go MAIN_PAGE_URL
        withFrame('header') {}

        then:
        $('span').text() == 'main'
    }

    def "ensure pages and modules have withForm available"() {
        when:
        to FrameSupportSpecPage

        then:
        page.callAllVariantsOfWithFrame() == 3
        mod.callAllVariantsOfWithFrame() == 3
    }
}

class FrameSupportSpecPage extends Page {
    static url = FrameSupportSpec.MAIN_PAGE_URL
	static content = {
        footer { $('#footer') }
		mod { module FrameSupportSpecModule }
	}

	def callAllVariantsOfWithFrame() {
        def count = 0
        def block = { count++ }
		withFrame(0, block)
        withFrame('header', block)
        println(footer.getClass())
        withFrame(footer, block)
        count
	}
}

class FrameSupportSpecModule extends Module {
	def callAllVariantsOfWithFrame() {
        def count = 0
        def block = { count++ }
		withFrame(0, block)
        withFrame('header', block)
        withFrame($('#footer'), block)
        count
	}
}
