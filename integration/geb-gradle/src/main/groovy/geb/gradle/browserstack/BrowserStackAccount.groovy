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

import org.gradle.api.tasks.testing.Test

class BrowserStackAccount {
    public static final String USER_ENV_VAR = "GEB_BROWSERSTACK_USERNAME"
    public static final String ACCESS_KEY_ENV_VAR = "GEB_BROWSERSTACK_AUTHKEY"
    public static final String LOCAL_ID_ENV_VAR = "GEB_BROWSERSTACK_LOCALID"
    public static final String PROXY_HOST_ENV_VAR = "GEB_BROWSERSTACK_PROXY_HOST"
    public static final String PROXY_PORT_ENV_VAR = "GEB_BROWSERSTACK_PROXY_PORT"
    public static final String PROXY_USER_ENV_VAR = "GEB_BROWSERSTACK_PROXY_USER"
    public static final String PROXY_PASS_ENV_VAR = "GEB_BROWSERSTACK_PROXY_PASS"

    String username
    String accessKey
    String localId
    String proxyHost
    String proxyPort
    String proxyUser
    String proxyPass

    void configure(Test test) {
        if (username) {
            test.environment(USER_ENV_VAR, username)
        }
        if (accessKey) {
            test.environment(ACCESS_KEY_ENV_VAR, accessKey)
        }
        if (localId) {
            test.environment(LOCAL_ID_ENV_VAR, localId)
        }
        if (proxyHost) {
            test.environment(PROXY_HOST_ENV_VAR, proxyHost)
        }
        if (proxyPort) {
            test.environment(PROXY_PORT_ENV_VAR, proxyPort)
        }
        if (proxyUser) {
            test.environment(PROXY_USER_ENV_VAR, proxyUser)
        }
        if (proxyPass) {
            test.environment(PROXY_PASS_ENV_VAR, proxyPass)
        }
    }
}
