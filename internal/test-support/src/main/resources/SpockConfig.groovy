import geb.test.CrossBrowser
import geb.test.Android
import geb.test.RequiresRealBrowser

def crossBrowser = System.getProperty("geb.saucelabs.browser") || System.getProperty("geb.browserstack.browser")
if (crossBrowser) {
    def android = System.getProperty("geb.browserstack.browser")?.contains("android")
    runner {
        include android ? Android : CrossBrowser
    }
} else {
    runner {
        exclude RequiresRealBrowser
    }
}