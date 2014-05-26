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

import geb.gradle.cloud.ExternalJavaTunnel
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.slf4j.Logger

class BrowserStackTunnel extends ExternalJavaTunnel {
	final protected BrowserStackAccount account
	final protected File tunnelJar
	final protected List<URL> applicationUrls

	final String outputPrefix = 'browserstack-tunnel'
	final String tunnelReadyMessage = 'You can now access your local server(s) in our remote browser:'

	BrowserStackTunnel(Project project, Logger logger, BrowserStackAccount account, File tunnelJar, List<URL> applicationUrls) {
		super(project, logger)
		this.account = account
		this.tunnelJar = tunnelJar
		this.applicationUrls = applicationUrls
	}

	@Override
	void validateState() {
		if (!account.accessKey) {
			throw new InvalidUserDataException('No BrowserStack access key set')
		}
	}

	@Override
	List<String> assembleArguments() {
		def args = ['-jar', tunnelJar.absolutePath]
		if (account.localId) {
			args << '-localIdentifier' << account.localId
		}
		args << '-skipCheck'
		args << account.accessKey << assembleAppSpecifier(applicationUrls)
		args
	}

	static String assembleAppSpecifier(List<URL> applicationUrls) {
		applicationUrls.collect { "${it.host},${determinePort(it)},${it.protocol == 'https' ? '1' : '0'}" }.join(',')
	}

	static int determinePort(URL url) {
		url.port > 0 ? url.port : (url.protocol == 'https' ? 443 : 80)
	}
}
