package org.matheclipse.core.sympy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class TestSubs {

  @Test
  public void test_equality_subs1() {
    // f = Function('f')
    // eq = Eq(f(x)**2, x)
    // res = Eq(Integer(16), x)
    // assert eq.subs(f(x), 4) == res
    ISymbol x = F.x;
    IASTMutable fun = F.unaryAST1(F.f, x);
    IExpr eq = F.Equal(F.Power(fun, 2), x);
    IExpr ans = eq.subs(fun, F.C4);
    assertEquals(ans.toString(), //
        "16==x");
  }

  @Test
  public void test_equality_subs2() {
    // f = Function('f')
    // eq = Eq(f(x)**2, 16)
    // assert bool(eq.subs(f(x), 3)) is False
    // assert bool(eq.subs(f(x), 4)) is True
    ISymbol x = F.x;
    IASTMutable fun = F.unaryAST1(F.f, x);
    IExpr eq = F.Equal(F.Power(fun, 2), 16);
    IExpr ans = eq.subs(fun, F.C3);
    assertEquals(ans.toString(), //
        "False");
    ans = eq.subs(fun, F.C4);
    assertEquals(ans.toString(), //
        "True");
  }

  @Test
  public void test_issue_3742() {
    // e = sqrt(x)*exp(y)
    // assert e.subs(sqrt(x), 1) == exp(y)
    ISymbol x = F.x;
    ISymbol y = F.y;
    IExpr ans = F.Times(F.Sqrt(x), F.Exp(y)).subs(F.Sqrt(x), F.C1);
    assertEquals(ans.toString(), //
        "E^y");

  }

  @Test
  public void test_subs_simple() {
    ISymbol a = F.a;
    ISymbol b = F.b;
    ISymbol c = F.c;
    ISymbol d = F.d;
    ISymbol e = F.e;
    ISymbol x = F.x;
    ISymbol y = F.y;
    // a = symbols('a', commutative=True)
    // x = symbols('x', commutative=False)

    // assert (2*a).subs(1, 3) == 2*a
    IExpr ans = F.Times(F.C2, a).subs(F.C1, F.C3);
    assertEquals(ans.toString(), //
        "2*a");

    // assert (2*a).subs(2, 3) == 3*a
    ans = F.Times(F.C2, a).subs(F.C2, F.C3);
    assertEquals(ans.toString(), //
        "3*a");
    // assert (2*a).subs(a, 3) == 6
    ans = F.Times(F.C2, a).subs(a, F.C3);
    assertEquals(ans.toString(), //
        "6");
    // assert sin(2).subs(1, 3) == sin(2)
    ans = F.Sin(F.C2).subs(F.C1, F.C3);
    assertEquals(ans.toString(), //
        "Sin(2)");
    // assert sin(2).subs(2, 3) == sin(3)
    ans = F.Sin(F.C2).subs(F.C2, F.C3);
    assertEquals(ans.toString(), //
        "Sin(3)");
    // assert sin(a).subs(a, 3) == sin(3)
    ans = F.Sin(a).subs(a, F.C3);
    assertEquals(ans.toString(), //
        "Sin(3)");


    // assert (2*x).subs(1, 3) == 2*x
    ans = F.Times(F.C2, x).subs(F.C1, F.C3);
    assertEquals(ans.toString(), //
        "2*x");
    // assert (2*x).subs(2, 3) == 3*x
    ans = F.Times(F.C2, x).subs(F.C2, F.C3);
    assertEquals(ans.toString(), //
        "3*x");
    // assert (2*x).subs(x, 3) == 6
    ans = F.Times(F.C2, x).subs(x, F.C3);
    assertEquals(ans.toString(), //
        "6");
    // assert sin(x).subs(x, 3) == sin(3)
    ans = F.Sin(x).subs(a, F.C3);
    assertEquals(ans.toString(), //
        "Sin(x)");
  }

  @Test
  public void test_subs_constants() {
    ISymbol a = F.a;
    ISymbol b = F.b;
    ISymbol c = F.c;
    ISymbol d = F.d;
    ISymbol e = F.e;
    ISymbol x = F.x;
    ISymbol y = F.y;

    // a, b = symbols('a b', commutative=True)
    // x, y = symbols('x y', commutative=False)
    // assert (a*b).subs(2*a, 1) == a*b
    // assert (1.5*a*b).subs(a, 1) == 1.5*b
    // assert (2*a*b).subs(2*a, 1) == b
    // assert (2*a*b).subs(4*a, 1) == 2*a*b
    // assert (x**2*y**(x*Rational(3, 2))).subs(x*y**(x/2), 2) == 4*y**(x/2)
    IExpr rans = F.Times(F.Power(x, 2), F.Power(y, F.Times(x, F.C3D2)))
        .subs(F.Times(x, F.Power(y, F.Times(x, F.C1D2))), F.C2);
    assertEquals(rans.toString(), //
        "4*y^(x/2)");

    IExpr ans = F.Times(a, b).subs(F.Times(F.C2, a), F.C1);
    assertEquals(ans.toString(), //
        "a*b");
    ans = F.Times(F.num(1.5), a, b).subs(a, F.C1);
    assertEquals(ans.toString(), //
        "1.5*b");
    ans = F.Times(F.C2, a, b).subs(F.Times(F.C2, a), F.C1);
    assertEquals(ans.toString(), //
        "b");
    ans = F.Times(F.C2, a, b).subs(F.Times(F.C4, a), F.C1);
    assertEquals(ans.toString(), //
        "2*a*b");

    // assert (x*y).subs(2*x, 1) == x*y
    // assert (1.5*x*y).subs(x, 1) == 1.5*y
    // assert (2*x*y).subs(2*x, 1) == y
    // assert (2*x*y).subs(4*x, 1) == 2*x*y

    ans = F.Times(x, y).subs(F.Times(F.C2, x), F.C1);
    assertEquals(ans.toString(), //
        "x*y");
    ans = F.Times(F.num(1.5), x, y).subs(x, F.C1);
    assertEquals(ans.toString(), //
        "1.5*y");
    ans = F.Times(F.C2, x, y).subs(F.Times(F.C2, x), F.C1);
    assertEquals(ans.toString(), //
        "y");
    ans = F.Times(F.C2, x, y).subs(F.Times(F.C4, x), F.C1);
    assertEquals(ans.toString(), //
        "2*x*y");

  }

  @Test
  public void test_subs_commutative() {
    ISymbol a = F.a;
    ISymbol b = F.b;
    ISymbol c = F.c;
    ISymbol d = F.d;
    ISymbol K = F.Dummy("K");

    // assert (a*b).subs(a*b, K) == K
    IExpr ans = F.Times(a, b).subs(F.Times(a, b), K);
    assertEquals(ans.toString(), //
        "K");
    // assert (a*b*a*b).subs(a*b, K) == K**2
    ans = F.Times(a, b, a, b).subs(F.Times(a, b), K);
    assertEquals(ans.toString(), //
        "K^2");
    // assert (a*a*b*b).subs(a*b, K) == K**2
    ans = F.Times(a, a, b, b).subs(F.Times(a, b), K);
    assertEquals(ans.toString(), //
        "K^2");
    // assert (a*b*c*d).subs(a*b*c, K) == d*K
    ans = F.Times(a, b, c, d).subs(F.Times(a, b, c), K);
    assertEquals(ans.toString(), //
        "d*K");
    // assert (a*b**c).subs(a, K) == K*b**c
    ans = F.Times(a, F.Power(b, c)).subs(a, K);
    assertEquals(ans.toString(), //
        "b^c*K");
    // assert (a*b**c).subs(b, K) == a*K**c
    ans = F.Times(a, F.Power(b, c)).subs(b, K);
    assertEquals(ans.toString(), //
        "a*K^c");
    // assert (a*b**c).subs(c, K) == a*b**K
    ans = F.Times(a, F.Power(b, c)).subs(c, K);
    assertEquals(ans.toString(), //
        "a*b^K");
    // assert (a*b*c*b*a).subs(a*b, K) == c*K**2
    ans = F.Times(a, b, c, b, a).subs(F.Times(a, b), K);
    assertEquals(ans.toString(), //
        "c*K^2");
    // assert (a**3*b**2*a).subs(a*b, K) == a**2*K**2
    ans = F.Times(F.Power(a, 3), F.Power(b, 2), a).subs(F.Times(a, b), K);
    assertEquals(ans.toString(), //
        "a^2*K^2");
  }

  @Test
  public void test_add() {
    ISymbol a = F.a;
    ISymbol b = F.b;
    ISymbol c = F.c;
    ISymbol d = F.d;
    ISymbol x = F.x;
    ISymbol y = F.y;
    ISymbol z = F.z;
    ISymbol t = F.t;

    // assert (a**2 - b - c).subs(a**2 - b, d) in [d - c, a**2 - b - c]
    IExpr ans = F.Plus(F.Power(a, 2), b.negate(), c.negate()).subs(F.Subtract(F.Power(a, 2), b), d);
    assertEquals(ans.toString(), //
        "-c+d");

    // assert (a**2 - c).subs(a**2 - c, d) == d
    ans = F.Plus(F.Power(a, 2), c.negate()).subs(F.Subtract(F.Power(a, 2), c), d);
    assertEquals(ans.toString(), //
        "d");
    // assert (a**2 - b - c).subs(a**2 - c, d) in [d - b, a**2 - b - c]
    ans = F.Plus(F.Power(a, 2), b.negate(), c.negate()).subs(F.Plus(F.Power(a, 2), c.negate()), d);
    assertEquals(ans.toString(), //
        "-b+d");
    // assert (a**2 - b - sqrt(a)).subs(a**2 - sqrt(a), c) == c - b
    ans = F.Plus(F.Power(a, 2), b.negate(), F.Sqrt(a).negate())
        .subs(F.Plus(F.Power(a, 2), F.Sqrt(a).negate()), c);
    assertEquals(ans.toString(), //
        "-b+c");
    // assert (a + b + exp(a + b)).subs(a + b, c) == c + exp(c)
    ans = F.Plus(a, b, F.Power(S.E, F.Plus(a, b))).subs(F.Plus(a, b), c);
    assertEquals(ans.toString(), //
        "c+E^c");

    // assert ((x + 1)*y).subs(x + 1, t) == t*y
    ans = F.Times(F.Plus(1, x), y).subs(F.Plus(1, x), t);
    assertEquals(ans.toString(), //
        "t*y");
    // assert ((-x - 1)*y).subs(x + 1, t) == -t*y
    ans = F.Times(F.Plus(F.CN1, x.negate()), y).subs(F.Plus(1, x), t);
    assertEquals(ans.toString(), //
        "-t*y");

    // assert ((x - 1)*y).subs(x + 1, t) == y*(t - 2)
    ans = F.Times(F.Plus(F.CN1, x), y).subs(F.Plus(1, x), t);
    assertEquals(ans.toString(), //
        "(-2+t)*y");
    // assert ((-x + 1)*y).subs(x + 1, t) == y*(-t + 2)
    ans = F.Times(F.Plus(F.C1, x.negate()), y).subs(F.Plus(1, x), t);
    assertEquals(ans.toString(), //
        "(2-t)*y");
    // # this should work every time:
    // e = a**2 - b - c
    // assert e.subs(Add(*e.args[:2]), d) == d + e.args[2]
    // assert e.subs(a**2 - c, d) == d - b
    //
    // # the fallback should recognize when a change has
    // # been made; while .1 == Rational(1, 10) they are not the same
    // # and the change should be made
    // assert (0.1 + a).subs(0.1, Rational(1, 10)) == Rational(1, 10) + a
    //
    // e = (-x*(-y + 1) - y*(y - 1))
    // ans = (-x*(x) - y*(-x)).expand()
    // assert e.subs(-y + 1, x) == ans
    //
    // #Test issue 18747
    // assert (exp(x) + cos(x)).subs(x, oo) == oo
    // assert Add(*[AccumBounds(-1, 1), oo]) == oo
    // assert Add(*[oo, AccumBounds(-1, 1)]) == oo
  }


  @Test
  public void test_mul() {
    ISymbol a = F.a;
    ISymbol b = F.b;
    ISymbol c = F.c;
    ISymbol x = F.x;
    ISymbol y = F.y;
    ISymbol z = F.z;

    // A, B, C = symbols('A B C', commutative=0)

    // TODO
    // assert (-2*x**3/9).subs(x/3, z) == -2*x*z**2
    IExpr rans = F.Times(F.QQ(-2, 9), F.Power(x, 3)).subs(F.Times(F.C1D3, x), z);
    assertEquals(rans.toString(), //
        "-2/9*x^3");

    // assert (x*y*z).subs(z*x, y) == y**2
    IExpr ans = F.Times(x, y, z).subs(F.Times(z, x), y);
    assertEquals(ans.toString(), //
        "y^2");
    // assert (z*x).subs(1/x, z) == 1
    ans = F.Times(x, z).subs(x.inverse(), z);
    assertEquals(ans.toString(), //
        "1");
    // assert (z*x**4).subs(x**2, z) == z**3
    ans = F.Times(F.Power(x, 4), z).subs(F.Power(x, 2), z);
    assertEquals(ans.toString(), //
        "z^3");
    // assert (z*x**3).subs(1/x, z) == 1/z**2
    ans = F.Times(F.Power(x, 3), z).subs(x.inverse(), z);
    assertEquals(ans.toString(), //
        "1/z^2");
    // assert (z*x**(-3)).subs(x**(-2), z) == z/x**3
    ans = F.Times(F.Power(x, -3), z).subs(F.Power(x, -2), z);
    assertEquals(ans.toString(), //
        "z/x^3");
    // assert (z*x**(-4)).subs(x**(-2), z) == z/x**3
    ans = F.Times(F.Power(x, -4), z).subs(F.Power(x, -2), z);
    assertEquals(ans.toString(), //
        "z^3");
    // assert (x*y/z).subs(1/z, a) == a*x*y
    ans = F.Times(x, y, z.inverse()).subs(z.inverse(), a);
    assertEquals(ans.toString(), //
        "a*x*y");
    // assert (x*y/z).subs(x/z, a) == a*y
    ans = F.Times(x, y, z.inverse()).subs(F.Times(x, z.inverse()), a);
    assertEquals(ans.toString(), //
        "a*y");
    // assert (x*y/z).subs(y/z, a) == a*x
    ans = F.Times(x, y, z.inverse()).subs(F.Times(y, z.inverse()), a);
    assertEquals(ans.toString(), //
        "a*x");
    // assert (x*y/z).subs(x/z, 1/a) == y/a
    ans = F.Times(x, y, z.inverse()).subs(F.Times(x, z.inverse()), a.inverse());
    assertEquals(ans.toString(), //
        "y/a");
    // assert (x*y/z).subs(x, 1/a) == y/(z*a)
    ans = F.Times(x, y, z.inverse()).subs(x, a.inverse());
    assertEquals(ans.toString(), //
        "y/(a*z)");
    // assert (2*x*y).subs(5*x*y, z) != z*Rational(2, 5)
    ans = F.Times(2, x, y).subs(F.Times(5, x, y), z);
    assertEquals(ans.toString(), //
        "2*x*y");
    // assert (x*y*A).subs(x*y, a) == a*A

    // assert (x**2*y**(x*Rational(3, 2))).subs(x*y**(x/2), 2) == 4*y**(x/2)
    ans = F.Times(F.Power(x, 2), F.Power(y, F.Times(x, F.C3D2)))
        .subs(F.Times(x, F.Power(y, F.Times(x, F.C1D2))), F.C2);
    assertEquals(ans.toString(), //
        "4*y^(x/2)");

    // assert (x*exp(x*2)).subs(x*exp(x), 2) == 2*exp(x)
    ans = F.Times(x, F.Power(S.E, F.Times(F.C2, x))).subs(F.Times(x, F.Power(S.E, x)), F.C2);
    assertEquals(ans.toString(), //
        "2*E^x");
    // assert ((x**(2*y))**3).subs(x**y, 2) == 64
    ans = F.Power(F.Power(x, F.Times(F.C2, y)), 3).subs(F.Power(x, y), F.C2);
    assertEquals(ans.toString(), //
        "64");
    // assert (x*A*B).subs(x*A, y) == y*B
    // assert (x*y*(1 + x)*(1 + x*y)).subs(x*y, 2) == 6*(1 + x)
    ans = F.Times(x, y, F.Plus(1, x), F.Plus(1, F.Times(x, y))).subs(F.Times(x, y), F.C2);
    assertEquals(ans.toString(), //
        "6*(1+x)");

    // assert ((1 + A*B)*A*B).subs(A*B, x*A*B)
    // assert (x*a/z).subs(x/z, A) == a*A
    // assert (x**3*A).subs(x**2*A, a) == a*x
    // assert (x**2*A*B).subs(x**2*B, a) == a*A
    // assert (x**2*A*B).subs(x**2*A, a) == a*B
    // assert (b*A**3/(a**3*c**3)).subs(a**4*c**3*A**3/b**4, z) == \
    // b*A**3/(a**3*c**3)
    // assert (6*x).subs(2*x, y) == 3*y
    ans = F.Times(6, x).subs(F.Times(2, x), y);
    assertEquals(ans.toString(), //
        "3*y");
    // assert (y*exp(x*Rational(3, 2))).subs(y*exp(x), 2) == 2*exp(x/2)
    ans = F.Times(y, F.Power(S.E, F.Times(F.C3D2, x))).subs(F.Times(y, F.Power(S.E, x)), F.C2);
    assertEquals(ans.toString(), //
        "2*E^(x/2)");

    // assert (A**2*B*A**2*B*A**2).subs(A*B*A, C) == A*C**2*A
    // assert (x*A**3).subs(x*A, y) == y*A**2
    // assert (x**2*A**3).subs(x*A, y) == y**2*A
    // assert (x*A**3).subs(x*A, B) == B*A**2
    // assert (x*A*B*A*exp(x*A*B)).subs(x*A, B) == B**2*A*exp(B*B)
    // assert (x**2*A*B*A*exp(x*A*B)).subs(x*A, B) == B**3*exp(B**2)
    // assert (x**3*A*exp(x*A*B)*A*exp(x*A*B)).subs(x*A, B) == \
    // x*B*exp(B**2)*B*exp(B**2)
    // assert (x*A*B*C*A*B).subs(x*A*B, C) == C**2*A*B
    // assert (-I*a*b).subs(a*b, 2) == -2*I
    ans = F.Times(F.CNI, a, b).subs(F.Times(a, b), F.C2);
    assertEquals(ans.toString(), //
        "-I*2");
    // # sympy issue 6361
    // assert (-8*I*a).subs(-2*a, 1) == 4*I
    ans = F.Times(F.CN8, F.CI, a).subs(F.Times(F.CN2, a), F.C1);
    assertEquals(ans.toString(), //
        "I*4");
    // assert (-I*a).subs(-a, 1) == I
    ans = F.Times(F.CNI, a).subs(F.Times(F.CN1, a), F.C1);
    assertEquals(ans.toString(), //
        "I");
    // # issue 6441
    // assert (4*x**2).subs(2*x, y) == y**2
    ans = F.Times(F.C4, F.Power(x, 2)).subs(F.Times(2, x), y);
    assertEquals(ans.toString(), //
        "y^2");
    // assert (2*4*x**2).subs(2*x, y) == 2*y**2
    ans = F.Times(F.C8, F.Power(x, 2)).subs(F.Times(2, x), y);
    assertEquals(ans.toString(), //
        "2*y^2");
    // assert (-x**3/9).subs(-x/3, z) == -z**2*x
    ans = F.Times(F.QQ(-1, 9), F.Power(x, 3)).subs(F.Times(F.CN1D3, x), z);
    assertEquals(ans.toString(), //
        "-x*z^2");
    // assert (x**3/9).subs(x/3, z) == z**2*x
    ans = F.Times(F.QQ(1, 9), F.Power(x, 3)).subs(F.Times(F.C1D3, x), z);
    assertEquals(ans.toString(), //
        "x*z^2");
    // assert (x**3/9).subs(-x/3, z) == -z**2*x
    ans = F.Times(F.QQ(1, 9), F.Power(x, 3)).subs(F.Times(F.CN1D3, x), z);
    assertEquals(ans.toString(), //
        "x^3/9");
    // assert (-x**3/9).subs(x/3, z) == -z**2*x
    ans = F.Times(F.QQ(-1, 9), F.Power(x, 3)).subs(F.Times(F.C1D3, x), z);
    assertEquals(ans.toString(), //
        "-x*z^2");

    // TODO
    // assert (-2*x**3/9).subs(x/3, z) == -2*x*z**2
    ans = F.Times(F.QQ(-2, 9), F.Power(x, 3)).subs(F.Times(F.C1D3, x), z);
    assertEquals(ans.toString(), //
        "-2/9*x^3");


    // assert (-2*x**3/9).subs(-x/3, z) == -2*x*z**2
    ans = F.Times(F.QQ(-2, 9), F.Power(x, 3)).subs(F.Times(F.CN1D3, x), z);
    assertEquals(ans.toString(), //
        "-2/9*x^3");

    // TODO
    // assert (-2*x**3/9).subs(-2*x, z) == z*x**2/9
    ans = F.Times(F.QQ(-2, 9), F.Power(x, 3)).subs(F.Times(-2, x), z);
    assertEquals(ans.toString(), //
        "-2/9*x^3");

    // TODO
    // assert (-2*x**3/9).subs(2*x, z) == -z*x**2/9
    ans = F.Times(F.QQ(-2, 9), F.Power(x, 3)).subs(F.Times(2, x), z);
    assertEquals(ans.toString(), //
        "-2/9*x^3");

    // TODO
    // assert (2*(3*x/5/7)**2).subs(3*x/5, z) == 2*(Rational(1, 7))**2*z**2
    ans = F.Times(2, F.Power(F.Times(F.QQ(3, 35), x), 2)).subs(F.Times(F.QQ(3, 5), x), z);
    assertEquals(ans.toString(), //
        "2*(3/35*x)^2");

    // assert (4*x).subs(-2*x, z) == 4*x // try keep subs literal
    ans = F.Times(4, x).subs(F.Times(-2, x), z);
    assertEquals(ans.toString(), //
        "4*x");
  }

  @Test
  public void test_issue_22033() {
    // xr = Symbol('xr', real=True)
    // e = (1/xr)
    // assert e.subs(xr**2, y) == e
    ISymbol xr = F.Dummy("xr");
    IExpr e = xr.inverse();
    IExpr ans = e.subs(F.Sqr(xr), F.y);
    assertEquals(ans.toString(), //
        "1/xr");
  }


  @Test
  public void test_guard_against_indeterminate_evaluation() {
    // eq = x**y

    // assert eq.subs({x: 1, y: oo}) is S.NaN
    // assert eq.subs([(x, 1), (y, oo)], simultaneous=True) is S.NaN
    IExpr eq = F.Power(F.x, F.y);
    IExpr ans = eq.subs(F.List(F.Rule(F.x, F.C1), F.Rule(F.y, F.oo)));

    // assert eq.subs([(x, 1), (y, oo)]) == 1 # because 1**y == 1
    assertEquals(ans.toString(), //
        "1");

    // assert eq.subs([(y, oo), (x, 1)]) is S.NaN
    ans = eq.subs(F.List(F.Rule(F.y, F.oo), F.Rule(F.x, F.C1)));
    assertEquals(ans.toString(), //
        "Indeterminate");
  }
}
