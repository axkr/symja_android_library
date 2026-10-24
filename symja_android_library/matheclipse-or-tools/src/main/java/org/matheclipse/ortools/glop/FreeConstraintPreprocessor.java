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

/** Removes the constraints with both bounds infinite. */
public final class FreeConstraintPreprocessor extends Preprocessor {

  private final RowDeletionHelper rowDeletionHelper = new RowDeletionHelper();

  /** Creates the preprocessor. */
  public FreeConstraintPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @Override
  public boolean run(LinearProgram lp) {
    if (!parameters.getUseFreeConstraintPreprocessor()) {
      return false;
    }
    final int numRows = lp.numConstraints();
    final double[] lbs = lp.constraintLowerBounds();
    final double[] ubs = lp.constraintUpperBounds();
    for (int row = 0; row < numRows; ++row) {
      if (lbs[row] == -LpTypes.INFINITY && ubs[row] == LpTypes.INFINITY) {
        rowDeletionHelper.markRowForDeletion(row);
      }
    }
    lp.deleteRows(rowDeletionHelper.getMarkedRows());
    return !rowDeletionHelper.isEmpty();
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    rowDeletionHelper.restoreDeletedRows(solution);
  }
}
