/*
 * Copyright 2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

def specTestTypeClassName = "grails.plugin.spock.test.GrailsSpecTestType"
def gebSpecClassName = "grails.plugin.geb.GebSpec"

def loadedSpock = false
def runningTests = false

tryToLoadSpock = {
	if (loadedSpock) return
	if (!binding.variables.containsKey("functionalTests")) return
	
	def specTestTypeClass = softLoadClass(specTestTypeClassName)
	if (specTestTypeClass) {
		if (!functionalTests.any { it.class == specTestTypeClass }) {
			functionalTests << specTestTypeClass.newInstance('spock', 'functional')
		}
		loadedSpock = true
	}
}

softLoadClass = { className ->
	try {
		classLoader.loadClass(className)
	} catch (ClassNotFoundException e) {
		null
	}
}

eventAllTestsStart = {
	runningTests = true
	tryToLoadSpock()
}

eventPackagePluginsEnd = {
	tryToLoadSpock()
}

eventTestPhaseStart = { phaseName ->
	if (phaseName == 'functional') {
		def specTestTypeClass = softLoadClass(specTestTypeClassName)
		if (specTestTypeClass) {
			def gebSpecClass = softLoadClass(gebSpecClassName)
			if (!gebSpecClass) {
				throw new IllegalStateException("failed to load geb spec class even though spock is in the house")
			}
			
			// Needed when being driven by a different build engine
			if (!binding.hasProperty('serverContextPath')) {
				includeTargets << grailsScript("_GrailsPackage") 
				configureServerContextPath()
			}
			
			gebSpecClass.classBaseUrl = argsMap["baseUrl"] ?: "http://localhost:$serverPort$serverContextPath/"
			gebSpecClass.classReportDir = new File(grailsSettings.testReportsDir, 'geb-spock')
		}
	}
}