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
package navigator

import geb.test.GebSpecWithCallbackServer
import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.http.entity.ContentType
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static geb.Browser.UTF8

class FileUploadSpec extends GebSpecWithCallbackServer {

    @Rule
    TemporaryFolder dir = new TemporaryFolder()

    private void setupServer() {
        responseHtml """
            <html>
                <form method="post" enctype="multipart/form-data">
                    // tag::html[]
                    <input type="file" name="csvFile"/>
                    // end::html[]
                    <input type="submit"/>
                </form>
            </html>
        """
        callbackServer.post = { HttpServletRequest request, HttpServletResponse response ->
            response.setContentType(ContentType.TEXT_HTML.toString())
            response.setCharacterEncoding(UTF8)
            response.writer << """
                <html>${extractUploadedFileText(request)}</html>
            """
        }
    }

    private String extractUploadedFileText(HttpServletRequest request) {
        def fileItemFactory = new DiskFileItemFactory()
        def files = new ServletFileUpload(fileItemFactory).parseRequest(request)
        files.first().inputStream.text
    }

    def "uploadig a file"() {
        given:
        def uploadedFile = dir.newFile() << "from file"
        setupServer()

        when:
        go()
        // tag::upload[]
        $("form").csvFile = uploadedFile.absolutePath
        // end::upload[]
        $('form input[type="submit"]').click()

        then:
        $().text() == "from file"
    }

}
