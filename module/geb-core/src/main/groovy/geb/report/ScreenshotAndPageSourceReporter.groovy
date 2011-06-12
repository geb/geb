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

/**
 * Writes the source of the browser's current page as html and takes a PNG screenshot
 * if the underlying driver implementation supports it.
 */
class ScreenshotAndPageSourceReporter extends PageSourceReporter {
	
	void writeReport(Browser browser, String label, File outputDir) {
		super.writeReport(browser, label, outputDir)

		// note - this is not covered by tests unless using a driver that can take screenshots
		def screenshotDriver = determineScreenshotDriver(browser)
		if (screenshotDriver) {
			def rawBase64 = screenshotDriver.getScreenshotAs(OutputType.BASE64)
			def decoded = Base64.decode(rawBase64)
			
			// WebDriver has a bug where sometimes the screenshot has been encoded twice
			if (!PngUtils.isPng(decoded)) {
				decoded = Base64.decode(decoded)
			}
			
			saveScreenshotPngBytes(outputDir, label, decoded)
		}
	}
	
	protected saveScreenshotPngBytes(File outputDir, String label, byte[] bytes) {
		getFile(outputDir, label, 'png').withOutputStream { it << bytes }
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