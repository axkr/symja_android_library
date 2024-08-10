package org.matheclipse.core.numerics.series.dp.complex;

import org.hipparchus.complex.Complex;

/**
 * Implements Brezinski's Theta algorithm for convergence of infinite series based on [1] and as
 * described in [2].
 * 
 * <p>
 * References:
 * <ul>
 * <li>[1] Brezinski, Claude. Acc�l�ration de la convergence en analyse num�rique. Vol. 584.
 * Springer, 2006.</li>
 * <li>[2] Weniger, Ernst Joachim. "Nonlinear sequence transformations for the acceleration of
 * convergence and the summation of divergent series." arXiv preprint math/0306302 (2003).</li>
 * </ul>
 * </p>
 */
public final class BrezinskiThetaComplex extends SeriesAlgorithmComplex {

  private final Complex[] myTabA, myTabB;

  public BrezinskiThetaComplex(final double tolerance, final int maxIters, final int patience) {
    super(tolerance, maxIters, patience);
    myTabA = new Complex[maxIters];
    myTabB = new Complex[maxIters];
  }

  @Override
  public final Complex next(final Complex e, final Complex term) {

    // first entry of table
    if (myIndex == 0) {
      myTabA[0] = term;
      ++myIndex;
      return term;
    }

    // swapping the A and B arrays
    final Complex[] a, b;
    if ((myIndex & 1) == 0) {
      a = myTabA;
      b = myTabB;
    } else {
      a = myTabB;
      b = myTabA;
    }

    // the case n >= 1
    final int jmax = ((myIndex << 1) + 1) / 3;
    Complex aux1 = a[0];
    Complex aux2 = Complex.ZERO;
    a[0] = term;
    for (int j = 1; j <= jmax; ++j) {
      final Complex aux3 = aux2;
      aux2 = aux1;
      if (j < jmax) {
        aux1 = a[j];
      }
      if ((j & 1) == 0) {
        final Complex denom = a[j - 1].subtract(b[j - 1].add(b[j - 1])).add(aux2);

        // correct for underflow
        if (denom.norm() <= TINY) {
          a[j] = new Complex(HUGE, HUGE);
        } else {
          a[j] = aux3
              .add((b[j - 2].subtract(aux3)).multiply((a[j - 1].subtract(b[j - 1]))).divide(denom));
        }
      } else {
        final Complex diff = a[j - 1].subtract(b[j - 1]);

        // correct for underflow
        if (diff.norm() <= TINY) {
          a[j] = new Complex(HUGE, HUGE);
        } else {
          a[j] = aux3.add(diff.reciprocal());
        }
      }
    }
    ++myIndex;

    // return result
    if ((jmax & 1) == 0) {
      return a[jmax];
    } else {
      return a[jmax - 1];
    }
  }

  @Override
  public final String getName() {
    return "Brezinski Theta";
  }
}
