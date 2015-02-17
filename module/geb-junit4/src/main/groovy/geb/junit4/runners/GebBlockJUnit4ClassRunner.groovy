package geb.junit4.runners

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.junit.internal.runners.model.ReflectiveCallable
import org.junit.internal.runners.statements.Fail
import org.junit.internal.runners.statements.RunAfters
import org.junit.rules.TestRule
import org.junit.runners.BlockJUnit4ClassRunner
import org.junit.runners.model.FrameworkMethod
import org.junit.runners.model.InitializationError
import org.junit.runners.model.Statement
import org.springframework.util.ReflectionUtils

import java.lang.reflect.Method

class GebBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner{

  private static final Log logger = LogFactory.getLog(GebBlockJUnit4ClassRunner.class);

  /**
   * Enhance JUnit execution chain with {@link GebRule}
   * and  {@link GebAfter}
   */
  public GebBlockJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
    super(clazz);
  }

  /**
   * Augments the default SpringJUnit4 behavior
   * {@link #withRules(FrameworkMethod, Object, Statement, List<TestRule>)
   *
   */
  @Override
  protected Statement methodBlock(FrameworkMethod frameworkMethod) {
    Object testInstance;
    try {
      testInstance = new ReflectiveCallable() {

        @Override
        protected Object runReflectiveCall() throws Throwable {
          return createTest();
        }
      }.run();
    }
    catch (Throwable ex) {
      return new Fail(ex);
    }

    Statement statement = methodInvoker(frameworkMethod, testInstance);
    statement = possiblyExpectingExceptions(frameworkMethod, testInstance, statement);
    statement = withBefores(frameworkMethod, testInstance, statement);

    //with GebRules
    statement = withRules(frameworkMethod, testInstance, statement, getGebRules(testInstance));

    //with GebAfters
    statement = withGebAfters(testInstance, statement);

    //with JUnit Afters
    statement = withAfters(frameworkMethod, testInstance, statement);

    //with JUnit Rules
    statement = withRules(frameworkMethod, testInstance, statement, getTestRules(testInstance));

    return statement;
  }



  private Statement withRules(FrameworkMethod method, Object target, Statement statement, List<TestRule> testRules) {
    Statement result = statement;
    result = withMethodRulesReflectively(method, testRules, target, result);
    result = withTestRulesReflectively(method, testRules, result);
    return result;
  }

  /**
   * Calling BlockJUnit4ClassRunner.withMethodRules() using reflection because the method is private
   */
  private Statement withMethodRulesReflectively(FrameworkMethod method, List<TestRule> testRules, Object target, Statement statement) {
    Method withRulesMethod = ReflectionUtils.findMethod(getClass(), "withMethodRules", FrameworkMethod.class, List.class, Object.class, Statement.class);
    if (withRulesMethod != null) {
      ReflectionUtils.makeAccessible(withRulesMethod);
      statement = (Statement) ReflectionUtils.invokeMethod(withRulesMethod, this, method, testRules,target, statement);
    }
    return statement;
  }

  /**
   * Calling BlockJUnit4ClassRunner.withTestRules() using reflection because the method is private
   */
  private Statement withTestRulesReflectively(FrameworkMethod method, List<TestRule> testRules, Statement statement) {
    Method withRulesMethod = ReflectionUtils.findMethod(getClass(), "withTestRules", FrameworkMethod.class, List.class, Statement.class);
    if (withRulesMethod != null) {
      ReflectionUtils.makeAccessible(withRulesMethod);
      statement = (Statement) ReflectionUtils.invokeMethod(withRulesMethod, this, method, testRules,statement);
    }
    return statement;
  }

  /**
   * @param target the test case instance
   * @return a list of TestRules that should be applied when executing this test
   */
  protected List<TestRule> getGebRules(Object target) {
    List<TestRule> result = getTestClass().getAnnotatedMethodValues(target, GebRule.class, TestRule.class);
    result.addAll(getTestClass().getAnnotatedFieldValues(target, GebRule.class, TestRule.class));
    return result;
  }

  protected Statement withGebAfters(Object target, Statement statement) {
    List<FrameworkMethod> gebAfters = getTestClass().getAnnotatedMethods(GebAfter.class);
    return gebAfters.isEmpty() ? statement : new RunAfters(statement, gebAfters,
    target);
  }

}
