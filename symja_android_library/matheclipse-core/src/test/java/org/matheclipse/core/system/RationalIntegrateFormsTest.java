package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * The form of the antiderivative of a rational function: <code>RootSum</code> for cubic and quartic
 * denominators which need nested radicals, and integer polynomials in <code>Log</code>,
 * <code>ArcTan</code> and <code>ArcTanh</code> arguments.
 */
public class RationalIntegrateFormsTest extends ExprEvaluatorTestCase {

  @Test
  public void testRootSumForCubicAndQuartic() {
    check("Integrate(1/(x^3+2*x+5),x)", //
        "RootSum(5+2*#1+#1^3&,Log(x-#1)/(2+3*#1^2)&)");
    check("Integrate(t^2/(t^3-4*t+7),t)", //
        "RootSum(7-4*#1+#1^3&,(Log(t-#1)*#1^2)/(-4+3*#1^2)&)");
    // the constant factor of the summand is taken in front of the RootSum
    check("Integrate(1/(-3*x^3-6*x-15),x)", //
        "-RootSum(5+2*#1+#1^3&,Log(x-#1)/(2+3*#1^2)&)/3");
    // a polynomial part, a logarithm and a RootSum together; the residue is reduced modulo the
    // cubic
    check("Integrate((x^4+1)/((x-2)*(x^3-x+3)),x)", //
        "x+17/9*Log(2-x)+RootSum(3-#1+#1^3&,(-6*Log(x-#1)+11*Log(x-#1)*#1+Log(x-#1)*#1^2)/(-\n"
            + "1+3*#1^2)&)/9");
    check("Integrate(1/(x^4+3*x+1),x)", //
        "RootSum(1+3*#1+#1^4&,Log(x-#1)/(3+4*#1^3)&)");
    // three real roots
    check("Integrate(x/(x^3-6*x+2),x)", //
        "RootSum(2-6*#1+#1^3&,(Log(x-#1)*#1)/(-2+#1^2)&)/3");
  }

  @Test
  public void testRootSumIsAnAntiderivative() {
    check("Together(D(Integrate((x^4+1)/((x-2)*(x^3-x+3)),x),x)-(x^4+1)/((x-2)*(x^3-x+3)))", //
        "0");
    check("Together(D(Integrate(1/(-3*x^3-6*x-15),x),x)-1/(-3*x^3-6*x-15))", //
        "0");
    check("Together(D(Integrate((2*x^2-1)/((x^2+2)*(x^3+3*x+1)),x),x)"
        + "-(2*x^2-1)/((x^2+2)*(x^3+3*x+1)))", //
        "0");
  }

  @Test
  public void testRadicalFriendlyCubicKeepsRadicals() {
    // a single cube root solves x^3+5: no RootSum
    check("FreeQ(Integrate(1/(x^3+5),x), RootSum)", //
        "True");
    // the shifted binomial (x-1)^3+5 gets a RootSum
    check("FreeQ(Integrate(1/(x^3-3*x^2+3*x+4),x), RootSum)", //
        "False");
    // the biquadratic x^4+2*x^2+5 keeps its radicals, the shifted (x-1)^4+2*(x-1)^2+5 doesn't
    check("FreeQ(Integrate(1/(x^4+2*x^2+5),x), RootSum)", //
        "True");
    check("FreeQ(Integrate(1/(x^4-4*x^3+8*x^2-8*x+8),x), RootSum)", //
        "False");
  }

  @Test
  public void testLogArgumentsAreIntegerPolynomials() {
    check("Integrate(1/(3*x-2),x)", //
        "Log(-2+3*x)/3");
    check("Integrate(x^2/(5*x-3),x)", //
        "3/25*x+x^2/10+9/125*Log(-3+5*x)");
    check("Integrate(x/(9+6*x^2),x)", //
        "Log(3+2*x^2)/12");
    check("Integrate(x/(10*x^2-6),x)", //
        "Log(3-5*x^2)/20");
    // several logarithms keep the positive constant term
    check("Integrate(1/((3*x-1)*(2*x+5)),x)", //
        "Log(1-3*x)/17-Log(5+2*x)/17");
  }

  @Test
  public void testArcTanArgumentIsAnIntegerPolynomial() {
    check("Integrate((3+x)/((x+1)*(5*x^2+2*x+3)),x)", //
        "1/3*Sqrt(7/2)*ArcTan((1+5*x)/Sqrt(14))+Log(1+x)/3-Log(3+2*x+5*x^2)/6");
    check("Integrate(1/(5*x^2+3*x-1),x)", //
        "(-2*ArcTanh((3+10*x)/Sqrt(29)))/Sqrt(29)");
    check("Together(D(Integrate((3+x)/((x+1)*(5*x^2+2*x+3)),x),x)-(3+x)/((x+1)*(5*x^2+2*x+3)))", //
        "0");
  }

  /** A cubic or quartic factor whose residues are all equal gives a plain logarithm. */
  @Test
  public void testConstantResidueIsALogarithm() {
    check("Integrate((2+6*x^2)/(3+2*x+2*x^3),x)", //
        "Log(3+2*x+2*x^3)");
    check("Integrate((5+20*x^3)/(3+5*x+5*x^4),x)", //
        "Log(3+5*x+5*x^4)");
    // the residue depends on the root
    check("Integrate(x^2/(1+2*x+x^3),x)", //
        "RootSum(1+2*#1+#1^3&,(Log(x-#1)*#1^2)/(2+3*#1^2)&)");
  }

  @Test
  public void testShiftedBinomialAndReducedResidue() {
    // (x+2)^3-3 needs the shift x -> x-2 for a root extraction: a RootSum
    check("Integrate(1/(x^3+6*x^2+12*x+5),x)", //
        "RootSum(5+12*#1+6*#1^2+#1^3&,Log(x-#1)/(4+4*#1+#1^2)&)/3");
    // the residue x/((x+1)*f'(x)) is reduced modulo f to a polynomial over f'
    check("Integrate(x/((x+1)*(x^3+2*x+1)),x)", //
        "Log(1+x)/2-RootSum(1+2*#1+#1^3&,(Log(x-#1)-Log(x-#1)*#1+Log(x-#1)*#1^2)/(2+3*#1^\n2)&)/2");
  }
}
