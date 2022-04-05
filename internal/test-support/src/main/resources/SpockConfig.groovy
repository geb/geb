import geb.test.browsers.Chrome
import geb.test.browsers.ChromeLinux
import geb.test.browsers.Edge
import geb.test.browsers.FirefoxLinux
import geb.test.browsers.CrossBrowser
import geb.test.browsers.Android
import geb.test.browsers.Firefox
import geb.test.browsers.InternetExplorer
import geb.test.browsers.LocalChrome
import geb.test.browsers.RequiresRealBrowser
import geb.test.browsers.Safari

def cloudBrowserSpecification = System.getProperty("geb.saucelabs.browser") ?: System.getProperty("geb.browserstack.browser") ?: System.getProperty("geb.lambdatest.browser")
def dockerizedDriver = System.getProperty("geb.dockerized.driver")
def localDriver = System.getProperty("geb.local.driver")
if (cloudBrowserSpecification) {
    def includes = []
    if (cloudBrowserSpecification.contains("realMobile")) {
        includes << Android
    } else  {
        includes << CrossBrowser
        if (cloudBrowserSpecification.contains("safari")) {
            includes << Safari
        }
        if (cloudBrowserSpecification.contains("explorer")) {
            includes << InternetExplorer
        }
        if (cloudBrowserSpecification.contains("edge")) {
            includes << InternetExplorer
            includes << Edge
        }
        if (cloudBrowserSpecification.contains("firefox")) {
            includes << Firefox
        }
        if (cloudBrowserSpecification.contains("chrome")) {
            includes << Chrome
        }
    }
    runner {
        include(*includes)
    }
} else if (dockerizedDriver) {
    def includes = []

    if (dockerizedDriver == "chrome") {
        includes << Chrome << ChromeLinux
    }
    if (dockerizedDriver == "firefox") {
        includes << Firefox << FirefoxLinux
    }

    runner {
        include(CrossBrowser, *includes)
    }
} else if (localDriver == "chrome") {
    runner {
        include(LocalChrome)
    }
} else {
    runner {
        exclude RequiresRealBrowser
    }
}