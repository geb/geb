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
import geb.gradle.cloud.TestTaskConfigurer
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.testing.Test
import org.gradle.process.ExecOperations

import javax.inject.Inject

abstract class SauceConnect extends ExternalTunnel implements TestTaskConfigurer {

    public static final String TUNNEL_ID_ENV_VAR = "GEB_SAUCE_LABS_TUNNEL_ID"

    final protected SauceAccount account

    final String outputPrefix = 'sauce-connect'
    final String tunnelReadyMessage = 'Sauce Connect is up, you may start your tests'

    @Internal
    String identifier

    @Internal
    int port = 4445

    @Internal
    List<String> additionalOptions = []

    @Inject
    SauceConnect(Project project, ExecOperations execOperations, SauceAccount account) {
        super(project, execOperations)
        this.account = account
    }

    @Override
    void validateState() {
        if (!account.username || !account.accessKey) {
            throw new InvalidUserDataException('No sauce labs username or passwords set')
        }
    }

    @Override
    List<String> assembleCommandLine() {
        def options = [executablePath, '--user', account.username, '--api-key', account.accessKey, '--se-port', port.toString()]
        if (identifier) {
            options << '--tunnel-identifier' << identifier
        }
        options + additionalOptions
    }

    void configure(Test test) {
        test.environment(TUNNEL_ID_ENV_VAR, identifier)
    }
}
