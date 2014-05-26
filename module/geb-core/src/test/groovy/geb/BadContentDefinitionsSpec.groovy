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

import geb.error.InvalidPageContent
import geb.test.*

class BadContentDefinitionsSpec extends GebSpecWithServer {

	def "no args"() {
		when:
		page BadContentDefinitionsSpecNoArgs
		then:
		thrown(InvalidPageContent)
	}

	def "non map arg"() {
		when:
		page BadContentDefinitionsSpecNonMap
		then:
		thrown(InvalidPageContent)
	}

	def "non closure factory"() {
		when:
		page BadContentDefinitionsSpecNonClosureFactory
		then:
		thrown(InvalidPageContent)
	}

	def "non map & non closure factory"() {
		when:
		page BadContentDefinitionsSpecNonMapNonClosureFactory
		then:
		thrown(InvalidPageContent)
	}

	def "more than two args"() {
		when:
		page BadContentDefinitionsSpecThreeArgs
		then:
		thrown(InvalidPageContent)
	}

	def "unknownElementAliased"() {
		when:
		page BadContentDefinitionsSpecUnknownElementAliased
		then:
		InvalidPageContent e = thrown()
		e.message == "Definition of page component template 'foo' of 'BadContentDefinitionsSpecUnknownElementAliased' aliases an unknown element 'bar'"
	}

}

class BadContentDefinitionsSpecNoArgs extends Page {
	static content = { foo() }
}

class BadContentDefinitionsSpecNonMap extends Page {
	static content = { foo(1) }
}

class BadContentDefinitionsSpecNonClosureFactory extends Page {
	static content = { foo([:], 1) }
}

class BadContentDefinitionsSpecNonMapNonClosureFactory extends Page {
	static content = { foo(1, 1) }
}

class BadContentDefinitionsSpecThreeArgs extends Page {
	static content = { foo(1, 1, 2) }
}

class BadContentDefinitionsSpecUnknownElementAliased extends Page {
	static content = { foo(aliases: 'bar') }
}
