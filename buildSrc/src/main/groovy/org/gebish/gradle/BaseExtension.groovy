package org.gebish.gradle

import org.gradle.api.Project

class BaseExtension {

    private final Project project

    BaseExtension(Project project) {
        this.project = project
    }

    void onCi(Closure closure) {
        if (project.hasProperty("ci")) {
            project.configure(project, closure)
        }
    }

    boolean isSnapshot() {
        project.version.endsWith("-SNAPSHOT")
    }
}