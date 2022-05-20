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

    private static final String COMMA = ","

    final LambdaTestExtension extension

    final String outputPrefix = 'lambdatest-tunnel'

    LambdaTestTunnel(Project project, Logger logger, LambdaTestExtension extension) {
        super(project, logger)
        this.extension = extension
    }

    @Override
    void validateState() {
        if (!extension.account.username) {
            throw new InvalidUserDataException("LambdaTest username not provided")
        }
        if (!extension.account.accessKey) {
            throw new InvalidUserDataException("LambdaTest accesskey not provided")
        }
    }

    @Override
    List<String> assembleCommandLine() {
        def tunnelPath = project.fileTree(project.tasks.unzipLambdaTestTunnel.outputs.files.singleFile).singleFile.absolutePath
        def commandLine = [tunnelPath]
        commandLine << "--user" << extension.account.username
        commandLine << "--key" << extension.account.accessKey
        commandLine << "-v"
        if (extension.local.tunnelName) {
            commandLine << '--tunnelName' << extension.local.tunnelName
        }
        if (extension.local.allowHosts) {
            commandLine << '--allowHosts' << extension.local.allowHosts.join(COMMA)
        }
        if (extension.local.bypassHosts) {
            commandLine << '--bypassHosts' << extension.local.bypassHosts.join(COMMA)
        }
        if (extension.local.callbackURL) {
            commandLine << '--callbackURL' << extension.local.callbackURL
        }
        if (extension.local.config) {
            commandLine << "--config" << extension.local.config
        }
        if (extension.local.clientCert) {
            commandLine << "--clientCert" << extension.local.clientCert
        }
        if (extension.local.clientKey) {
            commandLine << "--clientKey" << extension.local.clientKey
        }
        if (extension.local.dir) {
            commandLine << "--dir" << extension.local.dir
        }
        if (extension.local.dns) {
            commandLine << "--dns" << extension.local.dns
        }
        if (extension.local.egressOnly) {
            commandLine << "--egress-only"
        }
        if (extension.local.env) {
            commandLine << "--env" << extension.local.env
        }
        if (extension.local.infoAPIPort) {
            commandLine << "--infoAPIPort" << extension.local.infoAPIPort
        }
        if (extension.local.ingressOnly) {
            commandLine << "--ingress-only"
        }
        if (extension.local.loadBalanced) {
            commandLine << "--load-balanced"
        }
        if (extension.local.logFile) {
            commandLine << "--logFile" << extension.local.logFile
        }
        if (extension.local.mitm) {
            commandLine << "--mitm"
        }
        if (extension.local.mode) {
            commandLine << "--mode" << extension.local.mode
        }
        if (extension.local.mTLSHosts) {
            commandLine << "--mTLSHosts" << extension.local.mTLSHosts.join(COMMA)
        }
        if (extension.local.noProxy) {
            commandLine << "--no-proxy" << extension.local.noProxy.join(COMMA)
        }
        if (extension.local.pidfile) {
            commandLine << "--pidfile" << extension.local.pidfile
        }
        if (extension.local.port) {
            commandLine << "--port" << extension.local.port
        }
        if (extension.local.proxyhost) {
            commandLine << "--proxy-host" << extension.local.proxyhost
        }
        if (extension.local.proxypass) {
            commandLine << "--proxy-pass" << extension.local.proxypass
        }
        if (extension.local.proxyport) {
            commandLine << "--proxy-port" << extension.local.proxyport
        }
        if (extension.local.proxyuser) {
            commandLine << "--proxy-user" << extension.local.proxyuser
        }
        if (extension.local.pacfile) {
            commandLine << "--pacfile" << extension.local.pacfile
        }
        if (extension.local.sharedtunnel) {
            commandLine << "--shared-tunnel"
        }
        if (extension.local.sshConnType) {
            commandLine << "--sshConnType" << extension.local.sshConnType
        }
        if (extension.local.version) {
            commandLine << "--version"
        }

        commandLine.addAll(extension.local.additionalOptions)
        commandLine
    }

    @Override
    String getTunnelReadyMessage() {
        extension.local.tunnelReadyMessage
    }
}
