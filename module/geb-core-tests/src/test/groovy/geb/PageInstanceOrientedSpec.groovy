package geb

import geb.test.GebSpecWithServer
import spock.lang.Stepwise
import spock.lang.Unroll
import spock.lang.Issue

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

	@Unroll("verify our server is configured correctly for url #url")
	def "verify our server is configured correctly"() {
		when:
		go url
		then:
		$('span').text() == text

		where:
		url          | text
		'/someText'  | 'someText'
		'/otherText' | 'otherText'
	}

	@Issue('http://jira.codehaus.org/browse/GEB-104')
	@Unroll("check using navigator on page after setting the page with instance for #path")
	def "check that using navigator on page after setting the page with instance works"() {
		when:
		go path
		page(new PageWithText(text: text))
		then:
		verifyAt()

		where:
		path         | text
		'/someText'  | 'someText'
		'/otherText' | 'otherText'
	}

	@Issue('http://jira.codehaus.org/browse/GEB-105')
	def "verify the instance flavour of at checking works"() {
		when:
		go '/someText'
		then:
		def expectedPage = new PageWithText(text: 'someText')
		at expectedPage
		and: 'calling at method sets the page instance'
		page == expectedPage
	}

	def "verify the instance flavour of isAt() checking works"() {
		when:
		go '/someText'
		then:
		isAt(new PageWithText(text: 'someText'))
		!isAt(new PageWithText(text: 'otherText'))
	}
}

class PageWithText extends Page {
	static at = { $('span').text() == text }
	String text
}
