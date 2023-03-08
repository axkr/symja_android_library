package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IExpr;

public class OutputFormTest extends ExprEvaluatorTestCase {
  public OutputFormTest(String name) {
    super(name);
  }

  @Override
  public void check(String evalString, String expectedResult) {
    check(evaluator, evalString, expectedResult, -1);
  }

  public void testPlusReversed() {
    // github issue 694
    ExprEvaluator evaluator = new ExprEvaluator();
    IExpr input = evaluator.eval("I*27+(-46+I*46)*Sqrt(2)+27*Sqrt(3)");
    assertEquals(input.toString(), "I*27+(-46+I*46)*Sqrt(2)+27*Sqrt(3)");

    OutputFormFactory outputFormFactory = OutputFormFactory.get(true, false);
    String str = outputFormFactory.toString(input);
    assertEquals(str, "I*27+(-46+I*46)*Sqrt(2)+27*Sqrt(3)");

    OutputFormFactory outputFormFactory2 = OutputFormFactory.get(true, true);
    String str2 = outputFormFactory2.toString(input);
    assertEquals(str2, "27*Sqrt(3)+(-46+I*46)*Sqrt(2)+I*27");

    // junit.framework.AssertionFailedError:
    // Expected :(-18.28845206480269, 92.05382386916237)
    // Actual :(0.7653718043596811, 92.05382386916237)
    assertEquals(evaluator.evalComplex(str), evaluator.evalComplex(str2));

  }

  @Override
  protected void setUp() {
    super.setUp();
    if (Config.EXPENSIVE_JUNIT_TESTS) {
      Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    }
  }

}
