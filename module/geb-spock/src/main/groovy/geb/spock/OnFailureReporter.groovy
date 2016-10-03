/*
 * Copyright 2016 the original author or authors.
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
package geb.spock

import org.spockframework.runtime.AbstractRunListener
import org.spockframework.runtime.extension.IMethodInterceptor
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.ErrorInfo

import static org.spockframework.runtime.model.MethodKind.FEATURE

class OnFailureReporter extends AbstractRunListener implements IMethodInterceptor {

    private GebReportingSpec spec

    void intercept(IMethodInvocation invocation) throws Throwable {
        spec = invocation.instance
        invocation.proceed()
    }

    void error(ErrorInfo error) {
        if (error.method.kind == FEATURE) {
            try {
                spec.reportFailure()
            } catch (Exception e) {
                //ignore
            }
        }
    }
}
