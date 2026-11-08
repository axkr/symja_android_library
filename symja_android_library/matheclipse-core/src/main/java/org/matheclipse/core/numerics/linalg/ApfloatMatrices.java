package org.matheclipse.core.numerics.linalg;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;

/**
 * Real matrices of arbitrary-precision numbers: their spectral quantities in the precision of
 * their entries, not in machine precision.
 */
public final class ApfloatMatrices {

  /** The guard digits of the arithmetic. */
  private static final long GUARD_DIGITS = 10L;

  private ApfloatMatrices() {}

  /**
   * The least precision of the arbitrary-precision numbers of a real matrix which has no machine
   * number.
   *
   * @return <code>-1</code> if the matrix isn't such a matrix
   */
  public static long precision(IExpr matrix) {
    int[] dim = matrix.isMatrix(false);
    if (dim == null || dim[0] == 0 || dim[1] == 0
        || !AbstractMatrix1Expr.hasArbitraryPrecisionNumber(matrix)) {
      return -1L;
    }
    long precision = Long.MAX_VALUE;
    IAST rows = (IAST) matrix.normal(false);
    for (IExpr row : rows) {
      for (IExpr entry : (IAST) row) {
        if (entry instanceof ApfloatNum) {
          precision = Math.min(precision, ((ApfloatNum) entry).apfloatValue().precision());
        } else if (!entry.isRational()) {
          return -1L;
        }
      }
    }
    return precision == Long.MAX_VALUE ? -1L : precision;
  }

  /** The entries of a real matrix, see {@link #precision(IExpr)}, with <code>digits</code> digits. */
  private static Apfloat[][] toApfloat(IExpr matrix, long digits) {
    IAST rows = (IAST) matrix.normal(false);
    Apfloat[][] a = new Apfloat[rows.argSize()][];
    for (int i = 1; i < rows.size(); i++) {
      IAST row = (IAST) rows.get(i);
      a[i - 1] = new Apfloat[row.argSize()];
      for (int j = 1; j < row.size(); j++) {
        IExpr entry = row.get(j);
        if (!(entry instanceof ApfloatNum)) {
          entry = EvalEngine.get().evalN(entry, digits);
        }
        if (!(entry instanceof IReal)) {
          return null;
        }
        a[i - 1][j - 1] = ((IReal) entry).apfloatValue().precision(digits);
      }
    }
    return a;
  }

  /** The product <code>Transpose(a) . a</code>. */
  private static Apfloat[][] gram(Apfloat[][] a, long digits) {
    FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(digits);
    final int rows = a.length;
    final int columns = a[0].length;
    Apfloat[][] g = new Apfloat[columns][columns];
    for (int i = 0; i < columns; i++) {
      EvalEngine.get().checkTimeLimit();
      for (int j = 0; j <= i; j++) {
        Apfloat sum = Apfloat.ZERO;
        for (int k = 0; k < rows; k++) {
          sum = h.add(sum, h.multiply(a[k][i], a[k][j]));
        }
        g[i][j] = sum;
        g[j][i] = sum;
      }
    }
    return g;
  }

  /** The largest number of power iterations for the largest singular value. */
  private static final int MAX_POWER_ITERATIONS = 2000;

  /**
   * The largest singular value of a real matrix of arbitrary-precision numbers, in the least
   * precision of its entries: the power iteration on <code>Transpose(a) . a</code> from the machine
   * singular vector costs <code>O(m n)</code> per step. The error left is estimated from the rate
   * of the last changes; a slow rate falls back to all the eigenvalues of
   * <code>Transpose(a) . a</code>.
   *
   * @return {@link F#NIL} if it isn't such a matrix
   */
  public static IExpr largestSingularValue(IExpr matrix) {
    final long precision = precision(matrix);
    if (precision < 0) {
      return F.NIL;
    }
    final long digits = precision + GUARD_DIGITS;
    final Apfloat[][] a = toApfloat(matrix, digits);
    if (a == null) {
      return F.NIL;
    }
    final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(digits);
    final int rows = a.length;
    final int columns = a[0].length;
    Apfloat[] v = new Apfloat[columns];
    double[] start = machineSingularVector(a);
    for (int j = 0; j < columns; j++) {
      v[j] = new Apfloat(start[j], digits);
    }
    v = normalized(v, h);
    if (v == null) {
      // the machine matrix is zero: a matrix of tiny numbers
      IAST all = singularValues(matrix, true);
      return all.isPresent() ? all.arg1() : F.NIL;
    }
    final Apfloat tolerance = new Apfloat("1e-" + (precision + 2L), digits);
    Apfloat previous = null;
    Apfloat change = null;
    for (int iteration = 0; iteration < MAX_POWER_ITERATIONS; iteration++) {
      EvalEngine.get().checkTimeLimit();
      Apfloat[] w = new Apfloat[rows];
      Apfloat norm2 = Apfloat.ZERO;
      for (int i = 0; i < rows; i++) {
        Apfloat sum = Apfloat.ZERO;
        for (int j = 0; j < columns; j++) {
          sum = h.add(sum, h.multiply(a[i][j], v[j]));
        }
        w[i] = sum;
        norm2 = h.add(norm2, h.multiply(sum, sum));
      }
      if (norm2.signum() == 0) {
        return F.num(new Apfloat(0, precision));
      }
      if (previous != null) {
        Apfloat newChange = ApfloatMath.abs(h.subtract(norm2, previous));
        if (newChange.signum() == 0) {
          return F.num(h.sqrt(norm2).precision(precision));
        }
        if (change != null && change.signum() != 0) {
          // the error left after a geometric rate rho is change*rho/(1-rho)
          double rho = h.divide(newChange, change).doubleValue();
          if (rho < 0.99) {
            Apfloat left = h.multiply(newChange, new Apfloat(rho / (1.0 - rho), digits));
            if (left.compareTo(h.multiply(tolerance, norm2)) < 0) {
              return F.num(h.sqrt(norm2).precision(precision));
            }
          } else if (iteration > 50) {
            break;
          }
        }
        change = newChange;
      }
      previous = norm2;
      Apfloat[] u = new Apfloat[columns];
      for (int j = 0; j < columns; j++) {
        Apfloat sum = Apfloat.ZERO;
        for (int i = 0; i < rows; i++) {
          sum = h.add(sum, h.multiply(a[i][j], w[i]));
        }
        u[j] = sum;
      }
      v = normalized(u, h);
      if (v == null) {
        break;
      }
    }
    IAST all = singularValues(matrix, true);
    return all.isPresent() ? all.arg1() : F.NIL;
  }

  /** The vector divided by its length, <code>null</code> for the zero vector. */
  private static Apfloat[] normalized(Apfloat[] v, FixedPrecisionApfloatHelper h) {
    Apfloat norm2 = Apfloat.ZERO;
    for (Apfloat x : v) {
      norm2 = h.add(norm2, h.multiply(x, x));
    }
    if (norm2.signum() == 0) {
      return null;
    }
    Apfloat norm = h.sqrt(norm2);
    Apfloat[] result = new Apfloat[v.length];
    for (int i = 0; i < v.length; i++) {
      result[i] = h.divide(v[i], norm);
    }
    return result;
  }

  /**
   * The right singular vector of the largest singular value of the machine matrix of
   * <code>a</code>, or a vector of ones if the machine decomposition fails.
   */
  private static double[] machineSingularVector(Apfloat[][] a) {
    final int columns = a[0].length;
    double[] ones = new double[columns];
    java.util.Arrays.fill(ones, 1.0);
    try {
      double[][] d = new double[a.length][columns];
      for (int i = 0; i < a.length; i++) {
        for (int j = 0; j < columns; j++) {
          d[i][j] = a[i][j].doubleValue();
        }
      }
      org.hipparchus.linear.SingularValueDecomposition svd =
          new org.hipparchus.linear.SingularValueDecomposition(
              new org.hipparchus.linear.Array2DRowRealMatrix(d, false));
      double[] v = svd.getV().getColumn(0);
      for (double x : v) {
        if (!Double.isFinite(x)) {
          return ones;
        }
      }
      return v;
    } catch (RuntimeException rex) {
      return ones;
    }
  }

  /**
   * The eigenvalues of a real symmetric matrix of arbitrary-precision numbers, sorted by decreasing
   * absolute value, in the least precision of its entries.
   *
   * @return {@link F#NIL} if it isn't such a matrix
   */
  public static IAST symmetricEigenvalues(IExpr matrix) {
    long precision = precision(matrix);
    int[] dim = matrix.isMatrix(false);
    if (precision < 0 || dim[0] != dim[1] || !matrix.normal(false).equals(
        EvalEngine.get().evaluate(F.Transpose(matrix.normal(false))))) {
      return F.NIL;
    }
    Apfloat[][] a = toApfloat(matrix, precision + GUARD_DIGITS);
    if (a == null) {
      return F.NIL;
    }
    Apfloat[] values = ApfloatSymmetricEigenvalues.eigenvalues(a, precision + GUARD_DIGITS);
    if (values == null) {
      return F.NIL;
    }
    return F.mapRange(0, values.length, i -> F.num(values[i].precision(precision)));
  }

  /**
   * The singular values of a real matrix of arbitrary-precision numbers in decreasing order, in the
   * least precision of its entries: the square roots of the eigenvalues of
   * <code>Transpose(a) . a</code>, computed with twice the digits so that the small ones keep
   * theirs.
   *
   * @param largestOnly only the largest singular value, for which the precision of the entries
   *        suffices
   * @return {@link F#NIL} if it isn't such a matrix
   */
  public static IAST singularValues(IExpr matrix, boolean largestOnly) {
    long precision = precision(matrix);
    if (precision < 0) {
      return F.NIL;
    }
    long digits = (largestOnly ? precision : 2L * precision) + GUARD_DIGITS;
    Apfloat[][] a = toApfloat(matrix, digits);
    if (a == null) {
      return F.NIL;
    }
    Apfloat[] values = ApfloatSymmetricEigenvalues.eigenvalues(gram(a, digits), digits);
    if (values == null) {
      return F.NIL;
    }
    FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(digits);
    int count = largestOnly ? 1 : values.length;
    return F.mapRange(0, count,
        i -> F.num(values[i].signum() <= 0 ? new Apfloat(0, precision)
            : h.sqrt(values[i]).precision(precision)));
  }
}
