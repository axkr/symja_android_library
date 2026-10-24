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

import org.matheclipse.ortools.Loader;
import org.matheclipse.ortools.sat.CpModel;
import org.matheclipse.ortools.sat.CpSolver;
import org.matheclipse.ortools.sat.CpSolverSolutionCallback;
import org.matheclipse.ortools.sat.CpSolverStatus;
import org.matheclipse.ortools.sat.IntVar;

/** Code sample that solves a model and displays all solutions. */
public class SearchForAllSolutionsSampleSat {
  // [START print_solution]
  static class VarArraySolutionPrinter extends CpSolverSolutionCallback {
    public VarArraySolutionPrinter(IntVar[] variables) {
      variableArray = variables;
    }

    @Override
    public void onSolutionCallback() {
      System.out.printf("Solution #%d: time = %.02f s%n", solutionCount, wallTime());
      for (IntVar v : variableArray) {
        System.out.printf("  %s = %d%n", v.getName(), value(v));
      }
      solutionCount++;
    }

    public int getSolutionCount() {
      return solutionCount;
    }

    private int solutionCount;
    private final IntVar[] variableArray;
  }

  // [END print_solution]

  public static void main(String[] args) throws Exception {
    Loader.loadNativeLibraries();
    // Create the model.
    // [START model]
    CpModel model = new CpModel();
    // [END model]

    // Create the variables.
    // [START variables]
    int numVals = 3;

    IntVar x = model.newIntVar(0, numVals - 1, "x");
    IntVar y = model.newIntVar(0, numVals - 1, "y");
    IntVar z = model.newIntVar(0, numVals - 1, "z");
    // [END variables]

    // Create the constraints.
    // [START constraints]
    model.addDifferent(x, y);
    // [END constraints]

    // Create a solver and solve the model.
    // [START solve]
    CpSolver solver = new CpSolver();
    VarArraySolutionPrinter cb = new VarArraySolutionPrinter(new IntVar[] {x, y, z});
    // Tell the solver to enumerate all solutions.
    solver.getParameters().setEnumerateAllSolutions(true);
    // And solve.
    CpSolverStatus unusedStatus = solver.solve(model, cb);
    // [END solve]

    System.out.println(cb.getSolutionCount() + " solutions found.");
  }
}
// [END program]
