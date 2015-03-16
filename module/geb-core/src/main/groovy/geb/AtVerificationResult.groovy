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
package geb

import geb.error.RequiredPageContentNotPresent
import geb.waiting.ImplicitWaitTimeoutException
import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * Created by vijayb on 05-03-2015.
 *
 * Class to hold page data and its at verification result
 */
class AtVerificationResult {
    private Page page
    private Throwable errorThrown
    private boolean atResult

    AtVerificationResult(Page page, boolean atResult, Throwable errorThrown) {
        this.page = page
        this.atResult = atResult
        this.errorThrown = errorThrown
    }

    /**
     * Returns true if errorThrown is null and at verification result is true
     *
     * @return boolean value representing  at check verification result
     */
    boolean toBoolean() {
        errorThrown == null && atResult
    }

    /**
     * Returns the error thrown by the page verification.
     *
     * @return Error thrown during page at check
     */
    Throwable getErrorThrown() {
        errorThrown
    }

    /**
     * Returns error message to be displayed while throwing the exception by appending name of page class
     * and cause for its at check failure
     *
     * @return Error message to be displayed while throwing the exception
     */
    String toString() {
        String.format("\n %s : %s", page.getClass().getName(), getErrorMessage())
    }

    private String getErrorMessage() {
        String message = ""
        String stackTraceTag = ": StackTrace : "
        if (errorThrown instanceof AssertionError || errorThrown instanceof RequiredPageContentNotPresent) {
            message += "Exception message: " + errorThrown.getMessage()
            if (errorThrown.getStackTrace().length > 0) {
                message += stackTraceTag + errorThrown.getStackTrace()[0].toString()
            }

        } else if (errorThrown instanceof ImplicitWaitTimeoutException) {
            ImplicitWaitTimeoutException timeoutException = (ImplicitWaitTimeoutException) errorThrown
            message += "Timeout exception message: " + timeoutException.getMessage()
            message += ": Cause message : " + timeoutException.getCause().getMessage()
            if (timeoutException.getCause().getStackTrace().length > 0) {
                message += stackTraceTag + timeoutException.getCause().getStackTrace()[0].toString()
            }
        } else {
            message = DefaultGroovyMethods.toString(errorThrown)
        }
        message
    }
}
