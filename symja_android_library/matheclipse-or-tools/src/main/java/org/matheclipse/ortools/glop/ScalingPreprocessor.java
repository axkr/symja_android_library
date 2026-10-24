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
import org.matheclipse.ortools.lpdata.SparseMatrixScaler;

/**
 * Scales the matrix (geometric scaling and equilibration), the objective and the bounds; the
 * postsolve unscales the primal and dual values.
 */
public final class ScalingPreprocessor extends Preprocessor {

  private double[] variableLowerBounds = new double[0];
  private double[] variableUpperBounds = new double[0];
  private double costScalingFactor = 1.0;
  private double boundScalingFactor = 1.0;
  private final SparseMatrixScaler scaler = new SparseMatrixScaler();

  /** Creates the preprocessor. */
  public ScalingPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @Override
  public boolean run(LinearProgram lp) {
    if (!parameters.getUseScaling()) {
      return false;
    }
    variableLowerBounds = lp.variableLowerBounds().clone();
    variableUpperBounds = lp.variableUpperBounds().clone();
    lp.scale(scaler, parameters.getScalingMethod());
    costScalingFactor = lp.scaleObjective(parameters.getCostScaling());
    boundScalingFactor = lp.scaleBounds();
    return true;
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    scaler.scaleRowVector(false, solution.primalValues);
    for (int col = 0; col < solution.primalValues.length; ++col) {
      solution.primalValues[col] *= boundScalingFactor;
    }
    scaler.scaleColumnVector(false, solution.dualValues);
    for (int row = 0; row < solution.dualValues.length; ++row) {
      solution.dualValues[row] *= costScalingFactor;
    }
    final int numCols = solution.primalValues.length;
    for (int col = 0; col < numCols; ++col) {
      switch (solution.variableStatuses[col]) {
        case AT_UPPER_BOUND:
        case FIXED_VALUE:
          solution.primalValues[col] = variableUpperBounds[col];
          break;
        case AT_LOWER_BOUND:
          solution.primalValues[col] = variableLowerBounds[col];
          break;
        case FREE:
        case BASIC:
        default:
          break;
      }
    }
  }
}
