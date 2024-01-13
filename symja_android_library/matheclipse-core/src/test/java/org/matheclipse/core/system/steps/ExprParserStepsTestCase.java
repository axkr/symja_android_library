package org.matheclipse.core.system.steps;

import static org.junit.Assert.assertEquals;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.system.ExprEvaluatorTestCase;


@Execution(ExecutionMode.SAME_THREAD)
public class ExprParserStepsTestCase extends ExprEvaluatorTestCase {
  EvalEngine engine;
  ExprParser parser;


  private void checkParserFullForm(String expression, String expected) {
    IExpr expr = parser.parse(expression);
    assertEquals(expected, expr.fullFormString());
  }

  @Override
  @Before
  public void setUp() {
    super.setUp();
    Config.USER_STEPS_PARSER = true;
    this.engine = new EvalEngine("", 256, 256, System.out, System.err, true);
    this.parser = new ExprParser(engine, true);
  }

  @AfterClass
  public static void afterAll() {
    Config.USER_STEPS_PARSER = false;
  }


  @Test
  public void testDivideSymbols() {
    checkParserFullForm("a / b", //
        "Divide(a, b)");
  }

  @Test
  public void testDivideRational() {
    checkParserFullForm("3/4", //
        "Rational(3, 4)");
  }

  @Test
  public void testSubtract() {
    checkParserFullForm("a - b", //
        "Subtract(a, b)");
  }

  @Test
  public void testDivide() {
    checkParserFullForm("6÷4", //
        "Rational(6, 4)");
  }

  @Test
  public void testParser002() {
    checkParserFullForm("2/4", //
        "Rational(2, 4)");
  }
}
