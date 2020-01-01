import geb.buildadapter.BuildAdapterFactory
import geb.driver.BrowserStackDriverFactory
import geb.driver.LambdaTestDriverFactory
import geb.driver.SauceLabsDriverFactory
import org.openqa.selenium.Capabilities
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.testcontainers.Testcontainers
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.shaded.org.apache.commons.io.FileUtils
import org.testcontainers.utility.ResourceReaper

import static org.testcontainers.containers.BrowserWebDriverContainer.getImageForCapabilities

testValue = true // used in a test in geb-core

String getForkIndex(int total) {
    String workerName = System.getProperty('org.gradle.test.worker')
    int workerNumber = workerName ? ((workerName =~ /[^\d]*([\d]+)/)[0][1]).toInteger() : 0
    (workerNumber % total).toString()
}

void setPortIndexProperty(String index) {
    System.setProperty('geb.port.index', index)
}

BrowserWebDriverContainer containerForCapabilities(Capabilities capabilities) {
    new BrowserWebDriverContainer<>(getImageForCapabilities(capabilities, "3.141.59-oxygen"))
        .withCapabilities(capabilities)
}

BrowserWebDriverContainer containerForDriver(String driverName) {
    def container

    switch (driverName) {
        case "chrome":
            container = containerForCapabilities(new ChromeOptions())
            break
        case "firefox":
            container = containerForCapabilities(new FirefoxOptions())
            container.withSharedMemorySize(2 * FileUtils.ONE_GB)
            break
        default:
            throw new Exception("Unsupported dockerized driver: $driverName")
    }

    container.withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.SKIP, null)
            .start()

    ResourceReaper.instance().registerContainerForCleanup(container.containerId, container.dockerImageName)

    container
}

String findLocalIp() {
    def ip4Addresses = NetworkInterface.networkInterfaces.toList()
            .collectMany { it.inetAddresses.toList() }
            .findAll { it in Inet4Address }
            *.hostAddress

    ip4Addresses.find { it != "127.0.0.1" }
}

driver = "htmlunit"

if (!BuildAdapterFactory.getBuildAdapter(this.class.classLoader).reportsDir) {
    reportsDir = "build/geb"
}

def sauceLabsBrowser = System.getProperty("geb.saucelabs.browser")
if (sauceLabsBrowser) {
    setPortIndexProperty(getForkIndex(5))
    driver = {
        new SauceLabsDriverFactory().create()
    }
}

def browserStackBrowser = System.getProperty("geb.browserstack.browser")
if (browserStackBrowser) {
    setPortIndexProperty(getForkIndex(5))
    driver = {
        new BrowserStackDriverFactory().create()
    }

    if (browserStackBrowser.contains("realMobile")) {
        testHttpServerHost = findLocalIp()
    }
}

def lambdaTestBrowser = System.getProperty("geb.lambdatest.browser")
if (lambdaTestBrowser) {
    setPortIndexProperty(getForkIndex(5))
    driver = {
        new LambdaTestDriverFactory().create()
    }
}

def dockerizedDriver = System.getProperty("geb.dockerized.driver")
if (dockerizedDriver) {
    Testcontainers.exposeHostPorts(8080)

    driver = {
        containerForDriver(dockerizedDriver).webDriver
    }

    testHttpServerHost = "host.testcontainers.internal"

    testHttpServerPortHandler = { int port -> Testcontainers.exposeHostPorts(port) }
}