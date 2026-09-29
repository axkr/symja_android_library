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
}
