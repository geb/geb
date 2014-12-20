/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.frame

import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchFrameException
import geb.Browser
import geb.navigator.Navigator
import geb.content.SimplePageContent
import geb.Page

class DefaultFrameSupport implements FrameSupport {

	Browser browser

	DefaultFrameSupport(Browser browser) {
		this.browser = browser
	}

	def withFrame(frame, Class<? extends Page> page = null, Closure block) {
		executeWithFrame(frame, page, block)
	}

	def withFrame(frame, Page page, Closure block) {
		executeWithFrame(frame, page, block)
	}

	def withFrame(Navigator frameNavigator, Class<? extends Page> page = null, Closure block) {
		executeWithFrame(frameNavigator, page, block)
	}

	def withFrame(Navigator frameNavigator, Page page, Closure block) {
		executeWithFrame(frameNavigator, page, block)
	}

	def withFrame(SimplePageContent frame, Closure block) {
		executeWithFrame(frame, frame.templateParams.page, block)
	}

	private def executeWithFrame(frame, def page, Closure block) {
		def originalPage = browser.page
		browser.driver.switchTo().frame(frame)
		if (page) {
			browser.page(page)
		}
		try {
			block.call()
		} finally {
			browser.page(originalPage)
			browser.driver.switchTo().defaultContent()
		}
	}

	private def executeWithFrame(Navigator frameNavigator, def page, Closure block) {
		WebElement element = frameNavigator.firstElement()
		if (element == null) {
			throw new NoSuchFrameException("No elements for given content: ${frameNavigator}")
		}
		executeWithFrame(element, page, block)
	}
}
