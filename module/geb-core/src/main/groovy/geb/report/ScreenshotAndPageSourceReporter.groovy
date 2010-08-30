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
package geb.report

import geb.Browser

class ScreenshotAndPageSourceReporter extends PageSourceReporter {

	ScreenshotAndPageSourceReporter(File dir) {
		super(dir)
	}

	ScreenshotAndPageSourceReporter(File dir, boolean doClean) {
		super(dir, doClean)
	}
	
	ScreenshotAndPageSourceReporter(File dir, Class clazz) {
		super(dir, clazz)
	}

	ScreenshotAndPageSourceReporter(File dir, Class clazz, boolean doClean) {
		super(dir, clazz, doClean)
	}
	
	void writeReport(String reportNameBase, Browser browser) {
		super.writeReport(reportNameBase, browser)
		
		// Only the firefox driver has this functionality, and we check the
		// name because we don't have a compile dependency on the firefox driver.
		if (browser.driver.class.name.endsWith("FirefoxDriver")) {
			browser.driver.saveScreenshot(getFile(reportNameBase, 'png')) 
		}
	}

}