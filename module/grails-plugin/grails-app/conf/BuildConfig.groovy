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
grails.project.dependency.resolution = {
	inherits( "global" )
	repositories {
		grailsPlugins()
		grailsHome()
		mavenLocal()
		mavenCentral()
		mavenRepo "http://snapshots.repository.codehaus.org"
	}
	dependencies {
		// If we are building as part of geb, don't try and pull in
		// as gradle sets up the deps for us. This prop is set in the
		// gradle build.
		if (System.getProperty("geb.building") == null) {
			test("org.codehaus.geb:geb-grails:0.4")
		}
		
		// used for running this plugin's tests, not in user tests
		test("org.seleniumhq.selenium:selenium-htmlunit-driver:latest.release") {
			exclude 'xml-apis'
			export = false
		}
	}
}

/* -- disabled due to clover grails breaking clover in gradle
clover {
	license.path = new File(grailsSettings.baseDir, "../../clover.license").absolutePath
	initstring = new File(grailsSettings.projectWorkDir, "clover/clover.db").absolutePath
}
*/