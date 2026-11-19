package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Integrands over a tower with an exponential of a tangent, and exponential integrals
 * <code>E^u*u'/u</code> whose exponential <code>E^u</code> is split by the evaluation.
 */
public class IntegrateTangentTowerTest extends ExprEvaluatorTestCase {

  @Test
  public void testParallelIntegrationOverTangent() {
    // the derivative of Tan(u) is Sec(u)^2, rational in the tower as 1+Tan(u)^2
    check("Integrate(E^Tan(x+2)/x+E^Tan(x+2)*Log(2*x)*Sec(x+2)^2,x,Method->\"RischNorman\")", //
        "E^Tan(2+x)*Log(2*x)");
    check("Integrate(E^Tanh(3*x)*(1+3*x*Sech(3*x)^2),x,Method->\"RischNorman\")", //
        "E^Tanh(3*x)*x");
    // Tan(x-3) evaluates to -Tan(3-x), the square of Sec(x-3) is 1+Tan(3-x)^2
    check("Integrate(E^Tan(3-x)/x-E^Tan(3-x)*Log(5*x)*Sec(x-3)^2,x,Method->\"RischNorman\")", //
        "E^Tan(3-x)*Log(5*x)");
  }

  @Test
  public void testTrigOfLogarithm() {
    // a trigonometric function of Log(x) is no tower of exponentials
    check("Integrate(Sinh(Log(x))*Sin(Log(x)),x)", //
        "Cos(Log(x))/2-1/10*x^2*Cos(Log(x))+1/5*x^2*Sin(Log(x))");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testProductOverTangent() {
    check("Integrate(D(Log(2*x)*E^Tan(x+2),x),x)", //
        "E^Tan(2+x)*Log(2*x)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testExponentialOfTangentOverX() {
    // not elementary: an answer without grinding through the stages
    check("TimeConstrained(Integrate(E^Tan(x+2)/x,x),30,$Aborted)", //
        "Integrate(E^Tan(2+x)/x,x)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testExponentialIntegralOfSplitExponential() {
    // E^(3*x+Log(x)) is x*E^(3*x)
    check("Integrate(E^(3*x)*(1+3*x)/(3*x+Log(x)),x)", //
        "ExpIntegralEi(3*x+Log(x))");
    check("Integrate(x^2*E^x*(3+x)/(x+3*Log(x)),x)", //
        "ExpIntegralEi(x+3*Log(x))");
  }
}
