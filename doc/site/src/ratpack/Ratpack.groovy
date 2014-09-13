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


import ratpack.groovy.templating.TemplatingModule

import static ratpack.groovy.Groovy.groovyTemplate
import static ratpack.groovy.Groovy.ratpack

ratpack {
	bindings {
		config(TemplatingModule).staticallyCompile = true
		bind new StartupTime()
	}
	handlers {
		assets 'public', 'index.html'

		get('manual/:version/api/') {
			blocking {
				context.file("public/${request.path}").toFile().listFiles().toList()*.name
			} then { List<String> files ->
				render groovyTemplate('fileListing.html', files: files, title: "Groovy API for Geb ${pathTokens.get('version')}")
			}
		}

		get(':page?') { StartupTime startupTime ->
			lastModified(startupTime.time) {
				def highlightPages = [
					crossbrowser: "Cross Browser",
					content: "jQuery-like API",
					pages: "Page Objects",
					async: "Asynchronous Pages",
					testing: "Testing",
					integration: "Build Integration"
				]

				def page = pathTokens.page ?: 'index'
				if (page in (highlightPages.keySet() + 'index')) {
					def model = [
						oldManuals: launchConfig.getOther('old', '').tokenize(','),
						currentManual: launchConfig.getOther('current', ''),
						snapshotManual: launchConfig.getOther('snapshot', ''),
						pages: [Highlights: highlightPages],
						page: page
					]

					render groovyTemplate(model, 'main.html')
				} else {
					clientError(404)
				}
			}
		}
	}
}

class StartupTime {
	final Date time = new Date()
}
