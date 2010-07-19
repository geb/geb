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

import geb.test.*
import spock.lang.*

class ModulesSpec extends GebSpecWithServer {

	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<div class="a"><p>a</p></div>
				<div class="b"><p>a</p></div>
			</body>
			</html>"""
		}
	}
	
	def "modules"() {
		when:
		to ModulesSpecPage
		then:
		divNoBase("a").p.text() == "a"
		divWithBase("a").p.text() == "a"
	}
	
}

class ModulesSpecPage extends Page {
	static content = {
		divNoBase { module ModulesSpecDivModuleNoLocator, find("div.$it") }
		divWithBase { module ModulesSpecDivModuleWithLocator, className: it }
	}
}

class ModulesSpecDivModuleNoLocator extends Module {
	static content = {
		p { find("p") }
	}
}

class ModulesSpecDivModuleWithLocator extends Module {
	def className
	static locator = { find("div.$className") }
	static content = {
		p { find("p") }
	}
}