package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;

import org.hipparchus.Field;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.linear.AbstractFieldMatrix;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.hipparchus.util.MathUtils;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.ExprField;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieNode;
import org.matheclipse.parser.trie.Tries;

/**
 * <p>
 * Sparse array implementation.
 * </p>
 * <b>Note:</b> in Symja sparse arrays the offset is +1 compared to java arrays or hipparchus <code>FieldVector</code>,
 * <code>FieldMatrix</code>.
 * 
 */
public class SparseArrayExpr extends DataExpr<Trie<int[], IExpr>> implements ISparseArray, Externalizable {
	/**
	 * Create a sparse <code>FieldMatrix</code>.
	 *
	 * <b>Note:</b> in Symja sparse arrays the offset is +1 compared to java arrays or hipparchus
	 * <code>FieldMatrix</code>.
	 *
	 */
	public static class SparseExprMatrix extends AbstractFieldMatrix<IExpr> {
		final SparseArrayExpr array;

		/**
		 * Create a new SparseExprMatrix with the supplied row and column dimensions.
		 *
		 * @param rowDimension
		 *            Number of rows in the new matrix.
		 * @param columnDimension
		 *            Number of columns in the new matrix.
		 * @throws org.hipparchus.exception.MathIllegalArgumentException
		 *             if row or column dimension is not positive.
		 */
		public SparseExprMatrix(final int rowDimension, final int columnDimension, IExpr defaultValue) {
			super(ExprField.CONST, rowDimension, columnDimension);
			this.array = new SparseArrayExpr(Tries.forInts(), new int[] { rowDimension, columnDimension }, defaultValue,
					false);
		}

		public SparseExprMatrix(SparseArrayExpr array) {
			super(ExprField.CONST, array.dimension[0], array.dimension[1]);
			this.array = array;
		}

		/**
		 * Copy constructor.
		 *
		 * @param other
		 *            Instance to copy.
		 */
		public SparseExprMatrix(SparseExprMatrix other) {
			super(ExprField.CONST, other.array.dimension[0], other.array.dimension[1]);
			this.array = new SparseArrayExpr(other.array.fData, other.array.dimension, other.array.defaultValue, true);
		}

		@Override
		public void addToEntry(int row, int column, IExpr increment) throws MathIllegalArgumentException {
			final Trie<int[], IExpr> map = array.fData;
			final int[] key = new int[] { row + 1, column + 1 };
			IExpr value = map.get(key);
			value = S.Plus.of(value != null ? value : array.defaultValue, increment);
			if (value.equals(array.defaultValue)) {
				map.remove(key);
			} else {
				map.put(key, value);
			}
		}

		@Override
		public FieldMatrix<IExpr> copy() {
			return new SparseExprMatrix(this);
		}

		@Override
		public SparseExprMatrix createMatrix(int rowDimension, int columnDimension)
				throws MathIllegalArgumentException {
			return new SparseExprMatrix(rowDimension, columnDimension, F.C0);
		}

		@Override
		public int getColumnDimension() {
			return array.dimension[1];
		}

		@Override
		public IExpr getEntry(int arg0, int arg1) throws MathIllegalArgumentException {
			IExpr value = array.fData.get(new int[] { arg0 + 1, arg1 + 1 });
			return value == null ? array.defaultValue : value;
		}

		@Override
		public int getRowDimension() {
			return array.dimension[0];
		}

		public SparseArrayExpr getSparseArray() {
			return array;
		}

		/** {@inheritDoc} */
		@Override
		public SparseExprMatrix multiply(final FieldMatrix<IExpr> m) throws MathIllegalArgumentException {
			// safety check
			checkMultiplicationCompatible(m);

			final int nRows = getRowDimension();
			final int nCols = m.getColumnDimension();
			final int nSum = getColumnDimension();
			final SparseExprMatrix out = createMatrix(nRows, nCols);
			for (int row = 0; row < nRows; ++row) {
				for (int col = 0; col < nCols; ++col) {
					IExpr sum = F.C0;
					for (int i = 0; i < nSum; ++i) {
						sum = sum.add(getEntry(row, i).multiply(m.getEntry(i, col)));
					}
					out.setEntry(row, col, sum);
				}
			}

			return out;
		}

		@Override
		public void multiplyEntry(int row, int column, IExpr factor) throws MathIllegalArgumentException {
			final Trie<int[], IExpr> map = array.fData;
			final int[] key = new int[] { row + 1, column + 1 };
			IExpr value = map.get(key);
			value = S.Times.of(value != null ? value : array.defaultValue, factor);
			if (value.equals(array.defaultValue)) {
				map.remove(key);
			} else {
				map.put(key, value);
			}
		}

		/** {@inheritDoc} */
		@Override
		public SparseExprVector operate(final FieldVector<IExpr> fv) throws MathIllegalArgumentException {
			if (fv instanceof SparseExprVector) {
				SparseExprVector v = (SparseExprVector) fv;
				final int nRows = getRowDimension();
				final int nCols = getColumnDimension();
				if (v.getDimension() != nCols) {
					throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH, v.getDimension(),
							nCols);
				}

				SparseExprVector out = new SparseExprVector(nRows, array.defaultValue);
				for (int row = 0; row < nRows; row++) {
					IExpr sum = F.C0;
					for (int i = 0; i < nCols; i++) {
						sum = sum.add(getEntry(row, i).multiply(v.getEntry(i)));
					}
					out.setEntry(row, sum);
				}

				return out;
			}
			return null;
		}

		@Override
		public void setEntry(int row, int column, IExpr value) throws MathIllegalArgumentException {
			final Trie<int[], IExpr> map = array.fData;
			final int[] key = new int[] { row + 1, column + 1 };
			if (value.equals(array.defaultValue)) {
				map.remove(key);
			} else {
				map.put(key, value);
			}
		}
	}

	/**
	 * Create a sparse <code>FieldVector</code>.
	 * 
	 * <b>Note:</b> in Symja sparse arrays the offset is +1 compared to java arrays or hipparchus
	 * <code>FieldVector</code>.
	 */
	public static class SparseExprVector implements FieldVector<IExpr> {
		final SparseArrayExpr array;
		/** Dimension of the vector. */
		private final int virtualSize;

		/**
		 * Create a new SparseExprVector with the supplied row dimensions.
		 *
		 * @param dimension
		 *            Number of elements in the new vector.
		 * @throws org.hipparchus.exception.MathIllegalArgumentException
		 *             if row or column dimension is not positive.
		 */
		public SparseExprVector(final int dimension, IExpr defaultValue) {
			this.array = new SparseArrayExpr(Tries.forInts(), new int[] { dimension }, defaultValue, false);
			this.virtualSize = dimension;
		}

		public SparseExprVector(SparseArrayExpr array) {
			this.array = array;
			this.virtualSize = array.dimension[0];
		}

		/**
		 * Build a resized vector, for use with append.
		 *
		 * @param v
		 *            Original vector
		 * @param resize
		 *            Amount to add.
		 */
		protected SparseExprVector(SparseArrayExpr array, int resize) {
			this.array = new SparseArrayExpr(array.fData, array.dimension, array.defaultValue, true);
			this.virtualSize = array.dimension[0] + resize;
		}

		/**
		 * Copy constructor.
		 *
		 * @param other
		 *            Instance to copy.
		 */
		public SparseExprVector(SparseExprVector other) {
			this.array = new SparseArrayExpr(other.array.fData, other.array.dimension, other.array.defaultValue, true);
			this.virtualSize = other.array.dimension[0];
		}

		/**
		 * Build a resized vector, for use with append.
		 *
		 * @param v
		 *            Original vector
		 * @param resize
		 *            Amount to add.
		 */
		protected SparseExprVector(SparseExprVector v, int resize) {
			this.array = new SparseArrayExpr(v.array.fData, v.array.dimension, v.array.defaultValue, true);
			this.virtualSize = v.array.dimension[0] + resize;
		}

		@Override
		public SparseExprVector add(FieldVector<IExpr> v) throws MathIllegalArgumentException {
			// if (v instanceof SparseExprVector) {
			// return add((SparseExprVector) v);
			// } else {
			final int n = v.getDimension();
			checkVectorDimensions(n);
			SparseExprVector res = new SparseExprVector(getDimension(), array.defaultValue);
			for (int i = 0; i < n; i++) {
				res.setEntry(i, v.getEntry(i).add(getEntry(i)));
			}
			return res;
			// }
		}

		@Override
		public SparseExprVector append(FieldVector<IExpr> v) {
			// if (v instanceof SparseFieldVector<?>) {
			// return append((SparseFieldVector<T>) v);
			// } else {
			final int n = v.getDimension();
			SparseExprVector res = new SparseExprVector(array, n);
			for (int i = 0; i < n; i++) {
				res.setEntry(i + virtualSize, v.getEntry(i));
			}
			return res;
			// }
		}

		@Override
		public SparseExprVector append(IExpr d) {
			MathUtils.checkNotNull(d);
			SparseExprVector res = new SparseExprVector(this, 1);
			res.setEntry(virtualSize, d);
			return res;
		}

		/**
		 * Check whether an index is valid.
		 *
		 * @param index
		 *            Index to check.
		 * @throws MathIllegalArgumentException
		 *             if the index is not valid.
		 */
		private void checkIndex(final int index) throws MathIllegalArgumentException {
			MathUtils.checkRangeInclusive(index, 0, getDimension() - 1);
		}

		/**
		 * Checks that the indices of a subvector are valid.
		 *
		 * @param start
		 *            the index of the first entry of the subvector
		 * @param end
		 *            the index of the last entry of the subvector (inclusive)
		 * @throws MathIllegalArgumentException
		 *             if {@code start} of {@code end} are not valid
		 * @throws MathIllegalArgumentException
		 *             if {@code end < start}
		 */
		private void checkIndices(final int start, final int end) throws MathIllegalArgumentException {
			final int dim = getDimension();
			if ((start < 0) || (start >= dim)) {
				throw new MathIllegalArgumentException(LocalizedCoreFormats.INDEX, start, 0, dim - 1);
			}
			if ((end < 0) || (end >= dim)) {
				throw new MathIllegalArgumentException(LocalizedCoreFormats.INDEX, end, 0, dim - 1);
			}
			if (end < start) {
				throw new MathIllegalArgumentException(LocalizedCoreFormats.INITIAL_ROW_AFTER_FINAL_ROW, end, start,
						false);
			}
		}

		/**
		 * Check if instance dimension is equal to some expected value.
		 *
		 * @param n
		 *            Expected dimension.
		 * @throws MathIllegalArgumentException
		 *             if the dimensions do not match.
		 */
		protected void checkVectorDimensions(int n) throws MathIllegalArgumentException {
			if (getDimension() != n) {
				throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH, getDimension(), n);
			}
		}

		@Override
		public SparseExprVector copy() {
			return new SparseExprVector(this);
		}

		@Override
		public IExpr dotProduct(FieldVector<IExpr> v) throws MathIllegalArgumentException {
			checkVectorDimensions(v.getDimension());
			IASTAppendable plus = F.PlusAlloc(array.fData.size());
			for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
				int[] key = entry.getKey();
				IExpr value = entry.getValue();
				plus.append(v.getEntry(key[0] - 1).multiply(value));
			}
			return EvalEngine.get().evaluate(plus.oneIdentity(F.C0));
		}

		@Override
		public SparseExprVector ebeDivide(FieldVector<IExpr> v)
				throws MathIllegalArgumentException, MathRuntimeException {
			checkVectorDimensions(v.getDimension());
			SparseExprVector res = new SparseExprVector(this);
			for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
				int[] key = entry.getKey();
				IExpr value = entry.getValue();
				IExpr newEntry = value.divide(v.getEntry(key[0] - 1));
				res.setEntry(key[0] - 1, newEntry);
			}
			return res;
		}

		@Override
		public SparseExprVector ebeMultiply(FieldVector<IExpr> v) throws MathIllegalArgumentException {
			checkVectorDimensions(v.getDimension());
			SparseExprVector res = new SparseExprVector(this);
			for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
				int[] key = entry.getKey();
				IExpr value = entry.getValue();
				IExpr newEntry = value.times(v.getEntry(key[0] - 1));
				res.setEntry(key[0] - 1, newEntry);
			}
			return res;
		}

		@Override
		public int getDimension() {
			return array.dimension[0];
		}

		@Override
		public IExpr getEntry(int row) throws MathIllegalArgumentException {
			IExpr value = array.fData.get(new int[] { row + 1 });
			return value == null ? array.defaultValue : value;
		}

		@Override
		public Field<IExpr> getField() {
			return ExprField.CONST;
		}

		public SparseArrayExpr getSparseArray() {
			return array;
		}

		@Override
		public SparseExprVector getSubVector(int index, int n) throws MathIllegalArgumentException {
			if (n < 0) {
				throw new MathIllegalArgumentException(LocalizedCoreFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
			}
			checkIndex(index);
			checkIndex(index + n - 1);
			SparseExprVector res = new SparseExprVector(n, array.defaultValue);
			int end = index + n;

			for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
				int[] key = entry.getKey();
				IExpr value = entry.getValue();
				if (key[0] >= index + 1 && key[0] < end + 1) {
					res.setEntry(key[0] - index + 1, value);
				}
			}
			return res;
		}

		@Override
		public SparseExprVector mapAdd(IExpr d) throws NullArgumentException {
			return copy().mapAddToSelf(d);
		}

		@Override
		public SparseExprVector mapAddToSelf(IExpr d) throws NullArgumentException {
			for (int i = 0; i < virtualSize; i++) {
				setEntry(i, getEntry(i).add(d));
			}
			return this;
		}

		@Override
		public SparseExprVector mapDivide(IExpr d) throws NullArgumentException, MathRuntimeException {
			return copy().mapDivideToSelf(d);
		}

		@Override
		public SparseExprVector mapDivideToSelf(IExpr d) throws NullArgumentException, MathRuntimeException {
			Trie<int[], IExpr> map = array.fData;
			for (TrieNode<int[], IExpr> entry : map.nodeSet()) {
				int[] key = entry.getKey();
				IExpr value = entry.getValue();
				map.put(key, value.divide(d));
			}
			return this;
		}

		@Override
		public SparseExprVector mapInv() throws MathRuntimeException {
			return copy().mapInvToSelf();
		}

		@Override
		public SparseExprVector mapInvToSelf() throws MathRuntimeException {
			for (int i = 0; i < virtualSize; i++) {
				setEntry(i, getEntry(i).inverse());
			}
			return this;
		}

		@Override
		public SparseExprVector mapMultiply(IExpr d) throws NullArgumentException {
			return copy().mapMultiplyToSelf(d);
		}

		@Override
		public SparseExprVector mapMultiplyToSelf(IExpr d) throws NullArgumentException {
			Trie<int[], IExpr> map = array.fData;
			for (TrieNode<int[], IExpr> entry : map.nodeSet()) {
				int[] key = entry.getKey();
				IExpr value = entry.getValue();
				map.put(key, value.multiply(d));
			}
			return this;
		}

		@Override
		public SparseExprVector mapSubtract(IExpr d) throws NullArgumentException {
			return copy().mapSubtractToSelf(d);
		}

		@Override
		public SparseExprVector mapSubtractToSelf(IExpr d) throws NullArgumentException {
			return mapAddToSelf(d.negate());
		}

		@Override
		public SparseExprMatrix outerProduct(FieldVector<IExpr> fv) {
			if (fv instanceof SparseExprVector) {
				SparseExprVector v = (SparseExprVector) fv;
				final int n = v.getDimension();
				SparseExprMatrix res = new SparseExprMatrix(virtualSize, n, array.defaultValue);

				Trie<int[], IExpr> map1 = array.fData;
				for (TrieNode<int[], IExpr> entry1 : map1.nodeSet()) {
					int[] key1 = entry1.getKey();
					IExpr value1 = entry1.getValue();

					Trie<int[], IExpr> map2 = v.array.fData;
					for (TrieNode<int[], IExpr> entry2 : map2.nodeSet()) {
						int[] key2 = entry2.getKey();
						IExpr value2 = entry2.getValue();
						res.setEntry(key1[0] - 1, key2[0] - 1, value1.multiply(value2));
					}
				}
				return res;
			}
			return null;
		}

		@Override
		public SparseExprVector projection(FieldVector<IExpr> fv)
				throws MathIllegalArgumentException, MathRuntimeException {
			if (fv instanceof SparseExprVector) {
				SparseExprVector v = (SparseExprVector) fv;
				checkVectorDimensions(v.getDimension());
				return v.mapMultiply(dotProduct(v).divide(v.dotProduct(v)));
			}
			return null;
		}

		@Override
		public void set(IExpr value) {
			MathUtils.checkNotNull(value);
			for (int i = 0; i < virtualSize; i++) {
				setEntry(i, value);
			}
		}

		@Override
		public void setEntry(int row, IExpr value) throws MathIllegalArgumentException {
			final Trie<int[], IExpr> map = array.fData;
			final int[] key = new int[] { row + 1 };
			if (value.equals(array.defaultValue)) {
				map.remove(key);
			} else {
				map.put(key, value);
			}
		}

		@Override
		public void setSubVector(int index, FieldVector<IExpr> v) throws MathIllegalArgumentException {
			checkIndex(index);
			checkIndex(index + v.getDimension() - 1);
			final int n = v.getDimension();
			for (int i = 0; i < n; i++) {
				setEntry(i + index, v.getEntry(i));
			}
		}

		@Override
		public SparseExprVector subtract(FieldVector<IExpr> fv) throws MathIllegalArgumentException {

			if (fv instanceof SparseExprVector) {
				SparseExprVector v = (SparseExprVector) fv;
				checkVectorDimensions(v.getDimension());
				SparseExprVector res = copy();

				Trie<int[], IExpr> map = v.array.fData;
				for (TrieNode<int[], IExpr> entry : map.nodeSet()) {
					int[] key = entry.getKey();
					int position = key[0];
					IExpr value = entry.getValue();
					if (array.fData.containsKey(key)) {
						res.setEntry(position, array.fData.get(key).subtract(value));
					} else {
						res.setEntry(position, value.negate());
					}
				}
				return res;
			}
			return null;
		}

		@Override
		public IExpr[] toArray() {
			IExpr[] res = new IExpr[virtualSize];
			for (int i = 0; i < res.length; i++) {
				res[i] = array.defaultValue;
			}
			Trie<int[], IExpr> map = array.fData;
			for (TrieNode<int[], IExpr> entry : map.nodeSet()) {
				res[entry.getKey()[0]] = entry.getValue();
			}
			return res;
		}
	}

	/**
	 * Create array rules from the nested lists. From array rules a sparse array can be created.
	 * 
	 * @param nestedListsOfValues
	 * @return
	 */
	public static IAST arrayRules(IAST nestedListsOfValues, IExpr defaultValue) {
		int depth = SparseArrayExpr.depth(nestedListsOfValues, 1);
		if (depth < 0) {
			return F.NIL;
		}
		IASTAppendable result = F.ListAlloc();
		IASTMutable positions = F.constantArray(F.C1, depth);
		if (SparseArrayExpr.arrayRulesRecursive(nestedListsOfValues, depth + 1, depth, positions, defaultValue,
				result)) {
			result.append(F.Rule(F.constantArray(F.$b(), depth), defaultValue));
			return result;
		}
		return F.NIL;
	}

	private static boolean arrayRulesRecursive(IAST nestedListsOfValues, int count, int level, IASTMutable positions,
			IExpr defaultValue, IASTAppendable result) {
		final int position = count - level;
		level--;
		for (int i = 1; i < nestedListsOfValues.size(); i++) {
			IExpr arg = nestedListsOfValues.get(i);
			positions.set(position, F.ZZ(i));
			if (level == 0) {
				if (arg.isList()) {
					return false;
				}
				if (!arg.equals(defaultValue)) {
					result.append(F.Rule(positions.copy(), arg));
				}
			} else {
				if (!arg.isList() || //
						!arrayRulesRecursive((IAST) arg, count, level, positions, defaultValue, result)) {
					return false;
				}
			}
		}
		return true;
	}

	private static int[] checkPositions(IAST ast, IExpr arg, EvalEngine engine) {
		if (arg.isNonEmptyList()) {
			IAST list = (IAST) arg;
			int[] result = new int[list.argSize()];
			try {
				IExpr expr;
				for (int i = 1; i < list.size(); i++) {
					expr = list.get(i);
					int intValue = expr.toIntDefault();
					if (intValue == Integer.MIN_VALUE) {
						return null;
					}
					if (intValue <= 0) {
						return null;
					}
					result[i - 1] = intValue;
				}
				return result;
			} catch (RuntimeException rex) {
				if (FEConfig.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
		}
		return null;
	}

	private static int[] createTrie(IAST arrayRulesList, final Trie<int[], IExpr> value, int defaultDimension,
			IExpr[] defaultValue, EvalEngine engine) {
		int[] determinedDimension = null;
		IExpr arg1 = arrayRulesList.arg1();
		IAST rule1 = (IAST) arg1;
		int[] positions = null;
		int depth = 1;
		if (rule1.arg1().isList()) {
			IAST positionList = (IAST) rule1.arg1();
			depth = positionList.argSize();
			positions = checkPositions(arrayRulesList, positionList, engine);
			if (positions == null) {
				if (positionList.forAll(x -> x.isBlank())) {
					if (!defaultValue[0].isPresent()) {
						defaultValue[0] = rule1.arg2();
					} else if (!defaultValue[0].equals(rule1.arg2())) {
						// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
						IOFunctions.printMessage(S.SparseArray, "posr",
								F.List(arrayRulesList, rule1.arg1(), F.ZZ(depth)), engine);
						return null;
					}
				} else {
					// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
					IOFunctions.printMessage(S.SparseArray, "posr", F.List(arrayRulesList, positionList, F.ZZ(depth)),
							EvalEngine.get());
					return null;
				}
			}
		} else {
			int n = rule1.arg1().toIntDefault();
			if (n <= 0) {
				return null;
			}
			positions = new int[1];
			positions[0] = n;
		}

		if (positions != null) {

			determinedDimension = new int[depth];
			if (defaultDimension > 0) {
				for (int i = 0; i < depth; i++) {
					determinedDimension[i] = defaultDimension;
				}
			} else {
				for (int i = 0; i < depth; i++) {
					if (positions[i] > determinedDimension[i]) {
						determinedDimension[i] = positions[i];
					}
				}
			}

			value.put(positions, rule1.arg2());
		}
		for (int j = 2; j < arrayRulesList.size(); j++) {
			IExpr arg = arrayRulesList.get(j);
			if (arg.isRule()) {
				IAST rule = (IAST) arg;
				if (rule.arg1().isList()) {
					IAST positionList = (IAST) rule.arg1();
					positions = checkPositions(arrayRulesList, positionList, engine);
					if (positions == null) {
						if (positionList.forAll(x -> x.isBlank())) {
							if (!defaultValue[0].isPresent()) {
								defaultValue[0] = rule.arg2();
								continue;
							} else if (!defaultValue[0].equals(rule.arg2())) {
								// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
								IOFunctions.printMessage(S.SparseArray, "posr",
										F.List(arrayRulesList, rule.arg1(), F.ZZ(depth)), engine);
								return null;
							}
						} else {
							// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
							IOFunctions.printMessage(S.SparseArray, "posr",
									F.List(arrayRulesList, rule.arg1(), F.ZZ(depth)), engine);
							return null;
						}
					} else {
						if (positions.length != depth) {
							// The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
							IOFunctions.printMessage(S.SparseArray, "posr",
									F.List(arrayRulesList, rule.arg1(), F.ZZ(depth)), engine);
							return null;
						}
						for (int i = 0; i < depth; i++) {
							if (positions[i] > determinedDimension[i]) {
								determinedDimension[i] = positions[i];
							}
						}
						value.putIfAbsent(positions, rule.arg2());
					}
				} else {
					int n = rule.arg1().toIntDefault();
					if (n <= 0) {
						return null;
					}
					positions = new int[1];
					positions[0] = n;
					if (n > determinedDimension[0]) {
						determinedDimension[0] = n;
					}
					value.putIfAbsent(positions, rule.arg2());
				}
			}
		}
		return determinedDimension;
	}

	/**
	 * Determine the sparse array depth from the first entry, which isn't a <code>List(...)</code>.
	 * 
	 * @param list
	 * @param count
	 * @return
	 */
	private static int depth(IAST list, int count) {
		if (list.size() > 1) {
			if (list.arg1().isList()) {
				return depth((IAST) list.arg1(), ++count);
			}
			return count;
		}
		return -1;
	}

	public static SparseArrayExpr newInstance(IAST list, IExpr defaultValue) {
		ArrayList<Integer> dims = LinearAlgebra.dimensions(list);
		if (dims != null && dims.size() > 0) {
			defaultValue = defaultValue.orElse(F.C0);
			IAST listOfRules = SparseArrayExpr.arrayRules(list, defaultValue);
			if (listOfRules.isPresent()) {
				SparseArrayExpr result = SparseArrayExpr.newInstance(listOfRules, -1, defaultValue);
				int[] dimension = new int[dims.size()];
				for (int i = 0; i < dims.size(); i++) {
					dimension[i] = dims.get(i);
				}
				result.dimension = dimension;
				return result;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param arrayRulesList
	 * @param defaultDimension
	 * @param defaultValue
	 *            default value for positions not specified in arrayRulesList. If <code>F.NIL</code> determine from '_'
	 *            (Blank) rule.
	 * @param engine
	 * @return <code>null</code> if a new <code>SparseArrayExpr</code> cannot be created.
	 */
	public static SparseArrayExpr newInstance(IAST arrayRulesList, int defaultDimension, IExpr defaultValue) {
		IExpr[] defValue = new IExpr[] { defaultValue };
		final Trie<int[], IExpr> value = Tries.forInts();
		int[] determinedDimension = createTrie(arrayRulesList, value, defaultDimension, defValue, EvalEngine.get());
		if (determinedDimension != null) {
			return new SparseArrayExpr(value, determinedDimension, defValue[0].orElse(F.C0), false);
		}
		return null;
	}

	private transient IAST normalCache = null;

	/**
	 * The dimension of the sparse array.
	 */
	int[] dimension;

	/**
	 * The default value for the positions with no entry in the map. Usually <code>0</code>.
	 */
	IExpr defaultValue;

	/**
	 * Constructor for serialization.
	 */
	public SparseArrayExpr() {
		super(S.SparseArray, null);
	}

	/**
	 * Copy constructor. See <a href="https://en.wikipedia.org/wiki/Trie">Wikipedia - Trie</a>.
	 * 
	 * @param trie
	 *            map positions of a sparse array to a value
	 * @param dimension
	 *            the dimensions of the positions
	 * @param defaultValue
	 *            default value for positions not specified in the trie
	 * @param deepCopy
	 *            if <code>true</code> create a deep copy.
	 */
	protected SparseArrayExpr(final Trie<int[], IExpr> trie, int[] dimension, IExpr defaultValue, boolean deepCopy) {
		super(S.SparseArray, trie);
		if (deepCopy) {
			this.fData = Tries.forInts();
			for (TrieNode<int[], IExpr> entry : trie.nodeSet()) {
				this.fData.put(entry.getKey(), entry.getValue());
			}
			this.dimension = new int[dimension.length];
			System.arraycopy(dimension, 0, this.dimension, 0, dimension.length);
			this.defaultValue = defaultValue;
		} else {
			this.dimension = dimension;
			this.defaultValue = defaultValue;
		}
		// this.addEvalFlags(IAST.SEQUENCE_FLATTENED);
	}

	/**
	 * Convert this sparse array to array rules list format.
	 */
	@Override
	public IAST arrayRules() {
		IASTAppendable result = F.ListAlloc(fData.size() + 1);

		for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
			int[] key = entry.getKey();
			IExpr value = entry.getValue();
			IAST lhs = F.ast(S.List, key);
			result.append(F.Rule(lhs, value));
		}
		result.append(F.Rule(F.constantArray(F.$b(), dimension.length), defaultValue));
		return result;
	}

	@Override
	public SparseArrayExpr copy() {
		return new SparseArrayExpr(fData, dimension, defaultValue, true);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof SparseArrayExpr) {
			SparseArrayExpr s = (SparseArrayExpr) obj;
			if (Arrays.equals(dimension, s.dimension) && //
					defaultValue.equals(s.defaultValue)) {
				Trie<int[], IExpr> sData = s.fData;
				if (fData.size() == sData.size()) {
					for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
						int[] sequence = entry.getKey();
						IExpr sValue = sData.get(sequence);
						if (sValue == null) {
							return false;
						}
						IExpr value = entry.getValue();
						if (!value.equals(sValue)) {
							return false;
						}
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public IExpr getDefaultValue() {
		return defaultValue;
	}

	@Override
	public int[] getDimension() {
		return dimension;
	}

	@Override
	public IExpr getPart(IAST ast, int startPosition) {
		int[] dims = getDimension();

		final int partSize = ast.size() - startPosition;
		if (dims.length >= partSize) {

			int len = 0;
			int[] partIndex = new int[dims.length];
			int count = 0;
			for (int i = startPosition; i < ast.size(); i++) {
				partIndex[i - startPosition] = ast.get(i).toIntDefault(-1);
				if (partIndex[i - startPosition] == -1) {
					count++;
				} else if (partIndex[i - startPosition] > dims[i - startPosition] || //
						partIndex[i - startPosition] <= 0) {
					return IOFunctions.printMessage(S.Part, "partw", F.List(ast.get(i), ast), EvalEngine.get());
				}
			}
			for (int i = partSize; i < dims.length; i++) {
				partIndex[i] = -1;
				count++;
			}
			if (count == 0 && partSize == dims.length) {
				IExpr temp = fData.get(partIndex);
				if (temp == null) {
					return defaultValue;
				}
				return temp;
			}
			int[] newDimension = new int[count];
			count = 0;
			for (int i = 0; i < partIndex.length; i++) {
				if (partIndex[i] == (-1)) {
					len++;
					newDimension[count++] = dims[i];
				}
			}
			final Trie<int[], IExpr> value = Tries.forInts();
			for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
				int[] key = entry.getKey();
				boolean evaled = true;
				for (int i = 0; i < partIndex.length; i++) {
					if (partIndex[i] == (-1)) {
						continue;
					}
					if (partIndex[i] != key[i]) {
						evaled = false;
						break;
					}
				}
				if (evaled) {
					int[] newKey = new int[len];
					int j = 0;
					for (int i = 0; i < partIndex.length; i++) {
						if (partIndex[i] == (-1)) {
							newKey[j++] = key[i];
						}
					}
					value.put(newKey, entry.getValue());
				}
			}
			return new SparseArrayExpr(value, newDimension, defaultValue.orElse(F.C0), false);

		}
		return IOFunctions.printMessage(S.Part, "partd", F.List(ast), EvalEngine.get());

	}

	@Override
	public int hashCode() {
		return (fData == null) ? 541 : 541 + fData.hashCode();
	}

	@Override
	public ISymbol head() {
		return S.SparseArray;
	}

	@Override
	public int hierarchy() {
		return SPARSEARRAYID;
	}

	@Override
	public int[] isMatrix(boolean setMatrixFormat) {
		return (dimension.length == 2) ? dimension : null;
	}

	@Override
	public boolean isSparseArray() {
		return true;
	}

	@Override
	public int isVector() {
		return (dimension.length == 1) ? dimension[0] : -1;
	}

	/** {@inheritDoc} */
	@Override
	public final SparseArrayExpr mapThread(final IAST replacement, int position) {
		final Function<IExpr, IExpr> function = x -> replacement.setAtCopy(position, x);
		return mapValues(function);
	}

	public SparseArrayExpr mapValues(final Function<IExpr, IExpr> function ) {
		SparseArrayExpr result = copy();
		for (TrieNode<int[], IExpr> entry : result.fData.nodeSet()) {
			IExpr value = entry.getValue();
			IExpr temp = function.apply(value);
			if (temp.isPresent()) {
				result.fData.put(entry.getKey(), temp); 
			}
		}
		IExpr temp = function.apply(result.defaultValue);
		if (temp.isPresent()) {
			result.defaultValue=temp; 
		} 
		return result;
	}

	@Override
	public IAST normal(boolean nilIfUnevaluated) {
		if (dimension.length > 0) {
			if (normalCache != null) {
				return normalCache;
			}
			IASTAppendable list = normalAppendable();
			normalCache = list;
			return normalCache;
		}
		return F.headAST0(S.List);
	}

	private void normal(Trie<int[], IExpr> map, IASTMutable list, int[] dimension, int position, int[] index) {
		if (dimension.length - 1 == position) {
			int size = dimension[position];
			for (int i = 1; i <= size; i++) {
				index[position] = i;
				IExpr expr = map.get(index);
				if (expr == null) {
					list.set(i, defaultValue);
				} else {
					list.set(i, expr);
				}
			}
			return;
		}
		int size1 = dimension[position];
		int size2 = dimension[position + 1];
		for (int i = 1; i <= size1; i++) {
			index[position] = i;
			IASTAppendable currentList = F.ast(S.List, size2, true);
			list.set(i, currentList);
			normal(map, currentList, dimension, position + 1, index);
		}
	}

	private IASTAppendable normalAppendable() {
		IASTAppendable list = F.ast(S.List, dimension[0], true);
		int[] index = new int[dimension.length];
		for (int i = 0; i < index.length; i++) {
			index[i] = 1;
		}
		normal(fData, list, dimension, 0, index);
		return list;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		defaultValue = (IExpr) in.readObject();
		final int len = in.readInt();
		dimension = new int[len];
		for (int i = 0; i < len; i++) {
			dimension[i] = in.readInt();
		}
		IAST arrayRulesList = (IAST) in.readObject();
		fData = Tries.forInts();
		IExpr[] defValue = new IExpr[] { defaultValue };
		int[] determinedDimension = createTrie(arrayRulesList, fData, -1, defValue, EvalEngine.get());
		if (determinedDimension == null) {
			throw new java.io.InvalidClassException("no valid Trie creation");
		}
	}

	public IExpr set(int i, IExpr value) {
		if (dimension.length == 1 && i > 0 && i <= dimension[0]) {
			int[] positions = new int[1];
			positions[0] = i;
			IExpr old = fData.get(positions);
			fData.put(positions, value);
			return (old == null) ? defaultValue : old;
		}
		throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + dimension[0]);
	}

	@Override
	public int size() {
		return dimension[0];
	}

	/** {@inheritDoc} */
	@Override
	public double[][] toDoubleMatrix() {
		if (dimension.length == 2 && dimension[0] > 0 && dimension[1] > 0) {
			try {
				double[][] result = new double[dimension[0]][dimension[1]];
				if (!defaultValue.isZero()) {
					double d = defaultValue.evalDouble();
					for (int i = 0; i < dimension[0]; i++) {
						for (int j = 0; j < dimension[1]; j++) {
							result[i][j] = d;
						}
					}
				}
				for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
					int[] key = entry.getKey();
					IExpr value = entry.getValue();
					result[key[0] - 1][key[1] - 1] = value.evalDouble();
				}
				return result;
			} catch (ArgumentTypeException rex) {

			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public double[] toDoubleVector() {
		if (dimension.length == 1 && dimension[0] > 0) {
			try {
				double[] result = new double[argSize()];
				if (!defaultValue.isZero()) {
					double d = defaultValue.evalDouble();
					for (int i = 0; i < result.length; i++) {
						result[i] = d;
					}
				}
				for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
					int[] key = entry.getKey();
					IExpr value = entry.getValue();
					result[key[0] - 1] = value.evalDouble();
				}
				return result;
			} catch (ArgumentTypeException rex) {

			}
		}
		return null;
	}

	@Override
	public FieldMatrix<IExpr> toFieldMatrix() {
		if (dimension.length == 2 && dimension[0] > 0 && dimension[1] > 0) {
			return new SparseExprMatrix(this);
		}
		return null;
	}

	@Override
	public FieldVector<IExpr> toFieldVector() {
		if (dimension.length == 1 && dimension[0] > 0) {
			return new SparseExprVector(this);
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("SparseArray(Number of elements: ");
		buf.append(fData.size());
		buf.append(" Dimensions: {");
		for (int i = 0; i < dimension.length; i++) {
			buf.append(dimension[i]);
			if (i < dimension.length - 1) {
				buf.append(",");
			}
		}
		buf.append("} Default value: ");
		buf.append(defaultValue.toString());
		buf.append(")");
		return buf.toString();
	}

	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		output.writeObject(defaultValue);
		output.writeInt(dimension.length);
		for (int i = 0; i < dimension.length; i++) {
			output.writeInt(dimension[i]);
		}
		IAST rules = arrayRules();
		output.writeObject(rules);
	}

}
