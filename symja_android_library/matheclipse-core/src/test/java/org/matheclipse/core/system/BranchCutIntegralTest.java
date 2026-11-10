package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/**
 * Definite integrals whose antiderivatives have <code>ExpIntegralEi</code> or <code>Log</code> of
 * complex arguments: jumps on the branch cut inside the interval, the limits off the cut at
 * infinity, and the Laplace transform of <code>1/(a^2+t^2)</code>.
 */
public class BranchCutIntegralTest extends ExprEvaluatorTestCase {

  @Test
  public void testFourierIntegralsOfRationalFunctions() {
    // the antiderivative jumps where -2+3*I*x crosses the negative real axis
    check("Integrate(E^(3*I*x)/(4 + x^2), {x, -Infinity, Infinity})", //
        "Pi/(2*E^6)");
    check("Integrate(E^(-2*I*x)/(9 + x^2), {x, -Infinity, Infinity})", //
        "Pi/(3*E^6)");
    check("Integrate(x*E^(2*I*x)/(1 + x^2), {x, -Infinity, Infinity})", //
        "(I*Pi)/E^2");
    // a bound on the cut
    check("Abs(N(Integrate(E^(I*x)/(4 + x^2), {x, -Infinity, 0}) + "
        + "Integrate(E^(I*x)/(4 + x^2), {x, 0, Infinity}) - Pi/(2*E^2), 20)) < 10^-15", //
        "True");
  }

  @Test
  public void testExpIntegralEiOffTheCutAtInfinity() {
    // Ei(u) tends to I*Pi*Sign(Im(u)) when Re(u) tends to -Infinity
    check("Limit(ExpIntegralEi(-x - 2*I), x -> Infinity)", //
        "-I*Pi");
    check("Limit(ExpIntegralEi(3*x + I), x -> -Infinity)", //
        "I*Pi");
    check("Limit(ExpIntegralEi(-x), x -> Infinity)", //
        "0");
  }

  @Test
  public void testLaplaceTransformOfLorentzian() {
    check("Integrate(E^(-3*x)/(1 + x^2), {x, 0, Infinity})", //
        "CosIntegral(3)*Sin(3)+Cos(3)*(Pi/2-SinIntegral(3))");
    // positive: 0.0973192368973596
    check("Abs(N(LaplaceTransform(1/(9 + t^2), t, s) /. s -> 1) - 0.0973192368973596) < 10^-12", //
        "True");
  }

  @Test
  public void testExponentialTimesShiftedHyperbolic() {
    // the constant of the argument of Sinh(c+b*x) stays
    check("D(Integrate(E^(3*x)*Sinh(2 + 7*x), x), x) - E^(3*x)*Sinh(2 + 7*x) // TrigToExp // Simplify", //
        "0");
    check("D(Integrate(E^x*Cosh(4 + x), x), x) - E^x*Cosh(4 + x) // TrigToExp // Simplify", //
        "0");
    check("Integrate(Cosh(2 + x/3)*E^(-x), {x, 0, Infinity})", //
        "3/(8*E^2)+3/4*E^2");
  }

  @Test
  public void testPeriodicAbsOnInfiniteInterval() {
    // the zeros of Sin(2*x/3) on (0, Infinity) aren't listed: no value from Sin(2*x/3) alone
    check("Integrate(Abs(Sin(2*x/3))*E^(-5*x/2), {x, 0, Infinity})", //
        "Integrate(Abs(Sin(2*x/3))/E^(5*x/2),{x,0,Infinity})");
  }

  @Test
  public void testBesselPowersAtZero() {
    // x^a*BesselY(a,c*x) and x^(-a)*BesselJ(a,c*x) don't vanish at 0
    check("Limit(x^2*BesselY(2,3*x), x -> 0, Direction -> -1)", //
        "-4/(9*Pi)");
    check("Limit(x^(1/4)*BesselI(-1/4,x), x -> 0, Direction -> -1)", //
        "2^(1/4)/Gamma(3/4)");
    check("Integrate(x^(7/3)*BesselY(4/3, 2*x), {x, 0, 1})", //
        "BesselY(7/3,2)/2+Gamma(7/3)/(2*Pi)");
    check("Integrate(x^(-1/3)*BesselJ(4/3, 2*x), {x, 0, 1})", //
        "-BesselJ(1/3,2)/2+1/(2*Gamma(4/3))");
    check("Abs(N(Integrate(x^(7/3)*BesselY(4/3, 2*x), {x, 0, 1})) - "
        + "NIntegrate(x^(7/3)*BesselY(4/3, 2*x), {x, 0, 1})) < 10^-10", //
        "True");
  }

  @Test
  public void testRegularizedHypergeometricEqualParameters() {
    // 1/Gamma(c) of the lower parameter c == b
    check("Hypergeometric2F1Regularized(1/3, 0, 0, z)", //
        "0");
    check("Hypergeometric2F1Regularized(1/3, 2, 2, z)", //
        "1/(1-z)^(1/3)");
    check("Hypergeometric2F1Regularized(2, 1/3, 2, w)", //
        "1/(1-w)^(1/3)");
    check("Abs(N(Integrate(BesselY(0, 2*x)/E^(3*x), {x, 0, Infinity})) + "
        + "N(2*ArcSinh(3/2)/(Sqrt(13)*Pi))) < 10^-12", //
        "True");
  }

  @Test
  public void testEllipticOverX() {
    // the power rules have a pole at the exponent -1: the logarithm and a 4F3 instead
    check("Integrate(EllipticK(3*x)/x, x)", //
        "3/8*Pi*x*HypergeometricPFQ({1,1,3/2,3/2},{2,2,2},3*x)+1/2*Pi*Log(x)");
    check("Integrate(EllipticE(x)/x, x)", //
        "-1/8*Pi*x*HypergeometricPFQ({1/2,1,1,3/2},{2,2,2},x)+1/2*Pi*Log(x)");
    check("a = Integrate(EllipticE(2*x^3)/x, x); "
        + "Abs(N(D(a, x) - EllipticE(2*x^3)/x /. x -> 1/2)) < 10^-12", //
        "True");
    check("a = Integrate(EllipticK(x^2)/x, x); "
        + "Abs(N(D(a, x) - EllipticK(x^2)/x /. x -> 2/5)) < 10^-12", //
        "True");
  }

  @Test
  public void testOneSidedLimitsOnTheCut() {
    // Log(-1+I*x) tends to -I*Pi from below and to I*Pi from above
    check("Limit(Log(-1 + I*x), x -> 0, Direction -> \"FromBelow\")", //
        "-I*Pi");
    check("Limit(Log(-1 + I*x), x -> 0, Direction -> \"FromAbove\")", //
        "I*Pi");
    check("Limit(ExpIntegralEi(-2 - I*x), x -> 0, Direction -> \"FromAbove\")", //
        "-I*Pi-ExpIntegralE(1,2)");
  }

  @Test
  public void testBranchesOfLogarithmsAndCosIntegral() {
    // merged logarithms keep their multiple of 2*Pi*I
    check("Limit(-Log(1 - x) + Log(x - 1) + Sin(x), x -> 1, Direction -> -1)", //
        "-I*Pi+Sin(1)");
    check("Limit(CosIntegral(-x - I), x -> Infinity)", //
        "-I*Pi");
    check("Limit(CosIntegral(-x + I), x -> Infinity)", //
        "I*Pi");
    check("N(Integrate(x*Sin(2*x)/(1 + x^4 - x^2), {x, 0, Infinity}))", //
        "0.658603");
    // a removable singularity at 1
    check("Integrate((Cos(x/3) - Cos(1/(3*x)))/(1 - x^2), {x, 0, Infinity})", //
        "Pi*Sin(1/3)");
  }

  @Test
  public void testOppositeRadicandsAtABranchPoint() {
    // Sqrt(1-x^2)/Sqrt(-1+x^2) is I for x > 1
    check("Limit(Sqrt(-1 + x^2)*EllipticE(ArcSin(x), 1/2)/Sqrt(1 - x^2), x -> 1, Direction -> -1)", //
        "(-I*1/4*(Pi^2+2*Gamma(3/4)^4))/(Sqrt(Pi)*Gamma(3/4)^2)");
    check("Abs(N(Integrate(x^2/Sqrt((1 - x^2)*(-1/9 + x^2)), {x, 1/3, 1/2})) - "
        + "NIntegrate(x^2/Sqrt((1 - x^2)*(-1/9 + x^2)), {x, 1/3, 1/2})) < 10^-10", //
        "True");
  }

  @Test
  public void testAppellF1NextToOne() {
    // the singular point 1/x of the Euler integral next to 1
    check("Abs(N(AppellF1(1/2, 1/2, 3/2, 3/2, 1003/1000, -1003/1000), 30) - "
        + "(0.953620088340793290283699000523-I*0.0193118069676032246750038582725)) < 10^-25", //
        "True");
  }

  @Test
  public void testNoRuleInternalsInAnswers() {
    // a substitution into the marker of an integral without a rule isn't an antiderivative
    check("Integrate(Sqrt(x)*Tanh(x), x)", //
        "Integrate(Sqrt(x)*Tanh(x),x)");
    check("Integrate(x^(1/3)*Coth(x), x)", //
        "Integrate(x^(1/3)*Coth(x),x)");
    // the function of the RootSum is a function of the root only
    check("StringContainsQ(ToString(Integrate(Sec(11*x)*Sin(x)^11, x)), \"Rubi\")", //
        "False");
    check("a = Integrate(Sec(11*x)*Sin(x)^11, x); "
        + "Abs(N(((a /. x -> 37/100 + 10^-6) - (a /. x -> 37/100 - 10^-6))/(2*10^-6), 20) - "
        + "N(Sec(11*37/100)*Sin(37/100)^11, 20)) < 10^-8", //
        "True");
  }

  @Test
  public void testHypergeometric2F1AtInfinity() {
    check("Limit(Hypergeometric2F1(-1/3, 1/2, 2/3, -x^3)/x, x -> Infinity)", //
        "(Gamma(2/3)*Gamma(5/6))/Sqrt(Pi)");
    check("Abs(N(Integrate(1/(x^2*Sqrt(1 + x^4)), {x, 2, Infinity}) - "
        + "NIntegrate(1/(x^2*Sqrt(1 + x^4)), {x, 2, Infinity}), 15)) < 10^-10", //
        "True");
  }
}
