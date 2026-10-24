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
// ortools/glop/preprocessor.h/.cc C++ to Java, see NOTICE in the module root.

package org.matheclipse.ortools.glop;

import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpUtils;
import org.matheclipse.ortools.lpdata.ProblemSolution;
import org.matheclipse.ortools.lpdata.SparseColumn;

/**
 * Shifts the variables whose domain does not contain zero so that their bound of smallest
 * magnitude becomes zero. The constraint bounds and objective offset are updated exactly with
 * Kahan sums.
 */
public final class ShiftVariableBoundsPreprocessor extends Preprocessor {

  private double[] offsets = new double[0];
  private double[] variableInitialLbs = new double[0];
  private double[] variableInitialUbs = new double[0];
  private boolean onlyShiftByIntegerValues;

  /** Creates the preprocessor. */
  public ShiftVariableBoundsPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @Override
  public boolean run(LinearProgram lp) {
    if (!parameters.getUseShiftVariableBoundsPreprocessor()) {
      return false;
    }
    boolean allVariableDomainsContainZero = true;
    final int numCols = lp.numVariables();
    variableInitialLbs = lp.variableLowerBounds().clone();
    variableInitialUbs = lp.variableUpperBounds().clone();
    for (int col = 0; col < numCols; ++col) {
      if (0.0 < variableInitialLbs[col] || 0.0 > variableInitialUbs[col]) {
        allVariableDomainsContainZero = false;
      }
    }
    if (allVariableDomainsContainZero) {
      return false;
    }
    final int numRows = lp.numConstraints();
    final LpUtils.KahanSum objectiveOffset = new LpUtils.KahanSum();
    final LpUtils.KahanSum[] rowOffsets = new LpUtils.KahanSum[numRows];
    for (int row = 0; row < numRows; ++row) {
      rowOffsets[row] = new LpUtils.KahanSum();
    }
    offsets = new double[numCols];
    final double[] objective = lp.objectiveCoefficients();
    for (int col = 0; col < numCols; ++col) {
      if (0.0 < variableInitialLbs[col] || 0.0 > variableInitialUbs[col]) {
        double offset = minInMagnitudeOrZeroIfInfinite(variableInitialLbs[col],
            variableInitialUbs[col]);
        if (onlyShiftByIntegerValues || (inMipContext && lp.isVariableInteger(col))) {
          // In the integer case, round the offset: if for instance the lower bound is a positive
          // integer + epsilon, only shift by the integer and leave the lower bound at epsilon.
          offset = roundHalfAwayFromZero(offset);
        }
        offsets[col] = offset;
        lp.setVariableBounds(col, variableInitialLbs[col] - offset,
            variableInitialUbs[col] - offset);
        final SparseColumn column = lp.getSparseColumn(col);
        for (int i = 0; i < column.numEntries(); ++i) {
          rowOffsets[column.entryRow(i)].add(column.entryCoefficient(i) * offset);
        }
        objectiveOffset.add(objective[col] * offset);
      }
    }
    final double[] clb = lp.constraintLowerBounds();
    final double[] cub = lp.constraintUpperBounds();
    for (int row = 0; row < numRows; ++row) {
      final double rowOffset = rowOffsets[row].value();
      if (!Double.isFinite(rowOffset)) {
        status = ProblemStatus.INVALID_PROBLEM;
        return false;
      }
      lp.setConstraintBounds(row, clb[row] - rowOffset, cub[row] - rowOffset);
    }
    if (!Double.isFinite(objectiveOffset.value())) {
      status = ProblemStatus.INVALID_PROBLEM;
      return false;
    }
    lp.setObjectiveOffset(lp.objectiveOffset() + objectiveOffset.value());
    return true;
  }

  /** Only shifts the bounds by integer values (rounded offsets) for all the variables. */
  public void onlyShiftByIntegerValue() {
    onlyShiftByIntegerValues = true;
  }

  /** The C++ {@code std::round}: the nearest integer, halfway cases away from zero. */
  static double roundHalfAwayFromZero(double value) {
    final double magnitude = Math.abs(value);
    double t = Math.floor(magnitude);
    if (magnitude - t >= 0.5) {
      t += 1.0;
    }
    return Math.copySign(t, value);
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    final int numCols = solution.variableStatuses.length;
    for (int col = 0; col < numCols; ++col) {
      if (inMipContext) {
        solution.primalValues[col] += offsets[col];
      } else {
        switch (solution.variableStatuses[col]) {
          case FIXED_VALUE:
          case AT_LOWER_BOUND:
            solution.primalValues[col] = variableInitialLbs[col];
            break;
          case AT_UPPER_BOUND:
            solution.primalValues[col] = variableInitialUbs[col];
            break;
          case BASIC:
          case FREE:
            solution.primalValues[col] += offsets[col];
            break;
          default:
            break;
        }
      }
    }
  }
}
