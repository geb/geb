/*
 * Copyright 2011 the original author or authors.
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
package geb.waiting

import org.codehaus.groovy.runtime.powerassert.PowerAssertionError
import spock.lang.Specification
import spock.lang.Timeout

/**
 * Tests the waiting algorithm, note that WaitingSupportSpec also tests Wait and covers many cases.
 */
class WaitSpec extends Specification {

	@Timeout(5)
	def "wait algorithm handles cases where the block takes a long time"() {
		given:
		def wait = new Wait(2, 0.5)

		when:
		wait.waitFor { sleep 3000 }

		then:
		thrown WaitTimeoutException
	}

	def "waitFor block contents are implicitly asserted"() {
		given:
		def wait = new Wait(0.5)

		when:
		wait.waitFor { 'not empty'.empty }

		then:
		WaitTimeoutException exception = thrown()
		exception.cause in PowerAssertionError
		exception.cause.message.contains("'not empty'.empty")
	}

}