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
package geb.waiting;

import geb.error.GebException;

/**
 * Thrown when a wait operation exceeds its timeout.
 *
 * The {@code cause} of the exception will be the exception thrown while waiting.
 */
public class WaitTimeoutException extends GebException {

	private Object lastEvaluationValue;

	public WaitTimeoutException(Wait wait) {
		this(wait, null, null);
	}

	public WaitTimeoutException(Wait wait, Throwable cause) {
		this(wait, cause, null);
	}

	public WaitTimeoutException(Wait wait, Throwable cause, Object lastEvaluationValue) {
		super(toMessage(wait, cause), cause);
		this.lastEvaluationValue = lastEvaluationValue;
	}

	public Object getLastEvaluationValue() {
		return lastEvaluationValue;
	}

	private static String toMessage(Wait wait, Throwable cause) {
		StringBuilder message = new StringBuilder(String.format("condition did not pass in %s seconds", wait.getTimeout()));
		if (wait.getCustomMessage() != null) {
			message.append(String.format("(%s)", wait.getCustomMessage()));
		}
		if (cause != null) {
			message.append(" (failed with exception)");
		}
		return message.toString();
	}

}
