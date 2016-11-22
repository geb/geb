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

import geb.Configuration
import geb.navigator.EmptyNavigator
import geb.navigator.NonEmptyNavigator
import geb.test.GebSpecWithCallbackServer
import groovy.transform.InheritConstructors
import org.junit.ClassRule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared

class NavigatorFactoryConfigSpec extends GebSpecWithCallbackServer implements InlineConfigurationLoader {

    @ClassRule
    @Shared
    TemporaryFolder tempDir

    TemporaryFolder getTemporaryFolder() {
        tempDir
    }

    @Override
    Configuration createConf() {
        configScript """
            import configuration.MyCustomNavigator
            import configuration.MyCustomEmptyNavigator
            // tag::config[]
            import geb.Browser
            import org.openqa.selenium.WebElement

            innerNavigatorFactory = { Browser browser, List<WebElement> elements ->
                elements ? new MyCustomNavigator(browser, elements) : new MyCustomEmptyNavigator()
            }
            // end::config[]

            reportsDir = "${super.createConf().reportsDir.absolutePath.replaceAll("\\\\", "\\\\\\\\")}"
        """
        config
    }

    def "specifying custom navigator implementation"() {
        when:
        html {
        }

        then:
        $() instanceof MyCustomNavigator
        $("idontexist") instanceof MyCustomEmptyNavigator
    }
}

@InheritConstructors
class MyCustomNavigator extends NonEmptyNavigator {
}

@InheritConstructors
class MyCustomEmptyNavigator extends EmptyNavigator {
}
