import org.openqa.selenium.chrome.ChromeOptions
import org.testcontainers.Testcontainers
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.utility.ResourceReaper

import static org.testcontainers.containers.BrowserWebDriverContainer.getImageForCapabilities

reportsDir = "build/geb"

Testcontainers.exposeHostPorts(8080)

driver = {
    def container = new BrowserWebDriverContainer<>(getImageForCapabilities(new ChromeOptions(), "3.141.59-oxygen"))
            .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.SKIP, null)

    container.start()

    ResourceReaper.instance().registerContainerForCleanup(container.containerId, container.dockerImageName)

    container.webDriver
}

testHttpServerHost = "host.testcontainers.internal"

testHttpServerPortHandler = { int port -> Testcontainers.exposeHostPorts(port) }