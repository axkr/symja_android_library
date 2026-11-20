package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

/**
 * Integrands over a tower with an inverse tangent of an exponential: <code>ArcTan(u)</code> is a
 * logarithmic kernel, its derivative <code>u'/(1+u^2)</code> is rational in the kernels of
 * <code>u</code>.
 */
public class IntegrateInverseTangentTowerTest extends ExprEvaluatorTestCase {

  @Test
  public void testParallelIntegrationOverInverseTangent() {
    check("Integrate(D(ArcTan(E^(3*x))^2,x),x,Method->\"RischNorman\")", //
        "ArcTan(E^(3*x))^2");
    check("Integrate(D(x^2*ArcTanh(E^(-x)),x),x,Method->\"RischNorman\")", //
        "x^2*ArcTanh(E^(-x))");
    check("Integrate(D((x+1)*ArcCot(E^x),x),x,Method->\"RischNorman\")", //
        "(1+x)*ArcCot(E^x)");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testWholeIntegrandWithNonElementaryParts() {
    // ArcTan(E^(2*x))^2 alone has no elementary integral, the sum has
    check("Integrate(D(x*ArcTan(E^(2*x))^2,x),x)", //
        "x*ArcTan(E^(2*x))^2");
  }

  @Test
  public void testLogarithmTowerInLowestTerms() {
    check("Integrate(D(x*Log(1+E^(2*x))^2,x),x,Method->\"RischNorman\")", //
        "x*Log(1+E^(2*x))^2");
  }
}
