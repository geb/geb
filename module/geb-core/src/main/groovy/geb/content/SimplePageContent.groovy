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
package geb.content

import geb.navigator.Navigator

class SimplePageContent extends TemplateDerivedPageContent {

	void init(PageContentTemplate template, Navigator navigator, Object[] args) {
		super.init(template, navigator, *args)
	}

	@Override
	boolean equals(Object o) {
		if (o in SimplePageContent) {
			super.equals(o)
			false
		} else {
			def values = iterator()*.value().findAll() { it != null }
			def value
			switch (values.size()) {
				case 0:
					value = null
					break
				case 1:
					value = values.first()
					break
				default:
					value = values
			}
			value == o
		}
	}
}