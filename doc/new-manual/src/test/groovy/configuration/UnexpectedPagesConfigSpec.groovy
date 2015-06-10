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
package configuration

import geb.Page
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class UnexpectedPagesConfigSpec extends Specification implements InlineConfigurationLoader {

    @Rule
    TemporaryFolder temporaryFolder

    def "unexpected pages"() {
        when:
        configScript """
            import configuration.PageNotFoundPage
            import configuration.InternalServerErrorPage

            // tag::config[]
            unexpectedPages = [PageNotFoundPage, InternalServerErrorPage]
            // end::config[]
        """

        then:
        config.unexpectedPages == [PageNotFoundPage, InternalServerErrorPage]
    }
}

class PageNotFoundPage extends Page {
}

class InternalServerErrorPage extends Page {
}
