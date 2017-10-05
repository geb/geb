/*
 * Copyright 2015 the original author or authors.
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
package reporting

import fixture.Browser
import fixture.DriveMethodSupportingSpecWithServer

class ReportingSpec extends DriveMethodSupportingSpecWithServer {

    def "reporting"() {
        expect:
        // tag::reporting[]
        Browser.drive {
            go "http://google.com"
            report "google home page"
            // end::reporting[]
            assert new File(config.reportsDir, "google home page.html").exists()
            // tag::reporting[]
        }
        // end::reporting[]
    }

    def "reporting groups"() {
        expect:
        // tag::reporting_groups[]
        Browser.drive {
            reportGroup "google"
            go "http://google.com"
            report "home page"

            // end::reporting_groups[]
            assert new File(config.reportsDir, "google/home page.html").exists()

            // tag::reporting_groups[]
            reportGroup "geb"
            go "http://gebish.org"
            report "home page"
            // end::reporting_groups[]
            assert new File(config.reportsDir, "geb/home page.html").exists()
            // tag::reporting_groups[]
        }
        // end::reporting_groups[]
    }

    def "cleaning"() {
        expect:
        // tag::cleaning[]
        Browser.drive {
            cleanReportGroupDir()
            go "http://google.com"
            report "home page"
        }
        // end::cleaning[]
    }
}
