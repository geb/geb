/*
 * Copyright 2021 the original author or authors.
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
package org.gebish.gradle

import org.gebish.gradle.task.GatherManuals
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.WriteProperties

class ManualsPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.pluginManager.apply("geb.base")
        def baseExtension = project.extensions.getByType(BaseExtension)

        def gatherManualsTask = project.tasks.register("gatherManuals", GatherManuals) {
            into project.layout.buildDirectory.dir("manuals")
        }

        def manualsExtension = project.extensions.create('manuals', ManualsExtension, project)

        configureCurrentManualGathering(project, baseExtension, manualsExtension, gatherManualsTask)
        configureGenerateConfigPropertiesTask(project, baseExtension, manualsExtension)
    }

    private void configureCurrentManualGathering(
        Project project, BaseExtension baseExtension, ManualsExtension manualsExtension,
        TaskProvider<GatherManuals> gatherManualsTask
    ) {
        gatherManualsTask.configure {
            if (baseExtension.isSnapshot()) {
                gatherManual('snapshot', manualsExtension.currentManual)
                gatherPublishedManual(manualsExtension.includedManuals.map { it.last() }, 'current')
            } else {
                gatherManual('current', manualsExtension.currentManual)
                gatherManual(project.version.toString(), manualsExtension.currentManual)
            }
        }
    }

    private void configureGenerateConfigPropertiesTask(
        Project project, BaseExtension baseExtension, ManualsExtension manualsExtension
    ) {
        def currentVersion = manualsExtension.includedManuals.map {
            baseExtension.isSnapshot() ? it.last() : project.version
        }

        project.tasks.register("generateConfigProperties", WriteProperties) {
            outputFile = project.layout.buildDirectory.file("config-properties/ratpack.properties")

            property("manuals.old", manualsExtension.includedManuals.map {
                (it - currentVersion.get()).reverse().join(",")
            })
            property("manuals.current", currentVersion)
            property("manuals.snapshot", baseExtension.isSnapshot() ? project.version : '')
        }
    }
}
