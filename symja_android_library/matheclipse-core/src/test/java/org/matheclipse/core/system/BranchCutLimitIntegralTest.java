package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * One-sided limits on a branch cut, definite integrals whose antiderivative crosses a cut, and
 * integrals whose value holds for real parameters.
 */
public class BranchCutLimitIntegralTest extends ExprEvaluatorTestCase {

  @Test
  public void testOneSidedLimitsOnACut() {
    check("{Limit(ArcTan(-3, y), y -> 0, Direction -> \"FromBelow\"), "
        + "Limit(ArcTan(-3, y), y -> 0, Direction -> \"FromAbove\"), Limit(ArcTan(-3, y), y -> 0)}", //
        "{-Pi,Pi,Indeterminate}");
    check("{Limit(Log(-5 + I*y), y -> 0, Direction -> \"FromBelow\"), Limit(Log(-5 + I*y), y -> 0)}", //
        "{-I*Pi+Log(5),Indeterminate}");
    check("Limit((-3 + I*y)^(1/2), y -> 0, Direction -> \"FromBelow\")", //
        "-I*Sqrt(3)");
    // the side of the cut inside a 0/0 form: the derivative of Log there
    check("Limit((Log(-2 + I*y) - Log(2) + I*Pi)/y, y -> 0, Direction -> \"FromBelow\")", //
        "-I*1/2");
  }

  @Test
  public void testArcTanOfTwoVanishingArguments() {
    // the direction of (Sin(x), 1-Cos(x)) for x -> 0
    check("{Limit(ArcTan(Sin(x), 1 - Cos(x)), x -> 0, Direction -> \"FromBelow\"), "
        + "Limit(ArcTan(Sin(x), 1 - Cos(x)), x -> 0, Direction -> \"FromAbove\")}", //
        "{Pi,0}");
    check("ArcTan(Cot(3))", //
        "-3+Pi/2");
  }

  @Test
  public void testNumbersAsAtoms() {
    check("{Total(2/9), DeleteCases(2/9, 2)}", //
        "{2/9,2/9}");
    check("Select(2/9, True&)", //
        "Select(2/9,True&)");
  }

  @Test
  public void testNumbersMatchAPatternedHead() {
    // a fraction is Rational(n,d) and a complex number Complex(re,im) to h_[...]
    check("{MatchQ(4/11, h_[___]), MatchQ(-2*I, h_[___]), MatchQ(4, h_[___]), MatchQ(1.25, _[__])}", //
        "{True,True,False,False}");
    check("Replace(4/11, h_[a_, b_] :> {h, a, b})", //
        "{Rational,4,11}");
    check("Replace(3-2*I, h_[a_, b_] :> {h, a, b})", //
        "{Complex,3,-2}");
    check("Cases({3/8, 5, z, 1+I, g(w)}, _[__])", //
        "{3/8,1+I,g(w)}");
    check("Cases({1/3, 7, 1/9}, h_[1, d_] :> d)", //
        "{3,9}");
    // a real number is no Complex(re, im), an integer no Rational(n, d)
    check("{MatchQ(7, Complex(a_, b_)), MatchQ(7, Rational(a_, b_)), "
        + "MatchQ(3/4, Complex(a_, b_)), MatchQ(0.5, Complex(a_, b_)), MatchQ(2-I, Complex(a_, b_))}", //
        "{False,False,False,False,True}");
    check("Cases({4, 3*I, 1/5, 2.0, 2+I}, Complex(_, _))", //
        "{I*3,2+I}");
  }

  @Test
  public void testTangentAdditionAtAPole() {
    // Cot(1) is Tan(Pi/2-1), whose addition formula has Tan(Pi/2)
    check("Simplify(Csc(1) - Cot(1))", //
        "-Cot(1)+Csc(1)");
    check("Integrate(1/(x^2 - 2*x*Cos(1) + 1), x)", //
        "-ArcTan(Cot(1)-x*Csc(1))*Csc(1)");
    check("Integrate(1/(x^2 + 2*x*Cos(1) + 1), {x, -Infinity, Infinity})", //
        "Pi*Csc(1)");
  }

  @Test
  public void testAntiderivativeAcrossACut() {
    // Sqrt(-9+I*x) crosses the negative real axis at x == 0
    check("Chop(N(Integrate(x/Sqrt(-9 + I*x), {x, -2, 2}) - "
        + "NIntegrate(x/Sqrt(-9 + I*x), {x, -2, 2})))", //
        "0");
    // from the branch point x == 2 of the radicand: Sqrt(4-x^2)/Sqrt(x^3-4*x) is I/Sqrt(x) there
    check("Chop(N(Integrate(1/Sqrt(x^3 - 4*x), {x, 2, 3})) - "
        + "NIntegrate(1/Sqrt(x^3 - 4*x), {x, 2, 3}), 10^-6)", //
        "0");
  }

  @Test
  public void testRealParameters() {
    check("Integrate(Cos(b*x)/(x^2 + 9), {x, -Infinity, Infinity})", //
        "ConditionalExpression(Pi/(3*E^(3*Abs(b))),b∈Reals)");
    check("Integrate(Cos(b*x)/(x^2 + 9), {x, -Infinity, Infinity}, Assumptions -> b > 0)", //
        "Pi/(3*E^(3*b))");
  }
}
