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

/**
 * Changes a maximization problem into a minimization one by negating the objective; the scaling
 * factor becomes negative so that objective values are reported in the original direction. No
 * postsolve is needed.
 */
public final class ToMinimizationPreprocessor extends Preprocessor {

  /** Creates the preprocessor. */
  public ToMinimizationPreprocessor(GlopParameters parameters) {
    super(parameters);
  }

  @Override
  public boolean run(LinearProgram lp) {
    if (lp.isMaximizationProblem()) {
      for (int col = 0; col < lp.numVariables(); ++col) {
        final double coeff = lp.objectiveCoefficients()[col];
        if (coeff != 0.0) {
          lp.setObjectiveCoefficient(col, -coeff);
        }
      }
      lp.setMaximizationProblem(false);
      lp.setObjectiveOffset(-lp.objectiveOffset());
      lp.setObjectiveScalingFactor(-lp.objectiveScalingFactor());
    }
    return false;
  }

  @Override
  public void recoverSolution(ProblemSolution solution) {
    // nothing to do
  }
}
