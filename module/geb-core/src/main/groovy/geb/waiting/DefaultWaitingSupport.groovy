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
 * This is mixed into {@link geb.Page} and {@link geb.Module}.
 */
@SuppressWarnings("GrMethodMayBeStatic")
class DefaultWaitingSupport implements WaitingSupport {

	private final Configuration config

	DefaultWaitingSupport(Configuration config) {
		this.config = config
	}

	public <T> T waitFor(String waitPreset, Closure<T> block) {
		waitFor([:], waitPreset, block)
	}

	public <T> T waitFor(Map params, String waitPreset, Closure<T> block) {
		doWaitFor(params.message, config.getWaitPreset(waitPreset), block)
	}

	public <T> T waitFor(Closure<T> block) {
		waitFor([:], block)
	}

	public <T> T waitFor(Map params, Closure<T> block) {
		doWaitFor(params.message, config.defaultWait, block)
	}

	public <T> T waitFor(Double timeout, Closure<T> block) {
		waitFor([:], timeout, block)
	}

	public <T> T waitFor(Map params, Double timeout, Closure<T> block) {
		doWaitFor(params.message, config.getWait(timeout), block)
	}

	public <T> T waitFor(Double timeout, Double interval, Closure<T> block) {
		waitFor([:], timeout, interval, block)
	}

	public <T> T waitFor(Map params, Double timeout, Double interval, Closure<T> block) {
		doWaitFor(params.message, new Wait(timeout, interval), block)
	}

	private <T> T doWaitFor(String customMessage, Wait wait, Closure<T> block) {
		wait.customMessage = customMessage
		wait.waitFor(block)
	}

}