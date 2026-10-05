package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Limits of the incomplete Gamma function <code>Gamma(a,z)</code> at <code>0</code> and at
 * <code>Infinity</code>, and the definite integrals which are made of them.
 */
public class IncompleteGammaLimitTest extends ExprEvaluatorTestCase {

  @Test
  public void testLimitAtInfinity() {
    // the incomplete Gamma function vanishes; read as Gamma(1/4) by the Stirling step, this was Gamma(1/4)
    check("Limit(Gamma(1/4, 3*x), x->Infinity)", //
        "0");
    check("Limit(Gamma(a, x^2+1), x->Infinity)", //
        "0");
    check("Limit(Gamma(7/2, 3*x), x->Infinity)", //
        "0");
    // Gamma(a,z) ~ z^(a-1)*E^(-z)
    check("Limit(Gamma(1/4, 3*x)*E^(3*x)*x^(3/4), x->Infinity)", //
        "1/3^(3/4)");
    // along the imaginary axis the function oscillates and is bounded by x^(-3)
    check("Limit(Gamma(1/4, I*x^4), x->Infinity)", //
        "0");
    check("Limit(Gamma(1/4, 0, 3*x), x->Infinity)", //
        "Gamma(1/4)");
  }

  @Test
  public void testLimitAtZero() {
    check("Limit(Gamma(1/4, 3*x), x->0)", //
        "Gamma(1/4)");
    // Gamma(a,z) == Gamma(a)-z^a/a+...
    check("Limit((Gamma(1/4) - Gamma(1/4, x))/x^(1/4), x->0, Direction->-1)", //
        "4");
    check("Limit(x*Gamma(1/4, I*x^4)/(I*x^4)^(1/4), x->0, Direction->-1)", //
        "Cos(Pi/8)*Gamma(1/4)-I*Gamma(1/4)*Sin(Pi/8)");
    // 0*(c+DirectedInfinity(z)) is not 0
    check("Limit(x*(-4 + 5/(I*x^4)^(1/4)), x->0, Direction->-1)", //
        "5/I^(1/4)");
  }

  @Test
  public void testDefiniteIntegrals() {
    // the antiderivative is a sum of incomplete Gamma functions of I*x^4 and -I*x^4. With the
    // wrong limit at Infinity these integrals had the wrong sign
    check("Integrate(Sin(x^4), {x,0,Infinity})", //
        "1/4*Gamma(1/4)*Sin(Pi/8)");
    check("N(Integrate(Sin(x^4), {x,0,Infinity}))", //
        "0.346865");
    // the numerical value of a finite part: the same sign
    check("NIntegrate(Sin(x^4), {x,0,6})", //
        "0.346972");
    check("Integrate(Cos(x^4), {x,0,Infinity})", //
        "1/4*Cos(Pi/8)*Gamma(1/4)");
    check("Integrate(E^(-x^5), {x,0,Infinity})", //
        "Gamma(1/5)/5");
    check("Integrate(x^(5/2)*E^(-x^2), {x,0,Infinity})", //
        "Gamma(7/4)/2");
    check("Integrate(x^(1/3)*Sin(x^2), {x,0,Infinity})", //
        "1/4*Sqrt(3)*Gamma(2/3)");
    check("NIntegrate(Sin(x^3), {x,0,Infinity})", //
        "0.44649");
  }

  @Test
  public void testMellinTransforms() {
    // this was 0
    check("Integrate(x^(s-1)*E^(-b*x), {x,0,Infinity}, Assumptions->s>0&&b>0)", //
        "Gamma(s)/b^s");
    check("Integrate(x^(s-1)*E^(-b*x^2), {x,0,Infinity}, Assumptions->s>0&&b>0)", //
        "Gamma(s/2)/(2*b^(s/2))");
    // for p < -1 the two terms of the antiderivative are infinite at 0 and cancel
    check("Integrate(x^p*Sin(x^2), {x,0,Infinity}, Assumptions->-3<p<1)", //
        "1/2*Gamma(1/2*(1+p))*Sin(1/4*(1+p)*Pi)");
    check("Integrate(x^p*Cos(x^2), {x,0,Infinity}, Assumptions->-1<p<1)", //
        "1/2*Cos(1/4*(1+p)*Pi)*Gamma(1/2*(1+p))");
  }
}
