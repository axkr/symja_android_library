package org.matheclipse.core.system;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.S;

/**
 * Unit tests for the {@link S#Limit} built-in function, focusing on:
 * <ul>
 * <li>Oscillating special functions that must return {@code Indeterminate}</li>
 * <li>L'Hôpital's rule cases (0/0 and ∞/∞ forms)</li>
 * <li>Gruntz / MRV algorithm cases at ±Infinity</li>
 * <li>Squeeze theorem cases</li>
 * </ul>
 */
public class LimitTestOscillating extends ExprEvaluatorTestCase {

  private String limit(String expr, String var, String to) {
    return evalString("Limit(" + expr + ", " + var + " -> " + to + ")");
  }

  private String limit(String expr, String var, String to, String direction) {
    return evalString(
        "Limit(" + expr + ", " + var + " -> " + to + ", Direction -> " + direction + ")");
  }


  /** Sin(x) oscillates as x → +∞, so the limit is Indeterminate. */
  @Test
  public void testSinInfinityIsIndeterminate() {
    assertEquals("Indeterminate", limit("Sin(x)", "x", "Infinity"));
  }

  /** Cos(x) oscillates as x → +∞. */
  @Test
  public void testCosInfinityIsIndeterminate() {
    assertEquals("Indeterminate", limit("Cos(x)", "x", "Infinity"));
  }

  /** Sin(x) oscillates as x → −∞. */
  @Test
  public void testSinNegativeInfinityIsIndeterminate() {
    assertEquals("Indeterminate", limit("Sin(x)", "x", "-Infinity"));
  }

  /** Tan(x) oscillates as x → +∞. */
  @Test
  public void testTanInfinityIsIndeterminate() {
    assertEquals("Indeterminate", limit("Tan(x)", "x", "Infinity"));
  }

  /** An undamped product of two trig functions still oscillates. */
  @Test
  public void testSinTimesCosInfinityIsIndeterminate() {
    assertEquals("Indeterminate", limit("Sin(x)*Cos(x)", "x", "Infinity"));
  }

  /** Gamma has poles at every non-positive integer → Indeterminate as arg → −∞. */
  @Test
  public void testGammaAtNegativeInfinityIsIndeterminate() {
    assertEquals("Indeterminate", limit("Gamma(x)", "x", "-Infinity"));
  }

  /** Factorial(x) (= Gamma(x+1)) oscillates for negative integer arguments. */
  @Test
  public void testFactorialAtNegativeInfinityIsIndeterminate() {
    assertEquals("Indeterminate", limit("x!", "x", "-Infinity"));
  }

  /** LogGamma also has poles on the negative integers. */
  @Test
  public void testLogGammaAtNegativeInfinityIsIndeterminate() {
    assertEquals("Indeterminate", limit("LogGamma(x)", "x", "-Infinity"));
  }

  @Test
  public void testAiryAiAtNegativeInfinityIsZero() {
    assertEquals("0", limit("AiryAi(x)", "x", "-Infinity"));
  }

  @Test
  public void testAiryBiAtNegativeInfinityIsZero() {
    assertEquals("0", limit("AiryBi(x)", "x", "-Infinity"));
  }

  @Test
  public void testAiryAiAtInfinityIsZero() {
    assertEquals("0", limit("AiryAi(x)", "x", "Infinity"));
  }

  @Test
  public void testAiryBiAtInfinityIsInfinity() {
    assertEquals("Infinity", limit("AiryBi(x)", "x", "Infinity"));
  }

  @Test
  public void testBesselJAtInfinityIsZero() {
    assertEquals("0", limit("BesselJ(0, x)", "x", "Infinity"));
  }

  @Test
  public void testBesselJAtMinusInfinityIsZero() {
    assertEquals("0", limit("BesselJ(0, x)", "x", "-Infinity"));
  }

  @Test
  public void testBesselYAtInfinityIsZero() {
    assertEquals("0", limit("BesselY(0, x)", "x", "Infinity"));
  }

  @Test
  public void testBesselYAtMinusInfinityIsZero() {
    assertEquals("0", limit("BesselY(0, x)", "x", "-Infinity"));
  }


  /** Classic squeeze: x·Sin(1/x) → 0 as x → 0. */
  @Test
  public void testSqueezeXTimesSin1OverX() {
    assertEquals("0", limit("x*Sin(1/x)", "x", "0"));
  }

  /** Squeeze: x²·Sin(1/x)³ → 0 as x → 0. */
  @Test
  public void testSqueezeX2TimesSin1OverX3() {
    assertEquals("0", limit("x^2*Sin(1/x)^3", "x", "0"));
  }

  /** Squeeze: x·Cos(1/x) → 0 as x → 0. */
  @Test
  public void testSqueezeXTimesCos1OverX() {
    assertEquals("0", limit("x*Cos(1/x)", "x", "0"));
  }

  /** Standard 0/0: Sin(x)/x → 1 as x → 0. */
  @Test
  public void testLHopital_SinXoverX() {
    assertEquals("1", limit("Sin(x)/x", "x", "0"));
  }

  /** 0/0: (1 − Cos(x))/x → 0 as x → 0. */
  @Test
  public void testLHopital_OneMinusCosXoverX() {
    assertEquals("0", limit("(1-Cos(x))/x", "x", "0"));
  }

  /** 0/0: (1 − Cos(x))/x² → 1/2 as x → 0 (second application). */
  @Test
  public void testLHopital_OneMinusCosXoverX2() {
    assertEquals("1/2", limit("(1-Cos(x))/x^2", "x", "0"));
  }

  /** 0/0: (E^x − 1)/x → 1 as x → 0. */
  @Test
  public void testLHopital_ExpMinusOneOverX() {
    assertEquals("1", limit("(E^x-1)/x", "x", "0"));
  }

  /** 0/0: Log(1+x)/x → 1 as x → 0. */
  @Test
  public void testLHopital_LogOnePlusXoverX() {
    assertEquals("1", limit("Log(1+x)/x", "x", "0"));
  }

  /** 0/0: (x − Sin(x))/x³ → 1/6 as x → 0. */
  @Test
  public void testLHopital_XMinusSinXoverX3() {
    assertEquals("1/6", limit("(x-Sin(x))/x^3", "x", "0"));
  }

  /** 0/0: (Sqrt(1+x) − 1)/x → 1/2 as x → 0. */
  @Test
  public void testLHopital_SqrtOnePlusXMinusOneOverX() {
    assertEquals("1/2", limit("(Sqrt(1+x)-1)/x", "x", "0"));
  }

  /** 0/0: (a^x − 1)/x → Log(a) as x → 0. */
  @Test
  public void testLHopital_AtoXMinusOneOverX() {
    assertEquals("Log(a)", limit("(a^x-1)/x", "x", "0"));
  }

  /** ∞/∞: Log(x)/x → 0 as x → +∞. */
  @Test
  public void testLHopital_LogXoverX() {
    assertEquals("0", limit("Log(x)/x", "x", "Infinity"));
  }

  /** ∞/∞: x/E^x → 0 as x → +∞. */
  @Test
  public void testLHopital_XoverExpX() {
    assertEquals("0", limit("x/E^x", "x", "Infinity"));
  }

  /** ∞/∞: x^n / E^x → 0 for any fixed positive integer n. */
  @Test
  public void testLHopital_X3overExpX() {
    assertEquals("0", limit("x^3/E^x", "x", "Infinity"));
  }

  /** Classic MRV: E^x dominates any polynomial. */
  @Test
  public void testGruntz_ExpXDominatesPolynomial() {
    assertEquals("Infinity", limit("E^x/x^100", "x", "Infinity"));
  }

  /** MRV: E^(−x) → 0 as x → +∞. */
  @Test
  public void testGruntz_ExpNegX() {
    assertEquals("0", limit("E^(-x)", "x", "Infinity"));
  }

  /** MRV: iterated exponential limit. */
  @Test
  public void testGruntz_ExpExpX() {
    assertEquals("Infinity", limit("E^(E^x)", "x", "Infinity"));
  }

  /** MRV: x^(1/x) → 1 as x → +∞ (a classic Gruntz benchmark). */
  @Test
  public void testGruntz_XtoOneOverX() {
    assertEquals("1", limit("x^(1/x)", "x", "Infinity"));
  }

  /** MRV: (1 + 1/x)^x → E as x → +∞. */
  @Test
  public void testGruntz_OnePlusOneOverXtoX() {
    assertEquals("E", limit("(1+1/x)^x", "x", "Infinity"));
  }

  /** MRV: (1 + a/x)^x → E^a as x → +∞. */
  @Test
  public void testGruntz_OnePlusAoverXtoX() {
    assertEquals("E^a", limit("(1+a/x)^x", "x", "Infinity"));
  }

  /** MRV: x·Log(x) → 0 as x → 0 from above. */
  @Test
  public void testGruntz_XLogXAtZeroFromAbove() {
    assertEquals("0", limit("x*Log(x)", "x", "0", "-1"));
  }

  /** MRV: Log(x)/x^2 → −∞ ... actually 0 as x → +∞. */
  @Test
  public void testGruntz_LogXoverX2() {
    assertEquals("0", limit("Log(x)/x^2", "x", "Infinity"));
  }

  /** Gamma(x) → +∞ as x → +∞. */
  @Test
  public void testGammaAtPositiveInfinity() {
    assertEquals("Infinity", limit("Gamma(x)", "x", "Infinity"));
  }

  /** Limit(Gamma(1/t), t → +∞) = Gamma(0^+) = +∞. */
  @Test
  public void testGammaOneOverTAtInfinity() {
    assertEquals("Infinity", limit("Gamma(1/t)", "t", "Infinity"));
  }

  /** Limit(Gamma(1/t), t → −∞) = Gamma(0^−) = −∞. */
  @Test
  public void testGammaOneOverTAtNegativeInfinity() {
    assertEquals("-Infinity", limit("Gamma(1/t)", "t", "-Infinity"));
  }

  /** |x|/x has different one-sided limits → two-sided limit is Indeterminate. */
  @Test
  public void testAbsXoverXTwoSidedIsIndeterminate() {
    assertEquals("Indeterminate", limit("Abs(x)/x", "x", "0"));
  }

  /** |x|/x from above → 1. */
  @Test
  public void testAbsXoverXFromAbove() {
    assertEquals("1", limit("Abs(x)/x", "x", "0", "-1"));
  }

  /** |x|/x from below → −1. */
  @Test
  public void testAbsXoverXFromBelow() {
    assertEquals("-1", limit("Abs(x)/x", "x", "0", "1"));
  }

  /** 1/x from above → +∞. */
  @Test
  public void testOneOverXFromAbove() {
    assertEquals("Infinity", limit("1/x", "x", "0", "-1"));
  }

  /** 1/x from below → −∞. */
  @Test
  public void testOneOverXFromBelow() {
    assertEquals("-Infinity", limit("1/x", "x", "0", "1"));
  }

  /** 1^∞ form: (1 + 1/x)^x → E (handled via Exp(Limit(x·Log(1+1/x)))). */
  @Test
  public void testOnePlusOneOverXtoXPowerForm() {
    assertEquals("E", limit("(1+1/x)^x", "x", "Infinity"));
  }

  /** 0^0 form: x^x → 1 as x → 0^+. */
  @Test
  public void testXtoXAtZeroFromAbove() {
    assertEquals("1", limit("x^x", "x", "0", "-1"));
  }

  /** ∞^0 form: x^(1/Log(x)) → E as x → +∞. */
  @Test
  public void testXtoOneOverLogX() {
    assertEquals("E", limit("x^(1/Log(x))", "x", "Infinity"));
  }
}
