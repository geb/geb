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
package geb.gradle.cloud

import org.gradle.api.tasks.testing.Test

class BrowserSpec {
	final String cloudProvider
	final String name

	String browser
	String platform
	String version

	BrowserSpec(String cloudProvider, String name) {
		this.cloudProvider = cloudProvider
		this.name = name
		String browserSpec = name
		if (browserSpec) {
			String[] split = browserSpec.split("_", 3)
			browser = split[0]
			platform = split.size() > 1 ? split[1] : ""
			version = split.size() > 2 ? split[2] : ""
		}
	}

	String getDisplayName() {
		"$browser${platform?.capitalize() ?: ""}${version?.capitalize() ?: ""}"
	}

	void configure(Test test) {
		test.systemProperty "geb.${cloudProvider}.browser", "$browser:$platform:$version"
	}
}
