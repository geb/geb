/*
 * Copyright 2017 the original author or authors.
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
package geb.report

/**
 * Writes the source content of each top level frame of the browser's current page as a html files.
 */
class FramesSourceReporter extends ReporterSupport {

    @Override
    void writeReport(ReportState reportState) {
        def browser = reportState.browser
        def frames = browser.find('frame') + browser.find('iframe')
        def reportFiles = (0..<frames.size()).collect { index ->
            reportFrameSource(reportState, index)
        }
        notifyListeners(reportState, reportFiles)
    }

    private File reportFrameSource(ReportState reportState, int index) {
        def driver = reportState.browser.driver
        try {
            driver.switchTo().frame(index)
            def file = getReportFile(reportState, index)
            file.write(driver.pageSource)
            file
        } finally {
            driver.switchTo().defaultContent()
        }
    }

    private File getReportFile(ReportState reportState, int frameIndex) {
        getFile(reportState.outputDir, "${reportState.label}-frame-${frameIndex + 1}", 'html')
    }

}
