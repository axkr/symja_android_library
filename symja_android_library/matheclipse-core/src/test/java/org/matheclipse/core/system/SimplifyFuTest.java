package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.TrigSimplifyFu;

public class SimplifyFuTest extends ExprEvaluatorTestCase {

  /**
   * Tests the new static integration API for the general Simplify routine. Ensures that the
   * fast-fail guard correctly bypasses non-trigonometric expressions.
   */
  @Test
  public void testSimplifyStaticAPI() {
    EvalEngine engine = EvalEngine.get();

    // Test standard trig simplification via the static API
    IAST expr = F.Plus(F.Power(F.Sin(F.x), F.C2), F.Power(F.Cos(F.x), F.C2));
    IExpr simplified = TrigSimplifyFu.simplify(expr, engine, x -> x.leafCountSimplify());
    assertEquals("1", simplified.toString());

    // Test the fast-fail mechanism (no trig functions present)
    IAST noTrig = F.Plus(F.Power(F.x, F.C2), F.x);
    IExpr noTrigResult = TrigSimplifyFu.simplify(noTrig, engine, x -> x.leafCountSimplify());

    // Should return F.NIL because of the hasTrig guard, avoiding unnecessary tree searches
    assertEquals(F.NIL, noTrigResult);
  }

  /**
   * See: <a href=
   * "https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_fu.py">sympy/simplify/tests/test_fu.py</a>
   */
  @Test
  public void testTrigSimplifyFu001() {

    // CTR1 example
    check("TrigSimplifyFu(Sin(x)^4 - Cos(y)^2 + Sin(y)^2 + 2*Cos(x)^2)", //
        "2+Cos(x)^4-2*Cos(y)^2");

    check("TrigSimplifyFu(1/2 - Cos(2*x)/2)", //
        "Sin(x)^2");

    // CTR3
    check("TrigSimplifyFu(Sin(a)*(Cos(b) - Sin(b)) + Cos(a)*(Sin(b) + Cos(b)))", //
        "Cos(a+b)+Sin(a+b)");

    // CTR4
    check("TrigSimplifyFu(Sqrt(3)*Cos(x)/2 + Sin(x)/2)", //
        "Sin(Pi/3+x)");

    // Example 1
    check("TrigSimplifyFu(1-Sin(2*x)^2/4-Sin(y)^2-Cos(x)^4)", //
        "1-Cos(x)^4-Sin(2*x)^2/4-Sin(y)^2");

    check("TrigSimplifyFu(Cos(4*Pi/9))", //
        "Cos(4/9*Pi)");

    check("TrigSimplifyFu(Cos(Pi/9)*Cos(2*Pi/9)*Cos(3*Pi/9)*Cos(4*Pi/9))", //
        "1/16");

    check("TrigSimplifyFu(Sin(50)^2 + Cos(50)^2 + Sin(pi/6))", //
        "3/2");

    check("TrigSimplifyFu(Sqrt(6)*Cos(x) + Sqrt(2)*Sin(x))", //
        "2*Sqrt(2)*Sin(Pi/3+x)");

    check("TrigSimplifyFu(sin(x)^4 - cos(y)^2 + sin(y)^2 + 2*cos(x)^2)", //
        "2+Cos(x)^4-2*Cos(y)^2");

    check("TrigSimplifyFu(1/2 - cos(2*x)/2)", //
        "Sin(x)^2");

    check("TrigSimplifyFu(sin(a)*(cos(b) - sin(b)) + cos(a)*(sin(b) + cos(b)))", //
        "Cos(a+b)+Sin(a+b)");

    check("TrigSimplifyFu(sqrt(3)*cos(x)/2 + sin(x)/2)", //
        "Sin(Pi/3+x)");

    check("TrigSimplifyFu(1 - sin(2*x)^2/4 - sin(y)^2 - cos(x)^4)", //
        "1-Cos(x)^4-Sin(2*x)^2/4-Sin(y)^2");

    check("TrigSimplifyFu(Cos(Pi*4/9))", //
        "Cos(4/9*Pi)");

    check("TrigSimplifyFu(Tan(7/18*Pi) + Tan(5/18*Pi) - Sqrt(3)*Tan(5/18*Pi)*Tan(7/18*Pi))", //
        "-Sqrt(3)");

    check("TrigSimplifyFu(tan(1)*tan(2))", //
        "Tan(1)*Tan(2)");

    check("TrigSimplifyFu(Product(Cos(2^i),{i,0,9}))", //
        "1/1024*Csc(1)*Sin(1024)");

    // SymPy issue #18059:
    check("TrigSimplifyFu(cos(x) + sqrt(sin(x)^2))", //
        "Cos(x)+Sqrt(Sin(x)^2)");

    check(
        "TrigSimplifyFu((-14*sin(x)^3 + 35*sin(x) + 6*sqrt(3)*cos(x)^3 + 9*sqrt(3)*cos(x))/((cos(2*x) + 4)))", //
        "(9*Sqrt(3)*Cos(x)+6*Sqrt(3)*Cos(x)^3+35*Sin(x)-14*Sin(x)^3)/(4+Cos(2*x))");
  }

  @Test
  public void testTrigSimplifyFu003() {

    // check("TrigSimplifyFu(cot(x)*sec(x)*csc(x))", //
    // "Csc(x)^2");

    check("TrigSimplifyFu(sin(2*x)*cos(3*x) + cos(2*x)*sin(3*x))", //
        "Sin(5*x)");

    check("TrigSimplifyFu(sin(x)/csc(x))", "Sin(x)^2");

    check("TrigSimplifyFu(cos(x)/sec(x))", "Cos(x)^2");

    check("TrigSimplifyFu(tan(x)/cot(x))", "Tan(x)^2");

    check("TrigSimplifyFu(sin(x)*tan(x)/sec(x))", "Sin(x)^2");

    check("TrigSimplifyFu(cos(x)*cot(x)/csc(x))", "Cos(x)^2");

    check("TrigSimplifyFu(tan(x)*sec(x)*csc(x))", //
        "Sec(x)^2");

    check("TrigSimplifyFu(sin(2*x)*cos(3*x) + cos(2*x)*sin(3*x))", //
        "Sin(5*x)");

    check("TrigSimplifyFu(sin(x+y)*cos(x-y) + cos(x+y)*sin(x-y))", //
        "Sin(2*x)");

    check("TrigSimplifyFu(cos(x+y)*cos(x-y) - sin(x+y)*sin(x-y))", //
        "Cos(2*x)");

    check("TrigSimplifyFu(cos(x+y)*cos(x-y) + sin(x+y)*sin(x-y))", //
        "Cos(2*y)");

    check("TrigSimplifyFu(sin(x-y)*cos(x+y) - cos(x-y)*sin(x+y))", //
        "-Sin(2*y)");
  }

  @Test
  public void testTrigSimplifyTRmorrie() {
    IExpr trMorrie = TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.x), F.Cos(F.Times(F.C2, F.x))));
    assertEquals("1/4*Csc(x)*Sin(4*x)", trMorrie.toString());

    trMorrie = TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.x), F.Cos(F.Times(F.C2, F.x)),
        F.Cos(F.Times(F.C4, F.x)), F.Cos(F.Times(F.C6, F.x))));
    assertEquals("1/8*Cos(6*x)*Csc(x)*Sin(8*x)", trMorrie.toString());

    IASTAppendable times = F.TimesAlloc();
    times.append(F.C7);
    for (int i = 0; i < 10; i++) {
      times.append(F.Cos(F.ZZ(i)));
    }
    trMorrie = TrigSimplifyFu.trMorrie(times);
    assertEquals("7/64*Cos(5)*Cos(7)*Cos(9)*Csc(1)*Csc(3)*Sin(12)*Sin(16)", trMorrie.toString());

    times = F.TimesAlloc();
    for (int i = 0; i < 10; i++) {
      times.append(F.Cos(F.C2.pow(i)));
    }
    trMorrie = TrigSimplifyFu.trMorrie(times);
    assertEquals("1/1024*Csc(1)*Sin(1024)", trMorrie.toString());

    trMorrie = TrigSimplifyFu.trMorrie(F.x);
    assertEquals("x", trMorrie.toString());

    trMorrie = TrigSimplifyFu.trMorrie(F.Times(2, F.x));
    assertEquals("2*x", trMorrie.toString());

    trMorrie = TrigSimplifyFu.tr8(TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.Times(F.QQ(1, 7), S.Pi)),
        F.Cos(F.Times(F.QQ(2, 7), S.Pi)), F.Cos(F.Times(F.QQ(4, 7), S.Pi)))));
    assertEquals("-1/8", trMorrie.toString());

    times = F.TimesAlloc();
    for (int i = 1; i < 17; i++) {
      times.append(F.Cos(F.C2.pow(i).divide(F.ZZ(17)).multiply(S.Pi)));
    }
    trMorrie = TrigSimplifyFu.tr8(TrigSimplifyFu.tr3(TrigSimplifyFu.trMorrie(times)));
    assertEquals("1/65536", trMorrie.toString());

    // Issue 17063
    trMorrie = TrigSimplifyFu.trMorrie(F.Divide(F.Cos(F.x), F.Cos(F.Times(F.C1D2, F.x))));
    assertEquals("Cos(x)/Cos(x/2)", trMorrie.toString());

    // Issue 20430
    trMorrie = TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.Times(F.C1D2, F.x)),
        F.Sin(F.Times(F.C1D2, F.x)), F.Power(F.Cos(F.x), F.C3)));
    assertEquals("1/64*Csc(x/2)^2*Sec(x/2)^2*Sin(2*x)^3", trMorrie.toString());
  }

  @Test
  public void testTrigSimplifyTR1() {
    IExpr tr1 = TrigSimplifyFu.tr1(F.Plus(F.Times(F.C2, F.Csc(F.x)), F.Sec(F.x)));
    assertEquals("2*Csc(x)+Sec(x)", tr1.toString());
  }

  @Test
  public void testTrigSimplifyTR2() {
    IExpr tr2 = TrigSimplifyFu.tr2(F.Divide(F.C1, F.Tan(F.x)));
    assertEquals("1/(Sin(x)/Cos(x))", tr2.toString());

    tr2 = TrigSimplifyFu.tr2(F.Tan(F.x));
    assertEquals("Sin(x)/Cos(x)", tr2.toString());

    tr2 = TrigSimplifyFu.tr2(F.Tan(F.Subtract(F.Tan(F.x), F.Divide(F.Sin(F.x), F.Cos(F.x)))));
    assertEquals("Sin(-Sin(x)/Cos(x)+Sin(x)/Cos(x))/Cos(-Sin(x)/Cos(x)+Sin(x)/Cos(x))",
        tr2.toString());

    tr2 = F.eval(tr2);
    assertEquals("0", tr2.toString());
  }

  @Test
  public void testTrigSimplifyTR2i() {
    IExpr tr2i = TrigSimplifyFu
        .tr2i(F.Divide(F.Power(F.Sin(F.x), F.C2), F.Power(F.Plus(F.Cos(F.x), F.C1), F.C2)), true);
    tr2i = F.eval(tr2i);
    assertEquals("Tan(x/2)^2", tr2i.toString());

    tr2i = TrigSimplifyFu.tr2i(F.Divide(F.Sin(F.x), F.Cos(F.x)), false);
    tr2i = F.eval(tr2i);
    assertEquals("Tan(x)", tr2i.toString());

    tr2i = TrigSimplifyFu.tr2i(F.Divide(F.Times(F.Sin(F.x), F.Sin(F.y)), F.Cos(F.x)), false);
    tr2i = F.eval(tr2i);
    assertEquals("Sin(y)*Tan(x)", tr2i.toString());

    tr2i = TrigSimplifyFu.tr2i(F.Power(F.Divide(F.Sin(F.x), F.Cos(F.x)), F.CN1), false);
    tr2i = F.eval(tr2i);
    assertEquals("Cot(x)", tr2i.toString());

    tr2i = TrigSimplifyFu
        .tr2i(F.Power(F.Divide(F.Times(F.Sin(F.x), F.Sin(F.y)), F.Cos(F.x)), F.CN1), false);
    tr2i = F.eval(tr2i);
    assertEquals("Cot(x)*Csc(y)", tr2i.toString());

    tr2i = TrigSimplifyFu
        .tr2i(F.Times(F.C1D2, F.Sin(F.x), F.Power(F.Plus(F.C1, F.Cos(F.x)), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals("Tan(x/2)/2", tr2i.toString());

    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Sin(F.C1), F.Power(F.Plus(F.C1, F.Cos(F.C1)), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals("Tan(1/2)", tr2i.toString());

    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C4)), F.Negate(F.a)), //
        F.Power(F.Sin(F.C4), F.a)), true);
    tr2i = F.eval(tr2i);
    assertEquals("Sin(4)^a/(1+Cos(4))^a", tr2i.toString());

    ISymbol i = F.symbol("i", F.Element(F.Slot1, S.Integers));
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C5)), i), //
        F.Power(F.Sin(F.C5), F.Negate(i))), true);

    tr2i = F.eval(tr2i);
    assertEquals("Tan(5/2)^(-i)", tr2i.toString());

    tr2i = TrigSimplifyFu.tr2i(F.Power(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C5)), i), //
        F.Power(F.Sin(F.C5), F.Negate(i))), F.CN1), true);
    tr2i = F.eval(tr2i);
    assertEquals("Tan(5/2)^i", tr2i.toString());
  }

  @Test
  public void testTrigSimplifyTR3() {
    IExpr tr3 = TrigSimplifyFu.tr3(F.Cos(F.Plus(F.Times(F.C1D2, S.Pi), F.x)));
    tr3 = F.eval(tr3);
    assertEquals("-Sin(x)", tr3.toString());

    tr3 = TrigSimplifyFu.tr3(F.Cos(F.Plus(F.Times(F.ZZ(15), S.Pi), F.x)));
    tr3 = F.eval(tr3);
    assertEquals("-Cos(x)", tr3.toString());
  }

  @Test
  public void testTrigSimplifyTR5() {
    IExpr tr5 = TrigSimplifyFu.tr5(F.Power(F.Sin(F.x), F.C2));
    assertEquals("1-Cos(x)^2", tr5.toString());

    tr5 = TrigSimplifyFu.tr5(F.Power(F.Sin(F.x), F.CN2));
    assertEquals("Csc(x)^2", tr5.toString());

    tr5 = TrigSimplifyFu.tr5(F.Power(F.Sin(F.x), F.C4));
    assertEquals("(1-Cos(x)^2)^2", tr5.toString());
  }

  @Test
  public void testTrigSimplifyTR6() {
    IExpr tr6 = TrigSimplifyFu.tr6(F.Power(F.Cos(F.x), F.C2));
    assertEquals("1-Sin(x)^2", tr6.toString());

    tr6 = TrigSimplifyFu.tr6(F.Power(F.Cos(F.x), F.CN2));
    assertEquals("Sec(x)^2", tr6.toString());

    tr6 = TrigSimplifyFu.tr6(F.Power(F.Cos(F.x), F.C4));
    assertEquals("(1-Sin(x)^2)^2", tr6.toString());
  }

  @Test
  public void testTrigSimplifyTR56() {
    Function<IExpr, IExpr> h = x -> F.C1.subtract(x);

    IExpr tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C3), F.Sin, F.Cos, h, F.C4, false);
    assertEquals("(1-Cos(x)^2)*Sin(x)^2", tr56.toString());

    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C10), F.Sin, F.Cos, h, F.C4, false);
    assertEquals("Sin(x)^10", tr56.toString());

    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C6), F.Sin, F.Cos, h, F.C6, false);
    assertEquals("(1-Cos(x)^2)^3", tr56.toString());

    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C6), F.Sin, F.Cos, h, F.C6, true);
    assertEquals("Sin(x)^6", tr56.toString());

    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C8), F.Sin, F.Cos, h, F.C10, true);
    assertEquals("(1-Cos(x)^2)^4", tr56.toString());

    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.CI), F.Sin, F.Cos, h, F.C4, true);
    assertEquals("Sin(x)^I", tr56.toString());

    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.Plus(F.Times(F.C2, F.CI), F.C1)), //
        F.Sin, F.Cos, h, F.C4, true);
    assertEquals("Sin(x)^(1+I*2)", tr56.toString());
  }

  @Test
  public void testTrigSimplifyTR7() {
    IExpr tr7 = TrigSimplifyFu.tr7(F.Power(F.Cos(F.x), F.C2));
    assertEquals("1/2+Cos(2*x)/2", tr7.toString());

    tr7 = TrigSimplifyFu.tr7(F.Plus(1, F.Power(F.Cos(F.x), F.C2)));
    tr7 = F.eval(tr7);
    assertEquals("3/2+Cos(2*x)/2", tr7.toString());
  }

  @Test
  public void testTrigSimplifyTR8() {
    IExpr tr8 = TrigSimplifyFu.tr8(F.Times(F.Cos(F.C2), F.Cos(F.C3)), true);
    assertEquals("1/2*(Cos(1)+Cos(5))", tr8.toString());

    tr8 = TrigSimplifyFu.tr8(F.Times(F.Cos(F.C2), F.Sin(F.C3)), true);
    assertEquals("1/2*(Sin(1)+Sin(5))", tr8.toString());

    tr8 = TrigSimplifyFu.tr8(F.Times(F.Sin(F.C2), F.Sin(F.C3)), true);
    assertEquals("1/2*(Cos(1)-Cos(5))", tr8.toString());

    tr8 = TrigSimplifyFu.tr8(F.Times(F.Sin(F.C1), F.Sin(F.C2), F.Sin(F.C3)), true);
    assertEquals("Sin(2)/4+Sin(4)/4-Sin(6)/4", tr8.toString());

    tr8 = TrigSimplifyFu.tr8(F.Times(F.Cos(F.C2), F.Cos(F.C3), F.Cos(F.C4), F.Cos(F.C5)), true);
    assertEquals("1/8+Cos(2)/8+Cos(4)/4+Cos(6)/8+Cos(8)/8+Cos(10)/8+Cos(14)/8", tr8.toString());

    tr8 = TrigSimplifyFu
        .tr8(F.Times(F.Cos(F.C2), F.Cos(F.C3), F.Cos(F.C4), F.Cos(F.C5), F.Cos(F.C6)), true);
    tr8 = F.eval(tr8);
    assertEquals(
        "1/16+3/16*Cos(2)+Cos(4)/8+Cos(6)/8+Cos(8)/8+Cos(10)/8+Cos(12)/16+Cos(14)/16+Cos(\n"
            + "16)/16+Cos(20)/16",
        tr8.toString());

    tr8 = TrigSimplifyFu.tr8(
        F.Times(F.Sqr(F.Sin(F.Times(F.QQ(3, 7), S.Pi))), F.Sqr(F.Cos(F.Times(F.QQ(3, 7), S.Pi))),
            F.Power(F.Times(16, F.Sqr(F.Sin(F.Times(F.QQ(1, 7), S.Pi)))), F.CN1)),
        true);
    assertEquals("1/64", tr8.toString());
  }

  @Test
  public void testTrigSimplifyTR9() {
    IExpr tr9 = TrigSimplifyFu.tr9(F.C1D2);
    assertEquals(F.C1D2.toString(), tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C1), F.Cos(F.C2)));
    tr9 = F.eval(tr9);
    assertEquals("2*Cos(1/2)*Cos(3/2)", tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Subtract(F.Cos(F.C1), F.Cos(F.C2)));
    tr9 = F.eval(tr9);
    assertEquals("2*Sin(1/2)*Sin(3/2)", tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Subtract(F.Sin(F.C1), F.Sin(F.C2)));
    tr9 = F.eval(tr9);
    assertEquals("-2*Cos(3/2)*Sin(1/2)", tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Sin(F.C1), F.Sin(F.C2)));
    tr9 = F.eval(tr9);
    assertEquals("2*Cos(1/2)*Sin(3/2)", tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C1), F.Times(2, F.Sin(F.C1)), F.Times(2, F.Sin(F.C2))));
    tr9 = F.eval(tr9);
    assertEquals("Cos(1)+4*Cos(1/2)*Sin(3/2)", tr9.toString());

    tr9 =
        TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C4), F.Cos(F.C2), F.Times(2, F.Cos(F.C1), F.Cos(F.C3))));
    tr9 = F.eval(tr9);
    assertEquals("4*Cos(1)*Cos(3)", tr9.toString());

    tr9 = TrigSimplifyFu
        .tr9(F.Plus(F.Times(F.C1D2, F.Plus(F.Cos(F.C4), F.Cos(F.C2)), F.Power(F.Cos(F.C3), F.CN1)),
            F.Cos(F.C3)));
    tr9 = F.eval(tr9);
    assertEquals("Cos(1)+Cos(3)", tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C3), F.Cos(F.C4), F.Cos(F.C5), F.Cos(F.C6)));
    tr9 = F.eval(tr9);
    assertEquals("4*Cos(1/2)*Cos(1)*Cos(9/2)", tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C3), F.Times(F.Cos(F.C3), F.Cos(F.C2))));
    tr9 = F.eval(tr9);
    assertEquals("Cos(3)+Cos(2)*Cos(3)", tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.y).negate(), F.Cos(F.Times(F.x, F.y))));
    tr9 = F.eval(tr9);
    assertEquals("-2*Sin(1/2*(-y+x*y))*Sin(1/2*(y+x*y))", tr9.toString());

    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Sin(F.y).negate(), F.Sin(F.Times(F.x, F.y))));
    tr9 = F.eval(tr9);
    assertEquals("2*Cos(1/2*(y+x*y))*Sin(1/2*(-y+x*y))", tr9.toString());
  }

  @Test
  public void testTrigSimplifyTR10() {
    IExpr tr10 = TrigSimplifyFu.tr10(F.Cos(F.Plus(F.a, F.b)));
    assertEquals("Cos(a)*Cos(b)-Sin(a)*Sin(b)", tr10.toString());

    tr10 = TrigSimplifyFu.tr10(F.Sin(F.Plus(F.a, F.b)));
    assertEquals("Cos(b)*Sin(a)+Cos(a)*Sin(b)", tr10.toString());

    tr10 = TrigSimplifyFu.tr10(F.Sin(F.Plus(F.a, F.b, F.c)));
    assertEquals(
        "Cos(c)*Cos(b)*Sin(a)+Cos(c)*Cos(a)*Sin(b)+Cos(a)*Cos(b)*Sin(c)+-Sin(a)*Sin(b)*Sin(c)",
        tr10.toString());
  }

  @Test
  public void testTrigSimplifyTR10i() {
    IExpr tr10i = TrigSimplifyFu
        .tr10i(F.Plus(F.Times(F.Cos(F.C1), F.Cos(F.C3)), F.Times(F.Sin(F.C1), F.Sin(F.C3))));
    tr10i = F.eval(tr10i);
    assertEquals("Cos(2)", tr10i.toString());

    tr10i = TrigSimplifyFu
        .tr10i(F.Subtract(F.Times(F.Cos(F.C1), F.Cos(F.C3)), F.Times(F.Sin(F.C1), F.Sin(F.C3))));
    assertEquals("Cos(4)", tr10i.toString());

    tr10i = TrigSimplifyFu
        .tr10i(F.Subtract(F.Times(F.Cos(F.C1), F.Sin(F.C3)), F.Times(F.Sin(F.C1), F.Cos(F.C3))));
    tr10i = F.eval(tr10i);
    assertEquals("Sin(2)", tr10i.toString());

    tr10i = TrigSimplifyFu
        .tr10i(F.Plus(F.Times(F.Cos(F.C1), F.Sin(F.C3)), F.Times(F.Sin(F.C1), F.Cos(F.C3))));
    assertEquals("Sin(4)", tr10i.toString());

    tr10i = TrigSimplifyFu
        .tr10i(F.Plus(F.C7, F.Times(F.Cos(F.C1), F.Sin(F.C3)), F.Times(F.Sin(F.C1), F.Cos(F.C3))));
    assertEquals("Sin(4)+7", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(
        F.Plus(F.Times(F.Cos(F.C1), F.Sin(F.C3)), F.Times(F.Sin(F.C1), F.Cos(F.C3)), F.Cos(F.C3)));
    assertEquals("Cos(3)+Sin(4)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(F.Times(F.C2, F.Cos(F.C1), F.Sin(F.C3)),
        F.Times(F.C2, F.Sin(F.C1), F.Cos(F.C3)), F.Cos(F.C3)));
    assertEquals("Cos(3)+2*Sin(4)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.Cos(F.C2), F.Cos(F.C3)), //
        F.Times(F.Sin(F.C2), //
            F.Plus(F.Times(F.Cos(F.C1), F.Sin(F.C2)), F.Times(F.Cos(F.C2), F.Sin(F.C1))))));
    tr10i = F.eval(tr10i);
    assertEquals("Cos(1)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.Plus(//
            F.Times(F.Cos(F.C2), F.Cos(F.C3)), //
            F.Times(F.Sin(F.C2), //
                F.Plus(F.Times(F.Cos(F.C1), F.Sin(F.C2)), F.Times(F.Cos(F.C2), F.Sin(F.C1))))),
            F.Cos(F.C5)), //
        F.Times(F.Sin(F.C1), F.Sin(F.C5))));
    tr10i = F.eval(tr10i);
    assertEquals("Cos(4)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.CSqrt2, F.Cos(F.x), F.x), //
        F.Times(F.CSqrt6, F.Sin(F.x), F.x)));
    assertEquals("2*Sqrt(2)*x*Sin(Pi/6+x)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.C1DSqrt6, F.Cos(F.x)), //
        F.Times(F.C1DSqrt2, F.Sin(F.x)), //
        F.Times(F.C1D3, F.C1DSqrt6, F.Cos(F.x)), //
        F.Times(F.C1D3, F.C1DSqrt2, F.Sin(F.x))));
    tr10i = F.eval(tr10i);
    assertEquals("4/3*Sqrt(2/3)*Sin(Pi/6+x)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.C1DSqrt6, F.Cos(F.x)), //
        F.Times(F.C1DSqrt2, F.Sin(F.x)), //
        F.Times(F.C1D3, F.C1DSqrt6, F.Cos(F.y)), //
        F.Times(F.C1D3, F.C1DSqrt2, F.Sin(F.y))));
    tr10i = F.eval(tr10i);
    assertEquals("Sqrt(2/3)*Sin(Pi/6+x)+1/3*Sqrt(2/3)*Sin(Pi/6+y)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Cos(F.x), //
        F.Times(F.CSqrt3, F.Sin(F.x)), //
        F.Times(F.C2, F.CSqrt3, F.Cos(F.Plus(F.x, F.Times(F.C1D6, S.Pi)))) //
    ));
    tr10i = F.eval(tr10i);
    assertEquals("4*Cos(x)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Cos(F.x), //
        F.Times(F.CSqrt3, F.Sin(F.x)), //
        F.Times(F.C2, F.CSqrt3, F.Cos(F.Plus(F.x, F.Times(F.C1D6, S.Pi)))), //
        F.Times(F.C4, F.Sin(F.x))));
    assertEquals("4*Cos(x)+4*Sin(x)", tr10i.toString());

    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.Cos(F.C3), F.Sin(F.C2)), //
        F.Times(F.Sin(F.C2), F.Cos(F.C4))//
    ));
    assertEquals("Cos(3)*Sin(2)+Cos(4)*Sin(2)", tr10i.toString());
  }

  @Test
  public void testTrigSimplifyTR11() {
    IExpr tr11 =
        TrigSimplifyFu.tr11(F.Divide(F.Sin(F.Times(F.C1D3, F.x)), F.Cos(F.Times(F.C1D6, F.x))));
    assertEquals("Sin(x/3)/Cos(x/6)", tr11.toString());

    tr11 = TrigSimplifyFu.tr11(F.Sin(F.Times(F.C2, F.x)));
    assertEquals("2*Cos(x)*Sin(x)", tr11.toString());

    tr11 = TrigSimplifyFu.tr11(F.Cos(F.Times(F.C2, F.x)));
    assertEquals("1-2*Sin(x)^2", tr11.toString());

    tr11 = TrigSimplifyFu.tr11(F.Sin(F.Times(F.C4, F.x)));
    tr11 = F.eval(tr11);
    assertEquals("4*Cos(x)*Sin(x)*(1-2*Sin(x)^2)", tr11.toString());
  }

  @Test
  public void testTrigSimplifyTR11Simple() {
    IExpr tr11 = TrigSimplifyFu.tr11(F.Cos(F.C2));
    assertEquals("Cos(2)", tr11.toString());
  }

  @Test
  public void testTrigSimplifySO24819() {
    IExpr tr11 = TrigSimplifyFu.tr11(F.Subtract(F.C2,
        F.Times(F.C2, F.Cos(F.Divide(F.Times(F.Times(F.C2, F.Pi), F.Subtract(F.x, F.y)), F.p)))));
    IExpr tr6 = TrigSimplifyFu.tr6(tr11);

    assertEquals("2-2*Cos((2*Pi*(x-y))/p)", tr6.toString());
  }

  @Test
  public void testTrigSimplifyTR12() {
    IExpr tr12 = TrigSimplifyFu.tr12(F.Tan(F.Plus(F.x, F.y)));
    assertEquals("(Tan(x)+Tan(y))/(1-Tan(x)*Tan(y))", tr12.toString());

    tr12 = TrigSimplifyFu.tr12(F.Tan(F.Plus(F.x, F.y, F.z)));
    assertEquals(
        "(Tan(x)+(Tan(y)+Tan(z))/(1-Tan(y)*Tan(z)))/(1+(-Tan(x)*(Tan(y)+Tan(z)))/(1-Tan(y)*Tan(z)))",
        tr12.toString());

    tr12 = TrigSimplifyFu.tr12(F.Tan(F.Times(F.x, F.y)));
    assertEquals("Tan(x*y)", tr12.toString());
  }

  @Test
  public void testTrigSimplifyTR13() {
    IExpr tr13 = TrigSimplifyFu.tr13(F.Times(F.Tan(F.C2), F.Tan(F.C3)));
    assertEquals("1+-Tan(2)/Tan(2+3)-Tan(3)/Tan(2+3)", tr13.toString());

    tr13 = F.eval(tr13);
    assertEquals("1-Cot(5)*Tan(2)-Cot(5)*Tan(3)", tr13.toString());

    tr13 = TrigSimplifyFu.tr13(F.Times(F.Cot(F.C2), F.Cot(F.C3)));
    assertEquals("1+Cot(2)*Cot(2+3)+Cot(3)*Cot(2+3)", tr13.toString());

    tr13 = F.eval(tr13);
    assertEquals("1+Cot(2)*Cot(5)+Cot(3)*Cot(5)", tr13.toString());

    tr13 = TrigSimplifyFu.tr13(F.Times(F.Tan(F.C1), F.Tan(F.C2), F.Tan(F.C3)));
    assertEquals("(1+-Tan(2)/Tan(2+3)-Tan(3)/Tan(2+3))*Tan(1)", tr13.toString());

    tr13 = F.eval(tr13);
    assertEquals("Tan(1)*(1-Cot(5)*Tan(2)-Cot(5)*Tan(3))", tr13.toString());
  }

  @Test
  public void testTrigSimplifyTR14() {
    IExpr eq = F.Times(F.Plus(F.CN1, F.Cos(F.x)), F.Plus(F.C1, F.Cos(F.x)));
    IExpr tr14 = TrigSimplifyFu.tr14(eq);
    tr14 = F.eval(tr14);
    assertEquals("-Sin(x)^2", tr14.toString());

    tr14 = TrigSimplifyFu.tr14(F.Power(eq, -1));
    tr14 = F.eval(tr14);
    assertEquals("-Csc(x)^2", tr14.toString());

    tr14 = TrigSimplifyFu.tr14(//
        F.Times(F.Power(F.Plus(F.CN1, F.Cos(F.x)), 2), F.Power(F.Plus(F.C1, F.Cos(F.x)), 2)));
    tr14 = F.eval(tr14);
    assertEquals("Sin(x)^4", tr14.toString());
  }

  @Test
  public void testTrigSplit() {
    IExpr trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Cos(F.y));
    assertEquals("{1,1,1,x,y,True}", trigSplit.toString());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.C2, F.Cos(F.x)), F.Times(F.CN2, F.Cos(F.y)));
    assertEquals("{2,1,-1,x,y,True}", trigSplit.toString());

    trigSplit =
        TrigSimplifyFu.trigSplit(F.Times(F.Cos(F.x), F.Sin(F.y)), F.Times(F.Cos(F.y), F.Sin(F.y)));
    assertEquals("{Sin(y),1,1,x,y,True}", trigSplit.toString());

    trigSplit =
        TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Times(F.Sqrt(F.C3).negate(), F.Sin(F.x)), true);
    assertEquals("{2,1,-1,x,Pi/6,False}", trigSplit.toString());

    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Sin(F.x), true);
    assertEquals("{Sqrt(2),1,1,x,Pi/4,False}", trigSplit.toString());

    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Sin(F.x).negate(), true);
    assertEquals("{Sqrt(2),1,-1,x,Pi/4,False}", trigSplit.toString());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(2), F.Cos(F.x)),
        F.Times(F.Sqrt(6).negate(), F.Sin(F.x)), true);
    assertEquals("{2*Sqrt(2),1,-1,x,Pi/6,False}", trigSplit.toString());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(6).negate(), F.Cos(F.x)),
        F.Times(F.Sqrt(2).negate(), F.Sin(F.x)), true);
    assertEquals("{-2*Sqrt(2),1,1,x,Pi/3,False}", trigSplit.toString());

    trigSplit = TrigSimplifyFu.trigSplit(F.Divide(F.Cos(F.x), F.Sqrt(6)),
        F.Divide(F.Sin(F.x), F.Sqrt(2)), true);
    assertEquals("{Sqrt(2/3),1,1,x,Pi/6,False}", trigSplit.toString());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(6).negate(), F.Cos(F.x), F.Sin(F.y)),
        F.Times(F.Sqrt(2).negate(), F.Sin(F.x), F.Sin(F.y)), true);
    assertEquals("{-2*Sqrt(2)*Sin(y),1,1,x,Pi/3,False}", trigSplit.toString());

    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Sin(F.x));
    assertTrue(trigSplit.isNIL());

    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Sin(F.z));
    assertTrue(trigSplit.isNIL());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(2, F.Cos(F.x)), F.Sin(F.x).negate());
    assertTrue(trigSplit.isNIL());

    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Times(F.Sqrt(3).negate(), F.Sin(F.x)));
    assertTrue(trigSplit.isNIL());

    trigSplit =
        TrigSimplifyFu.trigSplit(F.Times(F.Cos(F.x), F.Cos(F.y)), F.Times(F.Sin(F.x), F.Sin(F.z)));
    assertTrue(trigSplit.isNIL());

    trigSplit =
        TrigSimplifyFu.trigSplit(F.Times(F.Cos(F.x), F.Cos(F.y)), F.Times(F.Sin(F.x), F.Sin(F.y)));
    assertTrue(trigSplit.isNIL());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(6).negate(), F.Cos(F.x)),
        F.Times(F.Sqrt(2), F.Sin(F.x), F.Sin(F.y)), true);
    assertTrue(trigSplit.isNIL());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(3), F.Sqrt(F.x)), F.Cos(F.C3), true);
    assertTrue(trigSplit.isNIL());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(3), F.Power(F.x, F.C3)),
        F.Times(F.Sin(F.C3), F.Cos(F.C2)), true);
    assertTrue(trigSplit.isNIL());

    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Cos(F.C5), F.Cos(F.C6)),
        F.Times(F.Cos(F.C7), F.Sin(F.C5)), true);
    assertTrue(trigSplit.isNIL());
  }

  /**
   * Advanced TR14 tests: Asymmetrical factored powers of sin/cos identities. SymPy: test_TR14()
   */
  @Test
  public void testTrigSimplifyTR14Advanced() {
    // (cos(x) - 1)^2 * (cos(x) + 1)^3 == sin(x)^4 * (cos(x) + 1)
    IExpr eq1 =
        F.Times(F.Power(F.Plus(F.CN1, F.Cos(F.x)), 2), F.Power(F.Plus(F.C1, F.Cos(F.x)), 3));
    IExpr tr14_1 = TrigSimplifyFu.tr14(eq1);
    tr14_1 = F.eval(tr14_1);
    assertEquals("(1+Cos(x))*Sin(x)^4", tr14_1.toString());

    // (cos(x) - 1)^3 * (cos(x) + 1)^2 == sin(x)^4 * (cos(x) - 1)
    IExpr eq2 =
        F.Times(F.Power(F.Plus(F.CN1, F.Cos(F.x)), 3), F.Power(F.Plus(F.C1, F.Cos(F.x)), 2));
    IExpr tr14_2 = TrigSimplifyFu.tr14(eq2);
    tr14_2 = F.eval(tr14_2);
    assertEquals("(-1+Cos(x))*Sin(x)^4", tr14_2.toString());
  }

  /**
   * Advanced TR2i tests: Handling ratios with symbolic exponents and half-angles. SymPy:
   * test_TR2i()
   */
  @Test
  public void testTrigSimplifyTR2iAdvanced() {
    // (cos(1) + 1)^(-a) * sin(1)^a, half=True -> tan(1/2)^a
    IExpr expr1 =
        F.Times(F.Power(F.Plus(F.C1, F.Cos(F.C1)), F.Negate(F.a)), F.Power(F.Sin(F.C1), F.a));
    IExpr tr2i_1 = TrigSimplifyFu.tr2i(expr1, true);
    tr2i_1 = F.eval(tr2i_1);
    assertEquals("Tan(1/2)^a", tr2i_1.toString());

    // 1 / ((cos(5) + 1)^i * sin(5)^(-i)), half=True -> tan(5/2)^i
    ISymbol i = F.symbol("i", F.Element(F.Slot1, S.Integers));
    IExpr expr2 = F.Power(
        F.Times(F.Power(F.Plus(F.C1, F.Cos(F.C5)), i), F.Power(F.Sin(F.C5), F.Negate(i))), F.CN1);
    IExpr tr2i_2 = TrigSimplifyFu.tr2i(expr2, true);
    tr2i_2 = F.eval(tr2i_2);
    assertEquals("Tan(5/2)^i", tr2i_2.toString());
  }

  /**
   * Advanced TR3 tests: Resolving specific fractional pi phase shifts. SymPy: test_TR3()
   */
  @Test
  public void testTrigSimplifyTR3Advanced() {
    // cos(9*pi/22) -> sin(pi/11)
    // (Because 9/22 * pi = pi/2 - pi/11)
    IExpr tr3 = TrigSimplifyFu.tr3(F.Cos(F.Times(F.QQ(9, 22), S.Pi)));
    tr3 = F.eval(tr3);
    assertEquals("Sin(Pi/11)", tr3.toString());
  }

  /**
   * Advanced TR10 tests: 3-argument angle expansions. SymPy: test_TR10()
   */
  @Test
  public void testTrigSimplifyTR10Advanced() {
    // cos(a + b + c) expansion
    IExpr tr10 = TrigSimplifyFu.tr10(F.Cos(F.Plus(F.a, F.b, F.c)));

    // Note: The terms represent the expanded form of:
    // (-sin(a)*sin(b) + cos(a)*cos(b))*cos(c) - (sin(a)*cos(b) + sin(b)*cos(a))*sin(c)
    assertEquals(
        "Cos(c)*Cos(a)*Cos(b)+Cos(c)*-Sin(a)*Sin(b)-Cos(b)*Sin(a)*Sin(c)-Cos(a)*Sin(b)*Sin(c)",
        tr10.toString());
  }

  /**
   * Complex integration checks verifying that the full algorithm handles combinations of these
   * identities flawlessly without hanging.
   */
  @Test
  public void testTrigSimplifyFuSophisticated() {
    // TODO
    // Equivalent of TR15 / TR16 behavior on evaluated reciprocal powers
    // check("TrigSimplifyFu(1 - Csc(x)^2)", //
    // "-Cot(x)^2");
    // check("TrigSimplifyFu(1 - Sec(x)^2)", //
    // "-Tan(x)^2");

    // Testing the integration of complex powers (issue 17137)
    check("TrigSimplifyFu(Sin(x)^I)", //
        "Sin(x)^I");
    check("TrigSimplifyFu(Sin(x)^(1+2*I))", //
        "Sin(x)^(1+I*2)");


  }

  /** The JUnit setup method */
  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    EvalEngine.setReset(EvalEngine.get());
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
