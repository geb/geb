package geb.frame

import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchFrameException
import geb.Browser
import geb.navigator.Navigator
import geb.content.SimplePageContent
import geb.Page

class FrameSupport {

	Browser browser

	FrameSupport(Browser browser) {
		this.browser = browser
	}

	def withFrame(frame, Closure block) {
		withFrame(frame, null, block)
	}

	def withFrame(frame, Class<? extends Page> page, Closure block) {
		def currentPage
		browser.driver.switchTo().frame(frame)
		if (page) {
			currentPage = browser.page
			browser.page(page)
		}
		try {
			block.call()
		} finally {
			if (currentPage) {
				browser.page(currentPage)
			}
			browser.driver.switchTo().defaultContent()
		}
	}

	private withFrameForContent(content, Class<? extends Page> page, Closure block) {
		WebElement element = content.firstElement()
		if (element == null) {
			throw new NoSuchFrameException("No elements for given content: $content")
		}
		withFrame(element, page, block)
	}

	def withFrame(Navigator frame, Class<? extends Page> page, Closure block) {
		withFrameForContent(frame, page, block)
	}

	def withFrame(Navigator frame, Closure block) {
		withFrame(frame, null, block)
	}

	def withFrame(SimplePageContent frame, Class<? extends Page> page, Closure block) {
		withFrameForContent(frame, page, block)
	}

	def withFrame(SimplePageContent frame, Closure block) {
		withFrame(frame, frame.pageParameter, block)
	}
}
