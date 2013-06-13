package geb.conf

import com.google.common.io.Files
import geb.Browser
import geb.Configuration
import spock.lang.Specification

import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger

class ReportDirSpecification extends Specification {
	def 'getReportGroupDir creates the report dir if it does not exist'() {
		given:
		def reportGroupDir = new File(Files.createTempDir(), "reportGroupDir")

		expect:
		!reportGroupDir.exists()

		when:
		def configuration = new Configuration()
		configuration.setReportsDir(reportGroupDir)
		new Browser(configuration).getReportGroupDir()

		then:
		reportGroupDir.exists()
	}

	def 'getReportGroupDir can be used with parallel execution'() {
		given:
		def parentDir = Files.createTempDir()
		def reportGroupDir = new File(parentDir, "reportgroupdir")

		expect:
		parentDir.exists()
		!reportGroupDir.exists()

		when:
		def exceptions = executeInParallel(reportGroupDir, { getReportGroupDir() })

		then:
		exceptions == 0

		and:
		reportGroupDir.exists()
	}

	def 'cleanReportGroupDir deletes the directory'() {
		given:
		def previouslyExistingReportGroupDir = Files.createTempDir()
		def configuration = new Configuration()
		configuration.setReportsDir(previouslyExistingReportGroupDir)

		expect:
		previouslyExistingReportGroupDir.exists()

		when:
		new Browser(configuration).cleanReportGroupDir()

		then:
		!previouslyExistingReportGroupDir.exists()
	}

	def 'cleanReportGroupDir can be used with parallel execution'() {
		given:
		def reportGroupDir = Files.createTempDir()

		when:
		def exceptions = executeInParallel(reportGroupDir, { cleanReportGroupDir() })

		then:
		exceptions == 0

		and:
		!reportGroupDir.exists()
	}

	private static int executeInParallel(File reportGroupDir, Closure executeOnBrowser) {
		Configuration configuration = getConfiguration(reportGroupDir)

		AtomicInteger exceptionCounter = new AtomicInteger()
		def pool = Executors.newFixedThreadPool(20)

		100000.times {
			pool.submit(doWithNewBrowserAndReportException(exceptionCounter, configuration, executeOnBrowser))
		}

		pool.shutdown()
		return exceptionCounter.get()
	}

	private static Configuration getConfiguration(File reportGroupDir) {
		def configuration = new Configuration()
		configuration.setReportsDir(reportGroupDir)
		return configuration
	}

	private static Closure doWithNewBrowserAndReportException(AtomicInteger exceptionCounter, Configuration configuration, Closure cl) {
		return {
			try {
				def browser = new Browser(configuration)
				cl.setDelegate(browser)
				cl.call()
			} catch (ignored) {
				exceptionCounter.incrementAndGet()
			}
		}
	}

}
