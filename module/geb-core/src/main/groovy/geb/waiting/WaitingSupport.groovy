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
package geb.waiting

import geb.Configuration

/**
 * A mixin style class that adds support for waiting for different things.
 * 
 * This is mixed into {@link geb.Browser}, {@link geb.Page} and {@link geb.Module}.
 */
class WaitingSupport {

	private final Configuration config
	
	WaitingSupport(Configuration config) {
		this.config = config
	}
	
	/**
	 * Uses the {@link geb.conf.Configuration#getWaitPreset(java.lang.String) wait preset} from the {@code configuration}
	 * with the given name to to wait for {@code block} to return a true value according to the Groovy Truth.
	 *
	 * @param waitPreset the name of the wait preset in {@code configuration} to use
	 * @param block what is to be waited on to return a true-ish value
	 * @return the true-ish return value from {@code block}
	 * @throws {@link geb.waiting.WaitTimeoutException} if the block does not produce a true-ish value in time
 	 * @see geb.conf.Configuration#getWaitPreset(java.lang.String)
	 * @see geb.waiting.Wait#waitFor(groovy.lang.Closure)
	 */
	def waitFor(String waitPreset, Closure block) {
		doWaitFor(config.getWaitPreset(waitPreset), block)
	}
	
	/**
	 * Uses the {@link geb.conf.Configuration#getDefaultWait() default wait} from the {@code configuration} to
	 * wait for {@code block} to return a true value according to the Groovy Truth.
	 * 
	 * @param block what is to be waited on to return a true-ish value
	 * @return the true-ish return value from {@code block}
	 * @throws {@link geb.waiting.WaitTimeoutException} if the block does not produce a true-ish value in time
	 * @see geb.conf.Configuration#getDefaultWait()
	 * @see geb.waiting.Wait#waitFor(groovy.lang.Closure)
  	 */
	def waitFor(Closure block) {
		doWaitFor(config.defaultWait, block)	
	}

	/**
	 * Invokes {@code block} every {@link geb.conf.Configuration#getDefaultWaitRetryInterval()} seconds, until it returns
	 * a true value according to the Groovy Truth, waiting at most {@code timeout} seconds.
	 * 
	 * @param timeout the number of seconds to wait for block to return (roughly)
	 * @param block what is to be waited on to return a true-ish value
	 * @return the true-ish return value from {@code block}
	 * @throws {@link geb.waiting.WaitTimeoutException} if the block does not produce a true-ish value in time
	 * @see geb.waiting.Wait#waitFor(groovy.lang.Closure)
	 */
	def waitFor(Double timeout, Closure block) {
		doWaitFor(config.getWait(timeout), block)
	}

	/**
	 * Invokes {@code block} every {@code interval} seconds, until it returns
	 * a true value according to the Groovy Truth, waiting at most {@code timeout} seconds.
	 * 
	 * @param interval the number of seconds to wait between invoking {@code block}
	 * @param timeout the number of seconds to wait for block to return (roughly)
	 * @param block what is to be waited on to return a true-ish value
	 * @return the true-ish return value from {@code block}
	 * @throws {@link geb.waiting.WaitTimeoutException} if the block does not produce a true-ish value in time
	 * @see geb.waiting.Wait#waitFor(groovy.lang.Closure)
	 */
	def waitFor(Double timeout, Double interval, Closure block) {
		doWaitFor(new Wait(timeout, interval), block)
	}
	
	private doWaitFor(Wait wait, Closure block) {
		wait.waitFor(block)
	}

}