package org.matheclipse.core.numerics.series.dp;

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
public abstract class OsadaAdaptiveAlgorithm extends SeriesAlgorithm {

  /** <code>1</code> if the last {@link #transform(double, boolean, int, double)} broke down. */
  protected int status;
  private double xx, dx, dd, xp, alpha;
  private final double[] x;

  protected OsadaAdaptiveAlgorithm(final double tolerance, final int maxIters,
      final int patience) {
    super(tolerance, maxIters, patience);
    x = new double[maxIters + 1];
  }

  @Override
  public final double next(final double e, final double term) {
    if (myIndex == 0) {
      xx = dx = dd = 0.0;
    }
    final double xxp = xx;
    x[myIndex] = xx = term;
    if (myIndex == 0) {
      dx = xx;
    } else if (myIndex == 1) {
      final double dxp = dx;
      dx = xx - xxp;
      dd = dx / (dx - dxp);
    } else {
      final double dxp = dx;
      final double ddp = dd;
      dx = xx - xxp;
      dd = dx / (dx - dxp);
      alpha = 1.0 / (dd - ddp) + 1.0;
      alpha = transform(alpha, true, myIndex - 1, -2.0);
    }
    xp = xx;
    if (myIndex >= 2) {
      for (int m = 1; m <= myIndex + 1; ++m) {
        xp = transform(x[m - 1], false, m, alpha);
      }
    }
    ++myIndex;
    if (status == 1) {
      return Double.NaN;
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
  protected abstract double transform(double xx, boolean alphaTable, int n, double theta);
}
