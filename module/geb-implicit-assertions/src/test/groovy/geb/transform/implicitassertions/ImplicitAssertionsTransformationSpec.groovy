/*
 * Copyright 2012 the original author or authors.
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
package geb.transform.implicitassertions

import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.runtime.powerassert.PowerAssertionError
import spock.lang.Specification
import spock.lang.Unroll

import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION

class ImplicitAssertionsTransformationSpec extends Specification {

	private String makeCodeTemplate(String... closureBody) {
		def resource = getClass().classLoader.getResource('TransformedClass.template')
		def template = new SimpleTemplateEngine().createTemplate(resource)
		template.make([closureBody: closureBody.join('\n')]).toString()
	}

	private Class getTransformedClassWithClosureBody(String... code) {
		File tempFile = File.createTempFile('TransformedClass', '.groovy')
		tempFile << makeCodeTemplate(code)

		def invoker = new TransformTestHelper() {
			protected configure(TransformTestHelper.Transforms transforms) {
				transforms.add(new ImplicitAssertionsTransformation(), CANONICALIZATION)
			}
		}

		invoker.parse(tempFile)

		Class transformed = invoker.parse(tempFile)
		tempFile.delete()
		transformed
	}

	private Class getTransformedClass() {
		getTransformedClassWithClosureBody('')
	}

	private def getTransformedInstanceWithClosureBody(String... code) {
		getTransformedClassWithClosureBody(code).newInstance()
	}

	@Unroll("expression '#closureBody' is asserted and fails")
	def "various falsy expressions are asserted and fail"() {
		when:
		getTransformedInstanceWithClosureBody(closureBody).run()

		then:
		PowerAssertionError error = thrown()
		error.message.contains(closureBody)

		where:
		closureBody << ['false', 'null', 'booleanMethod(false)', '1 == 2', 'booleanMethod(false) == true']

	}

	def "transformation is applied to multiple lines of the closure"() {
		when:
		getTransformedInstanceWithClosureBody(
			'true',
			'false'
		).run()

		then:
		PowerAssertionError error = thrown()
		error.message.contains('false')
	}

	@Unroll("expression '#closureBody' passes")
	def "various truly expressions pass"() {
		when:
		def returnValue = getTransformedInstanceWithClosureBody(closureBody).run()

		then:
		noExceptionThrown()

		and:
		returnValue == expectedReturnValue

		where:
		closureBody                     | expectedReturnValue
		'true'                          | true
		'1'                             | 1
		'booleanMethod(true)'           | true
		'booleanMethod(false) == false' | true
	}

	@Unroll("expression '#closureBody' is ignored")
	def "various ignored expressions pass"() {
		when:
		getTransformedInstanceWithClosureBody(closureBody).run()

		then:
		noExceptionThrown()

		where:
		closureBody << ['def a = false', 'assert true', 'voidMethod()']
	}

	@Unroll("compilation error is reported when not allowed statements are found")
	def "compilation error is reported on assignment statements in waitFor closure body"() {
		when:
		getTransformedInstanceWithClosureBody(closureBody)

		then:
		MultipleCompilationErrorsException exception = thrown()
		exception.message.contains(message)

		where:
		closureBody        | message
		'a = 2'            | "Expected a condition, but found an assignment. Did you intend to write '==' ?"
		'spreadCall(*foo)' | 'Spread expressions are not allowed here'
	}

	def "waitFor closure returns true if all assertions pass"() {
		expect:
		getTransformedInstanceWithClosureBody('true').run() == true
	}

	def "transform is also applied to at closures"() {
		when:
		transformedClass.at()

		then:
		PowerAssertionError error = thrown()
		error.message.contains('false')
	}
}
