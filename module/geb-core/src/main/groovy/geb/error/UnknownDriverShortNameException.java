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

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public class UnknownDriverShortNameException extends GebException {

    public UnknownDriverShortNameException(String unknown, String[] knowns) {
        super(String.format("The value '%s' is not a valid driver short name (valid: %s)", unknown, DefaultGroovyMethods.join(knowns, ", ")));
    }

}