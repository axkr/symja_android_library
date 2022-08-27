package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.ArcCos;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.Dot;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Map;
import static org.matheclipse.core.expression.F.MapThread;
import static org.matheclipse.core.expression.F.Most;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Norm;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Prepend;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hipparchus.complex.Complex;
import org.hipparchus.complex.ComplexField;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.linear.BlockFieldMatrix;
import org.hipparchus.linear.ComplexEigenDecomposition;
import org.hipparchus.linear.DecompositionSolver;
import org.hipparchus.linear.DependentVectorsHandler;
import org.hipparchus.linear.EigenDecomposition;
import org.hipparchus.linear.FieldDecompositionSolver;
import org.hipparchus.linear.FieldLUDecomposition;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldQRDecomposition;
import org.hipparchus.linear.FieldVector;
import org.hipparchus.linear.HessenbergTransformer;
import org.hipparchus.linear.MatrixUtils;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.linear.RiccatiEquationSolver;
import org.hipparchus.linear.RiccatiEquationSolverImpl;
import org.hipparchus.linear.SchurTransformer;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.eval.exception.LimitException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Matrix;
import org.matheclipse.core.eval.interfaces.AbstractNonOrderlessArgMultiple;
import org.matheclipse.core.eval.util.IndexFunctionDiagonal;
import org.matheclipse.core.eval.util.IndexTableGenerator;
import org.matheclipse.core.expression.ASTRealMatrix;
import org.matheclipse.core.expression.ASTRealVector;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.LinearSolveFunctionExpr;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.INumericArray;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

public final class LinearAlgebra {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.ArrayDepth.setEvaluator(new ArrayDepth());
      S.ArrayFlatten.setEvaluator(new ArrayFlatten());
      S.CharacteristicPolynomial.setEvaluator(new CharacteristicPolynomial());
      S.CholeskyDecomposition.setEvaluator(new CholeskyDecomposition());
      S.ConjugateTranspose.setEvaluator(new ConjugateTranspose());
      S.Cofactor.setEvaluator(new Cofactor());
      S.Cross.setEvaluator(new Cross());
      S.DesignMatrix.setEvaluator(new DesignMatrix());
      S.Det.setEvaluator(new Det());
      S.Diagonal.setEvaluator(new Diagonal());
      S.DiagonalMatrix.setEvaluator(new DiagonalMatrix());
      S.Dimensions.setEvaluator(new Dimensions());
      S.Dot.setEvaluator(new Dot());
      S.Eigenvalues.setEvaluator(new Eigenvalues());
      S.Eigenvectors.setEvaluator(new Eigenvectors());
      S.FourierMatrix.setEvaluator(new FourierMatrix());
      S.FromPolarCoordinates.setEvaluator(new FromPolarCoordinates());
      S.HessenbergDecomposition.setEvaluator(new HessenbergDecomposition());
      S.HilbertMatrix.setEvaluator(new HilbertMatrix());
      S.IdentityMatrix.setEvaluator(new IdentityMatrix());
      S.Inner.setEvaluator(new Inner());
      S.Inverse.setEvaluator(new Inverse());
      S.JacobiMatrix.setEvaluator(new JacobiMatrix());
      S.LeastSquares.setEvaluator(new LeastSquares());
      S.LinearSolve.setEvaluator(new LinearSolve());
      S.LinearSolveFunction.setEvaluator(new LinearSolveFunction());
      S.LowerTriangularize.setEvaluator(new LowerTriangularize());
      S.LUDecomposition.setEvaluator(new LUDecomposition());
      S.MatrixMinimalPolynomial.setEvaluator(new MatrixMinimalPolynomial());
      S.MatrixExp.setEvaluator(new MatrixExp());
      S.MatrixFunction.setEvaluator(new MatrixFunction());
      S.MatrixLog.setEvaluator(new MatrixLog());
      S.MatrixPower.setEvaluator(new MatrixPower());
      S.MatrixRank.setEvaluator(new MatrixRank());
      // S.Minor.setEvaluator(new Minor());
      S.Minors.setEvaluator(new Minors());
      S.Norm.setEvaluator(new Norm());
      S.Normalize.setEvaluator(new Normalize());
      S.NullSpace.setEvaluator(new NullSpace());
      S.Orthogonalize.setEvaluator(new Orthogonalize());
      S.PauliMatrix.setEvaluator(new PauliMatrix());
      S.PseudoInverse.setEvaluator(PseudoInverse.CONST);
      S.Projection.setEvaluator(new Projection());
      S.QRDecomposition.setEvaluator(new QRDecomposition());
      S.RiccatiSolve.setEvaluator(new RiccatiSolve());
      S.RowReduce.setEvaluator(new RowReduce());
      S.SchurDecomposition.setEvaluator(new SchurDecomposition());
      S.SingularValueDecomposition.setEvaluator(new SingularValueDecomposition());
      S.SingularValueList.setEvaluator(new SingularValueList());
      S.ToeplitzMatrix.setEvaluator(new ToeplitzMatrix());
      S.ToPolarCoordinates.setEvaluator(new ToPolarCoordinates());
      S.Tr.setEvaluator(new Tr());
      S.Transpose.setEvaluator(new Transpose());
      S.UpperTriangularize.setEvaluator(new UpperTriangularize());
      S.UnitVector.setEvaluator(new UnitVector());
      S.VandermondeMatrix.setEvaluator(new VandermondeMatrix());
      S.VectorAngle.setEvaluator(new VectorAngle());
    }
  }

  /**
   * Matrix class that wraps a <code>FieldMatrix&lt;T&gt;</code> matrix, which is transformed to
   * reduced row echelon format.
   *
   * <p>
   * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon form</a>.
   *
   * <p>
   * The code was adapted from:
   * <a href="http://rosettacode.org/wiki/Reduced_row_echelon_form#Java">Rosetta Code - Reduced row
   * echelon form</a>
   */
  private static final class FieldReducedRowEchelonForm {
    /** Wrapper for a row and column index. */
    private static class RowColIndex {
      int row;
      int col;

      RowColIndex(int r, int c) {
        row = r;
        col = c;
      }

      @Override
      public String toString() {
        return "(" + row + ", " + col + ")";
      }
    }

    private final FieldMatrix<IExpr> originalMatrix;
    private FieldMatrix<IExpr> rowReducedMatrix;
    private FieldMatrix<IExpr> nullSpaceCache;
    private int matrixRankCache;

    /** Number of rows. */
    private final int numRows;

    /** Number of columns. */
    private final int numCols;

    private final Predicate<IExpr> zeroChecker;

    /**
     * Constructor which creates row reduced echelon matrix from the given <code>
     * FieldMatrix&lt;T&gt;</code> matrix.
     *
     * <p>
     * <b>Note:</b> use {@link AbstractMatrix1Expr#POSSIBLE_ZEROQ_TEST} as <code>zeroChecker
     * </code>,to trigger the simple &quot;numeric&quot; rowReduce method.
     *
     * @param matrix matrix which will be transformed to a row reduced echelon matrix.
     * @zeroChecker check if an element is 0.
     * @see #rowReduceAdvancedZeroTest()
     */
    public FieldReducedRowEchelonForm(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker) {
      this.originalMatrix = matrix;
      this.zeroChecker = zeroChecker;
      this.numRows = matrix.getRowDimension();
      this.numCols = matrix.getColumnDimension();
      this.matrixRankCache = -1;
      this.nullSpaceCache = null;
      this.rowReducedMatrix = matrix.copy();
      if (zeroChecker instanceof AbstractMatrix1Expr.PossibleZeroQTest) {
        rowReduce();
      } else {
        rowReduceAdvancedZeroTest();
      }
    }

    /**
     * Create the &quot;reduced row echelon form&quot; of a matrix.
     *
     * <p>
     * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon
     * form</a>.
     *
     * @return
     */
    private void rowReduce() {
      int pivotRow = 0;
      int i;

      // number of rows and columns in matrix
      int numRows = rowReducedMatrix.getRowDimension();
      int numColumns = rowReducedMatrix.getColumnDimension();

      for (int k = 0; k < numRows; k++) {
        if (numColumns <= pivotRow) {
          break;
        }
        i = k;
        while (isZero(rowReducedMatrix.getEntry(i, pivotRow))) {
          i++;
          if (numRows == i) {
            i = k;
            pivotRow++;
            if (numColumns == pivotRow) {
              pivotRow--;
              break;
            }
          }
        }

        rowSwap(i, k);

        if (!isZero(rowReducedMatrix.getEntry(k, pivotRow))) {
          // Force pivot to be 1
          if (!isOne(rowReducedMatrix.getEntry(k, pivotRow))) {
            rowScale(k, (rowReducedMatrix.getEntry(k, pivotRow).inverse()));
          }
        }
        for (i = 0; i < numRows; i++) {
          if (i != k) {
            rowAddScale(k, i, rowReducedMatrix.getEntry(i, pivotRow).negate());
          }
        }
        pivotRow++;
      }

      EvalEngine.get().addTraceStep(() -> Convert.matrix2List(originalMatrix),
          () -> Convert.matrix2List(rowReducedMatrix),
          F.list(S.RowReduce, F.$str("ReducedRowEchelonForm")));
    }

    /**
     * Swap positions of 2 rows
     *
     * @param rowIndex1 first index of row to swap
     * @param rowIndex2 second index of row to swap
     */
    private void rowSwap(int rowIndex1, int rowIndex2) {
      if (rowIndex1 != rowIndex2) {
        // number of columns in matrix
        final int numColumns = rowReducedMatrix.getColumnDimension();

        for (int k = 0; k < numColumns; k++) {
          IExpr hold = rowReducedMatrix.getEntry(rowIndex2, k);
          rowReducedMatrix.setEntry(rowIndex2, k, rowReducedMatrix.getEntry(rowIndex1, k));
          rowReducedMatrix.setEntry(rowIndex1, k, hold);
        }
      }
    }

    /**
     * Multiplies a row by a scalar.
     *
     * @param rowIndex index of row to be scaled
     * @param scalar value to scale row by
     */
    private void rowScale(int rowIndex, IExpr scalar) {
      if (!isZero(scalar)) {
        // number of columns in matrix
        int numColumns = rowReducedMatrix.getColumnDimension();

        for (int k = 0; k < numColumns; k++) {
          rowReducedMatrix.setEntry(rowIndex, k,
              rowReducedMatrix.getEntry(rowIndex, k).multiply(scalar));
        }
      }
    }

    /**
     * Adds a row by the scalar of another row row2 = row2 + (row1 * scalar)
     *
     * @param rowIndex1 index of row to be added
     * @param rowIndex2 index or row that row1 is added to
     * @param scalar value to scale row by
     */
    private void rowAddScale(int rowIndex1, int rowIndex2, IExpr scalar) {
      if (!isZero(scalar)) {
        // number of columns in matrix
        int numColumns = rowReducedMatrix.getColumnDimension();

        for (int k = 0; k < numColumns; k++) {
          // matrix[rowIndex2][k] += (matrix[rowIndex1][k] * scalar);
          rowReducedMatrix.setEntry(rowIndex2, k, rowReducedMatrix.getEntry(rowIndex2, k)
              .add(rowReducedMatrix.getEntry(rowIndex1, k).multiply(scalar)));
        }
      }
    }

    /**
     * Test if <code>expr</code> equals the zero element.
     *
     * @param expr
     * @return
     */
    final boolean isZero(IExpr expr) {
      return zeroChecker.test(expr);
    }

    /**
     * Test if <code>expr</code> equals the one element.
     *
     * @param expr
     * @return
     */
    final boolean isOne(IExpr expr) {
      return zeroChecker.test(F.C1.subtract(expr));
    }

    private RowColIndex findPivot(RowColIndex a) {
      int first_row = a.row;
      RowColIndex pivot = new RowColIndex(a.row, a.col);
      RowColIndex current = new RowColIndex(a.row, a.col);

      for (int i = a.row; i < (numRows - first_row); i++) {
        current.row = i;
        if (isOne(rowReducedMatrix.getEntry(current.row, current.col))) {
          swapRow(current, a);
        }
      }

      current.row = a.row;
      for (int i = current.row; i < (numRows - first_row); i++) {
        current.row = i;
        if (!isZero(rowReducedMatrix.getEntry(current.row, current.col))) {
          pivot.row = i;
          break;
        }
      }

      return pivot;
    }

    private IExpr getCoordinate(RowColIndex a) {
      return rowReducedMatrix.getEntry(a.row, a.col);
    }

    /**
     * Get the row reduced echelon form of the matrix.
     *
     * <p>
     * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon
     * form</a>.
     *
     * @return
     */
    public FieldMatrix<IExpr> getRowReducedMatrix() {
      return rowReducedMatrix;
    }

    /**
     * Swap the rows <code>a.row</code> and <code>b.row</code> in the matrix and swap the <code>row
     * </code> values in the corresponding <code>RowColIndex</code> objects.
     *
     * @param a
     * @param b
     */
    private void swapRow(RowColIndex a, RowColIndex b) {
      IExpr[] temp = rowReducedMatrix.getRow(a.row);
      rowReducedMatrix.setRow(a.row, rowReducedMatrix.getRow(b.row));
      rowReducedMatrix.setRow(b.row, temp);

      int t = a.row;
      a.row = b.row;
      b.row = t;
    }

    /**
     * Test if the column <code>a.col</code> of the matrix contains only zero-elements starting with
     * the <code>a.row</code> element.
     *
     * @param a
     * @return
     */
    private boolean isColumnZeroFromRow(RowColIndex a) {
      for (int i = 0; i < numRows; i++) {
        if (!isZero(rowReducedMatrix.getEntry(i, a.col))) {
          return false;
        }
      }

      return true;
    }

    /**
     * Test if the <code>row</code> of the matrix contains only zero-elements.
     *
     * @param row
     * @return
     */
    private boolean isRowZeroes(int row) {
      IExpr[] temp = rowReducedMatrix.getRow(row);
      for (int i = 0; i < numCols; i++) {
        if (!isZero(temp[i])) {
          return false;
        }
      }

      return true;
    }

    /**
     * Add the values of the row <code>to.row</code> to the product of the values of the row <code>
     * from.row * factor</code> and assign the result values back to the row <code>to.row</code>.
     *
     * @param to
     * @param from
     * @param factor
     */
    private void multiplyAdd(RowColIndex to, RowColIndex from, IExpr factor) {
      IExpr[] row = rowReducedMatrix.getRow(to.row);
      IExpr[] rowMultiplied = rowReducedMatrix.getRow(from.row);

      for (int i = 0; i < numCols; i++) {
        rowReducedMatrix.setEntry(to.row, i, row[i].plus((rowMultiplied[i].times(factor))));
      }
    }

    /**
     * Get the nullspace of the row reduced matrix.
     *
     * <p>
     * See: <a href="http://en.wikipedia.org/wiki/Kernel_%28linear_algebra%29">Wikipedia - Kernel
     * (linear algebra)</a>. <a href="http://en.wikibooks.org/wiki/Linear_Algebra/Null_Spaces">
     * Wikibooks - Null Spaces</a>.
     *
     * @param minusOneFactor factor <code>-1</code> for multiplying all elements of the free part of
     *        the reduced row echelon form matrix
     * @return <code>null</code> if the input matrix has full rank, otherwise return the nullspaace.
     */
    public FieldMatrix<IExpr> getNullSpace(IExpr minusOneFactor) {
      int rank = getMatrixRank();
      int newRowDimension = rowReducedMatrix.getColumnDimension() - rank;
      if (newRowDimension == 0) {
        return null;
      }
      int newColumnDimension = rowReducedMatrix.getColumnDimension();
      if (nullSpaceCache != null) {
        return nullSpaceCache;
      }
      nullSpaceCache = rowReducedMatrix.createMatrix(newRowDimension, newColumnDimension);
      getResultOfNullspace(minusOneFactor, rank);
      return nullSpaceCache;
    }

    private void getResultOfNullspace(IExpr minusOneFactor, int rank) {
      // search free columns
      boolean[] columns = new boolean[nullSpaceCache.getColumnDimension()];
      int numberOfFreeColumns = 0;
      for (int i = 0; i < rank; i++) {
        if (!columns[i]) {
          for (int k = i; k < rowReducedMatrix.getColumnDimension(); k++) {
            if (isZero(rowReducedMatrix.getEntry(i, k))) {
              columns[k] = true;
              // free column
              int offset = 0;
              for (int j = 0; j < rank; j++) {
                if (columns[j]) {
                  offset++;
                }
                nullSpaceCache.setEntry(numberOfFreeColumns, j + offset,
                    rowReducedMatrix.getEntry(j, i));
              }
              numberOfFreeColumns++;
            } else {
              break;
            }
          }
        }
      }

      // Let's take the rest of the 'free part' of the reduced row echelon
      // form
      int start = rank + numberOfFreeColumns;
      int row = numberOfFreeColumns;
      for (int i = start; i < nullSpaceCache.getColumnDimension(); i++) {
        int offset = 0;
        for (int j = 0; j < rank; j++) {
          if (columns[j]) {
            offset++;
          }
          nullSpaceCache.setEntry(row, j + offset, rowReducedMatrix.getEntry(j, i));
        }
        row++;
      }
      for (int i = start; i < nullSpaceCache.getColumnDimension(); i++) {
        columns[i] = true;
      }

      // multiply matrix with scalar -1
      nullSpaceCache = nullSpaceCache.scalarMultiply(minusOneFactor);

      // append the 'one element' (typically as identity matrix)
      row = 0;
      for (int i = 0; i < columns.length; i++) {
        if (columns[i]) {
          nullSpaceCache.setEntry(row++, i, F.C1);
        }
      }
    }

    /**
     * Create the &quot;reduced row echelon form&quot; of a matrix with an advanced symbolic
     * ZeroTest
     *
     * <p>
     * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon
     * form</a>.
     *
     * @return
     */
    private void rowReduceAdvancedZeroTest() {
      RowColIndex pivot = new RowColIndex(0, 0);
      int submatrix = 0;
      for (int x = 0; x < numCols; x++) {
        pivot = new RowColIndex(pivot.row, x);
        // Step 1
        // Begin with the leftmost nonzero column. This is a pivot column.
        // The pivot position is at the top.
        for (int i = x; i < numCols; i++) {
          if (isColumnZeroFromRow(pivot) == false) {
            break;
          } else {
            pivot.col = i;
          }
        }
        // Step 2
        // Select a nonzero entry in the pivot column with the highest
        // absolute value as a pivot.
        pivot = findPivot(pivot);

        if (isZero(getCoordinate(pivot))) {
          pivot.row++;
          if (pivot.row + 1 > numRows) {
            pivot.row--;
            continue;
          }
        }

        // If necessary, interchange rows to move this entry into the pivot
        // position.
        // move this row to the top of the submatrix
        if (pivot.row != submatrix && !isRowZeroes(pivot.row)) {
          swapRow(new RowColIndex(submatrix, pivot.col), pivot);
        }

        if (!(isZero(getCoordinate(pivot)))) {
          // Force pivot to be 1
          if (!isOne(getCoordinate(pivot))) {
            IExpr scalar = getCoordinate(pivot).inverse();
            scaleRow(pivot, scalar);
          }
          // Step 3
          // Use row replacement operations to create zeroes in all positions
          // below the pivot.
          // belowPivot = belowPivot + (Pivot * -belowPivot)
          for (int i = pivot.row + 1; i < numRows; i++) {
            RowColIndex belowPivot = new RowColIndex(i, pivot.col);
            IExpr complement = getCoordinate(belowPivot).negate().divide(getCoordinate(pivot));
            multiplyAdd(belowPivot, pivot, complement);
          }
          // Step 5
          // Beginning with the rightmost pivot and working upward and to the
          // left, create zeroes above each pivot.
          // If a pivot is not 1, make it 1 by a scaling operation.
          // Use row replacement operations to create zeroes in all positions
          // above the pivot
          for (int i = pivot.row; i >= 0; i--) {
            if (i == pivot.row) {
              if (!isOne(getCoordinate(pivot))) {
                scaleRow(pivot, getCoordinate(pivot).inverse());
              }
              continue;
            }

            RowColIndex abovePivot = new RowColIndex(i, pivot.col);
            IExpr complement = getCoordinate(abovePivot).negate().divide(getCoordinate(pivot));
            multiplyAdd(abovePivot, pivot, complement);
          }
          submatrix++;
        }
        // Step 4
        // Ignore the row containing the pivot position and cover all rows,
        // if any, above it.
        // Apply steps 1-3 to the remaining submatrix. Repeat until there
        // are no more nonzero entries.
        if ((pivot.row + 1) >= numRows) { // || isRowZeroes(pivot.row + 1)) {
          break;
        }

        pivot.row++;
      }
      EvalEngine.get().addTraceStep(() -> Convert.matrix2List(originalMatrix),
          () -> Convert.matrix2List(rowReducedMatrix),
          F.list(S.RowReduce, F.$str("ReducedRowEchelonForm")));
    }

    /**
     * Get the rank of the row reduced matrix.
     *
     * <p>
     * See: <a href="http://en.wikipedia.org/wiki/Rank_%28linear_algebra%29">Wikipedia - Rank
     * (linear algebra)</a>.
     *
     * @return the rank of the matrix.
     */
    public int getMatrixRank() {
      if (rowReducedMatrix.getRowDimension() == 0 || rowReducedMatrix.getColumnDimension() == 0) {
        return 0;
      }
      if (matrixRankCache < 0) {
        matrixRankCache = 0;
        int rows = rowReducedMatrix.getRowDimension() - 1;
        for (int i = rows; i >= 0; i--) {
          if (!isRowZeroes(i)) {
            matrixRankCache = i + 1;
            return matrixRankCache;
          }
        }
      }
      return matrixRankCache;
    }

    /**
     * Multiply all <code>x.row</code> elements with the scalar <code>factor</code>.
     *
     * @param x
     * @param factor
     */
    private void scaleRow(RowColIndex x, IExpr factor) {
      for (int i = 0; i < numCols; i++) {
        rowReducedMatrix.multiplyEntry(x.row, i, factor);
      }
    }
  }

  /**
   *
   *
   * <pre>
   * ArrayDepth(a)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the depth of the non-ragged array <code>a</code>, defined as <code>
   * Length(Dimensions(a))</code>.<br>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ArrayDepth({{a,b},{c,d}})
   * 2
   *
   * &gt;&gt; ArrayDepth(x)
   * 0
   * </pre>
   */
  private static final class ArrayDepth extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        IAST list = (IAST) arg1;
        IExpr header = list.head();
        IntList dims = LinearAlgebra.dimensions(list, header);
        return F.ZZ(dims.size());
      }
      if (arg1.isSparseArray()) {
        int[] dims = ((ISparseArray) arg1).getDimension();
        return F.ZZ(dims.length);
      }
      if (arg1.isNumericArray()) {
        int[] dims = ((INumericArray) arg1).getDimension();
        return F.ZZ(dims.length);
      }
      return F.C0;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.AllowedHeads, S.Automatic)));
    }
  }

  private static final class ArrayFlatten extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int rank = 2;
      if (ast.isAST2()) {
        int r = ast.arg2().toIntDefault();
        if (r <= 0) {
          // Non-negative machine-sized integer expected at position `2` in `1`.
          return IOFunctions.printMessage(S.ArrayFlatten, "intnm", ast, engine);
        }
        rank = r;
      }
      if (rank != 2) {
        // Function `1` not implemented
        return IOFunctions.printMessage(S.ArrayFlatten, "zznotimpl",
            F.List(F.stringx("(rank != 2)")), engine);
      }
      boolean sparseArray = false;
      if (!arg1.isFree(x -> x.isSparseArray(), false)) {
        arg1 = arg1.normal(false);
        sparseArray = true;
      }
      if (arg1.isListOfLists() && arg1.argSize() > 0) {
        IAST list = (IAST) arg1;
        int rowSize = list.argSize();
        int columnSize = list.arg1().argSize();
        if (columnSize <= 0) {
          return F.NIL;
        } else {
          IntArrayList dimensions = LinearAlgebra.dimensions(list);
          if (dimensions.size() < rank) {
            // The array depth of the expression at position `1` of `2` must be at least equal
            // to the specified rank `3`.
            return IOFunctions.printMessage(S.ArrayFlatten, "depth", F.List(F.C1, list, F.ZZ(rank)),
                engine);
          }
          int[] rowDimensions = new int[rowSize];
          int[] columnDimensions = new int[columnSize];
          if (resultDimensions(list, rank, rowDimensions, columnDimensions, engine)) {
            IASTAppendable resultMatrix = F.ListAlloc();
            for (int i = 1; i < list.size(); i++) {
              IAST subList = (IAST) list.get(i);
              for (int l = 0; l < rowDimensions[i - 1]; l++) {
                IASTAppendable resultRow = F.ListAlloc();
                for (int j = 1; j < subList.size(); j++) {
                  IExpr element = subList.get(j);
                  boolean isScalar = true;
                  if (element.isList()) {
                    IntArrayList elementDimension = LinearAlgebra.dimensions((IAST) element);
                    if (elementDimension.size() >= rank) {
                      isScalar = false;
                    }
                  }
                  if (isScalar) {
                    for (int k = 0; k < columnDimensions[j - 1]; k++) {
                      resultRow.append(element);
                    }
                  } else {
                    IAST matrix = (IAST) element;
                    for (int k = 0; k < columnDimensions[j - 1]; k++) {
                      resultRow.append(matrix.getPart(l + 1, k + 1));
                    }
                  }

                }
                resultMatrix.append(resultRow);
              }
            }
            if (sparseArray) {
              return F.sparseArray(resultMatrix, F.C0);
            }
            return resultMatrix;
          }
        }
      }
      return F.NIL;
    }

    /**
     * Calculate the results dimensions for every <code>rowDimensions</code> and
     * <code>columnDimensions</code> entry.
     * 
     * @param list
     * @param rank
     * @param rowDimensions <code>1</code> if only scalars are in the i'th row; otherwise the row
     *        dimension, which must be the same for all non-scalars in this row
     * @param columnDimensions <code>1</code> if only scalars are in the i'th column; otherwise the
     *        column dimension, which must be the same for all non-scalars in this column
     * @param engine
     * @return <code>false</code> if the input is not appropriate to be transformed with
     *         <code>ArrayFlatten</code>
     */
    private boolean resultDimensions(IAST list, int rank, int[] rowDimensions,
        int[] columnDimensions, EvalEngine engine) {
      int columnSize = list.arg1().argSize();
      for (int i = 1; i < list.size(); i++) {
        IAST subList = (IAST) list.get(i);
        if (subList.argSize() != columnSize) {
          return false;
        }
        for (int j = 1; j < subList.size(); j++) {
          IExpr element = subList.get(j);
          if (element.isList()) {
            IntArrayList dimensions = LinearAlgebra.dimensions((IAST) element);
            if (dimensions.size() < rank) {
              // The array depth of the expression at position `1` of `2` must be at least equal
              // to the specified rank `3`.
              IOFunctions.printMessage(S.ArrayFlatten, "depth", F.List(F.C1, list, F.ZZ(rank)),
                  engine);
              return false;
            }
            if (rowDimensions[i - 1] != 0 && rowDimensions[i - 1] != dimensions.getInt(0)) {
              return false;
            }
            rowDimensions[i - 1] = dimensions.getInt(0);
            if (columnDimensions[j - 1] != 0 && columnDimensions[j - 1] != dimensions.getInt(1)) {
              return false;
            }
            columnDimensions[j - 1] = dimensions.getInt(1);
          }
        }
      }
      for (int i = 0; i < rowDimensions.length; i++) {
        if (rowDimensions[i] == 0) {
          rowDimensions[i] = 1;
        }
      }
      for (int i = 0; i < columnDimensions.length; i++) {
        if (columnDimensions[i] == 0) {
          columnDimensions[i] = 1;
        }
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }
  /**
   *
   *
   * <pre>
   * CharacteristicPolynomial(matrix, var)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the characteristic polynomial of a <code>matrix</code> for the variable <code>var
   * </code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href= "https://en.wikipedia.org/wiki/Characteristic_polynomial">Wikipedia -
   * Characteristic polynomial</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; CharacteristicPolynomial({{1, 2}, {42, 43}}, x)
   * -41-44*x+x^2
   * </pre>
   */
  private static final class CharacteristicPolynomial extends AbstractFunctionEvaluator {

    /**
     * Generate the characteristic polynomial of a square matrix.
     *
     * @param dim dimension of the square matrix
     * @param matrix the square matrix
     * @param variable the variable which should be used in the resulting characteristic polynomial
     * @return
     */
    public static IAST generateCharacteristicPolynomial(int dim, IAST matrix, IExpr variable) {
      final IExpr[] valuesForIdentityMatrix = {F.C0, variable};
      return F.Det(F.Subtract(matrix, diagonalMatrix(valuesForIdentityMatrix, dim)));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int[] dimensions = ast.arg1().isMatrix();
      if (dimensions != null && dimensions[0] == dimensions[1]) {
        // a matrix with square dimensions
        IAST matrix = (IAST) ast.arg1().normal(false);
        IExpr variable = ast.arg2();
        if (!variable.isVariable()) {
          // `1` is not a valid variable.
          return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(variable), engine);
        }
        return generateCharacteristicPolynomial(dimensions[0], matrix, variable);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * Calculates the Cholesky decomposition of a matrix.
   *
   * <p>
   * The Cholesky decomposition of a real symmetric positive-definite matrix A consists of a lower
   * triangular matrix L with same size such that: A = LL<sup>T</sup>. In a sense, this is the
   * square root of A.
   */
  private static final class CholeskyDecomposition extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        RealMatrix matrix = ast.arg1().toRealMatrix();
        if (matrix != null) {
          final org.hipparchus.linear.CholeskyDecomposition dcomposition =
              new org.hipparchus.linear.CholeskyDecomposition(matrix);
          // Returns the transpose of the matrix L of the decomposition.
          return new ASTRealMatrix(dcomposition.getLT(), false);
        }
      } catch (final ValidateException ve) {
        // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
        return IOFunctions.printMessage(ast.topHead(), ve, engine);
      } catch (final MathRuntimeException e) {
        // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
        LOGGER.log(engine.getLogLevel(), ast.topHead(), e);
      } catch (final IndexOutOfBoundsException e) {
        LOGGER.debug("CholeskyDecomposition.evaluate() failed", e);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * ConjugateTranspose(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the transposed <code>matrix</code> with conjugated matrix elements.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Transpose">Wikipedia - Transpose</a>
   * <li><a href="http://en.wikipedia.org/wiki/Complex_conjugation">Wikipedia - Complex
   * conjugation</a>
   * </ul>
   */
  private static final class ConjugateTranspose extends Transpose {

    @Override
    protected IExpr transform(final IExpr expr) {
      return expr.conjugate();
    }
  }

  private static final class Cofactor extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      int[] dims = arg1.isMatrix();
      if (dims != null && arg2.isList2()) {
        if (dims[0] == 0 || dims[1] == 0) {
          // TODO error message
          return F.NIL;
        }
        if (arg1.isSparseArray()) {
          arg1 = arg1.normal(false);
        }
        IAST matrix = (IAST) arg1;
        int i = arg2.first().toIntDefault();
        int j = arg2.second().toIntDefault();
        if (i <= 0 || j <= 0) {
          // TODO error message
          return F.NIL;
        }
        if (i > dims[0] || j > dims[1]) {
          // TODO error message
          return F.NIL;
        }
        if (i + j < 0) {
          // TODO overflow error message
          return F.NIL;
        }
        // (-1)^(i + j)*Det(Drop(matrix, {i}, {j}))
        return F.Times(F.Power(F.CN1, F.ZZ(i + j)), //
            F.Det( //
                F.Drop(matrix, F.list(F.ZZ(i)), F.list(F.ZZ(j))) //
            ));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Cross(a, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the vector cross product of <code>a</code> and <code>b</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Cross_product">Wikipedia: Cross product</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Cross({x1, y1, z1}, {x2, y2, z2})
   * {-y2*z1+y1*z2,x2*z1-x1*z2,-x2*y1+x1*y2}
   *
   * &gt;&gt; Cross({x, y})
   * {-y,x}
   * </pre>
   *
   * <p>
   * The arguments are expected to be vectors of equal length, and the number of arguments is
   * expected to be 1 less than their length.
   *
   * <pre>
   * &gt;&gt; Cross({1, 2}, {3, 4, 5})
   * Cross({1, 2}, {3, 4, 5})
   * </pre>
   */
  private static final class Cross extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        int dim1 = arg1.isVector();
        int dim2 = arg2.isVector();
        if (dim1 == 2 && dim2 == 2) {
          final IAST v1 = (IAST) arg1.normal(false);
          final IAST v2 = (IAST) arg2.normal(false);

          if ((v1.isAST2()) || (v2.isAST2())) {
            // Cross({a,b}, {c,d})", "a*d-b*c
            return F.Subtract(Times(v1.arg1(), v2.arg2()), Times(v1.arg2(), v2.arg1()));
          }
        } else if (dim1 == 3 && dim2 == 3) {
          final IAST v1 = (IAST) arg1.normal(false);
          final IAST v2 = (IAST) arg2.normal(false);

          if ((v1.isAST3()) || (v2.isAST3())) {
            return List(Plus(Times(v1.arg2(), v2.arg3()), Times(CN1, v1.arg3(), v2.arg2())),
                Plus(Times(v1.arg3(), v2.arg1()), Times(CN1, v1.arg1(), v2.arg3())),
                Plus(Times(v1.arg1(), v2.arg2()), Times(CN1, v1.arg2(), v2.arg1())));
          }
        }
      } else if (ast.isAST1()) {
        int dim1 = arg1.isVector();
        if (dim1 == 2) {
          final IAST v1 = (IAST) arg1.normal(false);
          return List(Negate(v1.arg2()), v1.arg1());
        }
        // The arguments are expected to be vectors of equal length, and the number of arguments is
        // expected to be 1 less than their length.
        return IOFunctions.printMessage(ast.topHead(), "nonn1", F.CEmptyList, engine);
      } else if (ast.size() > 3) {
        int dim1 = arg1.isVector();
        if (dim1 == ast.size()) {
          for (int i = 2; i < ast.size(); i++) {
            if (ast.get(i).isVector() != dim1) {
              return F.NIL;
            }
          }
          // TODO implement for more than 2 vector arguments
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   *
   *
   * <pre>
   * DesignMatrix(m, f, x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the design matrix.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, x, x)
   * {{1,2},{1,3},{1,5},{1,7}}
   *
   * &gt;&gt; DesignMatrix({{2, 1}, {3, 4}, {5, 3}, {7, 6}}, f(x), x)
   * {{1,f(2)},{1,f(3)},{1,f(5)},{1,f(7)}}
   * </pre>
   */
  private static class DesignMatrix extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr m = ast.arg1();
      IExpr f = ast.arg2();
      IExpr x = ast.arg3();
      if (f.isList()) {
        if (x.isAtom()) {
          // DesignMatrix(m_, f_List, x_?AtomQ) :=
          // DesignMatrix(m, {f}, ConstantArray(x, Length(f)))
          return F.DesignMatrix(m, F.list(f), F.ConstantArray(x, F.ZZ(((IAST) f).argSize())));
        } else if (x.isList()) {
          // DesignMatrix(m_, f_List, x_List) :=
          // Prepend(MapThread(Function({g, y, r}, g /. y -> r), {f, x, Most(#)}), 1)& /@ m
          return Map(Function(
              Prepend(MapThread(Function(List(S.g, S.y, S.r), ReplaceAll(S.g, Rule(S.y, S.r))),
                  List(f, x, Most(Slot1))), C1)),
              m);
        }
      } else {
        if (x.isAtom()) {
          // DesignMatrix(m_, f_, x_?AtomQ) := DesignMatrix(m, {f}, {x})
          return F.DesignMatrix(m, F.list(f), F.list(x));
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Det(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the determinant of the <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Determinant">Wikipedia: Determinant</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Det({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}})
   * -2
   * </pre>
   *
   * <p>
   * Symbolic determinant:
   *
   * <pre>
   * &gt;&gt; Det({{a, b, c}, {d, e, f}, {g, h, i}})
   * -c*e*g+b*f*g+c*d*h-a*f*h-b*d*i+a*e*i
   * </pre>
   */
  private static class Det extends AbstractMatrix1Expr {

    @Override
    public int[] checkMatrixDimensions(IExpr arg1) {
      return Convert.checkNonEmptySquareMatrix(S.Det, arg1);
    }

    @Override
    public IExpr matrixEval(final FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker) {
      if (matrix.getRowDimension() == 2 && matrix.getColumnDimension() == 2) {
        return determinant2x2(matrix);
      }
      if (matrix.getRowDimension() == 3 && matrix.getColumnDimension() == 3) {
        return determinant3x3(matrix);
      }
      // @since version 1.9
      // final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix,
      // zeroChecker);
      final FieldLUDecomposition<IExpr> lu =
          new FieldLUDecomposition<IExpr>(matrix, zeroChecker, false);
      return F.evalExpand(lu.getDeterminant());
    }

    @Override
    public IExpr realMatrixEval(RealMatrix matrix) {
      final org.hipparchus.linear.LUDecomposition lu =
          new org.hipparchus.linear.LUDecomposition(matrix);
      return F.num(lu.getDeterminant());
    }
  }

  private static class Diagonal extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isList() || arg1.isSparseArray()) {
        final int diff;
        if (ast.size() > 2) {
          diff = ast.arg2().toIntDefault();
          if (diff == Integer.MIN_VALUE) {
            return F.NIL;
          }
        } else {
          diff = 0;
        }
        if (arg1.isList()) {
          return F.mapList((IAST) arg1, (arg, i) -> {
            if (arg.isList()) {
              IAST subList = (IAST) arg;
              final int indx = i + diff;
              if (indx > 0 && indx <= subList.argSize()) {
                return subList.get(indx);
              }
            }
            return F.NIL;
          });
        } else if (arg1.isSparseArray()) {
          ISparseArray sparseArray = (ISparseArray) arg1;
          int[] dims = sparseArray.getDimension();
          if (dims.length == 1) {
            return F.CEmptyList;
          } else if (dims.length >= 2) {
            int rowLength = dims[0];
            int colLength = dims[1];
            IASTAppendable result = F.ListAlloc(rowLength);
            for (int i = 1; i <= rowLength; i++) {
              IExpr arg = sparseArray.get(i);
              if (arg.isSparseArray()) {
                ISparseArray subList = (ISparseArray) arg;
                int indx = i + diff;
                if (indx > 0 && indx <= colLength) {
                  result.append(subList.get(indx));
                }
              } else {
                break;
              }
            }
            return result;
          }
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * DiagonalMatrix(list)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives a matrix with the values in $list$ on its diagonal and zeroes elsewhere.
   *
   * </blockquote>
   *
   * <pre>
   * &gt;&gt; DiagonalMatrix({1, 2, 3})
   * {{1, 0, 0}, {0, 2, 0}, {0, 0, 3}}
   *
   * &gt;&gt; MatrixForm(%)
   *  1   0   0
   *  0   2   0
   *  0   0   3
   *
   * &gt;&gt; DiagonalMatrix(a + b)
   * DiagonalMatrix(a + b)
   * </pre>
   */
  private static class DiagonalMatrix extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int dimension = arg1.isVector();
      final IAST vector;
      if (dimension >= 0) {
        if (arg1.isSparseArray()) {
          // for sparse array vector input return a sparse array diagonal matrix
          ISparseArray sparseArray = (ISparseArray) arg1;
          int m = sparseArray.getDimension()[0] + 1;
          final int offset = ast.isAST2() ? Validate.checkIntType(ast, 2, Integer.MIN_VALUE) : 0;
          return F.sparseMatrix((i, j) -> (i + offset) == j ? sparseArray.get(i + 1) : F.C0, m - 1,
              m - 1);
        }
        IExpr normal = arg1.normal(false);
        if (normal.isAST()) {
          vector = (IAST) normal;
        } else {
          vector = F.NIL;
        }
      } else if (arg1.isAST()) {
        vector = (IAST) arg1;
      } else {
        vector = F.NIL;
      }
      if (vector.isPresent()) {
        int m = vector.size();
        final int offset = ast.isAST2() ? Validate.checkIntType(ast, 2, Integer.MIN_VALUE) : 0;
        return F.matrix((i, j) -> (i + offset) == j ? vector.get(i + 1) : F.C0, m - 1, m - 1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Dimensions(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of the dimensions of the expression <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * A vector of length 3:
   *
   * <pre>
   * &gt;&gt; Dimensions({a, b, c})
   *  = {3}
   * </pre>
   *
   * <p>
   * A 3x2 matrix:
   *
   * <pre>
   * &gt;&gt; Dimensions({{a, b}, {c, d}, {e, f}})
   *  = {3, 2}
   * </pre>
   *
   * <p>
   * Ragged arrays are not taken into account:
   *
   * <pre>
   * &gt;&gt; Dimensions({{a, b}, {b, c}, {c, d, e}})
   * {3}
   * </pre>
   *
   * <p>
   * The expression can have any head:
   *
   * <pre>
   * &gt;&gt; Dimensions[f[f[a, b, c]]]
   * {1, 3}
   * &gt;&gt; Dimensions({})
   * {0}
   * &gt;&gt; Dimensions({{}})
   * {1, 0}
   * </pre>
   */
  private static class Dimensions extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int maximumLevel = Integer.MAX_VALUE;
      if (ast.isAST2() && ast.arg2().isInteger()) {
        maximumLevel = ast.arg2().toIntDefault();
        if (maximumLevel < 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return IOFunctions.printMessage(S.Dimensions, "intpm", F.list(ast, F.C2), engine);
        }
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isAST()) {
        if (maximumLevel > 0) {
          return getDimensions(ast, maximumLevel);
        }
      } else if (arg1.isSparseArray()) {
        if (maximumLevel > 0) {
          return getDimensions(((ISparseArray) arg1).getDimension(), maximumLevel);
        }
      } else if (arg1.isNumericArray()) {
        if (maximumLevel > 0) {
          return getDimensions(((INumericArray) arg1).getDimension(), maximumLevel);
        }
      }

      return F.CEmptyList;
    }

    private static IAST getDimensions(final IAST ast, int maximumLevel) {
      IAST list = (IAST) ast.arg1();
      IExpr header = list.head();
      final IntList dims = dimensions(list, header, maximumLevel - 1);
      return F.mapRange(0, dims.size(), i -> F.ZZ(dims.getInt(i)));
    }

    private IExpr getDimensions(int[] dims, int maximumLevel) {
      if (dims.length > maximumLevel) {
        int[] dest = new int[maximumLevel];
        System.arraycopy(dims, 0, dest, 0, maximumLevel);
        return F.ast(S.List, dest);
      }
      return F.ast(S.List, dims);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, //
          F.list(F.Rule(S.AllowedHeads, S.Automatic)));
    }
  }

  /**
   *
   *
   * <pre>
   * Dot(x, y) or x . y
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * <code>x . y</code> computes the vector dot product or matrix product <code>x . y</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Matrix_multiplication">Wikipedia - Matrix
   * multiplication</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <p>
   * Scalar product of vectors:
   *
   * <pre>
   * &gt;&gt; {a, b, c} . {x, y, z}
   * a*x+b*y+c*z
   * </pre>
   *
   * <p>
   * Product of matrices and vectors:
   *
   * <pre>
   * &gt;&gt; {{a, b}, {c, d}} . {x, y}
   * {a*x+b*y,c*x+d*y}
   * </pre>
   *
   * <p>
   * Matrix product:
   *
   * <pre>
   * &gt;&gt; {{a, b}, {c, d}} . {{r, s}, {t, u}}
   * {{a*r+b*t,a*s+b*u}, {c*r+d*t,c*s+d*u}}
   *
   * &gt;&gt; a . b
   * a.b
   * </pre>
   */
  private static class Dot extends AbstractNonOrderlessArgMultiple {

    @Override
    public IExpr evaluateAST1(final IAST ast, EvalEngine engine) {
      return ast.arg1();
    }

    @Override
    public IExpr e2ObjArg(IAST ast, final IExpr arg1, final IExpr arg2) {

      if (arg1.size() == 1 && arg2.size() == 1) {
        if (arg1.isList() && arg2.isList()) {
          return F.C0;
        }
        return F.NIL;
      }

      EvalEngine engine = EvalEngine.get();
      boolean togetherMode = engine.isTogetherMode();
      engine.setTogetherMode(true);
      try {
        IExpr temp = numericalDot(arg1, arg2);
        if (temp.isPresent()) {
          return temp;
        }

        final IntList dimensions1 = dimensions(arg1, S.List, Integer.MAX_VALUE, true);
        final int dims1Size = dimensions1.size();
        if (dims1Size == 0) {
          return F.NIL;
        }
        final IntList dimensions2 = dimensions(arg2, S.List, Integer.MAX_VALUE, true);
        final int dims2Size = dimensions2.size();
        if (dims2Size == 0) {
          return F.NIL;
        }
        if (dimensions1.getInt(dims1Size - 1) != dimensions2.getInt(0)) {
          // Tensors `1` and `2` have incompatible shapes.
          return IOFunctions.printMessage(ast.topHead(), "dotsh", F.list(arg1, arg2), engine);
        }

        if (dims1Size == 2) {
          FieldMatrix<IExpr> matrix0;
          matrix0 = Convert.list2Matrix(arg1);
          if (matrix0 != null) {
            if (dims2Size == 2) {
              FieldMatrix<IExpr> matrix1 = Convert.list2Matrix(arg2);
              if (matrix1 != null) {
                return Convert.matrix2Expr(matrix0.multiply(matrix1));
              }
            } else if (dims2Size == 1) {
              FieldVector<IExpr> vector1 = Convert.list2Vector(arg2);
              if (vector1 != null) {
                return Convert.vector2Expr(matrix0.operate(vector1));
              }
            }
          }
        } else {
          if (dims1Size == 1) {
            int dim = dimensions1.getInt(0);
            if (dim == 0) {
              if (arg2.isVector() == 0) {
                return F.C0;
              }
            } else {
              FieldVector<IExpr> vector0 = Convert.list2Vector(arg1);
              if (vector0 != null) {
                if (dims2Size == 2) {
                  FieldMatrix<IExpr> matrix1 = Convert.list2Matrix(arg2);
                  if (matrix1 != null) {
                    return Convert.vector2Expr(matrix1.preMultiply(vector0));
                  }
                } else if (dims2Size == 1) {
                  FieldVector<IExpr> vector1 = Convert.list2Vector(arg2);
                  if (vector1 != null) {
                    return vector0.dotProduct(vector1);
                  }
                }
              }
            }
          }
        }

        return S.Inner.ofNIL(EvalEngine.get(), S.Times, arg1, arg2, S.Plus);
      } catch (IllegalArgumentException iae) {
        // print message: Nonrectangular tensor encountered
        return IOFunctions.printMessage(ast.topHead(), "rect", F.list(ast), engine);
      } catch (final RuntimeException e) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), e);
      } finally {
        engine.setTogetherMode(togetherMode);
      }
      return F.NIL;
    }

    private IExpr numericalDot(final IExpr o0, final IExpr o1) throws MathIllegalArgumentException {
      if (o0.isRealMatrix()) {
        if (o1.isMatrix() != null) {
          RealMatrix m1 = o1.toRealMatrix();
          if (m1 != null) {
            RealMatrix m0 = o0.toRealMatrix();
            return new ASTRealMatrix(m0.multiply(m1), false);
          }
        } else if (o1.isVector() != (-1)) {
          RealVector m1 = o1.toRealVector();
          if (m1 != null) {
            RealMatrix m0 = o0.toRealMatrix();
            return new ASTRealVector(m0.operate(m1), false);
          }
        }
      } else if (o0.isRealVector()) {
        if (o1.isMatrix() != null) {
          RealMatrix m1 = o1.toRealMatrix();
          if (m1 != null) {
            RealVector v0 = o0.toRealVector();
            return new ASTRealVector(m1.preMultiply(v0), false);
          }
        } else if (o1.isVector() != (-1)) {
          RealVector v1 = o1.toRealVector();
          if (v1 != null) {
            RealVector v0 = o0.toRealVector();
            return F.num(v0.dotProduct(v1));
          }
        }
      }

      if (o1.isRealMatrix()) {
        if (o0.isMatrix() != null) {
          RealMatrix m0 = o0.toRealMatrix();
          if (m0 != null) {
            RealMatrix m1 = o1.toRealMatrix();
            return new ASTRealMatrix(m0.multiply(m1), false);
          }
        } else if (o0.isVector() != (-1)) {
          RealVector v0 = o0.toRealVector();
          if (v0 != null) {
            RealMatrix m1 = o1.toRealMatrix();
            return new ASTRealVector(m1.preMultiply(v0), false);
          }
        }
      } else if (o1.isRealVector()) {
        if (o0.isMatrix() != null) {
          RealMatrix m0 = o0.toRealMatrix();
          if (m0 != null) {
            RealVector m1 = o1.toRealVector();
            return new ASTRealVector(m0.operate(m1), false);
          }
        } else if (o0.isVector() != (-1)) {
          RealVector v0 = o0.toRealVector();
          if (v0 != null) {
            RealVector v1 = o1.toRealVector();
            if (v0.getDimension() == v1.getDimension()) {
              return F.num(v0.dotProduct(v1));
            }
          }
        }
      }
      return F.NIL;
    }

    @Override
    public IExpr numericEval(final IAST ast, EvalEngine engine) {
      return evaluate(ast, engine);
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.FLAT | ISymbol.ONEIDENTITY);
    }
  }

  /**
   *
   *
   * <pre>
   * Eigenvalues(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the numerical eigenvalues of the <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Eigenvalue">Wikipedia - Eigenvalue</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt;&gt; Eigenvalues({{1,0,0},{0,1,0},{0,0,1}})
   * {1.0,1.0,1.0}
   * </pre>
   */
  private static class Eigenvalues extends AbstractMatrix1Expr {

    @Override
    public int[] checkMatrixDimensions(IExpr arg1) {
      return Convert.checkNonEmptySquareMatrix(S.Eigenvalues, arg1);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() && !engine.isNumericMode()) {
        FieldMatrix<IExpr> matrix;
        try {

          IExpr arg1 = ast.arg1();
          int[] dim = arg1.isMatrix();
          if (dim != null) {
            if (dim[0] == 1 && dim[1] == 1) {
              // Eigenvalues({{a}})
              return List(((IAST) arg1).getPart(1, 1));
            }
            if (dim[0] == 2 && dim[1] == 2) {
              matrix = Convert.list2Matrix(arg1);
              if (matrix != null) {
                // Eigenvalues({{a, b}, {c, d}}) =>
                // {
                // 1/2*(a + d - Sqrt(a^2 + 4*b*c - 2*a*d + d^2)),
                // 1/2*(a + d + Sqrt(a^2 + 4*b*c - 2*a*d + d^2))
                // }
                IExpr sqrtExpr = Sqrt(Plus(Sqr(matrix.getEntry(0, 0)),
                    Times(C4, matrix.getEntry(0, 1), matrix.getEntry(1, 0)),
                    Times(CN2, matrix.getEntry(0, 0), matrix.getEntry(1, 1)),
                    Sqr(matrix.getEntry(1, 1))));
                return List(
                    Times(C1D2,
                        Plus(Negate(sqrtExpr), matrix.getEntry(0, 0), matrix.getEntry(1, 1))),
                    Times(C1D2, Plus(sqrtExpr, matrix.getEntry(0, 0), matrix.getEntry(1, 1))));
              }
            }
            // if (((IAST) arg1).forAllLeaves(x->x.isExactNumber(), 1)) {
            // ISymbol x = F.Dummy("x");
            // IExpr m = engine.evaluate(F.CharacteristicPolynomial(arg1, x));
            // IAST list = PolynomialFunctions.roots(m, false, F.list(x), engine);
            // if (list.isPresent()) {
            // return F.Reverse(list);
            // }
            // }
          }

        } catch (final RuntimeException e) {
          LOGGER.debug("Eigenvalues.evaluate() failed", e);
        }
      }
      // switch to numeric calculation
      IExpr eigenValues = numericEval(ast, engine);
      if (eigenValues.isList()) {
        if (ast.isAST2()) {
          int n = ast.arg2().toIntDefault();
          if (n < 0) {
            if (n == Integer.MIN_VALUE) {
              return F.NIL;
            }
            return F.Reverse(F.TakeSmallestBy(eigenValues, S.Abs, F.ZZ(-n)));
          }
          return F.TakeLargestBy(eigenValues, S.Abs, F.ZZ(n));
        }
        return eigenValues;
      }
      return F.NIL;
    }

    @Override
    public IExpr matrixEval(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker) {
      return F.NIL;
    }

    @Override
    public IAST realMatrixEval(RealMatrix matrix) {
      EigenDecomposition ed = new EigenDecomposition(matrix);
      double[] realValues = ed.getRealEigenvalues();
      double[] imagValues = ed.getImagEigenvalues();
      return F.mapRange(0, realValues.length, (int i) -> {
        if (F.isZero(imagValues[i])) {
          return F.num(realValues[i]);
        }
        return F.complexNum(realValues[i], imagValues[i]);
      });
    }
  }

  /**
   * Eigenvectors(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the numerical eigenvectors of the <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Eigenvalue">Wikipedia - Eigenvalue</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt;&gt; Eigenvectors({{1,0,0},{0,1,0},{0,0,1}})
   * {{1.0,0.0,0.0},{0.0,1.0,0.0},{0.0,0.0,1.0}}
   * </pre>
   */
  private static class Eigenvectors extends AbstractMatrix1Expr {
    /**
     * Given a matrix A, it computes a complex eigen decomposition A = VDV^{T}.
     *
     * <p>
     * Eigenvectors with numeric eigenvalues are sorted in order of decreasing absolute value of
     * their eigenvalues.
     */
    // private static class OrderedComplexAbsEigenDecomposition extends ComplexEigenDecomposition {
    //
    // /**
    // * Constructor for the decomposition.
    // *
    // * @param matrix real matrix.
    // */
    // public OrderedComplexAbsEigenDecomposition(final RealMatrix matrix) {
    // this(
    // matrix,
    // ComplexEigenDecomposition.DEFAULT_EIGENVECTORS_EQUALITY,
    // ComplexEigenDecomposition.DEFAULT_EPSILON,
    // ComplexEigenDecomposition.DEFAULT_EPSILON_AV_VD_CHECK);
    // }
    //
    // /**
    // * Constructor for decomposition.
    // *
    // * <p>The {@code eigenVectorsEquality} threshold is used to ensure the L∞-normalized
    // * eigenvectors found using inverse iteration are different from each other. if
    // * \(min(|e_i-e_j|,|e_i+e_j|)\) is smaller than this threshold, the algorithm considers it has
    // * found again an already known vector, so it drops it and attempts a new inverse iteration
    // * with a different start vector. This value should be much larger than {@code epsilon} which
    // * is used for convergence
    // *
    // * @param matrix real matrix.
    // * @param eigenVectorsEquality threshold below which eigenvectors are considered equal
    // * @param epsilon Epsilon used for internal tests (e.g. is singular, eigenvalue ratio, etc.)
    // * @param epsilonAVVDCheck Epsilon criteria for final AV=VD check
    // * @since 1.9
    // */
    // public OrderedComplexAbsEigenDecomposition(
    // final RealMatrix matrix,
    // final double eigenVectorsEquality,
    // final double epsilon,
    // final double epsilonAVVDCheck) {
    // super(matrix, eigenVectorsEquality, epsilon, epsilonAVVDCheck);
    // final FieldMatrix<Complex> D = this.getD();
    // final FieldMatrix<Complex> V = this.getV();
    //
    // // getting eigen values
    // IndexedEigenvalue[] eigenValues = new IndexedEigenvalue[D.getRowDimension()];
    // for (int ij = 0; ij < matrix.getRowDimension(); ij++) {
    // eigenValues[ij] = new IndexedEigenvalue(ij, D.getEntry(ij, ij));
    // }
    //
    // // ordering
    // Arrays.sort(eigenValues);
    // for (int ij = 0; ij < matrix.getRowDimension() - 1; ij++) {
    // final IndexedEigenvalue eij = eigenValues[ij];
    //
    // if (ij == eij.index) {
    // continue;
    // }
    //
    // // exchanging D
    // final Complex previousValue = D.getEntry(ij, ij);
    // D.setEntry(ij, ij, eij.eigenValue);
    // D.setEntry(eij.index, eij.index, previousValue);
    //
    // // exchanging V
    // for (int k = 0; k < matrix.getRowDimension(); ++k) {
    // final Complex previous = V.getEntry(k, ij);
    // V.setEntry(k, ij, V.getEntry(k, eij.index));
    // V.setEntry(k, eij.index, previous);
    // }
    //
    // // exchanging eigenvalue
    // for (int k = ij + 1; k < matrix.getRowDimension(); ++k) {
    // if (eigenValues[k].index == ij) {
    // eigenValues[k].index = eij.index;
    // break;
    // }
    // }
    // }
    //
    // checkDefinition(matrix);
    // }
    //
    // /** {@inheritDoc} */
    // @Override
    // public FieldMatrix<Complex> getVT() {
    // return getV().transpose();
    // }
    //
    // /** Container for index and eigenvalue pair. */
    // private static class IndexedEigenvalue implements Comparable<IndexedEigenvalue> {
    //
    // /** Index in the diagonal matrix. */
    // private int index;
    //
    // /** Eigenvalue. */
    // private final Complex eigenValue;
    //
    // /**
    // * Build the container from its fields.
    // *
    // * @param index index in the diagonal matrix
    // * @param eigenvalue eigenvalue
    // */
    // IndexedEigenvalue(final int index, final Complex eigenvalue) {
    // this.index = index;
    // this.eigenValue = eigenvalue;
    // }
    //
    // /**
    // * {@inheritDoc}
    // *
    // * <p>Ordering uses real ordering as the primary sort order and imaginary ordering as the
    // * secondary sort order.
    // */
    // @Override
    // public int compareTo(final IndexedEigenvalue other) {
    // return Double.compare(other.eigenValue.abs().getReal(), eigenValue.abs().getReal());
    // }
    //
    // /** {@inheritDoc} */
    // @Override
    // public boolean equals(final Object other) {
    //
    // if (this == other) {
    // return true;
    // }
    //
    // if (other instanceof IndexedEigenvalue) {
    // final IndexedEigenvalue rhs = (IndexedEigenvalue) other;
    // return eigenValue.equals(rhs.eigenValue);
    // }
    //
    // return false;
    // }
    //
    // /**
    // * Get a hashCode for the pair.
    // *
    // * @return a hash code value for this object
    // */
    // @Override
    // public int hashCode() {
    // return 4563 + index + eigenValue.hashCode();
    // }
    // }
    // }

    @Override
    public int[] checkMatrixDimensions(IExpr arg1) {
      return Convert.checkNonEmptySquareMatrix(S.Eigenvectors, arg1);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.size() == 2) {
        FieldMatrix<IExpr> matrix;
        try {

          int[] dim = ast.arg1().isMatrix();
          if (dim != null) {
            if (dim[0] == 1 && dim[1] == 1) {
              // Eigenvectors({{a}})
              return C1;
            }
            if (dim[0] == 2 && dim[1] == 2) {
              matrix = Convert.list2Matrix(ast.arg1());
              if (matrix != null) {
                if (matrix.getEntry(1, 0).isZero()) {
                  if (matrix.getEntry(0, 0).equals(matrix.getEntry(1, 1))) {
                    // Eigenvectors({{a, b}, {0, a}})
                    return List(List(C1, C0), List(C0, C0));
                  } else {
                    // Eigenvectors({{a, b}, {0, d}})
                    return List(List(C1, C0), List(Divide(Negate(matrix.getEntry(0, 1)),
                        Subtract(matrix.getEntry(0, 0), matrix.getEntry(1, 1))), C1));
                  }
                } else {
                  // Eigenvectors({{a, b}, {c, d}}) =>
                  // {
                  // { - (1/(2*c)) * (-a + d + Sqrt(a^2 + 4*b*c - 2*a*d + d^2)), 1},
                  // { - (1/(2*c)) * (-a + d - Sqrt(a^2 + 4*b*c - 2*a*d + d^2)), 1}
                  // }
                  IExpr sqrtExpr = Sqrt(Plus(Sqr(matrix.getEntry(0, 0)),
                      Times(C4, matrix.getEntry(0, 1), matrix.getEntry(1, 0)),
                      Times(CN2, matrix.getEntry(0, 0), matrix.getEntry(1, 1)),
                      Sqr(matrix.getEntry(1, 1))));
                  return List(
                      List(
                          Times(CN1D2, Power(matrix.getEntry(1, 0), CN1),
                              Plus(sqrtExpr, Negate(matrix.getEntry(0, 0)), matrix.getEntry(1, 1))),
                          C1),
                      List(Times(CN1D2, Power(matrix.getEntry(1, 0), CN1), Plus(Negate(sqrtExpr),
                          Negate(matrix.getEntry(0, 0)), matrix.getEntry(1, 1))), C1));
                }
              }
            }
          }
        } catch (final MathRuntimeException mre) {
          // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 != 3
          LOGGER.log(engine.getLogLevel(), ast.topHead(), mre);
          return F.NIL;
        } catch (final ClassCastException | IndexOutOfBoundsException e) {
          LOGGER.debug("Eigenvectors.evaluate() failed", e);
        }
      }

      // switch to numeric calculation
      return numericEval(ast, engine);
    }

    @Override
    public IExpr matrixEval(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker) {
      return F.NIL;
    }

    @Override
    public IAST realMatrixEval(RealMatrix matrix) {
      // TODO https://github.com/Hipparchus-Math/hipparchus/issues/174
      ComplexEigenDecomposition ced = new ComplexEigenDecomposition(matrix);
      return F.mapRange(0, matrix.getColumnDimension(),
          j -> F.Normalize(Convert.complexVector2List(ced.getEigenvector(j))));
    }
  }

  private static class FourierMatrix extends AbstractFunctionEvaluator {

    /**
     * Complex number on unit circle with given argument.
     *
     * @param arg
     * @return complex number on unit circle with given argument
     */
    private static IExpr unit(IExpr arg) {
      return F.Plus(F.Cos(arg), F.Times(F.CI, F.Sin(arg)));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isInteger()) {
        final int m = ast.arg1().toIntDefault();
        if (m <= 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return IOFunctions.printMessage(S.FourierMatrix, "intpm", F.list(ast, F.C1), engine);
        }
        IAST scalar = F.Sqrt(F.QQ(1, m));
        return F.matrix((i, j) -> unit(F.QQ(2L * (i) * (j), m).times(S.Pi)).times(scalar), m, m);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * FromPolarCoordinates({r, t})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the cartesian coordinates for the polar coordinates <code>{r, t}</code>.
   *
   * </blockquote>
   *
   * <pre>
   * FromPolarCoordinates({r, t, p})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the cartesian coordinates for the polar coordinates <code>{r, t, p}</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FromPolarCoordinates({r, t})
   * {r*Cos(t),r*Sin(t)}
   *
   * &gt;&gt; FromPolarCoordinates({r, t, p})
   * {r*Cos(t),r*Cos(p)*Sin(t),r*Sin(p)*Sin(t)}
   * </pre>
   */
  private static final class FromPolarCoordinates extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int dim = arg1.isVector();
      if (dim > 0) {
        if (arg1.isAST()) {
          IAST list = (IAST) arg1;
          if (dim == 2) {
            IExpr r = list.arg1();
            IExpr theta = list.arg2();
            return F.list(F.Times(r, F.Cos(theta)), F.Times(r, F.Sin(theta)));
          } else if (dim == 3) {
            IExpr r = list.arg1();
            IExpr theta = list.arg2();
            IExpr phi = list.arg3();
            return F.list(F.Times(r, F.Cos(theta)), F.Times(r, F.Cos(phi), F.Sin(theta)),
                F.Times(r, F.Sin(theta), F.Sin(phi)));
          }
        } else {
          FieldVector<IExpr> vector = Convert.list2Vector(arg1);
          if (dim == 2) {
            IExpr r = vector.getEntry(0);
            IExpr theta = vector.getEntry(1);
            return F.list(F.Times(r, F.Cos(theta)), F.Times(r, F.Sin(theta)));
          } else if (dim == 3) {
            IExpr r = vector.getEntry(0);
            IExpr theta = vector.getEntry(1);
            IExpr phi = vector.getEntry(2);
            return F.list(F.Times(r, F.Cos(theta)), F.Times(r, F.Cos(phi), F.Sin(theta)),
                F.Times(r, F.Sin(theta), F.Sin(phi)));
          }
        }
      } else if (arg1.isList()) {
        return ((IAST) arg1).mapThreadEvaled(engine, F.ListAlloc(ast.size()), ast, 1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class HessenbergDecomposition extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      RealMatrix matrix;
      boolean togetherMode = engine.isTogetherMode();
      try {
        engine.setTogetherMode(true);
        int[] dim = ast.arg1().isMatrix();
        if (dim != null && dim[0] > 0 && dim[1] > 0) {
          if (dim[0] != dim[1]) {
            // Argument `1` at position `2` is not a non-empty square matrix.
            return IOFunctions.printMessage(ast.topHead(), "matsq", F.CEmptyList, engine);
          }
          matrix = ast.arg1().toRealMatrix();
          if (matrix != null) {
            HessenbergTransformer hessenbergTransformer = new HessenbergTransformer(matrix);
            final RealMatrix pMatrix = hessenbergTransformer.getP();
            final RealMatrix tMatrix = hessenbergTransformer.getH();
            IASTAppendable m1 = Convert.matrix2List(pMatrix);
            if (m1.isPresent()) {
              IASTAppendable m2 = Convert.matrix2List(tMatrix);
              if (m2.isPresent()) {
                return F.list(m1, m2);
              }
            }
          }
        }
      } catch (IndexOutOfBoundsException | ClassCastException e) {
        LOGGER.debug("SchurDecomposition.evaluate() failed", e);
      } finally {
        engine.setTogetherMode(togetherMode);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }
  /**
   *
   *
   * <pre>
   * HilbertMatrix(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the hilbert matrix with <code>n</code> rows and columns.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Hilbert_matrix">Wikipedia - Hilbert matrix</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; HilbertMatrix(2)
   * {{1,1/2},
   *  {1/2,1/3}}
   * </pre>
   */
  private static class HilbertMatrix extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int rowSize = 0;
      int columnSize = 0;
      if (ast.isAST1() && ast.arg1().isInteger()) {
        rowSize = ast.arg1().toIntDefault();
        if (rowSize < 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return IOFunctions.printMessage(S.HilbertMatrix, "intpm", F.list(ast, F.C1), engine);
        }
        columnSize = rowSize;
      } else if (ast.isAST2() && ast.arg1().isInteger() && ast.arg2().isInteger()) {
        rowSize = ast.arg1().toIntDefault();
        if (rowSize < 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return IOFunctions.printMessage(S.HilbertMatrix, "intpm", F.list(ast, F.C1), engine);
        }
        columnSize = ast.arg2().toIntDefault();
        if (columnSize < 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return IOFunctions.printMessage(S.HilbertMatrix, "intpm", F.list(ast, F.C2), engine);
        }
      } else {
        return F.NIL;
      }
      return F.matrix((i, j) -> F.QQ(1, i + j + 1), rowSize, columnSize);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * IdentityMatrix(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the identity matrix with <code>n</code> rows and columns.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; IdentityMatrix(3)
   * {{1, 0, 0}, {0, 1, 0}, {0, 0, 1}}
   * </pre>
   */
  private static class IdentityMatrix extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isInteger()) {
        int m = ast.arg1().toIntDefault();
        if (m < 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return IOFunctions.printMessage(S.IdentityMatrix, "intpm", F.list(ast, F.C1), engine);
        }
        if (ast.isAST2()) {
          if (ast.arg2().equals(S.SparseArray)) {
            int[] dimension = new int[] {m, m};
            // {{i_,i_} -> 1}
            return F.sparseArray(F.list(F.Rule(List(F.i_, F.i_), F.C1)), dimension);
          }
          return F.NIL;
        }
        return F.matrix((i, j) -> i == j ? F.C1 : F.C0, m, m);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Inner(f, x, y, g)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes a generalised inner product of <code>x</code> and <code>y</code>, using a
   * multiplication function <code>f</code> and an addition function <code>g</code>.
   *
   * </blockquote>
   *
   * <pre>
   * &gt;&gt; Inner(f, {a, b}, {x, y}, g)
   * g(f(a, x), f(b, y))
   * </pre>
   *
   * <p>
   * 'Inner' can be used to compute a dot product:
   *
   * <pre>
   * &gt;&gt; Inner(Times, {a, b}, {c, d}, Plus) == {a, b} . {c, d}
   * </pre>
   *
   * <p>
   * The inner product of two boolean matrices:
   *
   * <pre>
   * &gt;&gt; Inner(And, {{False, False}, {False, True}}, {{True, False}, {True, True}}, Or)
   * {{False, False}, {True, True}}
   * </pre>
   */
  private static class Inner extends AbstractFunctionEvaluator {

    private static class InnerAlgorithm {
      final IExpr f;
      final IExpr g;
      final IExpr head;
      final IAST list1;
      final IAST list2;
      int list2Dim0;

      private InnerAlgorithm(final IExpr f, final IAST list1, final IAST list2, final IExpr g) {
        this.f = f;
        this.list1 = list1;
        this.list2 = list2;
        this.g = g;
        this.head = list2.head();
      }

      private IAST inner() {
        IntArrayList list1Dimensions = dimensions(list1, list1.head(), Integer.MAX_VALUE);
        IntArrayList list2Dimensions = dimensions(list2, list2.head(), Integer.MAX_VALUE);
        list2Dim0 = list2Dimensions.getInt(0);
        return recursionInner(new IntArrayList(), new IntArrayList(),
            list1Dimensions.subList(0, list1Dimensions.size() - 1),
            list2Dimensions.subList(1, list2Dimensions.size()));
      }

      private IAST recursionInner(IntList list1Cur, IntList list2Cur, IntList list1RestDimensions,
          IntList list2RestDimensions) {
        if (list1RestDimensions.size() > 0) {
          int size = list1RestDimensions.getInt(0) + 1;
          return F.mapRange(head, 1, size, i -> {
            IntArrayList list1CurClone = new IntArrayList(list1Cur);
            list1CurClone.add(i);
            IAST recursionInner = recursionInner(list1CurClone, list2Cur,
                list1RestDimensions.subList(1, list1RestDimensions.size()), list2RestDimensions);
            if (recursionInner.isPresent()) {
              return recursionInner;
            }
            return null;
          });
        } else if (list2RestDimensions.size() > 0) {
          int size = list2RestDimensions.getInt(0) + 1;
          return F.mapRange(head, 1, size, i -> {
            IntArrayList list2CurClone = new IntArrayList(list2Cur);
            list2CurClone.add(i);
            IAST recursionInner = recursionInner(list1Cur, list2CurClone, list1RestDimensions,
                list2RestDimensions.subList(1, list2RestDimensions.size()));
            if (recursionInner.isPresent()) {
              return recursionInner;
            }
            return null;
          });
        } else {
          try {
            int size = list2Dim0 + 1;
            IASTAppendable part = F.ast(g, size);
            return part.appendArgs(size, i -> summand(list1Cur, list2Cur, i));
            // for (int i = 1; i < size; i++) {
            // part.append(summand(list1Cur, list2Cur, i));
            // }
            // return part;
          } catch (IndexOutOfBoundsException ioobe) {
            // TODO Length `1` of dimension `2` in `3` is incommensurate with length `4` of
            // dimension `5` in `6`.
            return IOFunctions.printMessage(S.Inner, "incom", F.CEmptyList, EvalEngine.get());
          }
        }
      }

      private IAST summand(IntList list1Cur, IntList list2Cur, final int i) {
        IASTAppendable result = F.ast(f, 2);
        IntArrayList list1CurClone = new IntArrayList(list1Cur);
        list1CurClone.add(i);
        result.append(list1.getPart(list1CurClone));
        IntArrayList list2CurClone = new IntArrayList(list2Cur);
        list2CurClone.add(0, i);
        result.append(list2.getPart(list2CurClone));
        return result;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg2 = ast.arg2();
      IExpr arg3 = ast.arg3();
      if ((arg2.isAST() || arg2.isSparseArray()) //
          && (arg3.isAST() || arg3.isSparseArray())) {
        IExpr f = ast.arg1();
        IAST list1 = (IAST) arg2.normal(false);
        IAST list2 = (IAST) arg3.normal(false);
        IExpr g;
        if (ast.isAST3()) {
          g = S.Plus;
        } else {
          g = ast.arg4();
        }
        IExpr head2 = list2.head();
        if (!list1.head().equals(head2)) {
          return F.NIL;
        }
        IntArrayList dim1 = dimensions(list1);
        IntArrayList dim2 = dimensions(list2);
        if (dim1.size() == 0) {
          // Nonatomic expression expected at position `1` in `2`.
          return IOFunctions.printMessage(S.Inner, "normal", F.list(F.C2, list1), EvalEngine.get());
        }
        if (dim2.size() == 0) {
          // Nonatomic expression expected at position `1` in `2`.
          return IOFunctions.printMessage(S.Inner, "normal", F.list(F.C3, list2), EvalEngine.get());
        }
        int dimSize1 = dim1.getInt(dim1.size() - 1);
        int dimSize2 = dim2.getInt(0);
        if (dimSize1 != dimSize2) {
          // Length `1` of dimension `2` in `3` is incommensurate with length `4` of dimension `5`
          // in `6`.
          return IOFunctions.printMessage(S.Inner, "incom",
              F.List(F.ZZ(dimSize1), F.ZZ(dim1.size()), list1, F.C1, F.ZZ(dimSize2), list2),
              EvalEngine.get());
        }
        // if (list1.size() == list2.size()) {
        InnerAlgorithm ic = new InnerAlgorithm(f, list1, list2, g);
        return ic.inner();
        // }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_4;
    }
  }

  /**
   *
   *
   * <pre>
   * Inverse(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the inverse of the <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Invertible_matrix">Wikipedia - Invertible matrix</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Inverse({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}})
   * {{-3,2,0},
   *  {2,-1,0},
   *  {1,-2,1}}
   * </pre>
   *
   * <p>
   * The matrix <code>{{1, 0}, {0, 0}}</code> is singular.
   *
   * <pre>
   * &gt;&gt; Inverse({{1, 0}, {0, 0}})
   * Inverse({{1, 0}, {0, 0}})
   *
   * &gt;&gt; Inverse({{1, 0, 0}, {0, Sqrt(3)/2, 1/2}, {0,-1 / 2, Sqrt(3)/2}})
   * {{1,0,0},
   *  {0,Sqrt(3)/2,-1/2},
   *  {0,1/2,1/(1/(2*Sqrt(3))+Sqrt(3)/2)}}
   * </pre>
   */
  private static final class Inverse extends AbstractMatrix1Matrix {

    @Override
    public int[] checkMatrixDimensions(IExpr arg1) {
      return Convert.checkNonEmptySquareMatrix(S.Inverse, arg1);
    }

    public static FieldMatrix<IExpr> inverseMatrix(FieldMatrix<IExpr> matrix,
        Predicate<IExpr> zeroChecker) {
      // @since version 1.9
      // final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix,
      // zeroChecker);
      final FieldLUDecomposition<IExpr> lu =
          new FieldLUDecomposition<IExpr>(matrix, zeroChecker, false);
      FieldDecompositionSolver<IExpr> solver = lu.getSolver();
      if (!solver.isNonSingular()) {
        // Matrix `1` is singular.
        IOFunctions.printMessage(S.Inverse, "sing", F.list(Convert.matrix2List(matrix, false)),
            EvalEngine.get());
        return null;
      }
      return solver.getInverse();
    }

    @Override
    public FieldMatrix<IExpr> matrixEval(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker) {
      return inverseMatrix(matrix, zeroChecker);
    }

    @Override
    public RealMatrix realMatrixEval(RealMatrix matrix) {
      final org.hipparchus.linear.LUDecomposition lu =
          new org.hipparchus.linear.LUDecomposition(matrix);
      DecompositionSolver solver = lu.getSolver();
      if (!solver.isNonSingular()) {
        // Matrix `1` is singular.
        IOFunctions.printMessage(S.Inverse, "sing", F.list(Convert.matrix2List(matrix, false)),
            EvalEngine.get());
        return null;
      }
      return solver.getInverse();
    }
  }

  /**
   *
   *
   * <pre>
   * JacobiMatrix(matrix, var)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * creates a Jacobian matrix.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Jacobian">Wikipedia - Jacobian</a>
   * </ul>
   */
  private static class JacobiMatrix extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isVector() >= 0) {
        IAST variables = F.NIL;
        if (ast.arg2().isSymbol()) {
          variables = F.CEmptyList;
        } else if (ast.arg2().isList() && ast.arg2().size() > 1) {
          variables = (IAST) ast.arg2();
        }
        if (variables.isPresent()) {
          int variablesSize = variables.size();
          if (ast.arg1().isAST()) {
            IAST vector = (IAST) ast.arg1();
            int vectorSize = vector.size();
            final IAST vars = variables;
            return F.mapRange(1, vectorSize,
                i -> F.mapRange(1, variablesSize, j -> F.D(vector.get(i), vars.get(j))));
          } else {
            FieldVector<IExpr> vector = Convert.list2Vector(ast.arg1());
            if (vector != null) {
              int vectorSize = vector.getDimension();
              final IAST vars = variables;
              return F.mapRange(1, vectorSize,
                  i -> F.mapRange(1, variablesSize, j -> F.D(vector.getEntry(i), vars.get(j))));
            }
          }
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * LeastSquares(matrix, right)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * solves the linear least-squares problem 'matrix . x = right'.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; LeastSquares(Table(Complex(i,Rational(2 * i + 2 + j, 1 + 9 * i + j)),{i,0,3},{j,0,2}), {1,1,1,1})
   * {-1577780898195/827587904419-I*11087326045520/827587904419,35583840059240/5793115330933+I*275839049310660/5793115330933,-3352155369084/827587904419-I*2832105547140/827587904419}
   * </pre>
   */
  private static class LeastSquares extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isMatrix() != null && ast.arg2().isVector() >= 0) {
        IAST matrix = (IAST) ast.arg1().normal(false);
        IAST vector = (IAST) ast.arg2().normal(false);
        if (matrix.isList() && vector.isList()) {
          try {
            if (matrix.isNumericMode() || vector.isNumericMode()) {
              RealMatrix realMatrix = ast.arg1().toRealMatrix();
              if (realMatrix != null) {
                RealVector realVector = ast.arg2().toRealVector();
                if (realVector != null) {
                  // for numerical stability use: Dot(PseudoInverse(matrix), vector)
                  realMatrix = PseudoInverse.CONST.realMatrixEval(realMatrix);
                  if (realMatrix != null) {
                    return new ASTRealVector(realMatrix.operate(realVector), false);
                  }
                }
              }
              return F.NIL;
            }
          } catch (MathIllegalArgumentException miae) {
            // `1`.
            return IOFunctions.printMessage(ast.topHead(), "error",
                F.list(F.$str(miae.getMessage())), engine);
          } catch (final MathRuntimeException mre) {
            // org.hipparchus.exception.MathIllegalArgumentException: inconsistent dimensions: 0 !=
            // 3
            LOGGER.log(engine.getLogLevel(), ast.topHead(), mre);
            return F.NIL;
          } catch (final ClassCastException | IndexOutOfBoundsException e) {
            LOGGER.debug("LeastSquares.evaluate() failed", e);
          }
          try {
            IAST matrixTransposed = (IAST) S.ConjugateTranspose.of(engine, matrix);
            return F.Expand(F.LinearSolve(F.ConjugateTranspose(F.Dot(matrixTransposed, matrix)),
                F.Dot(matrixTransposed, vector)));
          } catch (final ClassCastException | IndexOutOfBoundsException e) {
            LOGGER.debug("LeastSquares.evaluate() failed", e);
          }
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * LinearSolve(matrix, right)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * solves the linear equation system 'matrix . x = right' and returns one corresponding solution
   * <code>x</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; LinearSolve({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}, {1, 2, 3})
   * {0,1,2}
   * </pre>
   *
   * <p>
   * Test the solution:
   *
   * <pre>
   * &gt;&gt; {{1, 1, 0}, {1, 0, 1}, {0, 1, 1}} . {0, 1, 2}
   * {1,2,3}
   * </pre>
   *
   * <p>
   * If there are several solutions, one arbitrary solution is returned:
   *
   * <pre>
   * &gt;&gt; LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, 1, 1})
   * {-1,1,0}
   * </pre>
   *
   * <p>
   * Infeasible systems are reported:
   *
   * <pre>
   * &gt;&gt; LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, -2, 3})
   *  : Linear equation encountered that has no solution.
   * LinearSolve({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {1, -2, 3})
   * </pre>
   *
   * <p>
   * Argument {1, {2}} at position 1 is not a non-empty rectangular matrix.
   *
   * <pre>
   * &gt;&gt; LinearSolve({1, {2}}, {1, 2})
   * LinearSolve({1, {2}}, {1, 2})
   * &gt;&gt; LinearSolve({{1, 2}, {3, 4}}, {1, {2}})
   * LinearSolve({{1, 2}, {3, 4}}, {1, {2}})
   * </pre>
   */
  private static class LinearSolve extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final int[] matrixDims = ast.arg1().isMatrix();
      if (matrixDims != null) {
        boolean togetherMode = engine.isTogetherMode();
        engine.setTogetherMode(true);
        try {
          // TODO https://github.com/Hipparchus-Math/hipparchus/issues/144
          LinearSolveFunctionExpr<IExpr> lsf = null;
          IExpr temp = createLinearSolveFunction(ast, matrixDims, engine);
          if (temp instanceof LinearSolveFunctionExpr) {
            lsf = (LinearSolveFunctionExpr<IExpr>) temp;
          }
          if (ast.isAST1()) {
            return temp;
          }
          if (lsf != null) {
            return linearSolve(lsf, ast.arg2(), ast, engine);
          }
        } catch (final RuntimeException e) {
          LOGGER.debug("LinearSolve.evaluate() failed", e);
        } finally {
          engine.setTogetherMode(togetherMode);
        }
        try {
          final FieldMatrix<IExpr> matrix = Convert.list2Matrix(ast.arg1());
          if (ast.arg2().isVector() >= 0) {
            final FieldVector<IExpr> vector = Convert.list2Vector(ast.arg2());
            if (matrix != null && vector != null) {
              if (matrixDims[0] > matrixDims[1]) {
                if (vector.getDimension() == matrix.getRowDimension()
                    && vector.getDimension() <= matrix.getColumnDimension()) {
                  return underdeterminedSystem(matrix, vector, engine);
                }
                LOGGER.log(engine.getLogLevel(),
                    "LinearSolve: first argument is not a square matrix.");
                return F.NIL;
              }
              if (vector.getDimension() != matrix.getRowDimension()) {
                LOGGER.log(engine.getLogLevel(),
                    "LinearSolve: matrix row and vector have different dimensions.");
                return F.NIL;
              }
              if (matrixDims[0] == 1 && matrixDims[1] >= 1) {
                IExpr temp = eval1x1Matrix(matrix, vector, engine);
                if (temp.isPresent()) {
                  return temp;
                }
                return underdeterminedSystem(matrix, vector, engine);
              }
              if (matrixDims[0] == 2 && matrixDims[1] == 2) {
                IExpr temp = eval2x2Matrix(matrix, vector, engine);
                if (temp.isPresent()) {
                  return temp;
                }
                return underdeterminedSystem(matrix, vector, engine);
              }
              if (matrixDims[0] == 3 && matrixDims[1] == 3) {
                IExpr temp = eval3x3Matrix(matrix, vector, engine);
                if (temp.isPresent()) {
                  return temp;
                }
                return underdeterminedSystem(matrix, vector, engine);
              }
              if (matrixDims[0] != matrixDims[1]) {
                return underdeterminedSystem(matrix, vector, engine);
              }
              Predicate<IExpr> zeroChecker = AbstractMatrix1Expr.optionZeroTest(ast, 3, engine);
              // @since version 1.9
              // FieldDecompositionSolver<IExpr> solver =
              // new FieldLUDecomposition<IExpr>(matrix, zeroChecker).getSolver();
              FieldDecompositionSolver<IExpr> solver =
                  new FieldLUDecomposition<IExpr>(matrix, zeroChecker, false).getSolver();
              if (solver.isNonSingular()) {
                FieldVector<IExpr> resultVector = solver.solve(vector);
                for (int i = 0; i < resultVector.getDimension(); i++) {
                  if (resultVector.getEntry(i).isIndeterminate()
                      || resultVector.getEntry(i).isDirectedInfinity()) {
                    return underdeterminedSystem(matrix, vector, engine);
                  }
                }
                return Convert.vector2List(resultVector);
              } else {
                return underdeterminedSystem(matrix, vector, engine);
              }
            }
          }
        } catch (LimitException le) {
          throw le;
        } catch (final RuntimeException e) {
          LOGGER.debug("LinearSolve.evaluate() failed", e);
        }
      }

      return F.NIL;
    }

    private static IExpr createLinearSolveFunction(final IAST ast, final int[] matrixDims,
        EvalEngine engine) {
      if (matrixDims[0] > matrixDims[1]) {
        LOGGER.log(engine.getLogLevel(), "LinearSolve: first argument is not a square matrix.");
        return F.NIL;
      }

      final FieldMatrix<IExpr> matrix = Convert.list2Matrix(ast.arg1(), false);
      if (matrix != null) {
        Predicate<IExpr> zeroChecker = AbstractMatrix1Expr.optionZeroTest(ast, 2, engine);
        FieldDecompositionSolver<IExpr> solver =
            new FieldLUDecomposition<IExpr>(matrix, zeroChecker, false).getSolver();
        if (solver.isNonSingular()) {
          return LinearSolveFunctionExpr.createIExpr(solver, engine.getNumericPrecision());
        }
        if (ast.isAST1()) {
          // The matrix `1` is singular; a factorization will not be saved.
          return IOFunctions.printMessage(ast.topHead(), "sing1", F.list(ast.arg1()), engine);
        }
        return F.NIL;
      }

      final FieldMatrix<Complex> complexMatrix = Convert.list2ComplexMatrix(ast.arg1());
      if (complexMatrix != null) {
        Predicate<Complex> zeroChecker = c -> F.isZero(c);
        FieldDecompositionSolver<Complex> solver =
            new FieldLUDecomposition<Complex>(complexMatrix, zeroChecker, true).getSolver();
        if (solver.isNonSingular()) {
          return LinearSolveFunctionExpr.createComplex(solver);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    /**
     * For a underdetermined system, return one of the possible solutions through a row reduced
     * matrix.
     *
     * @param matrix
     * @param vector
     * @param engine
     * @return
     */
    private IExpr underdeterminedSystem(final FieldMatrix<IExpr> matrix, FieldVector<IExpr> vector,
        EvalEngine engine) {
      FieldMatrix<IExpr> augmentedMatrix = Convert.augmentedFieldMatrix(matrix, vector);
      if (augmentedMatrix != null) {
        return LinearAlgebra.rowReduced2List(augmentedMatrix, true, engine);
      }
      return F.NIL;
    }

    /**
     * Solve <code>matrix . vector</code> for a <code>1 x 1</code> matrix
     *
     * @param matrix
     * @param vector
     * @param engine
     * @return
     */
    private IExpr eval1x1Matrix(FieldMatrix<IExpr> matrix, FieldVector<IExpr> vector,
        EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(matrix.getColumnDimension());
      IExpr a = matrix.getEntry(0, 0);
      IExpr x = vector.getEntry(0);
      // x/a
      IExpr row = F.Divide(x, a);
      result.append(row);
      if (matrix.getColumnDimension() > 1) {
        for (int i = 1; i < matrix.getColumnDimension(); i++) {
          result.append(F.C0);
        }
      }
      IExpr temp = engine.evalQuiet(result);
      if (temp.isAST()) {
        IAST ast = (IAST) temp;
        for (int k = 1; k < ast.size(); k++) {
          if (ast.get(k).isIndeterminate() || ast.get(k).isDirectedInfinity()) {
            return F.NIL;
          }
        }
      }
      return temp;
    }

    /**
     * Solve <code>matrix . vector</code> for a <code>2 x 2</code> matrix
     *
     * @param matrix
     * @param vector
     * @param engine
     * @return
     */
    private IExpr eval2x2Matrix(FieldMatrix<IExpr> matrix, FieldVector<IExpr> vector,
        EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(matrix.getColumnDimension());
      IExpr a = matrix.getEntry(0, 0);
      IExpr b = matrix.getEntry(0, 1);
      IExpr c = matrix.getEntry(1, 0);
      IExpr d = matrix.getEntry(1, 1);

      IExpr x = vector.getEntry(0);
      IExpr y = vector.getEntry(1);
      // (d*x-b*y)/((-b)*c+a*d)
      IExpr row = F.Times(F.Power(F.Plus(F.Times(F.CN1, b, c), F.Times(a, d)), -1),
          F.Plus(F.Times(d, x), F.Times(F.CN1, b, y)));
      result.append(row);
      // (c*x-a*y)/(b*c-a*d)
      row = F.Times(F.Power(F.Plus(F.Times(b, c), F.Times(F.CN1, a, d)), -1),
          F.Plus(F.Times(c, x), F.Times(F.CN1, a, y)));
      result.append(row);

      if (matrix.getColumnDimension() > 2) {
        for (int i = 2; i < matrix.getColumnDimension(); i++) {
          result.append(F.C0);
        }
      }
      IExpr temp = engine.evalQuiet(result);
      if (temp.isAST()) {
        IAST ast = (IAST) temp;
        for (int k = 1; k < ast.size(); k++) {
          if (ast.get(k).isIndeterminate() || ast.get(k).isDirectedInfinity()) {
            return F.NIL;
          }
        }
      }
      return temp;
    }

    /**
     * Solve <code>matrix . vector</code> for a <code>3 x 3</code> matrix
     *
     * @param matrix
     * @param vector
     * @param engine
     * @return
     */
    private IExpr eval3x3Matrix(FieldMatrix<IExpr> matrix, FieldVector<IExpr> vector,
        EvalEngine engine) {
      IASTAppendable result = F.ListAlloc(matrix.getColumnDimension());
      IExpr a = matrix.getEntry(0, 0);
      IExpr b = matrix.getEntry(0, 1);
      IExpr c = matrix.getEntry(0, 2);
      IExpr d = matrix.getEntry(1, 0);
      IExpr e = matrix.getEntry(1, 1);
      IExpr f = matrix.getEntry(1, 2);
      IExpr g = matrix.getEntry(2, 0);
      IExpr h = matrix.getEntry(2, 1);
      IExpr i = matrix.getEntry(2, 2);

      IExpr x = vector.getEntry(0);
      IExpr y = vector.getEntry(1);
      IExpr z = vector.getEntry(2);
      // (f*h*x-e*i*x-c*h*y+b*i*y+c*e*z-b*f*z)/(c*e*g-b*f*g-c*d*h+a*f*h+b*d*i-a*e*i)
      IExpr row = F.Times(
          F.Power(F.Plus(F.Times(c, e, g), F.Times(F.CN1, b, f, g), F.Times(F.CN1, c, d, h),
              F.Times(a, f, h), F.Times(b, d, i), F.Times(F.CN1, a, e, i)), -1),
          F.Plus(F.Times(f, h, x), F.Times(F.CN1, e, i, x), F.Times(F.CN1, c, h, y),
              F.Times(b, i, y), F.Times(c, e, z), F.Times(F.CN1, b, f, z)));
      result.append(row);
      // ((-f)*g*x+d*i*x+c*g*y-a*i*y-c*d*z+a*f*z)/(c*e*g-b*f*g-c*d*h+a*f*h+b*d*i-a*e*i)
      row = F.Times(
          F.Power(F.Plus(F.Times(c, e, g), F.Times(F.CN1, b, f, g), F.Times(F.CN1, c, d, h),
              F.Times(a, f, h), F.Times(b, d, i), F.Times(F.CN1, a, e, i)), -1),
          F.Plus(F.Times(F.CN1, f, g, x), F.Times(d, i, x), F.Times(c, g, y),
              F.Times(F.CN1, a, i, y), F.Times(F.CN1, c, d, z), F.Times(a, f, z)));
      result.append(row);
      // (e*g*x-d*h*x-b*g*y+a*h*y+b*d*z-a*e*z)/(c*e*g-b*f*g-c*d*h+a*f*h+b*d*i-a*e*i)
      row = F.Times(
          F.Power(F.Plus(F.Times(c, e, g), F.Times(F.CN1, b, f, g), F.Times(F.CN1, c, d, h),
              F.Times(a, f, h), F.Times(b, d, i), F.Times(F.CN1, a, e, i)), -1),
          F.Plus(F.Times(e, g, x), F.Times(F.CN1, d, h, x), F.Times(F.CN1, b, g, y),
              F.Times(a, h, y), F.Times(b, d, z), F.Times(F.CN1, a, e, z)));
      result.append(row);

      if (matrix.getColumnDimension() > 3) {
        for (int k = 3; k < matrix.getColumnDimension(); k++) {
          result.append(F.C0);
        }
      }

      IExpr temp = engine.evalQuiet(result);
      if (temp.isAST()) {
        IAST ast = (IAST) temp;
        for (int k = 1; k < ast.size(); k++) {
          if (ast.get(k).isIndeterminate() || ast.get(k).isDirectedInfinity()) {
            return F.NIL;
          }
        }
      }
      return temp;
    }
  }

  private static class LinearSolveFunction extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.head() instanceof LinearSolveFunctionExpr && ast.isAST1()) {
        LinearSolveFunctionExpr<?> lsf = (LinearSolveFunctionExpr<?>) ast.head();
        IExpr arg1 = ast.arg1();
        return linearSolve(lsf, arg1, ast, engine);
      }
      return F.NIL;
    }
  }

  private static class LowerTriangularize extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int[] dim = ast.arg1().isMatrix(false);
      if (dim == null) {
        // Argument `1` at position `2` is not a non-empty rectangular matrix.
        return IOFunctions.printMessage(ast.topHead(), "matrix", F.list(ast.arg1(), F.C1), engine);
      }

      final int k;
      if (ast.size() == 3) {
        k = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
      } else {
        k = 0;
      }
      int m = dim[0];
      int n = dim[1];
      FieldMatrix<IExpr> matrix = Convert.list2Matrix(ast.arg1());
      return F.matrix((i, j) -> i >= j - k ? matrix.getEntry(i, j) : F.C0, m, n);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * LUDecomposition(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculate the LUP-decomposition of a square <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/LU_decomposition">Wikipedia - LU decomposition</a>
   * <li><a href=
   * "http://commons.apache.org/proper/commons-math/apidocs/org/apache/commons/math3/linear/FieldLUDecomposition.html">Commons
   * Math - Class FieldLUDecomposition</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt;&gt; LUDecomposition[{{1, 2, 3}, {3, 4, 11}, {13, 7, 8}}]
   * {{{1,0,0},
   *   {3,1,0},
   *   {13,19/2,1}},
   *  {{1,2,3},
   *   {0,-2,2},
   *   {0,0,-50}},{1,2,3}}
   * </pre>
   */
  private static class LUDecomposition extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      FieldMatrix<IExpr> matrix;
      boolean togetherMode = engine.isTogetherMode();
      try {
        engine.setTogetherMode(true);
        int[] dim = ast.arg1().isMatrix();
        if (dim != null && dim[0] > 0 && dim[1] > 0) {
          if (dim[0] != dim[1]) {
            // Argument `1` at position `2` is not a non-empty square matrix.
            return IOFunctions.printMessage(ast.topHead(), "matsq", F.CEmptyList, engine);
          }
          matrix = Convert.list2Matrix(ast.arg1());
          if (matrix != null) {

            Predicate<IExpr> zeroChecker = AbstractMatrix1Expr.optionZeroTest(ast, 2, engine);
            // @since version 1.9
            // final FieldLUDecomposition<IExpr> lu = new FieldLUDecomposition<IExpr>(matrix,
            // zeroChecker);
            final FieldLUDecomposition<IExpr> lu =
                new FieldLUDecomposition<IExpr>(matrix, zeroChecker, false);
            final FieldMatrix<IExpr> lMatrix = lu.getL();
            final FieldMatrix<IExpr> uMatrix = lu.getU();
            final int[] iArr = lu.getPivot();
            // final int permutationCount = lu.getPermutationCount();
            int size = iArr.length;
            final IASTAppendable iList = F.ListAlloc(size);
            // +1 because in Symja the offset is +1 compared to java arrays
            iList.appendArgs(0, size, i -> F.ZZ(iArr[i] + 1));
            IASTAppendable m1 = Convert.matrix2List(lMatrix);
            if (m1.isPresent()) {
              IASTAppendable m2 = Convert.matrix2List(uMatrix);
              if (m2.isPresent()) {
                return F.list(m1, m2, iList);
              }
            }
          }
        }
      } catch (IndexOutOfBoundsException | ClassCastException e) {
        LOGGER.debug("LUDecomposition.evaluate() failed", e);
      } finally {
        engine.setTogetherMode(togetherMode);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class MatrixExp extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int[] dim = ast.arg1().isMatrix();
      if (dim != null && dim[0] == dim[1] && dim[0] > 0) {
        RealMatrix matrix = ast.arg1().toRealMatrix();
        if (matrix != null) {
          try {
            RealMatrix result = MatrixUtils.matrixExponential(matrix);
            return new ASTRealMatrix(result, false);
          } catch (MathIllegalArgumentException miae) {
            return IOFunctions.printMessage(ast.topHead(), "error",
                F.list(F.stringx(miae.getMessage())), engine);
          }
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class MatrixFunction extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int[] dimensions = ast.arg2().isMatrix();
      if (dimensions != null) {
        if (dimensions[0] == 0 || dimensions[1] == 0) {
          return ast.arg2();
        }
        // TODO
        // final IExpr function = ast.arg1();
        // FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(ast.arg2());
        // return Convert.matrix2Expr(fieldMatrix);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }


  private static class MatrixLog extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {

      int[] dim = ast.arg1().isMatrix();
      if (dim != null && dim[0] == dim[1] && dim[0] > 0) {
        RealMatrix matrix = ast.arg1().toRealMatrix();
        if (matrix != null) {
          // TODO
          // FieldMatrix<IExpr> fieldMatrix = Convert.list2Matrix(ast.arg2());
          // return Convert.matrix2Expr(fieldMatrix);
        }
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * MatrixMinimalPolynomial(matrix, var)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the matrix minimal polynomial of a <code>matrix</code> for the variable <code>var
   * </code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href= "https://en.wikipedia.org/wiki/Minimal_polynomial_(linear_algebra)">Wikipedia -
   * Minimal polynomial (linear algebra)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MatrixMinimalPolynomial({{1, -1, -1}, {1, -2, 1}, {0, 1, -3}}, x)
   * -1+x+4*x^2+x^3
   * </pre>
   */
  private static class MatrixMinimalPolynomial extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int[] dimensions = ast.arg1().isMatrix(false);
      if (dimensions != null && dimensions[0] == dimensions[1] && dimensions[0] > 0) {
        // a matrix with square dimensions
        IExpr matrix = ast.arg1();
        IExpr variable = ast.arg2();
        if (!variable.isVariable()) {
          // `1` is not a valid variable.
          return IOFunctions.printMessage(ast.topHead(), "ivar", F.list(variable), engine);
        }
        ISymbol i = F.Dummy("i"); // new Symbol("§i", Context.SYSTEM);
        int n = 1;
        IAST qu = F.CEmptyList;
        IAST mnm = (IAST) engine
            .evaluate(F.list(F.Flatten(diagonalMatrix(new IExpr[] {F.C0, F.C1}, dimensions[0]))));
        if (!(mnm instanceof IASTAppendable)) {
          mnm = mnm.copyAppendable(2);
        }
        while (qu.isEmpty()) {
          ((IASTAppendable) mnm).append(engine.evaluate(F.Flatten(F.MatrixPower(matrix, F.ZZ(n)))));
          qu = (IAST) S.NullSpace.of(engine, F.Transpose(mnm));
          n++;
        }
        return S.Dot.of(engine, qu.arg1(),
            F.Table(F.Power(variable, i), F.list(i, F.C0, F.ZZ(--n))));
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * MatrixPower(matrix, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the <code>n</code>th power of a <code>matrix</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MatrixPower({{1, 2}, {1, 1}}, 10)
   * {{3363,4756},
   *  {2378,3363}}
   *
   * &gt;&gt; MatrixPower({{1, 2}, {2, 5}}, -3)
   * {{169,-70},
   *  {-70,29}}
   * </pre>
   *
   * <p>
   * Argument {{1, 0}, {0}} at position 1 is not a non-empty rectangular matrix.
   *
   * <pre>
   * &gt;&gt; MatrixPower({{1, 0}, {0}}, 2)
   * MatrixPower({{1, 0}, {0}}, 2)
   * </pre>
   */
  private static final class MatrixPower extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      FieldMatrix<IExpr> matrix;
      FieldMatrix<IExpr> resultMatrix;
      boolean togetherMode = engine.isTogetherMode();
      try {
        engine.setTogetherMode(true);
        final IExpr arg1 = ast.arg1();
        final IExpr arg2 = ast.arg2();
        int[] dimensions = arg1.isMatrix(false);
        if (dimensions != null && dimensions[1] > 0 && dimensions[0] > 0) {
          matrix = Convert.list2Matrix(arg1);
          if (matrix == null) {
            return F.NIL;
          }

          int p = arg2.toIntDefault();
          if (p == Integer.MIN_VALUE) {
            return F.NIL;
          }
          if (p == 1) {
            ((IAST) arg1).addEvalFlags(IAST.IS_MATRIX);
            return arg1;
          }
          if (p == 0) {
            resultMatrix = new BlockFieldMatrix<IExpr>(F.EXPR_FIELD, matrix.getRowDimension(),
                matrix.getColumnDimension());
            int min = matrix.getRowDimension();
            if (min > matrix.getColumnDimension()) {
              min = matrix.getColumnDimension();
            }
            for (int i = 0; i < min; i++) {
              resultMatrix.setEntry(i, i, F.C1);
            }

            return Convert.matrix2List(resultMatrix);
          }
          int iterationLimit = engine.getIterationLimit();
          if (iterationLimit >= 0 && iterationLimit <= p) {
            IterationLimitExceeded.throwIt(p, ast);
          }

          if (matrix.getRowDimension() != matrix.getColumnDimension()) {
            // Argument `1` at position `2` is not a non-empty square matrix.
            return IOFunctions.printMessage(ast.topHead(), "matsq", F.list(arg1, F.C1), engine);
          }
          if (p < 0) {
            resultMatrix = Inverse.inverseMatrix(matrix, x -> x.isPossibleZero(false));
            matrix = resultMatrix;
            p *= (-1);
          } else {
            resultMatrix = matrix;
          }
          for (int i = 1; i < p; i++) {
            resultMatrix = resultMatrix.multiply(matrix);
          }
          return Convert.matrix2List(resultMatrix);
        }
        return F.NIL;
      } catch (final RuntimeException e) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), e);
        return F.NIL;
      } finally {
        engine.setTogetherMode(togetherMode);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   *
   *
   * <pre>
   * MatrixRank(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the rank of <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href= "https://en.wikipedia.org/wiki/Rank_%28linear_algebra%29">Wikipedia - Rank (linear
   * algebra</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; MatrixRank({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})
   * 2
   * &gt;&gt; MatrixRank({{1, 1, 0}, {1, 0, 1}, {0, 1, 1}})
   * 3
   * &gt;&gt; MatrixRank({{a, b}, {3 a, 3 b}})
   * 1
   * </pre>
   *
   * <p>
   * Argument <code>{{1, 0}, {0}}</code> at position <code>1</code> is not a non-empty rectangular
   * matrix.
   *
   * <pre>
   * &gt;&gt; MatrixRank({{1, 0}, {0}})
   * MatrixRank({{1, 0}, {0}})
   * </pre>
   */
  private static final class MatrixRank extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      FieldMatrix<IExpr> matrix;
      try {
        IExpr arg1 = engine.evaluate(ast.arg1());
        if (arg1.isMatrix() != null) {
          matrix = Convert.list2Matrix(arg1);
          if (matrix != null) {
            Predicate<IExpr> zeroChecker = AbstractMatrix1Expr.optionZeroTest(ast, 2, engine);
            FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix, zeroChecker);
            return F.ZZ(fmw.getMatrixRank());
          }
        }

      } catch (final ClassCastException | IndexOutOfBoundsException e) {
        LOGGER.debug("MatrixRank.evaluate() failed", e);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL);
    }
  }

  // private static final class Minor extends AbstractFunctionEvaluator {
  //
  // @Override
  // public IExpr evaluate(final IAST ast, EvalEngine engine) {
  // IExpr arg1 = ast.arg1();
  // IExpr arg2 = ast.arg2();
  // int[] dims = arg1.isMatrix();
  // if (dims != null && arg2.isList2()) {
  // if (dims[0] != dims[1]) {
  // // TODO error message
  // return F.NIL;
  // }
  // IAST matrix = (IAST) arg1;
  // int i = arg2.first().toIntDefault();
  // int j = arg2.second().toIntDefault();
  // if (i <= 0 || j <= 0) {
  // // TODO error message
  // return F.NIL;
  // }
  // if (i > dims[0] || j > dims[1]) {
  // // TODO error message
  // return F.NIL;
  // }
  // if (i + j < 0) {
  // // TODO overflow error message
  // return F.NIL;
  // }
  // // (-1)^(i + j)*Det(Drop(matrix, {i}, {j}))
  // return F.Det(
  // F.Drop(matrix, F.list(F.ZZ(i)), F.list(F.ZZ(j))) //
  // );
  // }
  // return F.NIL;
  // }
  //
  // @Override
  // public int[] expectedArgSize(IAST ast) {
  // return ARGS_2_2;
  // }
  // }

  private static final class Minors extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int[] dims = arg1.isMatrix();
      if (dims != null) {
        if (dims[0] != dims[1]) {
          // TODO error message
          return F.NIL;
        }
        if (dims[0] <= 1) {
          // TODO error message
          return F.NIL;
        }
        if (arg1.isSparseArray()) {
          arg1 = arg1.normal(false);
        }
        int n = dims[0];
        IAST matrix = (IAST) arg1;
        IASTAppendable result = F.ListAlloc(n + 1);
        for (int i = 1; i <= n; i++) {
          IASTAppendable row = F.ListAlloc(n + 1);
          for (int j = 1; j <= n; j++) {
            IExpr det = engine.evaluate(F.Det( //
                F.Drop(matrix, F.list(F.ZZ(n - i + 1)), F.list(F.ZZ(n - j + 1)))));
            row.append(det);
          }
          result.append(row);
        }
        return result;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }
  /**
   * 0]
   *
   * <pre>
   * Norm(m, l)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the <code>l</code>-norm of matrix <code>m</code> (currently only works for
   * vectors!).<br>
   *
   * </blockquote>
   *
   * <pre>
   * Norm(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the 2-norm of matrix <code>m</code> (currently only works for vectors!).<br>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Norm({1, 2, 3, 4}, 2)
   * Sqrt(30)
   *
   * &gt;&gt; Norm({10, 100, 200}, 1)
   * 310
   *
   * &gt;&gt; Norm({a, b, c})
   * Sqrt(Abs(a)^2+Abs(b)^2+Abs(c)^2)
   *
   * &gt;&gt; Norm({-100, 2, 3, 4}, Infinity)
   * 100
   *
   * &gt;&gt; Norm(1 + I)
   * Sqrt(2)
   * </pre>
   *
   * <p>
   * The first Norm argument should be a number, vector, or matrix.<br>
   *
   * <pre>
   * &gt;&gt; Norm({1, {2, 3}})
   * Norm({1, {2, 3}})
   *
   * &gt;&gt; Norm({x, y})
   * Sqrt(Abs(x)^2+Abs(y)^2)
   *
   * &gt;&gt; Norm({x, y}, p)
   * (Abs(x) ^ p + Abs(y) ^ p) ^ (1 / p)
   * </pre>
   *
   * <p>
   * The second argument of Norm, 0, should be a symbol, Infinity, or an integer or real number not
   * less than 1 for vector p-norms; or 1, 2, Infinity, or &ldquo;Frobenius&rdquo; for matrix
   * norms.<br>
   *
   * <pre>
   * &gt;&gt; Norm({x, y}, 0)
   * Norm({x, y}, 0)
   * </pre>
   *
   * <p>
   * The second argument of Norm, 0.5, should be a symbol, Infinity, or an integer or real number
   * not less than 1 for vector p-norms; or 1, 2, Infinity, or &ldquo;Frobenius&rdquo; for matrix
   * norms.
   *
   * <pre>
   * &gt;&gt; Norm({x, y}, 0.5)
   * Norm({x, y}, 0.5)
   *
   * &gt;&gt; Norm({})
   * Norm({})
   *
   * &gt;&gt; Norm(0)
   * 0
   * </pre>
   */
  private static final class Norm extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int dim = arg1.isVector();
      if (dim > (-1)) {
        if (dim == 0) {
          // The first Norm argument should be a scalar, vector or matrix.
          return IOFunctions.printMessage(ast.topHead(), "nvm", F.CEmptyList, engine);
        }
        arg1 = arg1.normal(false);
        if (arg1.isList()) {
          IAST vector = (IAST) arg1;
          if (ast.isAST2()) {
            IExpr p = ast.arg2();
            if (p.isString("Frobenius")) {
              return F.Norm(vector);
            } else if (p.isInfinity()) {
              return vector.map(S.Max, x -> F.Abs(x));
            } else if (p.isSymbol() || p.isReal()) {
              if (p.isZero()) {
                LOGGER.log(engine.getLogLevel(), "Norm: 0 not allowed as second argument!");
                return F.NIL;
              }
              if (p.isReal() && p.lessThan(F.C1).isTrue()) {
                LOGGER.log(engine.getLogLevel(), "Norm: Second argument is < 1!");
                return F.NIL;
              }
              return F.Power(vector.map(S.Plus, x -> F.Power(F.Abs(x), p)), p.inverse());
            }

            return F.NIL;
          }
          return F.Sqrt(vector.map(S.Plus, x -> F.Sqr(F.Abs(x))));
        }
        return F.NIL;
      }
      int[] matrixDim = arg1.isMatrix();
      if (matrixDim != null) {
        if (matrixDim[1] == 0) {
          // The first Norm argument should be a scalar, vector or matrix.
          return IOFunctions.printMessage(ast.topHead(), "nvm", F.CEmptyList, engine);
        }
        RealMatrix matrix;
        try {
          matrix = arg1.toRealMatrix();
          if (matrix != null) {
            if (ast.isAST2() && ast.arg2().isString("Frobenius")) {
              return F.Norm(F.Flatten(arg1));
            }
            if (matrixDim[0] < matrixDim[1]) {
              int d = matrixDim[0];
              matrixDim[0] = matrixDim[1];
              matrixDim[1] = d;
              matrix = matrix.transpose();
            }
            final org.hipparchus.linear.SingularValueDecomposition svd =
                new org.hipparchus.linear.SingularValueDecomposition(matrix);
            RealMatrix sSVD = svd.getS();
            IASTAppendable result = F.ast(S.Max, matrixDim[1]);
            for (int i = 0; i < matrixDim[1]; i++) {
              result.append(sSVD.getEntry(i, i));
            }
            return result;
          }
        } catch (final IndexOutOfBoundsException e) {
          LOGGER.debug("Norm.evaluate() failed", e);
        }
      }
      if (arg1.isNumber()) {
        if (ast.isAST2()) {
          return F.NIL;
        }
        // absolute Value of a number
        return ((INumber) arg1).abs();
      }
      if (arg1.isNumericFunction(true) && !arg1.isList()) {
        if (ast.isAST2()) {
          return F.NIL;
        }
        // absolute Value
        return F.Abs(arg1);
      }
      // The first Norm argument should be a scalar, vector or matrix.
      return IOFunctions.printMessage(ast.topHead(), "nvm", F.CEmptyList, engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * Normalize(v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculates the normalized vector <code>v</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Normalize(z)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculates the normalized complex number <code>z</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Normalize({1, 1, 1, 1})
   * {1/2,1/2,1/2,1/2}
   *
   * &gt;&gt; Normalize(1 + I)
   * (1+I)/Sqrt(2)
   *
   * &gt;&gt; Normalize(0)
   * 0
   *
   * &gt;&gt; Normalize({0})
   * {0}
   *
   * &gt;&gt; Normalize({})
   * {}
   * </pre>
   */
  private static final class Normalize extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr normFunction = S.Norm;
      if (ast.isAST2()) {
        normFunction = ast.arg2();
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isEmptyList() && ast.isAST1()) {
        return arg1;
      }
      IExpr norm = engine.evaluate(F.unaryAST1(normFunction, ast.arg1()));
      if (norm.isZero()) {
        return arg1;
      }
      return F.Divide(ast.arg1(), norm);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * NullSpace(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of vectors that span the nullspace of the <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href= "http://en.wikipedia.org/wiki/Kernel_%28linear_algebra%29">Wikipedia - Kernel
   * (linear algebra)</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; NullSpace({{1,0,-3,0,2,-8},{0,1,5,0,-1,4},{0,0,0,1,7,-9},{0,0,0,0,0,0}})
   * {{3,-5,1,0,0,0},
   *  {-2,1,0,-7,1,0},
   *  {8,-4,0,9,0,1}}
   * </pre>
   *
   * <pre>
   * ```
   * &gt;&gt; NullSpace({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})
   * {{1,-2,1}}
   *
   * &gt;&gt; A = {{1, 1, 0}, {1, 0, 1}, {0, 1, 1}}
   * &gt;&gt; NullSpace(A)
   * {}
   *
   * &gt;&gt; MatrixRank(A)
   * 3
   * </pre>
   *
   * <p>
   * Argument {1, {2}} at position 1 is not a non-empty rectangular matrix.<br>
   *
   * <pre>
   * &gt;&gt; NullSpace({1, {2}})
   * NullSpace({1, {2}})
   * </pre>
   */
  private static class NullSpace extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      FieldMatrix<IExpr> matrix;
      boolean togetherMode = engine.isTogetherMode();
      try {
        engine.setTogetherMode(true);
        int[] dims = ast.arg1().isMatrix();
        if (dims != null) {
          matrix = Convert.list2Matrix(ast.arg1());
          if (matrix != null) {
            FieldReducedRowEchelonForm fmw =
                new FieldReducedRowEchelonForm(matrix, AbstractMatrix1Expr.POSSIBLE_ZEROQ_TEST);
            FieldMatrix<IExpr> nullspace = fmw.getNullSpace(F.CN1);
            if (nullspace == null) {
              return F.CEmptyList;
            }

            IASTMutable list2 = Convert.matrix2List(nullspace);
            // rows in descending orders
            EvalAttributes.sort(list2, Comparators.REVERSE_CANONICAL_COMPARATOR);
            return list2;
          }
        }
      } catch (final MathRuntimeException | ClassCastException | IndexOutOfBoundsException e) {
        // org.hipparchus.exception.MathIllegalArgumentException
        LOGGER.debug("NullSpace.evaluate() failed", e);
      } finally {
        engine.setTogetherMode(togetherMode);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  private static class Orthogonalize extends AbstractEvaluator {

    static IBuiltInSymbol oneStep = F.localBiFunction("oneStep", (vec, vecmat) -> {
      if (vecmat.isEmptyList()) {
        return vec;
      }
      IExpr function = // [$ (#1-(vec.#2)/(#2.#2)*#2)& $]
          F.Function(F.Plus(F.Slot1, F.Times(F.CN1, F.Dot(vec, F.Slot2),
              F.Power(F.Dot(F.Slot2, F.Slot2), F.CN1), F.Slot2))); // $$;
      return F.eval(F.Fold(function, vec, vecmat));
    });

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      int[] dim = arg1.isMatrix();
      if (dim != null) {
        boolean isNumeric = arg1.isNumericMode();
        if (isNumeric) {
          RealMatrix realMatrix = arg1.toRealMatrix();
          if (realMatrix != null) {
            final int length = realMatrix.getRowDimension();
            ArrayList<RealVector> arrayList = new ArrayList<RealVector>(length);
            for (int i = 0; i < length; i++) {
              arrayList.add(realMatrix.getRowVector(i));
            }
            try {
              final List<RealVector> basis = MatrixUtils.orthonormalize(arrayList,
                  Config.DOUBLE_TOLERANCE, DependentVectorsHandler.ADD_ZERO_VECTOR);
              if (basis != null) {
                return F.mapRange(0, length, i -> Convert.realVectors2List(basis.get(i)));
              }
            } catch (MathIllegalArgumentException mex) {
              return IOFunctions.printMessage(ast.topHead(), mex, engine);
            }
            return F.NIL;
          } else {
            FieldMatrix<Complex> list2ComplexMatrix = Convert.list2ComplexMatrix(arg1);
            if (list2ComplexMatrix != null) {
              org.hipparchus.complex.ComplexField field = ComplexField.getInstance();
              final int length = list2ComplexMatrix.getRowDimension();
              ArrayList<FieldVector<Complex>> arrayList =
                  new ArrayList<FieldVector<Complex>>(length);
              for (int i = 0; i < length; i++) {
                arrayList.add(list2ComplexMatrix.getRowVector(i));
              }
              try {
                final List<FieldVector<Complex>> basis = MatrixUtils.orthonormalize(field,
                    arrayList, new Complex(Config.DOUBLE_TOLERANCE, Config.DOUBLE_TOLERANCE),
                    DependentVectorsHandler.ADD_ZERO_VECTOR);
                if (basis != null) {
                  return F.mapRange(0, length, i -> Convert.complexVector2List(basis.get(i)));
                }
              } catch (MathIllegalArgumentException mex) {
                return IOFunctions.printMessage(ast.topHead(), mex, engine);
              }
            }
            return F.NIL;
          }
        }

        // Gram-Schmidt orthogonalization
        IExpr result = F.Map(F.Function(F.Normalize(F.Slot1)), //
            F.Fold(F.Function(F.Append(F.Slot1, F.binaryAST2(oneStep, F.Slot2, F.Slot1))),
                F.CEmptyList, arg1));

        return engine.evaluate(result);

      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  private static class PauliMatrix extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isInteger()) {
        int m = ast.arg1().toIntDefault();
        if (m < 0) {
          return F.NIL;
        }
        switch (m) {
          case 0:
            // identity matrix
            return F.list(F.list(F.C1, F.C0), F.list(F.C0, F.C1));
          case 1:
            return F.list(F.list(F.C0, F.C1), F.list(F.C1, F.C0));
          case 2:
            return F.list(F.list(F.C0, F.CNI), F.list(F.CI, F.C0));
          case 3:
            return F.list(F.list(F.C1, F.C0), F.list(F.C0, F.CN1));
          case 4:
            // identity matrix
            return F.list(F.list(F.C1, F.C0), F.list(F.C0, F.C1));
          default:
            break;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Projection(vector1, vector2)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Find the orthogonal projection of <code>vector1</code> onto another <code>vector2</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Projection(vector1, vector2, ipf)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * Find the orthogonal projection of <code>vector1</code> onto another <code>vector2</code> using
   * the inner product function <code>ipf</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Projection({5, I, 7}, {1, 1, 1})
   * {4+I*1/3,4+I*1/3,4+I*1/3}
   * </pre>
   */
  private static class Projection extends AbstractEvaluator {

    /**
     * Create a binary function &quot"dot product&quot; <code>head(u,v)</code> and evaluate it.
     *
     * @param head the header of the binary function
     * @param u the first argument of the function
     * @param v the second argument of the function
     * @param engine the evaluation engine
     * @return the evaluated <code>head(u,v)</code> AST.
     */
    private static IExpr dotProduct(IExpr head, IExpr u, IExpr v, EvalEngine engine) {
      return engine.evaluate(F.binaryAST2(head, u, v));
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();
      int dim1 = arg1.isVector();
      int dim2 = arg2.isVector();

      if (ast.size() == 4) {
        IExpr head = ast.arg3();
        if (dim1 >= 0) {
          if (dim1 != dim2) {
            // The vectors `1` and `2` have different lengths.
            return IOFunctions.printMessage(ast.topHead(), "length", F.List(arg1, arg2), engine);
          }
          if (dim1 == 0) {
            return F.CEmptyList;
          }
          if (head.equals(S.Dot)) {
            FieldVector<IExpr> u = Convert.list2Vector(arg1);
            FieldVector<IExpr> v = Convert.list2Vector(arg2);
            if (u != null && v != null) {
              return Convert.vector2List(u.projection(v));
            }
          }
        }
        IExpr u = arg1;
        IExpr v = arg2;
        return v.times(dotProduct(head, u, v, engine).divide(dotProduct(head, v, v, engine)));
      }
      if (dim1 >= 0) {
        if (dim1 != dim2) {
          // The vectors `1` and `2` have different lengths.
          return IOFunctions.printMessage(ast.topHead(), "length", F.List(arg1, arg2), engine);
        }
        if (dim1 == 0) {
          return F.CEmptyList;
        }
        FieldVector<IExpr> u = Convert.list2Vector(arg1);
        FieldVector<IExpr> v = Convert.list2Vector(arg2);
        FieldVector<IExpr> vConjugate = v.copy();
        for (int i = 0; i < dim2; i++) {
          vConjugate.setEntry(i, vConjugate.getEntry(i).conjugate());
        }

        return Convert
            .vector2List(v.mapMultiply(u.dotProduct(vConjugate).divide(v.dotProduct(vConjugate))));
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * PseudoInverse(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the Moore-Penrose pseudoinverse of the <code>matrix</code>. If <code>matrix</code> is
   * invertible, the pseudoinverse equals the inverse.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href= "https://en.wikipedia.org/wiki/Moore%E2%80%93Penrose_pseudoinverse">Wikipedia:
   * Moore-Penrose pseudoinverse</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; PseudoInverse({{1, 2}, {2, 3}, {3, 4}})
   * {{1.0000000000000002,2.000000000000001},
   *  {1.9999999999999976,2.999999999999996},
   *  {3.000000000000001,4.0}}
   * &gt;&gt; PseudoInverse({{1, 2, 0}, {2, 3, 0}, {3, 4, 1}})
   * {{-2.999999999999998,1.9999999999999967,4.440892098500626E-16},
   *  {1.999999999999999,-0.9999999999999982,-2.7755575615628914E-16},
   *  {0.9999999999999999,-1.9999999999999991,1.0}}
   * &gt;&gt; PseudoInverse({{1.0, 2.5}, {2.5, 1.0}})
   * {{-0.19047619047619038,0.47619047619047616},
   *  {0.47619047619047616,-0.1904761904761904}}
   * </pre>
   *
   * <p>
   * Argument {1, {2}} at position 1 is not a non-empty rectangular matrix.
   *
   * <pre>
   * &gt;&gt; PseudoInverse({1, {2}})
   * PseudoInverse({1, {2}})
   * </pre>
   */
  private static final class PseudoInverse extends AbstractMatrix1Matrix {
    protected static final PseudoInverse CONST = new PseudoInverse();

    @Override
    public int[] checkMatrixDimensions(IExpr arg1) {
      return Convert.checkNonEmptyRectangularMatrix(S.PseudoInverse, arg1);
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return numericEval(ast, engine);
    }

    @Override
    public FieldMatrix<IExpr> matrixEval(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker) {
      return null;
    }

    @Override
    public RealMatrix realMatrixEval(RealMatrix matrix) {
      final org.hipparchus.linear.SingularValueDecomposition lu =
          new org.hipparchus.linear.SingularValueDecomposition(matrix);
      DecompositionSolver solver = lu.getSolver();
      return solver.getInverse();
    }
  }

  /**
   *
   *
   * <pre>
   * QRDecomposition(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the QR decomposition of the <code>matrix</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; QRDecomposition({{1, 2}, {3, 4}, {5, 6}})
   * {
   * {{-0.16903085094570325,0.8970852271450604,0.4082482904638636},
   *  {-0.50709255283711,0.2760262237369421,-0.8164965809277258},
   *  {-0.8451542547285165,-0.3450327796711773,0.40824829046386274}},
   * {{-5.916079783099616,-7.437357441610944},
   *  {0.0,0.828078671210824},
   *  {0.0,0.0}}}
   * </pre>
   */
  private static class QRDecomposition extends AbstractFunctionEvaluator {

    // @Override
    // public int[] checkMatrixDimensions(IExpr arg1) {
    // return Convert.checkNonEmptyRectangularMatrix(S.QRDecomposition, arg1);
    // }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      boolean togetherMode = engine.isTogetherMode();

      try {
        engine.setTogetherMode(true);
        final IExpr arg1 = ast.arg1();
        int[] dim = arg1.isMatrix();
        if (dim != null) {
          if (engine.isSymbolicMode(S.QRDecomposition.getAttributes())) {
            // TODO improve for symbolic calculations
            // TODO https://github.com/Hipparchus-Math/hipparchus/issues/137
            final FieldMatrix<IExpr> matrix = Convert.list2Matrix(arg1);
            if (matrix != null) {
              engine.setTogetherMode(true);
              FieldQRDecomposition<IExpr> ed = new FieldQRDecomposition<IExpr>(matrix);
              FieldMatrix<IExpr> q = ed.getQ();
              if (Convert.matrix2List(q) != null) {
                q = q.transpose();
                FieldMatrix<IExpr> r = ed.getR();
                if (Convert.matrix2List(r) != null) {
                  return F.list(Convert.matrix2List(q), Convert.matrix2List(r));
                }
              }
              return F.NIL;
            }
          }
          if (engine.isArbitraryMode()) {
            final FieldMatrix<IExpr> matrix = Convert.list2Matrix(arg1);
            if (matrix != null) {
              engine.setTogetherMode(true);
              FieldQRDecomposition<IExpr> ed = new FieldQRDecomposition<IExpr>(matrix);
              FieldMatrix<IExpr> q = ed.getQ();
              if (Convert.matrix2List(q) != null) {
                FieldMatrix<IExpr> r = ed.getR();
                if (Convert.matrix2List(r) != null) {
                  return F.list(Convert.matrix2List(q), Convert.matrix2List(r));
                }
              }
              return F.NIL;
            }
          }
          final FieldMatrix<Complex> complexMatrix = Convert.list2ComplexMatrix(arg1);
          if (complexMatrix != null) {
            FieldQRDecomposition<Complex> ed = new FieldQRDecomposition<Complex>(complexMatrix);
            FieldMatrix<Complex> q = ed.getQ();
            if (Convert.complexMatrix2List(q) != null) {
              FieldMatrix<Complex> r = ed.getR();
              if (Convert.complexMatrix2List(r) != null) {
                return F.list(Convert.complexMatrix2List(q), Convert.complexMatrix2List(r));
              }
            }
            return F.NIL;
          }
        }

      } catch (final ClassCastException |

          IndexOutOfBoundsException e) {
        LOGGER.debug("QRDecomposition.evaluate() failed", e);
      } finally {
        engine.setTogetherMode(togetherMode);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
    // @Override
    // public IExpr matrixEval(FieldMatrix<IExpr> matrix, Predicate<IExpr> zeroChecker) {
    // return F.NIL;
    // }

    // @Override
    // public IAST realMatrixEval(RealMatrix matrix) {
    // org.hipparchus.linear.QRDecomposition ed = new
    // org.hipparchus.linear.QRDecomposition(matrix);
    // RealMatrix q = ed.getQ();
    // RealMatrix r = ed.getR();
    // IASTMutable qMatrix = Convert.realMatrix2List(q);
    // if (qMatrix != null) {
    // IASTMutable rMatrix = Convert.realMatrix2List(r);
    // if (rMatrix != null) {
    // return F.list(qMatrix, rMatrix);
    // }
    // }
    // return F.NIL;
    // }
  }


  private static class RiccatiSolve extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().argSize() == 2 && ast.arg1().isListOfMatrices() && //
          ast.arg2().argSize() == 2 && ast.arg2().isListOfMatrices()) {
        try {
          IAST list1 = (IAST) ast.arg1();
          IAST list2 = (IAST) ast.arg2();
          RealMatrix A = list1.arg1().toRealMatrix();
          if (A != null) {
            RealMatrix B = list1.arg2().toRealMatrix();
            if (B != null) {
              RealMatrix Q = list2.arg1().toRealMatrix();
              if (Q != null) {
                RealMatrix R = list2.arg2().toRealMatrix();
                if (R != null) {
                  RiccatiEquationSolver solver = new RiccatiEquationSolverImpl(A, B, Q, R);
                  RealMatrix result = solver.getP();
                  return new ASTRealMatrix(result, false);
                }
              }
            }
          }
        } catch (MathRuntimeException mrex) {
          LOGGER.log(engine.getLogLevel(), ast.topHead(), mrex);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }


  /**
   *
   *
   * <pre>
   * RowReduce(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns the reduced row-echelon form of <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon form</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; RowReduce({{1,1,0,1,5},{1,0,0,2,2},{0,0,1,4,-1},{0,0,0,0,0}})
   * {{1,0,0,2,2},
   *  {0,1,0,-1,3},
   *  {0,0,1,4,-1},
   *  {0,0,0,0,0}}
   *
   * &gt;&gt; RowReduce({{1, 0, a}, {1, 1, b}})
   * {{1,0,a},
   *  {0,1,-a+b}}
   *
   * &gt;&gt; RowReduce({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})
   * {{1,0,-1},
   *  {0,1,2},
   *  {0,0,0}}
   * </pre>
   *
   * <p>
   * Argument {{1, 0}, {0}} at position 1 is not a non-empty rectangular matrix.<br>
   *
   * <pre>
   * &gt;&gt; RowReduce({{1, 0}, {0}})
   * RowReduce({{1, 0}, {0}})
   * </pre>
   */
  private static class RowReduce extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      // see https://github.com/kredel/java-algebra-system/issues/21
      // GenMatrix<IExpr> matrix;
      // boolean togetherMode = engine.isTogetherMode();
      // try {
      // engine.setTogetherMode(true);
      // int[] dims = ast.arg1().isMatrix();
      // if (dims != null) {
      // matrix = Convert.list2GenMatrix(ast.arg1(), true);
      // if (matrix != null) {
      // Predicate<IExpr> zeroChecker = AbstractMatrix1Expr.optionZeroTest(ast,
      // 2,engine);
      // LinAlg<IExpr> lalg = new LinAlg<IExpr>();
      // GenMatrix<IExpr> res = lalg.rowEchelonForm(matrix);
      // return Convert.genmatrix2List(matrix, true);
      // }
      // }
      // }

      FieldMatrix<IExpr> matrix;
      boolean togetherMode = engine.isTogetherMode();
      try {
        engine.setTogetherMode(true);
        int[] dims = ast.arg1().isMatrix();
        if (dims != null) {
          matrix = Convert.list2Matrix(ast.arg1());
          if (matrix != null) {
            Predicate<IExpr> zeroChecker = AbstractMatrix1Expr.optionZeroTest(ast, 2, engine);
            FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix, zeroChecker);
            return Convert.matrix2List(fmw.getRowReducedMatrix());
          }
        }
      } catch (final ClassCastException | IndexOutOfBoundsException e) {
        LOGGER.debug("RowReduce.evaluate() failed", e);
      } finally {
        engine.setTogetherMode(togetherMode);
      }

      // FieldMatrix<IExpr> matrix;
      // boolean togetherMode = engine.isTogetherMode();
      // try {
      // engine.setTogetherMode(true);
      // int[] dims = ast.arg1().isMatrix();
      // if (dims != null) {
      // matrix = Convert.list2Matrix(ast.arg1());
      // if (matrix != null) {
      // Predicate<IExpr> zeroChecker = AbstractMatrix1Expr.optionZeroTest(ast, 2,
      // engine);
      // FieldReducedRowEchelonForm fmw = new FieldReducedRowEchelonForm(matrix,
      // zeroChecker);
      // return Convert.matrix2List(fmw.getRowReducedMatrix());
      // }
      // }
      // } catch (final ClassCastException e) {
      // LOGGER.debug("RowReduce.evaluate() failed", e);
      // } catch (final IndexOutOfBoundsException e) {
      // LOGGER.debug("RowReduce.evaluate() failed", e);
      // } finally {
      // engine.setTogetherMode(togetherMode);
      // }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class SchurDecomposition extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      RealMatrix matrix;
      boolean togetherMode = engine.isTogetherMode();
      try {
        engine.setTogetherMode(true);
        int[] dim = ast.arg1().isMatrix();
        if (dim != null && dim[0] > 0 && dim[1] > 0) {
          if (dim[0] != dim[1]) {
            // Argument `1` at position `2` is not a non-empty square matrix.
            return IOFunctions.printMessage(ast.topHead(), "matsq", F.CEmptyList, engine);
          }
          matrix = ast.arg1().toRealMatrix();
          if (matrix != null) {
            SchurTransformer schurTransformer = new SchurTransformer(matrix);
            final RealMatrix pMatrix = schurTransformer.getP();
            final RealMatrix tMatrix = schurTransformer.getT();
            IASTAppendable m1 = Convert.matrix2List(pMatrix);
            if (m1.isPresent()) {
              IASTAppendable m2 = Convert.matrix2List(tMatrix);
              if (m2.isPresent()) {
                return F.list(m1, m2);
              }
            }
          }
        }
      } catch (IndexOutOfBoundsException | ClassCastException e) {
        LOGGER.debug("SchurDecomposition.evaluate() failed", e);
      } finally {
        engine.setTogetherMode(togetherMode);
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * SingularValueDecomposition(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * calculates the singular value decomposition for the <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * 'SingularValueDecomposition' returns <code>u</code>, <code>s</code>, <code>w</code> such that
   * <code>matrix =u s v</code>, <code>u' u</code>=1, <code>v' v</code>=1, and <code>s</code> is
   * diagonal.
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href= "https://en.wikipedia.org/wiki/Singular_value_decomposition">Wikipedia: Singular
   * value decomposition</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; SingularValueDecomposition({{1.5, 2.0}, {2.5, 3.0}})
   * {
   * {{0.5389535334972082,0.8423354965397538},
   *  {0.8423354965397537,-0.5389535334972083}},
   * {{4.635554529660638,0.0},
   *  {0.0,0.10786196059193007}},
   * {{0.6286775450376476,-0.7776660879615599},
   *  {0.7776660879615599,0.6286775450376476}}}
   * </pre>
   *
   * <p>
   * Symbolic SVD is not implemented, performing numerically.
   *
   * <pre>
   * &gt;&gt; SingularValueDecomposition({{3/2, 2}, {5/2, 3}})
   * {
   * {{0.5389535334972082,0.8423354965397538},
   *  {0.8423354965397537,-0.5389535334972083}},
   * {{4.635554529660638,0.0},
   *  {0.0,0.10786196059193007}},
   * {{0.6286775450376476,-0.7776660879615599},
   *  {0.7776660879615599,0.6286775450376476}}}
   * </pre>
   *
   * <p>
   * Argument {1, {2}} at position 1 is not a non-empty rectangular matrix.
   *
   * <pre>
   * &gt;&gt; SingularValueDecomposition({1, {2}})
   * SingularValueDecomposition({1, {2}})
   * </pre>
   */
  private static final class SingularValueDecomposition extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      RealMatrix matrix;
      try {
        matrix = ast.arg1().toRealMatrix();
        if (matrix != null) {
          final org.hipparchus.linear.SingularValueDecomposition svd =
              new org.hipparchus.linear.SingularValueDecomposition(matrix);
          return F.list(new ASTRealMatrix(svd.getU(), false), new ASTRealMatrix(svd.getS(), false),
              new ASTRealMatrix(svd.getV(), false));
        }

      } catch (final IndexOutOfBoundsException e) {
        LOGGER.debug("SingularValueDecomposition.evaluate() failed", e);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }


  private static final class SingularValueList extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int[] dim = ast.arg1().isMatrix();
      if (dim != null && dim[0] > 0 && dim[1] > 0) {
        IExpr m = ast.arg1();
        IExpr singularValueList =
            engine.evaluate(F.Sqrt(F.Eigenvalues(F.Dot(F.ConjugateTranspose(m), m))));
        int vectorLength = singularValueList.isVector();
        if (vectorLength > 0) {
          singularValueList =
              ((IAST) singularValueList).filter(x -> keepSingularValue(x, engine))[0];
          int n = singularValueList.argSize();
          if (ast.isAST2()) {
            n = ast.arg2().toIntDefault();
            if (n <= 0) {
              // Positive integer (less equal 2147483647) expected at position `2` in `1`.
              return IOFunctions.printMessage(ast.topHead(), "intpm", F.list(ast, F.C2), engine);
            }
          }

          return S.TakeLargestBy.of(engine, singularValueList, S.Abs, F.ZZ(n));
        }
      }
      return F.NIL;
    }

    /**
     * Singular values are kept only when their <code>Abs(singularValue)</code> is larger than
     * {@link Config#DEFAULT_ROOTS_CHOP_DELTA}.
     *
     * @param singularValue
     * @param engine
     * @return <code>true</code> if the value should be kept; <code>false</code> otherwise.
     */
    private boolean keepSingularValue(IExpr singularValue, EvalEngine engine) {
      double y = engine.evalDouble(F.Abs(singularValue));
      if (F.isZero(y, Config.DEFAULT_ROOTS_CHOP_DELTA)) {
        return false;
      }
      return true;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class ToeplitzMatrix extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2()) {
        if (ast.arg1().isList() && ast.arg2().isList()) {
          IAST vector1 = (IAST) ast.arg1();
          IAST vector2 = (IAST) ast.arg2();
          int numberOfRows = vector1.argSize();
          int numberOfColumns = vector2.argSize();
          return F.matrix((i, j) -> i <= j ? vector2.get(j - i + 1) : vector1.get(i - j + 1),
              numberOfRows, numberOfColumns);
        }

        return F.NIL;
      }
      if (ast.arg1().isList()) {
        IAST vector = (IAST) ast.arg1();
        int m = vector.argSize();
        return F.matrix((i, j) -> i <= j ? vector.get(j - i + 1) : vector.get(i - j + 1), m, m);
      }

      if (ast.arg1().isInteger()) {
        int m = ast.arg1().toIntDefault();
        if (m < 0) {
          // Positive integer (less equal 2147483647) expected at position `2` in `1`.
          return IOFunctions.printMessage(S.ToeplitzMatrix, "intpm", F.list(ast, F.C1), engine);
        }
        int[] count = new int[1];
        count[0] = 1;
        return F.matrix((i, j) -> i <= j ? F.ZZ(j - i + 1) : F.ZZ(i - j + 1), m, m);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * ToPolarCoordinates({x, y})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the polar coordinates for the cartesian coordinates <code>{x, y}</code>.
   *
   * </blockquote>
   *
   * <pre>
   * ToPolarCoordinates({x, y, z})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * return the polar coordinates for the cartesian coordinates <code>{x, y, z}</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; ToPolarCoordinates({x, y})
   * {Sqrt(x^2+y^2),ArcTan(x,y)}
   *
   * &gt;&gt; ToPolarCoordinates({x, y, z})
   * {Sqrt(x^2+y^2+z^2),ArcCos(x/Sqrt(x^2+y^2+z^2)),ArcTan(y,z)}
   * </pre>
   */
  private static final class ToPolarCoordinates extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr arg1 = ast.arg1();
      int dim = arg1.isVector();
      if (dim > 0) {
        if (arg1.isAST()) {
          IAST list = (IAST) arg1;
          if (dim == 2) {
            IExpr x = list.arg1();
            IExpr y = list.arg2();
            return F.list(F.Sqrt(F.Plus(F.Sqr(x), F.Sqr(y))), F.ArcTan(x, y));
          } else if (dim == 3) {
            IExpr x = list.arg1();
            IExpr y = list.arg2();
            IExpr z = list.arg3();
            IAST sqrtExpr = F.Sqrt(F.Plus(F.Sqr(x), F.Sqr(y), F.Sqr(z)));
            return F.list(sqrtExpr, F.ArcCos(F.Divide(x, sqrtExpr)), F.ArcTan(y, z));
          }
        } else {
          FieldVector<IExpr> vector = Convert.list2Vector(arg1);
          if (dim == 2) {
            IExpr x = vector.getEntry(0);
            IExpr y = vector.getEntry(1);
            return F.list(F.Sqrt(F.Plus(F.Sqr(x), F.Sqr(y))), F.ArcTan(x, y));
          } else if (dim == 3) {
            IExpr x = vector.getEntry(0);
            IExpr y = vector.getEntry(1);
            IExpr z = vector.getEntry(2);
            IAST sqrtExpr = F.Sqrt(F.Plus(F.Sqr(x), F.Sqr(y), F.Sqr(z)));
            return F.list(sqrtExpr, F.ArcCos(F.Divide(x, sqrtExpr)), F.ArcTan(y, z));
          }
        }
      } else if (arg1.isList()) {
        IAST list = (IAST) arg1;
        return list.mapThreadEvaled(engine, F.ListAlloc(list.size()), ast, 1);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Tr(matrix)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the trace of the <code>matrix</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Trace_matrix">Wikipedia - Trace (linear
   * algebra)</a><br>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Tr({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}})
   * 15
   * </pre>
   *
   * <p>
   * Symbolic trace:
   *
   * <pre>
   * &gt;&gt; Tr({{a, b, c}, {d, e, f}, {g, h, i}})
   * a+e+i
   * </pre>
   */
  private static class Tr extends AbstractEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr header = S.Plus;
      int level = -1;
      if (ast.size() > 2) {
        header = ast.arg2();
      }

      try {
        final IntList dimensions = dimensions(arg1, S.List, Integer.MAX_VALUE, true);
        final int dimsSize = dimensions.size();
        if (dimsSize == 0) {
          return F.NIL;
        }

        if (ast.isAST3()) {
          level = ast.arg3().toIntDefault();
          if (level == Integer.MIN_VALUE) {
            return F.NIL;
          }

          // TODO calculate for level restrictions
          return F.NIL;
        }

        // determine the sum of elements with equal indices for tensors
        int minLength = Integer.MAX_VALUE;
        for (int i = 0; i < dimsSize; i++) {
          if (minLength > dimensions.getInt(i)) {
            minLength = dimensions.getInt(i);
          }
        }
        if (arg1.isSparseArray()) {
          final ISparseArray tensor = (ISparseArray) arg1;
          int[] part = new int[dimsSize];
          IASTMutable tr = F.astMutable(header, minLength++);
          for (int d = 1; d < minLength; d++) {
            for (int i = 0; i < dimsSize; i++) {
              part[i] = d;
            }
            tr.set(d, tensor.getIndex(part));
          }

          return tr;
        }

        final IAST tensor = (IAST) arg1.normal(false);
        int[] part = new int[dimsSize];
        IASTMutable tr = F.astMutable(header, minLength++);
        for (int d = 1; d < minLength; d++) {
          for (int i = 0; i < dimsSize; i++) {
            part[i] = d;
          }
          tr.set(d, tensor.getPart(part));
        }

        return tr;
      } catch (IllegalArgumentException iae) {
        // print message: Nonrectangular tensor encountered
        return IOFunctions.printMessage(ast.topHead(), "rect", F.list(ast), engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * Transpose(m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * transposes rows and columns in the matrix <code>m</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Transpose">Wikipedia - Transpose</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Transpose({{1, 2, 3}, {4, 5, 6}})
   * {{1, 4}, {2, 5}, {3, 6}}
   * &gt;&gt; MatrixForm(%)
   * 1   4
   * 2   5
   * 3   6
   *
   * &gt;&gt; Transpose(x)
   * Transpose(x)
   * </pre>
   */
  private static class Transpose extends AbstractEvaluator {

    private static class TransposePermute {
      /** The current tensor. */
      final IAST tensor;
      /** The dimensions of the current tensor. */
      final int[] dimensions;
      /** The permutation of the result tensor */
      final int[] permutation;
      /** The position from which to extract the current element */
      int[] positions;

      private TransposePermute(IAST tensor, IntList tensorDimensions, int[] permutation) {
        this.tensor = tensor;
        this.dimensions = new int[tensorDimensions.size()];
        for (int i = 0; i < tensorDimensions.size(); i++) {
          dimensions[i] = tensorDimensions.getInt(i);
        }
        this.permutation = permutation;
        this.positions = new int[dimensions.length];
      }

      private IAST transposeRecursive() {
        return transposeRecursive(0, null);
      }

      /**
       * @param permutationIndex the current permutation index, which should be used to get the
       *        element from permutation array
       * @param resultList the parent list or <code>null</code> if the root-list should be created.
       * @return
       */
      private IAST transposeRecursive(int permutationIndex, IASTAppendable resultList) {
        if (permutationIndex >= permutation.length) {
          if (resultList != null) {
            resultList.append(tensor.getPart(positions));
          }
        } else {
          int size = dimensions[permutation[permutationIndex] - 1];
          IASTAppendable list = F.ListAlloc(size);
          if (resultList != null) {
            resultList.append(list);
          }
          for (int i = 0; i < size; i++) {
            positions[permutation[permutationIndex] - 1] = i + 1;
            transposeRecursive(permutationIndex + 1, list);
          }
          return list;
        }
        return F.NIL;
      }
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1()) {
        final int[] dim = ast.arg1().isMatrix();
        if (dim != null) {
          // TODO improve for sparse arrays
          // final IAST originalMatrix = (IAST) ast.arg1().normal(false);
          final FieldMatrix<IExpr> matrix = Convert.list2Matrix(ast.arg1());
          if (matrix != null) {
            final FieldMatrix<IExpr> transposed = matrix.transpose();
            IExpr transposedMatrix = Convert.matrix2Expr(transposed).mapExpr(x -> transform(x));
            // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set
            // directly.
            // isMatrix()
            // must be used!
            transposedMatrix.isMatrix(true);
            return transposedMatrix;
            // return transpose(originalMatrix, dim[0], dim[1]);
          }
          if (dim[1] == 0) {
            return F.CEmptyList;
          }
        } else if (ast.arg1().isListOfLists()) {
          // Error messages inherits to ConjugateTranspose
          // The first two levels of `1` cannot be transposed.
          return IOFunctions.printMessage(ast.topHead(), "nmtx", F.List(ast), engine);
        }
      } else if (ast.isAST2()) {
        if (ast.arg1().isList() && ast.arg2().isList()) {
          IAST tensor = (IAST) ast.arg1();
          IntArrayList dims = dimensions(tensor, tensor.head(), Integer.MAX_VALUE);
          int[] permutation = Validate.checkListOfInts(ast, ast.arg2(), 1, dims.size(), engine);
          if (permutation == null) {
            return F.NIL;
          }
          return new TransposePermute(tensor, dims, permutation).transposeRecursive();
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    protected IExpr transform(final IExpr expr) {
      return expr;
    }

    /**
     * Transpose the given matrix.
     *
     * @param matrix the matrix which should be transposed
     * @param rows number of rows of the matrix
     * @param cols number of columns of the matrix
     * @return
     */
    private IAST transpose(final IAST matrix, int rows, int cols) {
      final IASTMutable transposedMatrix = F.astMutable(S.List, cols);
      transposedMatrix.setArgs(cols + 1, i -> F.astMutable(S.List, rows));
      // for (int i = 1; i <= cols; i++) {
      // transposedMatrix.set(i, F.ast(F.List, rows, true));
      // }

      IAST originalRow;
      IASTMutable transposedResultRow;
      for (int i = 1; i <= rows; i++) {
        originalRow = (IAST) matrix.get(i);
        for (int j = 1; j <= cols; j++) {
          transposedResultRow = (IASTMutable) transposedMatrix.get(j);
          transposedResultRow.set(i, transform(originalRow.get(j)));
        }
      }
      // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
      // isMatrix() must be
      // used!
      transposedMatrix.isMatrix(true);
      return transposedMatrix;
    }
  }

  /**
   *
   *
   * <pre>
   * UnitVector(position)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a unit vector with element <code>1</code> at the given <code>position</code>.
   *
   * </blockquote>
   *
   * <pre>
   * UnitVector(dimension, position)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a unit vector with dimension <code>dimension</code> and an element <code>1</code> at
   * the given <code>position</code>.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Unit_vector">Wikipedia - Unit vector</a><br>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; UnitVector(4,3)
   * {0,0,1,0}
   * </pre>
   */
  private static final class UnitVector extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (ast.isAST2()) {
        int n = arg1.toIntDefault();
        if (n <= 0) {
          if (n == Integer.MIN_VALUE && !arg1.isInteger()) {
            return F.NIL;
          }
          // Positive machine-sized integer expected at position `2` in `1`.
          return IOFunctions.printMessage(S.UnitVector, "intpm", F.list(ast, F.C1), engine);
        }
        int k = ast.arg2().toIntDefault();
        if (k <= 0) {
          if (k == Integer.MIN_VALUE) {
            return F.NIL;
          }
          // Positive machine-sized integer expected at position `2` in `1`.
          return IOFunctions.printMessage(S.UnitVector, "intpm", F.list(ast, F.C2), engine);
        }

        if (k <= n) {
          IASTAppendable vector = F.mapRange(0, n, i -> F.C0);
          vector.set(k, F.C1);
          return vector;
        }
        return F.NIL;
      }

      if (arg1.isInteger()) {
        int k = arg1.toIntDefault();
        if (k <= 0) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return IOFunctions.printMessage(S.UnitVector, "intpm", F.list(ast, F.C1), engine);
        }
        if (k == 1) {
          return F.list(F.C1, F.C0);
        }
        if (k == 2) {
          return F.list(F.C0, F.C1);
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  private static class UpperTriangularize extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int[] dim = ast.arg1().isMatrix(false);
      if (dim == null) {
        // Argument `1` at position `2` is not a non-empty rectangular matrix.
        return IOFunctions.printMessage(ast.topHead(), "matrix", F.list(ast.arg1(), F.C1), engine);
      }
      final int k;
      if (ast.size() == 3) {
        k = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
      } else {
        k = 0;
      }
      int m = dim[0];
      int n = dim[1];
      FieldMatrix<IExpr> matrix = Convert.list2Matrix(ast.arg1());
      return F.matrix((i, j) -> i <= j - k ? matrix.getEntry(i, j) : F.C0, m, n);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   *
   *
   * <pre>
   * VandermondeMatrix(n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the Vandermonde matrix with <code>n</code> rows and columns.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="http://en.wikipedia.org/wiki/Vandermonde_matrix">Wikipedia - Vandermonde
   * matrix</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; VandermondeMatrix({a,b,c})
   * {{1,a,a^2},
   *  {1,b,b^2},
   *  {1,c,c^2}}
   * </pre>
   */
  private static class VandermondeMatrix extends AbstractFunctionEvaluator {
    public VandermondeMatrix() {}

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isList()) {
        final IAST lst = (IAST) ast.arg1();
        final int len0 = lst.argSize();

        final int[] indexArray = new int[2];
        indexArray[0] = len0;
        indexArray[1] = len0;

        // final IIndexFunction<IExpr> function = new IIndexFunction<IExpr>() {
        // @Override
        // public IExpr evaluate(int[] index) {
        // return Power(lst.get(index[0] + 1), F.integer(index[1]));
        // }
        // };
        final IndexTableGenerator generator = new IndexTableGenerator(indexArray, S.List, //
            indx -> Power(lst.get(indx[0] + 1), F.ZZ(indx[1])));
        final IAST matrix = (IAST) generator.table();
        // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
        // isMatrix()
        // must be used!
        matrix.isMatrix(true);
        return matrix;
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   *
   *
   * <pre>
   * VectorAngle(u, v)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the angles between vectors <code>u</code> and <code>v</code>
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; VectorAngle({1, 0}, {0, 1})
   * Pi/2
   *
   * &gt;&gt; VectorAngle({1, 2}, {3, 1})
   * Pi/4
   *
   * &gt;&gt; VectorAngle({1, 1, 0}, {1, 0, 1})
   * Pi/3
   *
   * &gt;&gt; VectorAngle({0, 1}, {0, 1})
   * 0
   * </pre>
   */
  private static class VectorAngle extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      IExpr arg2 = ast.arg2();

      int dim1 = arg1.isVector();
      int dim2 = arg2.isVector();
      if (dim1 > (-1) && dim2 > (-1)) {
        if (dim1 != dim2) {
          // The vectors `1` and `2` have different lengths.
          return IOFunctions.printMessage(ast.topHead(), "length", F.List(arg1, arg2), engine);
        }
        return ArcCos(Divide(Dot(arg1, F.Conjugate(arg2)), Times(Norm(arg1), Norm(arg2))));
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }
  }

  /**
   * Use cramer's rule to solve linear equations represented by a <code>2 x 3</code> augmented
   * matrix which represents the system <code>M.x == b</code>, where the columns of the <code>2 x 2
   * </code> matrix <code>M</code> are augmented by the vector <code>b</code>. This method assumes
   * that the dimensions of the matrix are already checked by the caller. See:
   * <a href="https://en.wikipedia.org/wiki/Cramer's_rule">Wikipedia Cramer's rule</a>
   *
   * @param matrix the <code>2 x 3</code> augmented matrix
   * @param quiet show no message if there is no solution
   * @param engine the evaluation engine
   * @return a list of values which solve the equations or <code>F#NIL</code>, if the equations have
   *         no solution.
   */
  public static IAST cramersRule2x3(FieldMatrix<IExpr> matrix, boolean quiet, EvalEngine engine) {
    IASTAppendable list = F.ListAlloc(2);
    // a1 * b2 - b1 * a2
    IExpr denominator = determinant2x2(matrix);
    if (denominator.isZero()) {
      if (!quiet) {
        LOGGER.log(engine.getLogLevel(), "Row reduced linear equations have no solution.");
        return F.NIL;
      }
      return F.NIL;
    }
    // c1 * b2 - b1 * c2
    IExpr xNumerator = F.Subtract(F.Times(matrix.getEntry(0, 2), matrix.getEntry(1, 1)),
        F.Times(matrix.getEntry(0, 1), matrix.getEntry(1, 2)));
    list.append(F.Divide(xNumerator, denominator));
    // a1 * c2 - c1*a2
    IExpr yNumerator = F.Subtract(F.Times(matrix.getEntry(0, 0), matrix.getEntry(1, 2)),
        F.Times(matrix.getEntry(0, 2), matrix.getEntry(1, 0)));
    list.append(F.Divide(yNumerator, denominator));
    return list;
  }

  /**
   * Use cramer's rule to solve linear equations represented by a <code>3 x 4</code> augmented
   * matrix which represents the system <code>M.x == b</code>, where the columns of the <code>3 x 3
   * </code> matrix <code>M</code> are augmented by the vector <code>b</code>. This method assumes
   * that the dimensions of the matrix are already checked by the caller. See:
   * <a href="https://en.wikipedia.org/wiki/Cramer's_rule">Wikipedia Cramer's rule</a>
   *
   * @param matrix the <code>3 x 4</code> augmented matrix
   * @param quiet show no message if there is no solution
   * @param engine the evaluation engine
   * @return a list of values which solve the equations or <code>F#NIL</code>, if the equations have
   *         no solution.
   */
  public static IAST cramersRule3x4(FieldMatrix<IExpr> matrix, boolean quiet, EvalEngine engine) {
    IASTAppendable list = F.ListAlloc(3);
    FieldMatrix<IExpr> denominatorMatrix = matrix.getSubMatrix(0, 2, 0, 2);
    IExpr denominator = determinant3x3(denominatorMatrix);
    if (denominator.isZero()) {
      if (!quiet) {
        LOGGER.log(engine.getLogLevel(), "Row reduced linear equations have no solution.");
        return F.NIL;
      }
      return F.NIL;
    }

    FieldMatrix<IExpr> xMatrix = denominatorMatrix.copy();
    xMatrix.setColumn(0,
        new IExpr[] {matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3)});
    IExpr xNumerator = determinant3x3(xMatrix);

    list.append(F.Divide(xNumerator, denominator));

    FieldMatrix<IExpr> yMatrix = denominatorMatrix.copy();
    yMatrix.setColumn(1,
        new IExpr[] {matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3)});
    IExpr yNumerator = determinant3x3(yMatrix);

    list.append(F.Divide(yNumerator, denominator));

    FieldMatrix<IExpr> zMatrix = denominatorMatrix.copy();
    zMatrix.setColumn(2,
        new IExpr[] {matrix.getEntry(0, 3), matrix.getEntry(1, 3), matrix.getEntry(2, 3)});
    IExpr zNumerator = determinant3x3(zMatrix);

    list.append(F.Divide(zNumerator, denominator));

    return list;
  }

  /**
   * Get the determinant of a <code>2 x 2</code> matrix. This method assumes that the dimensions of
   * the matrix are already checked by the caller.
   *
   * @param matrix a 2x2 matrix
   * @return
   */
  public static IExpr determinant2x2(final FieldMatrix<IExpr> matrix) {
    // 2x2 matrix
    IExpr[] row1 = matrix.getRow(0);
    IExpr[] row2 = matrix.getRow(1);
    return F.evalExpand(row1[0].times(row2[1]).subtract(row1[1].times(row2[0])));
  }

  /**
   * Get the determinant of a <code>3 x 3</code> matrix. This method assumes that the dimensions of
   * the matrix are already checked by the caller.
   *
   * @param matrix a 3x3 matrix
   * @return the expression for the determinant
   */
  public static IExpr determinant3x3(final FieldMatrix<IExpr> matrix) {
    // 3x3 matrix
    IExpr[] row1 = matrix.getRow(0);
    IExpr[] row2 = matrix.getRow(1);
    IExpr[] row3 = matrix.getRow(2);
    return F.evalExpand(row1[0].times(row2[1].times(row3[2]))
        .subtract((row1[0].times(row2[2].times(row3[1]))))
        .subtract((row1[1].times(row2[0].times(row3[2]))))
        .plus((row1[1].times(row2[2].times(row3[0])))).plus((row1[2].times(row2[0].times(row3[1]))))
        .subtract((row1[2].times(row2[1].times(row3[0])))));
  }

  /**
   * Create a diagonal matrix from <code>valueArray[0]</code> (non-diagonal elements) and <code>
   * valueArray[1]</code> (diagonal elements).
   *
   * @param valueArray 2 values for non-diagonal and diagonal elements of the matrix.
   * @param dimension of the square matrix
   * @return the diagonal matrix
   */
  private static IAST diagonalMatrix(final IExpr[] valueArray, int dimension) {
    final int[] indexArray = new int[2];
    indexArray[0] = dimension;
    indexArray[1] = dimension;
    final IndexTableGenerator generator =
        new IndexTableGenerator(indexArray, S.List, new IndexFunctionDiagonal(valueArray));
    final IAST matrix = (IAST) generator.table();
    // because the rows can contain sub lists the IAST.IS_MATRIX flag cannot be set directly.
    // isMatrix() must be used!
    matrix.isMatrix(true);
    return matrix;
  }

  /**
   * Return the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * of an {@link IAST}.
   * 
   * @param ast
   * @return a list of size <code>0</code> if no dimensions are found
   */
  public static IntArrayList dimensions(IAST ast) {
    return dimensionsRecursive(ast, ast.head(), Integer.MAX_VALUE, false, new IntArrayList());
  }

  /**
   * Return the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * of an {@link IAST}. All (sub-)expressions in the dimension must have the same
   * <code>header</code>.
   * 
   * @param ast
   * @param header the header, which all sub-expressions of the detected dimension must contain
   * @return a list of size <code>0</code> if no dimensions are found
   */
  public static IntArrayList dimensions(IAST ast, IExpr header) {
    return dimensions(ast, header, Integer.MAX_VALUE);
  }

  /**
   * Return the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * of an {@link IAST}. All (sub-)expressions in the dimension must have the same
   * <code>header</code>.
   * 
   * @param ast
   * @param header the header, which all sub-expressions of the detected dimension must contain
   * @param maxLevel
   * @return a list of size <code>0</code> if no dimensions are found
   */
  public static IntArrayList dimensions(IAST ast, IExpr header, int maxLevel) {
    return dimensionsRecursive(ast, header, maxLevel, false, new IntArrayList());
  }

  /**
   * Return the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * of an <code>expr</code>. All (sub-)expressions in the dimension must have the same
   * <code>header</code>.
   * 
   * @param expr
   * @param header the header, which all sub-expressions of the detected dimension must contain
   * @param maxLevel the maximum level (depth) of analyzing for the dimension
   * @param throwIllegalArgumentException
   * @return a list of size <code>0</code> if no dimensions are found
   */
  public static IntArrayList dimensions(IExpr expr, IExpr header, int maxLevel,
      boolean throwIllegalArgumentException) {
    if (expr.isAST()) {
      return dimensionsRecursive((IAST) expr, header, maxLevel, throwIllegalArgumentException,
          new IntArrayList());
    }
    if (expr.isSparseArray()) {
      int[] dims = ((ISparseArray) expr).getDimension();

      if (dims.length > maxLevel) {
        IntArrayList list = new IntArrayList(maxLevel);
        if (throwIllegalArgumentException) {
          throw new IllegalArgumentException();
        }
        for (int i = 0; i < maxLevel; i++) {
          list.add(dims[i]);
        }
        return list;
      }
      IntArrayList list = new IntArrayList(dims.length);
      for (int i = 0; i < dims.length; i++) {
        list.add(dims[i]);
      }
      return list;
    }

    return new IntArrayList();
  }

  /**
   * Determine the <a href=
   * "https://github.com/axkr/symja_android_library/blob/master/symja_android_library/doc/functions/Dimensions.md">Dimensions</a>
   * recursively.
   * 
   * @param ast
   * @param header the header, which all sub-expressions of the detected dimension must contain
   * @param maxLevel the maximum level (depth) of analyzing for the dimension
   * @param throwIllegalArgumentException
   * @param dims
   * @return a list of size <code>0</code> if no dimensions are found
   * @throws IllegalArgumentException
   */
  private static IntArrayList dimensionsRecursive(IAST ast, IExpr header, int maxLevel,
      boolean throwIllegalArgumentException, IntArrayList dims) throws IllegalArgumentException {

    int size = ast.size();
    if (header.equals(ast.head())) {
      dims.add(size - 1);
      if (size > 1) {

        if (ast.arg1().isAST()) {
          IAST arg1AST = (IAST) ast.arg1();
          int arg1Size = arg1AST.size();
          if (header.equals(S.List)) {
            if (!arg1AST.isSparseArray() && !header.equals(arg1AST.head())) {
              return checkRectangularDimensions(ast, header, throwIllegalArgumentException, dims);
            }
          } else if (!header.equals(arg1AST.head())) {
            return checkRectangularDimensions(ast, header, throwIllegalArgumentException, dims);
          }
          if (maxLevel > 0) {
            for (int i = 2; i < size; i++) {
              IExpr arg = ast.get(i);
              if (header.equals(S.List)) {
                if (!arg.isSparseArray() && !header.equals(arg.head())) {
                  return checkRectangularDimensions(ast, header, throwIllegalArgumentException,
                      dims);
                }
              } else if (!header.equals(arg.head())) {
                return checkRectangularDimensions(ast, header, throwIllegalArgumentException, dims);
              }
              if (arg.isAST() || arg.isSparseArray()) {
                if (arg1Size == arg.size()) {
                  continue;
                }
              }
              return checkRectangularDimensions(ast, header, throwIllegalArgumentException, dims);
            }
            return dimensionsRecursive(arg1AST, header, maxLevel - 1, throwIllegalArgumentException,
                dims);
          }
        }
        return checkRectangularDimensions(ast, header, throwIllegalArgumentException, dims);
      }
    }
    return dims;
  }

  /**
   * If <code>throwIllegalArgumentException</code> is <code>true</code> the arguments of <code>ast
   * </code> are tested if they contain a list, sparse array or numeric array.
   *
   * @param ast
   * @param throwIllegalArgumentException
   * @param dims
   * @return
   */
  private static IntArrayList checkRectangularDimensions(IAST ast, IExpr header,
      boolean throwIllegalArgumentException, IntArrayList dims) throws IllegalArgumentException {
    if (throwIllegalArgumentException) {
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = ast.get(i);
        if (arg.isSparseArray() && arg.isList() || arg.isNumericArray()) {
          throw new IllegalArgumentException();
        } else if (header.equals(arg.head())) {
          throw new IllegalArgumentException();
        }
      }
    }
    return dims;
  }

  public static void initialize() {
    Initializer.init();
  }

  private static IExpr linearSolve(LinearSolveFunctionExpr<?> linearSolveFunction,
      IExpr vectorOrMatrix, final IAST ast, EvalEngine engine) {
    int vectorSize = vectorOrMatrix.isVector();
    if (vectorSize < 1) {
      int[] dims = vectorOrMatrix.isMatrix();
      if (dims == null) {
        return F.NIL;
      }

      if (linearSolveFunction.isComplexNumeric()) {
        FieldDecompositionSolver<Complex> data =
            (FieldDecompositionSolver<Complex>) linearSolveFunction.toData();
        if (data.isNonSingular()) {
          if (dims[0] != data.getRowDimension()) {
            // Coefficient matrix and target vector or matrix do not have the same dimensions.
            return IOFunctions.printMessage(ast.topHead(), "lslc", F.CEmptyList, engine);
          }
          final FieldMatrix<Complex> matrix = Convert.list2ComplexMatrix(vectorOrMatrix);
          if (matrix == null) {
            return F.NIL;
          }

          FieldMatrix<Complex> resultVector = data.solve(matrix);
          return Convert.complexMatrix2List(resultVector);
        }
      } else {
        long oldPrecision = engine.getNumericPrecision();
        long precision = linearSolveFunction.getNumericPrecision();
        try {
          if (precision > oldPrecision) {
            engine.setNumericPrecision(precision);
          }
          FieldDecompositionSolver<IExpr> data =
              (FieldDecompositionSolver<IExpr>) linearSolveFunction.toData();
          if (data.isNonSingular()) {
            if (dims[0] != data.getRowDimension()) {
              // Coefficient matrix and target vector or matrix do not have the same
              // dimensions.
              return IOFunctions.printMessage(ast.topHead(), "lslc", F.CEmptyList, engine);
            }
            final FieldMatrix<IExpr> matrix = Convert.list2Matrix(vectorOrMatrix);
            if (matrix == null) {
              return F.NIL;
            }

            FieldMatrix<IExpr> resultVector = data.solve(matrix);
            return Convert.matrix2List(resultVector);
          }
        } finally {
          engine.setNumericPrecision(oldPrecision);
        }
      }
      return F.NIL;
    }
    if (linearSolveFunction.isComplexNumeric()) {
      FieldDecompositionSolver<Complex> data =
          (FieldDecompositionSolver<Complex>) linearSolveFunction.toData();
      if (data.isNonSingular()) {
        if (vectorSize != data.getRowDimension()) {
          // Coefficient matrix and target vector or matrix do not have the same dimensions.
          return IOFunctions.printMessage(ast.topHead(), "lslc", F.CEmptyList, engine);
        }
        final FieldVector<Complex> vector = Convert.list2ComplexVector(vectorOrMatrix);
        if (vector == null) {
          return F.NIL;
        }

        FieldVector<Complex> resultVector = data.solve(vector);
        return Convert.complexVector2List(resultVector);
      }
    } else {
      long oldPrecision = engine.getNumericPrecision();
      long precision = linearSolveFunction.getNumericPrecision();
      try {
        if (precision > oldPrecision) {
          engine.setNumericPrecision(precision);
        }
        FieldDecompositionSolver<IExpr> data =
            (FieldDecompositionSolver<IExpr>) linearSolveFunction.toData();
        if (data.isNonSingular()) {
          if (vectorSize != data.getRowDimension()) {
            // Coefficient matrix and target vector or matrix do not have the same dimensions.
            return IOFunctions.printMessage(ast.topHead(), "lslc", F.CEmptyList, engine);
          }
          final FieldVector<IExpr> vector = Convert.list2Vector(vectorOrMatrix);
          if (vector == null) {
            return F.NIL;
          }
          FieldVector<IExpr> resultVector = data.solve(vector);
          return Convert.vector2List(resultVector);
        }
      } finally {
        engine.setNumericPrecision(oldPrecision);
      }
    }
    return F.NIL;
  }

  /**
   * Return the solution of the given (augmented-)matrix interpreted as a system of linear
   * equations.
   *
   * @param matrix
   * @param quiet suppress warning messages if <code>true</code>
   * @param engine the evaluation engine
   * @return {@link F#NIL} if the linear system is inconsistent and has no solution
   */
  public static IAST rowReduced2List(FieldMatrix<IExpr> matrix, boolean quiet, EvalEngine engine) {

    int rows = matrix.getRowDimension();
    int cols = matrix.getColumnDimension();
    if (rows == 2 && cols == 3) {
      IAST list = cramersRule2x3(matrix, quiet, engine);
      if (list.isPresent()) {
        return list;
      }
    } else if (rows == 3 && cols == 4) {
      IAST list = cramersRule3x4(matrix, quiet, engine);
      if (list.isPresent()) {
        return list;
      }
    }
    FieldReducedRowEchelonForm ref =
        new FieldReducedRowEchelonForm(matrix, AbstractMatrix1Expr.POSSIBLE_ZEROQ_TEST);
    FieldMatrix<IExpr> rowReduced = ref.getRowReducedMatrix();
    IExpr lastVarCoefficient = rowReduced.getEntry(rows - 1, cols - 2);
    if (lastVarCoefficient.isZero()) {
      if (!rowReduced.getEntry(rows - 1, cols - 1).isZero()) {
        LOGGER.log(engine.getLogLevel(), "Row reduced linear equations have no solution.");
        return F.NIL;
      }
    }
    IASTAppendable list = F.ListAlloc(rows < cols - 1 ? cols - 1 : rows);
    list.appendArgs(0, rows, j -> S.Together.of(engine, rowReduced.getEntry(j, cols - 1)));
    if (rows < cols - 1) {
      list.appendArgs(rows, cols - 1, i -> F.C0);
    }
    return list;
  }

  /**
   * Row reduce the given <code>(augmented-)matrix</code> and append the result as rules for the
   * given <code>variableList</code>.
   *
   * @param matrix a (augmented-)matrix
   * @param listOfVariables list of variable symbols
   * @param resultList a list to which the rules should be appended
   * @param engine the evaluation engine
   * @return resultList with the appended results as list of rules
   */
  public static IAST rowReduced2RulesList(FieldMatrix<IExpr> matrix, IAST listOfVariables,
      IASTAppendable resultList, EvalEngine engine) {
    int rows = matrix.getRowDimension();
    int cols = matrix.getColumnDimension();
    IAST smallList = null;
    if (rows == 2 && cols == 3) {
      smallList = cramersRule2x3(matrix, true, engine);
    } else if (rows == 3 && cols == 4) {
      smallList = cramersRule3x4(matrix, true, engine);
    }
    if (smallList != null) {
      if (!smallList.isPresent()) {
        // no solution
        return F.CEmptyList;
      }
      final IAST sList = smallList;
      resultList.append(F.mapRange(1, smallList.size(),
          j -> F.Rule(listOfVariables.get(j), engine.evaluate(sList.get(j)))));
      return resultList;
    }
    FieldReducedRowEchelonForm ref =
        new FieldReducedRowEchelonForm(matrix, AbstractMatrix1Expr.POSSIBLE_ZEROQ_TEST);
    FieldMatrix<IExpr> rowReduced = ref.getRowReducedMatrix();
    int size = listOfVariables.argSize();

    IExpr lastVarCoefficient = rowReduced.getEntry(rows - 1, cols - 2);

    if (lastVarCoefficient.isZero()) {
      if (!rowReduced.getEntry(rows - 1, cols - 1).isZero()) {
        // no solution
        return F.CEmptyList;
      }
    }
    IAST rule;
    IASTAppendable list = F.ListAlloc(rows);
    for (int j = 1; j < rows + 1; j++) {
      if (j < size + 1) {
        IExpr diagonal = rowReduced.getEntry(j - 1, j - 1);
        if (!diagonal.isZero()) {
          IASTAppendable plus = F.PlusAlloc(cols);
          plus.append(rowReduced.getEntry(j - 1, cols - 1));
          for (int i = j; i < cols - 1; i++) {
            if (!rowReduced.getEntry(j - 1, i).isZero()) {
              plus.append(
                  F.Times(rowReduced.getEntry(j - 1, i).negate(), listOfVariables.get(i + 1)));
            }
          }
          rule = F.Rule(listOfVariables.get(j), S.Together.of(engine, plus.oneIdentity0()));
          list.append(rule);
        }
      }
    }
    resultList.append(list);
    return resultList;
  }

  private LinearAlgebra() {}
}
