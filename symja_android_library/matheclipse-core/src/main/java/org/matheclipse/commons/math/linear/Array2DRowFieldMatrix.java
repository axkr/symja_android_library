/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.matheclipse.commons.math.linear;

import java.io.Serializable;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathIllegalStateException;
import org.apache.commons.math4.exception.NoDataException;
import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.linear.MatrixDimensionMismatchException;
import org.apache.commons.math4.util.MathUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Implementation of FieldMatrix<IExpr> using a {@link IExpr}[][] array to store entries.
 * <p>
 * As specified in the {@link FieldMatrix} interface, matrix element indexing is 0-based -- e.g., <code>getEntry(0, 0)</code>
 * returns the element in the first row, first column of the matrix.</li></ul>
 * </p>
 *
 * @param <T>
 *            the type of the field elements
 */
public class Array2DRowFieldMatrix extends AbstractFieldMatrix implements Serializable {
	/** Serializable version identifier */
	private static final long serialVersionUID = 7260756672015356458L;
	/** Entries of the matrix */
	private IExpr[][] data;

	/**
	 * Creates a matrix with no data
	 */
	public Array2DRowFieldMatrix() {
		super();
	}

	/**
	 * Create a new {@code FieldMatrix<IExpr>} with the supplied row and column dimensions.
	 * 
	 * @param rowDimension
	 *            Number of rows in the new matrix.
	 * @param columnDimension
	 *            Number of columns in the new matrix.
	 *
	 * @throws NotStrictlyPositiveException
	 *             if row or column dimension is not positive.
	 */
	public Array2DRowFieldMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException {
		super(rowDimension, columnDimension);
		data = MathArrays.buildArray(rowDimension, columnDimension);
	}

	/**
	 * Create a new {@code FieldMatrix<IExpr>} using the input array as the underlying data array.
	 * <p>
	 * The input array is copied, not referenced. This constructor has the same effect as calling
	 * {@link #Array2DRowFieldMatrix(FieldElemenIExpr[][], boolean)} with the second argument set to {@code true}.
	 * </p>
	 *
	 * @param d
	 *            Data for the new matrix.
	 * @throws DimensionMismatchException
	 *             if {@code d} is not rectangular.
	 * @throws NullArgumentException
	 *             if {@code d} is {@code null}.
	 * @throws NoDataException
	 *             if there are not at least one row and one column.
	 * @see #Array2DRowFieldMatrix(FieldElemenIExpr[][], boolean)
	 */
	public Array2DRowFieldMatrix(final IExpr[][] d) throws DimensionMismatchException, NullArgumentException, NoDataException {
		super();
		copyIn(d);
	}

	/**
	 * Create a new {@code FieldMatrix<IExpr>} using the input array as the underlying data array.
	 * <p>
	 * If an array is built specially in order to be embedded in a {@code FieldMatrix<IExpr>} and not used directly, the
	 * {@code copyArray} may be set to {@code false}. This will prevent the copying and improve performance as no new array will be
	 * built and no data will be copied.
	 * </p>
	 *
	 * @param field
	 *            Field to which the elements belong.
	 * @param d
	 *            Data for the new matrix.
	 * @param copyArray
	 *            Whether to copy or reference the input array.
	 * @throws DimensionMismatchException
	 *             if {@code d} is not rectangular.
	 * @throws NoDataException
	 *             if there are not at least one row and one column.
	 * @throws NullArgumentException
	 *             if {@code d} is {@code null}.
	 * @see #Array2DRowFieldMatrix()
	 */
	public Array2DRowFieldMatrix(final IExpr[][] d, final boolean copyArray) throws DimensionMismatchException, NoDataException,
			NullArgumentException {
		super();
		if (copyArray) {
			copyIn(d);
		} else {
			MathUtils.checkNotNull(d);
			final int nRows = d.length;
			if (nRows == 0) {
				throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
			}
			final int nCols = d[0].length;
			if (nCols == 0) {
				throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
			}
			for (int r = 1; r < nRows; r++) {
				if (d[r].length != nCols) {
					throw new DimensionMismatchException(nCols, d[r].length);
				}
			}
			data = d;
		}
	}

	/**
	 * Create a new (column) {@code FieldMatrix<IExpr>} using {@code v} as the data for the unique column of the created matrix. The
	 * input array is copied.
	 *
	 * @param field
	 *            Field to which the elements belong.
	 * @param v
	 *            Column vector holding data for new matrix.
	 */
	public Array2DRowFieldMatrix(final IExpr[] v) {
		super();
		final int nRows = v.length;
		data = MathArrays.buildArray(nRows, 1);
		for (int row = 0; row < nRows; row++) {
			data[row][0] = v[row];
		}
	}

	/** {@inheritDoc} */
	@Override
	public FieldMatrix createMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException {
		return new Array2DRowFieldMatrix(rowDimension, columnDimension);
	}

	/** {@inheritDoc} */
	@Override
	public FieldMatrix copy() {
		return new Array2DRowFieldMatrix(copyOut(), false);
	}

	/**
	 * Add {@code m} to this matrix.
	 *
	 * @param m
	 *            Matrix to be added.
	 * @return {@code this} + m.
	 * @throws MatrixDimensionMismatchException
	 *             if {@code m} is not the same size as this matrix.
	 */
	public Array2DRowFieldMatrix add(final Array2DRowFieldMatrix m) throws MatrixDimensionMismatchException {
		// safety check
		checkAdditionCompatible(m);

		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final IExpr[][] outData = MathArrays.buildArray(rowCount, columnCount);
		for (int row = 0; row < rowCount; row++) {
			final IExpr[] dataRow = data[row];
			final IExpr[] mRow = m.data[row];
			final IExpr[] outDataRow = outData[row];
			for (int col = 0; col < columnCount; col++) {
				outDataRow[col] = dataRow[col].plus(mRow[col]);
			}
		}

		return new Array2DRowFieldMatrix(outData, false);
	}

	/**
	 * Subtract {@code m} from this matrix.
	 *
	 * @param m
	 *            Matrix to be subtracted.
	 * @return {@code this} + m.
	 * @throws MatrixDimensionMismatchException
	 *             if {@code m} is not the same size as this matrix.
	 */
	public Array2DRowFieldMatrix subtract(final Array2DRowFieldMatrix m) throws MatrixDimensionMismatchException {
		// safety check
		checkSubtractionCompatible(m);

		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final IExpr[][] outData = MathArrays.buildArray(rowCount, columnCount);
		for (int row = 0; row < rowCount; row++) {
			final IExpr[] dataRow = data[row];
			final IExpr[] mRow = m.data[row];
			final IExpr[] outDataRow = outData[row];
			for (int col = 0; col < columnCount; col++) {
				outDataRow[col] = dataRow[col].subtract(mRow[col]);
			}
		}

		return new Array2DRowFieldMatrix(outData, false);

	}

	/**
	 * Postmultiplying this matrix by {@code m}.
	 *
	 * @param m
	 *            Matrix to postmultiply by.
	 * @return {@code this} * m.
	 * @throws DimensionMismatchException
	 *             if the number of columns of this matrix is not equal to the number of rows of {@code m}.
	 */
	public Array2DRowFieldMatrix multiply(final Array2DRowFieldMatrix m) throws DimensionMismatchException {
		// safety check
		checkMultiplicationCompatible(m);

		final int nRows = this.getRowDimension();
		final int nCols = m.getColumnDimension();
		final int nSum = this.getColumnDimension();
		final IExpr[][] outData = MathArrays.buildArray(nRows, nCols);
		for (int row = 0; row < nRows; row++) {
			final IExpr[] dataRow = data[row];
			final IExpr[] outDataRow = outData[row];
			for (int col = 0; col < nCols; col++) {
				IExpr sum = F.C0;
				for (int i = 0; i < nSum; i++) {
					sum = sum.plus(dataRow[i].times(m.data[i][col]));
				}
				outDataRow[col] = sum;
			}
		}

		return new Array2DRowFieldMatrix(outData, false);

	}

	/** {@inheritDoc} */
	@Override
	public IExpr[][] getData() {
		return copyOut();
	}

	/**
	 * Get a reference to the underlying data array. This methods returns internal data, <strong>not</strong> fresh copy of it.
	 *
	 * @return the 2-dimensional array of entries.
	 */
	public IExpr[][] getDataRef() {
		return data;
	}

	/** {@inheritDoc} */
	@Override
	public void setSubMatrix(final IExpr[][] subMatrix, final int row, final int column) throws OutOfRangeException,
			NullArgumentException, NoDataException, DimensionMismatchException {
		if (data == null) {
			if (row > 0) {
				throw new MathIllegalStateException(LocalizedFormats.FIRST_ROWS_NOT_INITIALIZED_YET, row);
			}
			if (column > 0) {
				throw new MathIllegalStateException(LocalizedFormats.FIRST_COLUMNS_NOT_INITIALIZED_YET, column);
			}
			final int nRows = subMatrix.length;
			if (nRows == 0) {
				throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
			}

			final int nCols = subMatrix[0].length;
			if (nCols == 0) {
				throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
			}
			data = MathArrays.buildArray(subMatrix.length, nCols);
			for (int i = 0; i < data.length; ++i) {
				if (subMatrix[i].length != nCols) {
					throw new DimensionMismatchException(nCols, subMatrix[i].length);
				}
				System.arraycopy(subMatrix[i], 0, data[i + row], column, nCols);
			}
		} else {
			super.setSubMatrix(subMatrix, row, column);
		}

	}

	/** {@inheritDoc} */
	@Override
	public IExpr getEntry(final int row, final int column) throws OutOfRangeException {
		checkRowIndex(row);
		checkColumnIndex(column);

		return data[row][column];
	}

	/** {@inheritDoc} */
	@Override
	public void setEntry(final int row, final int column, final IExpr value) throws OutOfRangeException {
		checkRowIndex(row);
		checkColumnIndex(column);

		data[row][column] = value;
	}

	/** {@inheritDoc} */
	@Override
	public void addToEntry(final int row, final int column, final IExpr increment) throws OutOfRangeException {
		checkRowIndex(row);
		checkColumnIndex(column);

		data[row][column] = data[row][column].plus(increment);
	}

	/** {@inheritDoc} */
	@Override
	public void multiplyEntry(final int row, final int column, final IExpr factor) throws OutOfRangeException {
		checkRowIndex(row);
		checkColumnIndex(column);

		data[row][column] = data[row][column].times(factor);
	}

	/** {@inheritDoc} */
	@Override
	public int getRowDimension() {
		return (data == null) ? 0 : data.length;
	}

	/** {@inheritDoc} */
	@Override
	public int getColumnDimension() {
		return ((data == null) || (data[0] == null)) ? 0 : data[0].length;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr[] operate(final IExpr[] v) throws DimensionMismatchException {
		final int nRows = this.getRowDimension();
		final int nCols = this.getColumnDimension();
		if (v.length != nCols) {
			throw new DimensionMismatchException(v.length, nCols);
		}
		final IExpr[] out = MathArrays.buildArray(nRows);
		for (int row = 0; row < nRows; row++) {
			final IExpr[] dataRow = data[row];
			IExpr sum = F.C0;
			for (int i = 0; i < nCols; i++) {
				sum = sum.plus(dataRow[i].times(v[i]));
			}
			out[row] = sum;
		}
		return out;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr[] preMultiply(final IExpr[] v) throws DimensionMismatchException {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		if (v.length != nRows) {
			throw new DimensionMismatchException(v.length, nRows);
		}

		final IExpr[] out = MathArrays.buildArray(nCols);
		for (int col = 0; col < nCols; ++col) {
			IExpr sum = F.C0;
			for (int i = 0; i < nRows; ++i) {
				sum = sum.plus(data[i][col].times(v[i]));
			}
			out[col] = sum;
		}

		return out;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr walkInRowOrder(final FieldMatrixChangingVisitor visitor) {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
		for (int i = 0; i < rows; ++i) {
			final IExpr[] rowI = data[i];
			for (int j = 0; j < columns; ++j) {
				rowI[j] = visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr walkInRowOrder(final FieldMatrixPreservingVisitor visitor) {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
		for (int i = 0; i < rows; ++i) {
			final IExpr[] rowI = data[i];
			for (int j = 0; j < columns; ++j) {
				visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr walkInRowOrder(final FieldMatrixChangingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int i = startRow; i <= endRow; ++i) {
			final IExpr[] rowI = data[i];
			for (int j = startColumn; j <= endColumn; ++j) {
				rowI[j] = visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr walkInRowOrder(final FieldMatrixPreservingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int i = startRow; i <= endRow; ++i) {
			final IExpr[] rowI = data[i];
			for (int j = startColumn; j <= endColumn; ++j) {
				visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr walkInColumnOrder(final FieldMatrixChangingVisitor visitor) {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
		for (int j = 0; j < columns; ++j) {
			for (int i = 0; i < rows; ++i) {
				final IExpr[] rowI = data[i];
				rowI[j] = visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr walkInColumnOrder(final FieldMatrixPreservingVisitor visitor) {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
		for (int j = 0; j < columns; ++j) {
			for (int i = 0; i < rows; ++i) {
				visitor.visit(i, j, data[i][j]);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr walkInColumnOrder(final FieldMatrixChangingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int j = startColumn; j <= endColumn; ++j) {
			for (int i = startRow; i <= endRow; ++i) {
				final IExpr[] rowI = data[i];
				rowI[j] = visitor.visit(i, j, rowI[j]);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	@Override
	public IExpr walkInColumnOrder(final FieldMatrixPreservingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws OutOfRangeException, NumberIsTooSmallException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int j = startColumn; j <= endColumn; ++j) {
			for (int i = startRow; i <= endRow; ++i) {
				visitor.visit(i, j, data[i][j]);
			}
		}
		return visitor.end();
	}

	/**
	 * Get a fresh copy of the underlying data array.
	 *
	 * @return a copy of the underlying data array.
	 */
	private IExpr[][] copyOut() {
		final int nRows = this.getRowDimension();
		final IExpr[][] out = MathArrays.buildArray(nRows, getColumnDimension());
		// can't copy 2-d array in one shot, otherwise get row references
		for (int i = 0; i < nRows; i++) {
			System.arraycopy(data[i], 0, out[i], 0, data[i].length);
		}
		return out;
	}

	/**
	 * Replace data with a fresh copy of the input array.
	 *
	 * @param in
	 *            Data to copy.
	 * @throws NoDataException
	 *             if the input array is empty.
	 * @throws DimensionMismatchException
	 *             if the input array is not rectangular.
	 * @throws NullArgumentException
	 *             if the input array is {@code null}.
	 */
	private void copyIn(final IExpr[][] in) throws NullArgumentException, NoDataException, DimensionMismatchException {
		setSubMatrix(in, 0, 0);
	}
}
