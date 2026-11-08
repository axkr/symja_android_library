package org.matheclipse.core.numerics.linalg;

import java.util.Arrays;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.matheclipse.core.eval.EvalEngine;

/**
 * The eigenvalues of a real symmetric matrix with a fixed number of digits: the Householder
 * reduction to a tridiagonal matrix and the QL iteration with implicit shifts.
 */
public final class ApfloatSymmetricEigenvalues {

  /** The largest number of QL iterations for one eigenvalue. */
  private static final int MAX_ITERATIONS = 100;

  private ApfloatSymmetricEigenvalues() {}

  /**
   * The eigenvalues of the real symmetric matrix <code>a</code>, sorted by decreasing absolute
   * value.
   *
   * @param a the rows of the matrix; only the lower triangle is read, the array is overwritten
   * @param precision the digits of the arithmetic
   * @return <code>null</code> if the iteration doesn't converge
   */
  public static Apfloat[] eigenvalues(Apfloat[][] a, long precision) {
    final int n = a.length;
    final FixedPrecisionApfloatHelper h = new FixedPrecisionApfloatHelper(precision);
    Apfloat[] d = new Apfloat[n];
    Apfloat[] e = new Apfloat[n];
    tridiagonal(a, d, e, h);
    if (!ql(d, e, precision, h)) {
      return null;
    }
    Arrays.sort(d, (x, y) -> ApfloatMath.abs(y).compareTo(ApfloatMath.abs(x)));
    return d;
  }

  /**
   * The Householder reduction of the symmetric matrix <code>a</code> to the tridiagonal matrix with
   * the diagonal <code>d</code> and the subdiagonal <code>e[1..n-1]</code>.
   */
  private static void tridiagonal(Apfloat[][] a, Apfloat[] d, Apfloat[] e,
      FixedPrecisionApfloatHelper h) {
    final int n = a.length;
    final EvalEngine engine = EvalEngine.get();
    for (int i = n - 1; i > 0; i--) {
      engine.checkTimeLimit();
      final int l = i - 1;
      if (l > 0) {
        Apfloat scale = Apfloat.ZERO;
        for (int k = 0; k <= l; k++) {
          scale = h.add(scale, ApfloatMath.abs(a[i][k]));
        }
        if (scale.signum() == 0) {
          e[i] = a[i][l];
          continue;
        }
        Apfloat sum = Apfloat.ZERO;
        for (int k = 0; k <= l; k++) {
          a[i][k] = h.divide(a[i][k], scale);
          sum = h.add(sum, h.multiply(a[i][k], a[i][k]));
        }
        Apfloat f = a[i][l];
        Apfloat g = f.signum() >= 0 ? h.negate(h.sqrt(sum)) : h.sqrt(sum);
        e[i] = h.multiply(scale, g);
        sum = h.subtract(sum, h.multiply(f, g));
        a[i][l] = h.subtract(f, g);
        f = Apfloat.ZERO;
        for (int j = 0; j <= l; j++) {
          g = Apfloat.ZERO;
          for (int k = 0; k <= j; k++) {
            g = h.add(g, h.multiply(a[j][k], a[i][k]));
          }
          for (int k = j + 1; k <= l; k++) {
            g = h.add(g, h.multiply(a[k][j], a[i][k]));
          }
          e[j] = h.divide(g, sum);
          f = h.add(f, h.multiply(e[j], a[i][j]));
        }
        Apfloat hh = h.divide(f, h.add(sum, sum));
        for (int j = 0; j <= l; j++) {
          f = a[i][j];
          g = h.subtract(e[j], h.multiply(hh, f));
          e[j] = g;
          for (int k = 0; k <= j; k++) {
            a[j][k] =
                h.subtract(a[j][k], h.add(h.multiply(f, e[k]), h.multiply(g, a[i][k])));
          }
        }
      } else {
        e[i] = a[i][l];
      }
    }
    e[0] = Apfloat.ZERO;
    for (int i = 0; i < n; i++) {
      d[i] = a[i][i];
    }
  }

  /**
   * The QL iteration with implicit shifts on the tridiagonal matrix with the diagonal
   * <code>d</code> and the subdiagonal <code>e[1..n-1]</code>; <code>d</code> gets the
   * eigenvalues.
   *
   * @return <code>false</code> if an eigenvalue needs too many iterations
   */
  private static boolean ql(Apfloat[] d, Apfloat[] e, long precision,
      FixedPrecisionApfloatHelper h) {
    final int n = d.length;
    final EvalEngine engine = EvalEngine.get();
    for (int i = 1; i < n; i++) {
      e[i - 1] = e[i];
    }
    e[n - 1] = Apfloat.ZERO;
    final Apfloat two = new Apfloat(2, precision);
    for (int l = 0; l < n; l++) {
      int iterations = 0;
      int m;
      do {
        for (m = l; m < n - 1; m++) {
          Apfloat dd = h.add(ApfloatMath.abs(d[m]), ApfloatMath.abs(d[m + 1]));
          if (negligible(e[m], dd, precision)) {
            break;
          }
        }
        if (m != l) {
          if (iterations++ == MAX_ITERATIONS) {
            return false;
          }
          engine.checkTimeLimit();
          Apfloat g = h.divide(h.subtract(d[l + 1], d[l]), h.multiply(two, e[l]));
          Apfloat r = hypot(g, Apfloat.ONE, h);
          g = h.add(h.subtract(d[m], d[l]),
              h.divide(e[l], h.add(g, g.signum() >= 0 ? r : h.negate(r))));
          Apfloat s = Apfloat.ONE;
          Apfloat c = Apfloat.ONE;
          Apfloat p = Apfloat.ZERO;
          int i;
          boolean deflated = false;
          for (i = m - 1; i >= l; i--) {
            Apfloat f = h.multiply(s, e[i]);
            Apfloat b = h.multiply(c, e[i]);
            r = hypot(f, g, h);
            e[i + 1] = r;
            if (r.signum() == 0) {
              d[i + 1] = h.subtract(d[i + 1], p);
              e[m] = Apfloat.ZERO;
              deflated = true;
              break;
            }
            s = h.divide(f, r);
            c = h.divide(g, r);
            g = h.subtract(d[i + 1], p);
            r = h.add(h.multiply(h.subtract(d[i], g), s), h.multiply(h.multiply(two, c), b));
            p = h.multiply(s, r);
            d[i + 1] = h.add(g, p);
            g = h.subtract(h.multiply(c, r), b);
          }
          if (deflated) {
            continue;
          }
          d[l] = h.subtract(d[l], p);
          e[l] = g;
          e[m] = Apfloat.ZERO;
        }
      } while (m != l);
    }
    return true;
  }

  /** Test if <code>x</code> is below the last digit of <code>size</code>. */
  private static boolean negligible(Apfloat x, Apfloat size, long precision) {
    if (x.signum() == 0) {
      return true;
    }
    if (size.signum() == 0) {
      return false;
    }
    return x.scale() < size.scale() - precision + 2;
  }

  private static Apfloat hypot(Apfloat a, Apfloat b, FixedPrecisionApfloatHelper h) {
    return h.sqrt(h.add(h.multiply(a, a), h.multiply(b, b)));
  }
}
