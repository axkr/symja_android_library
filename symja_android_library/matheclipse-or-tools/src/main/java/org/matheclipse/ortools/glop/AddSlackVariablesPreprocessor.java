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

import java.util.Arrays;
import org.matheclipse.ortools.lpdata.ConstraintStatus;
import org.matheclipse.ortools.lpdata.LinearProgram;
import org.matheclipse.ortools.lpdata.LpTypes;
import org.matheclipse.ortools.lpdata.ProblemSolution;
import org.matheclipse.ortools.lpdata.VariableStatus;

/**
 * Puts the problem in equation form by adding one slack variable per constraint; the postsolve
 * computes the constraint statuses from the slack statuses and drops the slack values.
 */
public final class AddSlackVariablesPreprocessor extends Preprocessor {

  private int firstSlackCol;

  /** Creates the preprocessor. */
  public AddSlackVariablesPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @Override
  public boolean run(LinearProgram lp) {
    lp.addSlackVariablesWhereNecessary(true);
    firstSlackCol = lp.getFirstSlackVariable();
    return true;
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    final int numRows = solution.dualValues.length;
    for (int row = 0; row < numRows; ++row) {
      final int slackCol = firstSlackCol + row;
      final VariableStatus variableStatus = solution.variableStatuses[slackCol];
      ConstraintStatus constraintStatus;
      switch (variableStatus) {
        case AT_LOWER_BOUND:
          constraintStatus = ConstraintStatus.AT_UPPER_BOUND;
          break;
        case AT_UPPER_BOUND:
          constraintStatus = ConstraintStatus.AT_LOWER_BOUND;
          break;
        default:
          constraintStatus = LpTypes.variableToConstraintStatus(variableStatus);
          break;
      }
      solution.constraintStatuses[row] = constraintStatus;
    }
    solution.primalValues = Arrays.copyOf(solution.primalValues, firstSlackCol);
    solution.variableStatuses = Arrays.copyOf(solution.variableStatuses, firstSlackCol);
  }
}
