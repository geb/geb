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

class SaucePlugin implements Plugin<Project> {

    public static final String CLOSE_TUNNEL_TASK_NAME = 'closeSauceTunnel'
    public static final String OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME = 'openSauceTunnelInBackground'
    public static final String UNPACK_CONNECT_TASK_NAME = 'unpackSauceConnect'
    Project project

    @Override
    void apply(Project project) {
        this.project = project

        def allSauceLabsTests = project.tasks.register("allSauceLabsTests")

        def sauceLabsExtension = project.extensions.create(
            'sauceLabs', SauceLabsExtension, allSauceLabsTests, "Sauce Test"
        )

        addTunnelTasks(sauceLabsExtension)
    }

    void addTunnelTasks(SauceLabsExtension sauceLabsExtension) {
        project.configurations.create('sauceConnect')
        def unpackSauceConnect = project.tasks.register(UNPACK_CONNECT_TASK_NAME, UnpackSauceConnect)

        sauceLabsExtension.connect.executable.from(unpackSauceConnect)

        project.tasks.register(CLOSE_TUNNEL_TASK_NAME, StopExternalTunnel) {
            tunnel = project.sauceLabs.connect
        }

        def openSauceTunnel = project.tasks.register('openSauceTunnel', StartExternalTunnel)

        def openSauceTunnelInBackground = project.tasks.register(OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME, StartExternalTunnel) {
            inBackground = true
            finalizedBy CLOSE_TUNNEL_TASK_NAME
        }

        [openSauceTunnel, openSauceTunnelInBackground]*.configure {
            tunnel = sauceLabsExtension.connect
            workingDir = project.buildDir
        }
    }
}
