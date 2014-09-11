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
package geb.gradle.browserstack

import geb.gradle.cloud.ExternalTunnel
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.slf4j.Logger

class BrowserStackTunnel extends ExternalTunnel {
	final protected BrowserStackAccount account
	final protected List<URL> applicationUrls

	final String outputPrefix = 'browserstack-tunnel'
	final String tunnelReadyMessage = 'You can now access your local server(s) in our remote browser.'

	BrowserStackTunnel(Project project, Logger logger, BrowserStackAccount account, List<URL> applicationUrls) {
		super(project, logger)
		this.account = account
		this.applicationUrls = applicationUrls
	}

	@Override
	void validateState() {
		if (!account.accessKey) {
			throw new InvalidUserDataException('No BrowserStack access key set')
		}
	}

	@Override
	List<String> assembleCommandLine() {
		def tunnelPath = project.fileTree(project.tasks.unzipBrowserStackTunnel.outputs.files.singleFile).singleFile.absolutePath
		def commandLine = [tunnelPath]
		commandLine << account.accessKey
		if (account.localId) {
			commandLine << '-localIdentifier' << account.localId
		}
		if (applicationUrls) {
			commandLine << "-only" << assembleAppSpecifier(applicationUrls)
		}
		commandLine
	}

	static String assembleAppSpecifier(List<URL> applicationUrls) {
		applicationUrls.collect { "${it.host},${determinePort(it)},${it.protocol == 'https' ? '1' : '0'}" }.join(',')
	}

	static int determinePort(URL url) {
		url.port > 0 ? url.port : (url.protocol == 'https' ? 443 : 80)
	}
}
