package org.matheclipse.core.system;

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


@Execution(ExecutionMode.SAME_THREAD)
public class ExprParserStepsTestCase extends ExprEvaluatorTestCase {
  EvalEngine engine;
  ExprParser parser;

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
    IExpr expr = parser.parse("a / b");
    assertEquals("Divide(a, b)", //
        expr.fullFormString());
  }

  @Test
  public void testDivideRational() {
    IExpr expr = parser.parse("3/4");
    assertEquals("Rational(3,4)", //
        expr.fullFormString());
  }

  @Test
  public void testSubtract() {
    IExpr expr = parser.parse("a - b");
    assertEquals("Subtract(a, b)", //
        expr.fullFormString());
  }

}
