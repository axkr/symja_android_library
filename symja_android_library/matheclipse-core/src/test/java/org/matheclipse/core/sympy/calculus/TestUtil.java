package org.matheclipse.core.sympy.calculus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.ISymbol;

public class TestUtil {
  @Test
  public void test_periodicity() {
    ISymbol x = F.x;
    ISymbol y = F.y;
    ISymbol z = F.Dummy("z", F.Element(F.Slot1, S.Reals));

    assertEquals(Util.periodicity(F.Power(S.E, F.Sin(x)), x).toString(), //
        "2*Pi");
    // assert periodicity(exp(sin(x)), x) == 2*pi


    assertEquals(Util.periodicity(F.Sin(F.Times(F.C2, x)), x).toString(), //
        "Pi");
    // assert periodicity(sin(2*x), x) == pi
    assertEquals(Util.periodicity(F.Times(F.CN2, F.Tan(F.Times(F.C4, x))), x).toString(), //
        "Pi/4");
    // assert periodicity((-2)*tan(4*x), x) == pi/4
    assertEquals(Util.periodicity(F.Sqr(F.Sin(x)), x).toString(), //
        "2*Pi");
    // assert periodicity(sin(x)**2, x) == 2*pi
    assertEquals(Util.periodicity(F.Power(F.C3, F.Tan(F.Times(F.C3, x))), x).toString(), //
        "Pi/3");
    // assert periodicity(3**tan(3*x), x) == pi/3
    assertEquals(Util.periodicity(F.Times(F.Tan(x), F.Cos(x)), x).toString(), //
        "2*Pi");
    // assert periodicity(tan(x)*cos(x), x) == 2*pi
    assertEquals(Util.periodicity(F.Power(F.Sin(x), F.Tan(x)), x).toString(), //
        "2*Pi");
    // assert periodicity(sin(x)**(tan(x)), x) == 2*pi

    assertEquals(Util.periodicity(F.Times(F.Tan(x), F.Sec(x)), x).toString(), //
        "2*Pi");
    // assert periodicity(tan(x)*sec(x), x) == 2*pi
    // assert periodicity(sin(2*x)*cos(2*x) - y, x) == pi/2
    assertEquals(Util.periodicity(F.Plus(F.Tan(x), F.Cot(x)), x).toString(), //
        "Pi");
    // assert periodicity(tan(x) + cot(x), x) == pi
    assertEquals(Util.periodicity(F.Subtract(F.Sin(x), F.Cos(F.Times(F.C2, x))), x).toString(), //
        "2*Pi");
    // assert periodicity(sin(x) - cos(2*x), x) == 2*pi
    assertEquals(Util.periodicity(F.Plus(F.CN1, F.Sin(x)), x).toString(), //
        "2*Pi");
    // assert periodicity(sin(x) - 1, x) == 2*pi
    assertEquals(
        Util.periodicity(F.Plus(F.Sin(F.Times(F.C4, x)), F.Times(F.Sin(x), F.Cos(x))), x)
            .toString(), //
        "2*Pi");
    // assert periodicity(sin(4*x) + sin(x)*cos(x), x) == pi
    assertEquals(Util.periodicity(F.Power(S.E, F.Sin(x)), x).toString(), //
        "2*Pi");
    // assert periodicity(exp(sin(x)), x) == 2*pi
    // assert periodicity(log(cot(2*x)) - sin(cos(2*x)), x) == pi
    // assert periodicity(sin(2*x)*exp(tan(x) - csc(2*x)), x) == pi
    // assert periodicity(cos(sec(x) - csc(2*x)), x) == 2*pi
    // assert periodicity(tan(sin(2*x)), x) == pi
    // assert periodicity(2*tan(x)**2, x) == pi
    // assert periodicity(sin(x%4), x) == 4
    // assert periodicity(sin(x)%4, x) == 2*pi
    // assert periodicity(tan((3*x-2)%4), x) == Rational(4, 3)
    // assert periodicity((sqrt(2)*(x+1)+x) % 3, x) == 3 / (sqrt(2)+1)
    // assert periodicity((x**2+1) % x, x) is None
    // assert periodicity(sin(re(x)), x) == 2*pi
    // assert periodicity(sin(x)**2 + cos(x)**2, x) is S.Zero
    // assert periodicity(tan(x), y) is S.Zero
    // assert periodicity(sin(x) + I*cos(x), x) == 2*pi
    // assert periodicity(x - sin(2*y), y) == pi
  }

  @Test
  public void test_lcim() {

    // assert lcim([S.Half, S(2), S(3)]) == 6
    // assert lcim([pi/2, pi/4, pi]) == pi
    // assert lcim([2*pi, pi/2]) == 2*pi
    // assert lcim([S.One, 2*pi]) is None
    // assert lcim([S(2) + 2*E, E/3 + Rational(1, 3), S.One + E]) == S(2) + 2*E

    assertEquals(Util.lcim(F.List(F.C1D2, F.C2, F.C3)).toString(), //
        "6");
    assertEquals(Util.lcim(F.List(F.CPiHalf, F.CPiQuarter, S.Pi)).toString(), //
        "Pi");
    assertEquals(Util.lcim(F.List(F.C2Pi, F.CPiHalf)).toString(), //
        "2*Pi");
    assertEquals(Util.lcim(F.List(F.C1, F.C2Pi)).toString(), //
        "NIL");
    assertEquals(Util.lcim(F.List(F.Plus(F.C2, F.Times(F.C2, S.E)), //
        F.Plus(F.C1D3, F.Times(F.C1D3, S.E)), //
        F.Plus(F.C1, S.E))) //
        .toString(), //
        "2*(1+E)");
  }
}
