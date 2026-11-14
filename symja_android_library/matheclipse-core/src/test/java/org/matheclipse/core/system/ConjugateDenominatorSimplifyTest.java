package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Products with sums of square roots in the denominator, which the simplifier multiplies by the
 * conjugates: the number factor of a conjugate pair is taken into the value once.
 */
public class ConjugateDenominatorSimplifyTest extends ExprEvaluatorTestCase {

  @Test
  public void testConjugateNumberFactorOnce() {
    // (5+Sqrt(3))*(5-Sqrt(3)) == 22 and a second denominator which is no number after the
    // multiplication by its conjugate: the value was divided by 22 twice
    String e = "(-2 + (5 + Sqrt(3))^2)/((5 + Sqrt(3))*(-4 + (5 + Sqrt(3))^4))";
    check("Abs(N(Simplify(" + e + ") - " + e + ", 30)) < 10^-25", //
        "True");
    check("Abs(N(FullSimplify(" + e + ") - " + e + ", 30)) < 10^-25", //
        "True");
    check("Abs(N(Apart(" + e + ") - " + e + ", 30)) < 10^-25", //
        "True");
    check("Simplify(" + e + ")", //
        "1/220*(3-4/Sqrt(3))");
  }

  @Test
  public void testRootOfNegativeQuotient() {
    // the sign goes into the negative sum: a root of a product of positive factors, which isn't
    // split and merged back until the recursion limit
    check("Sqrt(-5/(-4+Sqrt(3)))", //
        "Sqrt(5/(4-Sqrt(3)))");
    check("Sqrt(-3/(1-Pi))", //
        "Sqrt(3/(-1+Pi))");
    check("(-5/(-4+Sqrt(3)))^(1/3)", //
        "(5/(4-Sqrt(3)))^(1/3)");
    check("Sqrt(5)*Sqrt(-1/(-4+Sqrt(3)))", //
        "Sqrt(5)/Sqrt(4-Sqrt(3))");
    check("N(Sqrt(-(4+Sqrt(3))/(-4+Sqrt(3)))*Sqrt(-3+4*Sqrt(3)), 20)", //
        "3.1509051446013904362");
    check("Sqrt(3)*Sqrt(Pi)", //
        "Sqrt(3*Pi)");
  }

  @Test
  public void testIntegralOverQuarticRadical() {
    // the partial fractions of 1/(x^2+5*x+5) over Sqrt(1+x^4): the coefficients of the
    // elliptic and logarithmic parts are simplified in the rules
    check("f = 1/((x^2 + 5*x + 5)*Sqrt(1 + x^4)); r = Integrate(f, x);"
        + "Table(Abs(N((D(r, x) - f) /. x -> p, 30)) < 10^-20, {p, {2/7, 3/2, 11/5}})", //
        "{True,True,True}");
    check("f = 1/((x^2 + 7*x + 11)*Sqrt(2 - x^2 + x^4)); r = Integrate(f, x);"
        + "Table(Abs(N((D(r, x) - f) /. x -> p, 30)) < 10^-20, {p, {2/7, 3/2, 11/5}})", //
        "{True,True,True}");
  }
}
