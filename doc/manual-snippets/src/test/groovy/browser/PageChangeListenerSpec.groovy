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
package browser

import fixture.DriveMethodSupportingSpecWithServer
import geb.Page
import geb.PageChangeListener
import geb.Browser

class PageChangeListenerSpec extends DriveMethodSupportingSpecWithServer {

    def "registering a page change listener"() {
        given:
        // tag::using_page_change_listener[]
        def listener = new RecordingPageChangeListener()
        def browser = new Browser()

        // end::using_page_change_listener[]
        browser.baseUrl = server.baseUrl
        // tag::using_page_change_listener[]
        browser.page(FirstPage)
        browser.registerPageChangeListener(listener)

        // end::using_page_change_listener[]
        expect:
        // tag::using_page_change_listener[]
        assert listener.oldPage == null
        assert listener.newPage instanceof FirstPage

        // end::using_page_change_listener[]
        when:
        // tag::using_page_change_listener[]
        browser.page(SecondPage)

        // end::using_page_change_listener[]
        then:
        // tag::using_page_change_listener[]
        assert listener.oldPage instanceof FirstPage
        assert listener.newPage instanceof SecondPage
        // end::using_page_change_listener[]

        // tag::removing_listener[]
        browser.removePageChangeListener(listener)
        // end::removing_listener[]
    }
}

// tag::helper_classes[]
class RecordingPageChangeListener implements PageChangeListener {
    Page oldPage
    Page newPage

    @Override
    void pageWillChange(Browser browser, Page oldPage, Page newPage) {
        this.oldPage = oldPage
        this.newPage = newPage
    }
}

class FirstPage extends Page {
}

class SecondPage extends Page {
}
// end::helper_classes[]
