package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * <code>StruveH</code> and <code>ExpIntegralE</code>: series at 0, limits and antiderivatives.
 */
public class StruveExpIntegralETest extends ExprEvaluatorTestCase {

  @Test
  public void testSeries() {
    // from the power series: the derivatives at 0 were unevaluated limits
    check("Series(StruveH(0, z), {z,0,5})", //
        "(2*z)/Pi-2/9*z^3/Pi+2/225*z^5/Pi+O(z)^6");
    check("Series(StruveH(1, z), {z,0,6})", //
        "2/3*z^2/Pi-2/45*z^4/Pi+2/1575*z^6/Pi+O(z)^7");
    check("Series(StruveH(1, z^2), {z,0,6})", //
        "2/3*z^4/Pi+O(z)^7");
    check("N(Normal(Series(StruveH(1, z), {z,0,8})) /. z->1/10)", //
        "0.00212065");
    check("N(StruveH(1, 1/10))", //
        "0.00212065");
    // the logarithm is a part of the coefficient
    check("Series(ExpIntegralE(1, z), {z,0,2})", //
        "-EulerGamma-Log(z)+z-z^2/4+O(z)^3");
    check("Series(ExpIntegralE(3, z), {z,0,3})", //
        "1/2-z+1/2*(3/2-EulerGamma-Log(z))*z^2+z^3/6+O(z)^4");
    check("N(Normal(Series(ExpIntegralE(3, z), {z,0,6})) /. z->1/10)", //
        "0.416291");
    check("N(ExpIntegralE(3, 1/10))", //
        "0.416291");
  }

  @Test
  public void testLimit() {
    check("Limit(StruveH(0, z)/z, z->0)", //
        "2/Pi");
    check("Limit(StruveH(1, z)/z^2, z->0)", //
        "2/(3*Pi)");
    check("Limit(ExpIntegralE(1, z), z->Infinity)", //
        "0");
    check("Limit(z*E^z*ExpIntegralE(1, z), z->Infinity)", //
        "1");
    // the second term of the expansion at infinity
    check("Limit(z^2*E^z*ExpIntegralE(1, z) - z, z->Infinity)", //
        "-1");
    check("Limit(ExpIntegralE(1, z) + Log(z), z->0)", //
        "-EulerGamma");
    check("Limit(ExpIntegralE(3, z), z->0)", //
        "1/2");
    // ExpIntegralE(n, z) is Gamma(1-n)*z^(n-1) at 0 for n < 1
    check("Limit(Sqrt(z)*ExpIntegralE(1/2, z), z->0, Direction->-1)", //
        "Sqrt(Pi)");
    check("Limit(z^2*ExpIntegralE(-1, z), z->0)", //
        "1");
    // the order decides: this was 0, from 0*ExpIntegralE(n, 0)
    check("Limit(z*ExpIntegralE(n, z), z->0)", //
        "Limit(z*ExpIntegralE(n,z),z->0)");
    check("Limit(z^s*ExpIntegralE(1 - s, a*z), z->0, Direction->-1, Assumptions->s>0&&a>0)", //
        "Gamma(s)/a^s");
  }

  @Test
  public void testIntegrate() {
    check("Integrate(StruveH(1, x), x)", //
        "(2*x)/Pi-StruveH(0,x)");
    check("Integrate(StruveH(1, 3*x), x)", //
        "(2*x)/Pi-StruveH(0,3*x)/3");
    check("N(D(Integrate(StruveH(1, 3*x), x), x) /. x->7/10)", //
        "0.693042");
    check("N(StruveH(1, 21/10))", //
        "0.693042");
    check("Integrate(StruveH(0, x), x)", //
        "(x^2*HypergeometricPFQ({1,1},{3/2,3/2,2},-x^2/4))/Pi");
  }

  @Test
  public void testReviewFindings() {
    // an argument which is not the variable itself
    check("Series(ExpIntegralE(1, 2*z), {z,0,2})", //
        "-EulerGamma-Log(2)-Log(z)+2*z-z^2+O(z)^3");
    check("Series(ExpIntegralE(2, z^2), {z,0,4})", //
        "1+(-1+EulerGamma+Log(z^2))*z^2-z^4/2+O(z)^5");
    check("N(Normal(Series(ExpIntegralE(2, Sin(z)), {z,0,5})) /. z->1/10)", //
        "0.722849");
    check("N(ExpIntegralE(2, Sin(1/10)))", //
        "0.722849");
    // the term of the order 1/z^2 of the expansion at infinity
    check("Limit(z^3*E^z*ExpIntegralE(1, z) - z^2 + z, z->Infinity)", //
        "2");
    // only the 0 from 0*ExpIntegralE(n, 0) is refused
    check("Limit(ExpIntegralE(n, x)/ExpIntegralE(n, x), x->0)", //
        "1");
  }
}
