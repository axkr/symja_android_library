package org.matheclipse.core.builtin;

import java.math.BigInteger;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * Row reduction and determinant of a matrix with rational entries, computed fraction-free over
 * {@link BigInteger}.
 *
 * <p>
 * Each row is first multiplied by the least common multiple of its denominators, which changes
 * neither the reduced row echelon form nor the null space. Fraction-free Gauss-Jordan elimination
 * then keeps every entry an integer minor of the matrix, so each step divides exactly and no
 * <code>gcd</code> is ever taken. The elimination over <code>IExpr</code> fractions normalizes a
 * fraction in every one of its operations, which made a 120x120 integer <code>RowReduce</code>
 * take seconds.
 */
final class ExactRationalMatrix {

  private ExactRationalMatrix() {}

  /**
   * The integer rows of <code>matrix</code> with the denominators of each row cleared, or
   * <code>null</code> if an entry isn't rational.
   *
   * @param scales if not <code>null</code>, receives the factor each row was multiplied with
   */
  private static BigInteger[][] integerRows(FieldMatrix<IExpr> matrix, BigInteger[] scales) {
    final int rows = matrix.getRowDimension();
    final int cols = matrix.getColumnDimension();
    final BigInteger[][] m = new BigInteger[rows][cols];
    for (int i = 0; i < rows; i++) {
      BigInteger lcm = BigInteger.ONE;
      for (int j = 0; j < cols; j++) {
        final IExpr entry = matrix.getEntry(i, j);
        if (!entry.isRational()) {
          return null;
        }
        final BigInteger denominator = ((IRational) entry).toBigDenominator();
        if (!denominator.equals(BigInteger.ONE)) {
          lcm = lcm.divide(lcm.gcd(denominator)).multiply(denominator);
        }
      }
      for (int j = 0; j < cols; j++) {
        final IRational entry = (IRational) matrix.getEntry(i, j);
        final BigInteger numerator = entry.toBigNumerator();
        m[i][j] = lcm.equals(BigInteger.ONE) ? numerator
            : numerator.multiply(lcm.divide(entry.toBigDenominator()));
      }
      if (scales != null) {
        scales[i] = lcm;
      }
    }
    return m;
  }

  /**
   * The reduced row echelon form of <code>matrix</code>, or <code>null</code> if an entry isn't
   * rational.
   */
  static FieldMatrix<IExpr> rowReduce(FieldMatrix<IExpr> matrix) {
    final int rows = matrix.getRowDimension();
    final int cols = matrix.getColumnDimension();
    if (rows == 0 || cols == 0) {
      return null;
    }
    final BigInteger[][] m = integerRows(matrix, null);
    if (m == null) {
      return null;
    }
    final boolean[] pivotColumn = new boolean[cols];
    BigInteger previousPivot = BigInteger.ONE;
    int rank = 0;
    for (int c = 0; c < cols && rank < rows; c++) {
      int pivotRow = -1;
      for (int i = rank; i < rows; i++) {
        if (m[i][c].signum() != 0) {
          pivotRow = i;
          break;
        }
      }
      if (pivotRow < 0) {
        continue;
      }
      final BigInteger[] swap = m[rank];
      m[rank] = m[pivotRow];
      m[pivotRow] = swap;
      final BigInteger[] pivotRowEntries = m[rank];
      final BigInteger pivot = pivotRowEntries[c];
      pivotColumn[c] = true;
      for (int i = 0; i < rows; i++) {
        if (i == rank) {
          continue;
        }
        final BigInteger[] row = m[i];
        final BigInteger factor = row[c];
        // the entries of the earlier pivot columns are 0, or the common pivot on the diagonal,
        // and are never read again
        for (int k = 0; k < cols; k++) {
          if (!pivotColumn[k]) {
            BigInteger value = row[k].multiply(pivot);
            if (factor.signum() != 0 && pivotRowEntries[k].signum() != 0) {
              value = value.subtract(factor.multiply(pivotRowEntries[k]));
            }
            row[k] = value.divide(previousPivot);
          }
        }
        row[c] = BigInteger.ZERO;
      }
      previousPivot = pivot;
      rank++;
    }
    // every pivot row now has the last pivot on its diagonal
    final BigInteger divisor = previousPivot;
    final FieldMatrix<IExpr> result = matrix.createMatrix(rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int k = 0; k < cols; k++) {
        IExpr value;
        if (i >= rank) {
          value = F.C0;
        } else if (pivotColumn[k]) {
          value = F.C0;
        } else {
          value = F.QQ(m[i][k], divisor).normalize();
        }
        result.setEntry(i, k, value);
      }
    }
    // the pivots themselves
    int row = 0;
    for (int k = 0; k < cols && row < rank; k++) {
      if (pivotColumn[k]) {
        result.setEntry(row++, k, F.C1);
      }
    }
    return result;
  }

  /**
   * The inverse of the square <code>matrix</code>, or <code>null</code> if an entry isn't rational
   * or the matrix is singular.
   */
  static FieldMatrix<IExpr> inverse(FieldMatrix<IExpr> matrix) {
    final int n = matrix.getRowDimension();
    final FieldMatrix<IExpr> augmented = matrix.createMatrix(n, 2 * n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        augmented.setEntry(i, j, matrix.getEntry(i, j));
        augmented.setEntry(i, n + j, i == j ? F.C1 : F.C0);
      }
    }
    final FieldMatrix<IExpr> reduced = rowReduce(augmented);
    if (reduced == null || !reduced.getEntry(n - 1, n - 1).isOne()) {
      return null;
    }
    return reduced.getSubMatrix(0, n - 1, n, 2 * n - 1);
  }

  /**
   * The solution of <code>matrix . x == vector</code> for a square <code>matrix</code>, or
   * <code>null</code> if an entry isn't rational or the matrix is singular.
   */
  static IExpr[] solve(FieldMatrix<IExpr> matrix, IExpr[] vector) {
    final int n = matrix.getRowDimension();
    final FieldMatrix<IExpr> augmented = matrix.createMatrix(n, n + 1);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        augmented.setEntry(i, j, matrix.getEntry(i, j));
      }
      augmented.setEntry(i, n, vector[i]);
    }
    final FieldMatrix<IExpr> reduced = rowReduce(augmented);
    if (reduced == null || !reduced.getEntry(n - 1, n - 1).isOne()) {
      return null;
    }
    return reduced.getColumn(n);
  }

  /**
   * The determinant of the square <code>matrix</code>, or <code>null</code> if an entry isn't
   * rational.
   */
  static IRational determinant(FieldMatrix<IExpr> matrix) {
    final int n = matrix.getRowDimension();
    final BigInteger[] scales = new BigInteger[n];
    final BigInteger[][] m = integerRows(matrix, scales);
    if (m == null) {
      return null;
    }
    BigInteger previousPivot = BigInteger.ONE;
    boolean negate = false;
    for (int c = 0; c < n; c++) {
      int pivotRow = -1;
      for (int i = c; i < n; i++) {
        if (m[i][c].signum() != 0) {
          pivotRow = i;
          break;
        }
      }
      if (pivotRow < 0) {
        return F.C0;
      }
      if (pivotRow != c) {
        final BigInteger[] swap = m[c];
        m[c] = m[pivotRow];
        m[pivotRow] = swap;
        negate = !negate;
      }
      final BigInteger pivot = m[c][c];
      for (int i = c + 1; i < n; i++) {
        final BigInteger[] row = m[i];
        final BigInteger factor = row[c];
        for (int k = c + 1; k < n; k++) {
          BigInteger value = row[k].multiply(pivot);
          if (factor.signum() != 0 && m[c][k].signum() != 0) {
            value = value.subtract(factor.multiply(m[c][k]));
          }
          row[k] = value.divide(previousPivot);
        }
        row[c] = BigInteger.ZERO;
      }
      previousPivot = pivot;
    }
    BigInteger numerator = negate ? previousPivot.negate() : previousPivot;
    BigInteger denominator = BigInteger.ONE;
    for (BigInteger scale : scales) {
      denominator = denominator.multiply(scale);
    }
    return F.QQ(numerator, denominator).normalize();
  }
}
