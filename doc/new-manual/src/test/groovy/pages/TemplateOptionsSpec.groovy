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
import geb.error.RequiredPageContentNotPresent
import geb.test.GebSpecWithCallbackServer

class TemplateOptionsSpec extends GebSpecWithCallbackServer {

    def "example of using content template options"() {
        given:
        html { }

        when:
        to TemplateOptionsIntroductionPage

        then:
        noExceptionThrown()
    }

    def "required"() {
        given:
        html { }

        expect:
        // tag::required[]
        to PageWithTemplatesUsingRequiredOption

        assert !notRequiredDiv

        def thrown = false
        try {
            page.requiredDiv
        } catch (RequiredPageContentNotPresent e) {
            thrown = true
        }
        assert thrown
        // end::required[]
    }

    def "cache"() {
        given:
        html { }

        expect:
        // tag::cache[]
        to PageWithTemplateUsingCacheOption
        assert notCachedValue == 1
        assert cachedValue == 1

        // end::cache[]

        when:
        // tag::cache[]
        value = 2

        // end::cache[]

        then:
        // tag::cache[]
        assert notCachedValue == 2
        assert cachedValue == 1
        // end::cache[]
    }

    def "to"() {
        given:
        html {
            a("Help")
        }

        expect:
        // tag::to[]
        to PageWithTemplateUsingToOption
        helpLink.click()
        assert page instanceof HelpPage
        // end::to[]
    }

    def "list to"() {
        given:
        html {
            input(class: "loginButton")
        }

        when:
        to PageWithTemplateUsingListToOption
        loginButton.click()

        then:
        page instanceof LoginFailedPage
    }
}

class TemplateOptionsIntroductionPage extends Page {
    static content = {
        // tag::introduction[]
        theDiv(cache: false, required: false) { $("div", id: "a") }
        // end::introduction[]
    }
}

// tag::required_page[]
class PageWithTemplatesUsingRequiredOption extends Page {
    static content = {
        requiredDiv { $("div", id: "b") }
        notRequiredDiv(required: false) { $("div", id: "b") }
    }
}
// end::required_page[]

// tag::cache_page[]
class PageWithTemplateUsingCacheOption extends Page {
    def value = 1
    static content = {
        notCachedValue { value }
        cachedValue(cache: true) { value }
    }
}
// end::cache_page[]

// tag::to_page[]
class PageWithTemplateUsingToOption extends Page {
    static content = {
        helpLink(to: HelpPage) { $("a", text: "Help") }
    }
}

class HelpPage extends Page { }
// end::to_page[]

class PageWithTemplateUsingListToOption extends Page {
    // tag::to_list_page[]
    static content = {
        loginButton(to: [LoginSuccessfulPage, LoginFailedPage]) { $("input.loginButton") }
    }
    // end::to_list_page[]
}

class LoginSuccessfulPage extends Page {
    static at = { false }
}

class LoginFailedPage extends Page {
    static at = { true }
}
