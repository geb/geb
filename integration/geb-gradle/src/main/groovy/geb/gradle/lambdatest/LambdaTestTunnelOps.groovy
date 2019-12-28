/*
 * Copyright 2019 the original author or authors.
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
package geb.gradle.lambdatest

import geb.gradle.cloud.TestTaskConfigurer
import org.gradle.api.tasks.testing.Test

class LambdaTestTunnelOps implements TestTaskConfigurer {

    public static final String LOCAL_ID_ENV_VAR = "GEB_LAMBDATEST_TUNNELID"

    String tunnelName
    String config
    String controller
    String customSSHHost
    String customSSHPort
    String customSSHPrivateKey
    String customSSHUser
    String dir
    String dns
    String emulateChrome
    String env
    String infoAPIPort
    String localdomains
    String logFile
    String mode
    String nows
    String outputconfig
    String pac
    String pidfile
    String port
    String proxyhost
    String proxypass
    String proxyport
    String proxyuser
    String remotedebug
    String server
    String sharedtunnel
    String version

    List<String> additionalOptions = []

    void configure(Test test) {
        test.environment(LOCAL_ID_ENV_VAR, tunnelName)
    }
}
