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
package grails.plugin.geb

import grails.plugin.geb.internal.RuntimeAdapter
import geb.spock.GebReportingSpec
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.test.support.GrailsTestAutowirer

import spock.lang.*

class GebSpec extends GebReportingSpec {

	File getReportDir() {
		RuntimeAdapter.reportDir
	}
	
	String getBaseUrl() {
		RuntimeAdapter.baseUrl
	}
	
	def setupSpec() {
		new GrailsTestAutowirer(ApplicationHolder.application.mainContext).autowire(this)
	}
}