package org.matheclipse.core.sympy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.HashMap;
import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.core.ExprTools.Factors;

public class TestExprTools {

  @Test
  public void test_Factors001() {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/tests/test_exprtools.py#L38
    ISymbol t = F.t;
    ISymbol x = F.x;
    ISymbol y = F.y;
    ISymbol z = F.z;

    // Factors() == Factors({}) == Factors(S.One)
    assertEquals(new Factors(), new Factors(F.CEmptyList));
    assertEquals(new Factors(), new Factors(F.C1));

    // Factors().as_expr() is S.One
    assertEquals(new Factors().asExpr(), F.C1);

    // use Association - dict like expression
    // Factors({x: 2, y: 3, sin(x): 4}).as_expr() == x**2*y**3*sin(x)**4
    assertEquals(
        new Factors(F.assoc(F.List(F.Rule(x, F.C2), F.Rule(y, F.C3), F.Rule(F.Sin(x), F.C4))))
            .asExpr().toString(),
        "Sin(x)^4*x^2*y^3");


    // Factors(S.Infinity) == Factors({oo: 1})
    assertEquals(new Factors(F.CInfinity), //
        new Factors(F.assoc(F.List(F.Rule(F.CInfinity, F.C1)))));

    // Factors(S.NegativeInfinity) == Factors({oo: 1, -1: 1})
    assertEquals(new Factors(F.CNInfinity), //
        new Factors(F.assoc(F.List(F.Rule(F.CInfinity, F.C1), F.Rule(F.CN1, F.C1)))));

    // Factors((x**2)**S.Half).as_expr() == (x**2)**S.Half
    assertEquals(new Factors(F.Power(F.Power(x, F.C2), F.C1D2)).asExpr().toString(), "Sqrt(x^2)");

    Factors a = new Factors(F.assoc(F.List(F.Rule(x, F.C5), F.Rule(y, F.C3), F.Rule(z, F.C7))));
    Factors b = new Factors(F.assoc(F.List(F.Rule(y, F.C4), F.Rule(z, F.C3), F.Rule(t, F.C10))));

    // assert a.mul(b) == a*b == Factors({x: 5, y: 7, z: 10, t: 10})
    assertEquals(a.mul(b).asExpr().toString(), //
        "x^5*y^7*z^10*t^10");

    // assert a.div(b) == divmod(a, b) == \
    // (Factors({x: 5, z: 4}), Factors({y: 1, t: 10}))
    assertEquals(a.div(b)[0].asExpr().toString(), //
        "x^5*z^4");
    assertEquals(a.div(b)[1].asExpr().toString(), //
        "t^10*y");

    // assert a.quo(b) == a/b == Factors({x: 5, z: 4})
    assertEquals(a.quo(b).asExpr().toString(), //
        "x^5*z^4");
    // assert a.rem(b) == a % b == Factors({y: 1, t: 10})
    assertEquals(a.rem(b).asExpr().toString(), //
        "t^10*y");

    // assert a.pow(3) == a**3 == Factors({x: 15, y: 9, z: 21})
    assertEquals(a.pow(F.C3).asExpr().toString(), //
        "x^15*y^9*z^21");

    // assert b.pow(3) == b**3 == Factors({y: 12, z: 9, t: 30})
    assertEquals(b.pow(F.C3).asExpr().toString(), //
        "t^30*y^12*z^9");

    // assert a.gcd(b) == Factors({y: 3, z: 3})
    assertEquals(a.gcd(b).asExpr().toString(), //
        "y^3*z^3");

    // assert a.lcm(b) == Factors({x: 5, y: 4, z: 7, t: 10})
    assertEquals(a.lcm(b).asExpr().toString(), //
        "x^5*y^4*z^7*t^10");

    a = new Factors(F.assoc(F.List(F.Rule(x, F.C4), F.Rule(y, F.C7), F.Rule(t, F.C7))));
    b = new Factors(F.assoc(F.List(F.Rule(z, F.C1), F.Rule(t, F.C3))));

    // assert a.normal(b) == (Factors({x: 4, y: 7, t: 4}), Factors({z: 1}))
    assertEquals(a.normal(b)[0].toString(), //
        "{x=4, y=7, t=4}");
    assertEquals(a.normal(b)[1].toString(), //
        "{z=1}");

    // assert Factors(sqrt(2)*x).as_expr() == sqrt(2)*x
    assertEquals(new Factors(F.Times(F.Sqrt(2), x)).asExpr().toString(), //
        "Sqrt(2)*x");
    // assert Factors(-I)*I == Factors()
    assertEquals(new Factors(F.CNI).mul(F.CI).toString(), //
        "{}");

    // assert Factors({S.NegativeOne: S(3)})*Factors({S.NegativeOne: S.One, I: S(5)}) == Factors(I)
    assertEquals(new Factors(F.assoc(F.List(F.Rule(F.CN1, F.C3)))).mul(//
        new Factors(F.assoc(F.List(F.Rule(F.CN1, F.C1), F.Rule(F.CI, F.C5))))).asExpr().toString(),
        //
        "I");
    HashMap<IExpr, IExpr> aMap = new HashMap<IExpr, IExpr>();
    aMap.put(F.CN1, F.C3);
    HashMap<IExpr, IExpr> bMap = new HashMap<IExpr, IExpr>();
    bMap.put(F.CN1, F.C1);
    bMap.put(F.CI, F.C5);
    assertEquals(new Factors(aMap).mul(//
        new Factors(bMap)).asExpr().toString(), //
        "I");

    // assert Factors(sqrt(I)*I) == Factors(I**(S(3)/2)) == Factors({I: S(3)/2})
    assertEquals(new Factors(F.Times(F.CI, F.Power(F.CI, F.C1D2))).asExpr().toString(), //
        "I^(3/2)");
    assertEquals(new Factors(F.Power(F.CI, F.C3D2)).asExpr().toString(), //
        "I^(3/2)");

    // assert Factors({I: S(3)/2}).as_expr() == I**(S(3)/2)
    assertEquals(new Factors(F.assoc(F.List(F.Rule(F.CI, F.C3D2)))).asExpr().toString(), //
        "(-1)^(3/4)");

    aMap.clear();
    aMap.put(F.CI, F.C3D2);
    // symja evals I^(3/2) as (-1)^(3/4)
    assertEquals(new Factors(aMap).asExpr().toString(), //
        "(-1)^(3/4)");

    // assert Factors(S(2)**x).div(S(3)**x) == \
    // (Factors({S(2): x}), Factors({S(3): x}))
    assertEquals(new Factors(F.Power(F.C2, x)).div(F.Power(F.C3, x))[0].asExpr().toString(), //
        "2^x");
    assertEquals(new Factors(F.Power(F.C2, x)).div(F.Power(F.C3, x))[1].asExpr().toString(), //
        "3^x");

    // assert Factors(2**(2*x + 2)).div(S(8)) == \
    // (Factors({S(2): 2*x + 2}), Factors({S(8): S.One}))
    assertEquals(new Factors(F.Power(F.C2, F.Plus(F.C2, F.Times(F.C2, x)))).div(F.C8)[0].toString(), //
        "{2=2+2*x}");
    assertEquals(new Factors(F.Power(F.C2, F.Plus(F.C2, F.Times(F.C2, x)))).div(F.C8)[1].toString(), //
        "{8=1}");

    // # coverage
    // # /!\ things break if this is not True
    // assert Factors({S.NegativeOne: Rational(3, 2)}) == Factors({I: S.One, S.NegativeOne: S.One})

    assertEquals(new Factors(F.assoc(F.List(F.Rule(F.CN1, F.C3D2)))).toString(), //
        "{-1=1, I=1}");
    assertEquals(new Factors(F.assoc(F.List(F.Rule(F.CI, F.C1), F.Rule(F.CN1, F.C1)))).toString(), //
        "{-1=1, I=1}");

    // assert Factors({I: S.One, S.NegativeOne: Rational(1, 3)}).as_expr() == I*(-1)**Rational(1,
    // 3)
    assertEquals(
        new Factors(F.assoc(F.List(F.Rule(F.CI, F.C1), F.Rule(F.CN1, F.C1D3)))).asExpr().toString(), //
        "(-1)^(5/6)");

    // assert Factors(-1.) == Factors({S.NegativeOne: S.One, S(1.): 1})
    assertEquals(new Factors(F.CND1).toString(), //
        "{-1=1, 1.0=1}");
    // assert Factors(-2.) == Factors({S.NegativeOne: S.One, S(2.): 1})
    assertEquals(new Factors(F.num(-2.0)).toString(), //
        "{-1=1, 2.0=1}");

    // assert Factors((-2.)**x) == Factors({S(-2.): x})
    assertEquals(new Factors(F.Power(F.num(-2.0), x)).toString(), //
        "{-2.0=x}");
    // assert Factors(S(-2)) == Factors({S.NegativeOne: S.One, S(2): 1})
    assertEquals(new Factors(F.CN2).toString(), //
        "{-1=1, 2=1}");
    // assert Factors(S.Half) == Factors({S(2): -S.One})
    assertEquals(new Factors(F.C1D2).toString(), //
        "{2=-1}");
    // assert Factors(Rational(3, 2)) == Factors({S(3): S.One, S(2): S.NegativeOne})
    assertEquals(new Factors(F.C3D2).toString(), //
        "{2=-1, 3=1}");
    // assert Factors({I: S.One}) == Factors(I)
    assertEquals(new Factors(F.CI).toString(), //
        "{I=1}");

    // assert Factors({-1.0: 2, I: 1}) == Factors({S(1.0): 1, I: 1})
    HashMap<IExpr, IExpr> cMap = new HashMap<IExpr, IExpr>();
    cMap.clear();
    cMap.put(F.CND1, F.C2);
    cMap.put(F.CI, F.C1);
    assertEquals(new Factors(cMap).toString(), //
        "{1.0=1, I=1}");

    // assert Factors({S.NegativeOne: Rational(-3, 2)}).as_expr() == I
    cMap.clear();
    cMap.put(F.CN1, F.CN3D2);
    assertEquals(new Factors(cMap).asExpr().toString(), //
        "I");

    // TODO
    // A = symbols('A', commutative=False)
    // assert Factors(2*A**2) == Factors({S(2): 1, A**2: 1})


    // assert Factors(I) == Factors({I: S.One})
    assertEquals(new Factors(F.CI).toString(), //
        "{I=1}");

    // assert Factors(x).normal(S(2)) == (Factors(x), Factors(S(2)))
    assertEquals(new Factors(F.x).normal(F.C2)[0].toString(), //
        "{x=1}");
    assertEquals(new Factors(F.x).normal(F.C2)[1].toString(), //
        "{2=1}");

    // assert Factors(x).normal(S.Zero) == (Factors(), Factors(S.Zero))
    assertEquals(new Factors(F.x).normal(F.C0)[0].toString(), //
        "{}");
    assertEquals(new Factors(F.x).normal(F.C0)[1].toString(), //
        "{0=1}");

    // raises(ZeroDivisionError, lambda: Factors(x).div(S.Zero))
    try {
      new Factors(F.x).div(F.C0);
      fail();
    } catch (ArithmeticException aex) {
      // division by zero
    }
    // assert Factors(x).mul(S(2)) == Factors(2*x)
    assertEquals(new Factors(x).mul(F.C2).toString(), //
        "{x=1, 2=1}");
    assertEquals(new Factors(F.Times(F.C2, F.x)).toString(), //
        "{2=1, x=1}");

    // assert Factors(x).mul(S.Zero).is_zero
    assertEquals(new Factors(x).mul(F.C0).isZero(), //
        true);

    // assert Factors(x).mul(1/x).is_one
    assertEquals(new Factors(x).mul(F.Power(x, F.CN1)).isOne(), //
        true);
    // assert Factors(x**sqrt(2)**3).as_expr() == x**(2*sqrt(2))
    assertEquals(new Factors(F.Power(x, F.Power(F.Sqrt(F.C2), F.C3))).asExpr().toString(), //
        "x^(Sqrt(2))^3");
    // assert Factors(x)**Factors(S(2)) == Factors(x**2)
    assertEquals(new Factors(x).pow(F.C2).toString(), //
        "{x=2}");
    // assert Factors(x).gcd(S.Zero) == Factors(x)
    assertEquals(new Factors(x).gcd(F.C0).toString(), //
        "{x=1}");
    // assert Factors(x).lcm(S.Zero).is_zero
    assertEquals(new Factors(x).lcm(F.C0).isZero(), //
        true);
    // assert Factors(S.Zero).div(x) == (Factors(S.Zero), Factors())
    assertEquals(new Factors(F.C0).div(x)[0].toString(), //
        "{0=1}");
    assertEquals(new Factors(F.C0).div(x)[1].toString(), //
        "{}");
    // assert Factors(x).div(x) == (Factors(), Factors())
    assertEquals(new Factors(x).div(x)[0].toString(), //
        "{}");
    assertEquals(new Factors(x).div(x)[1].toString(), //
        "{}");

    // assert Factors({x: .2})/Factors({x: .2}) == Factors()
    assertEquals(new Factors(F.assoc(F.List(F.Rule(x, F.num(2.0)))))//
        .quo(new Factors(F.assoc(F.List(F.Rule(x, F.num(2.0)))))).toString(), //
        "{}");

    // assert Factors(x) != Factors()
    assertEquals(new Factors(x)//
        .equals(new Factors()), //
        false);
    // assert Factors(S.Zero).normal(x) == (Factors(S.Zero), Factors())
    assertEquals(new Factors(F.C0).normal(x)[0], //
        new Factors(F.C0));
    assertEquals(new Factors(F.C0).normal(x)[1], //
        new Factors());

    // n, d = x**(2 + y), x**2
    // f = Factors(n)
    // assert f.div(d) == f.normal(d) == (Factors(x**y), Factors())
    IExpr n = F.Power(F.x, F.Plus(F.C2, F.y));
    IExpr d = F.Power(F.x, F.C2);
    Factors f = new Factors(n);
    assertEquals(f.div(d)[0].asExpr().toString(), //
        "x^y");
    assertEquals(f.div(d)[1].toString(), //
        "{}");

    assertEquals(f.normal(d)[0].toString(), //
        "{x=y}");
    assertEquals(f.normal(d)[1].toString(), //
        "{}");

    // assert f.gcd(d) == Factors()
    assertEquals(f.gcd(d).toString(), //
        "{}");

    // d = x**y
    d = F.Power(F.x, F.y);
    // assert f.div(d) == f.normal(d) == (Factors(x**2), Factors())
    assertEquals(f.div(d)[0].toString(), //
        "{x=2}");
    assertEquals(f.div(d)[1].toString(), //
        "{}");

    assertEquals(f.normal(d)[0].toString(), //
        "{x=2}");
    assertEquals(f.normal(d)[1].toString(), //
        "{}");

    // assert f.gcd(d) == Factors(d)
    assertEquals(f.gcd(d).toString(), //
        "{x=y}");

  }

  @Test
  public void test_Factors002() {
    // n = d = 2**x
    // f = Factors(n)
    IExpr n = F.Power(F.C2, F.x);
    IExpr d = F.Power(F.C2, F.x);
    Factors f = new Factors(n);

    // assert f.div(d) == f.normal(d) == (Factors(), Factors())
    assertEquals(f.div(d)[0].toString(), //
        "{}");
    assertEquals(f.div(d)[1].toString(), //
        "{}");

    assertEquals(f.normal(d)[0].toString(), //
        "{}");
    assertEquals(f.normal(d)[1].toString(), //
        "{}");
    // assert f.gcd(d) == Factors(d)
    assertEquals(f.gcd(d).toString(), //
        "{2=x}");
  }

  @Test
  public void test_Factors003() {
    // n, d = 2**x, 2**y
    // f = Factors(n)
    IExpr n = F.Power(F.C2, F.x);
    IExpr d = F.Power(F.C2, F.y);
    Factors f = new Factors(n);

    // assert f.div(d) == f.normal(d) == (Factors({S(2): x}), Factors({S(2): y}))
    // assertEquals(f.div(d)[0].toString(), //
    // "{2=x}");
    assertEquals(f.div(d)[1].toString(), //
        "{2=y}");

    assertEquals(f.normal(d)[0].toString(), //
        "{2=x}");
    assertEquals(f.normal(d)[1].toString(), //
        "{2=y}");
    // assert f.gcd(d) == Factors()
    assertEquals(f.gcd(d).toString(), //
        "{}");
  }

  @Test
  public void test_Factors004() {
    // # extraction of constant only
    // n = x**(x + 3)
    IExpr n = F.Power(F.x, F.Plus(F.C3, F.x));
    Factors f = new Factors(n);
    // assert Factors(n).normal(x**-3) == (Factors({x: x + 6}), Factors({}))
    assertEquals(f.normal(F.Power(F.x, F.CN3))[0].toString(), //
        "{x=6+x}");
    assertEquals(f.normal(F.Power(F.x, F.CN3))[1].toString(), //
        "{}");
    // assert Factors(n).normal(x**3) == (Factors({x: x}), Factors({}))
    assertEquals(f.normal(F.Power(F.x, F.C3))[0].toString(), //
        "{x=x}");
    assertEquals(f.normal(F.Power(F.x, F.C3))[1].toString(), //
        "{}");
    // assert Factors(n).normal(x**4) == (Factors({x: x}), Factors({x: 1}))
    assertEquals(f.normal(F.Power(F.x, F.C4))[0].toString(), //
        "{x=x}");
    assertEquals(f.normal(F.Power(F.x, F.C4))[1].toString(), //
        "{x=1}");
    // assert Factors(n).normal(x**(y - 3)) == \
    // (Factors({x: x + 6}), Factors({x: y}))
    assertEquals(f.normal(F.Power(F.x, F.Subtract(F.y, F.C3)))[0].toString(), //
        "{x=6+x}");
    assertEquals(f.normal(F.Power(F.x, F.Plus(F.CN3, F.y)))[1].toString(), //
        "{x=y}");


    // assert Factors(n).normal(x**(y + 3)) == (Factors({x: x}), Factors({x: y}))
    // assert Factors(n).normal(x**(y + 4)) == \
    // (Factors({x: x}), Factors({x: y + 1}))
    //
    // assert Factors(n).div(x**-3) == (Factors({x: x + 6}), Factors({}))
    // assert Factors(n).div(x**3) == (Factors({x: x}), Factors({}))
    // assert Factors(n).div(x**4) == (Factors({x: x}), Factors({x: 1}))
    // assert Factors(n).div(x**(y - 3)) == \
    // (Factors({x: x + 6}), Factors({x: y}))
    // assert Factors(n).div(x**(y + 3)) == (Factors({x: x}), Factors({x: y}))
    // assert Factors(n).div(x**(y + 4)) == \
    // (Factors({x: x}), Factors({x: y + 1}))
    //
    // assert Factors(3 * x / 2) == Factors({3: 1, 2: -1, x: 1})
    // assert Factors(x * x / y) == Factors({x: 2, y: -1})
    // assert Factors(27 * x / y**9) == Factors({27: 1, x: 1, y: -9})
  }
}
