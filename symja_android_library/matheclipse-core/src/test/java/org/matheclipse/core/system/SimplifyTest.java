package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

public class SimplifyTest extends ExprEvaluatorTestCase {

  public SimplifyTest(String name) {
    super(name);
  }

  public void testTrigSimplify() {
    // check("TrigSimplifyFu(Sin(4*x/3))", //
    // " ");
    // check("TrigSimplifyFu(Cos(2*x))", //
    // "1-2*Sin(x)^2");
    // check("TrigSimplifyFu(Sin(2*x))", //
    // "2*Cos(x)*Sin(x)");
    // check("TrigSimplifyFu(Sin(x)^12)", //
    // "(1-Cos(x)^2)^6");
    // check("TrigSimplifyFu(Sin(x)^2)", //
    // "1-Cos(x)^2");
    // check("TrigSimplifyFu(1/Cos(x))", //
    // "Sec(x)");
  }

  /** The JUnit setup method */
  @Override
  protected void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
