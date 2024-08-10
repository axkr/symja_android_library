package org.matheclipse.core.numerics.series.dp;

import org.hipparchus.complex.Complex;

/**
 * Implements the Iterated Theta algorithm for convergence of infinite series as described in [1].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Weniger, Ernst Joachim. "Nonlinear sequence transformations for the acceleration of
 * convergence and the summation of divergent series." arXiv preprint math/0306302 (2003).</li>
 * </ul>
 * </p>
 */
public final class IteratedTheta extends SeriesAlgorithm {

  private final double[] myTab;
  private final Complex[] myComplexTab;

  public IteratedTheta(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
    myTab = new double[maxIters];
    myComplexTab = new Complex[maxIters];
  }

  @Override
  public final double next(final double e, final double term) {

    // initial value for J
    myTab[myIndex] = term;
    if (myIndex < 3) {
      ++myIndex;
      return term;
    }

    // higher-order J array
    final int lmax = myIndex / 3;
    int m = myIndex;
    for (int l = 1; l <= lmax; ++l) {
      m -= 3;
      final double diff0 = myTab[m + 1] - myTab[m];
      final double diff1 = myTab[m + 2] - myTab[m + 1];
      final double diff2 = myTab[m + 3] - myTab[m + 2];
      final double dfsq1 = diff1 - diff0;
      final double dfsq2 = diff2 - diff1;
      final double denom = diff2 * dfsq1 - diff0 * dfsq2;
      final double ratio = dfsq2 / denom;

      // safeguarded against underflow
      if (Math.abs(denom) < TINY) {
        myTab[m] = HUGE;
      } else {
        myTab[m] = myTab[m + 1] - diff0 * diff1 * ratio;
      }
    }
    ++myIndex;

    // return result
    return myTab[myIndex % 3];
  }

  @Override
  public final String getName() {
    return "Iterated Theta";
  }
}
