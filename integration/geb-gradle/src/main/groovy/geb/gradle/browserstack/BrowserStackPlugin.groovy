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

        def allBrowserStackTests = project.task("allBrowserStackTests") {
            group "BrowserStack Test"
        }

        def browserStackExtension = project.extensions.create('browserStack', BrowserStackExtension, project, allBrowserStackTests)
        browserStackExtension.addExtensions()

        addTunnelTasks(browserStackExtension)
    }

    void addTunnelTasks(BrowserStackExtension browserStackExtension) {
        def downloadBrowserStackTunnel = project.task('downloadBrowserStackTunnel', type: DownloadBrowserStackTunnel)

        def unzipBrowserStackTunnel = project.task(UNZIP_TUNNEL_TASK_NAME, type: Sync) {
            dependsOn downloadBrowserStackTunnel

            from(project.zipTree(downloadBrowserStackTunnel.outputs.files.singleFile))
            into(project.file("${project.buildDir}/browserstack/unzipped"))
        }

        def closeBrowserStackTunnel = project.task(CLOSE_TUNNEL_TASK_NAME, type: StopExternalTunnel) {
            tunnel = project.browserStack.tunnel
        }

        def openBrowserStackTunnel = project.task('openBrowserStackTunnel', type: StartExternalTunnel)

        def openBrowserStackTunnelInBackground = project.task(OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME, type: StartExternalTunnel) {
            inBackground = true
            finalizedBy CLOSE_TUNNEL_TASK_NAME
        }

        [openBrowserStackTunnel, openBrowserStackTunnelInBackground].each {
            it.configure {
                dependsOn UNZIP_TUNNEL_TASK_NAME

                tunnel = project.browserStack.tunnel
                workingDir = project.buildDir
            }
        }

        [downloadBrowserStackTunnel, unzipBrowserStackTunnel, openBrowserStackTunnelInBackground, closeBrowserStackTunnel].each {
            it.configure {
                onlyIf { browserStackExtension.useTunnel }
            }
        }
    }
}
