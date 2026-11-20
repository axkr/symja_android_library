package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Integrals over a tower with a <code>Tan(u)</code> or <code>Tanh(u)</code> and another kernel:
 * an exponential, a logarithm or a second tangent. The undetermined coefficients of the parallel
 * integration form linear systems of some hundred equations with numeric coefficients.
 */
public class IntegrateMixedTangentTowerTest extends ExprEvaluatorTestCase {

  @Test
  public void testHyperbolicTangentBesideExponential() {
    check("TimeConstrained(Integrate(D(x^2*E^(2*x)/(x+Tanh(x)),x),x), 20, slow)", //
        "(E^(2*x)*x^2)/(x+Tanh(x))");
    check("TimeConstrained(Integrate(D(E^Tanh(x)*x^3/(2*x^2+Tanh(x)^2),x),x), 20, slow)", //
        "(E^Tanh(x)*x^3)/(2*x^2+Tanh(x)^2)");
  }

  @Test
  public void testHyperbolicTangentBesideLogarithm() {
    check("TimeConstrained(Integrate(D(Log(1+Tanh(x+1))*Tanh(x+1)^2/(2*x),x),x), 20, slow)", //
        "(Log(1+Tanh(1+x))*Tanh(1+x)^2)/(2*x)");
  }

  @Test
  public void testTangentOverGaussianNumbers() {
    check("TimeConstrained(Integrate(D(x*Log(x+I)/(I+Tan(x)),x),x), 20, slow)", //
        "(x*Log(I+x))/(I+Tan(x))");
    check("TimeConstrained(Integrate(D(x*Log(-I+Tan(x+3))*Log(I+Tan(x+3)),x),x), 20, slow)", //
        "x*Log(-I+Tan(3+x))*Log(I+Tan(3+x))");
  }

  @Test
  public void testTwoTangents() {
    check("TimeConstrained(Integrate(D(Tan(x)*Log(1+Tan(x/3)^2)/x,x),x), 20, slow)", //
        "(Log(1+Tan(x/3)^2)*Tan(x))/x");
  }
}
