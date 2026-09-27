package org.matheclipse.core.sympy.calculus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestFiniteDiff extends ExprEvaluatorTestCase {

  @Test
  public void testFiniteDiffWeights() {
    // https://github.com/sympy/sympy/blob/master/sympy/calculus/tests/test_finite_diff.py
    // >>> finite_diff_weights(1, [-S(1)/2, S(1)/2, S(3)/2, S(5)/2], 0)
    IAST xList = F.List(F.CN1D2, F.C1D2, F.C3D2, F.QQ(5, 2));
    IAST weights = FiniteDiff.finiteDiffWeights(1, xList, F.C0);
    assertEquals(weights.argSize(), 2);
    assertEquals(weights.arg1().toString(), //
        "{{1,0,0,0},{1/2,1/2,0,0},{3/8,3/4,-1/8,0},{5/16,15/16,-5/16,1/16}}");
    assertEquals(weights.arg2().toString(), //
        "{{0,0,0,0},{-1,1,0,0},{-1,1,0,0},{-23/24,7/8,1/8,-1/24}}");

    // d = finite_diff_weights(1, [5, 6, 7], 5)
    // assert d[1][2] == [Rational(-3, 2), 2, Rational(-1, 2)]
    assertEquals(FiniteDiff.finiteDiffWeightsLast(1, F.List(F.C5, F.C6, F.C7), F.C5).toString(), //
        "{-3/2,2,-1/2}");

    // Table 1, p. 702 in doi:10.1090/S0025-5718-1988-0935077-0
    // d = finite_diff_weights(4, range(-4, 5), 0) - central 2nd derivative of order 4
    IAST central = F.List(F.CN2, F.CN1, F.C0, F.C1, F.C2);
    assertEquals(FiniteDiff.finiteDiffWeightsLast(2, central, F.C0).toString(), //
        "{-1/12,4/3,-5/2,4/3,-1/12}");
    assertEquals(FiniteDiff.finiteDiffWeightsLast(1, central, F.C0).toString(), //
        "{1/12,-2/3,0,2/3,-1/12}");
    assertEquals(FiniteDiff.finiteDiffWeightsLast(0, central, F.C0).toString(), //
        "{0,0,1,0,0}");
  }

  @Test
  public void testApplyFiniteDiff() {
    ISymbol x = F.x;
    ISymbol h = F.symbol("h");
    ISymbol f = F.f;
    // assert (apply_finite_diff(1, [x-h, x+h], [f(x-h), f(x+h)], x) -
    // (f(x+h)-f(x-h))/(2*h)).simplify() == 0
    IAST xList = F.List(F.Subtract(x, h), F.Plus(x, h));
    IAST yList = F.List(F.unaryAST1(f, F.Subtract(x, h)), F.unaryAST1(f, F.Plus(x, h)));
    assertEquals(F.Simplify.of(F.Subtract(FiniteDiff.applyFiniteDiff(1, xList, yList, x), //
        F.Divide(F.Subtract(yList.arg2(), yList.arg1()), F.Times(F.C2, h)))).toString(), //
        "0");

    // cubes of -3..3, second derivative at x0 = 2 is 6*x0 = 12
    IAST range = F.List(F.CN3, F.CN2, F.CN1, F.C0, F.C1, F.C2, F.C3);
    IAST cubes = F.List(F.ZZ(-27), F.ZZ(-8), F.CN1, F.C0, F.C1, F.C8, F.ZZ(27));
    assertEquals(FiniteDiff.applyFiniteDiff(2, range, cubes, F.C2).toString(), //
        "12");
  }
}
