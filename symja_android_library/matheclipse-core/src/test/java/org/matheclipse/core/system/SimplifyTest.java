package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.reflection.system.TrigSimplifyFu;

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

  public void testTrigSimplifyTRmorrie() {
    IExpr trMorrie = TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.x), F.Cos(F.Times(F.C2, F.x))));
    assertEquals(trMorrie.toString(), //
        "1/4*Csc(x)*Sin(4*x)");

    trMorrie = TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.x), F.Cos(F.Times(F.C2, F.x)),
        F.Cos(F.Times(F.C4, F.x)), F.Cos(F.Times(F.C6, F.x))));
    assertEquals(trMorrie.toString(), //
        "1/8*Cos(6*x)*Csc(x)*Sin(8*x)");

  }

  public void testTrigSimplifyTR2i() {
    // IExpr tr2i = TrigSimplifyFu.tr2i(F.Divide(F.Sin(F.x), F.Cos(F.x)), false);
    // assertEquals(tr2i.toString(), //
    // "NIL");

    IExpr tr2i = TrigSimplifyFu
        .tr2i(F.Divide(F.Power(F.Sin(F.x), F.C2), F.Power(F.Plus(F.Cos(F.x), F.C1), F.C2)), false);
    assertEquals(tr2i.toString(), //
        "NIL");
  }

  public void testTrigSimplifyTR8() {
    IExpr tr8 = TrigSimplifyFu.tr8(F.Times(F.Cos(F.C2), F.Cos(F.C3)), true);
    assertEquals(tr8.toString(), //
        "Cos(1)/2+Cos(5)/2");

    tr8 = TrigSimplifyFu.tr8(F.Times(F.Cos(F.C2), F.Sin(F.C3)), true);
    assertEquals(tr8.toString(), //
        "Sin(1)/2+Sin(5)/2");

    tr8 = TrigSimplifyFu.tr8(F.Times(F.Sin(F.C2), F.Sin(F.C3)), true);
    assertEquals(tr8.toString(), //
        "Cos(1)/2-Cos(5)/2");
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
