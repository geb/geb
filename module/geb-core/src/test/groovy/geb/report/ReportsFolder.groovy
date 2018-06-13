/*
 * Copyright 2018 the original author or authors.
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

import geb.Browser
import org.junit.rules.TemporaryFolder
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ReportsFolder implements TestRule {

    private final TemporaryFolder temporaryFolder = new TemporaryFolder()

    private final Browser browser
    private final String groupName

    ReportsFolder(Browser browser, String groupName) {
        this.browser = browser
        this.groupName = groupName
    }

    @Override
    Statement apply(Statement base, Description description) {
        temporaryFolder.apply({
            browser.config.reportsDir = temporaryFolder.root
            base.evaluate()
        }, description)
    }

    File getGroupDir() {
        new File(temporaryFolder.root, groupName)
    }

    Set<String> getReportFileNames() {
        groupDir.listFiles()*.name
    }

}
