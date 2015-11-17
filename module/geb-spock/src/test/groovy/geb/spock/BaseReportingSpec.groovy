import geb.report.ReporterSupport
import geb.spock.GebSpec
import org.junit.Rule
import org.junit.rules.TestName
import spock.lang.Shared

/**
 * Created by Leo on 11/11/2015.
 */
class BaseReportingSpec extends GebSpec {

    @Rule
    MethodExecutionRule failTracker = new MethodExecutionRule()
    // Ridiculous name to avoid name clashes
    @Rule
    TestName gebReportingSpecTestName
    private int gebReportingPerTestCounter = 1
    @Shared
    private int gebReportingSpecTestCounter = 1

    def setupSpec() {
        reportGroup getClass()
        cleanReportGroupDir()
    }

    def setup() {
        reportGroup getClass()
    }

    def cleanup() {
        report "end"
        ++gebReportingSpecTestCounter
    }

    void report(String label = "") {
        if (failTracker.failedTests.contains(gebReportingSpecTestName.methodName)) {
            browser.report(ReporterSupport.toTestReportLabel(gebReportingSpecTestCounter, gebReportingPerTestCounter++, gebReportingSpecTestName.methodName, label))
        }
    }

}
