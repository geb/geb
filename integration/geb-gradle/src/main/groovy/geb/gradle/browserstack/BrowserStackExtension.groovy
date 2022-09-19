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

import static geb.gradle.browserstack.BrowserStackPlugin.CLOSE_TUNNEL_TASK_NAME
import static geb.gradle.browserstack.BrowserStackPlugin.OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME

@InheritConstructors(constructorAnnotations = true)
abstract class BrowserStackExtension extends CloudBrowsersExtension {

    final String openTunnelInBackgroundTaskName = OPEN_TUNNEL_IN_BACKGROUND_TASK_NAME
    final String closeTunnelTaskName = CLOSE_TUNNEL_TASK_NAME
    final String providerName = "browserstack"

    BrowserStackTunnel tunnel
    BrowserStackLocal local
    List<URL> applicationUrls = []

    @Nested
    abstract BrowserStackAccount getAccount()

    void local(Closure configuration) {
        project.configure(local, configuration)
        configureTestTasksWith(local)
    }

    void account(Action<? super BrowserStackAccount> action) {
        action.execute(account)
    }

    void application(String... urls) {
        applicationUrls.addAll(urls.collect { new URL(it) })
    }

    void application(URL... urls) {
        applicationUrls.addAll(urls)
    }

    protected void addExtensions() {
        super.addExtensions()

        task { test ->
            test.environment(
                (BrowserStackAccount.USER_ENV_VAR): new ToStringProviderValue(account.username),
                (BrowserStackAccount.ACCESS_KEY_ENV_VAR): new ToStringProviderValue(account.accessKey)
            )
        }

        local = extensions.create("local", BrowserStackLocal)

        tunnel = objectFactory.newInstance(BrowserStackTunnel, this)
        tunnel.accessKey.convention(account.accessKey)
    }
}
