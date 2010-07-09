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

package org.codehaus.geb

import be.roam.hue.doj.Doj

class DojIntegrationSpec extends BaseGebSpec {

	def "basic doj integration"() {
		given:
		server.get = { req, res ->
			res.outputStream << """
			<html>
			<body>
				<div class="d1">
					<div class="d1d1">d1d1</div>
				</div>
				<div class="d2">
					<div class="d2d1">d2d1</div>
				</div>
			</body>
			</html>"""
		}

		when:
		get("/")

		then:
		find("div") instanceof Doj
		find("") instanceof Doj
		find("asdfasdfasdfa") instanceof Doj
		find(null instanceof Doj)
		
		and:
		find("div.d1d1").text() == "d1d1"
		find("div.d1").get(".d1d1").text() == "d1d1"
		find("div.d1").get(".d1d1").parent().next().get('.d2d1').text() == "d2d1"
	}
	
}