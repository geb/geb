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
package geb.gradle.saucelabs

import geb.gradle.cloud.task.StartExternalTunnel
import geb.gradle.cloud.task.StopExternalTunnel
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class SaucePlugin implements Plugin<Project> {

    public static final String CLOSE_TUNNEL_TASK_NAME = 'closeSauceTunnel'
    public static final String OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME = 'openSauceTunnelInBackground'
    public static final String UNPACK_CONNECT_TASK_NAME = 'unpackSauceConnect'
    Project project

    @Override
    void apply(Project project) {
        this.project = project

        def allSauceLabsTests = project.task("allSauceLabsTests") {
            group "Sauce Test"
        }

        project.configurations.create('sauceConnect')

        def sauceLabsExtension = project.extensions.create('sauceLabs', SauceLabsExtension, project, allSauceLabsTests)

        project.task(UNPACK_CONNECT_TASK_NAME, type: UnpackSauceConnect) { Task task ->
            task.onlyIf { sauceLabsExtension.useTunnel }
        }

        sauceLabsExtension.addExtensions()

        addTunnelTasks(sauceLabsExtension)
    }

    void addTunnelTasks(SauceLabsExtension sauceLabsExtension) {
        project.task(CLOSE_TUNNEL_TASK_NAME, type: StopExternalTunnel) {
            tunnel = project.sauceLabs.connect
            onlyIf { sauceLabsExtension.useTunnel }
        }

        def openSauceTunnel = project.task('openSauceTunnel', type: StartExternalTunnel)

        def openSauceTunnelInBackground = project.task(OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME, type: StartExternalTunnel) {
            inBackground = true
            finalizedBy CLOSE_TUNNEL_TASK_NAME
            onlyIf { sauceLabsExtension.useTunnel }
        }

        [openSauceTunnel, openSauceTunnelInBackground].each {
            it.configure {
                dependsOn UNPACK_CONNECT_TASK_NAME
                tunnel = project.sauceLabs.connect
                workingDir = project.buildDir
            }
        }
    }
}
