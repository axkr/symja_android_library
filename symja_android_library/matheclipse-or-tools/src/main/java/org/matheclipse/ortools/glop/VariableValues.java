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
// ortools/glop/variable_values.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import org.matheclipse.ortools.lpdata.CompactSparseMatrix;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ScatteredVector;
import org.matheclipse.ortools.lpdata.VariableStatus;

/**
 * The values of all the variables, such that A.x = 0 (the matrix includes the slacks). It also
 * maintains the dual prices (the normalized primal infeasibilities of the basic variables) used
 * by the dual simplex pricing.
 */
public final class VariableValues {

  private final RevisedSimplex.ParametersRef parameters;
  private final CompactSparseMatrix matrix;
  private final RevisedSimplex.IntArrayRef basis;
  private final VariablesInfo variablesInfo;
  private final BasisFactorization basisFactorization;
  private final DualEdgeNorms dualEdgeNorms;
  private final DynamicMaximum dualPrices;
  private boolean putMoreImportanceOnNorm;
  private double[] variableValues = new double[0];
  private final ScatteredVector scratchpad = new ScatteredVector();
  private final ScatteredVector initiallyAllZeroScratchpad = new ScatteredVector();

  VariableValues(RevisedSimplex.ParametersRef parameters, CompactSparseMatrix matrix,
      RevisedSimplex.IntArrayRef basis, VariablesInfo variablesInfo,
      BasisFactorization basisFactorization, DualEdgeNorms dualEdgeNorms,
      DynamicMaximum dualPrices) {
    this.parameters = parameters;
    this.matrix = matrix;
    this.basis = basis;
    this.variablesInfo = variablesInfo;
    this.basisFactorization = basisFactorization;
    this.dualEdgeNorms = dualEdgeNorms;
    this.dualPrices = dualPrices;
  }

  /** The value of a variable. */
  public double get(int col) {
    return variableValues[col];
  }

  /** All the values. */
  public double[] getDenseRow() {
    return variableValues;
  }

  /** Sets the value of a variable. */
  public void set(int col, double value) {
    variableValues[col] = value;
  }

  private void ensureSize() {
    final int n = matrix.numCols();
    if (variableValues.length != n) {
      variableValues = Arrays.copyOf(variableValues, n);
    }
  }

  /** Sets a non-basic variable to the value implied by its status. */
  public void setNonBasicVariableValueFromStatus(int col) {
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    ensureSize();
    switch (variablesInfo.getStatusRow()[col]) {
      case FIXED_VALUE:
      case AT_LOWER_BOUND:
        variableValues[col] = lowerBounds[col];
        break;
      case AT_UPPER_BOUND:
        variableValues[col] = upperBounds[col];
        break;
      case FREE:
      case BASIC:
      default:
        break;
    }
  }

  /** Sets all the non-basic variables to their status value (FREE ones to the given values). */
  public void resetAllNonBasicVariableValues(double[] freeInitialValue) {
    final double[] lowerBounds = variablesInfo.getVariableLowerBounds();
    final double[] upperBounds = variablesInfo.getVariableUpperBounds();
    final VariableStatus[] statuses = variablesInfo.getStatusRow();
    final int numCols = matrix.numCols();
    ensureSize();
    for (int col = 0; col < numCols; ++col) {
      switch (statuses[col]) {
        case FIXED_VALUE:
        case AT_LOWER_BOUND:
          variableValues[col] = lowerBounds[col];
          break;
        case AT_UPPER_BOUND:
          variableValues[col] = upperBounds[col];
          break;
        case FREE:
          variableValues[col] = col < freeInitialValue.length ? freeInitialValue[col] : 0.0;
          break;
        case BASIC:
        default:
          break;
      }
    }
  }

  /** Recomputes the basic variables from the non-basic ones (B.x_B = -N.x_N). */
  public void recomputeBasicVariableValues() {
    final int numRows = matrix.numRows();
    scratchpad.clearNonZeros();
    scratchpad.assignToZero(numRows);
    final BitSet notBasic = variablesInfo.getNotBasicBitRow();
    for (int col = notBasic.nextSetBit(0); col >= 0; col = notBasic.nextSetBit(col + 1)) {
      matrix.columnAddMultipleToDenseColumn(col, -variableValues[col], scratchpad.values);
    }
    basisFactorization.rightSolve(scratchpad);
    final int[] b = basis.get();
    for (int row = 0; row < numRows; ++row) {
      variableValues[b[row]] = scratchpad.values[row];
    }
    dualPrices.clear();
  }

  /** ||A.x||_inf. */
  public double computeMaximumPrimalResidual() {
    final double[] activity = new double[matrix.numRows()];
    final int numCols = matrix.numCols();
    for (int col = 0; col < numCols; ++col) {
      matrix.columnAddMultipleToDenseColumn(col, variableValues[col], activity);
    }
    return LpUtils.infinityNorm(activity);
  }

  private static double colInfeasibility(int col, double[] values, double[] lb, double[] ub) {
    return Math.max(values[col] - ub[col], lb[col] - values[col]);
  }

  /** The maximum bound violation. */
  public double computeMaximumPrimalInfeasibility() {
    double primalInfeasibility = 0.0;
    final int numCols = matrix.numCols();
    final double[] lb = variablesInfo.getVariableLowerBounds();
    final double[] ub = variablesInfo.getVariableUpperBounds();
    for (int col = 0; col < numCols; ++col) {
      primalInfeasibility =
          Math.max(primalInfeasibility, colInfeasibility(col, variableValues, lb, ub));
    }
    return primalInfeasibility;
  }

  /** The sum of the bound violations. */
  public double computeSumOfPrimalInfeasibilities() {
    double sum = 0.0;
    final int numCols = matrix.numCols();
    final double[] lb = variablesInfo.getVariableLowerBounds();
    final double[] ub = variablesInfo.getVariableUpperBounds();
    for (int col = 0; col < numCols; ++col) {
      sum += Math.max(0.0, colInfeasibility(col, variableValues, lb, ub));
    }
    return sum;
  }

  /** x_B -= step * direction and x_entering += step. */
  public void updateOnPivoting(ScatteredVector direction, int enteringCol, double step) {
    final int[] b = basis.get();
    for (int i = 0; i < direction.numNonZeros; ++i) {
      final int row = direction.nonZeros[i];
      variableValues[b[row]] -= direction.values[row] * step;
    }
    variableValues[enteringCol] += step;
  }

  /**
   * Sets the given non-basic variables to their status value and, if asked, updates the basic
   * variables and the dual prices incrementally.
   */
  public void updateGivenNonBasicVariables(List<Integer> colsToUpdate,
      boolean updateBasicVariables) {
    if (!updateBasicVariables) {
      for (int col : colsToUpdate) {
        setNonBasicVariableValueFromStatus(col);
      }
      return;
    }
    final int numRows = matrix.numRows();
    initiallyAllZeroScratchpad.resize(numRows);
    initiallyAllZeroScratchpad.clearSparseMask();
    boolean useDense = false;
    for (int col : colsToUpdate) {
      final double oldValue = variableValues[col];
      setNonBasicVariableValueFromStatus(col);
      if (useDense) {
        matrix.columnAddMultipleToDenseColumn(col, variableValues[col] - oldValue,
            initiallyAllZeroScratchpad.values);
      } else {
        matrix.columnAddMultipleToSparseScatteredColumn(col, variableValues[col] - oldValue,
            initiallyAllZeroScratchpad);
        useDense = initiallyAllZeroScratchpad.shouldUseDenseIteration();
      }
    }
    initiallyAllZeroScratchpad.clearSparseMask();
    initiallyAllZeroScratchpad.clearNonZerosIfTooDense();
    basisFactorization.rightSolve(initiallyAllZeroScratchpad);
    final int[] b = basis.get();
    if (initiallyAllZeroScratchpad.numNonZeros == 0) {
      for (int row = 0; row < numRows; ++row) {
        variableValues[b[row]] -= initiallyAllZeroScratchpad.values[row];
      }
      initiallyAllZeroScratchpad.assignToZero(numRows);
      recomputeDualPrices(false);
      return;
    }
    for (int i = 0; i < initiallyAllZeroScratchpad.numNonZeros; ++i) {
      final int row = initiallyAllZeroScratchpad.nonZeros[i];
      variableValues[b[row]] -= initiallyAllZeroScratchpad.values[row];
      initiallyAllZeroScratchpad.values[row] = 0.0;
    }
    updateDualPrices(initiallyAllZeroScratchpad.nonZeros, initiallyAllZeroScratchpad.numNonZeros);
    initiallyAllZeroScratchpad.clearNonZeros();
  }

  /** Recomputes all the dual prices. */
  public void recomputeDualPrices(boolean putMoreImportanceOnNorm) {
    final int numRows = matrix.numRows();
    dualPrices.clearAndResize(numRows);
    dualPrices.startDenseUpdates();
    this.putMoreImportanceOnNorm = putMoreImportanceOnNorm;
    final double tolerance = parameters.get().getPrimalFeasibilityTolerance();
    final double[] squaredNorms = dualEdgeNorms.getEdgeSquaredNorms();
    final int[] b = basis.get();
    final double[] lb = variablesInfo.getVariableLowerBounds();
    final double[] ub = variablesInfo.getVariableUpperBounds();
    for (int row = 0; row < numRows; ++row) {
      final int col = b[row];
      final double infeasibility = colInfeasibility(col, variableValues, lb, ub);
      if (infeasibility > tolerance) {
        dualPrices.denseAddOrUpdate(row,
            putMoreImportanceOnNorm ? Math.abs(infeasibility) / squaredNorms[row]
                : infeasibility * infeasibility / squaredNorms[row]);
      }
    }
  }

  /** Updates the dual prices of the given rows. */
  public void updateDualPrices(int[] rows, int numRows) {
    if (dualPrices.size() != matrix.numRows()) {
      recomputeDualPrices(putMoreImportanceOnNorm);
      return;
    }
    final double tolerance = parameters.get().getPrimalFeasibilityTolerance();
    final int[] b = basis.get();
    final double[] squaredNorms = dualEdgeNorms.getEdgeSquaredNorms();
    final double[] lb = variablesInfo.getVariableLowerBounds();
    final double[] ub = variablesInfo.getVariableUpperBounds();
    for (int i = 0; i < numRows; ++i) {
      final int row = rows[i];
      final int col = b[row];
      final double infeasibility = colInfeasibility(col, variableValues, lb, ub);
      if (infeasibility > tolerance) {
        dualPrices.addOrUpdate(row,
            putMoreImportanceOnNorm ? Math.abs(infeasibility) / squaredNorms[row]
                : infeasibility * infeasibility / squaredNorms[row]);
      } else {
        dualPrices.remove(row);
      }
    }
  }

  /**
   * Sets the primal phase I cost of the basic variables of the given rows: 1 above the upper bound,
   * -1 below the lower bound, 0 otherwise (with the primal tolerance). Returns true if some cost
   * changed.
   */
  public boolean updatePrimalPhaseICosts(int[] rows, int numRows, double[] objective) {
    boolean changed = false;
    final double tolerance = parameters.get().getPrimalFeasibilityTolerance();
    final double[] lb = variablesInfo.getVariableLowerBounds();
    final double[] ub = variablesInfo.getVariableUpperBounds();
    final int[] b = basis.get();
    for (int i = 0; i < numRows; ++i) {
      final int row = rows == null ? i : rows[i];
      final int col = b[row];
      double newCost = 0.0;
      if (variableValues[col] - ub[col] > tolerance) {
        newCost = 1.0;
      } else if (lb[col] - variableValues[col] > tolerance) {
        newCost = -1.0;
      }
      if (newCost != objective[col]) {
        changed = true;
        objective[col] = newCost;
      }
    }
    return changed;
  }
}
