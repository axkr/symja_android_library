package org.matheclipse.core.system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * JUnit test cases extracted from algebra.rs testing algebraic operations.
 */
public class AlgebraTest extends ExprEvaluatorTestCase {

  /** The JUnit setup method */
  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testPolynomialQ() {
    check("PolynomialQ(x^2 + 1, x)", //
        "True");
    check("PolynomialQ(3*x^3 + 2*x + 1, x)", //
        "True");
    check("PolynomialQ(5, x)", //
        "True");
    check("PolynomialQ(x, x)", //
        "True");
    check("PolynomialQ(Sin(x), x)", //
        "False");
    check("PolynomialQ(1/x, x)", //
        "False");
    check("PolynomialQ(x^2 + y, x)", //
        "True");
    check("PolynomialQ(x + y^2, {x, y})", //
        "True");
    check("PolynomialQ(x^2 + 2*x*y + y^2, {x, y})", //
        "True");
    check("PolynomialQ(Sin(x) + y, {x, y})", //
        "False");
    check("PolynomialQ(f(a) + f(a)^2, f(a))", //
        "True");
    check("PolynomialQ(f(a) + g(b)^2, {f(a), g(b)})", //
        "True");
    check("PolynomialQ(1/f(a), f(a))", //
        "False");
  }

  @Test
  public void testExponent() {
    check("Exponent((x + 1)^2, x + 1)", //
        "2");
    check("Exponent((x + 1)^2 y, x + 1)", //
        "2");
    check("Exponent((x + 1)^2 + 1, x + 1)", //
        "2");

    check("Exponent(x^3 + x, x)", //
        "3");
    check("Exponent(x^2 + 3*x + 2, x)", //
        "2");
    check("Exponent(x^2, 5)", //
        "0");
    check("Exponent(x^2 + 3 x, 5)", //
        "0");

    check("Exponent(Sin(x)^3, Sin(x))", //
        "3");
    check("Exponent(x^2 y, 2 x)", //
        "0");
    check("Exponent(Series(Exp(x), {x, 0, 5}), x)", //
        "5");
    check("Exponent(5, x)", //
        "0");
    check("Exponent(3*x + 1, x)", //
        "1");
    check("Exponent(b*x^(3/2), x)", //
        "3/2");
    check("Exponent(-4x, x, List)", //
        "{1}");
    check("Exponent(x^3 + 2x^2 - 5x + 1, x, List)", //
        "{0,1,2,3}");
  }

  @Test
  public void testCoefficient() {
    check("Coefficient(x^2 + 3*x + 2, x, 2)", //
        "1");
    check("Coefficient(x^2 + 3*x + 2, x, 1)", //
        "3");
    check("Coefficient(x^2 + 3*x + 2, x, 0)", //
        "2");
    check("Coefficient(x^2, x^2)", //
        "1");
    check("Coefficient(3 x y, x y)", //
        "3");
    check("Coefficient(x^2, 2 y)", //
        "0");
    check("Coefficient(x^2, x + 1)", //
        "0");
    check("CoefficientList(1 + 2 x + 3 x^2, x)", //
        "{1,2,3}");
    check("Coefficient(a*x^2 + b*x + c, x, 2)", //
        "a");
    check("Coefficient(x^2 + 1, x, 1)", //
        "0");
    check("Coefficient((x + 1)^5, x^3)", //
        "10");
    check("Coefficient(3 x^2 + 5 x, x^2)", //
        "3");
    check("Coefficient((x + y)^4, (x^2) * (y^2))", //
        "6");
    check("Coefficient(x*Cos(x + 3) + 6*y, x)", //
        "Cos(3+x)");
    check("Coefficient((x + 2)/(y - 3) + (x + 3)/(y - 2), x)", //
        "1/(-3+y)+1/(-2+y)");
    check("Coefficient(6*x, 2*x)", //
        "0");
    check("Coefficient(2*x, 2*x)", //
        "1");
  }

  @Test
  public void testExpand() {
    check("Expand((x + 1)*(x + 2))", //
        "2+3*x+x^2");
    check("Expand((x + 1)^2)", //
        "1+2*x+x^2");
    check("Expand((x + 1)^3)", //
        "1+3*x+3*x^2+x^3");
    check("Expand(x*(x + 1))", //
        "x+x^2");
    check("Expand(x^2 + 3*x + 2)", //
        "2+3*x+x^2");
    check("Expand(x/4)", //
        "x/4");
    check("Expand(5)", //
        "5");
    check("Expand((x + 2)*(x - 2))", //
        "-4+x^2");
    check("Expand((x + y)^2)", //
        "x^2+2*x*y+y^2");
    check("Expand((a + b)*(c + d))", //
        "a*c+b*c+a*d+b*d");
    check("Expand((x + 1)^2 > x)", //
        "1+2*x+x^2>x");
  }

  @Test
  public void testSimplify() {
    // TODO
    // check("Simplify((6 - 4*x)/(5*x))", //
    // "-4/5 + 6/(5*x)");
    // check("Simplify(4*x^2 - 2*x)", //
    // "2*x*(-1 + 2*x)");
    check("Simplify(x + x)", //
        "2*x");
    check("Simplify((1 - Cos(2*x))/2)", //
        "Sin(x)^2");
    check("Simplify((1 + Cos(2*x))/2)", //
        "Cos(x)^2");
    check("Simplify(Sqrt(2) + Sqrt(3))", //
        "Sqrt(2)+Sqrt(3)");
    check("Simplify(3 - 3*x)", //
        "3-3*x");
    check("Simplify(2*Log(2))", //
        "Log(4)");
    check("Simplify(Log(2) + Log(3))", //
        "Log(6)");
    check("Simplify(2*Sqrt(2))", //
        "2*Sqrt(2)");
    check("Simplify(x*x)", //
        "x^2");
    check("Simplify((x^2 - 1)/(x - 1))", //
        "1+x");
    check("Simplify(5)", //
        "5");
    check("Simplify(Sin(x)^2 + Cos(x)^2)", //
        "1");
    check("Simplify(Cosh(x)^2 - Sinh(x)^2)", //
        "1");
    check("Simplify(a/x + b/x)", //
        "(a+b)/x");
  }

  @Test
  @Disabled
  public void testFactor() {
    check("Factor(x^2 + 3*x + 2)", //
        "(1 + x)*(2 + x)");
    check("Factor((x^2 + 3)*(x^4 + x + 7))", //
        "(3 + x^2)*(7 + x + x^4)");
    check("Factor(x^3 + x^2 - x)", //
        "x*(-1 + x + x^2)");
    check("Factor(x^2 - 4)", //
        "(-2 + x)*(2 + x)");
    check("Factor(2*x^2 + 6*x + 4)", //
        "2*(1 + x)*(2 + x)");
    check("Factor(x^2 + 1)", //
        "1 + x^2");
    check("Factor(-x^2 - 5*x - 6)", //
        "-((2 + x)*(3 + x))");
    check("Factor(x^3 - 1)", //
        "(-1 + x)*(1 + x + x^2)");
    check("Factor(x^6 - 1)", //
        "(-1 + x)*(1 + x)*(1 - x + x^2)*(1 + x + x^2)");
  }

  @Test
  @Disabled
  public void testFactorList() {
    check("FactorList(x^3 - 1)", //
        "{{1, 1}, {-1 + x, 1}, {1 + x + x^2, 1}}");
    check("FactorList(2*x^2 + 4*x + 2)", //
        "{{2, 1}, {1 + x, 2}}");
    check("FactorList(x^2 + 1)", //
        "{{1, 1}, {1 + x^2, 1}}");
    check("FactorList(6)", //
        "{{6, 1}}");
    check("FactorList(3/4)", //
        "{{3, 1}, {4, -1}}");
    check("FactorList(-6)", //
        "{{-6, 1}}");
  }

  @Test
  @Disabled
  public void testCancel() {
    check("Cancel((x^2 - 1)/(x - 1))", //
        "1 + x");
    check("Cancel((x^3 - x)/(x^2 - 1))", //
        "x");
    check("Cancel((a*b)/(a*c))", //
        "b/c");
    check("Cancel((a^2*b)/(a*b^2))", //
        "a/b");
    check("Cancel((2*x)/(4*x))", //
        "1/2");
    check("Cancel((x^2 + 2*x + 1)/(x + 1))", //
        "1 + x");
    check("Cancel(x/(2 - 2 x))", //
        "-1/2*x/(-1 + x)");
  }

  @Test
  public void testCollect() {
    check("Collect(x*y + x*z, x)", //
        "x*(y+z)");
    check("Collect(x^2, 5)", //
        "x^2");
    check("Collect(x^3 + y + x, x)", //
        "x+x^3+y");
    check("Collect(q(x) + q(x) q(y), q(x))", //
        "q(x)*(1+q(y))");
    check("Collect(a*x^2 + b*x + c*x^2 + d*x, x)", //
        "(b+d)*x+(a+c)*x^2");
    check("Collect(a x^2 + b x^2 y + c x y, x)", //
        "c*x*y+x^2*(a+b*y)");
  }

  @Test
  public void testTogether() {
    check("Together(1+x/y)", //
        "(x+y)/y");
    check("Together(x*(1/x + 1/y))", //
        "(x+y)/y");
    check("Together(1/x + 1/y)", //
        "(x+y)/(x*y)");
    check("Together((3 - 5*x)/(2 - 2*x))", //
        "(3-5*x)/(2-2*x)");
    check("Together(1/3 + I/3)", //
        "1/3+I*1/3");
    check("Together((x^2 + x)/(x^2 - 1))", //
        "x/(-1+x)");
    check("Together(a/b + c/d)", //
        "(b*c+a*d)/(b*d)");

  }

  @Test
  public void testApart() {
    check("Apart(1/(x^2 - 1))", //
        "1/(2*(-1+x))-1/(2*(1+x))");
    check("Apart((x^2 + 1)/(x^3 - x))", //
        "1/(-1+x)-1/x+1/(1+x)");
    check("Apart(1/(-3 x))", //
        "-1/(3*x)");
    check("Apart(1/((x - 1)*(x - 2)))", //
        "1/(-2+x)-1/(-1+x)");
    check("Apart(x/((x - 1) (x - 2)))", //
        "2/(-2+x)-1/(-1+x)");
  }

}
