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
package geb.gradle.saucelabs

import geb.gradle.cloud.ExternalTunnel
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.internal.jvm.Jvm
import org.slf4j.Logger

class SauceConnect extends ExternalTunnel {
	final protected SauceAccount account
	final protected Configuration connectConfiguration

	final String outputPrefix = 'sauce-connect'
	final String tunnelReadyMessage = 'Connected! You may start your tests.'

	SauceConnect(Project project, Logger logger, SauceAccount account, Configuration connectConfiguration) {
		super(project, logger)
		this.account = account
		this.connectConfiguration = connectConfiguration
	}

	@Override
	void validateState() {
		if (!account.username || !account.accessKey) {
			throw new InvalidUserDataException('No sauce labs username or passwords set')
		}
		if (!sauceConnectJar.exists()) {
			throw new InvalidUserDataException('No sauce connect jar set')
		}
	}

	@Override
	List<String> assembleCommandLine() {
		[Jvm.current().javaExecutable.absolutePath, '-jar', sauceConnectJar.absolutePath, account.username, account.accessKey]
	}

	File getSauceConnectJar() {
		connectConfiguration.singleFile
	}
}
