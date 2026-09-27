package org.matheclipse.core.numerics.integral;

/**
 * Wynn's epsilon algorithm, fed one partial sum at a time. It accelerates the convergence of a
 * sequence of partial sums, in particular of an alternating series.
 *
 * <p>
 * The recurrence is <code>eps(k+1, m) = eps(k-1, m+1) + 1/(eps(k, m+1) - eps(k, m))</code>; only the
 * latest ascending diagonal of the table is kept, following E. J. Weniger, <i>Nonlinear sequence
 * transformations for the acceleration of convergence and the summation of divergent series</i>,
 * Computer Physics Reports 10 (1989), 189-371, subroutine <code>WYNNEP</code>.
 *
 * <p>
 * Summing a divergent oscillating series gives it a finite (Abel, Cesaro) value - <code>1-1+1-...
 * </code> becomes <code>1/2</code> - so the caller must know the series converges.
 */
public final class WynnEpsilon {

  private static final double HUGE = 1.0e60;
  private static final double TINY = 1.0e-60;

  private final double[] diagonal;
  private int count = 0;

  /** @param capacity the maximum number of partial sums */
  public WynnEpsilon(int capacity) {
    diagonal = new double[capacity];
  }

  /**
   * Add the next partial sum.
   *
   * @return the current accelerated estimate of the limit
   * @throws IllegalStateException if more partial sums are added than the capacity allows
   */
  public double add(double partialSum) {
    if (count >= diagonal.length) {
      throw new IllegalStateException("WynnEpsilon capacity exceeded");
    }
    final int n = count++;
    diagonal[n] = partialSum;
    if (n == 0) {
      return partialSum;
    }
    double aux2 = 0.0;
    for (int j = n; j >= 1; j--) {
      final double aux1 = aux2;
      aux2 = diagonal[j - 1];
      final double diff = diagonal[j] - aux2;
      diagonal[j - 1] = Math.abs(diff) < TINY ? HUGE : aux1 + 1.0 / diff;
    }
    final double estimate = diagonal[n % 2];
    // a difference below TINY means the sums have already settled; the table then holds the HUGE
    // sentinel instead of an estimate
    return Math.abs(estimate) < HUGE / 2 ? estimate : partialSum;
  }

  /** The number of partial sums added so far. */
  public int count() {
    return count;
  }
}
