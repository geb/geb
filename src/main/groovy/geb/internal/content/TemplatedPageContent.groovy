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
 * Content which was derived from a PageContentTemplate
 */
interface TemplatedPageContent extends PageContent {
		
	/**
	 * The arguments passed to the template to create this
	 * particular instance of the content
	 */
	Object[] getArgs()
	
	/**
	 * The template that created this content
	 */
	PageContentTemplate getTemplate()
}
