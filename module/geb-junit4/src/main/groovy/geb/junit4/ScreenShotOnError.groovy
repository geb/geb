package geb.junit4

import geb.report.ReporterSupport
import groovy.util.logging.Log4j
import org.junit.rules.TestName
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@Log4j
public class ScreenShotOnError extends TestWatcher{

  @Delegate public GebReportingTest gebReportingTest = new GebReportingTest()

  private TestName reportingTestName

  private Class clazz

  public ScreenShotOnError(Class clazz, TestName gebReportingTestName){
    this.clazz = clazz
    this.reportingTestName = gebReportingTestName
  }

  @Override
  protected void failed(Throwable e, Description description) {
    generateReport()
  }

  @Override
  protected void starting(Description description) {
    gebReportingTest.reportGroup clazz
    gebReportingTest.incrementTestCounterValue()
  }

  public void generateReport(){
    browser.report(ReporterSupport.toTestReportLabel(gebReportingTest.getTestCounterValue(), gebReportingTest.instanceTestCounter++,  reportingTestName.methodName, 'error'))
  }

}
