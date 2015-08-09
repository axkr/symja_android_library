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
 
import org.matheclipse.core.interfaces.IExpr;

/**
 * Sparse matrix implementation based on an open addressed map.
 *
 * <p>
 * Caveat: This implementation assumes that, for any {@code x}, the equality {@code x * 0d == 0d} holds. But it is is not true for
 * {@code NaN}. Moreover, zero entries will lose their sign. Some operations (that involve {@code NaN} and/or infinities) may thus
 * give incorrect results.
 * </p>
 * 
 * @param <T>
 *            the type of the field elements
 * @since 2.0
 */
public class SparseFieldMatrix extends AbstractFieldMatrix {

	/** Storage for (sparse) matrix elements. */
	private final OpenIntToFieldHashMap entries;
	/** Row dimension. */
	private final int rows;
	/** Column dimension. */
	private final int columns;

	/**
	 * Create a matrix with no data.
	 */
	public SparseFieldMatrix() {
		super();
		rows = 0;
		columns = 0;
		entries = new OpenIntToFieldHashMap();
	}

	/**
	 * Create a new SparseFieldMatrix with the supplied row and column dimensions.
	 * 
	 * @param rowDimension
	 *            Number of rows in the new matrix.
	 * @param columnDimension
	 *            Number of columns in the new matrix.
	 *
	 * @throws org.apache.commons.math4.exception.NotStrictlyPositiveException
	 *             if row or column dimension is not positive.
	 */
	public SparseFieldMatrix(final int rowDimension, final int columnDimension) {
		super(rowDimension, columnDimension);
		this.rows = rowDimension;
		this.columns = columnDimension;
		entries = new OpenIntToFieldHashMap();
	}

	/**
	 * Copy constructor.
	 *
	 * @param other
	 *            Instance to copy.
	 */
	public SparseFieldMatrix(SparseFieldMatrix other) {
		super(other.getRowDimension(), other.getColumnDimension());
		rows = other.getRowDimension();
		columns = other.getColumnDimension();
		entries = new OpenIntToFieldHashMap(other.entries);
	}

	/**
	 * Generic copy constructor.
	 *
	 * @param other
	 *            Instance to copy.
	 */
	public SparseFieldMatrix(FieldMatrix other) {
		super(other.getRowDimension(), other.getColumnDimension());
		rows = other.getRowDimension();
		columns = other.getColumnDimension();
		entries = new OpenIntToFieldHashMap();
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				setEntry(i, j, other.getEntry(i, j));
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void addToEntry(int row, int column, IExpr increment) {
		checkRowIndex(row);
		checkColumnIndex(column);
		final int key = computeKey(row, column);
		final IExpr value = entries.get(key).plus(increment);
		if (value.isZero()) {
			entries.remove(key);
		} else {
			entries.put(key, value);
		}
	}

	/** {@inheritDoc} */
	@Override
	public FieldMatrix copy() {
		return new SparseFieldMatrix(this);
	}

	/** {@inheritDoc} */
	@Override
	public FieldMatrix createMatrix(int rowDimension, int columnDimension) {
		return new SparseFieldMatrix(rowDimension, columnDimension);
	}

	/** {@inheritDoc} */
	@Override
	public int getColumnDimension() {
		return columns;
	}

	/** {@inheritDoc} */
	@Override
	public IExpr getEntry(int row, int column) {
		checkRowIndex(row);
		checkColumnIndex(column);
		return entries.get(computeKey(row, column));
	}

	/** {@inheritDoc} */
	@Override
	public int getRowDimension() {
		return rows;
	}

	/** {@inheritDoc} */
	@Override
	public void multiplyEntry(int row, int column, IExpr factor) {
		checkRowIndex(row);
		checkColumnIndex(column);
		final int key = computeKey(row, column);
		final IExpr value = entries.get(key).times(factor);
		if (value.isZero()) {
			entries.remove(key);
		} else {
			entries.put(key, value);
		}

	}

	/** {@inheritDoc} */
	@Override
	public void setEntry(int row, int column, IExpr value) {
		checkRowIndex(row);
		checkColumnIndex(column);
		if (value.isZero()) {
			entries.remove(computeKey(row, column));
		} else {
			entries.put(computeKey(row, column), value);
		}
	}

	/**
	 * Compute the key to access a matrix element.
	 *
	 * @param row
	 *            Row index of the matrix element.
	 * @param column
	 *            Column index of the matrix element.
	 * @return the key within the map to access the matrix element.
	 */
	private int computeKey(int row, int column) {
		return row * columns + column;
	}
}
