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
package geb.internal

import geb.error.WaitTimeoutException

/**
 * Provides covenience methods for waiting for a condition or something to occur
 */
class WaitingSupport {

	static public final Double DEFAULT_TIMEOUT = 5
	static public final Double DEFAULT_INTERVAL = 0.5
	
	/**
	 * Invokes {@code condition} every {@link DEFAULT_INTERVAL} seconds until it returns
	 * a true value according to the Groovy Truth. If {@code condition} does not pass after (roughly)
	 * {@link DEFAULT_TIMEOUT} seconds then an {@link AssertionError} will be thrown.
	 * 
	 * @param condition the test of the condition
	 * @return the true-ish return value from {@code condition}
	 */
	def waitFor(Closure condition) {
		waitFor(null, condition)
	}

	/**
	 * Invokes {@code condition} every {@link DEFAULT_INTERVAL} seconds until it returns
	 * a true value according to the Groovy Truth. If {@code condition} does not pass after (roughly)
	 * {@code timeoutSecs} seconds then an {@link AssertionError} will be thrown.
	 * 
	 * @param timeoutSecs the number of seconds to wait for (roughly)
	 * @param condition the test of the condition
	 * @return the true-ish return value from {@code condition}
	 */
	def waitFor(Double timeoutSecs, Closure condition) {
		waitFor(timeoutSecs, null, condition)
	}

	/**
	 * Invokes {@code condition} every {@code intervalSecs} seconds until it returns
	 * a true value according to the Groovy Truth. If {@code condition} does not pass after (roughly)
	 * {@code timeoutSecs} seconds then an {@link AssertionError} will be thrown.
	 * 
	 * @param intervalSecs the number of seconds to wait between testing the condition
	 * @param timeoutSecs the number of seconds to wait for (roughly)
	 * @param condition the test of the condition
	 * @return the true-ish return value from {@code condition}
	 */
	def waitFor(Double timeoutSecs, Double intervalSecs, Closure condition) {
		timeoutSecs = timeoutSecs ?: DEFAULT_TIMEOUT
		intervalSecs = [timeoutSecs, intervalSecs ?: DEFAULT_INTERVAL].min()
		
		def loops = Math.ceil(timeoutSecs / intervalSecs)
		def pass
		def thrown
		
		try {
			pass = condition()
		} catch (Throwable e) {
			pass = false
			thrown = e
		}
		
		def i = 0
		while (!pass && i++ < loops) {
			Thread.sleep((intervalSecs * 1000) as long)
			try {
				pass = condition()
				thrown = null
			} catch (Throwable e) {
				pass = false
				thrown = e
			}
		}
		
		if (i >= loops) {
			throw new WaitTimeoutException(timeoutSecs, intervalSecs, thrown)
		}
		
		pass
	}

}