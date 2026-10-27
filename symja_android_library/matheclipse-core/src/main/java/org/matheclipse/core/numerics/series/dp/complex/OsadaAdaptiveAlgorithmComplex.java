package org.matheclipse.core.numerics.series.dp.complex;

import org.hipparchus.complex.Complex;

/**
 * The base of the algorithms which estimate the exponent <code>alpha</code> of a logarithmically
 * convergent sequence adaptively and accelerate the sequence with the transformation for this
 * <code>alpha</code>, based on the method in [1].
 *
 * <p>
 * References:
 * <ul>
 * <li>[1] Osada, Naoki. Acceleration methods for slowly convergent sequences and their
 * applications. Diss. PhD thesis, Nagoya University, 1993.</li>
 * </ul>
 * </p>
 */
public abstract class OsadaAdaptiveAlgorithmComplex extends SeriesAlgorithmComplex {

  /** <code>1</code> if the last {@link #transform(Complex, boolean, int, Complex)} broke down. */
  protected int status;
  private Complex xx, dx, dd, xp, alpha;
  private final Complex[] x;

  protected OsadaAdaptiveAlgorithmComplex(final double tolerance, final int maxIters,
      final int patience) {
    super(tolerance, maxIters, patience);
    x = new Complex[maxIters + 1];
  }

  @Override
  public final Complex next(final Complex e, final Complex term) {
    if (myIndex == 0) {
      xx = dx = dd = Complex.ZERO;
    }
    final Complex xxp = xx;
    x[myIndex] = xx = term;
    if (myIndex == 0) {
      dx = xx;
    } else if (myIndex == 1) {
      final Complex dxp = dx;
      dx = xx.subtract(xxp);
      dd = dx.divide(dx.subtract(dxp));
    } else {
      final Complex dxp = dx;
      final Complex ddp = dd;
      dx = xx.subtract(xxp);
      dd = dx.divide(dx.subtract(dxp));
      alpha = dd.subtract(ddp).reciprocal().add(1.0);
      alpha = transform(alpha, true, myIndex - 1, new Complex(-2.0));
    }
    xp = xx;
    if (myIndex >= 2) {
      for (int m = 1; m <= myIndex + 1; ++m) {
        xp = transform(x[m - 1], false, m, alpha);
      }
    }
    ++myIndex;
    if (status == 1) {
      return Complex.NaN;
    } else {
      return xp;
    }
  }

  /**
   * Adds the <code>n</code>-th element <code>xx</code> to a table of the transformation with the
   * parameter <code>theta</code> and returns the new estimate of the limit.
   *
   * @param alphaTable <code>true</code> for the table which extrapolates the estimates of
   *        <code>alpha</code>, <code>false</code> for the table of the sequence
   */
  protected abstract Complex transform(Complex xx, boolean alphaTable, int n, Complex theta);
}
