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
package geb.gradle.saucelabs

import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

import javax.inject.Inject

class UnpackSauceConnect extends DefaultTask {

    protected final FileSystemOperations fileSystemOperations
    protected final ObjectFactory objectFactory

    @Inject
    UnpackSauceConnect(FileSystemOperations fileSystemOperations, ObjectFactory objectFactory) {
        this.fileSystemOperations = fileSystemOperations
        this.objectFactory = objectFactory
    }

    @InputFiles
    Configuration getSauceConnectConfiguration() {
        project.configurations.sauceConnect
    }

    @OutputDirectory
    File getSauceConnectDir() {
        new File(project.buildDir, "sauce-connect")
    }

    @TaskAction
    void unpack() {
        def operations = new SauceConnectOperations(sauceConnectConfiguration)
        def manager = operations.loadSauceConnectFourManagerClass().newInstance()

        manager.extractZipFile(temporaryDir, operations.operatingSystem)

        fileSystemOperations.delete {
            it.delete(sauceConnectDir)
        }
        fileSystemOperations.copy {
            it.from(
                objectFactory.fileTree().from(temporaryDir)
                    .include("${operations.directory}/${operations.operatingSystem.executable}")
            )
            into(sauceConnectDir)
        }
    }
}
