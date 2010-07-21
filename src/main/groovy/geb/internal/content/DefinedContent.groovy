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
package geb.internal.content

/**
 * Represents some kind of definition of content.
 * 
 * e.g. a whole page, or part of a page etc.
 */
interface DefinedContent {

	/**
	 * The actual content that was created from the definition.
	 */
	Content getContent()
	
	/**
	 * Returns the index'th content matching the selector in relation to this content
	 */
	Content get(CharSequence selector, Integer index) {
		getContent().get(selector, index)
	}

	/**
	 * Returns all content mathing the selector in relation to this content
	 */
	Content get(CharSequence selector) {
		getContent().get(selector)
	}
	
}