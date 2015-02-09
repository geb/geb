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
package intro

import geb.driver.CachingDriverFactory

// tag::imports[]
import geb.spock.GebSpec
// end::imports[]
import intro.page.GebHomePage

// tag::class[]
class GebHomepageSpec extends GebSpec {

    def setup() {
        browser.driver.javascriptEnabled = false
    }

    def cleanup() {
        CachingDriverFactory.clearCache()
    }

    def "can access The Book of Geb via homepage"() {
        when:
        to GebHomePage

        and:
        highlights.jQueryLikeApi.click()

        then:
        sectionTitles == ["Navigating Content", "Form Control Shortcuts"]
        highlights.jQueryLikeApi.selected
    }
}
// end::class[]
