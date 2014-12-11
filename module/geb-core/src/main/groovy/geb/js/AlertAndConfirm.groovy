/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package geb.js


public interface AlertAndConfirm {

	def withAlert(Closure actions)

	def withAlert(Map params, Closure actions)

	void withNoAlert(Closure actions)

	def withConfirm(Map params, Closure actions)

	def withConfirm(Closure actions)

	def withConfirm(boolean ok, Closure actions)

	def withConfirm(Map params, boolean ok, Closure actions)

	void withNoConfirm(Closure actions)

}
