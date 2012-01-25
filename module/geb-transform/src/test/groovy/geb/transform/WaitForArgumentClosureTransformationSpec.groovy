package geb.transform

import spock.lang.Specification
import groovy.text.SimpleTemplateEngine
import org.codehaus.groovy.tools.ast.TranformTestHelper
import org.codehaus.groovy.control.CompilePhase
import static org.codehaus.groovy.control.CompilePhase.*
import spock.lang.Unroll
import org.codehaus.groovy.control.MultipleCompilationErrorsException

class WaitForArgumentClosureTransformationSpec extends Specification {

	private String makeCodeTemplate(String... closureBody) {
		def resource = getClass().classLoader.getResource('TransformedClass.template')
		def template = new SimpleTemplateEngine().createTemplate(resource)
		template.make([closureBody: closureBody.join('\n')]).toString()
	}

	private def transformedInstanceWithClosureBody(String... code) {
		File tempFile = File.createTempFile('TransformedClass', '.groovy')
		tempFile << makeCodeTemplate(code)
		def invoker = new TranformTestHelper(new WaitForArgumentClosureTransformation(), SEMANTIC_ANALYSIS)
		def instance = invoker.parse(tempFile).newInstance()
		tempFile.delete()
		instance
	}

	private boolean containsPowerAssertLines(String message) {
		message.tokenize('\n').grep(~/( *\|)+/)
	}

	@Unroll("expression '#closureBody' is asserted and fails")
	def "various falsy expressions are asserted and fail"() {
		when:
		transformedInstanceWithClosureBody(closureBody).run()

		then:
		AssertionError error = thrown()
		error.message.contains(closureBody)
		containsPowerAssertLines(error.message) == shouldContainPowerAssertLines

		where:
		closureBody                    | shouldContainPowerAssertLines
		'false'                        | false
		'null'                         | false
		'booleanMethod(false)'         | true
		'1 == 2'                       | true
		'booleanMethod(false) == true' | true
	}

	def "transformation is applied to multiple lines of the closure"() {
		when:
		transformedInstanceWithClosureBody(
				'true',
				'false'
		).run()

		then:
		AssertionError error = thrown()
		error.message.contains('false')
	}
	@Unroll("expression '#closureBody' passes")
	def "various truly expressions pass"() {
		when:
		transformedInstanceWithClosureBody(closureBody).run()

		then:
		noExceptionThrown()

		where:
		closureBody << ['true', '1', 'booleanMethod(true)', '1 == 1', 'booleanMethod(false) == false']
	}

	@Unroll("expression '#closureBody' is ignored")
	def "various ignored expressions pass"() {
		when:
		transformedInstanceWithClosureBody(closureBody).run()

		then:
		noExceptionThrown()

		where:
		closureBody << ['def a = false', 'assert true']
	}

	def "compilation error is reported on assignment statements in waitFor closure body"() {
		when:
		transformedInstanceWithClosureBody('a = 2')

		then:
		MultipleCompilationErrorsException exception = thrown()
		exception.message.contains("Expected a condition, but found an assignment. Did you intend to write '==' ?")
	}
}
