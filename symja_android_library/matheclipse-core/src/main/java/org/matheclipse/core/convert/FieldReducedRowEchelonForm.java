package org.matheclipse.core.convert;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;
import org.apache.commons.math3.linear.FieldMatrix;

/**
 * Matrix class that wraps a <code>FieldMatrix&lt;T&gt;</code> matrix, which should be transformed to reduced row echelon format.
 * 
 * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon form</a>.
 * 
 * The code was adapted from: <a href="http://rosettacode.org/wiki/Reduced_row_echelon_form#Java">Rosetta Code - Reduced row echelon
 * form</a>
 */
public class FieldReducedRowEchelonForm<T extends FieldElement<T>> {
	static class RowColIndex {
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

	FieldMatrix<T> matrix;

	final T zero;
	final T one;
	final int numRows;
	final int numCols;

	/**
	 * Constructor that wraps a <code>FieldMatrix&lt;T&gt;</code> matrix, which should be transformed to reduced row echelon format.
	 * 
	 * @param matrix
	 * 
	 * @see #rowReduce()
	 */
	public FieldReducedRowEchelonForm(FieldMatrix<T> matrix) {
		this.matrix = matrix.copy();
		numRows = matrix.getRowDimension();
		numCols = matrix.getColumnDimension();
		Field<T> field = matrix.getField();
		zero = field.getZero();
		one = field.getOne();
	}

	private RowColIndex findPivot(RowColIndex a) {
		int first_row = a.row;
		RowColIndex pivot = new RowColIndex(a.row, a.col);
		RowColIndex current = new RowColIndex(a.row, a.col);

		for (int i = a.row + 1; i < (numRows - first_row); i++) {
			current.row = i;
			if (matrix.getEntry(current.row, current.col).equals(one)) {
				swapRow(current, a);
			}
		}

		current.row = a.row;
		for (int i = current.row; i < (numRows - first_row); i++) {
			current.row = i;
			if (!matrix.getEntry(current.row, current.col).equals(zero)) {
				pivot.row = i;
				break;
			}
		}

		return pivot;
	}

	private T getCoordinate(RowColIndex a) {
		return matrix.getEntry(a.row, a.col);
	}

	public FieldMatrix<T> getMatrix() {
		return matrix;
	}

	/**
	 * Swap the rows <code>a.row</code> and <code>b.row</code> in the matrix and swap the <code>row</code> values in the
	 * corresponding <code>RowColIndex</code> objects.
	 * 
	 * @param a
	 * @param b
	 */
	private void swapRow(RowColIndex a, RowColIndex b) {
		T[] temp = matrix.getRow(a.row);
		matrix.setRow(a.row, matrix.getRow(b.row));
		matrix.setRow(b.row, temp);

		int t = a.row;
		a.row = b.row;
		b.row = t;
	}

	/**
	 * Test if the column <code>a.col</code> of the matrix contains only zero-elements starting with the <code>a.row</code> element.
	 * 
	 * @param a
	 * @return
	 */
	private boolean isColumnZeroFromRow(RowColIndex a) {
		for (int i = a.row; i < numRows; i++) {
			if (!matrix.getEntry(i, a.col).equals(zero)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Test if the row <code>a.row</code> of the matrix contains only zero-elements.
	 * 
	 * @param a
	 * @return
	 */
	private boolean isRowZeroes(int row) {
		T[] temp = matrix.getRow(row);
		for (int i = 0; i < numCols; i++) {
			if (!temp[i].equals(zero)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Add the values of the row <code>to.row</code> to the product of the values of the row <code>from.row * factor</code> and
	 * assign the result values back to the row <code>to.row</code>.
	 * 
	 * @param to
	 * @param from
	 * @param scalar
	 */
	private void multiplyAdd(RowColIndex to, RowColIndex from, T factor) {
		T[] row = matrix.getRow(to.row);
		T[] rowMultiplied = matrix.getRow(from.row);

		for (int i = 0; i < numCols; i++) {
			matrix.setEntry(to.row, i, row[i].add((rowMultiplied[i].multiply(factor))));
		}
	}

	public FieldMatrix<T> nullSpace(T scalar) {
		FieldMatrix<T> matrix = rowReduce();
		int rows = matrix.getRowDimension() - 1;
		int rank = 0;
		for (int i = rows; i >= 0; i--) {
			if (!isRowZeroes(i)) {
				rank = i + 1;
				break;
			}
		}

		int newRowDimension = matrix.getColumnDimension() - rank;
		int newColumnDimension = matrix.getColumnDimension();
		FieldMatrix<T> result = matrix.createMatrix(newRowDimension, newColumnDimension);
		return getResultOfNullspace(result, scalar, rank);
	}

	private FieldMatrix<T> getResultOfNullspace(FieldMatrix<T> result, T scalar, int rank) {

		// search free columns
		boolean[] columns = new boolean[result.getColumnDimension()];
		int numberOfFreeColumns = 0;
		for (int i = 0; i < rank; i++) {
			if (!columns[i]) {
				for (int k = i; k < matrix.getColumnDimension(); k++) {
					if (matrix.getEntry(i, k).equals(zero)) {
						columns[k] = true;
						// free column
						int offset = 0;
						for (int j = 0; j < rank; j++) {
							if (columns[j]) {
								offset++;
							}
							result.setEntry(numberOfFreeColumns, j + offset, matrix.getEntry(j, i));
						}
						numberOfFreeColumns++;
					} else {
						break;
					}
				}
			}
		}

		// Let's take the rest of the 'free part' of the reduced row echelon form
		int start = rank + numberOfFreeColumns;
		int row = numberOfFreeColumns;
		for (int i = start; i < result.getColumnDimension(); i++) {
			int offset = 0;
			for (int j = 0; j < rank; j++) {
				if (columns[j]) {
					offset++;
				}
				result.setEntry(row, j + offset, matrix.getEntry(j, i));
			}
			row++;
		}
		for (int i = start; i < result.getColumnDimension(); i++) {
			columns[i] = true;
		}

		// multiply matrix with scalar -1
		result = result.scalarMultiply(scalar);

		// append the 'one element' (typically as identity matrix)
		row = 0;
		for (int i = 0; i < columns.length; i++) {
			if (columns[i]) {
				result.setEntry(row++, i, one);
			}
		}
		return result;
	}

	/**
	 * Create the &quot;reduced row echelon form&quot; of a matrix.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Row_echelon_form">Wikipedia - Row echelon form</a>.
	 * 
	 * @return
	 */
	public FieldMatrix<T> rowReduce() {
		int maxRows = numRows;
		RowColIndex pivot = new RowColIndex(0, 0);
		int submatrix = 0;
		for (int x = 0; x < numCols; x++) {
			pivot = new RowColIndex(pivot.row, x);
			// Step 1
			// Begin with the leftmost nonzero column. This is a pivot column. The pivot position is at the top.
			for (int i = x; i < numCols; i++) {
				if (isColumnZeroFromRow(pivot) == false) {
					break;
				} else {
					pivot.col = i;
				}
			}
			// Step 2
			// Select a nonzero entry in the pivot column with the highest absolute value as a pivot.
			pivot = findPivot(pivot);

			if (getCoordinate(pivot).equals(zero)) {
				pivot.row++;
				if (pivot.row >= maxRows) {
					break;
				}
				continue;
			}

			// If necessary, interchange rows to move this entry into the pivot position.
			// move this row to the top of the submatrix
			if (pivot.row != submatrix) {
				swapRow(new RowColIndex(submatrix, pivot.col), pivot);
			}

			// Force pivot to be 1
			if (!getCoordinate(pivot).equals(one)) {
				T scalar = getCoordinate(pivot).reciprocal();
				scaleRow(pivot, scalar);
			}
			// Step 3
			// Use row replacement operations to create zeroes in all positions below the pivot.
			// belowPivot = belowPivot + (Pivot * -belowPivot)
			for (int i = pivot.row; i < numRows; i++) {
				if (i == pivot.row) {
					continue;
				}
				RowColIndex belowPivot = new RowColIndex(i, pivot.col);
				T complement = (getCoordinate(belowPivot).negate().divide(getCoordinate(pivot)));
				multiplyAdd(belowPivot, pivot, complement);
			}
			// Step 5
			// Beginning with the rightmost pivot and working upward and to the left, create zeroes above each pivot.
			// If a pivot is not 1, make it 1 by a scaling operation.
			// Use row replacement operations to create zeroes in all positions above the pivot
			for (int i = pivot.row; i >= 0; i--) {
				if (i == pivot.row) {
					if (!getCoordinate(pivot).equals(one)) {
						scaleRow(pivot, getCoordinate(pivot).reciprocal());
					}
					continue;
				}
				if (i == pivot.row) {
					continue;
				}

				RowColIndex abovePivot = new RowColIndex(i, pivot.col);
				T complement = (getCoordinate(abovePivot).negate().divide(getCoordinate(pivot)));
				multiplyAdd(abovePivot, pivot, complement);
			}
			// Step 4
			// Ignore the row containing the pivot position and cover all rows, if any, above it.
			// Apply steps 1-3 to the remaining submatrix. Repeat until there are no more nonzero entries.
			if ((pivot.row + 1) >= maxRows) { // || isRowZeroes(pivot.row + 1)) {
				break;
			}

			submatrix++;
			pivot.row++;
		}
		return matrix;
	}

	public int rank() {
		if (matrix.getRowDimension() == 0 || matrix.getColumnDimension() == 0) {
			return 0;
		}
		FieldMatrix<T> matrix = rowReduce();
		int rows = matrix.getRowDimension() - 1;
		for (int i = rows; i >= 0; i--) {
			if (!isRowZeroes(i)) {
				return i + 1;
			}
		}
		return 0;
	}

	/**
	 * Multily the <code>x.row</code> elements with the scalar <code>factor</code>.
	 * 
	 * @param x
	 * @param d
	 */
	private void scaleRow(RowColIndex x, T factor) {
		for (int i = 0; i < numCols; i++) {
			matrix.multiplyEntry(x.row, i, factor);
		}
	}

}
