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

import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.NoDataException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NotStrictlyPositiveException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.linear.MatrixDimensionMismatchException;
import org.apache.commons.math4.linear.NonSquareMatrixException;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Interface defining field-valued matrix with basic algebraic operations.
 * <p>
 * Matrix element indexing is 0-based -- e.g., <code>getEntry(0, 0)</code> returns the element in the first row, first column of the
 * matrix.
 * </p>
 *
 * @param <T>
 *            the type of the field elements
 */
public interface FieldMatrix extends AnyMatrix {

	/**
	 * Create a new FieldMatrix of the same type as the instance with the supplied row and column dimensions.
	 *
	 * @param rowDimension
	 *            the number of rows in the new matrix
	 * @param columnDimension
	 *            the number of columns in the new matrix
	 * @return a new matrix of the same type as the instance
	 * @throws NotStrictlyPositiveException
	 *             if row or column dimension is not positive.
	 * @since 2.0
	 */
	FieldMatrix createMatrix(final int rowDimension, final int columnDimension) throws NotStrictlyPositiveException;

	/**
	 * Make a (deep) copy of this.
	 *
	 * @return a copy of this matrix.
	 */
	FieldMatrix copy();

	/**
	 * Compute the sum of this and m.
	 *
	 * @param m
	 *            Matrix to be added.
	 * @return {@code this} + {@code m}.
	 * @throws MatrixDimensionMismatchException
	 *             if {@code m} is not the same size as {@code this} matrix.
	 */
	FieldMatrix add(FieldMatrix m) throws MatrixDimensionMismatchException;

	/**
	 * Subtract {@code m} from this matrix.
	 *
	 * @param m
	 *            Matrix to be subtracted.
	 * @return {@code this} - {@code m}.
	 * @throws MatrixDimensionMismatchException
	 *             if {@code m} is not the same size as {@code this} matrix.
	 */
	FieldMatrix subtract(FieldMatrix m) throws MatrixDimensionMismatchException;

	/**
	 * Increment each entry of this matrix.
	 *
	 * @param d
	 *            Value to be added to each entry.
	 * @return {@code d} + {@code this}.
	 */
	FieldMatrix scalarAdd(IExpr d);

	/**
	 * Multiply each entry by {@code d}.
	 *
	 * @param d
	 *            Value to multiply all entries by.
	 * @return {@code d} * {@code this}.
	 */
	FieldMatrix scalarMultiply(IExpr d);

	/**
	 * Postmultiply this matrix by {@code m}.
	 *
	 * @param m
	 *            Matrix to postmultiply by.
	 * @return {@code this} * {@code m}.
	 * @throws DimensionMismatchException
	 *             if the number of columns of {@code this} matrix is not equal to the number of rows of matrix {@code m}.
	 */
	FieldMatrix multiply(FieldMatrix m) throws DimensionMismatchException;

	/**
	 * Premultiply this matrix by {@code m}.
	 *
	 * @param m
	 *            Matrix to premultiply by.
	 * @return {@code m} * {@code this}.
	 * @throws DimensionMismatchException
	 *             if the number of columns of {@code m} differs from the number of rows of {@code this} matrix.
	 */
	FieldMatrix preMultiply(FieldMatrix m) throws DimensionMismatchException;

	/**
	 * Returns the result multiplying this with itself <code>p</code> times. Depending on the type of the field elements, T,
	 * instability for high powers might occur.
	 *
	 * @param p
	 *            raise this to power p
	 * @return this^p
	 * @throws NotPositiveException
	 *             if {@code p < 0}
	 * @throws NonSquareMatrixException
	 *             if {@code this matrix} is not square
	 */
	FieldMatrix power(final int p) throws NonSquareMatrixException, NotPositiveException;

	/**
	 * Returns matrix entries as a two-dimensional array.
	 *
	 * @return a 2-dimensional array of entries.
	 */
	IExpr[][] getData();

	/**
	 * Get a submatrix. Rows and columns are indicated counting from 0 to n - 1.
	 *
	 * @param startRow
	 *            Initial row index
	 * @param endRow
	 *            Final row index (inclusive)
	 * @param startColumn
	 *            Initial column index
	 * @param endColumn
	 *            Final column index (inclusive)
	 * @return the matrix containing the data of the specified rows and columns.
	 * @throws NumberIsTooSmallException
	 *             is {@code endRow < startRow} of {@code endColumn < startColumn}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 */
	FieldMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn) throws NumberIsTooSmallException,
			OutOfRangeException;

	/**
	 * Get a submatrix. Rows and columns are indicated counting from 0 to n - 1.
	 *
	 * @param selectedRows
	 *            Array of row indices.
	 * @param selectedColumns
	 *            Array of column indices.
	 * @return the matrix containing the data in the specified rows and columns.
	 * @throws NoDataException
	 *             if {@code selectedRows} or {@code selectedColumns} is empty
	 * @throws NullArgumentException
	 *             if {@code selectedRows} or {@code selectedColumns} is {@code null}.
	 * @throws OutOfRangeException
	 *             if row or column selections are not valid.
	 */
	FieldMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns) throws NoDataException, NullArgumentException,
			OutOfRangeException;

	/**
	 * Copy a submatrix. Rows and columns are indicated counting from 0 to n-1.
	 *
	 * @param startRow
	 *            Initial row index.
	 * @param endRow
	 *            Final row index (inclusive).
	 * @param startColumn
	 *            Initial column index.
	 * @param endColumn
	 *            Final column index (inclusive).
	 * @param destination
	 *            The arrays where the submatrix data should be copied (if larger than rows/columns counts, only the upper-left part
	 *            will be used).
	 * @throws MatrixDimensionMismatchException
	 *             if the dimensions of {@code destination} do not match those of {@code this}.
	 * @throws NumberIsTooSmallException
	 *             is {@code endRow < startRow} of {@code endColumn < startColumn}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @exception IllegalArgumentException
	 *                if the destination array is too small.
	 */
	void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn, IExpr[][] destination)
			throws MatrixDimensionMismatchException, NumberIsTooSmallException, OutOfRangeException;

	/**
	 * Copy a submatrix. Rows and columns are indicated counting from 0 to n - 1.
	 *
	 * @param selectedRows
	 *            Array of row indices.
	 * @param selectedColumns
	 *            Array of column indices.
	 * @param destination
	 *            Arrays where the submatrix data should be copied (if larger than rows/columns counts, only the upper-left part
	 *            will be used)
	 * @throws MatrixDimensionMismatchException
	 *             if the dimensions of {@code destination} do not match those of {@code this}.
	 * @throws NoDataException
	 *             if {@code selectedRows} or {@code selectedColumns} is empty
	 * @throws NullArgumentException
	 *             if {@code selectedRows} or {@code selectedColumns} is {@code null}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 */
	void copySubMatrix(int[] selectedRows, int[] selectedColumns, IExpr[][] destination) throws MatrixDimensionMismatchException,
			NoDataException, NullArgumentException, OutOfRangeException;

	/**
	 * Replace the submatrix starting at {@code (row, column)} using data in the input {@code subMatrix} array. Indexes are 0-based.
	 * <p>
	 * Example:<br>
	 * Starting with
	 *
	 * <pre>
	 * 1  2  3  4
	 * 5  6  7  8
	 * 9  0  1  2
	 * </pre>
	 *
	 * and <code>subMatrix = {{3, 4} {5,6}}</code>, invoking <code>setSubMatrix(subMatrix,1,1))</code> will result in
	 *
	 * <pre>
	 * 1  2  3  4
	 * 5  3  4  8
	 * 9  5  6  2
	 * </pre>
	 *
	 * </p>
	 *
	 * @param subMatrix
	 *            Array containing the submatrix replacement data.
	 * @param row
	 *            Row coordinate of the top-left element to be replaced.
	 * @param column
	 *            Column coordinate of the top-left element to be replaced.
	 * @throws OutOfRangeException
	 *             if {@code subMatrix} does not fit into this matrix from element in {@code (row, column)}.
	 * @throws NoDataException
	 *             if a row or column of {@code subMatrix} is empty.
	 * @throws DimensionMismatchException
	 *             if {@code subMatrix} is not rectangular (not all rows have the same length).
	 * @throws NullArgumentException
	 *             if {@code subMatrix} is {@code null}.
	 * @since 2.0
	 */
	void setSubMatrix(IExpr[][] subMatrix, int row, int column) throws DimensionMismatchException, OutOfRangeException,
			NoDataException, NullArgumentException;

	/**
	 * Get the entries in row number {@code row} as a row matrix.
	 *
	 * @param row
	 *            Row to be fetched.
	 * @return a row matrix.
	 * @throws OutOfRangeException
	 *             if the specified row index is invalid.
	 */
	FieldMatrix getRowMatrix(int row) throws OutOfRangeException;

	/**
	 * Set the entries in row number {@code row} as a row matrix.
	 *
	 * @param row
	 *            Row to be set.
	 * @param matrix
	 *            Row matrix (must have one row and the same number of columns as the instance).
	 * @throws OutOfRangeException
	 *             if the specified row index is invalid.
	 * @throws MatrixDimensionMismatchException
	 *             if the matrix dimensions do not match one instance row.
	 */
	void setRowMatrix(int row, FieldMatrix matrix) throws MatrixDimensionMismatchException, OutOfRangeException;

	/**
	 * Get the entries in column number {@code column} as a column matrix.
	 *
	 * @param column
	 *            Column to be fetched.
	 * @return a column matrix.
	 * @throws OutOfRangeException
	 *             if the specified column index is invalid.
	 */
	FieldMatrix getColumnMatrix(int column) throws OutOfRangeException;

	/**
	 * Set the entries in column number {@code column} as a column matrix.
	 *
	 * @param column
	 *            Column to be set.
	 * @param matrix
	 *            column matrix (must have one column and the same number of rows as the instance).
	 * @throws OutOfRangeException
	 *             if the specified column index is invalid.
	 * @throws MatrixDimensionMismatchException
	 *             if the matrix dimensions do not match one instance column.
	 */
	void setColumnMatrix(int column, FieldMatrix matrix) throws MatrixDimensionMismatchException, OutOfRangeException;

	/**
	 * Get the entries in row number {@code row} as a vector.
	 *
	 * @param row
	 *            Row to be fetched
	 * @return a row vector.
	 * @throws OutOfRangeException
	 *             if the specified row index is invalid.
	 */
	FieldVector getRowVector(int row) throws OutOfRangeException;

	/**
	 * Set the entries in row number {@code row} as a vector.
	 *
	 * @param row
	 *            Row to be set.
	 * @param vector
	 *            row vector (must have the same number of columns as the instance).
	 * @throws OutOfRangeException
	 *             if the specified row index is invalid.
	 * @throws MatrixDimensionMismatchException
	 *             if the vector dimension does not match one instance row.
	 */
	void setRowVector(int row, FieldVector vector) throws MatrixDimensionMismatchException, OutOfRangeException;

	/**
	 * Returns the entries in column number {@code column} as a vector.
	 *
	 * @param column
	 *            Column to be fetched.
	 * @return a column vector.
	 * @throws OutOfRangeException
	 *             if the specified column index is invalid.
	 */
	FieldVector getColumnVector(int column) throws OutOfRangeException;

	/**
	 * Set the entries in column number {@code column} as a vector.
	 *
	 * @param column
	 *            Column to be set.
	 * @param vector
	 *            Column vector (must have the same number of rows as the instance).
	 * @throws OutOfRangeException
	 *             if the specified column index is invalid.
	 * @throws MatrixDimensionMismatchException
	 *             if the vector dimension does not match one instance column.
	 */
	void setColumnVector(int column, FieldVector vector) throws MatrixDimensionMismatchException, OutOfRangeException;

	/**
	 * Get the entries in row number {@code row} as an array.
	 *
	 * @param row
	 *            Row to be fetched.
	 * @return array of entries in the row.
	 * @throws OutOfRangeException
	 *             if the specified row index is not valid.
	 */
	IExpr[] getRow(int row) throws OutOfRangeException;

	/**
	 * Set the entries in row number {@code row} as a row matrix.
	 *
	 * @param row
	 *            Row to be set.
	 * @param array
	 *            Row matrix (must have the same number of columns as the instance).
	 * @throws OutOfRangeException
	 *             if the specified row index is invalid.
	 * @throws MatrixDimensionMismatchException
	 *             if the array size does not match one instance row.
	 */
	void setRow(int row, IExpr[] array) throws MatrixDimensionMismatchException, OutOfRangeException;

	/**
	 * Get the entries in column number {@code col} as an array.
	 *
	 * @param column
	 *            the column to be fetched
	 * @return array of entries in the column
	 * @throws OutOfRangeException
	 *             if the specified column index is not valid.
	 */
	IExpr[] getColumn(int column) throws OutOfRangeException;

	/**
	 * Set the entries in column number {@code column} as a column matrix.
	 *
	 * @param column
	 *            the column to be set
	 * @param array
	 *            column array (must have the same number of rows as the instance)
	 * @throws OutOfRangeException
	 *             if the specified column index is invalid.
	 * @throws MatrixDimensionMismatchException
	 *             if the array size does not match one instance column.
	 */
	void setColumn(int column, IExpr[] array) throws MatrixDimensionMismatchException, OutOfRangeException;

	/**
	 * Returns the entry in the specified row and column.
	 *
	 * @param row
	 *            row location of entry to be fetched
	 * @param column
	 *            column location of entry to be fetched
	 * @return matrix entry in row,column
	 * @throws OutOfRangeException
	 *             if the row or column index is not valid.
	 */
	IExpr getEntry(int row, int column) throws OutOfRangeException;

	/**
	 * Set the entry in the specified row and column.
	 *
	 * @param row
	 *            row location of entry to be set
	 * @param column
	 *            column location of entry to be set
	 * @param value
	 *            matrix entry to be set in row,column
	 * @throws OutOfRangeException
	 *             if the row or column index is not valid.
	 * @since 2.0
	 */
	void setEntry(int row, int column, IExpr value) throws OutOfRangeException;

	/**
	 * Change an entry in the specified row and column.
	 *
	 * @param row
	 *            Row location of entry to be set.
	 * @param column
	 *            Column location of entry to be set.
	 * @param increment
	 *            Value to add to the current matrix entry in {@code (row, column)}.
	 * @throws OutOfRangeException
	 *             if the row or column index is not valid.
	 * @since 2.0
	 */
	void addToEntry(int row, int column, IExpr increment) throws OutOfRangeException;

	/**
	 * Change an entry in the specified row and column.
	 *
	 * @param row
	 *            Row location of entry to be set.
	 * @param column
	 *            Column location of entry to be set.
	 * @param factor
	 *            Multiplication factor for the current matrix entry in {@code (row,column)}
	 * @throws OutOfRangeException
	 *             if the row or column index is not valid.
	 * @since 2.0
	 */
	void multiplyEntry(int row, int column, IExpr factor) throws OutOfRangeException;

	/**
	 * Returns the transpose of this matrix.
	 *
	 * @return transpose matrix
	 */
	FieldMatrix transpose();

	/**
	 * Returns the <a href="http://mathworld.wolfram.com/MatrixTrace.html"> trace</a> of the matrix (the sum of the elements on the
	 * main diagonal).
	 *
	 * @return trace
	 * @throws NonSquareMatrixException
	 *             if the matrix is not square.
	 */
	IExpr getTrace() throws NonSquareMatrixException;

	/**
	 * Returns the result of multiplying this by the vector {@code v}.
	 *
	 * @param v
	 *            the vector to operate on
	 * @return {@code this * v}
	 * @throws DimensionMismatchException
	 *             if the number of columns of {@code this} matrix is not equal to the size of the vector {@code v}.
	 */
	IExpr[] operate(IExpr[] v) throws DimensionMismatchException;

	/**
	 * Returns the result of multiplying this by the vector {@code v}.
	 *
	 * @param v
	 *            the vector to operate on
	 * @return {@code this * v}
	 * @throws DimensionMismatchException
	 *             if the number of columns of {@code this} matrix is not equal to the size of the vector {@code v}.
	 */
	FieldVector operate(FieldVector v) throws DimensionMismatchException;

	/**
	 * Returns the (row) vector result of premultiplying this by the vector {@code v}.
	 *
	 * @param v
	 *            the row vector to premultiply by
	 * @return {@code v * this}
	 * @throws DimensionMismatchException
	 *             if the number of rows of {@code this} matrix is not equal to the size of the vector {@code v}
	 */
	IExpr[] preMultiply(IExpr[] v) throws DimensionMismatchException;

	/**
	 * Returns the (row) vector result of premultiplying this by the vector {@code v}.
	 *
	 * @param v
	 *            the row vector to premultiply by
	 * @return {@code v * this}
	 * @throws DimensionMismatchException
	 *             if the number of rows of {@code this} matrix is not equal to the size of the vector {@code v}
	 */
	FieldVector preMultiply(FieldVector v) throws DimensionMismatchException;

	/**
	 * Visit (and possibly change) all matrix entries in row order.
	 * <p>
	 * Row order starts at upper left and iterating through all elements of a row from left to right before going to the leftmost
	 * element of the next row.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end of the walk
	 */
	IExpr walkInRowOrder(FieldMatrixChangingVisitor visitor);

	/**
	 * Visit (but don't change) all matrix entries in row order.
	 * <p>
	 * Row order starts at upper left and iterating through all elements of a row from left to right before going to the leftmost
	 * element of the next row.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end of the walk
	 */
	IExpr walkInRowOrder(FieldMatrixPreservingVisitor visitor);

	/**
	 * Visit (and possibly change) some matrix entries in row order.
	 * <p>
	 * Row order starts at upper left and iterating through all elements of a row from left to right before going to the leftmost
	 * element of the next row.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @param startRow
	 *            Initial row index
	 * @param endRow
	 *            Final row index (inclusive)
	 * @param startColumn
	 *            Initial column index
	 * @param endColumn
	 *            Final column index
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @throws NumberIsTooSmallException
	 *             if {@code endRow < startRow} or {@code endColumn < startColumn}.
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end of the walk
	 */
	IExpr walkInRowOrder(FieldMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
			throws OutOfRangeException, NumberIsTooSmallException;

	/**
	 * Visit (but don't change) some matrix entries in row order.
	 * <p>
	 * Row order starts at upper left and iterating through all elements of a row from left to right before going to the leftmost
	 * element of the next row.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @param startRow
	 *            Initial row index
	 * @param endRow
	 *            Final row index (inclusive)
	 * @param startColumn
	 *            Initial column index
	 * @param endColumn
	 *            Final column index
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @throws NumberIsTooSmallException
	 *             if {@code endRow < startRow} or {@code endColumn < startColumn}.
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end of the walk
	 */
	IExpr walkInRowOrder(FieldMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
			throws OutOfRangeException, NumberIsTooSmallException;

	/**
	 * Visit (and possibly change) all matrix entries in column order.
	 * <p>
	 * Column order starts at upper left and iterating through all elements of a column from top to bottom before going to the
	 * topmost element of the next column.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end of the walk
	 */
	IExpr walkInColumnOrder(FieldMatrixChangingVisitor visitor);

	/**
	 * Visit (but don't change) all matrix entries in column order.
	 * <p>
	 * Column order starts at upper left and iterating through all elements of a column from top to bottom before going to the
	 * topmost element of the next column.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end of the walk
	 */
	IExpr walkInColumnOrder(FieldMatrixPreservingVisitor visitor);

	/**
	 * Visit (and possibly change) some matrix entries in column order.
	 * <p>
	 * Column order starts at upper left and iterating through all elements of a column from top to bottom before going to the
	 * topmost element of the next column.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @param startRow
	 *            Initial row index
	 * @param endRow
	 *            Final row index (inclusive)
	 * @param startColumn
	 *            Initial column index
	 * @param endColumn
	 *            Final column index
	 * @throws NumberIsTooSmallException
	 *             if {@code endRow < startRow} or {@code endColumn < startColumn}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end of the walk
	 */
	IExpr walkInColumnOrder(FieldMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
			throws NumberIsTooSmallException, OutOfRangeException;

	/**
	 * Visit (but don't change) some matrix entries in column order.
	 * <p>
	 * Column order starts at upper left and iterating through all elements of a column from top to bottom before going to the
	 * topmost element of the next column.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @param startRow
	 *            Initial row index
	 * @param endRow
	 *            Final row index (inclusive)
	 * @param startColumn
	 *            Initial column index
	 * @param endColumn
	 *            Final column index
	 * @throws NumberIsTooSmallException
	 *             if {@code endRow < startRow} or {@code endColumn < startColumn}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end of the walk
	 */
	IExpr walkInColumnOrder(FieldMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
			throws NumberIsTooSmallException, OutOfRangeException;

	/**
	 * Visit (and possibly change) all matrix entries using the fastest possible order.
	 * <p>
	 * The fastest walking order depends on the exact matrix class. It may be different from traditional row or column orders.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end of the walk
	 */
	IExpr walkInOptimizedOrder(FieldMatrixChangingVisitor visitor);

	/**
	 * Visit (but don't change) all matrix entries using the fastest possible order.
	 * <p>
	 * The fastest walking order depends on the exact matrix class. It may be different from traditional row or column orders.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end of the walk
	 */
	IExpr walkInOptimizedOrder(FieldMatrixPreservingVisitor visitor);

	/**
	 * Visit (and possibly change) some matrix entries using the fastest possible order.
	 * <p>
	 * The fastest walking order depends on the exact matrix class. It may be different from traditional row or column orders.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @param startRow
	 *            Initial row index
	 * @param endRow
	 *            Final row index (inclusive)
	 * @param startColumn
	 *            Initial column index
	 * @param endColumn
	 *            Final column index (inclusive)
	 * @throws NumberIsTooSmallException
	 *             if {@code endRow < startRow} or {@code endColumn < startColumn}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixChangingVisitor#end()} at the end of the walk
	 */
	IExpr walkInOptimizedOrder(FieldMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
			throws NumberIsTooSmallException, OutOfRangeException;

	/**
	 * Visit (but don't change) some matrix entries using the fastest possible order.
	 * <p>
	 * The fastest walking order depends on the exact matrix class. It may be different from traditional row or column orders.
	 * </p>
	 * 
	 * @param visitor
	 *            visitor used to process all matrix entries
	 * @param startRow
	 *            Initial row index
	 * @param endRow
	 *            Final row index (inclusive)
	 * @param startColumn
	 *            Initial column index
	 * @param endColumn
	 *            Final column index (inclusive)
	 * @throws NumberIsTooSmallException
	 *             if {@code endRow < startRow} or {@code endColumn < startColumn}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInRowOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInRowOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInColumnOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @see #walkInColumnOrder(FieldMatrixPreservingVisitor, int, int, int, int)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixPreservingVisitor)
	 * @see #walkInOptimizedOrder(FieldMatrixChangingVisitor, int, int, int, int)
	 * @return the value returned by {@link FieldMatrixPreservingVisitor#end()} at the end of the walk
	 */
	IExpr walkInOptimizedOrder(FieldMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
			throws NumberIsTooSmallException, OutOfRangeException;
}
