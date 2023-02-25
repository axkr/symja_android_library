package org.matheclipse.core.sympy.series;

import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestFormal extends ExprEvaluatorTestCase {

  public TestFormal(String name) {
    super(name);
  }

  @Test
  public void test_fps() {
    ISymbol x = F.x;
    ISymbol y = F.y;
    // assert fps(1) == 1
    assertEquals(Formal.fps(F.C1).toString(), //
        "1");

    // assert fps(2, x) == 2
    assertEquals(Formal.fps(F.C2, x).toString(), //
        "2");
    // assert fps(2, x, dir='+') == 2
    assertEquals(Formal.fps(F.C2, x, '+').toString(), //
        "2");
    // assert fps(2, x, dir='-') == 2
    assertEquals(Formal.fps(F.C2, x, '-').toString(), //
        "2");

    // assert fps(1/x + 1/x**2) == 1/x + 1/x**2
    // TODO
    // assertEquals(Formal.fps(F.Plus(x.inverse(), x.pow(-2))).toString(), //
    // "1/x^2+1/x+O(x)^5");


    // assert fps(log(1 + x), hyper=False, rational=False) == log(1 + x)
    assertEquals(
        Formal.fps(F.Log(F.C1.plus(F.x)), F.NIL, F.C0, F.C1, false, 4, false, false).toString(), //
        "Log(1+x)");

    // f = fps(x**2 + x + 1)
    // assert isinstance(f, FormalPowerSeries)
    // assert f.function == x**2 + x + 1
    // assert f[0] == 1
    // assert f[2] == x**2
    // assert f.truncate(4) == x**2 + x + 1 + O(x**4)
    // assert f.polynomial() == x**2 + x + 1
    //
    // f = fps(log(1 + x))
    // assert isinstance(f, FormalPowerSeries)
    // assert f.function == log(1 + x)
    // assert f.subs(x, y) == f
    // assert f[:5] == [0, x, -x**2/2, x**3/3, -x**4/4]
    // assert f.as_leading_term(x) == x
    // assert f.polynomial(6) == x - x**2/2 + x**3/3 - x**4/4 + x**5/5
    //
    // k = f.ak.variables[0]
    // assert f.infinite == Sum((-(-1)**(-k)*x**k)/k, (k, 1, oo))
    //
    // ft, s = f.truncate(n=None), f[:5]
    // for i, t in enumerate(ft):
    // if i == 5:
    // break
    // assert s[i] == t
    //
    // f = sin(x).fps(x)
    // assert isinstance(f, FormalPowerSeries)
    // assert f.truncate() == x - x**3/6 + x**5/120 + O(x**6)
    //
    // raises(NotImplementedError, lambda: fps(y*x))
    // raises(ValueError, lambda: fps(x, dir=0))
  }
}
