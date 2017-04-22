/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.test

import geb.Browser
import spock.lang.Shared

abstract class GebSpecWithServer extends GebSpec {

    @Shared
    TestHttpServer server

    private static final List<Integer> CROSS_BROWSER_PORTS = [8000, 8080, 9000, 9090, 9999]

    def setupSpec() {
        server = serverInstance
        server.start(testPort)
        browser.baseUrl = server.baseUrl
    }

    abstract TestHttpServer getServerInstance()

    int getTestPort() {
        def portIndex = System.getProperty("geb.port.index")
        portIndex ? CROSS_BROWSER_PORTS[portIndex.toInteger()] : 0
    }

    Browser createBrowser() {
        def browser = super.createBrowser()
        if (server) {
            browser.baseUrl = server.baseUrl
        }
        browser
    }

    def cleanupSpec() {
        server?.stop()
    }
}