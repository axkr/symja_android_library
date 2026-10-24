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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/lp_data/sparse.h/.cc
// and matrix_utils.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.lpdata;

import java.util.ArrayList;
import java.util.List;

/** A column-major sparse matrix made of {@link SparseColumn}s. */
public final class SparseMatrix {

  private final List<SparseColumn> columns = new ArrayList<>();
  private int numRows;

  /** Creates an empty 0x0 matrix. */
  public SparseMatrix() {
    numRows = 0;
  }

  /** Removes all the columns and sets the number of rows to zero. */
  public void clear() {
    columns.clear();
    numRows = 0;
  }

  /** Returns true if the matrix has no column. */
  public boolean isEmpty() {
    return columns.isEmpty();
  }

  /** Cleans up all the columns, see {@link SparseColumn#cleanUp()}. */
  public void cleanUp() {
    for (SparseColumn c : columns) {
      c.cleanUp();
    }
  }

  /** Returns true if all the columns are cleaned up. */
  public boolean isCleanedUp() {
    for (SparseColumn c : columns) {
      if (!c.isCleanedUp()) {
        return false;
      }
    }
    return true;
  }

  /** Sets the number of rows. */
  public void setNumRows(int numRows) {
    this.numRows = numRows;
  }

  /** Appends an empty column and returns its index. */
  public int appendEmptyColumn() {
    columns.add(new SparseColumn());
    return columns.size() - 1;
  }

  /** Appends the unit column with <code>value</code> at <code>row</code>. */
  public void appendUnitVector(int row, double value) {
    SparseColumn c = new SparseColumn();
    c.setCoefficient(row, value);
    columns.add(c);
  }

  /** Resets to a matrix of the given dimension with empty columns. */
  public void reset(int numCols, int numRows) {
    columns.clear();
    for (int i = 0; i < numCols; ++i) {
      columns.add(new SparseColumn());
    }
    this.numRows = numRows;
  }

  /** The number of rows. */
  public int numRows() {
    return numRows;
  }

  /** The number of columns. */
  public int numCols() {
    return columns.size();
  }

  /** The total number of entries. */
  public long numEntries() {
    long n = 0;
    for (SparseColumn c : columns) {
      n += c.numEntries();
    }
    return n;
  }

  /** The column <code>col</code>. */
  public SparseColumn column(int col) {
    return columns.get(col);
  }

  /** The column <code>col</code> for modification. */
  public SparseColumn mutableColumn(int col) {
    return columns.get(col);
  }

  /** Replaces the content by the transpose of <code>input</code>. */
  public void populateFromTranspose(SparseMatrix input) {
    reset(input.numRows(), input.numCols());
    final int[] rowDegree = new int[input.numRows()];
    for (int col = 0; col < input.numCols(); ++col) {
      SparseColumn c = input.column(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        ++rowDegree[c.entryRow(i)];
      }
    }
    for (int row = 0; row < input.numRows(); ++row) {
      columns.get(row).reserve(rowDegree[row]);
    }
    for (int col = 0; col < input.numCols(); ++col) {
      SparseColumn c = input.column(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        columns.get(c.entryRow(i)).setCoefficient(col, c.entryCoefficient(i));
      }
    }
  }

  /** Replaces the content by a copy of <code>matrix</code>. */
  public void populateFromSparseMatrix(SparseMatrix matrix) {
    columns.clear();
    for (SparseColumn c : matrix.columns) {
      columns.add(new SparseColumn(c));
    }
    numRows = matrix.numRows;
  }

  /** Deletes the columns marked in <code>columnsToDelete</code>. */
  public void deleteColumns(boolean[] columnsToDelete) {
    if (columnsToDelete == null || columnsToDelete.length == 0) {
      return;
    }
    List<SparseColumn> kept = new ArrayList<>();
    for (int col = 0; col < columns.size(); ++col) {
      if (col >= columnsToDelete.length || !columnsToDelete[col]) {
        kept.add(columns.get(col));
      }
    }
    columns.clear();
    columns.addAll(kept);
  }

  /**
   * Deletes the rows mapped to {@link LpTypes#INVALID_ROW} by <code>permutation</code> and renumbers
   * the other ones.
   */
  public void deleteRows(int newNumRows, int[] permutation) {
    for (SparseColumn c : columns) {
      c.applyPartialRowPermutation(permutation);
    }
    setNumRows(newNumRows);
  }

  /** The value at (row, col). */
  public double lookUpValue(int row, int col) {
    return columns.get(col).lookUpCoefficient(row);
  }

  /**
   * Returns {min, max} of the non-zero coefficient magnitudes, {0, 0} for a matrix without non-zero.
   */
  public double[] computeMinAndMaxMagnitudes() {
    double min = LpTypes.INFINITY;
    double max = 0.0;
    for (SparseColumn c : columns) {
      for (int i = 0; i < c.numEntries(); ++i) {
        final double magnitude = Math.abs(c.entryCoefficient(i));
        if (magnitude != 0.0) {
          min = Math.min(min, magnitude);
          max = Math.max(max, magnitude);
        }
      }
    }
    if (max == 0.0) {
      min = 0.0;
    }
    return new double[] {min, max};
  }

  /**
   * Returns true if the rightmost square submatrix is the identity (the slack columns of the
   * equation form).
   */
  public boolean isRightMostSquareMatrixIdentity() {
    if (numRows > numCols()) {
      return false;
    }
    final int firstIdentityCol = numCols() - numRows;
    for (int col = firstIdentityCol; col < numCols(); ++col) {
      SparseColumn c = columns.get(col);
      if (c.numEntries() != 1 || c.entryCoefficient(0) != 1.0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns true if the first <code>numRows</code> x <code>numCols</code> parts of the two matrices
   * are exactly the same.
   */
  public static boolean areFirstColumnsAndRowsExactlyEquals(int numRows, int numCols,
      SparseMatrix matrixA, CompactSparseMatrix matrixB) {
    if (numRows > matrixA.numRows() || numRows > matrixB.numRows() || numCols > matrixA.numCols()
        || numCols > matrixB.numCols()) {
      return false;
    }
    for (int col = 0; col < numCols; ++col) {
      final SparseColumn colA = matrixA.column(col);
      final int bStart = matrixB.columnStart(col);
      final int bNum = matrixB.columnNumEntries(col);
      final int end = Math.min(colA.numEntries(), bNum);
      if (end < colA.numEntries() && colA.entryRow(end) < numRows) {
        return false;
      }
      if (end < bNum && matrixB.entryRow(bStart + end) < numRows) {
        return false;
      }
      for (int i = 0; i < end; ++i) {
        if (colA.entryRow(i) != matrixB.entryRow(bStart + i)) {
          if (colA.entryRow(i) < numRows || matrixB.entryRow(bStart + i) < numRows) {
            return false;
          } else {
            break;
          }
        }
        if (colA.entryCoefficient(i) != matrixB.entryCoefficient(bStart + i)) {
          return false;
        }
        if (colA.numEntries() > end && colA.entryRow(end) < numRows) {
          return false;
        }
        if (bNum > end && matrixB.entryRow(bStart + end) < numRows) {
          return false;
        }
      }
    }
    return true;
  }
}
