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

import geb.gradle.ToStringProviderValue
import geb.gradle.cloud.CloudBrowsersExtension
import groovy.transform.InheritConstructors

import static geb.gradle.saucelabs.SauceAccount.ACCESS_KEY_ENV_VAR
import static geb.gradle.saucelabs.SauceAccount.USER_ENV_VAR
import static geb.gradle.saucelabs.SaucePlugin.CLOSE_TUNNEL_TASK_NAME
import static geb.gradle.saucelabs.SaucePlugin.OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME

@InheritConstructors(constructorAnnotations = true)
abstract class SauceLabsExtension extends CloudBrowsersExtension {

    final String openTunnelInBackgroundTaskName = OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME
    final String closeTunnelTaskName = CLOSE_TUNNEL_TASK_NAME
    final String providerName = "saucelabs"

    SauceConnect connect

    void connect(Closure configuration) {
        project.configure(connect, configuration)
        configureTestTasksWith(connect)
    }

    protected void addExtensions() {
        super.addExtensions()
        def account = extensions.create("account", SauceAccount)

        task {
            it.environment(
                (USER_ENV_VAR): new ToStringProviderValue(account.username.orElse("")),
                (ACCESS_KEY_ENV_VAR): new ToStringProviderValue(account.accessKey.orElse(""))
            )
        }

        connect = objectFactory.newInstance(SauceConnect)
        connect.username.convention(account.username)
        connect.accessKey.convention(account.accessKey)
    }
}
