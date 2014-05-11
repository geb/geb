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

	private static final List<Integer> SAUCE_PORTS = [
		2000, 2001, 2020, 2222, 3000, 3001, 3030, 3333, 4000, 4001, 4040, 4502, 4503, 5000, 5001, 5050,
		5555, 6001, 6060, 7000, 7070, 7777, 8000, 8001, 8003, 8031, 8080, 8081, 8888, 9000,
		9001, 9080, 9090, 9999, 49221
	].asImmutable()

	def setupSpec() {
		server = serverInstance
		server.start(getTestPorts())
		browser.baseUrl = server.baseUrl
	}

	TestHttpServer getServerInstance() {
		new CallbackHttpServer()
	}

	List<Integer> getTestPorts() {
		if (System.getProperty("geb.saucelabs.browser")) {
			// the sauce connect tunnel only supports a limited set of ports if using
			// localhost, as we do. Therefore hard code it in this case.
			def ports = new LinkedList(SAUCE_PORTS)
			Collections.shuffle(ports)
			ports
		} else {
			[0] // ephemeral, use whatever is available.
		}
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
		go server.baseUrl
	}

}