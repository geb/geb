package org.gebish.gradle

import org.gradle.api.Project

import static java.lang.Runtime.getRuntime

class BaseExtension {

    private final Project project

    BaseExtension(Project project) {
        this.project = project
    }

    void onCi(Closure closure) {
        if (isCi()) {
            project.configure(project, closure)
        }
    }

    boolean isSnapshot() {
        project.version.endsWith("-SNAPSHOT")
    }

    int getMaxWorkers() {
        isCi() ? 2 : Math.min(runtime.availableProcessors().intdiv(2), 4)
    }

    private boolean isCi() {
        project.hasProperty("ci")
    }
}