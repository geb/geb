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
import spock.lang.Unroll

class UrlCalculationSpec extends GebSpecWithServer {

	def setupSpec() {
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<div class="url">${req.requestURL + (req.queryString ? "?${req.queryString}" : "")}</div>
				<div class="path">$req.requestURI</div>
				<div class="params">$req.parameterMap</div>
			</body>
			</html>"""
		}
	}

	protected toRequestParameterMapString(map) {
		def requestMap = [:]
		map.each { k, v ->
			if (!requestMap.containsKey(k)) {
				requestMap[k] = []
			}
			(v instanceof Collection ? v : [v]).each {
				requestMap[k] << it
			}
		}
		requestMap.toString()
	}

	@Unroll("to page: page = #page, args = #args, params = #params, path = #path")
	def "t1"() {
		when:
		to(page, *: params, * args)
		then:
		requestPath == path
		requestParams == toRequestParameterMapString(params)
		where:
		page                   | params      | args       | path
		UrlCalculationSpecPage | [:]         | []         | "/"
		UrlCalculationSpecPage | [a: 1]      | []         | "/"
		UrlCalculationSpecPage | [:]         | ["a"]      | "/a"
		UrlCalculationSpecPage | [:]         | ["a", "b"] | "/a/b"
		UrlCalculationSpecPage | [a: [1, 2]] | []         | "/"
	}

	@Unroll("go: baseUrl = #baseUrl, params = #params, path = #path, expectedRequestPath = #expectedRequestPath")
	def "t2"() {
		when:
		browser.baseUrl = base
		go(path, *: params)
		page UrlCalculationSpecPage

		then:
		requestUrl == expectedRequestURL

		where:
		base           | params | path  | expectedRequestURL
		server.baseUrl | [:]    | ""    | server.baseUrl
		server.baseUrl | [a: 1] | ""    | server.baseUrl + "?a=1"
		server.baseUrl | [:]    | "a/b" | server.baseUrl + "a/b"
	}
}

class UrlCalculationSpecPage extends Page {

	static content = {
		requestUrl(dynamic: true) { $("div.url").text() }
		requestPath(dynamic: true) { $("div.path").text() }
		requestParams(dynamic: true) { $("div.params").text() }
	}
}
