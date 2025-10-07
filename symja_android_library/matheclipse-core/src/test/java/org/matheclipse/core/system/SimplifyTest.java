package org.matheclipse.core.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reflection.system.TrigSimplifyFu;

public class SimplifyTest extends ExprEvaluatorTestCase {

  /**
   * See: <a href=
   * "https://github.com/sympy/sympy/blob/master/sympy/simplify/tests/test_fu.py">sympy/simplify/tests/test_fu.py</a>
   */
  @Test
  public void testTrigSimplifyFu() {

    // CTR1 example
    check("TrigSimplifyFu(Sin(x)^4 - Cos(y)^2 + Sin(y)^2 + 2*Cos(x)^2)", //
        "2+Cos(x)^4-2*Cos(y)^2");

    check("TrigSimplifyFu(1/2 - Cos(2*x)/2)", //
        "Sin(x)^2");

    // CTR3 - Sympy gets other result:
    check("TrigSimplifyFu(Sin(a)*(Cos(b) - Sin(b)) + Cos(a)*(Sin(b) + Cos(b)))", //
        "Cos(a+b)+Sin(a+b)");

    // CTR4 sin(x + pi/3)
    check("TrigSimplifyFu(Sqrt(3)*Cos(x)/2 + Sin(x)/2)", //
        "Sin(Pi/3+x)");

    // Example 1 - Sympy gets other result:
    check("TrigSimplifyFu(1-Sin(2*x)^2/4-Sin(y)^2-Cos(x)^4)", //
        "1-Cos(x)^4-Sin(2*x)^2/4-Sin(y)^2");
    // "Sin(x)^2-Sin(y)^2");

    // Example 2 - TODO
    check("TrigSimplifyFu(Cos(4*Pi/9))", //
        "Cos(4/9*Pi)");

    check("TrigSimplifyFu(Cos(Pi/9)*Cos(2*Pi/9)*Cos(3*Pi/9)*Cos(4*Pi/9))", //
        "1/16");

    // sin(50)**2 + cos(50)**2 + sin(pi/6)
    check("TrigSimplifyFu(Sin(50)^2 + Cos(50)^2 + Sin(pi/6))", //
        "3/2");

    // sqrt(6)*cos(x) + sqrt(2)*sin(x)
    check("TrigSimplifyFu(Sqrt(6)*Cos(x) + Sqrt(2)*Sin(x))", //
        // sympy 2*sqrt(2)*sin(x + pi/3)
        "2*Sqrt(2)*Sin(Pi/3+x)");

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

    // sqrt(3)*cos(x)/2 + sin(x)/2
    check("TrigSimplifyFu(sqrt(3)*cos(x)/2 + sin(x)/2)", //
        // sympy sin(x + pi/3)
        "Sin(Pi/3+x)");

    // TODO 1 - sin(2*x)**2/4 - sin(y)**2 - cos(x)**4
    check("TrigSimplifyFu(1 - sin(2*x)^2/4 - sin(y)^2 - cos(x)^4)", //
        // sympy -cos(x)**2 + cos(y)**2
        "1-Cos(x)^4-Sin(2*x)^2/4-Sin(y)^2");
    // "Sin(x)^2-Sin(y)^2");

    // TODO cos(pi*Rational(4, 9))
    check("TrigSimplifyFu(Cos(Pi*4/9))", //
        // sympy sin(pi/18)
        "Cos(4/9*Pi)");

    // TODO cos(pi/9)*cos(pi*Rational(2, 9))*cos(pi*Rational(3, 9))*cos(pi*Rational(4, 9))
    check("TrigSimplifyFu(Cos(pi/9)*Cos(pi*2(9)*Cos(Pi*3/9))*Cos(Pi*4/9))", //
        // sympy Rational(1, 16)
        "-Cos(Pi/9)*Cos(4/9*Pi)");

    // tan(pi*Rational(7, 18)) + tan(pi*Rational(5, 18)) -
    // sqrt(3)*tan(pi*Rational(5, 18))*tan(pi*Rational(7, 18))
    check("TrigSimplifyFu(Tan(7/18*Pi) + Tan(5/18*Pi) - Sqrt(3)*Tan(5/18*Pi)*Tan(7/18*Pi))", //
        // sympy -sqrt(3)
        "-Sqrt(3)");

    // tan(1)*tan(2)
    check("TrigSimplifyFu(tan(1)*tan(2))", //
        "Tan(1)*Tan(2)");

    // Mul(*[cos(2**i) for i in range(10)])
    check("TrigSimplifyFu(Product(Cos(2^i),{i,0,9}))", //
        // sympy sin(1024)/(1024*sin(1))
        "1/1024*Csc(1)*Sin(1024)");

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

  @Test
  public void testTrigSimplifyFu002() {

    // assert fu(sin(50)**2 + cos(50)**2 + sin(pi/6)) == Rational(3, 2)
    check("TrigSimplifyFu(sin(50)^2 + cos(50)^2 + sin(pi/6))", //
        "3/2");

    // assert fu(sqrt(6)*cos(x) + sqrt(2)*sin(x)) == 2*sqrt(2)*sin(x + pi/3)
    check("TrigSimplifyFu(sqrt(6)*cos(x) + sqrt(2)*sin(x))", //
        "2*Sqrt(2)*Sin(Pi/3+x)");


    // eq = sin(x)**4 - cos(y)**2 + sin(y)**2 + 2*cos(x)**2
    // assert fu(eq) == cos(x)**4 - 2*cos(y)**2 + 2
    check("TrigSimplifyFu(sin(x)^4 - cos(y)^2 + sin(y)^2 + 2*cos(x)^2)", //
        "2+Cos(x)^4-2*Cos(y)^2");

    // assert fu(S.Half - cos(2*x)/2) == sin(x)**2
    check("TrigSimplifyFu(1/2 - cos(2*x)/2)", //
        "Sin(x)^2");

    // assert fu(sin(a)*(cos(b) - sin(b)) + cos(a)*(sin(b) + cos(b))) == \
    // sqrt(2)*sin(a + b + pi/4)
    check("TrigSimplifyFu(sin(a)*(cos(b) - sin(b)) + cos(a)*(sin(b) + cos(b)))", //
        // sympy - sqrt(2)*sin(a + b + pi/4)
        "Cos(a+b)+Sin(a+b)");

    // assert fu(sqrt(3)*cos(x)/2 + sin(x)/2) == sin(x + pi/3)
    check("TrigSimplifyFu(sqrt(3)*cos(x)/2 + sin(x)/2)", //
        "Sin(Pi/3+x)");

    // TODO assert fu(1 - sin(2*x)**2/4 - sin(y)**2 - cos(x)**4) == \
    // -cos(x)**2 + cos(y)**2
    check("TrigSimplifyFu(1 - sin(2*x)^2/4 - sin(y)^2 - cos(x)^4)", //
        // -cos(x)**2 + cos(y)**2
        "1-Cos(x)^4-Sin(2*x)^2/4-Sin(y)^2");

    // TODO assert fu(cos(pi*Rational(4, 9))) == sin(pi/18)
    check("TrigSimplifyFu(cos(pi*4/9))", //
        "Cos(4/9*Pi)");


    // assert fu(cos(pi/9)*cos(pi*Rational(2, 9))*cos(pi*Rational(3, 9))*cos(pi*Rational(4, 9))) ==
    // Rational(1, 16)
    check("TrigSimplifyFu(cos(pi/9)*cos(pi*2/9)*cos(pi*3/9)*cos(pi*4/9))", //
        "1/16");

    // assert fu(
    // tan(pi*Rational(7, 18)) + tan(pi*Rational(5, 18)) - sqrt(3)*tan(pi*Rational(5,
    // 18))*tan(pi*Rational(7, 18))) == \
    // -sqrt(3)
    check("TrigSimplifyFu(tan(pi*7/18) + tan(pi*5/18) - sqrt(3)*tan(pi* 5/18 )*tan(pi*7/18)  )", //
        "-Sqrt(3)");

    // assert fu(tan(1)*tan(2)) == tan(1)*tan(2)
    check("TrigSimplifyFu(tan(1)*tan(2))", //
        "Tan(1)*Tan(2)");

    // expr = Mul(*[cos(2**i) for i in range(10)])
    // assert fu(expr) == sin(1024)/(1024*sin(1))
    check("TrigSimplifyFu(Product(Cos(2^i),{i,0,9}))", //
        "1/1024*Csc(1)*Sin(1024)");

    // # issue #18059:
    // assert fu(cos(x) + sqrt(sin(x)**2)) == cos(x) + sqrt(sin(x)**2)
    check("TrigSimplifyFu(cos(x) + sqrt(sin(x)^2))", //
        "Cos(x)+Sqrt(Sin(x)^2)");

    // TODO assert fu((-14*sin(x)**3 + 35*sin(x) + 6*sqrt(3)*cos(x)**3 +
    // 9*sqrt(3)*cos(x))/((cos(2*x) + 4))) == \
    // 7*sin(x) + 3*sqrt(3)*cos(x)
    check(
        "TrigSimplifyFu((-14*sin(x)^3 + 35*sin(x) + 6*sqrt(3)*cos(x)^3 + 9*sqrt(3)*cos(x))/((cos(2*x)+4)))", //
        "(9*Sqrt(3)*Cos(x)+6*Sqrt(3)*Cos(x)^3+35*Sin(x)-14*Sin(x)^3)/(4+Cos(2*x))");
  }

  @Test
  public void testTrigSimplifyTRmorrie() {
    IExpr trMorrie = TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.x), F.Cos(F.Times(F.C2, F.x))));
    assertEquals(trMorrie.toString(), //
        "1/4*Csc(x)*Sin(4*x)");

    trMorrie = TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.x), F.Cos(F.Times(F.C2, F.x)),
        F.Cos(F.Times(F.C4, F.x)), F.Cos(F.Times(F.C6, F.x))));
    assertEquals(trMorrie.toString(), //
        "1/8*Cos(6*x)*Csc(x)*Sin(8*x)");

    // 7 + Mul(*[cos(i) for i in range(10)])
    IASTAppendable times = F.TimesAlloc(11);
    times.append(F.C7);
    for (int i = 0; i < 10; i++) {
      times.append(F.Cos(F.ZZ(i)));
    }
    trMorrie = TrigSimplifyFu.trMorrie(times);
    assertEquals(trMorrie.toString(), //
        "7/64*Cos(5)*Cos(7)*Cos(9)*Csc(1)*Csc(3)*Sin(12)*Sin(16)");

    // Mul(*[cos(2**i) for i in range(10)])
    times = F.TimesAlloc(10);
    for (int i = 0; i < 10; i++) {
      times.append(F.Cos(F.C2.pow(i)));
    }
    trMorrie = TrigSimplifyFu.trMorrie(times);
    assertEquals(trMorrie.toString(), //
        // sympy sin(1024)/(1024*sin(1))
        "1/1024*Csc(1)*Sin(1024)");

    trMorrie = TrigSimplifyFu.trMorrie(F.x);
    assertEquals(trMorrie.toString(), //
        "x");

    trMorrie = TrigSimplifyFu.trMorrie(F.Times(2, F.x));
    assertEquals(trMorrie.toString(), //
        "2*x");

    // cos(pi/7)*cos(pi*Rational(2, 7))*cos(pi*Rational(4, 7))
    trMorrie = TrigSimplifyFu.tr8(TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.Times(F.QQ(1, 7), S.Pi)),
        F.Cos(F.Times(F.QQ(2, 7), S.Pi)), F.Cos(F.Times(F.QQ(4, 7), S.Pi)))));
    assertEquals(trMorrie.toString(), //
        "-1/8");

    // e = Mul(*[cos(2**i*pi/17) for i in range(1, 17)])
    // assert TR8(TR3(TRmorrie(e))) == Rational(1, 65536)
    times = F.TimesAlloc(16);
    for (int i = 1; i < 17; i++) {
      times.append(F.Cos(F.C2.pow(i).divide(F.ZZ(17)).multiply(S.Pi)));
    }
    trMorrie = TrigSimplifyFu.tr8(TrigSimplifyFu.tr3(TrigSimplifyFu.trMorrie(times)));
    assertEquals(trMorrie.toString(), //
        // sympy Rational(1, 65536)
        "1/65536");

    // # issue 17063
    // eq = cos(x)/cos(x/2)
    trMorrie = TrigSimplifyFu.trMorrie(F.Divide(F.Cos(F.x), F.Cos(F.Times(F.C1D2, F.x))));
    assertEquals(trMorrie.toString(), //
        "Cos(x)/Cos(x/2)");

    // TODO ?
    // # issue #20430
    // eq = cos(x/2)*sin(x/2)*cos(x)**3
    // assert TRmorrie(eq) == sin(2*x)*cos(x)**2/4
    trMorrie = TrigSimplifyFu.trMorrie(F.Times(F.Cos(F.Times(F.C1D2, F.x)),
        F.Sin(F.Times(F.C1D2, F.x)), F.Power(F.Cos(F.x), F.C3)));
    assertEquals(trMorrie.toString(), //
        // sympy sin(2*x)*cos(x)**2/4
        "1/64*Csc(x/2)^2*Sec(x/2)^2*Sin(2*x)^3");

  }

  @Test
  public void testTrigSimplifyTR1() {
    // 2*csc(x) + sec(x)
    IExpr tr1 = TrigSimplifyFu.tr1(F.Plus(F.Times(F.C2, F.Csc(F.x)), F.Sec(F.x)));
    assertEquals(tr1.toString(), //
        "2/Sin(x)+1/Cos(x)");
  }

  @Test
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
        "Sin(-Sin(x)/Cos(x)+Sin(x)/Cos(x))/Cos(-Sin(x)/Cos(x)+Sin(x)/Cos(x))");
    tr2 = F.eval(tr2);
    assertEquals(tr2.toString(), //
        "0");
  }

  @Test
  public void testTrigSimplifyTR2i() {
    IExpr tr2i = TrigSimplifyFu
        .tr2i(F.Divide(F.Power(F.Sin(F.x), F.C2), F.Power(F.Plus(F.Cos(F.x), F.C1), F.C2)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(x/2)^2");


    // sin(x)/cos(x)
    tr2i = TrigSimplifyFu.tr2i(F.Divide(F.Sin(F.x), F.Cos(F.x)), false);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(x)");

    // sin(x)*sin(y)/cos(x)
    tr2i = TrigSimplifyFu.tr2i(F.Divide(F.Times(F.Sin(F.x), F.Sin(F.y)), F.Cos(F.x)), false);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Sin(y)*Tan(x)");

    // 1/(sin(x)/cos(x))
    tr2i = TrigSimplifyFu.tr2i(F.Power(F.Divide(F.Sin(F.x), F.Cos(F.x)), F.CN1), false);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy 1/tan(x)
        "Cot(x)");

    // 1/(sin(x)*sin(y)/cos(x))
    tr2i = TrigSimplifyFu
        .tr2i(F.Power(F.Divide(F.Times(F.Sin(F.x), F.Sin(F.y)), F.Cos(F.x)), F.CN1), false);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy sin(x)/(cos(x) + 1)/2
        "Cot(x)*Csc(y)");


    // sin(x)/2/(cos(x) + 1), half=True
    tr2i = TrigSimplifyFu
        .tr2i(F.Times(F.C1D2, F.Sin(F.x), F.Power(F.Plus(F.C1, F.Cos(F.x)), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(x/2)/2");

    // sin(1)/(cos(1) + 1), half=True
    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Sin(F.C1), F.Power(F.Plus(F.C1, F.Cos(F.C1)), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(1/2)");

    // sin(2)/(cos(2) + 1), half=True
    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Sin(F.C2), F.Power(F.Plus(F.C1, F.Cos(F.C2)), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(1)");


    // sin(2)/(cos(2) + 1), half=True
    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Sin(F.C4), F.Power(F.Plus(F.C1, F.Cos(F.C4)), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(2)");

    // sin(5)/(cos(5) + 1), half=True
    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Sin(F.C5), F.Power(F.Plus(F.C1, F.Cos(F.C5)), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(5/2)");

    // (cos(1) + 1)/sin(1), half=True
    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Plus(F.C1, F.Cos(F.C1)), F.Power(F.Sin(F.C1), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy 1/tan(S.Half)
        "Cot(1/2)");

    // (cos(2) + 1)/sin(2), half=True
    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Plus(F.C1, F.Cos(F.C2)), F.Power(F.Sin(F.C2), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy 1/tan(1)
        "Cot(1)");

    // (cos(4) + 1)/sin(4), half=True
    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Plus(F.C1, F.Cos(F.C4)), F.Power(F.Sin(F.C4), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy 1/tan(1)
        "Cot(2)");

    // (cos(5) + 1)/sin(5), half=True
    tr2i =
        TrigSimplifyFu.tr2i(F.Times(F.Plus(F.C1, F.Cos(F.C5)), F.Power(F.Sin(F.C5), F.CN1)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy 1/tan(5*S.Half)
        "Cot(5/2)");

    // (cos(1) + 1)**(-a)*sin(1)**a, half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C1)), F.Negate(F.a)), //
        F.Power(F.Sin(F.C1), F.a)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(1/2)^a");

    // (cos(2) + 1)**(-a)*sin(2)**a, half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C2)), F.Negate(F.a)), //
        F.Power(F.Sin(F.C2), F.a)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        "Tan(1)^a");

    // (cos(4) + 1)**(-a)*sin(4)**a, half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C4)), F.Negate(F.a)), //
        F.Power(F.Sin(F.C4), F.a)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy (cos(4) + 1)**(-a)*sin(4)**a
        "Sin(4)^a/(1+Cos(4))^a");

    // (cos(5) + 1)**(-a)*sin(5)**a, half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C5)), F.Negate(F.a)), //
        F.Power(F.Sin(F.C5), F.a)), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy (cos(5) + 1)**(-a)*sin(5)**a
        "Sin(5)^a/(1+Cos(5))^a");

    // (cos(1) + 1)**a*sin(1)**(-a), half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C1)), F.a), //
        F.Power(F.Sin(F.C1), F.Negate(F.a))), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy tan(S.Half)**(-a)
        "Tan(1/2)^(-a)");

    // (cos(2) + 1)**a*sin(2)**(-a), half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C2)), F.a), //
        F.Power(F.Sin(F.C2), F.Negate(F.a))), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy tan(1)**(-a)
        "Tan(1)^(-a)");

    // (cos(4) + 1)**a*sin(4)**(-a), half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C4)), F.a), //
        F.Power(F.Sin(F.C4), F.Negate(F.a))), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy (cos(4) + 1)**a*sin(4)**(-a)
        "(1+Cos(4))^a/Sin(4)^a");

    // (cos(5) + 1)**a*sin(5)**(-a), half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C5)), F.a), //
        F.Power(F.Sin(F.C5), F.Negate(F.a))), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy (cos(5) + 1)**a*sin(5)**(-a)
        "(1+Cos(5))^a/Sin(5)^a");

    // i = symbols('i', integer=True)
    ISymbol i = F.symbol("i", F.Element(F.Slot1, S.Integers));
    // ((cos(5) + 1)**i*sin(5)**(-i)), half=True
    tr2i = TrigSimplifyFu.tr2i(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C5)), i), //
        F.Power(F.Sin(F.C5), F.Negate(i))), true);

    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy tan(5*S.Half)**(-i)
        "Tan(5/2)^(-i)");

    // 1/((cos(5) + 1)**i*sin(5)**(-i)), half=True
    tr2i = TrigSimplifyFu.tr2i(F.Power(F.Times(//
        F.Power(F.Plus(F.C1, F.Cos(F.C5)), i), //
        F.Power(F.Sin(F.C5), F.Negate(i))), F.CN1), true);
    tr2i = F.eval(tr2i);
    assertEquals(tr2i.toString(), //
        // sympy tan(5*S.Half)**i
        "Tan(5/2)^i");
  }

  @Test
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

  @Test
  public void testTrigSimplifyTR5() {
    IExpr tr5 = TrigSimplifyFu.tr5(F.Power(F.Sin(F.x), F.C2));
    // tr5 = F.eval(tr5);
    assertEquals(tr5.toString(), //
        "1-Cos(x)^2");

    tr5 = TrigSimplifyFu.tr5(F.Power(F.Sin(F.x), F.CN2));
    // tr5 = F.eval(tr5);
    assertEquals(tr5.toString(), //
        "1/Sin(x)^2");

    tr5 = TrigSimplifyFu.tr5(F.Power(F.Sin(F.x), F.C4));
    // tr5 = F.eval(tr5);
    assertEquals(tr5.toString(), //
        "(1-Cos(x)^2)^2");

  }

  @Test
  public void testTrigSimplifyTR6() {
    // assert TR6(cos(x)**2) == -sin(x)**2 + 1
    IExpr tr6 = TrigSimplifyFu.tr6(F.Power(F.Cos(F.x), F.C2));
    // tr6 = F.eval(tr6);
    assertEquals(tr6.toString(), //
        "1-Sin(x)^2");

    // assert TR6(cos(x)**-2) == cos(x)**(-2)
    tr6 = TrigSimplifyFu.tr6(F.Power(F.Cos(F.x), F.CN2));
    // tr6 = F.eval(tr6);
    assertEquals(tr6.toString(), //
        "1/Cos(x)^2");

    // assert TR6(cos(x)**4) == (-sin(x)**2 + 1)**2
    tr6 = TrigSimplifyFu.tr6(F.Power(F.Cos(F.x), F.C4));
    // tr6 = F.eval(tr6);
    assertEquals(tr6.toString(), //
        "(1-Sin(x)^2)^2");

  }

  @Test
  public void testTrigSimplifyTR56() {
    // h = lambda x: 1 - x
    Function<IExpr, IExpr> h = x -> F.C1.subtract(x);

    // assert T(sin(x)**3, sin, cos, h, 4, False) == sin(x)*(-cos(x)**2 + 1)
    IExpr tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C3), F.Sin, F.Cos, h, F.C4, false);
    assertEquals(tr56.toString(), //
        "(1-Cos(x)^2)*Sin(x)^2");

    // assert T(sin(x)**10, sin, cos, h, 4, False) == sin(x)**10
    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C10), F.Sin, F.Cos, h, F.C4, false);
    assertEquals(tr56.toString(), //
        "Sin(x)^10");

    // assert T(sin(x)**6, sin, cos, h, 6, False) == (-cos(x)**2 + 1)**3
    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C6), F.Sin, F.Cos, h, F.C6, false);
    // tr56 = F.eval(tr56);
    assertEquals(tr56.toString(), //
        "(1-Cos(x)^2)^3");

    // assert T(sin(x)**6, sin, cos, h, 6, True) == sin(x)**6
    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C6), F.Sin, F.Cos, h, F.C6, true);
    // tr56 = F.eval(tr56);
    assertEquals(tr56.toString(), //
        "Sin(x)^6");

    // assert T(sin(x)**8, sin, cos, h, 10, True) == (-cos(x)**2 + 1)**4
    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.C8), F.Sin, F.Cos, h, F.C10, true);
    // tr56 = F.eval(tr56);
    assertEquals(tr56.toString(), //
        "(1-Cos(x)^2)^4");

    // assert T(sin(x)**I, sin, cos, h, 4, True) == sin(x)**I
    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.CI), F.Sin, F.Cos, h, F.C4, true);
    // tr56 = F.eval(tr56);
    assertEquals(tr56.toString(), //
        "Sin(x)^I");

    // assert T(sin(x)**(2*I + 1), sin, cos, h, 4, True) == sin(x)**(2*I + 1)
    tr56 = TrigSimplifyFu.tr56(F.Power(F.Sin(F.x), F.Plus(F.Times(F.C2, F.CI), F.C1)), //
        F.Sin, F.Cos, h, F.C4, true);
    // tr56 = F.eval(tr56);
    assertEquals(tr56.toString(), //
        "Sin(x)^(1+I*2)");
  }

  @Test
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

  @Test
  public void testTrigSimplifyTR8() {
    IExpr tr8 = TrigSimplifyFu.tr8(F.Times(F.Cos(F.C2), F.Cos(F.C3)), true);
    assertEquals(tr8.toString(), //
        "1/2*(Cos(1)+Cos(5))");

    tr8 = TrigSimplifyFu.tr8(F.Times(F.Cos(F.C2), F.Sin(F.C3)), true);
    assertEquals(tr8.toString(), //
        "1/2*(Sin(1)+Sin(5))");

    tr8 = TrigSimplifyFu.tr8(F.Times(F.Sin(F.C2), F.Sin(F.C3)), true);
    assertEquals(tr8.toString(), //
        "1/2*(Cos(1)-Cos(5))");

    // sin(1)*sin(2)*sin(3)
    tr8 = TrigSimplifyFu.tr8(F.Times(F.Sin(F.C1), F.Sin(F.C2), F.Sin(F.C3)), true);
    assertEquals(tr8.toString(), //
        // sympy sin(4)/4 - sin(6)/4 + sin(2)/4)
        "Sin(2)/4+Sin(4)/4-Sin(6)/4");

    // cos(2)*cos(3)*cos(4)*cos(5)
    tr8 = TrigSimplifyFu.tr8(F.Times(F.Cos(F.C2), F.Cos(F.C3), F.Cos(F.C4), F.Cos(F.C5)), true);
    assertEquals(tr8.toString(), //
        // sympy cos(4)/4 + cos(10)/8 + cos(2)/8 + cos(8)/8 + cos(14)/8 + cos(6)/8 + Rational(1, 8)
        "1/8+Cos(2)/8+Cos(4)/4+Cos(6)/8+Cos(8)/8+Cos(10)/8+Cos(14)/8");

    // cos(2)*cos(3)*cos(4)*cos(5)*cos(6)
    tr8 = TrigSimplifyFu
        .tr8(F.Times(F.Cos(F.C2), F.Cos(F.C3), F.Cos(F.C4), F.Cos(F.C5), F.Cos(F.C6)), true);
    tr8 = F.eval(tr8);
    assertEquals(tr8.toString(), //
        // sympy cos(10)/8 + cos(4)/8 + 3*cos(2)/16 + cos(16)/16 + cos(8)/8 + cos(14)/16 +
        // cos(20)/16 + cos(12)/16 + Rational(1, 16) + cos(6)/8
        "1/16+3/16*Cos(2)+Cos(4)/8+Cos(6)/8+Cos(8)/8+Cos(10)/8+Cos(12)/16+Cos(14)/16+Cos(\n"
            + "16)/16+Cos(20)/16");

    // sin(pi*Rational(3, 7))**2*cos(pi*Rational(3, 7))**2/(16*sin(pi/7)**2)
    tr8 = TrigSimplifyFu.tr8(
        F.Times(F.Sqr(F.Sin(F.Times(F.QQ(3, 7), S.Pi))), F.Sqr(F.Cos(F.Times(F.QQ(3, 7), S.Pi))),
            F.Power(F.Times(16, F.Sqr(F.Sin(F.Times(F.QQ(1, 7), S.Pi)))), F.CN1)),
        true);
    assertEquals(tr8.toString(), //
        // sympy Rational(1, 64)
        "1/64");

  }

  @Test
  public void testTrigSimplifyTR9() {
    ISymbol a = F.Dummy("a");
    a.assignValue(F.C1D2);
    ISymbol b = F.Dummy("a");
    b.assignValue(F.C3D2);
    IExpr tr9 = TrigSimplifyFu.tr9(a);
    assertEquals(tr9.toString(), //
        a.toString());

    // cos(1) + cos(2)
    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C1), F.Cos(F.C2)));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // 2*cos(a)*cos(b)
        "2*Cos(1/2)*Cos(3/2)");

    // cos(1) - cos(2)
    tr9 = TrigSimplifyFu.tr9(F.Subtract(F.Cos(F.C1), F.Cos(F.C2)));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // 2*sin(a)*sin(b)
        "2*Sin(1/2)*Sin(3/2)");

    // sin(1) - sin(2)
    tr9 = TrigSimplifyFu.tr9(F.Subtract(F.Sin(F.C1), F.Sin(F.C2)));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // -2*sin(a)*cos(b)
        "-2*Cos(3/2)*Sin(1/2)");

    // sin(1) + sin(2)
    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Sin(F.C1), F.Sin(F.C2)));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // 2*sin(b)*cos(a)
        "2*Cos(1/2)*Sin(3/2)");

    // cos(1) + 2*sin(1) + 2*sin(2)
    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C1), F.Times(2, F.Sin(F.C1)), F.Times(2, F.Sin(F.C2))));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // cos(1) + 4*sin(b)*cos(a)
        "Cos(1)+4*Cos(1/2)*Sin(3/2)");

    // cos(4) + cos(2) + 2*cos(1)*cos(3)
    tr9 =
        TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C4), F.Cos(F.C2), F.Times(2, F.Cos(F.C1), F.Cos(F.C3))));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // 4*cos(1)*cos(3)
        "4*Cos(1)*Cos(3)");

    // (cos(4) + cos(2))/cos(3)/2 + cos(3)
    tr9 = TrigSimplifyFu
        .tr9(F.Plus(F.Times(F.C1D2, F.Plus(F.Cos(F.C4), F.Cos(F.C2)), F.Power(F.Cos(F.C3), F.CN1)),
            F.Cos(F.C3)));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // sympy - 2*cos(1)*cos(2)
        "Cos(1)+Cos(3)");

    // cos(3) + cos(4) + cos(5) + cos(6)
    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C3), F.Cos(F.C4), F.Cos(F.C5), F.Cos(F.C6)));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // 4*cos(S.Half)*cos(1)*cos(Rational(9, 2))
        "4*Cos(1/2)*Cos(1)*Cos(9/2)");

    // cos(3) + cos(3)*cos(2)
    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.C3), F.Times(F.Cos(F.C3), F.Cos(F.C2))));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // cos(3) + cos(2)*cos(3)
        "Cos(3)+Cos(2)*Cos(3)");

    // -cos(y) + cos(x*y)
    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Cos(F.y).negate(), F.Cos(F.Times(F.x, F.y))));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // -2*sin(x*y/2 - y/2)*sin(x*y/2 + y/2)
        "-2*Sin(1/2*(-y+x*y))*Sin(1/2*(y+x*y))");

    // -sin(y) + sin(x*y)
    tr9 = TrigSimplifyFu.tr9(F.Plus(F.Sin(F.y).negate(), F.Sin(F.Times(F.x, F.y))));
    tr9 = F.eval(tr9);
    assertEquals(tr9.toString(), //
        // 2*sin(x*y/2 - y/2)*cos(x*y/2 + y/2)
        "2*Cos(1/2*(y+x*y))*Sin(1/2*(-y+x*y))");
  }

  @Test
  public void testTrigSimplifyTR10() {
    IExpr tr10 = TrigSimplifyFu.tr10(F.Cos(F.Plus(F.a, F.b)));
    assertEquals(tr10.toString(), //
        "Cos(a)*Cos(b)-Sin(a)*Sin(b)");

    tr10 = TrigSimplifyFu.tr10(F.Sin(F.Plus(F.a, F.b)));
    assertEquals(tr10.toString(), //
        "Cos(b)*Sin(a)+Cos(a)*Sin(b)");

    tr10 = TrigSimplifyFu.tr10(F.Sin(F.Plus(F.a, F.b, F.c)));
    assertEquals(tr10.toString(), //
        "Cos(c)*Cos(b)*Sin(a)+Cos(c)*Cos(a)*Sin(b)+Cos(a)*Cos(b)*Sin(c)+-Sin(a)*Sin(b)*Sin(c)");
  }

  @Test
  public void testTrigSimplifyTR10i() {
    // cos(1)*cos(3) + sin(1)*sin(3)
    IExpr tr10i = TrigSimplifyFu
        .tr10i(F.Plus(F.Times(F.Cos(F.C1), F.Cos(F.C3)), F.Times(F.Sin(F.C1), F.Sin(F.C3))));
    tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // cos(2)
        "Cos(2)");


    // cos(1)*cos(3) - sin(1)*sin(3)
    tr10i = TrigSimplifyFu
        .tr10i(F.Subtract(F.Times(F.Cos(F.C1), F.Cos(F.C3)), F.Times(F.Sin(F.C1), F.Sin(F.C3))));
    assertEquals(tr10i.toString(), //
        // cos(4)
        "Cos(4)");

    // cos(1)*sin(3) - sin(1)*cos(3)
    tr10i = TrigSimplifyFu
        .tr10i(F.Subtract(F.Times(F.Cos(F.C1), F.Sin(F.C3)), F.Times(F.Sin(F.C1), F.Cos(F.C3))));
    tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // sin(2)
        "Sin(2)");

    // cos(1)*sin(3) + sin(1)*cos(3)
    tr10i = TrigSimplifyFu
        .tr10i(F.Plus(F.Times(F.Cos(F.C1), F.Sin(F.C3)), F.Times(F.Sin(F.C1), F.Cos(F.C3))));
    assertEquals(tr10i.toString(), //
        // sin(4)
        "Sin(4)");


    // cos(1)*sin(3) + sin(1)*cos(3) + 7
    tr10i = TrigSimplifyFu
        .tr10i(F.Plus(F.C7, F.Times(F.Cos(F.C1), F.Sin(F.C3)), F.Times(F.Sin(F.C1), F.Cos(F.C3))));
    assertEquals(tr10i.toString(), //
        // sin(4) + 7
        "Sin(4)+7");

    // cos(1)*sin(3) + sin(1)*cos(3) + cos(3)
    tr10i = TrigSimplifyFu.tr10i(
        F.Plus(F.Times(F.Cos(F.C1), F.Sin(F.C3)), F.Times(F.Sin(F.C1), F.Cos(F.C3)), F.Cos(F.C3)));
    assertEquals(tr10i.toString(), //
        // cos(3) + sin(4)
        "Cos(3)+Sin(4)");

    // 2*cos(1)*sin(3) + 2*sin(1)*cos(3) + cos(3)
    tr10i = TrigSimplifyFu.tr10i(F.Plus(F.Times(F.C2, F.Cos(F.C1), F.Sin(F.C3)),
        F.Times(F.C2, F.Sin(F.C1), F.Cos(F.C3)), F.Cos(F.C3)));
    assertEquals(tr10i.toString(), //
        // 2*sin(4) + cos(3)
        "Cos(3)+2*Sin(4)");

    // cos(2)*cos(3) + sin(2)*(cos(1)*sin(2) + cos(2)*sin(1))
    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.Cos(F.C2), F.Cos(F.C3)), //
        F.Times(F.Sin(F.C2), //
            F.Plus(F.Times(F.Cos(F.C1), F.Sin(F.C2)), F.Times(F.Cos(F.C2), F.Sin(F.C1))))));
    tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // cos(1)
        "Cos(1)");

    // eq = (cos(2)*cos(3) + sin(2)*(cos(1)*sin(2) + cos(2)*sin(1)))*cos(5) + sin(1)*sin(5)
    // assert TR10i(eq) == TR10i(eq.expand()) == cos(4)
    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.Plus(//
            F.Times(F.Cos(F.C2), F.Cos(F.C3)), //
            F.Times(F.Sin(F.C2), //
                F.Plus(F.Times(F.Cos(F.C1), F.Sin(F.C2)), F.Times(F.Cos(F.C2), F.Sin(F.C1))))),
            F.Cos(F.C5)), //
        F.Times(F.Sin(F.C1), F.Sin(F.C5))));
    tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // cos(4)
        "Cos(4)");

    // assert TR10i(sqrt(2)*cos(x)*x + sqrt(6)*sin(x)*x) == \
    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.CSqrt2, F.Cos(F.x), F.x), //
        F.Times(F.CSqrt6, F.Sin(F.x), F.x)));
    // tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // 2*sqrt(2)*x*sin(x + pi/6)
        "2*Sqrt(2)*x*Sin(Pi/6+x)");

    // assert TR10i(cos(x)/sqrt(6) + sin(x)/sqrt(2) +
    // cos(x)/sqrt(6)/3 + sin(x)/sqrt(2)/3) == 4*sqrt(6)*sin(x + pi/6)/9
    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.C1DSqrt6, F.Cos(F.x)), //
        F.Times(F.C1DSqrt2, F.Sin(F.x)), //
        F.Times(F.C1D3, F.C1DSqrt6, F.Cos(F.x)), //
        F.Times(F.C1D3, F.C1DSqrt2, F.Sin(F.x))));
    tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // 4*sqrt(6)*sin(x + pi/6)/9
        "4/3*Sqrt(2/3)*Sin(Pi/6+x)");

    // // assert TR10i(cos(x)/sqrt(6) + sin(x)/sqrt(2) +
    // // cos(y)/sqrt(6)/3 + sin(y)/sqrt(2)/3) == \
    // // sqrt(6)*sin(x + pi/6)/3 + sqrt(6)*sin(y + pi/6)/9
    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.C1DSqrt6, F.Cos(F.x)), //
        F.Times(F.C1DSqrt2, F.Sin(F.x)), //
        F.Times(F.C1D3, F.C1DSqrt6, F.Cos(F.y)), //
        F.Times(F.C1D3, F.C1DSqrt2, F.Sin(F.y))));
    tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // sqrt(6)*sin(x + pi/6)/3 + sqrt(6)*sin(y + pi/6)/9
        "Sqrt(2/3)*Sin(Pi/6+x)+1/3*Sqrt(2/3)*Sin(Pi/6+y)");


    // assert TR10i(cos(x) + sqrt(3)*sin(x) + 2*sqrt(3)*cos(x + pi/6)) == 4*cos(x)
    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Cos(F.x), //
        F.Times(F.CSqrt3, F.Sin(F.x)), //
        F.Times(F.C2, F.CSqrt3, F.Cos(F.Plus(F.x, F.Times(F.C1D6, S.Pi)))) //
    ));
    tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // 4*cos(x)
        "4*Cos(x)");

    // assert TR10i(cos(x) + sqrt(3)*sin(x) +
    // 2*sqrt(3)*cos(x + pi/6) + 4*sin(x)) == 4*sqrt(2)*sin(x + pi/4)
    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Cos(F.x), //
        F.Times(F.CSqrt3, F.Sin(F.x)), //
        F.Times(F.C2, F.CSqrt3, F.Cos(F.Plus(F.x, F.Times(F.C1D6, S.Pi)))), //
        F.Times(F.C4, F.Sin(F.x))));
    // tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // sympy 4*sqrt(2)*sin(x + pi/4)
        "4*Cos(x)+4*Sin(x)");

    // assert TR10i(cos(2)*sin(3) + sin(2)*cos(4)) == \
    // sin(2)*cos(4) + sin(3)*cos(2)
    tr10i = TrigSimplifyFu.tr10i(F.Plus(//
        F.Times(F.Cos(F.C3), F.Sin(F.C2)), //
        F.Times(F.Sin(F.C2), F.Cos(F.C4))//
    ));
    // tr10i = F.eval(tr10i);
    assertEquals(tr10i.toString(), //
        // sin(2)*cos(4) + sin(3)*cos(2)
        "Cos(3)*Sin(2)+Cos(4)*Sin(2)");

    // A = Symbol('A', commutative=False)
    // assert TR10i(sqrt(2)*cos(x)*A + sqrt(6)*sin(x)*A) == \
    // 2*sqrt(2)*sin(x + pi/6)*A
    //
    //
    // c = cos(x)
    // s = sin(x)
    // h = sin(y)
    // r = cos(y)
    // for si in ((1, 1), (1, -1), (-1, 1), (-1, -1)):
    // for argsi in ((c*r, s*h), (c*h, s*r)): # explicit 2-args
    // args = zip(si, argsi)
    // ex = Add(*[Mul(*ai) for ai in args])
    // t = TR10i(ex)
    // assert not (ex - t.expand(trig=True) or t.is_Add)
    //
    // c = cos(x)
    // s = sin(x)
    // h = sin(pi/6)
    // r = cos(pi/6)
    // for si in ((1, 1), (1, -1), (-1, 1), (-1, -1)):
    // for argsi in ((c*r, s*h), (c*h, s*r)): # induced
    // args = zip(si, argsi)
    // ex = Add(*[Mul(*ai) for ai in args])
    // t = TR10i(ex)
    // assert not (ex - t.expand(trig=True) or t.is_Add)
  }

  @Test
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

  @Test
  public void testTrigSimplifyTR11Simple() {
    IExpr tr11 = TrigSimplifyFu.tr11(F.Cos(F.C2));
    assertEquals(tr11.toString(), //
        "Cos(2)");

    // TODO use base to get -Sin(2)^2 + Cos(2)**2
    // tr11 = TrigSimplifyFu.tr11(F.Cos(F.C2), 2);
    // assertEquals(tr11.toString(), //
    // " ");
  }

  @Test
  public void testTrigSimplifySO24819() {
    // https://stackoverflow.com/a/79095795/24819
    // TR6(TR11(S('2 - 2*cos(2*pi*(x - y)/p)')))
    IExpr tr11 = TrigSimplifyFu.tr11(F.Subtract(F.C2,
        F.Times(F.C2, F.Cos(F.Divide(F.Times(F.Times(F.C2, F.Pi), F.Subtract(F.x, F.y)), F.p)))));
    IExpr tr6 = TrigSimplifyFu.tr6(tr11);
    // TODO
    assertEquals(tr6.toString(), //
        "2-2*Cos((2*Pi*(x-y))/p)");
  }

  @Test
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
        "(Tan(x)+(Tan(y)+Tan(z))/(1-Tan(y)*Tan(z)))/(1+(-Tan(x)*(Tan(y)+Tan(z)))/(1-Tan(y)*Tan(z)))");

    // tan(x * y)
    tr12 = TrigSimplifyFu.tr12(F.Tan(F.Times(F.x, F.y)));
    assertEquals(tr12.toString(), //
        "Tan(x*y)");

  }

  @Test
  public void testTrigSimplifyTR13() {
    // tan(2)*tan(3)
    IExpr tr13 = TrigSimplifyFu.tr13(F.Times(F.Tan(F.C2), F.Tan(F.C3)));
    // sympy -tan(2)/tan(5) - tan(3)/tan(5) + 1
    assertEquals(tr13.toString(), //
        "1+-Tan(2)/Tan(2+3)-Tan(3)/Tan(2+3)");
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
        "(1+-Tan(2)/Tan(2+3)-Tan(3)/Tan(2+3))*Tan(1)");
    tr13 = F.eval(tr13);
    assertEquals(tr13.toString(), //
        "Tan(1)*(1-Cot(5)*Tan(2)-Cot(5)*Tan(3))");
  }

  @Test
  public void testTrigSimplifyTR14() {
    // eq = (cos(x) - 1)*(cos(x) + 1)
    IExpr eq = F.Times(F.Plus(F.CN1, F.Cos(F.x)), F.Plus(F.C1, F.Cos(F.x)));
    IExpr ans = F.Power(F.Sin(F.x), F.C2).negate();
    IExpr tr14 = TrigSimplifyFu.tr14(eq);
    tr14 = F.eval(tr14);
    assertEquals(tr14.toString(), //
        // -sin(x)**2
        "-Sin(x)^2");

    tr14 = TrigSimplifyFu.tr14(F.Power(eq, -1));
    tr14 = F.eval(tr14);
    assertEquals(tr14.toString(), //
        // 1 / -sin(x)**2
        "-Csc(x)^2");

    // assert TR14((cos(x) - 1)**2*(cos(x) + 1)**2) == ans**2
    tr14 = TrigSimplifyFu.tr14(//
        F.Times(F.Power(F.Plus(F.CN1, F.Cos(F.x)), 2), F.Power(F.Plus(F.C1, F.Cos(F.x)), 2)));
    tr14 = F.eval(tr14);
    assertEquals(tr14.toString(), //
        // ans**2
        "Sin(x)^4");

    // assert TR14((cos(x) - 1)**2*(cos(x) + 1)**3) == ans**2*(cos(x) + 1)
    // assert TR14((cos(x) - 1)**3*(cos(x) + 1)**2) == ans**2*(cos(x) - 1)
    // eq = (cos(x) - 1)**y*(cos(x) + 1)**y
    // assert TR14(eq) == eq
    // eq = (cos(x) - 2)**y*(cos(x) + 1)
    // assert TR14(eq) == eq
    // eq = (tan(x) - 2)**2*(cos(x) + 1)
    // assert TR14(eq) == eq
    // i = symbols('i', integer=True)
    // assert TR14((cos(x) - 1)**i*(cos(x) + 1)**i) == ans**i
    // assert TR14((sin(x) - 1)**i*(sin(x) + 1)**i) == (-cos(x)**2)**i
    // # could use extraction in this case
    // eq = (cos(x) - 1)**(i + 1)*(cos(x) + 1)**i
    // assert TR14(eq) in [(cos(x) - 1)*ans**i, eq]
    //
    // assert TR14((sin(x) - 1)*(sin(x) + 1)) == -cos(x)**2
    // p1 = (cos(x) + 1)*(cos(x) - 1)
    // p2 = (cos(y) - 1)*2*(cos(y) + 1)
    // p3 = (3*(cos(y) - 1))*(3*(cos(y) + 1))
    // assert TR14(p1*p2*p3*(x - 1)) == -18*((x - 1)*sin(x)**2*sin(y)**4)

  }

  @Test
  public void testTrigSplit() {
    // https://github.com/sympy/sympy/blob/c79d74dafb21d631f54ba82d5ddecc56dc9efaa3/sympy/simplify/tests/test_fu.py#L316
    // assert trig_split(cos(x), cos(y)) == (1, 1, 1, x, y, True)
    IExpr trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Cos(F.y));

    assertEquals(trigSplit.toString(), //
        "{1,1,1,x,y,True}");

    // assert trig_split(2*cos(x), -2*cos(y)) == (2, 1, -1, x, y, True)
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.C2, F.Cos(F.x)), F.Times(F.CN2, F.Cos(F.y)));

    assertEquals(trigSplit.toString(), //
        "{2,1,-1,x,y,True}");

    // assert trig_split(cos(x)*sin(y), cos(y)*sin(y)) == \
    // (sin(y), 1, 1, x, y, True)
    trigSplit =
        TrigSimplifyFu.trigSplit(F.Times(F.Cos(F.x), F.Sin(F.y)), F.Times(F.Cos(F.y), F.Sin(F.y)));

    assertEquals(trigSplit.toString(), //
        "{Sin(y),1,1,x,y,True}");

    // assert trig_split(cos(x), -sqrt(3)*sin(x), two=True) == \
    // (2, 1, -1, x, pi/6, False)
    trigSplit =
        TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Times(F.Sqrt(F.C3).negate(), F.Sin(F.x)), true);

    assertEquals(trigSplit.toString(), //
        "{2,1,-1,x,Pi/6,False}");

    // assert trig_split(cos(x), sin(x), two=True) == \
    // (sqrt(2), 1, 1, x, pi/4, False)
    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Sin(F.x), true);
    assertEquals(trigSplit.toString(), //
        "{Sqrt(2),1,1,x,Pi/4,False}");
    // assert trig_split(cos(x), -sin(x), two=True) == \
    // (sqrt(2), 1, -1, x, pi/4, False)
    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Sin(F.x).negate(), true);
    assertEquals(trigSplit.toString(), //
        "{Sqrt(2),1,-1,x,Pi/4,False}");
    // assert trig_split(sqrt(2)*cos(x), -sqrt(6)*sin(x), two=True) == \
    // (2*sqrt(2), 1, -1, x, pi/6, False)
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(2), F.Cos(F.x)),
        F.Times(F.Sqrt(6).negate(), F.Sin(F.x)), true);
    assertEquals(trigSplit.toString(), //
        "{2*Sqrt(2),1,-1,x,Pi/6,False}");
    // assert trig_split(-sqrt(6)*cos(x), -sqrt(2)*sin(x), two=True) == \
    // (-2*sqrt(2), 1, 1, x, pi/3, False)
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(6).negate(), F.Cos(F.x)),
        F.Times(F.Sqrt(2).negate(), F.Sin(F.x)), true);
    assertEquals(trigSplit.toString(), //
        "{-2*Sqrt(2),1,1,x,Pi/3,False}");
    // assert trig_split(cos(x)/sqrt(6), sin(x)/sqrt(2), two=True) == \
    // (sqrt(6)/3, 1, 1, x, pi/6, False)
    trigSplit = TrigSimplifyFu.trigSplit(F.Divide(F.Cos(F.x), F.Sqrt(6)),
        F.Divide(F.Sin(F.x), F.Sqrt(2)), true);
    assertEquals(trigSplit.toString(), //
        "{Sqrt(2/3),1,1,x,Pi/6,False}");
    // assert trig_split(-sqrt(6)*cos(x)*sin(y),
    // -sqrt(2)*sin(x)*sin(y), two=True) == \
    // (-2*sqrt(2)*sin(y), 1, 1, x, pi/3, False)
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(6).negate(), F.Cos(F.x), F.Sin(F.y)),
        F.Times(F.Sqrt(2).negate(), F.Sin(F.x), F.Sin(F.y)), true);
    assertEquals(trigSplit.toString(), //
        "{-2*Sqrt(2)*Sin(y),1,1,x,Pi/3,False}");

    // assert trig_split(cos(x), sin(x)) is None
    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Sin(F.x));
    assertTrue(trigSplit.isNIL());

    // assert trig_split(cos(x), sin(z)) is None
    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Sin(F.z));
    assertTrue(trigSplit.isNIL());

    // assert trig_split(2*cos(x), -sin(x)) is None
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(2, F.Cos(F.x)), F.Sin(F.x).negate());
    assertTrue(trigSplit.isNIL());

    // assert trig_split(cos(x), -sqrt(3)*sin(x)) is None
    trigSplit = TrigSimplifyFu.trigSplit(F.Cos(F.x), F.Times(F.Sqrt(3).negate(), F.Sin(F.x)));
    assertTrue(trigSplit.isNIL());
    // assert trig_split(cos(x)*cos(y), sin(x)*sin(z)) is None
    trigSplit =
        TrigSimplifyFu.trigSplit(F.Times(F.Cos(F.x), F.Cos(F.y)), F.Times(F.Sin(F.x), F.Sin(F.z)));
    assertTrue(trigSplit.isNIL());
    // assert trig_split(cos(x)*cos(y), sin(x)*sin(y)) is None
    trigSplit =
        TrigSimplifyFu.trigSplit(F.Times(F.Cos(F.x), F.Cos(F.y)), F.Times(F.Sin(F.x), F.Sin(F.y)));
    assertTrue(trigSplit.isNIL());
    // assert trig_split(-sqrt(6)*cos(x), sqrt(2)*sin(x)*sin(y), two=True) is \
    // None
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(6).negate(), F.Cos(F.x)),
        F.Times(F.Sqrt(2), F.Sin(F.x), F.Sin(F.y)));
    assertTrue(trigSplit.isNIL());

    // assert trig_split(sqrt(3)*sqrt(x), cos(3), two=True) is None
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(3), F.Sqrt(F.x)), F.Cos(F.C3), true);
    assertTrue(trigSplit.isNIL());
    // assert trig_split(sqrt(3)*root(x, 3), sin(3)*cos(2), two=True) is None
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Sqrt(3), F.Power(F.x, F.C3)),
        F.Times(F.Sin(F.C3), F.Cos(F.C2)), true);
    assertTrue(trigSplit.isNIL());
    // assert trig_split(cos(5)*cos(6), cos(7)*sin(5), two=True) is None
    trigSplit = TrigSimplifyFu.trigSplit(F.Times(F.Cos(F.C5), F.Cos(F.C6)),
        F.Times(F.Cos(F.C7), F.Sin(F.C5)), true);
    assertTrue(trigSplit.isNIL());
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.SHORTEN_STRING_LENGTH = 1024;
    Config.MAX_AST_SIZE = 1000000;
    EvalEngine.get().setIterationLimit(50000);
  }

  @AfterEach
  public void tearDown() throws Exception {
    // super.tearDown();
    Config.SHORTEN_STRING_LENGTH = 80;
  }
}
