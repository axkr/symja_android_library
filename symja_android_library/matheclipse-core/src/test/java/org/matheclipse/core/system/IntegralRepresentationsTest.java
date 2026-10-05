package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Definite integrals which are the integral representation of a special function. Every closed
 * form is compared with the numerical integral at a sample point of the parameters.
 */
public class IntegralRepresentationsTest extends ExprEvaluatorTestCase {

  @Test
  public void testTrigonometricBeta() {
    check("Integrate(Sin(x)^a*Cos(x)^b, {x,0,Pi/2}, Assumptions->a>-1&&b>-1)", //
        "Beta(1/2*(1+a),1/2*(1+b))/2");
    check("N(Integrate(Sin(x)^a*Cos(x)^b, {x,0,Pi/2}, Assumptions->a>-1&&b>-1) /. {a->1/3,b->5/2})", //
        "0.495489");
    check("NIntegrate(Sin(x)^a*Cos(x)^b /. {a->1/3,b->5/2}, {x,0,Pi/2})", //
        "0.495489");
    // the conditions of convergence are a part of the result, if they are not assumed
    check("Integrate(Sin(x)^a*Cos(x)^b, {x,0,Pi/2})", //
        "ConditionalExpression(Beta(1/2*(1+a),1/2*(1+b))/2,a>-1&&b>-1)");
    check("Integrate(Sin(x)^a, {x,0,Pi/2}, Assumptions->a>-1)", //
        "Beta(1/2*(1+a),1/2)/2");
    check("N(Integrate(Sin(x)^a, {x,0,Pi/2}, Assumptions->a>-1) /. {a->7/5})", //
        "0.895522");
    check("NIntegrate(Sin(x)^a /. {a->7/5}, {x,0,Pi/2})", //
        "0.895522");
    check("Integrate(Sqrt(Sin(x))*Cos(x)^(3/2), {x,0,Pi/2})", //
        "1/2*Gamma(3/4)*Gamma(5/4)");
    check("N(Integrate(Sqrt(Sin(x))*Cos(x)^(3/2), {x,0,Pi/2}))", //
        "0.55536");
    check("NIntegrate(Sqrt(Sin(x))*Cos(x)^(3/2), {x,0,Pi/2})", //
        "0.55536");
    check("Integrate(Tan(x)^a, {x,0,Pi/2}, Assumptions->-1<a&&a<1)", //
        "Beta(1/2*(1+a),1/2*(1-a))/2");
    check("N(Integrate(Tan(x)^a, {x,0,Pi/2}, Assumptions->-1<a&&a<1) /. {a->1/3})", //
        "1.8138");
    check("NIntegrate(Tan(x)^a /. {a->1/3}, {x,0,Pi/2})", //
        "1.8138");
    check("Integrate(Sin(x)^a, {x,0,Pi}, Assumptions->a>0)", //
        "Beta(1/2*(1+a),1/2)");
    check("N(Integrate(Sin(x)^a, {x,0,Pi}, Assumptions->a>0) /. {a->7/5})", //
        "1.79104");
    check("NIntegrate(Sin(x)^a /. {a->7/5}, {x,0,Pi})", //
        "1.79104");
  }

  @Test
  public void testLogarithmOfCosine() {
    // the antiderivative jumps on the path; its values at the end points gave 0
    check("Integrate(Log(a^2-2*a*Cos(x)+1), {x,0,Pi}, Assumptions->a>1)", //
        "2*Pi*Log(a)");
    check("N(Integrate(Log(a^2-2*a*Cos(x)+1), {x,0,Pi}, Assumptions->a>1) /. {a->3})", //
        "6.90278");
    check("NIntegrate(Log(a^2-2*a*Cos(x)+1) /. {a->3}, {x,0,Pi})", //
        "6.90278");
    check("Integrate(Log(a^2-2*a*Cos(x)+1), {x,0,Pi}, Assumptions->0<a&&a<1)", //
        "0");
    check("Chop(NIntegrate(Log(a^2-2*a*Cos(x)+1) /. a->1/3, {x,0,Pi}))", //
        "0");
    // at a == 1 and a == -1 the logarithm has an integrable singularity
    check("Integrate(Log(a^2-2*a*Cos(x)+1), {x,0,Pi}, Assumptions->Element(a,Reals))", //
        "Pi*Log(1/2*(1+a^2+Abs(1-a^2)))");
    check("Integrate(Log(p+q*Cos(x)), {x,0,Pi}, Assumptions->p>q&&q>0)", //
        "Pi*Log(1/2*(p+Sqrt(p^2-q^2)))");
    check("N(Integrate(Log(p+q*Cos(x)), {x,0,Pi}, Assumptions->p>q&&q>0) /. {p->3,q->2})", //
        "3.02354");
    check("NIntegrate(Log(p+q*Cos(x)) /. {p->3,q->2}, {x,0,Pi})", //
        "3.02354");
    check("Integrate(Log(5+3*Cos(x)), {x,0,Pi})", //
        "Pi*Log(9/2)");
    check("N(Integrate(Log(5+3*Cos(x)), {x,0,Pi}))", //
        "4.7252");
    check("NIntegrate(Log(5+3*Cos(x)), {x,0,Pi})", //
        "4.7252");
    check("Integrate(Log(5+3*Cos(x)), {x,0,2*Pi})", //
        "Pi*Log(81/4)");
    check("N(Integrate(Log(5+3*Cos(x)), {x,0,2*Pi}))", //
        "9.4504");
    check("NIntegrate(Log(5+3*Cos(x)), {x,0,2*Pi})", //
        "9.4504");
  }

  @Test
  public void testDerivativesOfGamma() {
    check("Integrate(E^(-x)*x^(a-1)*Log(x), {x,0,Infinity}, Assumptions->a>0)", //
        "Gamma(a)*PolyGamma(0,a)");
    check("N(Integrate(E^(-x)*x^(a-1)*Log(x), {x,0,Infinity}, Assumptions->a>0) /. {a->7/5})", //
        "-0.0544643");
    check("NIntegrate(E^(-x)*x^(a-1)*Log(x) /. {a->7/5}, {x,0,Infinity})", //
        "-0.0544643");
    check("Integrate(E^(-x)*x^(a-1)*Log(x)^2, {x,0,Infinity}, Assumptions->a>0)", //
        "Gamma(a)*PolyGamma(0,a)^2+Gamma(a)*PolyGamma(1,a)");
    check("N(Integrate(E^(-x)*x^(a-1)*Log(x)^2, {x,0,Infinity}, Assumptions->a>0) /. {a->7/5})", //
        "0.913105");
    check("NIntegrate(E^(-x)*x^(a-1)*Log(x)^2 /. {a->7/5}, {x,0,Infinity})", //
        "0.913105");
    check("Integrate(E^(-p*x)*x^2*Log(x), {x,0,Infinity}, Assumptions->p>0)", //
        "(2*(3/2-EulerGamma))/p^3+(-2*Log(p))/p^3");
    check("N(Integrate(E^(-p*x)*x^2*Log(x), {x,0,Infinity}, Assumptions->p>0) /. {p->3/2})", //
        "0.30656");
    check("NIntegrate(E^(-p*x)*x^2*Log(x) /. {p->3/2}, {x,0,Infinity})", //
        "0.30656");
  }

  @Test
  public void testLerchAndHurwitzZeta() {
    // the numerical check found HurwitzZeta(s, 3/4) wrong: it was the value for s == 2
    check("Integrate(E^(-c*x)*x^(s-1)/(1-E^(-x)), {x,0,Infinity}, Assumptions->s>1&&c>0)", //
        "Gamma(s)*HurwitzZeta(s,c)");
    check("N(Integrate(E^(-c*x)*x^(s-1)/(1-E^(-x)), {x,0,Infinity}, Assumptions->s>1&&c>0) /. {s->11/5,c->3/4})", //
        "2.73639");
    check("NIntegrate(E^(-c*x)*x^(s-1)/(1-E^(-x)) /. {s->11/5,c->3/4}, {x,0,Infinity})", //
        "2.73639");
    check("HurwitzZeta(2, 3/4)", //
        "-8*Catalan+Pi^2");
    check("HurwitzZeta(3, 3/4)", //
        "HurwitzZeta(3,3/4)");
    check("N(HurwitzZeta(11/5, 3/4))", //
        "2.48356");
    check("Integrate(E^(-c*x)*x^(s-1)/(1-z*E^(-x)), {x,0,Infinity}, Assumptions->s>1&&c>0&&0<z&&z<1)", //
        "Gamma(s)*LerchPhi(z,s,c)");
    check("N(Integrate(E^(-c*x)*x^(s-1)/(1-z*E^(-x)), {x,0,Infinity}, Assumptions->s>1&&c>0&&0<z&&z<1) /. {s->11/5,c->3/4,z->1/3})", //
        "2.19801");
    check("NIntegrate(E^(-c*x)*x^(s-1)/(1-z*E^(-x)) /. {s->11/5,c->3/4,z->1/3}, {x,0,Infinity})", //
        "2.19801");
    check("Integrate(x^2*E^(-x/2)/(E^x-1), {x,0,Infinity})", //
        "2*HurwitzZeta(3,3/2)");
    check("N(Integrate(x^2*E^(-x/2)/(E^x-1), {x,0,Infinity}))", //
        "0.828797");
    check("NIntegrate(x^2*E^(-x/2)/(E^x-1), {x,0,Infinity})", //
        "0.828797");
  }

  @Test
  public void testBessel() {
    check("Integrate(E^(-a*Cosh(x))*Cosh(k*x), {x,0,Infinity}, Assumptions->a>0)", //
        "BesselK(k,a)");
    check("N(Integrate(E^(-a*Cosh(x))*Cosh(k*x), {x,0,Infinity}, Assumptions->a>0) /. {a->3/2,k->2/3})", //
        "0.24024");
    check("NIntegrate(E^(-a*Cosh(x))*Cosh(k*x) /. {a->3/2,k->2/3}, {x,0,Infinity})", //
        "0.24024");
    check("Integrate(E^(-a*Cosh(x)), {x,0,Infinity}, Assumptions->a>0)", //
        "BesselK(0,a)");
    check("N(Integrate(E^(-a*Cosh(x)), {x,0,Infinity}, Assumptions->a>0) /. {a->3/2})", //
        "0.213806");
    check("NIntegrate(E^(-a*Cosh(x)) /. {a->3/2}, {x,0,Infinity})", //
        "0.213806");
    check("Integrate(x^(v-1)*E^(-a*x-b/x), {x,0,Infinity}, Assumptions->a>0&&b>0)", //
        "2*(b/a)^(v/2)*BesselK(v,2*Sqrt(a*b))");
    check("N(Integrate(x^(v-1)*E^(-a*x-b/x), {x,0,Infinity}, Assumptions->a>0&&b>0) /. {a->3/2,b->2,v->1/3})", //
        "0.0434474");
    check("NIntegrate(x^(v-1)*E^(-a*x-b/x) /. {a->3/2,b->2,v->1/3}, {x,0,Infinity})", //
        "0.0434474");
    check("Integrate(E^(-a*x-b/x)/Sqrt(x), {x,0,Infinity}, Assumptions->a>0&&b>0)", //
        "Sqrt(Pi)/(Sqrt(a)*E^(2*Sqrt(a*b)))");
    check("N(Integrate(E^(-a*x-b/x)/Sqrt(x), {x,0,Infinity}, Assumptions->a>0&&b>0) /. {a->3/2,b->2})", //
        "0.045299");
    check("NIntegrate(E^(-a*x-b/x)/Sqrt(x) /. {a->3/2,b->2}, {x,0,Infinity})", //
        "0.045299");
    check("Integrate(E^(-a^2*x^2-b^2/x^2), {x,0,Infinity}, Assumptions->a>0&&b>0)", //
        "Sqrt(Pi)/(2*a*E^(2*a*b))");
    check("N(Integrate(E^(-a^2*x^2-b^2/x^2), {x,0,Infinity}, Assumptions->a>0&&b>0) /. {a->3/2,b->2})", //
        "0.00146449");
    check("NIntegrate(E^(-a^2*x^2-b^2/x^2) /. {a->3/2,b->2}, {x,0,Infinity})", //
        "0.00146449");
  }

  @Test
  public void testGaussianAndAiry() {
    check("Integrate(E^(-a^2*x^2)*Cos(b*x), {x,0,Infinity}, Assumptions->a>0)", //
        "Sqrt(Pi)/(2*a*E^(b^2/(4*a^2)))");
    check("N(Integrate(E^(-a^2*x^2)*Cos(b*x), {x,0,Infinity}, Assumptions->a>0) /. {a->3/2,b->2})", //
        "0.378821");
    check("NIntegrate(E^(-a^2*x^2)*Cos(b*x) /. {a->3/2,b->2}, {x,0,Infinity})", //
        "0.378821");
    check("Integrate(E^(-x^2)*Cos(b*x), {x,-Infinity,Infinity})", //
        "Sqrt(Pi)/E^(b^2/4)");
    // the integrand does not decay; the numerical reference is the integral along the ray
    // x == s*E^(I*Pi/6)
    check("Integrate(Cos(x^3/3+a*x), {x,0,Infinity}, Assumptions->Element(a,Reals))", //
        "Pi*AiryAi(a)");
    check("N(Pi*AiryAi(1/2))", //
        "0.727887");
    check("NIntegrate(E^(-x^3/3-x/4)*(Sqrt(3)/2*Cos(Sqrt(3)/4*x)-Sin(Sqrt(3)/4*x)/2), {x,0,Infinity})", //
        "0.727887");
    check("Integrate(Cos(2*x^3+a*x), {x,0,Infinity}, Assumptions->Element(a,Reals))", //
        "(Pi*AiryAi(a/6^(1/3)))/6^(1/3)");
    check("Integrate(Cos(x^3), {x,0,Infinity})", //
        "Gamma(1/3)/(2*Sqrt(3))");
  }

  @Test
  public void testEuler() {
    check("Integrate(x^(a-1)*(1-x)^(b-1)*(1+c*x)^(-a-b), {x,0,1}, Assumptions->a>0&&b>0&&c>0)", //
        "Beta(a,b)/(1+c)^a");
    check("N(Integrate(x^(a-1)*(1-x)^(b-1)*(1+c*x)^(-a-b), {x,0,1}, Assumptions->a>0&&b>0&&c>0) /. {a->1/3,b->3/2,c->2})", //
        "1.74999");
    check("NIntegrate(x^(a-1)*(1-x)^(b-1)*(1+c*x)^(-a-b) /. {a->1/3,b->3/2,c->2}, {x,0,1})", //
        "1.74999");
    check("Integrate(x^(a-1)*(1-x)^(b-1)*(1+c*x)^p, {x,0,1}, Assumptions->a>0&&b>0&&c>0)", //
        "Beta(a,b)*Hypergeometric2F1(a,-p,a+b,-c)");
    check("N(Integrate(x^(a-1)*(1-x)^(b-1)*(1+c*x)^p, {x,0,1}, Assumptions->a>0&&b>0&&c>0) /. {a->1/3,b->3/2,c->2,p->-3/4})", //
        "2.11718");
    check("NIntegrate(x^(a-1)*(1-x)^(b-1)*(1+c*x)^p /. {a->1/3,b->3/2,c->2,p->-3/4}, {x,0,1})", //
        "2.11718");
    // without a parameter the antiderivative is tried: its value is elementary
    check("Integrate(Sqrt(x)*Sqrt(1-x)/(1+x), {x,0,1})", //
        "3/2*Pi-Sqrt(2)*Pi");
  }

  @Test
  public void testLaplaceTransformOfATrigonometricPolynomial() {
    // the search for an antiderivative ran for 30 seconds on these
    check("Integrate(E^(-a*x)*(Sin(b*x)/x)^2, {x,0,Infinity}, Assumptions->a>0&&b>0)", //
        "b*ArcTan((2*b)/a)+1/2*a*Log(a)-1/4*a*Log(a^2+4*b^2)");
    check("N(Integrate(E^(-a*x)*(Sin(b*x)/x)^2, {x,0,Infinity}, Assumptions->a>0&&b>0) /. {a->3/2,b->2})", //
        "1.63909");
    check("NIntegrate(E^(-a*x)*(Sin(b*x)/x)^2 /. {a->3/2,b->2}, {x,0,Infinity})", //
        "1.63909");
    check("Integrate(E^(-a*x)*Sin(b*x)*Sin(c*x)/x, {x,0,Infinity}, Assumptions->a>0&&b>0&&c>0)", //
        "1/4*(-Log(a^2+(b-c)^2)+Log(a^2+(b+c)^2))");
    check("N(Integrate(E^(-a*x)*Sin(b*x)*Sin(c*x)/x, {x,0,Infinity}, Assumptions->a>0&&b>0&&c>0) /. {a->3/2,b->2,c->1/3})", //
        "0.10638");
    check("NIntegrate(E^(-a*x)*Sin(b*x)*Sin(c*x)/x /. {a->3/2,b->2,c->1/3}, {x,0,Infinity})", //
        "0.10638");
    check("Integrate(E^(-a*x)*(Cos(b*x)-Cos(c*x))/x, {x,0,Infinity}, Assumptions->a>0)", //
        "1/2*(-Log(a^2+b^2)+Log(a^2+c^2))");
    check("N(Integrate(E^(-a*x)*(Cos(b*x)-Cos(c*x))/x, {x,0,Infinity}, Assumptions->a>0) /. {a->3/2,b->2,c->1/3})", //
        "-0.486725");
    check("NIntegrate(E^(-a*x)*(Cos(b*x)-Cos(c*x))/x /. {a->3/2,b->2,c->1/3}, {x,0,Infinity})", //
        "-0.486725");
    check("Integrate(E^(-a*x)*Sin(b*x)^3/x^2, {x,0,Infinity}, Assumptions->a>0&&b>0)", //
        "1/8*(-6*a*ArcTan(b/a)+2*a*ArcTan((3*b)/a)-3*b*Log(a^2+b^2)+3*b*Log(a^2+9*b^2))");
    check("N(Integrate(E^(-a*x)*Sin(b*x)^3/x^2, {x,0,Infinity}, Assumptions->a>0&&b>0) /. {a->3/2,b->2})", //
        "0.812646");
    check("NIntegrate(E^(-a*x)*Sin(b*x)^3/x^2 /. {a->3/2,b->2}, {x,0,Infinity})", //
        "0.812646");
    // nothing assumed: the formula is the one for real frequencies
    check("Integrate(E^(-a*x)*(Cos(b*x)-Cos(c*x))/x, {x,0,Infinity})", //
        "ConditionalExpression(Log((a^2+c^2)/(a^2+b^2))/2,a>0&&b∈Reals&&c∈Reals)");
    check("Integrate(E^(-2*x)*(1-Cos(3*x))/x, {x,0,Infinity})", //
        "-Log(4)/2+Log(13)/2");
    check("N(Integrate(E^(-2*x)*(1-Cos(3*x))/x, {x,0,Infinity}))", //
        "0.589327");
    check("NIntegrate(E^(-2*x)*(1-Cos(3*x))/x, {x,0,Infinity})", //
        "0.589327");
  }

  /** The numerical integral of the Bessel function at the sample points takes seconds. */
  @Test
  @Tag(TestTags.SLOW)
  public void testLaplaceTransformOfBesselJ() {
    check("Integrate(E^(-p*x)*BesselJ(0,q*x), {x,0,Infinity}, Assumptions->p>0&&q>0)", //
        "1/Sqrt(p^2+q^2)");
    check("N(Integrate(E^(-p*x)*BesselJ(0,q*x), {x,0,Infinity}, Assumptions->p>0&&q>0) /. {p->3/2,q->2})", //
        "0.4");
    check("NIntegrate(E^(-p*x)*BesselJ(0,q*x) /. {p->3/2,q->2}, {x,0,Infinity})", //
        "0.4");
    check("Integrate(E^(-p*x)*BesselJ(v,q*x), {x,0,Infinity}, Assumptions->p>0&&q>0&&v>-1)", //
        "(-p+Sqrt(p^2+q^2))^v/(q^v*Sqrt(p^2+q^2))");
    check("N(Integrate(E^(-p*x)*BesselJ(v,q*x), {x,0,Infinity}, Assumptions->p>0&&q>0&&v>-1) /. {p->3/2,q->2,v->1/3})", //
        "0.31748");
    check("NIntegrate(E^(-p*x)*BesselJ(v,q*x) /. {p->3/2,q->2,v->1/3}, {x,0,Infinity})", //
        "0.31748");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testDeclined() {
    // the limit of the antiderivative at 0 was "0 if a>1 and a<1"
    check("Integrate(Sin(x)^a*Cos(x)^b, {x,0,Pi/4})", //
        "Integrate(Cos(x)^b*Sin(x)^a,{x,0,Pi/4})");
  }
}
