package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Sums of a rational summand over a long range in closed form, sums of a polynomial times a power
 * to infinity, numerical sums and products which converge slowly, and sums of many fractions.
 */
public class LongRangeSumTest extends ExprEvaluatorTestCase {

  @Test
  public void testLongRationalRange() {
    check("Sum(1/(k + 2/5), {k, 7, 2000000})", //
        "-PolyGamma(0,37/5)+PolyGamma(0,10000007/5)");
    check("Sum(1/(3*k + 1), {k, 0, 5*10^6})", //
        "1/3*(-PolyGamma(0,1/3)+PolyGamma(0,15000004/3))");
    // telescoping
    check("Sum(1/(k*(k + 2)), {k, 1, 10^6})", //
        "375000625000/500001500001");
    check("Sum(1/(k + b), {k, 1, 3*10^6})", //
        "-PolyGamma(0,1+b)+PolyGamma(0,3000001+b)");
    // a step
    check("Sum(1/(k + 2/5), {k, 7, 2000000, 3})", //
        "1/3*(-PolyGamma(0,37/15)+PolyGamma(0,10000012/15))");
    // a pole above the range
    check("Sum(1/(k - 40001), {k, 1, 30000}) + HarmonicNumber(40000) - HarmonicNumber(10000)", //
        "0");
    check("Sum(1/(k - 30001)^2, {k, 1, 20000})", //
        "PolyGamma(1,10001)-PolyGamma(1,30001)");
    check(
        "Abs(N(Sum(1/((k - 18001)*(k^2 + 2)), {k, 1, 15000}), 30)"
            + " - Total(Table(N(1/((k - 18001)*(k^2 + 2)), 40), {k, 1, 15000}))) < 10^-25", //
        "True");
  }

  @Test
  public void testLongRangeIrreducibleDenominator() {
    check("FreeQ(Sum(1/(k^3 + 2*k + 3), {k, 1, 10^6}), Sum)", //
        "True");
    // the finite sum and the tail make the infinite sum
    check(
        "Abs(N(Sum(1/(k^3 + 2*k + 3), {k, 1, 30000}) - Sum(1/(k^3 + 2*k + 3), {k, 1, Infinity})"
            + " + Sum(1/(k^3 + 2*k + 3), {k, 30001, Infinity}), 30)) < 10^-25", //
        "True");
    check(
        "Abs(N(Sum(1/((k^2 + 3)*(k + 1/3)^2), {k, 1, 20000})"
            + " - Sum(1/((j^2 + 3)*(j + 1/3)^2), {j, 1, 20000}), 30)) < 10^-25", //
        "True");
    check(
        "Abs(N(Sum(1/(2*k + 1), {k, 0, 12000}), 30) - Total(Table(N(1/(2*k + 1), 40), {k, 0, 12000})))"
            + " < 10^-25", //
        "True");
  }

  @Test
  public void testPolynomialTimesPower() {
    check("Sum(k*(997/1000)^k, {k, 1, Infinity})", //
        "997000/9");
    check("Sum((k^2 + 2)*(1/4)^k, {k, 0, Infinity})", //
        "92/27");
    check("Sum(k/3^(2*k + 1), {k, 1, Infinity})", //
        "3/64");
    check("Sum(k*(-1/3)^k, {k, 2, Infinity})", //
        "7/48");
    // divergent
    check("Sum(k*(5/2)^k, {k, 1, Infinity})", //
        "Sum((5/2)^k*k,{k,1,Infinity})");
    // the closed form keeps the digits of its machine value
    check("Abs(N(Sum(k*E^(-k/10^4), {k, 1, Infinity})) - 99999999.999999) < 10^-4*10^8", //
        "True");
  }

  @Test
  public void testNSumSlowGeometric() {
    check("Abs(NSum(k*0.997^k, {k, 1, Infinity}) - 997000/9) < 10^-6", //
        "True");
    check("Abs(NSum(E^(-k/10^4), {k, 1, Infinity}) - 1/(E^(1/10^4) - 1)) < 10^-8", //
        "True");
    check("Abs(NSum(k^2*0.5^k, {k, 1, Infinity}) - 6) < 10^-12", //
        "True");
    // the terms grow: no finite value
    check("NSum(3^k, {k, 2, Infinity})", //
        "ComplexInfinity");
    check("NSum(k^2*(-5/4)^k, {k, 1, Infinity})", //
        "ComplexInfinity");
    check("NSum(7, {k, 1, Infinity})", //
        "ComplexInfinity");
    // the terms tend to E
    check("NSum((1 + 2/k)^(k/2), {k, 1, Infinity})", //
        "ComplexInfinity");
    // terms which tend to 0 and alternate
    check("Abs(NSum((-1)^k/(2*k + 1), {k, 0, Infinity}) - Pi/4) < 10^-10", //
        "True");
  }

  @Test
  public void testHarmonicNumberSymbolicOrder() {
    check("HarmonicNumber(3, s)", //
        "1+2^(-s)+3^(-s)");
    check("HarmonicNumber(6, s)", //
        "HarmonicNumber(6,s)");
    check("HarmonicNumber(6, 3)", //
        "28567/24000");
  }

  @Test
  public void testNProduct() {
    // the factors are 1 + 10^-13 far out
    check("Abs(NProduct(1 + 1/k^2, {k, 2*10^6 + 1, Infinity}) - 1 - 1/(2*10^6)) < 10^-12", //
        "True");
    // a value of the index doesn't leak into the product
    check("Block({k = 3}, Abs(NProduct(1 + 1/k^2, {k, 1, Infinity}) - Sinh(Pi)/Pi) < 10^-12)", //
        "True");
    check(
        "Abs(N(Product(1 + 3/k^3, {k, 1, Infinity})) - NProduct(1 + 3/k^3, {k, 1, Infinity}))"
            + " < 10^-12", //
        "True");
  }

  @Test
  public void testManyFractions() {
    // exact up to the iteration limit of the tests' engine
    check("HarmonicNumber(300) - HarmonicNumber(299)", //
        "1/300");
    check(
        "Total(Table(1/(3*k + 2), {k, 0, 3000})) == Fold(Plus, 0, Table(1/(3*k + 2), {k, 0, 3000}))", //
        "True");
    check("Plus @@ Join(Table(1/5, {200}), Table(3^50/11, {40}), Table(-3^50/11, {40}))", //
        "40");
    check("Head(Plus @@ Join(Table(1/7, {139}), {1/7}))", //
        "Integer");
  }

  @Test
  public void testSimplifyThreeFactors() {
    // a pair of factors merged into one: the third factor stays once
    check("Simplify(1/(Gamma(1 + Sqrt(3))*Gamma(1 + 2*I)*Gamma(1 - 2*I)))", //
        "(Cosh(Pi)*Sinh(Pi))/(Pi*Gamma(1+Sqrt(3)))");
    check("Apart(1/(Log(5)*Log(3 + I)*Log(3 - I)))", //
        "1/(Log(3-I)*Log(3+I)*Log(5))");
    check("Simplify(1/(Gamma(2 + 5^(1/3))*Gamma(3 + I)*Gamma(3 - I)))", //
        "Sinh(Pi)/(10*Pi*Gamma(2+5^(1/3)))");
    // a product over the roots of a cubic
    check(
        "Abs(N(Product(1 + 3/k^3, {k, 1, Infinity})) - Product(N(1 + 3/k^3, 30), {k, 1, 4000}))"
            + " < 10^-6", //
        "True");
  }
}
