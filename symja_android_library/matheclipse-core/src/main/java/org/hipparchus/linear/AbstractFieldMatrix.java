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

package org.hipparchus.linear;

import org.hipparchus.Field;
import org.hipparchus.FieldElement;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.util.MathArrays;

import java.util.ArrayList;

/**
 * Basic implementation of {@link FieldMatrix} methods regardless of the underlying storage.
 * <p>All the methods implemented here use {@link #getEntry(int, int)} to access
 * matrix elements. Derived class can provide faster implementations. </p>
 *
 * @param <T> Type of the field elements.
 */
public abstract class AbstractFieldMatrix<T extends FieldElement<T>>
        implements FieldMatrix<T> {
    /**
     * Field to which the elements belong.
     */
    private final Field<T> field;

    /**
     * Constructor for use with Serializable
     */
    protected AbstractFieldMatrix() {
        field = null;
    }

    /**
     * Creates a matrix with no data
     *
     * @param field field to which the elements belong
     */
    protected AbstractFieldMatrix(final Field<T> field) {
        this.field = field;
    }

    /**
     * Create a new FieldMatrix<T> with the supplied row and column dimensions.
     *
     * @param field           Field to which the elements belong.
     * @param rowDimension    Number of rows in the new matrix.
     * @param columnDimension Number of columns in the new matrix.
     * @throws MathIllegalArgumentException if row or column dimension is not
     *                                      positive.
     */
    protected AbstractFieldMatrix(final Field<T> field,
                                  final int rowDimension,
                                  final int columnDimension)
            throws MathIllegalArgumentException {
        if (rowDimension <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSION,
                    rowDimension);
        }
        if (columnDimension <= 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSION,
                    columnDimension);
        }
        this.field = field;
    }

    /**
     * Get the elements type from an array.
     *
     * @param <T> Type of the field elements.
     * @param d   Data array.
     * @return the field to which the array elements belong.
     * @throws NullArgumentException        if the array is {@code null}.
     * @throws MathIllegalArgumentException if the array is empty.
     */
    protected static <T extends FieldElement<T>> Field<T> extractField(final T[][] d)
            throws MathIllegalArgumentException, NullArgumentException {
        if (d == null) {
            throw new NullArgumentException();
        }
        if (d.length == 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.AT_LEAST_ONE_ROW);
        }
        if (d[0].length == 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.AT_LEAST_ONE_COLUMN);
        }
        return d[0][0].getField();
    }

    /**
     * Get the elements type from an array.
     *
     * @param <T> Type of the field elements.
     * @param d   Data array.
     * @return the field to which the array elements belong.
     * @throws MathIllegalArgumentException if array is empty.
     */
    protected static <T extends FieldElement<T>> Field<T> extractField(final T[] d)
            throws MathIllegalArgumentException {
        if (d.length == 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.AT_LEAST_ONE_ROW);
        }
        return d[0].getField();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<T> getField() {
        return field;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract FieldMatrix<T> createMatrix(final int rowDimension,
                                                final int columnDimension)
            throws MathIllegalArgumentException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract FieldMatrix<T> copy();

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> add(FieldMatrix<T> m)
            throws MathIllegalArgumentException {
        // safety check
        checkAdditionCompatible(m);

        final int rowCount = getRowDimension();
        final int columnCount = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col).add(m.getEntry(row, col)));
            }
        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> subtract(final FieldMatrix<T> m)
            throws MathIllegalArgumentException {
        // safety check
        checkSubtractionCompatible(m);

        final int rowCount = getRowDimension();
        final int columnCount = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col).subtract(m.getEntry(row, col)));
            }
        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> scalarAdd(final T d) {

        final int rowCount = getRowDimension();
        final int columnCount = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col).add(d));
            }
        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> scalarMultiply(final T d) {
        final int rowCount = getRowDimension();
        final int columnCount = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(rowCount, columnCount);
        for (int row = 0; row < rowCount; ++row) {
            for (int col = 0; col < columnCount; ++col) {
                out.setEntry(row, col, getEntry(row, col).multiply(d));
            }
        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> multiply(final FieldMatrix<T> m)
            throws MathIllegalArgumentException {
        // safety check
        checkMultiplicationCompatible(m);

        final int nRows = getRowDimension();
        final int nCols = m.getColumnDimension();
        final int nSum = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(nRows, nCols);
        for (int row = 0; row < nRows; ++row) {
            for (int col = 0; col < nCols; ++col) {
                T sum = field.getZero();
                for (int i = 0; i < nSum; ++i) {
                    sum = sum.add(getEntry(row, i).multiply(m.getEntry(i, col)));
                }
                out.setEntry(row, col, sum);
            }
        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> preMultiply(final FieldMatrix<T> m)
            throws MathIllegalArgumentException {
        return m.multiply(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> power(final int p) throws MathIllegalArgumentException {
        if (p < 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_TOO_SMALL, p, 0);
        }

        if (!isSquare()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NON_SQUARE_MATRIX,
                    getRowDimension(), getColumnDimension());
        }

        if (p == 0) {
            return MatrixUtils.createFieldIdentityMatrix(this.getField(), this.getRowDimension());
        }

        if (p == 1) {
            return this.copy();
        }

        final int power = p - 1;

        /*
         * Only log_2(p) operations is used by doing as follows:
         * 5^214 = 5^128 * 5^64 * 5^16 * 5^4 * 5^2
         *
         * In general, the same approach is used for A^p.
         */

        final char[] binaryRepresentation = Integer.toBinaryString(power)
                .toCharArray();
        final ArrayList<Integer> nonZeroPositions = new ArrayList<Integer>();

        for (int i = 0; i < binaryRepresentation.length; ++i) {
            if (binaryRepresentation[i] == '1') {
                final int pos = binaryRepresentation.length - i - 1;
                nonZeroPositions.add(pos);
            }
        }

        ArrayList<FieldMatrix<T>> results = new ArrayList<FieldMatrix<T>>(
                binaryRepresentation.length);

        results.add(0, this.copy());

        for (int i = 1; i < binaryRepresentation.length; ++i) {
            final FieldMatrix<T> s = results.get(i - 1);
            final FieldMatrix<T> r = s.multiply(s);
            results.add(i, r);
        }

        FieldMatrix<T> result = this.copy();

        for (Integer i : nonZeroPositions) {
            result = result.multiply(results.get(i));
        }

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T[][] getData() {
        final T[][] data = MathArrays.buildArray(field, getRowDimension(), getColumnDimension());

        for (int i = 0; i < data.length; ++i) {
            final T[] dataI = data[i];
            for (int j = 0; j < dataI.length; ++j) {
                dataI[j] = getEntry(i, j);
            }
        }

        return data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> getSubMatrix(final int startRow, final int endRow,
                                       final int startColumn, final int endColumn)
            throws MathIllegalArgumentException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);

        final FieldMatrix<T> subMatrix =
                createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
        for (int i = startRow; i <= endRow; ++i) {
            for (int j = startColumn; j <= endColumn; ++j) {
                subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
            }
        }

        return subMatrix;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> getSubMatrix(final int[] selectedRows,
                                       final int[] selectedColumns)
            throws MathIllegalArgumentException, NullArgumentException {

        // safety checks
        checkSubMatrixIndex(selectedRows, selectedColumns);

        // copy entries
        final FieldMatrix<T> subMatrix =
                createMatrix(selectedRows.length, selectedColumns.length);
        subMatrix.walkInOptimizedOrder(new DefaultFieldMatrixChangingVisitor<T>(field.getZero()) {

            /** {@inheritDoc} */
            @Override
            public T visit(final int row, final int column, final T value) {
                return getEntry(selectedRows[row], selectedColumns[column]);
            }

        });

        return subMatrix;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copySubMatrix(final int startRow, final int endRow,
                              final int startColumn, final int endColumn,
                              final T[][] destination)
            throws MathIllegalArgumentException {
        // safety checks
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        final int rowsCount = endRow + 1 - startRow;
        final int columnsCount = endColumn + 1 - startColumn;
        if ((destination.length < rowsCount) || (destination[0].length < columnsCount)) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    destination.length, destination[0].length,
                    rowsCount, columnsCount);
        }

        // copy entries
        walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<T>(field.getZero()) {

            /** Initial row index. */
            private int startRow;

            /** Initial column index. */
            private int startColumn;

            /** {@inheritDoc} */
            @Override
            public void start(final int rows, final int columns,
                              final int startRow, final int endRow,
                              final int startColumn, final int endColumn) {
                this.startRow = startRow;
                this.startColumn = startColumn;
            }

            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column, final T value) {
                destination[row - startRow][column - startColumn] = value;
            }

        }, startRow, endRow, startColumn, endColumn);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void copySubMatrix(int[] selectedRows, int[] selectedColumns, T[][] destination)
            throws MathIllegalArgumentException, NullArgumentException {
        // safety checks
        checkSubMatrixIndex(selectedRows, selectedColumns);
        if ((destination.length < selectedRows.length) ||
                (destination[0].length < selectedColumns.length)) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    destination.length, destination[0].length,
                    selectedRows.length, selectedColumns.length);
        }

        // copy entries
        for (int i = 0; i < selectedRows.length; i++) {
            final T[] destinationI = destination[i];
            for (int j = 0; j < selectedColumns.length; j++) {
                destinationI[j] = getEntry(selectedRows[i], selectedColumns[j]);
            }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSubMatrix(final T[][] subMatrix, final int row,
                             final int column)
            throws MathIllegalArgumentException, NullArgumentException {
        if (subMatrix == null) {
            throw new NullArgumentException();
        }
        final int nRows = subMatrix.length;
        if (nRows == 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.AT_LEAST_ONE_ROW);
        }

        final int nCols = subMatrix[0].length;
        if (nCols == 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.AT_LEAST_ONE_COLUMN);
        }

        for (int r = 1; r < nRows; ++r) {
            if (subMatrix[r].length != nCols) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                        nCols, subMatrix[r].length);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> getRowMatrix(final int row) throws MathIllegalArgumentException {
        checkRowIndex(row);
        final int nCols = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(1, nCols);
        for (int i = 0; i < nCols; ++i) {
            out.setEntry(0, i, getEntry(row, i));
        }

        return out;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowMatrix(final int row, final FieldMatrix<T> matrix)
            throws MathIllegalArgumentException {
        checkRowIndex(row);
        final int nCols = getColumnDimension();
        if ((matrix.getRowDimension() != 1) ||
                (matrix.getColumnDimension() != nCols)) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    matrix.getRowDimension(), matrix.getColumnDimension(),
                    1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            setEntry(row, i, matrix.getEntry(0, i));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> getColumnMatrix(final int column)
            throws MathIllegalArgumentException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        final FieldMatrix<T> out = createMatrix(nRows, 1);
        for (int i = 0; i < nRows; ++i) {
            out.setEntry(i, 0, getEntry(i, column));
        }

        return out;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumnMatrix(final int column, final FieldMatrix<T> matrix)
            throws MathIllegalArgumentException {
        checkColumnIndex(column);
        final int nRows = getRowDimension();
        if ((matrix.getRowDimension() != nRows) ||
                (matrix.getColumnDimension() != 1)) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    matrix.getRowDimension(), matrix.getColumnDimension(),
                    nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            setEntry(i, column, matrix.getEntry(i, 0));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldVector<T> getRowVector(final int row)
            throws MathIllegalArgumentException {
        return new ArrayFieldVector<T>(field, getRow(row), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowVector(final int row, final FieldVector<T> vector)
            throws MathIllegalArgumentException {
        checkRowIndex(row);
        final int nCols = getColumnDimension();
        if (vector.getDimension() != nCols) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    1, vector.getDimension(),
                    1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            setEntry(row, i, vector.getEntry(i));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldVector<T> getColumnVector(final int column)
            throws MathIllegalArgumentException {
        return new ArrayFieldVector<T>(field, getColumn(column), false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumnVector(final int column, final FieldVector<T> vector)
            throws MathIllegalArgumentException {

        checkColumnIndex(column);
        final int nRows = getRowDimension();
        if (vector.getDimension() != nRows) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    vector.getDimension(), 1,
                    nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            setEntry(i, column, vector.getEntry(i));
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T[] getRow(final int row) throws MathIllegalArgumentException {
        checkRowIndex(row);
        final int nCols = getColumnDimension();
        final T[] out = MathArrays.buildArray(field, nCols);
        for (int i = 0; i < nCols; ++i) {
            out[i] = getEntry(row, i);
        }

        return out;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRow(final int row, final T[] array)
            throws MathIllegalArgumentException {
        checkRowIndex(row);
        final int nCols = getColumnDimension();
        if (array.length != nCols) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    1, array.length, 1, nCols);
        }
        for (int i = 0; i < nCols; ++i) {
            setEntry(row, i, array[i]);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T[] getColumn(final int column) throws MathIllegalArgumentException {
        checkColumnIndex(column);
        final int nRows = getRowDimension();
        final T[] out = MathArrays.buildArray(field, nRows);
        for (int i = 0; i < nRows; ++i) {
            out[i] = getEntry(i, column);
        }

        return out;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumn(final int column, final T[] array)
            throws MathIllegalArgumentException {
        checkColumnIndex(column);
        final int nRows = getRowDimension();
        if (array.length != nRows) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    array.length, 1, nRows, 1);
        }
        for (int i = 0; i < nRows; ++i) {
            setEntry(i, column, array[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract T getEntry(int row, int column) throws MathIllegalArgumentException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void setEntry(int row, int column, T value) throws MathIllegalArgumentException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void addToEntry(int row, int column, T increment) throws MathIllegalArgumentException;

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void multiplyEntry(int row, int column, T factor) throws MathIllegalArgumentException;

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldMatrix<T> transpose() {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        final FieldMatrix<T> out = createMatrix(nCols, nRows);
        walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor<T>(field.getZero()) {
            /** {@inheritDoc} */
            @Override
            public void visit(final int row, final int column, final T value) {
                out.setEntry(column, row, value);
            }
        });

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSquare() {
        return getColumnDimension() == getRowDimension();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int getRowDimension();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract int getColumnDimension();

    /**
     * {@inheritDoc}
     */
    @Override
    public T getTrace() throws MathIllegalArgumentException {
        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (nRows != nCols) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NON_SQUARE_MATRIX,
                    nRows, nCols);
        }
        T trace = field.getZero();
        for (int i = 0; i < nRows; ++i) {
            trace = trace.add(getEntry(i, i));
        }
        return trace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T[] operate(final T[] v) throws MathIllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nCols) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    v.length, nCols);
        }

        final T[] out = MathArrays.buildArray(field, nRows);
        for (int row = 0; row < nRows; ++row) {
            T sum = field.getZero();
            for (int i = 0; i < nCols; ++i) {
                sum = sum.add(getEntry(row, i).multiply(v[i]));
            }
            out[row] = sum;
        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldVector<T> operate(final FieldVector<T> v)
            throws MathIllegalArgumentException {
        try {
            return new ArrayFieldVector<T>(field, operate(((ArrayFieldVector<T>) v).getDataRef()), false);
        } catch (ClassCastException cce) {
            final int nRows = getRowDimension();
            final int nCols = getColumnDimension();
            if (v.getDimension() != nCols) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                        v.getDimension(), nCols);
            }

            final T[] out = MathArrays.buildArray(field, nRows);
            for (int row = 0; row < nRows; ++row) {
                T sum = field.getZero();
                for (int i = 0; i < nCols; ++i) {
                    sum = sum.add(getEntry(row, i).multiply(v.getEntry(i)));
                }
                out[row] = sum;
            }

            return new ArrayFieldVector<T>(field, out, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T[] preMultiply(final T[] v) throws MathIllegalArgumentException {

        final int nRows = getRowDimension();
        final int nCols = getColumnDimension();
        if (v.length != nRows) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    v.length, nRows);
        }

        final T[] out = MathArrays.buildArray(field, nCols);
        for (int col = 0; col < nCols; ++col) {
            T sum = field.getZero();
            for (int i = 0; i < nRows; ++i) {
                sum = sum.add(getEntry(i, col).multiply(v[i]));
            }
            out[col] = sum;
        }

        return out;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldVector<T> preMultiply(final FieldVector<T> v)
            throws MathIllegalArgumentException {
        try {
            return new ArrayFieldVector<T>(field, preMultiply(((ArrayFieldVector<T>) v).getDataRef()), false);
        } catch (ClassCastException cce) {
            final int nRows = getRowDimension();
            final int nCols = getColumnDimension();
            if (v.getDimension() != nRows) {
                throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                        v.getDimension(), nRows);
            }

            final T[] out = MathArrays.buildArray(field, nCols);
            for (int col = 0; col < nCols; ++col) {
                T sum = field.getZero();
                for (int i = 0; i < nRows; ++i) {
                    sum = sum.add(getEntry(i, col).multiply(v.getEntry(i)));
                }
                out[col] = sum;
            }

            return new ArrayFieldVector<T>(field, out, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor) {
        final int rows = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < columns; ++column) {
                final T oldValue = getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInRowOrder(final FieldMatrixChangingVisitor<T> visitor,
                            final int startRow, final int endRow,
                            final int startColumn, final int endColumn)
            throws MathIllegalArgumentException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                final T oldValue = getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInRowOrder(final FieldMatrixPreservingVisitor<T> visitor,
                            final int startRow, final int endRow,
                            final int startColumn, final int endColumn)
            throws MathIllegalArgumentException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                startRow, endRow, startColumn, endColumn);
        for (int row = startRow; row <= endRow; ++row) {
            for (int column = startColumn; column <= endColumn; ++column) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInColumnOrder(final FieldMatrixChangingVisitor<T> visitor) {
        final int rows = getRowDimension();
        final int columns = getColumnDimension();
        visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
        for (int column = 0; column < columns; ++column) {
            for (int row = 0; row < rows; ++row) {
                final T oldValue = getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> visitor) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInColumnOrder(final FieldMatrixChangingVisitor<T> visitor,
                               final int startRow, final int endRow,
                               final int startColumn, final int endColumn)
            throws MathIllegalArgumentException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                final T oldValue = getEntry(row, column);
                final T newValue = visitor.visit(row, column, oldValue);
                setEntry(row, column, newValue);
            }
        }
        return visitor.end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInColumnOrder(final FieldMatrixPreservingVisitor<T> visitor,
                               final int startRow, final int endRow,
                               final int startColumn, final int endColumn)
            throws MathIllegalArgumentException {
        checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
        visitor.start(getRowDimension(), getColumnDimension(),
                startRow, endRow, startColumn, endColumn);
        for (int column = startColumn; column <= endColumn; ++column) {
            for (int row = startRow; row <= endRow; ++row) {
                visitor.visit(row, column, getEntry(row, column));
            }
        }
        return visitor.end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> visitor) {
        return walkInRowOrder(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> visitor) {
        return walkInRowOrder(visitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInOptimizedOrder(final FieldMatrixChangingVisitor<T> visitor,
                                  final int startRow, final int endRow,
                                  final int startColumn, final int endColumn)
            throws MathIllegalArgumentException {
        return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T walkInOptimizedOrder(final FieldMatrixPreservingVisitor<T> visitor,
                                  final int startRow, final int endRow,
                                  final int startColumn, final int endColumn)
            throws MathIllegalArgumentException {
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
     * Returns true iff <code>object</code> is a
     * <code>FieldMatrix</code> instance with the same dimensions as this
     * and all corresponding matrix entries are equal.
     *
     * @param object the object to test equality against.
     * @return true if object equals this
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof FieldMatrix<?> == false) {
            return false;
        }
        FieldMatrix<?> m = (FieldMatrix<?>) object;
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
     * @param row Row index to check.
     * @throws MathIllegalArgumentException if {@code index} is not valid.
     */
    protected void checkRowIndex(final int row) throws MathIllegalArgumentException {
        if (row < 0 || row >= getRowDimension()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.ROW_INDEX,
                    row, 0, getRowDimension() - 1);
        }
    }

    /**
     * Check if a column index is valid.
     *
     * @param column Column index to check.
     * @throws MathIllegalArgumentException if {@code index} is not valid.
     */
    protected void checkColumnIndex(final int column)
            throws MathIllegalArgumentException {
        if (column < 0 || column >= getColumnDimension()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.COLUMN_INDEX,
                    column, 0, getColumnDimension() - 1);
        }
    }

    /**
     * Check if submatrix ranges indices are valid.
     * Rows and columns are indicated counting from 0 to n-1.
     *
     * @param startRow    Initial row index.
     * @param endRow      Final row index.
     * @param startColumn Initial column index.
     * @param endColumn   Final column index.
     * @throws MathIllegalArgumentException if the indices are not valid.
     * @throws MathIllegalArgumentException if {@code endRow < startRow} or
     *                                      {@code endColumn < startColumn}.
     */
    protected void checkSubMatrixIndex(final int startRow, final int endRow,
                                       final int startColumn, final int endColumn)
            throws MathIllegalArgumentException {
        checkRowIndex(startRow);
        checkRowIndex(endRow);
        if (endRow < startRow) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INITIAL_ROW_AFTER_FINAL_ROW,
                    endRow, startRow, true);
        }

        checkColumnIndex(startColumn);
        checkColumnIndex(endColumn);
        if (endColumn < startColumn) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN,
                    endColumn, startColumn, true);
        }
    }

    /**
     * Check if submatrix ranges indices are valid.
     * Rows and columns are indicated counting from 0 to n-1.
     *
     * @param selectedRows    Array of row indices.
     * @param selectedColumns Array of column indices.
     * @throws NullArgumentException        if the arrays are {@code null}.
     * @throws MathIllegalArgumentException if the arrays have zero length.
     * @throws MathIllegalArgumentException if row or column selections are not valid.
     */
    protected void checkSubMatrixIndex(final int[] selectedRows, final int[] selectedColumns)
            throws MathIllegalArgumentException, NullArgumentException {
        if (selectedRows == null ||
                selectedColumns == null) {
            throw new NullArgumentException();
        }
        if (selectedRows.length == 0 ||
                selectedColumns.length == 0) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.NO_DATA);
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
     * @param m Matrix to check.
     * @throws MathIllegalArgumentException if the matrix is not
     *                                      addition-compatible with instance.
     */
    protected void checkAdditionCompatible(final FieldMatrix<T> m)
            throws MathIllegalArgumentException {
        if ((getRowDimension() != m.getRowDimension()) ||
                (getColumnDimension() != m.getColumnDimension())) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    m.getRowDimension(), m.getColumnDimension(),
                    getRowDimension(), getColumnDimension());
        }
    }

    /**
     * Check if a matrix is subtraction compatible with the instance.
     *
     * @param m Matrix to check.
     * @throws MathIllegalArgumentException if the matrix is not
     *                                      subtraction-compatible with instance.
     */
    protected void checkSubtractionCompatible(final FieldMatrix<T> m)
            throws MathIllegalArgumentException {
        if ((getRowDimension() != m.getRowDimension()) ||
                (getColumnDimension() != m.getColumnDimension())) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH_2x2,
                    m.getRowDimension(), m.getColumnDimension(),
                    getRowDimension(), getColumnDimension());
        }
    }

    /**
     * Check if a matrix is multiplication compatible with the instance.
     *
     * @param m Matrix to check.
     * @throws MathIllegalArgumentException if the matrix is not
     *                                      multiplication-compatible with instance.
     */
    protected void checkMultiplicationCompatible(final FieldMatrix<T> m)
            throws MathIllegalArgumentException {
        if (getColumnDimension() != m.getRowDimension()) {
            throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
                    m.getRowDimension(), getColumnDimension());
        }
    }
}
