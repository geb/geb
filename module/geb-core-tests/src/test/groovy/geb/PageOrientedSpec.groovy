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
import geb.error.*
import geb.waiting.WaitTimeoutException
import org.codehaus.groovy.transform.powerassert.PowerAssertionError

@Stepwise
class PageOrientedSpec extends GebSpecWithServer {
	
	def setupSpec() {
		server.get = { req, res ->
			def path = req.requestURI == "/b" ? "b" :  "a"
			def other = path == "b" ? "a" : "b"
			res.outputStream << """
			<html>
			<body>
				<a href="/$other" id="$path">$other</a>
				<div id="uri">$req.requestURI</div>
			</body>
			</html>"""
		}
	}
	
	def "verify our server is configured correctly"() {
		when:
		go "/"
		then:
		$("#a").empty == false
		
		when:
		go "/a"
		then:
		$("#a").empty == false
		
		when:
		go "/b"
		then:
		$("#b").empty == false
	}
	
	def "verify the Page API works"() {
		when:
		to PageA
		
		then:
		at PageA
		
		when:
		link.click()
		
		then:
		at PageB
		page.class == PageB
		
		when:
		link.click()
		
		then:
		at PageA
		page.class == PageA
	}
	
	def "check accessing non navigator content"() {
		when:
		to PageA
		then:
		linkText == "b"
	}
	
	def "verify at checking works"() {
		when:
		to PageA
		and:
		at(PageC)

		then:
		PowerAssertionError error = thrown()
		error.message.contains('false')
	}

	def "verify isAt() works"() {
		when:
		to PageA

		then:
		isAt(PageA)
		!isAt(PageB)
		!isAt(PageC)
	}
	
	def "error when required value not present"() {
		when:
		to PageA
		notPresentValueRequired.text()
		then:
		thrown(RequiredPageValueNotPresent)
	}
	
	def "error when required component not present"() {
		when:
		to PageA
		notPresentRequired.text()
		then:
		thrown(RequiredPageContentNotPresent)
	}
	
	def "no error when non required component not present"() {
		when:
		to PageA
		notPresentNotRequired.text()
		then:
		notThrown(RequiredPageContentNotPresent)
	}

	def "no error when non required component times out"() {
		when:
		to PageA
		def content = notPresentNotRequiredWithWait
		then:
		notThrown(RequiredPageContentNotPresent)
		notThrown(WaitTimeoutException)
		content == null
	}
	
	def "error when explicitly requiring a component that is not present"() {
		when:
		to PageA
		notPresentNotRequired.require()
		then:
		thrown(RequiredPageContentNotPresent)
	}

	def "no error when explicitly requiring component that is present"() {
		when:
		to PageA
		link.require()
		then:
		notThrown(RequiredPageContentNotPresent)
	}
	
	def "variant to should cycle through and select match"() {
		when:
		to PageA
		linkWithVariantTo.click()
		then:
		at PageB
	}
	
	def "exception should be thrown when no to values match"() {
		when:
		to PageA
		linkWithVariantToNoMatches.click()
		then:
		thrown(UnexpectedPageException)
	}
	
	def "call in mixed in method from TextMatchingSupport"() {
		when:
		to PageA
		then:
		contains("b").matches("abc")
	}
	
	def "can use attribute notation on page content"() {
		when:
		to PageA
		then:
		link.@id == "a"
	}
	
	@Issue("http://jira.codehaus.org/browse/GEB-2")
	def "can call instance methods from content definition blocks"() {
		when:
		to InstanceMethodPage
		then:
		val == 3
	}
	
	@Issue("http://jira.codehaus.org/browse/GEB-139")
	def "convertToPath should not introduce slashes were it should not"() {
		when:'we go to the page by specifying the parameter manually'
		to ConvertPage, theParam: "foo"
		def manual = $('#uri').text()

		and:'using the convertToPath method'
		to ConvertPage, 'foo'
		def converted = $('#uri').text()

		then:'the results are the same'
		converted == manual

		and:'the raw page url does not contain the extra slash'
		getPageUrl(convertToPath('foo')) == 'http://domain.tld/theview?theParam=foo'

		and:'the default convertToPath still works'
		getPageUrl(convertToPath(1, 2)) == 'http://domain.tld/theview/1/2'
	}

	def "verify content aliasing works"() {
		when:
		to PageA
		then:
		linkTextAlias == 'b'
	}
}

class PageA extends Page {
	static at = { link }
	static content = {
		link(to: PageB) { $("#a") }
		linkWithVariantTo(to: [PageD, PageC, PageB]) { link }
		linkWithVariantToNoMatches(to: [PageD, PageC]) { link }
		linkText { link.text().trim() }
		linkTextAlias(aliases: 'linkText')
		notPresentValueRequired { $("div#asdfasdf").text() }
		notPresentRequired { $("div#nonexistant") }
		notPresentNotRequired(required: false) { $("div#nonexistant") }
		notPresentNotRequiredWithWait(required: false, wait: 1) { $("div#nonexistant") }
	}
}

class PageB extends Page {
	static at = { link }
	static content = {
		link(to: PageA) { $("#b") }
		linkText { link.text() }
	}
}

class PageC extends Page {
	static at = { false }
}

class PageD extends Page {
	static at = { assert 1 == 2 }
}

class ConvertPage extends Page {
	static url = 'http://domain.tld/theview'
	String convertToPath(param) {
		return "?theParam=$param"
	}
}

class InstanceMethodPage extends Page {
	static content = {
		val { getValue() }
	}
	
	def getValue() { 3 }
}