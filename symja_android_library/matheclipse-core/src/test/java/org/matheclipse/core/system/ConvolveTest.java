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
    // shifted Gaussians and normal densities (the cases of Woxi PR #813 and beyond). The second check
    // of each pair compares with Mathematica's result at a sample point.
    check("Convolve(E^(-(x-2)^2), E^(-(x-1)^2), x, y)", //
        "Sqrt(Pi/2)/E^((3-y)^2/2)");
    check("PossibleZeroQ((Convolve(E^(-(x-2)^2), E^(-(x-1)^2), x, y)) - (Sqrt(Pi/2)/E^((-3 + y)^2/2)) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    check("Convolve(E^(-(x-2)^2), E^(-2*(x-1)^2), x, y)", //
        "Sqrt(Pi/3)/E^(2/3*(3-y)^2)");
    check("PossibleZeroQ((Convolve(E^(-(x-2)^2), E^(-2*(x-1)^2), x, y)) - (Sqrt(Pi/3)/E^((2*(-3 + y)^2)/3)) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    check("Convolve(E^(-(x-2)^2), E^(-x^2), x, y)", //
        "Sqrt(Pi/2)/E^((2-y)^2/2)");
    check("PossibleZeroQ((Convolve(E^(-(x-2)^2), E^(-x^2), x, y)) - (Sqrt(Pi/2)/E^((-2 + y)^2/2)) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    check("Convolve(3*E^(-(x-2)^2 + x), E^(-x^2), x, y)", //
        "3*E^(9/4-(5/2-y)^2/2)*Sqrt(Pi/2)");
    check("PossibleZeroQ((Convolve(3*E^(-(x-2)^2 + x), E^(-x^2), x, y)) - (3*E^(-7/8 - ((-5 + y)*y)/2)*Sqrt(Pi/2)) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    check("Convolve(E^(-a*(x-u)^2), E^(-b*(x-v)^2), x, y)", //
        "Sqrt(Pi)/(Sqrt(a+b)*E^((a*b*(-u-v+y)^2)/(a+b)))");
    check("PossibleZeroQ((Convolve(E^(-a*(x-u)^2), E^(-b*(x-v)^2), x, y)) - (Sqrt(Pi)/(Sqrt(a + b)*E^((a*b*(u + v - y)^2)/(a + b)))) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    check("Convolve(PDF(NormalDistribution(0, 1), x), PDF(NormalDistribution(0, 1), x), x, y)", //
        "1/(2*E^(y^2/4)*Sqrt(Pi))");
    check("PossibleZeroQ((Convolve(PDF(NormalDistribution(0, 1), x), PDF(NormalDistribution(0, 1), x), x, y)) - (1/(2*E^(y^2/4)*Sqrt(Pi))) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    check("Convolve(PDF(NormalDistribution(0, 1), x - 2), PDF(NormalDistribution(0, 1), x - 1), x, y)", //
        "1/(2*E^((3-y)^2/4)*Sqrt(Pi))");
    check("PossibleZeroQ((Convolve(PDF(NormalDistribution(0, 1), x - 2), PDF(NormalDistribution(0, 1), x - 1), x, y)) - (1/(2*E^((-3 + y)^2/4)*Sqrt(Pi))) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    check("Convolve(PDF(NormalDistribution(0, 1), x - t), PDF(NormalDistribution(0, 1), x - s), x, y)", //
        "1/(2*E^((-s-t+y)^2/4)*Sqrt(Pi))");
    check("PossibleZeroQ((Convolve(PDF(NormalDistribution(0, 1), x - t), PDF(NormalDistribution(0, 1), x - s), x, y)) - (1/(2*E^((s + t - y)^2/4)*Sqrt(Pi))) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    check("Convolve(PDF(NormalDistribution(m1, s1), x), PDF(NormalDistribution(m2, s2), x), x, y)", //
        "1/(E^((-m1-m2+y)^2/(2*(s1^2+s2^2)))*Sqrt(2*Pi)*s1*s2*Sqrt((s1^2+s2^2)/(s1^2*s2^2)))");
    check("PossibleZeroQ((Convolve(PDF(NormalDistribution(m1, s1), x), PDF(NormalDistribution(m2, s2), x), x, y)) - (1/(E^((m1 + m2 - y)^2/(2*(s1^2 + s2^2)))*Sqrt(2*Pi)*s1*Sqrt(s1^(-2) + s2^(-2))*s2)) /. " //
        + "{y->37/100, s->3/10, t->-11/10, m1->2/5, m2->-1/5, s1->7/10, s2->13/10, a->4/5, b->19/10, u->1/4, v->-3/5})", //
        "True");
    // not a Gaussian or not convergent: no closed form from the Gaussian rule
    check("Convolve(E^x^2,E^(-x^2),x,y)", //
        "Convolve(E^x^2,E^(-x^2),x,y)");
    check("Convolve(E^(-x^3),E^(-x^2),x,y)", //
        "Convolve(E^(-x^3),E^(-x^2),x,y)");
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
