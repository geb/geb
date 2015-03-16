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
import geb.waiting.ImplicitWaitTimeoutException;
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
        super(String.format("Unable to find page match (given potential page and there exception are : %s )", UnexpectedPageException.getErrorMessage(pageVerificationResults)));
    }

    private static String getErrorMessage(Map<? extends Page, AtVerificationResult> pageVerificationResults) {
        String errorMessage = "";
        for (Map.Entry<? extends Page, AtVerificationResult> pageVerificationResult : pageVerificationResults.entrySet()) {
            Page page = pageVerificationResult.getKey();
            Throwable errorThrown = pageVerificationResult.getValue().getErrorThrown();
            String message = "";
            String stackTraceTag = ": StackTrace : ";
            if (errorThrown instanceof AssertionError || errorThrown instanceof RequiredPageContentNotPresent) {
                message += "Exception message: " + errorThrown.getMessage();
                if (errorThrown.getStackTrace().length > 0) {
                    message += stackTraceTag + errorThrown.getStackTrace()[0].toString();
                }
            } else if (errorThrown instanceof ImplicitWaitTimeoutException) {
                ImplicitWaitTimeoutException timeoutException = (ImplicitWaitTimeoutException) errorThrown;
                message += "Timeout exception message: " + timeoutException.getMessage();
                message += ": Cause message : " + timeoutException.getCause().getMessage();
                if (timeoutException.getCause().getStackTrace().length > 0) {
                    message += stackTraceTag + timeoutException.getCause().getStackTrace()[0].toString();
                }
            } else {
                message = DefaultGroovyMethods.toString(errorThrown);
            }
            errorMessage += String.format("\n %s : %s", page.getClass().getName(), message);
        }
        return errorMessage;
    }
}
