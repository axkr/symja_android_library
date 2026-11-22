package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Lists are no polynomials; the integral of a power of <code>x</code> times a generalized
 * hypergeometric function of <code>c*x^k</code>; values below the range of numbers with a precision
 * of their own; <code>Zeta</code> of a large negative real part.
 */
public class HypergeometricIntegralUnderflowTest extends ExprEvaluatorTestCase {

  @Test
  public void testPolynomialQOfLists() {
    check("{PolynomialQ({2,3},x), PolynomialQ({},y), PolynomialQ({x^3,1},x), "
        + "PolynomialQ(x^3+1,x)}", //
        "{False,False,False,True}");
  }

  @Test
  public void testHypergeometricTimesPower() {
    check("Integrate(x^2*HypergeometricPFQ({1/3,1/2},{5/2,7/4},-3*x^2),x)", //
        "1/3*x^3*HypergeometricPFQ({1/3,1/2,3/2},{7/4,5/2,5/2},-3*x^2)");
    // a term x^(-1) of the series integrates to a logarithm: no hypergeometric antiderivative
    check("Integrate(HypergeometricPFQ({1/3},{5/2,7/4},x^2)/x^5,x)", //
        "Integrate(HypergeometricPFQ({1/3},{7/4,5/2},x^2)/x^5,x)");
    // by parts, the remaining integral is a hypergeometric function of x^2
    check("f=Erf(2*x)*Log(x)/x;r=Integrate(f,x);"
        + "{FreeQ(r,Integrate),Chop(N((D(r,x)-f)/.x->7/10,30))}", //
        "{True,0}");
  }

  @Test
  public void testUnderflowWithPrecision() {
    check("{N(Erfc(10^25),30), N(Erfc(10^25)), N(Exp(-10^50),25)}", //
        "{Underflow(),0.0,Underflow()}");
  }

  @Test
  public void testZetaOfLargeNegativeRealPart() {
    // the functional equation, with the digits of the large argument of the sine
    check("Abs(Zeta(-2*10^6+1.0*I)/N(Zeta(-2*10^6+I),25)-1)<10^-12", //
        "True");
    check("Abs(N(Zeta(-3000+2*I),30)/N(2^(-3000+2*I)*Pi^(-3001+2*I)*Sin(Pi*(-3000+2*I)/2)"
        + "*Gamma(3001-2*I)*Zeta(3001-2*I),30)-1)<10^-25", //
        "True");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testExponentialIntegralTimesCosine() {
    check("f=Cos(x)*ExpIntegralEi(2*x);r=Integrate(f,x);"
        + "{FreeQ(r,Integrate),Chop(N((D(r,x)-f)/.x->7/10,30))}", //
        "{True,0}");
  }
}
