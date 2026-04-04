package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import org.hipparchus.Field;
import org.hipparchus.exception.LocalizedCoreFormats;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.exception.NullArgumentException;
import org.hipparchus.linear.AbstractFieldMatrix;
import org.hipparchus.linear.FieldMatrix;
import org.hipparchus.linear.FieldVector;
import org.hipparchus.linear.OpenMapRealMatrix;
import org.hipparchus.linear.OpenMapRealVector;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.util.MathUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.CombinatoricUtil;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.LinearAlgebraUtil;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.generic.Tensors;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISparseArray;
import org.matheclipse.core.patternmatching.IPatternMap;
import org.matheclipse.core.patternmatching.PatternMatcherAndEvaluator;
import org.matheclipse.parser.trie.Trie;
import org.matheclipse.parser.trie.TrieNode;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * A sparse array implementation using a Trie data structure to store non-default elements. This
 * class is designed for efficient handling of arrays with many default-valued (often zero)
 * elements.
 *
 * <p>
 * It supports multi-dimensional arrays and provides various methods for manipulation, such as
 * algebraic operations, transformations, and conversions to other formats (dense arrays, matrices,
 * vectors).
 */
public class SparseArrayExpr extends DataExpr<Trie<int[], IExpr>>
    implements ISparseArray, Externalizable {

  /**
   * Represents a sparse matrix of `IExpr` elements, backed by a `SparseArrayExpr`.
   */
  public static class SparseExprMatrix extends AbstractFieldMatrix<IExpr> {
    final SparseArrayExpr array;

    /**
     * Creates a `SparseExprMatrix` with the given dimensions and default value.
     *
     * @param rowDimension The number of rows.
     * @param columnDimension The number of columns.
     * @param defaultValue The default value for matrix elements.
     */
    public SparseExprMatrix(final int rowDimension, final int columnDimension, IExpr defaultValue) {
      super(F.EXPR_FIELD, rowDimension, columnDimension);
      this.array = new SparseArrayExpr(Config.TRIE_INT2EXPR_BUILDER.build(),
          new int[] {rowDimension, columnDimension}, defaultValue, false);
    }

    /**
     * Creates a `SparseExprMatrix` from a `SparseArrayExpr`.
     *
     * @param array The sparse array.
     * @param copyArray if `true` a deep copy of the array is performed.
     */
    public SparseExprMatrix(SparseArrayExpr array, boolean copyArray) {
      super(F.EXPR_FIELD, array.fDimension[0], array.fDimension[1]);
      if (copyArray) {
        this.array = new SparseArrayExpr(array.fData, array.fDimension, array.fDefaultValue, true);
      } else {
        this.array = array;
      }
    }

    /**
     * Copy constructor.
     *
     * @param other Instance to copy.
     */
    public SparseExprMatrix(SparseExprMatrix other) {
      super(F.EXPR_FIELD, other.array.fDimension[0], other.array.fDimension[1]);
      this.array = new SparseArrayExpr(other.array.fData, other.array.fDimension,
          other.array.fDefaultValue, true);
    }

    /**
     * Change an entry in the specified row and column.
     *
     * @param row Row location of entry to be set.
     * @param column Column location of entry to be set.
     * @param increment Value to add to the current matrix entry in {@code (row, column)}.
     * @throws MathIllegalArgumentException if the row or column index is not valid.
     */
    @Override
    public void addToEntry(int row, int column, IExpr increment)
        throws MathIllegalArgumentException {
      final Trie<int[], IExpr> trie = array.fData;
      final int[] key = new int[] {row + 1, column + 1};
      IExpr value = trie.get(key);
      value = (value != null ? value : array.fDefaultValue).plus(increment);
      if (value.equals(array.fDefaultValue)) {
        trie.remove(key);
      } else {
        trie.put(key, value);
      }
    }

    /**
     * Make a (deep) copy of this.
     *
     * @return a copy of this matrix.
     */
    @Override
    public FieldMatrix<IExpr> copy() {
      return new SparseExprMatrix(this);
    }

    /**
     * Create a new FieldMatrix<T> of the same type as the instance with the supplied row and column
     * dimensions.
     *
     * @param rowDimension the number of rows in the new matrix
     * @param columnDimension the number of columns in the new matrix
     * @return a new matrix of the same type as the instance
     * @throws MathIllegalArgumentException if row or column dimension is not positive.
     */
    @Override
    public SparseExprMatrix createMatrix(int rowDimension, int columnDimension)
        throws MathIllegalArgumentException {
      return new SparseExprMatrix(rowDimension, columnDimension, F.C0);
    }

    @Override
    public int getColumnDimension() {
      return array.fDimension[1];
    }

    /**
     * Returns the entry in the specified row and column.
     *
     * @param row row location of entry to be fetched
     * @param column column location of entry to be fetched
     * @return matrix entry in [row,column] position
     * @throws MathIllegalArgumentException if the row or column index is not valid.
     */
    @Override
    public IExpr getEntry(int row, int column) throws MathIllegalArgumentException {
      IExpr value = array.fData.get(new int[] {row + 1, column + 1});
      return value == null ? array.fDefaultValue : value;
    }

    @Override
    public int getRowDimension() {
      return array.fDimension[0];
    }

    public SparseArrayExpr getSparseArray() {
      return array;
    }

    /**
     * Postmultiply this matrix by {@code m}. Optimized to O(k) sparse-sparse matrix multiplication
     * if both defaults are zero.
     *
     * @param m Matrix to postmultiply by.
     * @return {@code this} * {@code m}.
     * @throws MathIllegalArgumentException if the number of columns of {@code this} matrix is not
     *         equal to the number of rows of matrix {@code m}.
     */
    @Override
    public SparseExprMatrix multiply(final FieldMatrix<IExpr> m)
        throws MathIllegalArgumentException {
      // safety check
      checkMultiplicationCompatible(m);

      final int nRows = getRowDimension();
      final int nCols = m.getColumnDimension();
      final int nSum = getColumnDimension();
      final SparseExprMatrix out = createMatrix(nRows, nCols);

      if (array.fDefaultValue.isZero() && m instanceof SparseExprMatrix) {
        SparseExprMatrix mSparse = (SparseExprMatrix) m;
        if (mSparse.array.fDefaultValue.isZero()) {
          // Fast path: Both matrices are sparse and default is 0.

          // Group matrix B by its native 1-based row index to avoid dense lookups
          java.util.Map<Integer, java.util.List<java.util.Map.Entry<Integer, IExpr>>> bRows =
              new java.util.HashMap<>();
          Iterator<Entry<int[], IExpr>> iteratorB = mSparse.array.fData.entrySet().iterator();
          while (iteratorB.hasNext()) {
            Entry<int[], IExpr> entryB = iteratorB.next();
            int[] keyB = entryB.getKey();
            bRows.computeIfAbsent(keyB[0], k -> new java.util.ArrayList<>())
                .add(new java.util.AbstractMap.SimpleEntry<>(keyB[1], entryB.getValue()));
          }

          // Accumulate directly into the output Trie
          EvalEngine engine = EvalEngine.get();
          Trie<int[], IExpr> outTrie = out.array.fData;

          Iterator<Entry<int[], IExpr>> iteratorA = array.fData.entrySet().iterator();
          while (iteratorA.hasNext()) {
            Entry<int[], IExpr> entryA = iteratorA.next();
            int[] keyA = entryA.getKey();
            IExpr valA = entryA.getValue();

            java.util.List<java.util.Map.Entry<Integer, IExpr>> bRow = bRows.get(keyA[1]);
            if (bRow != null) {
              for (java.util.Map.Entry<Integer, IExpr> bColEntry : bRow) {
                int cB = bColEntry.getKey();
                IExpr valB = bColEntry.getValue();

                IExpr product = engine.evaluate(F.Times(valA, valB));
                int[] keyC = new int[] {keyA[0], cB};

                IExpr oldSum = outTrie.get(keyC);
                if (oldSum == null) {
                  outTrie.put(keyC, product);
                } else {
                  outTrie.put(keyC, engine.evaluate(F.Plus(oldSum, product)));
                }
              }
            }
          }

          // 3. Purge any elements that evaluated back to the default value (e.g. 0)
          java.util.List<int[]> toRemove = new java.util.ArrayList<>();
          Iterator<Entry<int[], IExpr>> iteratorC = outTrie.entrySet().iterator();
          while (iteratorC.hasNext()) {
            Entry<int[], IExpr> entryC = iteratorC.next();
            if (entryC.getValue().equals(array.fDefaultValue)) {
              toRemove.add(entryC.getKey());
            }
          }
          for (int[] key : toRemove) {
            outTrie.remove(key);
          }
          return out;
        }
      }

      // Slow path: Fallback for dense matrices or matrices with non-zero default values
      EvalEngine engine = EvalEngine.get();
      for (int row = 0; row < nRows; ++row) {
        for (int col = 0; col < nCols; ++col) {
          IASTAppendable plus = F.PlusAlloc();
          INumber n = F.C0;
          for (int i = 0; i < nSum; ++i) {
            IExpr rowEntry = getEntry(row, i);
            IExpr columnEntry = m.getEntry(i, col);
            if (rowEntry.isNumber() && columnEntry.isNumber()) {
              n = n.plus(((INumber) rowEntry).times((INumber) columnEntry));
            } else {
              plus.append(F.Times(rowEntry, columnEntry));
            }
          }
          plus.append(n);
          out.setEntry(row, col, engine.evaluate(plus.oneIdentity0()));
        }
      }

      return out;
    }


    /**
     * Change an entry in the specified row and column.
     *
     * @param row Row location of entry to be set.
     * @param column Column location of entry to be set.
     * @param factor Multiplication factor for the current matrix entry in {@code (row,column)}
     * @throws MathIllegalArgumentException if the row or column index is not valid.
     */
    @Override
    public void multiplyEntry(int row, int column, IExpr factor)
        throws MathIllegalArgumentException {
      final Trie<int[], IExpr> trie = array.fData;
      final int[] key = new int[] {row + 1, column + 1};
      IExpr value = trie.get(key);
      value = (value != null ? value : array.fDefaultValue).multiply(factor);
      if (value.equals(array.fDefaultValue)) {
        trie.remove(key);
      } else {
        trie.put(key, value);
      }
    }

    /**
     * Returns the result of multiplying this by the vector {@code fv}. Optimized to iterate over
     * sparse nodes and bypass O(N^2) dense loops when the matrix default value is zero.
     *
     * @param fv the vector to operate on
     * @return {@code this * fv}
     * @throws MathIllegalArgumentException if the number of columns of {@code this} matrix is not
     *         equal to the size of the vector {@code fv}.
     */
    @Override
    public SparseExprVector operate(final FieldVector<IExpr> fv)
        throws MathIllegalArgumentException {
      final int nRows = getRowDimension();
      final int nCols = getColumnDimension();

      if (fv.getDimension() != nCols) {
        throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
            fv.getDimension(), nCols);
      }

      SparseExprVector out = new SparseExprVector(nRows, F.C0);

      if (array.fDefaultValue.isZero()) {
        // Fast path: Matrix default is 0. Time complexity is O(k)
        // where k is the number of non-zero matrix elements.
        java.util.Map<Integer, IASTAppendable> rowSums = new java.util.HashMap<>();

        for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
          int[] key = entry.getKey();
          int r = key[0] - 1;
          int c = key[1] - 1;

          IExpr mVal = entry.getValue();
          IExpr vVal = fv.getEntry(c);

          if (!vVal.isZero()) {
            IASTAppendable plus = rowSums.computeIfAbsent(r, k -> F.PlusAlloc(8)); //
            plus.append(mVal.times(vVal));
          }
        }

        EvalEngine engine = EvalEngine.get();
        for (java.util.Map.Entry<Integer, IASTAppendable> rowEntry : rowSums.entrySet()) {
          IExpr evaluatedSum = engine.evaluate(rowEntry.getValue().oneIdentity(F.C0));
          out.setEntry(rowEntry.getKey(), evaluatedSum);
        }
      } else {
        // Slow path: Fallback for non-zero default matrix elements.
        EvalEngine engine = EvalEngine.get();
        for (int row = 0; row < nRows; row++) {
          IASTAppendable plus = F.PlusAlloc(nCols); //
          for (int col = 0; col < nCols; col++) {
            plus.append(getEntry(row, col).times(fv.getEntry(col)));
          }
          out.setEntry(row, engine.evaluate(plus.oneIdentity(F.C0)));
        }
      }

      return out;
    }

    /**
     * Set the entry in the specified row and column.
     *
     * @param row row location of entry to be set
     * @param column column location of entry to be set
     * @param value matrix entry to be set in row,column
     * @throws MathIllegalArgumentException if the row or column index is not valid.
     */
    @Override
    public void setEntry(int row, int column, IExpr value) throws MathIllegalArgumentException {
      final Trie<int[], IExpr> trie = array.fData;
      final int[] key = new int[] {row + 1, column + 1};
      if (value.equals(array.fDefaultValue)) {
        trie.remove(key);
      } else {
        trie.put(key, value);
      }
    }
  }

  /**
   * This class implements the {@link FieldVector} interface with a {@link SparseArrayExpr} backing
   * store.
   *
   * <p>
   * <b>Note:</b> in Symja sparse arrays the offset is +1 compared to java arrays or hipparchus
   * <code>FieldVector</code>.
   */
  public static class SparseExprVector implements FieldVector<IExpr> {
    /** The underlying sparse array. */
    final SparseArrayExpr array;
    /** Dimension of the vector. */
    private final int virtualSize;

    /**
     * Creates a `SparseExprVector` with a given size and default value.
     *
     * @param virtualSize The size of the vector.
     * @param defaultValue The default value for vector elements.
     */
    public SparseExprVector(final int virtualSize, IExpr defaultValue) {
      this.array = new SparseArrayExpr(Config.TRIE_INT2EXPR_BUILDER.build(),
          new int[] {virtualSize}, defaultValue, false);
      this.virtualSize = virtualSize;
    }


    /**
     * Creates a `SparseExprVector` from a `SparseArrayExpr`.
     *
     * @param array The sparse array.
     * @param copyArray if `true` a deep copy of the array is performed.
     */
    public SparseExprVector(SparseArrayExpr array, boolean copyArray) {
      if (copyArray) {
        this.array = new SparseArrayExpr(Config.TRIE_INT2EXPR_BUILDER.build(),
            new int[] {array.fDimension[0]}, array.fDefaultValue, false);
      } else {
        this.array = array;
      }
      this.virtualSize = array.fDimension[0];
    }

    /**
     * Build a resized vector, for use with append.
     *
     * @param array original vector
     * @param resize amount to add.
     */
    protected SparseExprVector(SparseArrayExpr array, int resize) {
      this.array = new SparseArrayExpr(array.fData, array.fDimension, array.fDefaultValue, true);
      this.virtualSize = array.fDimension[0] + resize;
    }

    /**
     * Copy constructor.
     *
     * @param other Instance to copy.
     */
    public SparseExprVector(SparseExprVector other) {
      this.array = new SparseArrayExpr(other.array.fData, other.array.fDimension,
          other.array.fDefaultValue, true);
      this.virtualSize = other.array.fDimension[0];
    }

    /**
     * Build a resized vector, for use with append.
     *
     * @param v Original vector
     * @param resize Amount to add.
     */
    protected SparseExprVector(SparseExprVector v, int resize) {
      this.array =
          new SparseArrayExpr(v.array.fData, v.array.fDimension, v.array.fDefaultValue, true);
      this.virtualSize = v.array.fDimension[0] + resize;
    }


    /**
     * Compute the sum of `this` and `v`. Optimized to iterate over sparse nodes if `v` is also a
     * SparseExprVector.
     *
     * @param v vector to be added
     * @return `this + v`
     * @throws MathIllegalArgumentException if `v` is not the same size as `this`
     */
    @Override
    public SparseExprVector add(FieldVector<IExpr> v) throws MathIllegalArgumentException {
      final int n = v.getDimension();
      checkVectorDimensions(n);
      SparseExprVector res = new SparseExprVector(getDimension(), array.fDefaultValue);

      if (v instanceof SparseExprVector) {
        SparseExprVector vSparse = (SparseExprVector) v;

        // Add elements present in 'this'
        for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
          int[] key = entry.getKey();
          IExpr vValue = vSparse.array.fData.get(key);
          vValue = vValue == null ? vSparse.array.fDefaultValue : vValue;
          res.setEntry(key[0] - 1, entry.getValue().plus(vValue));
        }

        // Add elements present in 'v' but default in 'this'
        for (TrieNode<int[], IExpr> entry : vSparse.array.fData.nodeSet()) {
          int[] key = entry.getKey();
          if (!array.fData.containsKey(key)) {
            res.setEntry(key[0] - 1, array.fDefaultValue.plus(entry.getValue()));
          }
        }
      } else {
        // Fallback for dense vectors
        for (int i = 0; i < n; i++) {
          res.setEntry(i, getEntry(i).plus(v.getEntry(i)));
        }
      }
      return res;
    }

    /**
     * Append a vector to `this` vector.
     *
     * @param v vector to be appended
     * @return a new vector
     */
    @Override
    public SparseExprVector append(FieldVector<IExpr> v) {
      final int n = v.getDimension();
      SparseExprVector res = new SparseExprVector(array, n);

      if (v instanceof SparseExprVector) {
        SparseExprVector vSparse = (SparseExprVector) v;
        boolean sameDefault = array.fDefaultValue.equals(vSparse.array.fDefaultValue);

        if (sameDefault) {
          // Fast-path
          for (TrieNode<int[], IExpr> entry : vSparse.array.fData.nodeSet()) {
            res.setEntry(entry.getKey()[0] - 1 + virtualSize, entry.getValue());
          }
        } else {
          // Dense copy to explicitly override mismatched default spaces
          for (int i = 0; i < n; i++) {
            res.setEntry(i + virtualSize, v.getEntry(i));
          }
        }
      } else {
        for (int i = 0; i < n; i++) {
          res.setEntry(i + virtualSize, v.getEntry(i));
        }
      }
      return res;
    }

    /**
     * Append a single element to `this` vector.
     *
     * @param d element to be appended
     * @return a new vector
     */
    @Override
    public SparseExprVector append(IExpr d) {
      MathUtils.checkNotNull(d);
      SparseExprVector res = new SparseExprVector(this, 1);
      res.setEntry(virtualSize, d);
      return res;
    }

    /**
     * Checks if the given index is valid.
     *
     * @param index the index to be checked.
     * @throws MathIllegalArgumentException if the index is not valid.
     */
    private void checkIndex(final int index) throws MathIllegalArgumentException {
      MathUtils.checkRangeInclusive(index, 0, getDimension() - 1);
    }

    /**
     * Checks if the given dimension matches the vector's dimension.
     * 
     * @param n
     * @throws MathIllegalArgumentException
     */
    protected void checkVectorDimensions(int n) throws MathIllegalArgumentException {
      if (getDimension() != n) {
        throw new MathIllegalArgumentException(LocalizedCoreFormats.DIMENSIONS_MISMATCH,
            getDimension(), n);
      }
    }

    @Override
    public SparseExprVector copy() {
      return new SparseExprVector(this);
    }

    /**
     * Compute the dot product.
     *
     * @param v vector with which dot product should be computed
     * @return the scalar dot product of {@code this} and {@code v}
     * @throws MathIllegalArgumentException if {@code v} is not the same size as {@code this}
     */
    @Override
    public IExpr dotProduct(FieldVector<IExpr> v) throws MathIllegalArgumentException {
      checkVectorDimensions(v.getDimension());

      if (array.fDefaultValue.isZero()) {
        // Fast path for standard sparse vectors
        IASTAppendable plus = F.PlusAlloc(array.fData.size());
        for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
          int[] key = entry.getKey();
          plus.append(v.getEntry(key[0] - 1).multiply(entry.getValue()));
        }
        return EvalEngine.get().evaluate(plus.oneIdentity(F.C0));
      } else {
        // Mathematical fallback for vectors with non-zero default elements
        IASTAppendable plus = F.PlusAlloc(virtualSize);
        for (int i = 0; i < virtualSize; i++) {
          plus.append(getEntry(i).multiply(v.getEntry(i)));
        }
        return EvalEngine.get().evaluate(plus.oneIdentity(F.C0));
      }
    }

    /**
     * Element-by-element division. Optimized to avoid dense map lookups when both vectors are
     * sparse.
     *
     * @param v vector by which instance elements must be divided
     * @return a vector containing `this[i] / v[i]` for all `i`
     * @throws MathIllegalArgumentException if `v` is not the same size as `this`
     * @throws MathRuntimeException if one entry of `v` is zero.
     */
    @Override
    public SparseExprVector ebeDivide(FieldVector<IExpr> v)
        throws MathIllegalArgumentException, MathRuntimeException {
      checkVectorDimensions(v.getDimension());
      SparseExprVector res = new SparseExprVector(getDimension(), array.fDefaultValue);

      if (v instanceof SparseExprVector) {
        SparseExprVector vSparse = (SparseExprVector) v;

        // Divide elements present in 'this'
        for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
          int[] key = entry.getKey();
          IExpr vValue = vSparse.array.fData.get(key);
          vValue = vValue == null ? vSparse.array.fDefaultValue : vValue;
          res.setEntry(key[0] - 1, entry.getValue().divide(vValue));
        }

        // Divide elements present in 'v' but default in 'this'
        for (TrieNode<int[], IExpr> entry : vSparse.array.fData.nodeSet()) {
          int[] key = entry.getKey();
          if (!array.fData.containsKey(key)) {
            res.setEntry(key[0] - 1, array.fDefaultValue.divide(entry.getValue()));
          }
        }
      } else {
        // Fallback for dense vectors
        for (int i = 0; i < getDimension(); i++) {
          res.setEntry(i, getEntry(i).divide(v.getEntry(i)));
        }
      }
      return res;
    }

    /**
     * Element-by-element multiplication. Optimized to avoid dense map lookups when both vectors are
     * sparse.
     *
     * @param v vector by which instance elements must be multiplied
     * @return a vector containing `this[i] * v[i]` for all `i`
     * @throws MathIllegalArgumentException if `v` is not the same size as `this`
     */
    @Override
    public SparseExprVector ebeMultiply(FieldVector<IExpr> v) throws MathIllegalArgumentException {
      checkVectorDimensions(v.getDimension());
      SparseExprVector res = new SparseExprVector(getDimension(), array.fDefaultValue);

      if (v instanceof SparseExprVector) {
        SparseExprVector vSparse = (SparseExprVector) v;

        // Multiply elements present in 'this'
        for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
          int[] key = entry.getKey();
          IExpr vValue = vSparse.array.fData.get(key);
          vValue = vValue == null ? vSparse.array.fDefaultValue : vValue;
          res.setEntry(key[0] - 1, entry.getValue().times(vValue));
        }

        // Multiply elements present in 'v' but default in 'this'
        for (TrieNode<int[], IExpr> entry : vSparse.array.fData.nodeSet()) {
          int[] key = entry.getKey();
          if (!array.fData.containsKey(key)) {
            res.setEntry(key[0] - 1, array.fDefaultValue.times(entry.getValue()));
          }
        }
      } else {
        // Fallback for dense vectors
        for (int i = 0; i < getDimension(); i++) {
          res.setEntry(i, getEntry(i).times(v.getEntry(i)));
        }
      }
      return res;
    }

    /**
     * Returns the size of the vector.
     *
     * @return size
     */
    @Override
    public int getDimension() {
      return array.fDimension[0];
    }

    /**
     * Returns the entry in the specified index.
     *
     * @param index Index location of entry to be fetched.
     * @return the vector entry at {@code index}.
     * @throws MathIllegalArgumentException if the index is not valid.
     */
    @Override
    public IExpr getEntry(int index) throws MathIllegalArgumentException {
      IExpr value = array.fData.get(new int[] {index + 1});
      return value == null ? array.fDefaultValue : value;
    }

    /**
     * Get the type of field elements of the vector.
     *
     * @return type of field elements of the vector
     */
    @Override
    public Field<IExpr> getField() {
      return F.EXPR_FIELD;
    }

    /**
     * Get the underlying sparse array.
     *
     * @return The sparse array.
     */
    public SparseArrayExpr getSparseArray() {
      return array;
    }

    /**
     * Get a subvector from consecutive elements.
     *
     * @param index index of first element.
     * @param n number of elements to be retrieved.
     * @return a vector containing n elements.
     * @throws MathIllegalArgumentException if the index is not valid.
     * @throws MathIllegalArgumentException if the number of elements if not positive.
     */
    @Override
    public SparseExprVector getSubVector(int index, int n) throws MathIllegalArgumentException {
      if (n < 0) {
        throw new MathIllegalArgumentException(
            LocalizedCoreFormats.NUMBER_OF_ELEMENTS_SHOULD_BE_POSITIVE, n);
      }
      checkIndex(index);
      checkIndex(index + n - 1);
      SparseExprVector res = new SparseExprVector(n, array.fDefaultValue);
      int end = index + n;

      for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
        int[] key = entry.getKey();
        IExpr value = entry.getValue();
        if (key[0] >= index + 1 && key[0] < end + 1) {
          res.setEntry(key[0] - index - 1, value);
        }
      }
      return res;
    }

    /**
     * Map an addition operation to each entry.
     *
     * @param d value to be added to each entry
     * @return {@code this + d}
     * @throws NullArgumentException if {@code d} is {@code null}.
     */
    @Override
    public SparseExprVector mapAdd(IExpr d) throws NullArgumentException {
      return copy().mapAddToSelf(d);
    }

    /**
     * Map an addition operation to each entry.
     *
     * <p>
     * The instance <strong>is</strong> changed by this method.
     *
     * @param d value to be added to each entry
     * @return for convenience, return {@code this}
     * @throws NullArgumentException if {@code d} is {@code null}.
     */
    @Override
    public SparseExprVector mapAddToSelf(IExpr d) throws NullArgumentException {
      IExpr newDefault = array.fDefaultValue.plus(d);
      Trie<int[], IExpr> newTrie = Config.TRIE_INT2EXPR_BUILDER.build();
      for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
        IExpr newVal = entry.getValue().plus(d);
        if (!newVal.equals(newDefault)) {
          newTrie.put(entry.getKey(), newVal);
        }
      }
      array.fDefaultValue = newDefault;
      array.fData = newTrie;
      return this;
    }

    /**
     * Map a division operation to each entry.
     *
     * @param d value to divide all entries by
     * @return {@code this / d}
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws MathRuntimeException if {@code d} is zero.
     */
    @Override
    public SparseExprVector mapDivide(IExpr d) throws NullArgumentException, MathRuntimeException {
      return copy().mapDivideToSelf(d);
    }

    /**
     * Map a division operation to each entry.
     *
     * <p>
     * The instance <strong>is</strong> changed by this method.
     *
     * @param d value to divide all entries by
     * @return for convenience, return {@code this}
     * @throws NullArgumentException if {@code d} is {@code null}.
     * @throws MathRuntimeException if {@code d} is zero.
     */
    @Override
    public SparseExprVector mapDivideToSelf(IExpr d)
        throws NullArgumentException, MathRuntimeException {
      IExpr newDefault = array.fDefaultValue.divide(d);
      Trie<int[], IExpr> newTrie = Config.TRIE_INT2EXPR_BUILDER.build();
      for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
        IExpr newVal = entry.getValue().divide(d);
        if (!newVal.equals(newDefault)) {
          newTrie.put(entry.getKey(), newVal);
        }
      }
      array.fDefaultValue = newDefault;
      array.fData = newTrie;
      return this;
    }

    /**
     * Map the 1/x function to each entry.
     *
     * @return a vector containing the result of applying the function to each entry.
     * @throws MathRuntimeException if one of the entries is zero.
     */
    @Override
    public SparseExprVector mapInv() throws MathRuntimeException {
      return copy().mapInvToSelf();
    }

    /**
     * Map the 1/x function to each entry.
     *
     * <p>
     * The instance <strong>is</strong> changed by this method.
     *
     * @return for convenience, return {@code this}
     * @throws MathRuntimeException if one of the entries is zero.
     */
    @Override
    public SparseExprVector mapInvToSelf() throws MathRuntimeException {
      IExpr newDefault = array.fDefaultValue.inverse();
      Trie<int[], IExpr> newTrie = Config.TRIE_INT2EXPR_BUILDER.build();
      for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
        IExpr newVal = entry.getValue().inverse();
        if (!newVal.equals(newDefault)) {
          newTrie.put(entry.getKey(), newVal);
        }
      }
      array.fDefaultValue = newDefault;
      array.fData = newTrie;
      return this;
    }

    /**
     * Map a multiplication operation to each entry.
     *
     * @param d value to multiply all entries by
     * @return {@code this * d}
     * @throws NullArgumentException if {@code d} is {@code null}.
     */
    @Override
    public SparseExprVector mapMultiply(IExpr d) throws NullArgumentException {
      return copy().mapMultiplyToSelf(d);
    }

    /**
     * Map a multiplication operation to each entry.
     *
     * <p>
     * The instance <strong>is</strong> changed by this method.
     *
     * @param d value to multiply all entries by
     * @return for convenience, return {@code this}
     * @throws NullArgumentException if {@code d} is {@code null}.
     */
    @Override
    public SparseExprVector mapMultiplyToSelf(IExpr d) throws NullArgumentException {
      IExpr newDefault = array.fDefaultValue.multiply(d);
      Trie<int[], IExpr> newTrie = Config.TRIE_INT2EXPR_BUILDER.build();
      for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
        IExpr newVal = entry.getValue().multiply(d);
        if (!newVal.equals(newDefault)) {
          newTrie.put(entry.getKey(), newVal);
        }
      }
      array.fDefaultValue = newDefault;
      array.fData = newTrie;
      return this;
    }

    /**
     * Map a subtraction operation to each entry.
     *
     * @param d value to be subtracted to each entry
     * @return {@code this - d}
     * @throws NullArgumentException if {@code d} is {@code null}
     */
    @Override
    public SparseExprVector mapSubtract(IExpr d) throws NullArgumentException {
      return copy().mapSubtractToSelf(d);
    }

    /**
     * Map a subtraction operation to each entry.
     *
     * <p>
     * The instance <strong>is</strong> changed by this method.
     *
     * @param d value to be subtracted to each entry
     * @return for convenience, return {@code this}
     * @throws NullArgumentException if {@code d} is {@code null}
     */
    @Override
    public SparseExprVector mapSubtractToSelf(IExpr d) throws NullArgumentException {
      return mapAddToSelf(d.negate());
    }

    /**
     * Compute the outer product.
     *
     * @param fv vector with which outer product should be computed
     * @return the matrix outer product between instance and fv
     */
    @Override
    public SparseExprMatrix outerProduct(FieldVector<IExpr> fv) {
      if (fv instanceof SparseExprVector) {
        SparseExprVector v = (SparseExprVector) fv;
        final int n = v.getDimension();
        SparseExprMatrix res = new SparseExprMatrix(virtualSize, n, array.fDefaultValue);

        Trie<int[], IExpr> trie1 = array.fData;
        for (TrieNode<int[], IExpr> entry1 : trie1.nodeSet()) {
          int[] key1 = entry1.getKey();
          IExpr value1 = entry1.getValue();

          Trie<int[], IExpr> trie2 = v.array.fData;
          for (TrieNode<int[], IExpr> entry2 : trie2.nodeSet()) {
            int[] key2 = entry2.getKey();
            IExpr value2 = entry2.getValue();
            res.setEntry(key1[0] - 1, key2[0] - 1, value1.multiply(value2));
          }
        }
        return res;
      }
      return null;
    }

    /**
     * Find the orthogonal projection of this vector onto another vector.
     *
     * @param fv field vector onto which {@code this} must be projected
     * @return projection of {@code this} onto {@code fv}
     * @throws MathIllegalArgumentException if {@code fv} is not the same size as {@code this}
     * @throws MathRuntimeException if {@code fv} is the null vector.
     */
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

    /**
     * Set all elements to a single value.
     *
     * @param value single value to set for all elements
     */
    @Override
    public void set(IExpr value) {
      MathUtils.checkNotNull(value);
      for (int i = 0; i < virtualSize; i++) {
        setEntry(i, value);
      }
    }

    /**
     * Set a single element.
     *
     * @param index element index.
     * @param value new value for the element.
     * @throws MathIllegalArgumentException if the index is not valid.
     * @see #getEntry(int)
     */
    @Override
    public void setEntry(int index, IExpr value) throws MathIllegalArgumentException {
      final Trie<int[], IExpr> trie = array.fData;
      final int[] key = new int[] {index + 1};
      if (value.equals(array.fDefaultValue)) {
        trie.remove(key);
      } else {
        trie.put(key, value);
      }
    }

    /**
     * Set a set of consecutive elements.
     *
     * @param index index of first element to be set.
     * @param v vector containing the values to set.
     * @throws MathIllegalArgumentException if the index is not valid.
     */
    @Override
    public void setSubVector(int index, FieldVector<IExpr> v) throws MathIllegalArgumentException {
      checkIndex(index);
      checkIndex(index + v.getDimension() - 1);
      final int n = v.getDimension();
      for (int i = 0; i < n; i++) {
        setEntry(i + index, v.getEntry(i));
      }
    }

    /**
     * Compute {@code this} minus {@code fv}.
     *
     * @param fv vector to be subtracted
     * @return {@code this - fv}
     * @throws MathIllegalArgumentException if {@code fv} is not the same size as {@code this}
     */
    @Override
    public SparseExprVector subtract(FieldVector<IExpr> fv) throws MathIllegalArgumentException {
      if (fv instanceof SparseExprVector) {
        SparseExprVector v = (SparseExprVector) fv;
        checkVectorDimensions(v.getDimension());

        IExpr newDefault = array.fDefaultValue.subtract(v.array.fDefaultValue);
        SparseExprVector res = new SparseExprVector(getDimension(), newDefault);

        // Subtract explicitly set elements in 'this'
        for (TrieNode<int[], IExpr> entry : array.fData.nodeSet()) {
          int[] key = entry.getKey();
          IExpr vVal = v.array.fData.get(key);
          vVal = vVal == null ? v.array.fDefaultValue : vVal;
          IExpr newVal = entry.getValue().subtract(vVal);

          if (!newVal.equals(newDefault)) {
            res.array.fData.put(key, newVal);
          }
        }

        // Subtract explicit elements from 'v' that were default in 'this'
        for (TrieNode<int[], IExpr> entry : v.array.fData.nodeSet()) {
          int[] key = entry.getKey();
          if (!array.fData.containsKey(key)) {
            IExpr newVal = array.fDefaultValue.subtract(entry.getValue());
            if (!newVal.equals(newDefault)) {
              res.array.fData.put(key, newVal);
            }
          }
        }
        return res;
      }

      // Fallback for dense subtraction
      SparseExprVector res = new SparseExprVector(getDimension(), array.fDefaultValue);
      for (int i = 0; i < getDimension(); i++) {
        res.setEntry(i, getEntry(i).subtract(fv.getEntry(i)));
      }
      return res;
    }

    /**
     * Convert the vector to a IExpr array.
     *
     * <p>
     * The array is independent from vector data, it's elements are copied.
     *
     * @return array containing a copy of vector elements
     */
    @Override
    public IExpr[] toArray() {
      IExpr[] res = new IExpr[virtualSize];
      for (int i = 0; i < res.length; i++) {
        res[i] = array.fDefaultValue;
      }
      Trie<int[], IExpr> trie = array.fData;
      for (TrieNode<int[], IExpr> entry : trie.nodeSet()) {
        res[entry.getKey()[0] - 1] = entry.getValue();
      }
      return res;
    }

    /**
     * Get a string representation for this vector.
     * 
     * @return a string representation for this vector
     */
    @Override
    public String toString() {
      final int dims = getDimension();
      final StringBuffer res = new StringBuffer();
      String fullClassName = getClass().getName();
      String shortClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
      res.append(shortClassName).append('{');
      for (int j = 0; j < dims; ++j) {
        if (j > 0) {
          res.append(',');
        }
        res.append(getEntry(j));
      }
      res.append('}');
      return res.toString();
    }
  }

  /**
   * Create array rules from the nested lists. From array rules a sparse array can be created.
   *
   * @param nestedListsOfValues
   */
  public static IAST arrayRules(IAST nestedListsOfValues, IExpr defaultValue) {
    int depth = SparseArrayExpr.depth(nestedListsOfValues, 1);
    if (depth < 0) {
      return F.NIL;
    }
    // default value rule is additionally appended at the end!
    IASTAppendable result =
        F.ListAlloc(F.allocMin32(F.allocLevel1(nestedListsOfValues, x -> x.isList()) + 2));
    IASTMutable positions = F.constantArray(F.C1, depth);
    if (SparseArrayExpr.arrayRulesRecursive(nestedListsOfValues, depth + 1, depth, positions,
        defaultValue, result)) {
      result.append(F.Rule(F.constantArray(F.$b(), depth), defaultValue));
      return result;
    }
    return F.NIL;
  }

  private static boolean arrayRulesRecursive(IAST nestedListsOfValues, int count, int level,
      IASTMutable positions, IExpr defaultValue, IASTAppendable result) {
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
        if (!arg.isList()
            || !arrayRulesRecursive((IAST) arg, count, level, positions, defaultValue, result)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Add values to the <code>trie</code> according to the pattern matching rule <code>
   * ruleLHSPositionsList -> ruleRHS</code>. Returns <code>true</code> if <code>ruleLHSPositionsList
   * </code> is a list with correct length.
   *
   * @param trie the internal trie structure of the sparse array
   * @param ruleLHSPositionsList
   * @param ruleRHS
   * @param dimension the dimension of the sparse array
   * @param defaultValue <code>defaultValue[0]</code> may be set by this method, if not present.
   * @param arrayRulesList parameter for error message handling
   * @param engine
   * @return
   */
  private static boolean checkPatternPositions(final Trie<int[], IExpr> trie,
      IAST ruleLHSPositionsList, IExpr ruleRHS, int[] dimension, IExpr[] defaultValue,
      IAST arrayRulesList, EvalEngine engine) {
    final int depth = dimension.length;
    if (ruleLHSPositionsList.forAll(x -> x.isBlank())) {
      if (defaultValue[0].isNIL()) {
        defaultValue[0] = ruleRHS;
        return true;
      } else if (!defaultValue[0].equals(ruleRHS)) {
        // The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
        Errors.printMessage(S.SparseArray, "posr",
            F.list(arrayRulesList, ruleLHSPositionsList, F.ZZ(depth)), engine);
        return false;
      }
    } else {
      if (ruleLHSPositionsList.argSize() == depth) {
        // pattern matching
        if (!patternPositionsList(trie, ruleLHSPositionsList, ruleRHS, dimension, arrayRulesList,
            engine)) {
          return false;
        }
      } else {
        // The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
        Errors.printMessage(S.SparseArray, "posr",
            F.list(arrayRulesList, ruleLHSPositionsList, F.ZZ(depth)), engine);
        return false;
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
          if (F.isNotPresent(intValue)) {
            return null;
          }
          if (intValue <= 0) {
            return null;
          }
          result[i - 1] = intValue;
        }
        return result;
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        Errors.printMessage(S.SparseArray, rex, engine);
      }
    }
    return null;
  }

  private static int[] createTrie(IAST arrayRulesList, final Trie<int[], IExpr> trie,
      int[] dimension, int defaultDimension, IExpr[] defaultValue, EvalEngine engine) {
    boolean determineDimension = defaultDimension < 0 || dimension == null;

    int[] positions = null;
    int depth = 1;
    if (dimension != null) {
      depth = dimension.length;
    }
    if (arrayRulesList.isNonEmptyList()) {
      IExpr arg1 = arrayRulesList.arg1();
      IAST rule1 = (IAST) arg1;
      if (rule1.arg1().isList()) {
        IAST positionList = (IAST) rule1.arg1();
        if (dimension == null) {
          depth = positionList.argSize();
          dimension = new int[depth];
        } else {
          if (dimension.length != positionList.argSize()) {
            // `1` is not a valid dimension specification for `2`.
            Errors.printMessage(S.SparseArray, "dimss", F.list(F.ZZ(dimension.length), rule1),
                engine);
            return null;
          }
          depth = dimension.length;
        }
        positions = checkPositions(arrayRulesList, positionList, engine);
        if (positions == null) {
          if (!checkPatternPositions(trie, positionList, rule1.arg2(), dimension, defaultValue,
              arrayRulesList, engine)) {
            return null;
          }
        }
      } else {
        int n = rule1.arg1().toIntDefault();
        if (n > 0) {
          if (dimension == null) {
            depth = 1;
            dimension = new int[depth];
          } else {
            if (dimension.length != 1) {
              // `1` is not a valid dimension specification for `2`.
              Errors.printMessage(S.SparseArray, "dimss", F.list(F.ZZ(dimension.length), rule1),
                  engine);
              return null;
            }
            depth = 1;
          }
          positions = new int[1];
          positions[0] = n;
        } else {
          if (rule1.arg1().isBlank()) {
            // pattern matching
            if (defaultValue[0].isNIL()) {
              defaultValue[0] = rule1.arg2();
            } else if (!defaultValue[0].equals(rule1.arg2())) {
              // The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
              Errors.printMessage(S.SparseArray, "posr",
                  F.list(arrayRulesList, rule1.arg1(), F.ZZ(depth)), engine);
              return null;
            }
          } else if (!patternPositionsList(trie, rule1.arg1(), rule1.arg2(), dimension,
              arrayRulesList, engine)) {
            return null;
          }
        }
      }

      if (positions != null) {
        if (defaultDimension > 0) {
          for (int i = 0; i < depth; i++) {
            dimension[i] = defaultDimension;
          }
        } else {
          if (determineDimension) {
            for (int i = 0; i < depth; i++) {
              if (positions[i] > dimension[i]) {
                dimension[i] = positions[i];
              }
            }
          }
        }
        trie.put(positions, rule1.arg2());
      }
    }
    for (int j = 2; j < arrayRulesList.size(); j++) {
      IExpr arg = arrayRulesList.get(j);
      if (arg.isRuleAST()) {
        IAST rule = (IAST) arg;
        if (rule.arg1().isList()) {
          IAST positionList = (IAST) rule.arg1();
          positions = checkPositions(arrayRulesList, positionList, engine);
          if (positions == null) {
            if (!checkPatternPositions(trie, positionList, rule.arg2(), dimension, defaultValue,
                arrayRulesList, engine)) {
              return null;
            }
          } else {
            if (positions.length != depth) {
              // The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
              Errors.printMessage(S.SparseArray, "posr",
                  F.list(arrayRulesList, rule.arg1(), F.ZZ(depth)), engine);
              return null;
            }
            if (determineDimension) {
              for (int i = 0; i < depth; i++) {
                if (positions[i] > dimension[i]) {
                  dimension[i] = positions[i];
                }
              }
            }
            trie.putIfAbsent(positions, rule.arg2());
          }
        } else {
          int n = rule.arg1().toIntDefault();
          if (n > 0) {
            positions = new int[1];
            positions[0] = n;
            if (determineDimension) {
              if (n > dimension[0]) {
                dimension[0] = n;
              }
            }
            trie.putIfAbsent(positions, rule.arg2());
          } else {
            if (rule.arg1().isBlank()) {
              // pattern matching
              if (defaultValue[0].isNIL()) {
                defaultValue[0] = rule.arg2();
              } else if (!defaultValue[0].equals(rule.arg2())) {
                // The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
                Errors.printMessage(S.SparseArray, "posr",
                    F.list(arrayRulesList, rule.arg1(), F.ZZ(depth)), engine);
                return null;
              }
            } else if (!patternPositionsList(trie, rule.arg1(), rule.arg2(), dimension,
                arrayRulesList, engine)) {
              return null;
            }
          }
        }
      }
    }
    return dimension;
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

  private static void incrementKey(int[] key, int[] bounds) {
    for (int i = key.length - 1; i >= 0; i--) {
      if (key[i] < bounds[i]) {
        key[i]++;
        break;
      } else {
        key[i] = 1;
      }
    }
  }

  private static boolean isWithinBounds(int[] key, int[] bounds) {
    for (int i = 0; i < key.length; i++) {
      if (key[i] > bounds[i])
        return false;
    }
    return true;
  }

  /**
   * Create a sparse array from the array rules list.
   *
   * @param arrayRulesList
   * @param dimension
   * @param defaultDimension
   * @param defaultValue default value for positions not specified in arrayRulesList. If <code>F.NIL
   *     </code> determine from '_' (Blank) rule.
   * @return <code>null</code> if a new <code>SparseArrayExpr</code> cannot be created.
   */
  public static SparseArrayExpr newArrayRules(IAST arrayRulesList, int[] dimension,
      int defaultDimension, IExpr defaultValue) {
    IExpr[] defValue = new IExpr[] {defaultValue};
    final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
    int[] determinedDimension =
        createTrie(arrayRulesList, trie, dimension, defaultDimension, defValue, EvalEngine.get());
    if (determinedDimension == null) {
      determinedDimension = dimension;
    }
    if (determinedDimension != null) {
      defaultValue = defValue[0].orElse(F.C0);
      removeValue(trie, defaultValue);
      return new SparseArrayExpr(trie, determinedDimension, defaultValue, false);
    }
    return null;
  }

  /**
   * Create a sparse array from the dense list representation (for example dense vectors and
   * matrices)
   *
   * @param denseList
   * @param defaultValue default value for positions not specified in the dense list representation.
   * @return <code>null</code> if a new <code>SparseArrayExpr</code> cannot be created.
   */
  public static SparseArrayExpr newDenseList(IAST denseList, IExpr defaultValue) {
    IntList dims = LinearAlgebraUtil.dimensions(denseList);
    int dimsSize = dims.size();
    if (dimsSize > 0) {
      defaultValue = defaultValue.orElse(F.C0);
      IAST listOfRules = SparseArrayExpr.arrayRules(denseList, defaultValue);
      if (listOfRules.isPresent()) {
        SparseArrayExpr result = SparseArrayExpr.newArrayRules(listOfRules, null, -1, defaultValue);
        int[] dimension = new int[dimsSize];
        for (int i = 0; i < dimsSize; i++) {
          dimension[i] = dims.getInt(i);
        }
        result.fDimension = dimension;
        return result;
      }
    }
    return null;
  }

  /**
   * Create a sparse array from the compressed sparse row (CSR) storage format (input form): <code>
   * SparseArray(Automatic, dimension, defaultValue, {1,{rowPointers, columnIndiceMatrix}, nonZeroValues})
   * </code>. See: <a href="http://netlib.org/utk/papers/templates/node91.html">Netlib - Compressed
   * Row Storage (CRS)</a>
   *
   * @param dimension the dimension of the sparse array
   * @param defaultValue default value for positions not specified in input form.
   * @param rowPointers
   * @param columnIndiceMatrix
   * @param nonZeroValues
   * @return <code>null</code> if a new <code>SparseArrayExpr</code> cannot be created.
   */
  public static SparseArrayExpr newInputForm(int[] dimension, IExpr defaultValue, int[] rowPointers,
      IAST columnIndiceMatrix, IAST nonZeroValues) {
    int first = 0;
    final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
    final int depth = dimension.length;
    int k = 0;
    for (int i = 1; i < columnIndiceMatrix.size(); i++) {
      IAST row = (IAST) columnIndiceMatrix.get(i);
      int[] key = new int[depth];
      if (depth == 1) {
        for (int j = 1; j < row.size(); j++) {
          int p = row.get(j).toIntDefault();
          if (p < 1) {
            return null;
          }
          key[0] = p;
        }
      } else {
        while (rowPointers[k] < i) {
          k++;
          first++;
        }
        key[0] = first;
        for (int j = 1; j < row.size(); j++) {
          int p = row.get(j).toIntDefault();
          if (p < 1) {
            return null;
          }
          key[j] = p;
        }
      }
      trie.put(key, nonZeroValues.get(i));
    }
    return new SparseArrayExpr(trie, dimension, defaultValue, false);
  }

  /**
   * Fill the sparse array by evaluating the rules left-hand-side as a pattern-matching expression.
   *
   * @param trie the internal trie structure of the sparse array
   * @param ruleLHS
   * @param ruleRHS
   * @param dimension the dimension of the sparse array
   * @param arrayRulesList parameter for error message handling
   * @param engine
   * @return
   */
  private static boolean patternPositionsList(final Trie<int[], IExpr> trie, IExpr ruleLHS,
      IExpr ruleRHS, int[] dimension, IAST arrayRulesList, EvalEngine engine) {
    if (dimension == null) {
      return false;
    }
    final int depth = dimension.length;
    PatternMatcherAndEvaluator matcher = new PatternMatcherAndEvaluator(ruleLHS, ruleRHS);
    if (matcher.isRuleWithoutPatterns()) {
      // The left hand side of `2` in `1` doesn't match an int-array of depth `3`.
      Errors.printMessage(S.SparseArray, "posr", F.list(arrayRulesList, ruleLHS, F.ZZ(depth)),
          EvalEngine.get());
      return false;
    }
    IASTMutable positionList = F.constantArray(F.C1, depth);
    int[] positionsKey = new int[depth];

    IPatternMap patternMap = matcher.getPatternMap();
    IExpr[] patternValuesArray = patternMap.copyPattern();

    patternPositionsRecursive(trie, dimension, engine, matcher, positionList, 0, positionsKey,
        patternMap, patternValuesArray);

    return true;
  }

  private static void patternPositionsRecursive(final Trie<int[], IExpr> trie, int[] dimension,
      EvalEngine engine, PatternMatcherAndEvaluator matcher, IASTMutable positionList, int pointer,
      int[] positionsKey, IPatternMap patternMap, IExpr[] patternValuesArray) {
    if (pointer == dimension.length) {
      try {
        IExpr result = matcher.eval(positionList, engine);
        if (result.isPresent()) {
          trie.putIfAbsent(positionsKey.clone(), result);
        }
      } finally {
        patternMap.resetPattern(patternValuesArray);
      }
    } else {
      for (int i = 1; i <= dimension[pointer]; i++) {
        positionsKey[pointer] = i;
        positionList.set(pointer + 1, F.ZZ(i));
        patternPositionsRecursive(trie, dimension, engine, matcher, positionList, pointer + 1,
            positionsKey, patternMap, patternValuesArray);
      }
    }
  }

  private static void removeValue(final Trie<int[], IExpr> trie, IExpr value) {
    for (TrieNode<int[], IExpr> entry : trie.nodeSet()) {
      if (value.equals(entry.getValue())) {
        trie.remove(entry.getKey());
      }
    }
  }

  /**
   * The maximum number of elements this sparse array could contain.
   *
   * @param dimension assumes dimension contains at least one element.
   * @return total size as a 64-bit long to prevent integer overflow
   */
  private static long totalSize(int[] dimension) {
    long total = 1;
    for (int i = 0; i < dimension.length; i++) {
      total *= dimension[i];
    }
    return total;
  }

  /** Flags for controlling evaluation and left-hand-side pattern-matching expressions */
  protected int fEvalFlags = 0;

  /** The dimension of the sparse array. */
  private int[] fDimension;

  /** The default value for the positions with no entry in the map. Usually <code>0</code>. */
  private IExpr fDefaultValue;

  /** Constructor for serialization. */
  public SparseArrayExpr() {
    super(S.SparseArray, null);
  }

  /**
   * Copy constructor. If a deep copy is required, the <code>trie</code> elements will be copied
   * into a new {@link Trie} and new allocated <code>dimension</code> array.
   *
   * <p>
   * See <a href="https://en.wikipedia.org/wiki/Trie">Wikipedia - Trie</a>.
   *
   * @param trie map positions of a sparse array to a value
   * @param dimension the dimensions of the positions
   * @param defaultValue default value for positions not specified in the trie
   * @param deepCopy if <code>true</code> create a deep copy.
   */
  public SparseArrayExpr(final Trie<int[], IExpr> trie, int[] dimension, IExpr defaultValue,
      boolean deepCopy) {
    super(S.SparseArray, trie);
    if (deepCopy) {
      this.fData = Config.TRIE_INT2EXPR_BUILDER.build();
      for (TrieNode<int[], IExpr> entry : trie.nodeSet()) {
        int[] key = entry.getKey();
        int[] newKey = new int[key.length];
        System.arraycopy(key, 0, newKey, 0, key.length);
        this.fData.put(newKey, entry.getValue());
      }
      this.fDimension = new int[dimension.length];
      System.arraycopy(dimension, 0, this.fDimension, 0, dimension.length);
    } else {
      this.fDimension = dimension;
    }
    // this.addEvalFlags(IAST.SEQUENCE_FLATTENED);
    this.fDefaultValue = defaultValue;
  }

  /** {@inheritDoc} */
  @Override
  public final ISparseArray addEvalFlags(final int i) {
    fEvalFlags |= i;
    return this;
  }

  @Override
  public IAST arrayRules() {
    return arrayRules(fDefaultValue);
  }

  /** Convert this sparse array to array rules list format. */
  @Override
  public IAST arrayRules(IExpr defaultValue) {
    IASTAppendable result = F.ListAlloc(fData.size() + 1);

    for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
      int[] key = entry.getKey();
      IExpr value = entry.getValue();
      if (!value.equals(defaultValue)) {
        IAST lhs = F.ast(S.List, key);
        result.append(F.Rule(lhs, value));
      }
    }
    result.append(F.Rule(F.constantArray(F.$b(), fDimension.length), defaultValue));
    return result;
  }

  @Override
  public SparseArrayExpr copy() {
    return new SparseArrayExpr(fData, fDimension, fDefaultValue, true);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof SparseArrayExpr) {
      SparseArrayExpr s = (SparseArrayExpr) obj;
      if (Arrays.equals(fDimension, s.fDimension) && fDefaultValue.equals(s.fDefaultValue)) {
        Trie<int[], IExpr> trie = s.fData;
        if (fData.size() == trie.size()) {
          for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
            int[] sequence = entry.getKey();
            IExpr sValue = trie.get(sequence);
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

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (isEvalFlagOff(IAST.BUILT_IN_EVALED)) {
      boolean containsNumericArg = false;
      boolean evaled = false;
      IExpr newDefaultValue = fDefaultValue;
      IExpr temp = engine.evaluateNIL(fDefaultValue);
      if (temp.isPresent()) {
        evaled = true;
        if (temp.isNumericArgument(true)) {
          containsNumericArg = true;
        }
        newDefaultValue = temp;
      } else if (fDefaultValue.isNumericArgument(true)) {
        containsNumericArg = true;
      }
      final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
      for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
        IExpr value = entry.getValue();
        temp = engine.evaluateNIL(value);
        if (temp.isPresent()) {
          evaled = true;
          if (temp.isNumericArgument(true)) {
            containsNumericArg = true;
          }
          if (!temp.equals(newDefaultValue)) {
            trie.put(entry.getKey(), temp);
          }
        } else {
          if (value.isNumericArgument(true)) {
            containsNumericArg = true;
          }
          if (!value.equals(newDefaultValue)) {
            trie.put(entry.getKey(), value);
          }
        }
      }
      if (evaled) {
        SparseArrayExpr result = new SparseArrayExpr(trie, fDimension, newDefaultValue, false);
        if (containsNumericArg) {
          result.addEvalFlags(IAST.BUILT_IN_EVALED | IAST.CONTAINS_NUMERIC_ARG);
        } else {
          result.addEvalFlags(IAST.BUILT_IN_EVALED);
        }
        return result;
      }
      if (containsNumericArg) {
        addEvalFlags(IAST.BUILT_IN_EVALED | IAST.CONTAINS_NUMERIC_ARG);
      } else {
        addEvalFlags(IAST.BUILT_IN_EVALED);
      }
    }
    return F.NIL;
  }

  @Override
  public boolean exists(Predicate<? super IExpr> predicate) {
    if (predicate.test(fDefaultValue)) {
      return true;
    }
    for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
      IExpr value = entry.getValue();
      if (predicate.test(value)) {
        return true;
      }
    }
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr first() {
    return get(1);
  }

  @Override
  public ISparseArray flatten() {
    if (fDimension.length <= 1) {
      return this;
    }

    long vectorDimLong = totalSize(fDimension);
    if (vectorDimLong > Integer.MAX_VALUE) {
      // Cannot be represented as a 1D SparseArrayExpr because dimensions are int[]
      throw new ArithmeticException("Flattened sparse array dimension exceeds Integer.MAX_VALUE");
    }
    int vectorDim = (int) vectorDimLong;

    // Use long for offset calculations to prevent multiplication overflow
    long[] offsets = new long[fDimension.length];
    long val = fDimension[fDimension.length - 1];

    for (int i = offsets.length - 1; i >= 0; i--) {
      offsets[i] = val;
      if (i > 0) {
        val *= fDimension[i - 1];
      }
    }

    final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
    for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
      int[] key = entry.getKey();
      long keyDim = key[key.length - 1];
      for (int i = 0; i < key.length - 1; i++) {
        keyDim += (key[i] - 1) * offsets[i + 1];
      }
      trie.put(new int[] {(int) keyDim}, entry.getValue());
    }

    return new SparseArrayExpr(trie, new int[] {vectorDim}, fDefaultValue, false);
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAll(Predicate<? super IExpr> predicate) {
    long counter = fData.size();
    if (totalSize(fDimension) > counter) {
      if (!predicate.test(fDefaultValue)) {
        return false;
      }
    }
    for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
      IExpr value = entry.getValue();
      if (!predicate.test(value)) {
        return false;
      }
    }
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean forAllLeaves(Predicate<? super IExpr> predicate) {
    return forAll(predicate);
  }

  public IASTAppendable fullForm() {
    IAST dimensionList = F.ast(S.List, fDimension);
    IASTAppendable result = F.ast(S.SparseArray, 6);
    result.append(S.Automatic);
    result.append(dimensionList);
    result.append(fDefaultValue);
    // create compressed sparse row (CSR) storage format (input form):
    // SparseArray(Automatic, dimension, defaultValue, {1,{rowPointers, columnIndiceMatrix},
    // nonZeroValues})
    IASTAppendable list1 = F.ListAlloc(4);
    result.append(list1);
    list1.append(F.C1);

    IASTAppendable subList = F.ListAlloc(2);
    list1.append(subList);
    IASTAppendable rowPointers = F.ListAlloc(fData.size());
    subList.append(rowPointers);
    IASTAppendable columnIndiceMatrix = F.ListAlloc(fData.size());
    subList.append(columnIndiceMatrix);
    IASTAppendable nonZeroValues = F.ListAlloc(fData.size());

    int columnIndex = 0;
    int rowCounter = 0;
    if (fDimension.length > 1) {
      // matrix, general case
      for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
        int[] key = entry.getKey();
        int row = key[0];
        while (rowCounter < row) {
          rowPointers.append(columnIndex);
          rowCounter++;
        }
        columnIndex++;
        int[] newKey = new int[key.length - 1];
        System.arraycopy(key, 1, newKey, 0, newKey.length);
        IAST indice = F.ast(S.List, newKey);
        columnIndiceMatrix.append(indice);
        nonZeroValues.append(entry.getValue());
      }
    } else {
      // vector case
      rowPointers.append(columnIndex);
      for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
        int[] key = entry.getKey();
        columnIndex++;
        IAST indice = F.ast(S.List, key);
        columnIndiceMatrix.append(indice);
        nonZeroValues.append(entry.getValue());
      }
    }
    rowPointers.append(columnIndex);
    list1.append(nonZeroValues);
    return result;
  }

  /**
   * For the FullForm create a compressed sparse row (CSR) representation internally: <code>
   * SparseArray(Automatic, dimension, defaultValue, {1,{rowPointers, columnIndiceMatrix}, nonZeroValues})
   * </code>. See: <a href="http://netlib.org/utk/papers/templates/node91.html">Netlib - Compressed
   * Row Storage (CRS)</a>
   */
  @Override
  public String fullFormString() {
    IASTAppendable result = fullForm();
    return result.fullFormString();
  }

  @Override
  public IExpr get(int position) {
    if (position == 0) {
      return head();
    }
    if (position < 0 || position > fDimension[0]) {
      throw new IndexOutOfBoundsException("Index: " + position + ", Size: " + size());
    }

    int[] dims = getDimension();
    final int partSize = 1;

    int len = 0;
    int[] partIndex = new int[dims.length];
    int count = 0;
    partIndex[0] = position;

    for (int i = partSize; i < dims.length; i++) {
      partIndex[i] = -1;
      count++;
    }
    if (count == 0 && partSize == dims.length) {
      return getIndex(partIndex);
    }
    int[] newDimension = new int[count];
    count = 0;
    for (int i = 0; i < partIndex.length; i++) {
      if (partIndex[i] == (-1)) {
        len++;
        newDimension[count++] = dims[i];
      }
    }
    final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
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
        trie.put(newKey, entry.getValue());
      }
    }
    return new SparseArrayExpr(trie, newDimension, fDefaultValue.orElse(F.C0), false);
  }

  @Override
  public IExpr getDefaultValue() {
    return fDefaultValue;
  }

  @Override
  public int[] getDimension() {
    return fDimension;
  }

  /**
   * Determine the value for the given <code>index</code>. Return the value stored at the
   * <code>index</code> position or otherwise the default value of this sparse array.
   *
   * @param index
   * @return the default value if no element is stored for the index in the internal map.
   */
  @Override
  public IExpr getIndex(int... index) {
    IExpr expr = fData.get(index);
    if (expr == null) {
      return fDefaultValue;
    }
    return expr;
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
        IExpr arg = ast.get(i);
        if (arg.equals(S.All) || arg.isBlank()) {
          partIndex[i - startPosition] = -1; // safe internal wildcard marker
          count++;
        } else {
          int idx = arg.toIntDefault(Integer.MIN_VALUE);
          if (idx == Integer.MIN_VALUE) {
            return Errors.printMessage(S.Part, "partd", F.list(ast), EvalEngine.get());
          }
          if (idx < 0) {
            idx += dims[i - startPosition] + 1; // resolve negative index to tail
          }
          if (idx > dims[i - startPosition] || idx <= 0) {
            return Errors.printMessage(S.Part, "partw", F.list(arg, ast), EvalEngine.get());
          }
          partIndex[i - startPosition] = idx;
        }
      }
      for (int i = partSize; i < dims.length; i++) {
        partIndex[i] = -1;
        count++;
      }
      if (count == 0 && partSize == dims.length) {
        return getIndex(partIndex);
      }
      int[] newDimension = new int[count];
      count = 0;
      for (int i = 0; i < partIndex.length; i++) {
        if (partIndex[i] == (-1)) {
          len++;
          newDimension[count++] = dims[i];
        }
      }
      final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
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
          trie.put(newKey, entry.getValue());
        }
      }
      return new SparseArrayExpr(trie, newDimension, fDefaultValue.orElse(F.C0), false);
    }
    return Errors.printMessage(S.Part, "partd", F.list(ast), EvalEngine.get());
  }

  @Override
  public IExpr getPart(final int... partIndex) {
    int[] dims = getDimension();

    final int partSize = partIndex.length;
    if (dims.length >= partSize) {

      int len = 0;
      int count = 0;
      for (int i = partSize; i < dims.length; i++) {
        partIndex[i] = -1;
        count++;
      }
      if (count == 0 && partSize == dims.length) {
        return getIndex(partIndex);
      }
      int[] newDimension = new int[count];
      count = 0;
      for (int i = 0; i < partIndex.length; i++) {
        if (partIndex[i] == (-1)) {
          len++;
          newDimension[count++] = dims[i];
        }
      }
      final Trie<int[], IExpr> trie = Config.TRIE_INT2EXPR_BUILDER.build();
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
          trie.put(newKey, entry.getValue());
        }
      }
      return new SparseArrayExpr(trie, newDimension, fDefaultValue.orElse(F.C0), false);
    }
    return F.NIL;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 541 : 541 + fData.size() + fDefaultValue.hashCode();
  }

  @Override
  public int hierarchy() {
    return SPARSEARRAYID;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean isEvalFlagOn(final int i) {
    return (fEvalFlags & i) == i;
  }

  /** {@inheritDoc} */
  @Override
  public int isInexactVector() {
    int result = isVector();
    if (result >= 0) {
      if (isEvalFlagOn(IAST.CONTAINS_NUMERIC_ARG)) {
        return result;
      }
      if (exists(x -> x.isInexactNumber())) {
        return result;
      }
    }
    return -1;
  }

  @Override
  public int[] isMatrix(boolean setMatrixFormat) {
    return (fDimension.length == 2) ? fDimension : null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumericArgument(boolean allowList) {
    if (allowList) {
      if (isEvalFlagOn(IAST.CONTAINS_NUMERIC_ARG)) {
        return true;
      }
      return exists(x -> x.isNumericArgument(true));
    }
    return false;
  }

  @Override
  public boolean isSparseArray() {
    return true;
  }

  @Override
  public int isVector() {
    return (fDimension.length == 1) ? fDimension[0] : -1;
  }

  @Override
  public ISparseArray join(ISparseArray that) {
    SparseArrayExpr thatArray = (SparseArrayExpr) that;
    SparseArrayExpr result = copy();
    int rowDimension = result.fDimension[0];
    for (TrieNode<int[], IExpr> entry : thatArray.fData.nodeSet()) {
      int[] key = entry.getKey();
      int[] newKey = new int[key.length];
      System.arraycopy(key, 0, newKey, 0, key.length);
      newKey[0] += rowDimension;
      result.fData.put(newKey, entry.getValue());
    }
    result.fDimension[0] += thatArray.fDimension[0];
    return result;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr last() {
    return get(fDimension[0]);
  }

  /** {@inheritDoc} */
  @Override
  public SparseArrayExpr map(final Function<IExpr, IExpr> function) {
    IExpr newDefault = function.apply(fDefaultValue);
    newDefault = newDefault.isPresent() ? newDefault : fDefaultValue;

    Trie<int[], IExpr> newTrie = Config.TRIE_INT2EXPR_BUILDER.build();
    for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
      IExpr value = entry.getValue();
      IExpr temp = function.apply(value);
      temp = temp.isPresent() ? temp : value;
      // Guarantee elements matching the new default are cleared out
      if (!temp.equals(newDefault)) {
        newTrie.put(entry.getKey(), temp);
      }
    }

    return new SparseArrayExpr(newTrie, fDimension, newDefault, false);
  }

  /**
   * This method assumes that <code>this</code> is a list of lists in matrix form. It combines the
   * column values in a list as argument for the given <code>function</code>. <b>Example</b> a
   * matrix <code>{{x1, y1,...}, {x2, y2, ...}, ...}</code> will be converted to <code>
   * {f.apply({x1, x2,...}), f.apply({y1, y2, ...}), ...}</code>
   *
   * @param dim the dimension of the matrix
   * @param f a unary function
   */
  @Override
  public IExpr mapMatrixColumns(int[] dim, Function<IExpr, IExpr> f) {
    // Fallback for non-2D matrices
    if (fDimension.length != 2 || dim.length != 2) {
      IAST matrix = normal(false);
      return matrix.mapMatrixColumns(dim, f);
    }

    int rows = fDimension[0];
    int cols = fDimension[1];

    // Group the sparse entries by column index
    // key: column index (1-based), value: Trie of (row -> value)
    java.util.Map<Integer, Trie<int[], IExpr>> columnTries = new java.util.HashMap<>();
    for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
      int[] key = entry.getKey();
      if (key.length == 2) {
        int row = key[0];
        int col = key[1];
        Trie<int[], IExpr> colTrie =
            columnTries.computeIfAbsent(col, k -> Config.TRIE_INT2EXPR_BUILDER.build());
        colTrie.put(new int[] {row}, entry.getValue());
      }
    }

    int[] colDim = new int[] {rows};

    // Evaluate the default column once to determine the new default value
    SparseArrayExpr defaultCol =
        new SparseArrayExpr(Config.TRIE_INT2EXPR_BUILDER.build(), colDim, fDefaultValue, false);
    IExpr defaultResult = f.apply(defaultCol.normal(false));
    if (defaultResult == null) {
      defaultResult = F.NIL; //
    }

    // Build the resulting 1D Sparse Array
    Trie<int[], IExpr> resultTrie = Config.TRIE_INT2EXPR_BUILDER.build();

    for (java.util.Map.Entry<Integer, Trie<int[], IExpr>> entry : columnTries.entrySet()) {
      int col = entry.getKey();
      SparseArrayExpr colExpr = new SparseArrayExpr(entry.getValue(), colDim, fDefaultValue, false);

      IExpr colResult = f.apply(colExpr.normal(false));
      if (colResult == null) {
        colResult = F.NIL; //
      }

      if (!colResult.equals(defaultResult)) {
        resultTrie.put(new int[] {col}, colResult);
      }
    }

    return new SparseArrayExpr(resultTrie, new int[] {cols}, defaultResult, false);
  }

  /** {@inheritDoc} */
  @Override
  public final SparseArrayExpr mapThreadSparse(final IAST replacement, int position) {
    final Function<IExpr, IExpr> function = x -> replacement.setAtCopy(position, x);
    return map(function);
  }

  @Override
  public IASTMutable normal(boolean nilIfUnevaluated) {
    return normal(fDimension);
  }

  @Override
  public IASTMutable normal(int[] dimension) {
    if (dimension.length > 0) {
      IASTMutable result = Tensors.build(index -> getIndex(index), dimension);
      if (fDimension.length == 1) {
        result.addEvalFlags(IAST.IS_VECTOR);
      } else if (fDimension.length == 2) {
        result.addEvalFlags(IAST.IS_MATRIX);
      }
      return result;
    }
    return F.headAST0(S.List);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fDefaultValue = (IExpr) in.readObject();
    final int len = in.readInt();
    fDimension = new int[len];
    for (int i = 0; i < len; i++) {
      fDimension[i] = in.readInt();
    }
    IAST arrayRulesList = (IAST) in.readObject();
    fData = Config.TRIE_INT2EXPR_BUILDER.build();
    IExpr[] defValue = new IExpr[] {fDefaultValue};
    int[] determinedDimension =
        createTrie(arrayRulesList, fData, fDimension, -1, defValue, EvalEngine.get());
    if (determinedDimension == null) {
      throw new java.io.InvalidClassException("no valid Trie creation");
    }
  }

  /**
   * Resizes the sparse array natively, avoiding dense list allocations. Handles changes in both
   * dimensions and default values. * @param targetDimension the new dimensions, or null to keep the
   * current dimensions
   * 
   * @param targetDefaultValue the new default value, or F.NIL to keep the current default value
   * @return a natively resized SparseArrayExpr
   */
  public SparseArrayExpr resize(int[] targetDimension, IExpr targetDefaultValue) {
    if (targetDimension == null) {
      targetDimension = this.fDimension;
    }
    if (targetDefaultValue == null || targetDefaultValue.isNIL()) {
      targetDefaultValue = this.fDefaultValue;
    }

    boolean sameDefault = targetDefaultValue.equals(this.fDefaultValue);
    boolean sameDimension = Arrays.equals(targetDimension, this.fDimension);

    if (sameDefault && sameDimension) {
      return this; // No changes needed
    }

    boolean sameRank = targetDimension.length == this.fDimension.length;
    Trie<int[], IExpr> newTrie = Config.TRIE_INT2EXPR_BUILDER.build();

    if (sameDefault) {
      // Fast path: Only dimensions changed, default is the same.
      // We rapidly filter the existing trie elements.
      if (sameRank) {
        for (TrieNode<int[], IExpr> entry : this.fData.nodeSet()) {
          int[] key = entry.getKey();
          if (isWithinBounds(key, targetDimension)) {
            newTrie.put(key.clone(), entry.getValue());
          }
        }
      }
    } else {
      // Slow path: Default value changed.
      // Elements within the intersection of old and new dimensions that were implicitly
      // the old default value must now be explicitly stored if they differ from the new default.
      if (sameRank) {
        long intersectionSize = 1;
        int[] minBounds = new int[targetDimension.length];
        for (int i = 0; i < targetDimension.length; i++) {
          minBounds[i] = Math.min(this.fDimension[i], targetDimension[i]);
          intersectionSize *= minBounds[i];
        }

        // Safety limit to prevent memory exhaustion when iterating the bounding box
        if (intersectionSize > Config.MAX_AST_SIZE) {
          org.matheclipse.core.eval.exception.ASTElementLimitExceeded
              .throwIt((int) intersectionSize);
        }

        int[] currentKey = new int[targetDimension.length];
        Arrays.fill(currentKey, 1);

        for (long i = 0; i < intersectionSize; i++) {
          IExpr val = this.fData.get(currentKey);
          if (val != null) {
            if (!val.equals(targetDefaultValue)) {
              newTrie.put(currentKey.clone(), val);
            }
          } else {
            if (!this.fDefaultValue.equals(targetDefaultValue)) {
              newTrie.put(currentKey.clone(), this.fDefaultValue);
            }
          }
          incrementKey(currentKey, minBounds);
        }
      }
    }

    return new SparseArrayExpr(newTrie, targetDimension, targetDefaultValue, false);
  }

  public IExpr set(int i, IExpr value) {
    if (fDimension.length == 1 && i > 0 && i <= fDimension[0]) {
      int[] positions = new int[] {i};
      IExpr old = fData.get(positions);
      if (value.equals(fDefaultValue)) {
        fData.remove(positions);
      } else {
        fData.put(positions, value);
      }
      return (old == null) ? fDefaultValue : old;
    }
    throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + fDimension[0]);
  }

  @Override
  public IExpr set(int[] positions, IExpr value) {
    if (positions.length == fDimension.length) {
      for (int i = 0; i < positions.length; i++) {
        if (positions[i] <= 0 || positions[i] > fDimension[i]) {
          throw new IndexOutOfBoundsException(
              "Index: " + i + " Position: " + positions[i] + ", Size: " + fDimension[i]);
        }
      }
      IExpr old = fData.get(positions);
      if (value.equals(fDefaultValue)) {
        fData.remove(positions);
      } else {
        fData.put(positions, value);
      }
      return (old == null) ? fDefaultValue : old;
    }
    throw new IndexOutOfBoundsException(
        "Indeces: " + Arrays.toString(positions) + ", Size: " + fDimension[0]);
  }

  @Override
  public int size() {
    return fDimension[0] + 1;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr subList(int startPosition, int endPosition) {
    if (fDimension.length > 0 && startPosition >= 1 && endPosition <= fDimension[0] + 1
        && startPosition < endPosition) {
      SparseArrayExpr result = new SparseArrayExpr();
      result.fDefaultValue = fDefaultValue;
      Trie<int[], IExpr> newTrie = Config.TRIE_INT2EXPR_BUILDER.build();
      result.fData = newTrie;
      result.fDimension = new int[fDimension.length];
      System.arraycopy(fDimension, 0, result.fDimension, 0, fDimension.length);
      result.fDimension[0] = endPosition - startPosition;

      for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
        int[] oldKey = entry.getKey();
        int oldPos = oldKey[0];

        if (oldPos >= startPosition && oldPos < endPosition) {
          int newPos = oldPos - startPosition + 1;
          int[] newKey = Arrays.copyOf(oldKey, oldKey.length);
          newKey[0] = newPos;
          newTrie.put(newKey, entry.getValue());
        }
      }
      return result;
    }
    if (fDimension.length >= 1 && fDimension[0] == 1 && startPosition == endPosition) {
      return F.CEmptyList;
    }
    return super.subList(startPosition, endPosition);
  }

  /** {@inheritDoc} */
  @Override
  public double[][] toDoubleMatrix(boolean setMatrixFormat) {
    if (fDimension.length == 2 && fDimension[0] > 0 && fDimension[1] > 0) {
      IExpr value = null;
      try {
        double[][] result = new double[fDimension[0]][fDimension[1]];
        if (!fDefaultValue.isZero()) {
          double d = fDefaultValue.evalf();
          for (int i = 0; i < fDimension[0]; i++) {
            for (int j = 0; j < fDimension[1]; j++) {
              result[i][j] = d;
            }
          }
        }
        for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
          int[] key = entry.getKey();
          value = entry.getValue();
          result[key[0] - 1][key[1] - 1] = value.evalf();
        }
        return result;
      } catch (ArgumentTypeException rex) {
        if (value != null && value.isIndeterminate()) {
          // Input matrix contains an indeterminate entry.
          Errors.printMessage(S.SparseArray, "mindet", F.List());
          return null;
        }
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public double[] toDoubleVector() {
    if (fDimension.length == 1 && fDimension[0] > 0) {
      IExpr value = null;
      try {
        double[] result = new double[fDimension[0]];
        if (!fDefaultValue.isZero()) {
          double d = fDefaultValue.evalf();
          for (int i = 0; i < result.length; i++) {
            result[i] = d;
          }
        }
        for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
          int[] key = entry.getKey();
          value = entry.getValue();
          result[key[0] - 1] = value.evalf();
        }
        return result;
      } catch (ArgumentTypeException rex) {
        if (value != null && value.isIndeterminate()) {
          // Input matrix contains an indeterminate entry.
          Errors.printMessage(S.SparseArray, "mindet", F.List());
          return null;
        }
      }
    }
    return null;
  }

  @Override
  public FieldMatrix<IExpr> toFieldMatrix(boolean copyArray) {
    if (fDimension.length == 2 && fDimension[0] > 0 && fDimension[1] > 0) {
      return new SparseExprMatrix(this, copyArray);
    }
    return null;
  }

  @Override
  public FieldVector<IExpr> toFieldVector(boolean copyArray) {
    if (fDimension.length == 1 && fDimension[0] > 0) {
      return new SparseExprVector(this, copyArray);
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public RealMatrix toRealMatrix() {
    if (fDimension.length == 2 && fDimension[0] > 0 && fDimension[1] > 0) {
      IExpr value = null;
      try {
        OpenMapRealMatrix result = new OpenMapRealMatrix(fDimension[0], fDimension[1]);
        if (!fDefaultValue.isZero()) {
          double d = fDefaultValue.evalf();
          for (int i = 0; i < fDimension[0]; i++) {
            for (int j = 0; j < fDimension[1]; j++) {
              result.setEntry(i, j, d);
            }
          }
        }
        for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
          int[] key = entry.getKey();
          value = entry.getValue();
          result.setEntry(key[0] - 1, key[1] - 1, value.evalf());
        }
        return result;
      } catch (ArgumentTypeException rex) {
        if (value != null && value.isIndeterminate()) {
          // Input matrix contains an indeterminate entry.
          Errors.printMessage(S.SparseArray, "mindet", F.List());
          return null;
        }
      }
    }
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public RealVector toRealVector() {
    if (fDimension.length == 1 && fDimension[0] > 0) {
      IExpr value = null;
      try {
        OpenMapRealVector result = new OpenMapRealVector(fDimension[0]);
        if (!fDefaultValue.isZero()) {
          double d = fDefaultValue.evalf();
          for (int i = 0; i < fDimension[0]; i++) {
            result.setEntry(i, d);
          }
        }
        for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
          int[] key = entry.getKey();
          value = entry.getValue();
          result.setEntry(key[0] - 1, value.evalf());
        }
        return result;
      } catch (ArgumentTypeException rex) {
        if (value != null && value.isIndeterminate()) {
          // Input matrix contains an indeterminate entry.
          Errors.printMessage(S.SparseArray, "mindet", F.List());
          return null;
        }
      }
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("SparseArray(Number of elements: ");
    buf.append(fData.size());
    buf.append(" Dimensions: {");
    for (int i = 0; i < fDimension.length; i++) {
      buf.append(fDimension[i]);
      if (i < fDimension.length - 1) {
        buf.append(",");
      }
    }
    buf.append("} Default value: ");
    buf.append(fDefaultValue.toString());
    buf.append(")");
    return buf.toString();
  }

  @Override
  public IExpr total(IExpr head) {
    long totalElements = totalSize(fDimension);

    if (head.equals(S.Plus)) {
      // Fast path: O(k) mathematical summation for ANY default value
      IASTAppendable plus = F.PlusAlloc(fData.size());
      for (TrieNode<int[], IExpr> entry : fData.nodeSet()) {
        plus.append(entry.getValue());
      }
      IExpr explicitSum = EvalEngine.get().evaluate(plus.oneIdentity(F.C0));

      if (fDefaultValue.isZero()) {
        return explicitSum;
      } else {
        long defaultCount = totalElements - fData.size();
        IExpr defaultSum = EvalEngine.get().evaluate(F.Times(F.ZZ(defaultCount), fDefaultValue));
        return EvalEngine.get().evaluate(F.Plus(explicitSum, defaultSum));
      }
    }

    // Slow path: Building a full dense AST for non-Plus operations
    if (totalElements > Config.MAX_AST_SIZE) {
      org.matheclipse.core.eval.exception.ASTElementLimitExceeded.throwIt(Config.MAX_AST_SIZE);
    }

    IASTAppendable result = F.ast(head, (int) totalElements);
    return totalAppendable(result);
  }

  private IASTAppendable totalAppendable(IASTAppendable result) {
    int[] index = new int[fDimension.length];
    for (int i = 0; i < index.length; i++) {
      index[i] = 1;
    }
    totalRecursive(fData, fDimension, 0, index, result);
    return result;
  }

  private void totalRecursive(Trie<int[], IExpr> trie, int[] dimension, int position, int[] index,
      IASTAppendable result) {
    if (dimension.length - 1 == position) {
      int size = dimension[position];
      for (int i = 1; i <= size; i++) {
        index[position] = i;
        IExpr expr = trie.get(index);
        if (expr == null) {
          result.append(fDefaultValue);
        } else {
          result.append(expr);
        }
      }
      return;
    }
    int size1 = dimension[position];
    for (int i = 1; i <= size1; i++) {
      index[position] = i;
      totalRecursive(trie, dimension, position + 1, index, result);
    }
  }

  @Override
  public ISparseArray transpose(int[] permutation) {
    return transpose(permutation, x -> x);
  }

  @Override
  public ISparseArray transpose(final int[] permutation,
      Function<? super IExpr, ? extends IExpr> function) {
    int length = fDimension.length;
    for (int i = 0; i < permutation.length; i++) {
      if (permutation[i] > permutation.length || permutation[i] < 1) {
        return null;
      }
    }

    IAST permutationList = F.List(permutation);
    int[] dimensionsPermutated = CombinatoricUtil.permute(F.List(fDimension), permutationList);
    IAST range = IAST.range(fDimension.length + 1);
    int[] originalIndices = CombinatoricUtil.permute(range, permutationList);
    if (dimensionsPermutated == null || originalIndices == null) {
      return null;
    }

    final Trie<int[], IExpr> resultTrie = Config.TRIE_INT2EXPR_BUILDER.build();
    Trie<int[], IExpr> thisTrie = toData();
    for (int i = 0; i < originalIndices.length; i++) {
      originalIndices[i] = originalIndices[i] - 1;
    }
    Iterator<Entry<int[], IExpr>> iterator = thisTrie.entrySet().iterator();
    while (iterator.hasNext()) {
      Entry<int[], IExpr> entry = iterator.next();
      int[] sequence = entry.getKey();
      IExpr value = entry.getValue();
      int[] positions = new int[length];
      for (int i = 0; i < positions.length; i++) {
        positions[i] = sequence[originalIndices[i]];
      }
      resultTrie.putIfAbsent(positions, function.apply(value));
    }
    return new SparseArrayExpr(resultTrie, dimensionsPermutated, function.apply(getDefaultValue()),
        false);
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fDefaultValue);
    output.writeInt(fDimension.length);
    for (int i = 0; i < fDimension.length; i++) {
      output.writeInt(fDimension[i]);
    }
    IAST rules = arrayRules();
    output.writeObject(rules);
  }
}
