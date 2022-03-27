/*
 * Copyright 2020 the original author or authors.
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
package geb

import geb.error.IncorrectDriverTypeException
import geb.test.GebSpecWithCallbackServer
import geb.test.browsers.*
import org.openqa.selenium.UnsupportedCommandException

import java.time.Duration

class BrowserNetworkLatencySpec extends GebSpecWithCallbackServer {

    def "setting network latency using htmlunit driver results in a IncorrectDriverTypeException"() {
        when:
        networkLatency = Duration.ofMillis(1)

        then:
        def e = thrown(IncorrectDriverTypeException)
        e.message =~ /This operation is only possible on instances of RemoteWebDriver but org\.openqa\.selenium\.htmlunit\.HtmlUnitDriver.* was passed/
    }

    @Firefox
    @InternetExplorerAndEdge
    @Safari
    @Chrome
    @RequiresRealBrowser
    def "setting network latency using a driver other than chrome and htmlunit results in an UnsupportedCommandException"() {
        when:
        networkLatency = Duration.ofMillis(1)

        then:
        thrown(UnsupportedCommandException)
    }

    @RequiresRealBrowser
    @LocalChrome
    def "setting network latency"() {
        given:
        bodyWithJquery {
            button("Execute ajax request")
            script(type: "text/javascript") {
                mkp.yieldUnescaped '''
                    $("button").click(function() {
                        $.ajax({
                            success: function() {
                                $("body").append('<div class="ajax-completed">Completed</div>');
                            }
                        });
                    });
                '''
            }
        }

        and:
        // tag::setNetworkLatency[]
        def networkLatency = Duration.ofMillis(500)
        browser.networkLatency = networkLatency
        // end::setNetworkLatency[]

        when:
        def start = System.nanoTime()
        $("button").click()
        waitFor { $(".ajax-completed") }
        def duration = Duration.ofNanos(System.nanoTime() - start)

        then:
        duration >= networkLatency

        cleanup:
        resetNetworkLatency()
    }
}
