package org.matheclipse.core.system;

import org.junit.Test;

public class FourierTest extends ExprEvaluatorTestCase {

  @Test
  public void testFourierCosTransform() {
    check("FourierCosTransform(Exp(-t^2),t,w)", //
        "1/(Sqrt(2)*E^(w^2/4))");

    // TODO
    check("FourierCosTransform(1/Sqrt(t),t,w)", //
        "FourierCosTransform(1/Sqrt(t),t,w)");
    check("FourierCosTransform(Sin(t)/t,t,w)", //
        "FourierCosTransform(Sin(t)/t,t,w)");
  }

  @Test
  public void testFourierSinTransform() {
    check("FourierSinTransform(Exp(-t),t,w)", //
        "(Sqrt(2/Pi)*w)/(1+w^2)");

    // TODO
    check("FourierSinTransform(Cos(t)/t,t,w)", //
        "FourierSinTransform(Cos(t)/t,t,w)");
    check("FourierSinTransform(t/(t^2+1),t,w)", //
        "Sqrt(2/Pi)*(1/2*Pi*Cosh(w)-1/2*Pi*Sinh(w))");
  }


}
