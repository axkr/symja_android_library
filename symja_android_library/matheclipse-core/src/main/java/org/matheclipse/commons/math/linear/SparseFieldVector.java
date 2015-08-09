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

import org.apache.commons.math4.Field;
import org.apache.commons.math4.exception.DimensionMismatchException;
import org.apache.commons.math4.exception.MathArithmeticException;
import org.apache.commons.math4.exception.NotPositiveException;
import org.apache.commons.math4.exception.NullArgumentException;
import org.apache.commons.math4.exception.NumberIsTooSmallException;
import org.apache.commons.math4.exception.OutOfRangeException;
import org.apache.commons.math4.exception.util.LocalizedFormats;
import org.apache.commons.math4.util.MathUtils;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * This class implements the {@link FieldVector} interface with a {@link OpenIntToFieldHashMap} backing store.
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
public class SparseFieldVector implements FieldVector, Serializable {
	/** Serialization identifier. */
	private static final long serialVersionUID = 7841233292190413362L;
	/** Entries of the vector. */
	private final OpenIntToFieldHashMap entries;
	/** Dimension of the vector. */
	private final int virtualSize;

	/**
	 * Build a 0-length vector. Zero-length vectors may be used to initialize construction of vectors by data gathering. We start
	 * with zero-length and use either the {@link #SparseFieldVector(SparseFieldVector, int)} constructor or one of the
	 * {@code append} method ({@link #append(FieldVector)} or {@link #append(SparseFieldVector)}) to gather data into this vector.
	 *
	 * @param field
	 *            Field to which the elements belong.
	 */
	// public SparseFieldVector(Field<IExpr> field) {
	// this(field, 0);
	// }

	/**
	 * Construct a vector of zeroes.
	 *
	 * @param field
	 *            Field to which the elements belong.
	 * @param dimension
	 *            Size of the vector.
	 */
	public SparseFieldVector(int dimension) {
		virtualSize = dimension;
		entries = new OpenIntToFieldHashMap();
	}

	/**
	 * Build a resized vector, for use with append.
	 *
	 * @param v
	 *            Original vector
	 * @param resize
	 *            Amount to add.
	 */
	protected SparseFieldVector(SparseFieldVector v, int resize) {
		virtualSize = v.getDimension() + resize;
		entries = new OpenIntToFieldHashMap(v.entries);
	}

	/**
	 * Build a vector with known the sparseness (for advanced use only).
	 *
	 * @param field
	 *            Field to which the elements belong.
	 * @param dimension
	 *            Size of the vector.
	 * @param expectedSize
	 *            Expected number of non-zero entries.
	 */
	public SparseFieldVector(Field<IExpr> field, int dimension, int expectedSize) {
		virtualSize = dimension;
		entries = new OpenIntToFieldHashMap( expectedSize);
	}

	/**
	 * Create from a Field array. Only non-zero entries will be stored.
	 *
	 * @param field
	 *            Field to which the elements belong.
	 * @param values
	 *            Set of values to create from.
	 * @exception NullArgumentException
	 *                if values is null
	 */
	public SparseFieldVector(Field<IExpr> field, IExpr[] values) throws NullArgumentException {
		MathUtils.checkNotNull(values);
		virtualSize = values.length;
		entries = new OpenIntToFieldHashMap();
		for (int key = 0; key < values.length; key++) {
			IExpr value = values[key];
			entries.put(key, value);
		}
	}

	/**
	 * Copy constructor.
	 *
	 * @param v
	 *            Instance to copy.
	 */
	public SparseFieldVector(SparseFieldVector v) {
		virtualSize = v.getDimension();
		entries = new OpenIntToFieldHashMap(v.getEntries());
	}

	/**
	 * Get the entries of this instance.
	 *
	 * @return the entries of this instance
	 */
	private OpenIntToFieldHashMap getEntries() {
		return entries;
	}

	/**
	 * Optimized method to add sparse vectors.
	 *
	 * @param v
	 *            Vector to add.
	 * @return {@code this + v}.
	 * @throws DimensionMismatchException
	 *             if {@code v} is not the same size as {@code this}.
	 */
	public FieldVector add(SparseFieldVector v) throws DimensionMismatchException {
		checkVectorDimensions(v.getDimension());
		SparseFieldVector res = (SparseFieldVector) copy();
		OpenIntToFieldHashMap.Iterator iter = v.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			IExpr value = iter.value();
			if (entries.containsKey(key)) {
				res.setEntry(key, entries.get(key).plus(value));
			} else {
				res.setEntry(key, value);
			}
		}
		return res;

	}

	/**
	 * Construct a vector by appending a vector to this vector.
	 *
	 * @param v
	 *            Vector to append to this one.
	 * @return a new vector.
	 */
	public FieldVector append(SparseFieldVector v) {
		SparseFieldVector res = new SparseFieldVector(this, v.getDimension());
		OpenIntToFieldHashMap.Iterator iter = v.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key() + virtualSize, iter.value());
		}
		return res;
	}

	/** {@inheritDoc} */
	public FieldVector append(FieldVector v) {
		if (v instanceof SparseFieldVector) {
			return append((SparseFieldVector) v);
		} else {
			final int n = v.getDimension();
			FieldVector res = new SparseFieldVector(this, n);
			for (int i = 0; i < n; i++) {
				res.setEntry(i + virtualSize, v.getEntry(i));
			}
			return res;
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @exception NullArgumentException
	 *                if d is null
	 */
	public FieldVector append(IExpr d) throws NullArgumentException {
		MathUtils.checkNotNull(d);
		FieldVector res = new SparseFieldVector(this, 1);
		res.setEntry(virtualSize, d);
		return res;
	}

	/** {@inheritDoc} */
	public FieldVector copy() {
		return new SparseFieldVector(this);
	}

	/** {@inheritDoc} */
	public IExpr dotProduct(FieldVector v) throws DimensionMismatchException {
		checkVectorDimensions(v.getDimension());
		IExpr res = F.C0;
		OpenIntToFieldHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res = res.plus(v.getEntry(iter.key()).times(iter.value()));
		}
		return res;
	}

	/** {@inheritDoc} */
	public FieldVector ebeDivide(FieldVector v) throws DimensionMismatchException, MathArithmeticException {
		checkVectorDimensions(v.getDimension());
		SparseFieldVector res = new SparseFieldVector(this);
		OpenIntToFieldHashMap.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), iter.value().divide(v.getEntry(iter.key())));
		}
		return res;
	}

	/** {@inheritDoc} */
	public FieldVector ebeMultiply(FieldVector v) throws DimensionMismatchException {
		checkVectorDimensions(v.getDimension());
		SparseFieldVector res = new SparseFieldVector(this);
		OpenIntToFieldHashMap.Iterator iter = res.entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res.setEntry(iter.key(), iter.value().times(v.getEntry(iter.key())));
		}
		return res;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @deprecated as of 3.1, to be removed in 4.0. Please use the {@link #toArray()} method instead.
	 */
	@Deprecated
	public IExpr[] getData() {
		return toArray();
	}

	/** {@inheritDoc} */
	public int getDimension() {
		return virtualSize;
	}

	/** {@inheritDoc} */
	public IExpr getEntry(int index) throws OutOfRangeException {
		checkIndex(index);
		return entries.get(index);
	}

	/** {@inheritDoc} */
	public Field<IExpr> getField() {
		return null;
	}

	/** {@inheritDoc} */
	public FieldVector getSubVector(int index, int n) throws OutOfRangeException, NotPositiveException {
		if (n < 0) {
			throw new NotPositiveException(LocalizedFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
		}
		checkIndex(index);
		checkIndex(index + n - 1);
		SparseFieldVector res = new SparseFieldVector(null, n);
		int end = index + n;
		OpenIntToFieldHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if (key >= index && key < end) {
				res.setEntry(key - index, iter.value());
			}
		}
		return res;
	}

	/** {@inheritDoc} */
	public FieldVector mapAdd(IExpr d) throws NullArgumentException {
		return copy().mapAddToSelf(d);
	}

	/** {@inheritDoc} */
	public FieldVector mapAddToSelf(IExpr d) throws NullArgumentException {
		for (int i = 0; i < virtualSize; i++) {
			setEntry(i, getEntry(i).plus(d));
		}
		return this;
	}

	/** {@inheritDoc} */
	public FieldVector mapDivide(IExpr d) throws NullArgumentException, MathArithmeticException {
		return copy().mapDivideToSelf(d);
	}

	/** {@inheritDoc} */
	public FieldVector mapDivideToSelf(IExpr d) throws NullArgumentException, MathArithmeticException {
		OpenIntToFieldHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			entries.put(iter.key(), iter.value().divide(d));
		}
		return this;
	}

	/** {@inheritDoc} */
	public FieldVector mapInv() throws MathArithmeticException {
		return copy().mapInvToSelf();
	}

	/** {@inheritDoc} */
	public FieldVector mapInvToSelf() throws MathArithmeticException {
		for (int i = 0; i < virtualSize; i++) {
			setEntry(i, F.C1.divide(getEntry(i)));
		}
		return this;
	}

	/** {@inheritDoc} */
	public FieldVector mapMultiply(IExpr d) throws NullArgumentException {
		return copy().mapMultiplyToSelf(d);
	}

	/** {@inheritDoc} */
	public FieldVector mapMultiplyToSelf(IExpr d) throws NullArgumentException {
		OpenIntToFieldHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			entries.put(iter.key(), iter.value().times(d));
		}
		return this;
	}

	/** {@inheritDoc} */
	public FieldVector mapSubtract(IExpr d) throws NullArgumentException {
		return copy().mapSubtractToSelf(d);
	}

	/** {@inheritDoc} */
	public FieldVector mapSubtractToSelf(IExpr d) throws NullArgumentException {
		return mapAddToSelf(F.C0.subtract(d));
	}

	/**
	 * Optimized method to compute outer product when both vectors are sparse.
	 * 
	 * @param v
	 *            vector with which outer product should be computed
	 * @return the matrix outer product between instance and v
	 */
	public FieldMatrix outerProduct(SparseFieldVector v) {
		final int n = v.getDimension();
		SparseFieldMatrix res = new SparseFieldMatrix(virtualSize, n);
		OpenIntToFieldHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			OpenIntToFieldHashMap.Iterator iter2 = v.entries.iterator();
			while (iter2.hasNext()) {
				iter2.advance();
				res.setEntry(iter.key(), iter2.key(), iter.value().times(iter2.value()));
			}
		}
		return res;
	}

	/** {@inheritDoc} */
	public FieldMatrix outerProduct(FieldVector v) {
		if (v instanceof SparseFieldVector) {
			return outerProduct((SparseFieldVector) v);
		} else {
			final int n = v.getDimension();
			FieldMatrix res = new SparseFieldMatrix(virtualSize, n);
			OpenIntToFieldHashMap.Iterator iter = entries.iterator();
			while (iter.hasNext()) {
				iter.advance();
				int row = iter.key();
				IExpr value = iter.value();
				for (int col = 0; col < n; col++) {
					res.setEntry(row, col, value.times(v.getEntry(col)));
				}
			}
			return res;
		}
	}

	/** {@inheritDoc} */
	public FieldVector projection(FieldVector v) throws DimensionMismatchException, MathArithmeticException {
		checkVectorDimensions(v.getDimension());
		return v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)));
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @exception NullArgumentException
	 *                if value is null
	 */
	public void set(IExpr value) {
		MathUtils.checkNotNull(value);
		for (int i = 0; i < virtualSize; i++) {
			setEntry(i, value);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @exception NullArgumentException
	 *                if value is null
	 */
	public void setEntry(int index, IExpr value) throws NullArgumentException, OutOfRangeException {
		MathUtils.checkNotNull(value);
		checkIndex(index);
		entries.put(index, value);
	}

	/** {@inheritDoc} */
	public void setSubVector(int index, FieldVector v) throws OutOfRangeException {
		checkIndex(index);
		checkIndex(index + v.getDimension() - 1);
		final int n = v.getDimension();
		for (int i = 0; i < n; i++) {
			setEntry(i + index, v.getEntry(i));
		}
	}

	/**
	 * Optimized method to compute {@code this} minus {@code v}.
	 * 
	 * @param v
	 *            vector to be subtracted
	 * @return {@code this - v}
	 * @throws DimensionMismatchException
	 *             if {@code v} is not the same size as {@code this}.
	 */
	public SparseFieldVector subtract(SparseFieldVector v) throws DimensionMismatchException {
		checkVectorDimensions(v.getDimension());
		SparseFieldVector res = (SparseFieldVector) copy();
		OpenIntToFieldHashMap.Iterator iter = v.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			int key = iter.key();
			if (entries.containsKey(key)) {
				res.setEntry(key, entries.get(key).subtract(iter.value()));
			} else {
				res.setEntry(key, F.C0.subtract(iter.value()));
			}
		}
		return res;
	}

	/** {@inheritDoc} */
	public FieldVector subtract(FieldVector v) throws DimensionMismatchException {
		if (v instanceof SparseFieldVector) {
			return subtract((SparseFieldVector) v);
		} else {
			final int n = v.getDimension();
			checkVectorDimensions(n);
			SparseFieldVector res = new SparseFieldVector(this);
			for (int i = 0; i < n; i++) {
				if (entries.containsKey(i)) {
					res.setEntry(i, entries.get(i).subtract(v.getEntry(i)));
				} else {
					res.setEntry(i, F.C0.subtract(v.getEntry(i)));
				}
			}
			return res;
		}
	}

	/** {@inheritDoc} */
	public IExpr[] toArray() {
		IExpr[] res = MathArrays.buildArray(virtualSize);
		OpenIntToFieldHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			res[iter.key()] = iter.value();
		}
		return res;
	}

	/**
	 * Check whether an index is valid.
	 *
	 * @param index
	 *            Index to check.
	 * @throws OutOfRangeException
	 *             if the index is not valid.
	 */
	private void checkIndex(final int index) throws OutOfRangeException {
		if (index < 0 || index >= getDimension()) {
			throw new OutOfRangeException(index, 0, getDimension() - 1);
		}
	}

	/**
	 * Checks that the indices of a subvector are valid.
	 *
	 * @param start
	 *            the index of the first entry of the subvector
	 * @param end
	 *            the index of the last entry of the subvector (inclusive)
	 * @throws OutOfRangeException
	 *             if {@code start} of {@code end} are not valid
	 * @throws NumberIsTooSmallException
	 *             if {@code end < start}
	 * @since 3.3
	 */
	private void checkIndices(final int start, final int end) throws NumberIsTooSmallException, OutOfRangeException {
		final int dim = getDimension();
		if ((start < 0) || (start >= dim)) {
			throw new OutOfRangeException(LocalizedFormats.INDEX, start, 0, dim - 1);
		}
		if ((end < 0) || (end >= dim)) {
			throw new OutOfRangeException(LocalizedFormats.INDEX, end, 0, dim - 1);
		}
		if (end < start) {
			throw new NumberIsTooSmallException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, end, start, false);
		}
	}

	/**
	 * Check if instance dimension is equal to some expected value.
	 *
	 * @param n
	 *            Expected dimension.
	 * @throws DimensionMismatchException
	 *             if the dimensions do not match.
	 */
	protected void checkVectorDimensions(int n) throws DimensionMismatchException {
		if (getDimension() != n) {
			throw new DimensionMismatchException(getDimension(), n);
		}
	}

	/** {@inheritDoc} */
	public FieldVector add(FieldVector v) throws DimensionMismatchException {
		if (v instanceof SparseFieldVector) {
			return add((SparseFieldVector) v);
		} else {
			final int n = v.getDimension();
			checkVectorDimensions(n);
			SparseFieldVector res = new SparseFieldVector(null, getDimension());
			for (int i = 0; i < n; i++) {
				res.setEntry(i, v.getEntry(i).plus(getEntry(i)));
			}
			return res;
		}
	}

	/**
	 * Visits (but does not alter) all entries of this vector in default order (increasing index).
	 *
	 * @param visitor
	 *            the visitor to be used to process the entries of this vector
	 * @return the value returned by {@link FieldVectorPreservingVisitor#end()} at the end of the walk
	 * @since 3.3
	 */
	public IExpr walkInDefaultOrder(final FieldVectorPreservingVisitor visitor) {
		final int dim = getDimension();
		visitor.start(dim, 0, dim - 1);
		for (int i = 0; i < dim; i++) {
			visitor.visit(i, getEntry(i));
		}
		return visitor.end();
	}

	/**
	 * Visits (but does not alter) some entries of this vector in default order (increasing index).
	 *
	 * @param visitor
	 *            visitor to be used to process the entries of this vector
	 * @param start
	 *            the index of the first entry to be visited
	 * @param end
	 *            the index of the last entry to be visited (inclusive)
	 * @return the value returned by {@link FieldVectorPreservingVisitor#end()} at the end of the walk
	 * @throws NumberIsTooSmallException
	 *             if {@code end < start}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @since 3.3
	 */
	public IExpr walkInDefaultOrder(final FieldVectorPreservingVisitor visitor, final int start, final int end)
			throws NumberIsTooSmallException, OutOfRangeException {
		checkIndices(start, end);
		visitor.start(getDimension(), start, end);
		for (int i = start; i <= end; i++) {
			visitor.visit(i, getEntry(i));
		}
		return visitor.end();
	}

	/**
	 * Visits (but does not alter) all entries of this vector in optimized order. The order in which the entries are visited is
	 * selected so as to lead to the most efficient implementation; it might depend on the concrete implementation of this abstract
	 * class.
	 *
	 * @param visitor
	 *            the visitor to be used to process the entries of this vector
	 * @return the value returned by {@link FieldVectorPreservingVisitor#end()} at the end of the walk
	 * @since 3.3
	 */
	public IExpr walkInOptimizedOrder(final FieldVectorPreservingVisitor visitor) {
		return walkInDefaultOrder(visitor);
	}

	/**
	 * Visits (but does not alter) some entries of this vector in optimized order. The order in which the entries are visited is
	 * selected so as to lead to the most efficient implementation; it might depend on the concrete implementation of this abstract
	 * class.
	 *
	 * @param visitor
	 *            visitor to be used to process the entries of this vector
	 * @param start
	 *            the index of the first entry to be visited
	 * @param end
	 *            the index of the last entry to be visited (inclusive)
	 * @return the value returned by {@link FieldVectorPreservingVisitor#end()} at the end of the walk
	 * @throws NumberIsTooSmallException
	 *             if {@code end < start}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @since 3.3
	 */
	public IExpr walkInOptimizedOrder(final FieldVectorPreservingVisitor visitor, final int start, final int end)
			throws NumberIsTooSmallException, OutOfRangeException {
		return walkInDefaultOrder(visitor, start, end);
	}

	/**
	 * Visits (and possibly alters) all entries of this vector in default order (increasing index).
	 *
	 * @param visitor
	 *            the visitor to be used to process and modify the entries of this vector
	 * @return the value returned by {@link FieldVectorChangingVisitor#end()} at the end of the walk
	 * @since 3.3
	 */
	public IExpr walkInDefaultOrder(final FieldVectorChangingVisitor visitor) {
		final int dim = getDimension();
		visitor.start(dim, 0, dim - 1);
		for (int i = 0; i < dim; i++) {
			setEntry(i, visitor.visit(i, getEntry(i)));
		}
		return visitor.end();
	}

	/**
	 * Visits (and possibly alters) some entries of this vector in default order (increasing index).
	 *
	 * @param visitor
	 *            visitor to be used to process the entries of this vector
	 * @param start
	 *            the index of the first entry to be visited
	 * @param end
	 *            the index of the last entry to be visited (inclusive)
	 * @return the value returned by {@link FieldVectorChangingVisitor#end()} at the end of the walk
	 * @throws NumberIsTooSmallException
	 *             if {@code end < start}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @since 3.3
	 */
	public IExpr walkInDefaultOrder(final FieldVectorChangingVisitor visitor, final int start, final int end)
			throws NumberIsTooSmallException, OutOfRangeException {
		checkIndices(start, end);
		visitor.start(getDimension(), start, end);
		for (int i = start; i <= end; i++) {
			setEntry(i, visitor.visit(i, getEntry(i)));
		}
		return visitor.end();
	}

	/**
	 * Visits (and possibly alters) all entries of this vector in optimized order. The order in which the entries are visited is
	 * selected so as to lead to the most efficient implementation; it might depend on the concrete implementation of this abstract
	 * class.
	 *
	 * @param visitor
	 *            the visitor to be used to process the entries of this vector
	 * @return the value returned by {@link FieldVectorChangingVisitor#end()} at the end of the walk
	 * @since 3.3
	 */
	public IExpr walkInOptimizedOrder(final FieldVectorChangingVisitor visitor) {
		return walkInDefaultOrder(visitor);
	}

	/**
	 * Visits (and possibly change) some entries of this vector in optimized order. The order in which the entries are visited is
	 * selected so as to lead to the most efficient implementation; it might depend on the concrete implementation of this abstract
	 * class.
	 *
	 * @param visitor
	 *            visitor to be used to process the entries of this vector
	 * @param start
	 *            the index of the first entry to be visited
	 * @param end
	 *            the index of the last entry to be visited (inclusive)
	 * @return the value returned by {@link FieldVectorChangingVisitor#end()} at the end of the walk
	 * @throws NumberIsTooSmallException
	 *             if {@code end < start}.
	 * @throws OutOfRangeException
	 *             if the indices are not valid.
	 * @since 3.3
	 */
	public IExpr walkInOptimizedOrder(final FieldVectorChangingVisitor visitor, final int start, final int end)
			throws NumberIsTooSmallException, OutOfRangeException {
		return walkInDefaultOrder(visitor, start, end);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + virtualSize;
		OpenIntToFieldHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			int temp = iter.value().hashCode();
			result = prime * result + temp;
		}
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof SparseFieldVector)) {
			return false;
		}

		// OK, because "else if" check below ensures that
		// other must be the same type as this
		SparseFieldVector other = (SparseFieldVector) obj;

		if (virtualSize != other.virtualSize) {
			return false;
		}

		OpenIntToFieldHashMap.Iterator iter = entries.iterator();
		while (iter.hasNext()) {
			iter.advance();
			IExpr test = other.getEntry(iter.key());
			if (!test.equals(iter.value())) {
				return false;
			}
		}
		iter = other.getEntries().iterator();
		while (iter.hasNext()) {
			iter.advance();
			IExpr test = iter.value();
			if (!test.equals(getEntry(iter.key()))) {
				return false;
			}
		}
		return true;
	}
}
