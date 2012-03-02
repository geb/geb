package geb.frame

import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchFrameException
import geb.Browser
import geb.navigator.Navigator
import geb.content.SimplePageContent
import geb.Page
import geb.waiting.WaitTimeoutException

class FrameSupport {

	Browser browser

	FrameSupport(Browser browser) {
		this.browser = browser
	}

	def withFrame(frame, Closure block) {
		browser.driver.switchTo().frame(frame)
		try {
			block.call()
		} finally {
			browser.driver.switchTo().defaultContent()
		}
	}

	private withFrameForContent(content, Closure block) {
		withFrame(getElement(content), block)
	}

	private WebElement getElement(content) {
		WebElement element = content.firstElement()
		if (element == null) {
			throw new NoSuchFrameException("No elements for given content: $content")
		}
		return element
	}

	def withFrame(Navigator frame, Closure block) {
		withFrameForContent(frame, block)
	}

	def withFrame(SimplePageContent frame, Closure block) {
		withFrameForContent(frame, block)
	}

	/**
	 * Changes the browser's focus to the given frame and calls {@link geb.Browser#page} on the given page.
	 * @param content A {@link geb.navigator.Navigator}, {@link geb.content.SimplePageContent}, int or String.
	 * 		If a Navigator or SimplePageContent, the first WebElement is used.
	 * @param newPage a Class object that extends {@link geb.Page}, and Page instance, or an array of potential
	 * 		page classes.
	 */
	void frame(content, newPage) {
		switchToFrame(content)
		browser.page(newPage)
	}

	/**
	 * Changes focus to the frame or iframe matched by the given content, waiting for the content nav
	 * to return true first is necessary.
	 * @param content
	 * @return
	 */
	protected switchToFrame(content) {
		def element
		if ((content instanceof Navigator) || (content instanceof SimplePageContent)) {
			//if waitFor fails, throw a more descriptive exception
			try {
				browser.page.waitFor{content}
			} catch (WaitTimeoutException e) {
				throw new NoSuchFrameException("No elements for given content: $content")
			}
			element = getElement(content)
		}
		else
			element = content
		browser.driver.switchTo().frame(element)
	}
}
