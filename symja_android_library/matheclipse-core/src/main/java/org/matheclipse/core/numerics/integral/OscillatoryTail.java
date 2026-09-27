package org.matheclipse.core.numerics.integral;

import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.numerics.integral.Quadrature.QuadratureResult;

/**
 * The integral of <code>g(x)*Sin(w*x+c)</code> or <code>g(x)*Cos(w*x+c)</code> over
 * <code>[a, Infinity)</code>, for a <code>g</code> which tends to <code>0</code>: integrate from
 * one zero of the oscillating factor to the next and sum the resulting alternating series with
 * {@link WynnEpsilon} acceleration (Longman's method, the idea behind QUADPACK's
 * <code>QAWF</code>).
 *
 * <p>
 * Longman, I. (1956). Note on a method for computing infinite integrals of oscillatory functions.
 * Mathematical Proceedings of the Cambridge Philosophical Society, 52(4), 764-768.
 */
public final class OscillatoryTail {

  /** The maximum number of half periods. */
  public static final int MAX_PIECES = 200;

  private OscillatoryTail() {
    // static methods only
  }

  /**
   * @param f the whole integrand
   * @param a the finite lower limit
   * @param w the frequency, not <code>0</code>
   * @param c the phase
   * @param cosine <code>true</code> for a <code>Cos(w*x+c)</code> factor, <code>false</code> for
   *        <code>Sin(w*x+c)</code>
   * @param epsabs absolute error tolerance
   * @param epsrel relative error tolerance
   * @param maxEvaluations the evaluation budget for each half period
   * @return the result; {@link QuadratureResult#STATUS_DIVERGENT} if the accelerated sums did not
   *         settle within {@link #MAX_PIECES} half periods, or the status of a half period which
   *         failed
   */
  public static QuadratureResult integrate(DoubleUnaryOperator f, double a, double w, double c,
      boolean cosine, double epsabs, double epsrel, int maxEvaluations) {
    final GaussKronrod rule = new GaussKronrod(epsabs, epsrel, maxEvaluations);
    final double halfPeriod = Math.PI / Math.abs(w);
    // the zeros of the factor are the x where t = (w*x + c - phase0)/Pi is an integer
    final double phase0 = cosine ? Math.PI / 2.0 : 0.0;
    final double t = (w * a + c - phase0) / Math.PI;
    final double k = w > 0 ? Math.floor(t) + 1.0 : Math.ceil(t) - 1.0;
    double lower = a;
    double upper = (Math.PI * k + phase0 - c) / w;

    final WynnEpsilon epsilon = new WynnEpsilon(MAX_PIECES);
    double sum = 0.0;
    double error = 0.0;
    int evaluations = 0;
    double previous = Double.NaN;
    double beforePrevious = Double.NaN;
    for (int i = 0; i < MAX_PIECES; i++) {
      QuadratureResult piece = rule.integrate(f, lower, upper);
      evaluations += piece.evaluations;
      if (piece.status != QuadratureResult.STATUS_OK) {
        return new QuadratureResult(sum, Double.NaN, evaluations, piece.status, piece.worstPoint);
      }
      sum += piece.estimate;
      error += piece.error;
      double estimate = epsilon.add(sum);
      double tolerance = Math.max(epsabs, epsrel * Math.abs(estimate));
      if (i >= 4 && Math.abs(estimate - previous) <= tolerance
          && Math.abs(previous - beforePrevious) <= tolerance) {
        return new QuadratureResult(estimate, Math.abs(estimate - previous) + error, evaluations,
            QuadratureResult.STATUS_OK, Double.NaN);
      }
      beforePrevious = previous;
      previous = estimate;
      lower = upper;
      upper += halfPeriod;
    }
    return new QuadratureResult(previous, Double.NaN, evaluations,
        QuadratureResult.STATUS_DIVERGENT, lower);
  }
}
