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

/**
 * Writes the source content of the browser's current page as a html file.
 */
class PageSourceReporter extends ReporterSupport {

    static public final NO_PAGE_SOURCE_SUBSTITUTE = "-- no page source --"
    static public final SYSTEM_LINE_BREAK = "line.separator"
    static public final FRAME_IDENTIFIER = "=====>"

    @Override
    void writeReport(ReportState reportState) {
        def file = getReportFile(reportState)
        writePageSource(file, reportState.browser, reportState.downloadFrames)
        notifyListeners(reportState, [file])
    }

    protected getReportFile(ReportState reportState) {
        getFile(reportState.outputDir, reportState.label, getPageSourceFileExtension(reportState.browser))
    }

    protected writePageSource(File file, Browser browser, boolean downloadFrames) {
        file.write(getPageSource(browser, downloadFrames))
    }

    protected getPageSource(Browser browser, boolean downloadFrames) {
        def source = browser.driver.pageSource
        if (source && downloadFrames) {
            source = saveFrames(browser, source)
        }
        source ?: NO_PAGE_SOURCE_SUBSTITUTE
    }

    /**
     * Here to allow smarter calculation of the extension if necessary
     */
    @SuppressWarnings("UnusedMethodParameter")
    protected getPageSourceFileExtension(Browser browser) {
        "html"
    }
    /**
     * Traverse the html page source and append all frame innerHTML
     *
     * Note : If jsoup support is given we can add the frames where they belong
     * instead of at the end of pagesource
     * @param browser
     * @param pageSource
     * @return
     */
    private saveFrames(Browser browser, String pageSource) {
        def sourceBuffer = new StringBuffer(pageSource)
        def frames = browser.$("frame") + browser.$("iframe")
        if (frames) {
            def htmls = frames.collect {
                iterateFrames(browser, [it]) {
                    "<html>" + $("html", 0).attr('innerHTML') + "</html>"
                }
            }

            htmls.eachWithIndex { html, index  ->
                sourceBuffer.append(System.getProperty(SYSTEM_LINE_BREAK))
                sourceBuffer.append(FRAME_IDENTIFIER)
                sourceBuffer.append(System.getProperty(SYSTEM_LINE_BREAK))
                sourceBuffer.append(frames[index]?.attr("name") ?: (frames[index]?.attr("id") ?:"frame - #${index}"))
                sourceBuffer.append(System.getProperty(SYSTEM_LINE_BREAK))
                sourceBuffer.append(FRAME_IDENTIFIER)
                sourceBuffer.append(System.getProperty(SYSTEM_LINE_BREAK))
                sourceBuffer.append(html)
                sourceBuffer.append(System.getProperty(SYSTEM_LINE_BREAK))
                sourceBuffer.append(System.getProperty(SYSTEM_LINE_BREAK))
            }
        }
        sourceBuffer.toString()
    }

    private iterateFrames (Browser browser, def frames, def closure) {
            if (frames.size() > 1) {
                browser.withFrame(frames[0]) {
                    iterateFrames(frames.drop(1), closure)
                }
            } else {
                browser.withFrame(frames[0], closure)
            }
    }

}