package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

public class SimplifyTest extends ExprEvaluatorTestCase {

  public SimplifyTest(String name) {
    super(name);
  }

  public void testTrigSimplifyFu() {
    // CTR1 example
    check("TrigSimplifyFu(Sin(x)^4 - Cos(y)^2 + Sin(y)^2 + 2*Cos(x)^2)", //
        "2+Cos(x)^4-2*Cos(y)^2");

    check("TrigSimplifyFu(1/2 - Cos(2*x)/2)", //
        "Sin(x)^2");

    // check("TrigSimplifyFu(Sin(a)*(Cos(b) - Sin(b)) + Cos(a)*(Sin(b) + Cos(b)))", //
    // "Sqrt(2)*Sin(a + b + Pi/4)");


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
