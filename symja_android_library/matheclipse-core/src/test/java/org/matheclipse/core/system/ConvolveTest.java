package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

public class ConvolveTest extends ExprEvaluatorTestCase {
  @Test
  public void testConvolve() {
    // Gaussian (*) Gaussian
    check("Convolve(E^(-x^2),E^(-x^2),x,y)", //
        "Sqrt(Pi/2)/E^(y^2/2)");
    check("Convolve(E^(-x^2),E^(-2*x^2),x,y)", //
        "Sqrt(Pi/3)/E^(2/3*y^2)");
    // wrong number of arguments -> unevaluated
    check("Convolve(a,b,c)", //
        "Convolve(a,b,c)");
    check("Convolve(a,b,c,d,e)", //
        "Convolve(a,b,c,d,e)");
    // compactly supported / causal signals
    check("Convolve(UnitBox(x),UnitBox(x),x,y)", //
        "UnitTriangle(y)");
    check("Convolve(UnitStep(x),UnitStep(x),x,y)", //
        "y*UnitStep(y)");
    check("Convolve(Exp(-x)*UnitStep(x),Exp(-x)*UnitStep(x),x,y)", //
        "(y*UnitStep(y))/E^y");
    check("Convolve(Exp(-2*x)*UnitStep(x),Exp(-2*x)*UnitStep(x),x,y)", //
        "(y*UnitStep(y))/E^(2*y)");
    // DiracDelta sifting property
    check("Convolve(DiracDelta(x),Sin(x),x,y)", //
        "Sin(y)");
    check("Convolve(DiracDelta(x),x^2+1,x,y)", //
        "1+y^2");
    check("Convolve(Sin(x),DiracDelta(x),x,y)", //
        "Sin(y)");
    // no closed form -> unevaluated
    check("Convolve(Sin(x),Cos(x),x,y)", //
        "Convolve(Sin(x),Cos(x),x,y)");
  }
}
