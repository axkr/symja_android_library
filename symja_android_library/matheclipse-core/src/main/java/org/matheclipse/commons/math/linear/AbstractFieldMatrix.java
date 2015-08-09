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

import java.util.ArrayList;

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NoDataException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.linear.MatrixDimensionMismatchException;
import org.apache.commons.math4.linear.NonSquareMatrixException;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Basic implementation of {@link FieldMatrix} methods regardless of the underlying storage.
 * <p>
 * All the methods implemented here use {@link #getEntry(int, int)} to access matrix elements. Derived class can provide faster
 * implementations.
 * </p>
 *
 * @param <T>
 *            Type of the field elements.
 *
 * @since 2.0
 */
public abstract class AbstractFieldMatrix implements FieldMatrix {

	/**
	 * Constructor for use with Serializable
	 */
	protected AbstractFieldMatrix() {
	}

	/**
	 * Create a new FieldMatrix with the supplied row and column dimensions.
	 * 
	 * @param rowDimension
	 *            Number of rows in the new matrix.
	 * @param columnDimension
	 *            Number of columns in the new matrix.
	 *
	 * @throws NotStrictlyPositiveException
	 *             if row or column dimension is not positive.
	 */
	protected AbstractFieldMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException {
		if (rowDimension <= 0) {
			throw new NotStrictlyPositiveException(LocalizedFormats.DIMENSION, rowDimension);
		}
		if (columnDimension <= 0) {
			throw new NotStrictlyPositiveException(LocalizedFormats.DIMENSION, columnDimension);
		}
	}

	/**
	 * Get the elements type from an array.
	 *
	 * @param <T>
	 *            Type of the field elements.
	 * @param d
	 *            Data array.
	 * @return the field to which the array elements belong.
	 * @throws NullArgumentException
	 *             if the array is {@code null}.
	 * @throws NoDataException
	 *             if the array is empty.
	 */
	// protected static <T extends IExpr> Field<IExpr> extractField(final IExpr[][] d)
	// throws NoDataException, NullArgumentException {
	// if (d == null) {
	// throw new NullArgumentException();
	// }
	// if (d.length == 0) {
	// throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
	// }
	// if (d[0].length == 0) {
	// throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
	// }
	// return d[0][0].getField();
	// }

	/**
	 * Get the elements type from an array.
	 *
	 * @param <T>
	 *            Type of the field elements.
	 * @param d
	 *            Data array.
	 * @return the field to which the array elements belong.
	 * @throws NoDataException
	 *             if array is empty.
	 */
	// protected static <T extends IExpr> Field<T> extractField(final IExpr[] d)
	// throws NoDataException {
	// if (d.length == 0) {
	// throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
	// }
	// return d[0].getField();
	// }

	/**
	 * Build an array of elements.
	 * <p>
	 * Complete arrays are filled with field.getZero()
	 * </p>
	 * 
	 * @param <T>
	 *            Type of the field elements
	 * @param field
	 *            field to which array elements belong
	 * @param rows
	 *            number of rows
	 * @param columns
	 *            number of columns (may be negative to build partial arrays in the same way <code>new Field[rows][]</code> works)
	 * @return a new array
	 * @deprecated as of 3.2, replaced by {@link MathArrays#buildArray(Field, int, int)}
	 */
	@Deprecated
	protected static IExpr[][] buildArray(final int rows, final int columns) {
		return MathArrays.buildArray(rows, columns);
	}

	/**
	 * Build an array of elements.
	 * <p>
	 * Arrays are filled with field.getZero()
	 * </p>
	 * 
	 * @param <T>
	 *            the type of the field elements
	 * @param field
	 *            field to which array elements belong
	 * @param length
	 *            of the array
	 * @return a new array
	 * @deprecated as of 3.2, replaced by {@link MathArrays#buildArray(Field, int)}
	 */
	@Deprecated
	protected static IExpr[] buildArray(final int length) {
		return MathArrays.buildArray(length);
	}

	/** {@inheritDoc} */
	public abstract FieldMatrix createMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException;

	/** {@inheritDoc} */
	public abstract FieldMatrix copy();

	/** {@inheritDoc} */
	public FieldMatrix add(FieldMatrix m) throws MatrixDimensionMismatchException {
		// safety check
		checkAdditionCompatible(m);

		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final FieldMatrix out = createMatrix(rowCount, columnCount);
		for (int row = 0; row < rowCount; ++row) {
			for (int col = 0; col < columnCount; ++col) {
				out.setEntry(row, col, getEntry(row, col).plus(m.getEntry(row, col)));
			}
		}

		return out;
	}

	/** {@inheritDoc} */
	public FieldMatrix subtract(final FieldMatrix m) throws MatrixDimensionMismatchException {
		// safety check
		checkSubtractionCompatible(m);

		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final FieldMatrix out = createMatrix(rowCount, columnCount);
		for (int row = 0; row < rowCount; ++row) {
			for (int col = 0; col < columnCount; ++col) {
				out.setEntry(row, col, getEntry(row, col).subtract(m.getEntry(row, col)));
			}
		}

		return out;
	}

	/** {@inheritDoc} */
	public FieldMatrix scalarAdd(final IExpr d) {

		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final FieldMatrix out = createMatrix(rowCount, columnCount);
		for (int row = 0; row < rowCount; ++row) {
			for (int col = 0; col < columnCount; ++col) {
				out.setEntry(row, col, getEntry(row, col).plus(d));
			}
		}

		return out;
	}

	/** {@inheritDoc} */
	public FieldMatrix scalarMultiply(final IExpr d) {
		final int rowCount = getRowDimension();
		final int columnCount = getColumnDimension();
		final FieldMatrix out = createMatrix(rowCount, columnCount);
		for (int row = 0; row < rowCount; ++row) {
			for (int col = 0; col < columnCount; ++col) {
				out.setEntry(row, col, getEntry(row, col).times(d));
			}
		}

		return out;
	}

	/** {@inheritDoc} */
	public FieldMatrix multiply(final FieldMatrix m) throws DimensionMismatchException {
		// safety check
		checkMultiplicationCompatible(m);

		final int nRows = getRowDimension();
		final int nCols = m.getColumnDimension();
		final int nSum = getColumnDimension();
		final FieldMatrix out = createMatrix(nRows, nCols);
		for (int row = 0; row < nRows; ++row) {
			for (int col = 0; col < nCols; ++col) {
				IExpr sum = F.C0;
				for (int i = 0; i < nSum; ++i) {
					sum = sum.plus(getEntry(row, i).times(m.getEntry(i, col)));
				}
				out.setEntry(row, col, sum);
			}
		}

		return out;
	}

	/** {@inheritDoc} */
	public FieldMatrix preMultiply(final FieldMatrix m) throws DimensionMismatchException {
		return m.multiply(this);
	}

	/** {@inheritDoc} */
	public FieldMatrix power(final int p) throws NonSquareMatrixException, NotPositiveException {
		if (p < 0) {
			throw new NotPositiveException(p);
		}

		if (!isSquare()) {
			throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
		}

		if (p == 0) {
			return MatrixUtils.createFieldIdentityMatrix(this.getRowDimension());
		}

		if (p == 1) {
			return this.copy();
		}

		final int power = p - 1;

		/*
		 * Only log_2(p) operations is used by doing as follows: 5^214 = 5^128 * 5^64 * 5^16 * 5^4 * 5^2
		 * 
		 * In general, the same approach is used for A^p.
		 */

		final char[] binaryRepresentation = Integer.toBinaryString(power).toCharArray();
		final ArrayList<Integer> nonZeroPositions = new ArrayList<Integer>();

		for (int i = 0; i < binaryRepresentation.length; ++i) {
			if (binaryRepresentation[i] == '1') {
				final int pos = binaryRepresentation.length - i - 1;
				nonZeroPositions.add(pos);
			}
		}

		ArrayList<FieldMatrix> results = new ArrayList<FieldMatrix>(binaryRepresentation.length);

		results.add(0, this.copy());

		for (int i = 1; i < binaryRepresentation.length; ++i) {
			final FieldMatrix s = results.get(i - 1);
			final FieldMatrix r = s.multiply(s);
			results.add(i, r);
		}

		FieldMatrix result = this.copy();

		for (Integer i : nonZeroPositions) {
			result = result.multiply(results.get(i));
		}

		return result;
	}

	/** {@inheritDoc} */
	public IExpr[][] getData() {
		final IExpr[][] data = MathArrays.buildArray(getRowDimension(), getColumnDimension());

		for (int i = 0; i < data.length; ++i) {
			final IExpr[] dataI = data[i];
			for (int j = 0; j < dataI.length; ++j) {
				dataI[j] = getEntry(i, j);
			}
		}

		return data;
	}

	/** {@inheritDoc} */
	public FieldMatrix getSubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn)
			throws NumberIsTooSmallException, OutOfRangeException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);

		final FieldMatrix subMatrix = createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
		for (int i = startRow; i <= endRow; ++i) {
			for (int j = startColumn; j <= endColumn; ++j) {
				subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
			}
		}

		return subMatrix;

	}

	/** {@inheritDoc} */
	public FieldMatrix getSubMatrix(final int[] selectedRows, final int[] selectedColumns) throws NoDataException,
			NullArgumentException, OutOfRangeException {

		// safety checks
		checkSubMatrixIndex(selectedRows, selectedColumns);

		// copy entries
		final FieldMatrix subMatrix = createMatrix(selectedRows.length, selectedColumns.length);
		subMatrix.walkInOptimizedOrder(new DefaultFieldMatrixChangingVisitor(F.C0) {

			/** {@inheritDoc} */
			@Override
			public IExpr visit(final int row, final int column, final IExpr value) {
				return getEntry(selectedRows[row], selectedColumns[column]);
			}

		});

		return subMatrix;

	}

	/** {@inheritDoc} */
	public void copySubMatrix(final int startRow, final int endRow, final int startColumn, final int endColumn,
			final IExpr[][] destination) throws MatrixDimensionMismatchException, NumberIsTooSmallException, OutOfRangeException {
		// safety checks
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		final int rowsCount = endRow + 1 - startRow;
		final int columnsCount = endColumn + 1 - startColumn;
		if ((destination.length < rowsCount) || (destination[0].length < columnsCount)) {
			throw new MatrixDimensionMismatchException(destination.length, destination[0].length, rowsCount, columnsCount);
		}

		// copy entries
		walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor(F.C0) {

			/** Initial row index. */
			private int startRow;

			/** Initial column index. */
			private int startColumn;

			/** {@inheritDoc} */
			@Override
			public void start(final int rows, final int columns, final int startRow, final int endRow, final int startColumn,
					final int endColumn) {
				this.startRow = startRow;
				this.startColumn = startColumn;
			}

			/** {@inheritDoc} */
			@Override
			public void visit(final int row, final int column, final IExpr value) {
				destination[row - startRow][column - startColumn] = value;
			}

		}, startRow, endRow, startColumn, endColumn);

	}

	/** {@inheritDoc} */
	public void copySubMatrix(int[] selectedRows, int[] selectedColumns, IExpr[][] destination)
			throws MatrixDimensionMismatchException, NoDataException, NullArgumentException, OutOfRangeException {
		// safety checks
		checkSubMatrixIndex(selectedRows, selectedColumns);
		if ((destination.length < selectedRows.length) || (destination[0].length < selectedColumns.length)) {
			throw new MatrixDimensionMismatchException(destination.length, destination[0].length, selectedRows.length,
					selectedColumns.length);
		}

		// copy entries
		for (int i = 0; i < selectedRows.length; i++) {
			final IExpr[] destinationI = destination[i];
			for (int j = 0; j < selectedColumns.length; j++) {
				destinationI[j] = getEntry(selectedRows[i], selectedColumns[j]);
			}
		}

	}

	/** {@inheritDoc} */
	public void setSubMatrix(final IExpr[][] subMatrix, final int row, final int column) throws DimensionMismatchException,
			OutOfRangeException, NoDataException, NullArgumentException {
		if (subMatrix == null) {
			throw new NullArgumentException();
		}
		final int nRows = subMatrix.length;
		if (nRows == 0) {
			throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_ROW);
		}

		final int nCols = subMatrix[0].length;
		if (nCols == 0) {
			throw new NoDataException(LocalizedFormats.AT_LEAST_ONE_COLUMN);
		}

		for (int r = 1; r < nRows; ++r) {
			if (subMatrix[r].length != nCols) {
				throw new DimensionMismatchException(nCols, subMatrix[r].length);
			}
		}

		checkRowIndex(row);
		checkColumnIndex(column);
		checkRowIndex(nRows + row - 1);
		checkColumnIndex(nCols + column - 1);

		for (int i = 0; i < nRows; ++i) {
			for (int j = 0; j < nCols; ++j) {
				setEntry(row + i, column + j, subMatrix[i][j]);
			}
		}
	}

	/** {@inheritDoc} */
	public FieldMatrix getRowMatrix(final int row) throws OutOfRangeException {
		checkRowIndex(row);
		final int nCols = getColumnDimension();
		final FieldMatrix out = createMatrix(1, nCols);
		for (int i = 0; i < nCols; ++i) {
			out.setEntry(0, i, getEntry(row, i));
		}

		return out;

	}

	/** {@inheritDoc} */
	public void setRowMatrix(final int row, final FieldMatrix matrix) throws OutOfRangeException, MatrixDimensionMismatchException {
		checkRowIndex(row);
		final int nCols = getColumnDimension();
		if ((matrix.getRowDimension() != 1) || (matrix.getColumnDimension() != nCols)) {
			throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), 1, nCols);
		}
		for (int i = 0; i < nCols; ++i) {
			setEntry(row, i, matrix.getEntry(0, i));
		}

	}

	/** {@inheritDoc} */
	public FieldMatrix getColumnMatrix(final int column) throws OutOfRangeException {

		checkColumnIndex(column);
		final int nRows = getRowDimension();
		final FieldMatrix out = createMatrix(nRows, 1);
		for (int i = 0; i < nRows; ++i) {
			out.setEntry(i, 0, getEntry(i, column));
		}

		return out;

	}

	/** {@inheritDoc} */
	public void setColumnMatrix(final int column, final FieldMatrix matrix) throws OutOfRangeException,
			MatrixDimensionMismatchException {
		checkColumnIndex(column);
		final int nRows = getRowDimension();
		if ((matrix.getRowDimension() != nRows) || (matrix.getColumnDimension() != 1)) {
			throw new MatrixDimensionMismatchException(matrix.getRowDimension(), matrix.getColumnDimension(), nRows, 1);
		}
		for (int i = 0; i < nRows; ++i) {
			setEntry(i, column, matrix.getEntry(i, 0));
		}

	}

	/** {@inheritDoc} */
	public FieldVector getRowVector(final int row) throws OutOfRangeException {
		return new ArrayFieldVector(getRow(row), false);
	}

	/** {@inheritDoc} */
	public void setRowVector(final int row, final FieldVector vector) throws OutOfRangeException, MatrixDimensionMismatchException {
		checkRowIndex(row);
		final int nCols = getColumnDimension();
		if (vector.getDimension() != nCols) {
			throw new MatrixDimensionMismatchException(1, vector.getDimension(), 1, nCols);
		}
		for (int i = 0; i < nCols; ++i) {
			setEntry(row, i, vector.getEntry(i));
		}

	}

	/** {@inheritDoc} */
	public FieldVector getColumnVector(final int column) throws OutOfRangeException {
		return new ArrayFieldVector(getColumn(column), false);
	}

	/** {@inheritDoc} */
	public void setColumnVector(final int column, final FieldVector vector) throws OutOfRangeException,
			MatrixDimensionMismatchException {

		checkColumnIndex(column);
		final int nRows = getRowDimension();
		if (vector.getDimension() != nRows) {
			throw new MatrixDimensionMismatchException(vector.getDimension(), 1, nRows, 1);
		}
		for (int i = 0; i < nRows; ++i) {
			setEntry(i, column, vector.getEntry(i));
		}

	}

	/** {@inheritDoc} */
	public IExpr[] getRow(final int row) throws OutOfRangeException {
		checkRowIndex(row);
		final int nCols = getColumnDimension();
		final IExpr[] out = MathArrays.buildArray(nCols);
		for (int i = 0; i < nCols; ++i) {
			out[i] = getEntry(row, i);
		}

		return out;

	}

	/** {@inheritDoc} */
	public void setRow(final int row, final IExpr[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
		checkRowIndex(row);
		final int nCols = getColumnDimension();
		if (array.length != nCols) {
			throw new MatrixDimensionMismatchException(1, array.length, 1, nCols);
		}
		for (int i = 0; i < nCols; ++i) {
			setEntry(row, i, array[i]);
		}

	}

	/** {@inheritDoc} */
	public IExpr[] getColumn(final int column) throws OutOfRangeException {
		checkColumnIndex(column);
		final int nRows = getRowDimension();
		final IExpr[] out = MathArrays.buildArray(nRows);
		for (int i = 0; i < nRows; ++i) {
			out[i] = getEntry(i, column);
		}

		return out;

	}

	/** {@inheritDoc} */
	public void setColumn(final int column, final IExpr[] array) throws OutOfRangeException, MatrixDimensionMismatchException {
		checkColumnIndex(column);
		final int nRows = getRowDimension();
		if (array.length != nRows) {
			throw new MatrixDimensionMismatchException(array.length, 1, nRows, 1);
		}
		for (int i = 0; i < nRows; ++i) {
			setEntry(i, column, array[i]);
		}
	}

	/** {@inheritDoc} */
	public abstract IExpr getEntry(int row, int column) throws OutOfRangeException;

	/** {@inheritDoc} */
	public abstract void setEntry(int row, int column, IExpr value) throws OutOfRangeException;

	/** {@inheritDoc} */
	public abstract void addToEntry(int row, int column, IExpr increment) throws OutOfRangeException;

	/** {@inheritDoc} */
	public abstract void multiplyEntry(int row, int column, IExpr factor) throws OutOfRangeException;

	/** {@inheritDoc} */
	public FieldMatrix transpose() {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		final FieldMatrix out = createMatrix(nCols, nRows);
		walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor(F.C0) {
			/** {@inheritDoc} */
			@Override
			public void visit(final int row, final int column, final IExpr value) {
				out.setEntry(column, row, value);
			}
		});

		return out;
	}

	/** {@inheritDoc} */
	public boolean isSquare() {
		return getColumnDimension() == getRowDimension();
	}

	/** {@inheritDoc} */
	public abstract int getRowDimension();

	/** {@inheritDoc} */
	public abstract int getColumnDimension();

	/** {@inheritDoc} */
	public IExpr getTrace() throws NonSquareMatrixException {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		if (nRows != nCols) {
			throw new NonSquareMatrixException(nRows, nCols);
		}
		IExpr trace = F.C0;
		for (int i = 0; i < nRows; ++i) {
			trace = trace.plus(getEntry(i, i));
		}
		return trace;
	}

	/** {@inheritDoc} */
	public IExpr[] operate(final IExpr[] v) throws DimensionMismatchException {

		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		if (v.length != nCols) {
			throw new DimensionMismatchException(v.length, nCols);
		}

		final IExpr[] out = MathArrays.buildArray(nRows);
		for (int row = 0; row < nRows; ++row) {
			IExpr sum = F.C0;
			for (int i = 0; i < nCols; ++i) {
				sum = sum.plus(getEntry(row, i).times(v[i]));
			}
			out[row] = sum;
		}

		return out;
	}

	/** {@inheritDoc} */
	public FieldVector operate(final FieldVector v) throws DimensionMismatchException {
		try {
			return new ArrayFieldVector(operate(((ArrayFieldVector) v).getDataRef()), false);
		} catch (ClassCastException cce) {
			final int nRows = getRowDimension();
			final int nCols = getColumnDimension();
			if (v.getDimension() != nCols) {
				throw new DimensionMismatchException(v.getDimension(), nCols);
			}

			final IExpr[] out = MathArrays.buildArray(nRows);
			for (int row = 0; row < nRows; ++row) {
				IExpr sum = F.C0;
				for (int i = 0; i < nCols; ++i) {
					sum = sum.plus(getEntry(row, i).times(v.getEntry(i)));
				}
				out[row] = sum;
			}

			return new ArrayFieldVector(out, false);
		}
	}

	/** {@inheritDoc} */
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
				sum = sum.plus(getEntry(i, col).times(v[i]));
			}
			out[col] = sum;
		}

		return out;
	}

	/** {@inheritDoc} */
	public FieldVector preMultiply(final FieldVector v) throws DimensionMismatchException {
		try {
			return new ArrayFieldVector(preMultiply(((ArrayFieldVector) v).getDataRef()), false);
		} catch (ClassCastException cce) {
			final int nRows = getRowDimension();
			final int nCols = getColumnDimension();
			if (v.getDimension() != nRows) {
				throw new DimensionMismatchException(v.getDimension(), nRows);
			}

			final IExpr[] out = MathArrays.buildArray(nCols);
			for (int col = 0; col < nCols; ++col) {
				IExpr sum = F.C0;
				for (int i = 0; i < nRows; ++i) {
					sum = sum.plus(getEntry(i, col).times(v.getEntry(i)));
				}
				out[col] = sum;
			}

			return new ArrayFieldVector(out, false);
		}
	}

	/** {@inheritDoc} */
	public IExpr walkInRowOrder(final FieldMatrixChangingVisitor visitor) {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
		for (int row = 0; row < rows; ++row) {
			for (int column = 0; column < columns; ++column) {
				final IExpr oldValue = getEntry(row, column);
				final IExpr newValue = visitor.visit(row, column, oldValue);
				setEntry(row, column, newValue);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	public IExpr walkInRowOrder(final FieldMatrixPreservingVisitor visitor) {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
		for (int row = 0; row < rows; ++row) {
			for (int column = 0; column < columns; ++column) {
				visitor.visit(row, column, getEntry(row, column));
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	public IExpr walkInRowOrder(final FieldMatrixChangingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int row = startRow; row <= endRow; ++row) {
			for (int column = startColumn; column <= endColumn; ++column) {
				final IExpr oldValue = getEntry(row, column);
				final IExpr newValue = visitor.visit(row, column, oldValue);
				setEntry(row, column, newValue);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	public IExpr walkInRowOrder(final FieldMatrixPreservingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int row = startRow; row <= endRow; ++row) {
			for (int column = startColumn; column <= endColumn; ++column) {
				visitor.visit(row, column, getEntry(row, column));
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	public IExpr walkInColumnOrder(final FieldMatrixChangingVisitor visitor) {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
		for (int column = 0; column < columns; ++column) {
			for (int row = 0; row < rows; ++row) {
				final IExpr oldValue = getEntry(row, column);
				final IExpr newValue = visitor.visit(row, column, oldValue);
				setEntry(row, column, newValue);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	public IExpr walkInColumnOrder(final FieldMatrixPreservingVisitor visitor) {
		final int rows = getRowDimension();
		final int columns = getColumnDimension();
		visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
		for (int column = 0; column < columns; ++column) {
			for (int row = 0; row < rows; ++row) {
				visitor.visit(row, column, getEntry(row, column));
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	public IExpr walkInColumnOrder(final FieldMatrixChangingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int column = startColumn; column <= endColumn; ++column) {
			for (int row = startRow; row <= endRow; ++row) {
				final IExpr oldValue = getEntry(row, column);
				final IExpr newValue = visitor.visit(row, column, oldValue);
				setEntry(row, column, newValue);
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	public IExpr walkInColumnOrder(final FieldMatrixPreservingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
		checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
		visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
		for (int column = startColumn; column <= endColumn; ++column) {
			for (int row = startRow; row <= endRow; ++row) {
				visitor.visit(row, column, getEntry(row, column));
			}
		}
		return visitor.end();
	}

	/** {@inheritDoc} */
	public IExpr walkInOptimizedOrder(final FieldMatrixChangingVisitor visitor) {
		return walkInRowOrder(visitor);
	}

	/** {@inheritDoc} */
	public IExpr walkInOptimizedOrder(final FieldMatrixPreservingVisitor visitor) {
		return walkInRowOrder(visitor);
	}

	/** {@inheritDoc} */
	public IExpr walkInOptimizedOrder(final FieldMatrixChangingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
		return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
	}

	/** {@inheritDoc} */
	public IExpr walkInOptimizedOrder(final FieldMatrixPreservingVisitor visitor, final int startRow, final int endRow,
			final int startColumn, final int endColumn) throws NumberIsTooSmallException, OutOfRangeException {
		return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
	}

	/**
	 * Get a string representation for this matrix.
	 * 
	 * @return a string representation for this matrix
	 */
	@Override
	public String toString() {
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		final StringBuffer res = new StringBuffer();
		String fullClassName = getClass().getName();
		String shortClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
		res.append(shortClassName).append("{");

		for (int i = 0; i < nRows; ++i) {
			if (i > 0) {
				res.append(",");
			}
			res.append("{");
			for (int j = 0; j < nCols; ++j) {
				if (j > 0) {
					res.append(",");
				}
				res.append(getEntry(i, j));
			}
			res.append("}");
		}

		res.append("}");
		return res.toString();
	}

	/**
	 * Returns true iff <code>object</code> is a <code>FieldMatrix</code> instance with the same dimensions as this and all
	 * corresponding matrix entries are equal.
	 *
	 * @param object
	 *            the object to test equality against.
	 * @return true if object equals this
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}
		if (object instanceof FieldMatrix == false) {
			return false;
		}
		FieldMatrix m = (FieldMatrix) object;
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		if (m.getColumnDimension() != nCols || m.getRowDimension() != nRows) {
			return false;
		}
		for (int row = 0; row < nRows; ++row) {
			for (int col = 0; col < nCols; ++col) {
				if (!getEntry(row, col).equals(m.getEntry(row, col))) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Computes a hashcode for the matrix.
	 *
	 * @return hashcode for matrix
	 */
	@Override
	public int hashCode() {
		int ret = 322562;
		final int nRows = getRowDimension();
		final int nCols = getColumnDimension();
		ret = ret * 31 + nRows;
		ret = ret * 31 + nCols;
		for (int row = 0; row < nRows; ++row) {
			for (int col = 0; col < nCols; ++col) {
				ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * getEntry(row, col).hashCode();
			}
		}
		return ret;
	}

	/**
	 * Check if a row index is valid.
	 *
	 * @param row
	 *            Row index to check.
	 * @throws OutOfRangeException
	 *             if {@code index} is not valid.
	 */
	protected void checkRowIndex(final int row) throws OutOfRangeException {
		if (row < 0 || row >= getRowDimension()) {
			throw new OutOfRangeException(LocalizedFormats.ROW_INDEX, row, 0, getRowDimension() - 1);
		}
	}

	/**
	 * Check if a column index is valid.
	 *
	 * @param column
	 *            Column index to check.
	 * @throws OutOfRangeException
	 *             if {@code index} is not valid.
	 */
	protected void checkColumnIndex(final int column) throws OutOfRangeException {
		if (column < 0 || column >= getColumnDimension()) {
			throw new OutOfRangeException(LocalizedFormats.COLUMN_INDEX, column, 0, getColumnDimension() - 1);
		}
	}

	/**
	 * Check if submatrix ranges indices are valid. Rows and columns are indicated counting from 0 to n-1.
	 *
	 * @param startRow
	 *            Initial row index.
	 * @param endRow
	 *            Final row index.
	 * @param startColumn
	 *            Initial column index.
	 * @param endColumn
	 *            Final column index.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @throws NumberIsTooSmallException
	 *             if {@code endRow < startRow} or {@code endColumn < startColumn}.
	 */
	protected void checkSubMatrixIndex(final int startRow, final int endRow, final int startColumn, final int endColumn)
			throws NumberIsTooSmallException, OutOfRangeException {
		checkRowIndex(startRow);
		checkRowIndex(endRow);
		if (endRow < startRow) {
			throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, endRow, startRow, true);
		}

		checkColumnIndex(startColumn);
		checkColumnIndex(endColumn);
		if (endColumn < startColumn) {
			throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN, endColumn, startColumn, true);
		}
	}

	/**
	 * Check if submatrix ranges indices are valid. Rows and columns are indicated counting from 0 to n-1.
	 *
	 * @param selectedRows
	 *            Array of row indices.
	 * @param selectedColumns
	 *            Array of column indices.
	 * @throws NullArgumentException
	 *             if the arrays are {@code null}.
	 * @throws NoDataException
	 *             if the arrays have zero length.
	 * @throws OutOfRangeException
	 *             if row or column selections are not valid.
	 */
	protected void checkSubMatrixIndex(final int[] selectedRows, final int[] selectedColumns) throws NoDataException,
			NullArgumentException, OutOfRangeException {
		if (selectedRows == null || selectedColumns == null) {
			throw new NullArgumentException();
		}
		if (selectedRows.length == 0 || selectedColumns.length == 0) {
			throw new NoDataException();
		}

		for (final int row : selectedRows) {
			checkRowIndex(row);
		}
		for (final int column : selectedColumns) {
			checkColumnIndex(column);
		}
	}

	/**
	 * Check if a matrix is addition compatible with the instance.
	 *
	 * @param m
	 *            Matrix to check.
	 * @throws MatrixDimensionMismatchException
	 *             if the matrix is not addition-compatible with instance.
	 */
	protected void checkAdditionCompatible(final FieldMatrix m) throws MatrixDimensionMismatchException {
		if ((getRowDimension() != m.getRowDimension()) || (getColumnDimension() != m.getColumnDimension())) {
			throw new MatrixDimensionMismatchException(m.getRowDimension(), m.getColumnDimension(), getRowDimension(),
					getColumnDimension());
		}
	}

	/**
	 * Check if a matrix is subtraction compatible with the instance.
	 *
	 * @param m
	 *            Matrix to check.
	 * @throws MatrixDimensionMismatchException
	 *             if the matrix is not subtraction-compatible with instance.
	 */
	protected void checkSubtractionCompatible(final FieldMatrix m) throws MatrixDimensionMismatchException {
		if ((getRowDimension() != m.getRowDimension()) || (getColumnDimension() != m.getColumnDimension())) {
			throw new MatrixDimensionMismatchException(m.getRowDimension(), m.getColumnDimension(), getRowDimension(),
					getColumnDimension());
		}
	}

	/**
	 * Check if a matrix is multiplication compatible with the instance.
	 *
	 * @param m
	 *            Matrix to check.
	 * @throws DimensionMismatchException
	 *             if the matrix is not multiplication-compatible with instance.
	 */
	protected void checkMultiplicationCompatible(final FieldMatrix m) throws DimensionMismatchException {
		if (getColumnDimension() != m.getRowDimension()) {
			throw new DimensionMismatchException(m.getRowDimension(), getColumnDimension());
		}
	}
}
