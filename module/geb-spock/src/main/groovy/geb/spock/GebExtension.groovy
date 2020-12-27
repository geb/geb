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

import geb.test.GebTestManager
import geb.test.ManagedGebTest
import org.spockframework.runtime.extension.IGlobalExtension
import org.spockframework.runtime.extension.IMethodInvocation
import org.spockframework.runtime.model.SpecInfo

class GebExtension implements IGlobalExtension {

    @Override
    void start() {
    }

    @Override
    void stop() {
    }

    void visitSpec(SpecInfo spec) {
        if (ManagedGebTest.isAssignableFrom(spec.reflection)) {
            addOnFailureReporter(spec)
            addManagerCalls(spec)
        }
    }

    protected GebTestManager getManager(IMethodInvocation invocation) {
        def spec = invocation.instance as ManagedGebTest
        spec.testManager
    }

    private void addOnFailureReporter(SpecInfo spec) {
        def reporter = new OnFailureReporter()
        spec.addListener(reporter)
        spec.addSetupSpecInterceptor(reporter)
        spec.addSetupInterceptor(reporter)
    }

    private void addManagerCalls(SpecInfo spec) {
        spec.addSetupSpecInterceptor { invocation ->
            getManager(invocation).beforeTestClass(invocation.spec.reflection)
            invocation.proceed()
        }

        spec.addSetupInterceptor { invocation ->
            getManager(invocation).beforeTest(invocation.iteration.name)
            invocation.proceed()
        }

        spec.addCleanupInterceptor { invocation ->
            try {
                invocation.proceed()
            } finally {
                getManager(invocation).afterTest()
            }
        }

        spec.addCleanupSpecInterceptor { invocation ->
            try {
                invocation.proceed()
            } finally {
                getManager(invocation).afterTestClass()
            }
        }
    }
}
