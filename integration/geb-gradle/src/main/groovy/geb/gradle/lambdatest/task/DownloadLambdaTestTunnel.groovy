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
package geb.gradle.lambdatest.task

import geb.gradle.cloud.task.DownloadExternalTunnel
import org.apache.tools.ant.taskdefs.condition.Os

class DownloadLambdaTestTunnel extends DownloadExternalTunnel {

    protected String outputPath() {
        "lambdatest/LambdaTestTunnel.zip"
    }

    @Override
    protected String downloadUrl() {
        "https://downloads.lambdatest.com/tunnel/v3/${osSpecificUrlPart()}.zip"
    }

    private String osSpecificUrlPart() {
        def archPart = (Os.isArch("amd64") || Os.isArch("x86_64")) ? "64bit" : "32bit"
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            "windows/$archPart/LT_Windows"
        } else if (Os.isFamily(Os.FAMILY_MAC)) {
            "mac/$archPart/LT_Mac"
        } else if (Os.isFamily(Os.FAMILY_UNIX)) {
            "linux/$archPart/LT_Linux"
        }
    }
}
