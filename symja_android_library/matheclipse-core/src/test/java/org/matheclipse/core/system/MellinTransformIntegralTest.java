package org.matheclipse.core.system;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Mellin transforms <code>Integrate(x^(s-1)*f(a*x), {x,0,Infinity})</code> of special functions.
 */
public class MellinTransformIntegralTest extends ExprEvaluatorTestCase {

  @Test
  public void testDecaying() {
    // the integrand decays: the value is compared with the numerical integral over a finite
    // range - over {x,0,Infinity} NIntegrate would get the value of the table itself
    check("Integrate(x^(s-1)*BesselK(v,a*x), {x,0,Infinity}, Assumptions->s>v&&s>-v&&a>0)", //
        "(Gamma(1/2*(s-v))*Gamma(1/2*(s+v)))/(2^(2-s)*a^s)");
    check("N(Integrate(x^(s-1)*BesselK(v,a*x), {x,0,Infinity}, Assumptions->s>v&&s>-v&&a>0) /. {s->7/4,v->1/3,a->2})", //
        "0.314068");
    check("NIntegrate(x^(s-1)*BesselK(v,a*x) /. {s->7/4,v->1/3,a->2}, {x,0,30})", //
        "0.314068");
    check("Integrate(x^(s-1)*Csch(a*x), {x,0,Infinity}, Assumptions->s>1&&a>0)", //
        "(2*(1-1/2^s)*Gamma(s)*Zeta(s))/a^s");
    check("N(Integrate(x^(s-1)*Csch(a*x), {x,0,Infinity}, Assumptions->s>1&&a>0) /. {s->7/4,a->2})", //
        "0.753549");
    check("NIntegrate(x^(s-1)*Csch(a*x) /. {s->7/4,a->2}, {x,0,30})", //
        "0.753549");
    check("Integrate(x^(s-1)*Sech(a*x), {x,0,Infinity}, Assumptions->s>0&&a>0)", //
        "(2^(1-2*s)*Gamma(s)*(HurwitzZeta(s,1/4)-HurwitzZeta(s,3/4)))/a^s");
    check("N(Integrate(x^(s-1)*Sech(a*x), {x,0,Infinity}, Assumptions->s>0&&a>0) /. {s->3/4,a->2})", //
        "1.06688");
    check("NIntegrate(x^(s-1)*Sech(a*x) /. {s->3/4,a->2}, {x,0,30})", //
        "1.06688");
    check("Integrate(x^(s-1)*BesselK(u,a*x)*BesselK(v,a*x), {x,0,Infinity}, Assumptions->u>0&&v>0&&s>u+v&&a>0)", //
        "(Gamma(1/2*(s-u-v))*Gamma(1/2*(s+u-v))*Gamma(1/2*(s-u+v))*Gamma(1/2*(s+u+v)))/(2^(\n3-s)*a^s*Gamma(s))");
    check("N(Integrate(x^(s-1)*BesselK(u,a*x)*BesselK(v,a*x), {x,0,Infinity}, Assumptions->u>0&&v>0&&s>u+v&&a>0) /. {s->7/4,u->1/3,v->1/2,a->2})", //
        "0.284393");
    check("NIntegrate(x^(s-1)*BesselK(u,a*x)*BesselK(v,a*x) /. {s->7/4,u->1/3,v->1/2,a->2}, {x,0,30})", //
        "0.284393");
  }

  /** The numerical integrals of the Bessel function take seconds. */
  @Test
  @Tag(TestTags.SLOW)
  public void testDampedBesselJ() {
    check("Integrate(x^(s-1)*E^(-a*x)*BesselJ(v,b*x), {x,0,Infinity}, Assumptions->s+v>0&&a>0&&b>0)", //
        "((1/2)^v*b^v*Gamma(s+v)*Hypergeometric2F1(1/2*(s+v),1/2*(1+s+v),1+v,-b^2/a^2))/(a^(s+v)*Gamma(\n1+v))");
    check("N(Integrate(x^(s-1)*E^(-a*x)*BesselJ(v,b*x), {x,0,Infinity}, Assumptions->s+v>0&&a>0&&b>0) /. {s->7/4,v->1/3,a->2,b->3/2})", //
        "0.145963");
    check("NIntegrate(x^(s-1)*E^(-a*x)*BesselJ(v,b*x) /. {s->7/4,v->1/3,a->2,b->3/2}, {x,0,30})", //
        "0.145963");
    check("Integrate(x^(s-1)*E^(-a^2*x^2)*BesselJ(v,b*x), {x,0,Infinity}, Assumptions->s+v>0&&a>0&&b>0)", //
        "(b^v*Gamma(1/2*(s+v))*Hypergeometric1F1(1/2*(s+v),1+v,-b^2/(4*a^2)))/(2^(1+v)*a^(s+v)*Gamma(\n1+v))");
    check("N(Integrate(x^(s-1)*E^(-a^2*x^2)*BesselJ(v,b*x), {x,0,Infinity}, Assumptions->s+v>0&&a>0&&b>0) /. {s->7/4,v->1/3,a->2,b->3/2})", //
        "0.105222");
    check("NIntegrate(x^(s-1)*E^(-a^2*x^2)*BesselJ(v,b*x) /. {s->7/4,v->1/3,a->2,b->3/2}, {x,0,30})", //
        "0.105222");
  }

  @Test
  public void testOscillating() {
    // no numerical integral: the integrand oscillates without decay. Special values are known
    check("Integrate(x^(s-1)*Sin(a*x), {x,0,Infinity}, Assumptions->-1<s&&s<1&&a>0)", //
        "(Gamma(s)*Sin(1/2*Pi*s))/a^s");
    check("Integrate(x^(s-1)*Cos(a*x), {x,0,Infinity}, Assumptions->0<s&&s<1&&a>0)", //
        "(Cos(1/2*Pi*s)*Gamma(s))/a^s");
    // Sqrt(Pi/2)/Sqrt(2)
    check("Integrate(Sin(2*x)/Sqrt(x), {x,0,Infinity})", //
        "Sqrt(Pi)/2");
    check("Integrate(x^(s-1)*BesselJ(v,a*x), {x,0,Infinity}, Assumptions->v>0&&0<s&&s<3/2&&a>0)", //
        "Gamma(1/2*(s+v))/(2^(1-s)*a^s*Gamma(1/2*(2-s+v)))");
    // 1/3
    check("Integrate(BesselJ(0, 3*x), {x,0,Infinity})", //
        "1/3");
    check("Integrate(x^(s-1)*BesselY(v,a*x), {x,0,Infinity}, Assumptions->v>0&&s>v&&s<3/2&&a>0)", //
        "(-Cos(1/2*Pi*(s-v))*Gamma(1/2*(s-v))*Gamma(1/2*(s+v)))/(2^(1-s)*a^s*Pi)");
    check("Integrate(x^(s-1)*StruveH(0,a*x), {x,0,Infinity}, Assumptions->0<s&&s<1/2&&a>0)", //
        "(Gamma(s/2)*Tan(1/2*Pi*s))/(2^(1-s)*a^s*Gamma(1/2*(2-s)))");
    check("Integrate(x^(s-1)*SinIntegral(a*x), {x,0,Infinity}, Assumptions->-1/2<s&&s<0&&a>0)", //
        "(-Gamma(s)*Sin(1/2*Pi*s))/(a^s*s)");
    check("Integrate(x^(s-1)*CosIntegral(a*x), {x,0,Infinity}, Assumptions->0<s&&s<1&&a>0)", //
        "(-Cos(1/2*Pi*s)*Gamma(s))/(a^s*s)");
    check("Integrate(x^(s-1)*BesselJ(u,a*x)*BesselJ(v,a*x), {x,0,Infinity}, Assumptions->u>0&&v>0&&0<s&&s<1&&a>0)", //
        "(Gamma(1-s)*Gamma(1/2*(s+u+v)))/(2^(1-s)*a^s*Gamma(1/2*(2-s+u-v))*Gamma(1/2*(2-s-u+v))*Gamma(\n1/2*(2-s+u+v)))");
  }

  @Test
  public void testTabulated() {
    check("Integrate(x^(s-1)*AiryAi(a*x), {x,0,Infinity}, Assumptions->s>0&&a>0)", //
        "(3^(1/3*(-2-s))*Gamma(s))/(a^s*Gamma(1/3*(2+s)))");
    // 1/3
    check("Integrate(AiryAi(x), {x,0,Infinity})", //
        "1/3");
    // this was 0: the limit of the antiderivative at 0 was wrong
    check("Integrate(x^(s-1)*ExpIntegralE(1,a*x), {x,0,Infinity}, Assumptions->s>0&&a>0)", //
        "Gamma(s)/(a^s*s)");
    check("Integrate(x^2*ExpIntegralE(1,x), {x,0,Infinity})", //
        "2/3");
    check("Integrate(x^(s-1)*EllipticK(-a*x), {x,0,Infinity}, Assumptions->0<s&&s<1/2&&a>0)", //
        "(Gamma(1/2-s)^2*Gamma(s))/(2*a^s*Gamma(1-s))");
    check("Integrate(x^(s-1)*Hypergeometric2F1(a,b,c,-x), {x,0,Infinity}, Assumptions->0<s&&s<a&&s<b)", //
        "(Gamma(c)*Gamma(a-s)*Gamma(b-s)*Gamma(s))/(Gamma(a)*Gamma(b)*Gamma(c-s))");
    check("N(Integrate(x^(s-1)*Hypergeometric2F1(a,b,c,-x), {x,0,Infinity}, Assumptions->0<s&&s<a&&s<b) /. {s->3/4,a->2,b->3,c->5/2})", //
        "0.910117");
  }

  @Test
  public void testConditionsAndElementaryValues() {
    // the strip which is not assumed is the condition of the result
    check("Integrate(x^(s-1)*SinIntegral(a*x), {x,0,Infinity}, Assumptions->-1<s&&s<0&&a>0)", //
        "ConditionalExpression((-Gamma(s)*Sin(1/2*Pi*s))/(a^s*s),s>-1/2)");
    check("Integrate(x^(s-1)*BesselJ(v,a*x), {x,0,Infinity})", //
        "ConditionalExpression(Gamma(1/2*(s+v))/(2^(1-s)*a^s*Gamma(1/2*(2-s+v))),a>0&&s+v>\n0&&s<3/2)");
    // elementary integrands keep their elementary values
    check("Integrate(Sech(x), {x,0,Infinity})", //
        "Pi/2");
    check("Integrate(x*Csch(x), {x,0,Infinity})", //
        "Pi^2/4");
  }

  @Test
  public void testAssumptionsInForce() {
    // the option adds to the assumptions which are in force
    check("Assuming(a>0, Integrate(x^(s-1)*BesselK(0,a*x), {x,0,Infinity}, Assumptions->s>0))", //
        "Gamma(s/2)^2/(2^(2-s)*a^s)");
    check("Assuming(a>0&&s>0, Integrate(x^(s-1)*BesselK(0,a*x), {x,0,Infinity}))", //
        "Gamma(s/2)^2/(2^(2-s)*a^s)");
    check("Integrate(x^(s-1)*BesselK(0,a*x), {x,0,Infinity}, Assumptions->s>0)", //
        "ConditionalExpression(Gamma(s/2)^2/(2^(2-s)*a^s),a>0)");
    check("Assuming(a>0, Integrate(1/(x^2+a^2), {x,-Infinity,Infinity}))", //
        "Pi/a");
  }

  @Test
  @Tag(TestTags.SLOW)
  public void testTabulatedRowsAgainstQuadrature() {
    // The rows which are not confirmed when they are used: the integral over a finite range,
    // with the part of the integrand which does not oscillate integrated beyond it. An
    // integral over {x,0,Infinity} would be answered by the table itself.
    check("Abs((NIntegrate(x^(-5/4)*SinIntegral(2*x), {x,0,200}) + Pi/2*200^(-1/4)*4) - (Integrate(x^(s-1)*SinIntegral(a*x), {x,0,Infinity}, Assumptions->-1/2<s&&s<0&&a>0) /. {s->-1/4, a->2})) < 1/500", //
        "True");
    check("Abs((NIntegrate(x^(-3/4)*CosIntegral(2*x), {x,0,200})) - (Integrate(x^(s-1)*CosIntegral(a*x), {x,0,Infinity}, Assumptions->0<s&&s<1&&a>0) /. {s->1/4, a->2})) < 1/500", //
        "True");
    check("Abs((NIntegrate(x^(-3/4)*StruveH(0, 2*x), {x,0,200}) + 2/(Pi*2)*200^(-3/4)/(3/4)) - (Integrate(x^(s-1)*StruveH(0,a*x), {x,0,Infinity}, Assumptions->0<s&&s<1/2&&a>0) /. {s->1/4, a->2})) < 1/500", //
        "True");
    check("Abs((NIntegrate(x^(-3/4)*BesselJ(1, 2*x), {x,0,60})) - (Integrate(x^(s-1)*BesselJ(v,a*x), {x,0,Infinity}, Assumptions->v>0&&0<s&&s<3/2&&a>0) /. {s->1/4, v->1, a->2})) < 1/50", //
        "True");
    check("Abs((NIntegrate(x^(-3/2)*BesselJ(1, 2*x)^2, {x,0,60}) + 60^(-3/2)/(Pi*2)/(3/2)) - (Integrate(x^(s-1)*BesselJ(u,a*x)*BesselJ(v,a*x), {x,0,Infinity}, Assumptions->u>0&&v>0&&-1<s&&s<1&&a>0) /. {s->-1/2, u->1, v->1, a->2})) < 1/500", //
        "True");
    check("Abs((NIntegrate(x^(-1/4)*AiryAi(2*x), {x,0,4})) - (Integrate(x^(s-1)*AiryAi(a*x), {x,0,Infinity}, Assumptions->s>0&&a>0) /. {s->3/4, a->2})) < 1/500", //
        "True");
    // EllipticK: the range beyond 1 by the substitution x -> 1/t
    check("Abs((NIntegrate(t^(-5/4)*EllipticK(-2/t), {t,0,1}) + NIntegrate(x^(-3/4)*EllipticK(-2*x), {x,0,1})) - (Integrate(x^(s-1)*EllipticK(-a*x), {x,0,Infinity}, Assumptions->0<s&&s<1/2&&a>0) /. {s->1/4, a->2})) < 1/500", //
        "True");
    // BesselY by its definition with BesselJ, whose row is confirmed above
    check("Abs(((Cos(v*Pi)*jm(v) - jm(-v))/Sin(v*Pi) /. jm(w_) :> (Gamma((s+w)/2)/(2^(1-s)*a^s*Gamma((2+w-s)/2))) /. {s->3/4, v->1/3, a->2}) - (Integrate(x^(s-1)*BesselY(v,a*x), {x,0,Infinity}, Assumptions->v>0&&s>v&&s<3/2&&a>0) /. {s->3/4, v->1/3, a->2})) < 10^-9", //
        "True");
  }
}
