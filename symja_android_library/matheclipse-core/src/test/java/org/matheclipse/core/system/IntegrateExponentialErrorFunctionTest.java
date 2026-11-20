package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * An exponential of a linear argument times <code>Erf</code>, <code>Erfc</code> or
 * <code>Erfi</code> of a linear argument: integration by parts leaves a Gaussian whose center is
 * shifted by <code>d/(2*b)</code>.
 */
public class IntegrateExponentialErrorFunctionTest extends ExprEvaluatorTestCase {

  @Test
  public void testErf() {
    check("Integrate(E^(2*x)*Erf(x/2),x)", //
        "1/2*(E^4*Erf(2-x/2)+E^(2*x)*Erf(x/2))");
    check("f=E^(2*x)*Erf(x/2)"
        + ";r=Integrate(f,x);{FreeQ(r,Integrate),N((D(r,x)-f)/.x->7/10,20)}", //
        "{True,0}");
  }

  @Test
  public void testErfc() {
    check("Integrate(Erfc(3*x+1)/E^(2*x),x)", //
        "1/2*(-E^(7/9)*Erf(4/3+3*x)-Erfc(1+3*x)/E^(2*x))");
    check("f=Erfc(3*x+1)/E^(2*x)"
        + ";r=Integrate(f,x);{FreeQ(r,Integrate),N((D(r,x)-f)/.x->7/10,20)}", //
        "{True,0}");
  }

  @Test
  public void testErfi() {
    check("Integrate(E^(x/2-1)*Erfi(2*x-1),x)", //
        "-2*(-Erfi(7/8-2*x)/E^(49/64)+Erfi(1-2*x)/E^(1-x/2))");
    check("f=E^(x/2-1)*Erfi(2*x-1)"
        + ";r=Integrate(f,x);{FreeQ(r,Integrate),N((D(r,x)-f)/.x->7/10,20)}", //
        "{True,0}");
  }

  @Test
  public void testSymbolicCoefficients() {
    check("Integrate(E^(k*x)*Erf(p*x),x)", //
        "(E^(k*x)*Erf(p*x)+E^(k^2/(4*p^2))*Erf(k/(2*p)-p*x))/k");
  }
}
