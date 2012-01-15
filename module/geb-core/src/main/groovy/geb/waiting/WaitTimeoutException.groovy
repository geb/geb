/* 
 * Copyright 2011 the original author or authors.
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
package geb.waiting

import geb.error.GebException

/**
 * Thrown when a wait operation exceeds its timeout.
 * <p>
 * The {@code cause} of the exception will be the exception thrown while waiting.
 * 
 * @see geb.waiting.Wait#waitFor(groovy.lang.Closure)
 */
class WaitTimeoutException extends GebException {

	WaitTimeoutException(Wait wait, Throwable cause = null) {
		super(toMessage(wait, cause), cause)
	}
	
	private static toMessage(Wait wait, Throwable cause) {
		def message = "condition did not pass in $wait.timeout seconds"
		if (wait.customMessage) {
			message +=" (${wait.customMessage})"
		}
		if (cause) {
			message += " (failed with exception)"
		}
		message
	}

}
