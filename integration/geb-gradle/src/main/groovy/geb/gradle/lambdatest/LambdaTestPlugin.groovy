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
    public static final String UNZIP_TUNNEL_TASK_NAME = 'unzipLambdaTestTunnel'
    public static final String DOWNLOAD_TUNNEL_TASK_NAME = 'downloadLambdaTestTunnel'

    @Override
    void apply(Project project) {
        def allLambdaTestTests = project.tasks.register("allLambdaTestTests")

        def closeTunnel = project.tasks.register(CLOSE_TUNNEL_TASK_NAME, StopLambdaTestTunnel)

        def openLambdaTestTunnelInBackground = project.tasks.register(
            OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME, StartExternalTunnel
        ) {
            inBackground = true
            finalizedBy closeTunnel
        }

        def lambdaTestExtension = project.extensions.create(
            'lambdaTest', LambdaTestExtension, allLambdaTestTests, openLambdaTestTunnelInBackground, closeTunnel,
            "LambdaTest Test"
        )

        closeTunnel.configure {
            tunnelOps = lambdaTestExtension.tunnelOps
        }

        def openLambdaTestTunnel = project.tasks.register('openLambdaTestTunnel', StartExternalTunnel)

        [openLambdaTestTunnel, openLambdaTestTunnelInBackground]*.configure {
            tunnel = lambdaTestExtension.tunnelOps
            workingDir = project.buildDir
        }

        def downloadLambdaTestTunnel = project.tasks.register(DOWNLOAD_TUNNEL_TASK_NAME, DownloadLambdaTestTunnel)

        def unzipLambdaTestTunnel = project.tasks.register(UNZIP_TUNNEL_TASK_NAME, Sync) {
            from(project.zipTree(downloadLambdaTestTunnel.flatMap { it.tunnelZip }))
            into(project.file("${project.buildDir}/lambdatest/unzipped"))
        }

        lambdaTestExtension.tunnelOps.executable.from(unzipLambdaTestTunnel)
    }
}
