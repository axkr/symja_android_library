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


  /**
   * See: <a href=
   * "https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_fu.py">sympy/simplify/tests/test_fu.py</a>
   */
  public void testTrigSimplifyFu() {
    // CTR1 example
    check("TrigSimplifyFu(Sin(x)^4 - Cos(y)^2 + Sin(y)^2 + 2*Cos(x)^2)", //
        "2+Cos(x)^4-2*Cos(y)^2");

    check("TrigSimplifyFu(1/2 - Cos(2*x)/2)", //
        "Sin(x)^2");

    // CTR3 - Simpy gets other result:
    check("TrigSimplifyFu(Sin(a)*(Cos(b) - Sin(b)) + Cos(a)*(Sin(b) + Cos(b)))", //
        "Cos(a+b)+Sin(a+b)");

    // CTR4 TODO
    check("TrigSimplifyFu(Sqrt(3)*Cos(x)/2 + Sin(x)/2)", //
        "1/2*Sqrt(3)*Cos(x)+Sin(x)/2");

    // Example 1 - Simpy gets other result:
    check("TrigSimplifyFu(1-Sin(2*x)^2/4-Sin(y)^2-Cos(x)^4)", //
        "Sin(x)^2-Sin(y)^2");

    // Example 2 - TODO
    check("TrigSimplifyFu(Cos(4*Pi/9))", //
        "Cos(4/9*Pi)");

    check("TrigSimplifyFu(Cos(Pi/9)*Cos(2*Pi/9)*Cos(3*Pi/9)*Cos(4*Pi/9))", //
        "1/16");

    // sin(50)**2 + cos(50)**2 + sin(pi/6)
    check("TrigSimplifyFu(Sin(50)^2 + Cos(50)^2 + Sin(pi/6))", //
        "3/2");

    // TODO sqrt(6)*cos(x) + sqrt(2)*sin(x)
    check("TrigSimplifyFu(Sqrt(6)*Cos(x) + Sqrt(2)*Sin(x))", //
        // sympy 2*sqrt(2)*sin(x + pi/3)
        "Sqrt(6)*Cos(x)+Sqrt(2)*Sin(x)");

    // sin(x)**4 - cos(y)**2 + sin(y)**2 + 2*cos(x)**2
    check("TrigSimplifyFu(sin(x)^4 - cos(y)^2 + sin(y)^2 + 2*cos(x)^2)", //
        "2+Cos(x)^4-2*Cos(y)^2");

    // S.Half - cos(2*x)/2
    check("TrigSimplifyFu(1/2 - cos(2*x)/2)", //
        "Sin(x)^2");

    // TODO(?) sin(a)*(cos(b) - sin(b)) + cos(a)*(sin(b) + cos(b))
    check("TrigSimplifyFu(sin(a)*(cos(b) - sin(b)) + cos(a)*(sin(b) + cos(b)))", //
        // sympy sqrt(2)*sin(a + b + pi/4) (which is better?)
        "Cos(a+b)+Sin(a+b)");

    // TODO sqrt(3)*cos(x)/2 + sin(x)/2
    check("TrigSimplifyFu(sqrt(3)*cos(x)/2 + sin(x)/2)", //
        // sympy sin(x + pi/3)
        "1/2*Sqrt(3)*Cos(x)+Sin(x)/2");

    // TODO 1 - sin(2*x)**2/4 - sin(y)**2 - cos(x)**4
    check("TrigSimplifyFu(1 - sin(2*x)^2/4 - sin(y)^2 - cos(x)^4)", //
        // sympy -cos(x)**2 + cos(y)**2 (which is better?)
        "Sin(x)^2-Sin(y)^2");

    // TODO cos(pi*Rational(4, 9))
    check("TrigSimplifyFu(Cos(Pi*4/9))", //
        // sympy sin(pi/18)
        "Cos(4/9*Pi)");

    // TODO cos(pi/9)*cos(pi*Rational(2, 9))*cos(pi*Rational(3, 9))*cos(pi*Rational(4, 9))
    check("TrigSimplifyFu(Cos(pi/9)*Cos(pi*2(9)*Cos(Pi*3/9))*Cos(Pi*4/9))", //
        // sympy Rational(1, 16)
        "-Cos(Pi/9)*Cos(4/9*Pi)");

    // // ERROR - TODO tan(pi*Rational(7, 18)) + tan(pi*Rational(5, 18)) -
    // sqrt(3)*tan(pi*Rational(5,
    // // 18))*tan(pi*Rational(7, 18))
    // check("TrigSimplifyFu(Tan(Pi*7/18) + tan(Pi*5/18) - sqrt(3)*tan(Pi*5/18)*tan(Pi*7/18))", //
    // // sympy -sqrt(3)
    // "");

    // tan(1)*tan(2)
    check("TrigSimplifyFu(tan(1)*tan(2))", //
        "Tan(1)*Tan(2)");

    // TODO Mul(*[cos(2**i) for i in range(10)])
    check("TrigSimplifyFu(Product(Cos(2^i),{i,0,9}))", //
        // sympy sin(1024)/(1024*sin(1))
        "Cos(1)*Cos(2)*Cos(4)*Cos(8)*Cos(16)*Cos(32)*Cos(64)*Cos(128)*Cos(256)*Cos(512)");

    // # sympy issue #18059:
    // cos(x) + sqrt(sin(x)**2)
    check("TrigSimplifyFu(cos(x) + sqrt(sin(x)^2))", //
        "Cos(x)+Sqrt(Sin(x)^2)");

    // TODO (-14*sin(x)**3 + 35*sin(x) + 6*sqrt(3)*cos(x)**3 + 9*sqrt(3)*cos(x))/((cos(2*x) + 4))
    check(
        "TrigSimplifyFu((-14*sin(x)^3 + 35*sin(x) + 6*sqrt(3)*cos(x)^3 + 9*sqrt(3)*cos(x))/((cos(2*x) + 4)))", //
        // sympy 7*sin(x) + 3*sqrt(3)*cos(x)
        "(9*Sqrt(3)*Cos(x)+6*Sqrt(3)*Cos(x)^3+35*Sin(x)-14*Sin(x)^3)/(4+Cos(2*x))");
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

    // tan(tan(x) - sin(x)/cos(x))
    tr2 = TrigSimplifyFu.tr2(F.Tan(F.Subtract(F.Tan(F.x), F.Divide(F.Sin(F.x), F.Cos(F.x)))));
    assertEquals(tr2.toString(), //
        "Sin(Sin(x)/Cos(x)-Sin(x)/Cos(x))/Cos(Sin(x)/Cos(x)-Sin(x)/Cos(x))");
    tr2 = F.eval(tr2);
    assertEquals(tr2.toString(), //
        "0");
  }

  public void testTrigSimplifyTR2i() {
    IExpr tr2i = TrigSimplifyFu.tr2i(F.Divide(F.Sin(F.x), F.Cos(F.x)), false);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(x)");

    tr2i = TrigSimplifyFu
        .tr2i(F.Divide(F.Power(F.Sin(F.x), F.C2), F.Power(F.Plus(F.Cos(F.x), F.C1), F.C2)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(x/2)^2");
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
    IExpr tr8 = TrigSimplifyFu.tr8Step(F.Times(F.Cos(F.C2), F.Cos(F.C3)), true);
    assertEquals(tr8.toString(), //
        "1/2*(Cos(1)+Cos(5))");

    tr8 = TrigSimplifyFu.tr8Step(F.Times(F.Cos(F.C2), F.Sin(F.C3)), true);
    assertEquals(tr8.toString(), //
        "1/2*(Sin(1)+Sin(5))");

    tr8 = TrigSimplifyFu.tr8Step(F.Times(F.Sin(F.C2), F.Sin(F.C3)), true);
    assertEquals(tr8.toString(), //
        "1/2*(Cos(1)-Cos(5))");
  }

  public void testTrigSimplifyTR10() {
    IExpr tr10 = TrigSimplifyFu.tr10(F.Cos(F.Plus(F.a, F.b)));
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

  public void testTrigSimplifyTR12() {
    // tan(x + y)
    IExpr tr12 = TrigSimplifyFu.tr12(F.Tan(F.Plus(F.x, F.y)));
    assertEquals(tr12.toString(), //
        "(Tan(x)+Tan(y))/(1-Tan(x)*Tan(y))");
    // tan(x + y + z)
    tr12 = TrigSimplifyFu.tr12(F.Tan(F.Plus(F.x, F.y, F.z)));
    // sympy
    // (tan(z) + (tan(x) + tan(y))/(-tan(x)*tan(y) + 1))/(1 - (tan(x) +
    // tan(y))*tan(z)/(-tan(x)*tan(y) + 1))
    assertEquals(tr12.toString(), //
        "(Tan(x)+(Tan(y)+Tan(z))/(1-Tan(y)*Tan(z)))/(1-(Tan(x)*(Tan(y)+Tan(z)))/(1-Tan(y)*Tan(z)))");

    // tan(x * y)
    tr12 = TrigSimplifyFu.tr12(F.Tan(F.Times(F.x, F.y)));
    assertEquals(tr12.toString(), //
        "Tan(x*y)");

  }

  public void testTrigSimplifyTR13() {
    // tan(2)*tan(3)
    IExpr tr13 = TrigSimplifyFu.tr13(F.Times(F.Tan(F.C2), F.Tan(F.C3)));
    // sympy -tan(2)/tan(5) - tan(3)/tan(5) + 1
    assertEquals(tr13.toString(), //
        "1-(Tan(2)/Tan(2+3)+Tan(3)/Tan(2+3))");
    tr13 = F.eval(tr13);
    assertEquals(tr13.toString(), //
        "1-Cot(5)*Tan(2)-Cot(5)*Tan(3)");


    // cot(2)*cot(3)
    tr13 = TrigSimplifyFu.tr13(F.Times(F.Cot(F.C2), F.Cot(F.C3)));
    // sympy 1 + cot(3)*cot(5) + cot(2)*cot(5)
    assertEquals(tr13.toString(), //
        "1+Cot(2)*Cot(2+3)+Cot(3)*Cot(2+3)");
    tr13 = F.eval(tr13);
    assertEquals(tr13.toString(), //
        "1+Cot(2)*Cot(5)+Cot(3)*Cot(5)");


    // tan(1)*tan(2)*tan(3)
    tr13 = TrigSimplifyFu.tr13(F.Times(F.Tan(F.C1), F.Tan(F.C2), F.Tan(F.C3)));
    // sympy (-tan(2)/tan(5) - tan(3)/tan(5) + 1)*tan(1)
    assertEquals(tr13.toString(), //
        "(1-(Tan(2)/Tan(2+3)+Tan(3)/Tan(2+3)))*Tan(1)");
    tr13 = F.eval(tr13);
    assertEquals(tr13.toString(), //
        "Tan(1)*(1-Cot(5)*Tan(2)-Cot(5)*Tan(3))");
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
