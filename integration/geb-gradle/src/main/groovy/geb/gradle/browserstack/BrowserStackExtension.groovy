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

import geb.gradle.ToStringProviderValue
import geb.gradle.cloud.CloudBrowsersExtension
import groovy.transform.InheritConstructors
import org.gradle.api.Action
import org.gradle.api.tasks.Nested

import static geb.gradle.browserstack.BrowserStackAccount.ACCESS_KEY_ENV_VAR
import static geb.gradle.browserstack.BrowserStackAccount.USER_ENV_VAR
import static geb.gradle.browserstack.BrowserStackPlugin.CLOSE_TUNNEL_TASK_NAME
import static geb.gradle.browserstack.BrowserStackPlugin.OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME
import static BrowserStackLocal.LOCAL_ID_ENV_VAR

@InheritConstructors(constructorAnnotations = true)
abstract class BrowserStackExtension extends CloudBrowsersExtension {

    final String openTunnelInBackgroundTaskName = OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME
    final String closeTunnelTaskName = CLOSE_TUNNEL_TASK_NAME
    final String providerName = "browserstack"

    List<URL> applicationUrls = []

    @Nested
    abstract BrowserStackLocal getLocal()

    @Nested
    abstract BrowserStackAccount getAccount()

    void local(Action<? super BrowserStackLocal> action) {
        action.execute(local)
    }

    void account(Action<? super BrowserStackAccount> action) {
        action.execute(account)
    }

    void application(String... urls) {
        local.applicationUrls.addAll(urls.collect { new URL(it) })
    }

    void application(URL... urls) {
        local.applicationUrls.addAll(urls)
    }

    protected void addExtensions() {
        super.addExtensions()

        task { test ->
            test.environment(
                (USER_ENV_VAR): new ToStringProviderValue(account.username),
                (ACCESS_KEY_ENV_VAR): new ToStringProviderValue(account.accessKey),
                (LOCAL_ID_ENV_VAR): local.identifier.get()
            )
        }

        local.accessKey.convention(account.accessKey)
    }
}
