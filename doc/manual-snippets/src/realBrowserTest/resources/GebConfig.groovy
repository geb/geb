import org.openqa.selenium.chrome.ChromeOptions
import org.testcontainers.Testcontainers
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.utility.ResourceReaper

reportsDir = "build/geb"

Testcontainers.exposeHostPorts(8000, 8080, 9000, 9090, 9999)

driver = {
    def container = new BrowserWebDriverContainer<>()
            .withCapabilities(new ChromeOptions())
            .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.SKIP, null)

    container.start()

    ResourceReaper.instance().registerContainerForCleanup(container.containerId, container.dockerImageName)

    container.webDriver
}

testHttpServerHost = "host.testcontainers.internal"

testHttpServerPortHandler = { int port -> Testcontainers.exposeHostPorts(port) }