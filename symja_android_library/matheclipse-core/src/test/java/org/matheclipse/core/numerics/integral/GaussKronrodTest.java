package org.matheclipse.core.numerics.integral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.DoubleUnaryOperator;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.numerics.integral.Quadrature.QuadratureResult;

/** The QUADPACK port: QAGS on finite ranges, QAGI on infinite ones. */
public class GaussKronrodTest {

  private static final double TOL = 1.0e-10;

  private static QuadratureResult integrate(DoubleUnaryOperator f, double a, double b) {
    return new GaussKronrod(TOL, TOL, 10000).integrate(f, a, b);
  }

  private static void assertIntegral(double expected, DoubleUnaryOperator f, double a, double b) {
    QuadratureResult result = integrate(f, a, b);
    assertEquals(QuadratureResult.STATUS_OK, result.status, result.toString());
    assertEquals(expected, result.estimate, 1.0e-9 * Math.max(1.0, Math.abs(expected)));
  }

  @Test
  public void testSmooth() {
    assertIntegral(1.0 / 3.0, x -> x * x, 0.0, 1.0);
    assertIntegral(2.0, Math::sin, 0.0, Math.PI);
    assertIntegral(-2.0, Math::sin, Math.PI, 0.0);
  }

  @Test
  public void testEndpointSingularities() {
    // the epsilon extrapolation of QAGS
    assertIntegral(2.0 / 3.0, Math::sqrt, 0.0, 1.0);
    assertIntegral(-1.0, Math::log, 0.0, 1.0);
    assertIntegral(2.0, x -> 1.0 / Math.sqrt(x), 0.0, 1.0);
    assertIntegral(10.0, x -> Math.pow(x, -0.9), 0.0, 1.0);
  }

  @Test
  public void testInfiniteRanges() {
    assertIntegral(Math.sqrt(Math.PI), x -> Math.exp(-x * x), Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY);
    assertIntegral(Math.PI / 2.0, x -> 1.0 / (1.0 + x * x), 0.0, Double.POSITIVE_INFINITY);
    assertIntegral(1.0, x -> Math.exp(x), Double.NEGATIVE_INFINITY, 0.0);
  }

  @Test
  public void testDivergent() {
    QuadratureResult result = integrate(x -> 1.0 / x, 0.0, 1.0);
    assertTrue(result.status != QuadratureResult.STATUS_OK, result.toString());
    assertTrue(result.worstPoint < 0.01, "worst point " + result.worstPoint);
  }

  @Test
  public void testNonFiniteSample() {
    // the centre node of [-1,1] is 0
    QuadratureResult result = integrate(x -> 1.0 / x, -1.0, 1.0);
    assertEquals(QuadratureResult.STATUS_BAD_INTEGRAND, result.status);
    assertEquals(0.0, result.worstPoint);
  }

  @Test
  public void testTolerancesBelowMachinePrecision() {
    QuadratureResult result = new GaussKronrod(0.0, 1.0e-15, 10000).integrate(x -> x, 0.0, 2.0);
    assertEquals(QuadratureResult.STATUS_INVALID, result.status);
  }

  @Test
  public void testEvaluationBudget() {
    AtomicInteger count = new AtomicInteger();
    QuadratureResult result = new GaussKronrod(1.0e-14, 1.0e-13, 500).integrate(x -> {
      count.incrementAndGet();
      return Math.sin(1000.0 * x);
    }, 0.0, 100.0);
    assertEquals(QuadratureResult.STATUS_LIMIT, result.status);
    assertTrue(count.get() <= 500, "evaluations " + count.get());
    assertEquals(count.get(), result.evaluations);
  }
}
