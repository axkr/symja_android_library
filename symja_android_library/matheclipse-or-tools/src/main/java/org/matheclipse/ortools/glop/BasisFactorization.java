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
// ortools/glop/basis_representation.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.ScatteredVector;

/**
 * The factorization B = L.U.E_0.E_1...E_{k-1} of the current basis: an LU factorization computed
 * at refactorization time followed by one eta matrix per simplex pivot.
 *
 * <p>
 * The basis is refactorized at least every <code>basis_refactorization_period</code> updates. The
 * C++ class can alternatively use the middle product form update of Huangfu and Hall; this port
 * always uses the eta file (the <code>use_middle_product_form_update</code> parameter is ignored).
 * All the solves leave the exact, sorted list of non-zero positions in the result vector.
 */
public final class BasisFactorization {

  /**
   * An eta matrix: the identity except for one column e. B.E is the basis where the column at the
   * eta position is replaced by B.e.
   */
  static final class EtaMatrix {
    private static final double SPARSE_THRESHOLD = 0.5;
    private final int etaCol;
    private final double etaColCoefficient;
    private final double[] etaCoeff;
    private final int[] sparseRows;

    EtaMatrix(int etaCol, ScatteredVector direction) {
      this.etaCol = etaCol;
      this.etaColCoefficient = direction.values[etaCol];
      this.etaCoeff = direction.values.clone();
      this.etaCoeff[etaCol] = 0.0;
      int count = 0;
      for (int i = 0; i < etaCoeff.length; ++i) {
        if (etaCoeff[i] != 0.0) {
          ++count;
        }
      }
      if (count < SPARSE_THRESHOLD * etaCoeff.length) {
        sparseRows = new int[count];
        int k = 0;
        for (int i = 0; i < etaCoeff.length; ++i) {
          if (etaCoeff[i] != 0.0) {
            sparseRows[k++] = i;
          }
        }
      } else {
        sparseRows = null;
      }
    }

    void leftSolve(double[] y) {
      double yValue = y[etaCol];
      if (sparseRows != null) {
        for (int row : sparseRows) {
          yValue -= y[row] * etaCoeff[row];
        }
      } else {
        for (int row = 0; row < etaCoeff.length; ++row) {
          yValue -= y[row] * etaCoeff[row];
        }
      }
      y[etaCol] = yValue / etaColCoefficient;
    }

    void rightSolve(double[] d) {
      if (d[etaCol] == 0.0) {
        return;
      }
      final double coeff = d[etaCol] / etaColCoefficient;
      if (sparseRows != null) {
        for (int row : sparseRows) {
          d[row] -= etaCoeff[row] * coeff;
        }
      } else {
        for (int row = 0; row < etaCoeff.length; ++row) {
          d[row] -= etaCoeff[row] * coeff;
        }
      }
      d[etaCol] = coeff;
    }
  }

  private final CompactSparseMatrix compactMatrix;
  private final RevisedSimplex.IntArrayRef basis;
  private GlopParameters parameters = new GlopParameters();
  private int maxNumUpdates;
  private int numUpdates;
  private final List<EtaMatrix> etaMatrices = new ArrayList<>();
  private final LuFactorization luFactorization = new LuFactorization();
  private final ScatteredVector tau = new ScatteredVector();
  private double deterministicTime;

  /** Creates the factorization of the basis columns of <code>compactMatrix</code>. */
  BasisFactorization(CompactSparseMatrix compactMatrix, RevisedSimplex.IntArrayRef basis) {
    this.compactMatrix = compactMatrix;
    this.basis = basis;
    setParameters(parameters);
  }

  /** Sets the parameters. */
  public void setParameters(GlopParameters parameters) {
    this.maxNumUpdates = parameters.getBasisRefactorizationPeriod();
    this.parameters = parameters;
    luFactorization.setParameters(parameters);
  }

  /** The column permutation of the LU factorization (empty means identity). */
  public int[] getColumnPermutation() {
    return luFactorization.getColumnPermutation();
  }

  /** Sets the column permutation to the identity. */
  public void setColumnPermutationToIdentity() {
    luFactorization.setColumnPermutationToIdentity();
  }

  /** Resets to the factorization of the identity. */
  public void clear() {
    numUpdates = 0;
    etaMatrices.clear();
    luFactorization.clear();
  }

  /** Clears and factorizes the current basis (cheap for the identity basis). */
  public Status initialize() {
    clear();
    if (isIdentityBasis()) {
      return Status.OK;
    }
    return computeFactorization();
  }

  /** Selects a factorizable basis among the candidates, completed with slacks. */
  public int[] computeInitialBasis(List<Integer> candidates) {
    final int[] result = luFactorization.computeInitialBasis(compactMatrix, candidates);
    deterministicTime += luFactorization.deterministicTimeOfLastFactorization();
    return result;
  }

  /** The number of rows. */
  public int getNumberOfRows() {
    return compactMatrix.numRows();
  }

  /** Refactorizes if some updates were done since the last factorization. */
  public Status refactorize() {
    if (isRefactorized()) {
      return Status.OK;
    }
    return forceRefactorization();
  }

  /** Refactorizes the basis. */
  public Status forceRefactorization() {
    clear();
    return computeFactorization();
  }

  /** Returns true if no update was done since the last factorization. */
  public boolean isRefactorized() {
    return numUpdates == 0;
  }

  private Status computeFactorization() {
    final Status status = luFactorization.computeFactorization(compactMatrix, basis.get());
    deterministicTime += luFactorization.deterministicTimeOfLastFactorization();
    return status;
  }

  /** Updates the factorization after the pivot (entering column replaces the leaving row). */
  public Status update(int enteringCol, int leavingVariableRow, ScatteredVector direction) {
    if (numUpdates >= maxNumUpdates) {
      return forceRefactorization();
    }
    ++numUpdates;
    etaMatrices.add(new EtaMatrix(leavingVariableRow, direction));
    return Status.OK;
  }

  private void etaLeftSolve(double[] y) {
    for (int i = etaMatrices.size() - 1; i >= 0; --i) {
      etaMatrices.get(i).leftSolve(y);
    }
  }

  private void etaRightSolve(double[] d) {
    for (int i = 0; i < etaMatrices.size(); ++i) {
      etaMatrices.get(i).rightSolve(d);
    }
  }

  /**
   * The dense kernel does not reproduce the exactness of the sparse factorization for singleton
   * columns of the basis: for a basis column k with a single entry a at row r, y.B = rhs gives
   * exactly y[r] = rhs[k] / a. This is enforced after each left solve, so that for example the dual
   * value of a constraint whose slack is basic is exactly zero.
   */
  private void fixSingletonColumnsAfterLeftSolve(double[] rhs, double[] y) {
    if (etaMatrices.isEmpty() && luFactorization.isIdentityFactorization()) {
      return;
    }
    final int[] b = basis.get();
    for (int k = 0; k < b.length; ++k) {
      final int col = b[k];
      if (compactMatrix.columnNumEntries(col) == 1) {
        y[compactMatrix.columnFirstRow(col)] = rhs[k] / compactMatrix.columnFirstCoefficient(col);
      }
    }
  }

  /** Solves y.B = rhs, y initially contains rhs. */
  public void leftSolve(ScatteredVector y) {
    final double[] rhs = y.values.clone();
    etaLeftSolve(y.values);
    luFactorization.leftSolve(y.values);
    fixSingletonColumnsAfterLeftSolve(rhs, y.values);
    y.computeNonZerosFromValues();
    bumpDeterministicTimeForSolve(y.numNonZerosEstimate());
  }

  /** Solves y.B = e_j. */
  public void leftSolveForUnitRow(int j, ScatteredVector y) {
    y.clearAndResizeWithNonZeros(compactMatrix.numRows());
    y.values[j] = 1.0;
    final double[] rhs = y.values.clone();
    etaLeftSolve(y.values);
    luFactorization.leftSolve(y.values);
    fixSingletonColumnsAfterLeftSolve(rhs, y.values);
    y.computeNonZerosFromValues();
    bumpDeterministicTimeForSolve(y.numNonZerosEstimate());
  }

  /** Same as {@link #leftSolveForUnitRow(int, ScatteredVector)}, on a refactorized basis. */
  public void temporaryLeftSolveForUnitRow(int j, ScatteredVector y) {
    leftSolveForUnitRow(j, y);
  }

  /** Solves B.d = a, d initially contains a. */
  public void rightSolve(ScatteredVector d) {
    luFactorization.rightSolve(d.values);
    etaRightSolve(d.values);
    d.computeNonZerosFromValues();
    bumpDeterministicTimeForSolve(d.numNonZerosEstimate());
  }

  /** Solves B.d = matrix.column(col). */
  public void rightSolveForProblemColumn(int col, ScatteredVector d) {
    d.clearAndResizeWithNonZeros(compactMatrix.numRows());
    compactMatrix.columnCopyToClearedDenseColumn(col, d.values);
    luFactorization.rightSolve(d.values);
    etaRightSolve(d.values);
    d.computeNonZerosFromValues();
    bumpDeterministicTimeForSolve(d.numNonZerosEstimate());
  }

  /** Solves B.tau = a for the dual edge norm update; returns the dense result. */
  public double[] rightSolveForTau(ScatteredVector a) {
    tau.values = a.values.clone();
    luFactorization.rightSolve(tau.values);
    etaRightSolve(tau.values);
    tau.computeNonZerosFromValues();
    bumpDeterministicTimeForSolve(tau.numNonZerosEstimate());
    return tau.values;
  }

  /** The squared norm of B^-1.matrix.column(col); the basis must be refactorized. */
  public double rightSolveSquaredNorm(int col) {
    bumpDeterministicTimeForSolve(compactMatrix.columnNumEntries(col));
    if (luFactorization.isIdentityFactorization()) {
      return compactMatrix.columnSquaredNorm(col);
    }
    return luFactorization.rightSolveSquaredNorm(compactMatrix, col);
  }

  /** The squared norm of (B^T)^-1.e_row; the basis must be refactorized. */
  public double dualEdgeSquaredNorm(int row) {
    bumpDeterministicTimeForSolve(1);
    if (luFactorization.isIdentityFactorization()) {
      return 1.0;
    }
    return luFactorization.dualEdgeSquaredNorm(row, compactMatrix.numRows());
  }

  private boolean isIdentityBasis() {
    final int numRows = compactMatrix.numRows();
    final int[] b = basis.get();
    for (int row = 0; row < numRows; ++row) {
      final int col = b[row];
      if (compactMatrix.columnNumEntries(col) != 1) {
        return false;
      }
      final double coeff = compactMatrix.columnFirstCoefficient(col);
      final int entryRow = compactMatrix.columnFirstRow(col);
      if (entryRow != row || coeff != 1.0) {
        return false;
      }
    }
    return true;
  }

  /** The 1-norm of B. */
  public double computeOneNorm() {
    if (isIdentityBasis()) {
      return 1.0;
    }
    double norm = 0.0;
    final int[] b = basis.get();
    for (int i = 0; i < compactMatrix.numRows(); ++i) {
      final int col = b[i];
      double sum = 0.0;
      for (int k = compactMatrix.columnStart(col); k < compactMatrix.columnStart(col + 1); ++k) {
        sum += Math.abs(compactMatrix.entryCoefficient(k));
      }
      norm = Math.max(norm, sum);
    }
    return norm;
  }

  /** The infinity norm of B. */
  public double computeInfinityNorm() {
    if (isIdentityBasis()) {
      return 1.0;
    }
    final int numRows = compactMatrix.numRows();
    final double[] rowSum = new double[numRows];
    final int[] b = basis.get();
    for (int i = 0; i < numRows; ++i) {
      final int col = b[i];
      for (int k = compactMatrix.columnStart(col); k < compactMatrix.columnStart(col + 1); ++k) {
        rowSum[compactMatrix.entryRow(k)] += Math.abs(compactMatrix.entryCoefficient(k));
      }
    }
    double norm = 0.0;
    for (double v : rowSum) {
      norm = Math.max(norm, v);
    }
    return norm;
  }

  /** The 1-norm of B^-1. */
  public double computeInverseOneNorm() {
    if (isIdentityBasis()) {
      return 1.0;
    }
    final int numRows = compactMatrix.numRows();
    double norm = 0.0;
    for (int col = 0; col < numRows; ++col) {
      ScatteredVector rhs = new ScatteredVector();
      rhs.assignToZero(numRows);
      rhs.values[col] = 1.0;
      rightSolve(rhs);
      double columnNorm = 0.0;
      for (int row = 0; row < numRows; ++row) {
        columnNorm += Math.abs(rhs.values[row]);
      }
      norm = Math.max(norm, columnNorm);
    }
    return norm;
  }

  /** The infinity norm of B^-1. */
  public double computeInverseInfinityNorm() {
    if (isIdentityBasis()) {
      return 1.0;
    }
    final int numRows = compactMatrix.numRows();
    final double[] rowSum = new double[numRows];
    for (int col = 0; col < numRows; ++col) {
      ScatteredVector rhs = new ScatteredVector();
      rhs.assignToZero(numRows);
      rhs.values[col] = 1.0;
      rightSolve(rhs);
      for (int row = 0; row < numRows; ++row) {
        rowSum[row] += Math.abs(rhs.values[row]);
      }
    }
    double norm = 0.0;
    for (double v : rowSum) {
      norm = Math.max(norm, v);
    }
    return norm;
  }

  /** The 1-norm condition number of B. */
  public double computeOneNormConditionNumber() {
    if (isIdentityBasis()) {
      return 1.0;
    }
    return computeOneNorm() * computeInverseOneNorm();
  }

  /** The infinity norm condition number of B. */
  public double computeInfinityNormConditionNumber() {
    if (isIdentityBasis()) {
      return 1.0;
    }
    return computeInfinityNorm() * computeInverseInfinityNorm();
  }

  /** An upper bound of the infinity norm condition number of B. */
  public double computeInfinityNormConditionNumberUpperBound() {
    if (isIdentityBasis()) {
      return 1.0;
    }
    bumpDeterministicTimeForSolve(compactMatrix.numRows());
    return computeInfinityNorm() * luFactorization.computeInverseInfinityNormUpperBound();
  }

  /** The deterministic time spent in this class. */
  public double deterministicTime() {
    return deterministicTime;
  }

  /** The number of updates since the last factorization. */
  public int numUpdates() {
    return numUpdates;
  }

  /** The number of entries of L + U. */
  public long numberOfEntriesInLU() {
    return luFactorization.numberOfEntries();
  }

  private void bumpDeterministicTimeForSolve(int numEntries) {
    final int numRows = compactMatrix.numRows();
    if (numRows == 0) {
      return;
    }
    final double density = (double) numEntries / numRows;
    deterministicTime += density
        * LpTypes.deterministicTimeForFpOperations(luFactorization.numberOfEntries())
        + LpTypes.deterministicTimeForFpOperations(etaMatrices.size() * (long) numRows);
  }
}
