package org.matheclipse.commons.math.linear;

import org.apache.commons.math3.linear.FieldMatrix;
import org.apache.commons.math3.linear.FieldVector;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IEvalStepListener;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <p>
 * Matrix class that wraps a <code>FieldMatrix&lt;T&gt;</code> matrix, which is
 * transformed to reduced row echelon format.
 * </p>
 * <p>
 * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row
 * echelon form</a>.
 * </p>
 * <p>
 * The code was adapted from:
 * <a href="http://rosettacode.org/wiki/Reduced_row_echelon_form#Java">Rosetta
 * Code - Reduced row echelon form</a>
 * </p>
 */
public class FieldReducedRowEchelonForm {
	/**
	 * Wrapper for a row and column index.
	 * 
	 */
	private static class RowColIndex {
		int row;
		int col;

		RowColIndex(int r, int c) {
			row = r;
			col = c;
		}

		public String toString() {
			return "(" + row + ", " + col + ")";
		}
	}

	private final FieldMatrix<IExpr> originalMatrix;
	private final FieldMatrix<IExpr> rowReducedMatrix;
	private FieldMatrix<IExpr> nullSpaceCache;
	private int matrixRankCache;

	/**
	 * Number of rows.
	 */
	private final int numRows;

	/**
	 * Number of columns.
	 */
	private final int numCols;

	/**
	 * Constructor which creates row reduced echelon matrix from the given
	 * <code>FieldMatrix&lt;T&gt;</code> matrix.
	 * 
	 * @param matrix
	 *            matrix which will be transformed to a row reduced echelon
	 *            matrix.
	 * 
	 * @see #rowReduce()
	 */
	public FieldReducedRowEchelonForm(FieldMatrix<IExpr> matrix) {
		this.originalMatrix = matrix;
		this.rowReducedMatrix = matrix.copy();
		this.numRows = matrix.getRowDimension();
		this.numCols = matrix.getColumnDimension();
		this.matrixRankCache = -1;
		this.nullSpaceCache = null;
		rowReduce();
	}

	/**
	 * Constructor which creates row reduced echelon matrix from the given
	 * augmented <code>matrix</code> and column-vector <code>b</code>.
	 * 
	 * @param matrix
	 *            matrix which will be transformed to a row reduced echelon
	 *            matrix.
	 * 
	 * @see #rowReduce()
	 */
	public FieldReducedRowEchelonForm(FieldMatrix<IExpr> matrix, FieldVector<IExpr> b) {
		this.originalMatrix = matrix;
		this.rowReducedMatrix = matrix.copy();
		this.numRows = matrix.getRowDimension();
		this.numCols = matrix.getColumnDimension();
		this.matrixRankCache = -1;
		this.nullSpaceCache = null;
		rowReduce();
	}

	/**
	 * Test if <code>expr</code> equals the zero element.
	 * 
	 * @param expr
	 * @return
	 */
	protected boolean isZero(IExpr expr) {
		return expr.isZero();
	}

	/**
	 * Test if <code>expr</code> equals the one element.
	 * 
	 * @param expr
	 * @return
	 */
	protected boolean isOne(IExpr expr) {
		return expr.isOne();
	}

	private RowColIndex findPivot(RowColIndex a) {
		int first_row = a.row;
		RowColIndex pivot = new RowColIndex(a.row, a.col);
		RowColIndex current = new RowColIndex(a.row, a.col);

		for (int i = a.row + 1; i < (numRows - first_row); i++) {
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
	 * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia -
	 * Row echelon form</a>.
	 * 
	 * @return
	 */
	public FieldMatrix<IExpr> getRowReducedMatrix() {
		return rowReducedMatrix;
	}

	/**
	 * Swap the rows <code>a.row</code> and <code>b.row</code> in the matrix and
	 * swap the <code>row</code> values in the corresponding
	 * <code>RowColIndex</code> objects.
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
	 * Test if the column <code>a.col</code> of the matrix contains only
	 * zero-elements starting with the <code>a.row</code> element.
	 * 
	 * @param a
	 * @return
	 */
	private boolean isColumnZeroFromRow(RowColIndex a) {
		for (int i = a.row; i < numRows; i++) {
			if (!isZero(rowReducedMatrix.getEntry(i, a.col))) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Test if the row <code>a.row</code> of the matrix contains only
	 * zero-elements.
	 * 
	 * @param a
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
	 * Add the values of the row <code>to.row</code> to the product of the
	 * values of the row <code>from.row * factor</code> and assign the result
	 * values back to the row <code>to.row</code>.
	 * 
	 * @param to
	 * @param from
	 * @param scalar
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
	 * See: <a href="http://en.wikipedia.org/wiki/Kernel_%28linear_algebra%29">
	 * Wikipedia - Kernel (linear algebra)</a>.
	 * <a href="http://en.wikibooks.org/wiki/Linear_Algebra/Null_Spaces">
	 * Wikibooks - Null Spaces</a>.
	 * 
	 * @param minusOneFactor
	 *            factor <code>-1</code> for multiplying all elements of the
	 *            free part of the reduced row echelon form matrix
	 * @return <code>null</code> if the input matrix has full rank, otherwise
	 *         return the nullspaace.
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
							nullSpaceCache.setEntry(numberOfFreeColumns, j + offset, rowReducedMatrix.getEntry(j, i));
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
	 * Create the &quot;reduced row echelon form&quot; of a matrix.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia -
	 * Row echelon form</a>.
	 * 
	 * @return
	 */
	private FieldMatrix<IExpr> rowReduce() {
		int maxRows = numRows;
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
				if (pivot.row >= maxRows) {
					break;
				}
				continue;
			}

			// If necessary, interchange rows to move this entry into the pivot
			// position.
			// move this row to the top of the submatrix
			if (pivot.row != submatrix) {
				swapRow(new RowColIndex(submatrix, pivot.col), pivot);
			}

			// Force pivot to be 1
			if (!isOne(getCoordinate(pivot))) {
				IExpr scalar = getCoordinate(pivot).inverse();
				scaleRow(pivot, scalar);
			}
			// Step 3
			// Use row replacement operations to create zeroes in all positions
			// below the pivot.
			// belowPivot = belowPivot + (Pivot * -belowPivot)
			for (int i = pivot.row; i < numRows; i++) {
				if (i == pivot.row) {
					continue;
				}
				RowColIndex belowPivot = new RowColIndex(i, pivot.col);
				IExpr complement = (getCoordinate(belowPivot).negate().divide(getCoordinate(pivot)));
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
				if (i == pivot.row) {
					continue;
				}

				RowColIndex abovePivot = new RowColIndex(i, pivot.col);
				IExpr complement = (getCoordinate(abovePivot).negate().divide(getCoordinate(pivot)));
				multiplyAdd(abovePivot, pivot, complement);
			}
			// Step 4
			// Ignore the row containing the pivot position and cover all rows,
			// if any, above it.
			// Apply steps 1-3 to the remaining submatrix. Repeat until there
			// are no more nonzero entries.
			if ((pivot.row + 1) >= maxRows) { // || isRowZeroes(pivot.row + 1))
												// {
				break;
			}

			submatrix++;
			pivot.row++;
		}
		EvalEngine engine = EvalEngine.get();
		IEvalStepListener listener = engine.getStepListener();
		if (listener != null) {
			listener.add(Convert.matrix2List(originalMatrix), Convert.matrix2List(rowReducedMatrix),
					engine.getRecursionCounter(), -1, "ReducedRowEchelonForm");
		}
		return rowReducedMatrix;
	}

	/**
	 * Get the rank of the row reduced matrix.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Rank_%28linear_algebra%29">
	 * Wikipedia - Rank (linear algebra)</a>.
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
	 * Multiply the <code>x.row</code> elements with the scalar
	 * <code>factor</code>.
	 * 
	 * @param x
	 * @param d
	 */
	private void scaleRow(RowColIndex x, IExpr factor) {
		for (int i = 0; i < numCols; i++) {
			rowReducedMatrix.multiplyEntry(x.row, i, factor);
		}
	}

}
