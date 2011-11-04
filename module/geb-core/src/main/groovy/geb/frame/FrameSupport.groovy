package geb.frame

import org.openqa.selenium.WebElement
import org.openqa.selenium.NoSuchFrameException
import geb.Browser
import geb.navigator.Navigator
import geb.content.SimplePageContent

class FrameSupport {

    Browser browser

    FrameSupport(Browser browser) {
        this.browser = browser
    }

    void withFrame(def frame, Closure block) {
        if (frame == null) {
            throw new NoSuchFrameException('')
        }
        browser.driver.switchTo().frame(frame)
        block.call()
        browser.driver.switchTo().defaultContent()
    }

    void withFrame(Navigator frame, Closure block) {
        withFrame(frame.firstElement(), block)
    }

    void withFrame(SimplePageContent frame, Closure block) {
        withFrame(frame.firstElement(), block)
    }
}
