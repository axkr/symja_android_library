package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Sums and products whose closed form must not step over a pole, a zero or a divergence of the
 * terms, and the rational sums to infinity by the residues at the roots of the denominator.
 */
public class SumProductSoundnessTest extends ExprEvaluatorTestCase {

  @Test
  public void testDivergentZetaSeries() {
    // the analytic continuation of Zeta is no value of a divergent series
    check("Sum(1/k^(2/3), {k, 1, Infinity})", //
        "Sum(1/k^(2/3),{k,1,Infinity})");
    check("Sum(k^3, {k, 1, Infinity})", //
        "Sum(k^3,{k,1,Infinity})");
    check("Sum(k^(-5/2), {k, 1, Infinity})", //
        "Zeta(5/2)");
    check("Sum(1/k^s, {k, 1, Infinity})", //
        "Zeta(s)");
  }

  @Test
  public void testEmptyAndTwoSidedRanges() {
    check("Sum(k^2, {k, Infinity, 2})", //
        "0");
    check("Sum(1/k^3, {k, -Infinity, -1})", //
        "-Zeta(3)");
    check("Abs(N(Sum(1/(k + 1/3)^2, {k, -Infinity, Infinity}) - 4/3*Pi^2, 30)) < 10^-25", //
        "True");
  }

  @Test
  public void testPolesOnTheRange() {
    // the pole k == -2 is the first term
    check("Sum(1/(k*(k + 2)), {k, -2, n})", //
        "Sum(1/(k*(2+k)),{k,-2,n})");
    check("Sum(1/(k - 4)^2, {k, 1, Infinity})", //
        "ComplexInfinity");
    // the poles k == 3 and k == -3 lie before the range
    check("Sum(1/(k^2 - 9), {k, 4, Infinity})", //
        "49/120");
    check("Sum(1/(k^2 - 9), {k, 4, n}) /. n -> 6", //
        "733/3024");
  }

  @Test
  public void testProductsWithZerosOnTheRange() {
    // the zero k == 5 of the numerator: the product is not 0 before n == 5
    check("Table(Product((k - 5)/(k + 2), {k, 1, n}) /. n -> m, {m, 1, 6})", //
        "{-4/3,1,-2/5,1/15,0,0}");
    check("Table(Product(k/(k - 3), {k, 1, n}) /. n -> m, {m, 1, 2})", //
        "{-1/2,1}");
    // a base of the index is no constant of the product of the powers
    check("Product(k^(3*k), {k, 1, n})", //
        "Hyperfactorial(n)^3");
    check("Product((k + 2)^(k + 1)/(k + 1)^k, {k, 1, n})", //
        "(2+n)^(1+n)/2");
    // the factor k == 3 is 0/0 and k == 4 a pole: no closed form which steps over them
    check("Product((k - 4)^k/(k - 3)^(k + 1), {k, 1, n})", //
        "Product((-4+k)^k,{k,1,n})*Product((-3+k)^(-1-k),{k,1,n})");
    // an exponent with the index isn't a rational factor
    check("Product((k - 2)^k, {k, 1, n})", //
        "Product((-2+k)^k,{k,1,n})");
  }

  @Test
  public void testBoundsLinearInASymbol() {
    check("Sum(Binomial(2*n, k)^2, {k, 0, 2*n}) /. n -> 2", //
        "70");
    check("Table(Sum((-1)^k*k^2*Binomial(2*n, k), {k, 0, 2*n}) /. n -> m, {m, 0, 3})", //
        "{0,2,0,0}");
  }

  @Test
  public void testBinomialMoments() {
    // not 0 for small n: the closed form for a generic n fails there
    check("Table(Sum((-1)^k*k^3*Binomial(n, k), {k, 0, n}) /. n -> m, {m, 0, 4})", //
        "{0,-1,6,-6,0}");
    check("Sum(k^2*Binomial(n, k), {k, 0, n})", //
        "(n*(1+n))/2^(2-n)");
    check("Sum(k*3^k*Binomial(n, k), {k, 1, n})", //
        "(3*n)/4^(1-n)");
    // a series which stops at the upper bound
    check("Sum(Binomial(n, k)^4, {k, 0, n}) /. n -> 2", //
        "18");
  }

  @Test
  public void testTwoSidedAndRepeatedFactors() {
    check("Sum(1/(k + 1/4)^2, {k, -Infinity, Infinity})", //
        "2*Pi^2");
    check("Sum(1/(k^2 + 9), {k, -Infinity, Infinity})", //
        "1/3*Pi*Coth(3*Pi)");
    check("Sum(y^k, {k, -Infinity, -1})", //
        "1/(-1+y)");
    check("Sum(1/(k^2 + 4)^2, {k, 1, Infinity})", //
        "1/64*(-1+2*Pi*Coth(2*Pi)-4*PolyGamma(1,1-I*2)-4*PolyGamma(1,1+I*2))");
    check("Abs(N(Sum(1/(k^2 + 4)^2, {k, 1, Infinity}), 30) - "
        + "NSum(1/(k^2 + 4)^2, {k, 1, Infinity}, WorkingPrecision -> 40)) < 10^-25", //
        "True");
    // one fraction in the upper bound
    check("Sum(1/(k*(k + 3)), {k, 1, n})", //
        "(49*n+48*n^2+11*n^3)/(18*(1+n)*(2+n)*(3+n))");
    check("Sum(k^(-1/3), {k, 1, Infinity})", //
        "Sum(1/k^(1/3),{k,1,Infinity})");
  }

  @Test
  public void testParametricHolonomicSums() {
    // no recurrence with the parameter n inside, which a replacement doesn't reach
    check("Sum(k^3*Binomial(n, k), {k, 0, n}) /. n -> 4", //
        "224");
    check("Sum(Binomial(n, k)^3, {k, 0, n}) /. n -> 3", //
        "56");
  }

  @Test
  public void testRationalSumsToInfinity() {
    check("Sum(1/(k^2 + 4), {k, 1, Infinity})", //
        "1/8*(-1+2*Pi*Coth(2*Pi))");
    check("Abs(N(Sum(1/(k^4 + k^2 + 1), {k, 2, Infinity}), 30) - "
        + "0.06574030694801175711957672883537813312683`40) < 10^-25", //
        "True");
    // a RootSum over the roots of a quintic, at 30 digits
    check("Abs(N(Sum(1/(k^5 + 2), {k, 0, Infinity}), 30) - "
        + "0.8683871082766816419189883106332967778184`40) < 10^-25", //
        "True");
    check("Abs(N((Sum(1/(k^2 + 3), {k, 1, n}) /. n -> 2) - 11/28, 30)) < 10^-25", //
        "True");
  }
}
