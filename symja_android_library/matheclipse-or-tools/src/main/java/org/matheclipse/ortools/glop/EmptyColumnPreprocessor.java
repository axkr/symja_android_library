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
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.ProblemSolution;

/**
 * Removes the variables that appear in no constraint: they are set to the bound that minimizes
 * the objective (any feasible value for a zero cost). An unbounded improving direction gives the
 * status INFEASIBLE_OR_UNBOUNDED.
 */
public final class EmptyColumnPreprocessor extends Preprocessor {

  private final ColumnDeletionHelper columnDeletionHelper = new ColumnDeletionHelper();

  /** Creates the preprocessor. */
  public EmptyColumnPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @Override
  public boolean run(LinearProgram lp) {
    if (!parameters.getUseEmptyColumnPreprocessor()) {
      return false;
    }
    columnDeletionHelper.clear();
    final int numCols = lp.numVariables();
    for (int col = 0; col < numCols; ++col) {
      if (lp.getSparseColumn(col).isEmpty()) {
        final double lowerBound = lp.variableLowerBounds()[col];
        final double upperBound = lp.variableUpperBounds()[col];
        final double objectiveCoefficient = lp.getObjectiveCoefficientForMinimizationVersion(col);
        double value;
        if (objectiveCoefficient == 0) {
          if (upperBound != LpTypes.INFINITY) {
            value = upperBound;
          } else {
            if (lowerBound != -LpTypes.INFINITY) {
              value = lowerBound;
            } else {
              value = 0.0;
            }
          }
        } else {
          value = objectiveCoefficient > 0 ? lowerBound : upperBound;
          if (!LpTypes.isFinite(value)) {
            status = ProblemStatus.INFEASIBLE_OR_UNBOUNDED;
            return false;
          }
          lp.setObjectiveOffset(lp.objectiveOffset() + value * lp.objectiveCoefficients()[col]);
        }
        columnDeletionHelper.markColumnForDeletionWithState(col, value,
            computeVariableStatus(value, lowerBound, upperBound));
      }
    }
    lp.deleteColumns(columnDeletionHelper.getMarkedColumns());
    return !columnDeletionHelper.isEmpty();
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    columnDeletionHelper.restoreDeletedColumns(solution);
  }
}
