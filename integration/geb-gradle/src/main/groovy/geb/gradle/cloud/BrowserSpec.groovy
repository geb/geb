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
package geb.gradle.cloud

import org.gradle.api.DomainObjectSet
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test

import javax.inject.Inject

import static com.google.common.base.CaseFormat.LOWER_CAMEL
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE

abstract class BrowserSpec {
    final String cloudProvider
    final String name
    final String displayName

    private final Properties capabilities = new Properties()

    abstract DomainObjectSet<TaskProvider<Test>> getTasks()

    @Inject
    BrowserSpec(String cloudProvider, String name) {
        this.cloudProvider = cloudProvider
        this.name = name
        String[] split = name.split("_", 3)
        capabilities["browserName"] = split[0]
        if (split.size() > 1) {
            capabilities["platformName"] = split[1]
        }
        if (split.size() > 2) {
            capabilities["browserVersion"] = split[2]
        }
        displayName = "${camelCase(capabilities["browserName"])}${capabilities["platformName"]?.capitalize() ?: ""}${capabilities["browserVersion"]?.capitalize() ?: ""}"
        if (capabilities["platformName"]) {
            capabilities["platformName"] = capabilities["platformName"].toUpperCase()
        }
        setCapabilitiesOnTasks()
    }

    void capability(String capability, String value) {
        capabilities.put(capability, value)
        setCapabilitiesOnTasks()
    }

    void capabilities(Map<String, String> capabilities) {
        this.capabilities.putAll(capabilities)
        setCapabilitiesOnTasks()
    }

    void setCapabilities(Map<String, String> capabilities) {
        capabilities.clear()
        capabilities(capabilities)
    }

    void addTask(TaskProvider<Test> task) {
        tasks.add(task)
    }

    void configureTasks(Closure configuration) {
        tasks.all { TaskProvider taskProvider ->
            taskProvider.configure(configuration)
        }
    }

    protected void configureCapabilitiesOnTask(Test task) {
        task.systemProperty "geb.${cloudProvider}.browser", capabilitiesAsString
    }

    protected String getCapabilitiesAsString() {
        StringWriter writer = new StringWriter()
        capabilities.store(writer, null)

        writer.toString().readLines().findAll {
            !it.startsWith("#")
        }.join(System.lineSeparator())
    }

    protected void setCapabilitiesOnTasks() {
        tasks.all { TaskProvider<Test> taskProvider ->
            taskProvider.configure {
                configureCapabilitiesOnTask(it)
            }
        }
    }

    protected String camelCase(String camelCaseTextWithSpaces) {
        def lowerUnderscoreText = LOWER_CAMEL.to(LOWER_UNDERSCORE, camelCaseTextWithSpaces).replaceAll(' ', '_').toLowerCase()
        LOWER_UNDERSCORE.to(LOWER_CAMEL, lowerUnderscoreText)
    }
}
