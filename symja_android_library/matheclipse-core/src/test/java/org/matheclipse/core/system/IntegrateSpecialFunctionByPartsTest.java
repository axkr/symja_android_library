package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>Integrate(K*S, x)</code> for a special function <code>S</code> with an elementary
 * derivative (<code>Erf, Erfc, Erfi, ExpIntegralEi, LogIntegral</code>) and a factor
 * <code>K</code> with an elementary antiderivative: integration by parts.
 */
public class IntegrateSpecialFunctionByPartsTest extends ExprEvaluatorTestCase {

  private void checkByParts(String integrand) {
    check("f=" + integrand + ";r=Integrate(f,x);{FreeQ(r,Integrate),Simplify(Together(D(r,x)-f))}", //
        "{True,0}");
  }

  @Test
  public void testLogarithmicIntegral() {
    check("Integrate(x^2*Log(x)*LogIntegral(x),x)", //
        "-x^4/12+ExpIntegralEi(4*Log(x))/9+(-x^3/9+1/3*x^3*Log(x))*LogIntegral(x)");
    checkByParts("x^2*Log(x)*LogIntegral(x)");
  }

  @Test
  public void testErrorFunctions() {
    checkByParts("E^(2*x)*x*Erf(x)");
    checkByParts("(2+x)*E^(-3*x)*Erfi(x)");
  }
}
