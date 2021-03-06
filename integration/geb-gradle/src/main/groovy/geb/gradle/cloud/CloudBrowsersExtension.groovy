/*
 * Copyright 2019 the original author or authors.
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
package geb.gradle.cloud

import geb.gradle.EnvironmentVariablesCommandLineArgumentProvider
import org.gradle.api.DomainObjectCollection
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.testing.Test

abstract class CloudBrowsersExtension {
    protected final Project project
    protected final Task allTestsLifecycleTask
    protected final DomainObjectCollection<Test> testTasks

    protected NamedDomainObjectContainer<BrowserSpec> browsers

    CloudBrowsersExtension(Project project, Task allTestsLifecycleTask) {
        this.project = project
        this.allTestsLifecycleTask = allTestsLifecycleTask
        this.testTasks = project.objects.domainObjectSet(Test)
    }

    abstract String getOpenTunnelInBackgroundTaskName()
    abstract String getCloseTunnelTaskName()
    abstract String getProviderName()

    void addExtensions() {
        browsers = project.container(BrowserSpec) {
            def browser = new BrowserSpec(project.objects, providerName, it)
            addTestTask(browser)
            browser
        }
        extensions.browsers = browsers
    }

    void task(Closure configuration) {
        testTasks.all(configuration)
    }

    void additionalTask(String namePrefix, Closure configuration) {
        browsers.all {
            def task = addTestTask(it, namePrefix)
            task.configure(configuration)
        }
    }

    protected void configureTestTasksWith(TestTaskConfigurer configurer) {
        testTasks.all { Test task -> configurer.configure(task) }
    }

    protected Test addTestTask(BrowserSpec browser, String prefix = null) {
        def name = prefix ? "${prefix}${browser.displayName.capitalize()}" : browser.displayName

        def testTask = project.task("${name}Test", type: Test) { Test task ->
            group allTestsLifecycleTask.group
            task.dependsOn openTunnelInBackgroundTaskName
            allTestsLifecycleTask.dependsOn task
            finalizedBy closeTunnelTaskName

            def reporting = project.reporting as ReportingExtension
            def gebReportsDir = reporting.file("geb/${task.name}")
            outputs.dir(gebReportsDir)
            jvmArgumentProviders.add(
                new EnvironmentVariablesCommandLineArgumentProvider('geb.build.reportsDir': gebReportsDir.absolutePath)
            )
        }

        browser.addTask(testTask)
        testTasks.add(testTask)

        def decorateReportsTask = project.task("${name}DecorateReports", type: Copy) {
            from testTask.reports.junitXml.destination
            into "${testTask.reports.junitXml.destination}-decorated"
            filter { it.replaceAll("(testsuite|testcase) name=\"(.+?)\"", "\$1 name=\"\$2 ($name)\"") }
        }

        testTask.finalizedBy decorateReportsTask

        testTask
    }
}
