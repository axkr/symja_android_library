// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from
// ortools/lp_data/scattered_vector.h C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.lpdata;

import java.util.Arrays;

/**
 * A dense vector together with the list of its (possibly) non-zero positions. It is used both for
 * the C++ <code>ScatteredColumn</code> (indexed by row) and <code>ScatteredRow</code> (indexed by
 * column).
 *
 * <p>
 * The non-zero list may be empty, in which case only the dense representation is meaningful;
 * otherwise it is a superset of the actual non-zero positions. Iterating with
 * {@link #nonZero(int)} for <code>i &lt; numNonZeros</code> is only valid when the list is populated
 * (or the vector is all zero).
 */
public final class ScatteredVector {

  /** Kept as in C++: ratio above which a dense iteration is used. */
  public static final double DEFAULT_RATIO_FOR_USING_DENSE_ITERATION = 0.8;

  /** The dense values. */
  public double[] values = new double[0];

  /** The non-zero positions; only the first {@link #numNonZeros} are used. */
  public int[] nonZeros = new int[0];

  /** The number of used entries of {@link #nonZeros}. */
  public int numNonZeros;

  /** Whether {@link #nonZeros} is sorted. */
  public boolean nonZerosAreSorted;

  private boolean[] isNonZero = new boolean[0];

  /** The size of the dense vector. */
  public int size() {
    return values.length;
  }

  /** The value at <code>index</code>. */
  public double get(int index) {
    return values[index];
  }

  /** Sets the value at <code>index</code> (without updating the non-zeros). */
  public void set(int index, double value) {
    values[index] = value;
  }

  /** The i-th non-zero position. */
  public int nonZero(int i) {
    return nonZeros[i];
  }

  /** Resizes the dense values keeping the existing ones, new positions are zero. */
  public void resize(int n) {
    if (values.length != n) {
      values = Arrays.copyOf(values, n);
    }
  }

  /** Resizes the dense values to <code>n</code> and sets them all to zero. */
  public void assignToZero(int n) {
    if (values.length != n) {
      values = new double[n];
    } else {
      Arrays.fill(values, 0.0);
    }
  }

  /** Clears the non-zero list. */
  public void clearNonZeros() {
    numNonZeros = 0;
    nonZerosAreSorted = true;
  }

  /** Appends a position to the non-zero list. */
  public void pushNonZero(int index) {
    if (numNonZeros == nonZeros.length) {
      nonZeros = Arrays.copyOf(nonZeros, Math.max(8, 2 * nonZeros.length));
    }
    nonZeros[numNonZeros++] = index;
  }

  /** Adds <code>value</code> at <code>index</code>, tracking the new non-zero position. */
  public void add(int index, double value) {
    values[index] += value;
    if (!isNonZero[index] && value != 0.0) {
      isNonZero[index] = true;
      pushNonZero(index);
      nonZerosAreSorted = false;
    }
  }

  /** Sorts the non-zero positions if needed. */
  public void sortNonZerosIfNeeded() {
    if (!nonZerosAreSorted) {
      Arrays.sort(nonZeros, 0, numNonZeros);
      nonZerosAreSorted = true;
    }
  }

  /** Returns true if it is better to use the dense representation. */
  public boolean shouldUseDenseIteration(double ratioForUsingDenseRepresentation) {
    if (numNonZeros == 0) {
      return true;
    }
    return numNonZeros > ratioForUsingDenseRepresentation * values.length;
  }

  /** Same with the default ratio. */
  public boolean shouldUseDenseIteration() {
    return shouldUseDenseIteration(DEFAULT_RATIO_FOR_USING_DENSE_ITERATION);
  }

  /** Clears the sparse mask used by {@link #add(int, double)}. */
  public void clearSparseMask() {
    if (isNonZero.length != values.length) {
      isNonZero = new boolean[values.length];
    } else if (shouldUseDenseIteration()) {
      Arrays.fill(isNonZero, false);
    } else {
      for (int i = 0; i < numNonZeros; ++i) {
        isNonZero[nonZeros[i]] = false;
      }
    }
  }

  /** Makes the sparse mask consistent with the non-zero list. */
  public void repopulateSparseMask() {
    clearSparseMask();
    for (int i = 0; i < numNonZeros; ++i) {
      isNonZero[nonZeros[i]] = true;
    }
  }

  /** Clears the non-zero list if the vector is too dense. */
  public void clearNonZerosIfTooDense(double ratio) {
    if (shouldUseDenseIteration(ratio)) {
      clearSparseMask();
      numNonZeros = 0;
    }
  }

  /** Same with the default ratio. */
  public void clearNonZerosIfTooDense() {
    clearNonZerosIfTooDense(DEFAULT_RATIO_FOR_USING_DENSE_ITERATION);
  }

  /** An over-estimate of the number of non-zeros. */
  public int numNonZerosEstimate() {
    return numNonZeros == 0 ? values.length : numNonZeros;
  }

  /** Recomputes the exact (sorted) non-zero list from the dense values. */
  public void computeNonZerosFromValues() {
    numNonZeros = 0;
    for (int i = 0; i < values.length; ++i) {
      if (values[i] != 0.0) {
        pushNonZero(i);
      }
    }
    nonZerosAreSorted = true;
  }

  /** Clears the non-zeros and resizes/zeroes the dense values. */
  public void clearAndResizeWithNonZeros(int size) {
    if (shouldUseDenseIteration() || values.length != size) {
      assignToZero(size);
    } else {
      for (int i = 0; i < numNonZeros; ++i) {
        values[nonZeros[i]] = 0.0;
      }
    }
    numNonZeros = 0;
    nonZerosAreSorted = true;
  }

  /** Copies the content of <code>other</code> into this vector. */
  public void copyFrom(ScatteredVector other) {
    values = Arrays.copyOf(other.values, other.values.length);
    nonZeros = Arrays.copyOf(other.nonZeros, Math.max(other.numNonZeros, 8));
    numNonZeros = other.numNonZeros;
    nonZerosAreSorted = other.nonZerosAreSorted;
  }

  /** The sum of squares of the values, using the non-zero list when available. */
  public double squaredNorm() {
    double sum = 0.0;
    if (numNonZeros == 0) {
      for (double v : values) {
        sum += v * v;
      }
    } else {
      for (int i = 0; i < numNonZeros; ++i) {
        final double v = values[nonZeros[i]];
        sum += v * v;
      }
    }
    return sum;
  }
}
