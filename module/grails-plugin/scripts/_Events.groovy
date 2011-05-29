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
def buildAdapterClassName = "geb.buildadapter.SystemPropertiesBuildAdapter"

def loadedTestTypes = []
def runningTests = false

tryToLoadTestTypes = {
	tryToLoadTestType("spock", specTestTypeClassName)
}

tryToLoadTestType = { name, typeClassName ->
	if (name in loadedTestTypes) return
	if (!binding.variables.containsKey("functionalTests")) return
	
	def typeClass = softLoadClass(typeClassName)
	if (typeClass) {
		if (!functionalTests.any { it.class == typeClass }) {
			functionalTests << typeClass.newInstance(name, 'functional')
		}
		loadedTestTypes << name
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

	tryToLoadTestTypes()
	
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
	tryToLoadTestTypes()
}

eventTestPhaseStart = { phaseName ->
	if (phaseName == 'functional') {
		// Needed when being driven by a different build engine
		if (!binding.hasProperty('serverContextPath')) {
			includeTargets << grailsScript("_GrailsPackage") 
			configureServerContextPath()
		}
			
		def buildAdapterClass = softLoadClass(buildAdapterClassName)
		if (!buildAdapterClass) {
			throw new IllegalStateException("failed to load build adapter")
		}
		
		def baseUrl = argsMap["baseUrl"] ?: "http://localhost:$serverPort$serverContextPath/"
		System.setProperty(buildAdapterClass.BASE_URL_PROPERTY_NAME, baseUrl)
		
		def reportsDir = new File(grailsSettings.testReportsDir, 'geb')
		System.setProperty(buildAdapterClass.REPORTS_DIR_PROPERTY_NAME, reportsDir.absolutePath)
	}
}

// Just upgrade plugins without user input when building this plugin
// Has no effect for clients of this plugin
if (grailsAppName == 'geb') {
	
	// Override to workaround GRAILS-7296
	org.codehaus.groovy.grails.test.support.GrailsTestTypeSupport.metaClass.getSourceDir = { ->
		new File(delegate.buildBinding.grailsSettings.testSourceDir, delegate.relativeSourcePath)
	}
	
	def resolveDependenciesWasInteractive = false
	eventResolveDependenciesStart = {
		resolveDependenciesWasInteractive = isInteractive
		isInteractive = false
	}

	eventResolveDependenciesEnd = {
		isInteractive = resolveDependenciesWasInteractive
	}
}