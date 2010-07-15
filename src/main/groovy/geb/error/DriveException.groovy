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
package geb.error

import geb.Driver

class DriveException extends GebException {

	final Driver driver
	
	DriveException(Driver driver, Throwable cause) {
		super(calculateMessage(driver), cause)
		this.driver = driver
	}
	
	static calculateMessage(Driver driver) {
		def response = driver.response
		if (response) {
			def contentType = response.contentType
			if (contentType ==~ /text\/.+/) {
				def sb = new StringBuilder("Last Response: ")
				sb << response.contentAsStream
				sb.toString()
			} else {
				"Last response was non text (contentType: $contentType)"
			}
		} else {
			"No Response"
		}
	}

}