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

@Stepwise
class PageChangeCallbacksSpec extends GebSpecWithServer {

	def previousPage
	
	def "change callbacks"() {
		expect:
		page instanceof Page
		when:
		page(PageChangeCallbacksSpecPage1)
		then:
		page instanceof PageChangeCallbacksSpecPage1
		arg.class == Page
		method == "onLoad"
		when:
		previousPage = page
		page(PageChangeCallbacksSpecPage2)
		then:
		previousPage.arg.class == PageChangeCallbacksSpecPage2
		previousPage.method == 'onUnload'
		and:
		page instanceof PageChangeCallbacksSpecPage2
		page.arg.class == PageChangeCallbacksSpecPage1
		page.method == 'onLoad'
	}

}

class PageChangeCallbacksSpecPage1 extends Page {
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

class PageChangeCallbacksSpecPage2 extends PageChangeCallbacksSpecPage1 {}