package geb

import geb.test.util.GebSpecWithServer
import spock.lang.Stepwise
import spock.lang.Unroll

@Stepwise
class PageInstanceOrientedSpec extends GebSpecWithServer {
    def setupSpec() {
        server.get = { req, res ->
            def pageText = (~'/(.*)').matcher(req.requestURI)[0][1]
            res.outputStream << """
			<html>
			<body>
				<span>$pageText</span>
			</body>
			</html>"""
        }
    }

    def "verify our server is configured correctly"() {
        when:
        go '/someText'
        then:
        $('span').text() == 'someText'

        when:
        go '/otherText'
        then:
        $('span').text() == 'otherText'
    }

    @Unroll("check using navigator on page after setting the page with instance for #path")
    def "check that using navigator on page after setting the page with instance works"() {
        when:
        go path
        page(new PageWithText(text: text))
        then:
        verifyAt()

        where:
        path            | text
        '/someText'     | 'someText'
        '/otherText'    | 'otherText'
    }
}

class PageWithText extends Page {
    static at = { $('span').text() == text }
    String text
}
