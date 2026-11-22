package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Integrals over towers with exponentials of sums like <code>E^(2*x+x^2)</code> beside
 * <code>E^x</code>: the exponentials are products of powers of <code>E^x</code> and
 * <code>E^(x^2)</code>, which are the kernels of the ansatz.
 */
public class IntegrateExponentBasisTest extends ExprEvaluatorTestCase {

  private void checkAntiderivative(String antiderivative) {
    check("F=" + antiderivative + ";r=TimeConstrained(Integrate(D(F,x),x),10,slow);"
        + "{FreeQ(r,Integrate),Together(r-F)}", //
        "{True,0}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testExponentialsOfPolynomials() {
    checkAntiderivative("x*E^(2*x+x^2)/(1+E^x)");
    checkAntiderivative("x^2*E^(x+x^3)/(2+E^(2*x))");
  }
}
