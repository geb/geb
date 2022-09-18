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
package geb.gradle.browserstack

import geb.gradle.browserstack.task.DownloadBrowserStackTunnel
import geb.gradle.cloud.task.StartExternalTunnel
import geb.gradle.cloud.task.StopExternalTunnel
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync

class BrowserStackPlugin implements Plugin<Project> {

    public static final String CLOSE_TUNNEL_TASK_NAME = 'closeBrowserStackTunnel'
    public static final String OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME = 'openBrowserStackTunnelInBackground'
    public static final String UNZIP_TUNNEL_TASK_NAME = 'unzipBrowserStackTunnel'

    Project project

    @Override
    void apply(Project project) {
        this.project = project

        def allBrowserStackTests = project.tasks.register("allBrowserStackTests")

        def browserStackExtension = project.extensions.create(
            'browserStack', BrowserStackExtension, allBrowserStackTests, "BrowserStack Test"
        )

        addTunnelTasks(browserStackExtension)
    }

    void addTunnelTasks(BrowserStackExtension browserStackExtension) {
        def downloadBrowserStackTunnel = project.tasks.register('downloadBrowserStackTunnel', DownloadBrowserStackTunnel)

        def unzipBrowserStackTunnel = project.tasks.register(UNZIP_TUNNEL_TASK_NAME, Sync) {
            from(
                project.files(
                    project.zipTree(downloadBrowserStackTunnel.map { it.outputs.files.singleFile })
                ).builtBy(downloadBrowserStackTunnel)
            )
            into(project.file("${project.buildDir}/browserstack/unzipped"))
        }

        browserStackExtension.tunnel.executable.from(unzipBrowserStackTunnel)

        project.tasks.register(CLOSE_TUNNEL_TASK_NAME, StopExternalTunnel) {
            tunnel = browserStackExtension.tunnel
        }

        def openBrowserStackTunnel = project.tasks.register('openBrowserStackTunnel', StartExternalTunnel)

        def openBrowserStackTunnelInBackground = project.tasks.register(OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME, StartExternalTunnel) {
            inBackground = true
            finalizedBy CLOSE_TUNNEL_TASK_NAME
        }

        [openBrowserStackTunnel, openBrowserStackTunnelInBackground]*.configure {
            tunnel = browserStackExtension.tunnel
            workingDir = project.buildDir
        }
    }
}
