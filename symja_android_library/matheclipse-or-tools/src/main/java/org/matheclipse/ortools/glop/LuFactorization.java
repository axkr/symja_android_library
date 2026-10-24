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
// ortools/glop/lu_factorization.h/.cc C++ to Java, with a dense LU kernel in place of the sparse
// Markowitz factorization, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Locale;
import java.util.Arrays;
import java.util.List;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;

/**
 * LU factorization of a basis matrix B (the columns <code>basis[i]</code> of the matrix with
 * slacks) and the associated solves.
 *
 * <p>
 * The C++ class wraps the sparse Markowitz factorization (row and column permutations, sparse
 * triangular factors). This port keeps the public API of the class with a simpler kernel:
 * <ul>
 * <li>The singleton columns of the basis (the slack columns and the other columns with one
 * nonzero, at most one per row) are pivoted first on their row, without any fill-in, as the
 * Markowitz factorization also does.
 * <li>The remaining square block (the other columns restricted to the other rows) is factorized by
 * a dense LU with partial (row) pivoting which only visits the nonzeros of the pivot rows; the
 * factors are then stored row by row, so the solves cost the number of nonzeros of the factors.
 * </ul>
 * The positions of the basis are kept, the column permutation is therefore always the identity,
 * which the revised simplex handles as in C++ (an empty permutation).
 *
 * <p>
 * The factorization of an identity basis (the all slack basis) is detected by the caller and kept
 * implicit: every solve is then a no-op, for any vector size.
 */
public final class LuFactorization {

  private boolean isIdentityFactorization = true;
  // the size of the basis
  private int n;
  // the singleton columns: basis position, row and value
  private int numSingletons;
  private int[] singletonPosition = new int[0];
  private int[] singletonRow = new int[0];
  private double[] singletonValue = new double[0];
  // the entries of the kernel columns in the rows of the singletons, by singleton:
  // (local kernel column, value)
  private int[] couplingStart = new int[1];
  private int[] couplingCols = new int[0];
  private double[] couplingValues = new double[0];
  // the kernel: its size, global row of each local row, basis position of each local column
  private int kernelSize;
  private int[] kernelRow = new int[0];
  private int[] kernelPosition = new int[0];
  // row-major kernelSize^2, L strictly below the diagonal (unit diagonal), U on and above
  private double[] lu = new double[0];
  // rowPerm[i] = local kernel row placed at position i
  private int[] rowPerm = new int[0];
  private final int[] colPerm = new int[0];
  private double[] scratch = new double[0];
  private double[] output = new double[0];
  // the kernel factors row by row for the solves: L strictly lower (unit diagonal), U strictly
  // upper
  private double[] diagonal = new double[0];
  private int[] lRowStart = new int[1];
  private int[] lCols = new int[0];
  private double[] lValues = new double[0];
  private int[] uRowStart = new int[1];
  private int[] uCols = new int[0];
  private double[] uValues = new double[0];
  private GlopParameters parameters = new GlopParameters();
  private double lastFactorizationDeterministicTime;
  private long numEntries;

  /** Resets to the factorization of the identity matrix. */
  public void clear() {
    isIdentityFactorization = true;
    n = 0;
    numSingletons = 0;
    kernelSize = 0;
    lu = new double[0];
    rowPerm = new int[0];
    numEntries = 0;
  }

  /** Returns true if this is the implicit factorization of the identity. */
  public boolean isIdentityFactorization() {
    return isIdentityFactorization;
  }

  /** Sets the parameters (only the singularity threshold is used). */
  public void setParameters(GlopParameters parameters) {
    this.parameters = parameters;
  }

  private Status singular(double pivot, int m) {
    clear();
    lastFactorizationDeterministicTime = LpTypes.deterministicTimeForFpOperations((long) m * m);
    return new Status(Status.ErrorCode.ERROR_LU,
        String.format(Locale.ROOT, "The matrix is singular! pivot = %E", pivot));
  }

  /**
   * Factorizes the square matrix made of the columns <code>basis[0..m-1]</code> of
   * <code>matrix</code>. On failure the factorization is reset to the identity and an
   * {@link Status.ErrorCode#ERROR_LU} status is returned.
   */
  public Status computeFactorization(CompactSparseMatrix matrix, int[] basis) {
    final int m = matrix.numRows();
    n = m;
    final double singularityThreshold = parameters.getMarkowitzSingularityThreshold();
    // 1. the singleton columns, at most one per row
    final int[] singletonOfRow = new int[m];
    Arrays.fill(singletonOfRow, -1);
    singletonPosition = new int[m];
    singletonRow = new int[m];
    singletonValue = new double[m];
    numSingletons = 0;
    final boolean[] isSingletonPosition = new boolean[m];
    for (int j = 0; j < m; ++j) {
      final int col = basis[j];
      int row = -1;
      double value = 0.0;
      int count = 0;
      for (int k = matrix.columnStart(col); k < matrix.columnStart(col + 1); ++k) {
        if (matrix.entryCoefficient(k) != 0.0) {
          ++count;
          row = matrix.entryRow(k);
          value = matrix.entryCoefficient(k);
        }
      }
      if (count == 0) {
        return singular(0.0, m);
      }
      if (count == 1 && singletonOfRow[row] < 0) {
        if (Math.abs(value) <= singularityThreshold) {
          return singular(Math.abs(value), m);
        }
        singletonOfRow[row] = numSingletons;
        singletonPosition[numSingletons] = j;
        singletonRow[numSingletons] = row;
        singletonValue[numSingletons] = value;
        isSingletonPosition[j] = true;
        ++numSingletons;
      }
    }
    // 2. the kernel: the other columns restricted to the rows without singleton
    final int s = m - numSingletons;
    kernelSize = s;
    kernelRow = new int[s];
    kernelPosition = new int[s];
    final int[] localOfRow = new int[m];
    int numKernelRows = 0;
    for (int r = 0; r < m; ++r) {
      if (singletonOfRow[r] < 0) {
        localOfRow[r] = numKernelRows;
        kernelRow[numKernelRows++] = r;
      } else {
        localOfRow[r] = -1;
      }
    }
    int numKernelCols = 0;
    for (int j = 0; j < m; ++j) {
      if (!isSingletonPosition[j]) {
        kernelPosition[numKernelCols++] = j;
      }
    }
    lu = new double[s * s];
    final int[] couplingCount = new int[numSingletons + 1];
    for (int k = 0; k < s; ++k) {
      final int col = basis[kernelPosition[k]];
      for (int e = matrix.columnStart(col); e < matrix.columnStart(col + 1); ++e) {
        final double value = matrix.entryCoefficient(e);
        if (value == 0.0) {
          continue;
        }
        final int r = matrix.entryRow(e);
        if (localOfRow[r] >= 0) {
          lu[localOfRow[r] * s + k] = value;
        } else {
          ++couplingCount[singletonOfRow[r]];
        }
      }
    }
    couplingStart = new int[numSingletons + 1];
    for (int t = 0; t < numSingletons; ++t) {
      couplingStart[t + 1] = couplingStart[t] + couplingCount[t];
    }
    couplingCols = new int[couplingStart[numSingletons]];
    couplingValues = new double[couplingStart[numSingletons]];
    final int[] fill = Arrays.copyOf(couplingStart, numSingletons);
    for (int k = 0; k < s; ++k) {
      final int col = basis[kernelPosition[k]];
      for (int e = matrix.columnStart(col); e < matrix.columnStart(col + 1); ++e) {
        final double value = matrix.entryCoefficient(e);
        final int r = matrix.entryRow(e);
        if (value != 0.0 && localOfRow[r] < 0) {
          final int t = singletonOfRow[r];
          couplingCols[fill[t]] = k;
          couplingValues[fill[t]++] = value;
        }
      }
    }
    // 3. dense LU of the kernel with partial pivoting
    rowPerm = new int[s];
    for (int i = 0; i < s; ++i) {
      rowPerm[i] = i;
    }
    final int[] pivotRowCols = new int[s];
    for (int k = 0; k < s; ++k) {
      int pivotRow = -1;
      double maxMagnitude = 0.0;
      for (int i = k; i < s; ++i) {
        final double magnitude = Math.abs(lu[i * s + k]);
        if (magnitude > maxMagnitude) {
          maxMagnitude = magnitude;
          pivotRow = i;
        }
      }
      if (pivotRow < 0 || maxMagnitude <= singularityThreshold) {
        return singular(maxMagnitude, m);
      }
      if (pivotRow != k) {
        swapRows(k, pivotRow, s);
        final int t = rowPerm[k];
        rowPerm[k] = rowPerm[pivotRow];
        rowPerm[pivotRow] = t;
      }
      final int kOffset = k * s;
      final double pivot = lu[kOffset + k];
      // the nonzeros of the pivot row right of the diagonal
      int numPivotRowEntries = 0;
      for (int j = k + 1; j < s; ++j) {
        if (lu[kOffset + j] != 0.0) {
          pivotRowCols[numPivotRowEntries++] = j;
        }
      }
      for (int i = k + 1; i < s; ++i) {
        final double factor = lu[i * s + k];
        if (factor == 0.0) {
          continue;
        }
        final double l = factor / pivot;
        lu[i * s + k] = l;
        final int iOffset = i * s;
        for (int t = 0; t < numPivotRowEntries; ++t) {
          final int j = pivotRowCols[t];
          lu[iOffset + j] -= l * lu[kOffset + j];
        }
      }
    }
    buildSparseRows(s);
    numEntries += numSingletons + couplingCols.length;
    scratch = new double[s];
    output = new double[m];
    isIdentityFactorization = false;
    lastFactorizationDeterministicTime = LpTypes.deterministicTimeForFpOperations(
        (long) s * s * s / 3 + (long) m + numEntries);
    return Status.OK;
  }

  /** Extracts the nonzeros of L and U row by row for the solves. */
  private void buildSparseRows(int m) {
    diagonal = new double[m];
    lRowStart = new int[m + 1];
    uRowStart = new int[m + 1];
    int numL = 0;
    int numU = 0;
    for (int i = 0; i < m; ++i) {
      final int offset = i * m;
      for (int j = 0; j < i; ++j) {
        if (lu[offset + j] != 0.0) {
          ++numL;
        }
      }
      for (int j = i + 1; j < m; ++j) {
        if (lu[offset + j] != 0.0) {
          ++numU;
        }
      }
    }
    lCols = new int[numL];
    lValues = new double[numL];
    uCols = new int[numU];
    uValues = new double[numU];
    numL = 0;
    numU = 0;
    for (int i = 0; i < m; ++i) {
      final int offset = i * m;
      lRowStart[i] = numL;
      uRowStart[i] = numU;
      for (int j = 0; j < i; ++j) {
        final double v = lu[offset + j];
        if (v != 0.0) {
          lCols[numL] = j;
          lValues[numL++] = v;
        }
      }
      diagonal[i] = lu[offset + i];
      for (int j = i + 1; j < m; ++j) {
        final double v = lu[offset + j];
        if (v != 0.0) {
          uCols[numU] = j;
          uValues[numU++] = v;
        }
      }
    }
    lRowStart[m] = numL;
    uRowStart[m] = numU;
    numEntries = numL + numU + m;
  }

  private void swapRows(int a, int b, int m) {
    for (int j = 0; j < m; ++j) {
      final double t = lu[a * m + j];
      lu[a * m + j] = lu[b * m + j];
      lu[b * m + j] = t;
    }
  }

  /**
   * Given candidate columns, finds a maximal linearly independent subset (in a numerically stable
   * way) and completes it with slack columns into a square basis. The slacks come first.
   */
  public int[] computeInitialBasis(CompactSparseMatrix matrix, List<Integer> candidates) {
    final int m = matrix.numRows();
    final int k = candidates.size();
    // dense working copy, column-major m x k
    final double[][] w = new double[k][m];
    for (int j = 0; j < k; ++j) {
      final int col = candidates.get(j);
      for (int e = matrix.columnStart(col); e < matrix.columnStart(col + 1); ++e) {
        w[j][matrix.entryRow(e)] = matrix.entryCoefficient(e);
      }
    }
    final boolean[] rowUsed = new boolean[m];
    final boolean[] colUsed = new boolean[k];
    final int[] pivotRowOfStep = new int[Math.min(m, k)];
    final int[] pivotColOfStep = new int[Math.min(m, k)];
    int steps = 0;
    final double kRelativeThreshold = 1e-9;
    for (int j = 0; j < k && steps < m; ++j) {
      final double[] c = w[j];
      double colNorm = 0.0;
      for (int i = 0; i < m; ++i) {
        colNorm = Math.max(colNorm, Math.abs(c[i]));
      }
      // apply the previous elimination steps to this column
      for (int s = 0; s < steps; ++s) {
        final double[] p = w[pivotColOfStep[s]];
        final int pr = pivotRowOfStep[s];
        final double factor = c[pr] / p[pr];
        if (factor == 0.0) {
          continue;
        }
        for (int i = 0; i < m; ++i) {
          c[i] -= factor * p[i];
        }
        c[pr] = 0.0;
      }
      int best = -1;
      double bestMagnitude = 0.0;
      for (int i = 0; i < m; ++i) {
        if (rowUsed[i]) {
          continue;
        }
        final double magnitude = Math.abs(c[i]);
        if (magnitude > bestMagnitude) {
          bestMagnitude = magnitude;
          best = i;
        }
      }
      if (best >= 0 && bestMagnitude > Math.max(parameters.getMarkowitzSingularityThreshold(),
          kRelativeThreshold * colNorm)) {
        rowUsed[best] = true;
        colUsed[j] = true;
        pivotRowOfStep[steps] = best;
        pivotColOfStep[steps] = j;
        ++steps;
      }
    }
    final int[] basis = new int[m];
    int index = 0;
    final int firstSlack = matrix.numCols() - m;
    for (int row = 0; row < m; ++row) {
      if (!rowUsed[row]) {
        basis[index++] = firstSlack + row;
      }
    }
    for (int j = 0; j < k; ++j) {
      if (colUsed[j]) {
        basis[index++] = candidates.get(j);
      }
    }
    lastFactorizationDeterministicTime =
        LpTypes.deterministicTimeForFpOperations((long) m * m * Math.max(1, k));
    return basis;
  }

  /** The column permutation; always empty (identity). */
  public int[] getColumnPermutation() {
    return colPerm;
  }

  /** Sets the column permutation to the identity (nothing to do, it is always the identity). */
  public void setColumnPermutationToIdentity() {
    // the positions of the basis are never permuted
  }

  /** Solves B.x = b in place (b indexed by rows, x by basis positions). */
  public void rightSolve(double[] x) {
    if (isIdentityFactorization) {
      return;
    }
    final int m = n;
    final int s = kernelSize;
    final double[] y = scratch;
    for (int i = 0; i < s; ++i) {
      y[i] = x[kernelRow[rowPerm[i]]];
    }
    // L y = Pb
    for (int i = 0; i < s; ++i) {
      double sum = y[i];
      for (int e = lRowStart[i]; e < lRowStart[i + 1]; ++e) {
        sum -= lValues[e] * y[lCols[e]];
      }
      y[i] = sum;
    }
    // U z = y
    for (int i = s - 1; i >= 0; --i) {
      double sum = y[i];
      for (int e = uRowStart[i]; e < uRowStart[i + 1]; ++e) {
        sum -= uValues[e] * y[uCols[e]];
      }
      y[i] = sum / diagonal[i];
    }
    final double[] out = output;
    for (int k = 0; k < s; ++k) {
      out[kernelPosition[k]] = y[k];
    }
    // the singleton rows: v x_j + coupling . z = b_r
    for (int t = 0; t < numSingletons; ++t) {
      double sum = x[singletonRow[t]];
      for (int e = couplingStart[t]; e < couplingStart[t + 1]; ++e) {
        sum -= couplingValues[e] * y[couplingCols[e]];
      }
      out[singletonPosition[t]] = sum / singletonValue[t];
    }
    System.arraycopy(out, 0, x, 0, m);
  }

  /** Solves y.B = r in place (r indexed by basis positions, y by rows). */
  public void leftSolve(double[] yv) {
    if (isIdentityFactorization) {
      return;
    }
    final int m = n;
    final int s = kernelSize;
    final double[] out = output;
    // the singleton columns: y_r v = r_j
    for (int t = 0; t < numSingletons; ++t) {
      out[singletonRow[t]] = yv[singletonPosition[t]] / singletonValue[t];
    }
    final double[] z = scratch;
    for (int k = 0; k < s; ++k) {
      z[k] = yv[kernelPosition[k]];
    }
    for (int t = 0; t < numSingletons; ++t) {
      final double w = out[singletonRow[t]];
      if (w != 0.0) {
        for (int e = couplingStart[t]; e < couplingStart[t + 1]; ++e) {
          z[couplingCols[e]] -= couplingValues[e] * w;
        }
      }
    }
    // U^T z = r (forward, scattering the rows of U)
    for (int j = 0; j < s; ++j) {
      final double zj = z[j] / diagonal[j];
      z[j] = zj;
      if (zj != 0.0) {
        for (int e = uRowStart[j]; e < uRowStart[j + 1]; ++e) {
          z[uCols[e]] -= uValues[e] * zj;
        }
      }
    }
    // L^T w = z (backward, scattering the rows of L)
    for (int j = s - 1; j >= 0; --j) {
      final double wj = z[j];
      if (wj != 0.0) {
        for (int e = lRowStart[j]; e < lRowStart[j + 1]; ++e) {
          z[lCols[e]] -= lValues[e] * wj;
        }
      }
    }
    for (int i = 0; i < s; ++i) {
      out[kernelRow[rowPerm[i]]] = z[i];
    }
    System.arraycopy(out, 0, yv, 0, m);
  }

  /** Returns the squared norm of B^-1 times the given column of <code>matrix</code>. */
  public double rightSolveSquaredNorm(CompactSparseMatrix matrix, int col) {
    final double[] x = new double[matrix.numRows()];
    matrix.columnCopyToClearedDenseColumn(col, x);
    rightSolve(x);
    double sum = 0.0;
    for (double v : x) {
      sum += v * v;
    }
    return sum;
  }

  /** Returns the squared norm of (B^T)^-1.e_row. */
  public double dualEdgeSquaredNorm(int row, int numRows) {
    final double[] y = new double[numRows];
    y[row] = 1.0;
    leftSolve(y);
    double sum = 0.0;
    for (double v : y) {
      sum += v * v;
    }
    return sum;
  }

  /** The number of entries of the factors, 0 for the identity. */
  public long numberOfEntries() {
    return isIdentityFactorization ? 0 : numEntries;
  }

  /** The deterministic time of the last factorization. */
  public double deterministicTimeOfLastFactorization() {
    return lastFactorizationDeterministicTime;
  }

  /**
   * An upper bound of the infinity norm of B^-1; this port computes the norm itself, the largest
   * absolute row sum of B^-1, with one solve per column.
   */
  public double computeInverseInfinityNormUpperBound() {
    if (isIdentityFactorization) {
      return 1.0;
    }
    final int m = n;
    final double[] rowSums = new double[m];
    final double[] column = new double[m];
    for (int r = 0; r < m; ++r) {
      Arrays.fill(column, 0.0);
      column[r] = 1.0;
      rightSolve(column);
      for (int i = 0; i < m; ++i) {
        rowSums[i] += Math.abs(column[i]);
      }
    }
    double norm = 0.0;
    for (double v : rowSums) {
      norm = Math.max(norm, v);
    }
    return norm;
  }
}
