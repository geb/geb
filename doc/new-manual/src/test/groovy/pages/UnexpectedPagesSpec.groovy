/*
 * Copyright 2015 the original author or authors.
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
package pages

import geb.Page
import geb.error.UnexpectedPageException
import geb.test.GebSpecWithCallbackServer

class UnexpectedPagesSpec extends GebSpecWithCallbackServer {

    String getUnexpectedPagesConfig() {
        """
        import pages.PageNotFoundPage

        // tag::config[]
        unexpectedPages = [PageNotFoundPage]
        // end::config[]
        """
    }

    def setup() {
        browser.config.rawConfig.merge(new ConfigSlurper().parse(unexpectedPagesConfig))
        html {
            p(id: "error-message", "Sorry but we could not find that page")
        }
    }

    @SuppressWarnings(["ConstantAssertExpression", "BracesForTryCatchFinally"])
    def "using unexpected page"() {
        expect:
        // tag::usage[]
        try {
            at ExpectedPage
            assert false //should not get here
        } catch (UnexpectedPageException e) {
            assert e.message.startsWith("An unexpected page ${PageNotFoundPage.name} was encountered")
        }
        // end::usage[]
    }

    def "checking for unexpected page"() {
        when:
        // tag::checking_for_unexpected_page[]
        at PageNotFoundPage
        // end::checking_for_unexpected_page[]

        then:
        noExceptionThrown()
    }
}

// tag::unexpected_page[]
class PageNotFoundPage extends Page {
    static at = { $("#error-message").text() == "Sorry but we could not find that page" }
}
// end::unexpected_page[]

class ExpectedPage extends Page {
    static at = { }
}

