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
package geb.driver

import spock.lang.*
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.firefox.FirefoxDriver
import geb.error.UnknownDriverShortNameException
import geb.error.UnableToLoadAnyDriversException

class PropertyBasedDriverFactorySpec extends Specification {

	def "no property"() {
		given:
		def d = factory(props()).driver
		expect:
		d instanceof HtmlUnitDriver
		cleanup:
		d?.quit()
	}
	
	def "specific short name"() {
		given:
		def d = factory(props("geb.driver": "firefox")).driver
		expect:
		d instanceof FirefoxDriver
		cleanup:
		d?.quit()
	}

	def "specific valid short name but not available"() {
		when:
		factory(props("geb.driver": "ie")).driver
		then:
		thrown(UnableToLoadAnyDriversException)
	}
	
	def "specific invalid shortname"() {
		when:
		factory(props("geb.driver": "garbage")).driver
		then:
		thrown(UnknownDriverShortNameException)
	}
	
	def "specific list of drivers"() {
		given:
		def d = factory(props("geb.driver": "ie:firefox")).driver
		expect:
		d instanceof FirefoxDriver
		cleanup:
		d?.quit()
	}
	
	def "specific valid class name"() {
		given:
		def d = factory(props("geb.driver": HtmlUnitDriver.name)).driver
		expect:
		d instanceof HtmlUnitDriver
		cleanup:
		d?.quit()
	}

	def "specific invalid class name"() {
		when:
		def d = factory(props("geb.driver": "a.b.c")).driver
		then:
		thrown(UnableToLoadAnyDriversException)
	}
	
	def props(m = [:]) {
		def p = new Properties()
		p.putAll(m)
		p
	}
	
	def factory(Object[] args) {
		new PropertyBasedDriverFactory(*args)
	}

}