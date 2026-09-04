package org.matheclipse.core.eval.util;

import java.util.HashMap;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Determinant and adjugate of a square matrix with symbolic entries, computed by a memoized
 * <a href="https://en.wikipedia.org/wiki/Laplace_expansion">Laplace (cofactor) expansion</a>.
 *
 * <p>
 * The expansion is <b>division free</b>: every intermediate value is a sum of products of the
 * original matrix entries. That is the decisive property for symbolic matrices. Elimination based
 * methods have to divide by a pivot, and for an entry like <code>Sin(x)</code> or
 * <code>-1+Sqrt(6)</code> that division is not exact in any ring Symja can compute in, so the
 * quotient is either left as a nested fraction or pushed through <code>Cancel</code>, which
 * introduces <code>Cot</code>/<code>Csc</code> terms and spurious denominators.
 *
 * <p>
 * Sub-determinants are keyed by the pair of row/column bit masks that select the sub-matrix, so a
 * minor shared by several cofactors is evaluated once. A determinant therefore costs
 * <code>O(2^n * n)</code> evaluations instead of the <code>O(n!)</code> of a naive expansion, and
 * the whole adjugate reuses the same table.
 *
 * <p>
 * Intermediate sums are evaluated but deliberately <b>not</b> expanded. Symja's orderless pair
 * matcher rewrites <code>Sin(u)^2+Cos(u)^2</code> to <code>1</code> only while that two term shape
 * is intact; expanding first would distribute the factors and destroy it. Callers apply
 * {@link F#Expand(IExpr)} once to the finished result, which keeps a collapse that already
 * happened and canonicalises everything else.
 */
public class SymbolicDeterminant {

  /**
   * Maximum number of distinct sub-determinants that will be evaluated. Reaching it aborts the
   * expansion so the caller can fall back to another algorithm instead of running out of memory.
   */
  private static final int MAX_MINORS = 200000;

  /** Bit masks are held in an <code>int</code>, so this is the largest supported dimension. */
  private static final int MAX_DIMENSION = 30;

  /** Thrown when {@link #MAX_MINORS} is exceeded. */
  @SuppressWarnings("serial")
  private static final class TooManyMinorsException extends RuntimeException {
    static final TooManyMinorsException INSTANCE = new TooManyMinorsException();

    private TooManyMinorsException() {
      super(null, null, false, false);
    }
  }

  private final IExpr[][] matrix;
  private final int dimension;
  private final EvalEngine engine;
  private final HashMap<Long, IExpr> minorCache = new HashMap<Long, IExpr>();

  private SymbolicDeterminant(IExpr[][] matrix, EvalEngine engine) {
    this.matrix = matrix;
    this.dimension = matrix.length;
    this.engine = engine;
  }

  /**
   * Create an expansion for the given square matrix.
   *
   * @param matrix a square matrix
   * @param engine the evaluation engine
   * @return <code>null</code> if the dimension is not supported
   */
  public static SymbolicDeterminant create(final FieldMatrix<IExpr> matrix, EvalEngine engine) {
    final int n = matrix.getRowDimension();
    if (n < 1 || n > MAX_DIMENSION || n != matrix.getColumnDimension()) {
      return null;
    }
    final IExpr[][] entries = new IExpr[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        entries[i][j] = matrix.getEntry(i, j);
      }
    }
    return new SymbolicDeterminant(entries, engine);
  }

  /**
   * The determinant of the whole matrix, unexpanded.
   *
   * @return {@link F#NIL} if the expansion grew past {@link #MAX_MINORS}
   */
  public IExpr determinant() {
    final int full = fullMask();
    try {
      return minor(full, full);
    } catch (TooManyMinorsException tmme) {
      return F.NIL;
    }
  }

  /**
   * The adjugate (transposed cofactor matrix), every entry unexpanded.
   *
   * @return <code>null</code> if the expansion grew past {@link #MAX_MINORS}
   */
  public IExpr[][] adjugate() {
    final int n = dimension;
    final IExpr[][] adjugate = new IExpr[n][n];
    if (n == 1) {
      adjugate[0][0] = F.C1;
      return adjugate;
    }
    final int full = fullMask();
    try {
      for (int i = 0; i < n; i++) {
        for (int j = 0; j < n; j++) {
          // cofactor C[i][j] = (-1)^(i+j) * minor(remove row i and column j);
          // the adjugate is the transpose of the cofactor matrix
          final IExpr m = minor(full & ~(1 << i), full & ~(1 << j));
          adjugate[j][i] = ((i + j) & 1) == 0 ? m : engine.evaluate(F.Times(F.CN1, m));
        }
      }
    } catch (TooManyMinorsException tmme) {
      return null;
    }
    return adjugate;
  }

  private int fullMask() {
    return (1 << dimension) - 1;
  }

  /**
   * Determinant of the sub-matrix selected by the given row and column masks, which must have the
   * same number of bits set. Expansion is along the first selected row.
   */
  private IExpr minor(int rowMask, int colMask) {
    if (rowMask == 0) {
      // the empty determinant
      return F.C1;
    }
    final Long key = Long.valueOf(((long) rowMask << 32) | (colMask & 0xffffffffL));
    final IExpr cached = minorCache.get(key);
    if (cached != null) {
      return cached;
    }
    if (minorCache.size() >= MAX_MINORS) {
      throw TooManyMinorsException.INSTANCE;
    }
    final int row = Integer.numberOfTrailingZeros(rowMask);
    final int remainingRows = rowMask & ~(1 << row);
    final IASTAppendable sum = F.PlusAlloc(Integer.bitCount(colMask));
    boolean positive = true;
    for (int col = 0; col < dimension; col++) {
      if ((colMask & (1 << col)) == 0) {
        continue;
      }
      final IExpr entry = matrix[row][col];
      if (!entry.isZero()) {
        final IExpr sub = minor(remainingRows, colMask & ~(1 << col));
        if (!sub.isZero()) {
          sum.append(positive ? F.Times(entry, sub) : F.Times(F.CN1, entry, sub));
        }
      }
      positive = !positive;
    }
    // evaluate, but do not expand: an evaluated Plus lets the orderless pair matcher collapse
    // shapes like Sin(u)^2+Cos(u)^2 that expanding would destroy
    final IExpr result = engine.evaluate(sum);
    minorCache.put(key, result);
    return result;
  }
}
