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
package geb

import geb.error.InvalidPageContent
import geb.test.GebSpecWithCallbackServer
import spock.lang.Unroll

class PageContentNameSpec extends GebSpecWithCallbackServer {

    def setup() {
        html {
        }
    }

    @Unroll("using '#contentName' as content name causes an exception")
    def "using content names that will result in the content being shadowed causes an exception"() {
        when:
        to(new DynamicContentNamePage(contentName: contentName))

        then:
        InvalidPageContent e = thrown()
        e.message == "${DynamicContentNamePage.name} uses a not allowed content name: '$contentName'. Please use another name."

        where:
        contentName << ["x", "title", "focused", "at", "url", "base", "content", "owner"]
    }

}

class DynamicContentNamePage extends Page {
    String contentName

    static content = {
        "$contentName" { $() }
    }
}
