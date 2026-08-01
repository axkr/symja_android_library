package org.matheclipse.core.reflection.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

/**
 * Tests for {@link RootSum}.
 *
 */
public class RootSumTest extends ExprEvaluatorTestCase {

  @Test
  public void testSumOfRoots() {
    // The sum of the roots of x^n-c has no x^(n-1) term, so it is 0.
    check("RootSum(#^2 - 2 &, # &)", //
        "0");
    check("RootSum(#^3 - 2 &, # &)", //
        "0");
    check("RootSum(#^4 - 1 &, # &)", //
        "0");
    // Vieta: the roots 2 and 3 sum to 5.
    check("RootSum(#^2 - 5*# + 6 &, # &)", //
        "5");
  }

  @Test
  public void testPowerSumsOfRoots() {
    // roots 1, 2: sum of squares = 5
    check("RootSum(#^2 - 3*# + 2 &, #^2 &)", //
        "5");
    // roots 2, 3: sum of cubes = 8+27 = 35
    check("RootSum(#^2 - 5*# + 6 &, #^3 &)", //
        "35");
    // roots +-Sqrt(2): sum of squares = 4
    check("RootSum(#^2 - 2 &, #^2 &)", //
        "4");
    // roots +-I: sum of squares = -2
    check("RootSum(#^2 + 1 &, #^2 &)", //
        "-2");
    // roots 1, -1, I, -I: sum of squares = 0, sum of 4th powers = 4
    check("RootSum(#^4 - 1 &, #^2 &)", //
        "0");
    check("RootSum(#^4 - 1 &, #^4 &)", //
        "4");
  }

  @Test
  public void testUnsolvablePolynomials() {
    // The power-sum method needs no explicit roots, so it also works for polynomials
    // that have no closed-form (radical) roots.
    check("RootSum(#^3 - # - 1 &, #^2 &)", //
        "2");
    check("RootSum(#^3 + # + 1 &, #^2 &)", //
        "-2");
    check("RootSum(#^5 - # - 1 &, #^2 &)", //
        "0");
  }

  @Test
  public void testNonMonicAndAffineForm() {
    // non-monic f: 2*x^2-8 has roots +-2; sum of squares = 8
    check("RootSum(2*#^2 - 8 &, #^2 &)", //
        "8");
    // affine form 3*#^2+1: 3*(sum of squares)+1*(root count) = 3*4+2 = 14
    check("RootSum(#^2 - 2 &, 3*#^2 + 1 &)", //
        "14");
    // constant form sums to constant*(number of roots)
    check("RootSum(#^2 - 2 &, 5 &)", //
        "10");
    // linear polynomial: single root 3, squared = 9
    check("RootSum(# - 3 &, #^2 &)", //
        "9");
  }

  @Test
  public void testFormDegreeExceedingPolynomialDegree() {
    // A form of degree >= deg(f) is first reduced modulo f. For x^3-x-1:
    // #^3 -> #+1 gives sum = 3, and #^4 -> #^2+# gives sum = 2.
    check("RootSum(#^3 - # - 1 &, #^3 &)", //
        "3");
    check("RootSum(#^3 - # - 1 &, #^4 &)", //
        "2");
    check("RootSum(#^2 - 2 &, #^4 &)", //
        "8");
  }

  @Test
  public void testInertForms() {
    // A non-polynomial (Log) form is left unevaluated; this is the shape produced by
    // Integrate for an irreducible high-degree denominator.
    check("RootSum(1 + #1 + #1^2 + #1^3 + #1^4 & , Log(x + #1) & )", //
        "RootSum(1+#1+#1^2+#1^3+#1^4&,Log(x+#1)&)");
    // ... and it stays inert for a solvable quartic too, even though the roots could be written
    // as radicals: the closed RootSum form is the point.
    check("RootSum(#^4+1 &, Log(x-#1)/(4*#1^3) &)", //
        "RootSum(#1^4+1&,Log(x-#1)/(4*#1^3)&)");
  }

  @Test
  public void testDerivativeIsRationalFunction() {
    // D(RootSum(p&, Log(x-#1)/p'(#1) &), x) telescopes back to the integrand 1/p(x). Now that the
    // cubic is no longer expanded into radicals first, this collapses just like the quintic below.
    check("D(RootSum(#1^3 - 2 &, Log(x - #1)/(3*#1^2) &), x)", //
        "1/(-2+x^3)");
    check("D(RootSum(#1^5 + #1 - 7 &, Log(x - #1)/(5*#1^4 + 1) &), x)", //
        "1/(-7+x+x^5)");
    // The Integrate-produced antiderivative of x^7/(1+x^8) differentiates back to it.
    check("D(RootSum(1+#1^8&,(Log(x-#1)*#1^7)/(8*#1^7)&),x)", //
        "x^7/(1+x^8)");
  }

  /**
   * Regression: a Log summand over the solvable quartic {@code x^4+1} is expanded over its four
   * {@code (-1)^(1/4)} roots and then {@code FullSimplify}'d; that FullSimplify recursed until the
   * Java stack overflowed. The explicit sum is now returned even when the cosmetic simplify fails.
   *
   * <p>
   * The expansion is no longer automatic - it has to be requested with {@code Normal}.
   */
  @Test
  public void testSolvableQuarticLogSummand() {
    check("Normal(RootSum(#^4+1 &, Log(x-#1)/(4*#1^3) &))", //
        "-1/4*(-1)^(1/4)*Log(-(-1)^(1/4)+x)+1/4*(-1)^(1/4)*Log((-1)^(1/4)+x)-1/4*(-1)^(3/\n"
            + "4)*Log(-(-1)^(3/4)+x)+1/4*(-1)^(3/4)*Log((-1)^(3/4)+x)");
  }

  @Test
  public void testRootSumNumeric() {
    check("N(RootSum(#^5 - 3 # - 7 &, Sin))", //
        "0.292188+I*(-4.44089*10^-16)");
    // A Sin summand is not a rational function, but the roots of a quadratic are plain radicals, so
    // the sum is written out over them.
    check("RootSum(#^2 - # + a &, Sin(#) &)", //
        "Sin(1/2*(1-Sqrt(1-4*a)))+Sin(1/2*(1+Sqrt(1-4*a)))");
  }

  /**
   * A rational summand over a polynomial whose coefficients are symbolic. Both the root polynomial
   * and the summand carry free parameters here, so the modular inverse of the denominator has to be
   * computed over the field of rational functions in <code>a</code>, <code>b</code> and
   * <code>c</code>.
   */
  @Test
  public void testParametrizedPolynomialAndSummand() {
    // numerator and denominator are the negatives of the reference result
    // (-16*a+8*a^2-a^3-10*b^2+a*b^2+8*a*b*c+8*a*c^2-4*a^2*c^2-5*b*c^3+5*c^4) /
    // (-32*b+16*a*b-2*a^2*b+b^3+16*a*c-8*a^2*c+a^3*c-10*b^2*c+20*b*c^2+3*a*b*c^2-8*a*c^3-c^5)
    check("RootSum(#^5-a*#+b & ,(#^2- 1)/(#^3-2*#+c) &)", //
        "(16*a-8*a^2+a^3+10*b^2-a*b^2-8*a*b*c-8*a*c^2+4*a^2*c^2+5*b*c^3-5*c^4)/(32*b-16*a*b+\n"
            + "2*a^2*b-b^3-16*a*c+8*a^2*c-a^3*c+10*b^2*c-20*b*c^2-3*a*b*c^2+8*a*c^3+c^5)");
    // the sum of (r^2-1)/(r^3-2*r+5) over the five roots of r^5-2*r+3
    check("RootSum(#^5-a*#+b & ,(#^2- 1)/(#^3-2*#+c) &) /. {a->2,b->3,c->5}", //
        "-235/597");
  }
}
