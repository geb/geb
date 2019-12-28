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
package geb.gradle.lambdatest

import geb.gradle.cloud.ExternalTunnel

import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.slf4j.Logger

class LambdaTestTunnel extends ExternalTunnel {
    final LambdaTestExtension extension

    final String outputPrefix = 'lambdatest-tunnel'
    final String tunnelReadyMessage = 'Tunnel claim successful'

    LambdaTestTunnel(Project project, Logger logger, LambdaTestExtension extension) {
        super(project, logger)
        this.extension = extension
    }

    @Override
    void validateState() {
        if (!extension.account.username) {
            def errorMsg = 'LambdaTest username not provided, Set Environment variable :' + LambdaTestAccount.USER_ENV_VAR
            println(errorMsg)
            throw new InvalidUserDataException(errorMsg)
        }
        if (!extension.account.accessKey) {
            def errorMsg = 'LambdaTest accesskey not provided, Set Environment variable :' + LambdaTestAccount.ACCESS_KEY_ENV_VAR
            println(errorMsg)
            throw new InvalidUserDataException(errorMsg)
        }
    }

    @Override
    List<String> assembleCommandLine() {
        def tunnelPath = project.fileTree(project.tasks.unzipLambdaTestTunnel.outputs.files.singleFile).singleFile.absolutePath
        def commandLine = [tunnelPath]
        commandLine << "-user" << extension.account.username
        commandLine << "-key" << extension.account.accessKey
        commandLine << "-v"
        if (extension.local.tunnelName) {
            commandLine << '-tunnelName' << extension.local.tunnelName
        }
        if (extension.local.config) {
            commandLine << "-config" << extension.local.config
        }
        if (extension.local.controller) {
            commandLine << "-controller" << extension.local.controller
        }
        if (extension.local.customSSHHost) {
            commandLine << "-customSSHHost" << extension.local.customSSHHost
        }
        if (extension.local.customSSHPort) {
            commandLine << "-customSSHPort" << extension.local.customSSHPort
        }
        if (extension.local.customSSHPrivateKey) {
            commandLine << "-customSSHPrivateKey" << extension.local.customSSHPrivateKey
        }
        if (extension.local.customSSHUser) {
            commandLine << "-customSSHUser" << extension.local.customSSHUser
        }
        if (extension.local.dir) {
            commandLine << "-dir" << extension.local.dir
        }
        if (extension.local.dns) {
            commandLine << "-dns" << extension.local.dns
        }
        if (extension.local.emulateChrome) {
            commandLine << "-emulateChrome" << extension.local.emulateChrome
        }
        if (extension.local.env) {
            commandLine << "-env" << extension.local.env
        }
        if (extension.local.infoAPIPort) {
            commandLine << "-infoAPIPort" << extension.local.infoAPIPort
        }
        if (extension.local.localdomains) {
            commandLine << "-local-domains" << extension.local.localdomains
        }
        if (extension.local.logFile) {
            commandLine << "-logFile" << extension.local.logFile
        }
        if (extension.local.mode) {
            commandLine << "-mode" << extension.local.mode
        }
        if (extension.local.nows) {
            commandLine << "-nows" << extension.local.nows
        }
        if (extension.local.outputconfig) {
            commandLine << "-output-config" << extension.local.outputconfig
        }
        if (extension.local.pac) {
            commandLine << "-pac" << extension.local.pac
        }
        if (extension.local.pidfile) {
            commandLine << "-pidfile" << extension.local.pidfile
        }
        if (extension.local.port) {
            commandLine << "-port" << extension.local.port
        }
        if (extension.local.proxyhost) {
            commandLine << "-proxy-host" << extension.local.proxyhost
        }
        if (extension.local.proxypass) {
            commandLine << "-proxy-pass" << extension.local.proxypass
        }
        if (extension.local.proxyport) {
            commandLine << "-proxy-port" << extension.local.proxyport
        }
        if (extension.local.proxyuser) {
            commandLine << "-proxy-user" << extension.local.proxyuser
        }
        if (extension.local.remotedebug) {
            commandLine << "-remote-debug" << extension.local.remotedebug
        }
        if (extension.local.server) {
            commandLine << "-server" << extension.local.server
        }
        if (extension.local.sharedtunnel) {
            commandLine << "-shared-tunnel"
        }
        if (extension.local.version) {
            commandLine << "-version" << extension.local.version
        }

        commandLine.addAll(extension.local.additionalOptions)
        commandLine
    }
}
