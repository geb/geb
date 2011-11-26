package geb.frame

import geb.test.util.GebSpecWithServer
import geb.Page
import geb.Module
import spock.lang.Unroll
import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchFrameException

class FrameSupportSpec extends GebSpecWithServer {

	final static String MAIN_PAGE_URL = '/main'

	def setupSpec() {
		responseHtml { request ->
			def pageText = (~'/(.*)').matcher(request.requestURI)[0][1]
			body {
				if (request.requestURI == MAIN_PAGE_URL) {
					frame(name: 'header', id: 'header-id', src: '/header')
					frame(id: 'footer', src: '/footer')
					iframe(id: 'inline', src: '/inline')
				}
				span("$pageText")
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

	@Unroll("expect withFrame to fail if called for a non existing frame '#frame'")
	def "expect withFrame to fail if called for a non existing frame"() {
		when:
		withFrame(frame) {}

		then:
		thrown(NoSuchFrameException)

		where:
		frame << ['frame', 0]
	}

	@Unroll
	def "expect withFrame to fail if called for a navigator that doesn't contain a frame"() {
		given:
		go MAIN_PAGE_URL

		when:
		withFrame(navigatorFactory.call()) {}

		then:
		NoSuchFrameException e = thrown()
		e.message.startsWith(message)

		where:
		message                          | navigatorFactory
		''                               | { $('span') }
		'No elements for given content:' | { $('') }
	}

	def "expect withFrame to fail if called for an empty navigator"() {
		when:
		go MAIN_PAGE_URL
		withFrame($('nonexistingelem')) {}

		then:
		thrown(NoSuchFrameException)
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

	private String getFrameText(frame) {
		withFrame(frame) {
			$("span").text()
		}
	}

	@Unroll("withFrame changes focus to frame and returns closure return value for frame identifier '#frame'")
	def "withFrame changes focus to frame with given identifier and returns closure return value"() {
		when:
		go MAIN_PAGE_URL

		then:
		getFrameText(frame) == text

		where:
		frame    | text
		'header' | 'header'
		'footer' | 'footer'
		'inline' | 'inline'
		0        | 'header'
		1        | 'footer'
		2        | 'inline'
	}

	@Unroll("withFrame changes focus to frame and returns closure return value for selector '#selector'")
	def "withFrame changes focus to frame with given selector and returns closure return value"() {
		when:
		go MAIN_PAGE_URL

		then:
		getFrameText($(selector)) == text

		where:
		selector     | text
		'#header-id' | 'header'
		'#footer'    | 'footer'
		'#inline'    | 'inline'
	}

	private boolean isInMainFrameContext() {
		$('span').text() == 'main'
	}

	@Unroll
	def "ensure original context is kept after a withFrame call"() {
		given:
		go MAIN_PAGE_URL

		when:
		withFrame(frame) {}

		then:
		inMainFrameContext

		when:
		withFrame(frame) { throw new Exception() }

		then:
		thrown(Exception)
		inMainFrameContext

		where:
		frame << ['header', 0]
	}

	def "ensure pages and modules have withFrame available"() {
		when:
		to FrameSupportSpecPage

		then:
		page.callAllVariantsOfWithFrame() == 3
		page.returnValueOfWithFrameCallForPageContent == 'footer'
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
		withFrame(footer, block)
		count
	}

	def getReturnValueOfWithFrameCallForPageContent() {
		withFrame(footer) { $('span').text() }
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
