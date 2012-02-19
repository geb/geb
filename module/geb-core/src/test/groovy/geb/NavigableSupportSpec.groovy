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
import geb.error.UnresolvablePropertyException

class NavigableSupportSpec extends GebSpecWithServer {

	def setupSpec() {
		responseHtml {
			body {
				['a', 'b', 'c'].each {
					p(it, 'class': it)
				}
				input(type: "text", name: "e", value: "val")
			}
		}
	}
	
	def setup() {
		go()
	}
	
	def "no args"() {
		expect:
		$().tag() == "html"
		// find().tag() == "html" // doesn't work due to Groovy not dispatching to our method
	}

	def "just index"() {
		expect:
		$(0).tag() == "html"
		find(0).tag() == "html"
	}

	def "just selector"() {
		expect:
		$("p").size() == 3
		find("p").size() == 3
	}
	
	def "just attributes"() {
		expect:
		$(class: "a").text() == "a"
		find(class: "a").text() == "a"
	}

	def "just attributes - but text"() {
		expect:
		$(text: "a").text() == "a"
		find(text: "a").text() == "a"
	}

	def "selector and index"() {
		expect:
		$("p", 1).text() == "b"
		find("p", 1).text() == "b"
	}

	def "selector and attributes"() {
		expect:
		$("p", class: "c").text() == "c"
		find("p", class: "c").text() == "c"
	}
	
	@Ignore // See NavigableSupport
	def "attributes and index"() {
		expect:
		$(1, class: ~/\w/).text() == "b"
		find(1, class: ~/\w/).text() == "b"
	}

	@Ignore // See NavigableSupport
	def "selector, attributes and index"() {
		expect:
		$("p", 1, class: ~/\w/).text() == "b"
		find("p", 1, class: ~/\w/).text() == "b"
	}
	
	def "delegating missing properties to the navigator"() {
		expect:
		e == "val"
		when:
		e = "changed"
		then:
		e == "changed"
	}
	
	def "delegating missing methods to the navigator"() {
		expect:
		e().tag() == "input"
	}
	
	def "invalid property access throws unresolvable exception"() {
		when:
		z
		then:
		thrown(UnresolvablePropertyException)
	}

	def "invalid property assignment throws unresolvable exception"() {
		when:
		z = 3
		then:
		thrown(UnresolvablePropertyException)
	}

	def "dynamic method call returns empty navigator"() {
		expect:
		z().empty
	}
	
	def "composition with navigators"() {
		expect:
		$($(".a")).size() == 1
		$($(".a"), $(".c")).size() == 2
		$($("p"), $("input")).size() == 4
	}

	def "composition with content"() {
		when:
		to(NavigableSupportSpecPage)

		then:
		$(input).size() == 1
		$(input, pElem('a')).size() == 2
		$(input, pElems).size() == 4
	}

	def "composition with web elements"() {
		expect:
		$($(".a").firstElement()).size() == 1
		$($(".a").firstElement(), $(".c").firstElement()).size() == 2
		$(*($("p").allElements()), $("input").firstElement()).size() == 4
	}
	
	def "attribute access notation"() {
		expect:
		$("p").@class == 'a'
		$("p")*.@class == ['a', 'b', 'c']
	}
	
}

class NavigableSupportSpecPage extends Page {
	static content = {
		pElem { elemClass -> $('p', 'class': elemClass) }
		pElems { $('p') }
		input { $('input') }
	}
}