package org.matheclipse.core.numerics.series.dp.complex;

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
public final class IteratedThetaComplex extends SeriesAlgorithmComplex {

  private final Complex[] myComplexTab;

  public IteratedThetaComplex(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
    myComplexTab = new Complex[maxIters];
  }

  @Override
  public final Complex next(final Complex e, final Complex term) {

    // initial value for J
    myComplexTab[myIndex] = term;
    if (myIndex < 3) {
      ++myIndex;
      return term;
    }

    // higher-order J array
    final int lmax = myIndex / 3;
    int m = myIndex;
    for (int l = 1; l <= lmax; ++l) {
      m -= 3;
      final Complex diff0 = myComplexTab[m + 1].subtract(myComplexTab[m]);
      final Complex diff1 = myComplexTab[m + 2].subtract(myComplexTab[m + 1]);
      final Complex diff2 = myComplexTab[m + 3].subtract(myComplexTab[m + 2]);
      final Complex dfsq1 = diff1.subtract(diff0);
      final Complex dfsq2 = diff2.subtract(diff1);
      final Complex denom = diff2.multiply(dfsq1).subtract(diff0.multiply(dfsq2));
      final Complex ratio = dfsq2.divide(denom);

      // safeguarded against underflow
      if (denom.norm() < TINY) {
        myComplexTab[m] = new Complex(HUGE, HUGE);
      } else {
        myComplexTab[m] = myComplexTab[m + 1].subtract(diff0.multiply(diff1).multiply(ratio));
      }
    }
    ++myIndex;

    // return result
    return myComplexTab[myIndex % 3];
  }

  @Override
  public final String getName() {
    return "Iterated Theta Complex";
  }
}
