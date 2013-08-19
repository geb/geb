package geb.window

import geb.test.GebSpecWithServer

abstract class BaseWindowHandlingSpec extends GebSpecWithServer {
	final static String MAIN_PAGE_URL = '/main'

	def mainWindow

	def setup() {
		mainWindow = currentWindow
	}

	def cleanup() {
		def newlyOpenedWindows = availableWindows - mainWindow
		newlyOpenedWindows.each {
			switchToWindow(it)
			driver.close()
		}
		switchToWindow(mainWindow)
	}

	def setupSpec() {
		responseHtml { request ->
			def page = (~'/(.*)').matcher(request.requestURI)[0][1]
			head {
				title("Window $page")
			}
			body {
				[1, 2].each {
					def label = "$page-$it"
					a(id: label, target: "window-$label", href: "/$label", label)
				}
			}
		}
	}

	protected void openAllWindows() {
		go MAIN_PAGE_URL
		$('a')*.click()
		assert availableWindows.size() == 3
	}

	protected boolean isInContextOfMainWindow() {
		title == windowTitle()
	}

	protected String windowTitle(int[] indexes = []) {
		def name = "Window main"
		if (indexes) {
			name += "-" + indexes*.toString().join("-")
		}
		name
	}

	protected String windowName(int[] indexes = []) {
		def name = "window-main"
		if (indexes) {
			name += "-" + indexes*.toString().join("-")
		}
		name
	}

	protected void openWindow(int index) {
		$("a", index - 1).click()
	}
}
