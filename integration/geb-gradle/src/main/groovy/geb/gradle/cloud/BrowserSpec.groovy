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

import org.gradle.api.DomainObjectCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.testing.Test

import static com.google.common.base.CaseFormat.LOWER_CAMEL
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE

class BrowserSpec {
    final String cloudProvider
    final String name
    final String displayName

    private final DomainObjectCollection<Test> tasks
    private final Properties capabilities = new Properties()

    BrowserSpec(ObjectFactory objects, String cloudProvider, String name) {
        this.cloudProvider = cloudProvider
        this.name = name
        this.tasks = objects.domainObjectSet(Test)
        String browserSpec = name
        if (browserSpec) {
            String[] split = browserSpec.split("_", 3)
            capabilities["browserName"] = split[0]
            if (split.size() > 1) {
                capabilities["platform"] = split[1]
            }
            if (split.size() > 2) {
                capabilities["version"] = split[2]
            }
            displayName = "${camelCase(capabilities["browserName"])}${capabilities["platform"]?.capitalize() ?: ""}${capabilities["version"]?.capitalize() ?: ""}"
            if (capabilities["platform"]) {
                capabilities["platform"] = capabilities["platform"].toUpperCase()
            }
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

    void addTask(Test task) {
        tasks.add(task)
    }

    void configureTasks(Closure configuration) {
        tasks.all(configuration)
    }

    protected void configureCapabilitiesOnTask(Test task) {
        StringWriter writer = new StringWriter()
        capabilities.store(writer, null)
        task.systemProperty "geb.${cloudProvider}.browser", writer.toString()
    }

    private void setCapabilitiesOnTasks() {
        tasks.all { Test task ->
            configureCapabilitiesOnTask(task)
        }
    }

    private String camelCase(String camelCaseTextWithSpaces) {
        def lowerUnderscoreText = LOWER_CAMEL.to(LOWER_UNDERSCORE, camelCaseTextWithSpaces).replaceAll(' ', '_').toLowerCase()
        LOWER_UNDERSCORE.to(LOWER_CAMEL, lowerUnderscoreText)
    }
}
