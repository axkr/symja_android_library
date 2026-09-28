package org.matheclipse.core.sympy.calculus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.exception.ValueError;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Port of <a href="https://github.com/sympy/sympy/blob/master/sympy/calculus/tests/test_finite_diff.py">test_finite_diff.py</a>.
 * <code>test_as_finite_diff</code> and <code>test_differentiate_finite</code> aren't ported,
 * because <code>as_finite_diff</code> and <code>differentiate_finite</code> aren't ported.
 */
public class TestFiniteDiff extends ExprEvaluatorTestCase {

  private static String row(IExpr[][][] d, int m, int n) {
    return F.List(d[m][n]).toString();
  }

  private static IAST list(int... values) {
    IASTAppendable list = F.ListAlloc(values.length);
    for (int v : values) {
      list.append(F.ZZ(v));
    }
    return list;
  }

  /** The list <code>[j/2 for j in list(range(-i*2+1, 0, 2))+list(range(1, i*2+1, 2))]</code> */
  private static IAST halfIntegers(int i) {
    IASTAppendable list = F.ListAlloc(2 * i);
    for (int j = -2 * i + 1; j < 0; j += 2) {
      list.append(F.QQ(j, 2));
    }
    for (int j = 1; j < 2 * i + 1; j += 2) {
      list.append(F.QQ(j, 2));
    }
    return list;
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

    // assert (apply_finite_diff(1, [5, 6, 7], [f(5), f(6), f(7)], 5) -
    // (Rational(-3, 2)*f(5) + 2*f(6) - S.Half*f(7))).simplify() == 0
    IAST fValues = F.List(F.unaryAST1(f, F.C5), F.unaryAST1(f, F.C6), F.unaryAST1(f, F.C7));
    assertEquals(F.Simplify.of(F.Subtract(FiniteDiff.applyFiniteDiff(1, list(5, 6, 7), fValues, F.C5), //
        F.Plus(F.Times(F.QQ(-3, 2), fValues.arg1()), F.Times(F.C2, fValues.arg2()),
            F.Times(F.CN1D2, fValues.arg3())))).toString(), //
        "0");
    // raises(ValueError, lambda: apply_finite_diff(1, [x, h], [f(x)]))
    assertThrows(ValueError.class,
        () -> FiniteDiff.applyFiniteDiff(1, F.List(x, h), F.List(F.unaryAST1(f, x)), F.C0));

    // cubes of -3..3, second derivative at x0 = 2 is 6*x0 = 12
    IAST cubes = F.List(F.ZZ(-27), F.ZZ(-8), F.CN1, F.C0, F.C1, F.C8, F.ZZ(27));
    assertEquals(FiniteDiff.applyFiniteDiff(2, list(-3, -2, -1, 0, 1, 2, 3), cubes, F.C2).toString(), //
        "12");
  }

  @Test
  public void testFiniteDiffWeights() {
    // d = finite_diff_weights(1, [5, 6, 7], 5)
    // assert d[1][2] == [Rational(-3, 2), 2, Rational(-1, 2)]
    assertEquals("{-3/2,2,-1/2}", row(FiniteDiff.finiteDiffWeightsArray(1, list(5, 6, 7), F.C5), 1, 2));

    // Table 1, p. 702 in doi:10.1090/S0025-5718-1988-0935077-0
    // xl = [0, 1, -1, 2, -2, 3, -3, 4, -4]
    // d = finite_diff_weights(4, xl, S.Zero)
    IExpr[][][] d = FiniteDiff.finiteDiffWeightsArray(4, list(0, 1, -1, 2, -2, 3, -3, 4, -4), F.C0);
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 1, 0));
    assertEquals("{0,1/2,-1/2,0,0,0,0,0,0}", row(d, 1, 2));
    assertEquals("{0,2/3,-2/3,-1/12,1/12,0,0,0,0}", row(d, 1, 4));
    assertEquals("{0,3/4,-3/4,-3/20,3/20,1/60,-1/60,0,0}", row(d, 1, 6));
    assertEquals("{0,4/5,-4/5,-1/5,1/5,4/105,-4/105,-1/280,1/280}", row(d, 1, 8));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 2, 0));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 2, 1));
    assertEquals("{-2,1,1,0,0,0,0,0,0}", row(d, 2, 2));
    assertEquals("{-5/2,4/3,4/3,-1/12,-1/12,0,0,0,0}", row(d, 2, 4));
    assertEquals("{-49/18,3/2,3/2,-3/20,-3/20,1/90,1/90,0,0}", row(d, 2, 6));
    assertEquals("{-205/72,8/5,8/5,-1/5,-1/5,8/315,8/315,-1/560,-1/560}", row(d, 2, 8));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 3, 0));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 3, 1));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 3, 2));
    assertEquals("{0,-1,1,1/2,-1/2,0,0,0,0}", row(d, 3, 4));
    assertEquals("{0,-13/8,13/8,1,-1,-1/8,1/8,0,0}", row(d, 3, 6));
    assertEquals("{0,-61/30,61/30,169/120,-169/120,-3/10,3/10,7/240,-7/240}", row(d, 3, 8));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 4, 0));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 4, 1));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 4, 2));
    assertEquals("{0,0,0,0,0,0,0,0,0}", row(d, 4, 3));
    assertEquals("{6,-4,-4,1,1,0,0,0,0}", row(d, 4, 4));
    assertEquals("{28/3,-13/2,-13/2,2,2,-1/6,-1/6,0,0}", row(d, 4, 6));
    assertEquals("{91/8,-122/15,-122/15,169/60,169/60,-2/5,-2/5,7/240,7/240}", row(d, 4, 8));
    assertEquals("{1,0,0,0,0,0,0,0,0}", row(d, 0, 0));
    assertEquals("{1,0,0,0,0,0,0,0,0}", row(d, 0, 1));
    assertEquals("{1,0,0,0,0,0,0,0,0}", row(d, 0, 2));
    assertEquals("{1,0,0,0,0,0,0,0,0}", row(d, 0, 3));
    assertEquals("{1,0,0,0,0,0,0,0,0}", row(d, 0, 4));

    // Table 2, p. 703 in doi:10.1090/S0025-5718-1988-0935077-0
    // d = [finite_diff_weights({0: 1, 1: 2, 2: 4, 3: 4}[i], xl[i], 0) for i in range(4)]
    int[] orders = {1, 2, 4, 4};
    IExpr[][][][] d2 = new IExpr[4][][][];
    for (int i = 0; i < 4; i++) {
      d2[i] = FiniteDiff.finiteDiffWeightsArray(orders[i], halfIntegers(i + 1), F.C0);
    }
    assertEquals("{1/2,1/2}", row(d2[0], 0, 1));
    assertEquals("{-1/16,9/16,9/16,-1/16}", row(d2[1], 0, 3));
    assertEquals("{3/256,-25/256,75/128,75/128,-25/256,3/256}", row(d2[2], 0, 5));
    assertEquals("{-5/2048,49/2048,-245/2048,1225/2048,1225/2048,-245/2048,49/2048,-5/2048}", row(d2[3], 0, 7));
    assertEquals("{-1,1}", row(d2[0], 1, 1));
    assertEquals("{1/24,-9/8,9/8,-1/24}", row(d2[1], 1, 3));
    assertEquals("{-3/640,25/384,-75/64,75/64,-25/384,3/640}", row(d2[2], 1, 5));
    assertEquals("{5/7168,-49/5120,245/3072,-1225/1024,1225/1024,-245/3072,49/5120,-5/7168}", row(d2[3], 1, 7));

    // the nested list form
    IExpr[][][] small = FiniteDiff.finiteDiffWeightsArray(1, F.List(F.CN1D2, F.C1D2, F.C3D2, F.QQ(5, 2)), F.C0);
    assertEquals(FiniteDiff.finiteDiffWeights(1, F.List(F.CN1D2, F.C1D2, F.C3D2, F.QQ(5, 2)), F.C0)
        .arg2().toString(), //
        "{{0,0,0,0},{-1,1,0,0},{-1,1,0,0},{-23/24,7/8,1/8,-1/24}}");
    assertEquals("{5/16,15/16,-5/16,1/16}", row(small, 0, 3));

    // raises(ValueError, lambda: finite_diff_weights(-1, [1, 2]))
    assertThrows(ValueError.class, () -> FiniteDiff.finiteDiffWeights(-1, list(1, 2), F.C1));
    // finite_diff_weights(1.2, [1, 2]) and finite_diff_weights(x, [1, 2]) can't be expressed with
    // the int parameter of the Java API
  }
}
