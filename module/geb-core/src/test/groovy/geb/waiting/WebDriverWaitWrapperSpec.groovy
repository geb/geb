package geb.waiting
import org.openqa.selenium.By
import org.openqa.selenium.TimeoutException
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import spock.lang.Specification

class WebDriverWaitWrapperSpec extends Specification {
    def 'when WebDriverWait throws exception should wrap in Geb exception'() {
        given:
        WebDriver driver = Mock()
        WebDriverWaitWrapper waitWrapper = new WebDriverWaitWrapper(driver, 5)

        WebDriverWait webDriverWait = Mock(constructorArgs: [driver, 5])
        waitWrapper.webDriverWait = webDriverWait

        when:
        waitWrapper.waitUntilElementPresent(By.tagName("html"))

        then:
        1 * webDriverWait.until(_) >> { throw new TimeoutException() }

        WebDriverWaitWrapperException e = thrown()
        e.message.contains("WebDriver wait timeout of 5 seconds exceeded")
    }
}
