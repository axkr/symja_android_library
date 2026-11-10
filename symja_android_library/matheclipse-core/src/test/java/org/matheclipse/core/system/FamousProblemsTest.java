package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Sums, products, limits, equations and constants of classical problems: options of the sums,
 * exact arithmetic with arbitrary precision complex numbers, minimal polynomials and recurrences.
 */
public class FamousProblemsTest extends ExprEvaluatorTestCase {

  @Test
  public void testComplexPrecisionTimesInteger() {
    check("w = N(3 + I*Sqrt(7), 50); {Precision(w), Precision(-w), Precision(3*w), "
        + "Precision(w - w + w)}", //
        "{50,50,50,50}");
    // TimeConstrained evaluates in a copy of the engine
    check("w = N(3 + I*Sqrt(7), 50); TimeConstrained({Precision(-w), Precision(3*w)}, 10)", //
        "{50,50}");
    // an exact number next to a complex number of a precision of its own takes that precision
    check("v = N(2 - I*Sqrt(11), 45); {Precision(v), Precision(Times(-1, v)), "
        + "Precision(Times(5, v)), Precision({1, v} . {2, 3})}", //
        "{45,45,45,45}");
    // arbitrary precision parts of Complex keep their digits
    check("Precision(Complex(N(4, 40), N(Sqrt(3), 40)))", //
        "40");
  }

  @Test
  public void testPrecisionOfAnInputNotOfEarlierValues() {
    // the input's precision isn't the one of the value a variable had in an earlier input
    check("w = N(3 + I*Sqrt(7), 50); Precision(w)", //
        "50");
    check("w = N(2 - I*Sqrt(11), 45); {Precision(w), Precision(Times(-1, w)), Precision(7*w)}", //
        "{45,45,45}");
    check("u = N(Pi, 60);Precision(u)", //
        "60");
    check("u = N(E, 25); {Precision(u), Precision(u + 1/3), Precision(2*u)}", //
        "{25,25,25}");
  }

  @Test
  public void testSumProductAssumptionsOption() {
    check("Sum(1/k^4, {k, 1, Infinity}, Assumptions -> Element(k, Integers))", //
        "Pi^4/90");
    check("Sum(x^k, {k, 0, Infinity}, Assumptions -> 0 < x < 1)", //
        "1/(1-x)");
    check("Product(1 + 1/k, {k, 1, n}, Assumptions -> Element(n, Integers) && n > 0) // "
        + "FunctionExpand", //
        "1+n");
    // unevaluated, the option stays
    check("Sum(f(k), {k, 1, Infinity}, Assumptions -> k > 0)", //
        "Sum(f(k),{k,1,Infinity},Assumptions->k>0)");
  }

  @Test
  public void testAlternatingRationalSums() {
    // Sum((-1)^k/(3k+1)) = (Pi/Sqrt(3) + Log(2))/3
    check("Abs(N(Sum((-1)^k/(3*k + 1), {k, 0, Infinity}) - (Pi/Sqrt(3) + Log(2))/3, 30)) < 10^-25", //
        "True");
    check("Sum((-1)^(k + 1)/(2*k), {k, 1, Infinity}) - Log(2)/2 // N // Chop", //
        "0");
    // second order: Catalan's constant over 4 from the odd squares
    check("Abs(N(Sum((-1)^k/(4*k + 2)^2, {k, 0, Infinity}) - Catalan/4, 30)) < 10^-25", //
        "True");
    // the terms don't tend to 0
    check("Sum((-1)^k*k/(k + 1), {k, 0, Infinity})", //
        "Sum(((-1)^k*k)/(1+k),{k,0,Infinity})");
  }

  @Test
  public void testInfiniteRationalProducts() {
    // the limit of the partial products, not Gamma(Infinity)^2/Gamma(Infinity)^2
    check("Abs(N(Product(9*k^2/(9*k^2 - 1), {k, 1, Infinity}) - 2*Pi/(3*Sqrt(3)), 30)) < 10^-25", //
        "True");
    check("Product(1 - 4/k^2, {k, 3, Infinity})", //
        "1/6");
    // Gamma functions of the complex roots, paired by the reflection formula
    check("Abs(N(Product((k^2 + 4)/(k^2 + 3), {k, 1, Infinity}) - "
        + "Sqrt(3)*Sinh(2*Pi)/(2*Sinh(Sqrt(3)*Pi)), 30)) < 10^-25", //
        "True");
    check("FreeQ(Product((k^2 + 4)/(k^2 + 3), {k, 1, Infinity}), Gamma)", //
        "True");
    // linear factors of a degree above 4
    check("Abs(N(Product((k + 1)*(k + 2)*(k + 3)*(k + 4)*(k + 5)/((k + 1/2)*(k + 3/2)*(k + 5/2)*"
        + "(k + 7/2)*(k + 7)), {k, 1, Infinity}) - 11025*Pi^2/16384, 30)) < 10^-25", //
        "True");
    // a factor vanishes in the range
    check("Product((k^2 - 9)/(k^2 + 1), {k, 1, Infinity})", //
        "0");
    // the sums of the roots differ: the partial products tend to 0
    check("Product((k + 1)/(k + 3), {k, 1, Infinity})", //
        "0");
  }

  @Test
  public void testMinimalPolynomialTrigonometric() {
    check("MinimalPolynomial(Cos(2*Pi/9), x)", //
        "1-6*x+8*x^3");
    check("MinimalPolynomial(Sin(Pi/10), x)", //
        "-1+2*x+4*x^2");
    check("MinimalPolynomial(Tan(Pi/8), x)", //
        "-1+2*x+x^2");
    check("MinimalPolynomial(Cos(2*Pi/11), x)", //
        "1+6*x-12*x^2-32*x^3+16*x^4+32*x^5");
    // the factor of ChebyshevT(q, x) - 1 with the cosine as a root
    check("Exponent(MinimalPolynomial(Cos(2*Pi/19), x), x)", //
        "9");
  }

  @Test
  public void testMinimalPolynomialTranscendental() {
    // a transcendental number has no minimal polynomial
    check("MinimalPolynomial(Gamma(1/3), x)", //
        "MinimalPolynomial(Gamma(1/3),x)");
    check("MinimalPolynomial(Pi + E, x)", //
        "MinimalPolynomial(E+Pi,x)");
    check("MinimalPolynomial(Sqrt(3) + Pi, x)", //
        "MinimalPolynomial(Sqrt(3)+Pi,x)");
    check("MinimalPolynomial(Log(3), x)", //
        "MinimalPolynomial(Log(3),x)");
    // a denominator outside the range of the trigonometric relations
    check("MinimalPolynomial(Cos(Pi/10000000000000), x)", //
        "MinimalPolynomial(Cos(Pi/10000000000000),x)");
  }

  @Test
  public void testRSolveVariableCoefficients() {
    // a list of one function is the function; n-dependent coefficients aren't a Z transform system
    check("RSolve({y(n + 1)*(n + 3) == y(n)*(2*n + 1), y(0) == 1}, {y(n)}, n) === "
        + "RSolve({y(n + 1)*(n + 3) == y(n)*(2*n + 1), y(0) == 1}, y(n), n)", //
        "True");
    // the central binomial coefficients over n+1
    check("y(n) /. First(RSolve({y(n + 1)*(n + 2) == y(n)*(4*n + 2), y(0) == 1}, {y(n)}, n)) "
        + "/. n -> 7 // FunctionExpand", //
        "429");
    check("y(n) /. First(RSolve({y(n + 1) == (n + 1)*y(n)/(n + 4), y(0) == 6}, {y(n)}, n)) "
        + "/. n -> 3 // FunctionExpand", //
        "3/10");
  }
}
