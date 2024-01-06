package org.matheclipse.core.sympy.solvers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.ISymbol;

public class TestDecompogen {

  @Test
  void test_decompogen() {

    ISymbol x = F.x;
    ISymbol y = F.y;
    ISymbol z = F.Dummy("z", F.Element(F.Slot1, S.Reals));

    ISymbol u = F.Dummy("u");
    u.assignValue(F.Plus(F.C3, F.Times(F.C2, x)));
    // // u = 2*x + 3

    // assertEquals(Decompogen.decompogen(F.eval(F.Max(F.Sqrt(u), F.Sqr(u))), x).toString(), //
    // "{Max(Sqrt(x),x^2),3+2*x}");
    // assert decompogen(Max(sqrt(u),(u)**2), x) == [Max(sqrt(x), x**2), u]
    // assert decompogen(Max(u, u**2, y), x) == [Max(x, x**2, y), u]

    assertEquals(Decompogen.decompogen(F.eval(F.Max(F.Sin(x), u)), x).toString(), //
        "{Max(3+2*x,Sin(x))}");

    // assert decompogen(Max(sin(x), u), x) == [Max(2*x + 3, sin(x))]

    assertEquals(Decompogen.decompogen(F.Power(S.E, F.Sin(x)), x).toString(), //
        "{E^x,Sin(x)}");

    assertEquals(Decompogen.decompogen(F.Sin(F.Cos(x)), x).toString(), //
        "{Sin(x),Cos(x)}");
    // assert decompogen(sin(cos(x)), x) == [sin(x), cos(x)]

    assertEquals(
        Decompogen.decompogen(F.Plus(F.Power(F.Sin(x), F.C2), F.Sin(x), F.C1), x).toString(), //
        "{1+x+x^2,Sin(x)}");
    // assert decompogen(sin(x)**2 + sin(x) + 1, x) == [x**2 + x + 1, sin(x)]

    assertEquals(
        Decompogen.decompogen(F.Sqrt(F.Plus(F.CN5, F.Times(F.C6, F.Power(x, F.C2)))), x).toString(), //
        "{Sqrt(x),-5+6*x^2}");
    // assert decompogen(sqrt(6*x**2 - 5), x) == [sqrt(x), 6*x**2 - 5]

    assertEquals(Decompogen.decompogen(F.Sin(F.Sqrt(F.Cos(F.Plus(F.C1, F.Sqr(x))))), x).toString(), //
        "{Sin(x),Sqrt(x),Cos(x),1+x^2}");
    // assert decompogen(sin(sqrt(cos(x**2 + 1))), x) == [sin(x), sqrt(x), cos(x), x**2 + 1]

    assertEquals(
        Decompogen.decompogen(F.Abs(F.Plus(F.CN4, F.Sqr(F.Cos(x)), F.Times(F.C3, F.Cos(x)))), x)
            .toString(), //
        "{Abs(x),-4+3*Cos(x)+Cos(x)^2,3*x,x^2}");
    // assert decompogen(Abs(cos(x)**2 + 3*cos(x) - 4), x) == [Abs(x), x**2 + 3*x - 4, cos(x)]
    // assert decompogen(sin(x)**2 + sin(x) - sqrt(3)/2, x) == [x**2 + x - sqrt(3)/2, sin(x)]
    // assert decompogen(Abs(cos(y)**2 + 3*cos(x) - 4), x) == [Abs(x), 3*x + cos(y)**2 - 4, cos(x)]
    // assert decompogen(x, y) == [x]
    // assert decompogen(1, x) == [1]
    // assert decompogen(Max(3, x), x) == [Max(3, x)]
    // raises(TypeError, lambda: decompogen(x < 5, x))
    // u = 2*x + 3
    // assert decompogen(Max(sqrt(u),(u)**2), x) == [Max(sqrt(x), x**2), u]
    // assert decompogen(Max(u, u**2, y), x) == [Max(x, x**2, y), u]
    // assert decompogen(Max(sin(x), u), x) == [Max(2*x + 3, sin(x))]
  }

}
