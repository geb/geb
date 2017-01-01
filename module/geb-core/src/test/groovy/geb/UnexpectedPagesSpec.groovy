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
package geb

import geb.error.InvalidGebConfiguration
import geb.error.UnexpectedPageException
import geb.test.GebSpecWithCallbackServer
import spock.lang.Unroll

class UnexpectedPagesSpec extends GebSpecWithCallbackServer {

    def setup() {
        responseHtml { request ->
            def titleValue = request.parameterMap.title.first()
            def delayed = request.parameterMap.delayed
            head {
                if (!delayed) {
                    title(titleValue)
                } else {
                    script(type: "text/javascript", charset: "utf-8", """
                        setTimeout(function() {
                            document.title = "$titleValue";
                        }, 100);
                    """)
                }
            }
        }
    }

    void 'unxepected pages don not need to be defined'() {
        expect:
        to ExpectedPage
    }

    private void defineUnexpectedPages(Class<? extends Page>[] unexpectedPages = ([UnexpectedPage, AnotherUnexpectedPage] as Class<? extends Page>[])) {
        browser.config.unexpectedPages = unexpectedPages.toList()
    }

    @Unroll
    void 'verify that page response is configured as expected'() {
        when:
        go "?title=$pageTitle"

        then:
        title == pageTitle

        where:
        pageTitle << ['expected', 'unexpected']
    }

    void 'verify that page response is configured as expected for unexpected page with wait'() {
        when:
        go "?title=unexpected&delayed=true"

        then:
        !title

        and:
        waitFor { title == "unexpected" }
    }

    void 'an exception is not thrown when we are not at unexpected page'() {
        given:
        defineUnexpectedPages()

        when:
        via ExpectedPage

        then:
        at ExpectedPage
    }

    void 'an exception is not thrown when we are not at unexpected page and one of unexpected pages has atCheckWaiting specified'() {
        given:
        defineUnexpectedPages(UnexpectedPageWithWaiting)

        when:
        via ExpectedPage

        then:
        at ExpectedPage
    }

    void 'unexpected pages are not checked if the atCheck for the expected page succeeds'() {
        given:
        defineUnexpectedPages(ConflictingUnexpectedPage)

        when:
        via ExpectedPage

        then:
        at ExpectedPage
    }

    void 'an exception is thrown when we end up at an unexpected page'() {
        given:
        defineUnexpectedPages()

        when:
        via UnexpectedPage
        at ExpectedPage

        then:
        UnexpectedPageException e = thrown()
        e.getMessage() == 'An unexpected page geb.UnexpectedPage was encountered when expected to be at geb.ExpectedPage'
    }

    void 'atCheckWaiting configuration on page level is honoured for pages configured as unexpected'() {
        given:
        defineUnexpectedPages(UnexpectedPageWithWaiting)

        when:
        via UnexpectedPageWithWaiting
        at ExpectedPage

        then:
        UnexpectedPageException e = thrown()
        e.getMessage() == 'An unexpected page geb.UnexpectedPageWithWaiting was encountered when expected to be at geb.ExpectedPage'
    }

    void 'it is possible to do at checking for an unexpected page'() {
        given:
        defineUnexpectedPages()

        when:
        via UnexpectedPage

        then:
        at UnexpectedPage
    }

    void 'an exception is thrown when we end up on an unexpected page when setting a page from a list of possible pages'() {
        given:
        defineUnexpectedPages()

        when:
        via UnexpectedPage
        page(ExpectedPage, AnotherExpectedPage)

        then:
        UnexpectedPageException e = thrown()
        e.getMessage() == 'An unexpected page geb.UnexpectedPage was encountered when trying to find page match (given potentials: [class geb.ExpectedPage, class geb.AnotherExpectedPage])'
    }

    void 'an exception is thrown when we end up on an unexpected page when setting a page from a list of possible parametrized page instances'() {
        given:
        defineUnexpectedPages()

        when:
        via UnexpectedPage
        page(new ParametrizedPage(condition: true), new ParametrizedPage(condition: true))

        then:
        UnexpectedPageException e = thrown()
        e.getMessage() == "An unexpected page geb.UnexpectedPage was encountered when trying to find page match (given potentials: [${ParametrizedPage.name}, ${ParametrizedPage.name}])"
    }

    void 'it is possible to pass an unexpected page when setting a page from a list of possible pages'() {
        given:
        defineUnexpectedPages()

        when:
        via AnotherUnexpectedPage
        page(ExpectedPage, AnotherExpectedPage, AnotherUnexpectedPage)

        then:
        page.getClass() == AnotherUnexpectedPage
    }

    void 'isAt returns false if we end up at an unexpected page'() {
        given:
        defineUnexpectedPages()

        when:
        via UnexpectedPage

        then:
        !isAt(ExpectedPage)
    }

    void 'when at-check-waiting enabled should not wait for unexpected pages'() {
        given:
        defineUnexpectedPages()
        browser.config.atCheckWaiting = true

        when:
        via UnexpectedPageWithWaiting

        then:
        at(UnexpectedPageWithWaiting)
    }

    @Unroll
    void "an informative exception is thrown when unexpected pages config list contains something that is not a page class"() {
        given:
        browser.config.rawConfig.unexpectedPages = configValue

        when:
        browser.config.unexpectedPages

        then:
        InvalidGebConfiguration e = thrown()
        e.message == "Unexpected pages configuration has to be a collection of classes that extend ${Page.name} but found \"$message\". Did you forget to include some imports in your config file?"

        where:
        configValue    | message
        ["foo"]        | '[foo]'
        "foo"          | 'foo'
        UnexpectedPage | "class geb.UnexpectedPage"
    }
}

class UnexpectedPage extends Page {

    static url = "?title=unexpected"

    static at = { title == 'unexpected' }
}

class UnexpectedPageWithWaiting extends Page {

    static atCheckWaiting = 0.2

    static url = "?title=unexpected&delayed=true"

    static at = { title == 'unexpected' }
}

class AnotherUnexpectedPage extends Page {

    static url = "?title=anotherUnexpected"

    static at = { title == 'anotherUnexpected' }
}

class ExpectedPage extends Page {

    static url = "?title=expected"

    static at = { title == 'expected' }
}

class AnotherExpectedPage extends Page {
    static url = "?title=anotherExpected"

    static at = { title == 'anotherExpected' }
}

class ConflictingUnexpectedPage extends Page {

    static url = "?title=expected"

    static at = { title == 'expected' }
}

class ParametrizedPage extends Page {
    boolean condition

    static at = { condition }
}
