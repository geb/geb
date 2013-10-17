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

def pluginSpecTestTypeClassName = "grails.plugin.spock.test.GrailsSpecTestType"
def coreSpecTestTypeClassName = "org.codehaus.groovy.grails.test.spock.GrailsSpecTestType"
def junit3TestTypeClassName = "org.codehaus.groovy.grails.test.junit3.JUnit3GrailsTestType"
def junit4TestTypeClassName = "org.codehaus.groovy.grails.test.junit4.JUnit4GrailsTestType"
def buildAdapterClassName = "geb.buildadapter.SystemPropertiesBuildAdapter"

def loadedTestTypes = []
def runningTests = false

// For Grails 2.3 spock is part of core so the spock plugin should no
// longer be used. Try to support both configurations.
tryToLoadTestTypes = {
	if(!tryToLoadTestType("spock", coreSpecTestTypeClassName)) {
		tryToLoadTestType("spock", pluginSpecTestTypeClassName)
	}
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

loadGebBuildAdapterClass = {
	def buildAdapterClass = softLoadClass(buildAdapterClassName)
	if (buildAdapterClass == null) {
		def gebPluginVersion = pluginSettings.getPluginInfo(gebPluginDir.path).version
		def msg = """
ERROR: No Geb testing adapters are installed

Starting with 0.6 of the Geb plugin for Grails, testing adapters are no longer installed automatically by the plugin.

You need to explicitly add these dependency definitions to your BuildConfig.groovy file for the testing flavour you want to use.

For example:

dependencies {
   test "org.gebish:geb-spock:$gebPluginVersion"
}

The available testing adapters are:

- geb-spock (for Spock Framework, requires the grails-spock plugin)
- geb-junit (for JUnit testing, no other plugins required)
- geb-easyb (for EasyB, requires the grails-easyb plugin)


The Grails specific classes such as grails.plugin.geb.GebSpec have also been removed. You should replace your usage
of these classes with the equivalent from the relevant testing adapter:

- spock: geb.spock.GebReportingSpec
- junit: geb.junit4.GebReportingTest
- easyb: Use 'geb' plugin instead of 'geb-grails'

Please see the Geb website for more information if required.
"""
		event('StatusError', [msg])
		exit 1
	}
	buildAdapterClass
}

eventTestPhasesStart = { phases ->
	if ("functional" in phases) {
		// do this early to fail fast
		loadGebBuildAdapterClass()
	}
}

eventTestPhaseStart = { phaseName ->
	if (phaseName == 'functional') {
		// GRAILS-7563
		if (!binding.hasVariable('serverContextPath')) {
			includeTargets << grailsScript("_GrailsPackage") 
			createConfig() // GRAILS-7562
			configureServerContextPath()
		}
		
		def buildAdapterClass = loadGebBuildAdapterClass()
		def baseUrl = argsMap["baseUrl"] ?: "http://${serverHost ?: 'localhost'}:$serverPort${serverContextPath == "/" ? "" : serverContextPath}/"
		System.setProperty(buildAdapterClass.BASE_URL_PROPERTY_NAME, baseUrl)
		
		def reportsDir = new File(grailsSettings.testReportsDir, 'geb')
		System.setProperty(buildAdapterClass.REPORTS_DIR_PROPERTY_NAME, reportsDir.absolutePath)
	}
}

eventTestPhasePrepared = { phaseName ->
    if (phaseName == 'functional') {
        //reset the base URL based on actual server port (starting with Grails 2.2.5, 2.3.1)
        //this event gets called after Tomcat has started and actual local port as been assigned
        def buildAdapterClass = loadGebBuildAdapterClass()
        def baseUrl = argsMap["baseUrl"] ?:
            "http://${serverHost ?: 'localhost'}:${getServerPort()}${serverContextPath == "/" ? "" : serverContextPath}/"
        System.setProperty(buildAdapterClass.BASE_URL_PROPERTY_NAME, baseUrl)
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
