package org.matheclipse.core.system;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
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

  public void testTrigSimplifyTR1() {
    // 2*csc(x) + sec(x)
    IExpr tr1 = TrigSimplifyFu.tr1(F.Plus(F.Times(F.C2, F.Csc(F.x)), F.Sec(F.x)));
    assertEquals(tr1.toString(), //
        "2/Sin(x)+1/Cos(x)");
  }

  public void testTrigSimplifyTR2() {
    IExpr tr2 = TrigSimplifyFu.tr2(F.Divide(F.C1, F.Tan(F.x)));
    assertEquals(tr2.toString(), //
        "1/(Sin(x)/Cos(x))");
    tr2 = TrigSimplifyFu.tr2(F.Tan(F.x));
    assertEquals(tr2.toString(), //
        "Sin(x)/Cos(x)");
  }

  public void testTrigSimplifyTR2i() {
    IExpr tr2i = TrigSimplifyFu.tr2i(F.Divide(F.Sin(F.x), F.Cos(F.x)), false);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "{Tan(x)}");

    tr2i = TrigSimplifyFu
        .tr2i(F.Divide(F.Power(F.Sin(F.x), F.C2), F.Power(F.Plus(F.Cos(F.x), F.C1), F.C2)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "{Tan(x/2)^2}");
  }

  public void testTrigSimplifyTR3() {
    IExpr tr3 = TrigSimplifyFu.tr3(F.Cos(F.Plus(F.Times(F.C1D2, S.Pi), F.x)));
    tr3 = F.eval(tr3);
    assertEquals(tr3.toString(), //
        "-Sin(x)");

    tr3 = TrigSimplifyFu.tr3(F.Cos(F.Plus(F.Times(F.ZZ(15), S.Pi), F.x)));
    tr3 = F.eval(tr3);
    assertEquals(tr3.toString(), //
        "-Cos(x)");
  }

  public void testTrigSimplifyTR7() {
    // Cos(x^2)
    IExpr tr7 = TrigSimplifyFu.tr7(F.Power(F.Cos(F.x), F.C2));
    assertEquals(tr7.toString(), //
        "1/2+Cos(2*x)/2");

    // 1+Cos(x^2)
    tr7 = TrigSimplifyFu.tr7(F.Plus(1, F.Power(F.Cos(F.x), F.C2)));
    tr7 = F.eval(tr7);
    assertEquals(tr7.toString(), //
        "3/2+Cos(2*x)/2");
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

  public void testTrigSimplifyTR10() {
    IExpr tr10 =
        TrigSimplifyFu.tr10(F.Cos(F.Plus(F.a, F.b)));
    assertEquals(tr10.toString(), //
        "Cos(a)*Cos(b)-Sin(a)*Sin(b)");

    tr10 = TrigSimplifyFu.tr10(F.Sin(F.Plus(F.a, F.b)));
    assertEquals(tr10.toString(), //
        "Cos(b)*Sin(a)+Cos(a)*Sin(b)");

    tr10 = TrigSimplifyFu.tr10(F.Sin(F.Plus(F.a, F.b, F.c)));
    assertEquals(tr10.toString(), //
        "Sin(a)*Cos(b)*Cos(c)+Sin(a)*-Sin(b)*Sin(c)+Cos(a)*Cos(c)*Sin(b)+Cos(a)*Cos(b)*Sin(c)");
  }

  public void testTrigSimplifyTR11() {
    // TR11(sin(x/3)/(cos(x/6)))
    IExpr tr11 =
        TrigSimplifyFu.tr11(F.Divide(F.Sin(F.Times(F.C1D3, F.x)), F.Cos(F.Times(F.C1D6, F.x))));
    assertEquals(tr11.toString(), //
        "Sin(x/3)/Cos(x/6)");

    tr11 = TrigSimplifyFu.tr11(F.Sin(F.Times(F.C2, F.x)));
    assertEquals(tr11.toString(), //
        "2*Cos(x)*Sin(x)");
    tr11 = TrigSimplifyFu.tr11(F.Cos(F.Times(F.C2, F.x)));
    assertEquals(tr11.toString(), //
        "1-2*Sin(x)^2");

    tr11 = TrigSimplifyFu.tr11(F.Sin(F.Times(F.C4, F.x)));
    tr11 = F.eval(tr11);
    assertEquals(tr11.toString(), //
        "4*Cos(x)*Sin(x)*(1-2*Sin(x)^2)");
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
