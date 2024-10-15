/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
import geb.test.browsers.Chrome
import geb.test.browsers.ChromeLinux
import geb.test.browsers.Edge
import geb.test.browsers.FirefoxLinux
import geb.test.browsers.CrossBrowser
import geb.test.browsers.Android
import geb.test.browsers.Firefox
import geb.test.browsers.InternetExplorerAndEdge
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
            includes << InternetExplorerAndEdge
            includes << InternetExplorer
        }
        if (cloudBrowserSpecification.contains("edge")) {
            includes << InternetExplorerAndEdge
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