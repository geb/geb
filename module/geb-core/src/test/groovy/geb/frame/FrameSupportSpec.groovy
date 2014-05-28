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
package geb.frame

import geb.Module
import geb.Page
import spock.lang.Unroll

class FrameSupportSpec extends BaseFrameSupportSpec {

	def "verify the server is configured correctly for main page"() {
		expect:
		$('frame').size() == 2
	}

	@Unroll("verify that server is configured correctly for frame page: #text")
	def "verify that server is configured correctly for frame pages"() {
		when:
		go "$text"

		then:
		$('span').text() == text

		where:
		text << ['header', 'footer', 'inline']
	}

	def "expect the closure argument passed to withFrame to be executed for navigator as frame identifier"() {
		when:
		boolean called = false
		withFrame($('#footer')) {
			called = true
		}

		then:
		called
	}

	private boolean isInFramesContext() {
		title == 'frames'
	}

	@Unroll
	@SuppressWarnings(['SpaceAfterClosingBrace', 'SpaceBeforeOpeningBrace'])
	def "ensure original context is kept after a withFrame call"() {
		when:
		withFrame(frameFactory.call()) {
			assert page in FrameSupportSpecPage
		}

		then:
		inFramesContext

		when:
		withFrame(frameFactory.call()) {
			throw new Exception()
		}

		then:
		thrown(Exception)
		inFramesContext

		where:
		frameFactory << [{ 'header' }, { 0 }, { $('#header-id') }, { page.footer }]
	}

	@Unroll
	@SuppressWarnings(['SpaceAfterClosingBrace', 'SpaceBeforeOpeningBrace'])
	def "page is restored to what it was before a withFrame call"() {
		when:
		withFrame(frameFactory.call()) {
			page FrameSupportSpecFramePage
		}

		then:
		page in FrameSupportSpecPage

		when:
		withFrame(frameFactory.call()) {
			page FrameSupportSpecFramePage
			throw new Exception()
		}

		then:
		thrown(Exception)
		page in FrameSupportSpecPage

		where:
		frameFactory << [{ 'header' }, { 0 }, { $('#header-id') }, { page.footer }]
	}

	def "page content with page parameter specified changes the page for the closure body"() {
		when:
		withFrame(footerWithPageParam) {
			assert page in FrameSupportSpecFramePage
		}

		then:
		page in FrameSupportSpecPage

		when:
		withFrame(footerWithPageParam, FrameSupportSpecFramePage) {
			throw new Exception()
		}

		then:
		thrown(Exception)
		page in FrameSupportSpecPage
	}

	def "ensure pages and modules have withFrame available"() {
		when:
		to FrameSupportSpecPage

		then:
		page.callAllVariantsOfWithFrame() == 3
		page.returnValueOfWithFrameCallForPageContent == 'footer'
		mod.callAllVariantsOfWithFrame() == 3
	}
}

class FrameSupportSpecPage extends Page {
	static url = "/frames"
	static content = {
		footer { $('#footer') }
		footerWithPageParam(page: FrameSupportSpecFramePage) { footer }
		mod { module FrameSupportSpecModule }
	}

	def callAllVariantsOfWithFrame() {
		def count = 0
		def block = { count++ }
		withFrame(0, block)
		withFrame('header', block)
		withFrame(footer, block)
		count
	}

	def getReturnValueOfWithFrameCallForPageContent() {
		withFrame(footer) { $('span').text() }
	}
}

class FrameSupportSpecFramePage extends Page {
}

class FrameSupportSpecModule extends Module {
	def callAllVariantsOfWithFrame() {
		def count = 0
		def block = { count++ }
		withFrame(0, block)
		withFrame('header', block)
		withFrame(find('#footer'), block)
		count
	}
}
