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

import geb.test.GebSpecWithServer
import spock.lang.Stepwise

@Stepwise
class PageLoadUnloadListeningSpec extends GebSpecWithServer {

	def previousPage
	
	def "change callbacks"() {
		expect:
		page instanceof Page

		when:
		page(PageLoadUnloadListeningSpecPage1)

		then:
		page instanceof PageLoadUnloadListeningSpecPage1
		arg.class == Page
		method == "onLoad"

		when:
		previousPage = page
		page(PageLoadUnloadListeningSpecPage2)

		then:
		previousPage.arg.class == PageLoadUnloadListeningSpecPage2
		previousPage.method == 'onUnload'

		and:
		page instanceof PageLoadUnloadListeningSpecPage2
		page.arg.class == PageLoadUnloadListeningSpecPage1
		page.method == 'onLoad'
	}

}

class PageLoadUnloadListeningSpecPage1 extends Page {
	def arg
	def method
	
	void onLoad(Page previousPage) {
		method = 'onLoad'
		arg = previousPage
	}
	
	void onUnload(Page nextPage) {
		method = 'onUnload'
		arg = nextPage
	}
}

class PageLoadUnloadListeningSpecPage2 extends PageLoadUnloadListeningSpecPage1 {}