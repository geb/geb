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
package geb.error;

import geb.AtVerificationResult;
import geb.Page;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.Map;

public class UnexpectedPageException extends GebException {

    public UnexpectedPageException(Page page) {
        this(page, null);
    }

    public UnexpectedPageException(Class<? extends Page> page) {
        super(String.format("At checker page verification failed for page %s", page.getName()));
    }

    public UnexpectedPageException(Page page, Throwable cause) {
        super(String.format("At checker page verification failed for page %s", page), cause);
    }

    public UnexpectedPageException(Class<? extends Page> actualPage, Class<? extends Page> expectedPage) {
        super(String.format("An unexpected page %s was encountered when expected to be at %s", actualPage.getName(), expectedPage.getName()));
    }

    public UnexpectedPageException(Class<? extends Page> actualPage, Page expectedPage) {
        super(String.format("An unexpected page %s was encountered when expected to be at %s", actualPage.getName(), expectedPage.getClass().getName()));
    }

    public UnexpectedPageException(Class<? extends Page> actualPage, Class<? extends Page>[] potentials) {
        super(String.format("An unexpected page %s was encountered when trying to find page match (given potentials: %s)", actualPage.getName(), DefaultGroovyMethods.toString(potentials)));
    }

    public UnexpectedPageException(Class<? extends Page> actualPage, Page[] potentials) {
        super(String.format("An unexpected page %s was encountered when trying to find page match (given potentials: %s)", actualPage.getName(), DefaultGroovyMethods.toString(potentials)));
    }

    public UnexpectedPageException(Map<? extends Page, AtVerificationResult> pageVerificationResults) {
        super(String.format("Unable to find page match. At checker verification results:%n%n%s", UnexpectedPageException.format(pageVerificationResults)));
    }

    private static String format(Map<? extends Page, AtVerificationResult> pageVerificationResults) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<? extends Page, AtVerificationResult> atVerificationResultEntry : pageVerificationResults.entrySet()) {
            builder.append(String.format("Result for %s: %s%n%n", atVerificationResultEntry.getKey().getClass().getName(), atVerificationResultEntry.getValue()));
        }
        return builder.toString();
    }
}
