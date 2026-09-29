package org.matheclipse.core.sympy.assumptions;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Port of <a href=
 * "https://github.com/sympy/sympy/blob/master/sympy/assumptions/tests/test_refine.py">test_refine.py</a>.
 *
 * <p>
 * The sympy predicates are translated to Symja assumptions:
 * <code>Q.positive(x) -&gt; x&gt;0</code>, <code>Q.real(x) -&gt; Element(x,Reals)</code>,
 * <code>Q.integer(x) -&gt; Element(x,Integers)</code>,
 * <code>Q.even(x) -&gt; Element(x/2,Integers)</code>, <code>Q.odd(x) -&gt;
 * Element((x-1)/2,Integers)</code>, <code>Q.zero(x) -&gt; x==0</code>. The expected values are the
 * Symja forms of sympy's results. Not ported: the cases with <code>Q.imaginary</code>,
 * <code>Q.infinite</code>, <code>Q.extended_real</code> (no Symja assumptions), non-commutative
 * symbols, matrix elements, Heaviside with a second argument, and the rule
 * <code>refine((x**3)**(1/3), Q.real(x))</code>, which is wrong for odd powers in sympy.
 */
public class TestRefine extends ExprEvaluatorTestCase {

  @Test
  public void testAbs() {
    // sympy: Refine(Abs(x), x>0) == x
    check("Refine(Abs(x), x>0)", //
        "x");
    // sympy: Refine(1 + Abs(x), x>0) == 1 + x
    check("Refine(1 + Abs(x), x>0)", //
        "1+x");
    // sympy: Refine(Abs(x), x<0) == -x
    check("Refine(Abs(x), x<0)", //
        "-x");
    // sympy: Refine(1 + Abs(x), x<0) == 1 - x
    check("Refine(1 + Abs(x), x<0)", //
        "1-x");
    // sympy: Refine(Abs(x^2), Element(x,Reals)) == x^2
    check("Refine(Abs(x^2), Element(x,Reals))", //
        "x^2");
    // sympy: Refine(Abs(z)^2, Element(z,Reals)) == z^2
    check("Refine(Abs(z)^2, Element(z,Reals))", //
        "z^2");
  }

  @Test
  public void testPow1() {
    // sympy: Refine((-1)^x, Element(x/2,Integers)) == 1
    check("Refine((-1)^x, Element(x/2,Integers))", //
        "1");
    // sympy: Refine((-1)^x, Element((x-1)/2,Integers)) == -1
    check("Refine((-1)^x, Element((x-1)/2,Integers))", //
        "-1");
    // sympy: Refine((-2)^x, Element(x/2,Integers)) == 2^x
    check("Refine((-2)^x, Element(x/2,Integers))", //
        "2^x");
    // sympy: Refine(Sqrt(x^2), Element(x,Reals)) == Abs(x)
    check("Refine(Sqrt(x^2), Element(x,Reals))", //
        "Abs(x)");
    // sympy: Refine(Sqrt(x^2), x>0) == x
    check("Refine(Sqrt(x^2), x>0)", //
        "x");
    // sympy: Refine((x^3)^(1/3), x>0) == x
    check("Refine((x^3)^(1/3), x>0)", //
        "x");
    // sympy: Refine(Sqrt(1/x), x>0) == 1/Sqrt(x)
    check("Refine(Sqrt(1/x), x>0)", //
        "1/Sqrt(x)");
    // sympy: Refine((-1)^(x + y), Element(x/2,Integers)) == (-1)^y
    check("Refine((-1)^(x + y), Element(x/2,Integers))", //
        "(-1)^y");
    // sympy: Refine((-1)^(x + y + z), Element((x-1)/2,Integers) && Element((z-1)/2,Integers)) ==
    // (-1)^y
    check("Refine((-1)^(x + y + z), Element((x-1)/2,Integers) && Element((z-1)/2,Integers))", //
        "(-1)^y");
    // sympy: Refine((-1)^(x + y + 1), Element((x-1)/2,Integers)) == (-1)^y
    check("Refine((-1)^(x + y + 1), Element((x-1)/2,Integers))", //
        "(-1)^y");
    // sympy: Refine((-1)^(x + y + 2), Element((x-1)/2,Integers)) == (-1)^(y + 1)
    check("Refine((-1)^(x + y + 2), Element((x-1)/2,Integers))", //
        "(-1)^(1+y)");
    // sympy: Refine((-1)^(x + 3)) == (-1)^(x + 1)
    check("Refine((-1)^(x + 3))", //
        "(-1)^(1+x)");
    // sympy: Refine((-1)^((-1)^x/2 - 1/2), Element(x,Integers)) == (-1)^x
    check("Refine((-1)^((-1)^x/2 - 1/2), Element(x,Integers))", //
        "(-1)^x");
    // sympy: Refine((-1)^((-1)^x/2 + 1/2), Element(x,Integers)) == (-1)^(x + 1)
    check("Refine((-1)^((-1)^x/2 + 1/2), Element(x,Integers))", //
        "(-1)^(1+x)");
    // sympy: Refine((-1)^((-1)^x/2 + 5/2), Element(x,Integers)) == (-1)^(x + 1)
    check("Refine((-1)^((-1)^x/2 + 5/2), Element(x,Integers))", //
        "(-1)^(1+x)");
  }

  @Test
  public void testPow2() {
    // sympy: Refine((-1)^((-1)^x/2 - 7/2), Element(x,Integers)) == (-1)^(x + 1)
    check("Refine((-1)^((-1)^x/2 - 7/2), Element(x,Integers))", //
        "(-1)^(1+x)");
    // sympy: Refine((-1)^((-1)^x/2 - 9/2), Element(x,Integers)) == (-1)^x
    check("Refine((-1)^((-1)^x/2 - 9/2), Element(x,Integers))", //
        "(-1)^x");
    // sympy: Refine(Abs(x)^2, Element(x,Reals)) == x^2
    check("Refine(Abs(x)^2, Element(x,Reals))", //
        "x^2");
    // sympy: Refine(Abs(x)^3, Element(x,Reals)) == Abs(x)^3
    check("Refine(Abs(x)^3, Element(x,Reals))", //
        "Abs(x)^3");
    // sympy: Refine(Abs(x)^2) == Abs(x)^2
    check("Refine(Abs(x)^2)", //
        "Abs(x)^2");
  }

  @Test
  public void testExp() {
    // sympy: Refine(Exp(Pi*I*2*x), Element(x,Integers)) == 1
    check("Refine(Exp(Pi*I*2*x), Element(x,Integers))", //
        "1");
    // sympy: Refine(Exp(Pi*I*x), Element(x/2,Integers)) == 1
    check("Refine(Exp(Pi*I*x), Element(x/2,Integers))", //
        "1");
    // sympy: Refine(Exp(Pi*I*2*(x + 1/2)), Element(x,Integers)) == -1
    check("Refine(Exp(Pi*I*2*(x + 1/2)), Element(x,Integers))", //
        "-1");
    // sympy: Refine(Exp(Pi*I*x), Element((x-1)/2,Integers)) == -1
    check("Refine(Exp(Pi*I*x), Element((x-1)/2,Integers))", //
        "-1");
    // sympy: Refine(Exp(Pi*I*2*(x + 1/4)), Element(x,Integers)) == I
    check("Refine(Exp(Pi*I*2*(x + 1/4)), Element(x,Integers))", //
        "I");
    // sympy: Refine(Exp(Pi*I*2*(x + 3/4)), Element(x,Integers)) == -I
    check("Refine(Exp(Pi*I*2*(x + 3/4)), Element(x,Integers))", //
        "-I");
    // sympy: Refine(Exp(Pi*I*(x + 1/2)), Element(x/2,Integers)) == I
    check("Refine(Exp(Pi*I*(x + 1/2)), Element(x/2,Integers))", //
        "I");
    // sympy: Refine(Exp(Pi*I*(x + 1/2)), Element((x-1)/2,Integers)) == -I
    check("Refine(Exp(Pi*I*(x + 1/2)), Element((x-1)/2,Integers))", //
        "-I");
    // sympy: Refine(Exp(Pi*I*(x + 3/2)), Element((x-1)/2,Integers)) == I
    check("Refine(Exp(Pi*I*(x + 3/2)), Element((x-1)/2,Integers))", //
        "I");
    // sympy: Refine(Exp(Pi*I*(x + 1/2)), Element(x,Integers)) == I*(-1)^x
    check("Refine(Exp(Pi*I*(x + 1/2)), Element(x,Integers))", //
        "I*(-1)^x");
    // sympy: Refine(Exp(2*Pi*I*(x + y + 1/4)), Element(x,Integers) && Element(y,Integers)) == I
    check("Refine(Exp(2*Pi*I*(x + y + 1/4)), Element(x,Integers) && Element(y,Integers))", //
        "I");
    // sympy: Refine(Exp(Pi*I*x), Element(x,Integers)) == (-1)^x
    check("Refine(Exp(Pi*I*x), Element(x,Integers))", //
        "(-1)^x");
  }

  @Test
  public void testPiecewise() {
    // sympy: Refine(Piecewise({{1, x < 0}}, 3), x < 0) == 1
    check("Refine(Piecewise({{1, x < 0}}, 3), x < 0)", //
        "1");
    // sympy: Refine(Piecewise({{1, x < 0}}, 3), !(x < 0)) == 3
    check("Refine(Piecewise({{1, x < 0}}, 3), !(x < 0))", //
        "3");
    // sympy: Refine(Piecewise({{1, x < 0}}, 3), y < 0) == Piecewise({{1, x < 0}}, 3)
    check("Refine(Piecewise({{1, x < 0}}, 3), y < 0)", //
        "Piecewise({{1,x<0}},3)");
    // sympy: Refine(Piecewise({{1, x > 0}}, 3), x > 0) == 1
    check("Refine(Piecewise({{1, x > 0}}, 3), x > 0)", //
        "1");
    // sympy: Refine(Piecewise({{1, x > 0}}, 3), !(x > 0)) == 3
    check("Refine(Piecewise({{1, x > 0}}, 3), !(x > 0))", //
        "3");
    // sympy: Refine(Piecewise({{1, x <= 0}}, 3), x <= 0) == 1
    check("Refine(Piecewise({{1, x <= 0}}, 3), x <= 0)", //
        "1");
    // sympy: Refine(Piecewise({{1, x <= 0}}, 3), !(x <= 0)) == 3
    check("Refine(Piecewise({{1, x <= 0}}, 3), !(x <= 0))", //
        "3");
    // sympy: Refine(Piecewise({{1, x >= 0}}, 3), x >= 0) == 1
    check("Refine(Piecewise({{1, x >= 0}}, 3), x >= 0)", //
        "1");
    // sympy: Refine(Piecewise({{1, x >= 0}}, 3), !(x >= 0)) == 3
    check("Refine(Piecewise({{1, x >= 0}}, 3), !(x >= 0))", //
        "3");
    // sympy: Refine(Piecewise({{1, x == 0}}, 3), x == 0) == 1
    check("Refine(Piecewise({{1, x == 0}}, 3), x == 0)", //
        "1");
    // sympy: Refine(Piecewise({{1, x == 0}}, 3), 0 == x) == 1
    check("Refine(Piecewise({{1, x == 0}}, 3), 0 == x)", //
        "1");
    // sympy: Refine(Piecewise({{1, x == 0}}, 3), !(x == 0)) == 3
    check("Refine(Piecewise({{1, x == 0}}, 3), !(x == 0))", //
        "3");
    // sympy: Refine(Piecewise({{1, x != 0}}, 3), x != 0) == 1
    check("Refine(Piecewise({{1, x != 0}}, 3), x != 0)", //
        "1");
    // sympy: Refine(Piecewise({{1, x != 0}}, 3), !(x != 0)) == 3
    check("Refine(Piecewise({{1, x != 0}}, 3), !(x != 0))", //
        "3");
  }

  @Test
  public void testAtan2() {
    // sympy: Refine(ArcTan(x,y), Element(y,Reals) && x>0) == ArcTan(y/x)
    check("Refine(ArcTan(x,y), Element(y,Reals) && x>0)", //
        "ArcTan(y/x)");
    // sympy: Refine(ArcTan(x,y), y<0 && x>0) == ArcTan(y/x)
    check("Refine(ArcTan(x,y), y<0 && x>0)", //
        "ArcTan(y/x)");
    // sympy: Refine(ArcTan(x,y), y<0 && x<0) == ArcTan(y/x) - Pi
    check("Refine(ArcTan(x,y), y<0 && x<0)", //
        "-Pi+ArcTan(y/x)");
    // sympy: Refine(ArcTan(x,y), y>0 && x<0) == ArcTan(y/x) + Pi
    check("Refine(ArcTan(x,y), y>0 && x<0)", //
        "Pi+ArcTan(y/x)");
    // sympy: Refine(ArcTan(x,y), y==0 && x<0) == Pi
    check("Refine(ArcTan(x,y), y==0 && x<0)", //
        "Pi");
    // sympy: Refine(ArcTan(x,y), y>0 && x==0) == Pi/2
    check("Refine(ArcTan(x,y), y>0 && x==0)", //
        "Pi/2");
    // sympy: Refine(ArcTan(x,y), y<0 && x==0) == -Pi/2
    check("Refine(ArcTan(x,y), y<0 && x==0)", //
        "-Pi/2");
    // sympy: Refine(ArcTan(x,y), y==0 && x==0) == Indeterminate
    check("Refine(ArcTan(x,y), y==0 && x==0)", //
        "Indeterminate");
  }

  @Test
  public void testRe() {
    // sympy: Refine(Re(x), Element(x,Reals)) == x
    check("Refine(Re(x), Element(x,Reals))", //
        "x");
    // sympy: Refine(Re(x+y), Element(x,Reals) && Element(y,Reals)) == x + y
    check("Refine(Re(x+y), Element(x,Reals) && Element(y,Reals))", //
        "x+y");
    // sympy: Refine(Re(x*y), Element(x,Reals) && Element(y,Reals)) == x*y
    check("Refine(Re(x*y), Element(x,Reals) && Element(y,Reals))", //
        "x*y");
    // sympy: Refine(Re(x*y*z), Element(x,Reals) && Element(y,Reals) && Element(z,Reals)) == x*y*z
    check("Refine(Re(x*y*z), Element(x,Reals) && Element(y,Reals) && Element(z,Reals))", //
        "x*y*z");
  }

  @Test
  public void testIm() {
    // sympy: Refine(Im(x), Element(x,Reals)) == 0
    check("Refine(Im(x), Element(x,Reals))", //
        "0");
  }

  @Test
  public void testComplex() {
    // sympy: Refine(Re(1/(x + I*y)), Element(x,Reals) && Element(y,Reals)) == x/(x^2 + y^2)
    check("Refine(Re(1/(x + I*y)), Element(x,Reals) && Element(y,Reals))", //
        "x/(x^2+y^2)");
    // sympy: Refine(Im(1/(x + I*y)), Element(x,Reals) && Element(y,Reals)) == -y/(x^2 + y^2)
    check("Refine(Im(1/(x + I*y)), Element(x,Reals) && Element(y,Reals))", //
        "-y/(x^2+y^2)");
    // sympy: Refine(Re((w + I*x)*(y + I*z)), Element(w,Reals) && Element(x,Reals) &&
    // Element(y,Reals) && Element(z,Reals)) == w*y - x*z
    check(
        "Refine(Re((w + I*x)*(y + I*z)), Element(w,Reals) && Element(x,Reals) && Element(y,Reals) && Element(z,Reals))", //
        "w*y-x*z");
    // sympy: Refine(Im((w + I*x)*(y + I*z)), Element(w,Reals) && Element(x,Reals) &&
    // Element(y,Reals) && Element(z,Reals)) == w*z + x*y
    check(
        "Refine(Im((w + I*x)*(y + I*z)), Element(w,Reals) && Element(x,Reals) && Element(y,Reals) && Element(z,Reals))", //
        "x*y+w*z");
  }

  @Test
  public void testSign() {
    // sympy: Refine(Sign(x), x>0) == 1
    check("Refine(Sign(x), x>0)", //
        "1");
    // sympy: Refine(Sign(x), x<0) == -1
    check("Refine(Sign(x), x<0)", //
        "-1");
    // sympy: Refine(Sign(x), x==0) == 0
    check("Refine(Sign(x), x==0)", //
        "0");
    // sympy: Refine(Sign(x)) == Sign(x)
    check("Refine(Sign(x))", //
        "Sign(x)");
    // sympy: Refine(Sign(Abs(x)), x!=0) == 1
    check("Refine(Sign(Abs(x)), x!=0)", //
        "1");
  }

  @Test
  public void testArg() {
    // sympy: Refine(Arg(x), x>0) == 0
    check("Refine(Arg(x), x>0)", //
        "0");
    // sympy: Refine(Arg(x), x<0) == Pi
    check("Refine(Arg(x), x<0)", //
        "Pi");
  }

  @Test
  public void testIssueRefine9384() {
    // sympy: Refine(Piecewise({{1, x < 0}}, 0), x>0) == 0
    check("Refine(Piecewise({{1, x < 0}}, 0), x>0)", //
        "0");
    // sympy: Refine(Piecewise({{1, x < 0}}, 0), x<0) == 1
    check("Refine(Piecewise({{1, x < 0}}, 0), x<0)", //
        "1");
    // sympy: Refine(Piecewise({{1, x > 0}}, 0), x>0) == 1
    check("Refine(Piecewise({{1, x > 0}}, 0), x>0)", //
        "1");
    // sympy: Refine(Piecewise({{1, x > 0}}, 0), x<0) == 0
    check("Refine(Piecewise({{1, x > 0}}, 0), x<0)", //
        "0");
  }

  @Test
  public void testRefineIssue12724() {
    // sympy: Refine(Abs(x*y), x>0) == x*Abs(y)
    check("Refine(Abs(x*y), x>0)", //
        "x*Abs(y)");
    // sympy: Refine(Abs(x*y*z), x>0) == x*Abs(y*z)
    check("Refine(Abs(x*y*z), x>0)", //
        "x*Abs(y*z)");
    // sympy: Refine(Abs(x*y1^2*z), x>0 && Element(y1,Reals)) == x*y1^2*Abs(z)
    check("Refine(Abs(x*y1^2*z), x>0 && Element(y1,Reals))", //
        "x*y1^2*Abs(z)");
  }

  @Test
  public void testSinCos() {
    // sympy: Refine(Cos(n*Pi/2), Element((n-1)/2,Integers)) == 0
    check("Refine(Cos(n*Pi/2), Element((n-1)/2,Integers))", //
        "0");
    // sympy: Refine(Cos(n*Pi), Element(n/2,Integers)) == 1
    check("Refine(Cos(n*Pi), Element(n/2,Integers))", //
        "1");
    // sympy: Refine(Cos(n*Pi), Element((n-1)/2,Integers)) == -1
    check("Refine(Cos(n*Pi), Element((n-1)/2,Integers))", //
        "-1");
    // sympy: Refine(Sin(n*Pi), Element(n,Integers)) == 0
    check("Refine(Sin(n*Pi), Element(n,Integers))", //
        "0");
    // sympy: Refine(Sin(n*Pi/2), Element((n-1)/2,Integers) && Element((n-1)/4,Integers)) == 1
    check("Refine(Sin(n*Pi/2), Element((n-1)/2,Integers) && Element((n-1)/4,Integers))", //
        "1");
    // sympy: Refine(Sin(n*Pi/2), Element((n-1)/2,Integers) && Element(((n-1)/2-1)/2,Integers)) ==
    // -1
    check("Refine(Sin(n*Pi/2), Element((n-1)/2,Integers) && Element(((n-1)/2-1)/2,Integers))", //
        "-1");
    // sympy: Refine(Cos(n*Pi), Element(n,Integers)) == (-1)^n
    check("Refine(Cos(n*Pi), Element(n,Integers))", //
        "(-1)^n");
    // sympy: Refine(Sin(n*Pi/2), Element(n/2,Integers)) == 0
    check("Refine(Sin(n*Pi/2), Element(n/2,Integers))", //
        "0");
    // sympy: Refine(Cos(n*Pi/2), Element(n/2,Integers)) == (-1)^(n/2)
    check("Refine(Cos(n*Pi/2), Element(n/2,Integers))", //
        "(-1)^(n/2)");
    // sympy: Refine(Sin(n*Pi/2), Element((n-1)/2,Integers)) == (-1)^((n + 3)/2)
    check("Refine(Sin(n*Pi/2), Element((n-1)/2,Integers))", //
        "(-1)^(1/2*(-1+n))");
    // sympy: Refine(Sin(x + n*Pi), Element(n,Integers)) == (-1)^n*Sin(x)
    check("Refine(Sin(x + n*Pi), Element(n,Integers))", //
        "(-1)^n*Sin(x)");
    // sympy: Refine(Cos(x + n*Pi), Element(n,Integers)) == (-1)^n*Cos(x)
    check("Refine(Cos(x + n*Pi), Element(n,Integers))", //
        "(-1)^n*Cos(x)");
    // sympy: Refine(Sin(x + n*Pi), Element(n/2,Integers)) == Sin(x)
    check("Refine(Sin(x + n*Pi), Element(n/2,Integers))", //
        "Sin(x)");
    // sympy: Refine(Cos(x + n*Pi), Element(n/2,Integers)) == Cos(x)
    check("Refine(Cos(x + n*Pi), Element(n/2,Integers))", //
        "Cos(x)");
    // sympy: Refine(Sin(x + n*Pi), Element((n-1)/2,Integers)) == -Sin(x)
    check("Refine(Sin(x + n*Pi), Element((n-1)/2,Integers))", //
        "-Sin(x)");
    // sympy: Refine(Cos(x + n*Pi), Element((n-1)/2,Integers)) == -Cos(x)
    check("Refine(Cos(x + n*Pi), Element((n-1)/2,Integers))", //
        "-Cos(x)");
    // sympy: Refine(Sin(x - n*Pi), Element((n-1)/2,Integers)) == -Sin(x)
    check("Refine(Sin(x - n*Pi), Element((n-1)/2,Integers))", //
        "-Sin(x)");
    // sympy: Refine(Cos(x - n*Pi), Element(n/2,Integers)) == Cos(x)
    check("Refine(Cos(x - n*Pi), Element(n/2,Integers))", //
        "Cos(x)");
    // sympy: Refine(Sin(x + n*Pi/2), Element(n/2,Integers)) == (-1)^(n/2)*Sin(x)
    check("Refine(Sin(x + n*Pi/2), Element(n/2,Integers))", //
        "(-1)^(n/2)*Sin(x)");
    // sympy: Refine(Cos(x + n*Pi/2), Element(n/2,Integers)) == (-1)^(n/2)*Cos(x)
    check("Refine(Cos(x + n*Pi/2), Element(n/2,Integers))", //
        "(-1)^(n/2)*Cos(x)");
    // sympy: Refine(Sin(x + n*Pi/2), Element((n-1)/2,Integers)) == (-1)^((n + 3)/2)*Cos(x)
    check("Refine(Sin(x + n*Pi/2), Element((n-1)/2,Integers))", //
        "(-1)^(1/2*(-1+n))*Cos(x)");
    // sympy: Refine(Cos(x + n*Pi/2), Element((n-1)/2,Integers)) == (-1)^((n + 1)/2)*Sin(x)
    check("Refine(Cos(x + n*Pi/2), Element((n-1)/2,Integers))", //
        "(-1)^(1/2*(1+n))*Sin(x)");
    // sympy: Refine(Sin(x - n*Pi/2), Element((n-1)/2,Integers)) == -(-1)^((n + 3)/2)*Cos(x)
    check("Refine(Sin(x - n*Pi/2), Element((n-1)/2,Integers))", //
        "-(-1)^(1/2*(-1+n))*Cos(x)");
    // sympy: Refine(Cos(x - n*Pi/2), Element(n/2,Integers)) == (-1)^(n/2)*Cos(x)
    check("Refine(Cos(x - n*Pi/2), Element(n/2,Integers))", //
        "(-1)^(n/2)*Cos(x)");
    // sympy: Refine(Sin(x + y + 2*n*Pi), Element(n,Integers)) == Sin(x + y)
    check("Refine(Sin(x + y + 2*n*Pi), Element(n,Integers))", //
        "Sin(x+y)");
    // sympy: Refine(Cos(x + y + 2*n*Pi), Element(n,Integers)) == Cos(x + y)
    check("Refine(Cos(x + y + 2*n*Pi), Element(n,Integers))", //
        "Cos(x+y)");
    // sympy: Refine(Sin(x + n*Pi), n==0) == Sin(x)
    check("Refine(Sin(x + n*Pi), n==0)", //
        "Sin(x)");
    // sympy: Refine(Cos(x + n*Pi/2), Element(n,Integers)) == Cos(x + n*Pi/2)
    check("Refine(Cos(x + n*Pi/2), Element(n,Integers))", //
        "Cos(1/2*n*Pi+x)");
    // sympy: Refine(Cos(x + y + n*Pi/2), Element(n,Integers)) == Cos(x + y + n*Pi/2)
    check("Refine(Cos(x + y + n*Pi/2), Element(n,Integers))", //
        "Cos(1/2*n*Pi+x+y)");
    // sympy: Refine(Cos(x + n*Pi + m*Pi/2), Element(n,Integers) && Element(m/2,Integers)) ==
    // (-1)^(n + m/2)*Cos(x)
    // WMA writes several multiples of Pi/2 as I^k
    check("Refine(Cos(x + n*Pi + m*Pi/2), Element(n,Integers) && Element(m/2,Integers))", //
        "I^(m+2*n)*Cos(x)");
    // sympy: Refine(Cos(x + n*Pi + m*Pi/2), Element(n,Integers) && Element((m-1)/2,Integers)) ==
    // (-1)^(n + (m + 1)/2)*Sin(x)
    // WMA: -I^(-1+m+2*n)*Sin(x)
    check("Refine(Cos(x + n*Pi + m*Pi/2), Element(n,Integers) && Element((m-1)/2,Integers))", //
        "-Sin(x)/I^(1-m-2*n)");
    // sympy: Refine(Cos(x + n*Pi + m*Pi/2), Element(n,Integers) && Element(m,Integers)) ==
    // (-1)^n*Cos(x + m*Pi/2)
    // WMA: unevaluated, the integer m has an unknown parity
    check("Refine(Cos(x + n*Pi + m*Pi/2), Element(n,Integers) && Element(m,Integers))", //
        "Cos(1/2*m*Pi+n*Pi+x)");
    // sympy: Refine(Cos(x + (2*n + 1)*Pi + m*Pi/2), Element(n,Integers) && Element(m,Integers)) ==
    // -Cos(x + m*Pi/2)
    // WMA: unevaluated, the integer m has an unknown parity
    check("Refine(Cos(x + (2*n + 1)*Pi + m*Pi/2), Element(n,Integers) && Element(m,Integers))", //
        "Cos(1/2*m*Pi+(1+2*n)*Pi+x)");
    // the held (2*n+1) was sorted in place, which left a stale hash code in its parent
    check("Refine(Cos((2*n + 1)*Pi + x), Element(n,Integers))", //
        "-Cos(x)");
    check("Refine(Sin((2*n + 1)*Pi + x), Element(n,Integers))", //
        "-Sin(x)");
    check("Hold((2*n + 1)*Pi) // FullForm", //
        "Hold(Times(Plus(Times(2, n), 1), Pi))");
    // sympy: Refine(Sin(x - (2*n)*Pi + m*Pi/2), Element(n,Integers) && Element(m,Integers)) ==
    // Sin(x + m*Pi/2)
    // WMA: unevaluated, the integer m has an unknown parity
    check("Refine(Sin(x - (2*n)*Pi + m*Pi/2), Element(n,Integers) && Element(m,Integers))", //
        "Sin(1/2*m*Pi-2*n*Pi+x)");
    // sympy: Refine(Cos(x + n*Pi + k*Pi/2 + m*Pi/2), Element(n,Integers) &&
    // Element((k-1)/2,Integers) && Element(m,Integers)) == (-1)^(n + (k + 1)/2)*Sin(x + m*Pi/2)
    // WMA: unevaluated, the integer m has an unknown parity
    check(
        "Refine(Cos(x + n*Pi + k*Pi/2 + m*Pi/2), Element(n,Integers) && Element((k-1)/2,Integers) && Element(m,Integers))", //
        "Cos(1/2*k*Pi+1/2*m*Pi+n*Pi+x)");
    // sympy: Refine(Sin(x + n*Pi + k*Pi/2 + m*Pi/2), Element(n,Integers) &&
    // Element((k-1)/2,Integers) && Element(m,Integers)) == (-1)^(n + (k + 3)/2)*Cos(x + m*Pi/2)
    // WMA: unevaluated, the integer m has an unknown parity
    check(
        "Refine(Sin(x + n*Pi + k*Pi/2 + m*Pi/2), Element(n,Integers) && Element((k-1)/2,Integers) && Element(m,Integers))", //
        "Sin(1/2*k*Pi+1/2*m*Pi+n*Pi+x)");
    // sympy: Refine(Cos(x + n*Pi/2 + k*Pi/2 + m*Pi/2), Element((n-1)/2,Integers) &&
    // Element((k-1)/2,Integers) && Element(m,Integers)) == (-1)^((n + k)/2)*Cos(x + m*Pi/2)
    // WMA: unevaluated, the integer m has an unknown parity
    check(
        "Refine(Cos(x + n*Pi/2 + k*Pi/2 + m*Pi/2), Element((n-1)/2,Integers) && Element((k-1)/2,Integers) && Element(m,Integers))", //
        "Cos(1/2*k*Pi+1/2*m*Pi+1/2*n*Pi+x)");

    // the multiples of Pi/2 are only removed if the parity of their sum is known
    check("Refine(Cos(x + n*Pi), Element(n, Integers))", //
        "(-1)^n*Cos(x)");
    check("Refine(Cos(x + 2*n*Pi + m*Pi/2), Element(n, Integers) && Element(m, Integers))", //
        "Cos(1/2*m*Pi+2*n*Pi+x)");
    check("Refine(Cos(x + n*Pi + m*Pi), Element(n, Integers) && Element(m, Integers))", //
        "I^(2*m+2*n)*Cos(x)");
    // WMA: -I^(-1+m+2*n)*Sin(x), Symja's normal form of that product
    check("Refine(Cos(x + n*Pi + m*Pi/2), Element(n, Integers) && Element((m - 1)/2, Integers))", //
        "-Sin(x)/I^(1-m-2*n)");
    check("Refine(Cos(x + n*Pi + y*Pi), Element(n, Integers))", //
        "(-1)^n*Cos(x+Pi*y)");
    check("Assuming(Element(n, Integers), Cos(x + (2*n + 1)*Pi))", //
        "Cos((1+2*n)*Pi+x)");
    check("Refine(Sec(x + n*Pi + m*Pi/2), Element(n,Integers) && Element(m,Integers))", //
        "Sec(1/2*m*Pi+n*Pi+x)");
    check("Refine(Csc(x + n*Pi + m*Pi), Element(n,Integers) && Element(m,Integers))", //
        "I^(2*m+2*n)*Csc(x)");
    check("I^(2*n) // FullForm", //
        "Power(Complex(0,1), Times(2, n))");
    // sympy: Refine(Cos(x), x==0) == 1
    check("Refine(Cos(x), x==0)", //
        "1");
    // sympy: Refine(Sin(x), x==0) == 0
    check("Refine(Sin(x), x==0)", //
        "0");
  }

  @Test
  public void testFloorCeiling() {
    // sympy: Refine(Floor(x), Element(x,Integers)) == x
    check("Refine(Floor(x), Element(x,Integers))", //
        "x");
    // sympy: Refine(Ceiling(x), Element(x,Integers)) == x
    check("Refine(Ceiling(x), Element(x,Integers))", //
        "x");
    // sympy: Refine(Floor(y), Element(y,Reals)) == Floor(y)
    check("Refine(Floor(y), Element(y,Reals))", //
        "Floor(y)");
    // sympy: Refine(Ceiling(y), Element(y,Reals)) == Ceiling(y)
    check("Refine(Ceiling(y), Element(y,Reals))", //
        "Ceiling(y)");
    // sympy: Refine(Floor(x + y), Element(x,Integers)) == x + Floor(y)
    check("Refine(Floor(x + y), Element(x,Integers))", //
        "x+Floor(y)");
    // sympy: Refine(Ceiling(x + y), Element(x,Integers)) == x + Ceiling(y)
    check("Refine(Ceiling(x + y), Element(x,Integers))", //
        "x+Ceiling(y)");
    // sympy: Refine(Floor(x + y + z), Element(x,Integers) && Element(y,Integers)) == x + y +
    // Floor(z)
    check("Refine(Floor(x + y + z), Element(x,Integers) && Element(y,Integers))", //
        "x+y+Floor(z)");
    // sympy: Refine(Ceiling(x + y + z), Element(x,Integers) && Element(z,Integers)) == x + z +
    // Ceiling(y)
    check("Refine(Ceiling(x + y + z), Element(x,Integers) && Element(z,Integers))", //
        "x+z+Ceiling(y)");
    // sympy: Refine(Floor(x + y - z)) == Floor(x + y - z)
    check("Refine(Floor(x + y - z))", //
        "Floor(x+y-z)");
    // sympy: Refine(Ceiling(Ceiling(x) + y + Floor(z))) == Ceiling(x) + Ceiling(y) + Floor(z)
    check("Refine(Ceiling(Ceiling(x) + y + Floor(z)))", //
        "Ceiling(x)+Ceiling(y)+Floor(z)");
    // sympy: Refine(Floor(Floor(x) + Floor(y))) == Floor(x) + Floor(y)
    check("Refine(Floor(Floor(x) + Floor(y)))", //
        "Floor(x)+Floor(y)");
    // sympy: Refine(Ceiling(Ceiling(x) - Ceiling(y))) == Ceiling(x) - Ceiling(y)
    check("Refine(Ceiling(Ceiling(x) - Ceiling(y)))", //
        "Ceiling(x)-Ceiling(y)");
  }

  @Test
  public void testHeaviside() {
    // sympy: Refine(HeavisideTheta(x), x>0) == 1
    check("Refine(HeavisideTheta(x), x>0)", //
        "1");
    // sympy: Refine(HeavisideTheta(x), x<0) == 0
    check("Refine(HeavisideTheta(x), x<0)", //
        "0");
    // sympy: Refine(HeavisideTheta(x), x>=0) == HeavisideTheta(x)
    check("Refine(HeavisideTheta(x), x>=0)", //
        "HeavisideTheta(x)");
    // sympy: Refine(HeavisideTheta(x), x<=0) == HeavisideTheta(x)
    check("Refine(HeavisideTheta(x), x<=0)", //
        "HeavisideTheta(x)");
    // sympy: Refine(HeavisideTheta(x)) == HeavisideTheta(x)
    check("Refine(HeavisideTheta(x))", //
        "HeavisideTheta(x)");
  }

  @Test
  public void testSinCosNumericRemainder() {
    // deviation from sympy: a numeric remainder isn't split off
    check("Refine(Sin(Pi*(1/4+m)), Element(m, Integers))", //
        "Sin((1/4+m)*Pi)");
    check("Refine(Csc(Pi*(1/4+m)), Element(m, Integers))", //
        "Csc((1/4+m)*Pi)");
  }
}
