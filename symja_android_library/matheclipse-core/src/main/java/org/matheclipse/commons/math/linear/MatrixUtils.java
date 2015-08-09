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

import java.util.Arrays;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NoDataException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.ZeroException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.linear.MatrixDimensionMismatchException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * A collection of static methods that operate on or return matrices.
 *
 */
public class MatrixUtils {

	/**
	 * Private constructor.
	 */
	private MatrixUtils() {
		super();
	}

	/**
	 * Returns a {@link FieldMatrix} with specified dimensions.
	 * <p>
	 * The type of matrix returned depends on the dimension. Below 2<sup>12</sup> elements (i.e. 4096 elements or 64&times;64 for a
	 * square matrix), a {@link FieldMatrix} instance is built. Above this threshold a {@link BlockFieldMatrix} instance is built.
	 * </p>
	 * <p>
	 * The matrix elements are all set to field.getZero().
	 * </p>
	 * 
	 * @param <T>
	 *            the type of the field elements
	 * @param field
	 *            field to which the matrix elements belong
	 * @param rows
	 *            number of rows of the matrix
	 * @param columns
	 *            number of columns of the matrix
	 * @return FieldMatrix with specified dimensions
	 * @see #createFieldMatrix(IExpr[][])
	 * @since 2.0
	 */
	public static FieldMatrix createFieldMatrix(final int rows, final int columns) {
		return (rows * columns <= 4096) ? new Array2DRowFieldMatrix(rows, columns) : new BlockFieldMatrix(rows, columns);
	}

	/**
	 * Returns a {@link FieldMatrix} whose entries are the the values in the the input array.
	 * <p>
	 * The type of matrix returned depends on the dimension. Below 2<sup>12</sup> elements (i.e. 4096 elements or 64&times;64 for a
	 * square matrix), a {@link FieldMatrix} instance is built. Above this threshold a {@link BlockFieldMatrix} instance is built.
	 * </p>
	 * <p>
	 * The input array is copied, not referenced.
	 * </p>
	 * 
	 * @param <T>
	 *            the type of the field elements
	 * @param data
	 *            input array
	 * @return a matrix containing the values of the array.
	 * @throws org.apache.commons.math4.exception.DimensionMismatchException
	 *             if {@code data} is not rectangular (not all rows have the same length).
	 * @throws NoDataException
	 *             if a row or column is empty.
	 * @throws NullArgumentException
	 *             if either {@code data} or {@code data[0]} is {@code null}.
	 * @see #createFieldMatrix(Field, int, int)
	 * @since 2.0
	 */
	public static FieldMatrix createFieldMatrix(IExpr[][] data) throws DimensionMismatchException, NoDataException,
			NullArgumentException {
		if (data == null || data[0] == null) {
			throw new NullArgumentException();
		}
		return (data.length * data[0].length <= 4096) ? new Array2DRowFieldMatrix(data) : new BlockFieldMatrix(data);
	}

	/**
	 * Returns <code>dimension x dimension</code> identity matrix.
	 *
	 * @param <T>
	 *            the type of the field elements
	 * @param field
	 *            field to which the elements belong
	 * @param dimension
	 *            dimension of identity matrix to generate
	 * @return identity matrix
	 * @throws IllegalArgumentException
	 *             if dimension is not positive
	 * @since 2.0
	 */
	public static FieldMatrix createFieldIdentityMatrix(final int dimension) {
		final IExpr zero = F.C0;
		final IExpr one = F.C1;
		final IExpr[][] d = MathArrays.buildArray(dimension, dimension);
		for (int row = 0; row < dimension; row++) {
			final IExpr[] dRow = d[row];
			Arrays.fill(dRow, zero);
			dRow[row] = one;
		}
		return new Array2DRowFieldMatrix(d, false);
	}

	/**
	 * Returns a diagonal matrix with specified elements.
	 *
	 * @param <T>
	 *            the type of the field elements
	 * @param diagonal
	 *            diagonal elements of the matrix (the array elements will be copied)
	 * @return diagonal matrix
	 * @since 2.0
	 */
	public static FieldMatrix createFieldDiagonalMatrix(final IExpr[] diagonal) {
		final FieldMatrix m = createFieldMatrix(diagonal.length, diagonal.length);
		for (int i = 0; i < diagonal.length; ++i) {
			m.setEntry(i, i, diagonal[i]);
		}
		return m;
	}

	/**
	 * Creates a {@link FieldVector} using the data from the input array.
	 *
	 * @param <T>
	 *            the type of the field elements
	 * @param data
	 *            the input data
	 * @return a data.length FieldVector
	 * @throws NoDataException
	 *             if {@code data} is empty.
	 * @throws NullArgumentException
	 *             if {@code data} is {@code null}.
	 * @throws ZeroException
	 *             if {@code data} has 0 elements
	 */
	public static FieldVector createFieldVector(final IExpr[] data) throws NoDataException, NullArgumentException, ZeroException {
		if (data == null) {
			throw new NullArgumentException();
		}
		if (data.length == 0) {
			throw new ZeroException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT);
		}
		return new ArrayFieldVector(data, true);
	}

	/**
	 * Create a row {@link FieldMatrix} using the data from the input array.
	 *
	 * @param <T>
	 *            the type of the field elements
	 * @param rowData
	 *            the input row data
	 * @return a 1 x rowData.length FieldMatrix
	 * @throws NoDataException
	 *             if {@code rowData} is empty.
	 * @throws NullArgumentException
	 *             if {@code rowData} is {@code null}.
	 */
	public static <T extends IExpr> FieldMatrix createRowFieldMatrix(final T[] rowData) throws NoDataException,
			NullArgumentException {
		if (rowData == null) {
			throw new NullArgumentException();
		}
		final int nCols = rowData.length;
		if (nCols == 0) {
			throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
		}
		final FieldMatrix m = createFieldMatrix(1, nCols);
		for (int i = 0; i < nCols; ++i) {
			m.setEntry(0, i, rowData[i]);
		}
		return m;
	}

	/**
	 * Creates a column {@link FieldMatrix} using the data from the input array.
	 *
	 * @param <T>
	 *            the type of the field elements
	 * @param columnData
	 *            the input column data
	 * @return a columnData x 1 FieldMatrix
	 * @throws NoDataException
	 *             if {@code data} is empty.
	 * @throws NullArgumentException
	 *             if {@code columnData} is {@code null}.
	 */
	public static <T extends IExpr> FieldMatrix createColumnFieldMatrix(final T[] columnData) throws NoDataException,
			NullArgumentException {
		if (columnData == null) {
			throw new NullArgumentException();
		}
		final int nRows = columnData.length;
		if (nRows == 0) {
			throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
		}
		final FieldMatrix m = createFieldMatrix(nRows, 1);
		for (int i = 0; i < nRows; ++i) {
			m.setEntry(i, 0, columnData[i]);
		}
		return m;
	}

	/**
	 * Check if matrix indices are valid.
	 *
	 * @param m
	 *            Matrix.
	 * @param row
	 *            Row index to check.
	 * @param column
	 *            Column index to check.
	 * @throws OutOfRangeException
	 *             if {@code row} or {@code column} is not a valid index.
	 */
	public static void checkMatrixIndex(final AnyMatrix m, final int row, final int column) throws OutOfRangeException {
		checkRowIndex(m, row);
		checkColumnIndex(m, column);
	}

	/**
	 * Check if a row index is valid.
	 *
	 * @param m
	 *            Matrix.
	 * @param row
	 *            Row index to check.
	 * @throws OutOfRangeException
	 *             if {@code row} is not a valid index.
	 */
	public static void checkRowIndex(final AnyMatrix m, final int row) throws OutOfRangeException {
		if (row < 0 || row >= m.getRowDimension()) {
			throw new OutOfRangeException(LocalizedFormats.ROW_INDEX, row, 0, m.getRowDimension() - 1);
		}
	}

	/**
	 * Check if a column index is valid.
	 *
	 * @param m
	 *            Matrix.
	 * @param column
	 *            Column index to check.
	 * @throws OutOfRangeException
	 *             if {@code column} is not a valid index.
	 */
	public static void checkColumnIndex(final AnyMatrix m, final int column) throws OutOfRangeException {
		if (column < 0 || column >= m.getColumnDimension()) {
			throw new OutOfRangeException(LocalizedFormats.COLUMN_INDEX, column, 0, m.getColumnDimension() - 1);
		}
	}

	/**
	 * Check if submatrix ranges indices are valid. Rows and columns are indicated counting from 0 to {@code n - 1}.
	 *
	 * @param m
	 *            Matrix.
	 * @param startRow
	 *            Initial row index.
	 * @param endRow
	 *            Final row index.
	 * @param startColumn
	 *            Initial column index.
	 * @param endColumn
	 *            Final column index.
	 * @throws OutOfRangeException
	 *             if the indices are invalid.
	 * @throws NumberIsTooSmallException
	 *             if {@code endRow < startRow} or {@code endColumn < startColumn}.
	 */
	public static void checkSubMatrixIndex(final AnyMatrix m, final int startRow, final int endRow, final int startColumn,
			final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
		checkRowIndex(m, startRow);
		checkRowIndex(m, endRow);
		if (endRow < startRow) {
			throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, endRow, startRow, false);
		}

		checkColumnIndex(m, startColumn);
		checkColumnIndex(m, endColumn);
		if (endColumn < startColumn) {
			throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN, endColumn, startColumn, false);
		}

	}

	/**
	 * Check if submatrix ranges indices are valid. Rows and columns are indicated counting from 0 to n-1.
	 *
	 * @param m
	 *            Matrix.
	 * @param selectedRows
	 *            Array of row indices.
	 * @param selectedColumns
	 *            Array of column indices.
	 * @throws NullArgumentException
	 *             if {@code selectedRows} or {@code selectedColumns} are {@code null}.
	 * @throws NoDataException
	 *             if the row or column selections are empty (zero length).
	 * @throws OutOfRangeException
	 *             if row or column selections are not valid.
	 */
	public static void checkSubMatrixIndex(final AnyMatrix m, final int[] selectedRows, final int[] selectedColumns)
			throws NoDataException, NullArgumentException, OutOfRangeException {
		if (selectedRows == null) {
			throw new NullArgumentException();
		}
		if (selectedColumns == null) {
			throw new NullArgumentException();
		}
		if (selectedRows.length == 0) {
			throw new NoDataException(LocalizedFormats.EMPTY_SELECTED_ROW_INDEX_ARRAY);
		}
		if (selectedColumns.length == 0) {
			throw new NoDataException(LocalizedFormats.EMPTY_SELECTED_COLUMN_INDEX_ARRAY);
		}

		for (final int row : selectedRows) {
			checkRowIndex(m, row);
		}
		for (final int column : selectedColumns) {
			checkColumnIndex(m, column);
		}
	}

	/**
	 * Check if matrices are addition compatible.
	 *
	 * @param left
	 *            Left hand side matrix.
	 * @param right
	 *            Right hand side matrix.
	 * @throws MatrixDimensionMismatchException
	 *             if the matrices are not addition compatible.
	 */
	public static void checkAdditionCompatible(final AnyMatrix left, final AnyMatrix right) throws MatrixDimensionMismatchException {
		if ((left.getRowDimension() != right.getRowDimension()) || (left.getColumnDimension() != right.getColumnDimension())) {
			throw new MatrixDimensionMismatchException(left.getRowDimension(), left.getColumnDimension(), right.getRowDimension(),
					right.getColumnDimension());
		}
	}

	/**
	 * Check if matrices are subtraction compatible
	 *
	 * @param left
	 *            Left hand side matrix.
	 * @param right
	 *            Right hand side matrix.
	 * @throws MatrixDimensionMismatchException
	 *             if the matrices are not addition compatible.
	 */
	public static void checkSubtractionCompatible(final AnyMatrix left, final AnyMatrix right)
			throws MatrixDimensionMismatchException {
		if ((left.getRowDimension() != right.getRowDimension()) || (left.getColumnDimension() != right.getColumnDimension())) {
			throw new MatrixDimensionMismatchException(left.getRowDimension(), left.getColumnDimension(), right.getRowDimension(),
					right.getColumnDimension());
		}
	}

	/**
	 * Check if matrices are multiplication compatible
	 *
	 * @param left
	 *            Left hand side matrix.
	 * @param right
	 *            Right hand side matrix.
	 * @throws DimensionMismatchException
	 *             if matrices are not multiplication compatible.
	 */
	public static void checkMultiplicationCompatible(final AnyMatrix left, final AnyMatrix right) throws DimensionMismatchException {

		if (left.getColumnDimension() != right.getRowDimension()) {
			throw new DimensionMismatchException(left.getColumnDimension(), right.getRowDimension());
		}
	}

}
