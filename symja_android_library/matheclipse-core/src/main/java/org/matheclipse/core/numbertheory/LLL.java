package org.matheclipse.core.numbertheory;

import java.math.BigInteger;

/**
 * Lenstra-Lenstra-Lov&aacute;sz lattice basis reduction in exact integer arithmetic.
 *
 * <p>
 * This is the <i>integral</i> variant of the LLL algorithm (H. Cohen, <i>A Course in Computational
 * Algebraic Number Theory</i>, Algorithm 2.6.7). Instead of the rational Gram-Schmidt coefficients
 * <code>mu(i,j)</code> the algorithm carries the integers <code>d(i)</code> (the leading principal
 * minors of the Gram matrix) and <code>lambda(i,j) = d(j) * mu(i,j)</code>. Every division which
 * occurs is exact, so all intermediate values stay {@link BigInteger}s and no gcd normalization of
 * fractions is needed.
 *
 * <p>
 * The rows of the input matrix are interpreted as the basis vectors of the lattice. Vectors which
 * are linearly dependent over the rationals are handled by first computing a row Hermite normal
 * form of the input (which is a basis of the same lattice consisting of linearly independent rows)
 * and reducing that instead.
 */
public final class LLL {

  private static final BigInteger TWO = BigInteger.valueOf(2);

  /** Numerator of the default reduction parameter <code>delta = 3/4</code>. */
  public static final int DEFAULT_DELTA_NUMERATOR = 3;

  /** Denominator of the default reduction parameter <code>delta = 3/4</code>. */
  public static final int DEFAULT_DELTA_DENOMINATOR = 4;

  private LLL() {}

  /**
   * Reduce the lattice basis given by the rows of <code>rows</code> with the standard reduction
   * parameter <code>delta = 3/4</code>.
   *
   * @param rows the basis vectors; the array isn't modified
   * @return the reduced basis vectors; zero rows and rows which are linearly dependent on the
   *         others are removed, so the result can contain fewer rows than the input
   */
  public static BigInteger[][] reduce(BigInteger[][] rows) {
    return reduce(rows, DEFAULT_DELTA_NUMERATOR, DEFAULT_DELTA_DENOMINATOR);
  }

  /**
   * Reduce the lattice basis given by the rows of <code>rows</code>.
   *
   * @param rows the basis vectors; the array isn't modified
   * @param deltaNumerator numerator of the reduction parameter <code>delta</code>
   * @param deltaDenominator denominator of the reduction parameter <code>delta</code>; the quotient
   *        must fulfill <code>1/4 &lt; delta &lt; 1</code>
   * @return the reduced basis vectors; zero rows and rows which are linearly dependent on the
   *         others are removed, so the result can contain fewer rows than the input
   * @throws IllegalArgumentException if the rows don't all have the same length or if
   *         <code>delta</code> is out of range
   */
  public static BigInteger[][] reduce(BigInteger[][] rows, int deltaNumerator,
      int deltaDenominator) {
    if (deltaDenominator <= 0 || 4L * deltaNumerator <= deltaDenominator
        || deltaNumerator >= deltaDenominator) {
      throw new IllegalArgumentException("LLL: delta must fulfill 1/4 < delta < 1");
    }
    BigInteger[][] basis = copyNonZeroRows(rows);
    if (basis.length == 0) {
      return basis;
    }
    BigInteger num = BigInteger.valueOf(deltaNumerator);
    BigInteger den = BigInteger.valueOf(deltaDenominator);
    if (reduceIndependent(basis, num, den)) {
      return basis;
    }
    // the rows are linearly dependent over the rationals; the non-zero rows of the row Hermite
    // normal form are a basis of the same lattice and are linearly independent
    basis = copyNonZeroRows(hermiteNormalForm(basis));
    if (basis.length == 0) {
      return basis;
    }
    if (!reduceIndependent(basis, num, den)) {
      throw new IllegalStateException("LLL: Hermite normal form returned dependent rows");
    }
    return basis;
  }

  /**
   * The squared euclidean norm of a vector.
   *
   * @param vector
   * @return
   */
  public static BigInteger normSquared(BigInteger[] vector) {
    BigInteger result = BigInteger.ZERO;
    for (int i = 0; i < vector.length; i++) {
      result = result.add(vector[i].multiply(vector[i]));
    }
    return result;
  }

  /**
   * Copy the matrix and drop all rows which consist only of zeros.
   *
   * @param rows
   * @return
   * @throws IllegalArgumentException if the rows don't all have the same length
   */
  private static BigInteger[][] copyNonZeroRows(BigInteger[][] rows) {
    if (rows.length == 0) {
      return new BigInteger[0][];
    }
    int cols = rows[0].length;
    int count = 0;
    for (int i = 0; i < rows.length; i++) {
      if (rows[i].length != cols) {
        throw new IllegalArgumentException("LLL: all rows must have the same length");
      }
      if (!isZeroRow(rows[i])) {
        count++;
      }
    }
    BigInteger[][] result = new BigInteger[count][];
    int index = 0;
    for (int i = 0; i < rows.length; i++) {
      if (!isZeroRow(rows[i])) {
        result[index++] = rows[i].clone();
      }
    }
    return result;
  }

  private static boolean isZeroRow(BigInteger[] row) {
    for (int i = 0; i < row.length; i++) {
      if (row[i].signum() != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Cohen, Algorithm 2.6.7. Reduces <code>b</code> in-place. The rows are indexed
   * <code>1..n</code> internally, therefore all helper arrays have one additional entry.
   *
   * @param b the basis vectors, modified in-place
   * @param num numerator of <code>delta</code>
   * @param den denominator of <code>delta</code>
   * @return <code>false</code> if the rows turned out to be linearly dependent over the rationals;
   *         <code>b</code> is then left in a partially reduced state and must be discarded
   */
  private static boolean reduceIndependent(BigInteger[][] b, BigInteger num, BigInteger den) {
    final int n = b.length;
    BigInteger[] d = new BigInteger[n + 2];
    BigInteger[][] lambda = new BigInteger[n + 2][n + 2];
    for (int i = 0; i <= n + 1; i++) {
      for (int j = 0; j <= n + 1; j++) {
        lambda[i][j] = BigInteger.ZERO;
      }
      d[i] = BigInteger.ZERO;
    }

    d[0] = BigInteger.ONE;
    d[1] = dotProduct(b[0], b[0]);
    if (d[1].signum() == 0) {
      return false;
    }

    int k = 2;
    int kmax = 1;
    while (k <= n) {
      if (k > kmax) {
        // incremental Gram-Schmidt for row k
        kmax = k;
        for (int j = 1; j <= k; j++) {
          BigInteger u = dotProduct(b[k - 1], b[j - 1]);
          for (int i = 1; i <= j - 1; i++) {
            // this division is exact
            u = d[i].multiply(u).subtract(lambda[k][i].multiply(lambda[j][i])).divide(d[i - 1]);
          }
          if (j < k) {
            lambda[k][j] = u;
          } else {
            if (u.signum() == 0) {
              return false;
            }
            d[k] = u;
          }
        }
      }

      while (true) {
        red(b, d, lambda, k, k - 1);
        // Lovasz condition: d[k]*d[k-2] < delta*d[k-1]^2 - lambda[k][k-1]^2
        BigInteger left = den.multiply(d[k]).multiply(d[k - 2]);
        BigInteger right = num.multiply(d[k - 1]).multiply(d[k - 1])
            .subtract(den.multiply(lambda[k][k - 1]).multiply(lambda[k][k - 1]));
        if (left.compareTo(right) < 0) {
          swap(b, d, lambda, k, kmax);
          k = Math.max(2, k - 1);
        } else {
          break;
        }
      }

      for (int l = k - 2; l >= 1; l--) {
        red(b, d, lambda, k, l);
      }
      k++;
    }
    return true;
  }

  /** Cohen's subalgorithm RED(k, l): size reduce row <code>k</code> against row <code>l</code>. */
  private static void red(BigInteger[][] b, BigInteger[] d, BigInteger[][] lambda, int k, int l) {
    BigInteger lkl = lambda[k][l];
    if (lkl.abs().multiply(TWO).compareTo(d[l]) <= 0) {
      return;
    }
    // q = nearest integer to lambda[k][l] / d[l]; d[l] is always positive
    BigInteger q = floorDivide(lkl.multiply(TWO).add(d[l]), d[l].multiply(TWO));
    if (q.signum() == 0) {
      return;
    }
    BigInteger[] bk = b[k - 1];
    BigInteger[] bl = b[l - 1];
    for (int i = 0; i < bk.length; i++) {
      bk[i] = bk[i].subtract(q.multiply(bl[i]));
    }
    lambda[k][l] = lkl.subtract(q.multiply(d[l]));
    for (int i = 1; i <= l - 1; i++) {
      lambda[k][i] = lambda[k][i].subtract(q.multiply(lambda[l][i]));
    }
  }

  /** Cohen's subalgorithm SWAP(k): exchange the rows <code>k</code> and <code>k-1</code>. */
  private static void swap(BigInteger[][] b, BigInteger[] d, BigInteger[][] lambda, int k,
      int kmax) {
    BigInteger[] temp = b[k - 1];
    b[k - 1] = b[k - 2];
    b[k - 2] = temp;

    for (int j = 1; j <= k - 2; j++) {
      BigInteger t = lambda[k][j];
      lambda[k][j] = lambda[k - 1][j];
      lambda[k - 1][j] = t;
    }

    BigInteger lam = lambda[k][k - 1];
    // both divisions are exact
    BigInteger bNew = d[k - 2].multiply(d[k]).add(lam.multiply(lam)).divide(d[k - 1]);
    for (int i = k + 1; i <= kmax; i++) {
      BigInteger t = lambda[i][k];
      lambda[i][k] =
          d[k].multiply(lambda[i][k - 1]).subtract(lam.multiply(t)).divide(d[k - 1]);
      lambda[i][k - 1] = bNew.multiply(t).add(lam.multiply(lambda[i][k])).divide(d[k]);
    }
    d[k - 1] = bNew;
  }

  private static BigInteger dotProduct(BigInteger[] u, BigInteger[] v) {
    BigInteger result = BigInteger.ZERO;
    for (int i = 0; i < u.length; i++) {
      result = result.add(u[i].multiply(v[i]));
    }
    return result;
  }

  /**
   * Integer division rounding towards negative infinity.
   *
   * @param a
   * @param b must be positive
   * @return
   */
  private static BigInteger floorDivide(BigInteger a, BigInteger b) {
    BigInteger[] qr = a.divideAndRemainder(b);
    if (qr[1].signum() < 0) {
      return qr[0].subtract(BigInteger.ONE);
    }
    return qr[0];
  }

  /**
   * The row Hermite normal form of the given matrix. Rows which became zero are moved to the end,
   * so the leading rows are a basis of the row lattice of <code>matrix</code>.
   *
   * @param matrix the array isn't modified
   * @return
   */
  public static BigInteger[][] hermiteNormalForm(BigInteger[][] matrix) {
    return org.matheclipse.core.reflection.system.HermiteDecomposition.hermiteNormalForm(matrix);
  }
}
