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
package geb.page

import geb.Page
import geb.page.error.RequiredPageContentNotPresent
import be.roam.hue.doj.Doj

abstract class PageContent {

	def template
	def args
	def container
	
	String toString() {
		"$template.name - ${this.class.simpleName.toLowerCase()} (container: $container, args: $args)"
	}
	
	def getBase() {
		container
	}
	
	/**
	 * Delegates all method calls to the doj instance.
	 */
	def methodMissing(String name, args) {
		base."$name"(*args)
	}

	/**
	 * Delegates the property access to the doj instance.
	 */
	def propertyMissing(String name) {
		base."$name"
	}
	
	/**
	 * Delegates the property setting to the doj instance.
	 */
	def propertyMissing(String name, value) {
		base."$name" = value
	}
	
	def getToPage() {
		template.toPage
	}
	
	def getPage() {
		template.page
	}
	
	def getName() {
		template.name
	}
	
	def find(selector) {
		// Something is up here, we need a unified API
		if (base instanceof Doj) {
			base.get(selector.toString())
		} else if (base instanceof PageContent || base instanceof Page) {
			base.find(selector.toString())
		} else {
			throw new IllegalStateException("Can't handle base $base")
		}
	}
	
	boolean isPresent() {
		base instanceof Page ? true : !base.empty
	}
	
	void require() {
		if (!present) {
			throw new RequiredPageContentNotPresent(this)
		}
		this
	}
	

}