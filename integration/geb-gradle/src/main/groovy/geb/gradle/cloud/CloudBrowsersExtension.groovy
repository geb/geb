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
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.reporting.ReportingExtension
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test

import javax.inject.Inject

abstract class CloudBrowsersExtension implements ExtensionAware {
    protected final ObjectFactory objectFactory
    protected final Project project
    protected final TaskProvider<Task> allTestsLifecycleTask
    protected final String tasksGroup
    protected final DomainObjectCollection<TaskProvider<Test>> testTasks

    protected NamedDomainObjectContainer<BrowserSpec> browsers

    @Inject
    CloudBrowsersExtension(ObjectFactory objectFactory, Project project, TaskProvider<Task> allTestsLifecycleTask, String tasksGroup) {
        this.objectFactory = objectFactory
        this.project = project
        this.allTestsLifecycleTask = allTestsLifecycleTask
        this.tasksGroup = tasksGroup

        allTestsLifecycleTask.configure {
            it.group = tasksGroup
        }

        this.testTasks = project.objects.domainObjectSet(TaskProvider)
    }

    abstract String getOpenTunnelInBackgroundTaskName()
    abstract String getCloseTunnelTaskName()
    abstract String getProviderName()

    void addExtensions() {
        browsers = objectFactory.domainObjectContainer(BrowserSpec) { name ->
            objectFactory.newInstance(BrowserSpec, providerName, name).tap { browserSpec ->
                addTestTask(browserSpec)
            }
        }
        extensions.add("browsers", browsers)
    }

    void task(Closure configuration) {
        testTasks.all { TaskProvider taskProvider ->
            taskProvider.configure(configuration)
        }
    }

    void additionalTask(String namePrefix, Closure configuration) {
        browsers.all {
            def task = addTestTask(it, namePrefix)
            task.configure(configuration)
        }
    }

    protected void configureTestTasksWith(TestTaskConfigurer configurer) {
        testTasks.all { TaskProvider<Test> taskProvider ->
            taskProvider.configure(configurer.&configure)
        }
    }

    protected TaskProvider<Test> addTestTask(BrowserSpec browser, String prefix = null) {
        def name = prefix ? "${prefix}${browser.displayName.capitalize()}" : browser.displayName

        def testTask = project.tasks.register("${name}Test", Test) { Test task ->
            task.group = tasksGroup
            task.dependsOn openTunnelInBackgroundTaskName
            finalizedBy closeTunnelTaskName

            def reporting = project.reporting as ReportingExtension
            def gebReportsDir = reporting.file("geb/${task.name}")
            outputs.dir(gebReportsDir)
            jvmArgumentProviders.add(
                new EnvironmentVariablesCommandLineArgumentProvider('geb.build.reportsDir': gebReportsDir.absolutePath)
            )
        }

        allTestsLifecycleTask.configure {
            it.dependsOn testTask
        }

        browser.addTask(testTask)
        testTasks.add(testTask)

        def decorateReportsTask = project.tasks.register("${name}DecorateReports", Copy) {
            from testTask.map { it.reports.junitXml.destination }
            into testTask.map { "${it.reports.junitXml.destination}-decorated" }
            filter { it.replaceAll("(testsuite|testcase) name=\"(.+?)\"", "\$1 name=\"\$2 ($name)\"") }
        }

        testTask.configure { finalizedBy decorateReportsTask }

        testTask
    }
}
