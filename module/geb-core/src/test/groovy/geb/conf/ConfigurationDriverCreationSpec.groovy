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
package geb.conf

import geb.driver.*
import spock.lang.*

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import geb.error.UnknownDriverShortNameException
import geb.error.UnableToLoadAnyDriversException

class ConfigurationDriverCreationSpec extends Specification {

	@Shared classLoader
	
	def d
	
	def setupSpec() {
		// We have to remove the ie driver from the classpath
		def thisLoader = getClass().classLoader
		def classpath = thisLoader.getURLs().findAll { !it.path.contains("selenium-ie-driver") }
		classLoader = new URLClassLoader(classpath as URL[], thisLoader.parent)
	}
	
	def "no property"() {
		given:
		d = conf().driverInstance
		expect:
		isInstanceOf(HtmlUnitDriver, d)
		cleanup:
		d?.quit()
	}
	
	def "specific short name"() {
		given:
		d = conf(p("geb.driver": "htmlunit")).driverInstance
		expect:
		isInstanceOf(HtmlUnitDriver, d)
	}

	def "specific valid short name but not available"() {
		when:
		conf(p("geb.driver": "ie")).driverInstance
		then:
		Exception e = thrown()
		isInstanceOf(UnableToLoadAnyDriversException, e)
	}
	
	def "specific invalid shortname"() {
		when:
		conf(p("geb.driver": "garbage")).driverInstance
		then:
		Exception e = thrown()
		isInstanceOf(UnknownDriverShortNameException, e)
	}
	
	def "specific list of drivers"() {
		given:
		d = conf(p("geb.driver": "ie:htmlunit")).driverInstance
		expect:
		isInstanceOf(HtmlUnitDriver, d)
	}
	
	def "specific valid class name"() {
		given:
		d = conf(p("geb.driver": HtmlUnitDriver.name)).driverInstance
		expect:
		isInstanceOf(HtmlUnitDriver, d)
	}

	def "specific invalid class name"() {
		when:
		d = conf(p("geb.driver": "a.b.c")).driverInstance
		then:
		Exception e = thrown()
		isInstanceOf(UnableToLoadAnyDriversException, e)
	}
	
	def "specify instance"() {
		given:
		def driver = loadClass(HtmlUnitDriver).newInstance()
		d = conf(c(driver: driver)).driverInstance
		expect:
		d.is(driver)
	}
	
	def "specify driver name in config"() {
		given:
		d = conf(c(driver: HtmlUnitDriver.name)).driverInstance
		expect:
		isInstanceOf(HtmlUnitDriver, d)
	}

	def "specify driver names in config"() {
		given:
		d = conf(c(driver: "ie:htmlunit")).driverInstance
		expect:
		isInstanceOf(HtmlUnitDriver, d)
	}
	
	def "specify creation closure"() {
		given:
		def config = new ConfigObject()
		config.cacheDriver = false
		config.driver = { new HtmlUnitDriver() }
		d = new Configuration(config).driverInstance

		expect:
		d instanceof HtmlUnitDriver
	}
	
	def p(m = [:]) {
		def p = new Properties()
		p.putAll(m)
		p
	}
	
	def c(m = [:]) {
		def c = loadClass(ConfigObject).newInstance()
		c.putAll(m)
		c
	}
	
	def conf(Object[] args) {
		def conf = loadClass(Configuration).newInstance(*args)
		conf.cacheDriver = false
		conf
	}

	boolean isInstanceOf(Class clazz, Object instance) {
		loadClass(clazz).isInstance(instance)
	}
	
	def cleanup() {
		d?.quit()
	}
	
	def loadClass(Class clazz) {
		classLoader.loadClass(clazz.name)
	}
}