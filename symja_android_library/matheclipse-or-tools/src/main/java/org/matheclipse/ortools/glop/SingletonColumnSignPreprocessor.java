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

import java.util.ArrayList;
import java.util.List;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.ProblemSolution;
import org.matheclipse.ortools.lpdata.SparseColumn;
import org.matheclipse.ortools.lpdata.VariableStatus;

/**
 * Makes the coefficient of all the singleton columns positive by negating the variable, which
 * helps the singleton heuristic of the initial basis.
 */
public final class SingletonColumnSignPreprocessor extends Preprocessor {

  private final List<Integer> changedColumns = new ArrayList<>();

  /** Creates the preprocessor. */
  public SingletonColumnSignPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @Override
  public boolean run(LinearProgram lp) {
    if (!parameters.getUseSingletonColumnSignPreprocessor()) {
      return false;
    }
    final int numCols = lp.numVariables();
    if (numCols == 0) {
      return false;
    }
    changedColumns.clear();
    for (int col = 0; col < numCols; ++col) {
      final SparseColumn sparseColumn = lp.getSparseColumn(col);
      final double cost = lp.objectiveCoefficients()[col];
      if (sparseColumn.numEntries() == 1 && sparseColumn.getFirstCoefficient() < 0) {
        lp.getMutableSparseColumn(col).multiplyByConstant(-1.0);
        lp.setVariableBounds(col, -lp.variableUpperBounds()[col],
            -lp.variableLowerBounds()[col]);
        lp.setObjectiveCoefficient(col, -cost);
        changedColumns.add(col);
      }
    }
    return !changedColumns.isEmpty();
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    for (int col : changedColumns) {
      solution.primalValues[col] = -solution.primalValues[col];
      final VariableStatus s = solution.variableStatuses[col];
      if (s == VariableStatus.AT_UPPER_BOUND) {
        solution.variableStatuses[col] = VariableStatus.AT_LOWER_BOUND;
      } else if (s == VariableStatus.AT_LOWER_BOUND) {
        solution.variableStatuses[col] = VariableStatus.AT_UPPER_BOUND;
      }
    }
  }
}
