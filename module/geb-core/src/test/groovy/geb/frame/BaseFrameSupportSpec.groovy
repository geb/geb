package geb.frame

import geb.test.GebSpecWithServer

abstract class BaseFrameSupportSpec extends GebSpecWithServer {

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
		to FrameSupportSpecPage
	}

	protected String getFrameText(frame) {
		withFrame(frame) {
			$("span").text()
		}
	}
}
