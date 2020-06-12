/*
 * Copyright 2020 the original author or authors.
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
package geb.test

import geb.Browser

import java.util.function.Predicate
import java.util.function.Supplier

import static geb.report.ReporterSupport.toTestReportLabel

class GebTestManager {

    private final Supplier<Browser> browserCreator
    private final Predicate<Class<?>> resetBrowserAfterEachTestPredicate
    final boolean reportingEnabled

    protected Browser browser
    private Class<?> currentTestClass
    private int perTestReportCounter = 1
    private int testCounter = 1
    private String currentTestName

    GebTestManager(
            Supplier<Browser> browserCreator, Predicate<Class<?>> resetBrowserAfterEachTestPredicate,
            boolean reportingEnabled
    ) {
        this.browserCreator = browserCreator
        this.resetBrowserAfterEachTestPredicate = resetBrowserAfterEachTestPredicate
        this.reportingEnabled = reportingEnabled
    }

    Browser getBrowser() {
        if (browser == null) {
            browser = createBrowser()
        }
        browser
    }

    void report(String label = "") {
        if (!reportingEnabled) {
            throw new IllegalStateException("Reporting has not been enabled on this GebTestManager yet report() was called")
        }
        browser.report(createReportLabel(label))
        perTestReportCounter++
    }

    void reportFailure() {
        if (browser) {
            report("failure")
        }
    }

    void beforeTestClass(Class<?> testClass) {
        currentTestClass = testClass
        if (reportingEnabled) {
            getBrowser().reportGroup(testClass)
            getBrowser().cleanReportGroupDir()
        }
    }

    void beforeTest(String testName) {
        currentTestName = testName
        if (reportingEnabled) {
            getBrowser().reportGroup(currentTestClass)
        }
    }

    void afterTest() {
        if (reportingEnabled) {
            if (browser && !browser.config.reportOnTestFailureOnly) {
                report("end")
            }

            testCounter++
            perTestReportCounter = 1
        }

        if (resetBrowserAfterEachTest) {
            resetBrowser()
        }
        currentTestName = null
    }

    void afterTestClass() {
        if (!resetBrowserAfterEachTest) {
            resetBrowser()
        }
        if (reportingEnabled) {
            testCounter = 1
        }
        currentTestClass = null
    }

    String createReportLabel(String label) {
        def methodName = currentTestName ?: 'fixture'
        toTestReportLabel(testCounter, perTestReportCounter, methodName, label)
    }

    void resetBrowser() {
        def config = browser?.config
        if (config?.autoClearCookies) {
            browser.clearCookiesQuietly()
        }
        if (config?.autoClearWebStorage) {
            browser.clearWebStorage()
        }
        browser = null
    }

    private Browser createBrowser() {
        browserCreator ? browserCreator.get() : new Browser()
    }

    private boolean getResetBrowserAfterEachTest() {
        resetBrowserAfterEachTestPredicate.test(currentTestClass)
    }

}
