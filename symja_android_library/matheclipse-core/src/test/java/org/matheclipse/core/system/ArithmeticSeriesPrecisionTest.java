package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Dirichlet series of arithmetic functions in <code>Sum</code> and <code>NSum</code>, numeric
 * derivatives of <code>Zeta</code>, constrained extrema at a working precision and the accuracy of
 * the parabolic method of lines.
 */
public class ArithmeticSeriesPrecisionTest extends ExprEvaluatorTestCase {

  @Test
  public void testDirichletSeriesClosedForms() {
    check("Sum(MangoldtLambda(k)/k^s, {k, 1, Infinity})", //
        "-Zeta'(s)/Zeta(s)");
    check("Sum(PrimeNu(k)/k^s, {k, 1, Infinity})", //
        "PrimeZetaP(s)*Zeta(s)");
    // -Zeta'(3)/Zeta(3), the logarithmic derivative of the Euler product
    check("NSum(MangoldtLambda(k)/k^3, {k, 1, Infinity})", //
        "0.164823");
    check("NSum(PrimeNu(k)/k^4, {k, 1, Infinity}) - N(Zeta(4)*PrimeZetaP(4))", //
        "0.0");
  }

  @Test
  public void testArithmeticSummandNotExtrapolated() {
    // the arithmetic function only shows after evaluation of the summand
    check("h(k_) := EulerPhi(k); NSum(h(k)/k^4, {k, 1, Infinity}) - N(Zeta(3)/Zeta(4))", //
        "0.0");
    // a geometrically decreasing majorant: the terms are added one by one
    check("NSum(DivisorSigma(1, k)/3^k, {k, 1, Infinity}) "
        + "- N(Sum(DivisorSigma(1, k)/3^k, {k, 1, 120}))", //
        "0.0");
    check("Abs(NSum(PrimeOmega(k)/5^k, {k, 1, Infinity}, WorkingPrecision -> 25) "
        + "- Sum(PrimeOmega(k)/5^k, {k, 1, 60})) < 10^-23", //
        "True");
    // terms which cancel to 0: the sum ends when the majorant is negligible against its own sum
    check("NSum(Mod(k, 1)/2^k, {k, 1, Infinity})", //
        "0.0");
    check("Abs(NSum(Abs(MoebiusMu(k))/2^k, {k, 1, Infinity}) "
        + "- N(Sum(MoebiusMu(k)^2/2^k, {k, 1, 80}))) < 10^-15", //
        "True");
    // the primes themselves: p(k) < k^2 + 2 is the majorant
    check("Abs(NSum(Prime(k)/3^k, {k, 1, Infinity}) - N(Sum(Prime(k)/3^k, {k, 1, 80}))) "
        + "< 10^-15", //
        "True");
    // no closed form and no majorant which decreases fast: no number rather than a wrong one
    check("NSum(PrimeOmega(k)*(-1)^k/k^3, {k, 1, Infinity})", //
        "NSum(PrimeOmega(k)*(-1)^k/k^3,{k,1,Infinity})");
  }

  @Test
  public void testZetaDerivativesOfHigherOrder() {
    check("Zeta''(4.0)", //
        "0.0650582");
    check("Derivative(3)[Zeta][4.0]", //
        "-0.0726408");
    check("N(Derivative(2)[Zeta][4], 25)", //
        "0.06505816136788066186035476");
    // the reflection side and a complex argument
    check("Derivative(2)[Zeta][-2.5]", //
        "-0.0337977");
    check("Derivative(2)[Zeta][3.0+2.0*I]", //
        "-0.0740864+I*(-0.0597453)");
    // the pole
    check("Derivative(2)[Zeta][1.0]", //
        "ComplexInfinity");
    // next to the pole, and of a high order
    check("Derivative(4)[Zeta][1.01]", //
        "2.4*10^11");
    check("Derivative(48)[Zeta][3.0]", //
        "2.20515*10^46");
    // an exact argument stays
    check("Derivative(2)[Zeta][4]", //
        "Zeta''(4)");
  }

  @Test
  public void testConstrainedExtremumAtWorkingPrecision() {
    // an active disc constraint: Newton on the Lagrange conditions at 30 digits
    check("FindMinimum({3*x - y, x^2 + y^2 <= 4}, {{x, 0}, {y, 0}}, WorkingPrecision -> 30)", //
        "{-6.32455532033675866399778708887,{x->-1.89736659610102759919933612666,y->0."
            + "632455532033675866399778708887}}");
    // 2*Sqrt(2) at {3/Sqrt(2), 1/Sqrt(2)}
    check("NMaximize({x + y, x^2 + 3*y^2 == 6}, {x, y}, WorkingPrecision -> 30)", //
        "{2.82842712474619009760337744842,{x->2.12132034355964257320253308631,y->0."
            + "707106781186547524400844362105}}");
  }

  @Test
  public void testConstrainedExtremumDeclined() {
    // a strict inequality has no extremum on its boundary: the machine point stays
    check("MachineNumberQ(FindMinimum({x + y, x > 1 && y > 1}, {{x, 2}, {y, 2}}, "
        + "WorkingPrecision -> 30)[[1]])", //
        "True");
    // integer variables have no Lagrange conditions
    check("NMinimize({x + 2*y, x^2 + y^2 <= 10, Element(x, Integers)}, {x, y}, "
        + "WorkingPrecision -> 20)[[2, 1, 2]] // IntegerQ", //
        "True");
    // a constraint of a large scale is active too
    check("Abs(FindMinimum({x + 2*y, 10^6*(x^2 + y^2) <= 10^6}, {{x, 0}, {y, 0}}, "
        + "WorkingPrecision -> 30)[[1]] + Sqrt(5)) < 10^-28", //
        "True");
  }

  @Test
  public void testParabolicRichardson() {
    // smooth data: the extrapolation of two Crank-Nicolson solutions
    check("s = NDSolveValue({D(w(t, x), t) == D(w(t, x), x, x), w(0, x) == Sin(2*Pi*x), "
        + "w(t, 0) == 0, w(t, 1) == 0}, w, {t, 0, 1/50}, {x, 0, 1}); "
        + "Abs(s(1/50, 1/4) - Exp(-4*Pi^2/50)) < 10^-9", //
        "True");
  }
}
