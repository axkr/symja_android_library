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

// [START program]
package org.matheclipse.ortools.sat.samples;

// [START import]
import static java.util.Arrays.stream;

import org.matheclipse.ortools.Loader;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.CpSolver;
import org.matheclipse.ortools.sat.CpSolverStatus;
import org.matheclipse.ortools.sat.IntVar;
import org.matheclipse.ortools.sat.LinearExpr;

// [END import]

/** Minimal CP-SAT example to showcase calling the solver. */
public final class CpSatExample {
  public static void main(String[] args) {
    Loader.loadNativeLibraries();
    // Create the model.
    // [START model]
    CpModel model = new CpModel();
    // [END model]

    // Create the variables.
    // [START variables]
    int varUpperBound = stream(new int[] {50, 45, 37}).max().getAsInt();

    IntVar x = model.newIntVar(0, varUpperBound, "x");
    IntVar y = model.newIntVar(0, varUpperBound, "y");
    IntVar z = model.newIntVar(0, varUpperBound, "z");
    // [END variables]

    // Create the constraints.
    // [START constraints]
    model.addLessOrEqual(LinearExpr.weightedSum(new IntVar[] {x, y, z}, new long[] {2, 7, 3}), 50);
    model.addLessOrEqual(LinearExpr.weightedSum(new IntVar[] {x, y, z}, new long[] {3, -5, 7}), 45);
    model.addLessOrEqual(LinearExpr.weightedSum(new IntVar[] {x, y, z}, new long[] {5, 2, -6}), 37);
    // [END constraints]

    // [START objective]
    model.maximize(LinearExpr.weightedSum(new IntVar[] {x, y, z}, new long[] {2, 2, 3}));
    // [END objective]

    // Create a solver and solve the model.
    // [START solve]
    CpSolver solver = new CpSolver();
    CpSolverStatus status = solver.solve(model);
    // [END solve]

    // [START print_solution]
    if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
      System.out.printf("Maximum of objective function: %f%n", solver.objectiveValue());
      System.out.println("x = " + solver.value(x));
      System.out.println("y = " + solver.value(y));
      System.out.println("z = " + solver.value(z));
    } else {
      System.out.println("No solution found.");
    }
    // [END print_solution]

    // Statistics.
    // [START statistics]
    System.out.println("Statistics");
    System.out.printf("  conflicts: %d%n", solver.numConflicts());
    System.out.printf("  branches : %d%n", solver.numBranches());
    System.out.printf("  wall time: %f s%n", solver.wallTime());
    // [END statistics]
  }

  private CpSatExample() {}
}
// [END program]
