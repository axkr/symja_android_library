package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** PolynomialGCD with AlgebraicNumber coefficients and with inexact coefficients (WMA). */
public class PolynomialGCDTest extends ExprEvaluatorTestCase {

  @Test
  public void testAlgebraicNumberGeneratorWMA() {
    // without Extension an AlgebraicNumber is a generator of its own, as a radical is
    check("a = AlgebraicNumber(Sqrt(2), {0, 1}); "
        + "{PolynomialGCD((x+a*y)*(x+1), (x+a*y)*(x+2)), PolynomialGCD(a*x, a*x^2), "
        + "PolynomialGCD(x^2-2, x-a)}", //
        "{x+y*AlgebraicNumber(Sqrt(2),{0,1}),x*AlgebraicNumber(Sqrt(2),{0,1}),1}");
    // with Extension->Automatic the number field is used
    check("a = AlgebraicNumber(Sqrt(2), {0, 1}); "
        + "PolynomialGCD((x+a*y)*(x+1), (x+a*y)*(x+2), Extension->Automatic)", //
        "2*y+x*AlgebraicNumber(Sqrt(2),{0,1})");
  }

  @Test
  public void testInexactCoefficientsWMA() {
    // WMA: {-1.+1.*x, 0.6666666666666666*x+1.*y, 1.*x, 1.}
    check("{PolynomialGCD(x^2-1.0, x-1.0), PolynomialGCD(x+1.5*y, x^2+1.5*x*y), "
        + "PolynomialGCD(1.5*x, 3*x^2), PolynomialGCD(x^2-2., x-Sqrt(2.))}", //
        "{-1.0+x,0.666667*x+y,x,1.0}");
    check("PolynomialGCD(Expand((x+1.5*Sqrt(2)*y)*(x+1)), Expand((x+1.5*Sqrt(2)*y)*(x+2)))", //
        "1.0");
    // not a polynomial: no GCD, not 1.0
    check("PolynomialGCD(f(1.5*x), x)", //
        "PolynomialGCD(f(1.5*x),x)");
  }

  @Test
  public void testExposedFactors() {
    // the factors of products and powers in the arguments are kept
    check("PolynomialGCD((x+3)^3*(2*x-1),Expand((x+3)^2*(x-5)))", //
        "(3+x)^2");
    check("PolynomialGCD((x+1)*(x-2),(x+1)*(x-2)*(x+7))", //
        "(-2+x)*(1+x)");
    check("PolynomialGCD((x^2+1)*(x-3),(x-3)^2*(x^2+1)^2)", //
        "(-3+x)*(1+x^2)");
    // with the rational content
    check("PolynomialGCD(5*(x+4)^2,10*(x+4)^3)", //
        "5*(4+x)^2");
    check("PolynomialGCD((x+4)^2/3,(x+4)^3/5)", //
        "(4+x)^2/15");
    // a repeated factor counts only as often as it divides the other argument
    check("PolynomialGCD((x-2)^4,(x-2)^2*(x+5),(x-2)^3)", //
        "(2-x)^2");
    // expanded arguments and monomials keep the expanded GCD
    check("PolynomialGCD(3+3*x,6+6*x)", //
        "3+3*x");
    check("PolynomialGCD(1+2*x+x^2,1+3*x+3*x^2+x^3)", //
        "1+2*x+x^2");
    check("PolynomialGCD((2+2*x)^2,8*x)", //
        "4");
    // more factors than are taken one at a time: the expanded GCD
    check("PolynomialGCD((1+x)^70*(2+x),(1+x)^2*x)", //
        "1+2*x+x^2");
  }
}
