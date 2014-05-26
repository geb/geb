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

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class RemoteDriverOperationsSpec extends Specification {

	@Shared nonRemoteClassLoader
	@Shared withRemoteClassLoader

	def setupSpec() {
		withRemoteClassLoader = getClass().classLoader

		def classpath = withRemoteClassLoader.getURLs().findAll { it.path.contains("groovy-all") }
		nonRemoteClassLoader = new URLClassLoader(classpath as URL[], withRemoteClassLoader.parent)
	}

	@Unroll("remote driver availability test - available = #isAvailable")
	def "remote driver availability test"() {
		when:
		def o = new RemoteDriverOperations(classLoader)

		then:
		o.remoteDriverAvailable == isAvailable

		where:
		classLoader           | isAvailable
		nonRemoteClassLoader  | false
		withRemoteClassLoader | true
	}

}