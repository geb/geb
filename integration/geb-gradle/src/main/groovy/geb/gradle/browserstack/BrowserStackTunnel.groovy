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

import geb.gradle.cloud.ExternalTunnel
import org.gradle.api.InvalidUserDataException
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.process.ExecOperations

import javax.inject.Inject

abstract class BrowserStackTunnel extends ExternalTunnel {
    private static final String HTTPS_PROTOCOL = 'https'

    private final ObjectFactory objectFactory

    @Internal
    final BrowserStackExtension extension

    final String outputPrefix = 'browserstack-tunnel'

    @Inject
    BrowserStackTunnel(
        ExecOperations execOperations, ObjectFactory objectFactory, BrowserStackExtension extension
    ) {
        super(execOperations)
        this.objectFactory = objectFactory
        this.extension = extension
    }

    static String assembleAppSpecifier(List<URL> applicationUrls) {
        applicationUrls.collect { "${it.host},${determinePort(it)},${it.protocol == HTTPS_PROTOCOL ? '1' : '0'}" }.join(',')
    }

    static int determinePort(URL url) {
        url.port > 0 ? url.port : (url.protocol == HTTPS_PROTOCOL ? 443 : 80)
    }

    @Internal
    abstract Property<String> getAccessKey()

    @Override
    void validateState() {
        if (!accessKey.present) {
            throw new InvalidUserDataException('No BrowserStack access key set')
        }
    }

    @Override
    List<String> assembleCommandLine() {
        def commandLine = [executablePath, accessKey.get()]
        if (extension.local.identifier) {
            commandLine << '-localIdentifier' << extension.local.identifier
        }
        if (extension.applicationUrls) {
            commandLine << "-only" << assembleAppSpecifier(extension.applicationUrls)
        }
        if (extension.local.proxyHost) {
            commandLine << "-proxyHost" << extension.local.proxyHost
        }
        if (extension.local.proxyPort) {
            commandLine << "-proxyPort" << extension.local.proxyPort
        }
        if (extension.local.proxyUser) {
            commandLine << "-proxyUser" << extension.local.proxyUser
        }
        if (extension.local.proxyPass) {
            commandLine << "-proxyPass" << extension.local.proxyPass
        }
        if (extension.local.force) {
            commandLine << "-forcelocal"
        }
        commandLine.addAll(extension.local.additionalOptions)
        commandLine
    }

    @Override
    String getTunnelReadyMessage() {
        extension.local.tunnelReadyMessage
    }
}
