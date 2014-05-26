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
package geb

import geb.content.PageContentContainer
import geb.content.PageContentSupport
import geb.content.PageContentTemplate
import geb.content.PageContentTemplateBuilder
import geb.content.TemplateDerivedPageContent
import geb.download.DownloadSupport
import geb.frame.FrameSupport
import geb.js.AlertAndConfirmSupport
import geb.js.JavascriptInterface
import geb.navigator.factory.NavigatorFactory
import geb.textmatching.TextMatchingSupport
import geb.waiting.WaitingSupport

class Module extends TemplateDerivedPageContent implements PageContentContainer {

	static base = null

	@Delegate
	private PageContentSupport pageContentSupport
	@Delegate
	private DownloadSupport _downloadSupport
	@Delegate
	private WaitingSupport _waitingSupport
	@Delegate
	private FrameSupport frameSupport

	@Delegate
	private TextMatchingSupport textMatchingSupport = new TextMatchingSupport()
	@Delegate
	private AlertAndConfirmSupport _alertAndConfirmSupport

	@SuppressWarnings('SpaceBeforeOpeningBrace')
	void init(PageContentTemplate template, NavigatorFactory navigatorFactory, Object[] args) {
		Map<String, PageContentTemplate> contentTemplates = PageContentTemplateBuilder.build(template.config, this, navigatorFactory, 'content', this.class, Module)
		def navigator = navigatorFactory.base
		pageContentSupport = new PageContentSupport(this, contentTemplates, navigatorFactory, navigator)
		super.init(template, navigator, args)
		_downloadSupport = new DownloadSupport(browser)
		_waitingSupport = new WaitingSupport(browser.config)
		frameSupport = new FrameSupport(browser)
		_alertAndConfirmSupport = new AlertAndConfirmSupport({ this.getJs() }, browser.config)
	}

	JavascriptInterface getJs() {
		page.js
	}

	def methodMissing(String name, args) {
		pageContentSupport.methodMissing(name, args)
	}

	def propertyMissing(String name) {
		pageContentSupport.propertyMissing(name)
	}

	def propertyMissing(String name, val) {
		pageContentSupport.propertyMissing(name, val)
	}
}