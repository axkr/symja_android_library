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
// Modified for the Symja matheclipse-or-tools module: ported from ortools/lp_data/lp_data.h C++
// to Java, see NOTICE in the module root.

package org.matheclipse.ortools.lpdata;

import java.util.Arrays;
import org.matheclipse.ortools.glop.ProblemStatus;

/**
 * The solution of a linear program: status, primal values, dual values and basis statuses. The
 * preprocessors transform it back from the presolved problem to the original one.
 */
public final class ProblemSolution {

  /** The status of the problem. */
  public ProblemStatus status;

  /** The variable values, indexed by column. */
  public double[] primalValues;

  /** The dual values, indexed by row. */
  public double[] dualValues;

  /** The variable statuses, indexed by column. */
  public VariableStatus[] variableStatuses;

  /** The constraint statuses, indexed by row. */
  public ConstraintStatus[] constraintStatuses;

  /** An OPTIMAL solution of the given dimension with zero values and FREE statuses. */
  public ProblemSolution(int numRows, int numCols) {
    status = ProblemStatus.OPTIMAL;
    primalValues = new double[numCols];
    dualValues = new double[numRows];
    variableStatuses = new VariableStatus[numCols];
    Arrays.fill(variableStatuses, VariableStatus.FREE);
    constraintStatuses = new ConstraintStatus[numRows];
    Arrays.fill(constraintStatuses, ConstraintStatus.FREE);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Problem status: ").append(status);
    for (int col = 0; col < primalValues.length; ++col) {
      sb.append("\n  Var #").append(col).append(": ").append(variableStatuses[col]).append(' ')
          .append(primalValues[col]);
    }
    sb.append("\n------------------------------");
    for (int row = 0; row < dualValues.length; ++row) {
      sb.append("\n  Constraint #").append(row).append(": ").append(constraintStatuses[row])
          .append(' ').append(dualValues[row]);
    }
    return sb.toString();
  }
}
