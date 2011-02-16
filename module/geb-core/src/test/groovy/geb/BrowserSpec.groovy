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
package geb

import geb.test.util.*
import geb.conf.*

class BrowserSpec extends GebSpec {

	def "clear cookies"() {
		when:
		browser.clearCookies()
		then:
		notThrown(Throwable)
	}

	def "load default config"() {
		expect:
		browser.config.rawConfig.testValue == true
	}
	
	def "bad config location throws exception"() {
		when:
		new BadConfigLocationBrowser()
		
		then:
		thrown UnableToLoadConfigurationException
	}

	def "no config location yields default config"() {
		when:
		def browser = new NoConfigLocationBrowser()
		
		then:
		browser.config.rawConfig.size() == 0
	}
	
}

class BadConfigLocationBrowser extends Browser {
	protected URL getConfigurationLocation() {
		new URL("file:///idontexist")
	}
}

class NoConfigLocationBrowser extends Browser {
	protected URL getConfigurationLocation() {
		null
	}
}