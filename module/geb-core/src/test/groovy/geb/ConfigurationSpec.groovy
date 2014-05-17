package geb

import geb.report.CompositeReporter
import geb.report.Reporter
import geb.report.ReportingListener
import spock.lang.Specification

class ConfigurationSpec extends Specification {

	def "creates default reporter"() {
		when:
		def conf = conf()

		then:
		conf.reporter
		conf.reporter instanceof CompositeReporter
	}

	def "uses no listener by default"() {
		expect:
		conf().reportingListener == null
	}

	def "specified reporting listener is added to reporter"() {
		given:
		def listener = Mock(ReportingListener)
		def reporter = Mock(Reporter)
		def conf = conf(reportingListener: listener, reporter: reporter)

		when:
		conf.reporter

		then:
		1 * reporter.addListener(listener)
	}

	protected Configuration conf(Map<String, ?> props = [:]) {
		new Configuration(props)
	}

}
