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

import org.gradle.api.tasks.testing.Test

class SauceAccount {
	public static final String USER_ENV_VAR = "GEB_SAUCE_LABS_USER"
	public static final String ACCESS_KEY_ENV_VAR = "GEB_SAUCE_LABS_ACCESS_PASSWORD"
	public static final String SAUCE_CONNECT_OPTS_ENV_VAR = "GEB_SAUCE_LABS_OPTIONS"
	public static final String SAUCE_CONNECT_PORT_ENV_VAR = "GEB_SAUCE_LABS_PORT"

    /** The Sauce Labs username to use for the Sauce Connect instance. */
	String username
    /** The Sauce Labs access key to use for the Sauce Connect instance. */
	String accessKey
    /** The Sauce Labs command line options to be supplied when launching Sauce Connect. */
    String options
    /** The port that Sauce Connect should be launched on. */
    Integer port = 4445

	void configure(Test test) {
		test.environment(USER_ENV_VAR, username)
		test.environment(ACCESS_KEY_ENV_VAR, accessKey)
		test.environment(SAUCE_CONNECT_OPTS_ENV_VAR, options)
		test.environment(SAUCE_CONNECT_PORT_ENV_VAR, port)
	}
}
