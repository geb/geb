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
package geb.gradle.saucelabs

import org.gradle.api.InvalidUserDataException
import org.gradle.api.artifacts.Configuration

class SauceConnectOperations {

    private URLClassLoader sauceConnectManagerClassLoader

    Configuration sauceConnectConfiguration

    SauceConnectOperations(Configuration sauceConnectConfiguration) {
        this.sauceConnectConfiguration = sauceConnectConfiguration
    }

    def getOperatingSystem() {
        loadOperatingSystemClass().operatingSystem
    }

    Class loadSauceConnectFourManagerClass() {
        loadClass("com.saucelabs.ci.sauceconnect.SauceConnectFourManager")
    }

    private Class loadOperatingSystemClass() {
        loadClass('com.saucelabs.ci.sauceconnect.SauceConnectFourManager$OperatingSystem')
    }

    protected URLClassLoader getSauceConnectManagerClassLoader() {
        if (!sauceConnectManagerClassLoader) {
            if (sauceConnectConfiguration.empty) {
                throw new InvalidUserDataException("'sauceConnect' configuration is empty, please add a dependency on 'ci-sauce' artifact from 'com.saucelabs' group to it")
            }
            sauceConnectManagerClassLoader = new URLClassLoader(sauceConnectConfiguration.files*.toURI()*.toURL() as URL[])
        }
        sauceConnectManagerClassLoader
    }

    private Class loadClass(String name) {
        try {
            getSauceConnectManagerClassLoader().loadClass(name)
        } catch (ClassNotFoundException e) {
            throw new InvalidUserDataException("Could not load '$name' class, did you add a dependency on 'ci-sauce' artifact from 'com.saucelabs' group to 'sauceConnect' configuration?", e)
        }
    }
}
