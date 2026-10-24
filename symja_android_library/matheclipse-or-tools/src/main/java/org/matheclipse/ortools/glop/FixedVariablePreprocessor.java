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
import org.matheclipse.ortools.lpdata.ProblemSolution;
import org.matheclipse.ortools.lpdata.VariableStatus;

/** Removes the fixed variables (lower bound == upper bound) and updates the constraint bounds. */
public final class FixedVariablePreprocessor extends Preprocessor {

  private final ColumnDeletionHelper columnDeletionHelper = new ColumnDeletionHelper();

  /** Creates the preprocessor. */
  public FixedVariablePreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @Override
  public boolean run(LinearProgram lp) {
    if (!parameters.getUseFixedVariablePreprocessor()) {
      return false;
    }
    final int numCols = lp.numVariables();
    final double[] lbs = lp.variableLowerBounds();
    final double[] ubs = lp.variableUpperBounds();
    for (int col = 0; col < numCols; ++col) {
      final double lowerBound = lbs[col];
      final double upperBound = ubs[col];
      if (lowerBound == upperBound) {
        final double fixedValue = lowerBound;
        subtractColumnMultipleFromConstraintBound(col, fixedValue, lp);
        columnDeletionHelper.markColumnForDeletionWithState(col, fixedValue,
            VariableStatus.FIXED_VALUE);
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
