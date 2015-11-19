package geb.spock

import org.junit.rules.MethodRule
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.Statement

/**
 * Created by Leo on 11/11/2015.
 */
public class MethodExecutionRule implements MethodRule {

    public static def failedTests = [] //list of failed tests

    public Statement apply(final Statement statement, final FrameworkMethod frameworkMethod, final Object o) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate()
                } catch (Throwable t) {
                    failedTests.add(frameworkMethod.getName()) //only adds failed tests
                    throw t
                }
            }
        }
    }
}