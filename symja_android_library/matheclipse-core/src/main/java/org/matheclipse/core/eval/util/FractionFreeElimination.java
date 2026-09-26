package org.matheclipse.core.eval.util;

import java.math.BigInteger;
import java.util.function.Predicate;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * Fraction-free (<a href="https://en.wikipedia.org/wiki/Bareiss_algorithm">Bareiss</a>)
 * elimination, the forward pass for determinants and the Gauss-Jordan pass for row reduction,
 * inverse and adjugate.
 *
 * <p>
 * Every entry stays a minor of the matrix, so each step <code>(a*p - b*c)/previousPivot</code>
 * divides exactly. The passes run over a {@link Domain}:
 *
 * <ul>
 * <li>{@link #INTEGERS} for a matrix with rational entries and {@link #GAUSSIAN_INTEGERS} for one
 * with Gaussian rational entries: each row is first multiplied by the least common multiple of its
 * denominators, and no <code>gcd</code> is taken until the result is normalized once per entry. The
 * elimination over <code>IExpr</code> fractions takes a <code>gcd</code> in every operation, which
 * made a 120x120 integer <code>RowReduce</code> take seconds.
 * <li>{@link #expressions(Predicate, EvalEngine)} for a symbolic matrix: the entries stay
 * polynomial, <code>Together</code> performs the exact divisions.
 * </ul>
 */
public final class FractionFreeElimination {

  /** The integral domain in which the elimination runs. */
  interface Domain<T> {
    T zero();

    T one();

    boolean isZero(T a);

    /**
     * <code>(entry*pivot - factor*pivotRowEntry) / previousPivot</code>, where the division is
     * exact.
     */
    T eliminate(T entry, T pivot, T factor, T pivotRowEntry, T previousPivot);
  }

  /**
   * The exact numbers of a {@link Domain}: the conversion of a matrix, with the denominators of
   * each row cleared, and the quotients which normalize the result.
   */
  interface ExactNumbers<T> extends Domain<T> {
    /**
     * The rows of <code>matrix</code> with the denominators of each row cleared, or
     * <code>null</code> if an entry isn't a number of this domain.
     *
     * @param scales if not <code>null</code>, receives the factor each row was multiplied with
     */
    T[][] rows(FieldMatrix<IExpr> matrix, BigInteger[] scales);

    T negate(T a);

    /** <code>a / b</code> as a normalized number */
    IExpr quotient(T a, T b);

    /** <code>a / b</code> as a normalized number */
    IExpr quotient(T a, BigInteger b);
  }

  static final ExactNumbers<BigInteger> INTEGERS = new ExactNumbers<BigInteger>() {
    @Override
    public BigInteger zero() {
      return BigInteger.ZERO;
    }

    @Override
    public BigInteger one() {
      return BigInteger.ONE;
    }

    @Override
    public boolean isZero(BigInteger a) {
      return a.signum() == 0;
    }

    @Override
    public BigInteger eliminate(BigInteger entry, BigInteger pivot, BigInteger factor,
        BigInteger pivotRowEntry, BigInteger previousPivot) {
      BigInteger value = entry.multiply(pivot);
      if (factor.signum() != 0 && pivotRowEntry.signum() != 0) {
        value = value.subtract(factor.multiply(pivotRowEntry));
      }
      return previousPivot.equals(BigInteger.ONE) ? value : value.divide(previousPivot);
    }

    @Override
    public BigInteger[][] rows(FieldMatrix<IExpr> matrix, BigInteger[] scales) {
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
          lcm = lcm(lcm, ((IRational) entry).toBigDenominator());
        }
        for (int j = 0; j < cols; j++) {
          m[i][j] = scaled((IRational) matrix.getEntry(i, j), lcm);
        }
        if (scales != null) {
          scales[i] = lcm;
        }
      }
      return m;
    }

    @Override
    public BigInteger negate(BigInteger a) {
      return a.negate();
    }

    @Override
    public IExpr quotient(BigInteger a, BigInteger b) {
      return F.QQ(a, b).normalize();
    }
  };

  /** A Gaussian integer <code>re + im*I</code>. */
  static final class GaussianInteger {
    static final GaussianInteger ZERO = new GaussianInteger(BigInteger.ZERO, BigInteger.ZERO);
    static final GaussianInteger ONE = new GaussianInteger(BigInteger.ONE, BigInteger.ZERO);

    final BigInteger re;
    final BigInteger im;

    GaussianInteger(BigInteger re, BigInteger im) {
      this.re = re;
      this.im = im;
    }

    boolean isZero() {
      return re.signum() == 0 && im.signum() == 0;
    }

    boolean isOne() {
      return im.signum() == 0 && re.equals(BigInteger.ONE);
    }

    GaussianInteger multiply(GaussianInteger b) {
      if (isZero() || b.isZero()) {
        return ZERO;
      }
      if (im.signum() == 0 && b.im.signum() == 0) {
        return new GaussianInteger(re.multiply(b.re), BigInteger.ZERO);
      }
      return new GaussianInteger(re.multiply(b.re).subtract(im.multiply(b.im)),
          re.multiply(b.im).add(im.multiply(b.re)));
    }

    GaussianInteger subtract(GaussianInteger b) {
      return new GaussianInteger(re.subtract(b.re), im.subtract(b.im));
    }

    GaussianInteger negate() {
      return new GaussianInteger(re.negate(), im.negate());
    }

    GaussianInteger conjugate() {
      return new GaussianInteger(re, im.negate());
    }

    BigInteger norm() {
      return re.multiply(re).add(im.multiply(im));
    }

    /** The quotient <code>this / b</code>, which has to be exact. */
    GaussianInteger divide(GaussianInteger b) {
      if (b.im.signum() == 0) {
        return new GaussianInteger(re.divide(b.re), im.divide(b.re));
      }
      final GaussianInteger w = multiply(b.conjugate());
      final BigInteger norm = b.norm();
      return new GaussianInteger(w.re.divide(norm), w.im.divide(norm));
    }
  }

  static final ExactNumbers<GaussianInteger> GAUSSIAN_INTEGERS =
      new ExactNumbers<GaussianInteger>() {
        @Override
        public GaussianInteger zero() {
          return GaussianInteger.ZERO;
        }

        @Override
        public GaussianInteger one() {
          return GaussianInteger.ONE;
        }

        @Override
        public boolean isZero(GaussianInteger a) {
          return a.isZero();
        }

        @Override
        public GaussianInteger eliminate(GaussianInteger entry, GaussianInteger pivot,
            GaussianInteger factor, GaussianInteger pivotRowEntry, GaussianInteger previousPivot) {
          GaussianInteger value = entry.multiply(pivot);
          if (!factor.isZero() && !pivotRowEntry.isZero()) {
            value = value.subtract(factor.multiply(pivotRowEntry));
          }
          return previousPivot.isOne() ? value : value.divide(previousPivot);
        }

        @Override
        public GaussianInteger[][] rows(FieldMatrix<IExpr> matrix, BigInteger[] scales) {
          final int rows = matrix.getRowDimension();
          final int cols = matrix.getColumnDimension();
          final GaussianInteger[][] m = new GaussianInteger[rows][cols];
          for (int i = 0; i < rows; i++) {
            BigInteger lcm = BigInteger.ONE;
            for (int j = 0; j < cols; j++) {
              final IExpr entry = matrix.getEntry(i, j);
              if (entry.isRational()) {
                lcm = lcm(lcm, ((IRational) entry).toBigDenominator());
              } else if (entry instanceof IComplex) {
                lcm = lcm(lcm, ((IComplex) entry).re().toBigDenominator());
                lcm = lcm(lcm, ((IComplex) entry).im().toBigDenominator());
              } else {
                return null;
              }
            }
            for (int j = 0; j < cols; j++) {
              final IExpr entry = matrix.getEntry(i, j);
              m[i][j] = entry.isRational()
                  ? new GaussianInteger(scaled((IRational) entry, lcm), BigInteger.ZERO)
                  : new GaussianInteger(scaled(((IComplex) entry).re(), lcm),
                      scaled(((IComplex) entry).im(), lcm));
            }
            if (scales != null) {
              scales[i] = lcm;
            }
          }
          return m;
        }

        @Override
        public GaussianInteger negate(GaussianInteger a) {
          return a.negate();
        }

        @Override
        public IExpr quotient(GaussianInteger a, GaussianInteger b) {
          final GaussianInteger w = a.multiply(b.conjugate());
          return complex(w.re, w.im, b.norm());
        }

        @Override
        public IExpr quotient(GaussianInteger a, BigInteger b) {
          return complex(a.re, a.im, b);
        }

        private IExpr complex(BigInteger re, BigInteger im, BigInteger denominator) {
          final IRational real = F.QQ(re, denominator).normalize();
          if (im.signum() == 0) {
            return real;
          }
          return F.CC(real, F.QQ(im, denominator).normalize());
        }
      };

  private static BigInteger lcm(BigInteger a, BigInteger b) {
    if (b.equals(BigInteger.ONE)) {
      return a;
    }
    return a.divide(a.gcd(b)).multiply(b);
  }

  /** <code>value * scale</code> for a <code>scale</code> the denominator of value divides. */
  private static BigInteger scaled(IRational value, BigInteger scale) {
    final BigInteger numerator = value.toBigNumerator();
    return scale.equals(BigInteger.ONE) ? numerator
        : numerator.multiply(scale.divide(value.toBigDenominator()));
  }

  static Domain<IExpr> expressions(Predicate<IExpr> zeroChecker, EvalEngine engine) {
    return new Domain<IExpr>() {
      @Override
      public IExpr zero() {
        return F.C0;
      }

      @Override
      public IExpr one() {
        return F.C1;
      }

      @Override
      public boolean isZero(IExpr a) {
        return zeroChecker.test(a);
      }

      @Override
      public IExpr eliminate(IExpr entry, IExpr pivot, IExpr factor, IExpr pivotRowEntry,
          IExpr previousPivot) {
        IExpr numerator = engine
            .evaluate(F.Expand(F.Subtract(F.Times(entry, pivot), F.Times(factor, pivotRowEntry))));
        return exactDivide(numerator, previousPivot, engine);
      }
    };
  }

  /**
   * Divide <code>numerator</code> exactly by <code>denominator</code>. Division by a constant keeps
   * a polynomial polynomial; for a symbolic denominator <code>Together</code> reduces the (exact)
   * quotient to a polynomial. <code>Cancel</code> isn't enough: it can return the quotient as a sum
   * of fractions, and the next steps then build on those.
   */
  private static IExpr exactDivide(IExpr numerator, IExpr denominator, EvalEngine engine) {
    if (numerator.isZero()) {
      return F.C0;
    }
    if (denominator.isOne()) {
      return numerator;
    }
    if (denominator.isNumber()) {
      return engine.evaluate(F.Expand(numerator.times(denominator.inverse())));
    }
    return engine.evaluate(F.Expand(S.Together.of(engine, F.Divide(numerator, denominator))));
  }

  /** The pivots found by {@link #gaussJordan(Object[][], Domain)}. */
  private static final class Pivots<T> {
    final boolean[] column;
    int rank;
    boolean oddSwaps;
    /** after the pass, the common value of the pivots on the diagonal of every pivot row */
    T last;

    Pivots(int columns) {
      column = new boolean[columns];
    }
  }

  private FractionFreeElimination() {}

  /**
   * Forward elimination of the square matrix <code>m</code> in place. Afterwards
   * <code>m[n-1][n-1]</code> is the determinant times the returned sign.
   *
   * @return the sign <code>1</code> or <code>-1</code> of the row permutation, or <code>0</code> if
   *         a column has no pivot, which makes the determinant 0
   */
  private static <T> int forward(T[][] m, Domain<T> domain) {
    final int n = m.length;
    T previousPivot = domain.one();
    int sign = 1;
    for (int k = 0; k < n - 1; k++) {
      if (domain.isZero(m[k][k])) {
        int swapRow = -1;
        for (int i = k + 1; i < n; i++) {
          if (!domain.isZero(m[i][k])) {
            swapRow = i;
            break;
          }
        }
        if (swapRow < 0) {
          return 0;
        }
        final T[] swap = m[k];
        m[k] = m[swapRow];
        m[swapRow] = swap;
        sign = -sign;
      }
      final T pivot = m[k][k];
      for (int i = k + 1; i < n; i++) {
        final T[] row = m[i];
        for (int j = k + 1; j < n; j++) {
          row[j] = domain.eliminate(row[j], pivot, row[k], m[k][j], previousPivot);
        }
        row[k] = domain.zero();
      }
      previousPivot = pivot;
    }
    return sign;
  }

  /**
   * Fraction-free Gauss-Jordan elimination of <code>m</code> in place. Only the columns without a
   * pivot are updated: an earlier pivot column holds 0 in every other row and is never read again.
   * Afterwards every pivot row has {@link Pivots#last} on its diagonal, so dividing the row by it
   * gives the reduced row echelon form.
   */
  private static <T> Pivots<T> gaussJordan(T[][] m, Domain<T> domain) {
    final int rows = m.length;
    final int cols = rows == 0 ? 0 : m[0].length;
    final Pivots<T> pivots = new Pivots<>(cols);
    T previousPivot = domain.one();
    int rank = 0;
    for (int c = 0; c < cols && rank < rows; c++) {
      int pivotRow = -1;
      for (int i = rank; i < rows; i++) {
        if (!domain.isZero(m[i][c])) {
          pivotRow = i;
          break;
        }
      }
      if (pivotRow < 0) {
        continue;
      }
      if (pivotRow != rank) {
        final T[] swap = m[rank];
        m[rank] = m[pivotRow];
        m[pivotRow] = swap;
        pivots.oddSwaps = !pivots.oddSwaps;
      }
      final T[] pivotRowEntries = m[rank];
      final T pivot = pivotRowEntries[c];
      pivots.column[c] = true;
      for (int i = 0; i < rows; i++) {
        if (i == rank) {
          continue;
        }
        final T[] row = m[i];
        final T factor = row[c];
        for (int k = 0; k < cols; k++) {
          if (!pivots.column[k]) {
            row[k] = domain.eliminate(row[k], pivot, factor, pivotRowEntries[k], previousPivot);
          }
        }
        row[c] = domain.zero();
      }
      previousPivot = pivot;
      rank++;
    }
    pivots.rank = rank;
    pivots.last = previousPivot;
    return pivots;
  }

  private static IExpr[][] entries(FieldMatrix<IExpr> matrix, int extraColumns) {
    final int rows = matrix.getRowDimension();
    final int cols = matrix.getColumnDimension();
    final IExpr[][] m = new IExpr[rows][cols + extraColumns];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        m[i][j] = matrix.getEntry(i, j);
      }
    }
    return m;
  }

  /**
   * The reduced row echelon form of a matrix with rational or Gaussian rational entries, or
   * <code>null</code> for another matrix.
   */
  public static FieldMatrix<IExpr> rowReduce(FieldMatrix<IExpr> matrix) {
    if (matrix.getRowDimension() == 0 || matrix.getColumnDimension() == 0) {
      return null;
    }
    final FieldMatrix<IExpr> result = rowReduce(matrix, INTEGERS);
    return result != null ? result : rowReduce(matrix, GAUSSIAN_INTEGERS);
  }

  private static <T> FieldMatrix<IExpr> rowReduce(FieldMatrix<IExpr> matrix,
      ExactNumbers<T> numbers) {
    final int rows = matrix.getRowDimension();
    final int cols = matrix.getColumnDimension();
    final T[][] m = numbers.rows(matrix, null);
    if (m == null) {
      return null;
    }
    final Pivots<T> pivots = gaussJordan(m, numbers);
    final FieldMatrix<IExpr> result = matrix.createMatrix(rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int k = 0; k < cols; k++) {
        result.setEntry(i, k,
            i >= pivots.rank || pivots.column[k] ? F.C0 : numbers.quotient(m[i][k], pivots.last));
      }
    }
    int row = 0;
    for (int k = 0; k < cols && row < pivots.rank; k++) {
      if (pivots.column[k]) {
        result.setEntry(row++, k, F.C1);
      }
    }
    return result;
  }

  /**
   * The reduced row echelon form of a symbolic <code>matrix</code>
   * (<code>Method -> "DivisionFreeRowReduction"</code>). The rows below the rank are returned as
   * the elimination left them.
   */
  public static IASTAppendable rowReduce(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker,
      EvalEngine engine) {
    final IExpr[][] m = entries(matrix, 0);
    final int rows = m.length;
    final int cols = matrix.getColumnDimension();
    final Pivots<IExpr> pivots = gaussJordan(m, expressions(zeroChecker, engine));
    final IExpr pivot = pivots.last;
    int pivotCol = -1;
    for (int i = 0; i < pivots.rank; i++) {
      do {
        pivotCol++;
      } while (!pivots.column[pivotCol]);
      m[i][pivotCol] = F.C1;
      for (int j = 0; j < cols; j++) {
        if (j == pivotCol) {
          continue;
        }
        if (j < pivotCol || pivots.column[j] || zeroChecker.test(m[i][j])) {
          m[i][j] = F.C0;
        } else if (m[i][j].equals(pivot)) {
          m[i][j] = F.C1;
        } else {
          m[i][j] = engine.evaluate(S.Together.of(engine, F.Divide(m[i][j], pivot)));
        }
      }
    }
    final IASTAppendable result = F.ListAlloc(rows);
    for (int i = 0; i < rows; i++) {
      result.append(F.List(m[i]));
    }
    return result;
  }

  /**
   * The inverse of a square <code>matrix</code> with rational or Gaussian rational entries, or
   * <code>null</code> for another or a singular matrix.
   */
  public static FieldMatrix<IExpr> inverse(FieldMatrix<IExpr> matrix) {
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
   * The solution of <code>matrix . x == vector</code> for a square <code>matrix</code> and
   * <code>vector</code> with rational or Gaussian rational entries, or <code>null</code> for others
   * or a singular matrix.
   */
  public static IExpr[] solve(FieldMatrix<IExpr> matrix, IExpr[] vector) {
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
   * The determinant of a square <code>matrix</code> with rational or Gaussian rational entries, or
   * <code>null</code> for another matrix.
   */
  public static IExpr determinant(FieldMatrix<IExpr> matrix) {
    if (matrix.getRowDimension() == 0) {
      return null;
    }
    final IExpr det = determinant(matrix, INTEGERS);
    return det != null ? det : determinant(matrix, GAUSSIAN_INTEGERS);
  }

  private static <T> IExpr determinant(FieldMatrix<IExpr> matrix, ExactNumbers<T> numbers) {
    final int n = matrix.getRowDimension();
    final BigInteger[] scales = new BigInteger[n];
    final T[][] m = numbers.rows(matrix, scales);
    if (m == null) {
      return null;
    }
    final int sign = forward(m, numbers);
    if (sign == 0) {
      return F.C0;
    }
    final T det = m[n - 1][n - 1];
    BigInteger denominator = BigInteger.ONE;
    for (BigInteger scale : scales) {
      denominator = denominator.multiply(scale);
    }
    return numbers.quotient(sign < 0 ? numbers.negate(det) : det, denominator);
  }

  /** The determinant of a symbolic square <code>matrix</code>. */
  public static IExpr determinant(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker,
      EvalEngine engine) {
    final IExpr[][] m = entries(matrix, 0);
    final int sign = forward(m, expressions(zeroChecker, engine));
    if (sign == 0) {
      return F.C0;
    }
    final int n = m.length;
    final IExpr det = sign < 0 ? m[n - 1][n - 1].negate() : m[n - 1][n - 1];
    return engine.evaluate(F.Expand(det));
  }

  /**
   * The adjugate of a symbolic square <code>matrix</code>, polynomial for polynomial entries.
   *
   * <p>
   * For a nonsingular matrix the Gauss-Jordan pass over <code>[A | I]</code> leaves
   * <code>[D*I | D*Inverse(A)]</code> with <code>D = Det(P.A)</code> for the row permutation
   * <code>P</code>, so the right block is the adjugate up to the sign of <code>P</code>. A singular
   * matrix, or an elimination whose divisions <code>Cancel</code> couldn't reduce, takes the
   * cofactors, a determinant for every minor.
   */
  public static FieldMatrix<IExpr> adjugate(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker,
      EvalEngine engine) {
    final int n = matrix.getRowDimension();
    final FieldMatrix<IExpr> adjugate = matrix.copy();
    if (n == 1) {
      adjugate.setEntry(0, 0, F.C1);
      return adjugate;
    }
    final IExpr[][] m = entries(matrix, n);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        m[i][n + j] = i == j ? F.C1 : F.C0;
      }
    }
    final Pivots<IExpr> pivots = gaussJordan(m, expressions(zeroChecker, engine));
    // nonsingular: all pivots in the columns of A
    boolean eliminated = pivots.rank == n;
    for (int j = 0; eliminated && j < n; j++) {
      eliminated = pivots.column[j];
    }
    for (int i = 0; eliminated && i < n; i++) {
      for (int j = 0; j < n; j++) {
        final IExpr entry =
            engine.evaluate(F.Expand(pivots.oddSwaps ? m[i][n + j].negate() : m[i][n + j]));
        if (hasVariableDenominator(entry)) {
          eliminated = false;
          break;
        }
        adjugate.setEntry(i, j, entry);
      }
    }
    if (eliminated) {
      return adjugate;
    }
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        // cofactor C[i][j] = (-1)^(i+j) * det(minor removing row i and column j)
        final FieldMatrix<IExpr> minor = removeRowColumn(matrix, i, j);
        IExpr minorDet = LinearAlgebra.determinant(minor, zeroChecker);
        IExpr cofactor = ((i + j) & 1) == 0 ? minorDet : minorDet.negate();
        // the adjugate is the transpose of the cofactor matrix
        adjugate.setEntry(j, i, engine.evaluate(F.Expand(cofactor)));
      }
    }
    return adjugate;
  }

  /** Does <code>expr</code> divide by something which isn't a constant? */
  private static boolean hasVariableDenominator(IExpr expr) {
    return !expr.isFree(
        x -> x.isPower() && x.exponent().isNegativeResult() && !x.base().isNumericFunction(true),
        true);
  }

  /**
   * A copy of the square <code>matrix</code> without the given <code>row</code> and
   * <code>column</code>.
   */
  private static FieldMatrix<IExpr> removeRowColumn(final FieldMatrix<IExpr> matrix, int row,
      int column) {
    final int n = matrix.getRowDimension();
    final int[] selectedRows = new int[n - 1];
    final int[] selectedColumns = new int[n - 1];
    int r = 0;
    for (int i = 0; i < n; i++) {
      if (i != row) {
        selectedRows[r++] = i;
      }
    }
    int c = 0;
    for (int j = 0; j < n; j++) {
      if (j != column) {
        selectedColumns[c++] = j;
      }
    }
    return matrix.getSubMatrix(selectedRows, selectedColumns);
  }
}
