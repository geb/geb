/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.spock

import spock.lang.Stepwise
import spock.lang.Shared
import geb.test.CallbackHttpServer
import javax.servlet.http.HttpServletResponse
import static org.mortbay.jetty.HttpHeaders.*

@Stepwise
class ExceptionOnReportScreenshotSpec extends GebReportingSpec {
    @Shared
        server = new CallbackHttpServer()

    def setupSpec() {
        server.start()
        server.get = { req, HttpServletResponse res ->
            res.addHeader(CONTENT_TYPE, 'application/xml')
            res.outputStream << '''<?xml version="1.0"?>
            <test></test>
            '''
        }
    }

    def setup() {
        baseUrl = server.baseUrl
    }

    def "a request is made"() {
        given:
        go("/") // make a request
    }

    def "an error screenshot is taken"() {
        expect:
        new File(reportGroupDir, '001-001-a request is made-end.png').exists()
    }
}
