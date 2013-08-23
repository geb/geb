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

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import org.gradle.api.tasks.testing.Test

class SauceTasksBuilder {
	SauceAccount sauceAccount
	NamedDomainObjectContainer<BrowserSpec> browsers
	Task openSauceTunnel

	SauceTasksBuilder(SauceAccount sauceAccount, NamedDomainObjectContainer<BrowserSpec> browsers, Task openSauceTunnel) {
		this.sauceAccount = sauceAccount
		this.browsers = browsers
		this.openSauceTunnel = openSauceTunnel
	}

	void build(Test sourceTask) {
		Project project = sourceTask.project
		def allSauceTests = project.task("allSauceTests") {
			group "Sauce Test"
		}

		browsers.all { BrowserSpec browser ->
			def testTask = project.task("${browser.displayName}Test", type: SauceTest) { task ->
				group allSauceTests.group
				task.dependsOn openSauceTunnel
				allSauceTests.dependsOn task

				testClassesDir = sourceTask.testClassesDir
				testSrcDirs = sourceTask.testSrcDirs
				classpath = sourceTask.classpath

				systemProperty 'geb.build.reportsDir', project.reporting.file("$name-geb")

				sauceAccount.configure(task)
				browser.configure(task)
			}

			project.gradle.addListener(new TaskExecutionListener() {
				void beforeExecute(Task task) {}

				void afterExecute(Task task, TaskState taskState) {
					if (task != testTask) {
						return
					}

					project.copy {
						from testTask.reports.junitXml.destination
						into "${testTask.reports.junitXml.destination}-decorated"
						filter { it.replaceAll("(testsuite|testcase) name=\"(.+?)\"", "\$1 name=\"\$2 ($browser.displayName)\"") }
					}
				}
			})
		}
	}
}
