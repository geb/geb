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
package geb.waiting

import geb.Module
import geb.Page
import geb.fixture.CrossPlatformSupport
import geb.navigator.EmptyNavigator
import org.codehaus.groovy.runtime.powerassert.PowerAssertionError
import spock.lang.Unroll

@SuppressWarnings("TrailingWhitespace")
@Unroll
class WaitingSupportSpec extends WaitingSpec implements CrossPlatformSupport {

    def setup() {
        go()
        page WaitingSupportSpecPage
    }

    def subjects() {
        [
            [{ browser }, "browser"],
            [{ page }, "page"],
            [{ mod }, "module"]
        ]
    }

    def "basic waiting - when called on #subjectName"() {
        when:
        js.showIn(2)

        then:
        $("div").empty
        subjectFactory().waitFor(3) { !$("div").empty }

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "basic waiting throwing exception - when called on #subjectName"() {
        when:
        js.showIn(2)

        then:
        $("div").empty
        subjectFactory().waitFor(3) { assert !$("div").empty; true }

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "failed waiting - when called on #subjectName"() {
        when:
        js.showIn(3)
        subjectFactory().waitFor(1) { !$("div").empty }

        then:
        WaitTimeoutException exception = thrown()
        exception.cause in PowerAssertionError
        exception.cause.message.contains('!$("div").empty')

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "failed waiting throwing exception - when called on #subjectName"() {
        when:
        subjectFactory().waitFor(2) { throw new IllegalArgumentException("1") }

        then:
        WaitTimeoutException e = thrown()
        e.cause instanceof IllegalArgumentException

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "larger interval than timeout - when called on #subjectName"() {
        when:
        js.showIn(4)

        then:
        subjectFactory().waitFor(1, 10) { $("div").empty }

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "message argument is appended to the exception message - when called on #subjectName"() {
        when:
        subjectFactory().waitFor(1, message: 'Some custom message') { false }

        then:
        WaitTimeoutException e = thrown()
        e.message =~ 'Some custom message'

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "larger interval than timeout throwing exception - when called on #subjectName"() {
        when:
        js.showIn(4)

        then:
        subjectFactory().waitFor(1, 10) { assert $("div").empty; true }

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "waitFor block takes longer than the timeout but succeeds - when called on #subjectName"() {
        expect:
        subjectFactory().waitFor(0) { true }

        where:
        [subjectFactory, subjectName] << subjects()
    }

    @Unroll
    @SuppressWarnings(['SpaceAfterClosingBrace', 'SpaceBeforeOpeningBrace'])
    def "lastEvaluationValue is set on WaitTimeoutException when waiting for #waitForTime secs and expected result is #lastEvaluationValueClass.simpleName"() {
        when:
        waitFor(waitForTime, evaluatedClosure)

        then:
        WaitTimeoutException e = thrown()
        !e.lastEvaluationValue
        e.lastEvaluationValue in lastEvaluationValueClass

        where:
        evaluatedClosure << [{ false }, { $('#not-existing-element') }, { throw new Exception() }] * 2
        lastEvaluationValueClass << [Boolean, EmptyNavigator, UnknownWaitForEvaluationResult] * 2
        waitForTime << [0, 0.5].sum { [it] * 3 }
    }

    @Unroll
    def "UnknownWaitForEvaluationValue holds the thrown exception when waitFor times out due to it when waiting for #waitForTime secs - when called on #subjectName"() {
        given:
        def exception = new Exception()

        when:
        subjectFactory().waitFor(waitForTime) { sleep 100; throw exception }

        then:
        WaitTimeoutException e = thrown()
        e.lastEvaluationValue.thrown == exception

        where:
        waitForTime << [0, 0.5].sum { [it] * 3 }
        [subjectFactory, subjectName] << subjects() * 2
    }

    def "cause is appended to the exception message if configured - when called on #subjectName"() {
        given:
        config.includeCauseInWaitTimeoutExceptionMessage = true

        when:
        subjectFactory().waitFor(0.2) { 'not empty'.empty }

        then:
        WaitTimeoutException exception = thrown()
        normalizeEndOfLines(exception.message) == """condition did not pass in 0.2 seconds. Failed with exception:
Assertion failed: 

'not empty'.empty
            |
            false
"""

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "default variant - when called on #subjectName"() {
        when:
        js.showIn(2)

        then:
        subjectFactory().waitFor { $("div").empty }

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "using timeout and interval - when called on #subjectName"() {
        when:
        js.showIn(1)

        then:
        subjectFactory().waitFor(2, 0.1) { $("div") }

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "using preset - when called on #subjectName"() {
        given:
        browser.config.setWaitPreset("custom", 2, 0.1)

        when:
        js.showIn(1)

        then:
        subjectFactory().waitFor("custom") { $("div") }

        where:
        [subjectFactory, subjectName] << subjects()
    }

    def "available on page"() {
        when:
        js.showIn(3)
        then:
        $("div").empty
        waitForDiv()
    }

    def "available on module"() {
        when:
        js.showIn(3)
        then:
        $("div").empty
        mod.waitForDiv()
    }

}

class WaitingSupportSpecPage extends Page {
    static content = {
        mod { module WaitingSupportSpecModule }
    }

    def waitForDiv() {
        waitFor { !$("div").empty }
    }
}

class WaitingSupportSpecModule extends Module {
    def waitForDiv() {
        waitFor { !find("div").empty }
    }
}