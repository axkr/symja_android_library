package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Integrals whose value depends on the order or the signs of the parameters, integrals of a decay
 * times a periodic factor with Abs, and divergence which isn't claimed for a convergent integral.
 */
public class ParameterSignPeriodicIntegralTest extends ExprEvaluatorTestCase {

  @Test
  public void testOrderOfTheParameters() {
    // Pi*Min(p,q)/2: the samples of the parameters must not all have p < q
    check("Integrate(Sin(p*x)*Sin(q*x)/x^2, {x,0,Infinity}, Assumptions->p>0&&q>0) /. {p->3, q->1}", //
        "Pi/2");
    check("Integrate(Sin(p*x)*Sin(q*x)/x^2, {x,0,Infinity}, Assumptions->p>0&&q>0) /. {p->1, q->5}", //
        "Pi/2");
    // an odd saturating function of r*x with a real r: Sign(r) for r != 0
    check("Limit(SinIntegral(c*x), x->Infinity, Assumptions->Element(c, Reals))", //
        "ConditionalExpression(1/2*Pi*Sign(c),c!=0)");
    // at p == q the argument is 1, not infinite
    check("Limit(ArcTan((p-q)*x+1), x->Infinity, Assumptions->p>0&&q>0)", //
        "ConditionalExpression(1/2*Pi*Sign(p-q),p-q!=0)");
  }

  @Test
  public void testSignOfAMonomial() {
    check("{Refine(d/c > 0, c*d > 0), Refine(c^3*d < 0, c*d < 0), Refine(d/c > 0, c > 0)}", //
        "{True,True,d/c>0}");
    check("Chop(N(Integrate(Log(x)/((1+x^2)*(p^2+q^2*x^2)), {x,0,Infinity}, Assumptions->p*q>0) "
        + "/. {p->-2, q->-1}) - NIntegrate(Log(x)/((1+x^2)*(4+x^2)), {x,0,Infinity}), 10^-8)", //
        "0");
  }

  @Test
  public void testDecayTimesPeriodicFactor() {
    check("Integrate(Abs(Sin(2*x))*E^(-x), {x,0,Infinity})", //
        "2/5*(1+E^(Pi/2))/(-1+E^(Pi/2))");
    check("Chop(N(LaplaceTransform(Abs(Cos(t)), t, 3)) - "
        + "NIntegrate(Abs(Cos(t))*E^(-3*t), {t,0,40}), 10^-8)", //
        "0");
    // from a lower bound other than 0, and the transform for a symbolic s
    check("Chop(N(Integrate(Abs(Sin(x))*E^(-x), {x,2,Infinity})) - "
        + "NIntegrate(Abs(Sin(x))*E^(-x), {x,2,60}), 10^-8)", //
        "0");
    check("Chop(N(LaplaceTransform(Abs(Sin(t)), t, s) /. s->3/2) - "
        + "NIntegrate(Abs(Sin(t))*E^(-3/2*t), {t,0,40}), 10^-8)", //
        "0");
  }

  @Test
  public void testNoFalseDivergence() {
    // the unevaluated Limit(..., x->Infinity) of the antiderivative isn't a divergence
    check("Check(Integrate(Log(1+5*x^2)/(1+3*x^2)^2, {x,0,Infinity}), failed)", //
        "Integrate(Log(1+5*x^2)/(1+3*x^2)^2,{x,0,Infinity})");
  }
}
