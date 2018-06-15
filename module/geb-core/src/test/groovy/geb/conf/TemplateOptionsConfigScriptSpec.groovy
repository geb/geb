/*
 * Copyright 2018 the original author or authors.
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
package geb.conf

import configuration.InlineConfigurationLoader
import geb.error.InvalidGebConfiguration
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class TemplateOptionsConfigScriptSpec extends Specification implements InlineConfigurationLoader {

    @Rule
    TemporaryFolder temporaryFolder

    def "invalid waitCondition default template option configuration value"() {
        given:
        configScript """
            templateOptions {
                waitCondition = "foo"
            }
        """

        when:
        config.templateOptions

        then:
        InvalidGebConfiguration e = thrown()
        e.message == "Configuration for waitCondition template option should be a closure but found \"foo\""
    }

    @Unroll
    def "invalid min default template option configuration value"() {
        given:
        configScript """
            templateOptions {
                min = $value
            }
        """

        when:
        config.templateOptions

        then:
        InvalidGebConfiguration e = thrown()
        e.message == "Configuration for min template option should be a non-negative integer but found \"$errorValue\""

        where:
        value   | errorValue
        "'foo'" | "foo"
        -1      | -1
    }

    @Unroll
    def "conflicting min and required template option configuration"() {
        given:
        configScript """
            templateOptions {
                min = $min
                required = $required
            }
        """

        when:
        config.templateOptions

        then:
        InvalidGebConfiguration e = thrown()
        e.message == "Configuration for bounds and 'required' template options is conflicting"

        where:
        min | required
        0   | true
        1   | false
    }

}
