package org.matheclipse.core.eval.util;

/**
 * A running sum of machine numbers with Neumaier's compensated summation, which carries the
 * rounding error of every addition along: adding <code>0.1</code> ten times gives
 * <code>1.0</code>, where adding one after the other gives <code>0.9999999999999999</code>.
 *
 * <p>
 * An infinite or NaN value, or an overflow, makes the sum infinite or NaN like the plain sum.
 */
public final class CompensatedSum {
  private double sum;
  private double compensation;

  public CompensatedSum() {
    sum = 0.0;
    compensation = 0.0;
  }

  /**
   * Add <code>value</code> to the sum.
   *
   * @param value a machine number
   */
  public void add(final double value) {
    final double t = sum + value;
    if (Double.isFinite(t)) {
      compensation += Math.abs(sum) >= Math.abs(value) ? (sum - t) + value : (value - t) + sum;
    }
    sum = t;
  }

  /** The compensated value of the sum. */
  public double value() {
    // an infinite or NaN sum carries no rounding error
    return Double.isFinite(sum) ? sum + compensation : sum;
  }
}
