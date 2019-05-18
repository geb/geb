import geb.buildadapter.BuildAdapterFactory
import geb.driver.BrowserStackDriverFactory
import geb.driver.SauceLabsDriverFactory
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.testcontainers.Testcontainers
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.shaded.org.apache.commons.io.FileUtils
import org.testcontainers.utility.ResourceReaper

testValue = true // used in a test in geb-core

String getForkIndex(int total) {
    String workerName = System.getProperty('org.gradle.test.worker')
    int workerNumber = workerName ? ((workerName =~ /[^\d]*([\d]+)/)[0][1]).toInteger() : 0
    (workerNumber % total).toString()
}

void setPortIndexProperty(String index) {
    System.setProperty('geb.port.index', index)
}

BrowserWebDriverContainer containerForDriver(String driverName) {
    def container = new BrowserWebDriverContainer<>()
            .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.SKIP, null)

    switch (driverName) {
        case "chrome":
            container.withCapabilities(new ChromeOptions())
            break
        case "firefox":
            container.withCapabilities(new FirefoxOptions())
                .withSharedMemorySize(2 * FileUtils.ONE_GB)
            break
        default:
            throw new Exception("Unsupported dockerized driver: $driverName")
    }

    container.start()

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

if (!BuildAdapterFactory.getBuildAdapter(this.class.classLoader).reportsDir) {
    reportsDir = "build/geb"
}

def sauceLabsBrowser = System.getProperty("geb.saucelabs.browser")
if (sauceLabsBrowser) {
    setPortIndexProperty(getForkIndex(5))
    driver = {
        def username = System.getenv("GEB_SAUCE_LABS_USER")
        assert username
        def accessKey = System.getenv("GEB_SAUCE_LABS_ACCESS_PASSWORD")
        assert accessKey
        def tunnelId = System.getProperty("geb.saucelabs.tunnelId")
        assert tunnelId
        new SauceLabsDriverFactory().create(sauceLabsBrowser, username, accessKey, ["tunnel-identifier": tunnelId])
    }
}

def browserStackBrowser = System.getProperty("geb.browserstack.browser")
if (browserStackBrowser) {
    setPortIndexProperty(getForkIndex(5))
    driver = {
        def username = System.getenv("GEB_BROWSERSTACK_USERNAME")
        assert username
        def accessKey = System.getenv("GEB_BROWSERSTACK_AUTHKEY")
        assert accessKey
        def tunnelId = System.getenv("GEB_BROWSERSTACK_LOCALID")
        assert tunnelId
        new BrowserStackDriverFactory().create(browserStackBrowser, username, accessKey, ["browserstack.localIdentifier": tunnelId])
    }

    if (browserStackBrowser.contains("realMobile")) {
        testHttpServerHost = findLocalIp()
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

def devDriver = System.getProperty("geb.dev.driver")
if (devDriver != "htmlunit") {
    driver = devDriver
}