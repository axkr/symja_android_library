package org.matheclipse.core.system.steps;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.B2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.tex.TeXFormFactory;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

@Execution(ExecutionMode.SAME_THREAD)
public class TeXFormStepsTest extends ExprEvaluatorTestCase {

  @Override
  @Before
  public void setUp() {
    super.setUp();
    Config.USER_STEPS_PARSER = true;
  }


  @AfterClass
  public static void afterAll() {
    Config.USER_STEPS_PARSER = false;
  }

  @Override
  public void check(String evalString, String expectedResult) {
    check(evaluator, evalString, expectedResult, -1);
  }

  @Test
  public void testDivide() {
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    StringBuilder sb = new StringBuilder();
    fTeXFactory.convert(sb, new B2.Divide(F.a, F.b), 0);
    Assertions.assertEquals( //
        "frac{a}{b}", //
        sb.toString());
  }

  @Test
  public void testSubtract() {
    TeXFormFactory fTeXFactory = new TeXFormFactory();
    StringBuilder sb = new StringBuilder();
    fTeXFactory.convert(sb, new B2.Subtract(F.a, F.b), 0);
    Assertions.assertEquals( //
        "a - b", //
        sb.toString());
  }
}
