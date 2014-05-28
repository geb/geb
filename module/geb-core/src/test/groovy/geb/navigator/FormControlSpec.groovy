/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.navigator

import geb.test.CrossBrowser
import geb.test.GebSpecWithServer
import spock.lang.Issue

@CrossBrowser
class FormControlSpec extends GebSpecWithServer {

	def textInputs() {
		when:
		html {
			input(name: "i1", type: "text", id: "i1", "")
			input(name: "i2", type: "text", id: "i2", value: "value", "")
		}

		def i1 = $().i1()
		def i2 = $().i2()

		then:
		i1*.@id == ["i1"]
		i1.value() == ""
		i1.value("foo")
		i1.value() == "foo"

		and:
		i2*.@id == ["i2"]
		i2.value() == "value"

		when:
		$().i1 = "bar"

		then:
		$().i1 == "bar"
	}

	def textarea() {
		when:
		html {
			textarea(name: "i1", id: "i1", "")
			textarea(name: "i2", id: "i2", "value")
		}

		def i1 = $().i1()
		def i2 = $().i2()

		then:
		i1*.@id == ["i1"]
		i1.value() == ""
		i1.value("foo")
		i1.value() == "foo"

		and:
		i2*.@id == ["i2"]
		i2.value() == "value"
		i2 << "-add"
		i2.value() == "value-add"

		and:
		$("textarea").value() == "foo"

		when:
		$().i1 = "bar"

		then:
		$().i1 == "bar"
	}

	def checkbox() {
		when:
		html {
			input(name: "i1", type: "checkbox", id: "i1", value: "i1", "")
			input(name: "i2", type: "checkbox", id: "i2", value: "i2", checked: "checked", "")
		}

		def i1 = $().i1()
		def i2 = $().i2()

		then:
		i1*.@id == ["i1"]
		i1.value().is(false)
		i1.value(true)
		i1.value() == "i1"

		and:
		i2*.@id == ["i2"]
		i2.value() == "i2"
		i2.value(false)
		i2.value().is(false)

		and:
		$("input").value() == "i1"

		when:
		$().i1 = false

		then:
		$().i1.is(false)

		when:
		$().i1 = "i1"

		then:
		$().i1 == "i1"

		when:
		$().i1 = false
		$().i1 = "not-the-value"

		then:
		$().i1 == false
	}

	def "radio - by value"() {
		when:
		html {
			input(type: "radio", name: "r", value: "r1", checked: "checked")
			input(type: "radio", name: "r", value: "r2")
			input(type: "radio", name: "r", value: "r3")
		}

		then:
		$().r().value() == "r1"
		$().r == "r1"

		when:
		$().r = "r2"

		then:
		$().r == "r2"

		when:
		$().r = "foo" // TODO: should throw exception

		then:
		$().r == "r2"
	}

	@Issue("http://jira.codehaus.org/browse/GEB-37")
	def "radio - by label text"() {
		when:
		html {
			input(type: "radio", name: "r", value: "r1", id: "r1", "")
			label(for: "r1", "r1 label")
			input(type: "radio", name: "r", value: "r2", id: "r2")
			label(for: "r2", "r2 label")
			input(type: "radio", name: "r", value: "r3", id: "r3")
			label(for: "r3", "r3 label")
		}

		then:
		$().r == null

		when:
		$().r = "r1 label"

		then:
		$().r == "r1"

		when:
		$().r = "r3 label"

		then:
		$().r == "r3"
	}

	def "appending"() {
		given:
		html {
			input(name: "i", "")
			textarea(name: "t", "")
		}

		when:
		$().i() << "a"

		then:
		$().i == "a"

		when:
		$().t() << "a"

		then:
		$().t == "a"

		when:
		$("input,textarea") << "b"

		then:
		$().i == "ab"
		$().t == "ab"
	}
}
