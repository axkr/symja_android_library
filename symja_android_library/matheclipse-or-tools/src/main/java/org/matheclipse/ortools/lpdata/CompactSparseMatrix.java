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
// C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.lpdata;

import java.util.Arrays;

/**
 * A compact column-major sparse matrix: the entries of column <code>col</code> are stored in the
 * index range <code>[columnStart(col), columnStart(col) + columnNumEntries(col))</code> of two flat
 * arrays. This is the representation used inside the revised simplex.
 */
public final class CompactSparseMatrix {

  private int numRows;
  private int numCols;
  private int[] starts = new int[1];
  private int[] rows = new int[0];
  private double[] coefficients = new double[0];
  private int numEntries;

  /** Creates an empty matrix. */
  public CompactSparseMatrix() {}

  /** Resets to an empty matrix with <code>numRows</code> rows and no column. */
  public void reset(int numRows) {
    this.numRows = numRows;
    this.numCols = 0;
    this.starts = new int[1];
    this.numEntries = 0;
  }

  /** Returns true if the matrix has no column. */
  public boolean isEmpty() {
    return numCols == 0;
  }

  /** The number of rows. */
  public int numRows() {
    return numRows;
  }

  /** The number of columns. */
  public int numCols() {
    return numCols;
  }

  /** The number of entries. */
  public int numEntries() {
    return numEntries;
  }

  /** The index of the first entry of <code>col</code>. */
  public int columnStart(int col) {
    return starts[col];
  }

  /** The number of entries of <code>col</code>. */
  public int columnNumEntries(int col) {
    return starts[col + 1] - starts[col];
  }

  /** The row of the entry with the given flat index. */
  public int entryRow(int i) {
    return rows[i];
  }

  /** The coefficient of the entry with the given flat index. */
  public double entryCoefficient(int i) {
    return coefficients[i];
  }

  /** The coefficient of the first entry of <code>col</code>. */
  public double columnFirstCoefficient(int col) {
    return coefficients[starts[col]];
  }

  /** The row of the first entry of <code>col</code>. */
  public int columnFirstRow(int col) {
    return rows[starts[col]];
  }

  /** The coefficient at (row, col) or 0.0. */
  public double columnLookUpCoefficient(int col, int row) {
    for (int i = starts[col]; i < starts[col + 1]; ++i) {
      if (rows[i] == row) {
        return coefficients[i];
      }
    }
    return 0.0;
  }

  /** Copies <code>input</code>, a matrix already containing the slack columns. */
  public void populateFromMatrixView(SparseMatrix input) {
    numRows = input.numRows();
    numCols = input.numCols();
    final int n = (int) input.numEntries();
    starts = new int[numCols + 1];
    rows = new int[n];
    coefficients = new double[n];
    int index = 0;
    for (int col = 0; col < numCols; ++col) {
      starts[col] = index;
      SparseColumn c = input.column(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        rows[index] = c.entryRow(i);
        coefficients[index] = c.entryCoefficient(i);
        ++index;
      }
    }
    starts[numCols] = index;
    numEntries = index;
  }

  /** Copies <code>input</code> and appends one unit slack column per row. */
  public void populateFromSparseMatrixAndAddSlacks(SparseMatrix input) {
    final int inputNumCols = input.numCols();
    numCols = inputNumCols + input.numRows();
    numRows = input.numRows();
    final int n = (int) input.numEntries() + numRows;
    starts = new int[numCols + 1];
    rows = new int[n];
    coefficients = new double[n];
    int index = 0;
    for (int col = 0; col < inputNumCols; ++col) {
      starts[col] = index;
      SparseColumn c = input.column(col);
      for (int i = 0; i < c.numEntries(); ++i) {
        coefficients[index] = c.entryCoefficient(i);
        rows[index] = c.entryRow(i);
        ++index;
      }
    }
    for (int row = 0; row < numRows; ++row) {
      starts[inputNumCols + row] = index;
      coefficients[index] = 1.0;
      rows[index] = row;
      ++index;
    }
    starts[numCols] = index;
    numEntries = index;
  }

  /** Replaces the content by the transpose of <code>input</code>. */
  public void populateFromTranspose(CompactSparseMatrix input) {
    numCols = input.numRows();
    numRows = input.numCols();
    final int startSize = numCols + 2;
    int[] s = new int[startSize];
    for (int i = 0; i < input.numEntries; ++i) {
      ++s[input.rows[i] + 2];
    }
    for (int col = 2; col < startSize; ++col) {
      s[col] += s[col - 1];
    }
    final int n = s[startSize - 1];
    coefficients = new double[n];
    rows = new int[n];
    starts = Arrays.copyOf(s, numCols + 1);
    for (int col = 0; col < input.numCols(); ++col) {
      final int transposedRow = col;
      for (int i = input.starts[col]; i < input.starts[col + 1]; ++i) {
        final int transposedCol = input.rows[i];
        final int index = starts[transposedCol + 1]++;
        coefficients[index] = input.coefficients[i];
        rows[index] = transposedRow;
      }
    }
    numEntries = n;
  }

  /** dense += multiplier * column(col). */
  public void columnAddMultipleToDenseColumn(int col, double multiplier, double[] dense) {
    if (multiplier == 0.0) {
      return;
    }
    for (int i = starts[col]; i < starts[col + 1]; ++i) {
      dense[rows[i]] += multiplier * coefficients[i];
    }
  }

  /** column += multiplier * column(col), keeping track of the new non-zero positions. */
  public void columnAddMultipleToSparseScatteredColumn(int col, double multiplier,
      ScatteredVector column) {
    if (multiplier == 0.0) {
      return;
    }
    for (int i = starts[col]; i < starts[col + 1]; ++i) {
      column.add(rows[i], multiplier * coefficients[i]);
    }
  }

  /** Clears <code>dense</code> and copies column(col) into it. */
  public void columnCopyToDenseColumn(int col, double[] dense) {
    Arrays.fill(dense, 0.0);
    columnCopyToClearedDenseColumn(col, dense);
  }

  /** Copies column(col) into the all-zero <code>dense</code>. */
  public void columnCopyToClearedDenseColumn(int col, double[] dense) {
    for (int i = starts[col]; i < starts[col + 1]; ++i) {
      dense[rows[i]] = coefficients[i];
    }
  }

  /** The scalar product of column(col) with a dense vector indexed by row. */
  public double columnScalarProduct(int col, double[] vector) {
    double result = 0.0;
    for (int i = starts[col]; i < starts[col + 1]; ++i) {
      result += coefficients[i] * vector[rows[i]];
    }
    return result;
  }

  /** The squared euclidean norm of column(col). */
  public double columnSquaredNorm(int col) {
    double sum = 0.0;
    for (int i = starts[col]; i < starts[col + 1]; ++i) {
      sum += coefficients[i] * coefficients[i];
    }
    return sum;
  }

  /** The largest magnitude of column(col). */
  public double columnInfinityNorm(int col) {
    double norm = 0.0;
    for (int i = starts[col]; i < starts[col + 1]; ++i) {
      norm = Math.max(norm, Math.abs(coefficients[i]));
    }
    return norm;
  }
}
