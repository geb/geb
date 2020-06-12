/*
 * Copyright 2015 the original author or authors.
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
package configuration

import geb.Configuration
import geb.ConfigurationLoader

class InlineConfiguration {

    static Configuration parseConfigScript(ClassLoader classLoader, String script) {
        def tempDir = File.createTempDir()
        try {
            def groovyClassLoader = new GroovyClassLoader(classLoader)
            groovyClassLoader.addClasspath(tempDir.absolutePath)
            def configFile = new File(tempDir, "GebConfig.groovy")
            configFile << script
            new ConfigurationLoader(null, null, groovyClassLoader).getConf(configFile.name)
        } finally {
            tempDir.deleteDir()
        }
    }

}