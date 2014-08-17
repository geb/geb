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
import groovy.xml.MarkupBuilder
import spock.lang.Shared

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class GebSpecWithServer extends GebSpec {

	@Shared TestHttpServer server

	private static final List<Integer> CROSS_BROWSER_PORTS = [8000, 8080, 8888, 9000, 9090]

	def setupSpec() {
		server = serverInstance
		server.start(testPort)
		browser.baseUrl = server.baseUrl
	}

	TestHttpServer getServerInstance() {
		new CallbackHttpServer()
	}

	int getTestPort() {
		def portIndex = System.getProperty("geb.port.index")
		portIndex ?  CROSS_BROWSER_PORTS[portIndex.toInteger()] : 0
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
		server.get = { HttpServletRequest request, HttpServletResponse response ->
			synchronized (this) { // MarkupBuilder has some static state, so protect
				try {
					response.setContentType("text/html")
					response.setCharacterEncoding("utf8")
					def writer = new OutputStreamWriter(response.outputStream, "utf8")
					writer << "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n"
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
				} catch (Exception e) {
					e.printStackTrace()
				}
			}
		}
	}

	def responseHtml(String html) {
		server.get = { HttpServletRequest request, HttpServletResponse response ->
			response.writer << html
		}
	}

	void html(Closure html) {
		responseHtml(html)
		go()
	}

}