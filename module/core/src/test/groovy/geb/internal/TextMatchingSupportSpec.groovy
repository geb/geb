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
package geb.internal

import geb.test.util.GebSpec
import spock.lang.*

class TextMatchingSupportSpec extends Specification {

	// Can't have expected in name due to
	// http://code.google.com/p/spock/issues/detail?id=115
	@Unroll("#method - #input")
	def "t"() {
		given:
		def matchers = new TextMatchingSupport()
		expect:
		matchers."$method"(input).toString() == expected
		where:
		method           | input | expected
		"contains"       | "."   | ".*\\Q.\\E.*"
		"iContains"      | "."   | "(?i).*\\Q.\\E.*"
		"startsWith"     | "."   | "\\Q.\\E.*"
		"iStartsWith"    | "."   | "(?i)\\Q.\\E.*"
		"endsWith"       | "."   | ".*\\Q.\\E"
		"iEndsWith"      | "."   | "(?i).*\\Q.\\E"
		"containsWord"   | "."   | "(\$|\\s)\\Q.\\E(^|\\s)"
		"iContainsWord"  | "."   | "(?i)(\$|\\s)\\Q.\\E(^|\\s)"
		"contains"       | ~"."  | ".*..*"
		"iContains"      | ~"."  | "(?i).*..*"
		"startsWith"     | ~"."  | "..*"
		"iStartsWith"    | ~"."  | "(?i)..*"
		"endsWith"       | ~"."  | ".*."
		"iEndsWith"      | ~"."  | "(?i).*."
		"containsWord"   | ~"."  | "(\$|\\s).(^|\\s)"
		"iContainsWord"  | ~"."  | "(?i)(\$|\\s).(^|\\s)"
	}

}