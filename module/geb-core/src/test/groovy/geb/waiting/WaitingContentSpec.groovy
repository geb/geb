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

import geb.*
import geb.error.*
import geb.waiting.WaitTimeoutException
import geb.test.util.*
import spock.lang.*

class WaitingContentSpec extends GebSpecWithServer {

	def params = [:]
	def factory = { $("div") }
	def showDelay = 1
	
	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
				<html>
				<head>
				  <script type="text/javascript" charset="utf-8">
				    function showIn(i) {
				      setTimeout(function() {
				        document.body.innerHTML = "<div>a</div>";
				      }, i * 1000);
				    }
				  </script>
				</head>
				<body>
				</body>
				</html>
			"""
		}
	}
	
	protected getContent() {
		TestPage.content = {
			delegate.div(params, factory)
		}
		
		go()
		page TestPage
		js.showIn(showDelay)
		div
	}
	
	def "no wait"() {
		when:
		content
		
		then:
		thrown RequiredPageContentNotPresent
		
	}
	
	def "default wait"() {
		when:
		params = [wait: true]
		
		then:
		content.text() == "a"
	}
	
	def "wait timeout"() {
		when:
		showDelay = 2
		params = [wait: 1]
		
		and:
		content
		
		then:
		thrown WaitTimeoutException
	}
	
	def "custom retry interval"() {
		when:
		params = [wait: [5, 1]]
		
		then:
		content
	}
	
	def "wait preset"() {
		when:
		params = [wait: "somepreset"]
		
		then:
		content
	}

	@Unroll
	def "invalid wait values"() {
		when:
		params = [wait: value]
		
		and:
		content
		
		then:
		thrown IllegalArgumentException
		
		where:
		value << [[], [1], [1,2,3], ["asds", "asdas"]]
	}
	
	def "waiting for non content - fail"() {
		given:
		factory = { false }
		params = [wait: 1]
		
		when:
		content
		
		then:
		thrown WaitTimeoutException
	}

	def "waiting for non content - pass"() {
		when:
		def counter = 0
		factory = { counter++ > 3 }
		params = [wait: [3, 0.1]]
		
		then:
		content
	}
}

class TestPage extends Page {
	static content = null
}