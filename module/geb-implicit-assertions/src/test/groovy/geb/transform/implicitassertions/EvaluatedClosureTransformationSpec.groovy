package geb.transform.implicitassertions

import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.control.MultipleCompilationErrorsException
import org.codehaus.groovy.transform.powerassert.PowerAssertionError
import spock.lang.Specification
import spock.lang.Unroll
import static org.codehaus.groovy.control.CompilePhase.CANONICALIZATION
import org.codehaus.groovy.tools.ast.TranformTestHelper

class EvaluatedClosureTransformationSpec extends Specification {

	private String makeCodeTemplate(String... closureBody) {
		def resource = getClass().classLoader.getResource('TransformedClass.template')
		def template = new SimpleTemplateEngine().createTemplate(resource)
		template.make([closureBody: closureBody.join('\n')]).toString()
	}

	private Class getTransformedClassWithClosureBody(String... code) {
		File tempFile = File.createTempFile('TransformedClass', '.groovy')
		tempFile << makeCodeTemplate(code)
		def invoker = new TranformTestHelper(new ImplicitAssertionsTransformation(), CANONICALIZATION)
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
