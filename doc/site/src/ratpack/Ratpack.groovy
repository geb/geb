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


import geb.site.Manuals
import ratpack.groovy.template.TextTemplateModule
import ratpack.guice.ConfigurableModule
import ratpack.server.ServerConfig

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

ratpack {
    serverConfig {
        props(getClass().getResource("/ratpack.properties"))
    }
    bindings {
        module(TextTemplateModule) {
            it.staticallyCompile  = true
        }
        module(ManualsModule)
        add new StartupTime()
    }
    handlers {
        files {
            dir("public")
            indexFiles("index.html")
        }

        get(':page?') { StartupTime startupTime, Manuals manuals ->
            lastModified(startupTime.time) {
                def highlightPages = [
                    crossbrowser: "Cross Browser",
                    content     : "jQuery-like API",
                    pages       : "Page Objects",
                    async       : "Asynchronous Pages",
                    testing     : "Testing",
                    integration : "Build Integration"
                ]

                def pageToken = pathTokens.page ?: 'index'
                def page = pageToken in (highlightPages.keySet() + ['index', 'lists']) ? pageToken : "notfound"

                def model = [
                    manuals: manuals,
                    pages  : [Highlights: highlightPages],
                    page   : page
                ]

                render groovyTemplate(model, 'main.html')
            }
        }
    }
}

class StartupTime {
    final Date time = new Date()
}

class ManualsModule extends ConfigurableModule<Manuals> {

    protected void configure() {
    }

    @Override
    protected Manuals createConfig(ServerConfig serverConfig) {
        serverConfig.get("/manuals", Manuals)
    }
}

