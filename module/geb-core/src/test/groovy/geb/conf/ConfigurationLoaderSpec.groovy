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

import spock.lang.*

class ConfigurationLoaderSpec extends Specification {
	
	def loader
	def config
	
	protected loaderWithEnv(String env) {
		loader = new ConfigurationLoader(env)
	}

	protected loaderWithNoEnv() {
		loader = new ConfigurationLoader()
	}
	 
	protected load(URL location) {
		config = loader.load(location)
	}
	
	protected getGoodScript() {
		getClass().getResource("good-conf.groovy")
	}
	
	def "load file from classpath with no env"() {
		given:
		loaderWithNoEnv()
		
		when:
		load goodScript
		
		then:
		config.rawConfig.a == 1
	}
	
	def "load file from classpath with env"() {
		given:
		loaderWithEnv env
		
		when:
		load goodScript
		
		then:
		config.rawConfig.a == value
		
		where:
		env  | value
		"e1" | 2
		"e2" | 3
	}

	def "load non-existent bad url"() {
		given:
		loaderWithNoEnv()
		
		when:
		load new URL("file:///idontexist")
		
		then:
		thrown UnableToLoadConfigurationException
	}
	
}