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

class FrameSupport {

	Browser browser

	FrameSupport(Browser browser) {
		this.browser = browser
	}

	def withFrame(frame, Closure block) {
		withFrame(frame, null, block)
	}

	def withFrame(frame, Class<? extends Page> page, Closure block) {
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

	def withFrame(SimplePageContent frame, Closure block) {
		withFrame(frame, frame.templateParams.page, block)
	}
}
