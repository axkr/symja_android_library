package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Integrands over a tower of <code>Log</code> and <code>Exp</code> kernels, which the rules split
 * into terms that are not elementary on their own: the parallel integration takes the integrand
 * as a whole.
 */
public class ParallelIntegrationTowerTest extends ExprEvaluatorTestCase {

  /** The antiderivative is free of Integrate and its derivative is the integrand. */
  private void checkDerivativeOf(String f) {
    check("g = D(" + f + ", x); r = Integrate(g, x);"
        + "{FreeQ(r, Integrate), Table(Abs(N((D(r, x) - g) /. x -> p, 30)) < 10^-20,"
        + " {p, {2/7, 4/3, 13/5}})}", //
        "{True,{True,True,True}}");
  }

  @Test
  public void testExponentialQuotients() {
    checkDerivativeOf("x*E^(3*x)/(2 + E^(3*x))");
    checkDerivativeOf("x^2*E^x/(3 + E^x)");
    // E^(2*x) and E^(4*x) are powers of one generator
    checkDerivativeOf("x/(1 + E^(2*x))");
    // E^(2*x) inside a logarithm next to E^x
    check("Integrate(D(x*Log(1 + E^(2*x)) + E^x, x), x, Method -> \"RischNorman\")", //
        "E^x+x*Log(1+E^(2*x))");
    // the same stage in the next call
    check("Integrate((-1 + E^x)/(1 + E^x*x), x, Method -> \"RischNorman\")", //
        "-x+Log(1+E^x*x)");
    check("Integrate((-1 + E^x)/(1 + E^x*x), x, Method -> \"RischNorman\")", //
        "-x+Log(1+E^x*x)");
  }

  @Test
  public void testLogarithmsOverExponentials() {
    checkDerivativeOf("E^x/Log(5 + x)");
    checkDerivativeOf("x*Log(x + E^(2*x))");
    checkDerivativeOf("Log(2 + E^(3*x)) + Log(4 + 5*x)");
  }

  @Test
  public void testReducedForm() {
    // the rational part in lowest terms
    check("Integrate(D(x*E^(3*x)/(2 + E^(3*x)), x), x)", //
        "x+(-2*x)/(2+E^(3*x))");
    check("Integrate(D(E^x/Log(5 + x), x), x)", //
        "E^x/Log(5+x)");
    // the rules give two terms c*(u+v) which cancel once c is distributed
    check("Integrate(D(Log(x)*Sqrt(2 + x^3), x), x)", //
        "Sqrt(2+x^3)*Log(x)");
  }
}
