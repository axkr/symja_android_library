package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Integrals over towers whose tangent appears as <code>Coth, Csch, Sech</code> or
 * <code>Cot, Csc, Sec</code>, which are rational in <code>Tanh(u)</code> resp. <code>Tan(u)</code>;
 * sums whose terms carry different exponentials; and sums with a common constant factor like
 * <code>E^(-2*I)</code>.
 */
public class IntegrateReciprocalTangentTowerTest extends ExprEvaluatorTestCase {

  private void checkAntiderivative(String antiderivative) {
    check("F=" + antiderivative + ";r=TimeConstrained(Integrate(D(F,x),x),10,slow);"
        + "{FreeQ(r,Integrate),Together(r-F)}", //
        "{True,0}");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testCotangentForms() {
    checkAntiderivative("E^x*x/(2+Coth(x))");
    checkAntiderivative("x^2*Log(3+Cot(x))*Tan(x)");
    checkAntiderivative("x*Log(1+Coth(2*x))/(1+x^2)");
  }

  @Test
  public void testTermsWithDifferentExponentials() {
    checkAntiderivative("E^Tanh(2*x)*x^2*Log(1+Tanh(2*x)/x)+Log(x)*Tanh(2*x)/x^2");
    checkAntiderivative("E^(x+Tan(x)/x)/(2+Tan(x))+Log(x+Tan(x))*Tan(x)^2/x");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testCommonConstantFactor() {
    check("F=E^(-2*I)*x*Log(I+Tanh(x))/(2+x);r=TimeConstrained(Integrate(D(F,x),x),3,slow);"
        + "{FreeQ(r,Integrate),Together(r-F)}", //
        "{True,0}");
  }
}
