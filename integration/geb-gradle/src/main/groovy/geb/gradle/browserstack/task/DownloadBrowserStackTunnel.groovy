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
package geb.gradle.browserstack.task

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import org.apache.tools.ant.taskdefs.condition.Os

class DownloadBrowserStackTunnel extends DefaultTask {

	@OutputFile
	File tunnelZip = project.file("${project.buildDir}/browserstack/BrowserStackTunnel.zip")

	@TaskAction
	void download() {
		tunnelZip.parentFile.mkdirs()
		if (!tunnelZip.exists()) {
			def url = "https://www.browserstack.com/browserstack-local/BrowserStackLocal-${osSpecificUrlPart}.zip"
			logger.info("Downloading {} to {}", url, tunnelZip)
			tunnelZip << new URL(url).bytes
		}
	}

	String getOsSpecificUrlPart() {
		if (Os.isFamily(Os.FAMILY_WINDOWS)) {
			"win32"
		} else if (Os.isFamily(Os.FAMILY_MAC)) {
			"darwin-x64"
		} else if (Os.isFamily(Os.FAMILY_UNIX)) {
			Os.isArch("amd64") ? "linux-x64" : "linux-ia32"
		}
	}
}
