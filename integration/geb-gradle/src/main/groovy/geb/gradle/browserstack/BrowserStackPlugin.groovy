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

import geb.gradle.browserstack.task.StartBrowserStackTunnel
import geb.gradle.browserstack.task.StopBrowserStackTunnel
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.testing.Test

class BrowserStackPlugin implements Plugin<Project> {

	Project project

	@Override
	void apply(Project project) {
		this.project = project

		project.extensions.create('browserStack', BrowserStackExtension, project).addExtensions()

		addTunnelTasks()
		addBrowserStackTasks()
	}

	void addBrowserStackTasks() {
		def allBrowserStackTests = project.task("allBrowserStackTests") {
			group "BrowserStack Test"
		}

		project.browserStack.browsers.all { BrowserSpec browser ->
			def testTask = project.task("${browser.displayName}Test", type: Test) { Test task ->
				group allBrowserStackTests.group
				task.dependsOn 'openBrowserStackTunnelInBackground'
				allBrowserStackTests.dependsOn task
				finalizedBy 'closeBrowserStackTunnel'

				systemProperty 'geb.build.reportsDir', project.reporting.file("$name-geb")

				browser.configure(task)
			}

			def decorateReportsTask = project.task("${browser.displayName}DecorateReports", type: Copy) {
				from testTask.reports.junitXml.destination
				into "${testTask.reports.junitXml.destination}-decorated"
				filter { it.replaceAll("(testsuite|testcase) name=\"(.+?)\"", "\$1 name=\"\$2 ($browser.displayName)\"") }
			}

			testTask.finalizedBy decorateReportsTask
		}
	}

	void addTunnelTasks() {
		project.task('closeBrowserStackTunnel', type: StopBrowserStackTunnel) {
			browserStackTunnel = project.browserStack.tunnel
		}

		def openBrowserStackTunnel = project.task('openBrowserStackTunnel', type: StartBrowserStackTunnel)

		def openBrowserStackTunnelInBackground = project.task('openBrowserStackTunnelInBackground', type: StartBrowserStackTunnel) {
			inBackground = true
			finalizedBy 'closeBrowserStackTunnel'
		}

		[openBrowserStackTunnel, openBrowserStackTunnelInBackground].each {
			it.configure {
				browserStackTunnel = project.browserStack.tunnel
				workingDir = project.buildDir
				applicationUrls = project.browserStack.applicationUrls
			}
		}
	}
}
