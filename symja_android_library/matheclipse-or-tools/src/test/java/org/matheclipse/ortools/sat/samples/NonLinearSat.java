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
// Modified for the Symja matheclipse-or-tools module: sample ported to run as a JUnit test, see NOTICE in the module root.

package org.matheclipse.ortools.sat.samples;

import org.matheclipse.ortools.Loader;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.CpSolver;
import org.matheclipse.ortools.sat.CpSolverStatus;
import org.matheclipse.ortools.sat.IntVar;
import org.matheclipse.ortools.sat.LinearExpr;

/** Non linear examples using the addMultiplicationEquality(). */
public class NonLinearSat {
  public static void main(String[] args) throws Exception {
    Loader.loadNativeLibraries();
    CpModel model = new CpModel();

    final int perimeter = 20;
    IntVar x = model.newIntVar(0, perimeter, "x");
    IntVar y = model.newIntVar(0, perimeter, "y");

    model.addEquality(LinearExpr.weightedSum(new IntVar[] {x, y}, new long[] {2, 2}), perimeter);

    IntVar area = model.newIntVar(0, perimeter * perimeter, "s");
    model.addMultiplicationEquality(area, x, y);

    model.maximize(area);

    CpSolver solver = new CpSolver();
    CpSolverStatus status = solver.solve(model);

    if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
      System.out.println("x = " + solver.value(x));
      System.out.println("y = " + solver.value(y));
      System.out.println("s = " + solver.value(area));
    } else {
      System.out.println("No solution found");
    }
  }
}
