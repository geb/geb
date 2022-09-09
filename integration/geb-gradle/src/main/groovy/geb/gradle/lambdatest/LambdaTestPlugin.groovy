/*
 * Copyright 2014 the original author or authors.
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

import geb.gradle.cloud.task.StartExternalTunnel
import geb.gradle.lambdatest.task.DownloadLambdaTestTunnel
import geb.gradle.lambdatest.task.StopLambdaTestTunnel
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync

class LambdaTestPlugin implements Plugin<Project> {

    public static final String CLOSE_TUNNEL_TASK_NAME = 'closeLambdaTestTunnel'
    public static final String OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME = 'openLambdaTestTunnelInBackground'
    public static final String OPEN_TUNNEL_TASK_NAME = 'openLambdaTestTunnel'
    public static final String UNZIP_TUNNEL_TASK_NAME = 'unzipLambdaTestTunnel'
    public static final String DOWNLOAD_TUNNEL_TASK_NAME = 'downloadLambdaTestTunnel'
    Project project

    @Override
    void apply(Project project) {
        this.project = project

        def allLambdaTestTests = project.tasks.register("allLambdaTestTests")

        def lambdaTestExtension = project.extensions.create(
            'lambdaTest', LambdaTestExtension, allLambdaTestTests, "LambdaTest Test"
        )
        lambdaTestExtension.addExtensions()

        addTunnelTasks(lambdaTestExtension)
    }

    void addTunnelTasks(LambdaTestExtension lambdaTestExtension) {
        def downloadLambdaTestTunnel = project.tasks.register(DOWNLOAD_TUNNEL_TASK_NAME, DownloadLambdaTestTunnel)

        def unzipLambdaTestTunnel = project.tasks.register(UNZIP_TUNNEL_TASK_NAME, Sync) {
            dependsOn downloadLambdaTestTunnel
            into(project.file("${project.buildDir}/lambdatest/unzipped"))
            from(project.zipTree(downloadLambdaTestTunnel.map { it.outputs.files.singleFile }))
        }

        def closeLambdaTestTunnel = project.tasks.register(CLOSE_TUNNEL_TASK_NAME, StopLambdaTestTunnel) {
            tunnel = project.lambdaTest.tunnel
            lambdaTestTunnelOps = lambdaTestExtension.local
            onlyIf { lambdaTestExtension.useTunnel }
        }

        def openLambdaTestTunnel = project.tasks.register(OPEN_TUNNEL_TASK_NAME, StartExternalTunnel) {
            onlyIf { lambdaTestExtension.useTunnel }
        }

        def openLambdaTestTunnelInBackground = project.tasks.register(OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME, StartExternalTunnel) {
            inBackground = true
            finalizedBy CLOSE_TUNNEL_TASK_NAME
            onlyIf { lambdaTestExtension.useTunnel }
        }

        [openLambdaTestTunnel, openLambdaTestTunnelInBackground]*.configure {
            dependsOn UNZIP_TUNNEL_TASK_NAME

            tunnel = project.lambdaTest.tunnel
            workingDir = project.buildDir
        }

        [
            downloadLambdaTestTunnel, unzipLambdaTestTunnel, openLambdaTestTunnelInBackground, closeLambdaTestTunnel
        ]*.configure {
            onlyIf { lambdaTestExtension.useTunnel }
        }
    }
}
