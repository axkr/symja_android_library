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
// ortools/lp_data/sparse_vector.h and sparse_column.h C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.lpdata;

import java.util.Arrays;

/**
 * A sparse vector of (row, coefficient) entries, the column of a {@link SparseMatrix}.
 *
 * <p>
 * Entries are appended by {@link #setCoefficient(int, double)}; until {@link #cleanUp()} is called a
 * row may appear several times (the last entry wins) and entries may be unsorted or zero. After
 * <code>cleanUp()</code> the rows are strictly increasing and all coefficients are non-zero.
 */
public final class SparseColumn {

  private int[] rows;
  private double[] coefficients;
  private int numEntries;
  private boolean mayContainDuplicates;

  /** Creates an empty column. */
  public SparseColumn() {
    rows = new int[4];
    coefficients = new double[4];
    numEntries = 0;
    mayContainDuplicates = false;
  }

  /** Creates a copy of <code>other</code>. */
  public SparseColumn(SparseColumn other) {
    rows = Arrays.copyOf(other.rows, Math.max(4, other.numEntries));
    coefficients = Arrays.copyOf(other.coefficients, Math.max(4, other.numEntries));
    numEntries = other.numEntries;
    mayContainDuplicates = other.mayContainDuplicates;
  }

  /** Replaces the content of this column by a copy of <code>other</code>. */
  public void populateFromSparseVector(SparseColumn other) {
    if (rows.length < other.numEntries) {
      rows = new int[other.numEntries];
      coefficients = new double[other.numEntries];
    }
    System.arraycopy(other.rows, 0, rows, 0, other.numEntries);
    System.arraycopy(other.coefficients, 0, coefficients, 0, other.numEntries);
    numEntries = other.numEntries;
    mayContainDuplicates = other.mayContainDuplicates;
  }

  /** Removes all entries. */
  public void clear() {
    numEntries = 0;
    mayContainDuplicates = false;
  }

  /** Reserves room for <code>n</code> entries. */
  public void reserve(int n) {
    if (rows.length < n) {
      rows = Arrays.copyOf(rows, n);
      coefficients = Arrays.copyOf(coefficients, n);
    }
  }

  /** The number of stored entries. */
  public int numEntries() {
    return numEntries;
  }

  /** Returns true if there is no entry. */
  public boolean isEmpty() {
    return numEntries == 0;
  }

  /** The row of the i-th entry. */
  public int entryRow(int i) {
    return rows[i];
  }

  /** The coefficient of the i-th entry. */
  public double entryCoefficient(int i) {
    return coefficients[i];
  }

  /** Sets the coefficient of the i-th entry. */
  public void setEntryCoefficient(int i, double value) {
    coefficients[i] = value;
  }

  /** The row of the first entry. */
  public int getFirstRow() {
    return rows[0];
  }

  /** The coefficient of the first entry. */
  public double getFirstCoefficient() {
    return coefficients[0];
  }

  /** Appends an entry without any check. */
  public void addEntry(int row, double value) {
    if (numEntries == rows.length) {
      final int newSize = Math.max(4, 2 * rows.length);
      rows = Arrays.copyOf(rows, newSize);
      coefficients = Arrays.copyOf(coefficients, newSize);
    }
    rows[numEntries] = row;
    coefficients[numEntries] = value;
    ++numEntries;
  }

  /**
   * Sets the value at <code>row</code>. The entry is appended, so a previously stored value for the
   * same row stays until {@link #cleanUp()} removes it.
   */
  public void setCoefficient(int row, double value) {
    addEntry(row, value);
    mayContainDuplicates = true;
  }

  /** Sorts the entries by row, keeps the last value of a duplicated row and removes zeros. */
  public void cleanUp() {
    final int n = numEntries;
    final Integer[] order = new Integer[n];
    for (int i = 0; i < n; ++i) {
      order[i] = i;
    }
    // stable sort by row
    Arrays.sort(order, (a, b) -> Integer.compare(rows[a], rows[b]));
    final int[] sortedRows = new int[n];
    final double[] sortedCoeffs = new double[n];
    for (int i = 0; i < n; ++i) {
      sortedRows[i] = rows[order[i]];
      sortedCoeffs[i] = coefficients[order[i]];
    }
    int newSize = 0;
    for (int i = 0; i < n; ++i) {
      if (sortedCoeffs[i] == 0.0) {
        continue;
      }
      if (i + 1 == n || sortedRows[i] != sortedRows[i + 1]) {
        rows[newSize] = sortedRows[i];
        coefficients[newSize] = sortedCoeffs[i];
        ++newSize;
      }
    }
    numEntries = newSize;
    mayContainDuplicates = false;
  }

  /** Returns true if the rows are strictly increasing and no coefficient is zero. */
  public boolean isCleanedUp() {
    int previous = -1;
    for (int i = 0; i < numEntries; ++i) {
      if (rows[i] <= previous || coefficients[i] == 0.0) {
        return false;
      }
      previous = rows[i];
    }
    mayContainDuplicates = false;
    return true;
  }

  /** Returns the coefficient at <code>row</code> (the last one if duplicated) or 0.0. */
  public double lookUpCoefficient(int row) {
    double value = 0.0;
    for (int i = numEntries - 1; i >= 0; --i) {
      if (rows[i] == row) {
        value = coefficients[i];
        break;
      }
    }
    return value;
  }

  /** Multiplies all coefficients by <code>factor</code>. */
  public void multiplyByConstant(double factor) {
    for (int i = 0; i < numEntries; ++i) {
      coefficients[i] *= factor;
    }
  }

  /** Divides all coefficients by <code>factor</code>. */
  public void divideByConstant(double factor) {
    for (int i = 0; i < numEntries; ++i) {
      coefficients[i] /= factor;
    }
  }

  /** Divides each coefficient by <code>factors[row]</code>. */
  public void componentWiseDivide(double[] factors) {
    for (int i = 0; i < numEntries; ++i) {
      coefficients[i] /= factors[rows[i]];
    }
  }

  /** Multiplies each coefficient by <code>factors[row]</code>. */
  public void componentWiseMultiply(double[] factors) {
    for (int i = 0; i < numEntries; ++i) {
      coefficients[i] *= factors[rows[i]];
    }
  }

  /** Adds <code>multiplier</code> times this column to the dense vector. */
  public void addMultipleToDenseVector(double multiplier, double[] denseVector) {
    if (multiplier == 0.0) {
      return;
    }
    for (int i = 0; i < numEntries; ++i) {
      denseVector[rows[i]] += multiplier * coefficients[i];
    }
  }

  /** Removes the entries whose magnitude is at most <code>threshold</code>. */
  public void removeNearZeroEntries(double threshold) {
    int newSize = 0;
    for (int i = 0; i < numEntries; ++i) {
      if (Math.abs(coefficients[i]) > threshold) {
        rows[newSize] = rows[i];
        coefficients[newSize] = coefficients[i];
        ++newSize;
      }
    }
    numEntries = newSize;
  }

  /**
   * Renumbers the rows with <code>permutation</code>; entries mapped to {@link LpTypes#INVALID_ROW}
   * are removed.
   */
  public void applyPartialRowPermutation(int[] permutation) {
    int newSize = 0;
    for (int i = 0; i < numEntries; ++i) {
      final int newRow = permutation[rows[i]];
      if (newRow != LpTypes.INVALID_ROW) {
        rows[newSize] = newRow;
        coefficients[newSize] = coefficients[i];
        ++newSize;
      }
    }
    numEntries = newSize;
  }

  /** Renumbers the rows with <code>permutation</code>, entries stay in the same order. */
  public void applyRowPermutation(int[] permutation) {
    for (int i = 0; i < numEntries; ++i) {
      rows[i] = permutation[rows[i]];
    }
  }

  /** The scalar product with a dense vector indexed by row. */
  public double scalarProduct(double[] dense) {
    double sum = 0.0;
    for (int i = 0; i < numEntries; ++i) {
      sum += coefficients[i] * dense[rows[i]];
    }
    return sum;
  }

  /** The sum of the squared coefficients. */
  public double squaredNorm() {
    double sum = 0.0;
    for (int i = 0; i < numEntries; ++i) {
      sum += coefficients[i] * coefficients[i];
    }
    return sum;
  }

  /** The largest coefficient magnitude. */
  public double infinityNorm() {
    double norm = 0.0;
    for (int i = 0; i < numEntries; ++i) {
      norm = Math.max(norm, Math.abs(coefficients[i]));
    }
    return norm;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < numEntries; ++i) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(rows[i]).append(':').append(coefficients[i]);
    }
    return sb.append(']').toString();
  }
}
