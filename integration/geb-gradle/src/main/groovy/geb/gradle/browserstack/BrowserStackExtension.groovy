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

import org.gradle.api.Project

class BrowserStackExtension {

	Project project
	BrowserStackAccount account
    String tunnelJarUrl = "http://www.browserstack.com/BrowserStackTunnel.jar"
	File tunnelJar = new File(System.getProperty("user.home"), ".browserstack/BrowserStackTunnel.jar")
	List<URL> applicationUrls = []

	BrowserStackExtension(Project project) {
		this.project = project
	}

	void addExtensions() {
		extensions.browsers = project.container(BrowserSpec)
		account = new BrowserStackAccount()
		extensions.create('tunnel', BrowserStackTunnel, project, account, project.logger, tunnelJar)
	}

	void task(Closure configuration) {
		extensions.browsers.all { BrowserSpec browser ->
			project.tasks["${browser.displayName}Test"].configure configuration
		}
	}

	void account(Closure configuration) {
		project.configure(account, configuration)
		extensions.browsers.all { BrowserSpec browser ->
			account.configure project.tasks["${browser.displayName}Test"]
		}
	}

	void application(String... urls) {
		applicationUrls.addAll(urls.collect {new URL(it)})
	}

	void application(URL... urls) {
		applicationUrls.addAll(urls)
	}
}
