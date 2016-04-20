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
package modules

import fixture.Browser
import org.codehaus.groovy.runtime.typehandling.GroovyCastException

class UnwrappingModulesSnippetSpec extends FormContentSpec {

    @SuppressWarnings('UnusedVariable')
    def "assignment of a module to a variable of its declared type fails"() {
        when:
        //tag::module_variable_fail[]
        Browser.drive {
            to ModulePage
            FormModule foo = form   // GroovyCastException is thrown
        }
        //end::module_variable_fail[]
        then:
        thrown(GroovyCastException)
    }

    def "method invocation with an argument of module's declared type fails"() {
        when:
        //tag::module_argument_fail[]
        Browser.drive {
            to ModulePage
            submitForm(form)   // MissingMethodException is thrown
        }
        //end::module_argument_fail[]
        then:
        thrown(MissingMethodException)
    }

    def "unwrapped module may be used with its declared type"() {
        when:
        //tag::module_cast[]
        Browser.drive {
            to ModulePage
            FormModule foo = form as FormModule
            submitForm(foo)
        }
        //end::module_cast[]
        then:
        noExceptionThrown()
    }

    //tag::module_argument_fail[]

        void submitForm(FormModule formModule) {
            formModule.button.click()
        }
    //end::module_argument_fail[]
}