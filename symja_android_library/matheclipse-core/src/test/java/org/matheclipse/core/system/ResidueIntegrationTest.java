package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Definite integrals by the residue theorem: around the cut of a logarithm or a power, over a
 * strip for <code>1/Cosh</code> and <code>1/Sinh</code>, in a half plane for Fourier integrals,
 * over the unit circle for trigonometric integrands, and the Beta integral.
 */
public class ResidueIntegrationTest extends ExprEvaluatorTestCase {

  @Test
  public void testKeyholeLog() {
    // around the cut of the logarithm. The antiderivative is a PolyLog whose limits were not found, and
    // the integral was said not to converge
    check("Integrate(Log(x)/(x^2+c^2), {x,0,Infinity}, Assumptions->c>0)", //
        "(Pi*Log(c))/(2*c)");
    check("Integrate(Log(x)/(x^2+9), {x,0,Infinity})", //
        "1/6*Pi*Log(3)");
    check("Integrate(Log(x)^2/(4+x^2), {x,0,Infinity})", //
        "1/16*(Pi^3+4*Pi*Log(2)^2)");
    check("NIntegrate(Log(x)^2/(4+x^2), {x,0,Infinity})", //
        "2.31524");
    check("N(Pi^3/16+Pi*Log(2)^2/4)", //
        "2.31524");
    check("Integrate(Log(x)/(x^4+1), {x,0,Infinity})", //
        "-Pi^2/(8*Sqrt(2))");
    check("NIntegrate(Log(x)/(x^4+1), {x,0,Infinity})", //
        "-0.872358");
    check("N(-Pi^2/(8*Sqrt(2)))", //
        "-0.872358");
    // the pole at 1 is cancelled by the logarithm: the mean of the two sides of the cut
    check("Integrate(Log(x)/(x^3-1), {x,0,Infinity})", //
        "4/27*Pi^2");
    check("NIntegrate(Log(x)/(x^3-1), {x,0,Infinity})", //
        "1.46216");
    check("Integrate(Log(x)/((x-1)*(x^2+4)), {x,0,Infinity})", //
        "1/40*(3*Pi^2+4*Log(2)^2-Pi*Log(4))");
    check("N(Integrate(Log(x)/((x-1)*(x^2+4)), {x,0,Infinity}))", //
        "0.679386");
    check("NIntegrate(Log(x)/((x-1)*(x^2+4)), {x,0,Infinity})", //
        "0.679386");
  }

  @Test
  public void testPrincipalValue() {
    // a simple pole on the path, passed on both sides
    check("Integrate(Log(x)/(x^2-9), {x,0,Infinity}, PrincipalValue->True)", //
        "Pi^2/12");
    check("Integrate(1/(x^2-9), {x,0,Infinity}, PrincipalValue->True)", //
        "0");
    // the principal value is compared with the numerical integral which leaves out a small
    // interval around the pole
    check("Integrate(Log(x)^2/((x-2)*(x^2+1)), {x,0,Infinity}, PrincipalValue->True)", //
        "1/60*(-3*Pi^3+8*Pi^2*Log(2)-4*Log(2)^3)");
    check("Integrate(1/((x-2)*(x^2+1)), {x,0,Infinity}, PrincipalValue->True)", //
        "1/5*(-Pi-Log(2))");
    // a pole of order 2 has no principal value
    check("Integrate(1/((x-2)^2*(x^2+1)), {x,0,Infinity}, PrincipalValue->True)", //
        "Integrate(1/((-2+x)^2*(1+x^2)),{x,0,Infinity},PrincipalValue->True)");
  }

  @Test
  public void testKeyholePower() {
    check("Integrate(x^p*Log(x)/(1+x^2), {x,0,Infinity}, Assumptions->-1<p<1)", //
        "1/4*Pi^2*Csc(p*Pi)^2*(3*Sin(1/2*p*Pi)-Sin(3/2*p*Pi))");
    check("N(Integrate(x^p*Log(x)/(1+x^2), {x,0,Infinity}, Assumptions->-1<p<1) /. p->2/5)", //
        "2.21586");
    check("NIntegrate(x^(2/5)*Log(x)/(1+x^2), {x,0,Infinity})", //
        "2.21586");
    check("Integrate(x^p/(1+x^2), {x,0,Infinity}, Assumptions->-1<p<1)", //
        "Pi*Csc(p*Pi)*Sin(1/2*p*Pi)");
    // no assumptions: the convergence is not proved
    check("Integrate(x^p/(1+x^2), {x,0,Infinity})", //
        "Integrate(x^p/(1+x^2),{x,0,Infinity})");
  }

  @Test
  public void testStrip() {
    check("Integrate(x/Sinh(3*x), {x,-Infinity,Infinity})", //
        "Pi^2/18");
    check("Integrate(x/Sinh(3*x), {x,0,Infinity})", //
        "Pi^2/36");
    check("NIntegrate(x/Sinh(3*x), {x,0,Infinity})", //
        "0.274156");
    check("Integrate(x^3/Sinh(x), {x,0,Infinity})", //
        "Pi^4/8");
    check("Integrate(E^(c*x)/Cosh(2*x), {x,-Infinity,Infinity}, Assumptions->-2<c<2)", //
        "1/2*Pi*Sec(1/4*c*Pi)");
    check("Integrate(Cosh(c*x)/Cosh(d*x), {x,-Infinity,Infinity}, Assumptions->d>c>0)", //
        "(Pi*Sec((c*Pi)/(2*d)))/d");
    check("Integrate(Cos(c*x)/Cosh(d*x), {x,-Infinity,Infinity}, Assumptions->d>0&&c>0)", //
        "(Pi*Sech((c*Pi)/(2*d)))/d");
    check("Integrate(Sinh(c*x)/Sinh(d*x), {x,-Infinity,Infinity}, Assumptions->d>c>0)", //
        "(Pi*Tan((c*Pi)/(2*d)))/d");
    check("N(Integrate(Sinh(c*x)/Sinh(d*x), {x,-Infinity,Infinity}, Assumptions->d>c>0) /. {c->1/2, d->2})", //
        "0.650645");
    check("NIntegrate(Sinh(x/2)/Sinh(2*x), {x,-Infinity,Infinity})", //
        "0.650645");
    check("Integrate(x^2/Cosh(x), {x,-Infinity,Infinity})", //
        "Pi^3/4");
    // outside of the strip the integral does not exist
    check("Integrate(Cosh(c*x)/Cosh(d*x), {x,-Infinity,Infinity}, Assumptions->c>d>0)", //
        "Integrate(Cosh(c*x)/Cosh(d*x),{x,-Infinity,Infinity},Assumptions->c>d>0)");
  }

  @Test
  public void testStripPowersOfCosh() {
    // a pole of order n in the strip: Gamma(n/2+v)*Gamma(n/2-v) with v == u/(2*b)
    check("Integrate(Cos(a*x)/Cosh(b*x)^2, {x,0,Infinity}, Assumptions->a>0&&b>0)", //
        "(a*Pi*Csch((a*Pi)/(2*b)))/(2*b^2)");
    check("N(Integrate(Cos(a*x)/Cosh(b*x)^2, {x,0,Infinity}, Assumptions->a>0&&b>0) /. {a->3/2,b->2})", //
        "0.400669");
    check("NIntegrate(Cos(a*x)/Cosh(b*x)^2 /. {a->3/2,b->2}, {x,0,Infinity})", //
        "0.400669");
    check("Integrate(Cosh(a*x)/Cosh(b*x)^2, {x,0,Infinity}, Assumptions->0<a&&a<2*b)", //
        "(a*Pi*Csc((a*Pi)/(2*b)))/(2*b^2)");
    check("N(Integrate(Cosh(a*x)/Cosh(b*x)^2, {x,0,Infinity}, Assumptions->0<a&&a<2*b) /. {a->3/2,b->2})", //
        "0.637582");
    check("NIntegrate(Cosh(a*x)/Cosh(b*x)^2 /. {a->3/2,b->2}, {x,0,Infinity})", //
        "0.637582");
    check("Integrate(1/Cosh(x)^4, {x,-Infinity,Infinity})", //
        "4/3");
    check("N(Integrate(1/Cosh(x)^4, {x,-Infinity,Infinity}))", //
        "1.33333");
    check("NIntegrate(1/Cosh(x)^4, {x,-Infinity,Infinity})", //
        "1.33333");
    check("Integrate(1/Cosh(x)^3, {x,-Infinity,Infinity})", //
        "Pi/2");
    check("N(Integrate(1/Cosh(x)^3, {x,-Infinity,Infinity}))", //
        "1.5708");
    check("NIntegrate(1/Cosh(x)^3, {x,-Infinity,Infinity})", //
        "1.5708");
    check("Integrate(x^2/Cosh(x)^2, {x,-Infinity,Infinity})", //
        "Pi^2/6");
    check("N(Integrate(x^2/Cosh(x)^2, {x,-Infinity,Infinity}))", //
        "1.64493");
    check("NIntegrate(x^2/Cosh(x)^2, {x,-Infinity,Infinity})", //
        "1.64493");
    check("Integrate(Cos(a*x)/Cosh(b*x)^3, {x,-Infinity,Infinity}, Assumptions->a>0&&b>0)", //
        "((a^2+b^2)*Pi*Sech((a*Pi)/(2*b)))/(2*b^3)");
    check("N(Integrate(Cos(a*x)/Cosh(b*x)^3, {x,-Infinity,Infinity}, Assumptions->a>0&&b>0) /. {a->3/2,b->2})", //
        "0.690195");
    check("NIntegrate(Cos(a*x)/Cosh(b*x)^3 /. {a->3/2,b->2}, {x,-Infinity,Infinity})", //
        "0.690195");
    check("Integrate(Sech(x)^6, {x,-Infinity,Infinity})", //
        "16/15");
    check("N(Integrate(Sech(x)^6, {x,-Infinity,Infinity}))", //
        "1.06667");
    check("NIntegrate(Sech(x)^6, {x,-Infinity,Infinity})", //
        "1.06667");
  }

  @Test
  public void testFourier() {
    // a pole of order 2 at a symbolic place
    check("Integrate(Cos(x)/(x^2+c^2)^2, {x,-Infinity,Infinity}, Assumptions->c>0)", //
        "((1+c)*Pi)/(2*c^3*E^c)");
    check("Integrate(Cos(k*x)/(x^2+c^2)^3, {x,-Infinity,Infinity}, Assumptions->c>0&&k>0)", //
        "((3+3*c*k+c^2*k^2)*Pi)/(8*c^5*E^(c*k))");
    check("N(Integrate(Cos(k*x)/(x^2+c^2)^3, {x,-Infinity,Infinity}, Assumptions->c>0&&k>0) /. {c->3/2, k->2})", //
        "0.0540679");
    check("NIntegrate(Cos(2*x)/(x^2+9/4)^3, {x,-Infinity,Infinity})", //
        "0.0540679");
    // a pole on the path, where the integrand is finite
    check("Integrate(Sin(k*x)/(x*(x^2+c^2)), {x,-Infinity,Infinity}, Assumptions->k>0&&c>0)", //
        "((1-1/E^(c*k))*Pi)/c^2");
    // the two terms diverge one by one
    check("Integrate((Cos(c*x)-Cos(d*x))/x^2, {x,0,Infinity}, Assumptions->c>0&&d>0)", //
        "1/2*(-c+d)*Pi");
    check("Integrate(x*Sin(k*x)/(x^2+c^2), {x,0,Infinity}, Assumptions->k>0&&c>0)", //
        "Pi/(2*E^(c*k))");
  }

  @Test
  public void testUnitCircle() {
    check("Integrate(1/(c+d*Cos(x)), {x,0,2*Pi}, Assumptions->c>d>0)", //
        "(2*Pi)/Sqrt(c^2-d^2)");
    check("Integrate(1/(c+d*Cos(x))^2, {x,0,2*Pi}, Assumptions->c>d>0)", //
        "(2*c*Pi)/(c^2-d^2)^(3/2)");
    check("Integrate(1/(c+d*Sin(x))^2, {x,0,2*Pi}, Assumptions->c>d>0)", //
        "(2*c*Pi)/(c^2-d^2)^(3/2)");
  }

  @Test
  public void testUnitCircleThreeParameters() {
    check("Integrate(1/(c + d*Cos(t) + h*Sin(t))^2, {t,0,2*Pi}, Assumptions->c>Sqrt(d^2+h^2))", //
        "(2*c*Pi)/(c^2-d^2-h^2)^(3/2)");
    check("N(Integrate(1/(c + d*Cos(t) + h*Sin(t))^2, {t,0,2*Pi}, Assumptions->c>Sqrt(d^2+h^2)) /. {c->3, d->1, h->2})", //
        "2.35619");
    check("NIntegrate(1/(3 + Cos(t) + 2*Sin(t))^2, {t,0,2*Pi})", //
        "2.35619");
  }

  /** A pole of order 3 at a radical: the simplification of the residue takes seconds. */
  @Tag(TestTags.SLOW)
  @Test
  public void testUnitCircleOrderThree() {
    check("Integrate(1/(c+d*Cos(x))^3, {x,-Pi,Pi}, Assumptions->c>d>0)", //
        "((2*c^2+d^2)*Pi)/(c^2-d^2)^(5/2)");
  }

  /** Declined here, and the search for an antiderivative takes seconds. */
  @Tag(TestTags.SLOW)
  @Test
  public void testPoleCrossesThePath() {
    // complex for 0 < c < 2, on the path for c > 2: the sample points see both
    check("FreeQ(Integrate(Log(x)/(x^2-c*x+1), {x,0,Infinity}, Assumptions->c>0), Pi)", //
        "True");
  }

  @Test
  public void testBeta() {
    check("Integrate(x^(2*j)/(1+x^(2*k)), {x,0,Infinity}, Assumptions->j>=0&&k>=j+1)", //
        "(Pi*Csc(((1+2*j)*Pi)/(2*k)))/(2*k)");
    check("Integrate(x^p/(x+d)^q, {x,0,Infinity}, Assumptions->d>0&&q>p+1&&p>-1)", //
        "(d^(1+p-q)*Gamma(1+p)*Gamma(-1-p+q))/Gamma(q)");
    check("Integrate(x^p/(3+2*x^4)^q, {x,0,Infinity}, Assumptions->p>-1&&q>1&&p<3)", //
        "(2^(1/4*(-9-p))*3^(1/4*(1+p-4*q))*Gamma(1/4*(1+p))*Gamma(-1/4-p/4+q))/Gamma(q)");
    check("N(Integrate(x^p/(3+2*x^4)^q, {x,0,Infinity}, Assumptions->p>-1&&q>1&&p<3) /. {p->1/2, q->3/2})", //
        "0.141094");
    check("NIntegrate(x^(1/2)/(3+2*x^4)^(3/2), {x,0,Infinity})", //
        "0.141094");
    // the conditions on the exponents are missing
    check("Integrate(x^p/(x+d)^q, {x,0,Infinity}, Assumptions->d>0)", //
        "Integrate(x^p/(d+x)^q,{x,0,Infinity},Assumptions->d>0)");
  }

  /**
   * The place of the poles and the sign of a frequency are read at sample points of the
   * parameters: one for every combination of signs which the assumptions allow.
   */
  @Test
  public void testSignOfTheParameters() {
    check("Integrate(Cos(k*x)/(x^2+1), {x,-Infinity,Infinity}, Assumptions->k>0)", //
        "Pi/E^k");
    check("Integrate(Cos(k*x)/(x^2+1), {x,-Infinity,Infinity}, Assumptions->k<0)", //
        "E^k*Pi");
    // The sign is open: the two cases in one expression, even in k. That k is real is a
    // condition where the assumptions do not say it.
    check("Integrate(Cos(k*x)/(x^2+1), {x,-Infinity,Infinity}, Assumptions->k!=0)", //
        "ConditionalExpression(Pi/E^Abs(k),k∈Reals)");
    check("Integrate(Cos(k*x)/(x^2+1), {x,-Infinity,Infinity}, Assumptions->Element(k, Reals))", //
        "Pi/E^Abs(k)");
    check(
        "Integrate(Cos(k*x)/(x^2+c^2)^2, {x,-Infinity,Infinity}, Assumptions->c>0&&Element(k, Reals))", //
        "(Pi*(1+c*Abs(k)))/(2*c^3*E^(c*Abs(k)))");
    check(
        "N(Integrate(Cos(k*x)/(x^2+c^2)^2, {x,-Infinity,Infinity}, Assumptions->c>0&&Element(k, Reals)) /. {c->3/2, k->-2})", //
        "0.0926878");
    check("NIntegrate(Cos(2*x)/(x^2+9/4)^2, {x,-Infinity,Infinity})", //
        "0.0926878");
    // odd in k
    check("Integrate(Sin(k*x)/x, {x,-Infinity,Infinity}, Assumptions->Element(k, Reals))", //
        "Pi*Sign(k)");
    check("Integrate(x*Sin(k*x)/(x^2+c^2), {x,0,Infinity}, Assumptions->c>0&&k!=0)", //
        "ConditionalExpression((Pi*Sign(k))/(2*E^(c*Abs(k))),k∈Reals)");
    // the sign of c decides which of the two poles is inside of the unit circle
    check("Integrate(1/(c+d*Cos(x)), {x,0,2*Pi}, Assumptions->c^2>d^2)", //
        "(2*Pi*Sign(c))/Sqrt(c^2-d^2)");
    check("N(Integrate(1/(c+d*Cos(x)), {x,0,2*Pi}, Assumptions->c^2>d^2) /. {c->-3, d->2})", //
        "-2.80993");
    check("NIntegrate(1/(-3+2*Cos(x)), {x,0,2*Pi})", //
        "-2.80993");
    // no assumptions: k may be complex
    check("FreeQ(Integrate(Cos(k*x)/(x^2+1), {x,-Infinity,Infinity}), Abs)", //
        "True");
    check("Integrate(1/(c+d*Cos(x)), {x,0,2*Pi}, Assumptions->c>Abs(d))", //
        "(2*Pi)/Sqrt(c^2-d^2)");
  }

  @Test
  public void testPrincipalValueOutOfScope() {
    // the principal value Log(2) is not found: no answer, and no message that the integral
    // does not converge
    check("Integrate(1/x, {x,-1,2}, PrincipalValue->True)", //
        "Integrate(1/x,{x,-1,2},PrincipalValue->True)");
    // where the integral exists it is its own principal value
    check("Integrate(x^2, {x,0,2}, PrincipalValue->True)", //
        "8/3");
    check("Integrate(1/(x^2+c^2), {x,0,Infinity}, Assumptions->c>0, PrincipalValue->True)", //
        "Pi/(2*c)");
  }

  @Test
  public void testDeclined() {
    // divergent at 0
    check("Integrate(Cos(c*x)/x^2, {x,0,Infinity}, Assumptions->c>0)", //
        "Integrate(Cos(c*x)/x^2,{x,0,Infinity},Assumptions->c>0)");
    // a pole on the cut which the logarithm does not cancel
    check("Integrate(Log(x)/(x^2-4), {x,0,Infinity})", //
        "Integrate(Log(x)/(-4+x^2),{x,0,Infinity})");
  }
}
