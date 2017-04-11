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

import geb.Browser
import geb.Page
import geb.content.TemplateDerivedPageContent
import geb.navigator.Navigator
import org.openqa.selenium.NoSuchFrameException
import org.openqa.selenium.WebElement

import static groovy.lang.Closure.DELEGATE_FIRST

class DefaultFrameSupport implements FrameSupport {

    Browser browser

    DefaultFrameSupport(Browser browser) {
        this.browser = browser
    }

    public <T> T withFrame(frame, Class<? extends Page> page = null, Closure<T> block) {
        executeWithFrame(frame, page, block)
    }

    public <T> T withFrame(frame, Page page, Closure<T> block) {
        executeWithFrame(frame, page, block)
    }

    public <T> T withFrame(Navigator frameNavigator, Class<? extends Page> page = null, Closure<T> block) {
        executeWithFrame(frameNavigator, page, block)
    }

    public <T> T withFrame(Navigator frameNavigator, Page page, Closure<T> block) {
        executeWithFrame(frameNavigator, page, block)
    }

    public <T> T withFrame(TemplateDerivedPageContent frame, Closure<T> block) {
        executeWithFrame(frame, frame.templateParams.page, block)
    }

    private <T> T executeWithFrame(frame, def page, Closure<T> block) {
        def originalPage = browser.page
        browser.driver.switchTo().frame(frame)
        if (page) {
            if (page.at) {
                browser.at(page)
            } else {
                browser.page(page)
            }
        }
        try {
            Closure cloned = block.clone()
            cloned.delegate = new WithFrameDelegate(browser)
            cloned.resolveStrategy = DELEGATE_FIRST
            cloned.call()
        } finally {
            browser.page(originalPage)
            browser.driver.switchTo().defaultContent()
        }
    }

    private <T> T executeWithFrame(Navigator frameNavigator, def page, Closure<T> block) {
        WebElement element = frameNavigator.firstElement()
        if (element == null) {
            throw new NoSuchFrameException("No elements for given content: ${frameNavigator}")
        }
        executeWithFrame(element, page, block)
    }
}
