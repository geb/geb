/* Copyright 2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *			http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geb.test

import geb.Browser
import spock.lang.*
import javax.servlet.http.HttpServletResponse
import groovy.xml.MarkupBuilder

class GebSpecWithServer extends GebSpec {

	@Shared server
	
	def setupSpec() {
		server = new CallbackHttpServer()
		server.start()
		browser.baseUrl = server.baseUrl
	}
	
	Browser createBrowser() {
		def browser = super.createBrowser()
		if (server) {
			browser.baseUrl = server.baseUrl
		}
		browser
	}
	
	def cleanupSpec() {
		server?.stop()
	}

	def responseHtml(Closure htmlMarkup) {
		server.get = { request, response ->
			synchronized(this) { // MarkupBuilder has some static state, so protect
				def writer = new OutputStreamWriter(response.outputStream)
				new MarkupBuilder(writer).html {
					htmlMarkup.delegate = delegate
					htmlMarkup.resolveStrategy = Closure.DELEGATE_FIRST
					if (htmlMarkup.maximumNumberOfParameters < 2) {
						htmlMarkup(request)
					} else {
						htmlMarkup(request, response)
					}
				}
				writer.flush()
			}
		}
	}
}