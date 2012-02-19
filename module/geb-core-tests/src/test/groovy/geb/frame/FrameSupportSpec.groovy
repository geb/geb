package geb.frame

import geb.test.GebSpecWithServer
import geb.Page
import geb.Module
import spock.lang.Unroll
import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchFrameException

class FrameSupportSpec extends GebSpecWithServer {

	def setupSpec() {
		responseHtml { request, response ->
			def pageText = (~'/(.*)').matcher(request.requestURI)[0][1]
			if (pageText == "frames") {
				response.outputStream << "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">"
			}
			head {
				title pageText
			}
			if (pageText == "frames") {
				frameset(rows: "25%,75%") {
					frame(name: 'header', id: 'header-id', src: '/header')
					frame(id: 'footer', src: '/footer')
				}
			} else if (pageText == "iframe") { 
				body { iframe(id: 'inline', src: '/inline') }
			} else {
				body { span("$pageText") }
			}
		}
	}

	def setup() {
		go "frames"
	}

	def "verify the server is configured correctly for main page"() {
		expect:
		$('frame').size() == 2
	}

	@Unroll("verify that server is configured correctly for frame page: #text")
	def "verify that server is configured correctly for frame pages"() {
		when:
		go "$text"

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
		frame << ['frame', 'idontexist']
	}

	@Unroll
	def "expect withFrame to fail if called for a navigator that doesn't contain a frame"() {
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
		withFrame($('nonexistingelem')) {}

		then:
		thrown(NoSuchFrameException)
	}

	@Unroll("expect the closure argument passed to withFrame to be executed for '#frame' as frame identifier")
	def "expect the closure argument passed to withFrame to be executed"() {
		given:
		go pagePath
		
		when:
		boolean called = false
		withFrame(frameid) {
			called = true
		}

		then:
		called

		where:
		pagePath | frameid
		"frames" | "header"
		"iframe" | "inline"
		"frames" | 0
	}

	def "expect the closure argument passed to withFrame to be executed for navigator as frame identifier"() {
		when:
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
		given:
		go pagePath

		expect:
		getFrameText(frame) == text

		where:
		pagePath |frame    | text
		"frames" | 'header' | 'header'
		"frames" | 'footer' | 'footer'
		"iframe" | 'inline' | 'inline'
		"frames" | 0        | 'header'
		"frames" | 1        | 'footer'
		"iframe" | 0        | 'inline'
	}

	@Unroll("withFrame changes focus to frame and returns closure return value for selector '#selector'")
	def "withFrame changes focus to frame with given selector and returns closure return value"() {
		given:
		go pagePath
		
		expect:
		getFrameText($(selector)) == text

		where:
		pagePath | selector     | text
		"frames" | '#header-id' | 'header'
		"frames" | '#footer'    | 'footer'
		"iframe" | '#inline'    | 'inline'
	}

	private boolean isInFramesContext() {
		title == 'frames'
	}

	@Unroll
	def "ensure original context is kept after a withFrame call"() {
		when:
		withFrame(frame) {}

		then:
		inFramesContext

		when:
		withFrame(frame) { throw new Exception() }

		then:
		thrown(Exception)
		inFramesContext

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
	static url = "/frames"
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
