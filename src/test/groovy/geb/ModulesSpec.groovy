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

class ModulesSpec extends GebSpecWithServer {

	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<div class="a"><p>a</p></div>
				<div class="b"><p>a</p></div>
				<div class="c"><div class="d"><p>d</p></div></div>
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
		divWithBaseAndSpecificBaseAndParam.p.text() == "d"
		divWithBaseAndSpecificBaseAndParam.p.text() == "d"
		divA.p.text() == "a"
		divC.innerDiv.p.text() == "d"
		divCWithRelativeInner.innerDiv.p.text() == "d"
	}
	
	def "call in mixed in method from TextMatchingSupport"() {
		when:
		to ModulesSpecPage
		then:
		"abc" ==~ divA.contains("b")
	}
}

class ModulesSpecPage extends Page {
	static content = {
		// A module that doesn't define a locator, given one at construction
		divNoBase { module ModulesSpecDivModuleNoLocator, $("div.$it") }
		
		// A module that defines a locator, given a param at construction
		divWithBase { module ModulesSpecDivModuleWithLocator, className: it }
		
		// A module that defines a location, and uses a param given at construction in the locator
		divWithBaseAndSpecificBaseAndParam { module ModulesSpecDivModuleWithLocator, $("div.c"), className: "d" }
		
		// A module that defines a location, and is contructed with no base or params
		divA { module ModulesSpecSpecificDivModule }
		
		// A module that itself has a module
		divC { module ModulesSpecDivModuleWithNestedDiv }
		
		// A module whose inner module is defined by the owner module's base
		divCWithRelativeInner { module ModulesSpecDivModuleWithNestedDivRelativeToModuleBase }
	}
}

class ModulesSpecDivModuleNoLocator extends Module {
	static content = {
		p { $("p") }
	}
}

class ModulesSpecDivModuleWithLocator extends Module {
	def className
	static base = { $("div.$className") }
	static content = {
		p { $("p") }
	}
}

class ModulesSpecSpecificDivModule extends Module {
	static base = { $("div.a") }
	static content = {
		p { $("p") }
	}
}

class ModulesSpecDivModuleWithNestedDiv extends Module {
	static base = { $("div.c") }
	static content = {
		innerDiv { module ModulesSpecDivModuleWithLocator, className: "d" }
	}
}

class ModulesSpecDivModuleWithNestedDivRelativeToModuleBase extends Module {
	static base = { $("div.c") }
	static content = {
		innerDiv { module ModulesSpecDivModuleWithLocator, $(), className: "d" }
	}
}