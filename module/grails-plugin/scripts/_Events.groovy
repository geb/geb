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
def junit3TestTypeClassName = "org.codehaus.groovy.grails.test.junit3.JUnit3GrailsTestType"
def junit4TestTypeClassName = "org.codehaus.groovy.grails.test.junit4.JUnit4GrailsTestType"
def runtimeAdapterClassName = "grails.plugin.geb.internal.RuntimeAdapter"

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
	
	[junit3TestTypeClassName, junit4TestTypeClassName].each { testTypeClassName ->
		def testTypeClass = softLoadClass(testTypeClassName)
		if (testTypeClass) {
			if (!functionalTests.any { it.class == testTypeClass }) {
				functionalTests << testTypeClass.newInstance('functional', 'functional')
			}
		}
	}
}

eventPackagePluginsEnd = {
	tryToLoadSpock()
}

eventTestPhaseStart = { phaseName ->
	if (phaseName == 'functional') {
		// Needed when being driven by a different build engine
		if (!binding.hasProperty('serverContextPath')) {
			includeTargets << grailsScript("_GrailsPackage") 
			configureServerContextPath()
		}
			
		def runtimeAdapter = softLoadClass(runtimeAdapterClassName)
		if (!runtimeAdapter) {
			throw new IllegalStateException("failed to load runtime adapter")
		}
		runtimeAdapter.baseUrl = argsMap["baseUrl"] ?: "http://localhost:$serverPort$serverContextPath/"
		runtimeAdapter.reportDir = new File(grailsSettings.testReportsDir, 'geb')
	}
}