/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.spock

import spock.lang.*
import geb.spock.test.TestHttpServer

/**
 * This test exists to verify that subclassing GebReportingSpec
 * and not providing a reports directory does not cause exceptions.
 */
class GebReportingSpecNoReportingSpec extends GebReportingSpec {

	@Shared server

	def setupSpec() {
		server = new TestHttpServer()
		server.start()
		server.get = { req, res ->
			res.outputStream << ""
		}
		
	}

	def getBaseUrl() {
		server.baseUrl
	}
	
	def "no exception thrown when no report dir"() {
		expect:
		true
	}
}