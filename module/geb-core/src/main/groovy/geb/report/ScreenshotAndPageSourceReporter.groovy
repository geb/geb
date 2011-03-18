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

import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.OutputType

import org.openqa.selenium.WebDriver

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

		// note - this is not covered by tests unless using a driver that can take screenshots
		def screenshotDriver = determineScreenshotDriver(browser)
		if (screenshotDriver) {
			saveScreenshotPngBytes(reportNameBase, screenshotDriver.getScreenshotAs(OutputType.BYTES))
		}
	}
	
	protected saveScreenshotPngBytes(String reportNameBase, byte[] bytes) {
		getFile(reportNameBase, 'png').withOutputStream { it << bytes }
	}

	protected determineScreenshotDriver(Browser browser) {
		if (browser.driver instanceof TakesScreenshot) {
			browser.driver
		} else if (browser.augmentedDriver instanceof TakesScreenshot) {
			browser.augmentedDriver
		} else {
			null
		}
	}
}