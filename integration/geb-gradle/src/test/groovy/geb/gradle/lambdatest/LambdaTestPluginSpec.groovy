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
package geb.gradle.lambdatest

import geb.gradle.PluginSpec

import static LambdaTestPlugin.CLOSE_TUNNEL_TASK_NAME
import static LambdaTestPlugin.OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME
import static LambdaTestPlugin.UNZIP_TUNNEL_TASK_NAME
import static LambdaTestPlugin.DOWNLOAD_TUNNEL_TASK_NAME
import static org.gradle.testkit.runner.TaskOutcome.SKIPPED

class LambdaTestPluginSpec extends PluginSpec {

    def "tunnel related tasks are skipped if tunnel is disabled"() {
        given:
        buildScript """
            plugins {
                id 'geb-lambdatest'
            }

            lambdaTest {
                useTunnel = false
            }
        """

        when:
        def buildResult = runBuild(OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME)

        then:
        with(buildResult) {
            task(":$DOWNLOAD_TUNNEL_TASK_NAME").outcome == SKIPPED
            task(":$UNZIP_TUNNEL_TASK_NAME").outcome == SKIPPED
            task(":$OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME").outcome == SKIPPED
            task(":$CLOSE_TUNNEL_TASK_NAME").outcome == SKIPPED
        }
    }

}
