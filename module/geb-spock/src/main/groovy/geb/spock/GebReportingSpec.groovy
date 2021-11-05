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
package geb.spock

import geb.test.GebTestManager

class GebReportingSpec extends GebSpec {

    private final static ThreadLocal<GebTestManager> TEST_MANAGER = new ThreadLocal<GebTestManager>() {
        @Override
        protected GebTestManager initialValue() {
            new SpockGebTestManagerBuilder()
                .withReportingEnabled(true)
                .build()
        }
    }

    @Override
    @Delegate(includes = ["getBrowser", "report"])
    GebTestManager getTestManager() {
        TEST_MANAGER.get()
    }

}
