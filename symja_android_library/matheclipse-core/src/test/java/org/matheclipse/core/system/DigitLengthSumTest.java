package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Numerical sums of terms which are only known at the integers or are constant on the blocks of
 * equal digit length, slowly converging smooth sums, N of unevaluated sums, the membership of exact
 * constants in the number domains and the derivative of Bessel functions with respect to the order.
 */
public class DigitLengthSumTest extends ExprEvaluatorTestCase {

  @Test
  public void testDigitLengthBlocks() {
    // the block of the terms with d digits sums to (PolyGamma(10^d) - PolyGamma(10^(d-1)))/d^3,
    // and to Log(10)/d^3 beyond the digits
    check("s = NSum(1/(n*IntegerLength(n)^3), {n, 1, Infinity}); "
        + "r = Total(Table(N((PolyGamma(0, 10^d) - PolyGamma(0, 10^(d-1)))/d^3, 30), {d, 1, 40})) "
        + "+ N(Log(10)*Zeta(3, 41), 30); Abs(s - r) < 10^-13", //
        "True");
    // the bit length, with 30 digits
    check("s = NSum(1/(n*BitLength(n)^2), {n, 1, Infinity}, WorkingPrecision -> 30); "
        + "r = Total(Table(N((PolyGamma(0, 2^b) - PolyGamma(0, 2^(b-1)))/b^2, 40), {b, 1, 120})) "
        + "+ N(Log(2)*Zeta(2, 121), 40); {Precision(s), Abs(s - r) < 10^-27}", //
        "{30,True}");
    // block sums of alternating signs; beyond b = 400 Log(2) Sum((-1)^b/b^3)
    check("s = NSum((-1)^BitLength(n)/(n*BitLength(n)^3), {n, 1, Infinity}); "
        + "r = Total(Table(N((-1)^b*(PolyGamma(0, 2^b) - PolyGamma(0, 2^(b-1)))/b^3, 30), "
        + "{b, 1, 400})) + N(Log(2)/8*(Zeta(3, 201) - Zeta(3, 401/2)), 30); "
        + "Abs(s - r) < 10^-13", //
        "True");
    // terms of a function defined for integers, which decrease geometrically from block to block
    check("h(n_Integer) := If(n <= 2, n, n^3*h(BitLength(n))); "
        + "s = NSum(1/h(n), {n, 1, Infinity}); "
        + "r = N(Sum(1/h(n), {n, 1, 2000}), 30); Abs(s - r) < 10^-9", //
        "True");
  }

  @Test
  public void testIntegerOnlyDecline() {
    // the block sums of a self-similar series jump again in the number of digits: no value rather
    // than a wrong one
    check("g(n_Integer) := If(n <= 2, n, n*g(BitLength(n))); NSum(1/g(n), {n, 1, Infinity})", //
        "NSum(1/g(n),{n,1,Infinity})");
    check("k(n_Integer) := If(n <= 9, n, n*IntegerLength(n)*k(IntegerLength(n))); "
        + "NSum(1/k(n), {n, 1, Infinity})", //
        "NSum(1/k(n),{n,1,Infinity})");
  }

  @Test
  public void testSmoothIntegerOnly() {
    // a function of Round(n) is a smooth series at the integers
    check("NSum(1/Round(n)^3, {n, 1, Infinity}) - Zeta(3) // Abs // # < 10^-13 &", //
        "True");
  }

  @Test
  public void testLogarithmicTail() {
    // the tail integral of 1/(x Log(x)^2) is 1/Log(N)
    check("s = NSum(1/(n*Log(n)^2), {n, 3, Infinity}, WorkingPrecision -> 40); "
        + "t = NSum(1/(n*Log(n)^2), {n, 3, Infinity}); {Precision(s), Abs(s - t) < 10^-13}", //
        "{40,True}");
    check("s = NSum(1/(n*Log(n)^2), {n, 3, Infinity}, WorkingPrecision -> 40); "
        + "Abs(s - (Sum(1/(n*Log(n)^2), {n, 3, 40}) + NSum(1/(n*Log(n)^2), {n, 41, Infinity}, "
        + "WorkingPrecision -> 40))) < 10^-35", //
        "True");
  }

  @Test
  public void testNOfUnevaluatedSum() {
    check("N(Sum(1/(k^4 + Sqrt(k)), {k, 1, Infinity})) - NSum(1/(k^4 + Sqrt(k)), {k, 1, Infinity})", //
        "0.0");
    check("Abs(N(Product(1 + 1/(k^3 + Sqrt(k)), {k, 1, Infinity})) "
        + "- NProduct(1 + 1/(k^3 + Sqrt(k)), {k, 1, Infinity})) < 10^-12", //
        "True");
    check("Precision(N(Sum(1/(k^4 + Sqrt(k)), {k, 1, Infinity}), 25))", //
        "25");
    // a numerical sum of an unknown summand
    check("N(Sum(f(k), {k, 1, Infinity}))", //
        "NSum(f(k),{k,1,Infinity})");
    // with the working precision, Exp of the numerical sum of the logarithms
    check("Abs(NProduct(1 + 1/k^2, {k, 1, Infinity}, WorkingPrecision -> 30) - Sinh(Pi)/Pi) "
        + "< 10^-28", //
        "True");
    check("Precision(NProduct(1 + 1/k^2, {k, 1, 30}, WorkingPrecision -> 30))", //
        "30");
  }

  @Test
  public void testNIntegrateLargeAbscissa() {
    // Log(1 + 1/x^k) cancels about k Log10(x) digits at a large abscissa
    check("{Abs(NIntegrate(Log(1 + 1/x^2), {x, 50, Infinity}, WorkingPrecision -> 35) "
        + "- N(Integrate(Log(1 + 1/x^2), {x, 50, Infinity}), 40)) < 10^-37, "
        + "Abs(NIntegrate(Log(1 + 1/x^3), {x, 2, Infinity}, WorkingPrecision -> 35) "
        + "- N(Integrate(Log(1 + 1/x^3), {x, 2, Infinity}), 40)) < 10^-33, "
        + "Precision(NIntegrate(Log(1 + 1/x^2), {x, 50, Infinity}, WorkingPrecision -> 35))}", //
        "{True,True,35}");
  }

  @Test
  public void testTailIntegralPrecision() {
    // a tail integral without the working precision isn't used: Log(Sinh(Pi)/Pi)
    check("s = NSum(Log(1 + 1/k^2), {k, 1, Infinity}, WorkingPrecision -> 30); "
        + "{Precision(s), Abs(s - Log(Sinh(Pi)/Pi)) < 10^-28}", //
        "{30,True}");
  }

  @Test
  public void testExponentialPartialSum() {
    check("Sum(y^k/k!, {k, 0, m})", //
        "(E^y*Gamma(1+m,y))/m!");
    check("D(Sum(y^k/k!, {k, 0, m}), y)", //
        "-y^m/m!+(E^y*Gamma(1+m,y))/m!");
    check("Sum(5*y^k/k!, {k, 3, m}) /. m -> 6 // FunctionExpand // Expand", //
        "5/6*y^3+5/24*y^4+y^5/24+y^6/144");
  }

  @Test
  public void testElementOfConstants() {
    check("{Element(Log(3), Rationals), Element(2 + 5*Pi^3, Algebraics), "
        + "Element(Exp(2/3), Algebraics), Element(Sqrt(Pi), Algebraics), Element(Log(7), Integers)}", //
        "{False,False,False,False,False}");
    // exact algebraic numbers by their minimal polynomial
    check("{Element((1 + Sqrt(3))^2 - 2*Sqrt(3), Integers), "
        + "Element((1 + Sqrt(3))^2 - 2*Sqrt(3), Rationals), Element(Sqrt(3) + Sqrt(5), Rationals), "
        + "Element((Sqrt(2) + 1)^3, Integers)}", //
        "{True,True,False,False}");
    // unknown: the sum of two transcendental numbers, Euler's constant
    check("Element(Pi + Log(2), Rationals)", //
        "Pi+Log(2)∈Rationals");
    check("Element(EulerGamma, Rationals)", //
        "EulerGamma∈Rationals");
    check("Which(Element(Log(3), Rationals), 1, True, 2)", //
        "2");
  }

  @Test
  public void testBesselOrderDerivative() {
    // central differences of the order at 50 digits
    check("fd(f_, nu_, z_) := Module({h = 10^-20}, (f(N(nu + h, 50), N(z, 50)) "
        + "- f(N(nu - h, 50), N(z, 50)))/(2*h)); "
        + "{Abs(Derivative(1, 0)[BesselJ][2/5, 3.0] - fd(BesselJ, 2/5, 3)) < 10^-13, "
        + "Abs(Derivative(1, 0)[BesselI][2/5, 3.0] - fd(BesselI, 2/5, 3)) < 10^-13, "
        + "Abs(Derivative(1, 0)[BesselY][2/5, 3.0] - fd(BesselY, 2/5, 3)) < 10^-13, "
        + "Abs(Derivative(1, 0)[BesselK][2/5, 3.0] - fd(BesselK, 2/5, 3)) < 10^-13, "
        + "Abs(Derivative(1, 0)[BesselJ][0.25, 2.0 + 1.5*I] - fd(BesselJ, 1/4, 2 + 3/2*I)) < 10^-13, "
        + "Abs(Derivative(1, 0)[BesselJ][N(3/2, 30), N(9, 30)] - fd(BesselJ, 3/2, 9)) < 10^-27}", //
        "{True,True,True,True,True,True}");
    // at the order 0 the derivative of BesselJ is Pi/2 BesselY(0, z)
    check("Abs(Derivative(1, 0)[BesselJ][0, 2.5] - Pi/2*BesselY(0, 2.5)) < 10^-14", //
        "True");
    // integer orders of BesselY and BesselK by the finite sums of the lower orders
    check("fd(f_, nu_, z_) := Module({h = 10^-20}, (f(N(nu + h, 50), N(z, 50)) "
        + "- f(N(nu - h, 50), N(z, 50)))/(2*h)); "
        + "{Abs(Derivative(1, 0)[BesselY][1, 4.5] - fd(BesselY, 1, 9/2)) < 10^-13, "
        + "Abs(Derivative(1, 0)[BesselY][3, 2.5] - fd(BesselY, 3, 5/2)) < 10^-13, "
        + "Abs(Derivative(1, 0)[BesselK][2, 1.5] - fd(BesselK, 2, 3/2)) < 10^-13, "
        + "Derivative(1, 0)[BesselK][0, 1.5]}", //
        "{True,True,True,0.0}");
  }

  @Test
  public void testGroebnerBasisMethod() {
    check("GroebnerBasis({x^2 - y, x*y - 1}, {x, y}, Method -> \"Buchberger\") === "
        + "GroebnerBasis({x^2 - y, x*y - 1}, {x, y})", //
        "True");
    check("GroebnerBasis({x^2 - y, x*y - 1}, {x, y}, Method -> \"Other\")", //
        "GroebnerBasis({x^2-y,-1+x*y},{x,y},Method->Other)");
  }

  @Test
  public void testReviewFixes() {
    // next to an integer order the connection formula of BesselY cancels digits
    check("fd(f_, nu_, z_) := Module({h = 10^-40}, (f(N(nu + h, 120), N(z, 120)) "
        + "- f(N(nu - h, 120), N(z, 120)))/(2*h)); "
        + "{Abs(Derivative(1, 0)[BesselY][1 + 10^-10, 3.0] - fd(BesselY, 1 + 10^-10, 3)) < 10^-13, "
        + "Abs(Derivative(1, 0)[BesselK][2 - 10^-12, 1.5] - fd(BesselK, 2 - 10^-12, 3/2)) < 10^-13}", //
        "{True,True}");
    // a large machine value away from the integers
    check("Element(10^12*Gamma(1/3), Integers)", //
        "False");
    // products with a working precision: a stepped iterator, many factors
    check("Abs(NProduct(1 + 1/k^2, {k, 1, 9, 2}, WorkingPrecision -> 20) "
        + "- Product(1 + 1/k^2, {k, 1, 9, 2})) < 10^-18", //
        "True");
    check("Precision(NProduct(1 + 1/k^3, {k, 1, 150000}, WorkingPrecision -> 20))", //
        "20");
    // the precision of N(expr, {precision, accuracy})
    check("Precision(N(Sum(1/(k^4 + Sqrt(k)), {k, 1, Infinity}), {30, 30})) > 25", //
        "True");
  }

  @Test
  public void testLargeHarmonicNumber() {
    // the exact harmonic number of a large index isn't summed term by term, its value is numeric
    check("HarmonicNumber(3*10^6)", //
        "HarmonicNumber(3000000)");
    // binary splitting: an index of some thousands is exact
    check("HarmonicNumber(30000) - HarmonicNumber(29999) == 1/30000", //
        "True");
    check("Abs(N(HarmonicNumber(3*10^6)) - (Log(3*10^6) + EulerGamma + 1/(6*10^6))) < 10^-12", //
        "True");
    // PolyGamma(0, x) = Log(x) - 1/(2 x) - ...
    check("Abs(N(PolyGamma(0, 2^40) - PolyGamma(0, 2^39), 30) - (Log(2) + 1/2^41)) < 10^-24", //
        "True");
  }
}
