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

class CheckboxSpec extends GebSpecWithCallbackServer {

    def "setting single"() {
        given:
        html """
            <html>
                // tag::single_html[]
                <form>
                    <input type="checkbox" name="pet" value="dog" />
                </form>
                // end::single_html[]
            </html>
        """

        when:
        // tag::single[]
        $("form").pet = true
        // end::single[]

        then:
        $("form").pet == "dog"
    }

    def "setting multiple"() {
        given:
        html """
            <html>
                // tag::multiple_html[]
                <form>
                    <input type="checkbox" name="pet" value="dog" />
                    <input type="checkbox" name="pet" value="cat" />
                </form>
                // end::multiple_html[]
            </html>
        """

        when:
        // tag::multiple[]
        $("form").pet = "dog"
        // end::multiple[]

        then:
        $("form input", value: "dog").value()
        !$("form input", value: "cat").value()
    }

    def "checked value"() {
        given:
        html """
            <html>
                // tag::checked_html[]
                <form>
                    <input type="checkbox" name="pet" value="dog" checked/>
                </form>
                // end::checked_html[]
            </html>
        """

        expect:
        // tag::checked[]
        assert $("input", name: "pet").value() == "dog"
        assert $("form").pet == "dog"
        // end::checked[]
    }

    def "unchecked value"() {
        given:
        html """
            <html>
                // tag::unchecked_html[]
                <form>
                    <input type="checkbox" name="pet" value="dog"/>
                </form>
                // end::unchecked_html[]
            </html>
        """

        expect:
        // tag::unchecked[]
        assert $("input", name: 'pet').value() == false
        assert $("form").pet == false
        // end::unchecked[]
    }

    def "truth single"() {
        given:
        html """
            <html>
                // tag::truth_html[]
                <form>
                    <input type="checkbox" name="checkedByDefault" value="checkedByDefaulValue" checked/>
                    <input type="checkbox" name="uncheckedByDefault" value="uncheckedByDefaulValue"/>
                </form>
                // end::truth_html[]
            </html>
        """

        expect:
        // tag::truth[]
        assert $("form").checkedByDefault
        assert !$("form").uncheckedByDefault
        // end::truth[]
    }

    def "truth multiple"() {
        given:
        html """
            <html>
                // tag::truth_multiple_html[]
                <form>
                    <input type="checkbox" name="pet" value="dog" checked/>
                    <input type="checkbox" name="pet" value="cat" />
                </form>
                // end::truth_multiple_html[]
            </html>
        """

        expect:
        // tag::truth_multiple[]
        assert $("input", name: "pet", value: "dog").value()
        assert !$("input", name: "pet", value: "cat").value()
        // end::truth_multiple[]
    }
}
