/*
 * Copyright 2018 the original author or authors.
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
package geb.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class PluginSpec extends Specification {

    @Rule
    TemporaryFolder testProjectDir

    protected void buildScript(String contents) {
        testProjectDir.newFile('build.gradle') << contents
    }

    protected BuildResult runBuild(String... arguments) {
        GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments(*arguments)
                .withPluginClasspath()
                .forwardOutput()
                .build()
    }
}
